package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.Parser;
import io.gitlab.swded.system.model.Value;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class TableController {

    @FXML
    private TableView<DataRow> table;
    private ObservableList<DataRow> data = FXCollections.observableArrayList();
    private TableColumn<DataRow, ?>[] valueColumns;
    private String[] header;

    void displayData(Parser parser) {
        DataRow[] dataRows = parser.getData();
        DataRow firstDataRow = dataRows[0];
        header = parser.getHeader();
        if (header == null) {
            header = askUserForHeader(firstDataRow.size());
        }
        //noinspection unchecked
        valueColumns = new TableColumn[header.length];
        for (int i = 0; i < header.length; i++) {
            Value firstColumnValue = firstDataRow.getValue(i);
            if (firstColumnValue.isNumber()) {
                valueColumns[i] = createNumericColumn(header[i], i);
            } else {
                valueColumns[i] = createTextColumn(header[i], i);
            }
        }
        table.getColumns().addAll(valueColumns);
        data.addAll(dataRows);
        table.setItems(data);
    }

    private TableColumn<DataRow, Number> createNumericColumn(String header, int columnIndex) {
        TableColumn<DataRow, Number> valueColumn = new TableColumn<>(header);
        Label columnHeader = new Label(header);
        valueColumn.setGraphic(columnHeader);
        valueColumn.setSortable(false);
        columnHeader.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                mouseEvent.consume();
                ContextMenu contextMenu = createNumericContextMenu(columnIndex);
                valueColumn.setContextMenu(contextMenu);
            } else {
                enableSortingForAWhile(valueColumn);
            }
        });
        valueColumn.setCellValueFactory(p -> p.getValue().getValue(columnIndex).valueProperty());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        return valueColumn;
    }

    private TableColumn<DataRow, String> createTextColumn(String header, int columnIndex) {
        TableColumn<DataRow, String> textColumn = new TableColumn<>();
        Label columnHeader = new Label(header);
        textColumn.setGraphic(columnHeader);
        textColumn.setSortable(false);
        columnHeader.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                mouseEvent.consume();
                ContextMenu contextMenu = createTextContextMenu(columnIndex);
                textColumn.setContextMenu(contextMenu);
            } else {
                enableSortingForAWhile(textColumn);
            }
        });
        textColumn.setCellValueFactory(p -> p.getValue().getValue(columnIndex).textProperty());
        textColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        return textColumn;
    }


    private ContextMenu createNumericContextMenu(int columnIndex) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem toDiscrete = new MenuItem("Discretization");
        toDiscrete.setOnAction(event -> {
            toDiscrete(columnIndex);
        });
        MenuItem normalize = new MenuItem("Normalization");
        normalize.setOnAction(event -> {
            normalize(columnIndex);
        });
        MenuItem setInterval = new MenuItem("Interval");
        setInterval.setOnAction(event -> {
            setInterval(columnIndex);
        });
        contextMenu.getItems().addAll(toDiscrete, normalize, setInterval);
        contextMenu.setAutoHide(true);
        return contextMenu;
    }

    private void setInterval(int columnIndex) {
        Dialog<Pair<Double, Double>> intervalDialog = createIntervalDialog();
        intervalDialog.showAndWait()
                .ifPresent(newRange -> {
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    for (DataRow row : data) {
                        double value = row.getValue(columnIndex).getValue();
                        min = Math.min(min, value);
                        max = Math.max(max, value);
                    }
                    double previousRange = max - min;
                    double rangeScale = (newRange.getValue() - newRange.getKey()) / previousRange;
                    double currentMin = min;
                    data.forEach(row -> {
                        double value = row.getValue(columnIndex).getValue();
                        double newValue = (value - currentMin) * rangeScale + newRange.getKey();
                        row.addValue(new Value(newValue));
                    });
                    TableColumn<DataRow, Number> numericColumn = createNumericColumn(header[columnIndex] + "_INTERVAL", table.getColumns().size());
                    table.getColumns().add(numericColumn);
                });
    }

    private Dialog<Pair<Double, Double>> createIntervalDialog() {
        Dialog<Pair<Double, Double>> dialog = new Dialog<>();
        dialog.setTitle("Interval");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));

        TextField from = new TextField();
        from.setPromptText("From");
        from.setTextFormatter(createDoubleTextFormatter());
        TextField to = new TextField();
        to.setPromptText("To");
        to.setTextFormatter(createDoubleTextFormatter());

        gridPane.add(new Label("From:"), 0, 0);
        gridPane.add(from, 1, 0);
        gridPane.add(new Label("To:"), 2, 0);
        gridPane.add(to, 3, 0);

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(from::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(
                        Double.parseDouble(from.getText()),
                        Double.parseDouble(to.getText())
                );
            }
            return null;
        });
        return dialog;
    }

    private TextFormatter<Double> createDoubleTextFormatter() {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }


            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        return new TextFormatter<>(converter, 0.0, filter);
    }

    private void normalize(int columnIndex) {
        double sum = data.stream().mapToDouble(row -> row.getValue(columnIndex).getValue()).sum();
        double avg = sum / data.size();
        double sd = data.stream().mapToDouble(row -> {
            double value = row.getValue(columnIndex).getValue();
            double diff = value - avg;
            return diff * diff;
        }).sum() / (data.size() - 1);
        data.forEach(row -> {
            double x = row.getValue(columnIndex).getValue();
            row.addValue(new Value((x - avg) / sd));
        });
        TableColumn<DataRow, Number> numericColumn = createNumericColumn(header[columnIndex] + "_NORM", table.getColumns().size());
        table.getColumns().add(numericColumn);
    }

    private void toDiscrete(int columnIndex) {
        TextInputDialog inputDialog = new TextInputDialog("0");
        TextField editor = inputDialog.getEditor();
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                editor.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        inputDialog.setTitle("Discretization");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText("Number of subdivisions");
        int divisionsCount = Integer.parseInt(inputDialog.showAndWait().orElse("0"));
        if (divisionsCount <= 0) {
            return;
        }
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (DataRow row : data) {
            double value = row.getValue(columnIndex).getValue();
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        double range = max - min;
        double divisionRange = range / divisionsCount;
        double[] thresholds = new double[divisionsCount];
        thresholds[0] = min + divisionRange;
        for (int i = 1; i < thresholds.length; i++) {
            thresholds[i] = thresholds[i - 1] + divisionRange;
        }
        if (range == 0) {
            return;
        }
        for (DataRow row : data) {
            double value = row.getValue(columnIndex).getValue();
            for (int i = 0; i < thresholds.length; i++) {
                if (value <= thresholds[i]) {
                    row.addValue(new Value(i));
                    break;
                }
            }
        }
        TableColumn<DataRow, Number> numericColumn = createNumericColumn(header[columnIndex] + "_DISC", table.getColumns().size());
        table.getColumns().add(numericColumn);
    }

    private ContextMenu createTextContextMenu(int columnIndex) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem toNumeric = new MenuItem("To numeric");
        toNumeric.setOnAction(event -> {
            toNumeric(columnIndex);
        });
        contextMenu.getItems().add(toNumeric);
        contextMenu.setAutoHide(true);
        return contextMenu;
    }

    private void toNumeric(int columnIndex) {
        List<String> texts = new ArrayList<>();
        for (DataRow row : data) {
            String text = row.getValue(columnIndex).getText();
            if (!texts.contains(text)) {
                texts.add(text);
            }
        }
        for (DataRow row : data) {
            String text = row.getValue(columnIndex).getText();
            row.addValue(new Value(texts.indexOf(text) + 1));
        }
        TableColumn<DataRow, Number> numericColumn = createNumericColumn(header[columnIndex] + "_NUM", table.getColumns().size());
        table.getColumns().add(numericColumn);
    }

    private void enableSortingForAWhile(TableColumn<DataRow, ?> textColumn) {
        table.getSortOrder().add(textColumn);
        textColumn.setSortable(true);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                textColumn.setSortable(false);
            });
        }).start();
    }

    private String[] askUserForHeader(int size) {
        String[] header = new String[size];
        for (int i = 0; i < size; i++) {
            int number = i + 1;
            String defaultValue = "C" + number;
            TextInputDialog inputDialog = new TextInputDialog(defaultValue);
            inputDialog.setTitle("Header");
            inputDialog.setHeaderText(null);
            inputDialog.setContentText("Header for column No. " + number);
            header[i] = inputDialog.showAndWait().orElse(defaultValue);
        }
        return header;
    }
}

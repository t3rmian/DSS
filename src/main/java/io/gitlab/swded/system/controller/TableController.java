package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.Parser;
import io.gitlab.swded.system.model.Value;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.ArrayList;
import java.util.List;

public class TableController {

    @FXML
    private TableView<DataRow> table;
    private ObservableList<DataRow> observableList = FXCollections.observableArrayList();
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
        observableList.addAll(dataRows);
        table.setItems(observableList);
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
        MenuItem toNumeric = new MenuItem("Discretization");
        toNumeric.setOnAction(event -> {
            toDiscrete(columnIndex);
        });
        contextMenu.getItems().add(toNumeric);
        contextMenu.setAutoHide(true);
        return contextMenu;
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
        float min = 0;
        float max = 0;
        for (DataRow row : observableList) {
            float value = row.getValue(columnIndex).getValue();
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        float range = max - min;
        float divisionRange = range / divisionsCount;
        float[] thresholds = new float[divisionsCount];
        thresholds[0] = min + divisionRange;
        for (int i = 1; i < thresholds.length; i++) {
            thresholds[i] = thresholds[i - 1] + divisionRange;
        }
        if (range == 0) {
            return;
        }
        for (DataRow row : observableList) {
            float value = row.getValue(columnIndex).getValue();
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
        for (DataRow row : observableList) {
            String text = row.getValue(columnIndex).getText();
            if (!texts.contains(text)) {
                texts.add(text);
            }
        }
        for (DataRow row : observableList) {
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

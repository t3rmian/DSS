package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.Parser;
import io.gitlab.swded.system.model.Value;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.Optional;

public class TableController {

    @FXML
    private TableView<DataRow> table;
    private ObservableList<DataRow> observableList = FXCollections.observableArrayList();
    private TableColumn<DataRow, ?>[] valueColumns;

    void displayData(Parser parser) {
        DataRow[] dataRows = parser.getData();
        DataRow firstDataRow = dataRows[0];
        String[] header = parser.getHeader();
        if (header == null) {
            header = askUserForHeader(firstDataRow.size());
        }
        //noinspection unchecked
        valueColumns = new TableColumn[header.length];
        for (int i = 0; i < header.length; i++) {
            Value firstColumnValue = firstDataRow.getValue(i);
            final int columnIndex = i;
            if (firstColumnValue.isNumber()) {
                TableColumn<DataRow, Number> valueColumn = new TableColumn<>(header[i]);
                valueColumn.setCellValueFactory(p -> p.getValue().getValue(columnIndex).valueProperty());
                valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
                valueColumns[i] = valueColumn;
            } else {
                TableColumn<DataRow, String> textColumn = new TableColumn<>(header[i]);
                textColumn.setCellValueFactory(p -> p.getValue().getValue(columnIndex).textProperty());
                textColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
                valueColumns[i] = textColumn;
            }
        }
        table.getColumns().addAll(valueColumns);
        observableList.addAll(dataRows);
        table.setItems(observableList);
    }

    private String[] askUserForHeader(int size) {
        String[] header = new String[size];
        for (int i = 0; i < size; i++) {
            int number = i + 1;
            String defaultValue = "C" + number;
            TextInputDialog inputDialog = new TextInputDialog(defaultValue);
            inputDialog.setTitle("Header");
            inputDialog.setContentText("Header for column No. " + number);
            Optional<String> input = inputDialog.showAndWait();
            header[i] = input.orElse(defaultValue);
        }
        return header;
    }
}

package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.Data;
import io.gitlab.swded.system.model.Parser;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.NumberStringConverter;

public class TableController {

    @FXML
    private TableView<Data> table;
    private ObservableList<Data> observableList = FXCollections.observableArrayList();
    private TableColumn<Data, String> classesColumn;
    private TableColumn<Data, Number>[] valueColumns;

    void displayData(Parser parser) {
        String[] header = parser.getHeader();
        //noinspection unchecked
        valueColumns = new TableColumn[header.length - 1];
        for (int i = 0, j = 0; i < header.length; i++) {
            if (i == parser.getClassesColumnIndex()) {
                classesColumn = new TableColumn<>(header[i]);
                classesColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("klass"));
                classesColumn.setCellFactory(TextFieldTableCell.<Data, String>forTableColumn(new DefaultStringConverter()));
            } else {
                final int columnIndex = j;
                TableColumn<Data, Number> valueColumn = new TableColumn<>(header[i]);
                valueColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Data, Number>, ObservableValue<Number>>() {
                    @Override
                    public ObservableValue<Number> call(TableColumn.CellDataFeatures<Data, Number> p) {
                        return p.getValue().valuesProperty()[columnIndex];
                    }
                });
                valueColumn.setCellFactory(TextFieldTableCell.<Data, Number>forTableColumn(new NumberStringConverter()));
                valueColumns[j++] = valueColumn;
            }
        }
        table.getColumns().addAll(valueColumns);
        table.getColumns().add(classesColumn);
        observableList.addAll(parser.getData());
        table.setItems(observableList);
    }
}

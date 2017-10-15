package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.Data;
import io.gitlab.swded.system.model.Parser;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class TableController {

    @FXML
    private TableView<Data> table;
    private ObservableList<Data> observableList = FXCollections.observableArrayList();
    private TableColumn<Data, String> classesColumn;
    private TableColumn<Data, Number>[] valueColumns;

    void displayData(Parser parser) {
        String[] header = parser.getHeader();
        valueColumns = new TableColumn[header.length - 1];
        for (int i = 0, j = 0; i < header.length; i++) {
            if (i == parser.getClassesColumnIndex()) {
                classesColumn = new TableColumn<>(header[i]);
                classesColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("klass"));
            } else {
                final int columnIndex = j;
                TableColumn<Data, Number> valueColumn = new TableColumn<>(header[i]);
                valueColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Data, Number>, ObservableValue<Number>>() {
                    @Override
                    public ObservableValue<Number> call(TableColumn.CellDataFeatures<Data, Number> p) {
                        SimpleFloatProperty simpleFloatProperty = new SimpleFloatProperty((p.getValue().getValues()[columnIndex]));
                        return simpleFloatProperty;
                    }
                });
                valueColumns[j++] = valueColumn;
            }
        }
        table.getColumns().addAll(valueColumns);
        table.getColumns().add(classesColumn);
        observableList.addAll(parser.getData());
        table.setItems(observableList);
    }
}

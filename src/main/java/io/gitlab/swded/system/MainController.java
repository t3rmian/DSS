package io.gitlab.swded.system;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem loadMenuItem;

    @FXML
    private TableView<Data> table;
    private ObservableList<Data> observableList = FXCollections.observableArrayList();
    private TableColumn<Data, String> classesColumn;
    private TableColumn<Data, Number>[] valueColumns;

    @FXML
    private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(getWindow());
        System.out.println("Opening file: " + file.getAbsolutePath());
        readFile(file);
    }

    private void readFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            Parser parser = new Parser(bufferedReader);
            parser.parse();
            displayData(parser);
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initOwner(getWindow());
            errorAlert.setTitle("Error while opening the file");
            errorAlert.setContentText(e.getMessage());
            errorAlert.show();
        }

    }

    private void displayData(Parser parser) {
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

    private Window getWindow() {
        return menuBar.getScene().getWindow();
    }
}

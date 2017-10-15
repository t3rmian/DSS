package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.Parser;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private TableController tableController;

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
            tableController.displayData(parser);
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initOwner(getWindow());
            errorAlert.setTitle("Error while opening the file");
            errorAlert.setContentText(e.getMessage());
            errorAlert.show();
        }

    }

    private Window getWindow() {
        return menuBar.getScene().getWindow();
    }

}

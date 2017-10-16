package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.Parser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.*;
import java.util.Optional;

public class MainController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private TableController tableController;

    private final KeyCombination pasteKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);

    @FXML
    private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(getWindow());
        if (file != null) {
            System.out.println("Opening file: " + file.getAbsolutePath());
            readFile(file);
        }
    }

    private void readFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            readData(bufferedReader);
        } catch (IOException e) {
            printException(e);
        }

    }

    public void postInitialize() {
        getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (pasteKeyCombination.match(ke)) {
                System.out.println("Key Pressed: " + pasteKeyCombination);
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to paste data set from clipboard?", ButtonType.YES, ButtonType.NO);
                confirmationAlert.setTitle("CTRL+V");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.showAndWait()
                        .filter(response -> response == ButtonType.YES)
                        .ifPresent(response -> {
                            final Clipboard clipboard = Clipboard.getSystemClipboard();
                            String content = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
                            try (BufferedReader bufferedReader = new BufferedReader(new StringReader(content))) {
                                readData(bufferedReader);
                            } catch (IOException e) {
                                printException(e);
                            }
                        });
            }
        });
    }

    private void readData(BufferedReader bufferedReader) throws IOException {
        Alert questionAlert = new Alert(Alert.AlertType.CONFIRMATION, "Is header row present in the file?", ButtonType.NO, ButtonType.YES);
        questionAlert.initOwner(getWindow());
        questionAlert.setTitle("Header");
        questionAlert.setHeaderText(null);
        Optional<ButtonType> response = questionAlert.showAndWait();
        boolean headerPresent = false;
        if (response.isPresent() && response.get() == ButtonType.YES) {
            headerPresent = true;
        }
        Parser parser = new Parser(headerPresent);
        parser.parse(bufferedReader);
        tableController.displayData(parser);
    }

    private void printException(IOException e) {
        e.printStackTrace();
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.initOwner(getWindow());
        errorAlert.setTitle("Error while opening the file");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(e.getMessage());
        errorAlert.show();
    }

    private Window getWindow() {
        return getScene().getWindow();
    }

    private Scene getScene() {
        return menuBar.getScene();
    }

    public void showAbout(ActionEvent actionEvent) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION,
                "Project and implementation of decision support system\n" +
                        "Systemy Wspomagania Decyzji / Eksploracja Danych\n" +
                        "Group: PS2\n" +
                        "Authors: Maciej Borowik, Damian Terlecki");
        infoAlert.setTitle("About");
        infoAlert.setHeaderText(null);
        infoAlert.show();
    }
}

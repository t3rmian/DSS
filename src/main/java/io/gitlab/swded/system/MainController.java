package io.gitlab.swded.system;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class MainController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem loadMenuItem;

    @FXML
    private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.showOpenDialog(getWindow());
    }

    private Window getWindow() {
        return menuBar.getScene().getWindow();
    }
}

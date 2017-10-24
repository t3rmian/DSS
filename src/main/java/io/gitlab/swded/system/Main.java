package io.gitlab.swded.system;

import io.gitlab.swded.system.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("SWD System");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.centerOnScreen();
        primaryStage.setOnHidden(event -> System.exit(0));
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreen(false);
        primaryStage.requestFocus();
        MainController mainController = fxmlLoader.getController();
        mainController.postInitialize();
    }


    public static void main(String[] args) {
        launch(args);
    }

}

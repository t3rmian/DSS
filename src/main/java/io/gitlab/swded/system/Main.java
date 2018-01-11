package io.gitlab.swded.system;

import io.gitlab.swded.system.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

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

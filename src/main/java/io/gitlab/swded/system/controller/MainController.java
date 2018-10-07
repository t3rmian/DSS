package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.controller.chart.ChartController;
import io.gitlab.swded.system.controller.chart.ChartInputController;
import io.gitlab.swded.system.controller.classification.ClassificationInputController;
import io.gitlab.swded.system.controller.classification.ClassificationQAInputController;
import io.gitlab.swded.system.controller.classification.KNNClassificationInputController;
import io.gitlab.swded.system.controller.classification.KNNClassificationQAInputController;
import io.gitlab.swded.system.controller.grouping.GroupingInputController;
import io.gitlab.swded.system.controller.grouping.GroupingQAInputController;
import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.data.Parser;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class MainController implements ChartInputController.ChartInputListener, GroupingInputController.GroupingInputListener {

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
                        "Authors: Maciej Borowik, Damian Terlecki\n" +
                        "\nTechnologies and components used:\nJavaFX, Maven, JFreeChart (2d charts), JMathPlot (3d charts), ejm SimpleMatrix");
        infoAlert.setTitle("About");
        infoAlert.setHeaderText(null);
        infoAlert.show();
    }

    public void showHelp(ActionEvent actionEvent) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION,
                "Input file and pasted data should have columns separated by whitespaces.\nRight click on column headers for operations.");
        infoAlert.setTitle("Help");
        infoAlert.setHeaderText(null);
        infoAlert.show();
    }

    public void showChartInput(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/chartInput.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Chart input");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        ChartInputController chartInputController = loader.getController();
        DataRow dataRow = tableController.getData().get(0);
        List<String> header = tableController.getHeader();
        chartInputController.setInputListener(this);
        chartInputController.initializeUI(dataRow, header);
    }

    @Override
    public void onChartConfigSet(int classColumnIndex, int[] valueColumnIndexes) {
        new ChartController().showChart(tableController.getData(), classColumnIndex, valueColumnIndexes, tableController.getHeader());
    }

    public void showKNNClassification(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/knnClassificationInput.fxml"));
        Stage stage = new Stage();
        stage.setTitle("kNN classification input");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        KNNClassificationInputController classificationInputController = loader.getController();
        ObservableList<DataRow> data = tableController.getData();
        DataRow dataRow = data.get(0);
        List<String> header = tableController.getHeader();
        classificationInputController.setData(data);
        classificationInputController.initializeUI(dataRow, header);
    }

    public void showKNNClassificationQA(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/knnClassificationQAInput.fxml"));
        Stage stage = new Stage();
        stage.setTitle("kNN classification QA input");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        KNNClassificationQAInputController classificationQAInputController = loader.getController();
        ObservableList<DataRow> data = tableController.getData();
        DataRow dataRow = data.get(0);
        List<String> header = tableController.getHeader();
        classificationQAInputController.setData(data);
        classificationQAInputController.initializeUI(dataRow, header);
    }

    public void showGrouping(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/groupingInput.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Grouping input");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        GroupingInputController classificationQAInputController = loader.getController();
        ObservableList<DataRow> data = tableController.getData();
        DataRow dataRow = data.get(0);
        List<String> header = tableController.getHeader();
        classificationQAInputController.setListener(this);
        classificationQAInputController.setData(data);
        classificationQAInputController.initializeUI(dataRow, header);
    }


    public void showGroupingQA(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/groupingQAInput.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Grouping QA input");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        GroupingQAInputController classificationQAInputController = loader.getController();
        ObservableList<DataRow> data = tableController.getData();
        DataRow dataRow = data.get(0);
        List<String> header = tableController.getHeader();
        classificationQAInputController.setData(data);
        classificationQAInputController.initializeUI(dataRow, header);
    }

    @Override
    public void onGroupClassification(int[] classes, int groupsCount, Metric metric) {
        tableController.addColumn(classes, "GR_" + metric + "_" + groupsCount);
    }

    public void close(ActionEvent actionEvent) {
        getStage().close();
    }

    public void reset(ActionEvent actionEvent) {
        tableController.reset();
    }

    private Stage getStage() {
        return (Stage) getWindow();
    }

    public void showTreeClassification(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/classificationInput.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Tree classification input");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        ClassificationInputController classificationInputController = loader.getController();
        ObservableList<DataRow> data = tableController.getData();
        DataRow dataRow = data.get(0);
        List<String> header = tableController.getHeader();
        classificationInputController.setData(data);
        classificationInputController.initializeUI(dataRow, header);
    }

    public void showTreeClassificationQA(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/classificationQAInput.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Tree classification QA input");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        ClassificationQAInputController classificationInputController = loader.getController();
        ObservableList<DataRow> data = tableController.getData();
        DataRow dataRow = data.get(0);
        List<String> header = tableController.getHeader();
        classificationInputController.setData(data);
        classificationInputController.initializeUI(dataRow, header);
    }
}

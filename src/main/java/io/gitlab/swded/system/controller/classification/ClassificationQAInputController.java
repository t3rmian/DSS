package io.gitlab.swded.system.controller.classification;

import com.sun.javafx.collections.ObservableListWrapper;
import io.gitlab.swded.system.controller.chart.ChartController;
import io.gitlab.swded.system.controller.chart.ChartInputController;
import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.processing.Classifier;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassificationQAInputController extends ChartInputController {

    private static final DecimalFormat percentageFormat = new DecimalFormat("##.##");
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.####");

    List<DataRow> data;
    @FXML
    ComboBox<Integer> numberComboBox;
    @FXML
    ComboBox<Metric> metricComboBox;

    public void setData(List<DataRow> data) {
        this.data = data;
    }

    public void confirm(ActionEvent actionEvent) {
        if (!validateInput()) {
            return;
        }
        Classifier classifier = createClassifier();
        double quality = classifier.classificationQuality(numberComboBox.getValue(), metricComboBox.getValue());
        printOutput(quality);
    }

    private void printOutput(double quality) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "The classification quality is: "
                + decimalFormat.format(quality) + " (" + percentageFormat.format(quality * 100) + "%)");
        infoAlert.setHeaderText(null);
        infoAlert.setTitle("Classification output");
        infoAlert.show();
    }

    Classifier createClassifier() {
        Classifier classifier = new Classifier(data);
        classifier.setClassColumnIndex(getClassColumnIndex());
        classifier.setValueColumnIndexes(getValueColumnIndexes());
        return classifier;
    }

    @Override
    protected boolean validateInput() {
        return validateSelectedClassColumns();
    }

    public void close(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getTarget()).getScene().getWindow()).close();
    }

    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < data.size() - 1; i++) {
            numbers.add(i);
        }
        numberComboBox.setItems(new ObservableListWrapper<>(numbers));
        numberComboBox.getSelectionModel().selectFirst();
        metricComboBox.setItems(FXCollections.observableArrayList(Metric.values()));
        metricComboBox.getSelectionModel().selectFirst();
    }

    public void allKConfirm(ActionEvent actionEvent) {
        if (!validateInput()) {
            return;
        }
        Classifier classifier = createClassifier();
        List<Double> results = new ArrayList<>();
        numberComboBox.getItems().forEach(index -> results.add(100.0 * classifier.classificationQuality(index, metricComboBox.getValue())));
        new ChartController().showChart(numberComboBox.getItems(), results, Arrays.asList(metricComboBox.getValue() + " QA", "knn count", "Quality %"));
    }

}

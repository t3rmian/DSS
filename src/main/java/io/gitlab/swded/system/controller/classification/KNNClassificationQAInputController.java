package io.gitlab.swded.system.controller.classification;

import com.sun.javafx.collections.ObservableListWrapper;
import io.gitlab.swded.system.Main;
import io.gitlab.swded.system.controller.chart.ChartController;
import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.processing.MachineLearner;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KNNClassificationQAInputController extends ClassificationQAInputController {

    @FXML
    ComboBox<Integer> numberComboBox;
    @FXML
    ComboBox<Metric> metricComboBox;
    volatile private int count = 0;

    @Override
    double calculateClassificationQuality(MachineLearner machineLearner) {
        return machineLearner.classificationQuality(numberComboBox.getValue(), metricComboBox.getValue());
    }

    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
        List<Integer> numbers = IntStream.range(1, data.size()).boxed().collect(Collectors.toList());
        numberComboBox.setItems(new ObservableListWrapper<>(numbers));
        numberComboBox.getSelectionModel().selectFirst();
        metricComboBox.setItems(FXCollections.observableArrayList(Metric.values()));
        metricComboBox.getSelectionModel().selectFirst();
    }

    public void allKConfirm(ActionEvent actionEvent) {
        if (!validateInput()) {
            return;
        }
        count = 0;
        MachineLearner classifier = createClassifier();
        double[] results = new double[numberComboBox.getItems().size()];
        Runnable task = () -> {
            System.out.println("SUBMIT");
            numberComboBox.getItems().parallelStream().forEach(index -> {
                count++;
                System.out.println((100 * (float) count / data.size()) + "%");
                results[index - 1] = 100.0 * classifier.classificationQuality(index, metricComboBox.getValue());
            });
            new ChartController(true).showChart(numberComboBox.getItems(), results, Arrays.asList(metricComboBox.getValue() + " QA", "knn count", "Quality %"));
        };
        Main.executor.submit(task);
    }

}
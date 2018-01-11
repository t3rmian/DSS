package io.gitlab.swded.system.controller.grouping;

import io.gitlab.swded.system.Main;
import io.gitlab.swded.system.controller.chart.ChartController;
import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.processing.ClusteringQAType;
import io.gitlab.swded.system.model.processing.MachineLearner;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GroupingQAInputController extends GroupingInputController {
    @FXML
    ComboBox<Integer> numberComboBox;
    @FXML
    ComboBox<ClusteringQAType> clusteringComboBox;
    @FXML
    ComboBox<Metric> metricComboBox;

    public void onConfirmed(ActionEvent actionEvent) {
        MachineLearner classifier = createMachineLearner();
        Integer groupsCount = numberComboBox.getValue();
        ClusteringQAType clusteringAssessment = clusteringComboBox.getValue();
        Metric metric = metricComboBox.getValue();
        Runnable task = () -> {
            List<Pair<Integer, Double>> pairs = classifier.groupQA(groupsCount, metric, clusteringAssessment);
            new ChartController().showChart(
                    pairs.stream().map(Pair::getKey).collect(Collectors.toList()),
                    pairs.stream().mapToDouble(Pair::getValue).toArray(), Arrays.asList(metric + " " + clusteringAssessment + " similarity QA", "k count (groups)", "Similarity value"));
        };
        Main.executor.submit(task);
    }

    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
        clusteringComboBox.setItems(FXCollections.observableArrayList(ClusteringQAType.values()));
        clusteringComboBox.getSelectionModel().selectFirst();
    }

}

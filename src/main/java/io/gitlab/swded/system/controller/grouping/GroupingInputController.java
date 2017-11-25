package io.gitlab.swded.system.controller.grouping;

import com.sun.javafx.collections.ObservableListWrapper;
import io.gitlab.swded.system.controller.input.ValueColumnsInputController;
import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.processing.MachineLearner;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GroupingInputController extends ValueColumnsInputController {
    List<DataRow> data;
    @FXML
    ComboBox<Integer> numberComboBox;
    @FXML
    ComboBox<Metric> metricComboBox;
    private GroupingInputListener listener;

    public void setListener(GroupingInputListener listener) {
        this.listener = listener;
    }

    public void onConfirmed(ActionEvent actionEvent) {
        MachineLearner classifier = createMachineLearner();
        Integer groupsCount = numberComboBox.getValue();
        Metric metric = metricComboBox.getValue();
        int[] classes = classifier.group(groupsCount, metric).getValue();
        for (int i = 0; i < classes.length; i++) {
            classes[i] += 1;
        }
        listener.onGroupClassification(classes, groupsCount, metric);
        close(actionEvent);
    }

    MachineLearner createMachineLearner() {
        MachineLearner classifier = new MachineLearner(data);
        classifier.setValueColumnIndexes(getValueColumnIndexes());
        return classifier;
    }

    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
        List<Integer> numbers = IntStream.rangeClosed(1, data.size()).boxed().collect(Collectors.toList());
        numberComboBox.setItems(new ObservableListWrapper<>(numbers));
        numberComboBox.getSelectionModel().selectFirst();
        metricComboBox.setItems(FXCollections.observableArrayList(Metric.values()));
        metricComboBox.getSelectionModel().selectFirst();
    }

    public void setData(ObservableList<DataRow> data) {
        this.data = data;
    }

    public interface GroupingInputListener {
        void onGroupClassification(int[] classes, int groupsCount, Metric metric);
    }
}

package io.gitlab.swded.system.controller.grouping;

import com.sun.javafx.collections.ObservableListWrapper;
import io.gitlab.swded.system.controller.chart.ChartController;
import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.Value;
import io.gitlab.swded.system.model.processing.Classifier;
import io.gitlab.swded.system.model.processing.ClusteringAssessment;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GroupingQAInputController {
    @FXML
    protected ListView<String> valueColumnsToSelect;
    @FXML
    protected ListView<String> selectedValueColumns;
    List<String> header;
    List<DataRow> data;
    @FXML
    ComboBox<Integer> numberComboBox;
    @FXML
    ComboBox<ClusteringAssessment> clusteringComboBox;
    @FXML
    ComboBox<Metric> metricComboBox;

    public void selectValueColumn(ActionEvent actionEvent) {
        String selectedItem = valueColumnsToSelect.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        valueColumnsToSelect.getItems().remove(selectedItem);
        selectedValueColumns.getItems().add(selectedItem);
    }

    public void deselectValueColumn(ActionEvent actionEvent) {
        String selectedItem = selectedValueColumns.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        selectedValueColumns.getItems().remove(selectedItem);
        valueColumnsToSelect.getItems().add(selectedItem);
    }

    public void confirm(ActionEvent actionEvent) {
        if (!validateInput()) return;
        Classifier classifier = createClassifier();
        Integer groupsCount = numberComboBox.getValue();
        ClusteringAssessment clusteringAssessment = clusteringComboBox.getValue();
        Metric metric = metricComboBox.getValue();
        List<Pair<Integer, Double>> pairs = classifier.classifyGroupsQA(groupsCount, metric, clusteringAssessment);
        new ChartController().showChart(
                pairs.stream().map(Pair::getKey).collect(Collectors.toList()),
                pairs.stream().mapToDouble(Pair::getValue).toArray(), Arrays.asList(metric + " " + clusteringAssessment + " similarity QA", "k count (groups)", "Similarity value"));

    }

    Classifier createClassifier() {
        Classifier classifier = new Classifier(data);
        classifier.setValueColumnIndexes(getValueColumnIndexes());
        return classifier;
    }

    private int getHeaderIndex(String classHeader) {
        return header.indexOf(classHeader);
    }

    protected final int[] getValueColumnIndexes() {
        return selectedValueColumns.getItems().stream().mapToInt(this::getHeaderIndex).toArray();
    }

    protected boolean validateInput() {
        return validateSelectedValueColumns();
    }

    private boolean validateSelectedValueColumns() {
        if (selectedValueColumns.getItems().size() < 1) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must choose at least one grouping attribute");
            errorAlert.setHeaderText(null);
            errorAlert.setTitle("Invalid columns");
            errorAlert.show();
            return false;
        }
        return true;
    }

    public void close(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getTarget()).getScene().getWindow()).close();
    }

    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        if (defaultDataRow == null || defaultDataRow.getValues() == null) {
            return;
        }
        this.header = header;
        List<Value> values = defaultDataRow.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).isNumber()) {
                valueColumnsToSelect.getItems().add(header.get(i));
            }
        }
        valueColumnsToSelect.getSelectionModel().selectFirst();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < data.size() - 1; i++) {
            numbers.add(i);
        }
        numberComboBox.setItems(new ObservableListWrapper<>(numbers));
        numberComboBox.getSelectionModel().selectFirst();
        metricComboBox.setItems(FXCollections.observableArrayList(Metric.values()));
        metricComboBox.getSelectionModel().selectFirst();
        clusteringComboBox.setItems(FXCollections.observableArrayList(ClusteringAssessment.values()));
        clusteringComboBox.getSelectionModel().selectFirst();
    }

    public void selectAllValueColumns(ActionEvent actionEvent) {
        while (valueColumnsToSelect.getSelectionModel().getSelectedItem() != null) {
            String selectedItem = valueColumnsToSelect.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            valueColumnsToSelect.getItems().remove(selectedItem);
            selectedValueColumns.getItems().add(selectedItem);
        }
    }

    public void setData(ObservableList<DataRow> data) {
        this.data = data;
    }

}

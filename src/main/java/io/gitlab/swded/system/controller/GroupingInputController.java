package io.gitlab.swded.system.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.Value;
import io.gitlab.swded.system.model.processing.Classifier;
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

import java.util.ArrayList;
import java.util.List;

public class GroupingInputController {
    @FXML
    protected ListView<String> valueColumnsToSelect;
    @FXML
    protected ListView<String> selectedValueColumns;
    List<String> header;
    List<DataRow> data;
    @FXML
    ComboBox<Integer> numberComboBox;
    @FXML
    ComboBox<Metric> metricComboBox;
    private GroupingInputListener listener;

    public void setListener(GroupingInputListener listener) {
        this.listener = listener;
    }

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
        Metric metric = metricComboBox.getValue();
        int[] classes = classifier.classifyGroups(groupsCount, metric);
        listener.onGroupClassification(classes, groupsCount, metric);
        close(actionEvent);
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

    interface GroupingInputListener {
        void onGroupClassification(int[] classes, int groupsCount, Metric metric);
    }
}

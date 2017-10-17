package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.Value;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class ChartInputController {
    @FXML
    private ListView<String> valueColumnsToSelect;
    @FXML
    private ListView<String> selectedValueColumns;
    @FXML
    private ListView<String> classColumnsToSelect;
    @FXML
    private ListView<String> selectedClassColumns;
    private ChartInputListener inputListener;
    private DataRow dataRow;
    private List<String> header;

    public void setInputListener(ChartInputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void selectValueColumn(ActionEvent actionEvent) {
        String selectedItem = valueColumnsToSelect.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedValueColumns.getItems().size() == 3) {
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

    public void selectClassColumn(ActionEvent actionEvent) {
        String selectedItem = classColumnsToSelect.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedClassColumns.getItems().size() == 1) {
            return;
        }
        classColumnsToSelect.getItems().remove(selectedItem);
        selectedClassColumns.getItems().add(selectedItem);
    }

    public void deselectClassColumn(ActionEvent actionEvent) {
        String selectedItem = selectedClassColumns.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        selectedClassColumns.getItems().remove(selectedItem);
        classColumnsToSelect.getItems().add(selectedItem);
    }

    public void confirm(ActionEvent actionEvent) {
        if (!validateInput()) return;
        String classHeader = selectedClassColumns.getItems().get(0);
        int[] valueColumnIndexes = selectedValueColumns.getItems().stream().mapToInt(columnHeader -> header.indexOf(columnHeader)).toArray();
        inputListener.onChartConfigSet(header.indexOf(classHeader), valueColumnIndexes);
        close(actionEvent);
    }

    private boolean validateInput() {
        if (selectedValueColumns.getItems().size() < 2 || selectedValueColumns.getItems().size() > 3) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must choose 2 (2d chart) or 3 (3d chart) value columns");
            errorAlert.setHeaderText(null);
            errorAlert.setTitle("Invalid columns");
            errorAlert.show();
            return false;
        }
        if (selectedClassColumns.getItems().size() != 1) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must choose a class column");
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

    public void setData(DataRow defaultDataRow, List<String> header) {
        if (defaultDataRow == null || defaultDataRow.getValues() == null) {
            return;
        }
        this.dataRow = defaultDataRow;
        this.header = header;
        List<Value> values = defaultDataRow.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).isNumber()) {
                valueColumnsToSelect.getItems().add(header.get(i));
            } else {
                classColumnsToSelect.getItems().add(header.get(i));
            }
        }
        valueColumnsToSelect.getSelectionModel().selectFirst();
        classColumnsToSelect.getSelectionModel().selectFirst();
    }

    interface ChartInputListener {

        void onChartConfigSet(int classColumnIndex, int[] valueColumnIndexes);
    }
}

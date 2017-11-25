package io.gitlab.swded.system.controller.input;

import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.data.Value;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.util.List;

public abstract class ValueClassColumnsInputController extends ValueColumnsInputController {
    @FXML
    protected ListView<String> classColumnsToSelect;
    @FXML
    protected ListView<String> selectedClassColumns;

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

    protected final int getClassColumnIndex() {
        return getHeaderIndex(selectedClassColumns.getItems().get(0));
    }

    protected boolean validateInput() {
        return validateSelectedValueColumns() && validateSelectedClassColumns();
    }

    protected boolean validateSelectedClassColumns() {
        if (selectedClassColumns.getItems().size() != 1) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must choose a class column");
            errorAlert.setHeaderText(null);
            errorAlert.setTitle("Invalid columns");
            errorAlert.show();
            return false;
        }
        return true;
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
            classColumnsToSelect.getItems().add(header.get(i));
        }
        valueColumnsToSelect.getSelectionModel().selectFirst();
        classColumnsToSelect.getSelectionModel().selectFirst();
    }

}

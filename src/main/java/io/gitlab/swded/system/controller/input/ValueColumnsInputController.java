package io.gitlab.swded.system.controller.input;

import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.data.Value;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.Stage;

import java.util.List;

abstract public class ValueColumnsInputController {
    @FXML
    protected ListView<String> valueColumnsToSelect;
    @FXML
    protected ListView<String> selectedValueColumns;
    protected List<String> header;

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

    public final void confirm(ActionEvent actionEvent) {
        if (!validateInput()) return;
        onConfirmed(actionEvent);
    }

    abstract protected void onConfirmed(ActionEvent actionEvent);

    int getHeaderIndex(String classHeader) {
        return header.indexOf(classHeader);
    }

    protected final int[] getValueColumnIndexes() {
        return selectedValueColumns.getItems().stream().mapToInt(this::getHeaderIndex).toArray();
    }

    protected boolean validateInput() {
        return validateSelectedValueColumns();
    }

    protected boolean validateSelectedValueColumns() {
        if (selectedValueColumns.getItems().size() < 1) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must choose at least one value column");
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
    }

    public void selectAllValueColumns() {
        MultipleSelectionModel<String> selectionModel = valueColumnsToSelect.getSelectionModel();
        while (selectionModel.getSelectedItem() != null) {
            String selectedItem = selectionModel.getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            valueColumnsToSelect.getItems().remove(selectedItem);
            selectedValueColumns.getItems().add(selectedItem);
        }
    }

}

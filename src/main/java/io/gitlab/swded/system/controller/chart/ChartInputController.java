package io.gitlab.swded.system.controller.chart;

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
    ListView<String> valueColumnsToSelect;
    @FXML
    ListView<String> selectedValueColumns;
    @FXML
    ListView<String> classColumnsToSelect;
    @FXML
    ListView<String> selectedClassColumns;
    List<String> header;
    private ChartInputListener inputListener;
    protected int maxValueColumns = 3;

    public void setInputListener(ChartInputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void selectValueColumn(ActionEvent actionEvent) {
        String selectedItem = valueColumnsToSelect.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedValueColumns.getItems().size() == maxValueColumns) {
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
        int[] valueColumnIndexes = getValueColumnIndexes();
        inputListener.onChartConfigSet(getClassColumnIndex(), valueColumnIndexes);
        close(actionEvent);
    }

    protected final int getClassColumnIndex() {
        return getHeaderIndex(selectedClassColumns.getItems().get(0));
    }

    private int getHeaderIndex(String classHeader) {
        return header.indexOf(classHeader);
    }

    protected final int[] getValueColumnIndexes() {
        return selectedValueColumns.getItems().stream().mapToInt(this::getHeaderIndex).toArray();
    }

    protected boolean validateInput() {
        return validateSelectedValueColumns() && validateSelectedClassColumns();
    }

    private boolean validateSelectedValueColumns() {
        if (selectedValueColumns.getItems().size() < 2 || selectedValueColumns.getItems().size() > 3) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must choose 2 (2d chart) or 3 (3d chart) value columns");
            errorAlert.setHeaderText(null);
            errorAlert.setTitle("Invalid columns");
            errorAlert.show();
            return false;
        }
        return true;
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
            classColumnsToSelect.getItems().add(header.get(i));
        }
        valueColumnsToSelect.getSelectionModel().selectFirst();
        classColumnsToSelect.getSelectionModel().selectFirst();
    }

    public interface ChartInputListener {

        void onChartConfigSet(int classColumnIndex, int[] valueColumnIndexes);
    }
}

package io.gitlab.swded.system.controller.chart;

import io.gitlab.swded.system.controller.input.ValueClassColumnsInputController;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

public class ChartInputController extends ValueClassColumnsInputController {
    private ChartInputListener inputListener;

    public void setInputListener(ChartInputListener inputListener) {
        this.inputListener = inputListener;
    }

    @Override
    public void selectValueColumn(ActionEvent actionEvent) {
        String selectedItem = valueColumnsToSelect.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedValueColumns.getItems().size() == 3) {
            return;
        }
        valueColumnsToSelect.getItems().remove(selectedItem);
        selectedValueColumns.getItems().add(selectedItem);
    }

    @Override
    protected void onConfirmed(ActionEvent actionEvent) {
        int[] valueColumnIndexes = getValueColumnIndexes();
        inputListener.onChartConfigSet(getClassColumnIndex(), valueColumnIndexes);
        close(actionEvent);
    }

    @Override
    protected boolean validateSelectedValueColumns() {
        if (selectedValueColumns.getItems().size() < 2 || selectedValueColumns.getItems().size() > 3) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must choose 2 (2d chart) or 3 (3d chart) value columns");
            errorAlert.setHeaderText(null);
            errorAlert.setTitle("Invalid columns");
            errorAlert.show();
            return false;
        }
        return true;
    }

    public interface ChartInputListener {

        void onChartConfigSet(int classColumnIndex, int[] valueColumnIndexes);
    }
}

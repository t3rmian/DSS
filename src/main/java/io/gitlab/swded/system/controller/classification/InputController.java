package io.gitlab.swded.system.controller.classification;

import io.gitlab.swded.system.model.data.DataRow;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.MultipleSelectionModel;

import java.text.DecimalFormat;

public class InputController {

    private static final DecimalFormat percentageFormat = new DecimalFormat("##.##");
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.####");
    private DataRow defaultDataRow;

    InputController() {
    }

    InputController(DataRow defaultDataRow) {
        this.defaultDataRow = defaultDataRow;
    }

    DataRow parseInputRow(String input, int[] valueColumnIndexes, int classColumnIndex) {
        DataRow inputObject = new DataRow(input.split(" "));
        DataRow unknownObject = new DataRow(defaultDataRow.toString().split(" "));
        unknownObject.getValues().forEach(value -> {
            if (value.isNumber()) {
                value.setValue(-Double.MAX_VALUE);
            } else {
                value.setText("UNKNOWN");
            }
        });
        for (int i = 0; i < valueColumnIndexes.length; i++) {
            unknownObject.getValues().get(valueColumnIndexes[i]).setValue(inputObject.getNumericValue(i));
        }
        unknownObject.getValues().get(classColumnIndex).setText("UNCLASSIFIED");
        return unknownObject;
    }

    void printClassificationOutput(String aClass) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "The object has been classified as: " + aClass);
        infoAlert.setHeaderText(null);
        infoAlert.setTitle("Classification output");
        infoAlert.show();
    }

    public void printClassificationQAOutput(double quality) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "The classification quality is: "
                + decimalFormat.format(quality) + " (" + percentageFormat.format(quality * 100) + "%)");
        infoAlert.setHeaderText(null);
        infoAlert.setTitle("Classification output");
        infoAlert.show();
    }

}

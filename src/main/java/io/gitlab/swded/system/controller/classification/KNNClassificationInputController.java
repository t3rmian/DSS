package io.gitlab.swded.system.controller.classification;

import io.gitlab.swded.system.Main;
import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.processing.MachineLearner;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KNNClassificationInputController extends KNNClassificationQAInputController {

    @FXML
    private TextField inputTextField;

    @Override
    public void onConfirmed(ActionEvent actionEvent) {
        DataRow unknownObject = inputController.parseInputRow(inputTextField.getText(), getValueColumnIndexes(), getClassColumnIndex());
        MachineLearner classifier = createClassifier();
        Runnable task = () -> {
            String aClass = classifier.classify(unknownObject, numberComboBox.getValue(), metricComboBox.getValue());
            unknownObject.getValues().get(getClassColumnIndex()).setText(aClass);
            inputController.printClassificationOutput(aClass);
        };
        Main.executor.submit(task);
    }

    @Override
    protected boolean validateInput() {
        return validateSelectedClassColumns();
    }

    @Override
    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
        metricComboBox.setItems(FXCollections.observableArrayList(Metric.values()));
        metricComboBox.getSelectionModel().selectFirst();
    }

}

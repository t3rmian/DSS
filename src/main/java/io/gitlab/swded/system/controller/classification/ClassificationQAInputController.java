package io.gitlab.swded.system.controller.classification;

import io.gitlab.swded.system.controller.input.ValueClassColumnsInputController;
import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.processing.MachineLearner;
import javafx.event.ActionEvent;

import java.util.List;

public class ClassificationQAInputController extends ValueClassColumnsInputController {

    List<DataRow> data;
    InputController inputController;

    public void setData(List<DataRow> data) {
        this.data = data;
    }

    protected void onConfirmed(ActionEvent actionEvent) {
        MachineLearner classifier = createClassifier();
        double quality = calculateClassificationQuality(classifier);
        inputController.printClassificationQAOutput(quality);
    }

    double calculateClassificationQuality(MachineLearner machineLearner) {
        MachineLearner.TreeBuilder treeBuilder = machineLearner.getTreeBuilder();
        return machineLearner.classificationQuality(treeBuilder.buildDecisionTree());
    }

    MachineLearner createClassifier() {
        MachineLearner classifier = new MachineLearner(data);
        classifier.setClassColumnIndex(getClassColumnIndex());
        classifier.setValueColumnIndexes(getValueColumnIndexes());
        return classifier;
    }

    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
        inputController = new InputController(defaultDataRow);
    }

}

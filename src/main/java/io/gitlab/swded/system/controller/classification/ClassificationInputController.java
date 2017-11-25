package io.gitlab.swded.system.controller.classification;

import io.gitlab.swded.system.controller.DecisionTreeController;
import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.processing.MachineLearner;
import io.gitlab.swded.system.model.processing.tree.DataTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ClassificationInputController extends ClassificationQAInputController {

    @FXML
    private TextField inputTextField;
    private DataTree decisionTree;

    @Override
    public void onConfirmed(ActionEvent actionEvent) {
        DataRow unknownObject = inputController.parseInputRow(inputTextField.getText(), getValueColumnIndexes(), getClassColumnIndex());
        MachineLearner classifier = createClassifier();
        MachineLearner.TreeBuilder treeBuilder = classifier.getTreeBuilder();
        decisionTree = treeBuilder.buildDecisionTree();
        String aClass = classifier.classify(unknownObject, decisionTree);
        unknownObject.getValues().get(getClassColumnIndex()).setText(aClass);
        inputController.printClassificationOutput(aClass);
    }

    @Override
    protected boolean validateInput() {
        return validateSelectedClassColumns();
    }

    @Override
    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
    }

    public void displayTree(ActionEvent actionEvent) throws IOException {
        if (decisionTree == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "You must classify an object first");
            errorAlert.setHeaderText(null);
            errorAlert.setTitle("Invalid columns");
            errorAlert.show();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/decisionTree.fxml"));
        Stage stage = new Stage();
        stage.setTitle("DataTree classification visualization");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        DecisionTreeController decisionTreeController = loader.getController();
        decisionTreeController.setHeader(header);
        decisionTreeController.showTree(decisionTree);
    }

}

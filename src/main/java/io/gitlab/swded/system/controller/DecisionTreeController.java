package io.gitlab.swded.system.controller;

import io.gitlab.swded.system.model.processing.tree.DataNode;
import io.gitlab.swded.system.model.processing.tree.DataTree;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.Enumeration;
import java.util.List;

public class DecisionTreeController {
    @FXML
    TreeView treeView;
    private List<String> header;

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public void showTree(DataTree tree) {
        DataNode root = tree.getRoot();
        treeView.setRoot(new TreeItem<>("Decision tree | branching by: " + header.get(root.getSplitIndex())));
        displayTree(root, treeView.getRoot());
    }

    private void displayTree(DataNode root, TreeItem treeViewRoot) {
        Enumeration children = root.children();
        while (children.hasMoreElements()) {
            DataNode node = (DataNode) children.nextElement();
            Integer splitIndex = node.getSplitIndex();
            TreeItem item = new TreeItem<>(splitIndex == null ? node.getPossibleData().toString() : header.get(splitIndex));
            treeViewRoot.getChildren().add(item);
            displayTree(node, item);
        }
    }

}

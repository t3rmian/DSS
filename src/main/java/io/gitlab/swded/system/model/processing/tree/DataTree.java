package io.gitlab.swded.system.model.processing.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class DataTree extends DefaultTreeModel {
    public DataTree(TreeNode root) {
        super(root);
    }

    public DataNode getRoot() {
        return (DataNode) this.root;
    }
}

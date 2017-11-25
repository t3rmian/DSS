package io.gitlab.swded.system.model.processing.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class Tree extends DefaultTreeModel {
    public Tree(TreeNode root) {
        super(root);
    }
}

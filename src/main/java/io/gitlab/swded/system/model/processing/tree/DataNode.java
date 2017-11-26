package io.gitlab.swded.system.model.processing.tree;

import io.gitlab.swded.system.model.data.DataRow;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataNode extends DefaultMutableTreeNode {
    private Integer splitIndex;
    private List<DataRow> possibleData;

    public void setSplitIndex(Integer splitIndex) {
        this.splitIndex = splitIndex;
    }

    public DataNode getNextDecisionNode(DataRow dataRow) {
        if (isLeaf()) {
            return null;
        }
        List<DataNode> possiblePaths = (List<DataNode>) this.children.stream()
                .filter(child -> ((DataNode) child).possibleData.stream()
                        .map(row -> row.getNumericValue(splitIndex))
                        .collect(Collectors.toList())
                        .contains(dataRow.getNumericValue(splitIndex)))
                .collect(Collectors.toList());
        if (possiblePaths.size() > 1) {
            System.err.println("Multiple classes after branching by value");
            return null;
        } else if (possiblePaths.isEmpty()) {
            System.err.println("Value not present in dataset");
            return null;
        } else {
            return possiblePaths.get(0);
        }
    }

    public void setPossibleData(List<DataRow> possibleData) {
        this.possibleData = possibleData;
    }

    public List<DataRow> getPossibleData() {
        return possibleData;
    }

    public Integer getSplitIndex() {
        return splitIndex;
    }

    public String getMostPossibleClass(int classIndex) {
        return possibleData.stream()
                .collect(Collectors.groupingBy(row -> row.getTextValue(classIndex), Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .get().getKey();
    }
}

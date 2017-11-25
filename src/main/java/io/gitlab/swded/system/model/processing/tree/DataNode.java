package io.gitlab.swded.system.model.processing.tree;

import io.gitlab.swded.system.model.data.DataRow;

import javax.swing.tree.DefaultMutableTreeNode;
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
        Map<String, Long> countedClasses = possibleData.stream().collect(Collectors.groupingBy(row -> row.getTextValue(classIndex), Collectors.counting()));
        long sum = countedClasses.values().stream().mapToLong(value -> value).sum();
        long accumulator = 0;
        double randomValue = Math.random();
        for (Map.Entry<String, Long> countedClass : countedClasses.entrySet()) {
            accumulator += countedClass.getValue();
            if (((double) accumulator / sum) >= randomValue) {
                return countedClass.getKey();
            }
        }
        throw new RuntimeException("Programmer mistake, should return most probable class");
    }
}

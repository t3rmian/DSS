package io.gitlab.swded.system.model.processing.tree;

import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.data.Value;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class DataNode extends DefaultMutableTreeNode implements DataRow {
    private final DataRow dataRow;

    public DataNode(DataRow dataRow) {
        this.dataRow = dataRow;
    }

    @Override
    public List<Value> getValues() {
        return dataRow.getValues();
    }

    @Override
    public Value getValue(int i) {
        return dataRow.getValue(i);
    }

    @Override
    public double getNumericValue(int i) {
        return dataRow.getNumericValue(i);
    }

    @Override
    public String getTextValue(int i) {
        return dataRow.getTextValue(i);
    }

    @Override
    public int size() {
        return dataRow.size();
    }

    @Override
    public void addValue(Value value) {
        dataRow.addValue(value);
    }

    @Override
    public DataRow clear() {
        return dataRow.clear();
    }

    @Override
    public void add(DataRow dataRow) {
        dataRow.add(dataRow);
    }

    @Override
    public String toString() {
        return dataRow.toString();
    }
}

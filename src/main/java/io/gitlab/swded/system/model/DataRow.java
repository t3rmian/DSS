package io.gitlab.swded.system.model;

import java.util.ArrayList;
import java.util.List;

public class DataRow {
    List<Value> values = new ArrayList<>();

    public DataRow(String[] values) {
        for (int i = 0; i < values.length; i++) {
            this.values.add(new Value(values[i]));
        }
    }

    public List<Value> getValues() {
        return values;
    }

    public Value getValue(int i) {
        return values.get(i);
    }

    public int size() {
        return values.size();
    }

    public void addValue(Value value) {
        values.add(value);
    }
}

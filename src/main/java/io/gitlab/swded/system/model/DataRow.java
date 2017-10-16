package io.gitlab.swded.system.model;

public class DataRow {
    Value[] values;

    public DataRow(String[] values) {
        this.values = new Value[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = new Value(values[i]);
        }
    }

    public Value[] getValues() {
        return values;
    }

    public Value getValue(int i) {
        return values[i];
    }

    public int size() {
        return values.length;
    }

}

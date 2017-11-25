package io.gitlab.swded.system.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

    public double getNumericValue(int i) {
        return values.get(i).getValue();
    }

    public String getTextValue(int i) {
        return values.get(i).getText();
    }

    public int size() {
        return values.size();
    }

    public void addValue(Value value) {
        values.add(value);
    }

    public DataRow clear() {
        values.stream().filter(Value::isNumber).forEach(value -> value.setValue(0));
        return this;
    }

    public void add(DataRow dataRow) {
        IntStream.range(0, values.size())
                .filter(index -> values.get(index).isNumber())
                .forEach(index -> values.get(index).addValue(dataRow.getNumericValue(index)));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        values.forEach(value -> sb.append(value.getText()).append(" "));
        return sb.toString();
    }
}

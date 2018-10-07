package io.gitlab.swded.system.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DefaultDataRow implements DataRow {
    private List<Value> values = new ArrayList<>();

    public DefaultDataRow(String[] values) {
        for (String value : values) {
            this.values.add(new Value(value));
        }
    }

    @Override
    public List<Value> getValues() {
        return values;
    }

    @Override
    public Value getValue(int i) {
        return values.get(i);
    }

    @Override
    public double getNumericValue(int i) {
        return values.get(i).getValue();
    }

    @Override
    public String getTextValue(int i) {
        return values.get(i).getText();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public void addValue(Value value) {
        values.add(value);
    }

    @Override
    public DefaultDataRow clear() {
        values.stream().filter(Value::isNumber).forEach(value -> value.setValue(0));
        return this;
    }

    @Override
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

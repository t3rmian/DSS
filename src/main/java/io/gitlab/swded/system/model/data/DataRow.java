package io.gitlab.swded.system.model.data;

import java.util.List;

public interface DataRow {
    List<Value> getValues();

    Value getValue(int i);

    double getNumericValue(int i);

    String getTextValue(int i);

    int size();

    void addValue(Value value);

    io.gitlab.swded.system.model.data.DataRow clear();

    void add(io.gitlab.swded.system.model.data.DataRow dataRow);

    String toString();
}
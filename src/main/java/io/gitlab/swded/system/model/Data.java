package io.gitlab.swded.system.model;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Arrays;

public class Data {
    SimpleFloatProperty[] values;
    SimpleStringProperty klass;

    public Data(float[] values, String klass) {
        this.values = new SimpleFloatProperty[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = new SimpleFloatProperty(values[i]);
        }
        this.klass = new SimpleStringProperty(klass);
    }

    public float[] getValues() {
        float[] values = new float[this.values.length];
        for (int i =0; i < this.values.length; i++) {
            values[i] = this.values[i].get();
        }
        return values;
    }

    public void setValues(float[] values) {
        for (int i = 0; i < values.length; i++) {
            this.values[i].set(values[i]);
        }
    }

    public SimpleFloatProperty[] valuesProperty() {
        return this.values;
    }

    public String getKlass() {
        return klass.get();
    }

    public void setKlass(String klass) {
        this.klass.set(klass);
    }

    public SimpleStringProperty klassProperty() {
        return this.klass;
    }

    @Override
    public String toString() {
        return "Data{" +
                "values=" + Arrays.toString(values) +
                ", klass='" + klass + '\'' +
                '}';
    }
}

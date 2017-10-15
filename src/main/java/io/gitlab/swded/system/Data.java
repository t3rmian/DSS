package io.gitlab.swded.system;

public class Data {
    Float[] values;
    String klass;

    public Data(Float[] values, String klass) {
        this.values = values;
        this.klass = klass;
    }

    public Float[] getValues() {
        return values;
    }

    public void setValues(Float[] values) {
        this.values = values;
    }

    public String getKlass() {
        return klass;
    }

    public void setKlass(String klass) {
        this.klass = klass;
    }
}

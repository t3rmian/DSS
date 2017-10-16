package io.gitlab.swded.system.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Value {
    private StringProperty text;
    private FloatProperty value;

    public Value(String value) {
        try {
            this.value = new SimpleFloatProperty(Float.parseFloat(value));
        } finally {
            text = new SimpleStringProperty(value);
        }
    }

    public boolean isNumber() {
        return value != null;
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public float getValue() {
        return value.get();
    }

    public FloatProperty valueProperty() {
        return value;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public void setValue(float value) {
        this.value.set(value);
    }

}

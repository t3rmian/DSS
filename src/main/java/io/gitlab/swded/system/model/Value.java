package io.gitlab.swded.system.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Value {
    private StringProperty text;
    private DoubleProperty value;

    public Value(String value) {
        try {
            this.value = new SimpleDoubleProperty(Double.parseDouble(value));
        } catch (NumberFormatException nfe) {
        } finally {
            text = new SimpleStringProperty(value);
        }
    }

    public Value(int value) {
        this.value = new SimpleDoubleProperty(value);
    }

    public Value(double value) {
        this.value = new SimpleDoubleProperty(value);
    }

    public boolean isNumber() {
        return value != null;
    }

    public String getText() {
        if (text == null) {
            String textNumber = String.valueOf(value.get());
            return !textNumber.contains(".") ? textNumber : textNumber.replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public double getValue() {
        try {
            return value.get();
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public void setValue(double value) {
        this.value.set(value);
        this.setText(String.valueOf(value));
    }

}

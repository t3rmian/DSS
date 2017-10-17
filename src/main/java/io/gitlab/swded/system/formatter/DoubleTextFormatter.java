package io.gitlab.swded.system.formatter;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class DoubleTextFormatter extends TextFormatter<Double> {

    public DoubleTextFormatter() {
        super(new DoubleStringConverter(), 0.0, new DoubleChangeUnaryOperator());
    }

    private static class DoubleStringConverter extends StringConverter<Double> {

        @Override
        public Double fromString(String s) {
            if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                return 0.0;
            } else {
                return Double.valueOf(s);
            }
        }


        @Override
        public String toString(Double d) {
            return d.toString();
        }
    }

    private static class DoubleChangeUnaryOperator implements UnaryOperator<Change> {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        @Override
        public Change apply(Change change) {
            String text = change.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return change;
            } else {
                return null;
            }
        }
    }
}

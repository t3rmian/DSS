package io.gitlab.swded.system.view;

import io.gitlab.swded.system.formatter.DoubleTextFormatter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;


public class RangeDialog extends Dialog<Pair<Double, Double>> {
    public RangeDialog(String intervalFrom, String intervalTo) {
        this.setTitle("Interval");
        this.setHeaderText(null);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));

        javafx.scene.control.TextField from = new javafx.scene.control.TextField();
        from.setPromptText("From");
        from.setTextFormatter(new DoubleTextFormatter());
        from.setText(intervalFrom);
        javafx.scene.control.TextField to = new javafx.scene.control.TextField();
        to.setPromptText("To");
        to.setTextFormatter(new DoubleTextFormatter());
        to.setText(intervalTo);

        gridPane.add(new javafx.scene.control.Label("From:"), 0, 0);
        gridPane.add(from, 1, 0);
        gridPane.add(new javafx.scene.control.Label("To:"), 2, 0);
        gridPane.add(to, 3, 0);

        this.getDialogPane().setContent(gridPane);
        Platform.runLater(from::requestFocus);
        this.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(
                        Double.parseDouble(from.getText()),
                        Double.parseDouble(to.getText())
                );
            }
            return null;
        });
    }

}

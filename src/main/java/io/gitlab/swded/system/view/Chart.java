package io.gitlab.swded.system.view;

import javafx.stage.Stage;

public abstract class Chart {
    final Stage stage = new Stage();
    final int width = 800;
    final int height = 600;

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}

package io.gitlab.swded.system.view;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.Objects;

class Browser extends Region {

    private final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    Browser(String htmlFileName) {
        getStyleClass().add("browser");
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader().getResource("html/" + htmlFileName)).toExternalForm());
        getChildren().add(browser);
    }

    @Override
    protected void layoutChildren() {
        layoutInArea(browser, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return getWidth();
    }

    @Override
    protected double computePrefHeight(double width) {
        return getHeight();
    }
}

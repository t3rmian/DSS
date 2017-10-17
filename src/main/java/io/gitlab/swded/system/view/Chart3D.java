package io.gitlab.swded.system.view;

import com.jogamp.opengl.GLException;
import io.gitlab.swded.system.model.DataRow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.colors.Color;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.util.List;
import java.util.stream.Collectors;

public class Chart3D extends Chart {
    private static int[] COLOR_VALUES = new int[]{
            0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF, 0x000000,
            0x800000, 0x008000, 0x000080, 0x808000, 0x800080, 0x008080, 0x808080,
            0xC00000, 0x00C000, 0x0000C0, 0xC0C000, 0xC000C0, 0x00C0C0, 0xC0C0C0,
            0x400000, 0x004000, 0x000040, 0x404000, 0x400040, 0x004040, 0x404040,
            0x200000, 0x002000, 0x000020, 0x202000, 0x200020, 0x002020, 0x202020,
            0x600000, 0x006000, 0x000060, 0x606000, 0x600060, 0x006060, 0x606060,
            0xA00000, 0x00A000, 0x0000A0, 0xA0A000, 0xA000A0, 0x00A0A0, 0xA0A0A0,
            0xE00000, 0x00E000, 0x0000E0, 0xE0E000, 0xE000E0, 0x00E0E0, 0xE0E0E0,
    };
    private static final Color[] DEFAULT_COLOR_PALETTE = new Color[COLOR_VALUES.length];

    static {
        for (int i = 0; i < COLOR_VALUES.length; i++) {
            java.awt.Color color = java.awt.Color.decode(String.format("#%06X", COLOR_VALUES[i]));
            DEFAULT_COLOR_PALETTE[i] = new Color(color.getRed(), color.getBlue(), color.getBlue());
        }
    }

    private final StackPane pane = new StackPane();
    private final Scene scene = new Scene(pane, width, height);
    private final List<DataRow> data;
    private final int classColumnIndex;
    private final int[] valueColumnIndexes;
    private final List<String> header;
    private static final int DECIMAL_PRECISION = 2;

    private AWTChart chart;
    private List<String> chartClasses;

    public Chart3D(List<DataRow> data, int classColumnIndex, int[] valueColumnIndexes, List<String> header) {
        this.data = data;
        this.classColumnIndex = classColumnIndex;
        this.valueColumnIndexes = valueColumnIndexes;
        this.header = header;
        stage.setScene(scene);
        stage.setTitle("3D Chart");
        pane.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.web("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void show() {
        createChart();
        scene.getWindow().setOnCloseRequest(event -> chart.dispose());
        setUpAxes();
        setUpUI();
        addSceneSizeChangedListener(chart, scene, new Insets(0, 100, 50, 0));
        super.show();
    }

    private void createChart() {
        int size = data.size();
        double x;
        double y;
        double z;

        Coord3d[] points = new Coord3d[size];
        Color[] colors = new Color[size];
        chartClasses = data.stream().map(row -> row.getValues().get(classColumnIndex).getText()).distinct().collect(Collectors.toList());
        for (int i = 0; i < size; i++) {
            x = data.get(i).getValue(valueColumnIndexes[0]).getValue();
            y = data.get(i).getValue(valueColumnIndexes[1]).getValue();
            z = data.get(i).getValue(valueColumnIndexes[2]).getValue();
            String klass = data.get(i).getValue(classColumnIndex).getText();
            points[i] = new Coord3d(x, y, z);
            int classIndex = chartClasses.indexOf(klass);
            if (classIndex >= DEFAULT_COLOR_PALETTE.length) {
                colors[i] = DEFAULT_COLOR_PALETTE[classIndex % DEFAULT_COLOR_PALETTE.length];
                System.err.println("Exceeded default color palette!");
            } else {
                colors[i] = DEFAULT_COLOR_PALETTE[classIndex];
            }
        }
        Scatter scatter = new Scatter(points, colors, 2000f / size);
        chart = (AWTChart) JavaFXChartFactory.chart(Quality.Nicest, "offscreen");
        chart.getScene().getGraph().add(scatter);
    }

    private void setUpUI() {
        ImageView imageView = getChartImageView(chart);
        GridPane legendPane = createLegend();
        Label titleLabel = createChartTitle();
        pane.getChildren().add(titleLabel);
        pane.getChildren().add(imageView);
        pane.getChildren().add(legendPane);
        legendPane.setMaxWidth(200);
        setAlignment(imageView, legendPane, titleLabel);
        setMargins(imageView, legendPane, titleLabel);
    }

    private Label createChartTitle() {
        String title = header.get(valueColumnIndexes[0]) + " \\ " +
                header.get(valueColumnIndexes[1]) + " | " +
                header.get(valueColumnIndexes[2]) + " for " + header.get(classColumnIndex);
        return new Label(title);
    }

    private ImageView getChartImageView(AWTChart chart) {
        JavaFXChartFactory factory = new JavaFXChartFactory();
        return factory.bindImageView(chart);
    }

    private void setUpAxes() {
        chart.getView().getAxe().getLayout().setXAxeLabel(header.get(valueColumnIndexes[0]));
        chart.getView().getAxe().getLayout().setYAxeLabel(header.get(valueColumnIndexes[1]));
        chart.getView().getAxe().getLayout().setZAxeLabel(header.get(valueColumnIndexes[2]));
        chart.getAxeLayout().setXTickRenderer(new FixedDecimalTickRenderer(DECIMAL_PRECISION));
        chart.getAxeLayout().setYTickRenderer(new FixedDecimalTickRenderer(DECIMAL_PRECISION));
        chart.getAxeLayout().setZTickRenderer(new FixedDecimalTickRenderer(DECIMAL_PRECISION));
    }

    private GridPane createLegend() {
        GridPane legendPane = new GridPane();
        legendPane.setVgap(5);
        for (int i = 0; i < chartClasses.size(); i++) {
            Color color = DEFAULT_COLOR_PALETTE[i % DEFAULT_COLOR_PALETTE.length];
            legendPane.add(new javafx.scene.shape.Rectangle(20, 20,
                            javafx.scene.paint.Color.color((double) color.r, (double) color.g, (double) color.b)),
                    0, i);
            legendPane.add(new Label(chartClasses.get(i)), 1, i);
        }
        return legendPane;
    }

    private void setMargins(ImageView imageView, GridPane legendPane, Label titleLabel) {
        StackPane.setMargin(legendPane, new Insets(0, 20, 0, 0));
        StackPane.setMargin(titleLabel, new Insets(20, 0, 0, 0));
        StackPane.setMargin(imageView, new Insets(50, 100, 0, 0));
    }

    private void setAlignment(ImageView imageView, GridPane legendPane, Label titleLabel) {
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(imageView, Pos.TOP_LEFT);
        StackPane.setAlignment(legendPane, Pos.CENTER_RIGHT);
        legendPane.setAlignment(Pos.CENTER_RIGHT);
    }

    private void addSceneSizeChangedListener(final AWTChart chart, final Scene scene, Insets offset) {
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> resetTo(chart, scene.widthProperty().get() - offset.getRight() - offset.getLeft(), scene.heightProperty().get() - offset.getBottom() - offset.getTop()));
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> resetTo(chart, scene.widthProperty().get() - offset.getRight() - offset.getLeft(), scene.heightProperty().get() - offset.getBottom() - offset.getTop()));
    }

    private void resetTo(AWTChart chart, double width, double height) {
        try {
            if (chart.getCanvas() instanceof OffscreenCanvas) {
                OffscreenCanvas canvas = (OffscreenCanvas) chart.getCanvas();
                canvas.initBuffer(canvas.getCapabilities(), (int) width, (int) height);
                chart.render();
            } else {
                System.err.println("NOT AN OFFSCREEN CANVAS!");
            }
        } catch (GLException gle) {
        }
    }

}

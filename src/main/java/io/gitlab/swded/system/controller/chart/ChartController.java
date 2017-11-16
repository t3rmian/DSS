package io.gitlab.swded.system.controller.chart;

import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.XYZSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.math.plot.Plot3DPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartController {

    private final boolean percentage;

    public ChartController() {
        percentage = false;
    }

    public ChartController(boolean percentage) {
        this.percentage = percentage;
    }

    public void showChart(List<DataRow> data, int classColumnIndex, int[] valueColumnIndexes, List<String> header) {
        JFrame chartFrame;
        if (valueColumnIndexes.length == 2) {
            chartFrame = create2Dchart(data, classColumnIndex, valueColumnIndexes, header);
        } else {
            chartFrame = create3Dchart(data, classColumnIndex, valueColumnIndexes, header);
        }
        chartFrame.setSize(800, 600);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    public void showChart(List<? extends Number> x, double[] y, List<String> titleXYLabels) {
        JFrame chartFrame = create2Dchart(x, y, titleXYLabels);
        chartFrame.setSize(800, 600);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    private JFrame create3Dchart(List<DataRow> data, int classColumnIndex, int[] valueColumnIndexes, List<String> header) {
        String chartTitle = header.get(valueColumnIndexes[0]) + " \\ " + header.get(valueColumnIndexes[1]) + " | " + header.get(valueColumnIndexes[2]) + " for " + header.get(classColumnIndex);
        String xAxisTitle = header.get(valueColumnIndexes[0]);
        String yAxisTitle = header.get(valueColumnIndexes[1]);
        String zAxisTitle = header.get(valueColumnIndexes[2]);


        Plot3DPanel plot = new Plot3DPanel("SOUTH");
        plot.setAxisLabels(xAxisTitle, yAxisTitle, zAxisTitle);

        Map<String, XYZSeries> classifiedSeries = new HashMap<>();
        for (DataRow row : data) {
            String aClass = row.getValue(classColumnIndex).getText();
            if (!classifiedSeries.containsKey(aClass)) {
                classifiedSeries.put(aClass, new XYZSeries());
            }
            classifiedSeries.get(aClass).add(
                    row.getValue(valueColumnIndexes[0]).getValue(),
                    row.getValue(valueColumnIndexes[1]).getValue(),
                    row.getValue(valueColumnIndexes[2]).getValue());
        }
        classifiedSeries.forEach((aClass, xyzSeries) -> plot.addScatterPlot(aClass,
                xyzSeries.getXSeries(),
                xyzSeries.getYSeries(),
                xyzSeries.getZSeries()));

        JFrame frame = new JFrame("3D plot of " + chartTitle);
        frame.setContentPane(plot);
        return frame;
    }

    private JFrame create2Dchart(List<DataRow> data, int classColumnIndex, int[] valueColumnIndexes, List<String> header) {
        String chartTitle = header.get(valueColumnIndexes[0]) + " \\ " + header.get(valueColumnIndexes[1]) + " for " + header.get(classColumnIndex);
        String xAxisTitle = header.get(valueColumnIndexes[0]);
        String yAxisTitle = header.get(valueColumnIndexes[1]);

        XYSeriesCollection dataSet = new XYSeriesCollection();
        Map<String, XYSeries> classifiedSeries = new HashMap<>();
        for (DataRow row : data) {
            String aClass = row.getValue(classColumnIndex).getText();
            if (!classifiedSeries.containsKey(aClass)) {
                classifiedSeries.put(aClass, new XYSeries(aClass));
            }
            classifiedSeries.get(aClass).add(row.getValue(valueColumnIndexes[0]).getValue(), row.getValue(valueColumnIndexes[1]).getValue());
        }
        classifiedSeries.values().forEach(dataSet::addSeries);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "", xAxisTitle, yAxisTitle,
                dataSet, PlotOrientation.VERTICAL, true, true, false);

        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setDomainGridlinePaint(Color.GRAY);
        chart.getXYPlot().setRangeGridlinePaint(Color.GRAY);

        return new ChartFrame("2D Chart of " + chartTitle, chart);
    }

    private JFrame create2Dchart(List<? extends Number> x, double[] y, List<String> titleXYLabels) {
        String chartTitle = titleXYLabels.get(0);
        String xAxisTitle = titleXYLabels.get(1);
        String yAxisTitle = titleXYLabels.get(2);

        XYSeries categoryDataset = new XYSeries(chartTitle);
        for (int i = 0; i < x.size(); i++) {
            categoryDataset.add(x.get(i).intValue(), y[i]);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle, xAxisTitle, yAxisTitle,
                new XYSeriesCollection(categoryDataset), PlotOrientation.VERTICAL, false, true, false);

        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setDomainGridlinePaint(Color.GRAY);
        chart.getXYPlot().setRangeGridlinePaint(Color.GRAY);
        if (percentage) {
            chart.getXYPlot().getRangeAxis().setUpperBound(100);
        }
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2f));
        renderer.setAutoPopulateSeriesShape(false);
        chart.getXYPlot().setRenderer(renderer);

        return new ChartFrame("2D Chart of " + chartTitle, chart);
    }

}

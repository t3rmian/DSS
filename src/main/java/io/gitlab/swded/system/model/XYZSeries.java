package io.gitlab.swded.system.model;

import java.util.ArrayList;
import java.util.List;

public class XYZSeries {
    private List<Double> xSeries = new ArrayList<>();
    private List<Double> ySeries = new ArrayList<>();
    private List<Double> zSeries = new ArrayList<>();

    public void add(double x, double y, double z) {
        this.xSeries.add(x);
        this.ySeries.add(y);
        this.zSeries.add(z);
    }

    public double[] getXSeries() {
        return toPrimitiveDoubleArray(xSeries);
    }

    public double[] getYSeries() {
        return toPrimitiveDoubleArray(ySeries);
    }

    public double[] getZSeries() {
        return toPrimitiveDoubleArray(zSeries);
    }

    private double[] toPrimitiveDoubleArray(List<Double> doubleList) {
        double[] doubleArray = new double[doubleList.size()];
        for (int i = 0; i < doubleList.size(); i++) {
            doubleArray[i] = doubleList.get(i);
        }
        return doubleArray;
    }
}

package io.gitlab.swded.system.model.processing;

import io.gitlab.swded.system.model.DataRow;

import java.util.List;

class Calculator {

    private Metric metric;
    private final List<DataRow> data;
    private int[] indexes;

    public Calculator(List<DataRow> data) {
        this.data = data;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public void setColumnIndexes(int[] indexes) {
        this.indexes = indexes;
    }

    public double calculateDistance(DataRow v1, DataRow v2) {
        switch (metric) {
            case EUCLIDEAN:
                return euclideanDistance(v1, v2, indexes);
            case MANHATTAN:
                return manhattanDistance(v1, v2, indexes);
            case INFINITE:
                return infiniteDistance(v1, v2, indexes);
            case MAHALANOBIS:
                return mahalanobisDistance(data, v1, v2, indexes);
            default:
                throw new RuntimeException("metric not set");
        }
    }

    public double euclideanDistance(DataRow v1, DataRow v2, int[] indexes) {
        double result = 0;
        for (int index : indexes) {
            double diff = v1.getNumericValue(index) - v2.getNumericValue(index);
            result += diff * diff;
        }
        return result;
    }

    public double manhattanDistance(DataRow v1, DataRow v2, int[] indexes) {
        double result = 0;
        for (int index : indexes) {
            result += Math.abs(v1.getNumericValue(index) - v2.getNumericValue(index));
        }
        return result;
    }

    public double infiniteDistance(DataRow v1, DataRow v2, int[] indexes) {
        double result = Double.MIN_VALUE;
        for (int index : indexes) {
            result = Math.max(result, Math.abs(v1.getNumericValue(index) - v2.getNumericValue(index)));
        }
        return result;
    }

    public double mahalanobisDistance(List<DataRow> data, DataRow v1, DataRow v2, int[] indexes) {
        double[][] covarianceMatrix = covarianceMatrix(data, indexes);
        double[][] invertedCovarianceMatrix = MatrixUtils.invert(covarianceMatrix);
        double[][] diff = new double[1][indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            diff[0][i] = v1.getNumericValue(indexes[i]) - v2.getNumericValue(indexes[i]);
        }
        double[][] transposedDiff = MatrixUtils.transpose(diff);

        return Math.sqrt(MatrixUtils.multiply(MatrixUtils.multiply(transposedDiff, invertedCovarianceMatrix), diff)[0][0]);
    }

    private double[][] covarianceMatrix(List<DataRow> data, int[] indexes) {
        double[][] covarianceMatrix = new double[data.size()][data.size()];
        for (int i = 0; i < indexes.length; i++) {
            for (int j = 0; j < indexes.length; j++) {
                if (i == j) {
                    covarianceMatrix[i][j] = variance(data, indexes[i]);
                } else {
                    covarianceMatrix[i][j] = covariance(data, indexes[i], indexes[j]);
                }
            }
        }
        return covarianceMatrix;
    }


    private double variance(List<DataRow> data, int columnIndex) {
        double mean = mean(data, columnIndex);
        return data.stream().mapToDouble(row -> {
            double diff = row.getNumericValue(columnIndex) - mean;
            return diff * diff;
        }).sum() / (data.size() + 1);
    }

    private double covariance(List<DataRow> data, int column1Index, int column2Index) {
        double column1Mean = mean(data, column1Index);
        double column2Mean = mean(data, column2Index);
        return data.stream().mapToDouble(row -> (row.getNumericValue(column1Index) - column1Mean) * (row.getNumericValue(column2Index) * column2Mean))
                .sum() / (data.size() + 1);
    }

    private double mean(List<DataRow> data, int columnIndex) {
        return data.stream().mapToDouble(row -> row.getNumericValue(columnIndex)).sum() / data.size();
    }

    enum Metric {
        EUCLIDEAN, MANHATTAN, INFINITE, MAHALANOBIS
    }
}

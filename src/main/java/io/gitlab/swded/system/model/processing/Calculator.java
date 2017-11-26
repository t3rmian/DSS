package io.gitlab.swded.system.model.processing;

import io.gitlab.swded.system.model.data.DataRow;
import io.gitlab.swded.system.model.data.DefaultDataRow;
import io.gitlab.swded.system.model.data.Value;
import javafx.util.Pair;
import org.ejml.simple.SimpleMatrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Calculator {

    private Metric metric;
    private final List<DataRow> data;
    private int[] valueIndexes;
    private int classIndex;

    public Calculator(List<DataRow> data) {
        this.data = data;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public void setColumnIndexes(int[] indexes) {
        this.valueIndexes = indexes;
    }

    public void setClassIndex(int index) {
        this.classIndex = index;
    }

    public double calculateDistance(DataRow v1, DataRow v2) {
        switch (metric) {
            case EUCLIDEAN:
                return euclideanDistance(v1, v2, valueIndexes);
            case MANHATTAN:
                return manhattanDistance(v1, v2, valueIndexes);
            case INFINITE:
                return infiniteDistance(v1, v2, valueIndexes);
            case MAHALANOBIS:
                return mahalanobisDistance(data, v1, v2, valueIndexes);
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
        double result = -Double.MAX_VALUE;
        for (int index : indexes) {
            result = Math.max(result, Math.abs(v1.getNumericValue(index) - v2.getNumericValue(index)));
        }
        return result;
    }

    public double mahalanobisDistance(List<DataRow> data, DataRow v1, DataRow v2, int[] indexes) {

        double[][] covariance = covarianceMatrix(data, indexes);
        SimpleMatrix covarianceMatrix = new SimpleMatrix(covariance);
        SimpleMatrix invertedCovarianceMatrix = covarianceMatrix.invert();
        double[][] diff = new double[indexes.length][1];
        for (int i = 0; i < indexes.length; i++) {
            diff[i][0] = v1.getNumericValue(indexes[i]) - v2.getNumericValue(indexes[i]);
        }
        SimpleMatrix diffMatrix = new SimpleMatrix(diff);
        SimpleMatrix diffTMatrix = diffMatrix.transpose();

        return diffTMatrix.mult(invertedCovarianceMatrix).mult(diffMatrix).get(0, 0);
    }

    private double[][] covarianceMatrix(List<DataRow> data, int[] indexes) {
        double[][] covarianceMatrix = new double[indexes.length][indexes.length];
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

    public List<DataRow> selectInitialCentroids(int groupCount) {
        List<DataRow> shuffledData = new ArrayList<>(data);
        Collections.shuffle(shuffledData);
        return shuffledData.stream().limit(groupCount).collect(Collectors.toList());
    }

    public int[] associateCentroids(List<DataRow> centroids) {
        return data.stream().mapToInt(row ->
                IntStream.range(0, centroids.size())
                        .mapToObj(index -> new Pair<>(index, calculateDistance(row, centroids.get(index))))
                        .min(Comparator.comparingDouble(Pair::getValue))
                        .get()
                        .getKey()).toArray();
    }

    public List<DataRow> selectNextCentroids(List<DataRow> centroids, int[] classes) {
        return IntStream.range(0, classes.length)
                .mapToObj(index -> new Pair<>(centroids.get(classes[index]), data.get(index)))
                .collect(Collectors.groupingBy(Pair::getKey))
                .entrySet().stream()
                .map(this::createGroupCentroid)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private DataRow createGroupCentroid(Map.Entry<DataRow, List<Pair<DataRow, DataRow>>> group) {
        int size = group.getValue().size();
        if (size <= 0) {
            System.err.println("WARNING: EMPTY GROUP");
            return null;
        }
        DataRow centroid = new DefaultDataRow(data.get(0).toString().split(" ")).clear();
        group.getValue().forEach(pair -> centroid.add(pair.getValue()));
        centroid.getValues().stream().filter(Value::isNumber).forEach(value -> value.divValue(size));
        return centroid;
    }

    public double clustersSSE(Pair<List<DataRow>, int[]> clusters) {
        List<DataRow> centroids = clusters.getKey();
        int[] classes = clusters.getValue();
        return IntStream.range(0, centroids.size())
                .mapToDouble(centroidIndex ->
                        IntStream.range(0, data.size()).filter(index -> classes[centroidIndex] == centroidIndex)
                                .mapToObj(data::get)
                                .mapToDouble(clusterRow -> sse(clusterRow, centroids.get(centroidIndex)))
                                .sum()
                ).sum();
    }

    private double sse(DataRow clusterRow, DataRow centroidRow) {
        return IntStream.range(0, valueIndexes.length)
                .mapToDouble(columnIndex -> {
                    double diff = clusterRow.getNumericValue(columnIndex) - centroidRow.getNumericValue(columnIndex);
                    return diff * diff;
                }).sum();
    }

    public double clustersJaccard(Pair<List<DataRow>, int[]> clusters) {
        List<DataRow> centroids = clusters.getKey();
        int[] classes = clusters.getValue();
        return IntStream.range(0, centroids.size())
                .mapToDouble(centroidIndex ->
                        IntStream.range(0, data.size()).filter(index -> classes[centroidIndex] == centroidIndex)
                                .mapToObj(data::get)
                                .mapToDouble(clusterRow -> jaccardSimilarity(clusterRow, centroids.get(centroidIndex)))
                                .sum()
                ).sum();
    }

    private double jaccardSimilarity(DataRow clusterRow, DataRow centroidRow) {
        try {
            return IntStream.range(0, valueIndexes.length)
                    .mapToDouble(columnIndex -> Math.min(clusterRow.getNumericValue(columnIndex), centroidRow.getNumericValue(columnIndex))).sum()
                    /
                    IntStream.range(0, valueIndexes.length)
                            .mapToDouble(columnIndex -> Math.max(clusterRow.getNumericValue(columnIndex), centroidRow.getNumericValue(columnIndex))).sum();
        } catch (ArithmeticException ae) {
            System.err.println("Division by zero in Jaccard Similarity calculation, returning infinity");
            return Double.POSITIVE_INFINITY;
        }
    }

    public double calculateInformationGain(double entropyBefore, double entropyAfter) {
        return entropyBefore - entropyAfter;
    }

    public double calculateEntropy(List<DataRow> data) {
        return data.stream()
                .map(row -> row.getTextValue(classIndex))
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()))
                .values().stream()
                .mapToDouble(sameValues -> calculateSingleEntropy(sameValues.intValue(), data.size()))
                .sum();
    }

    public double calculateEntropyAfterSplit(List<DataRow> data, int splitIndex) {
        return data.stream()
                .collect(Collectors.groupingBy(row -> row.getNumericValue(splitIndex)))
                .keySet().stream()
                .mapToDouble(value -> calculateEntropy(data.stream()
                        .filter(row -> value == row.getNumericValue(splitIndex))
                        .collect(Collectors.toList())))
                .sum();
    }

    private double calculateSingleEntropy(int sameDataSize, int subDataSize) {
        double probability = (double) sameDataSize / subDataSize;
        return -probability * log2(probability);
    }

    double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

    public int[] getValueIndexes() {
        return valueIndexes;
    }
}
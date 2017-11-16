package io.gitlab.swded.system.model.processing;

import io.gitlab.swded.system.model.DataRow;
import javafx.util.Pair;

import java.util.*;
import java.util.function.DoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Classifier {
    private int classColumnIndex;
    private final Calculator calculator;
    private final List<DataRow> data;

    public Classifier(List<DataRow> data) {
        this.data = data;
        calculator = new Calculator(data);
    }

    public void setValueColumnIndexes(int[] valueColumnIndexes) {
        calculator.setColumnIndexes(valueColumnIndexes);
    }

    public void setClassColumnIndex(int classColumnIndex) {
        this.classColumnIndex = classColumnIndex;
    }

    public List<Pair<Integer, Double>> classifyGroupsQA(int groupCount, Metric metric, ClusteringAssessment clusteringAssessment) {
        switch (clusteringAssessment) {
            case ELBOW:
                return IntStream.rangeClosed(1, groupCount)
                        .parallel()
                        .mapToObj(iterationGroupCount -> new Pair<>(iterationGroupCount, calculator.clustersSSE(classifyGroups(iterationGroupCount, metric))))
                        .collect(Collectors.toList())
                        .stream().sorted(Comparator.comparing(Pair::getKey))
                        .collect(Collectors.toList())
                        ;
            case JACCARD:
                return IntStream.rangeClosed(1, groupCount)
                        .parallel()
                        .mapToObj(iterationGroupCount -> new Pair<>(iterationGroupCount, calculator.clustersJaccard(classifyGroups(iterationGroupCount, metric))))
                        .collect(Collectors.toList())
                        .stream().sorted(Comparator.comparing(Pair::getKey))
                        .collect(Collectors.toList());
            default:
                throw new RuntimeException("Unknown clustering QA method");
        }
    }

    public Pair<List<DataRow>, int[]> classifyGroups(int groupCount, Metric metric) {
        calculator.setMetric(metric);
        List<DataRow> centroids = calculator.selectInitialCentroids(groupCount);
        int[] classes = calculator.associateCentroids(centroids);
        boolean change = true;
        long iteration = 0;
        while (change) {
            if (++iteration % 5000 == 0) {
                System.out.println("K: " + groupCount + ", Iteration: " + iteration);
            }
            centroids = calculator.selectNextCentroids(centroids, classes);
            int[] newClasses = calculator.associateCentroids(centroids);
            change = false;
            try {
                for (int i = 0; i < classes.length; i++) {
                    if (classes[i] != newClasses[i]) {
                        change = true;
                        break;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException outEx) {
                change = true;
            }
            classes = newClasses;
        }
        return new Pair<>(centroids, classes);
    }

    public String classify(DataRow unknownObject, int knn, Metric metric) {
        calculator.setMetric(metric);
        List<Pair<Integer, Double>> distances = data.stream()
                .mapToDouble(row -> calculator.calculateDistance(unknownObject, row))
                .mapToObj(new PairDoubleFunction())
                .sorted(Comparator.comparingDouble(Pair::getValue))
                .limit(knn)
                .collect(Collectors.toList());
        Map<String, List<Pair<Integer, Double>>> classifiedDistances = new HashMap<>();
        distances.forEach(distance -> {
            String aClass = data.get(distance.getKey()).getTextValue(classColumnIndex);
            if (!classifiedDistances.containsKey(aClass)) {
                classifiedDistances.put(aClass, new ArrayList<>());
            }
            classifiedDistances.get(aClass).add(distance);
        });
        List<Pair<Integer, Double>> nearestGroup = classifiedDistances.values().stream().max((o1, o2) -> {
            int comparisonValue = Integer.compare(o1.size(), o2.size());
            if (comparisonValue == 0) {
                return -Double.compare(o1.stream().mapToDouble(Pair::getValue).sum(), o2.stream().mapToDouble(Pair::getValue).sum());
            }
            return comparisonValue;
        }).get();
        return data.get(nearestGroup.get(0).getKey()).getTextValue(classColumnIndex);
    }

    public double classificationQuality(int knn, Metric metric) {
        List<DataRow> data = new ArrayList<>(this.data);
        int size = data.size();
        int hits = 0;
        for (int i = 0; i < size; i++) {
            DataRow unknownObject = data.remove(0);
            try {
                String closestClass = classify(unknownObject, knn, metric);
                if (Objects.equals(closestClass, unknownObject.getTextValue(classColumnIndex))) {
                    hits++;
                }
            } catch (org.ejml.factory.SingularMatrixException e) {
                System.err.println("Could not invert matrix");
            }
            data.add(unknownObject);
        }
        return (double) hits / size;
    }

    private static class PairDoubleFunction implements DoubleFunction<Pair<Integer, Double>> {
        private int index = 0;

        @Override
        public Pair<Integer, Double> apply(double value) {
            return new Pair<>(index++, value);
        }
    }
}

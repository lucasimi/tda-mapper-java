package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ClusteringUtils {

    private ClusteringUtils() {
    }

    public static <S> ClusteringAlgorithm<S> trivialClustering() {
        return new ClusteringAlgorithm<S>() {
            @Override
            public Collection<Collection<S>> run(Collection<S> dataset) {
                return Collections.singleton(dataset);
            }
        };
    }

    public static <S> ClusteringAlgorithm.Builder<S> trivialClusteringBuilder() {
        return new ClusteringAlgorithm.Builder<S>() {
            @Override
            public ClusteringAlgorithm<S> build() {
                return trivialClustering();
            }
        };
    }

    public static <S> Map<S, Collection<Integer>> getMultiLabels(Collection<Collection<S>> clusters) {
        Map<S, Collection<Integer>> labels = new HashMap<>();
        int labelId = 0;
        for (Collection<S> cluster : clusters) {
            for (S point : cluster) {
                labels.putIfAbsent(point, new HashSet<>());
                labels.get(point).add(labelId);
            }
            labelId += 1;
        }
        return labels;
    }

    public static <S> Map<S, Integer> getLabels(Collection<Collection<S>> clusters) {
        Map<S, Integer> labels = new HashMap<>();
        int labelId = 0;
        for (Collection<S> cluster : clusters) {
            for (S point : cluster) {
                labels.put(point, labelId);
            }
            labelId += 1;
        }
        return labels;
    }

}

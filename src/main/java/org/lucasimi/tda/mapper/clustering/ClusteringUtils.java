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

}

package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;
import java.util.LinkedList;

public class ClusteringUtils {
    
    private ClusteringUtils() {}

    public static <S> ClusteringAlgorithm<S> trivialClustering() {
        return dataset -> {
            Collection<Collection<S>> trivialMap = new LinkedList<>();
            trivialMap.add(dataset);
            return trivialMap;
        };
    }
}

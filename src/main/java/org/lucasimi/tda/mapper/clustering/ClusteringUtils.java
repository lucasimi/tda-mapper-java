package org.lucasimi.tda.mapper.clustering;

import java.util.ArrayList;
import java.util.Collection;

public class ClusteringUtils {
    
    private ClusteringUtils() {}

    public static <S> ClusteringAlgorithm<S> trivialClustering() {
        return dataset -> {
            Collection<Collection<S>> trivialMap = new ArrayList<>(1);
            trivialMap.add(dataset);
            return trivialMap;
        };
    }
}

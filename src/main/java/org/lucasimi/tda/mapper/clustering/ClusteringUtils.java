package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;
import java.util.Collections;

public class ClusteringUtils {

    private ClusteringUtils() {
    }

    public static <S> Clustering<S> trivialClustering() {
        return new Clustering<S>() {
            @Override
            public Collection<Collection<S>> run(Collection<S> dataset) {
                return Collections.singleton(dataset);
            }
        };
    }

    public static <S> Clustering.Builder<S> trivialClusteringBuilder() {
        return new Clustering.Builder<S>() {
            @Override
            public Clustering<S> build() {
                return trivialClustering();
            }
        };
    }

}

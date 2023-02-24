package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;
import java.util.Collections;

import org.lucasimi.tda.mapper.pipeline.MapperException.ClusteringException;

public class ClusteringUtils {

    private ClusteringUtils() {
    }

    public static <S> Clustering<S> trivialClustering() {
        return new TrivialClustering<>();
    }

    public static <S> Clustering.Builder<S> trivialClusteringBuilder() {
        return new TrivialClustering.Builder<>();
    }

    private static class TrivialClustering<S> implements Clustering<S> {

        private TrivialClustering() {}

        @Override
        public Collection<Collection<S>> run(Collection<S> dataset) {
            return Collections.singleton(dataset);
        }

        public static class Builder<S> implements Clustering.Builder<S> {

            @Override
            public Clustering<S> build() throws ClusteringException {
                return new TrivialClustering<>();
            }

        }

    }
}

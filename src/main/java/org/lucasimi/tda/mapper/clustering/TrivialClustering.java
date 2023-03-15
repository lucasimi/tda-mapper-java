package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;
import java.util.Collections;

public class TrivialClustering<S> implements Clustering<S> {

    private TrivialClustering() {}

    public static <T> Builder<T> newBuilder() {
        return new TrivialClustering.Builder<>();
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        return Collections.singleton(dataset);
    }

    public static class Builder<S> implements Clustering.Builder<S> {

        @Override
        public Clustering<S> build() {
            return new TrivialClustering<>();
        }

    }
    
}

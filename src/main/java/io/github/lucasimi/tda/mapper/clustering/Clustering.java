package io.github.lucasimi.tda.mapper.clustering;

import java.util.Collection;

import io.github.lucasimi.tda.mapper.pipeline.MapperException.ClusteringException;

public interface Clustering<S> {

    public Collection<Collection<S>> run(Collection<S> dataset);

    public static interface Builder<S> {

        public Clustering<S> build() throws ClusteringException;

    }

}

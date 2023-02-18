package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;

public interface Clustering<S> {

    public Collection<Collection<S>> run(Collection<S> dataset);

    public static interface Builder<S> {

        public Clustering<S> build();

    }

}

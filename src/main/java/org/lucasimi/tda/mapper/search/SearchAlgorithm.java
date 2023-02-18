package org.lucasimi.tda.mapper.search;

import java.util.Collection;

public interface SearchAlgorithm<S> {

    public Collection<S> fit(Collection<S> dataset);

    public Collection<S> getNeighbors(S point);

    public static interface Builder<S> {

        public SearchAlgorithm<S> build();

    }

}

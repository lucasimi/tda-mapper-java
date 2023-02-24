package org.lucasimi.tda.mapper.search;

import java.util.Collection;

import org.lucasimi.tda.mapper.topology.Lens;

public interface Search<S> {

    public Collection<S> fit(Collection<S> dataset);

    public Collection<S> getNeighbors(S point);

    public static interface Builder<S> {

        public Search<S> build();

        public <R> Builder<R> pullback(Lens<R, S> lens);

    }

}

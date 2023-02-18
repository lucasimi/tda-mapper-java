package org.lucasimi.tda.mapper.search;

import java.util.Collection;

public interface Search<S> {

    public Collection<S> fit(Collection<S> dataset);

    public Collection<S> getNeighbors(S point);

    public static interface Builder<S> {

        public Search<S> build();

    }

}

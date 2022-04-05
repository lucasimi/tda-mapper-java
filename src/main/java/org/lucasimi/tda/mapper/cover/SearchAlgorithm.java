package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

public interface SearchAlgorithm<S> {

    public Collection<S> setup(Collection<S> dataset);

    public Collection<S> getNeighbors(S point);
    
}

package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

public interface SearchAlgorithm<S> {

    public void setup(Collection<S> dataset);

    public Collection<S> getNeighbors(S point);
    
}

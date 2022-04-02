package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

public interface CoverAlgorithm<S> {

    public Collection<Collection<S>> getClusters(Collection<S> dataset);

}

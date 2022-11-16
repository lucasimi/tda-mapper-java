package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

public interface CoverAlgorithm<S> {

    public CoverAlgorithm<S> fit(Collection<S> dataset);

    public Collection<Collection<S>> getCover();

}

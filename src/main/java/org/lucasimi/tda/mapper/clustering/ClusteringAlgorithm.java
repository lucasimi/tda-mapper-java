package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;

public interface ClusteringAlgorithm<S> {

    public Collection<Collection<S>> run(Collection<S> dataset);

}

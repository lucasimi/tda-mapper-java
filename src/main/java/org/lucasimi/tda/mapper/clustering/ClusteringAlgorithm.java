package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;

public interface ClusteringAlgorithm<S> {

    public ClusteringAlgorithm<S> fit(Collection<S> dataset);

    public Collection<Collection<S>> getClusters();

}

package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.lucasimi.tda.mapper.vptree.VPTreeImp;

public class KNNSearch<S> implements SearchAlgorithm<S> {

    private int neighbors;

    private Metric<S> metric;

    private VPTreeImp<S> vpTree;

    public <T> KNNSearch(Lens<S, T> lens, Metric<T> metric, int neighbors) {
        this(TopologyUtils.pullback(lens, metric), neighbors);
    }

    public KNNSearch(Metric<S> metric, int neighbors) {
        this.neighbors = neighbors;
        this.metric = metric;
    }

    @Override
    public Collection<S> setup(Collection<S> dataset) {
        this.vpTree = new VPTreeImp<>(metric, dataset, this.neighbors, 0.0);
        return this.vpTree.getCenters();
    }

    @Override
    public Collection<S> getNeighbors(S point) {
        return this.vpTree.knnSearch(point, this.neighbors);
    }

}

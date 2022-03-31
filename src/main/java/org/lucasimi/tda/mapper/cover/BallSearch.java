package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.lucasimi.tda.mapper.vptree.VPTree;

public class BallSearch<S> implements SearchAlgorithm<S> {

    private double radius;
    
    private Metric<S> metric;

	private VPTree<S> vpTree;
	
	public <T> BallSearch(Lens<S, T> lens, Metric<T> metric, double radius) {
		this(TopologyUtils.pullback(lens, metric), radius);
	}

    public BallSearch(Metric<S> metric, double radius) {
        this.radius = radius;
        this.metric = metric;
    }

    @Override
    public void setup(Collection<S> dataset) {
		this.vpTree = new VPTree<>(metric, dataset, 1000, this.radius);
    }

    @Override
    public Collection<S> getNeighbors(S point) {
        return this.vpTree.ballSearch(point, this.radius);
    }

}
package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.lucasimi.tda.mapper.vptree.VPTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BallSearch<S> implements SearchAlgorithm<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BallSearch.class);

    public static final double DEFAULT_LEAF_SIZE_FACTOR = 0.01;

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
    public Collection<S> setup(Collection<S> dataset) {
        int leafSize = (int) (DEFAULT_LEAF_SIZE_FACTOR * dataset.size());
        if (leafSize <= 0) {
            leafSize = 1;
        }
        LOGGER.info("Using leaf size of {}", leafSize);
		this.vpTree = new VPTree<>(metric, dataset, leafSize, this.radius);
        return this.vpTree.getCenters();
    }

    @Override
    public Collection<S> getNeighbors(S point) {
        return this.vpTree.ballSearch(point, this.radius);
    }

}
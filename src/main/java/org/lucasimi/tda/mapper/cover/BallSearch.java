package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.VPTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BallSearch<S> implements SearchAlgorithm<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BallSearch.class);

    public static final double DEFAULT_LEAF_SIZE_FACTOR = 0.01;

    private final double radius;

    private final Metric<S> metric;

    private VPTree<S> vpTree;

    private BallSearch(Metric<S> metric, double radius) {
        this.radius = radius;
        this.metric = metric;
    }

    @Override
    public Collection<S> fit(Collection<S> dataset) {
        int leafSize = (int) (DEFAULT_LEAF_SIZE_FACTOR * dataset.size());
        if (leafSize <= 0) {
            leafSize = 1;
        }
        LOGGER.debug("Using leaf size of {}", leafSize);
        this.vpTree = new VPTree.Builder<S>()
                .withMetric(this.metric)
                .withLeafCapacity(leafSize)
                .withLeafRadius(this.radius)
                .build(dataset);
        return this.vpTree.getCenters();
    }

    @Override
    public Collection<S> getNeighbors(S point) {
        return this.vpTree.ballSearch(point, this.radius);
    }

    public static class Builder<S> implements SearchAlgorithm.Builder<S> {

        private double radius;

        private Metric<S> metric;

        public Builder<S> withRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder<S> withMetric(Metric<S> metric) {
            this.metric = metric;
            return this;
        }

        @Override
        public SearchAlgorithm<S> build() {
            return new BallSearch<>(this.metric, this.radius);
        }

    }

}

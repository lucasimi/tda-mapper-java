package io.github.lucasimi.tda.mapper.search;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lucasimi.tda.mapper.topology.Lens;
import io.github.lucasimi.tda.mapper.topology.TopologyUtils;
import io.github.lucasimi.utils.Metric;
import io.github.lucasimi.vptree.VPTree;

public class BallSearch<S> implements Search<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BallSearch.class);

    public static final double DEFAULT_LEAF_SIZE_FACTOR = 0.01;

    private final double radius;

    private final Metric<S> metric;

    private VPTree<S> vpTree;

    private BallSearch(Builder<S> builder) {
        this.radius = builder.radius;
        this.metric = builder.metric;
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
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

    public static class Builder<S> implements Search.Builder<S> {

        private double radius;

        private Metric<S> metric;

        private Builder() {}

        public Builder<S> withRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder<S> withMetric(Metric<S> metric) {
            this.metric = metric;
            return this;
        }

        @Override
        public Search<S> build() {
            return new BallSearch<>(this);
        }

        @Override
        public <R> Builder<R> pullback(Lens<R, S> lens) {
            return new Builder<R>()
                .withRadius(this.radius)
                .withMetric(TopologyUtils.pullback(lens, this.metric));
        }


    }

}

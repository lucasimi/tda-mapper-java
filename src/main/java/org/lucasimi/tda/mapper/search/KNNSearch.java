package org.lucasimi.tda.mapper.search;

import java.util.Collection;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.VPTree;

public class KNNSearch<S> implements Search<S> {

    private final int neighbors;

    private final Metric<S> metric;

    private VPTree<S> vpTree;

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    private KNNSearch(Builder<S> builder) {
        this.neighbors = builder.neighbors;
        this.metric = builder.metric;
    }

    @Override
    public Collection<S> fit(Collection<S> dataset) {
        this.vpTree = new VPTree.Builder<S>()
                .withMetric(this.metric)
                .withLeafCapacity(this.neighbors)
                .build(dataset);
        return this.vpTree.getCenters();
    }

    @Override
    public Collection<S> getNeighbors(S point) {
        return this.vpTree.knnSearch(point, this.neighbors);
    }

    public static class Builder<S> implements Search.Builder<S> {

        private int neighbors;

        private Metric<S> metric;

        private Builder() {}

        public Builder<S> withNeighbors(int neighbors) {
            this.neighbors = neighbors;
            return this;
        }

        public Builder<S> withMetric(Metric<S> metric) {
            this.metric = metric;
            return this;
        }

        @Override
        public Search<S> build() {
            return new KNNSearch<>(this);
        }

    }

}

package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.VPTree;

public class KNNSearch<S> implements SearchAlgorithm<S> {

    private final int neighbors;

    private final Metric<S> metric;

    private VPTree<S> vpTree;

    private KNNSearch(Metric<S> metric, int neighbors) {
        this.neighbors = neighbors;
        this.metric = metric;
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

    public static class Builder<S> implements SearchAlgorithm.Builder<S> {

        private int neighbors;

        private Metric<S> metric;

        public Builder<S> withNeighbors(int neighbors) {
            this.neighbors = neighbors;
            return this;
        }

        public Builder<S> withMetric(Metric<S> metric) {
            this.metric = metric;
            return this;
        }

        @Override
        public SearchAlgorithm<S> build() {
            return new KNNSearch<>(this.metric, this.neighbors);
        }

    }

}

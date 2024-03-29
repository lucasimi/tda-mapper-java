package org.lucasimi.tda.mapper.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.VPTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBSCANFaster<T> implements Clustering<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBSCANFaster.class);

    private static final double EPS_DEFAULT = 0.5;

    private static final int MIN_SAMPLES_DEFAULT = 5;

    private final Metric<T> metric;

    private final double eps;

    private final int minSamples;

    private enum PointStatus {
        NOISE,
        CLUSTERED
    }

    public static <S> Builder<S> newBuilder() {
        return new Builder<>();
    }

    private DBSCANFaster(Metric<T> metric, Double eps, Integer minSamples) {
        this.metric = metric;
        this.eps = eps;
        this.minSamples = minSamples;
    }

    private Collection<T> propagateCluster(final Collection<T> cluster,
            final T point,
            final Collection<T> neighbors,
            final VPTree<T> ballTree,
            final Map<T, PointStatus> visited) {
        cluster.add(point);
        visited.put(point, PointStatus.CLUSTERED);
        List<T> seeds = new ArrayList<>(neighbors);
        int index = 0;
        while (index < seeds.size()) {
            final T currentPoint = seeds.get(index);
            PointStatus pointStatus = visited.get(currentPoint);
            if (pointStatus == null) {
                final Collection<T> currentNeighbors = getNeighbors(ballTree, currentPoint, eps);
                if (currentNeighbors.size() >= minSamples) {
                    merge(seeds, currentNeighbors);
                }
            }
            if (pointStatus != PointStatus.CLUSTERED) {
                visited.put(currentPoint, PointStatus.CLUSTERED);
                cluster.add(currentPoint);
            }
            index++;
        }
        return cluster;
    }

    private Collection<T> getNeighbors(VPTree<T> vpTree, T point, double eps) {
        return vpTree.ballSearch(point, eps);
    }

    private void merge(final Collection<T> one, final Collection<T> two) {
        final Set<T> oneSet = new HashSet<>(one);
        for (T item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
    }

    @Override
    public Collection<Collection<T>> run(final Collection<T> points) {
        VPTree<T> vpTree = new VPTree.Builder<T>()
                .withMetric(metric)
                .withLeafCapacity(minSamples)
                .build(points);
        final Collection<Collection<T>> clusters = new ArrayList<>();
        final Map<T, PointStatus> visited = new HashMap<>();
        for (final T point : points) {
            if (visited.get(point) != null) {
                continue;
            }
            final Collection<T> neighbors = getNeighbors(vpTree, point, eps);
            if (neighbors.size() >= minSamples) {
                final List<T> cluster = new ArrayList<>();
                clusters.add(propagateCluster(cluster, point, neighbors, vpTree, visited));
            } else {
                visited.put(point, PointStatus.NOISE);
            }
        }
        return clusters;
    }

    public static class Builder<S> implements Clustering.Builder<S> {

        private int minSamples = MIN_SAMPLES_DEFAULT;

        private double eps = EPS_DEFAULT;

        private Metric<S> metric;

        private Builder() {}

        public Builder<S> withMetric(Metric<S> metric) {
            this.metric = metric;
            return this;
        }

        public Builder<S> withEps(double eps) {
            this.eps = eps;
            return this;
        }

        public Builder<S> withMinSamples(int minSamples) {
            this.minSamples = minSamples;
            return this;
        }

        @Override
        public Clustering<S> build() {
            if (Double.isNaN(this.eps) || Double.isInfinite(this.eps) || this.eps <= 0) {
                LOGGER.warn("Found eps = {}, but expected > 0. Using default eps = {}", eps, EPS_DEFAULT);
                this.eps = EPS_DEFAULT;
            }
            if (this.minSamples <= 0) {
                LOGGER.warn("Found minSamples = %d, but expected > 0. Using default minSamples = %d", minSamples,
                        MIN_SAMPLES_DEFAULT);
                this.minSamples = MIN_SAMPLES_DEFAULT;
            }
            return new DBSCANFaster<>(this.metric, this.eps, this.minSamples);
        }

    }

}

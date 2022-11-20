package org.lucasimi.tda.mapper.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lucasimi.utils.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBSCANSimple<T> implements ClusteringAlgorithm<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBSCANSimple.class);

    private static final double EPS_DEFAULT = 0.5;

    private static final int MIN_SAMPLES_DEFAULT = 5;

    private final Metric<T> metric;

    private final double eps;

    private final int minSamples;

    private enum PointStatus {
        NOISE,
        CLUSTERED
    }

    private DBSCANSimple(Metric<T> metric, Double eps, Integer minSamples) {
        this.metric = metric;
        this.eps = eps;
        this.minSamples = minSamples;
    }

    private Collection<T> propagateCluster(final Collection<T> cluster,
            final T point,
            final List<T> neighbors,
            final Collection<T> points,
            final Map<T, PointStatus> visited) {
        cluster.add(point);
        visited.put(point, PointStatus.CLUSTERED);

        List<T> seeds = new ArrayList<T>(neighbors);
        int index = 0;
        while (index < seeds.size()) {
            final T currentPoint = seeds.get(index);
            PointStatus pointStatus = visited.get(currentPoint);
            if (pointStatus == null) {
                final List<T> currentNeighbors = getNeighbors(points, currentPoint, eps);
                if (currentNeighbors.size() >= minSamples) {
                    seeds = merge(seeds, currentNeighbors);
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

    private List<T> getNeighbors(Collection<T> data, T point, double eps) {
        List<T> neighbors = new ArrayList<>(data.size());
        for (T p : data) {
            if (this.metric.eval(point, p) < eps) {
                neighbors.add(p);
            }
        }
        return neighbors;
    }

    private List<T> merge(final List<T> one, final List<T> two) {
        final Set<T> oneSet = new HashSet<T>(one);
        for (T item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }

    @Override
    public Collection<Collection<T>> run(final Collection<T> points) {
        final Collection<Collection<T>> clusters = new ArrayList<>();
        final Map<T, PointStatus> visited = new HashMap<T, PointStatus>();
        for (final T point : points) {
            if (visited.get(point) != null) {
                continue;
            }
            final List<T> neighbors = getNeighbors(points, point, eps);
            if (neighbors.size() >= minSamples) {
                final List<T> cluster = new ArrayList<>();
                clusters.add(propagateCluster(cluster, point, neighbors, points, visited));
            } else {
                visited.put(point, PointStatus.NOISE);
            }
        }
        return clusters;
    }

    public static class Builder<S> implements ClusteringAlgorithm.Builder<S> {

        private int minSamples = MIN_SAMPLES_DEFAULT;

        private double eps = EPS_DEFAULT;

        private Metric<S> metric;

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
        public ClusteringAlgorithm<S> build() {
            if (Double.isNaN(this.eps) || Double.isInfinite(this.eps) || this.eps <= 0) {
                LOGGER.warn("Found eps = {}, but expected > 0. Using default eps = {}", eps, EPS_DEFAULT);
                this.eps = EPS_DEFAULT;
            }
            if (this.minSamples <= 0) {
                LOGGER.warn("Found minSamples = %d, but expected > 0. Using default minSamples = %d", minSamples,
                        MIN_SAMPLES_DEFAULT);
                this.minSamples = MIN_SAMPLES_DEFAULT;
            }
            return new DBSCANSimple<>(this.metric, this.eps, this.minSamples);
        }

    }

}

package io.github.lucasimi.tda.mapper.search;

import java.util.Collection;
import java.util.stream.Collectors;

import io.github.lucasimi.tda.mapper.topology.Lens;
import io.github.lucasimi.tda.mapper.topology.Point;
import io.github.lucasimi.tda.mapper.topology.TopologyUtils;
import io.github.lucasimi.utils.Metric;

public class CubicSearch<S> implements Search<S> {

    private int intervals;

    private double overlap;

    private Lens<S, float[]> coords;

    private float[] sides;

    private Search<Point<S>> search;

    public static <S> Builder<S> newBuilder() {
        return new Builder<>();
    }

    public CubicSearch(Builder<S> builder) {
        this.intervals = builder.intervals;
        this.overlap = builder.overlap;
        this.coords = builder.coords;
    }

    private int getDimensions(Collection<Point<S>> dataset) {
        for (Point<S> point : dataset) {
            return point.getCoords().length;
        }
        return 0;
    }

    private float[] getCoordinatesMin(Collection<Point<S>> dataset) {
        int dim = getDimensions(dataset);
        float[] min = new float[dim];
        for (int j = 0; j < dim; j++) {
            min[j] = Float.POSITIVE_INFINITY;
        }
        for (Point<S> point : dataset) {
            float[] coords = point.getCoords();
            for (int j = 0; j < dim; j++) {
                if (coords[j] < min[j]) {
                    min[j] = coords[j];
                }
            }
        }
        return min;
    }

    private float[] getCoordinatesMax(Collection<Point<S>> dataset) {
        int dim = getDimensions(dataset);
        float[] max = new float[dim];
        for (int j = 0; j < dim; j++) {
            max[j] = Float.NEGATIVE_INFINITY;
        }
        for (Point<S> point : dataset) {
            float[] coords = point.getCoords();
            for (int j = 0; j < dim; j++) {
                if (coords[j] > max[j]) {
                    max[j] = coords[j];
                }
            }
        }
        return max;
    }

    private float[] getCoordinatesRange(Collection<Point<S>> dataset) {
        float[] min = getCoordinatesMin(dataset);
        float[] max = getCoordinatesMax(dataset);
        float[] range = new float[min.length];
        for (int i = 0; i < range.length; i++) {
            range[i] = max[i] - min[i];
            if (Float.compare(range[i], 0.0f) <= 0) {
                range[i] = 1.0f;
            }
        }
        return range;
    }

    @Override
    public Collection<S> fit(Collection<S> dataset) {
        Collection<Point<S>> points = dataset.stream()
            .map(p -> Point.newPoint(p, this.coords))
            .collect(Collectors.toList());
        float[] range = getCoordinatesRange(points);
        this.sides = new float[range.length];
        for (int i = 0; i < this.sides.length; i++) {
            this.sides[i] = range[i] / this.intervals;
        }
        Lens<Point<S>, float[]> normalizedCoords = TopologyUtils.pullback(Point::getCoords, TopologyUtils.normalize(sides));
        Metric<Point<S>> normalizedChebyshev = TopologyUtils.pullback(normalizedCoords, TopologyUtils.chebyshevMetric());
        double normalizedRadius = 1.0 + this.overlap;
        this.search = BallSearch.<Point<S>>newBuilder() 
            .withMetric(normalizedChebyshev)
            .withRadius(normalizedRadius)
            .build();
        return this.search.fit(points).stream()
            .map(Point::getData)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<S> getNeighbors(S point) {
        Point<S> centroid = getGridCentroid(this.sides, Point.newPoint(point, this.coords));
        return this.search.getNeighbors(centroid).stream()
            .map(Point::getData)
            .collect(Collectors.toList());
    }

    private Point<S> getGridCentroid(float[] ranges, Point<S> point) {
        float[] pointCoords = point.getCoords();
        float[] centroidCoords = new float[pointCoords.length];
        for (int i = 0; i < centroidCoords.length; i++) {
            centroidCoords[i] = (float) Math.round(pointCoords[i] / ranges[i]) * ranges[i];
        }
        return Point.newPoint(null, centroidCoords);
    }

    public static class Builder<S> implements Search.Builder<S> {

        private int intervals;

        private double overlap;

        private Lens<S, float[]> coords;

        public Builder<S> withIntervals(int intervals) {
            this.intervals = intervals;
            return this;
        }

        public Builder<S> withOverlap(double overlap) {
            this.overlap = overlap;
            return this;
        }

        public Builder<S> withCoordinates(Lens<S, float[]> coords) {
            this.coords = coords;
            return this;
        }

        @Override
        public Search<S> build() {
            if (this.intervals <= 0) {
                throw new IllegalArgumentException();
            }
            if (Double.compare(this.overlap, 0.0) <= 0) {
                throw new IllegalArgumentException();
            }
            if (Double.compare(this.overlap, 1.0) >= 0) {
                throw new IllegalArgumentException();
            }
            CubicSearch<S> search = new CubicSearch<>(this);
            return search;
        }

        @Override
        public <R> Builder<R> pullback(Lens<R, S> lens) {
            return new CubicSearch.Builder<R>()
                .withIntervals(this.intervals)
                .withOverlap(this.overlap)
                .withCoordinates(TopologyUtils.pullback(lens, this.coords));
        }

    }


}

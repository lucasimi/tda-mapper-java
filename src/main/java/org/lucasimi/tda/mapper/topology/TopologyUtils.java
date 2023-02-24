package org.lucasimi.tda.mapper.topology;

import org.lucasimi.utils.Metric;

public class TopologyUtils {
    
    private TopologyUtils() {}

    public static <S> Lens<S, S> identity() {
        return source -> source;
    }

    public static Lens<float[], float[]> normalize(float[] sides) {
        return point -> {
            float[] nomalized = new float[point.length];
            for (int i = 0; i < nomalized.length; i++) {
                nomalized[i] = Float.NaN;
            }
            if (point.length != sides.length) {
                return nomalized;
            }
            for (int i = 0; i < nomalized.length; i++) {
                nomalized[i] = point[i] / sides[i];
            }
            return nomalized;
        };
    }

    public static Lens<float[], float[]> projection(int i) {
        return source -> {
            if ((i < 0) || (i >= source.length)) {
                return new float[] { Float.NaN };
            }
            return new float[] { source[i] };
        };
    }

    public static Lens<float[], float[]> projection(int[] ids) {
        return source -> {
            float[] val = new float[ids.length];
            for (int j = 0; j < ids.length; j++) {
                int i = ids[j];
                if ((i < 0) || (i >= source.length)) {
                    val[j] = Float.NaN;
                } else {
                    val[j] = source[i];
                }
            }
            return val;
        };
    }

    public static <R, S, T> Lens<R, T> pullback(Lens<R, S> lens, Lens<S, T> fun) {
        return x -> fun.evaluate(lens.evaluate(x));
    }

    public static <S, T> Metric<S> pullback(Lens<S, T> lens, Metric<T> targetMetric) {
        return (first, second) -> targetMetric.eval(lens.evaluate(first), lens.evaluate(second));
    }

    public static Metric<float[]> euclideanMetric() {
        return (first, second) -> {
            if (first.length != second.length) {
                return Float.NaN;
            }
            float sum = 0.0f;
            for (int i = 0; i < first.length; i++) {
                float diff = first[i] - second[i];
                sum += diff * diff;
            }
            return (float) Math.sqrt(sum);
        };
    }

    public static Metric<float[]> minkowskiMetric(float p) {
        return (first, second) -> {
            if (Float.compare(p, 0.0f) <= 0) {
                return Float.NaN;
            }
            if (first.length != second.length) {
                return Float.NaN;
            }
            float sum = 0.0f;
            for (int i = 0; i < first.length; i++) {
                float diff = first[i] - second[i];
                sum += Math.pow(diff, p);
            }
            return (float) Math.pow(sum, 1.0f / p);
        };
    }

    public static Metric<float[]> chebyshevMetric() {
        return (first, second) -> {
            if (first.length != second.length) {
                return Float.NaN;
            }
            float max = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < first.length; i++) {
                float absDiff = Math.abs(first[i] - second[i]);
                if (Float.compare(absDiff, max) > 0) {
                    max = absDiff;
                }
            }
            return max;
        };
    }

}

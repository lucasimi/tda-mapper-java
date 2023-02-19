package org.lucasimi.tda.mapper.topology;

import org.lucasimi.utils.Metric;

public class TopologyUtils {
    
    private TopologyUtils() {}

    public static <S> Lens<S, S> identity() {
        return source -> source;
    }

    public static <S, T> Metric<S> pullback(Lens<S, T> lens, Metric<T> targetMetric) {
        return (first, second) -> targetMetric.eval(lens.evaluate(first), lens.evaluate(second));
    }

    public static Metric<float[]> euclideanMetric() {
        return (first, second) -> {
            if (first.length != second.length) {
                return Float.MAX_VALUE;
            }
            float sum = 0.0f;
            for (int i = 0; i < first.length; i++) {
                float diff = first[i] - second[i];
                sum += diff * diff;
            }
            return (float) Math.sqrt(sum);
        };
    }

}

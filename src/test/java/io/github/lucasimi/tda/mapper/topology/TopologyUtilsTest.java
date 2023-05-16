package io.github.lucasimi.tda.mapper.topology;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.lucasimi.utils.Metric;

public class TopologyUtilsTest {

    @Test
    public void testProjection() {
        Lens<float[], float[]> lens = TopologyUtils.projection(2);
        float[] point = new float[] { 0.0f, 1.0f, 2.0f };
        assertArrayEquals(lens.evaluate(point), new float[] { 2.0f });
    }

    @Test
    public void testProjectionList() {
        Lens<float[], float[]> lens = TopologyUtils.projection(new int[] { 2, 1 });
        float[] point = new float[] { 0.0f, 1.0f, 2.0f };
        assertArrayEquals(lens.evaluate(point), new float[] { 2.0f, 1.0f });
    }
    
    @Test
    public void testMinkowski() {
        Metric<float[]> metric = TopologyUtils.minkowskiMetric(2);
        float[] point1 = new float[] { 0.0f, 0.0f, 2.0f };
        float[] point2 = new float[] { 0.0f, 3.0f, -2.0f };
        assertEquals(metric.eval(point1, point2), 5.0f );
    }

    @Test
    public void testChebyshev() {
        Metric<float[]> metric = TopologyUtils.chebyshevMetric();
        float[] point1 = new float[] { 0.0f, 0.0f, 2.0f };
        float[] point2 = new float[] { 0.0f, 3.0f, -2.0f };
        assertEquals(metric.eval(point1, point2), 4.0f );
    }

}

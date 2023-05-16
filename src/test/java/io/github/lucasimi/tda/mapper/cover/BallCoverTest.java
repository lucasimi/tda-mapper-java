package io.github.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.lucasimi.tda.mapper.DatasetGenerator;
import io.github.lucasimi.tda.mapper.pipeline.MapperException.CoverException;
import io.github.lucasimi.tda.mapper.search.BallSearch;
import io.github.lucasimi.tda.mapper.topology.Lens;
import io.github.lucasimi.tda.mapper.topology.TopologyUtils;
import io.github.lucasimi.utils.Metric;

public class BallCoverTest {

    Metric<float[]> metric = TopologyUtils.euclideanMetric();

    Lens<float[], float[]> lens = TopologyUtils.identity();

    @Test
    public void testOneCover() throws CoverException {
        List<float[]> dataset = new LinkedList<>();
        dataset.add(new float[] { 0.0f, 1.0f });
        dataset.add(new float[] { 1.0f, 1.0f });
        dataset.add(new float[] { 1.0f, 0.0f });

        Cover<float[]> covering = SearchCover.<float[]>newBuilder()
                .withSearch(BallSearch.<float[]>newBuilder()
                        .withMetric(TopologyUtils.pullback(lens, metric))
                        .withRadius(0.5))
                .build();
        Collection<Collection<float[]>> groups = covering.run(dataset);
        Assertions.assertEquals(3, groups.size());

        covering = SearchCover.<float[]>newBuilder()
                .withSearch(BallSearch.<float[]>newBuilder()
                        .withMetric(TopologyUtils.pullback(lens, metric))
                        .withRadius(1.5))
                .build();
        groups = covering.run(dataset);
        Assertions.assertEquals(1, groups.size());
    }

    @Test
    public void testTwoCovers() throws CoverException {
        List<float[]> dataset = new LinkedList<>();
        // first cc
        dataset.add(new float[] { 0.0f, 1.0f });
        dataset.add(new float[] { 0.0f, 1.1f });
        dataset.add(new float[] { 0.0f, 1.2f });
        // second cc
        dataset.add(new float[] { 0.0f, -1.0f });
        dataset.add(new float[] { 0.0f, -1.1f });
        dataset.add(new float[] { 0.0f, -1.2f });

        Cover<float[]> covering = SearchCover.<float[]>newBuilder()
                .withSearch(BallSearch.<float[]>newBuilder()
                        .withMetric(TopologyUtils.pullback(lens, metric))
                        .withRadius(0.5))
                .build();
        Collection<Collection<float[]>> groups = covering.run(dataset);
        Assertions.assertEquals(2, groups.size());

        covering = SearchCover.<float[]>newBuilder()
                .withSearch(BallSearch.<float[]>newBuilder()
                        .withMetric(TopologyUtils.pullback(lens, metric))
                        .withRadius(0.09))
                .build();
        groups = covering.run(dataset);
        Assertions.assertEquals(6, groups.size());
    }

    @Test
    public void testRandom() throws CoverException {
        ArrayList<float[]> dataset = new ArrayList<>();
        dataset.addAll(DatasetGenerator.randomDataset(20000, new float[] { 1.0f, 0.0f }, 0.3f));
        dataset.addAll(DatasetGenerator.randomDataset(20000, new float[] { 0.0f, 1.0f }, 0.3f));

        Cover<float[]> covering = SearchCover.<float[]>newBuilder()
                .withSearch(BallSearch.<float[]>newBuilder()
                        .withMetric(TopologyUtils.pullback(lens, metric))
                        .withRadius(0.85))
                .build();
        Collection<Collection<float[]>> groups = covering.run(dataset);
        Assertions.assertEquals(2, groups.size());
    }

}

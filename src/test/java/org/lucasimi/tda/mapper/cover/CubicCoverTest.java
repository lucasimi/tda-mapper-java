package org.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.pipeline.MapperException.CoverException;
import org.lucasimi.tda.mapper.search.CubicSearch;
import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.lucasimi.utils.Metric;

public class CubicCoverTest {

    Metric<float[]> metric = TopologyUtils.euclideanMetric();

    Lens<float[], float[]> lens = TopologyUtils.identity();

    @Test
    public void testOneCover() throws CoverException {
        List<float[]> dataset = new LinkedList<>();
        dataset.add(new float[] { 0.0f, 1.0f });
        dataset.add(new float[] { 1.0f, 1.0f });
        dataset.add(new float[] { 1.0f, 0.0f });

        Cover<float[]> covering = SearchCover.<float[]>newBuilder()
                .withSearch(CubicSearch.<float[]>newBuilder()
                    .withCoordinates(TopologyUtils.identity())
                    .withIntervals(3)
                    .withOverlap(0.25))
                .build();
        Collection<Collection<float[]>> groups = covering.run(dataset);
        Assertions.assertEquals(3, groups.size());

        covering = SearchCover.<float[]>newBuilder()
                .withSearch(CubicSearch.<float[]>newBuilder()
                    .withCoordinates(TopologyUtils.identity())
                    .withIntervals(1)
                    .withOverlap(0.25))
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
                .withSearch(CubicSearch.<float[]>newBuilder()
                        .withCoordinates(TopologyUtils.identity())
                        .withIntervals(3)
                        .withOverlap(0.125))
                .build();
        Collection<Collection<float[]>> groups = covering.run(dataset);
        Assertions.assertEquals(2, groups.size());

        covering = SearchCover.<float[]>newBuilder()
                .withSearch(CubicSearch.<float[]>newBuilder()
                        .withCoordinates(TopologyUtils.identity())
                        .withIntervals(6)
                        .withOverlap(0.125))
                .build();
        groups = covering.run(dataset);
        Assertions.assertEquals(2, groups.size());
    }

    @Test
    public void testRandom() throws CoverException {
        ArrayList<float[]> dataset = new ArrayList<>();
        dataset.addAll(DatasetGenerator.randomDataset(20000, new float[] { 1.0f, 0.0f }, 0.2f));
        dataset.addAll(DatasetGenerator.randomDataset(20000, new float[] { 0.0f, 1.0f }, 0.2f));

        Cover<float[]> covering = SearchCover.<float[]>newBuilder()
                .withSearch(CubicSearch.<float[]>newBuilder()
                        .withCoordinates(TopologyUtils.identity())
                        .withIntervals(2)
                        .withOverlap(0.125))
                .build();
        Collection<Collection<float[]>> groups = covering.run(dataset);
        Assertions.assertEquals(2, groups.size());
    }
    
}

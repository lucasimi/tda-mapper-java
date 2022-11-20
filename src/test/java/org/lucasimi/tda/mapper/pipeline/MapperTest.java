package org.lucasimi.tda.mapper.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.clustering.ClusteringAlgorithm;
import org.lucasimi.tda.mapper.clustering.ClusteringUtils;
import org.lucasimi.tda.mapper.cover.BallSearch;
import org.lucasimi.tda.mapper.cover.SearchCover;
import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.lucasimi.utils.Metric;

public class MapperTest {

    ClusteringAlgorithm<float[]> clusterer = ClusteringUtils.trivialClustering();

    Lens<float[], float[]> lens = TopologyUtils.identity();

    Metric<float[]> metric = TopologyUtils.euclideanMetric();

    @Test
    public void testMapperPerf() throws MapperException {
        int dim = 128;
        int k = 5;
        int size = (int) Math.pow(10, k);
        float side = 1.0f;
        ArrayList<float[]> dataset = DatasetGenerator.randomDataset(size, dim, 0.0f, side);
        float radius = (float) (side * Math.sqrt(dim) / Math.pow(size, 1.0 / dim));
        MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
                .withCoverAlgorithm(new SearchCover.Builder<float[]>()
                        .withSearchAlgorithm(new BallSearch.Builder<float[]>()
                                .withMetric(TopologyUtils.pullback(lens, metric))
                                .withRadius(radius * 0.4)))
                .withClusteringAlgorithm(ClusteringUtils.trivialClusteringBuilder())
                .build();

        mapperPipeline.run(dataset);
    }

    @Test
    public void testBase() throws MapperException {
        List<float[]> dataset = new LinkedList<>();
        dataset.add(new float[] { 0.0f, 1.0f });
        dataset.add(new float[] { 1.0f, 1.0f });
        dataset.add(new float[] { 1.0f, 0.0f });

        MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
                .withCoverAlgorithm(new SearchCover.Builder<float[]>()
                        .withSearchAlgorithm(new BallSearch.Builder<float[]>()
                                .withMetric(TopologyUtils.pullback(lens, metric))
                                .withRadius(0.5)))
                .withClusteringAlgorithm(ClusteringUtils.trivialClusteringBuilder())
                .build();

        MapperGraph graph = mapperPipeline.run(dataset);
        assertEquals(3, graph.countConnectedComponents());
    }

    @Test
    public void testBaseTwoConnectedComponents() throws MapperException {
        List<float[]> dataset = new LinkedList<>();
        // first cc
        dataset.add(new float[] { 0.0f, 1.0f });
        dataset.add(new float[] { 0.0f, 1.1f });
        dataset.add(new float[] { 0.0f, 1.2f });
        // second cc
        dataset.add(new float[] { 0.0f, -1.0f });
        dataset.add(new float[] { 0.0f, -1.1f });
        dataset.add(new float[] { 0.0f, -1.2f });

        MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
                .withCoverAlgorithm(new SearchCover.Builder<float[]>()
                        .withSearchAlgorithm(new BallSearch.Builder<float[]>()
                                .withMetric(TopologyUtils.pullback(lens, metric))
                                .withRadius(0.09)))
                .withClusteringAlgorithm(ClusteringUtils.trivialClusteringBuilder())
                .build();

        MapperGraph graph = mapperPipeline.run(dataset);
        assertEquals(6, graph.getVertices().size());
        assertEquals(6, graph.countConnectedComponents());

        mapperPipeline = new MapperPipeline.Builder<float[]>()
                .withCoverAlgorithm(new SearchCover.Builder<float[]>()
                        .withSearchAlgorithm(new BallSearch.Builder<float[]>()
                                .withMetric(TopologyUtils.pullback(lens, metric))
                                .withRadius(0.15)))
                .withClusteringAlgorithm(ClusteringUtils.trivialClusteringBuilder())
                .build();

        graph = mapperPipeline.run(dataset);
        assertEquals(2, graph.countConnectedComponents());
    }

    @Test
    public void testTwoConnectedComponents() throws MapperException {
        ArrayList<float[]> dataset = new ArrayList<>();
        dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] { 1.0f, 0.0f }, 0.3f));
        dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] { 0.0f, 1.0f }, 0.3f));

        MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
                .withCoverAlgorithm(new SearchCover.Builder<float[]>()
                        .withSearchAlgorithm(new BallSearch.Builder<float[]>()
                                .withMetric(TopologyUtils.pullback(lens, metric))
                                .withRadius(0.5)))
                .withClusteringAlgorithm(ClusteringUtils.trivialClusteringBuilder())
                .build();

        MapperGraph graph = mapperPipeline.run(dataset);
        assertEquals(2, graph.countConnectedComponents());
    }

}

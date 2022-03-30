package org.lucasimi.tda.mapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.clustering.ClusteringAlgorithm;
import org.lucasimi.tda.mapper.clustering.ClusteringUtils;
import org.lucasimi.tda.mapper.cover.BallCover;
import org.lucasimi.tda.mapper.graph.MapperGraph;
import org.lucasimi.tda.mapper.pipeline.MapperException;
import org.lucasimi.tda.mapper.pipeline.MapperPipeline;
import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.Point;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperTest.class);

	ClusteringAlgorithm<Point<float[]>> clusterer = ClusteringUtils.trivialClustering();
	
	Lens<Point<float[]>, Point<float[]>> lens = TopologyUtils.identity();

	Metric<Point<float[]>> metric = TopologyUtils.euclideanMetric();

	@Test
	public void testMapperPerf() throws MapperException {
		int dim = 128;
		int k = 5;
		int size = (int) Math.pow(10, k);
		float side = 1.0f;
		long t0 = System.currentTimeMillis();
		ArrayList<Point<float[]>> dataset = DatasetGenerator.randomDataset(size, dim, 0.0f, side);
		long t1 = System.currentTimeMillis();
		float radius = (float) (side * Math.sqrt(dim) / Math.pow(size, 1.0 / dim));
		LOGGER.info("Dataset created in {}", t1 - t0);
		long startTime = System.currentTimeMillis();

		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new BallCover<>(lens, metric, radius * 0.4))
			.withClustering(clusterer)
			.build();
			
		MapperGraph mapperGraph = mapperPipeline.run(dataset);
		long endTime = System.currentTimeMillis();
		LOGGER.info("Mapper computed in {}", endTime - startTime);
		LOGGER.info("Mapper graph has {} vertices", mapperGraph.getVertices().size());
		LOGGER.info("Mapper graph has {} connected components", mapperGraph.countConnectedComponents());
	}

	@Test
	public void testBase() throws MapperException {
		List<Point<float[]>> dataset = new LinkedList<>();
		dataset.add(new Point<>(0, new float[] {0.0f, 1.0f}));
		dataset.add(new Point<>(1, new float[] {1.0f, 1.0f}));
		dataset.add(new Point<>(2, new float[] {1.0f, 0.0f}));

		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new BallCover<>(lens, metric, 0.5))
			.withClustering(clusterer)
			.build();
		
		MapperGraph graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(Integer.valueOf(3), graph.countConnectedComponents());
	}
	
	@Test
	public void testBaseTwoConnectedComponents() throws MapperException {
		List<Point<float[]>> dataset = new LinkedList<>();
		// first cc
		dataset.add(new Point<>(0, new float[] {0.0f, 1.0f}));
		dataset.add(new Point<>(1, new float[] {0.0f, 1.1f}));
		dataset.add(new Point<>(2, new float[] {0.0f, 1.2f}));
		// second cc
		dataset.add(new Point<>(3, new float[] {0.0f, -1.0f}));
		dataset.add(new Point<>(4, new float[] {0.0f, -1.1f}));
		dataset.add(new Point<>(5, new float[] {0.0f, -1.2f}));
		
		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new BallCover<>(lens, metric, 0.09))
			.withClustering(clusterer)
			.build();

		MapperGraph graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(6, graph.getVertices().size());
		Assertions.assertEquals(Integer.valueOf(6), graph.countConnectedComponents());
		
		mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new BallCover<>(lens, metric, 0.15))
			.withClustering(clusterer)
			.build();

		graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(Integer.valueOf(2), graph.countConnectedComponents());
	}
	
	@Test
	public void testTwoConnectedComponents() throws MapperException {
		ArrayList<Point<float[]>> dataset = new ArrayList<>();
		dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] {1.0f, 0.0f}, 0.3f));
		dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] {0.0f, 1.0f}, 0.3f));
		
		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new BallCover<>(lens, metric, 0.03))
			.withClustering(clusterer)
			.build();

		MapperGraph graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(Integer.valueOf(2), graph.countConnectedComponents());
	}
		
}

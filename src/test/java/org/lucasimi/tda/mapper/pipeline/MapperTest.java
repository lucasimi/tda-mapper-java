package org.lucasimi.tda.mapper.pipeline;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.clustering.ClusteringAlgorithm;
import org.lucasimi.tda.mapper.clustering.ClusteringUtils;
import org.lucasimi.tda.mapper.cover.BallSearch;
import org.lucasimi.tda.mapper.cover.SearchCover;
import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperTest.class);

	ClusteringAlgorithm<float[]> clusterer = ClusteringUtils.trivialClustering();
	
	Lens<float[], float[]> lens = TopologyUtils.identity();

	Metric<float[]> metric = TopologyUtils.euclideanMetric();

	@Test
	public void testMapperPerf() throws MapperException {
		int dim = 128;
		int k = 4;
		int size = (int) Math.pow(10, k);
		float side = 1.0f;
		long t0 = System.currentTimeMillis();
		ArrayList<float[]> dataset = DatasetGenerator.randomDataset(size, dim, 0.0f, side);
		long t1 = System.currentTimeMillis();
		float radius = (float) (side * Math.sqrt(dim) / Math.pow(size, 1.0 / dim));
		LOGGER.info("Dataset created in {}ms", t1 - t0);
		long startTime = System.currentTimeMillis();

		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new SearchCover<>(new BallSearch<>(lens, metric, radius * 0.4)))
			.withClustering(clusterer)
			.build();
			
		MapperGraph<float[]> mapperGraph = mapperPipeline.run(dataset);
		long endTime = System.currentTimeMillis();
		LOGGER.info("Mapper computed in {}ms", endTime - startTime);
		int vertNum = mapperGraph.getVertices().size();
		int ccNum = mapperGraph.countConnectedComponents();
		LOGGER.info("Mapper graph has {} vertices and {} connected components", vertNum, ccNum);
	}

	@Test
	public void testBase() throws MapperException {
		List<float[]> dataset = new LinkedList<>();
		dataset.add(new float[] {0.0f, 1.0f});
		dataset.add(new float[] {1.0f, 1.0f});
		dataset.add(new float[] {1.0f, 0.0f});

		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new SearchCover<>(new BallSearch<>(lens, metric, 0.5)))
			.withClustering(clusterer)
			.build();
		
		MapperGraph<float[]> graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(3, graph.countConnectedComponents());
	}
	
	@Test
	public void testBaseTwoConnectedComponents() throws MapperException {
		List<float[]> dataset = new LinkedList<>();
		// first cc
		dataset.add(new float[] {0.0f, 1.0f});
		dataset.add(new float[] {0.0f, 1.1f});
		dataset.add(new float[] {0.0f, 1.2f});
		// second cc
		dataset.add(new float[] {0.0f, -1.0f});
		dataset.add(new float[] {0.0f, -1.1f});
		dataset.add(new float[] {0.0f, -1.2f});
		
		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new SearchCover<>(new BallSearch<>(lens, metric, 0.09)))
			.withClustering(clusterer)
			.build();

		MapperGraph<float[]> graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(6, graph.getVertices().size());
		Assertions.assertEquals(6, graph.countConnectedComponents());
		
		mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new SearchCover<>(new BallSearch<>(lens, metric, 0.15)))
			.withClustering(clusterer)
			.build();

		graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(2, graph.countConnectedComponents());
	}
	
	@Test
	public void testTwoConnectedComponents() throws MapperException {
		ArrayList<float[]> dataset = new ArrayList<>();
		dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] {1.0f, 0.0f}, 0.3f));
		dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] {0.0f, 1.0f}, 0.3f));
		
		MapperPipeline<float[]> mapperPipeline = new MapperPipeline.Builder<float[]>()
			.withCover(new SearchCover<>(new BallSearch<>(lens, metric, 0.03)))
			.withClustering(clusterer)
			.build();

		MapperGraph<float[]> graph = mapperPipeline.run(dataset);
		Assertions.assertEquals(2, graph.countConnectedComponents());
	}
		
}

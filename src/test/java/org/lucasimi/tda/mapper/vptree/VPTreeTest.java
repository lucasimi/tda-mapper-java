package org.lucasimi.tda.mapper.vptree;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VPTreeTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(VPTreeTest.class);

	private static final int MAX_POWER = 10;

	private static final int BASE = 2;

	private static final int DIMENSION = 128;
    
    private Metric<float[]> metric = TopologyUtils.euclideanMetric();

    @Test
	public void testVPTreeBuild() throws InterruptedException {
		int size = (int) Math.pow(BASE, MAX_POWER);
		long t0 = System.currentTimeMillis();
		ArrayList<float[]> dataset = DatasetGenerator.randomDataset(size, DIMENSION, 0.0f, 1.0f);
		long t1 = System.currentTimeMillis();
		LOGGER.info("Dataset created in {}ms", t1 - t0);
		long startTime = System.currentTimeMillis();
		new VPTree<>(metric, dataset, 1000, 0.2);
		long endTime = System.currentTimeMillis();
		LOGGER.info("VPTree created in {}ms", endTime - startTime);
	}

    @Test
	public void testVPTreeBuildPerf() throws InterruptedException {
		for (int k = 0; k <= MAX_POWER; k++) {
			int size = (int) Math.pow(BASE, k);
			ArrayList<float[]> dataset = DatasetGenerator.randomDataset(size, DIMENSION, 0.0f, 1.0f);
			long startTime = System.currentTimeMillis();
			new VPTree<>(metric, dataset, 1, 0.0);
			long endTime = System.currentTimeMillis();
			long delta = endTime - startTime;
			float expected = size * (float) Math.log(size);
			LOGGER.info("VPTree Creation Performance: for k = {} => ratio = {}", k, delta / expected);
		}
	}

    @Test
	public void testBallSearchSingleton() {
		ArrayList<float[]> dataset = new ArrayList<>(1);
		float[] point = new float[] {1.0f};
		dataset.add(point);
		VPTree<float[]> ballTree = new VPTree<>(metric, dataset, 1);
		Collection<float[]> res = ballTree.ballSearch(point, 10.0);
		Assertions.assertTrue(res.contains(point));
	}

    @Test
	public void testBallSearchLine() {
		ArrayList<float[]> dataset = new ArrayList<>();
		int size = (int) Math.pow(BASE, MAX_POWER);
		for (int i = 0; i < size; i++) {
			dataset.add(new float[] {Float.valueOf(i)});
		}
		VPTree<float[]> ballTree = new VPTree<>(metric, dataset, 100);
		for (float[] point : dataset) {
			Collection<float[]> res = ballTree.ballSearch(point, 1.5);
			Assertions.assertTrue(res.size() <= 3);
			Assertions.assertTrue(res.contains(point));
		}	
	}

	@Test
	public void testBallSearchDuplicates() {
		ArrayList<float[]> dataset = new ArrayList<>();
		int size = (int) Math.pow(BASE, MAX_POWER);
		for (int i = 0; i < size; i++) {
			dataset.add(new float[] {0.0f});
		}
		VPTree<float[]> ballTree = new VPTree<>(metric, dataset, 1);
		Collection<float[]> res = ballTree.ballSearch(new float[]{0.0f}, 1.5);
		Assertions.assertEquals(size, res.size());
	}

    @Test
	public void testBallSearchPerf() {
		for (int k = 0; k <= MAX_POWER; k++) {
			int size = (int) Math.pow(BASE, k);
			ArrayList<float[]> dataset = DatasetGenerator.randomDataset(size, DIMENSION, 0.0f, 1.0f);
			VPTree<float[]> ballTree = new VPTree<>(metric, dataset, 100);
			long startTime = System.currentTimeMillis();
			for (float[] point : dataset) {
				ballTree.ballSearch(point, 2.0);
			}	
			long endTime = System.currentTimeMillis();
			float delta = endTime - startTime;
			float expected = size * (float) Math.log(size);
			LOGGER.info("Ball Search Performance: for k = {} => ratio = {}", k, delta / expected);
		}
	}

	@Test
	public void testKNNSearch() {
		ArrayList<float[]> dataset = DatasetGenerator.linearDataset(1, 1);
		VPTree<float[]> ballTree = new VPTree<>(metric, dataset, 100);
		for (float[] point : dataset) {
			Collection<float[]> res = ballTree.knnSearch(point, 10);
			Assertions.assertTrue(res.contains(point));
			Assertions.assertTrue(res.size() == 1);
		}
	}

	@Test
	public void testKNNSearchLine() {
		int size = (int) Math.pow(BASE, MAX_POWER);
		ArrayList<float[]> dataset = DatasetGenerator.linearDataset(size, 1);
		VPTree<float[]> ballTree = new VPTree<>(metric, dataset, 100);
		for (float[] point : dataset) {
			Collection<float[]> res = ballTree.knnSearch(point, 10);
			Assertions.assertTrue(res.size() <= 10);
			Assertions.assertTrue(res.contains(point));

			boolean flag = false;
			for (float[] x : res) {
				if (metric.evaluate(x, point) == 0.0) {
					flag = true;
				}
			} 
			Assertions.assertTrue(flag);
		}	
	}

	@Test
	public void testKNNSearchPerf() {
		for (int k = 0; k <= MAX_POWER; k++) {
			int size = (int) Math.pow(BASE, k);
			ArrayList<float[]> dataset = DatasetGenerator.randomDataset(size, DIMENSION, 0.0f, 1.0f);
			VPTree<float[]> ballTree = new VPTree<>(metric, dataset, 100);
			long startTime = System.currentTimeMillis();
			for (float[] point : dataset) {
				ballTree.knnSearch(point, 10);
			}	
			long endTime = System.currentTimeMillis();
			float delta = endTime - startTime;
			float expected = size * (float) Math.log(size);
			LOGGER.info("KNN Search Performance: for k = {} => ratio = {}", k, delta / expected);
		}
	}

}

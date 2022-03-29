package org.lucasimi.tda.mapper.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Point;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PivoterTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PivoterTest.class);

	Lens<Point<float[]>, Float> norm = TopologyUtils.euclideanNorm();
	
	@Test
	public void testPivotLinearity() {
		int dimension = 10;
		Random rand = new Random();
		for (int k = 0; k < 6; k++) {
			int size = (int) Math.pow(10, k);
			ArrayList<Point<float[]>> dataset = DatasetGenerator.randomDataset(size, dimension, 0.0f, 1.0f);
			long startTime = System.currentTimeMillis();
			
			int order = rand.ints(0, dataset.size()).findFirst().getAsInt();
			Pivoter.quickSelect(norm, dataset, 0, dataset.size(), order);
			for (int i = 0; i < order; i++) {
				Assertions.assertTrue(norm.evaluate(dataset.get(i)) <= norm.evaluate(dataset.get(order)));
			}
			for (int i = order; i < dataset.size(); i++) {
				Assertions.assertTrue(norm.evaluate(dataset.get(i)) >= norm.evaluate(dataset.get(order)));
			}
			long endTime = System.currentTimeMillis();
			float delta = endTime - startTime;
			float expected = size;
			LOGGER.info("Pivoter Performance: for k = {} => ratio = {}", k, delta / expected);
		}
	}

	@Test
	public void testMin() {
		ArrayList<Point<float[]>> array = new ArrayList<>();
		array.add(new Point<>(0, new float[] { 1.0f }));
		array.add(new Point<>(1, new float[] { 0.0f }));
		Pivoter.quickSelect(norm, array, 0, array.size(), 0);
		Point<float[]> foundMin = array.get(0);
		Assertions.assertEquals(norm.evaluate(findMin(array)), norm.evaluate(foundMin));
	}
	
	@Test
	public void testMinRandom() {
		int times = 1000;
		for (int i = 0; i < times; i++) {
			ArrayList<Point<float[]>> array = DatasetGenerator.randomDataset(100, 100, 0.0f, 1.0f);
			Pivoter.quickSelect(norm, array, 0, array.size(), 0);
			Point<float[]> foundMin = array.get(0);
			Assertions.assertEquals(norm.evaluate(findMin(array)), norm.evaluate(foundMin));
		}
	}
	
	Point<float[]> findMin(Collection<Point<float[]>> array) {
		Point<float[]> bestPoint = null;
		for (Point<float[]> point : array) {
			if ((bestPoint == null) || (norm.evaluate(point) < norm.evaluate(bestPoint))) {
				bestPoint = point;
			}
		}
		return bestPoint;
	}
	
}

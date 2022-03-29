package org.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.Point;
import org.lucasimi.tda.mapper.topology.TopologyUtils;

public class BallCoverTest {

	Metric<Point<float[]>> metric = TopologyUtils.euclideanMetric();

	Lens<Point<float[]>, Point<float[]>> lens = TopologyUtils.identity();

	@Test
	public void testOneCover() {
		List<Point<float[]>> dataset = new LinkedList<>();
		dataset.add(new Point<float[]>(0, new float[] {0.0f, 1.0f}));
		dataset.add(new Point<float[]>(1, new float[] {1.0f, 1.0f}));
		dataset.add(new Point<float[]>(2, new float[] {1.0f, 0.0f}));
				
		BallCover<Point<float[]>> covering = new BallCover<>(lens, metric, 0.5);
		Collection<Collection<Point<float[]>>> groups = covering.groups(dataset);
		Assertions.assertEquals(3, groups.size());
		
		covering = new BallCover<>(lens, metric, 1.5);
		groups = covering.groups(dataset);
		Assertions.assertEquals(1, groups.size());
	}
	
	@Test
	public void testTwoCovers() {
		List<Point<float[]>> dataset = new LinkedList<>();
		// first cc
		dataset.add(new Point<float[]>(0, new float[] {0.0f, 1.0f}));
		dataset.add(new Point<float[]>(1, new float[] {0.0f, 1.1f}));
		dataset.add(new Point<float[]>(2, new float[] {0.0f, 1.2f}));
		// second cc
		dataset.add(new Point<float[]>(3, new float[] {0.0f, -1.0f}));
		dataset.add(new Point<float[]>(4, new float[] {0.0f, -1.1f}));
		dataset.add(new Point<float[]>(5, new float[] {0.0f, -1.2f}));
		
		BallCover<Point<float[]>> covering = new BallCover<>(lens, metric, 0.5);
		Collection<Collection<Point<float[]>>> groups = covering.groups(dataset);
		Assertions.assertEquals(2, groups.size());
		
		covering = new BallCover<>(lens, metric, 0.09);
		groups = covering.groups(dataset);
		Assertions.assertEquals(6, groups.size());
	}
	
	@Test
	public void testRandom() {
		ArrayList<Point<float[]>> dataset = new ArrayList<>();
		dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] {1.0f, 0.0f}, 0.3f));
		dataset.addAll(DatasetGenerator.randomDataset(20000, 2, new float[] {0.0f, 1.0f}, 0.3f));
		
		BallCover<Point<float[]>> covering = new BallCover<>(lens, metric, 0.85);
		Collection<Collection<Point<float[]>>> groups = covering.groups(dataset);
		Assertions.assertEquals(2, groups.size());
	}
	
}

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
import org.lucasimi.tda.mapper.topology.TopologyUtils;

public class KNNCoverTest {

	Metric<float[]> metric = TopologyUtils.euclideanMetric();

	Lens<float[], float[]> lens = TopologyUtils.identity();

	@Test
	public void testOneCover() {
		List<float[]> dataset = new LinkedList<>();
		dataset.add(new float[] {0.0f, 1.0f});
		dataset.add(new float[] {1.0f, 1.0f});
		dataset.add(new float[] {1.0f, 0.0f});
				
		KNNCover<float[]> covering = new KNNCover<>(lens, metric, 3);
		Collection<Collection<float[]>> groups = covering.groups(dataset);
		Assertions.assertEquals(1, groups.size());
		
		covering = new KNNCover<>(lens, metric, 1);
		groups = covering.groups(dataset);
		Assertions.assertEquals(3, groups.size());
	}
	
	@Test
	public void testTwoCovers() {
		List<float[]> dataset = new LinkedList<>();
		// first cc
		dataset.add(new float[] {0.0f, 1.0f});
		dataset.add(new float[] {0.0f, 1.1f});
		dataset.add(new float[] {0.0f, 1.2f});
		// second cc
		dataset.add(new float[] {0.0f, -1.0f});
		dataset.add(new float[] {0.0f, -1.1f});
		dataset.add(new float[] {0.0f, -1.2f});
		
		KNNCover<float[]> covering = new KNNCover<>(lens, metric, 3);
		Collection<Collection<float[]>> groups = covering.groups(dataset);
		Assertions.assertEquals(2, groups.size());
		
		covering = new KNNCover<>(lens, metric, 1);
		groups = covering.groups(dataset);
		Assertions.assertEquals(6, groups.size());
	}
	
	@Test
	public void testRandom() {
		ArrayList<float[]> dataset = new ArrayList<>();
		dataset.addAll(DatasetGenerator.randomDataset(2000, 2, new float[] {1.0f, 0.0f}, 0.1f));
		dataset.addAll(DatasetGenerator.randomDataset(2000, 2, new float[] {0.0f, 1.0f}, 0.1f));
		KNNCover<float[]> covering = new KNNCover<>(lens, metric, 2000);
		Collection<Collection<float[]>> groups = covering.groups(dataset);
		Assertions.assertEquals(2, groups.size());
	}
	
}

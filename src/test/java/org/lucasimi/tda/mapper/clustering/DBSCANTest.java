package org.lucasimi.tda.mapper.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.topology.TopologyUtils;

public class DBSCANTest {

    @Test
    public void testDBSCANFaster() {
        Collection<float[]> dataset1 = DatasetGenerator.randomDataset(100, 2, 0.0f, 10.0f);
        Collection<float[]> dataset2 = DatasetGenerator.randomDataset(100, 2, 30.0f, 40.0f);
        Collection<float[]> dataset = new ArrayList<>(dataset1.size() + dataset2.size());
        dataset.addAll(dataset1);
        dataset.addAll(dataset2);

        ClusteringAlgorithm<float[]> dbscan = new DBSCANFaster.Builder<float[]>()
                .withMetric(TopologyUtils.euclideanMetric())
                .withEps(15.0)
                .withMinSamples(5)
                .build();

        Collection<Collection<float[]>> clusters = dbscan.run(dataset);
        assertEquals(2, clusters.size());
    }

    @Test
    public void testDBSCANFasterMinSamples() {
        Collection<float[]> dataset = DatasetGenerator.randomDataset(100, 2, 0.0f, 10.0f);
        ClusteringAlgorithm<float[]> dbscan = new DBSCANFaster.Builder<float[]>()
                .withMetric(TopologyUtils.euclideanMetric())
                .withEps(15.0)
                .withMinSamples(101)
                .build();
        Collection<Collection<float[]>> clustersMin = dbscan.run(dataset);
        assertEquals(0, clustersMin.size());
    }

    @Test
    public void testDBSCANSimple() {
        Collection<float[]> dataset1 = DatasetGenerator.randomDataset(100, 2, 0.0f, 10.0f);
        Collection<float[]> dataset2 = DatasetGenerator.randomDataset(100, 2, 30.0f, 40.0f);
        Collection<float[]> dataset = new ArrayList<>(dataset1.size() + dataset2.size());
        dataset.addAll(dataset1);
        dataset.addAll(dataset2);

        ClusteringAlgorithm<float[]> dbscan = new DBSCANSimple.Builder<float[]>()
                .withMetric(TopologyUtils.euclideanMetric())
                .withEps(15.0)
                .withMinSamples(5)
                .build();

        Collection<Collection<float[]>> clusters = dbscan.run(dataset);
        assertEquals(2, clusters.size());
    }

    @Test
    public void testDBSCANSimpleMinSamples() {
        Collection<float[]> dataset = DatasetGenerator.randomDataset(100, 2, 0.0f, 10.0f);
        ClusteringAlgorithm<float[]> dbscan = new DBSCANSimple.Builder<float[]>()
                .withMetric(TopologyUtils.euclideanMetric())
                .withEps(15.0)
                .withMinSamples(101)
                .build();
        Collection<Collection<float[]>> clustersMin = dbscan.run(dataset);
        assertEquals(0, clustersMin.size());
    }

}

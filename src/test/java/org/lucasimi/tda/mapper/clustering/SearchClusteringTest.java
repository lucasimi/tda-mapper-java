package org.lucasimi.tda.mapper.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.search.BallSearch;
import org.lucasimi.tda.mapper.topology.TopologyUtils;

public class SearchClusteringTest {

    @Test
    public void testBallSearch() {
        Collection<float[]> dataset1 = DatasetGenerator.randomDataset(1, 2, 0.0f, 10.0f);
        Collection<float[]> dataset2 = DatasetGenerator.randomDataset(1, 2, 30.0f, 40.0f);
        Collection<float[]> dataset = new ArrayList<>(dataset1.size() + dataset2.size());
        dataset.addAll(dataset1);
        dataset.addAll(dataset2);

        ClusteringAlgorithm<float[]> searchClustering = new SearchClustering.Builder<float[]>()
                .withSearchAlgorithm(new BallSearch.Builder<float[]>()
                        .withMetric(TopologyUtils.euclideanMetric())
                        .withRadius(15.0))
                .build();

        Collection<Collection<float[]>> clusters = searchClustering.run(dataset);
        assertEquals(2, clusters.size());

    }

}

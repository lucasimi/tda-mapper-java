package org.lucasimi.tda.mapper.clustering;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;

public class TrivialClusteringTest {

    @Test
    public void testTrivialCover() {
        Collection<float[]> dataset = DatasetGenerator.randomDataset(100, 100, 0.0f, 1.0f);
        Clustering<float[]> clustering = TrivialClustering.<float[]>newBuilder().build();
        Collection<Collection<float[]>> clusters = clustering.run(dataset);
        Assertions.assertEquals(1, clusters.size());
    }

}

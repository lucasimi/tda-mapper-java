package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;

public class CoverUtilsTest {

    @Test
    public void testTrivialCover() {
        Collection<float[]> dataset = DatasetGenerator.randomDataset(100, 100, 0.0f, 1.0f);
        CoverAlgorithm<float[]> coverAlgorithm = CoverUtils.trivialCover();
        Collection<Collection<float[]>> clusters = coverAlgorithm.fit(dataset).getCover();
        Assertions.assertEquals(1, clusters.size());
    }

}

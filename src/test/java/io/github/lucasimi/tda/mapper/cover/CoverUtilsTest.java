package io.github.lucasimi.tda.mapper.cover;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.lucasimi.tda.mapper.DatasetGenerator;

public class CoverUtilsTest {

    @Test
    public void testTrivialCover() {
        Collection<float[]> dataset = DatasetGenerator.randomDataset(100, 100, 0.0f, 1.0f);
        Cover<float[]> coverAlgorithm = CoverUtils.trivialCover();
        Collection<Collection<float[]>> clusters = coverAlgorithm.run(dataset);
        Assertions.assertEquals(1, clusters.size());
    }

}

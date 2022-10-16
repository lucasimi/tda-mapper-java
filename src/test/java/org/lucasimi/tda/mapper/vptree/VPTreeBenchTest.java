package org.lucasimi.tda.mapper.vptree;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.DatasetGenerator;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VPTreeBenchTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(VPTreeBenchTest.class);

    private static final int MAX_POWER = 5;

    private static final int BASE = 10;

    private static final int DIMENSION = 128;

    private Metric<float[]> metric = TopologyUtils.euclideanMetric();

    @Test
    public void benchVPTreeRandom() throws InterruptedException {
        int size = (int) Math.pow(BASE, MAX_POWER);
        long t0 = System.currentTimeMillis();
        ArrayList<float[]> dataset = DatasetGenerator.randomDataset(size, DIMENSION, 0.0f, 1.0f);
        long t1 = System.currentTimeMillis();
        LOGGER.info("Bench random: Dataset created in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        VPTree<float[]> vpTree = new VPTree<>(metric, dataset, 1000, 0.2);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench random: VPTree created in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        VPTreeImp<float[]> vpTreeImp = new VPTreeImp<>(metric, dataset, 1000, 0.2);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench random: VPTreeImp created in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        float[] point = dataset.get(0);
        vpTree.ballSearch(point, 1.5);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench random: VPTree searched in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        vpTreeImp.ballSearch(point, 1.5);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench random: VPTreeImp searched in {}ms", t1 - t0);
    }

    // @Test
    public void benchVPTreeLine() {
        ArrayList<float[]> dataset = new ArrayList<>();
        int size = (int) Math.pow(BASE, MAX_POWER);
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            dataset.add(new float[] { Float.valueOf(i) });
        }
        long t1 = System.currentTimeMillis();
        LOGGER.info("Bench line: Dataset created in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        VPTree<float[]> vpTree = new VPTree<>(metric, dataset, 100);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench line: VPTree created in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        VPTreeImp<float[]> vpTreeImp = new VPTreeImp<>(metric, dataset, 10, 100);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench line: VPTreeImp created in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        float[] point = dataset.get(0);
        vpTree.ballSearch(point, 1.5);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench line: VPTree searched in {}ms", t1 - t0);
        t0 = System.currentTimeMillis();
        vpTreeImp.ballSearch(point, 1.5);
        t1 = System.currentTimeMillis();
        LOGGER.info("Bench line: VPTreeImp searched in {}ms", t1 - t0);
    }

}

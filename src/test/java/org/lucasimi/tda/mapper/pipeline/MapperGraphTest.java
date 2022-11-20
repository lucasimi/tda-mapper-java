package org.lucasimi.tda.mapper.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.lucasimi.tda.mapper.pipeline.MapperGraph.Vertex;

public class MapperGraphTest {

    @Test
    public void testEmptyGraph() {
        List<float[]> dataset = new ArrayList<>();
        Collection<Collection<float[]>> clusters = new ArrayList<>();
        MapperGraph graph = new MapperGraph(dataset, clusters);
        Map<Integer, Set<Vertex>> cc = graph.getConnectedComponents();
        assertTrue(cc.isEmpty());
    }

    @Test
    public void testSingletonGraph() {
        List<float[]> dataset = new ArrayList<>();
        float[] point = new float[] { 0.0f };
        dataset.add(point);
        Collection<Collection<float[]>> clusters = new ArrayList<>();
        Collection<float[]> cluster = new ArrayList<>();
        cluster.add(point);
        clusters.add(cluster);
        MapperGraph graph = new MapperGraph(dataset, clusters);
        Map<Integer, Set<Vertex>> cc = graph.getConnectedComponents();
        assertEquals(1, cc.size());
        assertTrue(cc.containsKey(0));
        assertEquals(graph.getVertices(), cc.get(0));
    }

    @Test
    public void testLineGraph() {
        List<Integer> dataset = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dataset.add(i);
        }
        Collection<Collection<Integer>> clusters = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Collection<Integer> cluster = new LinkedList<>();
            cluster.add(i);
            if (i > 0) {
                cluster.add(i - 1);
            }
            if (i < 10) {
                cluster.add(i + 1);
            }
            clusters.add(cluster);
        }
        MapperGraph graph = new MapperGraph(dataset, clusters);
        Map<Integer, Set<Vertex>> cc = graph.getConnectedComponents();
        assertEquals(1, cc.size());
        assertTrue(cc.containsKey(0));
        assertEquals(graph.getVertices(), cc.get(0));
    }

    @Test
    public void testTwoLineGraph() {
        List<Integer> dataset = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataset.add(i);
        }
        Collection<Collection<Integer>> clusters = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Collection<Integer> cluster = new HashSet<>();
            cluster.add(i);
            if (i > 0) {
                cluster.add(i - 1);
            }
            if (i < 9) {
                cluster.add(i + 1);
            }
            clusters.add(cluster);
        }
        for (int i = 10; i < 20; i++) {
            Collection<Integer> cluster = new HashSet<>();
            cluster.add(i);
            if (i > 10) {
                cluster.add(i - 1);
            }
            if (i < 19) {
                cluster.add(i + 1);
            }
            clusters.add(cluster);
        }
        MapperGraph graph = new MapperGraph(dataset, clusters);
        Map<Integer, Set<Vertex>> cc = graph.getConnectedComponents();
        assertEquals(2, cc.size());
        Set<MapperGraph.Vertex> union = new HashSet<>();
        union.addAll(cc.get(0));
        union.addAll(cc.get(1));
        assertEquals(graph.getVertices(), union);
    }

}

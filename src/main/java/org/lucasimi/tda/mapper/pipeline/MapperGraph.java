package org.lucasimi.tda.mapper.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperGraph.class);

    public class Vertex {

        private int id;

        private final Collection<Integer> points;

        public Vertex(int id, Collection<Integer> points) {
            this.id = id;
            this.points = points;
        }

        public int getId() {
            return this.id;
        }

        public Collection<Integer> getPoints() {
            return this.points;
        }

    }

    private Map<Vertex, Set<Vertex>> graph = new HashMap<>();

    public MapperGraph() {
    }

    public <S> MapperGraph(List<S> dataset, Collection<Collection<S>> clusters) {
        long t0 = System.currentTimeMillis();
        Map<S, Integer> pointIdMap = new HashMap<>(dataset.size());
        int pointId = 0;
        for (S point : dataset) {
            pointIdMap.put(point, pointId);
            pointId += 1;
        }
        Map<S, List<Vertex>> intersectionMap = new HashMap<>();
        int vertexId = 0;
        int extraCount = 0;
        for (Collection<S> cluster : clusters) {
            Vertex vertex = new Vertex(vertexId, new HashSet<>());
            this.graph.put(vertex, new HashSet<>());
            for (S point : cluster) {
                intersectionMap.putIfAbsent(point, new ArrayList<>());
                intersectionMap.get(point).add(vertex);
                if (!pointIdMap.containsKey(point)) {
                    pointIdMap.put(point, pointId);
                    pointId += 1;
                    extraCount += 1;
                    LOGGER.warn("Found extra point {} inside clusters", extraCount);
                }
                int pointToAddId = pointIdMap.get(point);
                vertex.getPoints().add(pointToAddId);
            }
            vertexId += 1;
        }
        long t1 = System.currentTimeMillis();
        for (List<Vertex> intersection : intersectionMap.values()) {
            int vertNum = intersection.size();
            for (int sId = 0; sId < vertNum; sId++) {
                Vertex source = intersection.get(sId);
                for (int tId = sId + 1; tId < vertNum; tId++) {
                    Vertex target = intersection.get(tId);
                    Set<Vertex> sourceAdjaciency = this.graph.get(source);
                    if (!sourceAdjaciency.contains(target)) {
                        sourceAdjaciency.add(target);
                    }
                    Set<Vertex> targetAdjaciency = this.graph.get(target);
                    if (!targetAdjaciency.contains(source)) {
                        targetAdjaciency.add(source);
                    }
                }
            }
        }
        long t2 = System.currentTimeMillis();
        LOGGER.debug("### Mapper Graph build report");
        LOGGER.debug("* total build time:    \t{}ms", t2 - t0);
        LOGGER.debug("* vertices build time: \t{}ms", t1 - t0);
        LOGGER.debug("* edges build time:    \t{}ms", t2 - t1);
    }

    public Map<Integer, Set<Vertex>> getConnectedComponents() {
        Map<Integer, Set<Vertex>> components = new HashMap<>();
        Set<Vertex> visited = new HashSet<>();
        int componentId = 0;
        for (Vertex source : this.graph.keySet()) {
            if (!visited.contains(source)) {
                Set<Vertex> component = new HashSet<>();
                components.put(componentId, component);
                componentId += 1;
                visitConnectedComponent(source, visited, component);
            }
        }
        return components;
    }

    public int countConnectedComponents() {
        Set<Vertex> visited = new HashSet<>();
        int connectedComponents = 0;
        for (Vertex source : this.graph.keySet()) {
            if (!visited.contains(source)) {
                connectedComponents += 1;
                visited.add(source);
                visitConnectedComponent(source, visited);
            }
        }
        return connectedComponents;
    }

    private void visitConnectedComponent(Vertex source, Set<Vertex> visited) {
        Set<Vertex> adjacient = this.graph.get(source);
        for (Vertex target : adjacient) {
            if (!visited.contains(target)) {
                visited.add(target);
                visitConnectedComponent(target, visited);
            }
        }
    }

    private void visitConnectedComponent(Vertex source, Set<Vertex> visited, Set<Vertex> component) {
        visited.add(source);
        component.add(source);
        Set<Vertex> adjacient = this.graph.get(source);
        for (Vertex target : adjacient) {
            if (!visited.contains(target)) {
                visitConnectedComponent(target, visited, component);
            }
        }
    }

    public Set<Vertex> getVertices() {
        return this.graph.keySet();
    }

    public Map<Vertex, Set<Vertex>> getMap() {
        return this.graph;
    }

}

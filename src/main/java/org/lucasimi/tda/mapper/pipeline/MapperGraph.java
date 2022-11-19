package org.lucasimi.tda.mapper.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperGraph<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperGraph.class);

    public class Vertex {

        private final Collection<S> points;

        public Vertex(Collection<S> points) {
            this.points = points;
        }

        public Collection<S> getPoints() {
            return this.points;
        }

    }

    private Map<Vertex, List<Vertex>> graph = new HashMap<>();

    private Map<S, Integer> pointIdMap = new HashMap<>();

    public MapperGraph() {
    }

    public MapperGraph(List<S> dataset, Collection<Collection<S>> clusters) {
        long t0 = System.currentTimeMillis();
        this.pointIdMap = new HashMap<>(dataset.size());
        int pointId = 0;
        for (S point : dataset) {
            this.pointIdMap.put(point, pointId++);
        }
        Map<S, List<Vertex>> intersectionMap = new HashMap<>();
        for (Collection<S> cluster : clusters) {
            Vertex vertex = new Vertex(cluster);
            this.graph.put(vertex, new ArrayList<>());
            for (S point : cluster) {
                intersectionMap.putIfAbsent(point, new ArrayList<>());
                intersectionMap.get(point).add(vertex);
                if (!this.pointIdMap.containsKey(point)) {
                    this.pointIdMap.put(point, pointId++);
                }
            }
        }
        long t1 = System.currentTimeMillis();
        for (List<Vertex> intersection : intersectionMap.values()) {
            int vertNum = intersection.size();
            for (int sId = 0; sId < vertNum; sId++) {
                Vertex source = intersection.get(sId);
                for (int tId = sId + 1; tId < vertNum; tId++) {
                    Vertex target = intersection.get(tId);
                    this.graph.get(source).add(target);
                    this.graph.get(target).add(source);
                }
            }
        }
        long t2 = System.currentTimeMillis();
        LOGGER.debug("### Mapper Graph build report");
        LOGGER.debug("* total build time:    \t{}ms", t2 - t0);
        LOGGER.debug("* vertices build time: \t{}ms", t1 - t0);
        LOGGER.debug("* edges build time:    \t{}ms", t2 - t1);
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
        List<Vertex> adjacient = this.graph.get(source);
        for (Vertex target : adjacient) {
            if (!visited.contains(target)) {
                visited.add(target);
                visitConnectedComponent(target, visited);
            }
        }
    }

    public Set<Vertex> getVertices() {
        return this.graph.keySet();
    }

    public MapperGraph<Integer> getStructureMap() {
        MapperGraph<Integer> structureGraph = new MapperGraph<>();
        structureGraph.pointIdMap = new HashMap<>(this.pointIdMap.size());
        for (Integer pointId : this.pointIdMap.values()) {
            structureGraph.pointIdMap.put(pointId, pointId);
        }
        Set<Vertex> vertices = this.graph.keySet();
        Map<Vertex, MapperGraph<Integer>.Vertex> vertexMap = new HashMap<>(vertices.size());
        for (Vertex vertex : vertices) {
            Collection<Integer> pointIds = vertex.getPoints().stream()
                    .map(this.pointIdMap::get)
                    .collect(Collectors.toList());
            MapperGraph<Integer>.Vertex structureVertex = structureGraph.new Vertex(pointIds);
            vertexMap.put(vertex, structureVertex);
            structureGraph.graph.put(structureVertex, new ArrayList<>());
        }
        for (Entry<Vertex, List<Vertex>> entry : this.graph.entrySet()) {
            Vertex source = entry.getKey();
            MapperGraph<Integer>.Vertex sSource = vertexMap.get(source);
            for (Vertex target : entry.getValue()) {
                MapperGraph<Integer>.Vertex sTarget = vertexMap.get(target);
                structureGraph.graph.get(sSource).add(sTarget);
            }

        }
        return structureGraph;
    }

}

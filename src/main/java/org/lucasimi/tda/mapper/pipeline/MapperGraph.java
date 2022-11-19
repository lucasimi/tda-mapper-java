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

public class MapperGraph<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperGraph.class);

    private Map<Vertex, Set<Vertex>> graph = new HashMap<>();

    public MapperGraph(List<S> dataset, Collection<Collection<S>> clusters) {
        long t0 = System.currentTimeMillis();
        Map<S, List<Vertex>> intersections = new HashMap<>(dataset.size());
        for (S point : dataset) {
            intersections.put(point, new ArrayList<>());
        }
        for (Collection<S> cluster : clusters) {
            Vertex vertex = new Vertex(cluster);
            for (S point : cluster) {
                intersections.get(point).add(vertex);
            }
            this.graph.put(vertex, new HashSet<>());
        }
        long t1 = System.currentTimeMillis();
        for (List<Vertex> vertices : intersections.values()) {
            int vertexNum = vertices.size();
            for (int sourceId = 0; sourceId < vertexNum; sourceId++) {
                Vertex source = vertices.get(sourceId);
                for (int targetId = sourceId + 1; targetId < vertexNum; targetId++) {
                    Vertex target = vertices.get(targetId);
                    if (!this.graph.get(source).contains(target)) {
                        this.graph.get(source).add(target);
                        this.graph.get(target).add(source);
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

    public Set<Vertex> getVertices() {
        return this.graph.keySet();
    }

    public class Vertex {

        private final Collection<S> points;

        public Vertex(Collection<S> points) {
            this.points = points;
        }

        public Collection<S> getPoints() {
            return this.points;
        }

    }

}

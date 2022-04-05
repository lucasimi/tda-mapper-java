package org.lucasimi.tda.mapper.pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperGraph<S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperGraph.class);

    private Map<Vertex, Map<Vertex, Edge>> graph = new HashMap<>();

	public MapperGraph(Collection<Collection<S>> clusters) {
		long t0 = System.currentTimeMillis();
		Map<S, Collection<Vertex>> vertexMap = buildVertexMap(clusters);
		addEdges(vertexMap);
		long t1 = System.currentTimeMillis();
		LOGGER.info("Graph computed in {}ms", t1 - t0);
	}

    private Map<S, Collection<Vertex>> buildVertexMap(Collection<Collection<S>> clusters) {
		Map<S, Collection<Vertex>> vertexMap = new HashMap<>();
		for (Collection<S> cluster : clusters) {
			if (!cluster.isEmpty()) {
				Vertex vertex = new Vertex(cluster);
				this.graph.put(vertex, new HashMap<>());
				for (S data : cluster) {
					vertexMap.computeIfAbsent(data, x -> new LinkedList<>());
					vertexMap.get(data).add(vertex);
				}
			}
		}
		return vertexMap;
	}

	private void addEdges(Map<S, Collection<Vertex>> vertexMap) {
		for (Entry<S, Collection<Vertex>> entry : vertexMap.entrySet()) {
			for (Vertex source : entry.getValue()) {
				for (Vertex target : entry.getValue()) {
					if (!source.equals(target)) {
						if (this.graph.get(source).get(target) == null) {
							Edge edge = new Edge();
        					this.graph.get(source).put(target, edge);
						}
					}
				}
			}
		}
	}

	public Map<Vertex, Edge> getAdjaciency(Vertex source) {
		return this.graph.get(source);
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
		Map<Vertex, Edge> adjacient = this.graph.get(source);
		for (Vertex target : adjacient.keySet()) {
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

	public class Edge {

		private double union;

		private double intersection;

		public Edge() {
			this.union = 0;
			this.intersection = 0;
		}

	}

}

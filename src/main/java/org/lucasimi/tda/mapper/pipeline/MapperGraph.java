package org.lucasimi.tda.mapper.pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperGraph<S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperGraph.class);

    private Map<Vertex, Map<Vertex, Edge>> vertexMap = new HashMap<>();

	public MapperGraph(Collection<Collection<S>> clusters) {
		long t0 = System.currentTimeMillis();
		Map<S, Collection<Vertex>> vertexMap = buildVertexMap(clusters);
		addEdges(vertexMap);
		long t1 = System.currentTimeMillis();
		LOGGER.info("Graph computed in {}ms", t1 - t0);
	}

    private Map<S, Collection<Vertex>> buildVertexMap(Collection<Collection<S>> clusters) {
		Map<S, Collection<Vertex>> vertexMap = new HashMap<>();
		clusters.forEach(cluster -> {
			if (!cluster.isEmpty()) {
				Vertex vertex = new Vertex(cluster);
				this.vertexMap.put(vertex, new HashMap<>());
				cluster.forEach(data -> {
					vertexMap.computeIfAbsent(data, x -> new LinkedList<>());
					vertexMap.get(data).add(vertex);
				});
			}
		});
		return vertexMap;
	}

	private void addEdges(Map<S, Collection<Vertex>> vertexMap) {
		vertexMap.forEach((p, clusters) -> {
			clusters.forEach(source -> {
				clusters.forEach(target -> {
					if (!source.equals(target)) {
						this.vertexMap.get(source).computeIfAbsent(target, t -> new Edge());
					}
				});
			});
		});
		/*
		for (Entry<S, Collection<Vertex>> entry : vertexMap.entrySet()) {
			for (Vertex source : entry.getValue()) {
				for (Vertex target : entry.getValue()) {
					if (!source.equals(target)) {
						if (this.vertexMap.get(source).get(target) == null) {
							Edge edge = new Edge();
        					this.vertexMap.get(source).put(target, edge);
						}
						Edge edge = graph.getEdge(source, target);
						edge.addCommonPoint();
						double intersection = edge.getIntersection();
						double union = edge.getUnion();
					}
				}
			}
		}
		*/
	}

    public Integer countConnectedComponents() {
		Set<Vertex> visited = new HashSet<>();
		Integer connectedComponents = 0;
		for (Vertex source : this.vertexMap.keySet()) {
			if (!visited.contains(source)) {
				connectedComponents += 1;
				visited.add(source);
				visitConnectedComponent(source, visited);
			}
		}
		return connectedComponents;
	}

	private void visitConnectedComponent(Vertex source, Set<Vertex> visited) {
		Map<Vertex, Edge> adjacient = this.vertexMap.get(source);
		for (Vertex target : adjacient.keySet()) {
			if (!visited.contains(target)) {
				visited.add(target);
				visitConnectedComponent(target, visited);
			}
		}
	}

	public Set<Vertex> getVertices() {
		return this.vertexMap.keySet();
	}
	
	private class Vertex {
	
		private final Collection<S> points;

		public Vertex(Collection<S> points) {
			this.points = points;
		}

		public Collection<S> getPoints() {
			return this.points;
		}

	}

	private class Edge {

		private double union;

		private double intersection;

		public Edge() {
			this.union = 0;
			this.intersection = 0;
		}

	}

}

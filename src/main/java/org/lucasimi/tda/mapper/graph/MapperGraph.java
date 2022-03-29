package org.lucasimi.tda.mapper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapperGraph {

    private Map<MapperVertex, Map<MapperVertex, MapperEdge>> vertexMap = new HashMap<>();

    public void addVertex(MapperVertex vertex) {
        this.vertexMap.put(vertex, new HashMap<>());
    }

    public void setEdge(MapperVertex source, MapperVertex target, MapperEdge edge) {
        this.vertexMap.get(source).put(target, edge);
    }

    public MapperEdge getEdge(MapperVertex source, MapperVertex target) {
        return this.vertexMap.get(source).get(target);
    }

    public Integer countConnectedComponents() {
		Set<MapperVertex> visited = new HashSet<>();
		Integer connectedComponents = 0;
		for (MapperVertex source : this.vertexMap.keySet()) {
			if (!visited.contains(source)) {
				connectedComponents += 1;
				visited.add(source);
				visitConnectedComponent(source, visited);
			}
		}
		return connectedComponents;
	}

	private void visitConnectedComponent(MapperVertex source, Set<MapperVertex> visited) {
		Map<MapperVertex, MapperEdge> adjacient = this.vertexMap.get(source);
		for (MapperVertex target : adjacient.keySet()) {
			if (!visited.contains(target)) {
				visited.add(target);
				visitConnectedComponent(target, visited);
			}
		}
	}

    public Map<MapperVertex, MapperEdge> getAdjaciency(MapperVertex vertex) {
        return this.vertexMap.get(vertex);
    }

	public Set<MapperVertex> getVertices() {
		return this.vertexMap.keySet();
	}
	
}

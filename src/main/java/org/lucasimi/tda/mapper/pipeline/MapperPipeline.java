package org.lucasimi.tda.mapper.pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lucasimi.tda.mapper.clustering.ClusteringAlgorithm;
import org.lucasimi.tda.mapper.cover.CoverAlgorithm;
import org.lucasimi.tda.mapper.graph.MapperEdge;
import org.lucasimi.tda.mapper.graph.MapperGraph;
import org.lucasimi.tda.mapper.graph.MapperVertex;
import org.lucasimi.tda.mapper.topology.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperPipeline<S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperPipeline.class);

	private ClusteringAlgorithm<Point<S>> clusterer;

	private CoverAlgorithm<Point<S>> coverAlgorithm;

	private MapperPipeline(Builder<S> builder) {
		this.clusterer = builder.clustering;
		this.coverAlgorithm = builder.coverAlgorithm;
	}

	private Collection<Collection<Point<S>>> buildCover(List<Point<S>> dataset) {
		long t0 = System.currentTimeMillis();
		Collection<Collection<Point<S>>> cover = this.coverAlgorithm.groups(dataset);
		long t1 = System.currentTimeMillis();
		LOGGER.info("Pullback cover computed in {} milliseconds", t1 - t0);
		return cover;
	}

	private Collection<Collection<Point<S>>> computeClustering(Collection<Collection<Point<S>>> pullbackCover) {
		long t2 = System.currentTimeMillis();
		Stream<Collection<Point<S>>> parallel = pullbackCover.stream().parallel();
		LOGGER.info("Using parallel computation: {}", parallel.isParallel());
		Collection<Collection<Point<S>>> clusters = parallel
			.map(this.clusterer::performClustering)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		long t3 = System.currentTimeMillis();
		LOGGER.info("Clustering computed in {} milliseconds", t3 - t2);
		return clusters;
	}

	private MapperGraph buildGraph(Collection<Collection<Point<S>>> clusters) {
		long t4 = System.currentTimeMillis();
		MapperGraph graph = new MapperGraph();
		Map<Point<S>, Collection<MapperVertex>> vertexMap = buildVertexMap(graph, clusters);
		addEdges(graph, vertexMap);
		long t5 = System.currentTimeMillis();
		LOGGER.info("Graph computed in {} milliseconds", t5 - t4);
		return graph;
	}

	public MapperGraph run(List<Point<S>> dataset) {
		Collection<Collection<Point<S>>> cover = buildCover(dataset);
		Collection<Collection<Point<S>>> clusters = computeClustering(cover);
		return buildGraph(clusters);
	}

    private Map<Point<S>, Collection<MapperVertex>> buildVertexMap(MapperGraph graph, Collection<Collection<Point<S>>> clusters) {
		Map<Point<S>, Collection<MapperVertex>> vertexMap = new HashMap<>();
		for (Collection<Point<S>> cluster : clusters) {
			if (!cluster.isEmpty()) {
				MapperVertex vertex = buildVertex(cluster);
				graph.addVertex(vertex);
				for (Point<S> data : cluster) {
					vertexMap.computeIfAbsent(data, x -> new LinkedList<>());
					vertexMap.get(data).add(vertex);
				}
			}
		}
		return vertexMap;
	}

	private void addEdges(MapperGraph graph, Map<Point<S>, Collection<MapperVertex>> vertexMap) {
		for (Entry<Point<S>, Collection<MapperVertex>> entry : vertexMap.entrySet()) {
			for (MapperVertex source : entry.getValue()) {
				for (MapperVertex target : entry.getValue()) {
					if (!source.equals(target)) {
						if (graph.getEdge(source, target) == null) {
							MapperEdge edge = new MapperEdge();
							edge.setWeight(1.0);
							edge.setIntersection(0);
							edge.setUnion(source.getSize() + target.getSize());
							graph.setEdge(source, target, edge);
						}
						MapperEdge edge = graph.getEdge(source, target);
						double intersection = edge.getIntersection();
						double union = edge.getUnion();
						edge.setIntersection(intersection + 1);
						edge.setUnion(union - 1);
					}
				}
			}
		}
	}

    private MapperVertex buildVertex(Collection<Point<S>> dataset) {
		Collection<Integer> ids = dataset.stream()
			.map(Point::getId)
			.collect(Collectors.toList());
        return new MapperVertex(ids);
    }
	
	public static class Builder<S> {
		
		private CoverAlgorithm<Point<S>> coverAlgorithm;

		private ClusteringAlgorithm<Point<S>> clustering;

		public Builder<S> withCover(CoverAlgorithm<Point<S>> coveringAlgo) {
			this.coverAlgorithm = coveringAlgo;
			return this;
		}

		public Builder<S> withClustering(ClusteringAlgorithm<Point<S>> clustering) {
			this.clustering = clustering;
			return this;
		}

		public MapperPipeline<S> build() throws MapperException {
			if (this.clustering == null) {
				throw new MapperException.NoClusteringAlgorithm();
			} else if (this.coverAlgorithm == null) {
				throw new MapperException.NoCoverAlgorithm();
			} else {			
				return new MapperPipeline<>(this);
			}
		}
		
	}
	
}

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperPipeline<S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperPipeline.class);

	private ClusteringAlgorithm<S> clusterer;

	private CoverAlgorithm<S> coverAlgorithm;

	private MapperPipeline(Builder<S> builder) {
		this.clusterer = builder.clustering;
		this.coverAlgorithm = builder.coverAlgorithm;
	}

	private Collection<Collection<S>> buildCover(List<S> dataset) {
		long t0 = System.currentTimeMillis();
		Collection<Collection<S>> cover = this.coverAlgorithm.groups(dataset);
		long t1 = System.currentTimeMillis();
		LOGGER.info("Pullback cover computed in {} milliseconds", t1 - t0);
		return cover;
	}

	private Collection<Collection<S>> computeClustering(Collection<Collection<S>> pullbackCover) {
		long t2 = System.currentTimeMillis();
		Stream<Collection<S>> parallel = pullbackCover.stream().parallel();
		LOGGER.info("Using parallel computation: {}", parallel.isParallel());
		Collection<Collection<S>> clusters = parallel
			.map(this.clusterer::performClustering)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		long t3 = System.currentTimeMillis();
		LOGGER.info("Clustering computed in {} milliseconds", t3 - t2);
		return clusters;
	}

	private MapperGraph buildGraph(Collection<Collection<S>> clusters) {
		long t4 = System.currentTimeMillis();
		MapperGraph graph = new MapperGraph();
		Map<S, Collection<MapperVertex>> vertexMap = buildVertexMap(graph, clusters);
		addEdges(graph, vertexMap);
		long t5 = System.currentTimeMillis();
		LOGGER.info("Graph computed in {} milliseconds", t5 - t4);
		return graph;
	}

	public MapperGraph run(List<S> dataset) {
		Collection<Collection<S>> cover = buildCover(dataset);
		Collection<Collection<S>> clusters = computeClustering(cover);
		return buildGraph(clusters);
	}

    private Map<S, Collection<MapperVertex>> buildVertexMap(MapperGraph graph, Collection<Collection<S>> clusters) {
		Map<S, Collection<MapperVertex>> vertexMap = new HashMap<>();
		for (Collection<S> cluster : clusters) {
			if (!cluster.isEmpty()) {
				MapperVertex vertex = buildVertex(cluster);
				graph.addVertex(vertex);
				for (S data : cluster) {
					vertexMap.computeIfAbsent(data, x -> new LinkedList<>());
					vertexMap.get(data).add(vertex);
				}
			}
		}
		return vertexMap;
	}

	private void addEdges(MapperGraph graph, Map<S, Collection<MapperVertex>> vertexMap) {
		for (Entry<S, Collection<MapperVertex>> entry : vertexMap.entrySet()) {
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

    private MapperVertex buildVertex(Collection<S> dataset) {
        return new MapperVertex(new LinkedList<>()); // TODO: improve this
    }
	
	public static class Builder<S> {
		
		private CoverAlgorithm<S> coverAlgorithm;

		private ClusteringAlgorithm<S> clustering;

		public Builder<S> withCover(CoverAlgorithm<S> coveringAlgo) {
			this.coverAlgorithm = coveringAlgo;
			return this;
		}

		public Builder<S> withClustering(ClusteringAlgorithm<S> clustering) {
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

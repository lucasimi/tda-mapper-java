package org.lucasimi.tda.mapper.pipeline;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lucasimi.tda.mapper.clustering.ClusteringAlgorithm;
import org.lucasimi.tda.mapper.cover.CoverAlgorithm;
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
		LOGGER.info("Dataset covered in {}ms", t1 - t0);
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
		LOGGER.info("Clustering computed in {}ms", t3 - t2);
		return clusters;
	}

	public MapperGraph<S> run(List<S> dataset) {
		Collection<Collection<S>> cover = buildCover(dataset);
		Collection<Collection<S>> clusters = computeClustering(cover);
		return new MapperGraph<>(clusters);
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

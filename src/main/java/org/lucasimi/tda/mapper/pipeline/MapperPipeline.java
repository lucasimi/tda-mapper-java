package org.lucasimi.tda.mapper.pipeline;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.lucasimi.tda.mapper.clustering.ClusteringAlgorithm;
import org.lucasimi.tda.mapper.cover.CoverAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperPipeline<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperPipeline.class);

    private CoverAlgorithm<S> coverAlgorithm;

    private ClusteringAlgorithm<S> clusteringAlgorithm;

    private MapperPipeline(Builder<S> builder) {
        this.coverAlgorithm = builder.cover;
        this.clusteringAlgorithm = builder.clustering;
    }

    public MapperGraph<S> run(List<S> dataset) {
        long t0 = System.currentTimeMillis();
        Collection<Collection<S>> cover = this.coverAlgorithm.run(dataset);
        long t1 = System.currentTimeMillis();
        Collection<Collection<S>> clusters = cover.parallelStream()
                .map(this.clusteringAlgorithm::run)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        long t2 = System.currentTimeMillis();
        MapperGraph<S> graph = new MapperGraph<>(dataset, clusters);
        long t3 = System.currentTimeMillis();
        LOGGER.debug("### Mapper Pipeline build report");
        LOGGER.debug("* total build time:    \t{}ms", t3 - t0);
        LOGGER.debug("* cover build time:    \t{}ms", t1 - t0);
        LOGGER.debug("* clusters build time: \t{}ms", t2 - t1);
        LOGGER.debug("* graph build time:    \t{}ms", t3 - t2);
        return graph;
    }

    public static class Builder<S> {

        private CoverAlgorithm<S> cover;

        private ClusteringAlgorithm<S> clustering;

        public Builder<S> withCoverAlgorithm(CoverAlgorithm<S> cover) {
            this.cover = cover;
            return this;
        }

        public Builder<S> withClusteringAlgorithm(ClusteringAlgorithm<S> clustering) {
            this.clustering = clustering;
            return this;
        }

        public MapperPipeline<S> build() throws MapperException {
            if (this.clustering == null) {
                throw new MapperException.NoClusteringAlgorithm();
            } else if (this.cover == null) {
                throw new MapperException.NoCoverAlgorithm();
            } else {
                return new MapperPipeline<>(this);
            }
        }

    }

}

package org.lucasimi.tda.mapper.pipeline;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.lucasimi.tda.mapper.clustering.Clustering;
import org.lucasimi.tda.mapper.clustering.ClusteringUtils;
import org.lucasimi.tda.mapper.cover.Cover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperPipeline<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperPipeline.class);

    private Cover.Builder<S> coverBuilder;

    private Clustering.Builder<S> clusteringBuilder;

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    private MapperPipeline(Builder<S> builder) {
        this.coverBuilder = builder.coverBuilder;
        this.clusteringBuilder = builder.clusteringBuilder;
    }

    public MapperGraph run(List<S> dataset) {
        long t0 = System.currentTimeMillis();
        Collection<Collection<S>> cover;
        try {
            cover = this.coverBuilder
                    .build()
                    .run(dataset);
        } catch (MapperException e) {
            e.printStackTrace();
            return new MapperGraph();
        }
        long t1 = System.currentTimeMillis();
        Collection<Collection<S>> clusters = cover.parallelStream()
                .map(ds -> {
                    Clustering<S> clustering;
                    try {
                        clustering = this.clusteringBuilder.build();
                    } catch (MapperException e) {
                        e.printStackTrace();
                        clustering = ClusteringUtils.trivialClustering();
                    }
                    return clustering.run(ds);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        long t2 = System.currentTimeMillis();
        MapperGraph graph = new MapperGraph(dataset, clusters);
        long t3 = System.currentTimeMillis();
        LOGGER.debug("### Mapper Pipeline build report");
        LOGGER.debug("* total build time:    \t{}ms", t3 - t0);
        LOGGER.debug("* cover build time:    \t{}ms", t1 - t0);
        LOGGER.debug("* clusters build time: \t{}ms", t2 - t1);
        LOGGER.debug("* graph build time:    \t{}ms", t3 - t2);
        return graph;
    }

    public static class Builder<S> {

        private Cover.Builder<S> coverBuilder;

        private Clustering.Builder<S> clusteringBuilder;

        private Builder() {}

        public Builder<S> withCover(Cover.Builder<S> coverBuilder) {
            this.coverBuilder = coverBuilder;
            return this;
        }

        public Builder<S> withClustering(Clustering.Builder<S> clusteringBuilder) {
            this.clusteringBuilder = clusteringBuilder;
            return this;
        }

        public MapperPipeline<S> build() throws MapperException {
            if (this.clusteringBuilder == null) {
                throw new MapperException.NoClusteringAlgorithm();
            }
            if (this.coverBuilder == null) {
                throw new MapperException.NoCoverAlgorithm();
            }
            return new MapperPipeline<>(this);
        }

    }

}

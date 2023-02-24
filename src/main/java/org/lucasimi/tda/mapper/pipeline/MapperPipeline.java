package org.lucasimi.tda.mapper.pipeline;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.lucasimi.tda.mapper.clustering.Clustering;
import org.lucasimi.tda.mapper.clustering.TrivialClustering;
import org.lucasimi.tda.mapper.cover.Cover;
import org.lucasimi.tda.mapper.topology.Lens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperPipeline<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperPipeline.class);

    private Cover.Builder<S> coverBuilder;

    private Clustering.Builder<S> clusteringBuilder;

    public static <S, T> Builder<S, T> newBuilder() {
        return new Builder<>();
    }

    private <T> MapperPipeline(Builder<S, T> builder) {
        this.coverBuilder = builder.coverBuilder.pullback(builder.lens);
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
                        clustering = TrivialClustering.<S>newBuilder().build();
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

    public static class Builder<S, T> {

        private Cover.Builder<T> coverBuilder;

        private Clustering.Builder<S> clusteringBuilder;

        private Lens<S, T> lens;

        private Builder() {
        }

        public Builder<S, T> withCover(Cover.Builder<T> coverBuilder) {
            this.coverBuilder = coverBuilder;
            return this;
        }

        public Builder<S, T> withClustering(Clustering.Builder<S> clusteringBuilder) {
            this.clusteringBuilder = clusteringBuilder;
            return this;
        }

        public Builder<S, T> withLens(Lens<S, T> lens) {
            this.lens = lens;
            return this;
        }

        public MapperPipeline<S> build() throws MapperException {
            if (this.clusteringBuilder == null) {
                throw new MapperException.ClusteringException();
            }
            if (this.coverBuilder == null) {
                throw new MapperException.CoverException();
            }
            if (this.lens == null) {
                throw new MapperException.LensException();
            }
            return new MapperPipeline<>(this);
        }

    }

}

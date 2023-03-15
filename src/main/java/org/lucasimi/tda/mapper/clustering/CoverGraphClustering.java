package org.lucasimi.tda.mapper.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.lucasimi.tda.mapper.cover.Cover;
import org.lucasimi.tda.mapper.pipeline.MapperException.ClusteringException;
import org.lucasimi.tda.mapper.pipeline.MapperException.CoverException;
import org.lucasimi.tda.mapper.pipeline.MapperGraph;

public class CoverGraphClustering<S> implements Clustering<S> {

    private Cover<S> cover;

    private CoverGraphClustering(Cover<S> cover) {
        this.cover = cover;
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        List<S> dataList = new ArrayList<>(dataset);
        Collection<Collection<S>> clusters = this.cover.run(dataset);
        MapperGraph graph = new MapperGraph(new ArrayList<>(dataset), clusters);
        Map<Integer, Set<MapperGraph.Vertex>> comps = graph.getConnectedComponents();
        Collection<Collection<S>> ccs = new ArrayList<>(comps.size());
        for (Set<MapperGraph.Vertex> component : comps.values()) {
            Collection<S> cluster = new HashSet<>();
            for (MapperGraph.Vertex vertex : component) {
                Collection<S> points = vertex.getPoints().stream()
                        .map(dataList::get)
                        .collect(Collectors.toList());
                cluster.addAll(points);
            }
            ccs.add(cluster);
        }
        return ccs;
    }

    public static class Builder<S> implements Clustering.Builder<S> {

        private Cover.Builder<S> coverBuilder;

        private Builder() {}

        public Builder<S> withCover(Cover.Builder<S> coverBuilder) {
            this.coverBuilder = coverBuilder;
            return this;
        }

        @Override
        public Clustering<S> build() throws ClusteringException {
            if (this.coverBuilder == null) {
                throw new ClusteringException();
            }
            try {
                Cover<S> cover = this.coverBuilder.build();
                return new CoverGraphClustering<>(cover);
            } catch (CoverException e) {
                e.printStackTrace();
                throw new ClusteringException();
            }
        }

    }

}

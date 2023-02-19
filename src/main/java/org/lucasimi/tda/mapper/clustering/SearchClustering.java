package org.lucasimi.tda.mapper.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.lucasimi.tda.mapper.cover.Cover;
import org.lucasimi.tda.mapper.cover.SearchCover;
import org.lucasimi.tda.mapper.pipeline.MapperException.NoClusteringAlgorithm;
import org.lucasimi.tda.mapper.pipeline.MapperException.NoCoverAlgorithm;
import org.lucasimi.tda.mapper.pipeline.MapperGraph;
import org.lucasimi.tda.mapper.search.Search;

public class SearchClustering<S> implements Clustering<S> {

    private Cover<S> searchCover;

    private SearchClustering(Cover<S> searchCover) {
        this.searchCover = searchCover;
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        List<S> dataList = new ArrayList<>(dataset);
        Collection<Collection<S>> clusters = this.searchCover.run(dataset);
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

        private Search.Builder<S> searchBuilder;


        private Builder() {}

        public Builder<S> withSearch(Search.Builder<S> searchBuilder) {
            this.searchBuilder = searchBuilder;
            return this;
        }

        @Override
        public Clustering<S> build() throws NoClusteringAlgorithm {
            if (this.searchBuilder == null) {
                throw new NoClusteringAlgorithm();
            }
            Cover<S> searchCover;
            try {
                searchCover = SearchCover.<S>newBuilder()
                        .withSearch(this.searchBuilder)
                        .build();
            } catch (NoCoverAlgorithm e) {
                e.printStackTrace();
                throw new NoClusteringAlgorithm();
            }
            if (searchCover == null) {
                throw new NoClusteringAlgorithm();
            }
            return new SearchClustering<>(searchCover);
        }

    }

}

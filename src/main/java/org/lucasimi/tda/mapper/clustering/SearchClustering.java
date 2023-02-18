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
import org.lucasimi.tda.mapper.pipeline.MapperGraph;
import org.lucasimi.tda.mapper.search.Search;

public class SearchClustering<S> implements Clustering<S> {

    private Search<S> search;

    private SearchClustering(Builder<S> builder) {
        this.search = builder.search;
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        List<S> dataList = new ArrayList<>(dataset);
        Cover<S> searchCover = SearchCover.<S>newBuilder()
                .withSearch(search)
                .build();
        Collection<Collection<S>> clusters = searchCover.run(dataset);
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

        private Search<S> search;

        public Builder<S> withSearch(Search<S> search) {
            this.search = search;
            return this;
        }

        public Builder<S> withSearch(Search.Builder<S> searchBuilder) {
            this.searchBuilder = searchBuilder;
            return this;
        }

        @Override
        public Clustering<S> build() {
            if (this.search == null) {
                if (this.searchBuilder == null) {
                    return null;
                }
                this.search = this.searchBuilder.build();
            }
            return new SearchClustering<>(this);
        }

    }

}

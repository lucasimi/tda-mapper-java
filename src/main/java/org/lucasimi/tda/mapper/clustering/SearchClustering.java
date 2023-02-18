package org.lucasimi.tda.mapper.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.lucasimi.tda.mapper.cover.CoverAlgorithm;
import org.lucasimi.tda.mapper.cover.SearchCover;
import org.lucasimi.tda.mapper.pipeline.MapperGraph;
import org.lucasimi.tda.mapper.search.SearchAlgorithm;

public class SearchClustering<S> implements ClusteringAlgorithm<S> {

    private SearchAlgorithm<S> searchAlgorithm;

    private SearchClustering(SearchAlgorithm<S> searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        List<S> dataList = new ArrayList<>(dataset);
        CoverAlgorithm<S> searchCover = SearchCover.<S>newBuilder()
                .withSearchAlgorithm(searchAlgorithm)
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

    public static class Builder<S> implements ClusteringAlgorithm.Builder<S> {

        private SearchAlgorithm.Builder<S> searchAlgorithmBuilder;

        private SearchAlgorithm<S> searchAlgorithm;

        public Builder<S> withSearchAlgorithm(SearchAlgorithm<S> searchAlgorithm) {
            this.searchAlgorithm = searchAlgorithm;
            return this;
        }

        public Builder<S> withSearchAlgorithm(SearchAlgorithm.Builder<S> searchAlgorithmBuilder) {
            this.searchAlgorithmBuilder = searchAlgorithmBuilder;
            return this;
        }

        @Override
        public ClusteringAlgorithm<S> build() {
            if (this.searchAlgorithm != null) {
                return new SearchClustering<>(this.searchAlgorithm);
            } else {
                return new SearchClustering<>(this.searchAlgorithmBuilder.build());
            }
        }

    }

}

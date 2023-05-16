package io.github.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.github.lucasimi.tda.mapper.search.Search;
import io.github.lucasimi.tda.mapper.topology.Lens;

public class SearchCover<S> implements Cover<S> {

    private final Search<S> search;

    private SearchCover(Search<S> search) {
        this.search = search;
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        Collection<S> centers = this.search.fit(dataset);
        Collection<Collection<S>> clusters = new ArrayList<>(dataset.size());
        Set<S> coverSet = new HashSet<>(dataset.size());
        for (S point : centers) {
            if (!coverSet.contains(point)) {
                Collection<S> neighbors = this.search.getNeighbors(point);
                for (S neighbor : neighbors) {
                    coverSet.add(neighbor);
                }
                if (!neighbors.isEmpty()) {
                    clusters.add(neighbors);
                }
            }
        }
        return clusters;
    }

    public static class Builder<S> implements Cover.Builder<S> {

        private Search.Builder<S> searchBuilder;

        private Builder() {}

        public Builder<S> withSearch(Search.Builder<S> searchBuilder) {
            this.searchBuilder = searchBuilder;
            return this;
        }

        @Override
        public Cover<S> build() {
            Search<S> search = this.searchBuilder.build();
            return new SearchCover<>(search);
        }

        @Override
        public <R> Builder<R> pullback(Lens<R, S> lens) {
            return new Builder<R>()
                .withSearch(this.searchBuilder.pullback(lens));
        }

    }

}

package org.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.lucasimi.tda.mapper.search.Search;

public class SearchCover<S> implements Cover<S> {

    private Search<S> search;

    private SearchCover(Builder<S> builder) {
        this.search = builder.search;
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

        private Search<S> search;

        private Builder() {}

        public Builder<S> withSearch(Search<S> search) {
            this.search = search;
            return this;
        }

        public Builder<S> withSearch(Search.Builder<S> searchBuilder) {
            this.searchBuilder = searchBuilder;
            return this;
        }

        @Override
        public Cover<S> build() {
            if (this.search == null) {
                if (this.searchBuilder == null) {
                    return null;
                }
                this.search = this.searchBuilder.build();
            }
            return new SearchCover<>(this);
        }

    }

}

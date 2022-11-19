package org.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SearchCover<S> implements CoverAlgorithm<S> {

    private SearchAlgorithm<S> searchAlgorithm;

    public <T> SearchCover(SearchAlgorithm<S> searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        Collection<S> centers = this.searchAlgorithm.fit(dataset);
        Collection<Collection<S>> clusters = new ArrayList<>(dataset.size());
        HashMap<S, Boolean> coverMap = new HashMap<>(dataset.size());
        for (S point : dataset) {
            coverMap.put(point, false);
        }
        for (S point : centers) {
            if (!coverMap.get(point)) {
                Collection<S> neighbors = this.searchAlgorithm.getNeighbors(point);
                for (S neighbor : neighbors) {
                    coverMap.put(neighbor, true);
                }
                if (!neighbors.isEmpty()) {
                    clusters.add(neighbors);
                }
            }
        }
        return clusters;
    }

}

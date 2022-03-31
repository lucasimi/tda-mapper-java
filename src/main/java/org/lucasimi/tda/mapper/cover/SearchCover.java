package org.lucasimi.tda.mapper.cover;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchCover<S> implements CoverAlgorithm<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCover.class);

	private SearchAlgorithm<S> searchAlgorithm;
	
	public <T> SearchCover(SearchAlgorithm<S> searchAlgorithm) {
		this.searchAlgorithm = searchAlgorithm;
	}

	private Optional<S> pickElement(Collection<S> collection) {
		Optional<S> toReturn = Optional.empty();
		for (S element : collection) {
			if (toReturn.isEmpty()) {
				toReturn = Optional.of(element);
			} else {
				break;
			}
		}
		return toReturn; 
	}

	@Override
	public Collection<Collection<S>> groups(Collection<S> dataset) {
		this.searchAlgorithm.setup(dataset);
		Set<S> nonCoveredPoints = new HashSet<>(dataset);
		Collection<Collection<S>> groups = new LinkedList<>();
		while (!nonCoveredPoints.isEmpty()) {
			Optional<S> point = pickElement(nonCoveredPoints);
			point.ifPresent(p -> {
				Collection<S> neighbors = this.searchAlgorithm.getNeighbors(point.get());
				nonCoveredPoints.removeAll(neighbors);
				if (!neighbors.isEmpty()) {
					groups.add(neighbors);
				}
			});
		}
		return groups;
	}

}
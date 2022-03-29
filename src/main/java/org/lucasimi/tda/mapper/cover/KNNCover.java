package org.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.lucasimi.tda.mapper.vptree.VPTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KNNCover<S> implements CoverAlgorithm<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KNNCover.class);

    private int k;
    
    private Metric<S> metric;
	
	public <T> KNNCover(Lens<S, T> lens, Metric<T> metric, int k) {
		this(TopologyUtils.pullback(lens, metric), k);
	}

    public KNNCover(Metric<S> metric, int k) {
        this.k = k;
        this.metric = metric;
    }
    
	private Optional<S> pickElement(Set<S> collection) {
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
		VPTree<S> vpTree = new VPTree<>(this.metric, dataset, this.k);
		Set<S> nonCoveredPoints = new HashSet<>(dataset);
		Collection<Collection<S>> groups = new LinkedList<>();
		int missingPointsCount = 0;
		while (!nonCoveredPoints.isEmpty()) {
			Optional<S> point = pickElement(nonCoveredPoints);
			if (!point.isEmpty()) {
				Collection<S> neighbors = vpTree.knnSearch(point.get(), this.k);
				nonCoveredPoints.removeAll(neighbors);
				if (!neighbors.contains(point.get())) {
					missingPointsCount += 1;
				}
				if (neighbors.size() > this.k) {
					LOGGER.warn("Found {} neigbors, exceeding max size of {}", neighbors.size(), this.k);
				}
				nonCoveredPoints.remove(point.get());
				if (!neighbors.isEmpty()) {
					groups.add(new ArrayList<>(neighbors));
				}
			}
		}
		if (missingPointsCount > 0) {
			LOGGER.warn("Missing {} points", missingPointsCount);
		}
		return groups;
	}

}

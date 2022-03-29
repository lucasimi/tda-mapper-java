package org.lucasimi.tda.mapper.cover;

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

public class BallCover<S> implements CoverAlgorithm<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BallCover.class);

	private Metric<S> metric;

	private double radius;
	
	public <T> BallCover(Lens<S, T> lens, Metric<T> metric, double radius) {
		this(TopologyUtils.pullback(lens, metric), radius);
	}

	public BallCover(Metric<S> metric, double radius) {
		this.metric = metric;
		this.radius = radius;
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
		// TODO: this leaf size looks quite arbitrary...
		VPTree<S> vpTree = new VPTree<>(this.metric, dataset, 1000, radius);
		Set<S> nonCoveredPoints = new HashSet<>(dataset);
		Collection<Collection<S>> groups = new LinkedList<>();
		int missingPointsCount = 0;
		while (!nonCoveredPoints.isEmpty()) {
			Optional<S> point = pickElement(nonCoveredPoints);
			if (!point.isEmpty()) {
				Collection<S> neighbors = vpTree.ballSearch(point.get(), radius);
				nonCoveredPoints.removeAll(neighbors);
				if (!neighbors.contains(point.get())) {
					missingPointsCount += 1;
				}
				nonCoveredPoints.remove(point.get());
				if (!neighbors.isEmpty()) {
					groups.add(neighbors);
				}
			}
		}
		if (missingPointsCount > 0) {
			LOGGER.warn("Missing {} points", missingPointsCount);
		}
		return groups;
	}

}
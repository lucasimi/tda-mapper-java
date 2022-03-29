package org.lucasimi.tda.mapper.vptree;

import java.util.Collection;
import java.util.Collections;

class VPNodeSplit<T> implements VPNode<T> {

	T center;
	
    double radius;
	
	public VPNodeSplit(T center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	@Override
	public double getRadius() {
		return this.radius;
	}

	@Override
	public T getCenter() {
		return this.center;
	}

	@Override
	public Collection<T> getPoints() {
		return Collections.emptyList();
	}
	
}
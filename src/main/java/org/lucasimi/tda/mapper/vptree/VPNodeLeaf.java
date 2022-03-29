package org.lucasimi.tda.mapper.vptree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class VPNodeLeaf<T> implements VPNode<T> {

    List<T> points;
	
	public VPNodeLeaf(List<T> points, int start, int end) {
		this.points = new LinkedList<>();
		for (int i = start; i < end; i++) {
			this.points.add(points.get(i));
		}
	}

	@Override
	public double getRadius() {
		throw new UnsupportedOperationException();
	}

	@Override
	public T getCenter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<T> getPoints() {
		return this.points;
	}
	
}
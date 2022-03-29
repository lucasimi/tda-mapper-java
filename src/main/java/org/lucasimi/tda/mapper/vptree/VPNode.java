package org.lucasimi.tda.mapper.vptree;

import java.util.Collection;

interface VPNode<T> {

	public double getRadius();

	public T getCenter();

	public Collection<T> getPoints();

}
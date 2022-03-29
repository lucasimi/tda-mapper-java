package org.lucasimi.tda.mapper.vptree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.utils.BinaryTree;

class BallSearch<T> {

	private final T center;

	private final double eps;

	private final List<T> points;

    private final Metric<T> metric;

	public BallSearch(Metric<T> metric, T center, double eps, BinaryTree<VPNode<T>> tree) {
		this.center = center;
		this.eps = eps;
        this.metric = metric;
		this.points = new LinkedList<>();
		this.search(tree);
	}

	private void addAll(Collection<T> points) {
		for (T point : points) {
			if (this.metric.evaluate(this.center, point) < this.eps) {
				this.points.add(point);	
			}
		}
	}

	public List<T> getPoints() {
		return this.points;
	}

	private void search(BinaryTree<VPNode<T>> tree) {
		if (tree.isTerminal()) {
			this.addAll(tree.getData().getPoints());
		} else {
			T center = tree.getData().getCenter();
			double radius = tree.getData().getRadius();
			double dist = this.metric.evaluate(this.center, center);
			if (dist <= radius + this.eps) {
				this.search(tree.getLeft());
			} 
			if (dist > radius - this.eps) {
				this.search(tree.getRight());
			}
		}	
	}

}
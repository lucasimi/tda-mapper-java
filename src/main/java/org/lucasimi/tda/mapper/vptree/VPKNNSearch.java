package org.lucasimi.tda.mapper.vptree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.utils.BinaryTree;
import org.lucasimi.tda.mapper.utils.MaxHeap;

class VPKNNSearch<T> {

	private final T center;

	private final int neighbors;

    private final Metric<T> metric;

	private final MaxHeap<HeapNode> points;

	private class HeapNode {

		float dist;

		T point;

		public HeapNode(T point) {
			this.point = point;
			this.dist = metric.evaluate(point, center);
		}

	}

	public VPKNNSearch(Metric<T> metric, T center, int neighbors, BinaryTree<VPNode<T>> tree) {
		this.center = center;
		this.neighbors = neighbors;
		this.metric = metric;
		Comparator<HeapNode> distComparator = (arg0, arg1) -> Float.compare(arg0.dist, arg1.dist);
		this.points = new MaxHeap<>(distComparator, neighbors);
		this.search(tree);
	}

	public void addAll(Collection<T> data) {
		data.stream()
			.map(HeapNode::new)
			.forEach(b -> this.points.add(b, this.neighbors));
	}

	public double getRadius() {
		if (this.points.size() < this.neighbors) {
			return Float.POSITIVE_INFINITY;
		} else {
			return this.points.getMax().dist;
		}
	}

	public Set<T> extract() {
		Set<T> collected = new HashSet<>();
		while (!this.points.isEmpty()) {
			this.points.extractMax().map(b -> b.point).ifPresent(collected::add);
		}
		return collected;
	}

	private void search(BinaryTree<VPNode<T>> tree) {
		if (tree.isTerminal()) {
			this.addAll(tree.getData().getPoints());
		} else {
			T center = tree.getData().getCenter();
			double radius = tree.getData().getRadius();
			double dist = this.metric.evaluate(this.center, center);
			double eps = this.getRadius();
			if (dist <= radius + eps) {
				this.search(tree.getLeft());
				eps = this.getRadius();
			} 
			if (dist > radius - eps) {
				this.search(tree.getRight());
			}	
		}
	}
	
}
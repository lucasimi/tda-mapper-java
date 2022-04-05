package org.lucasimi.tda.mapper.vptree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.topology.TopologyUtils;
import org.lucasimi.tda.mapper.utils.BinaryTree;
import org.lucasimi.tda.mapper.utils.MaxHeap;

class VPKNNSearch<T> {

	T center;

	private int k;

	private MaxHeap<T> points;

    private Metric<T> metric;

	public VPKNNSearch(Metric<T> metric, T center, int k, BinaryTree<VPNode<T>> tree) {
		this.metric = metric;
		this.center = center;
		this.k = k;
		Lens<T, Float> distFrom = TopologyUtils.distFrom(this.metric, center);
		Comparator<T> distFromComparator = (arg0, arg1) -> {
			Float val0 = distFrom.evaluate(arg0);
			Float val1 = distFrom.evaluate(arg1);
			return Float.compare(val0, val1);
		};
		this.points = new MaxHeap<>(distFromComparator);
		search(tree);
	}

	public void addAll(Collection<T> data) {
		this.points.addAll(data, this.k);
	}

	public double getRadius() {
		if (this.points.size() < this.k) {
			return Float.POSITIVE_INFINITY;
		} else {
			T furthest = this.points.getMax();
			return this.metric.evaluate(this.center, furthest);
		}
	}

	public Set<T> extract() {
		Set<T> collected = new HashSet<>();
		while (!this.points.isEmpty()) {
			this.points.extractMax().ifPresent(collected::add);
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
			if (dist < radius) {
				knnSearchInside(tree);
			} else {
				knnSearchOutside(tree);
			}	
		}
	}

	private void knnSearchInside(BinaryTree<VPNode<T>> tree) {
		T center = tree.getData().getCenter();
		double radius = tree.getData().getRadius();
		search(tree.getLeft());
		double firstEstimation = this.getRadius();
		double dist = this.metric.evaluate(this.center, center); 
		if (dist + firstEstimation > radius) {
			search(tree.getRight());
		}
	}
	
	private void knnSearchOutside(BinaryTree<VPNode<T>> tree) {
		T center = tree.getData().getCenter();
		double radius = tree.getData().getRadius();
		search(tree.getRight());
		double firstEstimation = this.getRadius();
		double dist = this.metric.evaluate(this.center, center); 
		if (dist < radius + firstEstimation) {
			search(tree.getLeft());
		}
	}
	
}
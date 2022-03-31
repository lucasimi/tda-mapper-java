package org.lucasimi.tda.mapper.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.utils.BinaryTree;
import org.lucasimi.tda.mapper.utils.Distance;
import org.lucasimi.tda.mapper.utils.Pivoter;

public class VPTree<T> {
    
	Metric<T> metric;

	private Distance<T> distance;

	private BinaryTree<VPNode<T>> tree;

	private int leafSize;

	private double leafRadius;

	private List<T> dataset;

    public VPTree(Metric<T> metric, Collection<T> data, int leafSize) {
		this.metric = metric;
		this.distance = new Distance<>(metric);
        this.dataset = new ArrayList<>(data);
		this.leafSize = leafSize;
		this.tree = buildWithoutLeafRadius(0, this.dataset.size());
    }

    public VPTree(Metric<T> metric, Collection<T> data, int leafSize, Double leafRadius) {
		this.metric = metric;
		this.distance = new Distance<>(metric);
        this.dataset = new ArrayList<>(data);
		this.leafRadius = leafRadius;
		this.leafSize = leafSize;
		this.tree = buildWithLeafRadius(0, this.dataset.size());
    }

	private BinaryTree<VPNode<T>> buildWithoutLeafRadius(int start, int end) {
		if (end - start <= this.leafSize) {
			return new BinaryTree<>(new VPNodeLeaf<>(this.dataset, start, end));
		} else {
			T center = this.dataset.get(start);
			int mid = (start + end) / 2;
			this.distance.setCenter(center);
			Pivoter.quickSelect(this.distance, this.dataset, start, end, mid);
			double radius = this.metric.evaluate(center, this.dataset.get(mid));
			BinaryTree<VPNode<T>> leftTree = buildWithoutLeafRadius(start, mid);
			BinaryTree<VPNode<T>> rightTree = buildWithoutLeafRadius(mid, end);
			VPNode<T> container = new VPNodeSplit<>(center, radius);
			return new BinaryTree<>(container, leftTree, rightTree);
		}
	}

	private BinaryTree<VPNode<T>> buildWithLeafRadius(int start, int end) {
		if (end - start <= this.leafSize) {
			return new BinaryTree<>(new VPNodeLeaf<>(this.dataset, start, end));
		} else {
			T center = this.dataset.get(start);
			int mid = (start + end) / 2;
			this.distance.setCenter(center);
			Pivoter.quickSelect(this.distance, this.dataset, start, end, mid);
			double radius = this.metric.evaluate(center, this.dataset.get(mid));
			BinaryTree<VPNode<T>> leftTree;
			BinaryTree<VPNode<T>> rightTree;
			if (radius < this.leafRadius) {
				leftTree = new BinaryTree<>();
				VPNode<T> leftBall = new VPNodeLeaf<>(this.dataset, start, mid);
				leftTree.setData(leftBall);
				rightTree = new BinaryTree<>();
				VPNode<T> rightBall = new VPNodeLeaf<>(this.dataset, mid, end);
				rightTree.setData(rightBall);
			} else {
				leftTree = buildWithLeafRadius(start, mid);
				rightTree = buildWithLeafRadius(mid, end);
			}
			VPNode<T> container = new VPNodeSplit<>(center, radius);
			return new BinaryTree<>(container, leftTree, rightTree);
		}
	}

	public List<T> ballSearch(T testPoint, double eps) {
		VPBallSearch<T> search = new VPBallSearch<T>(this.metric, testPoint, eps, this.tree);
		return search.getPoints();
	}

	public Set<T> knnSearch(T testPoint, int k) {
		VPKNNSearch<T> search = new VPKNNSearch<T>(this.metric, testPoint, k, this.tree);
		return search.extract();
	}

}

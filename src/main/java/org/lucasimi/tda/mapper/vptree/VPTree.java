package org.lucasimi.tda.mapper.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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

	private Collection<T> centers;

	private static final Random rand = new Random();

	public Collection<T> getCenters() {
		return this.centers;
	}

    public VPTree(Metric<T> metric, Collection<T> data, int leafSize) {
		this.metric = metric;
		this.distance = new Distance<>(metric);
        this.dataset = new ArrayList<>(data);
		this.leafSize = leafSize;
		this.centers = new ArrayList<>(data.size());
		this.tree = buildWithoutLeafRadius(0, this.dataset.size());
    }

    public VPTree(Metric<T> metric, Collection<T> data, int leafSize, Double leafRadius) {
		this.metric = metric;
		this.distance = new Distance<>(metric);
        this.dataset = new ArrayList<>(data);
		this.leafRadius = leafRadius;
		this.leafSize = leafSize;
		this.centers = new ArrayList<>(data.size());
		this.tree = buildWithLeafRadius(0, this.dataset.size());
    }

	private BinaryTree<VPNode<T>> buildWithoutLeafRadius(int start, int end) {
		int mid = (start + end) / 2;
		T vp = pickVantagePoint(start, end);
		double radius = performPivoting(vp, start, end, mid);
		BinaryTree<VPNode<T>> leftTree;
		BinaryTree<VPNode<T>> rightTree;
		if (end - start <= 2 * this.leafSize) {
			leftTree = new BinaryTree<>(new VPNodeLeaf<>(this.dataset, start, mid));
			this.centers.add(vp);
			rightTree = new BinaryTree<>(new VPNodeLeaf<>(this.dataset, mid, end));
			for (int i = mid; i < end; i++) {
				this.centers.add(this.dataset.get(i));
			}
		} else {
			leftTree = buildWithoutLeafRadius(start, mid);
			rightTree = buildWithoutLeafRadius(mid, end);
		}
		return new BinaryTree<>(new VPNodeSplit<T>(vp, radius), leftTree, rightTree);
	}

	private T pickVantagePoint(int start, int end) {
		int idx = start + rand.nextInt(end - start);
		return this.dataset.get(idx);
	}

	private double performPivoting(T vp, int start, int end, int k) {
		this.distance.setCenter(vp);
		Pivoter.quickSelect(this.distance, this.dataset, start, end, k);
		return this.metric.evaluate(vp, this.dataset.get(k));
	}

	private BinaryTree<VPNode<T>> buildWithLeafRadius(int start, int end) {
		int mid = (start + end) / 2;
		T vp = pickVantagePoint(start, end);
		double radius = performPivoting(vp, start, end, mid);
		BinaryTree<VPNode<T>> leftTree;
		BinaryTree<VPNode<T>> rightTree;
		if (radius < this.leafRadius || end - start <= 2 * this.leafSize) {
			leftTree = new BinaryTree<>(new VPNodeLeaf<>(this.dataset, start, mid));
			this.centers.add(vp);
			rightTree = new BinaryTree<>(new VPNodeLeaf<>(this.dataset, mid, end));
			for (int i = mid; i < end; i++) {
				this.centers.add(this.dataset.get(i));
			}
		} else {
			leftTree = buildWithLeafRadius(start, mid);
			rightTree = buildWithLeafRadius(mid, end);
		}
		VPNode<T> container = new VPNodeSplit<>(vp, radius);
		return new BinaryTree<>(container, leftTree, rightTree);
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

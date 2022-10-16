package org.lucasimi.tda.mapper.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.lucasimi.tda.mapper.topology.Metric;
import org.lucasimi.tda.mapper.utils.BinaryTree;
import org.lucasimi.tda.mapper.utils.Pivoter;

public class VPTreeImp<T> {

    private Metric<T> metric;

    private BinaryTree<VPNode<T>> tree;

    private int leafSize;

    private double leafRadius;

    private List<WithOrd> dataset;

    private Collection<T> centers;

    private class WithOrd implements Comparable<WithOrd> {

        private T data;

        private Float ord;

        public WithOrd(Float ord, T data) {
            this.ord = ord;
            this.data = data;
        }

        @Override
        public int compareTo(WithOrd withOrd) {
            return this.ord.compareTo(withOrd.ord);
        }

    }

    public Collection<T> getCenters() {
        return this.centers;
    }

    public VPTreeImp(Metric<T> metric, Collection<T> data, int leafSize, double leafRadius) {
        this.metric = metric;
        this.dataset = new ArrayList<>(data.size());
        for (T x : data) {
            this.dataset.add(new WithOrd(0.0f, x));
        }
        this.leafRadius = leafRadius;
        this.leafSize = leafSize;
        this.centers = new ArrayList<>(data.size());
        this.tree = buildUpdate(0, this.dataset.size());
    }

    private void updateDist(T center, int start, int end) {
        for (int j = start; j < end; j++) {
            WithOrd wo = this.dataset.get(j);
            wo.ord = this.metric.evaluate(center, wo.data);
        }
    }

    private BinaryTree<VPNode<T>> buildLeaf(int start, int end) {
        List<T> points = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            points.add(this.dataset.get(i).data);
        }
        return new BinaryTree<>(new VPNodeLeaf<>(points));
    }

    private BinaryTree<VPNode<T>> buildUpdate(int start, int end) {
        if (end - start <= this.leafSize) {
            for (int i = start; i < end; i++) {
                this.centers.add(this.dataset.get(i).data);
            }
            return buildLeaf(start, end);
        } else {
            int mid = (start + end) / 2;
            WithOrd center = this.dataset.get(start);
            updateDist(center.data, start + 1, end);
            Pivoter.quickSelect(this.dataset, start + 1, end, mid);
            WithOrd furthest = this.dataset.get(mid);
            float radius = furthest.ord;
            BinaryTree<VPNode<T>> leftTree;
            BinaryTree<VPNode<T>> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.data);
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdate(start, mid);
            }
            rightTree = buildUpdate(mid, end);
            VPNode<T> container = new VPNodeSplit<>(center.data, radius);
            return new BinaryTree<>(container, leftTree, rightTree);
        }
    }

    private BinaryTree<VPNode<T>> buildNoUpdate(int start, int end) {
        if (end - start <= this.leafSize) {
            for (int i = start; i < end; i++) {
                this.centers.add(this.dataset.get(i).data);
            }
            return buildLeaf(start, end);
        } else {
            int mid = (start + end) / 2;
            WithOrd center = this.dataset.get(start);
            Pivoter.quickSelect(this.dataset, start + 1, end, mid);
            WithOrd furthest = this.dataset.get(mid);
            float radius = furthest.ord;
            BinaryTree<VPNode<T>> leftTree;
            BinaryTree<VPNode<T>> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.data);
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdate(start, mid);
            }
            rightTree = buildUpdate(mid, end);
            VPNode<T> container = new VPNodeSplit<>(center.data, radius);
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

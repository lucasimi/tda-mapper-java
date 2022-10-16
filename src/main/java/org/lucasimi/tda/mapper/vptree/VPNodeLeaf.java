package org.lucasimi.tda.mapper.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class VPNodeLeaf<T> implements VPNode<T> {

    private List<T> points;

    public VPNodeLeaf(List<T> points) {
        this.points = points;
    }

    public VPNodeLeaf(List<T> points, int start, int end) {
        this.points = new ArrayList<>(end - start);
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

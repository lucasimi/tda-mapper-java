package org.lucasimi.tda.mapper.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MaxHeap<T> {
    
    private List<T> array;

    private Comparator<T> comparator;

    public MaxHeap(Comparator<T> comparator, int capacity) {
        this.comparator = comparator;
        //this.array = new ArrayList<>(capacity + 1);
        this.array = new LinkedList<>();
    }

    public T getMax() {
        return array.get(0);
    }

    public Optional<T> extractMax() {
        if (this.array.isEmpty()) {
            return Optional.empty();
        } else {
            T max = this.array.get(0);
            int lastIndex = this.array.size() - 1;
            this.array.set(0, this.array.get(lastIndex));
            this.array.remove(lastIndex);
            this.heapify(0);
            return Optional.of(max);
        }
    }

    public boolean add(T value) {
        this.array.add(value);
        int nodeIndex = this.array.size() - 1;
        int parentIndex = getParent(nodeIndex);
        T nodeValue = this.array.get(nodeIndex);
        T parentValue = this.array.get(parentIndex);
        while (nodeIndex > 0 && this.comparator.compare(nodeValue, parentValue) > 0) {
            this.array.set(nodeIndex, parentValue);
            this.array.set(parentIndex, nodeValue);
            nodeIndex = parentIndex;
            parentIndex = getParent(nodeIndex);
            parentValue = this.array.get(parentIndex);
        }
        return true;
    }

    public void add(T value, int maxSize) {
        this.add(value);
        while (this.size() > maxSize) {
            this.extractMax();
        }
    }

    public void addAll(Collection<T> values) {
        for (T value : values) {
            this.add(value);
        }
    }

    public void addAll(Collection<T> values, int maxSize) {
        for (T value : values) {
            this.add(value);
            while (this.size() > maxSize) {
                this.extractMax();
            }
        }
    }

    private int getLeft(int i) {
        return 2 * i + 1;
    }

    private int getRight(int i) {
        return 2 * i + 2;
    }

    private int getParent(int i) {
        return (i - 1) / 2;
    }

    private void heapify(int i) {
        int left = getLeft(i);
        int right = getRight(i);
        if (left >= this.array.size()) {
            return;
        }
        int maxChild = left;
        if (right < this.array.size()) {
            T leftValue = this.array.get(left);
            T rightValue = this.array.get(right);
            if (this.comparator.compare(leftValue, rightValue) < 0) {
                maxChild = right;
            }
        }
        T value = this.array.get(i);
        T minValue = this.array.get(maxChild);
        if (this.comparator.compare(value, minValue) < 0) {
            this.array.set(i, minValue);
            this.array.set(maxChild, value);
            this.heapify(maxChild);
        }
    }

    public int size() {
        return this.array.size();
    }

    public boolean isEmpty() {
        return this.array.isEmpty();
    }

}

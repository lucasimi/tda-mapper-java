package org.lucasimi.tda.mapper.utils;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaxHeapTest {
    
    @Test
    public void testMax() {
        MaxHeap<Integer> heap = new MaxHeap<>((x, y) -> Integer.compare(x, y), 1);
        Collection<Integer> points = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            points.add(i);
        }
        Assertions.assertTrue(heap.isEmpty());
        heap.addAll(points, 10);
        Assertions.assertEquals(10, heap.size());
        for (int i = 9; i > -1; i--) {
            Integer max = heap.extractMax().orElse(null);
            Assertions.assertEquals(Integer.valueOf(i), max);
            Assertions.assertEquals(i, heap.size());
        }
        Assertions.assertTrue(heap.isEmpty());
    }

    @Test
    public void testIdentical() {
        MaxHeap<Integer> heap = new MaxHeap<>((x, y) -> Integer.compare(x, y), 1);
        Collection<Integer> points = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            points.add(10);
        }
        Assertions.assertTrue(heap.isEmpty());
        heap.addAll(points);
        Assertions.assertEquals(20, heap.size());
    }

}

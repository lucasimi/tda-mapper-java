package org.lucasimi.tda.mapper.utils;

import java.util.List;

import org.lucasimi.tda.mapper.topology.Lens;

public class Pivoter {

	private Pivoter() {}
	
	private static final <T> void swap(List<T> data, int i, int j) {
		T xi = data.get(i);
		T xj = data.get(j);
		data.set(i, xj);
		data.set(j, xi);
	}
	
	/* Iteratively apply pivotAt until the pivoting element is in the right position,
	** according to the order given by lens. */
	public static final <T> void quickSelect(Lens<T, Float> lens, List<T> data, int start, int end, int k) {
		int startIndex = start;
		int endIndex = end;
		Integer higher = null;
		while ((higher == null) || (higher != k)) {
			higher = pivotAt(lens, data, startIndex, endIndex, k);
			if (k < higher) {
				endIndex = higher;
			} else {
				startIndex = higher;
			}
		}
	}
	
	/* Swap on the rightmost part, those elements of data having lens(data[j]) > lens(data[i]).
	** As a consequence x[i] will take its correct place according to the total order given by lens.
	** Returns the new index of x[i] */ 
	private static final <T> int pivotAt(Lens<T, Float> lens, List<T> data, int start, int end, int i) {
		swap(data, start, i);
		T startElem = data.get(start);
		float startValue = lens.evaluate(startElem);
		int higher = start + 1;
		for (int j = start + 1; j < end; j++) {
			if (lens.evaluate(data.get(j)) <= startValue) {
				swap(data, higher, j);
				higher += 1;
			}
		}
		swap(data, start, higher - 1);
		return higher - 1;
	}

}

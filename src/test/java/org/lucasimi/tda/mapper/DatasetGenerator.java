package org.lucasimi.tda.mapper;

import java.util.ArrayList;
import java.util.Random;

public class DatasetGenerator {
	
	private DatasetGenerator() {}

	public static ArrayList<float[]> linearDataset(int n, int dim) {
		ArrayList<float[]> dataset = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			float coords[] = new float[dim];
			for (int j = 0; j < dim; j++) {
				coords[j] = Float.valueOf(i);
			}
			float[] point = coords;
			dataset.add(point);
		}	
		return dataset;
	}

	public static ArrayList<float[]> randomDataset(int size, int dimension, float minValue, float maxBound) {
		ArrayList<float[]> array = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			float[] values = new float[dimension];
			for (int j = 0; j < dimension; j++) {
				values[j] = minValue + (float) Math.random() * (maxBound - minValue);
			}
			array.add(values);
		}
		return array;
	}
	
	public static ArrayList<float[]> randomDataset(int size, int dimension, float[] center, float side) {
		Random random = new Random();
		ArrayList<float[]> array = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			float[] values = new float[dimension];
			for (int j = 0; j < dimension; j++) {
				values[j] = center[j] + (float) random.doubles(-side, side).findFirst().getAsDouble();
			}
			array.add(values);
		}
		return array;
	}

}

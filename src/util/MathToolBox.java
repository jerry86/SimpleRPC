package util;

import java.util.Arrays;

public class MathToolBox {

	public MathToolBox() {

	}

	public double[][] multiply(double[][] a, double[][] b) {
		int rowsInA = a.length;
		int columnsInA = a[0].length; // same as rows in B
		int columnsInB = b[0].length;
		double[][] c = new double[rowsInA][columnsInB];
		for (int i = 0; i < rowsInA; i++) {
			for (int j = 0; j < columnsInB; j++) {
				for (int k = 0; k < columnsInA; k++) {
					c[i][j] = c[i][j] + a[i][k] * b[k][j];
				}
			}
		}
		return c;
	}

	public double[] sort(double[] array) {
		Arrays.sort(array);
		return array;
	}

	public double max(double[] array) {
		if (array == null || array.length == 0)
			return Integer.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			max = Math.max(max, array[i]);
		}
		return max;
	}

	public double min(double[] array) {
		if (array == null || array.length == 0)
			return Integer.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < array.length; i++) {
			min = Math.min(min, array[i]);
		}
		return min;
	}

}

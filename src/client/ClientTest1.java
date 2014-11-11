package client;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import client_lib.*;

/**
 * 
 * This client is used for testing the multi-threading of mapper and server.
 * 
 * @author Zichuan
 *
 */
public class ClientTest1 {
	static int count = 0;

	public static void foo() {
		/* Test "multiply" procedure call */
		Date d = new Date();

		Set<Thread> set = new HashSet<>();
		for (int i = 0; i < 1000; i++) { // <--- change # of threads here
			Thread t = new Testing();
			set.add(t);
		}
		set.stream().parallel().unordered().forEach(x -> {
			count++;
			x.run();
		});
		System.out.println(count + " of executions were completed in "
				+ (System.currentTimeMillis() - d.getTime()) / 1000 + " seconds. \n");
	}

	public static void main(String[] args) {
		foo();
	}

}

class Testing extends Thread {
	@Override
	public void run() {
		A1 a1 = new A1();

		double[] matrixArray = { 1.1, 2.3, 4.2, 1.11, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1,
				2.3, 4.2, 1.1, 2.3, 14.2, 1.1, 2.3, 4.22, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3,
				4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 13.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2,
				1.1, 2.3, 84.2, 1.1, 2.3, 49.2, 1.1, 2.03, 4.52, 41.1, 2.3, 4.2, 1.1, 2.3, 4.2,
				1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.3, 2.3, 4.2, 3.1,
				2.3, 4.2, 0.1, 2.3, 4.2, 1.1, 2.3, 4.2, 17.1, 2.3, 4.12, 1.1, 2.3, 4.2, 1.1, 2.3,
				4.2, 1.1, 2.3, 47.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.21, 2.3, 4.2, 1.1, 2.3, 4.2,
				1.1, 2.3, 4.2, 61.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1,
				2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.37, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3,
				4.2, 1.1, 2.3, 4.2, 15.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 4.1, 2.3, 4.2,
				1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 14.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1,
				2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 47.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3,
				4.2, 1.1, 2.3, 4.2, 6.1, 2.3, 4.2, 1.8, 2.3, 4.2, 1.4, 9.3 };
		double[][] bigMatrix = new double[200][200];
		for (int i = 0; i < 200; i++) {
			bigMatrix[i] = matrixArray;
		}
		/* Test "multiply" procedure call */
		double[][] resultMatrix = a1.multiply(bigMatrix, bigMatrix);
		if (resultMatrix != null) {
			for (int i = 0; i < resultMatrix.length; i++) {
				System.out.println();
				for (int j = 0; j < resultMatrix[0].length; j++) {
					System.out.print((int) resultMatrix[i][j] + " ");
				}
			}
		}
		System.out.println("");

		double[] array = { 108, 1.1, 11.02, 9.67, 3.5, 0.884, 2.3, 0.97, 12.4, 0.1 };
		/* Test "max" procedure call */
		double max = a1.max(array);
		System.out.println(max);
		/* Test "min" procedure call */
		double min = a1.min(array);
		System.out.println(min);
		/* Test "sort" procedure call */
		double[] na = a1.sort(array);
		for (int i = 0; i < na.length; i++) {
			System.out.print(na[i]);
		}
		System.out.println("");
	}
}

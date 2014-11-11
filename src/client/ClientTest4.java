package client;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import client_lib.B1;
import client_lib.B2;

/**
 * 
 * This client is used for testing the multi-threading of mapper and server.
 * 
 * @author Zichuan
 *
 */
public class ClientTest4 {

	static int count = 0;

	public static void foo() {
		/* Test "multiply" procedure call */
		Date d = new Date();
		Set<Thread> set = new HashSet<>();
		for (int i = 0; i < 100; i++) { // <--- change # of threads here
			Thread t = new Testing4();
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

class Testing4 extends Thread {
	@Override
	public void run() {
		/**
		 * Calling server B1
		 */
		B1 b1 = new B1();

		double[] matrixArray = { 10.1, 42.3, 0.2, 1.91, 2.3, 4.2, 0.19, 2.3, 4.2, 1.5, 2.3, 4.2,
				1.1, 2.3, 4.2, 1.1, 2.3, 14.2, 1.1, 2.3, 4.22, 16.01, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1,
				2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 13.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3,
				4.2, 1.1, 2.3, 84.2, 1.51, 2.3, 49.2, 1.532, 2.03, 4.52, 41.1, 2.3, 4.2, 1.1, 2.3,
				4.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.31, 2.3, 4.2, 1.1, 2.3, 4.2, 1.3, 2.3, 4.2,
				3.1, 2.3, 4.2, 0.1, 21.53, 4.2, 1.1, 2.3, 4.2, 17.1, 2.3, 4.12, 1.1, 2.3, 4.2,
				19.1, 2.3, 4.2, 1.1, 2.3, 47.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2, 1.21, 2.3, 4.2, 1.1,
				2.3, 4.2, 3.77, 2.3, 4.2, 61.1, 2.3, 4.2, 1.1, 25.3, 4.2, 1.1, 2.3, 4.2, 1.5, 2.3,
				4.2, 1.1, 2.3, 4.2, 1, 2.3, 4.2, 1.1, 2.37, 4.2, 1.1, 2.3, 4.2, 1.91, 2.3, 4.2,
				2.1, 2.3, 4.2, 7.1, 2.3, 4.2, 15.1, 2.3, 4.2, 21.1, 2.3, 4.2, 1.1, 2.3, 4.2, 4.1,
				2.3, 4.2, 1.1, 2.3, 4.2, 5.1, 2.3, 4.2, 14.1, 2.113, 4.2, 1.1, 2.3, 4.2, 1.1, 2.3,
				4.2, 1.1, 2.3, 4.2, 10.1, 29.3, 4.2, 1.1, 2.3, 47.2, 1.1, 2.3, 4.2, 1.1, 2.3, 4.2,
				1.1, 2.3, 4.2, 0.1, 2.3, 4.2, 6.1, 2.3, 4.2, 1.8, 2.3, 4.2, 1.4, 9.3 };
		double[][] bigMatrix = new double[200][200];
		for (int i = 0; i < 200; i++) {
			bigMatrix[i] = matrixArray;
		}
		/* Test "multiply" procedure call */
		double[][] resultMatrix = b1.multiply(bigMatrix, bigMatrix);
		if (resultMatrix != null) {
			for (int i = 0; i < resultMatrix.length; i++) {
				System.out.println();
				for (int j = 0; j < resultMatrix[0].length; j++) {
					System.out.print((int) resultMatrix[i][j] + " ");
				}
			}
		}
		System.out.println("");
		/* Test "min" procedure call */
		double min = b1.min(matrixArray);
		System.out.println(min);

		/**
		 * Calling server A2
		 */
		B2 b2 = new B2();
		/* Test "multiply" procedure call */
		double[][] resultMatrix2 = b2.multiply(bigMatrix, bigMatrix);
		if (resultMatrix2 != null) {
			for (int i = 0; i < resultMatrix2.length; i++) {
				System.out.println();
				for (int j = 0; j < resultMatrix2[0].length; j++) {
					System.out.print((int) resultMatrix2[i][j] + " ");
				}
			}
		}
		System.out.println("");
		/* Test "max" procedure call */
		double max2 = b2.max(matrixArray);
		System.out.println(max2);

	}
}

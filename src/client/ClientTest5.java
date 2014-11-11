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
public class ClientTest5 {

	static int count = 0;

	public static void foo() {
		Date d = new Date();
		Set<Thread> set = new HashSet<>();
		for (int i = 0; i < 100; i++) { // <--- change # of threads here
			Thread t = new Testing5();
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

class Testing5 extends Thread {
	@Override
	public void run() {
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

		A1 a1 = new A1();
		A2 a2 = new A2();
		B1 b1 = new B1();
		B2 b2 = new B2();
		C1 c1 = new C1();

		double[][] resultMatrixa1 = a1.multiply(bigMatrix, bigMatrix);
		if (resultMatrixa1 != null) {
			for (int i = 0; i < resultMatrixa1.length; i++) {
				System.out.println();
				for (int j = 0; j < resultMatrixa1[0].length; j++) {
					System.out.print((int) resultMatrixa1[i][j] + " ");
				}
			}
		}
		System.out.println("");

		double mina2 = a2.min(matrixArray);
		System.out.println(mina2);

		double[][] resultMatrixb1 = b1.multiply(bigMatrix, bigMatrix);
		if (resultMatrixb1 != null) {
			for (int i = 0; i < resultMatrixb1.length; i++) {
				System.out.println();
				for (int j = 0; j < resultMatrixb1[0].length; j++) {
					System.out.print((int) resultMatrixb1[i][j] + " ");
				}
			}
		}
		System.out.println("");

		double maxb2 = b2.max(matrixArray);
		System.out.println(maxb2);

		double[] arrayc1 = c1.sort(matrixArray);
		if (arrayc1 != null) {
			for (int i = 0; i < arrayc1.length; i++) {
				System.out.print(arrayc1[i] + "  ");
			}
		}
		System.out.println("");

	}
}

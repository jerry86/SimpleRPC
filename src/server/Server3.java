package server;

import server_lib.B1;
import server_lib.ServerConnectionHandler;
import util.MathToolBox;

public class Server3 implements B1 {

	public static void main(String[] args) {
		ServerConnectionHandler.run("Server3", B1.getPuidList());
	}

	@Override
	public String procedure_0(B1 b1) {
		return "PROCEDURE_0_RECEIVED";
	}

	@Override
	public double[][] multiply(double[][] matrix1, double[][] matrix2, B1 b1) {
		MathToolBox m = new MathToolBox();
		return m.multiply(matrix1, matrix2);
	}

	@Override
	public double max(double[] array, B1 b1) {
		MathToolBox m = new MathToolBox();
		return m.max(array);
	}

}

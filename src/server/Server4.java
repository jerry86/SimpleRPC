package server;

import server_lib.B2;
import server_lib.ServerConnectionHandler;
import util.MathToolBox;

public class Server4 implements B2 {

	public static void main(String[] args) {
		ServerConnectionHandler.run("Server4", B2.getPuidList());
	}

	@Override
	public String procedure_0(B2 b2) {
		return "PROCEDURE_0_RECEIVED";
	}

	@Override
	public double[][] multiply(double[][] matrix1, double[][] matrix2, B2 b2) {
		MathToolBox m = new MathToolBox();
		return m.multiply(matrix1, matrix2);
	}

	@Override
	public double min(double[] array, B2 b2) {
		MathToolBox m = new MathToolBox();
		return m.min(array);
	}

}

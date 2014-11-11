package server;

import server_lib.A1;
import server_lib.ServerConnectionHandler;
import util.MathToolBox;

public class Server1 extends ServerConnectionHandler implements A1 {

	public static void main(String[] args) {
		ServerConnectionHandler.run("Server1", A1.getPuidList());
	}

	@Override
	public String procedure_0(A1 a1) {
		return "PROCEDURE_0_RECEIVED";
	}

	@Override
	public double[][] multiply(double[][] matrix1, double[][] matrix2, A1 a1) {
		MathToolBox m = new MathToolBox();
		return m.multiply(matrix1, matrix2);
	}

	@Override
	public double[] sort(double[] array, A1 a1) {
		MathToolBox m = new MathToolBox();
		return m.sort(array);
	}

	@Override
	public double max(double[] array, A1 a1) {
		MathToolBox m = new MathToolBox();
		return m.max(array);
	}

	@Override
	public double min(double[] array, A1 a1) {
		MathToolBox m = new MathToolBox();
		return m.min(array);
	}

}

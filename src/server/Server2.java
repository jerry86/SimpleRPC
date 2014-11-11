package server;

import server_lib.A2;
import server_lib.ServerConnectionHandler;
import util.MathToolBox;

public class Server2 extends ServerConnectionHandler implements A2 {

	public static void main(String[] args) {
		ServerConnectionHandler.run("Server2", A2.getPuidList());
	}

	@Override
	public String procedure_0(A2 a2) {
		return "PROCEDURE_0_RECEIVED";
	}

	@Override
	public double max(double[] array, A2 a2) {
		MathToolBox m = new MathToolBox();
		return m.max(array);
	}

	@Override
	public double min(double[] array, A2 a2) {
		MathToolBox m = new MathToolBox();
		return m.min(array);
	}

}

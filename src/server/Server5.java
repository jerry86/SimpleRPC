package server;

import server_lib.C1;
import server_lib.ServerConnectionHandler;
import util.MathToolBox;

public class Server5 implements C1 {

	public static void main(String[] args) {
		ServerConnectionHandler.run("Server5", C1.getPuidList());
	}

	public String procedure_0(C1 c1) {
		return "PROCEDURE_0_RECEIVED";
	}

	@Override
	public double[] sort(double[] array, C1 c1) {
		MathToolBox m = new MathToolBox();
		return m.sort(array);
	}
}

package client_lib;

import java.util.ArrayList;

import util.Address;
import message.ClientToMapperMsg;
import message.ClientToServerMsg;

public class B2 extends ClientConnectionHandler {

	public String procedure_0() {
		Address clientAddress = getPublicAddress();
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		String transactionId = System.currentTimeMillis() + "B" + "2" + "procedure_0"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "B", "2",
				"procedure_0", parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "B", "2",
				"procedure_0", parameterTypeList);
		try {
			return (String) getResult(clientToMapperMsg, clientToServerMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double[][] multiply(double[][] a, double[][] b) {
		System.out.println(procedure_0());
		Address clientAddress = getPublicAddress();
		// System.out.println("Client address is: " + clientAddress.toString() + "\n");
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("double[][]");
		parameterTypeList.add("double[][]");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		parameterValueList.add(a);
		parameterValueList.add(b);
		String transactionId = System.currentTimeMillis() + "B" + "2" + "multiply"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "B", "2",
				"multiply", parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "B", "2",
				"multiply", parameterTypeList);
		try {
			return (double[][]) getResult(clientToMapperMsg, clientToServerMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double max(double[] a) {
		System.out.println(procedure_0());
		Address clientAddress = getPublicAddress();
		// System.out.println("Client address is: " + clientAddress.toString() + "\n");
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("double[]");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		parameterValueList.add(a);
		String transactionId = System.currentTimeMillis() + "B" + "2" + "max"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "B", "2", "max",
				parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "B", "2", "max",
				parameterTypeList);
		try {
			Object result = getResult(clientToMapperMsg, clientToServerMsg);
			if (result != null) {
				return (double) result;
			} else {
				return Double.NaN;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}
}

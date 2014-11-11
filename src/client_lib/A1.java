package client_lib;

import java.util.ArrayList;

import util.Address;
import message.ClientToMapperMsg;
import message.ClientToServerMsg;

public class A1 extends ClientConnectionHandler {

	public String procedure_0() {
		Address clientAddress = getPublicAddress();
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		String transactionId = System.currentTimeMillis() + "A" + "1" + "procedure_0"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "A", "1",
				"procedure_0", parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "A", "1",
				"procedure_0", parameterTypeList);
		try {
			return (String) getResult(clientToMapperMsg, clientToServerMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double[][] multiply(double[][] a, double[][] b) {
		// print procedure_0
		System.out.println(procedure_0());
		// run the rest of RPC
		Address clientAddress = getPublicAddress();
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("double[][]");
		parameterTypeList.add("double[][]");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		parameterValueList.add(a);
		parameterValueList.add(b);
		String transactionId = System.currentTimeMillis() + "A" + "1" + "multiply"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "A", "1",
				"multiply", parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "A", "1",
				"multiply", parameterTypeList);
		try {
			return (double[][]) getResult(clientToMapperMsg, clientToServerMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double[] sort(double[] a) {
		System.out.println(procedure_0());
		Address clientAddress = getPublicAddress();
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("double[]");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		parameterValueList.add(a);
		String transactionId = System.currentTimeMillis() + "A" + "1" + "sort"
				+ parameterTypeList.toString();
		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "A", "1",
				"sort", parameterTypeList, parameterValueList, transactionId);
		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "A", "1",
				"sort", parameterTypeList);
		try {
			return (double[]) getResult(clientToMapperMsg, clientToServerMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double min(double[] a) {
		// print procedure_0
		System.out.println(procedure_0());
		// run the rest of RPC
		Address clientAddress = getPublicAddress();
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("double[]");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		parameterValueList.add(a);
		String transactionId = System.currentTimeMillis() + "A" + "1" + "min"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "A", "1", "min",
				parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "A", "1", "min",
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

	public double max(double[] a) {
		System.out.println(procedure_0());
		Address clientAddress = getPublicAddress();
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("double[]");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		parameterValueList.add(a);
		String transactionId = System.currentTimeMillis() + "A" + "1" + "max"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "A", "1", "max",
				parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "A", "1", "max",
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

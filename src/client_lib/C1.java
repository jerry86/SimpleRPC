package client_lib;

import java.util.ArrayList;

import util.Address;
import message.ClientToMapperMsg;
import message.ClientToServerMsg;

public class C1 extends ClientConnectionHandler {

	public String procedure_0() {
		Address clientAddress = getPublicAddress();
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		String transactionId = System.currentTimeMillis() + "C" + "1" + "procedure_0"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "C", "1",
				"procedure_0", parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "C", "1",
				"procedure_0", parameterTypeList);
		try {
			return (String) getResult(clientToMapperMsg, clientToServerMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double[] sort(double[] a) {
		System.out.println(procedure_0());
		Address clientAddress = getPublicAddress();
		// System.out.println("Client address is: " + clientAddress.toString() + "\n");
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		parameterTypeList.add("double[]");
		ArrayList<Object> parameterValueList = new ArrayList<>();
		parameterValueList.add(a);
		String transactionId = System.currentTimeMillis() + "C" + "1" + "sort"
				+ parameterTypeList.toString();

		ClientToServerMsg clientToServerMsg = new ClientToServerMsg(clientAddress, "C", "1",
				"sort", parameterTypeList, parameterValueList, transactionId);

		ClientToMapperMsg clientToMapperMsg = new ClientToMapperMsg(clientAddress, "C", "1",
				"sort", parameterTypeList);
		try {
			return (double[]) getResult(clientToMapperMsg, clientToServerMsg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

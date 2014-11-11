package message;

import java.io.Serializable;
import java.util.ArrayList;

import util.Address;

public class ClientToServerMsg implements Serializable {
	public Address clientAddress;
	public String programName, version, procedureName, transactionId;
	public ArrayList<String> parameterTypeList;
	public ArrayList<Object> parameterList;

	public ClientToServerMsg(Address clientAddress, String programName, String version,
			String procedureName, ArrayList<String> parameterTypeList,
			ArrayList<Object> parameterList, String transactionId) {
		this.clientAddress = clientAddress;
		this.programName = programName;
		this.version = version;
		this.procedureName = procedureName;
		this.parameterTypeList = parameterTypeList;
		this.parameterList = parameterList;
		this.transactionId = transactionId;
	}

}

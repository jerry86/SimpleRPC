package message;

import java.io.Serializable;

import util.Address;

public class ServerToClientMsg implements Serializable {
	public String resultType, transactionId;
	public Object resultValue;
	public Address clientAddress;

	public ServerToClientMsg(String resultType, Object resultValue, Address clientAddress) {
		this.resultType = resultType;
		this.resultValue = resultValue;
		this.clientAddress = clientAddress;
		transactionId = "";
	}

	// Jerry's code
	// public ServerToClientMsg(String transactionId, String resultType, Object resultValue) {
	// this.resultType = resultType;
	// this.resultValue = resultValue;
	// this.transactionId = transactionId;
	// }

	// Yanbing's code 10/25
	public ServerToClientMsg(String transactionId, String resultType, Object resultValue,
			Address clientAddress) {
		this.resultType = resultType;
		this.resultValue = resultValue;
		this.transactionId = transactionId;
		this.clientAddress = clientAddress;
	}

}

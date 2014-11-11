package message;

import java.io.Serializable;
import java.util.ArrayList;

import util.Address;
import util.ProgramUniqueIdentifier;

public class ClientToMapperMsg implements Serializable {

	public Address clientAddress;
	public String programName;
	public String version;
	public String procedureName;
	public ArrayList<String> parameterTypes;

	public ClientToMapperMsg(Address clientAddress, String programName, String version,
			String procedureName, ArrayList<String> parameterTypes) {
		super();
		this.clientAddress = clientAddress;
		this.programName = programName;
		this.version = version;
		this.procedureName = procedureName;
		this.parameterTypes = parameterTypes;
	}

	public String getClientRequestProgram() {
		return programName + version + procedureName + parameterTypes.toString();
	}

	public ProgramUniqueIdentifier getRequestingPUID() {
		ProgramUniqueIdentifier puid = new ProgramUniqueIdentifier();
		puid.programName = this.programName;
		puid.version = this.version;
		puid.procedureName = this.procedureName;
		puid.parameterTypes = this.parameterTypes;
		return puid;
	}

	public String printRequestingContent() {
		String str = programName + ", " + version + ", " + procedureName;
		for (String s : parameterTypes) {
			str += ", " + s;
		}
		return str;
	}

}

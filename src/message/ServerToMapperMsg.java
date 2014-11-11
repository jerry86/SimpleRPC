package message;

import java.io.Serializable;
import java.util.ArrayList;

import util.Address;
import util.ProgramUniqueIdentifier;

/**
 * This data structure is used to handle the messages that are sent 
 * from a server to the Port_Mapper. It carries three major tasks, 
 * including (1) registering the server to Port_Mapper when it starts 
 * running; (2) the "heart-beat" acknowledgement to notify 
 * Port_Mapper that this server is still alive; and (3) re-registering a 
 * certain procedure call to Port_Mapper when a server becomes 
 * available again (TODO need to think a little bit more...).
 * 
 * @author Zichuan
 * @version 2014/10/26
 */
public class ServerToMapperMsg implements Serializable {

	public Address serverAddress;
	public ArrayList<ProgramUniqueIdentifier> supportedProgramList;
	public boolean isReregistration;
	public ProgramUniqueIdentifier reRegisteringPUID;

	/*---> for registering <---*/
	public ServerToMapperMsg(Address serverAddress,
			ArrayList<ProgramUniqueIdentifier> supportedProgramList) {
		super();
		this.serverAddress = serverAddress;
		this.supportedProgramList = supportedProgramList;
		this.isReregistration = false;
	}

	/*---> for re-registering (may not need) <---*/
	public ServerToMapperMsg(Address serverAddress, ProgramUniqueIdentifier reRegisteringPUID) {
		super();
		this.serverAddress = serverAddress;
		this.isReregistration = true;
		this.reRegisteringPUID = reRegisteringPUID;
	}

}

package message;

import java.io.Serializable;
import java.util.ArrayList;

import util.Address;

/**
 *  MapperToClientMsg is the object that carries the information that Port_Mapper returns 
 *  to the client after processing a client-to-mapper request. Since Port_Mapper only serves 
 *  for the purpose of binding between an available server and the requesting client, the 
 *  message that Port_Mapper returns to the client only contains the address of the available 
 *  server. Once the client gets the server address, it starts connecting to the server by 
 *  following RPC protocols.
 *  
 *  In order to implement a multi-threading Port_Mapper, this message is also defined as 
 *  "Serializable".
 *  
 * @author Zichuan
 */
public class MapperToClientMsg implements Serializable {
	
	public Address serverAddress;

	public MapperToClientMsg(Address serverAddress) {
		super();
		this.serverAddress = serverAddress;
	}

	// public MapperToClientMsg() {
	// super();
	// this.serverAddress = null;
	// }
}

package name_server;

import java.util.Queue;
import java.util.concurrent.Callable;

import message.ClientToMapperMsg;
import util.Address;

public class SupportedServerLookUp implements Callable<Address> {

	ClientToMapperMsg requestMsg;

	public SupportedServerLookUp(ClientToMapperMsg requestMsg) {
		this.requestMsg = requestMsg;
	}

	/**
	 * Using client-to-mapper message's PUID to find out a supporting server 
	 * from Port_Mapper.puidQueue.
	 * 
	 * @return an available server's address or null if no one was found
	 */
	public Address getAvailableServerAddress() {
		Address serverAddress = null;
		if (requestMsg == null || requestMsg.clientAddress == null) {
			return null;
		}
		if (PortMapper.puidQueue.containsKey(requestMsg.getRequestingPUID())) {
			Queue<Address> puidQueue = PortMapper.puidQueue.get(requestMsg.getRequestingPUID());
			if (puidQueue != null && puidQueue.size() > 0) {
				/*---> pick the first one from puid_map <---*/
				serverAddress = puidQueue.poll(); // get a server from the head of queue
				if (serverAddress != null) {
					System.out.println("Found a supporting server{" + serverAddress.toString()
							+ "}.\n");
				}
				puidQueue.add(serverAddress); // move the server to the end of the queue
			}
		}
		if (serverAddress == null) {
			System.out.println("Did not find a supporting server!\n");
		}
		return serverAddress;
	}

	@Override
	public Address call() throws Exception {
		return getAvailableServerAddress();
	}
}
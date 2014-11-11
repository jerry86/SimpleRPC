package name_server;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

import message.ServerToMapperMsg;
import util.Address;
import util.ProgramUniqueIdentifier;

/**
 * Server registration is a key part of RPC functionalities. When starting a server, 
 * the first task it needs to accomplish is to report to the Port_Mapper about its 
 * existence and to register the procedure calls it supports. It also carries the 
 * "heart-beat" communication in which a server periodically sends to mapper 
 * an acknowledgement, informing mapper that it is still alive and ready for 
 * client requests.
 * 
 * @author Zichuan
 * @version 2014/10/26
 */

public class ServerRegistration implements Callable<Boolean>, Serializable {

	ServerToMapperMsg requestMsg;

	public ServerRegistration(ServerToMapperMsg msg) {
		requestMsg = msg;
	}

	public boolean run() {
		if (requestMsg != null) {
			System.out.println("\nPort_Mapper is registering server{"
					+ requestMsg.serverAddress.toString() + "}...");
			/*---> begin of debugging <---*/
			System.out.println("PortMapper received a message from server{"
					+ requestMsg.serverAddress.toString() + "}.");
			System.out.println("Server{" + requestMsg.serverAddress.toString()
					+ "} is registering the following procedure call(s):");
			// lambda expression, prints the content of each supported puid
			if (requestMsg.supportedProgramList != null) {
				requestMsg.supportedProgramList.parallelStream().unordered().forEach(x -> {
					System.out.println("  " + x.toString());
				});
			}
			/*---> end of debugging <---*/

			// update address <--> List<PUID>
			PortMapper.addressMap.put(requestMsg.serverAddress, requestMsg.supportedProgramList);
			// update address <--> date
			Date date = new Date();
			PortMapper.serverActivityMap.put(requestMsg.serverAddress, date);
			// update puid <--> Queue<address>
			for (ProgramUniqueIdentifier puid : requestMsg.supportedProgramList) {
				if (PortMapper.puidQueue.containsKey(puid)) {
					Queue<Address> list = PortMapper.puidQueue.get(puid);
					list.remove(requestMsg.serverAddress);
					list.add(requestMsg.serverAddress);
				} else {
					Queue<Address> list = new LinkedList<>();
					list.remove(requestMsg.serverAddress);
					list.add(requestMsg.serverAddress);
					PortMapper.puidQueue.put(puid, list);
				}
			}
			System.out.println("Server registration completed.\n");
			// System.out.println(PortMapper.puidQueue.size());
			// System.out.println(PortMapper.addressMap.size());
			// System.out.println(PortMapper.serverActivityMap.size());
			return true;
		} else {
			System.out.println("Error updating the server tables in PortMapper!\n");
			return false;
		}

	}

	@Override
	public Boolean call() throws Exception {
		return run();
	}
}

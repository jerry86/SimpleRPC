package name_server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import message.ClientToMapperMsg;
import message.MapperToClientMsg;
import message.MapperToMapperMsg;
import message.ServerToMapperMsg;
import util.Address;

/**
 * This class is defined as the to carry the operations of a single Port_Mapper thread. The 
 * operations include:
 * (1) Create a socket to listen to the server socket in Port_Mapper
 * (2) Get a message from the listener and process it if it is 
 * (3) a server-to-mapper message, then register server's reporting PUIDs
 * (4) a client-to-mapper message, then return a supporting server address according 
 *      requesting PUID.
 * (5) Turn off the input/output streams and shut down the listener socket.
 * 
 * @author Zichuan
 *
 */
public class PMHandlingThread implements Callable<Boolean> {

	@Override
	public Boolean call() throws Exception {
		System.out.println("\nPort_Mapper is waiting for requests...\n");
		try {
			/**
			 * (3) listen to a requests which was sent by a server or a client
			 */
			Socket socket = PortMapper.listener.accept(); // create a socket for listener
			InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			Object requestMsg;

			// get an object from the input stream
			requestMsg = objectInputStream.readObject();
			System.out.println("Port_Mappr received a message!\n");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

			if (requestMsg == null) {
				// do nothing
				System.out.println("Port_Mapper received a message 'null'.\n");
			} else if (requestMsg instanceof MapperToMapperMsg) {
				System.out.println("Port_Mapper received a message from PM_replica.\n");
				MapperToMapperMsg m2mResponse = new MapperToMapperMsg();
				m2mResponse.isAlive = true;
				objectOutputStream.writeObject(m2mResponse);
				/* Need flush the output stream */
				objectOutputStream.flush();
				return true;
				/**
				 * (4) Update the server tables by creating a thread.
				 */
			} else if (requestMsg instanceof ServerToMapperMsg) {
				// ServerToMapperMsg s2mMsg = (ServerToMapperMsg) requestMsg;
				// s2mMsg.supportedProgramList.add(e)
				ServerRegistration sr = new ServerRegistration((ServerToMapperMsg) requestMsg);
				Boolean result = sr.run();
				if (!result) {
					System.out.println("Server registration failed!\n");
				}
				/**
				 * (5) Find a server that matches a client binding request. Update the server
				 * tables by creating a thread. Send the address of the server back to the
				 * client.
				 */
			} else if (requestMsg instanceof ClientToMapperMsg) {
				// in case of failure
				MapperToClientMsg mapperToClient = null;
				if (((ClientToMapperMsg) requestMsg) == null
						|| ((ClientToMapperMsg) requestMsg).clientAddress == null) {
					System.out
							.println("Error (client --> mapper)! Client-to-PortMapp request messge is null.\n");
					mapperToClient = new MapperToClientMsg(null);
				} else {
					System.out.println("PortMapper received a message from client:{"
							+ ((ClientToMapperMsg) requestMsg).clientAddress.toString()
							+ "} requesting a PUID: "
							+ ((ClientToMapperMsg) requestMsg).printRequestingContent() + "\n");
					// Address serverAddress = PortMapper.executorService.submit(
					// new SupportedServerLookUp((ClientToMapperMsg) requestMsg)).get();
					SupportedServerLookUp sslu = new SupportedServerLookUp(
							(ClientToMapperMsg) requestMsg);
					Address serverAddress = sslu.getAvailableServerAddress();
					if (serverAddress == null) { // if server is not found
						System.out
								.println("Error! Port_Mapper didn't found an available server for the client request.\n");
						mapperToClient = new MapperToClientMsg(null);
					} else { // if a server is found
						// Send the address of the server back to the client
						mapperToClient = new MapperToClientMsg(serverAddress);
						System.out.println("Port_Mapper found a server{"
								+ mapperToClient.serverAddress.toString() + "} for client:{"
								+ ((ClientToMapperMsg) requestMsg).clientAddress.toString()
								+ "}.\n");
					}
				}
				objectOutputStream.writeObject(mapperToClient);
				/* Need flush the output stream */
				objectOutputStream.flush();

			} else { // the message is not belong to null, ClientToMapperMsg nor
						// ServerToMapperMsg
				System.out
						.println("Error! PortMapper received a message that cannot be identified.\n");
				// MapperToClientMsg mapperToClient = new MapperToClientMsg();
				// objectOutputStream.writeObject(mapperToClient);
				// /* Need flush the output stream */
				// objectOutputStream.flush();
			}

			objectOutputStream.close();
			objectInputStream.close();
			socket.close();
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}

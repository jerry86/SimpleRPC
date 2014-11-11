package client_lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import util.Address;
import message.ClientToMapperMsg;
import message.ClientToServerMsg;
import message.MapperToClientMsg;
import message.ServerToClientMsg;

/**
 * The Connection_Handler is used to let a client connect to the Port_Mapper and a 
 * server. It needs to implement a variety of methods that handles two major types 
 * of connections: (1) connect to the public directory where Port_Mapper's address 
 * is stored; (2) client to Port_Mapper connect; (3) client to server connection.
 * 
 * (1) {Connect to the public directory}:
 *      To find a server that supports the remote procedure call, the connection 
 *      handler needs to query the address of the Port_Mapper before the binding 
 *      between client/server is provided by the mapper. This approach mocks the 
 *      typical client connection to the DNS.
 * 
 * (2) {Client to Port_Mapper Connection}:
 *      In client/Port_Mapper connection, the connection_handler first goes the public 
 *      directory where the IP address of the Port_Mapper is stored. This inquiry is a 
 *      mocked process of a client querying on a DNS. After getting the Port_Mapper's 
 *      address, the client sends a message (defined as ClientToMapperMsg) to the Port_
 *      Mapper, requesting the address of an server that supports the procedure which 
 *      the client needs. As a result, the Port_Mapper returns either the address of an 
 *      available server, or a message which tells the client that no server is found.
 *      
 *      In case that no message was sent back by the mapper, the client implements 
 *      a strategy to re_send the request after a certain amount of time. In this project, we 
 *      set the default time_out span as 30 seconds. It is certain that such time span can 
 *      vary depending on a series of parameters of the network and Port_Mapper design. 
 *      
 *      On the other situation where the mapper finds no supported server to the client's 
 *      procedure call, there are several different ways we can handle it, such as throwing 
 *      exceptions, blocking the process and return some default value that indicated a 
 *      failed execution result. In our implementation, we take transparency into account, 
 *      and hence decided to use a "heart_beat" + "throw exception" strategy. Connection_
 *      Handler sends the request to the mapper for a certain time interval (default is 30 s) 
 *      if the mapper doesn't find out an available server. After a number of attempts (default
 *      is 3 attempts), it throws an exception indicating that execution is failed. This entire 
 *      process, however, will NOT be seen by the client who calls the procedure using client 
 *      library. He/she is only notified that the execution is failed.  
 *      
 * (3) {Client to Server Connection}:
 *      After getting the address of an available server that supports the procedure which 
 *      the client need, a connection between a client and a server is established by the 
 *      Connection_Handler.
 * 
 * @author Zichuan
 * @version 2014/10/26
 *
 */
public class ClientConnectionHandler {

	public Socket mapperSocket, serverSocket;
	public Address clientToMapperAddress, clientToServerAddress;

	public Object getResult(ClientToMapperMsg clientToMapperMsg, ClientToServerMsg clientToServerMsg)
			throws Exception {
		// try {
			/**
			 * (1) read the public directory to find out the address of the mapper
			 */
			String mapperIP = null;
			int mapperPortNum = -1;
			BufferedReader br = null;
			String sCurrentLine;
			// ** need to change the directory if debugging using cmd instead of eclipse
			br = new BufferedReader(new FileReader("src/publicDNS.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] strArray = sCurrentLine.split(":");
				if (strArray[0] != null && strArray[1] != null) {
					// create a socket
					mapperIP = strArray[0];
					mapperPortNum = Integer.parseInt(strArray[1]);
				}
			}
			if (br != null) {
				br.close();
			}

			/**
			 * (2) This part handles the connection: client to Port_Mapper
			 */
			// build a socket that links to Port_Mapper
			if (mapperIP == null || mapperPortNum == -1) {
				System.out.println("Failed in finding the mapper IP or Port number.\nIP: "
						+ mapperIP + ", port number: " + mapperPortNum + " \n");
			}

			mapperSocket = new Socket(mapperIP, mapperPortNum); // connect to port-mapper

			System.out.println("\nConnection between client{"
					+ clientToMapperMsg.clientAddress.toString() + "} and Port_Mapper{" + mapperIP
					+ ":" + mapperPortNum + "} is established successfully.\n");

			// create an output stream
			ObjectOutputStream clientMapperObjectOutputStream = new ObjectOutputStream(
					mapperSocket.getOutputStream());

			// send msg to mapper
			clientMapperObjectOutputStream.writeObject(clientToMapperMsg);
			clientMapperObjectOutputStream.flush();

			// create an input stream. Receive msg from mapper
			ObjectInputStream clientMapperObjectInputStream = new ObjectInputStream(
					mapperSocket.getInputStream());

			// The RETURN RESULT from mapper to client, which may contain a server address if a
			// supported one exists, or returns null if none exists.
			MapperToClientMsg mapperToClientMsg = (MapperToClientMsg) clientMapperObjectInputStream
					.readObject();

			Address serverAddress = mapperToClientMsg.serverAddress;
			// print server address for debugging purposes
			if (serverAddress != null)
				System.out.println("A supporting server{" + serverAddress.toString()
						+ "} is found.\n");

			clientMapperObjectOutputStream.close();
			clientMapperObjectInputStream.close();
			mapperSocket.close();

			/* return null if no server supports the procedure call was found */
			if (serverAddress == null || serverAddress.ip == "" || serverAddress.port == "") {
				System.out.println("Transaction aborted! No supported server has been found.\n");
				return null;
			}

			/**
			 * (3) This part handles the connection: client to server
			 */
			Object serverReturnedResult = null;
			// create a client-to-server socket
			serverSocket = new Socket(serverAddress.ip, Integer.parseInt(serverAddress.port));
			ObjectOutputStream clientServerObjectOutputStream = new ObjectOutputStream(
					serverSocket.getOutputStream());

			// send msg to server
			System.out.println("Client{" + clientToServerMsg.clientAddress.toString()
					+ "} is sending a message to server{" + serverAddress + "}.\n");
			clientServerObjectOutputStream.writeObject(clientToServerMsg);
			clientServerObjectOutputStream.flush();

			// create an input stream. Receive msg from server
			ObjectInputStream clientServerObjectInputStream = new ObjectInputStream(
					serverSocket.getInputStream());
			ServerToClientMsg serverToClientMsg = (ServerToClientMsg) clientServerObjectInputStream
					.readObject();

			System.out.println("Client{" + clientToServerMsg.clientAddress.toString()
					+ "} received a message from server{" + serverAddress + "}.\n");

			clientServerObjectOutputStream.close();
			clientServerObjectInputStream.close();
			serverSocket.close();

			serverReturnedResult = serverToClientMsg.resultValue;

			if (serverReturnedResult != null) {
				System.out.println("Yes, a result message is returned from server!\n");
				return serverReturnedResult;
			} else {
				System.out.println("Error in getting the return result from the server!\n");
			}
//
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			return null;
//		}
		return null;
	}

	/**
	 * get the public IP address of this machine where PortMapper resides
	*/
	public Address getPublicAddress() {
		ServerSocket ss;
		String publicIP = "";
		String publicPort = "";
		try {
			ss = new ServerSocket(0);
			publicPort = String.valueOf(ss.getLocalPort());
			Enumeration<NetworkInterface> e;
			e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				Enumeration<InetAddress> addresses = e.nextElement().getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) addresses.nextElement();
					/* check if inetAddress is a public address */
					if (!inetAddress.isAnyLocalAddress() && !inetAddress.isLinkLocalAddress()
							&& !inetAddress.isLoopbackAddress() && !inetAddress.isMCGlobal()
							&& !inetAddress.isMCLinkLocal() && !inetAddress.isMCNodeLocal()
							&& !inetAddress.isMCOrgLocal() && !inetAddress.isMCSiteLocal()
							&& !inetAddress.isMulticastAddress()) {
						publicIP = inetAddress.getHostAddress();
					}
				}
			}
			ss.close();
		} catch (IOException e) {
			System.out.println("Error in identifying the public IP of this Connection_Handler.\n");
			e.printStackTrace();
		}
		if (publicIP == "" || publicPort == "") {
			System.out
					.println("Error! Can't resolve the public address for this Connection_Handler.\n");
			return null;
		} else {
			return new Address(publicIP, publicPort);
		}
	}
}

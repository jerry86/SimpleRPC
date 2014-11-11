package server_lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.Address;
import util.ProgramUniqueIdentifier;
import message.ClientToServerMsg;
import message.ServerToMapperMsg;

/**
 * In our implementation of RPC, a "Server Connection Handler" is used to 
 * handle the connections between the server and the clients and/or the Port-
 * Mapper. This idea of design is similar to what we have accomplished in Client 
 * Stub. In this way, we simplify and bring the operations into modules in a way 
 * that can be easily managed and iterated.
 * 
 * In the programming perspective, a server class contains two major functional 
 * blocks: local computation and network transmission. In general, a server first 
 * receives information regarding the program name, return type, parameter types 
 * and values. Then it computes the result out from the input information. At the 
 * end, the result is sent back to the client. Again, this entire sequence of events is 
 * transparent to the clients and should be maintained by the server as well as the 
 * Port_Mapper.
 * 
 * The basic operations that a Server Connection Handler need to implement 
 * include: 

 * (1) {Create a server listener to accept the request sent by client}
 * 
 * (2) {Register a server's supporting procedure to the Port_Mapper}
 *      
 * (3) {Compute the result from the parameters sent by client}
 * 
 * (4) {Send the result back to the client}
 *      What if the message was lost during transferring from server to client?
 * 
 * (5) {Report to Port_Mapper to inform that the server is available again}
 * 
 * (6) {"Heat-beat" acknowledgement to report the server is alive}
 * 
 * 
 * @author Zichuan "Jerry" Ye
 * @version 2014/10/26
 */
public class ServerConnectionHandler {

	public static Socket mapperSocket;
	// public static ArrayList<ProgramUniqueIdentifier> puidList = new ArrayList<>();
	public static ServerSocket listener;
	public static ExecutorService executorService;
	public static Timer timer = new Timer();
	public static Address serverAddress = null;
	public final static int SERVER_REFRESH_TIME = 30; // in unit of seconds

	// local cache
	public static ServerLocalCache cache = new ServerLocalCache(10);

	public static void run(String serverName, ArrayList<ProgramUniqueIdentifier> puidList) {
		// Address serverAddress = null;
		try {
			/*---> (1) set up a server listener <---*/
			listener = new ServerSocket(0); // create a listening port
			executorService = Executors.newCachedThreadPool(); // create a thread pool
			// create a thread pool with fixed threads
			// executorService = Executors.newFixedThreadPool(100);
			String publicIP = getPublicIP();
			String publicPortNumber = String.valueOf(listener.getLocalPort());
			if (publicIP == null || publicPortNumber == null) {
				System.out.println("Failed in creating a listener at server.\n");
				return;
			}
			System.out.println("\nServer{" + publicIP + ":" + publicPortNumber
					+ "} is created successfully! \n");

			// get the server address
			serverAddress = new Address(publicIP, publicPortNumber);

			if (serverAddress != null) {
				/* (2, 6) register to Port_Mapper and "heart-beat" acknowledgement */
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						registerServerToMapper(serverAddress, puidList);
						System.out.println("\"Heart-beat\" acknowledgement timer is working.\n");
					}
				}, 0, SERVER_REFRESH_TIME * 1000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		ClientToServerMsg c2sMsgStatic = null; // yanbing 1025

		while (true) {
			try {
				Socket socket = listener.accept(); // create a socket listener

				Boolean result = executorService.submit(
						new ServerHandlingThread(socket, serverName, puidList, c2sMsgStatic,
								serverAddress)).get();
				if (result == true) {
					System.out.println("Server{" + serverAddress.toString()
							+ "} successfully completed executing an RPC thread.\n");
				} else {
					System.out.println("Exception occurred! Server{" + serverAddress.toString()
							+ "} failed in executing an RPC thread.\n");
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Error! Server{" + serverAddress.toString()
						+ "} failed in executing an RPC.\n");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error! Server{" + serverAddress.toString()
						+ "} failed in I/O executing for listener.accept().\n");
				e.printStackTrace();
			}
		}

	}

	/**
	 * Register a server's supporting procedure to Port_Mapper
	 */
	public static void registerServerToMapper(Address serverAddress,
			ArrayList<ProgramUniqueIdentifier> puidList) {
		/**
		 * (a) read the public directory to find out the address of the mapper
		 */
		try {
			String mapperIP = null;
			int mapperPortNum = -1;
			BufferedReader br = null;
			String sCurrentLine;
			br = new BufferedReader(new FileReader("publicDNS.txt"));
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
			// build a socket that links to Port_Mapper
			if (mapperIP == null || mapperPortNum == -1) {
				System.out.println("Failed in finding Port_Mapper.\n");
			}

			/**
			 * (b) This part handles the connection: server to Port_Mapper
			 */
			mapperSocket = new Socket(mapperIP, mapperPortNum); // connect to Port_Mapper
			System.out.println("Connection to Port_Mapper{" + mapperIP + ", port number: "
					+ mapperPortNum + "} is completed.\n");

			// create an output stream
			ObjectOutputStream serverMapperObjectOutputStream = new ObjectOutputStream(
					mapperSocket.getOutputStream());

			// send a server-to-mapper message to mapper
			ServerToMapperMsg s2mMsg = new ServerToMapperMsg(serverAddress, puidList);

			/* ----> for debugging purposes <--- */
			System.out.println("\nServer{" + s2mMsg.serverAddress.toString()
					+ "} is registering the following procedure call(s):");
			(s2mMsg).supportedProgramList.parallelStream().unordered().forEach(x -> {
				System.out.println("  " + x.toString());
			});
			/* ---> debugging ends <--- */

			serverMapperObjectOutputStream.writeObject(s2mMsg);
			serverMapperObjectOutputStream.flush();

			serverMapperObjectOutputStream.close();
			mapperSocket.close();

		} catch (Exception e) {
			System.out.println("Error in registering the server to the mapper.\n");
			e.printStackTrace();
		}
	}

	/**
	 * (5) Re-register a server's supporting procedure to the mapper once the 
	 *      computation and sending result back to client is completed.
	 */
	public static void reRegisterToMapper(Address serverAddress,
			ProgramUniqueIdentifier reRegisteringPUID) {
		/**
		 * (a) read the public directory to find out the address of the mapper
		 */
		try {
			String mapperIP = null;
			int mapperPortNum = -1;
			BufferedReader br = null;
			String sCurrentLine;
			br = new BufferedReader(new FileReader("publicDNS.txt"));
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
			// build a socket that links to Port_Mapper
			if (mapperIP == null || mapperPortNum == -1) {
				System.out
						.println("Failed in finding the Port_Mapper when re-registering available servers.\n");
			}

			/**
			 * (b) This part handles the connection: server to Port_Mapper
			 */
			mapperSocket = new Socket(mapperIP, mapperPortNum); // connect to port-mapper
			System.out.println("Connection to Port_Mapper{" + mapperIP + ", port number: "
					+ mapperPortNum + "} is completed.\n");

			// create an output stream
			ObjectOutputStream serverMapperObjectOutputStream = new ObjectOutputStream(
					mapperSocket.getOutputStream());

			// send a server-to-mapper message to mapper
			ServerToMapperMsg s2mMsg = new ServerToMapperMsg(serverAddress, reRegisteringPUID);

			/* ----> for debugging purposes <--- */
			System.out.println("\nServer{" + s2mMsg.serverAddress.toString()
					+ "} is re-registering the following procedure call(s):");
			System.out.println("  " + s2mMsg.reRegisteringPUID.toString());
			/* ---> debugging ends <--- */

			serverMapperObjectOutputStream.writeObject(s2mMsg);
			serverMapperObjectOutputStream.flush();

			serverMapperObjectOutputStream.close();
			mapperSocket.close();

		} catch (Exception e) {
			System.out.println("Error in re-registering the server to the mapper.\n");
			e.printStackTrace();
		}
	}

	/**
	 * get the public IP address and the port number of this server
	*/
	public static Address getPublicAddress() {
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

	/**
	 * get the public IP address of this machine 
	*/
	public static String getPublicIP() {
		String publicIP = null;
		Enumeration<NetworkInterface> e;
		try {
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
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return publicIP;
	}
}

package name_server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

import util.Address;
import util.ProgramUniqueIdentifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is the main component of a port-mapper, which is used to 
 * bind the clients with servers that can provide programs/procedure calls
 * according to clients requests.
 * 
 * In principle, the execution sequence of a port-mapper includes 5 steps:
 * 
 * (1) Get the public IP address, port number of the machine where it resides.
 * 
 * (2) Update this address to the public directory, as the public directory 
 *      serves as a mock domain that all servers and clients must first inquire 
 *      before connecting to the mapper.
 * 
 * (3) Establish a listening port that handles requests sent by servers and 
 *      clients. If the request was sent by a server, move to Step (4). Or if 
 *      the request is sent by a client, move to Step (5). It is noted that each 
 *      request is handled in an individual thread which is created by the Port- 
 *      Mapper. This thread will take care of receiving, parsing, server look-up 
 *      and/or responding to a client.
 * 
 * (4) Create a thread in handling the request. The execution carried by this 
 *      thread need to do the following operations: (a) update the server tables 
 *      managed in Port-Mapper; (2) check if such update is successfully 
 *      completed. The executions a thread carries is defined in a class named 
 *      "ServerRegistration".
 * 
 * (5) Also create a thread in handling the request. The execution carried by this 
 *      thread need to do the following operations: (a) find a server that matches 
 *      a client binding request; (b) send the address of the server back to the 
 *      client; (c) update the server tables because one of it is now occupied by a 
 *      client. The execution is defined in a class named "SupportedServerLookUp".
 *      
 * Beside the five basic steps, Port_Mapper also needs to periodically check the 
 * availabilities of servers that are registered. Each server is labeled with its latest 
 * access time to the Port_Mapper. The access time includes all the request and/or 
 * acknowledgement that a server has sent to Port_Mapper. After a certain interval 
 * of time (default is 90 seconds, which is thrice as a server's expected "heart-beat" 
 * acknowledgement time), Port_Mapper removes the servers that have not been 
 * refreshed. Such "time-out" implementation is used to enhance the performance 
 * and reliability of the RPC functionalities. 
 * 
 * @author Zichuan "Jerry" Ye
 * @version 2014/10/26
 */

public class PortMapper {

	static String publicIP, publicPortNumber;
	static ServerSocket listener;
	static ExecutorService executorService;
	static Timer timer = new Timer();
	final static int SERVER_TIME_OUT = 90; // in unit of seconds
	// supported programs under each address (registration)
	static ConcurrentHashMap<Address, ArrayList<ProgramUniqueIdentifier>> addressMap = new ConcurrentHashMap<>();
	// most recent active time of an address (heart-beat validity)
	static ConcurrentHashMap<Address, Date> serverActivityMap = new ConcurrentHashMap<>();
	// which servers support a certain program (server look-up), implemented using an access queue
	static ConcurrentHashMap<ProgramUniqueIdentifier, Queue<Address>> puidQueue = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		try {
			listener = new ServerSocket(0);// create a listening port
			executorService = Executors.newCachedThreadPool(); // create a thread pool
			// create a thread pool with fixed threads
			// executorService = Executors.newFixedThreadPool(100);

			/* (1) get the public address of the PortMapper */
			publicIP = getPublicIP();
			publicPortNumber = String.valueOf(listener.getLocalPort());
			System.out.println("\nPort_Mapper {" + publicIP + ":" + publicPortNumber
					+ "} started successfully! \n");

			/* (2) update this address to the public directory */
			updatePublicDirectory();

			/* Remove entries that remain inactive for more than 90 seconds */
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						removeTimeOutServer();
					} catch (IOException e) {
						System.out.println("Error in removing time-out servers.\n");
						e.printStackTrace();
					}
				}
			}, 0, SERVER_TIME_OUT * 1000);

		} catch (IOException e1) {
			System.out.println("Error in creating a listener for Port_Mapper.\n");
			e1.printStackTrace();
		}
		while (true) { // looping for continuously listening to requests
			try {
				Boolean result = executorService.submit(new PMHandlingThread()).get();
				if (result != true) {
					System.out.println("Port_Mapper failed in handling the request.\n");
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out
						.println("Port_Mapper failed in getting a message or processing the message.\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * get the public IP address of this machine where PortMapper resides
	*/
	public static String getPublicIP() {
		String publicIP = "";
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

	/**
	 * update this address to the public directory
	 */
	public static void updatePublicDirectory() {
		try {
			/* Please CHANGE the file name and address in case needed!! */
			BufferedWriter writer = new BufferedWriter(new FileWriter("publicDNS.txt"));
			writer.write(publicIP + ":" + publicPortNumber);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Error: port-mapper cannot write to the public directory!\n");
			e.printStackTrace();
		}
	}

	/**
	 * Removes the servers that have not being active for a certain amount of time
	 */
	public static void removeTimeOutServer() throws IOException {
		System.out.println("Updating mapping DB.\n");
		Date currentDate = new Date();
		for (Address addr : serverActivityMap.keySet()) {
			Date serverDate = serverActivityMap.get(addr);
			// inactive after 90 seconds
			if ((currentDate.getTime() - serverDate.getTime()) > SERVER_TIME_OUT * 1000) {
				for (ProgramUniqueIdentifier puid : addressMap.get(addr)) {
					// puidMap.remove(puid);
					puidQueue.remove(puid);
				}
				addressMap.remove(addr);
				serverActivityMap.remove(addr);
				System.out.println("Time out! Server{" + addr.toString()
						+ "} is removed from Port_Mapper due to inactivity.\n");
			}
		}
		// update local mapping DB after removing inactive servers
		updateLocalMappingDB();
		System.out.println("Completed updating mapping DB.\n");
	}

	/**
	 * Write current mapping DB to local file
	 */
	public static void updateLocalMappingDB() throws IOException {
		byte[] btArrayAddressMap = serialize(addressMap);
		byte[] btArrayServerActivityMap = serialize(serverActivityMap);
		byte[] btArrayPuidQueue = serialize(puidQueue);
		writeByteArrayToFile(btArrayAddressMap, "AddressMap.sb");
		writeByteArrayToFile(btArrayServerActivityMap, "ServerActivityMap.sb");
		writeByteArrayToFile(btArrayPuidQueue, "PuidQueue.sb");
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}

	public static void writeByteArrayToFile(byte[] bFile, String path) {
		try { // convert array of bytes into file
			FileOutputStream fileOuputStream = new FileOutputStream(path);
			fileOuputStream.write(bFile);
			fileOuputStream.close();
//			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

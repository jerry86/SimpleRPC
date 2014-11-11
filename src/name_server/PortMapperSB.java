package name_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import message.MapperToMapperMsg;
import util.Address;
import util.ProgramUniqueIdentifier;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PortMapperSB {

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
	static String originalMapperIP = null;
	static int originalMapperPortNum = -1;
	// to monitor the original port_mapper
	static Socket orginalPortMapper;
	static final int CHECK_PM_VALID_FREQUENCY = 2; // in unit of seconds

	public static void main(String[] args) {
		/*---> send request to Port_Mapper_#1 <---*/
		try {
			/**
			* (1) read the public directory to find out the address of the mapper
			*/

			BufferedReader br = null;
			String sCurrentLine;
			// ** need to change the directory if debugging using cmd instead of eclipse
			br = new BufferedReader(new FileReader("publicDNS.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] strArray = sCurrentLine.split(":");
				if (strArray[0] != null && strArray[1] != null) {
					// create a socket
					originalMapperIP = strArray[0];
					originalMapperPortNum = Integer.parseInt(strArray[1]);
				}
			}
			if (br != null) {
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * (2) This part handles the connection: client to Port_Mapper
		 */
		// build a socket that links to Port_Mapper
		if (originalMapperIP == null || originalMapperPortNum == -1) {
			System.out.println("Failed in finding the mapper IP or Port number.\nIP: "
					+ originalMapperIP + ", port number: " + originalMapperPortNum + " \n");
			return;
		}

		while (true) { // keep sending request to PM_original to check if it is alive
			try {
				Thread.sleep(CHECK_PM_VALID_FREQUENCY * 1000);
				// connect to port_mapper
				orginalPortMapper = new Socket(originalMapperIP, originalMapperPortNum);

				System.out.println("\nReplicated PM Connection to Port_Mapper{" + originalMapperIP
						+ ":" + originalMapperPortNum + "} is established successfully.\n");

				// create an output stream
				ObjectOutputStream m2mObjectOutputStream = new ObjectOutputStream(
						orginalPortMapper.getOutputStream());
				MapperToMapperMsg m2mMsg = new MapperToMapperMsg();
				// send msg to mapper, just to check if the link remain valid
				m2mObjectOutputStream.writeObject(m2mMsg);
				m2mObjectOutputStream.flush();

			} catch (Exception e) {
				System.out.println("Connection between Port_Mapper and PM_replica failed!\n");
				// e.printStackTrace();
				System.out.println("Replacing the original Port_Mapper\n");
				break;
			}
		}
		/*---> replace old PM with this  PM_replica<---*/
		createNewPortMapper();
	}

	/**
	* All tasks that a port_mapper is supposed to do
	*/
	public static void createNewPortMapper() {
		try {
			listener = new ServerSocket(0);// create a listening port
			executorService = Executors.newCachedThreadPool(); // create a thread pool
			// create a thread pool with fixed threads
			/* (1) get the public address of the PortMapper */
			publicIP = getPublicIP();
			publicPortNumber = String.valueOf(listener.getLocalPort());
			System.out.println("\nPort_Mapper {" + publicIP + ":" + publicPortNumber
					+ "} started successfully! \n");
			/* (2) update this address to the public directory */
			updatePublicDirectory();
			/* Read from the local mapper DB */
			readFromLocalMappingDB();
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
				Boolean result = executorService.submit(new PMReplicaHandlingThread()).get();
				if (result != true) {
					System.out.println("Port_Mapper failed in handling the request.\n");
				}
			} catch (InterruptedException | ExecutionException e1) {
				System.out
						.println("Port_Mapper failed in getting a message or processing the message.\n");
				e1.printStackTrace();
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
	 * Convert an object to a byte[]
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	/**
	 * Convert an object to a byte[]
	 */
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}

	/**
	 * Write current mapping DB to three local files accordingly
	 */
	public static void updateLocalMappingDB() throws IOException {
		byte[] btArrayAddressMap = serialize(addressMap);
		byte[] btArrayServerActivityMap = serialize(serverActivityMap);
		byte[] btArrayPuidQueue = serialize(puidQueue);
		writeByteArrayToFile(btArrayAddressMap, "AddressMap.sb");
		writeByteArrayToFile(btArrayServerActivityMap, "ServerActivityMap.sb");
		writeByteArrayToFile(btArrayPuidQueue, "PuidQueue.sb");
	}

	public static void writeByteArrayToFile(byte[] bFile, String path) {
		try { // convert array of bytes into file
			FileOutputStream fileOuputStream = new FileOutputStream(path);
			fileOuputStream.write(bFile);
			fileOuputStream.close();
			// System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read file from a local MappingDB, which was converted from byte[]
	 */
	public static void readFromLocalMappingDB() {
		try {
			byte[] btArrayAddressMap = Files.readAllBytes(Paths.get("AddressMap.sb"));
			byte[] btArrayServerActivityMap = Files.readAllBytes(Paths.get("ServerActivityMap.sb"));
			byte[] btArrayPuidQueue = Files.readAllBytes(Paths.get("PuidQueue.sb"));
			addressMap = (ConcurrentHashMap<Address, ArrayList<ProgramUniqueIdentifier>>) deserialize(btArrayAddressMap);
			serverActivityMap = (ConcurrentHashMap<Address, Date>) deserialize(btArrayServerActivityMap);
			puidQueue = (ConcurrentHashMap<ProgramUniqueIdentifier, Queue<Address>>) deserialize(btArrayPuidQueue);
			System.out.println("addressMap.size(): " + addressMap.size());
			System.out.println("serverActivityMap.size(): " + serverActivityMap.size());
			System.out.println("puidQueue.size(): " + puidQueue.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
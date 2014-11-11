package server_lib;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import util.Address;
import util.ProgramUniqueIdentifier;
import message.ClientToServerMsg;
import message.ServerToClientMsg;

/**
 * A content that needs to be operated each time a thread is created by 
 * a ServerConnectionHandler.
 * 
 * @author Zichuan
 * @version 2014/10/27
 */
public class ServerHandlingThread implements Callable<Boolean> {
	public String serverName;
	public ArrayList<ProgramUniqueIdentifier> puidList;
	public ClientToServerMsg c2sMsgStatic;
	public Address serverAddress;
	public Socket socket;

	public ServerHandlingThread(Socket socket, String serverName,
			ArrayList<ProgramUniqueIdentifier> puidList, ClientToServerMsg c2sMsgStatic,
			Address serverAddress) {
		super();
		this.socket = socket;
		this.serverName = serverName;
		this.puidList = puidList;
		this.c2sMsgStatic = c2sMsgStatic;
		this.serverAddress = serverAddress;
	}

	public boolean run() {
		System.out.println("\nServer is waiting for requests...\n");
		try {
			InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			Object requestMsg;

			// get an object from the input stream
			requestMsg = objectInputStream.readObject();
			System.out.println("Server received a message!\n");

			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

			/**
			 *  Yanbing's code starts here
			 */
			String name = "server." + serverName; // Please move it into
			// parameter list :)
			Object result = null;

			if (requestMsg instanceof ClientToServerMsg) {
				ClientToServerMsg c2sMsg = (ClientToServerMsg) requestMsg;
				c2sMsgStatic = c2sMsg;

				/*
				 * If the request is procedure_0
				 */
				if (c2sMsg.procedureName.equals("procedure_0")) {
					// (4) send the result back to the client
					ServerToClientMsg s2cMsg = new ServerToClientMsg(c2sMsgStatic.transactionId,
							"", "PROCEDURE_0_RECEIVED", c2sMsgStatic.clientAddress);
					objectOutputStream.writeObject(s2cMsg);
					objectOutputStream.flush();
					System.out.println("Server responsed procedure_0 back to client.\n");
					System.out
							.println("Current server task is completed. Terminating data streams...\n");
					System.out
							.println("Current server task is completed. Terminating data streams...\n");
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
					return true;
				}

				System.out.println("Server received a message from a client{"
						+ c2sMsg.clientAddress.toString() + "}!");
				System.out.println("Server is computing...\n");

				Class<?>[] reflectionParameterType = new Class<?>[c2sMsg.parameterList.size() + 1];
				Object[] reflectionParameter = new Object[c2sMsg.parameterList.size() + 1];
				for (int i = 0; i < c2sMsg.parameterList.size(); i++) {
					String stringType = c2sMsg.parameterTypeList.get(i), stringArray = "[]";
					int dimension = 0, start = 0;
					while (stringType.indexOf(stringArray, start) >= 0
							&& start < stringType.length()) {
						dimension++;
						start = stringType.indexOf(stringArray, start) + stringArray.length();
					}
					if (dimension > 0) {
						String stringTypeBasic = stringType.substring(0,
								stringType.indexOf(stringArray, 0));
						Class<?> type;
						switch (stringTypeBasic) {
						case "double":
							type = double.class;
							break;
						case "int":
							type = int.class;
							break;
						case "long":
							type = long.class;
							break;
						case "short":
							type = short.class;
							break;
						case "float":
							type = float.class;
							break;
						case "boolean":
							type = boolean.class;
							break;
						case "char":
							type = char.class;
							break;
						case "byte":
							type = byte.class;
							break;
						default:
							type = Class.forName(stringTypeBasic);
						}

						int[] dimensionArray = new int[dimension];
						for (int j = 0; j < dimension; j++)
							dimensionArray[j] = 2;
						reflectionParameterType[i] = Array.newInstance(type, dimensionArray)
								.getClass();
					} else {
						Class<?> type;
						String stringTypeBasic = c2sMsg.parameterTypeList.get(i);
						switch (stringTypeBasic) {
						case "double":
							type = double.class;
							break;
						case "int":
							type = int.class;
							break;
						case "long":
							type = long.class;
							break;
						case "short":
							type = short.class;
							break;
						case "float":
							type = float.class;
							break;
						case "boolean":
							type = boolean.class;
							break;
						case "char":
							type = char.class;
							break;
						case "byte":
							type = byte.class;
							break;
						default:
							type = Class.forName(stringTypeBasic);
						}
						reflectionParameterType[i] = type;
					}
					reflectionParameter[i] = c2sMsg.parameterList.get(i);
				}
				reflectionParameterType[c2sMsg.parameterList.size()] = Class.forName("server_lib."
						+ c2sMsg.programName + c2sMsg.version);
				reflectionParameter[c2sMsg.parameterList.size()] = null;
				// Temporary string is referred here
				Class<?> callClass = Class.forName(name);
				Method callMethod = callClass.getMethod(c2sMsg.procedureName,
						reflectionParameterType);
				result = callMethod.invoke(callClass.newInstance(), reflectionParameter);
				System.out.println("Server computing is completed.\n");
			} else {
				System.out
						.println("Error! Server received a message that can not be identified as a client-to-server message.\n");
				return false;
			}
			/**
			 * Yanbing's code ends here
			 */

			// (4) send the result back to the client
			ServerToClientMsg s2cMsg = new ServerToClientMsg(c2sMsgStatic.transactionId, "",
					result, c2sMsgStatic.clientAddress);

			// add the result to local cache
			ServerConnectionHandler.cache.addRecode(s2cMsg.transactionId, s2cMsg);

			// write to OutputStream and flush
			objectOutputStream.writeObject(s2cMsg);
			objectOutputStream.flush();
			System.out.println("Server sends a result message back to client.\n");

			// After everything is completed, close all streams then socket
			System.out.println("Current server task is completed. Terminating data streams...\n");
			objectOutputStream.close();
			objectInputStream.close();
			socket.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Boolean call() throws Exception {
		return run();
	}

}

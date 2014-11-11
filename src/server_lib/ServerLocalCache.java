package server_lib;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import message.ServerToClientMsg;

public class ServerLocalCache implements Serializable {

	ConcurrentHashMap<String, Node> contentMap;
	ConcurrentLinkedQueue<Node> priorityQueue;
	int FIXED_CACHE_SIZE;

	public ServerLocalCache(int fixed_cache_size) {
		FIXED_CACHE_SIZE = fixed_cache_size;
		contentMap = new ConcurrentHashMap<>();
		priorityQueue = new ConcurrentLinkedQueue<>();
	}

	public boolean containsRecord(String transactionId) {
		if (contentMap.containsKey(transactionId)) {
			return true;
		} else {
			return false;
		}
	}

	public ServerToClientMsg getRecord(String transactionId) {
		if (contentMap.containsKey(transactionId)) {
			return contentMap.get(transactionId).record;
		} else {
			return null;
		}
	}

	public void addRecode(String transactionId, ServerToClientMsg record) {
		// check if cache already has the transactionId
		if (contentMap.containsKey(transactionId)) {
			contentMap.remove(transactionId);
			for (Node n : priorityQueue) {
				if (n.transactionId.equals(transactionId)) {
					priorityQueue.remove(n);
				}
			}
		}
		// make sure cache is not over-sized
		if (contentMap.size() >= FIXED_CACHE_SIZE) {
			Node node = priorityQueue.poll();
			contentMap.remove(node.transactionId);
		}
		Node node = new Node(transactionId, record);
		contentMap.put(transactionId, node);
		priorityQueue.add(node);
	}

}

class Node implements Serializable {
	String transactionId;
	ServerToClientMsg record;

	public Node(String transactionId, ServerToClientMsg record) {
		super();
		this.transactionId = transactionId;
		this.record = record;
	}

}

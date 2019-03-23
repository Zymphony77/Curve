package server;

import java.util.*;

import connection.Connection;
import model.*;

import java.net.*;
import java.sql.Timestamp;

import server.*;
import utility.event.*;
import utility.csv.*;

public class ServerLogic {
	private static final ServerLogic INSTANCE = new ServerLogic();
	
	private final String CLIENT_DATA_PATH = "data/client.csv";
	private final String GROUP_DATA_PATH = "data/group.csv";
	private final String GROUP_MESSAGE_DATA_PATH = "data/group_message.csv";
	private final String GROUP_LOG_PATH = "data/group_log.csv";
	
	private HashMap<Integer, Socket> clientSocketMap;
	private HashMap<Integer, ClientData> clientDataMap;
	private HashMap<Integer, GroupData> groupDataMap;
	private HashMap<Integer, GroupMessageData> groupMessageMap;
	private HashMap<Integer, GroupLog> groupLogMap;
	private HashMap<Integer, GroupMemberData> groupMemberMap;
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////  INITIALIZATION  /////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ServerLogic() {
		while (!Server.startInitialization);
		
		System.out.print("Initiating ");
		System.out.print(Server.IS_PRIMARY ? "Primary" : "Secondary");
		System.out.println(" Server...");
		
		clientSocketMap = new HashMap<>();
		
		retrieveClientData();
		retrieveGroupData();
		retrieveGroupMessageData();
		retrieveGroupLog();
		
//		System.out.println(clientDataMap.size());
//		System.out.println(groupDataMap.size());
//		System.out.println(groupLogMap.size());
//		System.out.println(groupMemberMap.size());
//		System.out.println(groupMessageMap.size());
	}
	
	void synchronizeFile(String ipAddress, int port) {
		System.out.println("Synchronizing files with another server...");
		try {
			Socket syncSocket = new Socket(ipAddress, port);
			Connection.sendObject(syncSocket, new SynchronizeEvent());
			
			ServerFileTransferEvent response = 
					(ServerFileTransferEvent) Connection.receiveObject(syncSocket);
			
			CSVHandler.writeCSV(CLIENT_DATA_PATH, response.getClientVector());
			CSVHandler.writeCSV(GROUP_DATA_PATH, response.getGroupVector());
			CSVHandler.writeCSV(GROUP_MESSAGE_DATA_PATH, response.getGroupMessageVector());
			CSVHandler.writeCSV(GROUP_LOG_PATH, response.getGroupLogVector());

			syncSocket.close();
		} catch (Exception e) {
			System.out.println("[Server cannot be reach] " + ipAddress + ":" + port + " is not accessible.");
		}
	}
	
	void retrieveClientData() {
		System.out.println("Retrieving Client Data from the file...");
		clientDataMap = new HashMap<>();
		try {
			for (Vector<Object> person: CSVHandler.readCSV(CLIENT_DATA_PATH)) {
				if (person.size() == 0) {
					continue;
				}
				
				int cid = (Integer) person.elementAt(0);
				String clientName = (String) person.elementAt(1);
				
				clientDataMap.put(cid, new ClientData(cid, clientName));
			}
		} catch (Exception e) {
			System.out.println(e.getClass() + " " + e.getMessage());
			System.out.println(CLIENT_DATA_PATH + " does not exist");
			CSVHandler.createFile(CLIENT_DATA_PATH);
		}
	}
	
	void retrieveGroupData() {
		System.out.println("Retrieving Group Data from the file...");
		groupDataMap = new HashMap<>();
		try {
			for (Vector<Object> group: CSVHandler.readCSV(GROUP_DATA_PATH)) {
				if (group.size() == 0) {
					continue;
				}
				
				int gid = (Integer) group.elementAt(0);
				String groupName = (String) group.elementAt(1);
				
				groupDataMap.put(gid, new GroupData(gid, groupName));
			}
		} catch (Exception e) {
			System.out.println(GROUP_DATA_PATH + " does not exist");
			CSVHandler.createFile(GROUP_DATA_PATH);
		}
	}
	
	void retrieveGroupMessageData() {
		System.out.println("Retrieving Group Message Data from the file...");
		groupMessageMap = new HashMap<>();
		
		for (int gid: groupDataMap.keySet()) {
			if (!groupMessageMap.containsKey(gid)) {
				groupMessageMap.put(gid, new GroupMessageData(gid));
			}
		}
		
		try {
			for (Vector<Object> message: CSVHandler.readCSV(GROUP_MESSAGE_DATA_PATH)) {
				if (message.size() == 0) {
					continue;
				}
				
				int gid = (Integer) message.elementAt(0);
				int cid = (Integer) message.elementAt(1);
				Timestamp time = new Timestamp((long) message.elementAt(2));
				String text = (String) message.elementAt(3);
				
				groupMessageMap.get(gid).addMessage(cid, time, text);
			}
		} catch (Exception e) {
			System.out.println(GROUP_MESSAGE_DATA_PATH + " does not exist");
			CSVHandler.createFile(GROUP_MESSAGE_DATA_PATH);
		}
	}
	
	void retrieveGroupLog() {
		System.out.println("Retrieving Group Log from the file...");
		groupLogMap = new HashMap<>();
		groupMemberMap = new HashMap<>();
		
		for (int gid: groupDataMap.keySet()) {
			if (!groupLogMap.containsKey(gid)) {
				groupLogMap.put(gid, new GroupLog(gid));
				groupMemberMap.put(gid, new GroupMemberData(gid));
			}
		}
		
		try {
			for (Vector<Object> log: CSVHandler.readCSV(GROUP_LOG_PATH)) {
				if (log.size() == 0) {
					continue;
				}
				
				int gid = (Integer) log.elementAt(0);
				int cid = (Integer) log.elementAt(1);
				Timestamp time = new Timestamp((long) log.elementAt(2));
				String event = (String) log.elementAt(3);
				
				if (!groupLogMap.containsKey(gid)) {
					groupLogMap.put(gid, new GroupLog(gid));
					groupMemberMap.put(gid, new GroupMemberData(gid));
				}
				
				groupLogMap.get(gid).addLog(cid, time, event);
				
				if (event.equals("JOIN")) {
					groupMemberMap.get(gid).addMember(cid);
				} else {
					groupMemberMap.get(gid).removeMember(cid);
				}
			}
		} catch (Exception e) {
			System.out.println(GROUP_LOG_PATH + " does not exist");
			CSVHandler.createFile(GROUP_LOG_PATH);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////  REQUEST HANDLING  ////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void waitSynchronization() {
		if (Server.syncThread != null) {
			try {
				Server.syncThread.join();
			} catch (Exception e) {}
		}
	}
	
	public void handleRequest(Socket clientSocket, Event request) {
		waitSynchronization();
		
		System.out.println("Incoming request from " + clientSocket.getInetAddress());
		if (!Server.IS_PRIMARY) {
			if (!(request instanceof ConnectEvent) && 
					!(request instanceof DisconnectEvent) &&
					!(request instanceof GetUpdateEvent) &&
					!(request instanceof SynchronizeEvent) &&
					!(request instanceof NewClientEvent) &&
					!(request instanceof NewGroupEvent) &&
					!(request instanceof NewMessageEvent) &&
					!(request instanceof GroupLogTransferEvent) &&
					!(request instanceof UpdateTransferEvent)) {
				try {
					System.out.println("Forwarding request to Primary Server...");
					forwardRequest(clientSocket, request);
					return;
				} catch (Exception e) {
					System.out.println("Primary server cannot be reached");
				}
			}
		}
		
		if (request instanceof ConnectEvent) {
			createConnection(clientSocket, (ConnectEvent) request);
		} else if (request instanceof DisconnectEvent) {
			removeConnection((DisconnectEvent) request);
		} else if (request instanceof CreateClientEvent) {
			int cid = createClient(clientSocket, (CreateClientEvent) request);
			if (clientSocketMap.get(-1) != clientSocket) { 			// If not forwarded
				createConnection(clientSocket, new ConnectEvent(cid));
			} else {
				System.out.println("Forwarded -- Ignore");
			}
		} else if (request instanceof CreateGroupEvent) {
			updateGroupList(clientSocket, (CreateGroupEvent) request);
		} else if (request instanceof SendMessageEvent) {
			updateMessage((SendMessageEvent) request);
		} else if (request instanceof GetUpdateEvent) {
			updateClient((GetUpdateEvent) request);
		} else if (request instanceof JoinGroupEvent) {
			addMember(clientSocket, (JoinGroupEvent) request);
		} else if (request instanceof LeaveGroupEvent) {
			removeMember(clientSocket, (LeaveGroupEvent) request);
		} else if (request instanceof SynchronizeEvent) {
			sendServerFile(clientSocket);
		} else {
			handleResponse(request);
		}
	}
	
	private void forwardRequest(Socket clientSocket, Event request) throws Exception {
		if (Server.IS_PRIMARY) {
			return;
		}
		
		waitSynchronization();
		
		Socket forwardSocket = new Socket(Server.PRIMARY_IP, Server.PRIMARY_PORT);
		Connection.sendObject(forwardSocket, request);
		
		int cnt = (request instanceof CreateGroupEvent ? 3 : 1);
		
		for (int i = 0; i < cnt; ++i) {
			Event response = (Event) Connection.receiveObject(forwardSocket);
			
			if (response == null) {
				return;
			}
			
			handleResponse(response);
			
			// Only further forward response to client
			if (response instanceof NewClientEvent) {
				Connection.sendObject(clientSocket, response);
				// Connect here -- Special case because clientSocket needs to be known
				createConnection(clientSocket, new ConnectEvent(((NewClientEvent) response).getCid()));
			} else if (response instanceof NewGroupEvent) {
				for (int gid: clientSocketMap.keySet()) {
					Connection.sendObject(clientSocketMap.get(gid), response);
				}
			} else if (response instanceof NewMessageEvent) {
				for (int cid: groupMemberMap.get(((NewMessageEvent) response).getGid()).getCidSet()) {
					if (clientSocketMap.containsKey(cid)) {
						Connection.sendObject(clientSocketMap.get(cid), response);
					}
				}
			} else if (response instanceof UpdateTransferEvent) {
				Connection.sendObject(clientSocket, response);
			} else if (response instanceof GroupLogTransferEvent) {
				Timestamp time = ((GroupLogTransferEvent) response).getTime();
				
				// Do here -- Also special case
				if (((GroupLogTransferEvent) response).getEvent().equals("JOIN")) {
					addMember(clientSocket, time, (JoinGroupEvent) request);
				} else {
					removeMember(clientSocket, time, (LeaveGroupEvent) request);
				}
			}
		}
		
		forwardSocket.close();
		
		System.out.println("Forwarded request successfully executed!");
	}
	
	private void forwardResponse(Event response) {
		if (!Server.IS_PRIMARY) {
			return;
		}
		
		System.out.println("Function: Forward response");
		
		if (clientSocketMap.containsKey(-1)) {
			System.out.println("Forward response");
//			Connection.sendObject(clientSocketMap.get(-1), response);
			Connection.sendObject(Server.connectSocket, response);
		}
	}
	
	private void handleResponse(Event response) {
		if (Server.IS_PRIMARY) {
			return;
		}
		
		System.out.println("Handle response");
		
		waitSynchronization();
		
		if (response instanceof NewClientEvent) {
			int cid = ((NewClientEvent) response).getCid();
			String clientName = ((NewClientEvent) response).getClientName();
			
			clientDataMap.put(cid, new ClientData(cid, clientName));
			
			Vector<Object> update = new Vector<>();
			update.add(cid);
			update.add(clientName);

			CSVHandler.appendLineToCSV(CLIENT_DATA_PATH, update);
		} else if (response instanceof NewGroupEvent) {
			int gid = ((NewGroupEvent) response).getGid();
			String groupName = ((NewGroupEvent) response).getGroupName();
			
			groupDataMap.put(gid, new GroupData(gid, groupName));
			groupMemberMap.put(gid, new GroupMemberData(gid));
			groupMessageMap.put(gid, new GroupMessageData(gid));
			groupLogMap.put(gid, new GroupLog(gid));
			
			Vector<Object> update = new Vector<>();
			update.add(gid);
			update.add(groupName);
			
			CSVHandler.appendLineToCSV(GROUP_DATA_PATH, update);
		} else if (response instanceof NewMessageEvent) {
			int gid = ((NewMessageEvent) response).getGid();
			int cid = ((NewMessageEvent) response).getCid();
			Timestamp time = ((NewMessageEvent) response).getTime();
			String text = ((NewMessageEvent) response).getMessage();
			
			groupMessageMap.get(gid).addMessage(cid, time, text);
			
			Vector<Object> update = new Vector<>();
			update.add(gid);
			update.add(cid);
			update.add(time.getTime());
			update.add(text);
			
			CSVHandler.appendLineToCSV(GROUP_MESSAGE_DATA_PATH, update);
		} else if (response instanceof GroupLogTransferEvent) {
			int gid = ((GroupLogTransferEvent) response).getGid();
			int cid = ((GroupLogTransferEvent) response).getCid();
			Timestamp time = ((GroupLogTransferEvent) response).getTime();
			String event = ((GroupLogTransferEvent) response).getEvent();
			
			groupLogMap.get(gid).addLog(cid, time, event);
			
			if (event.equals("JOIN")) {
				groupMemberMap.get(gid).addMember(cid);
			} else {
				groupMemberMap.get(gid).removeMember(cid);
			}
			
			Vector<Object> update = new Vector<>();
			update.add(gid);
			update.add(cid);
			update.add(time.getTime());
			update.add(event);
			
			CSVHandler.appendLineToCSV(GROUP_LOG_PATH, update);
		}
		
		System.out.println("Handle response done");
	}
	
	private void createConnection(Socket clientSocket, ConnectEvent event) {
		System.out.println("Connected with client #" + event.getCid());
		clientSocketMap.put(event.getCid(), clientSocket);
	}
	
	private void removeConnection(DisconnectEvent event) {
		System.out.println("Disconnected with client #" + event.getCid());
		clientSocketMap.remove(event.getCid());
	}
	
	private int createClient(Socket clientSocket, CreateClientEvent event) {
		int cid = clientDataMap.size() + 1;
		clientDataMap.put(cid, new ClientData(cid, event.getClientName()));
		
		System.out.println("New client being added -> client #" + cid);
		
		Vector<Object> update = new Vector<>();
		update.add(cid);
		update.add(event.getClientName());

		CSVHandler.appendLineToCSV(CLIENT_DATA_PATH, update);
		NewClientEvent response = new NewClientEvent(cid, event.getClientName());
		Connection.sendObject(clientSocket, response);
		
		forwardResponse(response);
		
		System.out.println("Transaction completed");
		
		return cid;
	}
	
	private void updateGroupList(Socket clientSocket, CreateGroupEvent event) {
		int gid = groupDataMap.size() + 1;
		
		groupDataMap.put(gid, new GroupData(gid, event.getGroupName()));
		groupMemberMap.put(gid, new GroupMemberData(gid));
		groupMessageMap.put(gid, new GroupMessageData(gid));
		groupLogMap.put(gid, new GroupLog(gid));
		
		System.out.println("New group being added -> group #" + gid);
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(event.getGroupName());
		
		CSVHandler.appendLineToCSV(GROUP_DATA_PATH, update);
		
		NewGroupEvent response = new NewGroupEvent(gid, event.getGroupName());
		
		for (int client: clientSocketMap.keySet()) {
			if (clientSocketMap.containsKey(client)) {
				Connection.sendObject(clientSocketMap.get(client), response);
			}
		}
		
		forwardResponse(response);
		
		addMember(clientSocket, new JoinGroupEvent(event.getCid(), gid));
		
		System.out.println("Transaction completed");
	}
	
	private void updateMessage(SendMessageEvent event) {
		System.out.println("Incoming new message from client #" + event.getCid() + " @group #" + event.getGid());
		
		int gid = event.getGid();
		int cid = event.getCid();
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		String text = event.getMessage();
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(cid);
		update.add(currentTime.getTime());
		update.add(text);
		
		CSVHandler.appendLineToCSV(GROUP_MESSAGE_DATA_PATH, update);
		
		groupMessageMap.get(gid).addMessage(cid, currentTime, text);
		
		NewMessageEvent response = new NewMessageEvent(gid, cid, clientDataMap.get(cid).getClientName(), 
				currentTime, text);
		
		for (int client: groupMemberMap.get(gid).getCidSet()) {
			if (clientSocketMap.containsKey(client)) {
				System.out.println(">> Broadcast to client #" + client);
				Connection.sendObject(clientSocketMap.get(client), response);
			}
		}
		
		forwardResponse(response);
		
		System.out.println("Transaction completed");
	}
	
	private void updateClient(GetUpdateEvent event) {
		System.out.println("Fetching data for client #" + event.getCid() + "...");
		
		int cid = event.getCid();
		Timestamp latest = event.getLatestTimestamp();
		
		HashMap<Integer, Vector<NewMessageEvent>> unread = new HashMap<>();
		
		for (int gid: groupDataMap.keySet()) {
			
			if (groupMemberMap.get(gid).getCidSet().contains(cid)) {
				unread.put(gid, new Vector<>());
			} else {
				continue;
			}
			
			Vector<NewMessageEvent> unreadMessage = unread.get(gid);
			
			for (GroupMessageData.Message message: groupMessageMap.get(gid).getMessageVector()) {
				if (message.getTime().after(latest)) {
					unreadMessage.add(new NewMessageEvent(gid, message.getCid(),
							clientDataMap.get(cid).getClientName(),
							message.getTime(), message.getText()));
				}
			}
			
			System.out.println("group #" + gid + " DONE");
		}
		
		try {
			Connection.sendObject(clientSocketMap.get(event.getCid()), 
					new UpdateTransferEvent(CSVHandler.readCSV(GROUP_DATA_PATH), unread));
		} catch (Exception e) {}
		
		System.out.println("Transaction completed");
	}
	
	private void addMember(Socket clientSocket, JoinGroupEvent event) {
		addMember(clientSocket, new Timestamp((new Date()).getTime()), event);
	}
	
	private void addMember(Socket clientSocket, Timestamp time, JoinGroupEvent event) {
		int gid = event.getGid();
		int cid = event.getCid();
		
		System.out.println("client #" + cid + " joining group #" + gid);
		
		groupMemberMap.get(gid).addMember(cid);
		groupLogMap.get(gid).addLog(cid, time, "JOIN");
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(cid);
		update.add(time.getTime());
		update.add("JOIN");
		
		CSVHandler.appendLineToCSV(GROUP_LOG_PATH, update);
		
		GroupLogTransferEvent response = new GroupLogTransferEvent(gid, cid, time, "JOIN");
		
		Connection.sendObject(clientSocket, response);
		
		forwardResponse(response);
		
		updateMessage(new SendMessageEvent(cid, gid, "joined the group."));
		
		System.out.println("Transaction completed");
	}
	
	private void removeMember(Socket clientSocket, LeaveGroupEvent event) {
		removeMember(clientSocket, new Timestamp((new Date()).getTime()), event);
	}
	
	private void removeMember(Socket clientSocket, Timestamp time, LeaveGroupEvent event) {
		int gid = event.getGid();
		int cid = event.getCid();
		
		System.out.println("client #" + cid + " leaving group #" + gid);
		
		updateMessage(new SendMessageEvent(cid, gid, "left the group."));
		
		groupMemberMap.get(gid).removeMember(cid);
		groupLogMap.get(gid).addLog(cid, time, "LEAVE");
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(cid);
		update.add(time.getTime());
		update.add("LEAVE");
		
		CSVHandler.appendLineToCSV(GROUP_LOG_PATH, update);
		
		GroupLogTransferEvent response = new GroupLogTransferEvent(gid, cid, time, "LEAVE");
		
		Connection.sendObject(clientSocket, response);
		
		forwardResponse(response);
		
		System.out.println("Transaction completed");
	}
	
	private void sendServerFile(Socket clientSocket) {
		try {
			ServerFileTransferEvent response = new ServerFileTransferEvent(
					CSVHandler.readCSV(CLIENT_DATA_PATH), 
					CSVHandler.readCSV(GROUP_DATA_PATH),  
					CSVHandler.readCSV(GROUP_MESSAGE_DATA_PATH), 
					CSVHandler.readCSV(GROUP_LOG_PATH));
			
			Connection.sendObject(clientSocket, response);
		} catch (Exception e) {}
		
		System.out.println("Transaction completed");
	}
	
	public static ServerLogic getInstance() {
		return INSTANCE;
	}
}

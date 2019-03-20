package server;

import java.util.*;

import connection.Connection;
import model.*;

import java.net.*;
import java.sql.Timestamp;

import utility.event.*;
import utility.csv.*;

public class ServerLogic {
	private static final ServerLogic INSTANCE = new ServerLogic();
	
	private static final String PRIMARY_IP = "";
	private static final int PRIMARY_PORT = 1234;
	private static final String SECONDARY_IP = "";
	private static final int SECONDARY_PORT = 1234;
	private static boolean IS_PRIMARY = true;
	
	private final String CLIENT_DATA_PATH = "data/client.csv";
	private final String GROUP_DATA_PATH = "data/group.csv";
	private final String GROUP_MESSAGE_DATA_PATH = "data/group_message.csv";
	private final String GROUP_LOG_PATH = "data/group_log.csv";
	
	private ServerSocket serverSocket;
	
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
		System.out.print("Initiating ");
		System.out.print(IS_PRIMARY ? "Primary" : "Secondary");
		System.out.println(" Server...");
		
		clientSocketMap = new HashMap<>();
		
		if (IS_PRIMARY) {
			synchronizeFile(SECONDARY_IP, SECONDARY_PORT);
		} else {
			synchronizeFile(PRIMARY_IP, PRIMARY_PORT);
		}
		
		retrieveClientData();
		retrieveGroupData();
		retrieveGroupMessageData();
		retrieveGroupLog();
	}
	
	private void synchronizeFile(String ipAddress, int port) {
		System.out.println("Synchronizing files with another server...");
		try {
			Socket clientServerSocket = new Socket(ipAddress, port);
			
			ServerFileTransferEvent response = 
					(ServerFileTransferEvent) Connection.receiveObject(clientServerSocket);
			
			CSVHandler.writeCSV(CLIENT_DATA_PATH, response.getClientVector());
			CSVHandler.writeCSV(GROUP_DATA_PATH, response.getGroupVector());
			CSVHandler.writeCSV(GROUP_MESSAGE_DATA_PATH, response.getGroupMessageVector());
			CSVHandler.writeCSV(GROUP_LOG_PATH, response.getGroupLogVector());

			clientServerSocket.close();
		} catch (Exception e) {
			System.out.println("[Server cannot be reach] " + ipAddress + ":" + port + " is not accessible.");
		}
	}
	
	private void retrieveClientData() {
		System.out.println("Retrieving Client Data from the file...");
		clientDataMap = new HashMap<>();
		try {
			for (Vector<Object> person: CSVHandler.readCSV(CLIENT_DATA_PATH)) {
				int cid = (Integer) person.elementAt(0);
				String clientName = (String) person.elementAt(1);
				
				clientDataMap.put(cid, new ClientData(cid, clientName));
			}
		} catch (Exception e) {
			CSVHandler.writeCSV(CLIENT_DATA_PATH, new Vector<>());
		}
	}
	
	private void retrieveGroupData() {
		System.out.println("Retrieving Group Data from the file...");
		groupDataMap = new HashMap<>();
		try {
			for (Vector<Object> group: CSVHandler.readCSV(GROUP_DATA_PATH)) {
				int gid = (Integer) group.elementAt(0);
				String groupName = (String) group.elementAt(1);
				
				groupDataMap.put(gid, new GroupData(gid, groupName));
			}
		} catch (Exception e) {
			CSVHandler.writeCSV(GROUP_DATA_PATH, new Vector<Vector<Object>>());
		}
	}
	
	private void retrieveGroupMessageData() {
		System.out.println("Retrieving Group Message Data from the file...");
		groupMessageMap = new HashMap<>();
		try {
			for (Vector<Object> message: CSVHandler.readCSV(GROUP_MESSAGE_DATA_PATH)) {
				int gid = (Integer) message.elementAt(0);
				int cid = (Integer) message.elementAt(1);
				Timestamp time = new Timestamp((long) message.elementAt(2));
				String text = (String) message.elementAt(3);
				
				if (!groupMessageMap.containsKey(gid)) {
					groupMessageMap.put(gid, new GroupMessageData(gid));
				}
				
				groupMessageMap.get(gid).addMessage(cid, time, text);
			}
		} catch (Exception e) {
			CSVHandler.writeCSV(GROUP_MESSAGE_DATA_PATH, new Vector<Vector<Object>>());
		}
	}
	
	private void retrieveGroupLog() {
		System.out.println("Retrieving Group Log from the file...");
		groupLogMap = new HashMap<>();
		groupMemberMap = new HashMap<>();
		try {
			for (Vector<Object> log: CSVHandler.readCSV(GROUP_LOG_PATH)) {
				int gid = (Integer) log.elementAt(0);
				int cid = (Integer) log.elementAt(1);
				Timestamp time = new Timestamp((long) log.elementAt(2));
				String event = (String) log.elementAt(3);
				
				if (!groupLogMap.containsKey(gid)) {
					groupLogMap.put(gid, new GroupLog(gid));
					groupMemberMap.put(gid, new GroupMemberData(gid));
				}
				
				groupLogMap.get(gid).addLog(cid, time, event);
				
				if (event == "JOIN") {
					groupMemberMap.get(gid).addMember(cid);
				} else {
					groupMemberMap.get(gid).removeMember(cid);
				}
			}
		} catch (Exception e) {
			CSVHandler.writeCSV(GROUP_LOG_PATH, new Vector<Vector<Object>>());
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////  REQUEST HANDLING  ////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void handleRequest(Socket clientSocket, Event request) {
		System.out.println("Incoming request from " + clientSocket.getInetAddress());
		if (!IS_PRIMARY) {
			if (!(request instanceof ConnectEvent) && 
					!(request instanceof DisconnectEvent) &&
					!(request instanceof CreateClientEvent) &&
					!(request instanceof GetUpdateEvent)) {
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
			createConnection(clientSocket, new ConnectEvent(cid));
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
		}
	}
	
	private void forwardRequest(Socket clientSocket, Event request) throws Exception {
		Socket secondarySocket = new Socket(PRIMARY_IP, PRIMARY_PORT);
		
		Connection.sendObject(secondarySocket, request);
		
		Event response = (Event) Connection.receiveObject(secondarySocket);
		
		if (response instanceof NewClientEvent) {
			Connection.sendObject(clientSocket, response);
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
			
			if (((GroupLogTransferEvent) response).getEvent() == "JOIN") {
				addMember(clientSocket, time, (JoinGroupEvent) request);
			} else {
				removeMember(clientSocket, time, (LeaveGroupEvent) request);
			}
		}
		
		secondarySocket.close();
		
		System.out.println("Forwarded request successfully executed!");
	}
	
	private void createConnection(Socket clientSocket, ConnectEvent event) {
		System.out.println("Connected with " + event.getCid());
		clientSocketMap.put(event.getCid(), clientSocket);
	}
	
	private void removeConnection(DisconnectEvent event) {
		System.out.println("Disconnected with " + event.getCid());
		clientSocketMap.remove(event.getCid());
	}
	
	private int createClient(Socket clientSocket, CreateClientEvent event) {
		int cid = clientDataMap.size();
		clientDataMap.put(cid, new ClientData(cid, event.getClientName()));
		
		System.out.println("New client being added -> " + cid);
		
		Vector<Object> update = new Vector<>();
		update.add(cid);
		update.add(event.getClientName());
		
		CSVHandler.appendLineToCSV(CLIENT_DATA_PATH, update);
		
		NewClientEvent response = new NewClientEvent(cid, event.getClientName());
		Connection.sendObject(clientSocketMap.get(cid), response);
		
		return cid;
	}
	
	private void updateGroupList(Socket clientSocket, CreateGroupEvent event) {
		int gid = groupDataMap.size();
		groupDataMap.put(gid, new GroupData(gid, event.getGroupName()));
		
		System.out.println("New group being added -> " + gid);
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(event.getGroupName());
		
		CSVHandler.appendLineToCSV(GROUP_DATA_PATH, update);
		
		addMember(clientSocket, new JoinGroupEvent(event.getCid(), gid));
		
		NewGroupEvent response = new NewGroupEvent(gid, event.getGroupName());
		
		for (int client: clientSocketMap.keySet()) {
			if (clientSocketMap.containsKey(client)) {
				Connection.sendObject(clientSocketMap.get(client), response);
			}
		}
	}
	
	private void updateMessage(SendMessageEvent event) {
		System.out.println("Incoming new message from " + event.getCid() + "@" + event.getGid() + "...");
		int gid = event.getGid();
		int cid = event.getCid();
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		String text = event.getMessage();
		
		groupMessageMap.get(gid).addMessage(cid, currentTime, text);
		
		NewMessageEvent response = new NewMessageEvent(gid, cid, clientDataMap.get(cid).getClientName(), 
				currentTime, text);
		
		for (int client: groupMemberMap.get(gid).getCidSet()) {
			if (clientSocketMap.containsKey(client)) {
				Connection.sendObject(clientSocketMap.get(client), response);
			}
		}
	}
	
	private void updateClient(GetUpdateEvent event) {
		System.out.println("Fetching data for " + event.getCid() + "...");
		Timestamp latest = event.getLatestTimestamp();
		HashMap<Integer, GroupMessageData> unread = new HashMap<>();
		
		for (int gid: groupDataMap.keySet()) {
			if (groupDataMap.containsKey(event.getCid())) {
				unread.put(gid, new GroupMessageData(gid));
			} else {
				continue;
			}
			
			GroupMessageData unreadMessage = unread.get(gid);
			
			for (GroupMessageData.Message message: groupMessageMap.get(gid).getMessageVector()) {
				if (message.getTime().after(latest)) {
					unreadMessage.addMessage(message.getCid(), message.getTime(), message.getText());
				}
			}
		}
		
		try {
			Connection.sendObject(clientSocketMap.get(event.getCid()), 
					new UpdateTransferEvent(CSVHandler.readCSV(GROUP_DATA_PATH), unread));
		} catch (Exception e) {}
	}
	
	private void addMember(Socket clientSocket, JoinGroupEvent event) {
		addMember(clientSocket, new Timestamp((new Date()).getTime()), event);
	}
	
	private void addMember(Socket clientSocket, Timestamp time, JoinGroupEvent event) {
		int gid = event.getGid();
		int cid = event.getCid();
		
		System.out.println(cid + " joining " + gid + "...");
		
		groupLogMap.get(gid).addLog(cid, time, "JOIN");
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(cid);
		update.add(time.getTime());
		update.add("JOIN");
		
		CSVHandler.appendLineToCSV(GROUP_LOG_PATH, update);
		
		Connection.sendObject(clientSocket, new GroupLogTransferEvent(gid, cid, time, "JOIN"));
	}
	
	private void removeMember(Socket clientSocket, LeaveGroupEvent event) {
		removeMember(clientSocket, new Timestamp((new Date()).getTime()), event);
	}
	
	private void removeMember(Socket clientSocket, Timestamp time, LeaveGroupEvent event) {
		int gid = event.getGid();
		int cid = event.getCid();
		
		System.out.println(cid + " leaving " + gid + "...");
		
		groupLogMap.get(gid).addLog(cid, time, "LEAVE");
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(cid);
		update.add(time.getTime());
		update.add("LEAVE");
		
		CSVHandler.appendLineToCSV(GROUP_LOG_PATH, update);
		
		Connection.sendObject(clientSocket, new GroupLogTransferEvent(gid, cid, time, "LEAVE"));
	}
	
	public boolean isPrimary() {
		return IS_PRIMARY;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public static String getPrimaryServerIp() {
		return PRIMARY_IP;
	}
	
	public static int getPrimaryPort() {
		return PRIMARY_PORT;
	}
	
	public static String getSecondaryServerIp() {
		return SECONDARY_IP;
	}
	
	public static int getSecondaryPort() {
		return SECONDARY_PORT;
	}
	
	public static ServerLogic getInstance() {
		return INSTANCE;
	}
}

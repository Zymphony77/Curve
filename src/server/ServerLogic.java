package server;

import java.util.*;

import connection.Connection;

import java.net.*;
import java.sql.Timestamp;

import utility.event.*;
import utility.csv.*;

public class ServerLogic {
	private static final ServerLogic INSTANCE = new ServerLogic();
	
	private static final String PRIMARY_IP = "";
	private static final String SECONDARY_IP = "";
	private static final int SERVER_PORT = 1234;
	private static final boolean IS_PRIMARY = false;
	
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
	
	private ServerLogic() {
		clientSocketMap = new HashMap<>();
		
		if (IS_PRIMARY) {
			synchronizeFile(SECONDARY_IP, PRIMARY_IP);
		} else {
			synchronizeFile(PRIMARY_IP, SECONDARY_IP);
		}
		
		retrieveClientData();
		retrieveGroupData();
		retrieveGroupMessageData();
		retrieveGroupLog();
	}
	
	private void synchronizeFile(String sourceIp, String destinationIp) {
		// Transfer file from sourceIp to destinationIp
		// Possibly merge with FileTransferEvent
	}
	
	private void retrieveClientData() {
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
	
	public void handleRequest(Socket clientSocket, Event request) {
		if (!IS_PRIMARY) {
			if (!(request instanceof ConnectEvent) && !(request instanceof DisconnectEvent)) {
				try {
					// Send to Primary Server
					// Acts as a client with server identity -- cid = -1
					return;
				} catch (Exception e) {}
			}
		}
		
		if (request instanceof ConnectEvent) {
			createConnection(clientSocket, (ConnectEvent) request);
		} else if (request instanceof DisconnectEvent) {
			removeConnection((DisconnectEvent) request);
		} else if (request instanceof CreateClientEvent) {
			updateClient(clientSocket, (CreateClientEvent) request);
		} else if (request instanceof CreateGroupEvent) {
			updateGroupList((CreateGroupEvent) request);
		} else if (request instanceof SendMessageEvent) {
			updateMessage((SendMessageEvent) request);
		} else if (request instanceof GetUnreadMessageEvent) {
			updateUnreadMessage((GetUnreadMessageEvent) request);
		} else if (request instanceof JoinGroupEvent) {
			addMember((JoinGroupEvent) request);
		} else if (request instanceof LeaveGroupEvent) {
			removeMember((LeaveGroupEvent) request);
		} else if (request instanceof ServerUpdateEvent) {
			updateData((ServerUpdateEvent) request);
		}
	}
	
	private void createConnection(Socket clientSocket, ConnectEvent event) {
		// Check server identity -- cid = -1
		clientSocketMap.put(event.getCid(), clientSocket);
	}
	
	private void removeConnection(DisconnectEvent event) {
		// Check server identity -- cid = -1
		clientSocketMap.remove(event.getCid());
	}
	
	private void updateClient(Socket clientSocket, CreateClientEvent event) {
		int cid = clientDataMap.size();
		clientDataMap.put(cid, new ClientData(cid, event.getClientName()));
		
		Vector<Object> update = new Vector<>();
		update.add(cid);
		update.add(event.getClientName());
		
		CSVHandler.appendLineToCSV(CLIENT_DATA_PATH, update);
		
		createConnection(clientSocket, new ConnectEvent(cid));
		
		NewClientEvent response = new NewClientEvent(cid, event.getClientName());
		Connection.sendObject(clientSocketMap.get(cid), response);
	}
	
	private void updateGroupList(CreateGroupEvent event) {
		int gid = groupDataMap.size();
		groupDataMap.put(gid, new GroupData(gid, event.getGroupName()));
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(event.getGroupName());
		
		CSVHandler.appendLineToCSV(GROUP_DATA_PATH, update);
		
		addMember(new JoinGroupEvent(event.getCid(), gid));
		
		NewGroupEvent response = new NewGroupEvent(gid, event.getGroupName());
		for (int client: clientSocketMap.keySet()) {
			Connection.sendObject(clientSocketMap.get(client), response);
		}
	}
	
	private void updateMessage(SendMessageEvent event) {
		int gid = event.getGid();
		int cid = event.getCid();
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		String text = event.getMessage();
		
		groupMessageMap.get(gid).addMessage(cid, currentTime, text);
		
		NewMessageEvent response = new NewMessageEvent(gid, cid, clientDataMap.get(cid).getClientName(), 
				currentTime, text);
		for (int client: groupMemberMap.get(gid).getCidSet()) {
			Connection.sendObject(clientSocketMap.get(client), response);
		}
	}
	
	private void updateUnreadMessage(GetUnreadMessageEvent event) {
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
		
		// Send unread back
		// *NEW TYPE* of response -- ResponseUnreadMessageEvent
	}
	
	private void addMember(JoinGroupEvent event) {
		int gid = event.getGid();
		int cid = event.getCid();
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		
		groupLogMap.get(gid).addLog(cid, currentTime, "JOIN");
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(cid);
		update.add(currentTime.getTime());
		update.add("JOIN");
		
		CSVHandler.appendLineToCSV(GROUP_LOG_PATH, update);
	}
	
	private void removeMember(LeaveGroupEvent event) {
		int gid = event.getGid();
		int cid = event.getCid();
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		
		groupLogMap.get(gid).addLog(cid, currentTime, "LEAVE");
		
		Vector<Object> update = new Vector<>();
		update.add(gid);
		update.add(cid);
		update.add(currentTime.getTime());
		update.add("LEAVE");
		
		CSVHandler.appendLineToCSV(GROUP_LOG_PATH, update);
	}
	
	private void updateData(ServerUpdateEvent event) {
		// *REMOVE* ServerUpdateEvent -- too rough
		// *NEW TYPE* of response -- FileTransferEvent
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public static String getPrimaryServerIp() {
		return PRIMARY_IP;
	}
	
	public static String getSecondaryServerIp() {
		return SECONDARY_IP;
	}
	
	public static int getServerPort() {
		return SERVER_PORT;
	}
	
	public static ServerLogic getInstance() {
		return INSTANCE;
	}
}

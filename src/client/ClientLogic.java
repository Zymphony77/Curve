package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;

import utility.csv.CSVHandler;
import utility.event.ConnectEvent;
import utility.event.CreateClientEvent;
import utility.event.CreateGroupEvent;
import utility.event.DisconnectEvent;
import utility.event.Event;
import utility.event.GetUpdateEvent;
import utility.event.GroupLogTransferEvent;
import utility.event.JoinGroupEvent;
import utility.event.LeaveGroupEvent;
import utility.event.NewClientEvent;
import utility.event.NewGroupEvent;
import utility.event.NewMessageEvent;
import utility.event.SendMessageEvent;
import utility.event.UpdateTransferEvent;
import connection.Connection;
import model.GroupMessageData;
import model.GroupMessageData.Message;
import server.*;

public class ClientLogic {
	private final static ClientLogic instance = new ClientLogic();
	private static Socket socket;
	private final static String PRIMARY_SERVER_IP="localhost";
	private final static int PRIMARY_SERVER_PORT=1234;
	private final static String SECONDARY_SERVER_IP="localhost";
	private final static int SECONDARY_SERVER_PORT=1234;
	
	public ClientLogic() {
		socket = Connection.connectToServer(Server.getServerIp(), Server.getServerPort());
	}

	public static ClientLogic getInstance() {
		return instance;
	}

	// Handle receivedObj
	public static void handleReceivedObj(Event receivedObj) {

		if (receivedObj instanceof NewClientEvent) {
			newClient((NewClientEvent) receivedObj);
		} else if (receivedObj instanceof NewGroupEvent) {
			newGroup((NewGroupEvent) receivedObj);
		} else if (receivedObj instanceof NewMessageEvent) {
			newMessage((NewMessageEvent) receivedObj);
		} else if (receivedObj instanceof UpdateTransferEvent) {
			updateTransfer((UpdateTransferEvent) receivedObj);
		}
	}

	// Create Client
	// send
	public static void createClient(String clientName) {
		CreateClientEvent createClient = new CreateClientEvent(clientName);
		Connection.sendObject(socket, createClient);

	}

	// receive
	public static void newClient(NewClientEvent newClient) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> client = new Vector<Object>(newClient.getCid());
		client.addElement(newClient.getClientName());
		
		String ipAddress = "cannot work";
		client.addElement(ipAddress);
		data.add(client);
		CSVHandler.writeCSV("Client.csv", data);

		// TODO add function to tell UI
	}

	// Connecting
	// send
	public static void connect(int cid, String ipAddress) {
		ConnectEvent connect = new ConnectEvent(cid);
		Connection.sendObject(socket, connect);

	}

	// receive
	public static void disconnect(int cid) {
		DisconnectEvent disconnect = new DisconnectEvent(cid);
		Connection.sendObject(socket, disconnect);

	}

	// Group Event
	// send
	public static void createGroup(int cid, String groupName) throws FileNotFoundException {
		CreateGroupEvent createGroup = new CreateGroupEvent(cid, groupName);
		Connection.sendObject(socket, createGroup);

		// if successful, write
		Vector<Vector<Object>> data = CSVHandler.readCSV("GroupLst.csv");
		int gid = 0;
		for (int i = 0; i < data.size(); i++) {
			if ((String) data.get(i).get(1) == groupName) {
				gid = (int) data.get(i).get(0);
				break;
			}
		}
		data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(gid);
		group.addElement(groupName);
		data.add(group);
		String fileName = "GroupOf" + cid + ".csv";
		CSVHandler.appendToCSV(fileName, data);

		// TODO add function to tell UI
	}

	// send
	public static void join(int cid, int gid) {
		JoinGroupEvent joinGroup = new JoinGroupEvent(cid, gid);
		Connection.sendObject(socket, joinGroup);

		// if successful, write
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(gid);
		data.add(group);
		String fileName = "GroupOf" + cid + ".csv";
		CSVHandler.appendToCSV(fileName, data);

		// TODO add function to tell UI
	}

	// send
	public static void leave(int cid, int gid) {
		LeaveGroupEvent leaveGroup = new LeaveGroupEvent(cid, gid);
		Connection.sendObject(socket, leaveGroup);
	}

	// receive
	public static void newGroup(NewGroupEvent newGroup) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>(newGroup.getGid());
		group.addElement(newGroup.getGroupName());
		data.add(group);
		CSVHandler.appendToCSV("GroupList.csv", data);

		// TODO add function to tell UI
	}

	// Message Event
	// send
	public static void getUnreadMesaage(int cid, Timestamp lastestTimestamp) {
		GetUpdateEvent getUnreadMessage = new GetUpdateEvent(cid, lastestTimestamp);
		Connection.sendObject(socket, getUnreadMessage);
	}

	// send
	public static void sendMessage(int cid, int gid, String Message) {
		SendMessageEvent sendMessageEvent = new SendMessageEvent(cid, gid, Message);
		Connection.sendObject(socket, sendMessageEvent);
	}

	// receive
	public static void newMessage(NewMessageEvent newMessage) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> message = new Vector<Object>();
		message.addElement(newMessage.getClientName());
		message.addElement(newMessage.getTime());
		message.addElement(newMessage.getMessage());
		data.add(message);

		String fileName = "MessageListOf" + newMessage.getGid() + ".csv";
		CSVHandler.appendToCSV(fileName, data);

		// TODO add function to tell UI
	}

	// Update Event
	public static void updateTransfer(UpdateTransferEvent updateTransfer) {
		Vector<Vector<Object>> groupData = updateTransfer.getGroupData(); // ������ group Ẻ� csv
		HashMap<Integer, GroupMessageData> unread = updateTransfer.getUnread(); //

		CSVHandler.appendToCSV("GroupList.csv", groupData);

		for (Integer i : unread.keySet()) {
			GroupMessageData groupMessageData = unread.get(i);
			int gid = groupMessageData.getGid();
			Vector<Message> messageVector = groupMessageData.getMessageVector();

			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			for (Message m : messageVector) {
				Vector<Object> message = new Vector<Object>();
				message.addElement(m.getCid());
				message.addElement(m.getTime());
				message.addElement(m.getText());
				data.add(message);
			}
			String fileName = "MessageListOf" + gid + ".csv";
			CSVHandler.appendToCSV(fileName, data);
		}

		// TODO add function to tell UI
	}

	public static void groupLogTransfer(GroupLogTransferEvent groupLogTransfer) {
		int gid = groupLogTransfer.getGid();
		int cid = groupLogTransfer.getCid();
		Timestamp time = groupLogTransfer.getTime();
		String event = groupLogTransfer.getEvent();

		// TODO add function to tell UI
	}

	public static void notifyUI(Event event) {

		// TODO
	}

	public Socket getSocket() {
		return socket;
	}
}

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
	private final static String PRIMARY_SERVER_IP = Server.PRIMARY_IP;
	private final static int PRIMARY_PORT = Server.PRIMARY_PORT;
	private final static String SECONDARY_SERVER_IP = Server.SECONDARY_IP;
	private final static int SECONDARY_PORT = Server.SECONDARY_PORT;

	public ClientLogic() {
		socket = Connection.connectToServer(PRIMARY_SERVER_IP, PRIMARY_PORT);
		// TODO Po: auto reconnection
	}

	public static ClientLogic getInstance() {
		return instance;
	}

	// Create Client
	// send
	public static void createClient(String clientName) {
		CreateClientEvent createClient = new CreateClientEvent(clientName);
		Connection.sendObject(socket, createClient);

	}

	// receive
	public static Vector<Object> newClient(NewClientEvent newClient) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		Vector<Object> client = new Vector<Object>(newClient.getCid());
		client.addElement(newClient.getClientName());

		// String ipAddress = "cannot work";
		// client.addElement(ipAddress);
		data.add(client);
		CSVHandler.writeCSV("Client.csv", data);

		return client;

		// TODO add function to tell UI
//		UILogic.addNewClient(newClient.getCid(), newClient.getClientName()); // temporary name
//		return true;
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
	public static Vector<Object> createGroup(int cid, String groupName) throws FileNotFoundException {
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
		return group;
	}

	// receive
	public static Vector<Object> newGroup(NewGroupEvent newGroup) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>(newGroup.getGid());
		group.addElement(newGroup.getGroupName());
		data.add(group);
		CSVHandler.appendToCSV("GroupList.csv", data);

		// TODO add function to tell UI
//		UILogic.addNewGroup(gid, groupName);
		return group;
	}

	// send
	public static Vector<Object> join(int cid, int gid) {
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
//		UILogic.addJoinGroup(cid, gid);
		return group;
	}

	// send
	public static Vector<Object> leave(int cid, int gid) {
		LeaveGroupEvent leaveGroup = new LeaveGroupEvent(cid, gid);
		Connection.sendObject(socket, leaveGroup);
		
		Vector<Vector<Object>> dataNew = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(gid);
		String fileName = "GroupOf" + cid + ".csv";

		Vector<Vector<Object>> dataOld = CSVHandler.readCSV(fileName);
		for(Vector<Object> line: dataOld) {
			if (line!=group) {
				dataNew.add(line);
			}
		}
		CSVHandler.writeCSV(fileName, dataNew);
		return group;
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
	public static Vector<Object> newMessage(NewMessageEvent newMessage) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> message = new Vector<Object>();
		message.addElement(newMessage.getClientName());
		message.addElement(newMessage.getTime());
		message.addElement(newMessage.getMessage());
		data.add(message);

		String fileName = "MessageListOf" + newMessage.getGid() + ".csv";
		CSVHandler.appendToCSV(fileName, data);

		// TODO add function to tell UI
//		UILogic.addNewMessage(newMessage.getGid(), newMessage.getCid(), newMessage.getClientName(),
//				newMessage.getTime(), newMessage.getMessage());
		return message;

	}

	// Update Event
	public static void updateTransfer(UpdateTransferEvent updateTransfer) {
		Vector<Vector<Object>> groupData = updateTransfer.getGroupData();
		HashMap<Integer, Vector<NewMessageEvent>> unread = updateTransfer.getUnread();

		CSVHandler.writeCSV("GroupList.csv", groupData);

		for (int i : unread.keySet()) {
			for (NewMessageEvent m : unread.get(i)) {
				int gid = m.getGid();
				int cid = m.getCid();
				String ClientName = m.getClientName();
				Timestamp time = m.getTime();
				String text = m.getMessage();

				Vector<Vector<Object>> data = new Vector<Vector<Object>>();
				Vector<Object> message = new Vector<Object>();

				message.addElement(ClientName);
				message.addElement(time);
				message.addElement(text);
				data.add(message);
				String fileName = "MessageListOf" + gid + ".csv";
				CSVHandler.appendToCSV(fileName, data);

//				UILogic.addNewMessage(gid, cid, ClientName, time, text);
			}
		}
//		return true;
	}

	public Socket getSocket() {
		return socket;
	}
}

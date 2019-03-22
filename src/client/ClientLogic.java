package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
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
	private int cid;
	private int lastCreatedGid;
	private final static String FILEPATH = "src/data/";

	public ClientLogic() {
		reconnect();
	}
	
	public void reconnect() {
		socket = null;
		while (socket == null) {
			System.out.println("reconnecting");
			socket = Connection.connectToServer(Server.PRIMARY_IP, Server.PRIMARY_PORT);
			if (socket == null)
				socket = Connection.connectToServer(Server.SECONDARY_IP, Server.SECONDARY_PORT);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
		}
		System.out.println("connected");
	}

	public static ClientLogic getInstance() {
		return instance;
	}

	// Client Creation Send
	public void createClient(String clientName) {
		CreateClientEvent createClient = new CreateClientEvent(clientName);
		Connection.sendObject(socket, createClient);
	}

	// Client Creation Receive
	public Vector<Object> newClient(NewClientEvent newClient) {
		instance.cid = newClient.getCid();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> client = new Vector<Object>();
		client.addElement(newClient.getCid());
		client.addElement(newClient.getClientName());
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		client.addElement((long) currentTime.getTime());
		data.add(client);
		CSVHandler.writeCSV(FILEPATH + "ClientInfo.csv", data);

		return client;
	}

	// Connection Send
	public void connect(int cid, String ipAddress) {
		ConnectEvent connect = new ConnectEvent(cid);
		Connection.sendObject(socket, connect);
	}

	// Disconnection Send
	public void disconnect(int cid) {
		DisconnectEvent disconnect = new DisconnectEvent(cid);
		Connection.sendObject(socket, disconnect);
	}

	// Group Creation Send
	public Vector<Object> createGroup(int cid, String groupName) throws FileNotFoundException {
		CreateGroupEvent createGroup = new CreateGroupEvent(cid, groupName);
		instance.lastCreatedGid = 0;
		Connection.sendObject(socket, createGroup); // FOR TEST
		while(instance.lastCreatedGid == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		Vector<Vector<Object>> data = CSVHandler.readCSV(FILEPATH + "GroupLst.csv");
		data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(instance.lastCreatedGid);
		group.addElement(groupName);
		data.add(group);
		String fileName = FILEPATH + "GroupOf" + cid + ".csv";
		CSVHandler.appendToCSV(fileName, data);

		return group;
	}

	// Group Creation Receive
	public Vector<Object> newGroup(NewGroupEvent newGroup) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(newGroup.getGid());
		group.addElement(newGroup.getGroupName());
		data.add(group);
		CSVHandler.appendToCSV(FILEPATH + "GroupLst.csv", data);
		instance.lastCreatedGid = newGroup.getGid();
		return group;
	}

	// Client Join Group Send
	public Vector<Object> join(int cid, int gid) throws FileNotFoundException {
		JoinGroupEvent joinGroup = new JoinGroupEvent(cid, gid);
		Connection.sendObject(socket, joinGroup); // FOR TEST

		Vector<Vector<Object>> data = CSVHandler.readCSV(FILEPATH + "GroupLst.csv");
		String groupName = "";
		for (int i = 0; i < data.size(); i++) {
			if ((int) data.get(i).get(0) == gid) {
				groupName = (String) data.get(i).get(1);
				break;
			}
		}
		Vector<Vector<Object>> data1 = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(gid);
		group.addElement(groupName);
		data1.add(group);
		String fileName = FILEPATH + "GroupOf" + cid + ".csv";
		CSVHandler.appendToCSV(fileName, data1);

		return group;
	}

	// Client Leave Group Send
	public Vector<Object> leave(int cid, int gid) throws FileNotFoundException {
		LeaveGroupEvent leaveGroup = new LeaveGroupEvent(cid, gid);
		Connection.sendObject(socket, leaveGroup); // FOR TEST

		Vector<Vector<Object>> data = CSVHandler.readCSV(FILEPATH + "GroupLst.csv");
		String groupName = "";
		for (int i = 0; i < data.size(); i++) {
			if ((int) data.get(i).get(0) == gid) {
				groupName = (String) data.get(i).get(1);
				break;
			}
		}

		Vector<Vector<Object>> dataNew = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(gid);
		group.addElement(groupName);
		String fileName = FILEPATH + "GroupOf" + cid + ".csv";

		Vector<Vector<Object>> dataOld = CSVHandler.readCSV(fileName);
		System.out.println("the gid is = " + gid);
		for (Vector<Object> line : dataOld) {
			System.out.println("the line.elementAt(0) is = " + (int) line.elementAt(0));
			if ((int) line.elementAt(0) != (int) gid) {
				dataNew.add(line);
			} else {
			}
		}
		System.out.println(dataNew);
		CSVHandler.writeCSV(fileName, dataNew);

		return group;
	}

	// Client Get Unread Message //misspelled
	public void getUnreadMessage(int cid, Timestamp lastestTimestamp) {
		GetUpdateEvent getUnreadMessage = new GetUpdateEvent(cid, lastestTimestamp);
		Connection.sendObject(socket, getUnreadMessage);
	}

	// Client Message Send
	public void sendMessage(int cid, int gid, String Message) {
		SendMessageEvent sendMessageEvent = new SendMessageEvent(cid, gid, Message);
		Connection.sendObject(socket, sendMessageEvent);

	}

	// Client Message Receive
	public Vector<Object> newMessage(NewMessageEvent newMessage) throws FileNotFoundException {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> message = new Vector<Object>();

		int cid = newMessage.getCid();
		String clientName = newMessage.getClientName();
		long time = newMessage.getTime().getTime();
		String text = newMessage.getMessage();
		message.addElement(cid);
		message.addElement(clientName);
		message.addElement(time);
		message.addElement(text);
		data.add(message);

		String fileName = FILEPATH + "MessageListOf" + newMessage.getGid() + ".csv";
		CSVHandler.appendToCSV(fileName, data);

		String clientFileName = FILEPATH + "ClientInfo.csv";

		Vector<Vector<Object>> oldClientList = CSVHandler.readCSV(clientFileName);
		Vector<Vector<Object>> newClientList = new Vector<Vector<Object>>();
		Vector<Object> clientData = new Vector<Object>();
		clientData.addElement(cid);
		clientData.addElement(clientName);
		clientData.addElement(time);
		newClientList.add(clientData);
		CSVHandler.writeCSV(clientFileName, newClientList);

		return message;

	}

	// Client Get Update Events(Group File, Unread Messages) Send
	public void getUpdateTransfer(int cid) throws FileNotFoundException {
		String clientFileName = FILEPATH + "ClientInfo.csv";
		Vector<Vector<Object>> client = CSVHandler.readCSV(clientFileName);

		Timestamp currentTime = new Timestamp((long) client.get(0).get(2));
		GetUpdateEvent getUpdateEvent = new GetUpdateEvent(cid, currentTime);
		Connection.sendObject(socket, getUpdateEvent);
	}

	// Client Get Update Events(Group File, Unread Messages) Receive
	public void updateTransfer(UpdateTransferEvent updateTransfer) {
		Vector<Vector<Object>> groupData = updateTransfer.getGroupData();
		HashMap<Integer, Vector<NewMessageEvent>> unread = updateTransfer.getUnread();

		CSVHandler.writeCSV(FILEPATH + "GroupLst.csv", groupData);

		for (int i : unread.keySet()) {
			for (NewMessageEvent m : unread.get(i)) {
				int gid = m.getGid();
				int cid = m.getCid();
				String ClientName = m.getClientName();
				long time = m.getTime().getTime();
				String text = m.getMessage();

				Vector<Vector<Object>> data = new Vector<Vector<Object>>();
				Vector<Object> message = new Vector<Object>();
				message.addElement(cid);
				message.addElement(ClientName);
				message.addElement(time);
				message.addElement(text);
				data.add(message);
				String fileName = FILEPATH + "MessageListOf" + gid + ".csv";
				System.out.println(fileName);
				CSVHandler.appendToCSV(fileName, data);

			}
		}
	}
	
	public int getCid() {
		return cid;
	}
	
	public void setCid(int cid) {
		this.cid = cid;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		instance.socket = socket;
	}
}

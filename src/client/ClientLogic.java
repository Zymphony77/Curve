package client;

import java.io.FileNotFoundException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Vector;

import utility.csv.CSVHandler;
import utility.event.ConnectEvent;
import utility.event.CreateClientEvent;
import utility.event.CreateGroupEvent;
import utility.event.DisconnectEvent;
import utility.event.Event;
import utility.event.GetUpdateEvent;
import utility.event.JoinGroupEvent;
import utility.event.LeaveGroupEvent;
import utility.event.NewClientEvent;
import utility.event.NewGroupEvent;
import utility.event.NewMessageEvent;
import utility.event.SendMessageEvent;
import connection.Connection;

public class ClientLogic {
	private final static ClientLogic instance = new ClientLogic();

	private static final String SERVER_IP = "";
	private static final int SERVER_PORT = 1234;

	private final static Socket clientSocket = Connection.connectToServer(SERVER_IP, SERVER_PORT);
	
	
	public static ClientLogic getInstance() {
		return instance;
	}
	
	// Handle receivedObj
	public void handleReceivedObj(Socket clientSocket, Event receivedObj) {
		
		if (receivedObj instanceof NewClientEvent) {
			NewClient((NewClientEvent) receivedObj);
		} else if (receivedObj instanceof NewGroupEvent) {
			NewGroup((NewGroupEvent)receivedObj);
		} else if (receivedObj instanceof NewMessageEvent) {
			NewMessage((NewMessageEvent)receivedObj);
		} 
	}
	
	
	
	
	// Create Client
<<<<<<< HEAD:src/Client/ClientLogic.java
	public static void CreateClient(String clientName) {
||||||| merged common ancestors
	public void CreateClient(String clientName) {
=======
	//send
	public void CreateClient(String clientName) {
>>>>>>> 03b97b3e641535370080bafa86ce75a5e3fe5091:src/client/ClientLogic.java
		CreateClientEvent createClient = new CreateClientEvent(clientName);
		Connection.sendObject(clientSocket, createClient);

	}
	//receive
	public void NewClient(NewClientEvent newClient) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> client = new Vector<Object>(newClient.getCid());
		client.addElement(newClient.getClientName());

		String ipAddress = "cannot work";
		client.addElement(ipAddress);
		data.add(client);
		CSVHandler.writeCSV("Client.csv", data);
	}

	// Connecting
	//send
	public void Connect(int cid, String ipAddress) {
		ConnectEvent connect = new ConnectEvent(cid);
		// Note: Oak removed ipAddress in the argument here
		Connection.sendObject(clientSocket, connect);

	}
	//receive
	public void Disconnect(int cid) {
		DisconnectEvent disconnect = new DisconnectEvent(cid);
		Connection.sendObject(clientSocket, disconnect);

	}

	// Group Event
	// send
	public void CreateGroup(int cid, String groupName) throws FileNotFoundException {
		CreateGroupEvent createGroup = new CreateGroupEvent(cid, groupName);
		Connection.sendObject(clientSocket, createGroup);

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
	}

	//send
	public void Join(int cid, int gid) {
		JoinGroupEvent joinGroup = new JoinGroupEvent(cid, gid);
		Connection.sendObject(clientSocket, joinGroup);

		// if successful, write
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(gid);
		data.add(group);
		String fileName = "GroupOf" + cid + ".csv";
		CSVHandler.appendToCSV(fileName, data);
	}
	//send
	public void Leave(int cid, int gid) {
		LeaveGroupEvent leaveGroup = new LeaveGroupEvent(cid, gid);
		Connection.sendObject(clientSocket, leaveGroup);
	}
	//receive
	public void NewGroup(NewGroupEvent newGroup) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>(newGroup.getGid());
		group.addElement(newGroup.getGroupName());
		data.add(group);
		CSVHandler.appendToCSV("GroupList.csv", data);
	}

	// Message Event
	//send
	public void GetUnreadMesaage(int cid, Timestamp lastestTimestamp) {
		GetUpdateEvent getUnreadMessage = new GetUpdateEvent(cid, lastestTimestamp);
		Connection.sendObject(clientSocket, getUnreadMessage);
	}
	//send
	public void SendMessage(int cid, int gid, String Message) {
		SendMessageEvent sendMessageEvent = new SendMessageEvent(cid, gid, Message);
		Connection.sendObject(clientSocket, sendMessageEvent);
	}
	//receive
	public void NewMessage(NewMessageEvent newMessage) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> message = new Vector<Object>();
		message.addElement(newMessage.getClientName());
		message.addElement(newMessage.getTime());
		message.addElement(newMessage.getMessage());
		data.add(message);

		String fileName = "MessageListOf" + newMessage.getGid() + ".csv";
		CSVHandler.appendToCSV(fileName, data);
	}

}

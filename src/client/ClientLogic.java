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

public class ClientLogic {
	private final static ClientLogic instance = new ClientLogic();
	private Socket clientSocket;

	public static ClientLogic getInstance() {
		return instance;
	}

	public void setClientSocket(Socket socket) {
		this.clientSocket = socket;
	}

	// Handle receivedObj
	public void handleReceivedObj(Socket clientSocket, Event receivedObj) {

		if (receivedObj instanceof NewClientEvent) {
			NewClient((NewClientEvent) receivedObj);
		} else if (receivedObj instanceof NewGroupEvent) {
			NewGroup((NewGroupEvent) receivedObj);
		} else if (receivedObj instanceof NewMessageEvent) {
			NewMessage((NewMessageEvent) receivedObj);
		} else if (receivedObj instanceof UpdateTransferEvent) {
			UpdateTransfer((UpdateTransferEvent) receivedObj);
		}
	}

	// Create Client
	// send
	public void CreateClient(String clientName) {
		CreateClientEvent createClient = new CreateClientEvent(clientName);
		Connection.sendObject(clientSocket, createClient);

	}

	// receive
	public void NewClient(NewClientEvent newClient) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> client = new Vector<Object>(newClient.getCid());
		client.addElement(newClient.getClientName());

		String ipAddress = "cannot work";
		client.addElement(ipAddress);
		data.add(client);
		CSVHandler.writeCSV("Client.csv", data);

		// TODO add  function to tell UI
	}

	// Connecting
	// send
	public void Connect(int cid, String ipAddress) {
		ConnectEvent connect = new ConnectEvent(cid);
		Connection.sendObject(clientSocket, connect);

	}

	// receive
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

		// TODO add  function to tell UI
	}

	// send
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

		// TODO add  function to tell UI
	}

	// send
	public void Leave(int cid, int gid) {
		LeaveGroupEvent leaveGroup = new LeaveGroupEvent(cid, gid);
		Connection.sendObject(clientSocket, leaveGroup);
	}

	// receive
	public void NewGroup(NewGroupEvent newGroup) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>(newGroup.getGid());
		group.addElement(newGroup.getGroupName());
		data.add(group);
		CSVHandler.appendToCSV("GroupList.csv", data);

		// TODO add  function to tell UI
	}

	// Message Event
	// send
	public void GetUnreadMesaage(int cid, Timestamp lastestTimestamp) {
		GetUpdateEvent getUnreadMessage = new GetUpdateEvent(cid, lastestTimestamp);
		Connection.sendObject(clientSocket, getUnreadMessage);
	}

	// send
	public void SendMessage(int cid, int gid, String Message) {
		SendMessageEvent sendMessageEvent = new SendMessageEvent(cid, gid, Message);
		Connection.sendObject(clientSocket, sendMessageEvent);
	}

	// receive
	public void NewMessage(NewMessageEvent newMessage) {

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> message = new Vector<Object>();
		message.addElement(newMessage.getClientName());
		message.addElement(newMessage.getTime());
		message.addElement(newMessage.getMessage());
		data.add(message);

		String fileName = "MessageListOf" + newMessage.getGid() + ".csv";
		CSVHandler.appendToCSV(fileName, data);

		// TODO add  function to tell UI
	}

	// Update Event
	public void UpdateTransfer(UpdateTransferEvent updateTransfer) {
		Vector<Vector<Object>> groupData = updateTransfer.getGroupData(); //ข้อมูล group แบบใน csv
		HashMap<Integer, GroupMessageData> unread = updateTransfer.getUnread(); //
		
		CSVHandler.appendToCSV("GroupList.csv", groupData);
		
		for(Integer i:unread.keySet()) {
			GroupMessageData groupMessageData = unread.get(i);
			int gid = groupMessageData.getGid();
			Vector<Message> messageVector = groupMessageData.getMessageVector();

			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			for(Message m:messageVector) {
				Vector<Object> message = new Vector<Object>();
				message.addElement(m.getCid());
				message.addElement(m.getTime());
				message.addElement(m.getText());
				data.add(message);
			}
			String fileName = "MessageListOf" + gid + ".csv";
			CSVHandler.appendToCSV(fileName, data);
		}
		
		// TODO add  function to tell UI
	}

	public void GroupLogTransfer(GroupLogTransferEvent groupLogTransfer) {
		int gid = groupLogTransfer.getGid();
		int cid = groupLogTransfer.getCid();
		Timestamp time = groupLogTransfer.getTime();
		String event = groupLogTransfer.getEvent();

		// TODO
		// TODO add  function to tell UI
	}

	public void notifyUI(Event event) {

		// TODO
	}

}

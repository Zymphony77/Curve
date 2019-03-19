package Client;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Vector;

import utility.csv.CSVHandler;
import utility.event.ConnectEvent;
import utility.event.CreateClientEvent;
import utility.event.CreateGroupEvent;
import utility.event.DisconnectEvent;
import utility.event.GetUnreadMessageEvent;
import utility.event.JoinGroupEvent;
import utility.event.LeaveGroupEvent;
import utility.event.NewClientEvent;
import utility.event.NewGroupEvent;
import utility.event.NewMessageEvent;
import utility.event.SendMessageEvent;

public class ClientLogic {
	private final static ClientLogic instance = new ClientLogic();

	public static ClientLogic getInstance() {
		return instance;
	}

	// Create Client
	public void CreateClient(String clientName) {
		CreateClientEvent createClient = new CreateClientEvent(clientName);

	}

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
	public void Connect(int cid, String ipAddress) {
		ConnectEvent connect = new ConnectEvent(cid, ipAddress);

	}

	public void Disconnect(int cid) {
		DisconnectEvent disconnect = new DisconnectEvent(cid);

	}

	// Group Event
	public void CreateGroup(int cid, String groupName) throws FileNotFoundException {
		CreateGroupEvent createGroup = new CreateGroupEvent(cid, groupName);

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
		data.add(group);
		String fileName = "GroupOf" + cid + ".csv";

		CSVHandler.appendToCSV(fileName, data);
	}

	public void Join(int cid, int gid) {
		JoinGroupEvent joinGroup = new JoinGroupEvent(cid, gid);

		// if successful, write
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>();
		group.addElement(gid);
		data.add(group);
		String fileName = "GroupOf" + cid + ".csv";
		CSVHandler.appendToCSV(fileName, data);
	}

	public void Leave(int cid, int gid) {
		LeaveGroupEvent leaveGroup = new LeaveGroupEvent(cid, gid);
	}

	public void NewGroup(NewGroupEvent newGroup) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> group = new Vector<Object>(newGroup.getGid());
		group.addElement(newGroup.getGroupName());
		data.add(group);
		CSVHandler.appendToCSV("GroupList.csv", data);
	}

	// Message Event
	public void GetUnreadMesaage(int cid, Timestamp lastestTimestamp) {
		GetUnreadMessageEvent getUnreadMessage = new GetUnreadMessageEvent(cid, lastestTimestamp);
	}

	public void SendMessage(int cid, int gid, String Message) {
		SendMessageEvent sendMessageEvent = new SendMessageEvent(cid, gid, Message);
	}

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

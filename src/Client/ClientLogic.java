package Client;

import java.sql.Timestamp;
import utility.csv.*;
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

	// Create Client
	public static void CreateClient(String clientName) {
		CreateClientEvent createClient = new CreateClientEvent(clientName);
	}

	public static void NewClient(NewClientEvent newClient) {

	}

	// Connecting
	public static void Connect(int cid, String ipAddress) {
		ConnectEvent connect = new ConnectEvent(cid, ipAddress);

	}

	public static void Disconnect(int cid) {
		DisconnectEvent disconnect = new DisconnectEvent(cid);

	}

	// Group Event
	public static void CreateGroup(int cid, String groupName) {
		CreateGroupEvent createGroup = new CreateGroupEvent(cid, groupName);
	}

	public static void Join(int cid, int gid) {
		JoinGroupEvent joinGroup = new JoinGroupEvent(cid, gid);
	}

	public static void Leave(int cid, int gid) {
		LeaveGroupEvent leaveGroup = new LeaveGroupEvent(cid, gid);
	}

	public static void NewGroup(NewGroupEvent newGroup) {

	}

	// Message Event
	public static void GetUnreadMesaage(int cid, Timestamp lastestTimestamp) {
		GetUnreadMessageEvent getUnreadMessage = new GetUnreadMessageEvent(cid, lastestTimestamp);
	}

	public static void SendMessage(int cid, int gid, String Message) {
		SendMessageEvent sendMessageEvent = new SendMessageEvent(cid, gid, Message);
	}

	public static void NewMessage(NewMessageEvent newMessage) {

	}

}

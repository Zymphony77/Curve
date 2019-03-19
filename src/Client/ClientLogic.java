package Client;

import utility.csv.*;
import utility.event.*;
public class ClientLogic {

	// Create Client
	public static void CreateClient(String Username) {

	}

	public static void NewClient(Object X) {

	}

	// Connecting
	public static void Connect(int cid, String ipAddress) {
		ConnectEvent connectEvent = new ConnectEvent(cid,ipAddress);
		
	}

	public static void Disconnect(int cid) {
		DisconnectEvent disconnectEvent = new DisconnectEvent(cid);
		
	}

	// Group Event
	public static void CreateGroup(int cid, String Groupname) {

	}

	public static void Join(String Groupname) {

	}

	public static void Leave(int gid) {

	}

	public static void NewGroup(NewGroupEvent NewGroup) {
			
	}

	// Message Event
	public static void GetUnread(int gid) {

	}

	public static void Send(String Message, int cid, int gid) {

	}

	public static void UpdateMessage(Object X) {

	}

}

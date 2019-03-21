package client;

import utility.event.*;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import client.ClientLogic;

public class TestClientLogic {

	public static void testNewClient() {
		NewClientEvent newClient1 = new NewClientEvent(1, "Client#1");
		Vector<Object> test1 = ClientLogic.newClient(newClient1);
		System.out.println(test1);
		NewClientEvent newClient2 = new NewClientEvent(2, "Client#2");
		Vector<Object> test2 = ClientLogic.newClient(newClient2);
		System.out.println(test2);
		NewClientEvent newClient3 = new NewClientEvent(3, "Client#3");
		Vector<Object> test3 = ClientLogic.newClient(newClient3);
		System.out.println(test3);
	}

	public static void testNewGroup() {
		NewGroupEvent newGroup1 = new NewGroupEvent(11, "Group#11");
		Vector<Object> test1 = ClientLogic.newGroup(newGroup1);
		System.out.println(test1);
		NewGroupEvent newGroup2 = new NewGroupEvent(12, "Group#12");
		Vector<Object> test2 = ClientLogic.newGroup(newGroup2);
		System.out.println(test2);
	}

	public static void testCreateGroup() throws FileNotFoundException {
		Vector<Object> group = ClientLogic.createGroup(1, "Group#newGroup");

		// NewGroupEvent newGroup2 = new NewGroupEvent(13,"Group#newGroup");
		// Vector<Object> test2 = ClientLogic.newGroup(newGroup2);
		System.out.println("Client#1 will now be in Group Group#newGroup");

	}

	public static void testJoinGroup() throws FileNotFoundException {
		Vector<Object> group = ClientLogic.join(2, 11);
		Vector<Object> group2 = ClientLogic.join(3, 11);
	}
	public static void testLeaveGroup() throws FileNotFoundException {
		Vector<Object> group = ClientLogic.leave(2, 11);
	}

	public static void testNewMessage() throws FileNotFoundException {
		Timestamp testTime = new Timestamp((new Date()).getTime());
		NewMessageEvent newMessage = new NewMessageEvent(1, 11, "Client#1", testTime,"Message1 from Client#1 to Group#11");

		System.out.println(testTime.getTime());
		ClientLogic.newMessage(newMessage);
		
		testTime = new Timestamp((new Date()).getTime());
		newMessage = new NewMessageEvent(2, 11, "Client#2", testTime, "Message2 from Client#2 to Group#11");
		System.out.println(testTime.getTime());
		ClientLogic.newMessage(newMessage);

		testTime = new Timestamp((new Date()).getTime());
		newMessage = new NewMessageEvent(3, 11, "Client#3", testTime, "Message3 from Client#3 to Group#11");
		System.out.println(testTime.getTime());
		ClientLogic.newMessage(newMessage);
		
		System.out.println("");
	}

	public static void testUpdateTransfer() {
		Vector<Vector<Object>> groupData = new Vector<Vector<Object>>();
		Vector<Object> group1 = new Vector<Object>();
		group1.addElement(1);
		group1.addElement("Group#1");
		groupData.add(group1);
		Vector<Object> group2 = new Vector<Object>();
		group2.addElement(2);
		group2.addElement("Group#2");
		groupData.add(group2);

		HashMap<Integer, Vector<NewMessageEvent>> unread = new HashMap<Integer, Vector<NewMessageEvent>>();
		Vector<NewMessageEvent> NewMessageEventVector = new Vector<NewMessageEvent>();
		Timestamp testTime1 = new Timestamp((new Date()).getTime());
		Timestamp testTime2 = new Timestamp((new Date()).getTime());
		NewMessageEvent NewMessageEvent1 = new NewMessageEvent(1, 1, "Client#1", testTime1, "Message1G1C1");
		NewMessageEvent NewMessageEvent2 = new NewMessageEvent(1, 2, "Client#2", testTime2, "Message2G1C2");
		NewMessageEventVector.add(NewMessageEvent1);
		NewMessageEventVector.add(NewMessageEvent2);
		unread.put(1, NewMessageEventVector);

		UpdateTransferEvent updateTransferEvent = new UpdateTransferEvent(groupData, unread);
		ClientLogic.updateTransfer(updateTransferEvent);

	}

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		System.out.println("Start Test New Client . . .");
		testNewClient();

		System.out.println("Start Test New Group . . .");
		testNewGroup();

		System.out.println("Start Test Create Group . . .");
		testCreateGroup();

		System.out.println("Start Test Join Group . . .");
		testJoinGroup();

		System.out.println("Start Test Leave Group . . .");
		testLeaveGroup();
		System.out.println("Start Test New Message . . .");
		testNewMessage();

		System.out.println("Start Test Update Transfer . . .");
		testUpdateTransfer();

		System.out.println("------ TEST FINISHED ------");

//		testLeaveGroup();
	}

}

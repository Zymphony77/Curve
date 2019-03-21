package client;

import utility.event.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import client.ClientLogic;
public class TestClientLogic {

	public static void testNewClient() {
		NewClientEvent newClient = new NewClientEvent(1,"TestClient");
		Vector<Object> test = ClientLogic.newClient(newClient);
		System.out.println(test);
	}
	public static void testNewGroup() {
		NewGroupEvent newGroup = new NewGroupEvent(11,"Testgroup");
		Vector<Object> test = ClientLogic.newGroup(newGroup);
		System.out.println(test);
	}
	public static void testNewMessage() {
		Timestamp testTime = new Timestamp((new Date()).getTime());
		NewMessageEvent newMessage = new NewMessageEvent(1,11,"TestClient",testTime,"TestMessage");
		Vector<Object> test = ClientLogic.newMessage(newMessage);
		System.out.println(test);
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
		NewMessageEvent NewMessageEvent1 = new NewMessageEvent(1,1,"Client#1",testTime1,"Message1G1C1");
		NewMessageEvent NewMessageEvent2 = new NewMessageEvent(1,2,"Client#2",testTime2,"Message2G1C2");
		NewMessageEventVector.add(NewMessageEvent1);
		NewMessageEventVector.add(NewMessageEvent2);
		unread.put(1,NewMessageEventVector);
		
		UpdateTransferEvent updateTransferEvent = new UpdateTransferEvent(groupData, unread);
		ClientLogic.updateTransfer(updateTransferEvent);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testNewClient(); 
		testNewGroup(); 
		testNewMessage(); 
		testUpdateTransfer();

	}

}

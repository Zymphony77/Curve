package client;

import java.net.*;
import java.util.Vector;

import application.Main;

import java.io.*;

import client.*;
import connection.Connection;
import utility.event.Event;
import utility.event.NewClientEvent;
import utility.event.NewGroupEvent;
import utility.event.NewMessageEvent;
import utility.event.UpdateTransferEvent;
import java.sql.Timestamp;

public class ClientThread extends Thread {
	
	Socket socket;
	
	public ClientThread (Socket socket) {
		this.socket = socket;
		this.start();
	}
	
	public void run() {
		while(true) {
			Object receivedObj = Connection.receiveObject(socket);
			
			synchronized (ClientLogic.getInstance()) {
				// Function to handle receivedObj (request)
				if (receivedObj instanceof NewClientEvent) {
					ClientLogic.newClient((NewClientEvent) receivedObj);
					Main.isConected = true;
					//TODO Puwong cid cname

				} else if (receivedObj instanceof NewGroupEvent) {
					Vector<Object> newGroup = ClientLogic.newGroup((NewGroupEvent) receivedObj);
					ClientGUI.addGroupLst((int) newGroup.get(0), (String) newGroup.get(1));
				} else if (receivedObj instanceof NewMessageEvent) {
					Vector<Object> newMessage = ClientLogic.newMessage((NewMessageEvent) receivedObj);
					//TODO cname ts text
					ClientGUI.displayMessage((String) newMessage.get(0), (Timestamp) newMessage.get(1), (String) newMessage.get(2));

				} else if (receivedObj instanceof UpdateTransferEvent) {
					ClientLogic.updateTransfer((UpdateTransferEvent) receivedObj);
					//TODO Puwong [groups(gid gname],[gid,(messages(gid cid ts strmsg cname))]
					
				}
					
			}
		}
	}
}

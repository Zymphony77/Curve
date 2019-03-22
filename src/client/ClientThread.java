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
			
			if (receivedObj == null) {
				ClientLogic.getInstance().reconnect();
				socket = ClientLogic.getInstance().getSocket();
			}
			
			synchronized (ClientLogic.getInstance()) {
				// Function to handle receivedObj (request)
				if (receivedObj instanceof NewClientEvent) {
					ClientLogic.getInstance().newClient((NewClientEvent) receivedObj);
					
					NewClientEvent r = (NewClientEvent) receivedObj;
					System.out.println("--> [NewClientEvent]");
					System.out.println("\tCid: " + r.getCid() + 
							"\n\tClientName: " + r.getClientName());
					Main.isConected = true;

				} else if (receivedObj instanceof NewGroupEvent) {
					Vector<Object> newGroup = ClientLogic.getInstance().newGroup((NewGroupEvent) receivedObj);
//					ClientGUI.addGroupLst((int) newGroup.get(0), (String) newGroup.get(1));
					

					NewGroupEvent r = (NewGroupEvent) receivedObj;
					System.out.println("--> [NewGroupEvent]");
					System.out.println("\tGid: " + r.getGid() + 
							"\n\tGroupName: " + r.getGroupName());
				} else if (receivedObj instanceof NewMessageEvent) {
					Vector<Object> newMessage = null;
					
					NewMessageEvent r = (NewMessageEvent) receivedObj;
					System.out.println("--> [NewMessageEvent]");
					System.out.println("\tGid: " + r.getGid() + 
							"\n\tCid: " + r.getCid() +
							"\n\tClientName: " + r.getClientName() +
							"\n\tTime:" + r.getTime() +
							"\n\tText: " + r.getMessage());
					try {
						newMessage = ClientLogic.getInstance().newMessage((NewMessageEvent) receivedObj);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//TODO cname ts text
//					ClientGUI.displayMessage((String) newMessage.get(0), (Timestamp) newMessage.get(1), (String) newMessage.get(2));

				} else if (receivedObj instanceof UpdateTransferEvent) {
					ClientLogic.getInstance().updateTransfer((UpdateTransferEvent) receivedObj);
					//TODO Puwong [groups(gid gname],[gid,(messages(gid cid ts strmsg cname))]
					
					UpdateTransferEvent r = (UpdateTransferEvent) receivedObj;
					System.out.println("--> [UpdateTransferEvent]");
					
					System.out.println("\tGroupData: ");
					for (Vector<Object> group: r.getGroupData()) {
						for (Object data: group) {
							System.out.print(data + "\t");
						}
						System.out.println();
					}
					
					System.out.println("\n\tUnreadData: ");
					for (int gid: r.getUnread().keySet()) {
						System.out.println("--> " + gid);
						for (NewMessageEvent data: r.getUnread().get(gid)) {
							System.out.println("Client #" + data.getCid() +
									"\t@Time " + data.getTime() +
									"\n\tMessage: " + data.getMessage());
						}
						System.out.println();
					}
					
				}
					
			}
		}
	}
}

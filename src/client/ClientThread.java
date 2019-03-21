package client;

import java.net.*;
import java.io.*;

import client.*;
import connection.Connection;
import utility.event.Event;
import utility.event.NewClientEvent;
import utility.event.NewGroupEvent;
import utility.event.NewMessageEvent;
import utility.event.UpdateTransferEvent;

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
					//TODO Puwong cid cname

				} else if (receivedObj instanceof NewGroupEvent) {
					ClientLogic.newGroup((NewGroupEvent) receivedObj);
					//TODO Puwong gid gname

				} else if (receivedObj instanceof NewMessageEvent) {
					ClientLogic.newMessage((NewMessageEvent) receivedObj);
					//TODO Puwong gid cid ts strmsg cname

				} else if (receivedObj instanceof UpdateTransferEvent) {
					ClientLogic.updateTransfer((UpdateTransferEvent) receivedObj);
					//TODO Puwong [groups(gid gname],[gid,(messages(gid cid ts strmsg cname))]

				}
					
			}
		}
	}
}

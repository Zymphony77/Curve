package server;

import java.net.*;

import connection.Connection;

import utility.event.*;

public class ServerThread extends Thread {
	private Socket clientSocket;
	
	public ServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.start();
	}
	
	public void run() {
		try {
			while(true) {
				Object request = Connection.receiveObject(clientSocket);
				if (request == null) {
					break;
				}
				
				synchronized (ServerLogic.getInstance()) {
					ServerLogic.getInstance().handleRequest(clientSocket, (Event) request);
				}
			}
		} catch (Exception e) {
			
		}
	}
	
}
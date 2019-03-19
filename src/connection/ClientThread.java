package connection;

import java.net.*;
import java.io.*;

import client.*;

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
			}
		}
	}
}

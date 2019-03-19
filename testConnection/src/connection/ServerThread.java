package connection;

import java.net.*;
import java.io.*;

import model.*;

class ServerThread extends Thread {
	Socket clientSocket;
	
	public ServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.start();
	}
	
	public void run() {
		while(true) {
			TestObject obj = Connection.recieveObject(clientSocket);
			System.out.println(obj.getId() + ": " + obj.getMessage());
			
			for (Socket socket : TestServer.clientSockets)
				Connection.sendObject(socket, obj);
		}
	}
}
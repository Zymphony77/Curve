package connection;

import java.net.*;
import java.io.*;

import model.*;

public class ClientThread extends Thread{
	
	Socket socket;
	
	public ClientThread (Socket socket) {
		this.socket = socket;
		this.start();
	}
	
	public void run() {
		while(true) {
			TestObject recievedObj = Connection.recieveObject(socket);
	        System.out.println(recievedObj.getId() + ": " + recievedObj.getMessage());
		}
	}
}

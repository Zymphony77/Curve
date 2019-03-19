package connection;

import java.net.*;
import java.io.*;

import model.*;

public class Connection {
	
	public static Socket connectToServer(String serverIp, int serverPort) {
		try {
			return new Socket(serverIp, serverPort);
		} catch (UnknownHostException e) {} catch (IOException e) {}
		return null;
	}
	
	public static void sendObject(Socket socket, TestObject obj) {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(obj);
		} catch (IOException e) {}
	}
	
	public static TestObject recieveObject(Socket socket) {
		ObjectInputStream ois;
		TestObject recievedObj = null;
		
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			recievedObj = (TestObject) ois.readObject();
		} catch (IOException e) {} catch (ClassNotFoundException e) {}
        
		return recievedObj;
	}
}

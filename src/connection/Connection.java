package connection;

import java.net.*;
import java.io.*;

public class Connection {
	
	public static Socket connectToServer(String serverIp, int serverPort) {
		try {
			return new Socket(serverIp, serverPort);
		} catch (UnknownHostException e) {} catch (IOException e) {}
		return null;
	}
	
	public static void sendObject(Socket socket, Object obj) {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(obj);
		} catch (IOException e) {}
	}
	
	public static Object receiveObject(Socket socket) {
		ObjectInputStream ois;
		Object receivedObj = null;
		
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			receivedObj = ois.readObject();
		} catch (IOException e) {} catch (ClassNotFoundException e) {}
        
		return receivedObj;
	}
}

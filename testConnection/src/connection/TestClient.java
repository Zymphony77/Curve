package connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import model.TestObject;

public class TestClient {
	
	static Scanner sc = new Scanner(System.in);
	
	private static int cid = 1;

	public static void main(String[] args) {
		try {
			
			Socket socket = Connection.connectToServer(TestServer.serverIp, TestServer.serverPort);
			ClientThread c = new ClientThread(socket);
			
			while(true) {
				String message = sc.nextLine();
				
				if (message.equals("exit"))
					break;
				
				TestObject obj = new TestObject(cid, message);
				Connection.sendObject(socket, obj);
			}
	        
	        socket.close();
			
		} catch (IOException e) {}
	}
}

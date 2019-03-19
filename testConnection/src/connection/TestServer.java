package connection;

import java.net.*;
import java.io.*;

import java.util.*;

public class TestServer {
	public static String serverIp = "localhost";
	public static int serverPort = 1234;
	
	public static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
	
	public static void main (String[] args) {
		try{
			ServerSocket listenSocket = new ServerSocket(serverPort);
			
			while(true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("connect");
				ServerThread c = new ServerThread(clientSocket);
				
				clientSockets.add(clientSocket);
			}
		} catch(IOException e) {}
	}
}

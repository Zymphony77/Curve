package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import connection.Connection;
import utility.event.ConnectEvent;

public class Server {
	public static final String PRIMARY_IP = "localhost";
	public static final int PRIMARY_PORT = 1;
	public static final String SECONDARY_IP = "localhost";
	public static final int SECONDARY_PORT = 2;
	public static boolean IS_PRIMARY = true;
	
	static ServerSocket serverSocket;
	static Socket connectSocket;

	static Thread syncThread = null;
	static boolean threadClosed = false;
	
	public static void main(String[] args) {
		// Run when the program is closed
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		        try {
		        	threadClosed = true;
		        	serverSocket.close();
		        	connectSocket.close();
		        } catch (Exception e) {}
		    }
		}));
		
		// Connect to another server
		new Thread(() -> {
			while (!threadClosed) {
				try {
					if (IS_PRIMARY) {
						connectSocket = Connection.connectToServer(SECONDARY_IP, SECONDARY_PORT);
					} else {
						connectSocket = Connection.connectToServer(PRIMARY_IP,  PRIMARY_PORT);
					}
					
					// Server identity -- cid = -1
					Connection.sendObject(connectSocket, new ConnectEvent(-1));
					
					// Synchronize files
					syncThread = new Thread(() -> {
						if (IS_PRIMARY) {
							ServerLogic.getInstance().synchronizeFile(SECONDARY_IP, SECONDARY_PORT);
						} else {
							ServerLogic.getInstance().synchronizeFile(PRIMARY_IP, PRIMARY_PORT);
						}
						
						ServerLogic.getInstance().retrieveClientData();
						ServerLogic.getInstance().retrieveGroupData();
						ServerLogic.getInstance().retrieveGroupMessageData();
						ServerLogic.getInstance().retrieveGroupLog();
					});
					syncThread.start();
				} catch (Exception e) {}
			}
		}).start();
		
		System.out.println("Starting server...");
		
		ServerLogic.getInstance();
		
		try {
			if (IS_PRIMARY) {
				serverSocket = new ServerSocket(PRIMARY_PORT);
			} else {
				serverSocket = new ServerSocket(SECONDARY_PORT);
			}
			
			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("New Connection from Client: " + clientSocket.getInetAddress());
				new ServerThread(clientSocket);
			}
		} catch(IOException e) {
			System.out.println("Server terminated");
		}
	}
	
	
}

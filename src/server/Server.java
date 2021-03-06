package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import connection.Connection;
import utility.event.ConnectEvent;

public class Server {
	public static final String PRIMARY_IP = "192.168.1.103";
	public static final int PRIMARY_PORT = 1024;
	public static final String SECONDARY_IP = "192.168.1.108";
	public static final int SECONDARY_PORT = 1025;
	public static boolean IS_PRIMARY = true;
	
	static ServerSocket serverSocket;
	static Socket connectSocket;

	static Thread syncThread = null;
	static boolean threadClosed = false;
	static boolean startInitialization = false;
	static boolean openedFirst = false;
	
	public static void main(String[] args) {
		if (args.length == 1) {
			if (args[0].equals("PRIMARY")) {
				IS_PRIMARY = true;
			} else if (args[0].equals("SECONDARY")) {
				IS_PRIMARY = false;
			} else {
				System.out.println("Usage: java server/Server [PRIMARY/SECONDARY]");
				return;
			}
		} else {
			System.out.println("Usage: java server/Server [PRIMARY/SECONDARY]");
			return;
		}
		
		startInitialization = true;
		
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
						if (!openedFirst) {
							if (IS_PRIMARY) {
								ServerLogic.getInstance().synchronizeFile(SECONDARY_IP, SECONDARY_PORT);
							} else {
								ServerLogic.getInstance().synchronizeFile(PRIMARY_IP, PRIMARY_PORT);
							}
							
							ServerLogic.getInstance().retrieveClientData();
							ServerLogic.getInstance().retrieveGroupData();
							ServerLogic.getInstance().retrieveGroupMessageData();
							ServerLogic.getInstance().retrieveGroupLog();
						}
					});
					syncThread.start();
					
					while (!connectSocket.isClosed());
				} catch (Exception e) {
					openedFirst = true;
				}
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

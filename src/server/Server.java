package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket listenSocket;
	
	public static void main(String[] args) {
		// Run when the program is closed
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		        try {
		        	listenSocket.close();
		        } catch (Exception e) {}
		    }
		}));
		
		try {
			if (ServerLogic.getInstance().isPrimary()) {
				listenSocket = new ServerSocket(ServerLogic.getPrimaryPort());
			} else {
				listenSocket = new ServerSocket(ServerLogic.getSecondaryPort());
			}
			
			while(true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("New Connection from Client: " + clientSocket.getInetAddress());
				ServerThread c = new ServerThread(clientSocket);
			}
		} catch(IOException e) {}
	}

	
	
}

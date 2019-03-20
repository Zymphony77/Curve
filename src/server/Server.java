package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket listenSocket;
	
	public static void main(String[] args) {
		try {
			listenSocket = new ServerSocket(ServerLogic.getServerPort());
			
			while(true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("New Connection from Client:" + clientSocket.getInetAddress());
				ServerThread c = new ServerThread(clientSocket);
			}
		} catch(IOException e) {}
		
		listenSocket.close();
	}
}

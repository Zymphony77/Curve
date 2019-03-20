package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import connection.Connection;
import server.*;

public class Client {
	private static Socket socket;
	
	public static void main(String[] args) {
		try {
			
			socket = Connection.connectToServer(Server.getServerIp(), Server.getServerPort());
			ClientThread c = new ClientThread(socket);
			
			while(true) {
				/*
				 * TODO 
				 * */
			}
		} catch (IOException e) {}
		
		socket.close();
	}
}

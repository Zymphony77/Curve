package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import connection.Connection;
import server.*;

public class Client {
	private static ClientLogic clientLogic;
	
	public static void main(String[] args) {
		clientLogic = ClientLogic.getInstance();
		
		ClientThread c = new ClientThread(clientLogic.getSocket());
		
		while(true) {
			/*
			 * TODO 
			 * */
		}
	}
}

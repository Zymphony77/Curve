package server;

import java.net.Socket;
import java.util.Scanner;

import client.*;
import model.*;
import utility.event.*;

public class TestClient {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		Socket clientSocket;
		boolean exit = false;
		
		try {
			clientSocket = new Socket(Server.PRIMARY_IP, Server.PRIMARY_PORT);
		} catch (Exception e) {
			System.out.println("Server cannot be reached!");
			return;
		}
		
		ClientThread clientThread = new ClientThread(clientSocket);
		
		while (true) {
			System.out.println("Request List");
			System.out.println("-------------------------------");
			System.out.println("1: ConnectEvent");
			System.out.println("2: CreateClientEvent");
			System.out.println("3: DisconnectEvent");
			System.out.println("4: GetUpdateEvent");
			System.out.println("5: JoinGroupEvent");
			System.out.println("6: LeaveGroupEvent");
			System.out.println("7: SendMessageEvent");
			System.out.println("8: Exit");
			System.out.println("-------------------------------");
			
			System.out.print("Request : ");
			int req = sc.nextInt();
			
			// TODO
			
			switch (req) {
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			default:
				exit = true;
			}
			
			if (exit) {
				break;
			}
		}
	}
}

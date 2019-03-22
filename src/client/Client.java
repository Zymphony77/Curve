package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;

import connection.Connection;
import server.*;
import utility.event.ConnectEvent;
import utility.event.CreateClientEvent;
import utility.event.CreateGroupEvent;
import utility.event.DisconnectEvent;
import utility.event.GetUpdateEvent;
import utility.event.JoinGroupEvent;
import utility.event.LeaveGroupEvent;
import utility.event.SendMessageEvent;

public class Client {
	private static ClientLogic clientLogic;
	
	public static void main(String[] args) {
		clientLogic = ClientLogic.getInstance();
		
		ClientThread c = new ClientThread(clientLogic.getSocket());
		
		Scanner sc = new Scanner(System.in);
		
		int gid;
		boolean exit = false;
		
		while (true) {
			print();
			
			System.out.print("Request: ");
			int req = sc.nextInt();
			
			if ((req != 0 && req != 2 && req != 9 && req != -1) && clientLogic.getCid() == -1) {
				System.out.println("Set cid first");
				continue;
			}
			
			switch (req) {
			case 0:		// [Locally] Set cid
				System.out.print("Cid: ");
				int cid = sc.nextInt();
				if (cid <= 0) {
					System.out.println(">> cid starts from 1");
					cid = -1;
				} else {
					clientLogic.setCid(cid);
				}
				break;
			case 1:		// ConnectEvent
				System.out.println("Connecting...");
				Connection.sendObject(clientLogic.getSocket(), new ConnectEvent(clientLogic.getCid()));
				break;
			case 2:		// CreateClientEvent
				System.out.print("Client name: ");
				String clientName = sc.next();
				Connection.sendObject(clientLogic.getSocket(), new CreateClientEvent(clientName));
				break;
			case 3:		// CreateGroupEvent
				System.out.print("Group name: ");
				String groupName = sc.next();
				try {
					clientLogic.createGroup(clientLogic.getCid(), groupName);
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				break;
			case 4:		// DisconnectEvent
				System.out.println("Disconnecting...");
				Connection.sendObject(clientLogic.getSocket(), new DisconnectEvent(clientLogic.getCid()));
				break;
			case 5: 	// GetUpdateEvent
				System.out.println("Fetching update...");
				Connection.sendObject(clientLogic.getSocket(), new GetUpdateEvent(clientLogic.getCid(), new Timestamp(0)));
				break;
			case 6:		// JoinGroupEvent
				System.out.print("Gid: ");
				gid = sc.nextInt();
				if (gid <= 0) {
					System.out.println(">> gid starts from 1");
					break;
				}
				try {
					clientLogic.join(clientLogic.getCid(), gid);
				} catch (FileNotFoundException e1) {}
				break;
			case 7: 	// LeaveGroupEvent
				System.out.print("Gid: ");
				gid = sc.nextInt();
				if (gid <= 0) {
					System.out.println(">> gid starts from 1");
					break;
				}
				try {
					clientLogic.leave(clientLogic.getCid(), gid);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case 8:		// SendMessageEvent
				System.out.print("Gid: ");
				gid = sc.nextInt();
				if (gid <= 0) {
					System.out.println(">> gid starts from 1");
					break;
				}
				System.out.print("Text: ");
				String text = sc.next();
				Connection.sendObject(clientLogic.getSocket(), new SendMessageEvent(clientLogic.getCid(), gid, text));
				break;
			case 9:
				break;
			default:	// Exit program
				exit = true;
			}
			
			if (req != 9 && req != -1) {
				System.out.println("Sending a request...");
			}
			
			if (exit) {
				try {
					clientLogic.getSocket().close();
				} catch (Exception e) {}
				
				break;
			}
		}
	}
	
	public static void print() {
		System.out.println("-------------------------------");
		System.out.println("Request List");
		System.out.println("Current cid: " + (ClientLogic.getInstance().getCid() == -1 ? "Not Set" : ClientLogic.getInstance().getCid()));
		System.out.println("-------------------------------");
		System.out.println("0: [Locally] Set cid");
		System.out.println("1: ConnectEvent");
		System.out.println("2: CreateClientEvent");
		System.out.println("3: CreateGroupEvent");
		System.out.println("4: DisconnectEvent");
		System.out.println("5: GetUpdateEvent");
		System.out.println("6: JoinGroupEvent");
		System.out.println("7: LeaveGroupEvent");
		System.out.println("8: SendMessageEvent");
		System.out.println("9: Reprint request list");
		System.out.println("-1: Exit program");
		System.out.println("-------------------------------");
	}
	
}

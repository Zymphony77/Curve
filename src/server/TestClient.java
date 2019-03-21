package server;

import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.Vector;

import connection.Connection;
import utility.event.*;

public class TestClient {
	private static boolean threadClosed;
	
	static Socket clientSocket;
	static int cid = -1;
	static int gid;
	static boolean exit = false;
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		try {
			clientSocket = new Socket(Server.PRIMARY_IP, Server.PRIMARY_PORT);
		} catch (Exception e) {
			System.out.println("Server cannot be reached!");
			sc.close();
			return;
		}
		
		new Thread(() -> {
			while (!threadClosed) {
				Event response = (Event) Connection.receiveObject(clientSocket);
				
				if (response != null) {
					System.out.println("\nIncoming request...");
				}
				
				if (response instanceof GroupLogTransferEvent) {
					GroupLogTransferEvent r = (GroupLogTransferEvent) response;
					System.out.println("--> [GroupLogTransferEvent]");
					System.out.println("\tGid: " + r.getGid() + 
							"\n\tCid: " + r.getCid() +
							"\n\tTimestamp: " + r.getTime() +
							"\n\tEvent: " + r.getEvent());
				} else if (response instanceof NewClientEvent) {
					NewClientEvent r = (NewClientEvent) response;
					System.out.println("--> [NewClientEvent]");
					System.out.println("\tCid: " + r.getCid() + 
							"\n\tClientName: " + r.getClientName());
				} else if (response instanceof NewGroupEvent) {
					NewGroupEvent r = (NewGroupEvent) response;
					System.out.println("--> [NewGroupEvent]");
					System.out.println("\tGid: " + r.getGid() + 
							"\n\tGroupName: " + r.getGroupName());
				} else if (response instanceof NewMessageEvent) {
					NewMessageEvent r = (NewMessageEvent) response;
					System.out.println("--> [NewMessageEvent]");
					System.out.println("\tGid: " + r.getGid() + 
							"\n\tCid: " + r.getCid() +
							"\n\tClientName: " + r.getClientName() +
							"\n\tTime:" + r.getTime() +
							"\n\tText: " + r.getMessage());
				} else if (response instanceof UpdateTransferEvent) {
					UpdateTransferEvent r = (UpdateTransferEvent) response;
					System.out.println("--> [UpdateTransferEvent]");
					System.out.println("\tGroupData: ");
					for (Vector<Object> group: r.getGroupData()) {
						for (Object data: group) {
							System.out.print(data + "\t");
						}
						System.out.println();
					}
				}
			}
		}).start();
		
		while (true) {
			print();
			
			System.out.print("Request: ");
			int req = sc.nextInt();
			
			if ((req != 0 && req != 2 && req != 9 && req != -1) && cid == -1) {
				System.out.println("Set cid first");
				continue;
			}
			
			switch (req) {
			case 0:		// [Locally] Set cid
				System.out.print("Cid: ");
				cid = sc.nextInt();
				break;
			case 1:		// ConnectEvent
				System.out.println("Connecting...");
				Connection.sendObject(clientSocket, new ConnectEvent(cid));
				break;
			case 2:		// CreateClientEvent
				System.out.print("Client name: ");
				String clientName = sc.next();
				Connection.sendObject(clientSocket, new CreateClientEvent(clientName));
				break;
			case 3:		// CreateGroupEvent
				System.out.print("Group name: ");
				String groupName = sc.next();
				Connection.sendObject(clientSocket, new CreateGroupEvent(cid, groupName));
				break;
			case 4:		// DisconnectEvent
				System.out.println("Disconnecting...");
				Connection.sendObject(clientSocket, new DisconnectEvent(cid));
				break;
			case 5: 	// GetUpdateEvent
				System.out.println("Fetching update...");
				Connection.sendObject(clientSocket, new GetUpdateEvent(cid, new Timestamp(0)));
				break;
			case 6:		// JoinGroupEvent
				System.out.print("Gid: ");
				gid = sc.nextInt();
				Connection.sendObject(clientSocket, new JoinGroupEvent(cid, gid));
				break;
			case 7: 	// LeaveGroupEvent
				System.out.print("Gid: ");
				gid = sc.nextInt();
				Connection.sendObject(clientSocket, new LeaveGroupEvent(cid, gid));
				break;
			case 8:		// SendMessageEvent
				System.out.print("Gid: ");
				gid = sc.nextInt();
				System.out.print("Text: ");
				String text = sc.next();
				Connection.sendObject(clientSocket, new SendMessageEvent(cid, gid, text));
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
				threadClosed = true;
				
				try {
					clientSocket.close();
				} catch (Exception e) {}
				
				break;
			}
		}
		
		sc.close();
	}
	
	private static void print() {
		System.out.println("-------------------------------");
		System.out.println("Request List");
		System.out.println("Current cid: " + (cid == -1 ? "Not Set" : cid));
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

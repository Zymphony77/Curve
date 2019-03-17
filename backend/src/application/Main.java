package application;

import java.net.*; 
import java.io.*; 
import java.util.*;
import java.util.ArrayList;

import model.*;

public class Main {
	
	static Main main = new Main();
	
	static Scanner sc = new Scanner(System.in);
	
	public static String name;
	public static String ipAddress = "239.0.0.0";
	public static InetAddress ip;
	public static int defaultPort = 49151;
	
	ArrayList<Group> groups;
	ArrayList<ReadThread> readThreads;
	GroupsThread groupsThread;
	boolean isInChat;
	
	public Main() {
		this.readThreads = new ArrayList<ReadThread> (0);
		this.isInChat = false;
	}
	
	public static void main(String[] args) {
		main.groups = new ArrayList<Group> (0);
		try {
			ip = InetAddress.getByName(ipAddress);
			
			MulticastSocket socket = new MulticastSocket(defaultPort);
			socket.setTimeToLive(0);
			socket.joinGroup(ip);
			
			main.groupsThread = new GroupsThread(socket, ip, defaultPort);
			Thread t = new Thread(main.groupsThread);
			t.start();
		} catch (IOException e) {}
		
		System.out.print("Enter your name: ");
		name = sc.nextLine();
		
		while (true) {
			loadGroups();
			
			String[] cmd = sc.nextLine().split(" ");
			
			if (cmd[0].equals("create")) {
				createGroup(cmd[1]);
			} else if (cmd[0].equals("join")) {
				joinGroup(cmd[1]);
			} else if (cmd[0].equals("leave")) {
				leaveGroup(cmd[1]);
			} else if (cmd[0].equals("enter")) {
				enterGroupChat(cmd[1]);
			} else if (cmd[0].equals("exit")) {
				break;
			}
		}
	}

	private static void showMenu() {
		System.out.println("");
		System.out.println("--------------------------------------------------------");
		System.out.println("");
		System.out.println("Welcome to Curve, " + name);
		System.out.println("Menu");
		System.out.println("1. Create a new group (create <group name>)");
		System.out.println("2. Join group (join <group name>)");
		System.out.println("3. Leave group (leave <group name>)");
		System.out.println("4. Enter group's chat (enter <group name>)");
		System.out.println("");
		showAllGroup();
		System.out.println("");
		showJoinedGroup();
		System.out.println("");
		System.out.print("Enter a command: ");
	}
	
	private static void showAllGroup() {
		System.out.println("All group (" + main.groups.size() + "):");
		for (Group group : main.groups) {
			System.out.println("- " + group.getGroupName() + " (" + group.getMemberSize() + " members)");
		}
	}
	
	private static void showJoinedGroup() {
		int cnt = 0;
		for (Group group : main.groups) {
			if (group.isMemberIn(name)) {
				cnt++;
			}
		}
		System.out.println("Joined groups (" + cnt + "):");
		for (Group group : main.groups) {
			if (group.isMemberIn(name)) {
				System.out.println("- " + group.getGroupName() + " (" + group.getMemberSize() + " members)");
			}
		}
	}
	
	private static void createGroup(String groupName) {
		for (Group group : main.groups) {
			if (group.getGroupName().equals(groupName)) {
				return;
			}
		}
		int groupId = main.groups.size() + 1024;
		Group newGroup = new Group(groupId, groupName);
		main.groups.add(newGroup);
		joinGroup(groupName);
		
		saveGroups();
	}
	
	private static void joinGroup(String groupName) {
		try {
			for (Group group : main.groups) {
				if (group.getGroupName().equals(groupName)) {
					group.addMember(name);
					
					MulticastSocket socket = new MulticastSocket(group.getGroupId());
					socket.setTimeToLive(0);
					socket.joinGroup(ip);
					
					ReadThread readThread = new ReadThread(socket, ip, group.getGroupId());
					sendMessage(readThread, name + " joined the group.");
					main.readThreads.add(readThread);
					Thread t = new Thread(readThread);
					t.start();
					
					break;
				}
			}
			saveGroups();
		} catch(SocketException se) {
			System.out.println("Error creating socket");
			se.printStackTrace();
		} catch(IOException ie) {
			System.out.println("Error reading/writing from/to socket");
			ie.printStackTrace();
		}
	}
	
	private static void leaveGroup(String groupName) {
		for (Group group : main.groups) {
			if (group.getGroupName().equals(groupName)) {
				group.removeMember(name);
				
				for (ReadThread readThread : main.readThreads) {
					if (group.getGroupId() == readThread.getPort()) {
						try {
							sendMessage(readThread, name + " left the group.");
							
							readThread.setFinish(true);
							readThread.getSocket().leaveGroup(ip);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
				main.readThreads.removeIf(readThread -> (group.getGroupId() == readThread.getPort()));
				break;
			}
		}
		saveGroups();
	}
	
	private static void enterGroupChat(String groupName) {
		for (Group group : main.groups) {
			if (group.getGroupName().equals(groupName)) {
				if (group.isMemberIn(name)) {
					for (ReadThread readThread : main.readThreads) {
						if (group.getGroupId() == readThread.getPort()) {
							System.out.println("");
							System.out.println("--------------------------------------------------------");
							System.out.println("");
							System.out.println("Group: " + groupName);
							System.out.println("Type 'exit' to exit this chat.");
							
							showUnreadMessages(readThread);
							main.isInChat = true;
							readThread.setInChat(main.isInChat);
							
							while(true) {
								System.out.print("> ");
								String message = sc.nextLine();
								if (message.equals("exit")) {
									main.isInChat = false;
									readThread.setInChat(main.isInChat);
									readThread.resetUnreadMessages();
									break;
								}
								message = name + " " + message;
								sendMessage(readThread, message);
							}
							break;
						}
					}
				} else {
					System.out.println("You are not in this group.");
				}
				
				break;
			}
		}
	}
	
	private static void saveGroups() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("groups.tmp"));
			oos.writeObject(main.groups);
			oos.close();
			
			byte[] buffer = name.getBytes();
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, ip, defaultPort);
			main.groupsThread.getSocket().send(datagram);
		} catch (IOException e) {}
	}
	
	public static void loadGroups() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("groups.tmp"));
			main.groups = (ArrayList<Group>) ois.readObject();
			ois.close();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {}
		if (!main.isInChat) {
			showMenu();
		}
	}
	
	private static void sendMessage(ReadThread readThread, String message) {
		try {
			byte[] buffer = message.getBytes();
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, ip, readThread.getPort());
			readThread.getSocket().send(datagram);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void showUnreadMessages(ReadThread readThread) {
		ArrayList<Message> unreadMessages = readThread.getUnreadMessages();
		if (unreadMessages.size() > 0) {
			System.out.println("You have " + unreadMessages.size() + " unread messages.");
		}
		for (Message message : unreadMessages) {
			if (!message.getSender().equals(name)) {
				System.out.println(message.getSender() + ": " + message.getMessage() + "\t" + message.getTimestamp());
			} else {
				System.out.println("You: " + message.getMessage() + "\t" + message.getTimestamp());
			}
		}
		System.out.println("");
	}
}

package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import application.Main;
import model.Message;

public class ReadThread implements Runnable {
	private boolean isInChat;
	private boolean isFinish;
	private MulticastSocket socket;
	private InetAddress ip;
	private int port;
	private static final int MAX_LEN = 1000;
	private ArrayList<Message> unreadMessages;
	
	public ReadThread(MulticastSocket socket, InetAddress ip, int port) {
		this.isFinish = false;
		this.socket = socket;
		this.ip = ip;
		this.port = port;
		this.unreadMessages = new ArrayList<Message> (0);
	} 
	
	@Override
	public void run() {
		while(true) {
			if (isFinish) {
				continue;
			}
			byte[] buffer = new byte[MAX_LEN];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, ip, port);
			try {
				socket.receive(datagram); 
				String[] text = new String(buffer, 0, datagram.getLength(), "UTF-8").split(" ", 2);
				Message message = new Message(text[0], text[1]);
				unreadMessages.add(message);
				
				if (isInChat && !text[0].equals(Main.name)) {
					System.out.println(message.getSender() + ": " + message.getMessage() + "\t" + message.getTimestamp());
					System.out.print("> ");
				}
			}
			catch(IOException e) {
				System.out.println("Socket closed!");
			} 
		}
	}
	
	public MulticastSocket getSocket() {
		return socket;
	}

	public void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}

	public int getPort() {
		return port;
	}

	public ArrayList<Message> getUnreadMessages() {
		return unreadMessages;
	}
	
	public void resetUnreadMessages() {
		this.unreadMessages = new ArrayList<Message> (0);
	}
	
	public void setInChat(boolean isInChat) {
		this.isInChat = isInChat;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
}
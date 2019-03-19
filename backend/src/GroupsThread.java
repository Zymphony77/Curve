package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import application.Main;
import model.Message;

public class GroupsThread implements Runnable {
	private MulticastSocket socket;
	private InetAddress ip;
	private int port;
	private static final int MAX_LEN = 1000;
	
	public GroupsThread(MulticastSocket socket, InetAddress ip, int port) {
		this.socket = socket;
		this.ip = ip;
		this.port = port;
	} 
	
	@Override
	public void run() {
		while(true) {
			byte[] buffer = new byte[MAX_LEN];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, ip, port);
			try {
				socket.receive(datagram);
				String message = new String(buffer, 0, datagram.getLength(), "UTF-8");
				if (!message.equals(Main.name)) {
					Main.loadGroups();
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
}
package server;

import java.util.Vector;
import java.sql.Timestamp;

public class GroupMessageData {
	public class Message {
		private int cid;
		private Timestamp time;
		private String text;
		
		public Message(int cid, Timestamp time, String text) {
			this.cid = cid;
			this.time = time;
			this.text = text;
		}
		
		public Timestamp getTime() {
			return time;
		}
		
		public int getCid() {
			return cid;
		}
		
		public String getText() {
			return text;
		}
	}
	
	private int gid;
	private Vector<Message> messageVector;
	
	public GroupMessageData(int gid) {
		this.gid = gid;
		this.messageVector = new Vector<>();
	}
	
	public void addMessage(int cid, Timestamp time, String text) {
		messageVector.add(new Message(cid, time, text));
	}
	
	public int getGid() {
		return gid;
	}
	
	public Vector<Message> getMessageVector() {
		return messageVector;
	}
}

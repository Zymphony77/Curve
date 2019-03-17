package model;

import java.sql.Timestamp;

public class Message {
	private String sender;
	private String message;
	private Timestamp timestamp;
	
	public Message(String sender, String message) {
		this.sender = sender;
		this.message = message;
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

}

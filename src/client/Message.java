package client;

import java.sql.Timestamp;

public class Message {
	private String userName;
	private String message;
	private Timestamp time;
	private int status;
	
	public Message(String userName,Timestamp time, String message, int status) {
		this.userName = userName;
		this.message = message;
		this.time = time;
		this.status = status;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String toString() {
		if(status==0) {
			return userName+" : "+message+"     "+time;
		}else {
			return time+"     "+message+" : "+userName;
		}
	}
}

package utility.event;

import java.sql.Timestamp;

public class NewMessageEvent extends Event {
	private int gid;
	private int cid;
	private String clientName;
	private Timestamp time;
	private String message;
	
	public NewMessageEvent(int gid, int cid, String clientName, Timestamp time, String message) {
		this.gid = gid;
		this.cid = cid;
		this.clientName = clientName;
		this.time = new Timestamp(time.getTime());
		this.message = message;
	}
	
	public int getGid() {
		return gid;
	}
	
	public int getCid() {
		return cid;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public String getMessage() {
		return message;
	}
}

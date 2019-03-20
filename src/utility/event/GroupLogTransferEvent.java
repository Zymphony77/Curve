package utility.event;

import java.sql.Timestamp;

public class GroupLogTransferEvent extends Event {
	private int gid;
	private int cid;
	private Timestamp time;
	private String event;
	
	public GroupLogTransferEvent(int gid, int cid, Timestamp time, String event) {
		this.gid = gid;
		this.cid = cid;
		this.time = time;
		this.event = event;
	}
	
	public int getGid() {
		return gid;
	}
	
	public int getCid() {
		return cid;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public String getEvent() {
		return event;
	}
}

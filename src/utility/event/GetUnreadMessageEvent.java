package utility.event;

import java.sql.Timestamp;

public class GetUnreadMessageEvent extends Event {
	private int cid;
	private Timestamp latestTimestamp;
	
	public GetUnreadMessageEvent(int cid, Timestamp lastestTimestamp) {
		this.cid = cid;
		this.latestTimestamp = new Timestamp(latestTimestamp.getTime());
	}
	
	public int getCid() {
		return cid;
	}
	
	public Timestamp getLatestTimestamp() {
		return latestTimestamp;
	}
}

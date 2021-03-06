package utility.event;

import java.sql.Timestamp;

public class GetUpdateEvent extends Event {
	private int cid;
	private Timestamp latestTimestamp;
	
	public GetUpdateEvent(int cid, Timestamp latestTimestamp) {
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

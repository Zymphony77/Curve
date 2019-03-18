package utility.event;

public class LeaveGroupEvent extends Event {
	private int cid;
	private int gid;
	
	public LeaveGroupEvent(int cid, int gid) {
		this.cid = cid;
		this.gid = gid;
	}
	
	public int getCid() {
		return cid;
	}
	
	public int getGid() {
		return gid;
	}
}

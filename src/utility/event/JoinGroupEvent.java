package utility.event;

public class JoinGroupEvent extends Event {
	private int cid;
	private int gid;
	
	public JoinGroupEvent(int cid, int gid) {
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

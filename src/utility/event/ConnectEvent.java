package utility.event;

public class ConnectEvent extends Event {
	private int cid;
	
	public ConnectEvent(int cid) {
		this.cid = cid;
	}
	
	public int getCid() {
		return cid;
	}
}

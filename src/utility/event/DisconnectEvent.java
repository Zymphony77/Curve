package utility.event;

public class DisconnectEvent extends Event {
	private int cid;
	
	public DisconnectEvent(int cid) {
		this.cid = cid;
	}
	
	public int getCid() {
		return cid;
	}
}

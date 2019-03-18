package utility.event;

public class NewClientEvent extends Event {
	private int cid;
	
	public NewClientEvent(int cid) {
		this.cid = cid;
	}
	
	public int getCid() {
		return cid;
	}
}

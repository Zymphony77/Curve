package utility.event;

public class NewClientEvent extends Event {
	private int cid;
	private String clientName;
	
	public NewClientEvent(int cid, String clientName) {
		this.cid = cid;
		this.clientName = clientName;
	}
	
	public String getClientName() {
		return clientName;
	}

	public int getCid() {
		return cid;
	}
}

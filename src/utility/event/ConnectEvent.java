package utility.event;

public class ConnectEvent extends Event {
	private int cid;
	private String ipAddress;
	
	public ConnectEvent(int cid, String ipAddress) {
		this.cid = cid;
		this.ipAddress = ipAddress;
	}
	
	public int getCid() {
		return cid;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
}

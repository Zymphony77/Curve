package utility.event;

public class SendMessageEvent extends Event {
	private int cid;
	private int gid;
	private String message;
	
	public SendMessageEvent(int cid, int gid, String message) {
		this.cid = cid;
		this.gid = gid;
		this.message = message;
	}
	
	public int getCid() {
		return cid;
	}
	
	public int getGid() {
		return gid;
	}
	
	public String getMessage() {
		return message;
	}
}

package utility.event;

public class CreateGroupEvent extends Event {
	private int cid;
	private String groupName;
	
	public CreateGroupEvent(int cid, String groupName) {
		this.cid = cid;
		this.groupName = groupName;
	}
	
	public int getCid() {
		return cid;
	}
	
	public String getGroupName() {
		return groupName;
	}
}

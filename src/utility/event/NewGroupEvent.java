package utility.event;

public class NewGroupEvent extends Event {
	private int gid;
	private String groupName;
	
	public NewGroupEvent(int gid, String groupName) {
		this.gid = gid;
		this.groupName = groupName;
	}
	
	public int getGid() {
		return gid;
	}
	
	public String getGroupName() {
		return groupName;
	}
}

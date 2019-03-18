package utility.event;

public class UpdateGroupEvent extends Event {
	private int gid;
	private String groupName;
	
	public UpdateGroupEvent(int gid, String groupName) {
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

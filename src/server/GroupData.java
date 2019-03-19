package server;

public class GroupData {
	private int gid;
	private String groupName;
	
	public GroupData(int gid, String groupName) {
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

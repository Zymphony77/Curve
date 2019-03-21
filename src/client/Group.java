package client;

public class Group {
	private int gid;
	private String groupName;
	
	public Group(int gid, String groupName) {
		this.gid = gid;
		this.groupName = groupName;
	}

	public int getGid() {
		return gid;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return groupName;
	}
	
	
}

package model;

import java.util.HashSet;

public class GroupMemberData {
	private int gid;
	private HashSet<Integer> cidSet;
	
	public GroupMemberData(int gid) {
		this.gid = gid;
		this.cidSet = new HashSet<>();
	}
	
	public void addMember(int cid) {
		cidSet.add(cid);
	}
	
	public void removeMember(int cid) {
		cidSet.remove(cid);
	}
	
	public int getGid() {
		return gid;
	}
	
	public HashSet<Integer> getCidSet() {
		return cidSet;
	}
}

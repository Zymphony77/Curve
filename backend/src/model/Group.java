package model;

import java.io.Serializable;
import java.util.*;

public class Group implements Serializable {
	private int groupId;
	private String groupName;
	private HashSet<String> member;
	
	public Group(int groupId, String groupName) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.member = new HashSet<String>();
	}

	public int getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public HashSet<String> getMember() {
		return member;
	}
	
	public int getMemberSize() {
		return member.size();
	}
	
	public boolean isMemberIn(String name) {
		return member.contains(name);
	}
	
	public void addMember(String name) {
		member.add(name);
	}
	
	public void removeMember(String name) {
		member.remove(name);
	}
}

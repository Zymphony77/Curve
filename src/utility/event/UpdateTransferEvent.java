package utility.event;

import java.util.HashMap;
import java.util.Vector;

import model.GroupMessageData;

public class UpdateTransferEvent extends Event {
	private Vector<Vector<Object>> groupData;
	private HashMap<Integer, GroupMessageData> unread;
	
	public UpdateTransferEvent(Vector<Vector<Object>> groupData, HashMap<Integer, GroupMessageData> unread) {
		this.groupData = groupData;
		this.unread = unread;
	}
	
	public Vector<Vector<Object>> getGroupData() {
		return groupData;
	}
	
	public HashMap<Integer, GroupMessageData> getUnread() {
		return unread;
	}
}

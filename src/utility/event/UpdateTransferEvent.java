package utility.event;

import java.util.HashMap;
import java.util.Vector;

public class UpdateTransferEvent extends Event {
	private Vector<Vector<Object>> groupData;
	private HashMap<Integer, Vector<NewMessageEvent>> unread;
	
	public UpdateTransferEvent(Vector<Vector<Object>> groupData, 
			HashMap<Integer, Vector<NewMessageEvent>> unread) {
		this.groupData = groupData;
		this.unread = unread;
	}
	
	public Vector<Vector<Object>> getGroupData() {
		return groupData;
	}
	
	public HashMap<Integer, Vector<NewMessageEvent>> getUnread() {
		return unread;
	}
}

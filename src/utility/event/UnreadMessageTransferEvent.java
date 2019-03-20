package utility.event;

import java.util.HashMap;
import java.util.Vector;

import model.GroupMessageData;

public class UnreadMessageTransferEvent {
	private HashMap<Integer, GroupMessageData> unread;
	
	public UnreadMessageTransferEvent(HashMap<Integer, GroupMessageData> unread) {
		this.unread = unread;
	}
	
	public HashMap<Integer, GroupMessageData> getUnread() {
		return unread;
	}
}

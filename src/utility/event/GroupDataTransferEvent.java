package utility.event;

import java.util.Vector;

public class GroupDataTransferEvent {
	private Vector<Vector<Object>> groupData;
	
	public GroupDataTransferEvent(Vector<Vector<Object>> groupData) {
		this.groupData = groupData;
	}
	
	public Vector<Vector<Object>> getGroupData() {
		return groupData;
	}
}

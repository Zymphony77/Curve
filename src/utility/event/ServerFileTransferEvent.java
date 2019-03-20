package utility.event;

import java.util.Vector;

public class ServerFileTransferEvent extends Event {
	private Vector<Vector<Object>> clientVector;
	private Vector<Vector<Object>> groupVector;
	private Vector<Vector<Object>> groupMessageVector;
	private Vector<Vector<Object>> groupLogVector;
	
	public ServerFileTransferEvent(Vector<Vector<Object>> clientVector, 
			Vector<Vector<Object>> groupVector, 
			Vector<Vector<Object>> groupMessageVector, 
			Vector<Vector<Object>> groupLogVector) {
		this.clientVector = clientVector;
		this.groupVector = groupVector;
		this.groupMessageVector = groupMessageVector;
		this.groupLogVector = groupLogVector;
	}
	
	public Vector<Vector<Object>> getClientVector() {
		return clientVector;
	}
	
	public Vector<Vector<Object>> getGroupVector() {
		return groupVector;
	}
	
	public Vector<Vector<Object>> getGroupMessageVector() {
		return groupMessageVector;
	}
	
	public Vector<Vector<Object>> getGroupLogVector() {
		return groupLogVector;
	}
}

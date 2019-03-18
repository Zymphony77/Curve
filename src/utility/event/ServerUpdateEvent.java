package utility.event;

import java.util.Vector;

public class ServerUpdateEvent extends Event {
	private String fileName;
	private Vector<Vector<Object>> info;
	
	public ServerUpdateEvent(String fileName, Vector<Vector<Object>> info) {
		this.fileName = fileName;
		
		this.info = new Vector<>(info.size());
		for (int i = 0; i < info.size(); ++i) {
			this.info.setElementAt(new Vector<>(info.elementAt(i)), i);
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public Vector<Vector<Object>> getInfo() {
		return info;
	}
}

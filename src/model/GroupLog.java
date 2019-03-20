package model;

import java.sql.Timestamp;
import java.util.Vector;

public class GroupLog {
	public class Log {
		private int cid;
		private Timestamp time;
		private String event;
		
		public Log(int cid, Timestamp time, String event) {
			this.cid = cid;
			this.time = time;
			this.event = event;
		}
		
		public int getCid() {
			return cid;
		}
		
		public Timestamp getTime() {
			return time;
		}
		
		public String getEvent() {
			return event;
		}
	}
	
	private int gid;
	private Vector<Log> logVector;
	
	public GroupLog(int gid) {
		this.gid = gid;
		this.logVector = new Vector<>();
	}
	
	public void addLog(int cid, Timestamp time, String event) {
		logVector.add(new Log(cid, time, event));
	}
	
	public int getGid() {
		return gid;
	}
	
	public Vector<Log> getLogVector() {
		return logVector;
	}
}

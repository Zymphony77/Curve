package model;

public class ClientData {
	private int cid;
	private String clientName;
	
	public ClientData(int cid, String clientName) {
		this.cid = cid;
		this.clientName = clientName;
	}
	
	public int getCid() {
		return cid;
	}
	
	public String getClientName() {
		return clientName;
	}
}

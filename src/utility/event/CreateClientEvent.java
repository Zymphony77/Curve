package utility.event;

public class CreateClientEvent extends Event {
	private String clientName;
	
	public CreateClientEvent(String clientName) {
		this.clientName = clientName;
	}
	
	public String getClientName() {
		return clientName;
	}
}

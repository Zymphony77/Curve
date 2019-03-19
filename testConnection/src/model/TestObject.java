package model;

import java.io.Serializable;

public class TestObject implements Serializable {
	private int id;
	private String message;
	
	public TestObject (int id, String message) {
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}
}
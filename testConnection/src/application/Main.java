package application;

import java.net.*;
import java.io.*;

import connection.*;
import model.*;

public class Main {
	
	public static String defaultIp = "localhost";
	public static int defaultPort = 1234;
	
	public static void main(String[] args) {
		new TestServer();
		new TestClient();
	}
}

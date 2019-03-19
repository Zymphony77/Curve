package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import connection.*;
import server.*;
import utility.csv.*;	

public class Main extends Application {
	private static ServerSocket listenSocket;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			listenSocket = new ServerSocket(ServerLogic.getServerPort());
			
			while(true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("New Connection from Client:" + clientSocket.getInetAddress());
				ServerThread c = new ServerThread(clientSocket);
			}
		} catch(IOException e) {}
		
		launch(args);
	}
	
	@Override
	public void stop() {
		try {
			listenSocket.close();
		} catch (Exception e) {}
	}
}

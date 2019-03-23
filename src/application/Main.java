package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;

import client.Client;
import client.ClientGUI;
import client.ClientLogic;
import client.ClientThread;
import connection.*;
import server.*;

import utility.csv.*;
import utility.event.NewMessageEvent;	

public class Main extends Application {
	public static boolean isConected = false;
	private static Label curve_heading;
	private static Label username_label;
	private static TextField username_field;
	private static Button login_button;
	private static ServerSocket listenSocket;
	private static Stage stage;
	private Vector<Vector<Object>> data;
	public final static String FILEPATH = "src/data/";
	private static ClientLogic clientLogic;
	private static ClientGUI gui;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			data = CSVHandler.readCSV(FILEPATH + "ClientInfo.csv");
			System.out.println(data);
			gui = new ClientGUI((String) data.get(0).get(1), (int) data.get(0).get(0));
			clientLogic.setCid((int)data.get(0).get(0));
			if(clientLogic.getCid() > 0) {
				clientLogic.connect(clientLogic.getCid());
			}
			loadOldMessage();
			//ClientLogic.createClient((String) data.get(0).get(1));
			gui.start(new Stage());
			
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			try {
				utility.csv.CSVHandler.createFile(FILEPATH + "ClientInfo.csv");
				stage = primaryStage;
				Pane root = new Pane();
				
				Image background = new Image("background.jpg");
				ImageView backgroundShow = new ImageView(background);
				backgroundShow.setFitHeight(378);
				backgroundShow.setFitWidth(310);
				backgroundShow.toBack();
				root.getChildren().add(backgroundShow);
				
				Image logo = new Image("logo.png");
				ImageView logoShow = new ImageView(logo);
				logoShow.setFitHeight(65);
				logoShow.setFitWidth(65);
				logoShow.setLayoutX(125);
				logoShow.setLayoutY(80);
				logoShow.toFront();
				root.getChildren().add(logoShow);
				
		      	Scene scene = new Scene(root, 310, 328);
		      	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		      	
		      	curve_heading = new Label("Welcome to CURVE");
		      	curve_heading.setLayoutX(40);
		      	curve_heading.setLayoutY(25);
		      	curve_heading.setFont(new Font(25));
		        root.getChildren().add(curve_heading);
		        
		        username_label = new Label("Enter username");
		    	    username_label.setLayoutX(100);
		      	username_label.setLayoutY(170);
		      	username_label.setFont(new Font(15));
		    	    root.getChildren().add(username_label);
		    	    
		    	    username_field = new TextField();
		    	    username_field.setPrefSize(200, 30);
		        	username_field.setLayoutX(60);
		        	username_field.setLayoutY(200);
		        	root.getChildren().add(username_field);
		        	
		        	login_button = new Button();
		        	login_button.setLayoutX(40);
		        	login_button.setLayoutY(250);
		        	login_button.setText("CONNECT");
		        	login_button.setPrefSize(240, 20);
		        	root.getChildren().add(login_button);
		        
		        	primaryStage.setTitle("Curve");
		        primaryStage.setScene(scene);
		        primaryStage.setResizable(false);
				primaryStage.show();
				
				root.setOnKeyPressed(new EventHandler<KeyEvent>()
		        {
		            @Override
		            public void handle(KeyEvent key)
		            {
		                if (key.getCode().equals(KeyCode.ENTER))
		                {
		                	try {
		                		ClientLogic.getInstance().createClient(username_field.getText());
		            			while(!isConected) {
		            				Thread.sleep(100);
		            			}
		            			connect();
						} catch (Exception e) {}
		                }
		            }
		        });
		          
		        login_button.setOnAction(new EventHandler<ActionEvent>() {
		            @Override public void handle(ActionEvent e) {
		            	try {
		            			ClientLogic.getInstance().createClient(username_field.getText());
		            			while(!isConected) {
		            				Thread.sleep(100);
		            			}
		            			connect();
						} catch (Exception e1) {}
		            }
		        });
				
			} catch(Exception ee) {
				ee.printStackTrace();
			}
			
		}
	}
	
	public void connect() {
		stage.hide();
		try {
			data = CSVHandler.readCSV(FILEPATH + "ClientInfo.csv");
			gui = new ClientGUI(username_field.getText(), (int) data.get(0).get(0));
			gui.start(new Stage());
		} catch (FileNotFoundException e) {}
	}
	
	public void loadOldMessage() {
		try {
			data = CSVHandler.readCSV(FILEPATH + "groupLst.csv");
			for(Vector<Object> i : data) {
				int gid = (int) i.get(0);
				try {
					Vector<Vector<Object>> tempp = CSVHandler.readCSV(FILEPATH + "MessageListOf"+gid+".csv");
					ClientGUI.OldMessages.put(gid, new Vector<>());
					for(Vector<Object> j : tempp) {
						ClientGUI.OldMessages.get(gid).add(new NewMessageEvent(gid, 
								(int) j.get(0), (String) j.get(1), new Timestamp((long) j.get(2)), (String) j.get(3)));
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		clientLogic = ClientLogic.getInstance();
		
		ClientThread c = new ClientThread(clientLogic.getSocket());
		
		launch(args);
	}

	public static ClientGUI getGui() {
		return gui;
	}
}

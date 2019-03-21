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
import java.util.*;


import client.ClientGUI;
import client.ClientLogic;

import connection.*;
import server.*;

import utility.csv.*;	

public class Main extends Application {
	public static boolean isConected = false;
	private static Label curve_heading;
	private static Label username_label;
	private static TextField username_field;
	private static Button login_button;
	private static ServerSocket listenSocket;
	private static Stage stage;
	private Vector<Vector<Object>> data;
	private final static String FILEPATH = "data/csv/";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			data = CSVHandler.readCSV(FILEPATH + "Client.csv");
			ClientGUI gui = new ClientGUI((String) data.get(0).get(1), (int) data.get(0).get(0));
			//ClientLogic.createClient((String) data.get(0).get(1));
			gui.start(new Stage());
			
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			try {
				utility.csv.CSVHandler.createFile(FILEPATH + "Client.csv");
				stage = primaryStage;
				Pane root = new Pane();
				
				Image background = new Image("file:res/background.jpg");
				ImageView backgroundShow = new ImageView(background);
				backgroundShow.setFitHeight(378);
				backgroundShow.setFitWidth(310);
				backgroundShow.toBack();
				root.getChildren().add(backgroundShow);
				
				Image logo = new Image("file:res/logo.png");
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
		                			test();
		                			//ClientLogic.createClient(username_field.getText());
//		                			while(!isConected) {
//		                				connect();
//		                			}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                }
		            }
		        });
		          
		        login_button.setOnAction(new EventHandler<ActionEvent>() {
		            @Override public void handle(ActionEvent e) {
		            	try {
		            			//test();
		            			ClientLogic.createClient(username_field.getText());
		            			while(!isConected) {
		            				connect();
		            			}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            }
		        });
				
			} catch(Exception ee) {
				ee.printStackTrace();
			}
			
		}
			
	}
	
	public void test() throws Exception {
		stage.hide();
		ClientGUI gui = new ClientGUI("Pooh", 10);
		gui.start(new Stage());
	}
	
	public void connect() {
		stage.hide();
		try {
			data = CSVHandler.readCSV(FILEPATH + "Client.csv");
			ClientGUI gui = new ClientGUI(username_field.getText(), (int) data.get(0).get(0));
			ClientLogic.createClient(username_field.getText());
			gui.start(new Stage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

<<<<<<< HEAD
import Client.ClientGUI;
import Client.ClientLogic;
||||||| merged common ancestors
=======
import connection.*;
import server.*;
>>>>>>> 03b97b3e641535370080bafa86ce75a5e3fe5091
import utility.csv.*;	

public class Main extends Application {
<<<<<<< HEAD
	private static Label curve_heading;
	private static Label username_label;
	private static TextField username_field;
	private static Button login_button;
	
||||||| merged common ancestors
=======
	private static ServerSocket listenSocket;
	
>>>>>>> 03b97b3e641535370080bafa86ce75a5e3fe5091
	@Override
	public void start(Stage primaryStage) {
		try {
			Pane root = new Pane();
	      	root.setStyle("-fx-background-color: #ffde87");
	      	Scene scene = new Scene(root, 350, 300);
	      	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	      	
	      	curve_heading = new Label("Curve");
	      	curve_heading.setLayoutX(110);
	      	curve_heading.setLayoutY(15);
	      	curve_heading.setFont(new Font(45));
	        root.getChildren().add(curve_heading);
	        
	        username_label = new Label("Username: ");
	    	    username_label.setLayoutX(50);
	      	username_label.setLayoutY(100);
	    	    root.getChildren().add(username_label);
	    	    
	    	    username_field = new TextField();
	        	username_field.setLayoutX(120);
	        	username_field.setLayoutY(97);
	        	root.getChildren().add(username_field);
	        	
	        	login_button = new Button();
	        	login_button.setLayoutX(65);
	        	login_button.setLayoutY(180);
	        	login_button.setText("Connect To Curve");
	        	login_button.setPrefSize(225, 25);
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
							//connect();
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
	            			test();
						//connect();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
	        });
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void test() throws Exception {
		ClientGUI gui = new ClientGUI("pooh", 123);
		gui.start(new Stage());
	}
	
	public static void connect() throws Exception {
		Vector<Vector<Object>> data = CSVHandler.readCSV("Client.csv");
		for (int i = 0; i < data.size(); i++) {
			if ((String) data.get(i).get(1) == username_field.getText()) {
				ClientGUI gui = new ClientGUI(username_field.getText(), (int) data.get(i).get(0));
				gui.start(new Stage());
		}else {
			ClientLogic.CreateClient(username_field.getText());
			ClientGUI gui = new ClientGUI(username_field.getText(), (int) data.get(data.size()-1).get(0));
			gui.start(new Stage());
		}}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

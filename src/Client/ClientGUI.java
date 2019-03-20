package Client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utility.csv.CSVHandler;


public class ClientGUI extends Application {
    private String username;
    private int cid;
    private int gid;
    private ClientLogic client;
    //private ListView<Message> chatBox;
    private TextArea textChat;
    private TextArea textGroup;
    private ListView<String> groupLstView;
    private ObservableList<String> groupObservableLst;
    private ArrayList<String> groupLst = new ArrayList<String>();

    //private ListView<Group> GroupName;
    
    
	public ClientGUI(String username, int cid) {
			this.username = username;
			this.cid = cid;
	}
	
	@Override
	public void start(Stage stage) {
			Pane root = new Pane();
			root.setStyle("-fx-background-color: #ffa88c");
			
			Scene scene = new Scene(root, 605, 450);
	   
			//text box of chat message
	    
			textChat = new TextArea();
			textChat.setText("Enter a message");
			textChat.setLayoutX(5);
		    	textChat.setLayoutY(400);
		    	textChat.setPrefSize(335, 45);
		    	textChat.requestFocus();
		    	textChat.setWrapText(true);
		    	root.getChildren().add(textChat);
		    	
		    	Image sendImage = new Image("file:res/send.png");
		    ImageView imageViewSend = new ImageView(sendImage);
		    imageViewSend.setFitWidth(35);
		    imageViewSend.setFitHeight(35);
		        
		    	Button sendButton = new Button("",imageViewSend);
		    	sendButton.setLayoutX(344);
		    	sendButton.setLayoutY(400);
		    	sendButton.setText("");
		    	//sendButton.setPrefHeight(35);
		    	//sendButton.setPrefWidth(35);
		    	sendButton.setPrefSize(35, 35);
		    	root.getChildren().add(sendButton);
		    	
		    	// text box of new group
		    	
		    	textGroup = new TextArea();
		    	textGroup.setText("Enter a group name");
		    	textGroup.setLayoutX(400);
		    	textGroup.setLayoutY(400);
		    	textGroup.setPrefSize(140, 45);
		    	textGroup.requestFocus();
		    	textGroup.setWrapText(true);
		    	root.getChildren().add(textGroup);
		    	
		    	Image newGroupImage = new Image("file:res/newGroup.png",45,45,false,false);
		    	ImageView imageViewNG = new ImageView(newGroupImage);
		    imageViewNG.setFitHeight(35);
		    	imageViewNG.setFitWidth(35);
		    	
		    	Button newGroupButton = new Button("",imageViewNG);
		    	newGroupButton.setLayoutX(545);
		    	newGroupButton.setLayoutY(400);
		    	newGroupButton.setText("");
		    	newGroupButton.setPrefSize(35, 35);
		    	root.getChildren().add(newGroupButton);
		    	
		    //	VBox groupLst = new VBox();
		    	String fileName = "GroupOf" + cid + ".csv";
		    	Vector<Vector<Object>> data;
				try {
					data = CSVHandler.readCSV(fileName);
					for (int r = 0; r < data.size(); r++) {
				    		groupObservableLst.add((String) data.get(r).get(1));
			    		
			    	}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
		 
//		    	ScrollPane scrollPane = new ScrollPane(groupLst);
//		    	scrollPane.setPrefSize(200, 390);
//		    	scrollPane.setLayoutX(400);
//		    	scrollPane.setLayoutY(5);
//		    	root.getChildren().add(scrollPane);
		    
		    	groupLstView = new ListView<String>();
		    	groupLstView.setLayoutX(400);
		    	groupLstView.setLayoutY(5);
		    	groupLstView.setPrefSize(200, 390);
		    groupObservableLst = FXCollections.observableArrayList();
		    groupLstView.setItems(groupObservableLst);
		    groupLstView.setStyle("");
		    root.getChildren().add(groupLstView);
	        
		    stage.setTitle("Curve");
	    		stage.setScene(scene);
	    		stage.setResizable(false);
	    	
	    		stage.show();
	    	
//	    	groupLstView.setOnMouseClicked(new EventHandler<MouseEvent>(){
//	    		 
//	             @Override
//	             public void handle(MouseEvent arg0) {
//	                 String text = groupLstView.getSelectionModel().getSelectedItem();
//	                 
//	             }
//	    
//	     });
	    	
	    	groupLstView.setOnMouseClicked((arg0) -> {
	                 String groupName = groupLstView.getSelectionModel().getSelectedItem();
	                 Vector<Vector<Object>> temp;
					try {
						temp = CSVHandler.readCSV("GroupLst.csv");
						int gid = 0;
		         		for (int i = 0; i < temp.size(); i++) {
		         			if ((String) temp.get(i).get(1) == groupName) {
		         				gid = (int) temp.get(i).get(0);
		         				break;
		         			}
		         		}
		                 displayChat(gid);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	             }
	    
	     );
	    	
	    	
	    	textChat.setOnKeyPressed(new EventHandler<KeyEvent>()
	        {
	            @Override
	            public void handle(KeyEvent ke)
	            {
	                if (ke.getCode().equals(KeyCode.ENTER))
	                {
	                		client.SendMessage(cid,gid,textChat.getText());
	                		displayMessage(textChat.getText(), 0);
						textChat.clear();
						ke.consume();
	                }
	            }
	        });

	    	  sendButton.setOnAction(new EventHandler<ActionEvent>() {
	            	  @Override
	                  public void handle(ActionEvent ee)
	                  {
	                      	client.SendMessage(cid,gid,textChat.getText());
							textChat.clear();
							ee.consume();
	                      }
	                  
	          });
	    	  
	    	  newGroupButton.setOnAction(new EventHandler<ActionEvent>() {
	        	  @Override
	              public void handle(ActionEvent ee)
	              {
	                  	try {
	  						client.CreateGroup(cid, textGroup.getText());
	  						displayGroupLst(textGroup.getText());
	  	              		textGroup.clear();
	  	              		ee.consume();
	  					} catch (IOException e) {
	  						// TODO Auto-generated catch block
	  						e.printStackTrace();
	  					}
	                  }
	              
	      });
	    	  
	    	  stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	              public void handle(WindowEvent we) {
	                  System.exit(0);
	              }
	          });    
		}
	
		public void displayChat(int gid) {
			
		}
		
		// status = 0 is myself, status = 1 is other
		public void displayMessage(String message, int status){	
			Platform.runLater(new Runnable(){
				@Override
				public void run() {
					
				}
			});
		}
		
		public void displayGroupLst(String Groupname) {
			groupLstView = new ListView<String>();
			groupLstView.setLayoutX(400);
	    		groupLstView.setLayoutY(5);
	    		groupLstView.setPrefSize(200, 390);
	    		groupObservableLst = FXCollections.observableArrayList();
	    		groupLstView.setItems(groupObservableLst);
	    		groupLstView.setStyle("");
	    		
	    		groupLst.add(Groupname);
				
	    		groupObservableLst.clear();
	    		groupObservableLst = FXCollections.observableArrayList(groupLst);
	    		groupLstView.setItems(groupObservableLst);	
	    		groupLstView.scrollTo(groupLst.size()-1); 
		}
	
	
}
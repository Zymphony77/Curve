package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import application.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utility.csv.CSVHandler;
import utility.event.Event;
import utility.event.NewMessageEvent;


public class ClientGUI extends Application {
    private String username;
    private int cid;
    private int gid = 0;
	private ClientLogic client;
    private TextArea textChat;
    private TextArea textGroup;
    private static ListView<Group> groupLstView;
    private static ObservableList<Group> groupObservableLst;
    private static ArrayList<Group> groupLst = new ArrayList<Group>();
    private static ListView<Group> groupLstViewAll;
    private static ObservableList<Group> groupObservableLstAll;
    private static ArrayList<Group> groupLstAll = new ArrayList<Group>();
    private static ListView<Message> historyLstView;
    private static ObservableList<Message> historyObservableLst;
    private static ArrayList<Message> history = new ArrayList<Message>();
    //test
    private static ArrayList<Message> test = new ArrayList<Message>();
    //--
    private Vector<Vector<Object>> groupOf = new Vector<Vector<Object>> ();
    private Vector<Vector<Object>> groupAll = new Vector<Vector<Object>> ();
    private String groupSelected = null;
    
    public static HashMap<Integer, Vector<NewMessageEvent>> OldMessages = new HashMap<Integer, Vector<NewMessageEvent>> ();
    public static HashMap<Integer, Vector<NewMessageEvent>> UnreadMessages = new HashMap<Integer, Vector<NewMessageEvent>> ();
    
	public ClientGUI(String username, int cid) {
			this.username = username;
			this.cid = cid;
	}
	
	public int getGid() {
		return gid;
	}
	
	@Override
	public void start(Stage stage) {
			Pane root = new Pane();
			//root.setStyle("-fx-background-color: #ffa88c");
			Image background = new Image("file:res/background2.png");
			ImageView backgroundShow = new ImageView(background);
			backgroundShow.setFitHeight(665);
			backgroundShow.setFitWidth(665);
			root.getChildren().add(backgroundShow);
			
			Scene scene = new Scene(root, 655, 450);
	   
			//text box of chat message
			
			HBox node = new HBox();
		    Circle online = new Circle(3);
		    Text space = new Text(" ");
		    Text text  = new Text(username+" is online");
		    online.setFill(Color.web("#0ad159"));
	        node.setSpacing(5);
	        node.setStyle("-fx-background-color: #FFFFFF;");
	        node.setAlignment(Pos.CENTER_LEFT);
	        node.getChildren().addAll(space, online, text);
	        node.setLayoutX(5);
	        node.setLayoutY(5);
	        node.setPrefSize(335, 25);
	        //node.setStyle("-fx-background-color: transparent;");
	        root.getChildren().add(node);
	    
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
		    	
		    	Image newGroupImage = new Image("file:res/newGroup.png");
		    	ImageView imageViewNG = new ImageView(newGroupImage);
		    imageViewNG.setFitHeight(35);
		    	imageViewNG.setFitWidth(35);
		    	
		    	Button newGroupButton = new Button("",imageViewNG);
		    	newGroupButton.setLayoutX(544);
		    	newGroupButton.setLayoutY(400);
		    	newGroupButton.setText("");
		    	newGroupButton.setPrefSize(35, 35);
		    	root.getChildren().add(newGroupButton);
		    	
		    	Image joinGroupImage = new Image("file:res/joinGroup.png");
		    	ImageView imageViewJG = new ImageView(joinGroupImage);
		    imageViewJG.setFitHeight(35);
		    	imageViewJG.setFitWidth(35);
		    	
		    	Button joinGroupButton = new Button("",imageViewJG);
		    	joinGroupButton.setLayoutX(599);
		    	joinGroupButton.setLayoutY(400);
		    	joinGroupButton.setText("");
		    	joinGroupButton.setPrefSize(35, 35);
		    	root.getChildren().add(joinGroupButton);
		    	
		    	Button leaveGroupButton = new Button("Leave");
		    	leaveGroupButton.setLayoutX(340);
		    	leaveGroupButton.setLayoutY(5);
		    	leaveGroupButton.setText("Leave");
		    	leaveGroupButton.setPrefSize(55, 10);
		    	
		    	
		    	
		    //	VBox groupLst = new VBox();
//		    	String fileName = "GroupOf" + cid + ".csv";
//		    	Vector<Vector<Object>> data;
//				try {
//					data = CSVHandler.readCSV(fileName);
//					for (int r = 0; r < data.size(); r++) {
//				    		groupObservableLst.add((String) data.get(r).get(1));
//			    		
//			    	}
//				} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			
		 
//		    	ScrollPane scrollPane = new ScrollPane(groupLst);
//		    	scrollPane.setPrefSize(200, 390);
//		    	scrollPane.setLayoutX(400);
//		    	scrollPane.setLayoutY(5);
//		    	root.getChildren().add(scrollPane);
			try {
				groupAll = CSVHandler.readCSV(Main.FILEPATH + "GroupLst.csv");
			} catch (FileNotFoundException e1) {
				utility.csv.CSVHandler.createFile(Main.FILEPATH + "GroupLst.csv");
			}
			
			try {
				groupOf = CSVHandler.readCSV(Main.FILEPATH + "GroupOf" + cid + ".csv");
			} catch (FileNotFoundException e1) {
				utility.csv.CSVHandler.createFile(Main.FILEPATH + "GroupOf" + cid + ".csv");
			}
			System.out.println(groupOf);
			System.out.println(groupAll);
		    	groupLstView = new ListView<Group>();
		    	groupLstView.setLayoutX(400);
		    	groupLstView.setLayoutY(5);
		    	groupLstView.setPrefSize(250, 195);
         	for (Vector<Object> i : groupOf) {
         		for(Vector<Object> j : groupAll) {
         			if(i.get(0)==j.get(0)) {
         				groupLst.add(new Group((int) i.get(0),(String) i.get(1)));
         			}
         				
         		}
        		}
		    groupObservableLst = FXCollections.observableArrayList(groupLst);
		    groupLstView.setItems(groupObservableLst);
		    groupLstView.setStyle("");
		    root.getChildren().add(groupLstView);
		    
		    
		    groupLstViewAll = new ListView<Group>();
	    		groupLstViewAll.setLayoutX(400);
		    	groupLstViewAll.setLayoutY(205);
		    	groupLstViewAll.setPrefSize(250, 190);
         	for (int j = 0; j < groupAll.size(); j++) {
         		groupLstAll.add(new Group((int) groupAll.get(j).get(0), (String) groupAll.get(j).get(1)));
        		}
		    groupObservableLstAll = FXCollections.observableArrayList(groupLstAll);
		    groupLstViewAll.setItems(groupObservableLst);
		    groupLstViewAll.setStyle("");
		    root.getChildren().add(groupLstViewAll);
	        
		    
		    historyLstView = new ListView<Message>();
		    historyLstView.setLayoutX(5);
		    historyLstView.setLayoutY(35);
		    historyLstView.setPrefSize(390, 360);
		    historyLstView.setCellFactory((param) -> {
	    		ListCell<Message> cell = new ListCell<Message>() {
	    			@Override
	    			protected void updateItem(Message item, boolean empty) {
	    				super.updateItem(item, empty);
	    				if (item != null) {
	    					System.out.println(item);
	    					setText(item.toString());
	    					if(item.getStatus() == true) {
	    						setBackground(new Background(new BackgroundFill(
		    							Color.ALICEBLUE, new CornerRadii(0), new Insets(0))));
	    					}else {
	    						setBackground(new Background(new BackgroundFill(
		    							Color.ANTIQUEWHITE, new CornerRadii(0), new Insets(0))));
	    					}
	    					
	    				}
	    			}
	    		};
	    		cell.setMouseTransparent(true);
	    		cell.setPrefWidth(387);
	    		cell.setWrapText(true);
	    		
	    		return cell;
	    });
		    //test
		    Timestamp testTime = new Timestamp(1);
		    test.add(new Message("Pooh", testTime, "Hello", true));
		    test.add(new Message("Ppeiei", testTime, "OMG", false));
		    //
	    		historyObservableLst = FXCollections.observableArrayList(test);
	    		historyLstView.setItems(historyObservableLst);
	    		historyLstView.setStyle("");
	    		root.getChildren().add(historyLstView);
		    
	    	
		    	groupLstView.setOnMouseClicked((arg0) -> {
		    				groupSelected = null;
		                String groupName = groupLstView.getSelectionModel().getSelectedItem().getGroupName();
		                Vector<Vector<Object>> temp;
		                try {
		                		root.getChildren().add(leaveGroupButton);
							temp = CSVHandler.readCSV(Main.FILEPATH + "GroupLst.csv");
			         		for (int i = 0; i < temp.size(); i++) {
			         			if ((String) temp.get(i).get(1) == groupName) {
			         				gid = (int) temp.get(i).get(0);
			         				break;
			         			}
			         		}
			         		 
			                showChat(gid);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		             }
		    
		     );
		    	
		    	groupLstViewAll.setOnMouseClicked((arg0) -> {
	                groupSelected = groupLstViewAll.getSelectionModel().getSelectedItem().getGroupName();
	                
		    	});
		    	
		    	
		    	textChat.setOnKeyPressed(new EventHandler<KeyEvent>()
		        {
		            @Override
		            public void handle(KeyEvent ke)
		            {
		            		groupSelected = null;
		                if (ke.getCode().equals(KeyCode.ENTER))
		                {
		                		if(gid!=0) {
		                			ClientLogic.getInstance().sendMessage(cid,gid,textChat.getText());
								textChat.clear();
								ke.consume();
		                		}
		                }
		            }
		        });
	
		    	 sendButton.setOnAction(new EventHandler<ActionEvent>() {
		            	  @Override
		                  public void handle(ActionEvent ee)
		                  {
		            		  	 groupSelected = null;
		            		  	 if(gid!=0) {
		                      	ClientLogic.getInstance().sendMessage(cid,gid,textChat.getText());
								textChat.clear();
								ee.consume();
		            		  	 }
		                  }
		                  
		          });
		    	  
		    	 newGroupButton.setOnAction(new EventHandler<ActionEvent>() {
		        	  @Override
		              public void handle(ActionEvent ee)
		              {
		        		  		System.out.println("IN");
		        		  		groupSelected = null;
		                  	try {
		                  		if(textGroup.getText()!="") {
		                  			ClientLogic.getInstance().createGroup(cid, textGroup.getText());
			  	              		textGroup.clear();
			  	              		ee.consume();
		                  		}
		  					} catch (Exception e) {
		  						// TODO Auto-generated catch block
		  						e.printStackTrace();
		  					}
		                  }
		              
		      });
		    	  
		    	 joinGroupButton.setOnAction(new EventHandler<ActionEvent>() {
		    		  public void handle(ActionEvent ee)
		    		  {
		    			  try {
		    				  if(gid!=0 && groupSelected!=null) {
		    					  ClientLogic.getInstance().join(cid, gid);
			    				  textGroup.clear();
			    				  ee.consume();
		    				  }
		    			  } catch (Exception e) {
		    				  e.printStackTrace();
		    			  }
		    		  }
		    	  });
		    	 
		    	 leaveGroupButton.setOnAction(new EventHandler<ActionEvent>() {
		    		  public void handle(ActionEvent ee)
		    		  {
		    			  groupSelected = null;
		    			  try {
		    				  if(gid!=0) {
		    					  deleteGroupLst(gid);
		    					  ClientLogic.getInstance().leave(cid, gid);
			    				  textGroup.clear();
			    				  ee.consume();
		    				  }
		    			  } catch (Exception e) {
		    				  e.printStackTrace();
		    			  }
		    		  }
		    	 });
		    	  
		    	 stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		              public void handle(WindowEvent we) {
		                  System.exit(0);
		              }
		     });
		    	 
		    	 stage.setTitle("Curve");
		    	 stage.setScene(scene);
		    	 stage.setResizable(false);
		    	
		    	 stage.show();
		}

//		public void displayMessage(String username, Timestamp time, String message, boolean status){
//			
//			Message chat = new Message(username, time, message, status);
//			history.add(chat);
//			
//			historyObservableLst.clear();
//	    		historyObservableLst = FXCollections.observableArrayList(history);
//	    		historyLstView.setItems(historyObservableLst);
//	    		historyLstView.scrollTo(history.size()-1);
//	    		
//		}

		public void addGroupLst(int gid, String groupname) {
			System.out.println("add group");
			groupLst.add(new Group(gid, groupname));
			
			groupObservableLst.clear();
			groupObservableLst = FXCollections.observableArrayList(groupLst);
			groupLstView.setItems(groupObservableLst);
			
			
			groupLstAll.add(new Group(gid, groupname));
			
			groupObservableLstAll.clear();
			groupObservableLstAll = FXCollections.observableArrayList(groupLstAll);
			groupLstViewAll.setItems(groupObservableLstAll);
			
		}

		public void deleteGroupLst(int gid) {
			for(int i=0;i<groupLst.size();i++) {
				if(groupLst.get(i).getGid()==gid) {
					groupLst.remove(groupLst.get(i));
					break;
				}
			}
			
			groupObservableLst.clear();
			groupObservableLst = FXCollections.observableArrayList(groupLst);
			groupLstView.setItems(groupObservableLst);
			
		}
		 
		
		public void showChat(int gid) {
			if(OldMessages.containsKey(gid)) {
				Vector<NewMessageEvent> temp = UnreadMessages.get(gid);
				for(NewMessageEvent i : temp) {
					OldMessages.get(gid).add(i);
				}
			Vector<NewMessageEvent> temp2 = OldMessages.get(gid);
			for(NewMessageEvent i:temp) {
				boolean status;
				if(i.getCid()==cid) {
					status = true;
				}else {
					status = false;
				}	
				Message message = new Message(i.getClientName(),i.getTime(),i.getMessage(),status);
				history.add(message);
			}
			
			historyObservableLst.clear();
	    		historyObservableLst = FXCollections.observableArrayList(history);
	    		historyLstView.setItems(historyObservableLst);
	    		historyLstView.scrollTo(history.size()-1);
			}else {
				System.out.println("Bugggggggg!!!");
				historyObservableLst.clear();
	    			historyObservableLst = FXCollections.observableArrayList();
	    			historyLstView.setItems(historyObservableLst);
			}
		}
		
		public void addOldMessage(NewMessageEvent message) {
			if(OldMessages.containsKey(message.getGid())) {
				Vector<NewMessageEvent> temp = OldMessages.get(message.getGid());
				temp.add(message);
				showChat(message.getGid());
			}else {
				Vector<NewMessageEvent> temp = new Vector<>();
				temp.add(message);
				OldMessages.put(message.getGid(), temp);
				
			}
			
		}
		
		public void addToUnreadMessage(NewMessageEvent message) {
			if(UnreadMessages.containsKey(message.getGid())){
				Vector<NewMessageEvent> temp = UnreadMessages.get(message.getGid());
				temp.add(message);
			}else {
				Vector<NewMessageEvent> temp = new Vector<>();
				temp.add(message);
				UnreadMessages.put(message.getGid(), temp);
			}
			
		}
		
		
}
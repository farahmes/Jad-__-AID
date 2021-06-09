package sma.com.enset.agents;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;



public class ConsumerContainer extends Application{
	
	protected ConsumerAgent consumerAgent;
	public ObservableList<String> observableList;
	public ListView<String> listView;

	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		primaryStage.setTitle("consumer agent");
		BorderPane borderPane = new BorderPane();
		HBox hbox = new HBox(30);
		Label labelBookName = new Label("Book name :");
		TextField textFieldBookName = new TextField();
		Button button = new Button("Send Message");
		hbox.getChildren().addAll(labelBookName, textFieldBookName, button);
		
		hbox.setPadding(new Insets(10));
		borderPane.setTop(hbox);
		
		observableList = FXCollections.observableArrayList();
		listView = new ListView<>(observableList);
		borderPane.setCenter(listView);
		borderPane.setPadding(new Insets(20));
		button.setOnAction(e->{
			String name= textFieldBookName.getText().trim();
			GuiEvent guiEvent = new GuiEvent(this, 1);
			guiEvent.addParameter(name);
			consumerAgent.onGuiEvent(guiEvent);
//			observableList.add(name);
		});
		
		Scene scene = new Scene(borderPane,600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void startContainer() throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		AgentContainer container=runtime.createAgentContainer(profile);
		
		AgentController consumerAgent = 
			container.createNewAgent("consumer", "sma.com.enset.agents.ConsumerAgent", 
					new Object[]{this});
		consumerAgent.start();
		System.out.println("consumer agent start ......");
	}
	
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(()->{
			observableList.add(aclMessage.getSender().getName() +
					" => " + 
					aclMessage.getContent());
		});
	}
	
	public static void main(String[] args) throws Exception {
		
		Application.launch(args);
	}

}

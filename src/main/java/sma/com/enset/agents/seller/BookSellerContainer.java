package sma.com.enset.agents.seller;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sma.com.enset.agents.buyer.BookBuyerAgent;

public class BookSellerContainer extends Application{
	
	protected BookSellerAgent bookSelleAgent;
	public ListView<String> listView;
	public ObservableList<String> observableList;

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		primaryStage.setTitle("Seller Book Agent...");
		BorderPane borderPane = new BorderPane();
		VBox vBox = new VBox(20);
		Scene scene = new Scene(borderPane,400,500);
		
		observableList = FXCollections.observableArrayList();
		listView = new  ListView<String>(observableList);
		
		vBox.getChildren().add(listView);
		vBox.setPadding(new Insets(10));
		borderPane.setCenter(vBox);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	

	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(()->{
			observableList.add(aclMessage.getSender().getName() +
					" => " + 
					aclMessage.getContent());
		});
	}
	
	
	
	
	//deployer seller book agent
	private void startContainer() throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		AgentContainer container=runtime.createAgentContainer(profile);
		AgentController consumerAgent = 
			container.createNewAgent("BookSellerAgent", BookSellerAgent.class.getName(), 
					new Object[]{this});
		consumerAgent.start();
		System.out.println("book Seller agent starts !!!.");
	}

	
	public static void main(String[] args) {
		Application.launch(args);
	}

}

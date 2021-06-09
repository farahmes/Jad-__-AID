package sma.com.enset.agents.seller;

import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BookSellerAgent extends GuiAgent {
	protected BookSellerContainer bookSellerContainer;
	protected Double bookPrice;
	public String bookName;
	public ACLMessage reply;
	public MessageTemplate messageTemplate;
	public Map<String, Double> bookData = new HashMap<String, Double>();
	public boolean notFound=false;
	@Override
	protected void setup() {
		this.initalisationData();
		if (this.getArguments().length == 1) {
			this.bookSellerContainer = (BookSellerContainer) this.getArguments()[0];
			this.bookSellerContainer.bookSelleAgent = this;
		}

		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);

		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {

				try {

					messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CFP),
							MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));

					ACLMessage aclMessage = receive(messageTemplate);
					if (aclMessage != null) {
						switch (aclMessage.getPerformative()) {
						case ACLMessage.CFP:
							bookPrice = bookData.get(aclMessage.getContent().toUpperCase());
							if (bookPrice != null) {
								notFound = false;
								bookName = aclMessage.getContent();
								bookSellerContainer.logMessage(aclMessage);
								reply = aclMessage.createReply();
								reply.setPerformative(ACLMessage.PROPOSE);
								reply.addReceiver(new AID("BookSellerAgent", AID.ISLOCALNAME));
								reply.setContent(String.valueOf(bookPrice));
								Thread.sleep(2000);
								send(reply);
							}else {
								notFound = true;
								bookNotFound(aclMessage);
							}
							
							break;
						case ACLMessage.ACCEPT_PROPOSAL:
							if(!notFound) {
							bookSellerContainer.logMessage(aclMessage);
							reply = aclMessage.createReply();
							reply.setPerformative(ACLMessage.CONFIRM);
							reply.addReceiver(new AID("BookSellerAgent", AID.ISLOCALNAME));
							reply.setContent("I Confirm ok Good luck");
							Thread.sleep(2000);
							send(reply);
							}else {
								bookNotFound(aclMessage);
							}
							
							break;
						}
						aclMessage = null;
					} else {
						block();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	public void bookNotFound(ACLMessage aclMessage) {
		try {
			reply = aclMessage.createReply();
			reply.setPerformative(ACLMessage.PROPOSE);
			reply.addReceiver(new AID("BookSellerAgent", AID.ISLOCALNAME));
			reply.setContent("Book not Found !!!");
			Thread.sleep(5000);
			send(reply);	
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public void initalisationData() {
		bookData.put("XML", 100.0);
		bookData.put("JAVA", 500.0);
		bookData.put("UML", 200.0);
		bookData.put("C++", 300.0);
		bookData.put("HTML", 200.0);
		bookData.put("CSS", 200.0);
	}

	@Override
	protected void beforeMove() {

	}

	@Override
	protected void afterMove() {

	}

	@Override
	protected void takeDown() {
		System.out.println("seller agent is going to die ...!!!!!!");

	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {

	}

	public static void main(String[] args) {

	}

}

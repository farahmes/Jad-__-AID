package sma.com.enset.agents.buyerToSellers;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BookBuyerToSellerAgent extends GuiAgent{
	protected BookBuyerToSellerContainer bookBuyerContainer;
	public MessageTemplate messageTemplate;
	public ACLMessage reply;
	public String bookName;
	public double price;
	public AID requester;
	public int requesterCounter=0;

	@Override
	protected void setup() {
		if (this.getArguments().length == 1) {
			bookBuyerContainer = (BookBuyerToSellerContainer) this.getArguments()[0];
			bookBuyerContainer.bookBuyerAgent = this;
		}

		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				try {
					messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
					ACLMessage aclMessage = receive(messageTemplate);
					if (aclMessage != null) {
						bookBuyerContainer.logMessage(aclMessage);
						String livre = aclMessage.getContent();
						requester = aclMessage.getSender();
						++requesterCounter;
						String conversationId = "transaction"+"-"+requesterCounter;
						parallelBehaviour.addSubBehaviour(
								new RequestBehaviour(myAgent, livre, requester, conversationId, bookBuyerContainer));						
					} else {
						block();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static void main(String[] args) {

	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {

	}

}

package sma.com.enset.agents.buyer;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BookBuyerAgent extends GuiAgent {

	protected BookBuyerContainer bookBuyerContainer;
	public MessageTemplate messageTemplate;
	public ACLMessage reply;
	public String bookName;
	public double price;
	public AID requester;

	@Override
	protected void setup() {
		if (this.getArguments().length == 1) {
			bookBuyerContainer = (BookBuyerContainer) this.getArguments()[0];
			bookBuyerContainer.bookBuyerAgent = this;
		}

		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				try {

					messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
							MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
									MessageTemplate.MatchPerformative(ACLMessage.CONFIRM)));
					ACLMessage aclMessage = receive(messageTemplate);
					if (aclMessage != null) {
						bookBuyerContainer.logMessage(aclMessage);
						reply = aclMessage.createReply();

						switch (aclMessage.getPerformative()) {
						case ACLMessage.REQUEST:
							requester = aclMessage.getSender();
							bookName = aclMessage.getContent();
							reply.setPerformative(ACLMessage.CFP);
							reply.addReceiver(new AID("BookSellerAgent", AID.ISLOCALNAME));
							reply.setContent(aclMessage.getContent());
							Thread.sleep(2000);
							send(reply);
							break;
						case ACLMessage.PROPOSE:
							price = Double.parseDouble(aclMessage.getContent());
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							reply.addReceiver(new AID("BookSellerAgent", AID.ISLOCALNAME));
							reply.setContent("I Accept ok ");
							Thread.sleep(2000);
							send(reply);
							break;
						case ACLMessage.CONFIRM:
							reply.setPerformative(ACLMessage.INFORM);
							reply.addReceiver(requester);
							reply.setContent("The book => " + bookName + " Costs => " + price);
							Thread.sleep(2000);
							send(reply);
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

	public static void main(String[] args) {

	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {

	}

}

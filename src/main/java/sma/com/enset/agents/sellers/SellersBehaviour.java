package sma.com.enset.agents.sellers;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SellersBehaviour extends CyclicBehaviour {
	public String conversationId;
	protected MessageTemplate template;
	protected BookSellersContainer container;

	public SellersBehaviour(Agent agent, String conversationId, BookSellersContainer container) {
		super(agent);
		this.conversationId = conversationId;
		this.container =container;
	}
	
	@Override
	public void action() {
		try {
			template = MessageTemplate.and(MessageTemplate.MatchConversationId(conversationId),
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
			ACLMessage aclMessage = myAgent.receive();
			if (aclMessage != null) {
				container.logMessage(aclMessage);
				System.out.println("--------------------------------");
				System.out.println("Conversation ID:" + aclMessage.getConversationId());
				System.out.println("Validation de la transaction .....");
				ACLMessage reply = aclMessage.createReply();
				reply.setPerformative(ACLMessage.CONFIRM);
				System.out.println("...... En cours");
				Thread.sleep(2000);
				myAgent.send(reply);
			} else {
				block();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

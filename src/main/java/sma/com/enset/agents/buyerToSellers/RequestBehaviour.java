package sma.com.enset.agents.buyerToSellers;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestBehaviour extends CyclicBehaviour {

	private String livre;
	private String conversationID;
	private AID requester;
	private List<AID> vendeurs = new ArrayList<AID>();
	private AID meilleureOffre;
	private double meilleurPrix;
	private int index;
	private int compteur = 0;
	private double prix;
	private BookBuyerToSellerContainer container;

	public RequestBehaviour(Agent agent, String livre, AID requester, String conversationID, BookBuyerToSellerContainer container) {
		super(agent);
		this.livre = livre;
		this.conversationID = conversationID;
		this.requester = requester;
		this.vendeurs = chercherService(myAgent, "book-selling");
		this.container=container;
		try {
			for (AID aid : this.vendeurs) {
				System.out.println("vendeur => " + aid.getName());
			}
			++compteur;
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);
			msg.setContent(livre);
			msg.setConversationId(this.conversationID);
			msg.addUserDefinedParameter("compteur", String.valueOf(compteur));
			for (AID aid : vendeurs) {
				msg.addReceiver(aid);
			}
			System.out.println("....... En cours");
			Thread.sleep(2000);
			index = 0;
			myAgent.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<AID> chercherService(Agent agent, String serviceType) {
		List<AID> vendeurs = new ArrayList<>();
		DFAgentDescription agentDescription = new DFAgentDescription();
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType(serviceType);
		agentDescription.addServices(serviceDescription);
		try {
			DFAgentDescription[] descriptions = DFService.search(agent, agentDescription);
			for (DFAgentDescription dfad : descriptions) {
				vendeurs.add(dfad.getName());
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return vendeurs;
	}

	@Override
	public void action() {
		try {

			MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId(conversationID),
					MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
							MessageTemplate.MatchPerformative(ACLMessage.CONFIRM)));
			
			ACLMessage aclMessage = myAgent.receive(template);
			
			if (aclMessage != null) {
				switch (aclMessage.getPerformative()) {
					case ACLMessage.PROPOSE:
						container.logMessage(aclMessage);
						prix = Double.parseDouble(aclMessage.getContent());
						System.out.println("***********************************");
						System.out.println("Conversation ID:" + aclMessage.getConversationId());
						System.out.println("Réception de l'offre :");
						System.out.println("From :" + aclMessage.getSender().getName());
						if (index == 0) {
							meilleurPrix = prix;
							meilleureOffre = aclMessage.getSender();
						} else {
							if (prix < meilleurPrix) {
								meilleurPrix = prix;//on va prendre le minimum
								meilleureOffre = aclMessage.getSender();
							}
						}
						++index;
						if (index == vendeurs.size()) {
							index = 0;
							System.out.println("-----------------------------------");
							System.out.println("Conclusion de la transaction.......");
							ACLMessage aclMessage2 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
							aclMessage2.addReceiver(meilleureOffre);
							aclMessage2.setConversationId(conversationID);
							aclMessage2.setContent("I chose your offer");
							System.out.println("...... En cours");
							Thread.sleep(2000);
							myAgent.send(aclMessage2);
						}
						break;
					case ACLMessage.CONFIRM:
						container.logMessage(aclMessage);
						System.out.println(".........................");
						System.out.println("Reçu de la confirmation ...");
						System.out.println("Conversation ID:" + aclMessage.getConversationId());
						ACLMessage msg3 = new ACLMessage(ACLMessage.INFORM);
						msg3.addReceiver(requester);
						msg3.setConversationId(conversationID);
						msg3.setContent("<transaction>" + "<livre>" + livre + "</livre>" + "<prix>" + meilleurPrix
								+ "</prix>" + "<fournisseur>" + aclMessage.getSender().getName() + "</fournisseur>"
								+ "</transaction");
						myAgent.send(msg3);
						break;
				}
			} else {
				block();
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

}

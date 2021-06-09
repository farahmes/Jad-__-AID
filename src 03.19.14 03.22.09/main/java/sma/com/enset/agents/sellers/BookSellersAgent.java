package sma.com.enset.agents.sellers;

import java.util.HashMap;
import java.util.Map;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.com.enset.agents.seller.BookSellerContainer;

public class BookSellersAgent extends GuiAgent {
	protected BookSellersContainer bookSellersContainer;
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
			this.bookSellersContainer = (BookSellersContainer) this.getArguments()[0];
			this.bookSellersContainer.bookSellersAgent=this;
		}

		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		sd.setName("book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		
		//cyclique behavior
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				try {
					messageTemplate=
							MessageTemplate.MatchPerformative(ACLMessage.CFP);
					ACLMessage aclMessage=receive(messageTemplate);
					if(aclMessage!=null){
						bookSellersContainer.logMessage(aclMessage);
						System.out.println("Conversation ID:"+aclMessage.getConversationId());
						String livre=aclMessage.getContent();
						double prix=bookData.get(livre.toUpperCase());
						ACLMessage reply=aclMessage.createReply();
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(prix));
						System.out.println("...... En cours");
						Thread.sleep(2000);
						send(reply);
						parallelBehaviour
						.addSubBehaviour(new SellersBehaviour(myAgent, aclMessage.getConversationId(), bookSellersContainer));
						
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	public void initalisationData() {
		bookData.put("XML",  new Double(132+Math.random()*200));
		bookData.put("JAVA", new Double(58+Math.random()*200));
		bookData.put("UML",  new Double(103+Math.random()*200));
		bookData.put("C++",  new Double(87+Math.random()*200));
		bookData.put("HTML",  new Double(143+Math.random()*200));
		bookData.put("CSS",  new Double(223+Math.random()*400));
	}
	
	@Override
	protected void takeDown() {
		System.out.println("the service is going to die.....");
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	@Override
	protected void onGuiEvent(GuiEvent evt) {

	}

}

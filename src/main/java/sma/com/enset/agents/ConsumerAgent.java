package sma.com.enset.agents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;

public class ConsumerAgent extends GuiAgent{
	
	protected ConsumerContainer consumerContainer;
	
	@Override
	protected void setup() {
		
		String bookName=null;
		
		if(this.getArguments().length==1) {
			consumerContainer = (ConsumerContainer) this.getArguments()[0];
			consumerContainer.consumerAgent=this;
		}
		
		System.out.println("initialisation de l'agent name " + this.getAID().getName());
		System.out.println("i'm trying to buy the book  " + bookName );
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		/*
		addBehaviour(new Behaviour() {
			private int counter=0;
			@Override
			public boolean done() {
				return (counter==10);
			}
			
			@Override
			public void action() {
				System.out.println("step => :" + this.counter);
				++counter;
			}
		});*/
		
		
		//comportement predefinie OneShotBehaviour() execute la methode action une seul fois
		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				System.out.println("one shot behaviour !!");
			}
		});
		
		//autre comportement la methode action() s'execute tout le temps sans arret
		//surveiller le systeme de messagerie ar exemple
		/*addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				System.out.println("cyclic behaviour .......");
			}
		});*/
		
		
		/*addBehaviour(new TickerBehaviour(this, 1000) {
			@Override
			protected void onTick() {
				System.out.println("TIC ...");
				System.out.println(myAgent.getAID().getLocalName());
			}
		});*/
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm");
		Date date;
		try {
			date = dateFormat.parse("03/06/2021:22:12");
			parallelBehaviour.addSubBehaviour(new WakerBehaviour(this,date) {
				@Override
				protected void onWake() {
					System.out.println("waker behaviour ....");
				}
				
			});
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			MessageTemplate messageTemplate = 
					MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			
//					MessageTemplate.and(
//									MessageTemplate.MatchPerformative(ACLMessage.INFORM),
//									MessageTemplate.MatchLanguage("fr")
//									);
			@Override
			public void action() {
				ACLMessage message =receive(messageTemplate);
				if(message!=null) {
					System.out.println("Sender => " + message.getSender().getName());
					System.out.println("Content => " + message.getContent());
					System.out.println("SpeechAct => " + ACLMessage.getPerformative(message.getPerformative()));
					//reply with a message
//					ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
//					reply.addReceiver(message.getSender());
//					reply.setContent("Price = 1000");
//					send(reply);
					
					consumerContainer.logMessage(message);
					
				}else {
					System.out.println("block() ...............");
					block();
				}
			}
		});
		
		
	}
	
	@Override
	protected void beforeMove() {
		try {
			System.out.println("Before migration to another component ............" + 
					this.getContainerController().getContainerName());
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("..............................................");
	}
	
	
	@Override
	protected void afterMove() {
		try {
			System.out.println("after migration to another component ............" + 
					this.getContainerController().getContainerName());
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		System.out.println(".................................................");
		
	}
	
	//methode qui s'execute just avant la destruction de l'agent !!
	@Override
	protected void takeDown() {
		System.out.println("i'm going to die ...............................");
	}
	
	@Override
	protected void onGuiEvent(GuiEvent evt) {
		if(evt.getType()==1) {
			String bookName =(String) evt.getParameter(0);
			ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
			aclMessage.setContent(bookName);
			aclMessage.addReceiver(new AID("BookBuyerAgent", AID.ISLOCALNAME));
			send(aclMessage);
			//System.out.println("Agent => " + this.getAID(). () +"  "  + bookName);
		}
	}
}

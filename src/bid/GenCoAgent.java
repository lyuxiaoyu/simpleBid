package bid;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class GenCoAgent extends Agent{
	private static final long serialVersionUID = 1L;

	private GenCoInfo info;
	private double[] bid;
	private double[] result;
	private double[] desicion = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
	private ReAlgorithm reA;
	
	@Override
	public void setup(){
		
		init();
		FSMBehaviour fsm=new FSMBehaviour() {
			private static final long serialVersionUID = 1L;
			@Override
            public int onEnd() {
                System.out.println(getLocalName() +" FSMBehaviour finished");
                myAgent.doDelete();
                return super.onEnd();
            }
        };
        
     // 1. Receive demand
        fsm.registerFirstState(new ReceiveDemand(), "ReceiveDemand");
        fsm.registerTransition("ReceiveDemand", "MakeDecision", 1);
        
//		make decision by Algorithm
        fsm.registerState(new MakeDecision(), "MakeDecision");
        fsm.registerDefaultTransition("MakeDecision", "SendBid");
        
// 		send bid
        fsm.registerState(new SendBid("iso"), "SendBid");
        fsm.registerDefaultTransition("SendBid", "ReceiveResult");
        
     // 2. receive result
        fsm.registerState(new ReceiveResult(), "ReceiveResult");
        fsm.registerTransition("ReceiveResult", "ImproveDecision", 1);//转换规则
        
//		improve Algorithm & save record
        fsm.registerState(new ImproveDecision(), "ImproveDecision");
        fsm.registerDefaultTransition("ImproveDecision", "ReceiveDemand");
        
        
        addBehaviour(fsm);
			
		
		// 4. end.
	}
	
	@Override 
	public void takeDown(){
		
	}
		
	private void init(){
		Object[] args = getArguments();
		if (args != null){
			info = (GenCoInfo)args[0];
			bid = new double[2];
			bid[1] = info.powerMax;
			result = new double[2];
			reA = new ReAlgorithm(desicion.length);
//			System.out.println(info.name + ": " + info.cost + "\t" + info.powerMax);
		}
	}
	
	
	private class ReceiveDemand extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;
		int finished = 0;
        @Override
        public void action() {
            ACLMessage acl1 = receive();
            finished = 0;
            if(acl1!=null) {
//            	double demand = Double.valueOf(acl1.getContent());
//                System.out.println(getLocalName() +" receive a demand: " + demand);
                finished = 1;
            }
            else {
//                System.out.println(getLocalName() +" does not receive a demand");
                block();//如果没有消息，则阻塞行为
            }
        }
        
        @Override
        public int onEnd() {
            return finished;
        }
        
        @Override
        public boolean done() {
            return finished == 1;
        }
        
    };
	
    
    private class MakeDecision extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;
	
		@Override
		public void action() {
			int index = reA.makeDecison();
			bid[0] = (desicion[index] / 50 + 1) * info.cost;
		}
	};
    
	
	private class SendBid extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;
		
		String receiveName;
		
		SendBid(String name){
			receiveName = name;
		}

		@Override
		public void action() {
			 
			ACLMessage acl = new ACLMessage(ACLMessage.INFORM); //通知
			AID r = new AID();
			r.setLocalName(receiveName);  //设置接收Agent的本地名
			acl.addReceiver(r);     //添加到ACL消息中
			acl.setContent(String.valueOf(bid[0]) + " " + String.valueOf(bid[1])); //设置内容
			send(acl);   //发送消息
		}
	};
	
	
	private class ReceiveResult extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;
		int finished = 0;
        @Override
        public void action() {
            ACLMessage acl1=receive();
            finished = 0;
            if(acl1!=null) {
            	String[] contents = acl1.getContent().split(" ");
            	result[0] = Double.valueOf(contents[0]); 
            	result[1] = Double.valueOf(contents[1]); 
//                System.out.println(getLocalName() +" receive a result: " + acl1.getContent());
                finished = 1;
            }
            else {
//                System.out.println(getLocalName() +" does not receive a result");
                block();//如果没有消息，则阻塞行为
            }
        }
        
        @Override
        public int onEnd() {
            return finished;
        }
        
        @Override
        public boolean done() {
            return finished == 1;
        }
    };
    
    
    private class ImproveDecision extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			double reward = 0;
			if (result[0] != 0 && result[1] > 0.0001) {
				reward += (result[0] - info.cost) * result[1];
			}
			reA.improveDecision(reward);
		}
	};
	
}

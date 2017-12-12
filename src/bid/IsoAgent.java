package bid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import matlab.TestPlot;


public class IsoAgent extends Agent{
	private static final long serialVersionUID = 1L;

	
	private int iterMax = 3000;
	private int iter = 0;
	private GenCoInfo[] genCosInfo;
	private int genCosNum;
	private double demand = 5.5;
	private double[] clearPrice = new double[iterMax];
	private double[] averagePrice = new double[iterMax];
	private Map<String, Integer> agentIndex = new HashMap<String, Integer>();
	private double[][] bids;		//�õ��ı���
	private double[][] results;	//����õ��Ľ��
	
	
	
	@Override
	public void setup() {

		init();
		
		FSMBehaviour fsm=new FSMBehaviour() {
			private static final long serialVersionUID = 1L;
			@Override
            public int onEnd() {
                System.out.println(getLocalName() +" FSMBehaviour finished");
                myAgent.doDelete();
                return super.onEnd();
            }
        };;

        // 1. send demand to every GenCo;
        fsm.registerFirstState(new SendDemand(), "SendDemand");
        fsm.registerDefaultTransition("SendDemand", "ReceiveBid");
        
     // 2. receive bids
        fsm.registerState(new ReceiveBid(), "ReceiveBid");
        fsm.registerDefaultTransition("ReceiveBid", "CalculateResult");//ת������
        
//		calculate the clear price & save record
        fsm.registerState(new CalculateResult(), "CalculateResult");
        fsm.registerDefaultTransition("CalculateResult", "SendResult");
        
// 		send the result to every GenCo
        fsm.registerState(new SendResult(), "SendResult");
        fsm.registerDefaultTransition("SendResult", "JudgeFinish");
	
     // 3. repeat step 1-2 
        fsm.registerState(new JudgeFinish(), "JudgeFinish");
        fsm.registerTransition("JudgeFinish", "SendDemand", 1);//ת������
        fsm.registerTransition("JudgeFinish", "BidFinish", 0);//ת������
		
	 // 4. 	send "bid is finished"
        fsm.registerLastState(new BidFinish(), "BidFinish");
        
// 		show record
        
        addBehaviour(fsm);
	}
	
	
	@Override
	public void takeDown() {
		
	}
	
	
	//agent��ʼ��
	private void init() {
		Object[] args = getArguments();
		if (args != null) {
			genCosInfo = (GenCoInfo[])args;
			genCosNum = genCosInfo.length;
			bids = new double[2][genCosNum];
			results = new double[2][genCosNum];
			for(int i = 0; i < genCosNum; i++) {
				agentIndex.put(genCosInfo[i].name, i);
			}
			System.out.println(demand + " " + genCosNum + " " +genCosInfo);
			System.out.println(agentIndex);
		}
	}
	
	
	private class SendDemand extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			
			for (GenCoInfo  genCo: genCosInfo) {
				ACLMessage acl = new ACLMessage(ACLMessage.INFORM); //֪ͨ
				AID r = new AID();
				r.setLocalName(genCo.name);  //���ý���Agent�ı�����
				acl.addReceiver(r);     //��ӵ�ACL��Ϣ��
				acl.setContent(String.valueOf(demand)); //��������
				send(acl);   //������Ϣ
			}
			
		}
	};
	
	
	private class ReceiveBid extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;
		private int receiveNum = 0;
		boolean finished = false;
		//public String genCoName;
		
        @Override
        public void action() {
            ACLMessage acl1=receive();
            finished = false;
            if(acl1!=null) {
            	String agentName = acl1.getSender().getLocalName();
            	int index =agentIndex.get(agentName);
            	String[] contents = acl1.getContent().split(" ");
            	bids[0][index] = Double.valueOf(contents[0]); 
            	bids[1][index] = Double.valueOf(contents[1]); 
//                System.out.println(getLocalName() +" receive " + agentName +"'s bid: " 
//                		+ bids[0][index] + " " + bids[1][index]);
                if (++receiveNum == genCosNum){
                	receiveNum = 0;
                	finished = true;
                }
            }
            else {
                block();//���û����Ϣ����������Ϊ
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
    };
    
   
    private class CalculateResult extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			int[] index = mySort(bids[0]);
			double sum = 0;
			
			int k = 0;
			
			Arrays.fill(results[0], 0.0);
			Arrays.fill(results[0], 0.0);
			for (int i = 0; i < index.length; i++) {
				sum += bids[1][index[i]];
				results[0][index[i]] = bids[0][index[i]];
				results[1][index[i]] = bids[1][index[i]];
				if (sum > demand) {
					k = i;
					results[1][index[i]] -= sum - demand;
					break;
				}			
			}
			clearPrice[iter] = bids[0][index[k]];
			
			double sumPrice = 0;
			for (int i = 0; i < index.length; i++) {
				sumPrice += bids[0][index[i]];		
			}
			averagePrice[iter] = sumPrice / index.length;
			
			
		}
		
		private int[] mySort(double[] b) {
			double[] a = b.clone();
			int[] index = new int[a.length];
			
			for(int i = 0; i < a.length; i++) {
				index[i] = i;
			}
			
			for (int i = 0; i < a.length - 1; i++) {
				int min = i;
				for (int j = i + 1; j < a.length; j++) {
					if (a[j] < a[min]) {
						min = j;
					}
				}
				double tmp1 = a[i];
				a[i] = a[min];
				a[min] = tmp1;
				int tmp2 = index[i];
				index[i] = index[min];
				index[min] = tmp2;
			}
			return index;
		}
	};

    
    
    private class SendResult extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			
			for (GenCoInfo  genCo: genCosInfo) {
				ACLMessage acl = new ACLMessage(ACLMessage.INFORM); //֪ͨ
				AID r = new AID();
				r.setLocalName(genCo.name);  //���ý���Agent�ı�����
				acl.addReceiver(r);     //��ӵ�ACL��Ϣ��
            	int index =agentIndex.get(genCo.name);
				acl.setContent(String.valueOf(results[0][index]) + " " +String.valueOf(results[1][index])); //��������
				send(acl);   //������Ϣ
			}
			
		}
	};
	
	
	private class JudgeFinish extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;
		int finished = 1;
		
		@Override
		public void action() {
			
			if (++iter >= iterMax) {
				finished = 0;
			}
//			System.out.println("*******************************"+ iter +"**********************************");
			
		}
		
		@Override
        public int onEnd() {
            return finished;
        }
	};
	
	
	private class BidFinish extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void action() {
			TestPlot.draw(clearPrice);
		}
	};
}




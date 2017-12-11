package bid;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {   
	public static void main(String[] args) {
		// ������������
		Properties pp = new ExtendedProperties();
//		pp.setProperty(Profile.GUI, "true");	//����RMA
		pp.setProperty(Profile.MAIN, "true");
		Profile p = new ProfileImpl(pp);
		System.out.println(p);
		// �������л���
		Runtime rt = Runtime.instance();
		// ��������
		AgentContainer ac = rt.createMainContainer(p);	
		try {
			//�����µ�agent
			AgentController agent = ac.createNewAgent("build", "bid.Build", null);
			agent.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}

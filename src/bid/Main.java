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
		// 设置容器属性
		Properties pp = new ExtendedProperties();
//		pp.setProperty(Profile.GUI, "true");	//开启RMA
		pp.setProperty(Profile.MAIN, "true");
		Profile p = new ProfileImpl(pp);
		System.out.println(p);
		// 创建运行环境
		Runtime rt = Runtime.instance();
		// 创建容器
		AgentContainer ac = rt.createMainContainer(p);	
		try {
			//创建新的agent
			AgentController agent = ac.createNewAgent("build", "bid.Build", null);
			agent.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}

package bid;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;




public class Build extends Agent{
	private static final long serialVersionUID = 1L;

	private GenCoInfo[] multiGenCoInfo = {
			new GenCoInfo("gen1", 1.00, 0.4),
			new GenCoInfo("gen2", 1.10, 1),
			new GenCoInfo("gen3", 1.05, 1.2),
			new GenCoInfo("gen4", 0.95, 1.5),
			new GenCoInfo("gen5", 1.00, 1),
			new GenCoInfo("gen6", 0.98, 0.6),
			new GenCoInfo("gen7", 1.10, 0.7),
			new GenCoInfo("gen8", 1.05, 0.8),
			new GenCoInfo("gen9", 0.90, 0.5),
			new GenCoInfo("gen10", 0.80, 0.5),
	};
	
	public void setup(){
		AgentContainer ac = getContainerController();
		try {
			for (int i = 0; i < multiGenCoInfo.length; i++) {
				Object[] args = {multiGenCoInfo[i]};
				AgentController genCoAgent = ac.createNewAgent(multiGenCoInfo[i].name, "bid.GenCoAgent", args);
				genCoAgent.start();
			}
			AgentController isoAgent = ac.createNewAgent("iso", "bid.IsoAgent", multiGenCoInfo);
			isoAgent.start();
			System.out.println("Agents are built.");
		}catch(Exception e) {
			System.out.println("Create agent error");
		}
		
		doDelete();
	}
}

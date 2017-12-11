package bid;

import jade.core.Agent;

public class Test extends Agent{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setup(){
		System.out.println("test");
		doDelete();
	}
}

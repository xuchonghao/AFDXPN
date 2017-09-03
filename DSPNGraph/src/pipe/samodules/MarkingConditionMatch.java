package pipe.samodules;

import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;

public class MarkingConditionMatch extends MarkingCondition{

	private Marking target;
	public MarkingConditionMatch() {
		// TODO Auto-generated constructor stub
	}
	
	public MarkingConditionMatch(Marking paramMarking) {
		this.target = paramMarking;
	}
	
	public boolean evaluate(Marking paramMarking){
		
		boolean result = paramMarking.containsSubMarking(target);
		System.out.println("evaluate:"+paramMarking+" "+result);
		return result;
	}

}

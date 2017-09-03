package pipe.samodules;

import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.SuccessionEvaluator;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.models.pn.MarkingUpdater;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.pn.PetriSuccessionEvaluator;
import it.unifi.oris.sirio.models.pn.PostUpdater;
import it.unifi.oris.sirio.models.pn.ResetSet;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;
import java.util.LinkedHashSet;
import java.util.Set;

public class MyPetriSuccessionEvaluator extends PetriSuccessionEvaluator{

  private MarkingUpdater tokensRemover;
  private MarkingUpdater tokensAdder;
  private boolean distinctNewlyEnablingConditions;
  
  public MyPetriSuccessionEvaluator(MarkingUpdater tokensRemover, MarkingUpdater tokensAdder, boolean distinctNewlyEnablingConditions)
  {
	    super();
	  }
  
  
  
  public Succession computeSuccession(PetriNet petriNet, State state, Transition fired)
  {
				//计算出的succession是传入的state,变迁，一个新的state,新state marking不变，没有一个变迁可以发生
    PetriStateFeature prev = (PetriStateFeature)state.getFeature(PetriStateFeature.class);
    
    Marking tmpMarking = new Marking(prev.getMarking());
    
    Set<Transition> prevEnabled = petriNet.getEnabledTransitions(prev.getMarking());
    Set<Transition> tmpEnabled = petriNet.getEnabledTransitions(tmpMarking);

    Set<Transition> disabled = new LinkedHashSet(prevEnabled);
    disabled.remove(fired);
    
    PetriStateFeature next = new PetriStateFeature();
    next.setMarking(tmpMarking);
    next.setPersistent(new LinkedHashSet());
    next.setNewlyEnabled(new LinkedHashSet());
    next.setEnabled(new LinkedHashSet());
    next.setDisabled(disabled);
    next.setDistinctNewlyEnablingConditions(this.distinctNewlyEnablingConditions);
    
    State nextState = new State();
    nextState.addFeature(next);
    
    return new Succession(state, fired, nextState);
  }
}


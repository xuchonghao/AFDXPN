package pipe.steadymodules;



import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.SuccessionEvaluator;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.models.pn.MarkingUpdater;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.pn.PetriTokensAdder;
import it.unifi.oris.sirio.models.pn.PetriTokensRemover;
import it.unifi.oris.sirio.models.pn.PostUpdater;
import it.unifi.oris.sirio.models.pn.ResetSet;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;

import java.util.LinkedHashSet;
import java.util.Set;

public class MyPetriSuccessionEvaluator
  implements SuccessionEvaluator<PetriNet, Transition>
{
  private MarkingUpdater tokensRemover;
  private MarkingUpdater tokensAdder;
  private boolean distinctNewlyEnablingConditions;
  
  public MyPetriSuccessionEvaluator()
  {
    this(new PetriTokensRemover(), new PetriTokensAdder(), false);
  }
  
  public MyPetriSuccessionEvaluator(MarkingUpdater tokensRemover, MarkingUpdater tokensAdder, boolean distinctNewlyEnablingConditions)
  {
    this.tokensRemover = tokensRemover;
    this.tokensAdder = tokensAdder;
    this.distinctNewlyEnablingConditions = distinctNewlyEnablingConditions;
  }
  

			 public Succession computeSuccession(PetriNet petriNet, State state, Transition fired)
  {
				return null;
			 }

  public StoSuccession computeStoSuccession(PetriNet petriNet, StoState state, Transition fired)
  {
    PetriStateFeature prev = (PetriStateFeature)state.getFeature(PetriStateFeature.class);
    
    Marking tmpMarking = new Marking(prev.getMarking());
    this.tokensRemover.update(tmpMarking, petriNet, fired);
    
    Marking nextMarking = new Marking(tmpMarking);
    this.tokensAdder.update(nextMarking, petriNet, fired);
    
    if (fired.hasFeature(PostUpdater.class)) {
      ((PostUpdater)fired.getFeature(PostUpdater.class)).update(nextMarking, petriNet, fired);
    }
    Set<Transition> prevEnabled = petriNet.getEnabledTransitions(prev.getMarking());
    Set<Transition> tmpEnabled = petriNet.getEnabledTransitions(tmpMarking);
    Set<Transition> nextEnabled = petriNet.getEnabledTransitions(nextMarking);
    
    if (fired.hasFeature(ResetSet.class)) {
      tmpEnabled.removeAll(((ResetSet)fired.getFeature(ResetSet.class)).getResetSet());
    }
    Set<Transition> persistent = new LinkedHashSet(nextEnabled);
    persistent.retainAll(tmpEnabled);
    persistent.retainAll(prevEnabled);
    persistent.remove(fired);
    
    Set<Transition> newlyEnabled = new LinkedHashSet(nextEnabled);
    newlyEnabled.removeAll(persistent);
    
    Set<Transition> prevAndNotNext = new LinkedHashSet(prevEnabled);
    prevAndNotNext.removeAll(nextEnabled);
    Set<Transition> prevAndNew = new LinkedHashSet(prevEnabled);
    prevAndNew.retainAll(newlyEnabled);
    Set<Transition> disabled = new LinkedHashSet(prevAndNotNext);
    disabled.addAll(prevAndNew);
    disabled.remove(fired);
    
    PetriStateFeature next = new PetriStateFeature();
    next.setMarking(nextMarking);
    next.setPersistent(persistent);
    next.setNewlyEnabled(newlyEnabled);
    next.setEnabled(nextEnabled);
    next.setDisabled(disabled);
    next.setDistinctNewlyEnablingConditions(this.distinctNewlyEnablingConditions);
    
    StoState nextState = new StoState();
    nextState.addFeature(next);
    
    return new StoSuccession(state, fired, nextState);
  }
}


package pipe.steadymodules;

import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.analyzer.state.StateBuilder;
import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.math.function.StateDensityFunction;
import it.unifi.oris.sirio.models.pn.InitialPetriStateBuilder;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticTransitionFeature;
import it.unifi.oris.sirio.models.stpn.TransientStochasticStateFeature;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NewlyEnablingStateBuilder
  implements StateBuilder<Marking>
{
  private PetriNet petriNet;
  private boolean transientAnalysis;
  boolean distinctNewlyEnabledConditions;
  private BigDecimal epsilon;
  private int numSamples;
  
  public NewlyEnablingStateBuilder(PetriNet petriNet, boolean transientAnalysis)
  {
    this(petriNet, transientAnalysis, false, BigDecimal.ZERO, 3);
  }
  
  public NewlyEnablingStateBuilder(PetriNet petriNet, boolean transientAnalysis, boolean distinctNewlyEnabledConditions, BigDecimal epsilon, int numSamples)
  {
    this.petriNet = petriNet;
    this.transientAnalysis = transientAnalysis;
    this.distinctNewlyEnabledConditions = distinctNewlyEnabledConditions;  
    this.epsilon = epsilon;   
    this.numSamples = numSamples;  
  }
  //transientanalysis 为false  distinctNewlyEnabledConditions  false
			//epsilon  0.001  numSamples  3
  public State build(Marking marking)
  {
    State state = InitialPetriStateBuilder.computeInitialState(this.petriNet, marking, this.distinctNewlyEnabledConditions);
    Set<Transition> enabledTransitions = ((PetriStateFeature)state.getFeature(PetriStateFeature.class)).getNewlyEnabled();
    
    StochasticStateFeature ssf = new StochasticStateFeature(this.epsilon, this.numSamples);
    StateDensityFunction f = new StateDensityFunction();
    ssf.setStateDensity(f);
    
    for (Transition t : enabledTransitions) {
      ssf.addVariable(new Variable(t.getName()), 
        ((StochasticTransitionFeature)t.getFeature(StochasticTransitionFeature.class)).getFiringTimeDensity());
    }
    
    boolean hasEnabledImm = false;
    for (Object e : f.getDeterministicValues()) {
      if ((!((Variable)((Map.Entry)e).getKey()).equals(Variable.AGE)) && (((BigDecimal)((Map.Entry)e).getValue()).compareTo(BigDecimal.ZERO) == 0)) {
        hasEnabledImm = true;
        break;
      }
    }
    
    ssf.setVanishing(hasEnabledImm);
				//设置absorbing状态！
    ssf.setAbsorbing(enabledTransitions.isEmpty());
    state.addFeature(ssf);
    
    if (this.transientAnalysis) {
      ssf.addAgeVariable(Variable.AGE);
      
      TransientStochasticStateFeature tssf = new TransientStochasticStateFeature();
      tssf.setReachingProbability(BigDecimal.ONE);
      
      StateDensityFunction enteringTime = new StateDensityFunction();
      enteringTime.addDeterministicVariable(Variable.AGE, BigDecimal.ZERO);
      tssf.setEnteringTimeDensity(enteringTime);
      
      state.addFeature(tssf);
    }
    
    return state;
  }
}


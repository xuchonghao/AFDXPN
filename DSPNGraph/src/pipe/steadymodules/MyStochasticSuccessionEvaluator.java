package pipe.steadymodules;

import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.SuccessionEvaluator;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.math.expression.Expolynomial;
import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.math.function.StateDensityFunction;
import it.unifi.oris.sirio.models.pn.MarkingUpdater;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.pn.PetriSuccessionEvaluator;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticSuccessionEvaluator;
import it.unifi.oris.sirio.models.stpn.StochasticSuccessionFeature;
import it.unifi.oris.sirio.models.stpn.StochasticTransitionFeature;
import it.unifi.oris.sirio.models.stpn.TransientStochasticStateFeature;
import it.unifi.oris.sirio.models.tpn.Priority;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MyStochasticSuccessionEvaluator
				extends StochasticSuccessionEvaluator
  implements SuccessionEvaluator<PetriNet, Transition>
{
  private MyPetriSuccessionEvaluator petriSuccessionEvaluator;
  private boolean transientAnalysis;
  
  public MyStochasticSuccessionEvaluator(boolean transientAnalysis, MarkingUpdater tokensRemover, MarkingUpdater tokensAdder, boolean distinctNewlyEnablingConditions, OmegaBigDecimal tauAgeLimit)
  {
				super(transientAnalysis, tokensRemover,tokensAdder,
						distinctNewlyEnablingConditions, tauAgeLimit);
				
    this.petriSuccessionEvaluator = new MyPetriSuccessionEvaluator(tokensRemover, tokensAdder, 
      distinctNewlyEnablingConditions);
    
    this.transientAnalysis = transientAnalysis;
  }
  
  public StoSuccession computeSuccession(PetriNet petriNet, StoState state, Transition fired)
  {
		
    StoSuccession succession = this.petriSuccessionEvaluator.computeStoSuccession(petriNet, state, fired);

    PetriStateFeature prevPetriStateFeature = (PetriStateFeature)succession.getParent().getFeature(PetriStateFeature.class);
    PetriStateFeature nextPetriStateFeature = (PetriStateFeature)succession.getChild().getFeature(PetriStateFeature.class);
    StochasticStateFeature prevStochasticStateFeature = (StochasticStateFeature)succession.getParent().getFeature(StochasticStateFeature.class);
					//孩子的StochasticStateFeature 是new出来的
    StochasticStateFeature nextStochasticStateFeature = new StochasticStateFeature(prevStochasticStateFeature);

    StateDensityFunction prevStateDensity = prevStochasticStateFeature.getStateDensity();
    StateDensityFunction nextStateDensity = nextStochasticStateFeature.getStateDensity();
    
    Variable firedVar = new Variable(fired.getName());
    
    Set<Variable> otherNonExpVars = prevStochasticStateFeature.getFiringVariables();
    otherNonExpVars.remove(firedVar);
    
    BigDecimal prob = BigDecimal.ONE;
    
    Variable minEXP = new Variable("minEXP");
    if (prevStochasticStateFeature.getEXPVariables().size() > 0)
    {
      nextStochasticStateFeature.addTruncatedExp(minEXP, prevStochasticStateFeature.getTotalExpRate(), OmegaBigDecimal.POSITIVE_INFINITY);
      otherNonExpVars.add(minEXP);
    }
    
    Priority firedPriority;
    if (prevStochasticStateFeature.getEXPVariables().contains(firedVar))
    {
      prob = 
        prob.multiply(prevStochasticStateFeature.getEXPRate(firedVar)).divide(prevStochasticStateFeature.getTotalExpRate(), MathContext.DECIMAL128);
      
      nextStochasticStateFeature.removeExpVariable(firedVar);
      firedVar = minEXP;
    }
    else
    {
      Set<Variable> nullDelayVariables = prevStateDensity.getNullDelayVariables(firedVar);
      Set<Variable> nullDelaySamePriority = new LinkedHashSet();
      firedPriority = (Priority)fired.getFeature(Priority.class);
      
      for (Variable v : nullDelayVariables) {
        if (!v.equals(Variable.AGE)) {
          Priority otherPriority = (Priority)petriNet.getTransition(v.toString()).getFeature(Priority.class);
          if ((otherPriority != null) && ((firedPriority == null) || (firedPriority.value() < otherPriority.value())))
          {
            return null;
          }
          if (((firedPriority == null) && (otherPriority == null)) || (
            (firedPriority != null) && (firedPriority.equals(otherPriority))))
          {
            nullDelaySamePriority.add(v);
          }
        }
      }
      prob = prob.multiply(computeRandomSwitchProbability(
        nullDelaySamePriority, fired, prevPetriStateFeature.getEnabled()));
    }
    
    BigDecimal minProbability = nextStateDensity.conditionAllToBound(firedVar, otherNonExpVars, OmegaBigDecimal.ZERO);
    if (minProbability.compareTo(BigDecimal.ZERO) == 0) {
      return null;
    }
    prob = prob.multiply(minProbability);
    
    nextStateDensity.shiftAndProject(firedVar);
    
    if ((prevStochasticStateFeature.getEXPVariables().size() > 0) && (!firedVar.equals(minEXP))) {
      nextStateDensity.marginalizeVariable(minEXP);
    }
    
    for (Variable v : Transition.newVariableSetInstance(nextPetriStateFeature.getDisabled())) {
      if (nextStochasticStateFeature.getEXPVariables().contains(v)) {
        nextStochasticStateFeature.removeExpVariable(v);
      } else {
        nextStateDensity.marginalizeVariable(v);
      }
    }
    
    if (this.transientAnalysis) {
      TransientStochasticStateFeature tssf = new TransientStochasticStateFeature();
      tssf.setReachingProbability(((TransientStochasticStateFeature)state.getFeature(TransientStochasticStateFeature.class))
        .getReachingProbability().multiply(prob));
      
      if (nextStateDensity.getVariables().equals(Collections.singleton(Variable.AGE)))
      {
        tssf.setEnteringTimeDensity(new StateDensityFunction(nextStateDensity));
      }
      succession.getChild().addFeature(tssf);
    }
    //不会有NewlyEnabled
    for (Transition t : nextPetriStateFeature.getNewlyEnabled()) {
      nextStochasticStateFeature.addVariable(new Variable(t.getName()), 
        ((StochasticTransitionFeature)t.getFeature(StochasticTransitionFeature.class)).getFiringTimeDensity());
    }
    
    Set<Transition> nextEnabled = nextPetriStateFeature.getEnabled();
    boolean hasEnabledImm = false;
    for (Map.Entry<Variable, BigDecimal> e : nextStateDensity.getDeterministicValues()) {
      if ((!((Variable)e.getKey()).equals(Variable.AGE)) && (((BigDecimal)e.getValue()).compareTo(BigDecimal.ZERO) == 0)) {
        hasEnabledImm = true;
        break;
      }
    }
    
    nextStochasticStateFeature.setVanishing(hasEnabledImm);
    nextStochasticStateFeature.setAbsorbing(nextEnabled.isEmpty());
				

    StochasticSuccessionFeature ssf = new StochasticSuccessionFeature(prob);

					//System.out.println("输出prob="+prob+"\n");
    succession.addFeature(ssf);
    
    succession.getChild().addFeature(nextStochasticStateFeature);
    
    return succession;

  }
  
  public BigDecimal computeRandomSwitchProbability(Set<Variable> nullDelayVariables, Transition fired, Set<Transition> enabled)
  {
    if (nullDelayVariables.size() == 0) {
      return BigDecimal.ONE;
    }
    
    BigDecimal totalWeight = BigDecimal.ZERO;
    for (Transition t : enabled) {
      if ((!t.equals(fired)) && (nullDelayVariables.contains(new Variable(t.getName()))))
        totalWeight = totalWeight.add(((StochasticTransitionFeature)t.getFeature(StochasticTransitionFeature.class)).getWeight());
    }
    BigDecimal firedWeight = ((StochasticTransitionFeature)fired.getFeature(StochasticTransitionFeature.class)).getWeight();
    return firedWeight.divide(firedWeight.add(totalWeight), Expolynomial.mathContext);
  }
}


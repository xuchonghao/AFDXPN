package pipe.samodules;


import it.unifi.oris.sirio.analyzer.Analyzer;
import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.enumeration_policy.EnumerationPolicy;
import it.unifi.oris.sirio.analyzer.graph.Edge;
import it.unifi.oris.sirio.analyzer.graph.Node;
import it.unifi.oris.sirio.analyzer.graph.SuccessionGraph;
import it.unifi.oris.sirio.analyzer.log.AnalysisLogger;
import it.unifi.oris.sirio.analyzer.log.AnalysisMonitor;
import it.unifi.oris.sirio.analyzer.log.PrintStreamLogger;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.analyzer.stop_criterion.MonitorStopCriterion;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.math.expression.Expolynomial;
import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.math.function.GEN;
import it.unifi.oris.sirio.math.function.PartitionedGEN;
import it.unifi.oris.sirio.math.function.StateDensityFunction;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.stpn.NewlyEnablingStateBuilder;
import it.unifi.oris.sirio.models.stpn.Regeneration;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.models.stpn.TransientStochasticStateFeature;
import it.unifi.oris.sirio.models.stpn.components_factory.StochasticComponentsFactory;
import it.unifi.oris.sirio.models.stpn.enumeration_policy.TruncationPolicy;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTextField;

		public class MyTransientAnalysis {

  private Set<Marking> reachableMarkings;
  private Set<Marking> alwaysRegenerativeMarkings;
  private Set<Marking> neverRegenerativeMarkings;
  private Set<Marking> regenerativeAndNotRegenerativeMarkings;
  private Marking initialMarking;
  private PetriNet petriNet;
  private EnumerationPolicy truncationPolicy;
  private Map<Marking, Set<State>> stateClasses;
  
				private double result_j = 0;
  public Set<Marking> getReachableMarkings()
  {
    return Collections.unmodifiableSet(this.reachableMarkings);
  }
  
  public Set<Marking> getAlwaysRegenerativeMarkings() { return Collections.unmodifiableSet(this.alwaysRegenerativeMarkings); }
  
  public Set<Marking> getNeverRegenerativeMarkings() {
    return Collections.unmodifiableSet(this.neverRegenerativeMarkings);
  }
  
  public Set<Marking> getRegenerativeAndNotRegenerativeMarkings() { return Collections.unmodifiableSet(this.regenerativeAndNotRegenerativeMarkings); }
  
  public Marking getInitialMarking()
  {
    return new Marking(this.initialMarking);
  }
  
  public PetriNet getPetriNet() {
    return this.petriNet;
  }
  
  public EnumerationPolicy getTruncationPolicy() {
    return this.truncationPolicy;
  }
  
  public static MyTransientAnalysis compute(PetriNet petriNet, 
		Marking initialMarking, TruncationPolicy truncationPolicy)
  {
    return compute(petriNet, 
		initialMarking, truncationPolicy, MarkingCondition.NONE, new PrintStreamLogger(System.out), null, true, null);
  }
  
  public static MyTransientAnalysis compute(PetriNet petriNet, 
		Marking initialMarking, TruncationPolicy truncationPolicy, AnalysisLogger l)
  {
    return compute(petriNet, 
		initialMarking, truncationPolicy, MarkingCondition.NONE, l, null, true, null);
  }
  
  public static MyTransientAnalysis compute(PetriNet petriNet, 
			Marking initialMarking, TruncationPolicy truncationPolicy, 
			MarkingCondition stopCondition, AnalysisLogger l, 
			AnalysisMonitor monitor, boolean verbose,final JTextField resultlabel)
  {
	
				
    MyTransientAnalysis a = new MyTransientAnalysis();
    
    if (l != null) {
      l.log(">> Standard analysis starting from " + initialMarking + 
        " (pruning threshold " + truncationPolicy.getEpsilon() + 
        ", tauAgeLimit " + truncationPolicy.getTauAgeLimit());
      if (stopCondition != MarkingCondition.NONE)
        l.log(", stopCondition");
      l.log(")\n");
    }
    
    long startTime = System.currentTimeMillis();
    
    a.petriNet = petriNet;
    a.initialMarking = initialMarking;
    a.truncationPolicy = truncationPolicy;
    
    a.stateClasses = new HashMap();
    
    Set<Marking> sometimesRegenerativeMarkings = new LinkedHashSet();
    Set<Marking> sometimesNotRegenerativeMarkings = new LinkedHashSet();
    
    sometimesRegenerativeMarkings.add(initialMarking);
    
				//首先把参数传给“参数工厂” StochasticComponentsFactory
    StochasticComponentsFactory f = new StochasticComponentsFactory(true,
					null, null, false, truncationPolicy, 
      truncationPolicy.getTauAgeLimit(), stopCondition, null, 0, monitor);
  
				//重要代码：这里分析出了可达图
				//new NewlyEnablingStateBuilder(petriNet, true).build(initialMarking)
				//这一行建立了state
    MyAnalyzer<PetriNet, Transition> analyzer = new MyAnalyzer(f, petriNet, 
      new NewlyEnablingStateBuilder(petriNet, true).build(initialMarking));
    
    SuccessionGraph graph = analyzer.analyze(resultlabel);
				//可达图已经有了，然后分析，这后面我就不用改了
    
    if (l != null) {
      l.log(">> " + graph.getNodes().size() + " state classes found\n");
    }
    
    if (monitor != null) {
      if (((f.getGlobalStopCriterion() instanceof MonitorStopCriterion)) && 
        (((MonitorStopCriterion)f.getGlobalStopCriterion()).interruptedExecution())) {
        monitor.notifyMessage("Interrupted after the enumeration of " + graph.getNodes().size() + " state classes");
        return null;
      }
      monitor.notifyMessage("Analyzing the transient tree (" + graph.getNodes().size() + " state classes)");
    }
    
					
    int treeClasses = 0;
    int treeZones = 0;
    int treeTerms = 0;
    int treeDepth = 0;
    
    Deque<Node> stack = new LinkedList();
    stack.push(graph.getRoot());
    String offset = "";
    while (!stack.isEmpty())
    {
      Node n = (Node)stack.pop();
      if (n != null) {
        State s = graph.getState(n);
        PetriStateFeature petriFeature = (PetriStateFeature)s.getFeature(PetriStateFeature.class);
        StochasticStateFeature stochasticFeature = (StochasticStateFeature)s.getFeature(StochasticStateFeature.class);
        StateDensityFunction densityFunction = stochasticFeature.getStateDensity();
        TransientStochasticStateFeature transientFeature = (TransientStochasticStateFeature)s.getFeature(TransientStochasticStateFeature.class);
        
        treeClasses++;
        treeZones += densityFunction.getPartitionedGen().getFunctions().size();
        for (GEN g : densityFunction.getPartitionedGen().getFunctions())
          treeTerms += g.getDensity().getExmonomials().size();
        treeDepth = Math.max(treeDepth, offset.length() / 2);
        
        if (s.hasFeature(Regeneration.class)) {
          sometimesRegenerativeMarkings.add(petriFeature.getMarking());
        } else {
          sometimesNotRegenerativeMarkings.add(petriFeature.getMarking());
        }
        
        if (!a.stateClasses.containsKey(petriFeature.getMarking())) {
          a.stateClasses.put(petriFeature.getMarking(), new LinkedHashSet());
        }
        ((Set)a.stateClasses.get(petriFeature.getMarking())).add(s);
        
        if ((l != null) && (verbose)) {
          l.log(offset);
          if (s.hasFeature(Regeneration.class)) {
            l.log("{" + n.getId() + "}");
          } else
            l.log("(" + n.getId() + ")");
          l.log(" " + formatProbability(transientFeature.getReachingProbability()));
          l.log(" " + formatProbability(transientFeature.computeVisitedProbability(OmegaBigDecimal.ZERO, truncationPolicy.getTauAgeLimit(), stochasticFeature)));
          l.log(" (" + petriFeature.getMarking() + ")");
          l.log(" [" + transientFeature.getEnteringTimeLowerBound(stochasticFeature) + "," + transientFeature.getEnteringTimeUpperBound(stochasticFeature) + "].." + transientFeature.getTimeUpperBound(stochasticFeature) + " ");
          
          Set<Variable> exps = stochasticFeature.getEXPVariables();
          Variable tau; 
						//输出所有可能的后继变迁
						for (Node m : graph.getSuccessors(n)) {
            Succession succ = (Succession)graph.getSuccessions(new Edge(n, m)).iterator().next();
            Transition t = (Transition)succ.getEvent();
            tau = new Variable(t.getName());
            l.log(" ");
            if (petriFeature.getNewlyEnabled().contains(t)) {
              l.log("~");
            } else if (stochasticFeature.getEXPVariables().contains(tau)) {}
            l.log("*");
            
            l.log(t.getName());
            l.log("[" + (exps.contains(tau) ? "0" : densityFunction.getMinBound(Variable.TSTAR, tau).negate()) + "," + (
              exps.contains(tau) ? "0" : densityFunction.getMinBound(tau, Variable.TSTAR)) + "]");
            l.log("->");
            
            if (succ.getChild().hasFeature(Regeneration.class)) {
              l.log("{" + m.getId() + "}");
            } else {
              l.log("(" + m.getId() + ")");
            }
          }
          Set<Transition> notFiredEnabledTransitions = petriNet.getEnabledTransitions(petriFeature.getMarking());
         
					//这一顿问题特多
						Iterator tau1;
						for (Iterator succ = graph.getSuccessors(n).iterator(); succ.hasNext(); 
              tau1.hasNext())
          {
            Node m = (Node)succ.next();
            tau1 = graph.getSuccessions(new Edge(n, m)).iterator();
							continue;
						
          }
						
					//这一段
						
						
//          for (Transition t : notFiredEnabledTransitions) {
//            l.log(" ");
//            if (petriFeature.getNewlyEnabled().contains(t)) {
//              l.log("~");
//            } else if (!stochasticFeature.getEXPVariables().contains(new Variable(t.getName()))) {
//              l.log("*");
//            }
//            l.log(t.getName());
//            OmegaBigDecimal eft = stochasticFeature.getEXPVariables().contains(new Variable(t.getName())) ? OmegaBigDecimal.ZERO : 
//              densityFunction.getMinBound(Variable.TSTAR, new Variable(t.getName())).negate();
//            
//            OmegaBigDecimal lft = stochasticFeature.getEXPVariables().contains(new Variable(t.getName())) ? OmegaBigDecimal.POSITIVE_INFINITY : 
//              densityFunction.getMinBound(new Variable(t.getName()), Variable.TSTAR);
//            
//            l.log("[" + eft + "," + lft + "]");
//          }
          
				
          l.log("\n");
          l.log(transientFeature.toString().replaceAll("^|(\\n)", "$1" + offset + " | "));
          l.log("\n");
          if (s.hasFeature(Regeneration.class)) {
            l.log(((Regeneration)s.getFeature(Regeneration.class)).toString().replaceAll("^|(\\n)", "$1" + offset + " | "));
            l.log("\n");
          }
          l.log(stochasticFeature.toString().replaceAll("^|(\\n)", "$1" + offset + " | "));
          l.log("\n");
        }
        
        stack.push(null);
        for (Object notFiredEnabledTransitions = graph.getSuccessors(n).iterator(); ((Iterator)notFiredEnabledTransitions).hasNext();) { Node m = (Node)((Iterator)notFiredEnabledTransitions).next();
          stack.push(m);
        }
        offset = "  " + offset;
      } else {
        offset = offset.substring(2);
      }
    }
    if (l != null) {
      l.log(">> Analysis took " + (System.currentTimeMillis() - startTime) / 1000L + "s\n");
      l.log(String.format(">> Tree: %d classes, %d zones, %d terms\n", new Object[] { Integer.valueOf(treeClasses), Integer.valueOf(treeZones), Integer.valueOf(treeTerms) }));
    }
    
    a.alwaysRegenerativeMarkings = new LinkedHashSet(sometimesRegenerativeMarkings);
    a.alwaysRegenerativeMarkings.removeAll(sometimesNotRegenerativeMarkings);
    
    a.neverRegenerativeMarkings = new LinkedHashSet(sometimesNotRegenerativeMarkings);
    a.neverRegenerativeMarkings.removeAll(sometimesRegenerativeMarkings);
    
    a.regenerativeAndNotRegenerativeMarkings = new LinkedHashSet(sometimesRegenerativeMarkings);
    a.regenerativeAndNotRegenerativeMarkings.retainAll(sometimesNotRegenerativeMarkings);
    
    a.reachableMarkings = new LinkedHashSet(sometimesRegenerativeMarkings);
    a.reachableMarkings.addAll(sometimesNotRegenerativeMarkings);
    
    if (l != null) {
      l.log("Always regenerative markings: " + a.alwaysRegenerativeMarkings + "\n");
      l.log("Never regenerative markings: " + a.neverRegenerativeMarkings + "\n");
      l.log("Markings both regenerative and not regenerative: " + a.regenerativeAndNotRegenerativeMarkings + "\n");
    }
    
    if (monitor != null) {
      monitor.notifyMessage("Analysis completed");
    }
    return a;
  }
  private static String formatProbability(BigDecimal prob) {
    return new DecimalFormat("###.##########", new DecimalFormatSymbols(new Locale("en", "US"))).format(prob);
  }

				public TransientSolution<Marking, Marking> solveDiscretizedBeingProbabilities(BigDecimal timeLimit, BigDecimal step, MarkingCondition markingCondition, AnalysisLogger l)
  {
    return solveDiscretized(timeLimit, step, markingCondition, false, l, null ,null);
  }
  
  public TransientSolution<Marking, Marking> solveDiscretizedBeingProbabilities(BigDecimal timeLimit, BigDecimal step, MarkingCondition markingCondition)
  {
    return solveDiscretized(timeLimit, step, markingCondition, false, new PrintStreamLogger(System.out), null ,null);
  }
  
  public TransientSolution<Marking, Marking> solveDiscretizedBeingProbabilities(BigDecimal timeLimit, BigDecimal step, MarkingCondition markingCondition, AnalysisLogger l, AnalysisMonitor m ,JTextField test)
  {
    return solveDiscretized(timeLimit, step, markingCondition, false, l, m ,test);
  }
  
  public TransientSolution<Marking, Marking> solveDiscretizedVisitedProbabilities(BigDecimal timeLimit, BigDecimal step, MarkingCondition markingCondition, AnalysisLogger l)
  {
    return solveDiscretized(timeLimit, step, markingCondition, true, l, null ,null);
  }
  
  public TransientSolution<Marking, Marking> solveDiscretizedVisitedProbabilities(BigDecimal timeLimit, BigDecimal step, MarkingCondition markingCondition)
  {
    return solveDiscretized(timeLimit, step, markingCondition, true, new PrintStreamLogger(System.out), null ,null);
  }
  
  public TransientSolution<Marking, Marking> solveDiscretizedVisitedProbabilities(BigDecimal timeLimit, BigDecimal step, MarkingCondition markingCondition, AnalysisLogger l, AnalysisMonitor m)
  {
    return solveDiscretized(timeLimit, step, markingCondition, true, l, m ,null);
  }
  
  private TransientSolution<Marking, Marking> solveDiscretized
				(BigDecimal timeLimit, BigDecimal step, 
						MarkingCondition markingCondition, boolean visitedProbabilies,
						AnalysisLogger l, AnalysisMonitor monitor, final JTextField resultlabel)
  {
    if (l != null) {
      l.log(">> Solving in [0, " + timeLimit + "] with step " + step + " ");
      if (markingCondition == MarkingCondition.ANY) {
        l.log("for any reachable marking\n");
      } else {
        l.log("for any reachable marking satisfying the specified marking condition\n");
      }
    }
    long startTime = System.currentTimeMillis();
    
    List<Marking> rowMarkings = new ArrayList();
    rowMarkings.add(getInitialMarking());
    
    List<Marking> columnMarkings = new ArrayList();
    for (Marking m : getReachableMarkings()) {
      if (markingCondition.evaluate(m))
        columnMarkings.add(m);
    }
    if (l != null) {
      l.log("Row markings: " + rowMarkings + "\n");
      l.log("Column markings: " + columnMarkings + "\n");
    }
    
    TransientSolution<Marking, Marking> p = new TransientSolution(timeLimit, step, rowMarkings, columnMarkings);
    
    OmegaBigDecimal timeValue = OmegaBigDecimal.ZERO;
    OmegaBigDecimal timeStep = new OmegaBigDecimal(step);

    for (int t = 0; t < p.samplesNumber; t++)
    {
	  result_j = 0;
      if (l != null) {
        l.log(timeValue.toString());
      }
      if (monitor != null) {
        monitor.notifyMessage("Computing probabilities at time t=" + timeValue);
      }
      for (int j = 0; j < columnMarkings.size(); j++) {
						
        if (this.stateClasses.get(columnMarkings.get(j)) != null)
        {
          for (State s : (Set<State>)this.stateClasses.get(columnMarkings.get(j))) {
            TransientStochasticStateFeature transientFeature = (TransientStochasticStateFeature)s.getFeature(TransientStochasticStateFeature.class);
            StochasticStateFeature stochasticFeature = (StochasticStateFeature)s.getFeature(StochasticStateFeature.class);
            //重点就在这
						p.solution[t][0][j] += (!visitedProbabilies ? 
              transientFeature.computeTransientClassProbability(timeValue, stochasticFeature).doubleValue() : 
              transientFeature.computeVisitedProbability(OmegaBigDecimal.ZERO, timeValue, stochasticFeature).doubleValue());
            
            if ((monitor != null) && (monitor.interruptRequested())) {
              monitor.notifyMessage("Aborted");
              return null;
            }
          }
        }
        if (l != null) {
        	
          OmegaBigDecimal currentTime = timeStep.multiply(new OmegaBigDecimal(t));
          l.log(" " + p.solution[t][0][j]);
						result_j = result_j + p.solution[t][0][j];
						resultlabel.setText("t="+currentTime+"  "+p.solution[t][0][j]+"");
        }
      }
      if (l != null)
					{
						l.log(" sum=" + result_j);
						l.log("\n");					
					}
        
      timeValue = timeValue.add(timeStep);
    }
			
    
    if (l != null) {
      l.log(">> Discretization took " + (System.currentTimeMillis() - startTime) / 1000L + "s\n");
    }
    if (monitor != null) {
      monitor.notifyMessage("Computation completed");
    }



    return p;
  }	
}


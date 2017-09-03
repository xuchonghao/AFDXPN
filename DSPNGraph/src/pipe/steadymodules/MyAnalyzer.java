package pipe.steadymodules;


import it.unifi.oris.sirio.analyzer.AnalyzerComponentsFactory;
import it.unifi.oris.sirio.analyzer.AnalyzerObserver;
import it.unifi.oris.sirio.analyzer.EnabledEventsBuilder;
import it.unifi.oris.sirio.analyzer.Event;
import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.SuccessionEvaluator;
import it.unifi.oris.sirio.analyzer.SuccessionProcessor;
import it.unifi.oris.sirio.analyzer.enumeration_policy.EnumerationPolicy;
import it.unifi.oris.sirio.analyzer.enumeration_policy.FIFOPolicy;
import it.unifi.oris.sirio.analyzer.graph.SuccessionGraph;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.analyzer.stop_criterion.StopCriterion;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.math.domain.DBMZone;
import it.unifi.oris.sirio.math.expression.Exmonomial;
import it.unifi.oris.sirio.math.expression.Expolynomial;
import it.unifi.oris.sirio.math.function.EXP;
import it.unifi.oris.sirio.math.function.Function;
import it.unifi.oris.sirio.math.function.GEN;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticTransitionFeature;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;
import it.unifi.oris.sirio.petrinet.TransitionFeature;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class MyAnalyzer
{
  private Set<AnalyzerObserver> observers = new LinkedHashSet();
  private StoSuccessionGraph graph = new StoSuccessionGraph();


  
  private PetriNet model;
  private State initialState;
  private AnalyzerComponentsFactory componentsFactory;
  
  EnabledEventsBuilder enabledEventsBuilder;
  SuccessionEvaluator successionEvaluator;
				//加一个
			MysuccessionEvaluator mysuccessionEvaluator;
  SuccessionProcessor preProcessor;
  SuccessionProcessor postProcessor;
  StopCriterion globalStopCriterion;
  StopCriterion localStopCriterion;
  
			SuccessionEvaluator successionEvaluator1;
			
  public MyAnalyzer(AnalyzerComponentsFactory componentsFactory, PetriNet model, State initialState)
  {
    this.componentsFactory = componentsFactory;
    this.model = model;
    this.initialState = initialState;
  }
  
  public void addObserver(AnalyzerObserver observer) {
    this.observers.add(observer);
  }
  
  public void removeObserver(AnalyzerObserver observer) {
    this.observers.remove(observer);
  }
  
  public StoSuccessionGraph analyze() throws IOException
  {
				 Queue<StoSuccession> queue = new LinkedList();
    //this.enumerationPolicy = this.componentsFactory.getEnumerationPolicy();
    this.enabledEventsBuilder = this.componentsFactory.getEnabledEventsBuilder();
    this.successionEvaluator = this.componentsFactory.getSuccessionEvaluator();
    this.preProcessor = this.componentsFactory.getPreProcessor();
    this.postProcessor = this.componentsFactory.getPostProcessor();
    this.globalStopCriterion = this.componentsFactory.getGlobalStopCriterion();
    this.localStopCriterion = this.componentsFactory.getLocalStopCriterion();
    

				this.mysuccessionEvaluator = new MysuccessionEvaluator(
						      false, 
						      new it.unifi.oris.sirio.models.pn.PetriTokensRemover(), 
						      new it.unifi.oris.sirio.models.pn.PetriTokensAdder(), 
						      false,
						      new OmegaBigDecimal("0"));
				//我觉得这个不重要，试试先写个数字
				
				//this.successionEvaluator1 = (SuccessionEvaluator<M, E>) this.mysuccessionEvaluator;
				
    StoSuccession initialSuccession = new StoSuccession(null, null, new StoState(this.initialState.getFeatures()));
    //notifySuccessionCreated(initialSuccession);
    
    //initialSuccession = this.postProcessor.process(initialSuccession);
   // notifySuccessionPostProcessed(initialSuccession);
    
    if (initialSuccession != null) {
				
      queue.add(initialSuccession);
     // notifySuccessionInserted(initialSuccession);
    }
    
					int id = 1;
				
	FileWriter file1 = new FileWriter("D:\\log.txt",true);
    do
    {

      StoSuccession currentSuccession = queue.remove();
      //notifySuccessionExtracted(currentSuccession);
      
     // currentSuccession = this.preProcessor.process(currentSuccession);
      //notifySuccessionPreProcessed(currentSuccession);
      
					//说明没有存在的相等的
					boolean newChild = this.graph.addSuccession(currentSuccession);
					System.out.println("newChild = "+ newChild);
					//file1.write("newChild = "+ newChild+"\r\n");
					//notifyNodeAdded(currentSuccession);
					
					
//     				boolean newChild = this.graph.addSuccession(currentSuccession);
      
      
      if ((newChild) && (!this.localStopCriterion.stop()))
      {
						//file1.write(id + " state class"+"\r\n");
						System.out.println(id + " state class");
						
							//file1.write("parent "+currentSuccession.getChild().getFeature(PetriStateFeature.class).getMarking()+"\r\n");
							//file1.write(currentSuccession.getChild().getFeature(StochasticStateFeature.class)+"\r\n");
							//file1.flush();
						
						
						id++;
        for (Object e : this.enabledEventsBuilder.getEnabledEvents(this.model, currentSuccession.getChild())) {
          StoSuccession childSuccession = this.mysuccessionEvaluator.computeSuccession(this.model, currentSuccession.getChild(), (Transition)e);
          		
						if (childSuccession != null)
						{
							
							System.out.println(childSuccession.getChild().getFeature(PetriStateFeature.class).getMarking());	
							System.out.println(childSuccession.getChild().getFeature(StochasticStateFeature.class));
							//file1.write("child\r\n");
							//file1.write(childSuccession.getChild().getFeature(PetriStateFeature.class).getMarking()+"\r\n");
							//file1.write(childSuccession.getChild().getFeature(StochasticStateFeature.class)+"\r\n");
							//file1.flush();
							
							//得到变迁的平均发生时间
					Transition transition = (Transition) e;
					StochasticTransitionFeature stf = transition.getFeature(StochasticTransitionFeature.class);
					Function firingTimeDensity= stf.getFiringTimeDensity();
					
					
					if(firingTimeDensity instanceof GEN)
					{
						DBMZone domain = firingTimeDensity.getDomain();
						OmegaBigDecimal lft = domain.getCoefficient(it.unifi.oris.sirio.math.expression.Variable.X, it.unifi.oris.sirio.math.expression.Variable.TSTAR);
						OmegaBigDecimal eftNegate = domain.getCoefficient(it.unifi.oris.sirio.math.expression.Variable.TSTAR, it.unifi.oris.sirio.math.expression.Variable.X);
						OmegaBigDecimal eft = eftNegate.negate();
						
						OmegaBigDecimal result = lft.add(eft);
						OmegaBigDecimal meanTime = result.divide(new BigDecimal(2), MathContext.DECIMAL128);
						childSuccession.addFeature(new SuccessionSojournTimeFeature(meanTime));							
					}
					else if(firingTimeDensity instanceof EXP)
					{
						Expolynomial density = firingTimeDensity.getDensity();
						List<Exmonomial> exmonomials = density.getExmonomials();
						OmegaBigDecimal lambda = exmonomials.get(0).getConstantTerm();
						
						OmegaBigDecimal meanTime = new OmegaBigDecimal(1).divide(lambda.bigDecimalValue(), MathContext.DECIMAL128);
						
						childSuccession.addFeature(new SuccessionSojournTimeFeature(meanTime));							
					}
					else if(firingTimeDensity instanceof ErLang)
					{
						BigDecimal shape = new BigDecimal(((ErLang) firingTimeDensity).getShape());
						BigDecimal lambda = ((ErLang) firingTimeDensity).getLambda();
						BigDecimal result = shape.divide(lambda, MathContext.DECIMAL128);
						
						OmegaBigDecimal meanTime = new OmegaBigDecimal(result);			
						
						childSuccession.addFeature(new SuccessionSojournTimeFeature(meanTime));							
					}
           // notifySuccessionCreated(childSuccession);
            
            //childSuccession = this.postProcessor.process(childSuccession);
           // notifySuccessionPostProcessed(childSuccession);
            	
            queue.add(childSuccession);
           // notifySuccessionInserted(childSuccession);
          }
          
          if (this.globalStopCriterion.stop()) {
            break;
          }
        }
      }
				
       if (queue.isEmpty()) break; 
				} while (!this.globalStopCriterion.stop());
			file1.close();
    
    while (!queue.isEmpty()) {
      StoSuccession finalSuccession = queue.remove();
      //notifySuccessionExtracted(finalSuccession);
      
      //finalSuccession = this.preProcessor.process(finalSuccession);
     // notifySuccessionPreProcessed(finalSuccession);
      
      this.graph.addSuccession(finalSuccession);
      //notifyNodeAdded(finalSuccession);
    }
    
		
    return this.graph;
  }
  
//  private void notifySuccessionCreated(Succession succession) {
//    this.globalStopCriterion.notifySuccessionCreated(succession);
//    this.localStopCriterion.notifySuccessionCreated(succession);
//    for (AnalyzerObserver o : this.observers)
//      o.notifySuccessionCreated(succession);
//  }
//  
//  private void notifySuccessionPostProcessed(Succession succession) {
//    this.globalStopCriterion.notifySuccessionPostProcessed(succession);
//    this.localStopCriterion.notifySuccessionPostProcessed(succession);
//    for (AnalyzerObserver o : this.observers)
//      o.notifySuccessionPostProcessed(succession);
//  }
//  
//  private void notifySuccessionInserted(Succession succession) {
//    this.globalStopCriterion.notifySuccessionInserted(succession);
//    this.localStopCriterion.notifySuccessionInserted(succession);
//    for (AnalyzerObserver o : this.observers)
//      o.notifySuccessionInserted(succession);
//  }
  
  private void notifySuccessionExtracted(Succession succession) {
    this.globalStopCriterion.notifySuccessionExtracted(succession);
    this.localStopCriterion.notifySuccessionExtracted(succession);
    for (AnalyzerObserver o : this.observers)
      o.notifySuccessionExtracted(succession);
  }
  
  private void notifySuccessionPreProcessed(Succession succession) {
    this.globalStopCriterion.notifySuccessionPreProcessed(succession);
    this.localStopCriterion.notifySuccessionPreProcessed(succession);
    for (AnalyzerObserver o : this.observers)
      o.notifySuccessionPreProcessed(succession);
  }
  
  private void notifyNodeAdded(Succession succession) {
    this.globalStopCriterion.notifyNodeAdded(succession);
    this.localStopCriterion.notifyNodeAdded(succession);
    for (AnalyzerObserver o : this.observers) {
      o.notifyNodeAdded(succession);
    }
  }
}



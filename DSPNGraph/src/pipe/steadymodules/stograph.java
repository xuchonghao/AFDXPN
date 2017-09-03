package pipe.steadymodules;

import it.unifi.oris.sirio.analyzer.enumeration_policy.FIFOPolicy;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticSuccessionFeature;
import it.unifi.oris.sirio.models.stpn.StochasticTransitionFeature;
import it.unifi.oris.sirio.models.stpn.components_factory.StochasticComponentsFactory;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Place;
import it.unifi.oris.sirio.petrinet.Transition;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class stograph {
	
//这个类实现了生成stochastic state class graph
//就是没有transient state feature，与时间无关 
//有限的图
	public static void build_test(PetriNet petriNet, Marking initialMarking) {
		
		Place p1 = petriNet.addPlace("p1");
		Place p2 = petriNet.addPlace("p2");
		Place p3 = petriNet.addPlace("p3");
		Transition t1 = petriNet.addTransition("t1");		
		Transition t2 = petriNet.addTransition("t2");
		Transition t3 = petriNet.addTransition("t3");
		petriNet.addPrecondition(p1, t1);
		petriNet.addPostcondition(t1, p2);
		petriNet.addPrecondition(p2, t2);
		petriNet.addPostcondition(t2, p3);
		petriNet.addPrecondition(p2, t3);
		petriNet.addPostcondition(t3, p1);
		
		t1.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(1)));
		t2.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(1)));
		t3.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(1)));

		initialMarking.setTokens(p1, 3);
	}
	public static void build_sto(PetriNet petriNet, Marking initialMarking) {
		
		Place p1 = petriNet.addPlace("p1");
		Place p2 = petriNet.addPlace("p2");
		Place p3 = petriNet.addPlace("p3");
		Place p4 = petriNet.addPlace("p4");
		Place p5 = petriNet.addPlace("p5");
		Place p6 = petriNet.addPlace("p6");
		
		Transition t1 = petriNet.addTransition("t1");		
		Transition t2 = petriNet.addTransition("t2");
		Transition t3 = petriNet.addTransition("t3");
		Transition t4 = petriNet.addTransition("t4");

		petriNet.addPrecondition(p1, t4);
		petriNet.addPrecondition(p2, t4);
		petriNet.addPrecondition(p3, t4);
		petriNet.addPrecondition(p4, t1);
		petriNet.addPrecondition(p5, t2);
		petriNet.addPrecondition(p6, t3);
		
		petriNet.addPostcondition(t1, p1);
		petriNet.addPostcondition(t2, p2);
		petriNet.addPostcondition(t3, p3);
		petriNet.addPostcondition(t4, p4);
		petriNet.addPostcondition(t4, p5);
		petriNet.addPostcondition(t4, p6);
		
		//t1.addFeature(StochasticTransitionFeature.newUniformInstance(new OmegaBigDecimal("5"), new OmegaBigDecimal("10")));
		
		StochasticTransitionFeature newFeature = new StochasticTransitionFeature();
		
		ErLang firingTimeDensity =  new ErLang(Variable.X, 1,new BigDecimal(3));
		newFeature.setFiringTimeDensity(firingTimeDensity);
		
		t1.addFeature(newFeature);
		t2.addFeature(StochasticTransitionFeature.newUniformInstance(new OmegaBigDecimal("2"), new OmegaBigDecimal("8")));
		t3.addFeature(StochasticTransitionFeature.newUniformInstance(new OmegaBigDecimal("3"), new OmegaBigDecimal("9")));
		t4.addFeature(StochasticTransitionFeature.newUniformInstance(new OmegaBigDecimal("2"), new OmegaBigDecimal("4")));

		initialMarking.setTokens(p4, 1);
		initialMarking.setTokens(p5, 1);
		initialMarking.setTokens(p6, 1);
	}
	
public static void build_abo(PetriNet petriNet, Marking initialMarking) {
		
		Place p1 = petriNet.addPlace("p1");
		Place p2 = petriNet.addPlace("p2");
		
		Transition t1 = petriNet.addTransition("t1");	

		petriNet.addPrecondition(p1, t1);
		petriNet.addPostcondition(t1, p2);
		
		//t1.addFeature(StochasticTransitionFeature.newUniformInstance(new OmegaBigDecimal("5"), new OmegaBigDecimal("10")));
		
		StochasticTransitionFeature newFeature = new StochasticTransitionFeature();
		
		ErLang firingTimeDensity =  new ErLang(Variable.X, 1,new BigDecimal(3));
		newFeature.setFiringTimeDensity(firingTimeDensity);
		
		t1.addFeature(newFeature);
		
		initialMarking.setTokens(p1, 1);
	}

public static void build_three(PetriNet petriNet, Marking initialMarking) {
	
	Place up = petriNet.addPlace("up");
	Place clock = petriNet.addPlace("clock");
	Place fprob = petriNet.addPlace("fprob");
	Place down = petriNet.addPlace("down");
	Place rej = petriNet.addPlace("rej");
	Place flush = petriNet.addPlace("flush");
	
	Transition Tprob = petriNet.addTransition("Tprob");	
	Transition Tdown = petriNet.addTransition("Tdown");	
	Transition Tup = petriNet.addTransition("Tup");	
	Transition Tclock = petriNet.addTransition("Tclock");	
	Transition Trej1 = petriNet.addTransition("Trej1");	
	Transition Trej2 = petriNet.addTransition("Trej2");	
	Transition Tflush = petriNet.addTransition("Tflush");	
	Transition Tstop = petriNet.addTransition("Tstop");	

	petriNet.addPrecondition(up, Tprob);
	petriNet.addPrecondition(fprob, Tdown);
	petriNet.addPrecondition(fprob, Tflush);
	petriNet.addPrecondition(fprob, Trej1);
	petriNet.addPrecondition(down, Tup);
	petriNet.addPrecondition(clock, Tclock);
	petriNet.addPrecondition(rej, Trej1);
	petriNet.addPrecondition(rej, Trej2);
	petriNet.addPrecondition(flush, Tflush);
	petriNet.addPrecondition(flush, Tstop);
	
	
	petriNet.addInhibitorArc(fprob,Trej2);
	petriNet.addInhibitorArc(fprob,Tstop);
	petriNet.addInhibitorArc(rej,Tprob);
	petriNet.addInhibitorArc(rej,Tdown);
	
	petriNet.addPostcondition(Tup, up);
	petriNet.addPostcondition(Tdown, down);
	petriNet.addPostcondition(Tprob, fprob);
	petriNet.addPostcondition(Tclock, rej);
	petriNet.addPostcondition(Trej1, flush);
	petriNet.addPostcondition(Trej1, up);
	petriNet.addPostcondition(Tflush, flush);
	petriNet.addPostcondition(Tflush, up);
	petriNet.addPostcondition(Tstop, clock);
	petriNet.addPostcondition(Trej2, clock);
	//t1.addFeature(StochasticTransitionFeature.newUniformInstance(new OmegaBigDecimal("5"), new OmegaBigDecimal("10")));

	Tprob.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(0.066666667)));
	Tdown.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(0.066666667)));
	Tup.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(72)));
	Trej1.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(72)));
	Trej2.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(72)));

	Tclock.addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal(1)));
	Tflush.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(0)));
	Tstop.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(0)));

//	StochasticTransitionFeature newFeature = new StochasticTransitionFeature();
//	
//	ErLang firingTimeDensity =  new ErLang(Variable.X, 2,new BigDecimal(0.142857));
//	newFeature.setFiringTimeDensity(firingTimeDensity);
//	
//	Tprob.addFeature(newFeature);
	
	initialMarking.setTokens(up, 3);
	initialMarking.setTokens(clock, 1);
}	


public static void main(String[] args) throws IOException {
		PetriNet petriNet = new PetriNet();
		Marking initialMarking = new Marking();
		build_three(petriNet, initialMarking);
		//build_GG122_12(petriNet, initialMarking);
		//成功得到了瞬态分析的API
	
		//这个timebound没什么用
		OmegaBigDecimal timeBound = new OmegaBigDecimal("0");
		
		StochasticComponentsFactory f = new StochasticComponentsFactory(false,
				null, null, false, new FIFOPolicy(), 
				timeBound, MarkingCondition.NONE, null, 0, null);
		
		MyAnalyzer analyzer = new MyAnalyzer(f, petriNet, 
				new NewlyEnablingStateBuilder(petriNet, false).build(initialMarking));
			//transientanalysis 为false  distinctNewlyEnabledConditions  false
							
		StoSuccessionGraph graph = analyzer.analyze();
			
			System.out.println(graph.getNodes().size());
						
			int len = graph.getNodes().size();
		
			//prob相当于所有两个节点间的转换概率的矩阵
			double [][] prob = new double[len][len];
			
			Set<Marking> marking = new HashSet<Marking>();
			Map<Integer, Marking> NodeId_to_Marking = new HashMap<Integer, Marking>();
						
			//输出可达图的Node，以及从Node出发的各边发生的概率
			for (SSGNode n: graph.getNodes()) {
				System.out.println(n.getId());
				NodeId_to_Marking.put(n.getId(), graph.getState(n).getFeature(PetriStateFeature.class).getMarking());
	            for (StoSuccession s: graph.getOutgoingSuccessions(n)) 
	            {            	
	            	Marking parent = s.getParent().getFeature(PetriStateFeature.class).getMarking();
	            	marking.add(parent);
	            	
	            	Marking child = s.getChild().getFeature(PetriStateFeature.class).getMarking();
	            	BigDecimal probability = s.getFeature(StochasticSuccessionFeature.class).getProbability();
	            	System.out.println("Parent: "+ parent);
	            	System.out.println("Successor marking: " + child);

	                System.out.println("Successor probability: "+ probability);

	                System.out.println("Child Node Id " + graph.getNode(s.getChild()).getId());
	                System.out.println("MeanTime " + s.getFeature(SuccessionSojournTimeFeature.class));
	                
	                prob[n.getId()-1][graph.getNode(s.getChild()).getId()-1] = 
	                		s.getFeature(StochasticSuccessionFeature.class).getProbability().doubleValue();
	            }
			}

			//得到各个随机状态类转移概率矩阵
			FindBSCC findbscc = new FindBSCC(prob);
			HashSet<HashSet<Integer>> bsccList = findbscc.computeBSCC();
			List<HashSet<Integer>> bsccList1 = new LinkedList(bsccList);
			//计算从初始进入各个BSCC的概率
			EnterBSCCProb e = new EnterBSCCProb(prob, bsccList1);			
			e.compute();
			//以数组的形式给出结果
			double[] enterProb = e.getEnterProb();
			System.out.println(enterProb);
			
			//代表各个Marking的稳定状态概率的Map
			Map<Marking, Double> steady = new HashMap<Marking, Double>();
			
			for(int i = 0; i < bsccList1.size(); i++){
				HashSet<Integer> bscc = bsccList1.get(i);
				if(bscc.size() == 1){
					Object[] array =  bscc.toArray();
					Integer a = (Integer) array[0];
					Marking m = NodeId_to_Marking.get(a);
					steady.put(m, enterProb[i]);
				}
				else{
					int size = bscc.size();
					Object[] array = bscc.toArray();
				
					double[][] inner_prob = new double[size][size];
					for(int parent = 0; parent < size; parent++){
						for(int child = 0; child < size; child++){
							inner_prob[parent][child] = prob[(Integer) array[parent]-1][(Integer) array[child]-1];	
						}
					}
					
					//离散时间的稳态概率
					double [] discrete_result = DTMCSteady.compute(inner_prob);
					//算出每个状态的平均逗留时间
					double []meanTime = new double[size];
					for(int parent = 0; parent < size; parent++){
						int NodeId = (Integer) array[parent];
						Set<SSGNode> NodeSet = graph.getNodes();
						SSGNode current_node = null;
						for(SSGNode node: NodeSet){
							if(node.getId() == NodeId){
								current_node = node;
								break;
							}
						}
						if(current_node!=null){						
							for (StoSuccession s: graph.getOutgoingSuccessions(current_node)) 
				            {            				
								//因为这个节点在BSCC中，所以所有的边都包含在这个BSCC中
				            	BigDecimal probability = s.getFeature(StochasticSuccessionFeature.class).getProbability();
				            	
				            	OmegaBigDecimal sojournTime = s.getFeature(SuccessionSojournTimeFeature.class).getMeanTime();

				                System.out.println("Succession probability: "+ probability);
				                System.out.println("Succession MeanTime " + sojournTime);
				                meanTime[parent] += probability.multiply(sojournTime.bigDecimalValue()).doubleValue();				                			                
				            }						
							
						}
						else{
							System.out.println("没找到NodeId对应的Node");
						}
					}
					
					//计算连续时间的稳定状态概率

					double [] continous_result = new double[size];
					double sum = 0;
					for(int num = 0 ; num < size; num++){
						sum += discrete_result[num] * meanTime[num];
					}
					for(int num = 0 ; num < size; num++){
						continous_result[num] = discrete_result[num] * meanTime[num]/sum;
						Marking m = NodeId_to_Marking.get(array[num]);
						Double currentProb = steady.get(m);
						if(currentProb==null){
							steady.put(m, continous_result[num]);
						}
						else{
							steady.put(m, currentProb + continous_result[num]);
						}
					}
					
				}
			}	
			System.out.println(steady);
			
//			//下面这些只针对强连通的
//			double [] result = DTMCSteady.compute(prob);
//			Map<Marking, Double> steady = new HashMap<Marking, Double>();
//			for(Marking m : marking)
//			{
//				double sum = 0;
//				for(int i = 0; i < result.length; i++)
//				{
//					if(NodeId_to_Marking.get(i+1).equals(m))
//						sum += result[i];
//				}
//				steady.put(m, sum);
//			}
//			
//			System.out.println(steady);
//			
			
	}
}

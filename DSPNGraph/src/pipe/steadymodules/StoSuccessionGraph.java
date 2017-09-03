package pipe.steadymodules;


import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.state.State;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;



			  public class StoSuccessionGraph {

  private SSGNode root;
  private Set<SSGNode> nodes = new LinkedHashSet();
  
  private Map<SSGNode, Set<SSGNode>> successors = new LinkedHashMap();
  private Map<SSGNode, Set<SSGNode>> predecessors = new LinkedHashMap();
  
  private Map<SSGNode, StoState> nodeState = new LinkedHashMap();
  private Map<StoState, SSGNode> stateNode = new LinkedHashMap();
  
  private Map<SSGEdge, Set<StoSuccession>> successions = new LinkedHashMap();
  
  private boolean addNode(SSGNode n, StoState s)
  {
    boolean newNode = this.nodes.add(n);
    
    if (newNode) {
      this.nodeState.put(n, s);
      this.stateNode.put(s, n);
      this.successors.put(n, new LinkedHashSet());
      this.predecessors.put(n, new LinkedHashSet());
    }
    
    return newNode;
  }
  
  private boolean addEdge(SSGNode n1, SSGNode n2)
  {
    boolean newEdge = ((Set)this.successors.get(n1)).add(n2);
    ((Set)this.predecessors.get(n1)).add(n1);
    
    if (newEdge) {
      this.successions.put(new SSGEdge(n1, n2), new LinkedHashSet());
    }
    return newEdge;
  }
  
  public boolean addSuccession(StoSuccession s)
  {
				//	System.out.println("StoSuccesionGraph addSuccession"+s);
    if (s.getParent() == null)
    {
      if (this.root != null) {
        throw new IllegalStateException("Root already set");
      }
      this.root = new SSGNode();

      addNode(this.root, s.getChild());
      return true;
    }
    
    SSGNode predecessorNode = (SSGNode)this.stateNode.get(s.getParent());
    if (predecessorNode == null) {
      throw new IllegalArgumentException("No node is associated with the predecessor state of succession:\n" + s);
    }
    boolean newSuccessorNode = false;
    SSGNode successorNode = (SSGNode)this.stateNode.get(s.getChild());
    if (successorNode == null) {
      newSuccessorNode = true;
      successorNode = new SSGNode();


					
      addNode(successorNode, s.getChild());
    }
    
    addEdge(predecessorNode, successorNode);
    ((Set)this.successions.get(new SSGEdge(predecessorNode, successorNode))).add(s);
    
    return newSuccessorNode;
  }
  
  public SSGNode getRoot()
  {
    return this.root;
  }
  
  public Set<SSGNode> getNodes() {
    return Collections.unmodifiableSet(this.nodes);
  }
  
  public Set<SSGNode> getPredecessors(SSGNode n) {
    return (Set)this.predecessors.get(n);
  }
  
  public Set<SSGNode> getSuccessors(SSGNode n) {
    return (Set)this.successors.get(n);
  }
  
  public StoState getState(SSGNode n) {
    return (StoState)this.nodeState.get(n);
  }
  
  public SSGNode getNode(StoState s) {
    return (SSGNode)this.stateNode.get(s);
  }
  
  public Set<StoState> getStates() {
    return this.stateNode.keySet();
  }
  
  public Set<StoSuccession> getSuccessions() {
    Set<StoSuccession> r = new LinkedHashSet();
    
    for (Set<StoSuccession> s : this.successions.values()) {
      r.addAll(s);
    }
    return r;
  }
  
  public Set<StoSuccession> getSuccessions(SSGEdge e) {
    return (Set)this.successions.get(e);
  }
  
  public Set<StoSuccession> getIngoingSuccessions(SSGNode n) {
    Set<StoSuccession> ingoingSuccessions = new LinkedHashSet();
    
    for (SSGNode predecessor : (Set<SSGNode>)this.predecessors.get(n)) {
      ingoingSuccessions.addAll((Collection)this.successions.get(new SSGEdge(predecessor, n)));
    }
    return ingoingSuccessions;
  }
  
  public Set<StoSuccession> getOutgoingSuccessions(SSGNode n) {
    Set<StoSuccession> outgoingSuccessions = new LinkedHashSet();
    
    for (SSGNode successor : (Set<SSGNode>)this.successors.get(n)) {
      outgoingSuccessions.addAll((Collection)this.successions.get(new SSGEdge(n, successor)));
    }
    return outgoingSuccessions;
  }
}


/* Location:           D:\SirioExample\SirioExample\lib\Sirio-1.0.0.jar
 * Qualified Name:     it.unifi.oris.sirio.analyzer.graph.SuccessionGraph
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */
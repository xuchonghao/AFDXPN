package pipe.steadymodules;

import it.unifi.oris.sirio.analyzer.graph.Node;

import java.util.concurrent.atomic.AtomicInteger;

public class SSGNode {

  private static AtomicInteger counter = new AtomicInteger(0);
  private int id = 0;
  

			public static void clear() {
     counter = new AtomicInteger(0);
  }

  public SSGNode() {
    this.id = counter.addAndGet(1);
  }
  
  public int getId() {
    return this.id;
  }
}
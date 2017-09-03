package pipe.steadymodules;



public class SSGEdge {

		  SSGNode predecessor;
		  SSGNode successor;
		  
		  public SSGEdge(SSGNode predecessor, SSGNode successor) {
		    this.predecessor = predecessor;
		    this.successor = successor;
		  }
		  
		  public boolean equals(Object obj)
		  {
		    if (obj == this) {
		      return true;
		    }
		    if (!(obj instanceof SSGEdge)) {
		      return false;
		    }
		    SSGEdge o = (SSGEdge)obj;
		    
		    return (this.predecessor.equals(o.predecessor)) && (this.successor.equals(o.successor));
		  }
		  
		  public int hashCode()
		  {
		    int result = 17;
		    result = 31 * result + this.predecessor.hashCode();
		    result = 31 * result + this.successor.hashCode();
		    return result;
		  }
		}

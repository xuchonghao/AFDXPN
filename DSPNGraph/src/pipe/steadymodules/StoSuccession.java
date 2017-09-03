package pipe.steadymodules;

import it.unifi.oris.sirio.analyzer.Event;
import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.SuccessionFeature;
import it.unifi.oris.sirio.util.Featurizable;



public class StoSuccession extends Featurizable<SuccessionFeature>
{
  private StoState parent;
  private Event event;
  private StoState child;
  
  public StoSuccession(StoState parent, Event event, StoState child)
  {
    this.parent = parent;
    this.event = event;
    this.child = child;
  }
  
			public StoSuccession(Succession s)
  {
			   if(s.getParent()!=null)
				   this.parent = new StoState(s.getParent().getFeatures());
			   else
				   this.parent = null;
			   this.event = s.getEvent();

				if(s.getChild()!=null)
					this.child = new StoState(s.getChild().getFeatures());
			   else
				   this.child = null;

			}
			
  public StoState getChild() {
    return this.child;
  }
  
  public StoState getParent() {
    return this.parent;
  }
  
  public Event getEvent() {
    return this.event;
  }
  
  public void setParent(StoState parent) {
    this.parent = parent;
  }
  
  public void setChild(StoState child) {
    this.child = child;
  }
  
  public void setEvent(Event event) {
    this.event = event;
  }
  
  public String toString()
  {
    StringBuilder b = new StringBuilder();
    b.append("-- Parent --\n");
    b.append(this.parent);
    b.append("-- Event --\n");
    b.append(this.event + "\n");
    b.append("-- Child --\n");
    b.append(this.child);
    b.append(super.toString());
    return b.toString();
  }
}

/*
 * placeRateEdit.java
 */
package pipe.historyActions;

import pipe.views.TransitionView;


/**
 *
 * @author corveau
 */
public class TransitionRate
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   private final Double newRate;
   private final Double oldRate;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _transitionView
    * @param _oldRate
    * @param _newRate*/
   public TransitionRate(
           TransitionView _transitionView, Double _oldRate, Double _newRate) {
      this._transitionView = _transitionView;
      oldRate = _oldRate;      
      newRate = _newRate;
   }

   
   /** */
   public void undo() {
      _transitionView.setRate(oldRate);
   }

   
   /** */
   public void redo() {
      _transitionView.setRate(newRate);
   }
   
}

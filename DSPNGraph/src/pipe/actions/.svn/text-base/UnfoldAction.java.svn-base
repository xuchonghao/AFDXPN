package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.utilities.Expander;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * @author Alex Charalambous, June 2010: Unfolds a coloured Petri net
 *         to an ordinary Petri net.
 */
public class UnfoldAction extends GuiAction
{

    public UnfoldAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        Expander expander = new Expander(ApplicationSettings.getApplicationView().getCurrentPetriNetView());
        PetriNetView unfolded = expander.unfold();
        pipeApplicationView.createNewTab(expander.saveAsXml(unfolded), false);
    }
}

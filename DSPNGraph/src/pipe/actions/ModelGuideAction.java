package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.newwidges.ModelGuideDialog1;

import java.awt.event.ActionEvent;

/**
 * Created by hanson on 2017/8/15.
 */
public class ModelGuideAction extends GuiAction
{
    public ModelGuideAction(String name, String tooltip, String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ModelGuideDialog1 guiDialog =  new ModelGuideDialog1(ApplicationSettings.getApplicationView(), true);

        guiDialog.pack();

        guiDialog.setLocationRelativeTo(null);

        guiDialog.setVisible(true);
    }
}

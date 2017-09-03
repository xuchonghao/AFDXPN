package pipe.views;

import pipe.controllers.MarkingController;
import pipe.models.Marking;
import pipe.models.interfaces.IObserver;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class MarkingView extends JComponent implements Serializable, IObserver
{
    private TokenView _tokenView;
    private final Marking _model;
    private MarkingController _controller;

    public MarkingView(MarkingController controller, Marking model)
    {
        _controller = controller;
        _model = model;
        _model.registerObserver(this);
        _tokenView = new TokenView(_controller.getTokenController(), _model.getToken());
    }


    public MarkingView(TokenView tokenView, int marking)
    {
        _tokenView = tokenView;
        _model = new Marking(tokenView.getModel(), marking);
    }

    public TokenView getToken()
    {
        return _tokenView;
    }

    public void setToken(TokenView tokenView)
    {
        _tokenView = tokenView;
        _model.setToken(tokenView.getModel());
    }

    public void setCurrentMarking(int marking)
    {
        _model.setCurrentMarking(marking);
    }

    public int getCurrentMarking()
    {
        return _model.getCurrentMarking();
    }

    @Override
    public void update()
    {
       // paint()
    }

    public void update(Graphics canvas, Insets insets, int count, int tempTotalMarking)
    {
        _tokenView.update(canvas,insets,count, tempTotalMarking, getCurrentMarking());
    }
}

package pipe.models;

import java.io.Serializable;

public class Transition extends Connectable implements Serializable
{
    private double _rate;
    private Integer _priority;
    public Transition(String id, String name)
    {
        this(id, name, 1,1);
    }

    public Transition(String id, String name, double rate, int priority)
    {
        super(id, name);
        _rate = rate;
        _priority = priority;
    }


    public double getRate()
    {
        return _rate;
    }

    public void setRate(double rate)
    {
        _rate = rate;
    }

    public Integer getPriority()
    {
        return _priority;
    }

    public void setPriority(Integer priority)
    {
        _priority = priority;
    }
}

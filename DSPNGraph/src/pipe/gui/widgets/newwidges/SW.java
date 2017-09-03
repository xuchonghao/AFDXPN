package pipe.gui.widgets.newwidges;


import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanson on 2017/8/29.
 */
public class SW {

    public ArrayList<Element> getIn() {
        return in;
    }

    public void setIn(ArrayList<Element> in) {
        this.in = in;
    }

    public List<String> getOut() {
        return out;
    }

    public void setOut(List<String> out) {
        this.out = out;
    }

    private ArrayList<Element> in = new ArrayList<Element>();
    private List<String> out = new ArrayList<String>();
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package pipe.DSPNModules;

import java.util.HashMap;

/**
 * Created by hanson on 2017/8/3.
 */
public class ArrayOfGraph {

    //HashMap<String,Object> vexs = new HashMap<String,Object>();
    //所用到的两个参数
    //private int edgeLen;
    HashMap<String,Object> vexsMap = new HashMap<String,Object>();
    String[][] edges;

    //public ArrayOfGraph() {
    //}
    public ArrayOfGraph(int edgeLen) {
        edges = new String[edgeLen][2];
    }


    public HashMap<String, Object> getVexsMap() {
        return vexsMap;
    }

    public void setVexsMap(HashMap<String, Object> vexsMap) {
        this.vexsMap = vexsMap;
    }

    public String[][] getEdges() {
        return edges;
    }

    public void setEdges(String[][] edges) {
        this.edges = edges;
    }
}

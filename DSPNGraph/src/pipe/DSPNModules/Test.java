package pipe.DSPNModules;

import pipe.modules.minimalSiphons.MinimalSiphons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hanson on 2017/8/3.
 */
public class Test {

    public static void main(String[] args){
       /* String[] vexs = {"0","1","2","3","4"};
        String[][] edges = {
                {"0","1"}, {"0","3"},
                {"1","0"}, {"1","2"},
                {"2","1"}, {"2","3"}, {"2","4"},
                {"3","0"}, {"3","2"}, {"3","4"},
                {"4","2"}, {"4","3"}
        };
        String[] vexs = {"a","b","c","d","e","f","g"};
        String[][] edges = {
                {"a","b"}, {"b","e"},
                {"e","b"}, {"a","c"},
                {"c","a"}, {"c","f"}, {"f","g"},
                {"g","d"}, {"d","c"}, {"d","a"}
        };*/

        Set<String> vexs = new HashSet<String>();
        vexs.add("a");
        vexs.add("b");vexs.add("c");vexs.add("d");vexs.add("e");vexs.add("f");vexs.add("g");
        String[][] edges = {
                {"a","b"}, {"b","e"},
                {"e","b"}, {"a","c"},
                {"c","a"}, {"c","f"}, {"f","g"},
                {"g","d"}, {"d","c"}, {"d","a"}
        };
        Graph graph = new Graph();
        HashMap<String,Object> vexMap = new HashMap<String,Object>();
        vexMap.put("a","b");vexMap.put("b","e");vexMap.put("e","b");vexMap.put("a","c");
        vexMap.put("c","a");vexMap.put("c","f");vexMap.put("f","g");vexMap.put("g","d");
        vexMap.put("d","c");vexMap.put("d","a");
        //graph.buildGraph(vexs,edges);
        graph.buildGraph(vexMap,edges);
        graph.printGraph();

        FindAllPath findAllPath = new FindAllPath();
        findAllPath.visit(graph,"a","g");
    }

}
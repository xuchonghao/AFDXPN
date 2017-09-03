package pipe.DSPNModules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by hanson on 2017/8/3.
 */
public class Graph {
    private ArrayList<VexNode> vexArray = null;//初始化？

    public Graph() {
        vexArray = new ArrayList();//这里有一个基本问题需要解决！！！
    }

    public void linkLast(EdgeNode target,EdgeNode node){
        while(target.nextEdge!=null){
            target = target.nextEdge;
        }
        target.nextEdge = node;
    }



    public VexNode getVexNode(String data){//这里怎么没有显示错误！

        for(int i=0;i<vexArray.size();i++){
             System.out.println(data == vexArray.get(i).data);
            if(data == vexArray.get(i).data)//
                return vexArray.get(i);
        }
        return null;
    }

    public void addVertex(int sequence,Object data,HashMap<String,Object> vexMap) {
        VexNode vex = new VexNode();// 添加点
        vex.data = (String)data;
        vex.sequence = sequence;
        vex.object = vexMap.get((String) data);
        vex.firstEdge = null;
        vexArray.add(vex);
       //nVerts++;
    }
    public void buildGraph(HashMap<String,Object> vexMap, String[][] edges){
    //public void buildGraph(Set<String> svexs, String[][] edges){
        //Set<String> set = vexMap.keySet(); HashMap<String,Object> vexMap  Set<String> svexs
        Set<String> svexs = vexMap.keySet();
        Object[] vexs = svexs.toArray();
        int vlen = vexs.length;
        int elen = edges.length;
        //vexArray = new ArrayList();//这里有一个基本问题需要解决！！！

        for(int i=0;i<vlen;i++){
            addVertex(i,vexs[i],vexMap);
        }

        for(int i=0;i<elen;i++){
            //每条边由2个元素构成
            String source = edges[i][0];
            String target = edges[i][1];
            //应该根据source：元素的名字   找到对应的Vex
            VexNode s = getVexNode(source);
            VexNode t = getVexNode(target);

            int start = s.sequence;//边的左点对应的序号
            int end = t.sequence; //边的右点对应的序号

            EdgeNode edgeNode = new EdgeNode();
            edgeNode.adjvex = end;//这条边指向终点

            //第一条依附于起始顶点的边若为空，把连向终点的边设为第一个
            if(vexArray.get(start).firstEdge == null)
                vexArray.get(start).firstEdge = edgeNode;
            else//把当前边设为边数组的最后一个
                linkLast(vexArray.get(start).firstEdge,edgeNode);
        }
    }
    public void printGraph(){
        for(int i=0;i<vexArray.size();i++){
            System.out.printf("%s--",vexArray.get(i).data);
            EdgeNode node = vexArray.get(i).firstEdge;
            while(node!=null){
                System.out.printf("%s(%s)--",node.adjvex,vexArray.get(node.adjvex).data);
                node = node.nextEdge;
            }
            System.out.println();
        }
    }
    public ArrayList<VexNode> getVexArray() {
        return vexArray;
    }

    public void setVexArray(ArrayList<VexNode> vexArray) {
        this.vexArray = vexArray;
    }
    class EdgeNode{//边表节点
        int adjvex;//该弧指向的顶点的位置
        EdgeNode nextEdge;
    }

    class VexNode{
        String data;//顶点的信息，这个之后可以改为自己的类型
        int sequence;//该节点在图中对应的序号
        Object object;
        EdgeNode firstEdge;
    }
}

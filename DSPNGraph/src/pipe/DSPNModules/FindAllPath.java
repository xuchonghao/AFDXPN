package pipe.DSPNModules;

/**
 * Created by hanson on 2017/8/3.
 */

import java.util.*;

/**
 * Created by hanson on 2017/8/3.
 */
public class FindAllPath {
    //代表某节点是否在stack中，避免产生回路
    public Map<Integer,Boolean> states = new HashMap();
    //存放放入stack中的结点
    public Stack<Graph.VexNode> stack = new Stack();

    ArrayList<LinkedList> list = new ArrayList<LinkedList>();

    //打印stack中的信息，即路径信息
    public LinkedList<Graph.VexNode> printPath(){
        LinkedList<Graph.VexNode> linkedList = new LinkedList<Graph.VexNode>();
        StringBuilder sb = new StringBuilder();
        for(Graph.VexNode i : stack){
            linkedList.add(i);
            //sb.append(i.data + "->");
        }
        printPath2(linkedList);
        //sb.delete(sb.length()-2,sb.length());
        //System.out.println(sb.toString());
        return linkedList;
    }
    public void printPath2(LinkedList<Graph.VexNode> linkedList){
        StringBuilder sb = new StringBuilder();
        for(Graph.VexNode i : linkedList){
            sb.append(i.data + "->");
        }
        sb.delete(sb.length()-2,sb.length());
        System.out.println(sb.toString());
    }

    //得到x的邻接点为y的后一个邻接点位置，为-1说明没有找到
    public int getNextNode(Graph graph,int top_node_sequence,int last_visited_adjvex_node_sequence){
        int next_node = -1;//这里x指的是？图中的序号和data好像一样
        Graph.EdgeNode edge = graph.getVexArray().get(top_node_sequence).firstEdge;
        if(null!=edge && last_visited_adjvex_node_sequence==-1){//top节点的第一条边不为空，而且第一次访问该节点的边
            int n = edge.adjvex;//这条边指向的结点
            //结点元素不在stack中
            if(!states.get(n))//边指向的目标不在栈中，就可以返回
                return n;
        }

        while(null!=edge){//目标节点如果刚出栈，或者还在栈中，但是是当前节点的第一条边（环路）
            //System.out.println(edge.adjvex);//如果第一条边刚出栈
            if(edge.adjvex == last_visited_adjvex_node_sequence || -1 == last_visited_adjvex_node_sequence){
                //就看下一条边存在与否
                if(null != edge.nextEdge){
                    next_node = edge.nextEdge.adjvex;

                    if(!states.get(next_node))
                        return next_node;
                }
                else
                    return -1;
            }//第一条边不是刚出栈的那个
            edge = edge.nextEdge;
        }
        return -1;
    }
    public Graph.VexNode getVexNode(ArrayList<Graph.VexNode> vexArray,String data){
        //System.out.println("vexArray.size():"+vexArray.size());
        for(int i=0;i<vexArray.size();i++){
            //System.out.println(data == vexArray.get(i).data);
            if(data .equals(vexArray.get(i).data)){//注意，字符串不能用==来表达两个内容相同的东西
                return vexArray.get(i);
            }
        }
        return null;
    }
    public Graph.VexNode getVexNodeFromSequence(ArrayList<Graph.VexNode> vexArray,int sequence){
        for(int i=0;i<vexArray.size();i++)
            if(sequence == vexArray.get(i).sequence)
                return vexArray.get(i);
        return null;
    }
    public ArrayList<LinkedList> visit(Graph graph,String startNode,String endNode){
        ArrayList<Graph.VexNode> vexArray = graph.getVexArray();

        int len = vexArray.size();

        //初始化所有节点在stack中的情况
        for(int i=0;i<len;i++){
            states.put(i,false);
        }

        //根据data获得VexNode节点
        Graph.VexNode nodeX = getVexNode(vexArray,startNode);//TODO
        //System.out.println("......." + nodeX.data);
        Graph.VexNode nodeY = getVexNode(vexArray,endNode);
        Graph.VexNode top_node;

        //应该指从栈中出来的元素；存放当前top元素已经访问过的邻接点，若不存在则置-1，此时代表该top元素的第一个邻接点
        int last_visited_adjvex_node_sequence = -1;
        int next_node;

        stack.add(nodeX);
        states.put(nodeX.sequence,true);//当前x在栈中  TODO sequence 的值

        while(!stack.isEmpty()){//栈不为空
            top_node = stack.peek();//TODO 此时的节点
            //找到需要访问的点
            if(top_node == nodeY) {
                LinkedList<Graph.VexNode> s = printPath();
                list.add(s);
                last_visited_adjvex_node_sequence = stack.pop().sequence;//当前top元素已经访问过的邻接点
                states.put(last_visited_adjvex_node_sequence, false);
            }else{//访问top_node的第advex_node个邻接点
                next_node = getNextNode(graph,top_node.sequence,last_visited_adjvex_node_sequence);
                if(next_node != -1){//存在没有被访问过的邻接点
                    stack.push(getVexNodeFromSequence(graph.getVexArray(),next_node));
                    states.put(next_node,true);
                    //邻接点重置***
                    last_visited_adjvex_node_sequence = -1;
                }else{//不存在邻接点，将stack top元素退出
                    last_visited_adjvex_node_sequence = stack.pop().sequence;
                    states.put(last_visited_adjvex_node_sequence,false);
                }
            }
        }
        return list;
    }
}

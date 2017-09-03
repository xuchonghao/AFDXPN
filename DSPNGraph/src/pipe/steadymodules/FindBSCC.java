package pipe.steadymodules;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public class FindBSCC {

	//表示访问的序号
	private static int index = 0;
	private HashSet<HashSet<Integer>> BSCC = new HashSet<HashSet<Integer>>();
	private double[][] prob;
	private int num;
	
	private int[] DFN;
	private int[] LOW;
	private Stack<Integer> stack = new Stack<Integer>();
	
	FindBSCC(double[][]probability){
		prob = probability;
		num = probability.length;
		DFN = new int[num];
		LOW = new int[num];
	}
	public void tarjan(int u) {
		
		DFN[u] = LOW[u] = ++index;
		stack.push(u);
		
		for(int i = 0; i < num ; i++)
		{
			if(i!=u && prob[u][i] > 0){
				//i没有被访问过
				if(DFN[i]==0){
					tarjan(i);
					LOW[u] = (LOW[u]<LOW[i])?LOW[u]:LOW[i];
				}
				else if(stack.contains(i)){
					LOW[u] = (LOW[u]<DFN[i])?LOW[u]:DFN[i];
				}
			}		
		}
		if (DFN[u] == LOW[u]){	
			HashSet<Integer> h = new HashSet<Integer>();
			int v;
			do{
				v = stack.pop();
				h.add(v+1);
			}while(u!=v);
			BSCC.add(h);
		}						
	}
	
	public void validateBSCC(){
		//从强连通分量中找到BSCC，就是看有没有通向除了自身之外的节点的边
		Iterator<HashSet<Integer>> iterator = BSCC.iterator();
		while(iterator.hasNext()){
			HashSet<Integer> h = iterator.next();
			boolean flag = true;
			for(int a: h){
				for(int i=0;i<num;i++){
					if(prob[a-1][i]>0 && !h.contains(i+1)){
						flag = false;
						break;
					}
				}
				if(flag == false){
					break;
				}
			}
			if(flag == false){
				iterator.remove();
			}
		}
	}
	
	public HashSet<HashSet<Integer>> computeBSCC(){
		tarjan(0);
		validateBSCC();
		System.out.println(this.BSCC);
		return this.BSCC;
	}
}

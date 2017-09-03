package pipe.steadymodules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class EnterBSCCProb {

	private List<HashSet<Integer>> BSCC = new LinkedList();
	private double []enterProb;
	private double[][] prob;
	int num;
	int BSCCnum;
	private HashSet<Integer> allInBSCC = new HashSet<Integer>();
	private List<HashMap<Integer, Double>> equationList = new LinkedList<HashMap<Integer, Double>>();
	
	EnterBSCCProb(double[][]probability, List<HashSet<Integer>> BSCC){
		prob = probability;
		num = probability.length;
		this.BSCC = BSCC;
		BSCCnum = BSCC.size();
		enterProb = new double[BSCCnum];
	}
		
	public double[] getEnterProb(){
		return enterProb;
	}
	
	public void compute(){
		//把所有BSCC里的元素都加到allInBSCC里
		for (HashSet<Integer> h: BSCC){
			allInBSCC.addAll(h);			
		}
		for(int i = 0; i < BSCCnum; i++){
			equationList = new LinkedList<HashMap<Integer, Double>>();
			HashSet<Integer> bscc = BSCC.get(i);
			//这个bscc就包括初始状态
			if(bscc.contains(1)){
				if(BSCCnum==1){
					enterProb[i] = 1;
					break;
				}	
				else{
					System.out.println("包含初始状态，又有多个BSCC，显然有问题");
				}
			}
			else{
				Queue<Integer> s = new LinkedList<Integer>();
				List<Integer> variant = new LinkedList<Integer>();
				List<Integer> visited = new LinkedList<Integer>();
				s.add(1);
				variant.add(1);
				while(!s.isEmpty()){
					HashMap<Integer, Double> equation = new HashMap<Integer, Double>();
					int current = s.poll();
					visited.add(current);
					for(int j = 1; j < num+1 ; j++){						
						//找到从current出发的所有的边
						if(prob[current-1][j-1] > 0){
							if(allInBSCC.contains(j) && !bscc.contains(j)){
								//通向别的BSCC中
								continue;
							}
							else if(bscc.contains(j)){
								//通向这个BSCC的一个节点
								Double constant = equation.get(num+1);
								if(constant == null){
									equation.put(num+1, prob[current-1][j-1]);
								}
								else{
									equation.put(num+1, constant + prob[current-1][j-1]);
								}
							}
							else{
									if(!visited.contains(j)){
									//if(j > current){
										//节点j还没访问过，入栈
										if(!s.contains(j)){
											s.add(j);
											variant.add(j);			
										}
										equation.put(j, -prob[current-1][j-1]);	
									}
									else if(visited.contains(j) && j!=current){
										equation.put(j, -prob[current-1][j-1]);
									}
									
									if(j == current){
										equation.put(j, 1-prob[current-1][j-1]);
									}
									else{
										equation.put(current, (double) 1);
									}
								
							}
											
						}
					}//end for
					equationList.add(equation);
				}//end while
				//得到进入每一个BSCC的概率
				int equationNum = equationList.size();
				double [][] klm = new double[equationNum][variant.size()+1];
				//根据方程 得到行列式，在调用克拉默算法求解n元一次方程
				for(int e = 0; e < equationNum; e++){
					HashMap<Integer, Double> equation = equationList.get(e);
					for(int j = 0; j < variant.size(); j++){
						int v = variant.get(j);
						Double coefficient = equation.get(v);
						if(coefficient != null){
							klm[e][j] = coefficient;
						}
					}
					Double c = equation.get(num+1);
					if(c!=null){
						klm[e][variant.size()] = equation.get(num+1);
					}
					
				}
				
				GetMatrix gm = new GetMatrix(klm);
		        double[] uu = new double[variant.size()];// 返回结果集
		        uu = gm.getResult();
		        //得到出初始状态进入第i+1个BSCC的概率
		        enterProb[i] = uu[0];
		
			}
					
		}
	}
		
}

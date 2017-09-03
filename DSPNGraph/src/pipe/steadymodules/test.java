package pipe.steadymodules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double [][]a = new double[][]
		{
			{0, 0.5, 0, 0.5, 0, 0},
			{0.5,0,0.25,0,0.25,0},
			{0,0,0.5,0,0,0.5},
			{0,0,0,1,0,0},
			{0,0,0,0,0,0},
			{0,0,0.5,0,0,0.5}
		}; 
		
		FindBSCC f = new FindBSCC(a);
		HashSet<HashSet<Integer>> bscc = f.computeBSCC();
		List<HashSet<Integer>> bsccList = new LinkedList(bscc);
		EnterBSCCProb e = new EnterBSCCProb(a, bsccList);
		
		e.compute();
	}

}

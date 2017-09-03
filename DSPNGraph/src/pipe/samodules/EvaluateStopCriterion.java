package pipe.samodules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class EvaluateStopCriterion {

	public static boolean evaluateStopCriterion(ArrayList<Boolean> result_left,ArrayList<Boolean> result_right, ArrayList<Integer> pfrelation_left, ArrayList<Integer> pfrelation_right)
	{
		Queue<Integer> pfr_left = new LinkedList();
		Queue<Integer> pfr_right = new LinkedList();
		for(Integer pfrelation : pfrelation_left)
		{
			pfr_left.add(pfrelation);
		}
		for(Integer pfrelation : pfrelation_right)
		{
			pfr_right.add(pfrelation);
		}
		
		Boolean left = evaluateTrue(result_left, pfr_left);
		
		//left要取反
		if(left == false)
		{
			return true;
		}
		else
		{
			Boolean right = evaluateTrue(result_right, pfr_right);
			return right;
		}
	
	}
	
	public static boolean evaluateTrue(ArrayList<Boolean> result,Queue<Integer> pfrelation)
	{
		Stack<Boolean> s = new Stack();
		Stack<Integer> relation = new Stack();
		
		if(result.isEmpty())
			return true;
		//这里相当于把∧的优先级设置高于∨，只要有∧，就先做∧，有∨，放入栈中
		for(int i = 0; i < result.size() || s.size() >1; i++ )
		{
			if(s.isEmpty())
			{
				s.push(result.get(i));				
			}
			else if(i < result.size())
			{
				// "", "∧", "∨",
				int currelation = pfrelation.poll();
				if( currelation == 1)
				{
					Boolean a = s.pop();
					Boolean b = result.get(i);
					Boolean ab = a && b;
					s.push(ab);
				}
				else
				{
					s.push(result.get(i));
					relation.push(currelation);
				}
			}
			else
			{
				Boolean a = s.pop();
				Boolean b = s.pop();
				int currelation = relation.pop();
				if(currelation == 1)
				{
					s.push(a && b);
				}
				else
				{
					s.push(a || b);
				}
			}
		}
		Boolean final_result = s.pop();
		return final_result;
	}
	
	public static void main(String[] args) {
		ArrayList<Boolean> result = new ArrayList();
		Queue<Integer> pfrelation = new LinkedList();
		
		result.add(true);
		result.add(true);
		result.add(false);
		
		pfrelation.add(2);
		pfrelation.add(1);
		
		evaluateTrue(result,pfrelation);
	}
	
	
}

package pipe.samodules;

import java.util.ArrayList;

import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;

public class SafetyCondition extends MarkingCondition{

	private ArrayList<PlaceFormula> pf_left;
	private ArrayList<PlaceFormula> pf_right;
	private ArrayList<Integer> pfrelation_left;
	private ArrayList<Integer> pfrelation_right;
	
	public SafetyCondition() {
		// TODO Auto-generated constructor stub
	}
	
	public SafetyCondition(ArrayList<PlaceFormula> pf_left, ArrayList<PlaceFormula> pf_right, ArrayList<Integer> pfrelation_left, ArrayList<Integer> pfrelation_right) {
		this.pf_left = pf_left;
		this.pf_right = pf_right;
		this.pfrelation_left = pfrelation_left;
		this.pfrelation_right = pfrelation_right;
	}
	
	public ArrayList<Boolean> placeFormulaResult(ArrayList<PlaceFormula> pf, Marking curMarking)
	{
		ArrayList<Boolean> result = new ArrayList();
		for(PlaceFormula a : pf)
		{
			String placeId = a.getPlaceId();
			int relation = a.getRelation();
			int num = a.getNum();
			
			int curnum = curMarking.getTokens(placeId);
			switch (relation)
			{
				//1 < ; 2 >; 3 =; 4 !=; 5 <=;6 >=
				case 1:
					if(curnum < num)
					{
						result.add(true);
					}
					else
					{
						result.add(false);
					}
					break;
				case 2:
					if(curnum > num)
					{
						result.add(true);
					}
					else
					{
						result.add(false);
					}
					break;
				case 3:
					if(curnum == num)
					{
						result.add(true);
					}
					else
					{
						result.add(false);
					}
					break;
				case 4:
					if(curnum != num)
					{
						result.add(true);
					}
					else
					{
						result.add(false);
					}
					break;
				case 5:
					if(curnum <= num)
					{
						result.add(true);
					}
					else
					{
						result.add(false);
					}
					break;
				case 6:
					if(curnum >= num)
					{
						result.add(true);
					}
					else
					{
						result.add(false);
					}
					break;
				default:
					result.add(false);	
			}
			
		}
		return result;
	}
	public boolean evaluate(Marking paramMarking){
		
		//paramMarking表示当前状态
		ArrayList<Boolean> result_left = placeFormulaResult(pf_left, paramMarking);
		ArrayList<Boolean> result_right = placeFormulaResult(pf_right, paramMarking);
		
		boolean result = EvaluateStopCriterion.evaluateStopCriterion(result_left, result_right, pfrelation_left, pfrelation_right);
		
		//System.out.println("evaluate:"+paramMarking+" "+result);
		return result;
	}

}
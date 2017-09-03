package pipe.steadymodules;
import Jama.Matrix;


public class DTMCSteady {
	
	public static double[] compute(double[][] prob) {
		int n = prob.length;
		double[] steady = new double[n];
		double[][] middle = prob;
		for(int i = 0; i < prob.length; i++)
		{
			for(int j = 0; j< prob.length; j++)
			{
				if(i == j)
				{
					middle[i][j] = 1 - prob[i][j] + 1;
				}
				else
				{
					middle[i][j] = -prob[i][j] + 1;
				}
				//System.out.print(middle[i][j]+" ");
			}		
		}
		Matrix matrix = new Matrix(middle);
		Matrix m = matrix.inverse();
		double [][] inverse;
		inverse = m.getArray();
		for(int j = 0; j < prob.length; j++)
		{
			steady[j] = 0;
			for(int i = 0; i < prob.length; i++)
			{
				steady[j] += inverse[i][j]; 
			}
			System.out.println(steady[j]);
		}
		
		return steady;
			
	}
}

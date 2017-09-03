package pipe.steadymodules;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.analyzer.state.StateFeature;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.math.expression.Expolynomial;
import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.math.function.PartitionedGEN;
import it.unifi.oris.sirio.math.function.StateDensityFunction;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.math.function.GEN;
import it.unifi.oris.sirio.math.expression.Exmonomial;
import it.unifi.oris.sirio.math.expression.AtomicTerm;

public class StoState extends State{

	public StoState()
	{
		super();
		
	}
	
	public StoState(Collection<StateFeature> features)
	{
		super();
		for(StateFeature sf:features){
			this.addFeature(sf);
		}
	}
	
	public boolean equals(Object obj)
	{
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StoState)) {
			return false;
		}
		StoState o = (StoState)obj;
   
		//return this.features.equals(o.features);
		//PetriStateFeature是否相等
		PetriStateFeature psf = this.getFeature(PetriStateFeature.class);
		PetriStateFeature opsf = o.getFeature(PetriStateFeature.class);
		if(!psf.equals(opsf)){
			//System.out.println("psf not equal");
			return false;
		}
		
		//StochasticStateFeature是否相等
		StochasticStateFeature ssf = this.getFeature(StochasticStateFeature.class);
		StochasticStateFeature ossf = o.getFeature(StochasticStateFeature.class);
		
		if(!ssf.getEXPRates().equals(ossf.getEXPRates())){
			//System.out.println("EXPRates not equal");
			return false;
		}
			
		if(!ssf.getAgeVariables().equals(ossf.getAgeVariables())){
			//System.out.println("AgeVariables not equal");
			return false;
		}
			
		
		//判断StateDensityFunction是否相等
		StateDensityFunction sd = ssf.getStateDensity();
		StateDensityFunction osd = ossf.getStateDensity();
		
		if (!sd.getDeterministicValues().equals(osd.getDeterministicValues())) {
			//System.out.println("getDeterministicValues not equal");
			 return false;
		} 
		Set<Variable> sv = sd.getSynchronizedVariables();
		Set<Variable> osv = osd.getSynchronizedVariables();
		if (!sv.equals(osv)) {
			//System.out.println("getSynchronizedVariables not equal");
			 return false;
		}
		else{
			for(Variable v:sv){
				if(!sd.getSynchronization(v).equals(osd.getSynchronization(v))){
					//System.out.println("getSynchronization not equal");
					return false;
				}
			}
		}
		
		//判断PartitionedGEN是否相等
		PartitionedGEN pg = sd.getPartitionedGen();
		PartitionedGEN opg = osd.getPartitionedGen();
	
		List<GEN> pgfunctions = pg.getFunctions();
		List<GEN> opgfunctions = opg.getFunctions();
		
		if (pgfunctions.size() != opgfunctions.size()) {
			return false;
		}
		
		for (int i = 0; i < pgfunctions.size(); i++) {
			boolean found = false;
			for (int j = 0; (j < opgfunctions.size()) && (!found); j++) {
				if (((GEN)pgfunctions.get(i)).getDomain().equals(((GEN)opgfunctions.get(j)).getDomain())) {					
					Expolynomial e = new Expolynomial(((GEN)pgfunctions.get(i)).getDensity());
					Expolynomial eobj = new Expolynomial(((GEN)opgfunctions.get(j)).getDensity());
					if(isEqual(e,eobj)){
						found = true;
						break;
					}
				}
			}
			   
			if (!found) {
				
				try {
					FileWriter file1 = new FileWriter("D:\\log.txt",true);
					file1.write("this "+this.getFeature(PetriStateFeature.class).getMarking()+"\r\n");
					file1.write(this.getFeature(StochasticStateFeature.class)+"\r\n");
					
					file1.write("o "+o.getFeature(PetriStateFeature.class).getMarking()+"\r\n");
					file1.write(o.getFeature(StochasticStateFeature.class)+"\r\n");
					file1.flush();
					file1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    return false;
			}
		}   		
		return true;	
		
	}
	
	public static boolean isEqual(Expolynomial e,Expolynomial eobj){
		List<Exmonomial> exm = e.getExmonomials();
		List<Exmonomial> objexm = eobj.getExmonomials();
		
		if (exm.size() != objexm.size()) {
			return false;
		}
		
		for (int i = 0; i < exm.size(); i++) {
			boolean found = false;
			for (int j = 0; (j < objexm.size()) && (!found); j++) {
//				System.out.println(exm.get(i).getConstantTerm());
//				System.out.println(objexm.get(j).getConstantTerm());
//				System.out.println("exm "+exm.get(i).getConstantTerm().subtract(objexm.get(j).getConstantTerm()));
//				System.out.println((exm.get(i).getConstantTerm().subtract(objexm.get(j).getConstantTerm()).compareTo(new OmegaBigDecimal("0.0001"))==-1));
				if((exm.get(i).getConstantTerm().subtract(objexm.get(j).getConstantTerm()).compareTo(new OmegaBigDecimal("0.0001"))==-1)
						&&isEqual(exm.get(i).getAtomicTerms(),objexm.get(j).getAtomicTerms())){
					found = true;
					break;
				}
			}			
			   
			if (!found) {
				//System.out.println("Exmonomial not equal");
				
			    return false;
			    
			}
		}   		
		return true;
	}
	
	public static boolean isEqual(List<AtomicTerm> eAT,List<AtomicTerm> objAT){
		
		if (eAT.size() != objAT.size()) {
			return false;
		}
		
		for (int i = 0; i < eAT.size(); i++) {
			boolean found = false;
			
			AtomicTerm a = eAT.get(i);
			for (int j = 0; (j < objAT.size()) && (!found); j++) {
				AtomicTerm aobj = objAT.get(j);
				if(a.equals(aobj)){
					found = true;
					break;
				}		
			}			
			   
			if (!found) {
				//System.out.println("AtomicTerm not equal");
			    return false;
			}
		}   		
		return true;
	}
	
}


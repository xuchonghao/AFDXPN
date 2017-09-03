package pipe.steadymodules;


import it.unifi.oris.sirio.analyzer.SuccessionFeature;
import it.unifi.oris.sirio.math.OmegaBigDecimal;

import java.math.BigDecimal;

public class SuccessionSojournTimeFeature implements SuccessionFeature{

	 //发生这个变迁的平均时间
	private OmegaBigDecimal meanTime;


	public SuccessionSojournTimeFeature(OmegaBigDecimal meanTime) {
		super();
		this.meanTime = meanTime;
	}
	
	public OmegaBigDecimal getMeanTime() {
		return meanTime;
	}

	public void setMeanTime(OmegaBigDecimal meanTime) {
		this.meanTime = meanTime;
	}
	  
	@Override
	public String toString() {
		return "SuccessionSojournTimeFeature [meanTime=" + meanTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((meanTime == null) ? 0 : meanTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuccessionSojournTimeFeature other = (SuccessionSojournTimeFeature) obj;
		if (meanTime == null) {
			if (other.meanTime != null)
				return false;
		} else if (!meanTime.equals(other.meanTime))
			return false;
		return true;
	}


}

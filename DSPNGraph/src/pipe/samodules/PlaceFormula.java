package pipe.samodules;

public class PlaceFormula {

	@Override
	public String toString() {
		return "PlaceFormula [placeId=" + placeId + ", relation=" + relation
				+ ", num=" + num + "]";
	}

	private String placeId;
	//1 < ; 2 >; 3 =; 4 !=; 5 <=;6 >=
	private int relation;
	private int num;
	
	PlaceFormula(String p, int r, int num)
	{
		this.placeId = p;
		this.relation = r;
		this.num = num;
	}

	public PlaceFormula(String p, int r, String num)
	{
		this.placeId = p;
		this.relation = r;
		this.num = Integer.parseInt(num);
	}
	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
}

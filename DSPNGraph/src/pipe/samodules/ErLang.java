package pipe.samodules;


/*   3:    */import it.unifi.oris.sirio.math.OmegaBigDecimal;
/*   4:    */import it.unifi.oris.sirio.math.domain.DBMZone;
/*   5:    */import it.unifi.oris.sirio.math.expression.Exmonomial;
/*   6:    */import it.unifi.oris.sirio.math.expression.Expolynomial;
/*   7:    */import it.unifi.oris.sirio.math.expression.ExponentialTerm;
/*   8:    */import it.unifi.oris.sirio.math.expression.MonomialTerm;
/*   9:    */import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.math.function.Function;

/*  10:    */import java.math.BigDecimal;
/*  11:    */import java.math.BigInteger;
/*  12:    */import java.math.MathContext;
/*  13:    */import java.util.Collection;
/*  14:    */
/*  19:    */public class ErLang
/*  20:    */  implements Function
/*  21:    */{
/*  22:    */  private DBMZone domain;
/*  23:    */  private Expolynomial density;
/*  24:    */  private Variable x;
/*  25:    */  private BigDecimal lambda;
/*  26:    */  private int k;
/*  27:    */  
/*  28:    */  public ErLang(Variable x, int k, BigDecimal lambda)
/*  29:    */  {
/*  30: 30 */    this.x = x;
/*  31: 31 */    this.lambda = lambda;
/*  32: 32 */    this.k = k;
/*  33:    */    
/*  34:    */    OmegaBigDecimal lft;
/*  35:    */    OmegaBigDecimal eft;

/*  36: 36 */    if (lambda.compareTo(BigDecimal.ZERO) > 0) {
/*  37: 37 */      eft = OmegaBigDecimal.ZERO;
/*  38: 38 */      lft = OmegaBigDecimal.POSITIVE_INFINITY; } else { 
/*  39: 39 */      if (lambda.compareTo(BigDecimal.ZERO) < 0) {
/*  40: 40 */        eft = OmegaBigDecimal.NEGATIVE_INFINITY;
/*  41: 41 */        lft = OmegaBigDecimal.ZERO;
/*  42:    */      } else {
/*  43: 43 */        throw new IllegalArgumentException("The lambda rate must different than zero"); } }
/*  44:    */    
/*  45: 45 */     this.domain = new DBMZone(new Variable[] { x });
/*  46: 46 */    this.domain.setCoefficient(x, Variable.TSTAR, lft);
/*  47: 47 */    this.domain.setCoefficient(Variable.TSTAR, x, eft.negate());
/*  48:    */    
/*  50: 50 */    this.density = new Expolynomial();
				//已经修正了问题了
/*  51: 51 */    Exmonomial exmon = new Exmonomial(
/*  53: 53 */      new OmegaBigDecimal(BigDecimal.valueOf(Math.pow(lambda.doubleValue(), k)))
/*  54: 54 */      .divide(new BigDecimal(factorial(k - 1)), MathContext.DECIMAL128));
/*  55:    */    
/*  56: 56 */    exmon.addAtomicTerm(new MonomialTerm(x, k - 1));
/*  57: 57 */    exmon.addAtomicTerm(new ExponentialTerm(x, lambda));
/*  58: 58 */    this.density.addExmonomial(exmon);
/*  59:    */  }
/*  60:    */  
/*  61:    */  private static BigInteger factorial(int n) {
/*  62: 62 */    BigInteger r = BigInteger.ONE;
/*  63: 63 */    for (int i = 1; i <= n; i++)
/*  64: 64 */      r = r.multiply(BigInteger.valueOf(i));
/*  65: 65 */    return r;
/*  66:    */  }
/*  67:    */  
/*  68:    */  public ErLang(ErLang e) {
/*  69: 69 */    this(e.getVariable(), e.getShape(), e.getLambda());
/*  70:    */  }
/*  71:    */  
/*  72:    */  public int getShape() {
/*  73: 73 */    return this.k;
/*  74:    */  }
/*  75:    */  
/*  76:    */  public BigDecimal getLambda() {
/*  77: 77 */    return this.lambda;
/*  78:    */  }
/*  79:    */  
/*  80:    */  public Variable getVariable()
/*  81:    */  {
/*  82: 82 */    return this.x;
/*  83:    */  }
/*  84:    */  
/*  86:    */  public boolean equals(Object obj)
/*  87:    */  {
/*  88: 88 */    if (this == obj) {
/*  89: 89 */      return true;
/*  90:    */    }
/*  91: 91 */    if (!(obj instanceof ErLang)) {
/*  92: 92 */      return false;
/*  93:    */    }
/*  94: 94 */    ErLang other = (ErLang)obj;
/*  95:    */    
/*  96: 96 */    return (getVariable().equals(other.getVariable())) && 
/*  97: 97 */      (getLambda().compareTo(other.getLambda()) == 0);
/*  98:    */  }
/*  99:    */  
/* 101:    */  public int hashCode()
/* 102:    */  {
/* 103:103 */    int result = 17;
/* 104:    */    
/* 105:105 */    result = 31 * result + getLambda().hashCode();
/* 106:106 */    result = 31 * result + getVariable().hashCode();
/* 107:    */    
/* 108:108 */    return result;
/* 109:    */  }
/* 110:    */  
/* 115:    */  public String toString()
/* 116:    */  {
/* 117:117 */    String result = "Domain\n";
/* 118:118 */    result = result + this.domain.toString();
/* 119:119 */    result = result + "Density\n";
/* 120:120 */    result = result + this.density.toString() + "\n";
/* 121:121 */    return result;
/* 122:    */  }
/* 123:    */  
/* 125:    */  public String toMathematicaString()
/* 126:    */  {
/* 127:127 */    Collection<Variable> variables = this.domain.getVariables();
/* 128:128 */    variables.remove(Variable.TSTAR);
/* 129:    */    
/* 130:130 */    String result = "f[";
/* 131:131 */    String prefix = "";
/* 132:132 */    for (Variable v : variables) {
/* 133:133 */      result = result + prefix + v;
/* 134:134 */      prefix = "_,";
/* 135:    */    }
/* 136:136 */    result = result + "_";
/* 137:137 */    result = result + "] := ( " + this.density.toString() + " ) * " + this.domain.toUnitStepsString();
/* 138:    */    
/* 139:139 */    return result;
/* 140:    */  }
/* 141:    */  
/* 142:    */  public Expolynomial getDensity()
/* 143:    */  {
/* 144:144 */    return this.density;
/* 145:    */  }
/* 146:    */  
/* 147:    */  public DBMZone getDomain()
/* 148:    */  {
/* 149:149 */    return this.domain;
/* 150:    */  }
/* 151:    */}


/* Location:           D:\SirioExample\SirioExample\lib\Sirio-1.0.0.jar
 * Qualified Name:     it.unifi.oris.sirio.math.function.Erlang
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */
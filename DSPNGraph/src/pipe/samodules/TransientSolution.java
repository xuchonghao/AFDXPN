package pipe.samodules;



import it.unifi.oris.sirio.models.stpn.RewardRate;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TransientSolution<R, S>
{
  private BigDecimal timeLimit;
  private BigDecimal step;
  int samplesNumber;
  List<R> regenerations;
  List<S> columnStates;
  double[][][] solution;
  
  TransientSolution(BigDecimal timeLimit, BigDecimal step, List<R> regenerations, List<S> columnStates)
  {
    this.timeLimit = timeLimit;
    this.step = step;
    this.samplesNumber = (timeLimit.divide(step).intValue() + 1);
    
    this.regenerations = new ArrayList(regenerations);
    this.columnStates = new ArrayList(columnStates);
    
    this.solution = new double[this.samplesNumber][regenerations.size()][columnStates.size()];
  }
  
  public String toString()
  {
    StringBuilder b = new StringBuilder();
    
    for (int i = 0; i < this.regenerations.size(); i++) {
      b.append(">> Initial regeneration: ");
      b.append(this.regenerations.get(i));
      b.append("\n");
      
      b.append(">> Column states: ");
      b.append(this.columnStates);
      b.append("\n");
      
      for (int t = 0; t < this.samplesNumber; t++) {
        b.append(this.step.multiply(new BigDecimal(t)));
        for (int j = 0; j < this.columnStates.size(); j++) {
          b.append(" ");
          b.append(this.solution[t][i][j]);
        }
        b.append("\n");
      }
    }
    
    return b.toString();
  }
  
  public BigDecimal getTimeLimit() {
    return this.timeLimit;
  }
  
  public BigDecimal getStep() {
    return this.step;
  }
  
  public int getSamplesNumber() {
    return this.samplesNumber;
  }
  
  public List<R> getRegenerations() {
    return Collections.unmodifiableList(this.regenerations);
  }
  
  public List<S> getColumnStates() {
    return Collections.unmodifiableList(this.columnStates);
  }
  
  public double[][][] getSolution() {
    return this.solution;
  }
  
  public TransientSolution<R, S> computeIntegralSolution() {
    TransientSolution<R, S> integralSolution = new TransientSolution(
      this.timeLimit, this.step, this.regenerations, this.columnStates);
    
    for (int t = 1; t < integralSolution.solution.length; t++) {
      for (int i = 0; i < integralSolution.solution[t].length; i++)
        for (int j = 0; j < integralSolution.solution[t][i].length; j++)
          integralSolution.solution[t][i][j] = 
            (integralSolution.solution[(t - 1)][i][j] + 0.5D * this.step.doubleValue() * (this.solution[t][i][j] + this.solution[(t - 1)][i][j]));
    }
    return integralSolution;
  }
  
  public static <R> TransientSolution<R, MarkingCondition> computeAggregateSolution(TransientSolution<R, Marking> solution, String markingConditions)
  {
    String[] c = markingConditions.split(",");
    MarkingCondition[] mc = new MarkingCondition[c.length];
    for (int i = 0; i < c.length; i++) {
      mc[i] = MarkingCondition.fromString(c[i]);
    }
    return computeAggregateSolution(solution, mc);
  }
  
  public static <R> TransientSolution<R, MarkingCondition> computeAggregateSolution(TransientSolution<R, Marking> solution, MarkingCondition... markingConditions) {
    return computeAggregateSolution(solution, solution.regenerations.get(0), markingConditions);
  }
  
  public static <R> TransientSolution<R, MarkingCondition> computeAggregateSolution(TransientSolution<R, Marking> solution, R initialRegeneration, MarkingCondition... markingConditions)
  {
    TransientSolution<R, MarkingCondition> aggregateSolution = new TransientSolution(
      solution.timeLimit, solution.step, Collections.singletonList(initialRegeneration), Arrays.asList(markingConditions));
    
    int initialRegenerationIndex = solution.regenerations.indexOf(initialRegeneration);
    
    for (int j = 0; j < markingConditions.length; j++) {
      for (int m = 0; m < solution.columnStates.size(); m++)
        if (markingConditions[j].evaluate((Marking)solution.columnStates.get(m)))
          for (int t = 0; t < aggregateSolution.solution.length; t++)
            aggregateSolution.solution[t][0][j] += solution.solution[t][initialRegenerationIndex][m];
    }
    return aggregateSolution;
  }
  
  public static <R> TransientSolution<R, RewardRate> computeRewards(boolean cumulative, TransientSolution<R, Marking> solution, String rewardRates)
  {
    String[] c = rewardRates.split(";");
    RewardRate[] rs = new RewardRate[c.length];
    for (int i = 0; i < c.length; i++) {
      rs[i] = RewardRate.fromString(c[i]);
    }
    return computeRewards(cumulative, solution, rs);
  }
  
  public static <R> TransientSolution<R, RewardRate> computeRewards(boolean cumulative, TransientSolution<R, Marking> solution, RewardRate... rewardRates) {
    return computeRewards(cumulative, solution, solution.regenerations.get(0), rewardRates);
  }
  
  public static <R> TransientSolution<R, RewardRate> computeRewards(boolean cumulative, TransientSolution<R, Marking> solution, R initialRegeneration, RewardRate... rewardRates)
  {
    TransientSolution<R, RewardRate> rewards = new TransientSolution(
      solution.timeLimit, solution.step, Collections.singletonList(initialRegeneration), Arrays.asList(rewardRates));
    
    int initialRegenerationIndex = solution.regenerations.indexOf(initialRegeneration);
    
    double step = solution.step.doubleValue();
    double time = 0.0D;
    
    if (!cumulative) {
      for (int t = 0; t < rewards.solution.length; t++) {
        for (int j = 0; j < rewardRates.length; j++)
        {
          for (int m = 0; m < solution.columnStates.size(); m++)
            rewards.solution[t][0][j] += 
              rewardRates[j].evaluate(time, (Marking)solution.columnStates.get(m)) * 
              solution.solution[t][initialRegenerationIndex][m]; }
        time += step;
      }
    }
    else {
      double[] prevExpectedRewardRates = null;
      for (int t = 0; t < rewards.solution.length; t++)
      {
        double[] expectedRewardRates = new double[rewardRates.length];
        for (int j = 0; j < rewardRates.length; j++)
        {
          for (int m = 0; m < solution.columnStates.size(); m++)
            expectedRewardRates[j] += 
              rewardRates[j].evaluate(time, (Marking)solution.columnStates.get(m)) * 
              solution.solution[t][initialRegenerationIndex][m];
        }
        if (prevExpectedRewardRates != null) {
          for (int j = 0; j < rewardRates.length; j++) {
            rewards.solution[t][0][j] = 
              (rewards.solution[(t - 1)][0][j] + 0.5D * step * (expectedRewardRates[j] + prevExpectedRewardRates[j]));
          }
        }
        prevExpectedRewardRates = expectedRewardRates;
        time += step;
      }
    }
    
    return rewards;
  }
}


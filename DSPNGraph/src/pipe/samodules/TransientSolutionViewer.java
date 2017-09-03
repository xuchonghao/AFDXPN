package pipe.samodules;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
			import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class TransientSolutionViewer extends JFrame
{
  private static <R, S> ChartPanel solutionChart(TransientSolution<R, S> s)
  {
    XYSeriesCollection dataset = new XYSeriesCollection();
    
    for (int m = 0; m < s.getColumnStates().size(); m++)
    {
      XYSeries series = new XYSeries(s.getColumnStates().get(m).toString());
      
      double step = s.getStep().doubleValue();
      int i = 0; for (int size = s.getSamplesNumber(); i < size; i++) {
        series.add(i * step, s.getSolution()[i][0][m]);
      }
      dataset.addSeries(series);
    }
    
    JFreeChart chart = ChartFactory.createXYLineChart("基于路径的安全性指标", 
      "Time", "Probability", dataset, PlotOrientation.VERTICAL, true, true, false);
    
    XYPlot plot = (XYPlot)chart.getPlot();
    plot.setBackgroundPaint(Color.WHITE);
    
    plot.setDomainGridlinesVisible(true);
    plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
    
    plot.setRangeGridlinesVisible(true);
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

				//显示数据点
				XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
				xylineandshaperenderer.setBaseShapesVisible(true);
    
    NumberAxis domain = (NumberAxis)plot.getDomainAxis();
    domain.setRange(0.0D, s.getStep().doubleValue() * s.getSamplesNumber());
    
    domain.setVerticalTickLabels(true);
    
    NumberAxis range = (NumberAxis)plot.getRangeAxis();
    range.setAutoRangeMinimumSize(1.01D);
    
    ChartPanel chartPanel = new ChartPanel(chart);
    
    return chartPanel;
  }
  
  public <R, S> TransientSolutionViewer(TransientSolution<R, S> transientSolution)
  {
    ChartPanel chartPanel = solutionChart(transientSolution);
    
    
    setDefaultCloseOperation(2);
    add(chartPanel);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }
}


/* Location:           D:\SirioExample\SirioExample\lib\Sirio-1.0.0.jar
 * Qualified Name:     it.unifi.oris.sirio.models.stpn.TransientSolutionViewer
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */
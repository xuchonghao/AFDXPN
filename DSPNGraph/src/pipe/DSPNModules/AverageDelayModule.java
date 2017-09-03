/**
 * Simulation IModule
 * @author James D Bloom (UI)
 * @author Clare Clark (Maths)
 * @author Maxim (replacement UI and cleanup)
 *
 * @author Davd Patterson (handle null return from fireRandomTransition)
 *
 */
package pipe.DSPNModules;

import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.gui.widgets.newwidges.GuideModel;
import pipe.modules.interfaces.IModule;
import pipe.utilities.transformers.TNTransformer;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;


public class AverageDelayModule extends SwingWorker implements IModule
{

    private static final String MODULE_NAME = "AverageDelayModule";

    private PetriNetChooserPanel sourceFilePanel;
    private ResultsHTMLPane results;

    private JTextField jtfFirings, jtfCycles;
    private JTextField source, target;

    private static GuideModel guideModel;
    public static GuideModel getGuideModel() {
        return guideModel;
    }

    public static void setGuideModel(GuideModel guideModel) {
        AverageDelayModule.guideModel = guideModel;
    }
    public void start()
    {
        /**TODO current net*/
        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

        // 1 Set layout
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        JPanel settings3 = new JPanel();
        settings3.setLayout(new BoxLayout(settings3, BoxLayout.LINE_AXIS));
        settings3.add(new JLabel("Please save the Petri Net to a file firstly!!!"));
        settings3.add(Box.createHorizontalStrut(5));
        settings3.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                settings3.getPreferredSize().height));
        contentPane.add(settings3);

        JPanel settings4 = new JPanel();
        settings4.setLayout(new BoxLayout(settings4, BoxLayout.LINE_AXIS));
        settings4.add(new JLabel("Then open the file with the Ctrl+o!!!"));
        settings4.add(Box.createHorizontalStrut(5));
        settings4.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                settings4.getPreferredSize().height));
        contentPane.add(settings4);

        // 2 Add file browser
        sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
        contentPane.add(sourceFilePanel);

        // 2.5 Add edit boxes
        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.LINE_AXIS));
        settings.add(new JLabel("Firings:"));
        settings.add(Box.createHorizontalStrut(5));
        settings.add(jtfFirings = new JTextField("100", 5));
        settings.add(Box.createHorizontalStrut(10));
        settings.add(new JLabel("Replications:"));
        settings.add(Box.createHorizontalStrut(5));
        settings.add(jtfCycles = new JTextField("5", 5));
        settings.setBorder(new TitledBorder(new EtchedBorder(),
                "Simulation parameters"));
        settings.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                settings.getPreferredSize().height));
        contentPane.add(settings);

        // 2.8 Add edit boxes
        JPanel settings2 = new JPanel();
        settings2.setLayout(new BoxLayout(settings2, BoxLayout.LINE_AXIS));

        settings2.add(new JLabel("Source:"));
        settings2.add(Box.createHorizontalStrut(5));
        settings2.add(source = new JTextField("VL2-Wait1",5));
        settings2.add(Box.createHorizontalStrut(10));
        settings2.add(new JLabel("Target:"));
        settings2.add(Box.createHorizontalStrut(5));
        settings2.add(target = new JTextField("VL3-Receiption-Port",5));


        settings2.setBorder(new TitledBorder(new EtchedBorder(),
                "Source/Target parameters"));
        settings2.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                settings2.getPreferredSize().height));
        contentPane.add(settings2);

        // 3 Add results pane
        results = new ResultsHTMLPane(pnmlData.getPNMLName());
        contentPane.add(results);

        // 4 Add button
        contentPane.add(new ButtonBar("SimulateCompute", simulateButtonClick,
                guiDialog.getRootPane()));

        // 5 Make window fit contents' preferred size
        guiDialog.pack();

        // 6 Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setVisible(true);
    }


    public String getName()
    {
        return MODULE_NAME;
    }
    //if (!sourceDataLayer.getPetriNetObjects().hasNext()) {

    /**
     * Simulate button click handler
     */
    private final ActionListener simulateButtonClick = new ActionListener()
    {

        public void actionPerformed(ActionEvent arg0)
        {
            PetriNetView sourceDataLayer = sourceFilePanel.getDataLayer();
            String s = "<h2>Petri net simulation results</h2>";
            if(sourceDataLayer == null)
            {
                JOptionPane.showMessageDialog(null, "Please, choose a source net",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(!sourceDataLayer.hasPlaceTransitionObjects())
            {
                s += "No Petri net objects defined!";
            }
            else
            {
                try
                {
                    File file = PNMLWriter.saveTemporaryFile2(sourceDataLayer, this.getClass().getName());
                    /**可以从这里在重新读入*/
                    //PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
                    //pipeApplicationView.createNewTab2(file, true);

                    int firings = Integer.parseInt(jtfFirings.getText());
                    int cycles = Integer.parseInt(jtfCycles.getText());

                    String start = source.getText().trim();
                    String end = target.getText().trim();

                    s += simulate(sourceDataLayer, cycles, firings,start,end);
                    results.setEnabled(true);
                }
                catch(NumberFormatException e)
                {
                    s += "Invalid parameter!";
                }
                catch(OutOfMemoryError oome)
                {
                    System.gc();
                    results.setText("");
                    s = "Memory error: " + oome.getMessage();

                    s += "<br>Not enough memory. Please use a larger heap size."
                            + "<br>"
                            + "<br>Note:"
                            + "<br>The Java heap size can be specified with the -Xmx option."
                            + "<br>E.g., to use 512MB as heap size, the command line looks like this:"
                            + "<br>java -Xmx512m -classpath ...\n";
                    results.setText(s);
                    return;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    s = "<br>Error" + e.getMessage();
                    results.setText(s);
                    return;
                }
            }
            results.setText(s);
        }
    };


    public String simulate(PetriNetView data, int cycles, int firings,String start,String end)
    {
        data.storeCurrentMarking();
        LinkedList<MarkingView>[] markings = data.getInitialMarkingVector();
        double[] delay = data.get_initialDelayMatrix();
        if(markings == null)
            return "No markings present. Try to add coloured tokens.";
        int length = markings.length;
        int[] marking = new int[length];//每个Token的id
        //int[] mCount = new int[length];//统计每种状态出现的次数
        for(int i = 0; i < length; i++)
        {
            if(markings[i]!= null && markings[i].size() > 0)
            {
                MarkingView first = markings[i].getFirst();//这里取出来之后会
                if(first != null)
                    marking[i] = first.getCurrentMarking();
            }
        }
        double averageTokens[] = new double[length];
        int totalTokens[] = new int[length];
        double avgResult[] = new double[length];
        double errorResult[] = new double[length];

        double overallAverages[][] = new double[cycles][length];

        int i, j;

        // Initialise arrays
        for(i = 0; i < length; i++)
        {
            averageTokens[i] = 0;
            totalTokens[i] = 0;
            avgResult[i] = 0;
            errorResult[i] = 0;
        }

        //Initialise matrices
        for(i = 0; i < cycles; i++)
        {
            for(j = 0; j < length; j++)
            {
                overallAverages[i][j] = 0;
            }
        }

        for(i = 0; i < cycles; i++)
        {
            //Need to initialise the transition count again
            int transCount = 0;

            delay = data.get_initialDelayMatrix();
            //Get initial marking
            markings = data.getInitialMarkingVector();
            marking = new int[length];
            for(int k = 0; k < length; k++)
            {
                if(markings[k]!= null && markings[k].size() > 0)
                {
                    MarkingView first = markings[k].getFirst();
                    if(first!=null)
                        marking[k] = first.getCurrentMarking();
                }
            }
            if(ApplicationSettings.getApplicationView() != null) data.restorePreviousMarking();

            //Initialise matrices for each new cycle
            for(j = 0; j < length; j++)
            {
                averageTokens[j] = 0;
                totalTokens[j] = 0;
                avgResult[j] = 0;
            }

            //Add initial marking to the total
            addTotal(marking, totalTokens);

            // Fire as many transitions as required and evaluate averages
            // Changed by Davd Patterson April 24, 2007
            // Handle a null return from fireRandomTransition if no transition
            // can be found.
            for(j = 0; j < firings; j++)
            {
                System.out.println("Firing " + j + " now");
                //Fire a random transition 选择一个合适的变迁,里面有变迁发生规则
                TransitionView fired = data.getRandomTransition();
                if(fired == null)
                {
                    ApplicationSettings.getApplicationView().getStatusBar().changeText(
                            "ERROR: No transitions to fire after " + j + " firings");
                    break;        // no point to keep trying to find a transition
                }
                else
                {
                    //data.createCurrentMarkingVector();
                    //在这里修改时延
                    data.fireTransition(fired); //NOU-PERE
                    //Get the new marking from the _dataLayer object
                    markings = data.getCurrentMarkingVector();
                    marking = new int[length];
                    for(int k = 0; k < length; k++)
                    {
                        if(markings[k]!= null && markings[k].size() > 0)
                        {
                            MarkingView first = markings[k].getFirst();
                            if(first != null)
                                marking[k] = first.getCurrentMarking();
                        }
                    }
                   /* for (int k=0; k<marking.length; k++)
                        System.out.print("" + marking[k] + ",");
                    System.out.println("");*/


                    //Add to the totalTokens array
                    addTotal(marking, totalTokens);
                    //Increment the transition count
                    transCount++;
                }
            }

            //Evaluate averages
            for(j = 0; j < length; j++)
            {
                //Divide by transCount + 1 as total number of markings
                //considered includes the original marking which is outside
                //the loop which counts the number of randomly fired transitions.
                averageTokens[j] = (totalTokens[j] / (transCount + 1.0));

                //add appropriate to appropriate row of overall averages for each cycle
                overallAverages[i][j] = averageTokens[j];
            }
        }
        /**将平均队列长度保存到相应的库所中*/
        ArrayList<PlaceView> placeViews = data.getPlacesArrayList();
        for(int t = 0; t < averageTokens.length; t++)
        {
            //System.out.println(averageTokens.length==length);
            placeViews.get(t).setForAverageDelay(averageTokens[t]);
        }
        //Add up averages for each cycle and divide by number of cycles
        //Perform evaluation on the overallAverages matrix.
        //for each column
        for(i = 0; i < length; i++)
        {
            //for each row
            for(j = 0; j < cycles; j++)
            {
                avgResult[i] = avgResult[i] + overallAverages[j][i];
            }
            avgResult[i] = (avgResult[i] / cycles);
        }

        //Generate the 95% confidence interval for the table of results

        //Find standard deviation and mulitply by 1.95996 assuming approx
        //to gaussian distribution

        //For each column in result array
        for(i = 0; i < length; i++)
        {
            //Find variance
            for(j = 0; j < cycles; j++)
            {
                //Sum of squares
                errorResult[i] = errorResult[i] +
                        ((overallAverages[j][i] - avgResult[i]) *
                                (overallAverages[j][i] - avgResult[i]));
            }

            //Divide by number of cycles
            //Find standard deviation by taking square root
            //Multiply by 1.95996 to give 95% confidence interval
            errorResult[i] = 1.95996 * Math.sqrt(errorResult[i] / cycles);
        }


        /**计算图中的的路线的算法*/
        ArrayOfGraph arrayOfGraph = data.getArrayOfGraph();
        Graph graph = new Graph();
        graph.buildGraph(arrayOfGraph.getVexsMap(),arrayOfGraph.getEdges());
        graph.printGraph();

        FindAllPath findAllPath = new FindAllPath();
        //得到了n条路径，可以计算相应的平均时间了
        ArrayList<LinkedList> list = findAllPath.visit(graph,start,end);
        int size = list.size();
        double[] averageDelay = new double[size];
        for(i=0;i<size;i++){//在我们的项目中一般只有一条路径
            LinkedList<Graph.VexNode> linkedList = list.get(i);
            //System.out.println(linkedList.size());
            //开始对链表进行遍历
            Graph.VexNode preNode = linkedList.getFirst();
            Graph.VexNode currentNode = linkedList.getFirst();
            for(Graph.VexNode vex : linkedList){
                currentNode = vex;
                //当前变迁为库所或者是瞬时变迁的时候，直接break
                if(currentNode.object instanceof PlaceView ||
                        (currentNode.object instanceof TransitionView && ((TransitionView) currentNode.object).getType()==0)){
                    continue;
                }else if(currentNode.object instanceof TransitionView && ((TransitionView) currentNode.object).getType()!=0 && preNode.object instanceof PlaceView){
                    if(((TransitionView)currentNode.object).getType() == 1){
                        double time = 1 / ((TransitionView)currentNode.object).getRate();
                        //System.out.println("time" + time);
                        averageDelay[i] += ((PlaceView) preNode.object).getForAverageDelay() * time;
                    }else if(((TransitionView)currentNode.object).getType() == 2){
                        double time = ((TransitionView)currentNode.object).getRate();
                        //System.out.println("time" + time);
                        averageDelay[i] += ((PlaceView) preNode.object).getForAverageDelay() * time;
                    }
                    //System.out.println("averageDelay:" + averageDelay[i]);
                }
                preNode = currentNode;
            }
        }

        ArrayList results = new ArrayList();
        DecimalFormat f = new DecimalFormat();
        f.setMaximumFractionDigits(5);

        if(averageTokens != null && errorResult != null && averageTokens.length > 0 && errorResult.length > 0)
        {
            // Write table of results
            results.add("Path");
            results.add("AverageDelay");
            results.add(" ");
            for(i = 0; i < size; i++)
            {
                    results.add(printPath(list.get(i)));
                    results.add(f.format(averageDelay[i]));
                    results.add(" ");

            }
        }
        if(ApplicationSettings.getApplicationView() != null) data.restorePreviousMarking();
        return ResultsHTMLPane.makeTable(results.toArray(), 3, false, true, true, true);
    }
    public StringBuilder printPath(LinkedList<Graph.VexNode> linkedList){
        StringBuilder sb = new StringBuilder();
        for(Graph.VexNode i : linkedList){
            sb.append(i.data + "->");
        }
        sb.delete(sb.length()-2,sb.length());
     //   System.out.println(sb.toString());
        return sb;
    }

    public int getIndexOfAverageTokens(PetriNetView data,PlaceView placeView){
        ArrayList<PlaceView> placeViews = data.getPlacesArrayList();
        int len = placeViews.size();
        for(int i=0;i<len;i++){
            if(placeView.getId().equals(placeViews.get(i).getId()))
                return i;
        }
        return -1;
    }
    public double computeThroughout(PetriNetView data,TransitionView transitionView,double[] averageTokens ){

        double throughout = 0;
        ArrayList<ArcView> _arcViews = data.getArcsArrayList();
        ArrayList<Integer> indexArray = new ArrayList<Integer>();
        for(ArcView arcView : _arcViews){
            ConnectableView source = arcView.getSource();
            ConnectableView target = arcView.getTarget();
            if(target instanceof TransitionView && target.getId().equals(transitionView.getId())){
                if(source instanceof PlaceView){//获得averageTokens的下标索引
                    int index = getIndexOfAverageTokens(data,(PlaceView) source);
                    indexArray.add(index);
                }
            }
        }
        //transitionView的类型在这个方法之前再作判断，凡是传进来的参数都不是瞬时变迁的！
        int len = indexArray.size();
        double allTokens = 0;
        for(int j=0;j<len;j++){
          //  System.out.println(indexArray.get(j) + "," + averageTokens[indexArray.get(j)]);
            allTokens += averageTokens[indexArray.get(j)];
        }
        if(transitionView.getType() == 1){
            throughout = allTokens / (1 / transitionView.getRate());
        }else if(transitionView.getType() == 2){
            throughout = allTokens / transitionView.getRate();
        }
        return throughout;
    }
    private void addTotal(int array[], int dest[])
    {
        if(array.length == dest.length)
        {
            for(int i = 0; i < dest.length; i++)
            {
                dest[i] += array[i];
            }
        }
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

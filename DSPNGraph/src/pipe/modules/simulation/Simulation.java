/**
 * Simulation IModule
 * @author James D Bloom (UI)
 * @author Clare Clark (Maths)
 * @author Maxim (replacement UI and cleanup)
 *
 * @author Davd Patterson (handle null return from fireRandomTransition)
 *
 */
package pipe.modules.simulation;

import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.models.Marking;
import pipe.modules.interfaces.IModule;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;


public class Simulation extends SwingWorker
        implements IModule
{

    private static final String MODULE_NAME = "Simulation";

    private PetriNetChooserPanel sourceFilePanel;
    private ResultsHTMLPane results;

    private JTextField jtfFirings, jtfCycles;

    public void start()
    {
        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

        // 1 Set layout
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

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

        // 3 Add results pane
        results = new ResultsHTMLPane(pnmlData.getPNMLName());
        contentPane.add(results);

        // 4 Add button
        contentPane.add(new ButtonBar("Simulate", simulateButtonClick,
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
        {   //返回要分析的petri网得view
            PetriNetView sourceDataLayer = sourceFilePanel.getDataLayer();
            String s = "<h2>Petri net simulation results</h2>";
            if(sourceDataLayer == null)
            {//petri为空，则报错
                JOptionPane.showMessageDialog(null, "Please, choose a source net",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(!sourceDataLayer.hasPlaceTransitionObjects())
            {//没有库所和变迁，报错
                s += "No Petri net objects defined!";
            }
            else//正常情况
            {
                try
                {
                    //首先保存临时文件到bin的路径
                    PNMLWriter.saveTemporaryFile(sourceDataLayer,
                            this.getClass().getName());
                    //获得两个参数：Firings和Replication应该是发射的次数和副本数
                    int firings = Integer.parseInt(jtfFirings.getText());
                    int cycles = Integer.parseInt(jtfCycles.getText());
                    s += simulate(sourceDataLayer, cycles, firings);
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


    public String simulate(PetriNetView data, int cycles, int firings)
    {
        data.storeCurrentMarking();//这里存储的应该是当前所有库所中的标识数
        //markings指的是所有place的情况----LinkedList<MarkingView>[]
        LinkedList<MarkingView>[] markings = data.getInitialMarkingVector();
        //TODO 这里的markingS是什么，输出一下
        /*** for( LinkedList<MarkingView> m : markings){//markings指的是所有place的情况
         int s = m.size();//m指的是LinkedList<MarkingView>每个库所中的标识整体，可能包含各种类型id的
         int i = 0;
         while(i < s){//getFirst应该就是默认的id，应该没有第二个了，经验证size==1
         System.out.println(m.getFirst().getCurrentMarking());
         i++;
         }

         }*/
        if(markings == null)
            return "No markings present. Try to add coloured tokens.";
        int length = markings.length;//place的数量
        int[] marking = new int[length];

        for(int i = 0; i < length; i++)
        {
            if(markings[i]!= null && markings[i].size() > 0)
            {   //markings是LinkedList<MarkingView>[]类型；markings[i]是LinkedList<MarkingView>类型，代表一个place的情况
                MarkingView first = markings[i].getFirst();//所以first是MarkingView类型；应该只有一个元素，这里代表某一个palce的情况
                if(first != null)//TODO 输出一下这里的marking【i】，这里应该成为了LinkedList<MarkingView>类型，但其实只有一个元素，因为我们没有用到颜色petri网
                    marking[i] = first.getCurrentMarking();//marking={2,0}
                //System.out.println(marking[i]);
            }
        }
        double averageTokens[] = new double[length];
        int totalTokens[] = new int[length];
        double avgResult[] = new double[length];
        double errorResult[] = new double[length];
        /**存储的是每一轮循环中每一个库所得平均值*/
        double overallAverages[][] = new double[cycles][length];

        int i, j;

        // Initialise arrays
        for(i = 0; i < length; i++)//对palce的个数进行遍历初始化
        {
            averageTokens[i] = 0;
            totalTokens[i] = 0;
            avgResult[i] = 0;
            errorResult[i] = 0;
        }

        //Initialise matrices cycles是不是指的replication呢-----肯定是
        for(i = 0; i < cycles; i++)
        {
            for(j = 0; j < length; j++)
            {
                overallAverages[i][j] = 0;
            }
        }

        for(i = 0; i < cycles; i++)//注意： 第i次循环（cycles）开始
        {
            //Need to initialise the transition count again
            int transCount = 0;//初始化变迁的数量，有什么用？

            //Get initial marking  感觉这一段都是重复的
            markings = data.getInitialMarkingVector();//TODO 虽然一样，但应该不能删，因为这是5个cycles
            marking = new int[length];
            for(int k = 0; k < length; k++)
            {
                if(markings[k]!= null && markings[k].size() > 0)
                {
                    MarkingView first = markings[k].getFirst();
                    if(first!=null)
                        marking[k] = first.getCurrentMarking();//marking={2,0}
                }
            }
            /***如果当前的配置不为空，就恢复之前的状态，这个应该是为了排错，不重要*/
            if(ApplicationSettings.getApplicationView() != null)
                data.restorePreviousMarking();

            //Initialise matrices for each new cycle
            for(j = 0; j < length; j++)//对每一个place都要有一个下面的属性
            {
                averageTokens[j] = 0;
                totalTokens[j] = 0;
                avgResult[j] = 0;
            }

            //Add initial marking to the total  应该是后面求性能指标的时候用{2,0}+{0,0}
            addTotal(marking, totalTokens);//TODO 将数组相加，但是：这个totalTokens总的token数有什么用呢？
            // Fire as many transitions as required and evaluate averages
            // Changed by Davd Patterson April 24, 2007
            // Handle a null return from fireRandomTransition if no transition
            // can be found.
            for(j = 0; j < firings; j++)//firing指的是自己设置的参数，应该是指100个变迁发生,TODO 好像不是指100个变迁？
            {//firing次开始
                System.out.println("Firing " + j + " now");
                //Fire a random transition TODO 这里应该是获得一个随机发生的变迁，和变迁发生规则有关系
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
                    data.fireTransition(fired); //NOU-PERE  TODO 应该只是单纯地发生
                    //Get the new marking from the _dataLayer object
                    markings = data.getCurrentMarkingVector();//markings和marking好像是一一对应的
                    marking = new int[length];//marking={0,0}
                    for(int k = 0; k < length; k++)//得到新的marking后，开始对每一个palce相对应的markings[k]进行遍历
                    {//这里markings[k].size()的值为1，是因为data.fireTransition(fired)方法中已经重新存储了currentMarking---应该不对
                        System.out.println("markings[k].size()simulation中:"+markings[k].size());//1
                        if(markings[k]!= null && markings[k].size() > 0)
                        {   //TODO 猜测是没有实现颜色petri网的模拟，而只是模拟了default类型的
                            MarkingView first = markings[k].getFirst();//为什么只得到First，就这一次，应该多次呀
                            //TODO 输出这里的curentMarking,应该是变迁发生一次后的数量
                            System.out.println("simulation中变迁发生一次后的数量:"+" "+first.getCurrentMarking());//按照测试的例子，这里应该变成了1,1
                            if(first != null)
                                marking[k] = first.getCurrentMarking();//给每个palce相对应的 marking[k]赋值
                        }//test 输出marking
                        for(int tmp : marking){
                            System.out.println("simulation中marking的值:"+tmp);
                        }
                    }

                    /*     for (int k=0; k<marking.length; k++)
                    System.out.print("" + marking[k] + ",");
                    System.out.println("");*/

                    //Add to the totalTokens array
                    addTotal(marking, totalTokens);
                    //Increment the transition count
                    transCount++;//TODO 是不是变迁的数量和firing是一样的
                }
            }//firing次结束

            //Evaluate averages
            for(j = 0; j < length; j++)
            {
                //Divide by transCount + 1 as total number of markings
                //considered includes the original marking which is outside
                //the loop which counts the number of randomly fired transitions.
                averageTokens[j] = (totalTokens[j] / (transCount + 1.0));

                //add appropriate to appropriate row of overall averages for each cycle
                overallAverages[i][j] = averageTokens[j];//给第i轮cycles里面的第j个palce的相对应的东西赋值
            }
        }//第i 圈cycle结束

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
                errorResult[i] =
                        errorResult[i] + ((overallAverages[j][i] - avgResult[i]) * (overallAverages[j][i] - avgResult[i]));
            }

            //Divide by number of cycles
            //Find standard deviation by taking square root
            //Multiply by 1.95996 to give 95% confidence interval
            errorResult[i] = 1.95996 * Math.sqrt(errorResult[i] / cycles);
        }

        ArrayList results = new ArrayList();
        DecimalFormat f = new DecimalFormat();
        f.setMaximumFractionDigits(5);

        if(averageTokens != null && errorResult != null
                && averageTokens.length > 0 && errorResult.length > 0)
        {
            // Write table of results
            results.add("Place");
            results.add("Average number of tokens");
            results.add("95% confidence interval (+/-)");
            for(i = 0; i < averageTokens.length; i++)
            {
                results.add(data.getPlace(i).getName());
                results.add(f.format(averageTokens[i]));
                results.add(f.format(errorResult[i]));
            }
        }
        if(ApplicationSettings.getApplicationView() != null) data.restorePreviousMarking();
        return ResultsHTMLPane.makeTable(results.toArray(), 3, false, true, true, true);
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


package pipe.samodules;

import pipe.exceptions.EmptyNetException;
import pipe.gui.ApplicationSettings;
import pipe.modules.interfaces.IModule;
import pipe.steadymodules.SSGNode;
import pipe.utilities.writers.PNMLWriter;
import pipe.utilities.Expander;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;
import it.unifi.oris.sirio.analyzer.log.PrintStreamLogger;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.models.stpn.StochasticTransitionFeature;
import it.unifi.oris.sirio.models.stpn.enumeration_policy.TruncationPolicy;
import it.unifi.oris.sirio.models.tpn.Priority;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class PathMeasure
        implements IModule
{

    private static final String MODULE_NAME = "PathMeasure";
    private PetriNetChooserPanel sourceFilePanel;
    private ResultsHTMLPane results;
    
	private JPanel contentPane;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JComboBox comboBox;
	private JComboBox comboBox_1;
	private JComboBox comboBox_2;
	private JComboBox comboBox_3;
	private JComboBox comboBox_4;
	private JComboBox comboBox_5;
	private JComboBox comboBox_6;
	private JComboBox comboBox_7;

	private int num1 = 1;
	private int num2 = 6;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JComboBox comboBox_11;
	private JComboBox comboBox_12;
	private JComboBox comboBox_13;
	private JComboBox comboBox_8;
	private JComboBox comboBox_9;
	private JComboBox comboBox_10;
	private JPanel panel_7;
	private JPanel panel_6;
	private JTextField textField_7;
	private JTextField textField_6;
	private JLabel lblT;
	private JPanel panel_until;
	private JPanel panel_4;
	private JPanel panel_5;
	private JComboBox comboBox_16;
	private JComboBox comboBox_17;
	private JComboBox comboBox_18;
	private JComboBox comboBox_19;
	private JTextField textField_8;
	private JComboBox comboBox_20;
	private JComboBox comboBox_21;
	private JComboBox comboBox_22;
	private JTextField textField_9;
	private JPanel panel_8;
	private JComboBox comboBox_23;
	private JComboBox comboBox_24;
	private JComboBox comboBox_25;
	private JTextField textField_10;
	private JPanel panel_9;
	private JComboBox comboBox_26;
	private JComboBox comboBox_27;
	private JTextField textField_11;
	private JPanel panel_10;
	private JComboBox comboBox_28;
	private JComboBox comboBox_29;
	private JComboBox comboBox_30;
	private JTextField textField_12;
	private JComboBox comboBox_14;
	private JComboBox comboBox_15;
	private JLabel lblU;
	private JButton btnPrint;
	private JTextField progressTextField;
	private JButton graph_button;
	
	//公式φ1
	ArrayList<PlaceFormula> pf_left;
	//公式φ2
	ArrayList<PlaceFormula> pf_right;
	//并或者交的关系
	ArrayList<Integer> pfrelation_left;
	ArrayList<Integer> pfrelation_right;
	String time;
	
	ArrayList<ArrayList> list;
	ArrayList<JComponent> list_left;
	ArrayList<JComponent> list_panel_1;
	ArrayList<JComponent> list_panel_2;
	ArrayList<JComponent> list_panel_3;
	ArrayList<JComponent> list_panel_4;
	ArrayList<JComponent> list_panel_5;
	ArrayList<JComponent> list_panel_6;
	ArrayList<JComponent> list_panel_7;
	ArrayList<JComponent> list_panel_8;
	ArrayList<JComponent> list_panel_9;
	ArrayList<JComponent> list_panel_10;
	
	

    public void start()
    {      
        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        if(pnmlData.getTokenViews().size() > 1)
        {
            Expander expander = new Expander(pnmlData);
            pnmlData = expander.unfold();
        }
        
        //建立图形界面
        buildInterface(pnmlData);
       
    }

    public void	computePathMeaure(ArrayList<PlaceFormula> pf_left, ArrayList<PlaceFormula> pf_right, ArrayList<Integer> pfrelation_left, ArrayList<Integer> pfrelation_right, String time)
    {  	
    	
        PetriNet petriNet = new PetriNet();
		Marking initialMarking = new Marking();
		build_PetriNet(petriNet, initialMarking);
							
		SafetyCondition formula = new SafetyCondition(pf_left, pf_right, pfrelation_left, pfrelation_right);
		
		SafetyCondition right_formula = new SafetyCondition(new ArrayList(), pf_right, new ArrayList(), pfrelation_right);
//		FileUtils.readfile(resultlabel);
		
		SSGNode.clear();
//      带停止条件的普通的瞬态分析
		OmegaBigDecimal timeBound = new OmegaBigDecimal(time);
		MyTransientAnalysis a = MyTransientAnalysis.compute(petriNet, initialMarking,
				new TruncationPolicy(new BigDecimal("0"), timeBound),
				formula,new PrintStreamLogger(System.out),null,false,progressTextField);
	
		
	
		BigDecimal step = new BigDecimal(time).divide(new BigDecimal(10));
		if(step.compareTo(new BigDecimal(0.1))== -1)
		{
			step = new BigDecimal(0.1);
		}
			
		final TransientSolution<Marking, Marking> ts = a.solveDiscretizedBeingProbabilities(new BigDecimal(time), step,
				right_formula,new PrintStreamLogger(System.out),null,progressTextField);
   
		graph_button.setVisible(true);
		graph_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new TransientSolutionViewer(ts);
				
			}			
			
		});
    }
    
    
    
    public int getnum1()
	{
		return num1;
	}
	public void setnum1(int _num)
	{
		num1 = _num;
	}
	public int getnum2()
	{
		return num2;
	}
	public void setnum2(int _num)
	{
		num2 = _num;
	}
	
    public void buildInterface(PetriNetView pnmlData)
    {
    	
    	 // Build interface
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, false);

        // 1 Set layout
        Container container = guiDialog.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        // 2 Add file browser
        sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
        container.add(sourceFilePanel);

        Vector placeId = new Vector();
        placeId.addElement("");
        
        
        //先得到Place信息
        PlaceView[] placeViewList = pnmlData.places();
        for(int i = 0;i < placeViewList.length;i++)
        {
//        	System.out.println(placeViewList[i].getId());
//        	System.out.println(placeViewList[i].getName());
//        	System.out.println(placeViewList[i].getTotalMarking());
        	
        	if(!placeViewList[i].isDeleted())
        	{
        		String id = placeViewList[i].getId();
        		placeId.addElement(id);
            	
        	}	
        }
        
		final Vector placeIdcopy = placeId;
        placeId.addElement("");
		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(null, "CSL\u516C\u5F0F", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		container.add(contentPane);
		
		JPanel panel = new JPanel();
		
		comboBox = new JComboBox();
		
		comboBox.setModel(new javax.swing.DefaultComboBoxModel(placeId));
		
		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_1 = new JTextField();
		textField_1.setColumns(3);
		
		panel_1 = new JPanel();
		
		
		comboBox_2 = new JComboBox();
		panel_1.add(comboBox_2);
		
		comboBox_2.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));
		
		comboBox_3 = new JComboBox();
		panel_1.add(comboBox_3);
		
		
		comboBox_3.setModel(new DefaultComboBoxModel(placeId));		
		
		comboBox_4 = new JComboBox();
		panel_1.add(comboBox_4);
		
		comboBox_4.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_2 = new JTextField();
		panel_1.add(textField_2);
		textField_2.setColumns(3);
		
			
		panel_2 = new JPanel();
	
		comboBox_5 = new JComboBox();
		panel_2.add(comboBox_5);
		
		comboBox_5.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));
		
		comboBox_6 = new JComboBox();
		panel_2.add(comboBox_6);
		
		comboBox_6.setModel(new javax.swing.DefaultComboBoxModel(placeId));		
		
		comboBox_7 = new JComboBox();
		panel_2.add(comboBox_7);
		
		comboBox_7.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_3 = new JTextField();
		panel_2.add(textField_3);
		textField_3.setColumns(3);

		
		panel_6 = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(comboBox);
		panel.add(comboBox_1);
		panel.add(textField_1);
		panel.add(panel_1);
		panel.add(panel_2);
		
		panel_3 = new JPanel();
		
		comboBox_8 = new JComboBox();
		panel_3.add(comboBox_8);
		
		comboBox_8.setModel(new javax.swing.DefaultComboBoxModel
		(new String[] { "", "∧", "∨",
				 }));
		
		comboBox_9 = new JComboBox();
		panel_3.add(comboBox_9);
		
		comboBox_9.setModel(new javax.swing.DefaultComboBoxModel(placeId));	
		
		comboBox_10 = new JComboBox();
		panel_3.add(comboBox_10);
		
		comboBox_10.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
				"=","!=", "<=", ">=" }));
		
		textField_4 = new JTextField();
		panel_3.add(textField_4);
		textField_4.setColumns(3);
		
		
		panel.add(panel_3);		
		
		panel_4 = new JPanel();
		panel.add(panel_4);
		
		
		comboBox_11 = new JComboBox();
		panel_4.add(comboBox_11);
		
		comboBox_11.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));
		comboBox_12 = new JComboBox();
		panel_4.add(comboBox_12);
		
		comboBox_12.setModel(new javax.swing.DefaultComboBoxModel(placeId));	
		comboBox_13 = new JComboBox();
		panel_4.add(comboBox_13);
		
		comboBox_13.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_5 = new JTextField();
		panel_4.add(textField_5);
		textField_5.setColumns(3);
		
		panel_5 = new JPanel();
		panel.add(panel_5);
		
		comboBox_14 = new JComboBox();
		panel_5.add(comboBox_14);
		
		comboBox_14.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));	
		
		comboBox_15 = new JComboBox();
		panel_5.add(comboBox_15);
		
		comboBox_15.setModel(new javax.swing.DefaultComboBoxModel(placeId));
		
		comboBox_16 = new JComboBox();
		
		comboBox_16.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		panel_5.add(comboBox_16);
		
		textField_6 = new JTextField();
		panel_5.add(textField_6);
		textField_6.setColumns(3);
		
		panel_until = new JPanel();
		panel.add(panel_until);
		
		lblU = new JLabel("U");
		lblU.setFont(new Font("Arial", Font.PLAIN, 24));
		
		lblT = new JLabel("t<=");
		lblT.setFont(new Font("Arial", Font.PLAIN, 16));
		
		textField_7 = new JTextField();
		textField_7.setColumns(2);
		
		comboBox_17 = new JComboBox();
		panel_6.add(comboBox_17);
		
		comboBox_17.setModel(new javax.swing.DefaultComboBoxModel(placeId));	
		
		comboBox_18 = new JComboBox();
		panel_6.add(comboBox_18);
		
		comboBox_18.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_8 = new JTextField();
		panel_6.add(textField_8);
		textField_8.setColumns(3);
		
		panel.add(panel_6);
		
		panel_7 = new JPanel();
		panel_7.setVisible(false);
		panel.add(panel_7);
		
		comboBox_19 = new JComboBox();
		panel_7.add(comboBox_19);
		
		comboBox_19.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));
		
		comboBox_20 = new JComboBox();
		panel_7.add(comboBox_20);
		
		comboBox_20.setModel(new javax.swing.DefaultComboBoxModel(placeId));	
		
		comboBox_21 = new JComboBox();
		panel_7.add(comboBox_21);
		
		comboBox_21.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_9 = new JTextField();
		panel_7.add(textField_9);
		textField_9.setColumns(3);
		
		panel_8 = new JPanel();
		panel.add(panel_8);
		
		comboBox_22 = new JComboBox();
		panel_8.add(comboBox_22);
		
		comboBox_22.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));
		
		comboBox_23 = new JComboBox();
		panel_8.add(comboBox_23);
		
		comboBox_23.setModel(new javax.swing.DefaultComboBoxModel(placeId));	
		
		comboBox_24 = new JComboBox();
		panel_8.add(comboBox_24);
		
		comboBox_24.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_10 = new JTextField();
		panel_8.add(textField_10);
		textField_10.setColumns(3);
		
		panel_9 = new JPanel();
		panel.add(panel_9);
		
		comboBox_25 = new JComboBox();
		panel_9.add(comboBox_25);
		
		comboBox_25.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));
		
		comboBox_26 = new JComboBox();
		panel_9.add(comboBox_26);
		
		comboBox_26.setModel(new javax.swing.DefaultComboBoxModel(placeId));	
		
		comboBox_27 = new JComboBox();
		panel_9.add(comboBox_27);
		
		comboBox_27.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_11 = new JTextField();
		panel_9.add(textField_11);
		textField_11.setColumns(3);
		
		panel_10 = new JPanel();
		panel.add(panel_10);
		
		comboBox_28 = new JComboBox();
		panel_10.add(comboBox_28);
		
		comboBox_28.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "∧", "∨",
        				 }));
		
		comboBox_29 = new JComboBox();
		panel_10.add(comboBox_29);
		
		comboBox_29.setModel(new javax.swing.DefaultComboBoxModel(placeId));	
		
		comboBox_30 = new JComboBox();
		panel_10.add(comboBox_30);
		
		comboBox_30.setModel(new javax.swing.DefaultComboBoxModel
				(new String[] { "", "<", ">",
        				"=","!=", "<=", ">=" }));
		
		textField_12 = new JTextField();
		panel_10.add(textField_12);
		textField_12.setColumns(3);
		
		//整一些列表
		list = new ArrayList<ArrayList>();
		list_left = new ArrayList<JComponent>();
		list_panel_1 = new ArrayList<JComponent>();
		list_panel_2 = new ArrayList<JComponent>();
		list_panel_3 = new ArrayList<JComponent>();
		list_panel_4 = new ArrayList<JComponent>();
		list_panel_5 = new ArrayList<JComponent>();
		list_panel_6 = new ArrayList<JComponent>();
		list_panel_7 = new ArrayList<JComponent>();
		list_panel_8 = new ArrayList<JComponent>();
		list_panel_9 = new ArrayList<JComponent>();
		list_panel_10 = new ArrayList<JComponent>();
		
		list_left.add(comboBox);
		list_left.add(comboBox_1);
		list_left.add(textField_1);
		
		list_panel_1.add(comboBox_2);
		list_panel_1.add(comboBox_3);
		list_panel_1.add(comboBox_4);
		list_panel_1.add(textField_2);
		
		list_panel_2.add(comboBox_5);
		list_panel_2.add(comboBox_6);
		list_panel_2.add(comboBox_7);
		list_panel_2.add(textField_3);
		
		list_panel_3.add(comboBox_8);
		list_panel_3.add(comboBox_9);
		list_panel_3.add(comboBox_10);
		list_panel_3.add(textField_4);
		
		list_panel_4.add(comboBox_11);
		list_panel_4.add(comboBox_12);
		list_panel_4.add(comboBox_13);
		list_panel_4.add(textField_5);
		
		list_panel_5.add(comboBox_14);
		list_panel_5.add(comboBox_15);
		list_panel_5.add(comboBox_16);
		list_panel_5.add(textField_6);
		
		list_panel_6.add(comboBox_17);
		list_panel_6.add(comboBox_18);
		list_panel_6.add(textField_8);
		
		list_panel_7.add(comboBox_19);
		list_panel_7.add(comboBox_20);
		list_panel_7.add(comboBox_21);
		list_panel_7.add(textField_9);
		
		list_panel_8.add(comboBox_22);
		list_panel_8.add(comboBox_23);
		list_panel_8.add(comboBox_24);
		list_panel_8.add(textField_10);
		
		list_panel_9.add(comboBox_25);
		list_panel_9.add(comboBox_26);
		list_panel_9.add(comboBox_27);
		list_panel_9.add(textField_11);
		
		list_panel_10.add(comboBox_28);
		list_panel_10.add(comboBox_29);
		list_panel_10.add(comboBox_30);
		list_panel_10.add(textField_12);
		
		list.add(list_left);
		list.add(list_panel_1);
		list.add(list_panel_2);
		list.add(list_panel_3);
		list.add(list_panel_4);
		list.add(list_panel_5);
		list.add(list_panel_6);
		list.add(list_panel_7);
		list.add(list_panel_8);
		list.add(list_panel_9);
		list.add(list_panel_10);
		
		
		
		
		JPanel panel_button = new JPanel();
		
		btnPrint = new JButton("计算");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pf_left = new ArrayList();
				
				pf_right = new ArrayList();
				
				pfrelation_left = new ArrayList();
				pfrelation_right = new ArrayList();
				
				//获取输入的公式，传给computePathMeasure,计算概率
				for(ArrayList<JComponent> child : list)
				{
					if(child == list_left)
					{
						JComboBox a = (JComboBox)(child.get(0));
						JComboBox b = (JComboBox)(child.get(1));
						JTextField c = (JTextField)(child.get(2));
						if(a.getSelectedIndex()!=0 && b.getSelectedIndex()!=0 &&!c.getText().equals(""))
						{
							System.out.println(placeIdcopy.get(a.getSelectedIndex()));
							System.out.println(a.getSelectedIndex() + b.getSelectedIndex() + c.getText());
							pf_left.add(new PlaceFormula(placeIdcopy.get(a.getSelectedIndex())+"", b.getSelectedIndex(), c.getText()));
								
						}
						else
						{
							System.out.println("最左边为空");
						}
					}
					else if(child == list_panel_6)
					{
						JComboBox a = (JComboBox)(child.get(0));
						JComboBox b = (JComboBox)(child.get(1));
						JTextField c = (JTextField)(child.get(2));
						if(a.getSelectedIndex()!=0 &&b.getSelectedIndex()!=0 &&!c.getText().equals(""))
						{
							System.out.println(a.getSelectedIndex()+b.getSelectedIndex()+c.getText());
							pf_right.add(new PlaceFormula(placeIdcopy.get(a.getSelectedIndex())+"",b.getSelectedIndex(),c.getText()));
						}
						else
						{
							System.out.println("右边公式为空");
						}
					}
					else 
					{
						JComboBox a = (JComboBox)(child.get(0));
						
						
						if(a.getParent().isVisible())
						{
							JComboBox b = (JComboBox)(child.get(1));
							JComboBox d = (JComboBox)(child.get(2));
							JTextField c = (JTextField)(child.get(3));
							
							if(a.getSelectedIndex()!=0 &&b.getSelectedIndex()!=0 &&d.getSelectedIndex()!=0 &&!c.getText().equals(""))
							{
								System.out.println(a.getSelectedIndex()+b.getSelectedIndex()+d.getSelectedIndex()+c.getText());
								if(child == list_panel_1|| child == list_panel_2|| child == list_panel_3|| child == list_panel_4|| child == list_panel_5)
								{
									pf_left.add(new PlaceFormula(placeIdcopy.get(b.getSelectedIndex())+"",d.getSelectedIndex(),c.getText()));
									pfrelation_left.add(a.getSelectedIndex());
								}
								else
								{
									pf_right.add(new PlaceFormula(placeIdcopy.get(b.getSelectedIndex())+"",d.getSelectedIndex(),c.getText()));
									pfrelation_right.add(a.getSelectedIndex());
								}						
							}
							else
							{
								System.out.println("null");
							}
						}
						
					}
					
				}
				
				time = textField_7.getText();
				
				new Thread(new Runnable() {
					public void run() {
				
						//调用方法计算基于路径的值,单独起一个线程
						computePathMeaure(pf_left, pf_right, pfrelation_left, pfrelation_right, time);
				
					}
				}).start();			
				System.out.println("button事件结束");	
			}
			
			
		});
		
		JLabel lblcsl = new JLabel("输入安全性度量指标，形如\u03C61");
		
		JLabel lblNewLabel = new JLabel("U");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		
		JLabel lblNewLabel_1 = new JLabel("t <= \u9650\u5B9A\u65F6\u95F4");
		
		JLabel lblNewLabel_2 = new JLabel("\u03C62");
		
		
		
		progressTextField = new JTextField();
		progressTextField.setBackground(UIManager.getColor("Panel.background"));
		progressTextField.setBorder(null);
		progressTextField.setColumns(10);
		
		//显示详细结果的按钮
		graph_button = new JButton("\u67E5\u770B\u8BE6\u7EC6");
		
		graph_button.setVisible(false);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(152)
							.addComponent(lblcsl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel_2))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(272)
							.addComponent(btnPrint))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(56)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_button, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(progressTextField, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
									.addGap(47)
									.addComponent(graph_button))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(30)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 551, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(39, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(8)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblcsl)
								.addComponent(lblNewLabel)
								.addComponent(lblNewLabel_2)))
						.addComponent(lblNewLabel_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_button, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnPrint)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(progressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(graph_button))
					.addContainerGap())
		);
		
		
		
		GroupLayout gl_panel_until = new GroupLayout(panel_until);
		gl_panel_until.setHorizontalGroup(
			gl_panel_until.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_until.createSequentialGroup()
					.addGap(5)
					.addComponent(lblU)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblT)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField_7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel_until.setVerticalGroup(
			gl_panel_until.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_until.createSequentialGroup()
					.addGap(5)
					.addComponent(lblU))
				.addGroup(gl_panel_until.createParallelGroup(Alignment.BASELINE)
					.addComponent(lblT)
					.addComponent(textField_7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel_until.setLayout(gl_panel_until);
		
		//panel_11显示“=计算的结果”
		JPanel panel_11 = new JPanel();
		panel.add(panel_11);
		
		JLabel equlas_label = new JLabel(" = ");
		equlas_label.setFont(new Font("宋体", Font.PLAIN, 16));
		panel_11.add(equlas_label);
		
		JLabel result_label = new JLabel("               ");
		panel_11.add(result_label);
		
		
		panel_1.setVisible(false);
		panel_2.setVisible(false);
		panel_3.setVisible(false);
		panel_4.setVisible(false);
		panel_5.setVisible(false);
		panel_7.setVisible(false);
		panel_8.setVisible(false);
		panel_9.setVisible(false);
		panel_10.setVisible(false);
		panel_11.setVisible(false);
		
		JButton btnNewButton = new JButton("add \u03C61");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					int a = getnum1();
					switch(a)
					{
						case 1:
							panel_1.setVisible(true);
							break;
						case 2:
							panel_2.setVisible(true);
							break;
						case 3:
							panel_3.setVisible(true);
							break;
						case 4:
							panel_4.setVisible(true);
							break;
						case 5:
							panel_5.setVisible(true);
							break;
						default:
							a--;
							break;
					
					}
					a = a+1;
					setnum1(a);
				}
			
		});
		
		JButton btnNewButton_1 = new JButton("remove \u03C61");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				int a = getnum1();
				a = a-1;
				switch(a)
				{
					case 1:
						panel_1.setVisible(false);
						break;
					case 2:
						panel_2.setVisible(false);
						break;
					case 3:
						panel_3.setVisible(false);
						break;
					case 4:
						panel_4.setVisible(false);
						break;
					case 5:
						panel_5.setVisible(false);
						break;	
					default:
						a++;
						break;
				
				}
				setnum1(a);
			}
			
		});
		
		
		
		JButton button = new JButton("add \u03C62");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					int a = getnum2();
					switch(a)
					{
						case 6:
							panel_7.setVisible(true);
							break;
						case 7:
							panel_8.setVisible(true);
							break;
						case 8:
							panel_9.setVisible(true);
							break;
						case 9:
							panel_10.setVisible(true);
							break;
						default:
							a--;
							break;
					
					}
					a = a+1;
					setnum2(a);
				}
			
		});
		
		JButton button_1 = new JButton("remove \u03C62");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				int a = getnum2();			
				switch(a)
				{
					case 7:
						panel_7.setVisible(false);
						break;
					case 8:
						panel_8.setVisible(false);
						break;
					case 9:
						panel_9.setVisible(false);
						break;
					case 10:
						panel_10.setVisible(false);
						break;	
					default:
						a++;
						break;
				
				}
				a--;
				setnum2(a);
			}
			
		});
		GroupLayout gl_panel_button = new GroupLayout(panel_button);
		gl_panel_button.setHorizontalGroup(
			gl_panel_button.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_button.createSequentialGroup()
					.addContainerGap(42, Short.MAX_VALUE)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnNewButton_1)
					.addGap(18)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(button_1)
					.addGap(52))
		);
		gl_panel_button.setVerticalGroup(
			gl_panel_button.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_button.createSequentialGroup()
					.addGroup(gl_panel_button.createParallelGroup(Alignment.BASELINE)
						.addComponent(button_1)
						.addComponent(button)
						.addComponent(btnNewButton_1)
						.addComponent(btnNewButton))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_button.setLayout(gl_panel_button);
		contentPane.setLayout(gl_contentPane);
        //
        
//        
//        // 3 Add results pane
//        results = new ResultsHTMLPane(pnmlData.getPNMLName());
//        container.add(results);
//
//        // 4 Add button
//        container.add(new ButtonBar("Classify", classifyButtonClick,
//                                      guiDialog.getRootPane()));

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


    public static void build_PetriNet(PetriNet petriNet, Marking initialMarking) {
		
    	
    	PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        if(pnmlData.getTokenViews().size() > 1)
        {
            Expander expander = new Expander(pnmlData);
            pnmlData = expander.unfold();
        }
        
        //先得到Place信息
        PlaceView[] placeViewList = pnmlData.places();
        for(int i=0;i < placeViewList.length;i++)
        {
//        	System.out.println(placeViewList[i].getId());
//        	System.out.println(placeViewList[i].getName());
//        	System.out.println(placeViewList[i].getTotalMarking());
        	
        	if(!placeViewList[i].isDeleted())
        	{
        		String name = placeViewList[i].getName();
            	petriNet.addPlace(name);
            	
            	if(placeViewList[i].getTotalMarking()!=0)
            	{
            		initialMarking.setTokens(petriNet.getPlace(placeViewList[i].getName()), placeViewList[i].getTotalMarking());
            	}
        	}	
        }

        //还没有加入权重！
		//得到变迁信息
        TransitionView[] transitionViewList = pnmlData.getTransitionViews();
        for(int i=0;i < transitionViewList.length;i++)
        {
//        	System.out.println(transitionViewList[i].getId());
//        	System.out.println(transitionViewList[i].getName());
//        	System.out.println(transitionViewList[i].getType());
//        	System.out.println(transitionViewList[i].getRate());
//        	System.out.println(transitionViewList[i].getPriority());
//        	System.out.println(transitionViewList[i].getDelay());
        	       	
        	if(!transitionViewList[i].isDeleted())
        	{
        		String name = transitionViewList[i].getName();
            	Transition current = petriNet.addTransition(name);
            	switch (transitionViewList[i].getType())
            	{
            		case 0:
            			current.addFeature(StochasticTransitionFeature
            					.newDeterministicInstance(new BigDecimal(0), new BigDecimal(transitionViewList[i].getRate())));
            			current.addFeature(new Priority(transitionViewList[i].getPriority()));
            			break;
            		case 1:
            			current.addFeature(StochasticTransitionFeature
            					.newExponentialInstance(new BigDecimal(transitionViewList[i].getRate())));
            			break;
            		case 2:
            			//delay  weight 
            			current.addFeature(StochasticTransitionFeature
            					.newDeterministicInstance(new BigDecimal(transitionViewList[i].getDelay()), new BigDecimal(transitionViewList[i].getRate())));
            			current.addFeature(new Priority(transitionViewList[i].getPriority()));
            			break;
            		case 3:
            			current.addFeature(StochasticTransitionFeature
            					.newUniformInstance(new OmegaBigDecimal(transitionViewList[i].getRate()+""), new OmegaBigDecimal(transitionViewList[i].getDelay()+"")));
            			break;
            		case 4:
            			
            			StochasticTransitionFeature newFeature = new StochasticTransitionFeature();
            		
            			ErLang firingTimeDensity =  new ErLang(Variable.X, (int)(transitionViewList[i].getRate()),new BigDecimal(transitionViewList[i].getDelay()));
            			newFeature.setFiringTimeDensity(firingTimeDensity);
            			//Erlang分布式没有设置权重的，默认为1
            			newFeature.setWeight(java.math.BigDecimal.ONE);
            			
            			current.addFeature(newFeature);
            			break;
            		      		
            	}
        	}
        	     	
        }

        
        int[][] forwards = pnmlData.getActiveTokenView().getForwardsIncidenceMatrix(pnmlData.getArcsArrayList(),
                pnmlData.getTransitionsArrayList(), pnmlData.getPlacesArrayList());
        
        int[][] backwords = pnmlData.getActiveTokenView().getBackwardsIncidenceMatrix(pnmlData.getArcsArrayList(),
                pnmlData.getTransitionsArrayList(), pnmlData.getPlacesArrayList());
        
        int[][] inhibition = pnmlData.getActiveTokenView().getInhibitionMatrix(pnmlData.getInhibitorsArrayList(), pnmlData.getTransitionsArrayList(), pnmlData.getPlacesArrayList());
        
        
        for(int i=0; i<forwards.length;i++)
        {
        	for(int j=0;j<forwards[i].length;j++)
        	{
        		//i表示place的序号 j是变迁序号 forwards表示transition到place
        		//System.out.println("["+i+"]"+"["+j+"]"+forwards[i][j]);
        		if(forwards[i][j]!=0 && !placeViewList[i].isDeleted() && !transitionViewList[j].isDeleted())
        		{
        			petriNet.addPostcondition(petriNet.getTransition(transitionViewList[j].getName())
        					, petriNet.getPlace(placeViewList[i].getName()), forwards[i][j]);
        		}
        		
        	}
        		
        }
        for(int i=0; i<backwords.length;i++)
        {
        	for(int j=0;j<backwords[i].length;j++)
        	{
        		//i表示place的序号 j是变迁序号 backwords表示place到transition
        		//System.out.println("["+i+"]"+"["+j+"]"+backwords[i][j]);
        		if(backwords[i][j]!=0 && !placeViewList[i].isDeleted() && !transitionViewList[i].isDeleted())
        		{
        			petriNet.addPrecondition(petriNet.getPlace(placeViewList[i].getName())
        					, petriNet.getTransition(transitionViewList[j].getName()), backwords[i][j]);
        		}
        	}
        		
        }
        
        for(int i=0; i<inhibition.length;i++)
        {
        	for(int j=0;j<inhibition[i].length;j++)
        	{
        		//i表示place的序号 j是变迁序号 inhibition表示place到transition有抑制弧
        		System.out.println("抑制弧["+i+"]"+"["+j+"]"+inhibition[i][j]);
        		if(inhibition[i][j]!=0 && !placeViewList[i].isDeleted() && !transitionViewList[i].isDeleted())
        		{
        			petriNet.addInhibitorArc(petriNet.getPlace(placeViewList[i].getName())
        					, petriNet.getTransition(transitionViewList[j].getName()), inhibition[i][j]);
        		}
        	}
        		
        }
        
	
	}

}

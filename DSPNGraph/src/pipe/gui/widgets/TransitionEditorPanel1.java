package pipe.gui.widgets;

import pipe.gui.PetriNetTab;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;


/**
 *
 * @author  pere
 */
public class TransitionEditorPanel1
        extends javax.swing.JPanel {
   
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private final TransitionView _transitionView;
   private final boolean attributesVisible;
//   private final boolean timed;
   private final int type;
   private final boolean infiniteServer;
   private Integer priority = 0;
   private final Double rate;
   private final String name;
   private final RateParameter rParameter;
   private final PetriNetView _pnmlData;
   private final PetriNetTab _view;
   private final JRootPane rootPane;
   
   
   /**
    * Creates new form PlaceEditor
    * @param _rootPane
    * @param _transitionView
    * @param _pnmlData
    * @param _view
    */
   public TransitionEditorPanel1(JRootPane _rootPane, TransitionView _transitionView,
           PetriNetView _pnmlData, PetriNetTab _view) {
      this._transitionView = _transitionView;
      this._pnmlData = _pnmlData;
      this._view = _view;
      rParameter = this._transitionView.getRateParameter();
      name = this._transitionView.getName();
      type = this._transitionView.getType();
      infiniteServer = this._transitionView.isInfiniteServer();
      rootPane = _rootPane;
      
      initComponents();
      
      this.serverLabel.setVisible(false);
      this.serverPanel.setVisible(false);
      
      rootPane.setDefaultButton(okButton);

      attributesVisible = this._transitionView.getAttributesVisible();
      
      rate = this._transitionView.getRate();
         
      typeTransition(type);
      
//      if (type!=0){
//         timedTransition();
//      } else {
//         immediateTransition();
//         priority = _transitionView.getPriority();
//      }
      
      if (infiniteServer) {
         infiniteServerRadioButton.setSelected(true);
      } else {
         singleServerRadioButton.setSelected(true);
      }
      
      
      if (rParameter != null){
         for (int i = 1; i < rateComboBox.getItemCount(); i++) {
            if (rParameter == rateComboBox.getItemAt(i)){
               rateComboBox.setSelectedIndex(i);
            }
         }
      }      
   }
   
   private void typeTransition(Integer type){
	      typeComboBox.setSelectedIndex(type+1);
	      switch(type){
	      	case 0:
	      		  rateLabel.setText("Weight:");
	      	      rateTextField.setText("" + _transitionView.getRate());
	      	      
	      	      prioritySlider.setEnabled(true);
	      	      priorityTextField.setText("" + _transitionView.getPriority());
	      	      
	      	      priorityLabel.setEnabled(true);
	      	      priorityPanel.setEnabled(true);    
	      	      
	      	      delayLabel.setVisible(false);
	      	      delayTextField.setVisible(false);
//	      	      delayLabel.setText("Delay:");
//	      	      delayLabel.setEnabled(false);
//	      		  delayTextField.setText("0");
//	      		  delayTextField.setEnabled(false);
	      	      break;
	      	case 1:
	      		  rateLabel.setText("Rate:");
	      		  rateTextField.setText("" + _transitionView.getRate());
	  	      
	      		  prioritySlider.setEnabled(false);
	      		  priorityTextField.setText("0");
	      		  priorityLabel.setEnabled(false);
	      	      priorityPanel.setEnabled(false); 
	      	      
	      	    
	      	      
	      		  delayLabel.setText("Delay:");
	      	      delayLabel.setEnabled(false);
	      		  delayTextField.setText("");
	      		  delayTextField.setEnabled(false);
	      		break;
	      	case 2:
	      		  rateLabel.setText("Weight:");
	      	      rateTextField.setText("" + _transitionView.getRate());
	      	      
	      	      prioritySlider.setEnabled(true);
	      	      priorityTextField.setText("" + _transitionView.getPriority());
	      	      
	      	      priorityLabel.setEnabled(true);
	      	      priorityPanel.setEnabled(true); 
	      	      
	      	      delayLabel.setVisible(true);
	      	      delayTextField.setVisible(true);
	      	      delayLabel.setEnabled(true);
	      		  delayTextField.setEnabled(true);
	      	      delayLabel.setText("Delay:");
	      	      delayTextField.setText("" + _transitionView.getDelay());
	      	      
	      		break;
	      	case 3:
	      		  rateLabel.setText("EFT:");
	      		  rateTextField.setText("" + _transitionView.getRate());
	  	      
	      		  prioritySlider.setEnabled(false);
	      		  priorityTextField.setText("0");
	      		  
	      		  priorityLabel.setEnabled(false);
	      	      priorityPanel.setEnabled(false); 
	      	      
	      		  delayLabel.setEnabled(true);
	      		  delayTextField.setEnabled(true);
	      		  delayLabel.setText("LFT:");
	      		  delayTextField.setText("" + _transitionView.getDelay());
	      		break;
	      	case 4:
	      		  rateLabel.setText("K:");
	      		  rateTextField.setText("" + _transitionView.getRate());
	  	      
	      		  prioritySlider.setEnabled(false);
	      		  priorityTextField.setText("0");
	      		  
	      		  priorityLabel.setEnabled(false);
	      	      priorityPanel.setEnabled(false); 
	      	      
	      		  delayLabel.setEnabled(true);
	      		  delayTextField.setEnabled(true);
	      		  delayLabel.setText("Lambda:");
	      		  delayTextField.setText("" + _transitionView.getDelay());
	      		break;
	      	default:
	      		break;	      
	      }	          
	   }
   
   private void timedTransition(){
      timedRadioButton.setSelected(true);
      
      rateLabel.setText("Rate:");
      rateTextField.setText("" + _transitionView.getRate());
      
      prioritySlider.setEnabled(false);
      priorityTextField.setText("0");
      
      Enumeration buttons = semanticsButtonGroup.getElements();
      while (buttons.hasMoreElements()){
         ((AbstractButton)buttons.nextElement()).setEnabled(true);
      }      
      
      priorityLabel.setEnabled(false);
      priorityPanel.setEnabled(false);

      RateParameter[] rates = _pnmlData.markingRateParameters();
      if (rates.length > 0) {
         rateComboBox.addItem("");
          for(RateParameter rate1 : rates)
          {
              rateComboBox.addItem(rate1);
          }
      } else {
         rateComboBox.setEnabled(false);
      }      
   }
   
   
   private void immediateTransition(){
      immediateRadioButton.setSelected(true); 
      
      rateLabel.setText("Weight:");
      rateTextField.setText("" + _transitionView.getRate());
      
      prioritySlider.setEnabled(true);
      priorityTextField.setText("" + _transitionView.getPriority());
      
      priorityLabel.setEnabled(true);
      priorityPanel.setEnabled(true);      

      RateParameter[] rates = _pnmlData.markingRateParameters();
      if (rates.length > 0) {
         rateComboBox.addItem("");
          for(RateParameter rate1 : rates)
          {
              rateComboBox.addItem(rate1);
          }
      } else {
         rateComboBox.setEnabled(false);
      }            
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        ButtonGroup timingButtonGroup = new ButtonGroup();
        semanticsButtonGroup = new javax.swing.ButtonGroup();
        JPanel transitionEditorPanel = new JPanel();
        JLabel nameLabel = new JLabel();
        delayLabel = new JLabel();
        JLabel typeLabel = new JLabel();
        nameTextField = new javax.swing.JTextField();
        delayTextField = new javax.swing.JTextField();
        rateLabel = new javax.swing.JLabel();
        priorityLabel = new javax.swing.JLabel();
        attributesCheckBox = new javax.swing.JCheckBox();
        rateComboBox = new javax.swing.JComboBox();
        JPanel timingPanel = new JPanel();
        timedRadioButton = new javax.swing.JRadioButton();
        immediateRadioButton = new javax.swing.JRadioButton();
        serverPanel = new javax.swing.JPanel();
        singleServerRadioButton = new javax.swing.JRadioButton();
        infiniteServerRadioButton = new javax.swing.JRadioButton();
        JLabel rotationLabel = new JLabel();
        rotationComboBox = new javax.swing.JComboBox();
        typeComboBox = new javax.swing.JComboBox();;
        rateTextField = new javax.swing.JTextField();
        serverLabel = new javax.swing.JLabel();
        JLabel timingLabel = new JLabel();
        priorityPanel = new javax.swing.JPanel();
        prioritySlider = new javax.swing.JSlider();
        priorityTextField = new javax.swing.JTextField();
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton();
        okButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        transitionEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Transition Editor"));
        transitionEditorPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(nameLabel, gridBagConstraints);

        nameTextField.setText(_transitionView.getName());
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(nameTextField, gridBagConstraints);

        rateLabel.setText("Rate:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rateLabel, gridBagConstraints);

        priorityLabel.setText("Priority:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(priorityLabel, gridBagConstraints);

        attributesCheckBox.setSelected(_transitionView.getAttributesVisible());
        attributesCheckBox.setText("Show transition attributes");
        attributesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        attributesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(attributesCheckBox, gridBagConstraints);

//        rateComboBox.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                rateComboBoxActionPerformed(evt);
//            }
//        });
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 2;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.gridwidth = 2;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
//        transitionEditorPanel.add(rateComboBox, gridBagConstraints);

        
        //py
        delayLabel.setText("Delay:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(delayLabel, gridBagConstraints);

        delayTextField.setText(_transitionView.getDelay()+"");
        delayTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                delayTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                delayTextFieldFocusLost(evt);
            }
        });
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(delayTextField, gridBagConstraints);
        
        rateTextField.setText(_transitionView.getRate()+"");
        rateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusLost(evt);
            }
        });
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rateTextField, gridBagConstraints);
        
//        rateTextField.setMaximumSize(new java.awt.Dimension(30, 19));
//        rateTextField.setMinimumSize(new java.awt.Dimension(30, 19));
//        rateTextField.setPreferredSize(new java.awt.Dimension(30, 19));
//        rateTextField.addCaretListener(new javax.swing.event.CaretListener() {
//            public void caretUpdate(javax.swing.event.CaretEvent evt) {
//                rateTextFieldCaretUpdate(evt);
//            }
//        });
//        rateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
//            public void focusGained(java.awt.event.FocusEvent evt) {
//                rateTextFieldFocusGained(evt);
//            }
//            public void focusLost(java.awt.event.FocusEvent evt) {
//                rateTextFieldFocusLost(evt);
//            }
//        });
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
//        transitionEditorPanel.add(rateTextField, gridBagConstraints);


        
//        timingLabel.setText("Timing:");
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
//        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
//        transitionEditorPanel.add(timingLabel, gridBagConstraints);
        
        typeLabel.setText("Transition type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(typeLabel, gridBagConstraints);

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "Immediate", "Exponential",
        				"Deterministic ","Uniform ","Erlang " }));
        typeComboBox.setMaximumSize(new java.awt.Dimension(140, 20));
        typeComboBox.setMinimumSize(new java.awt.Dimension(140, 20));
        typeComboBox.setPreferredSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(typeComboBox, gridBagConstraints);
        
        typeComboBox.addItemListener(new ItemListener() {
            
			@Override
			public void itemStateChanged(ItemEvent event) {
				// TODO Auto-generated method stub
				if(event.getStateChange() == ItemEvent.SELECTED){
                    typeTransition(typeComboBox.getSelectedIndex()-1);   //修改后
                }
			}
        });

 
        
        //py
                    
        
//        timingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
//        timingPanel.setLayout(new java.awt.GridLayout(1, 0));
//
//        timingButtonGroup.add(timedRadioButton);
//        timedRadioButton.setText("Timed");
//        timedRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        timedRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
//        timedRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
//        timedRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
//        timedRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
//        timedRadioButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                timedRadioButtonActionPerformed(evt);
//            }
//        });
//        timingPanel.add(timedRadioButton);
//
//        timingButtonGroup.add(immediateRadioButton);
//        immediateRadioButton.setText("Immediate");
//        immediateRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        immediateRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
//        immediateRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
//        immediateRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
//        immediateRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
//        immediateRadioButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                immediateRadioButtonActionPerformed(evt);
//            }
//        });
//        timingPanel.add(immediateRadioButton);
//
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.gridwidth = 3;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
//        transitionEditorPanel.add(timingPanel, gridBagConstraints);

        serverPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        serverPanel.setLayout(new java.awt.GridLayout(1, 0));

        semanticsButtonGroup.add(singleServerRadioButton);
        singleServerRadioButton.setText("Single");
        singleServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        singleServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        serverPanel.add(singleServerRadioButton);

        semanticsButtonGroup.add(infiniteServerRadioButton);
        infiniteServerRadioButton.setSelected(true);
        infiniteServerRadioButton.setText("Infinite");
        infiniteServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        infiniteServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        serverPanel.add(infiniteServerRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(serverPanel, gridBagConstraints);

        rotationLabel.setText("Rotation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rotationLabel, gridBagConstraints);

        rotationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "+45\u00B0", "+90\u00B0", "-45\u00B0" }));
        rotationComboBox.setMaximumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setMinimumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rotationComboBox, gridBagConstraints);

        

        serverLabel.setText("Server:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(serverLabel, gridBagConstraints);

        

        prioritySlider.setMajorTickSpacing(50);
        prioritySlider.setMaximum(127);
        prioritySlider.setMinimum(1);
        prioritySlider.setMinorTickSpacing(1);
        prioritySlider.setSnapToTicks(true);
        prioritySlider.setToolTipText("1: lowest priority; 127: highest priority");
        prioritySlider.setValue(_transitionView.getPriority());
        prioritySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                prioritySliderStateChanged(evt);
            }
        });
        priorityPanel.add(prioritySlider);

        //prova
        priorityTextField.setEditable(false);
        priorityTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        priorityTextField.setText("1");
        priorityTextField.setMaximumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setMinimumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setPreferredSize(new java.awt.Dimension(36, 19));
        priorityTextField.setText(""+ _transitionView.getPriority());
        priorityPanel.add(priorityTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        transitionEditorPanel.add(priorityPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(transitionEditorPanel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        buttonPanel.add(cancelButton, gridBagConstraints);

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(75, 25));
        okButton.setMinimumSize(new java.awt.Dimension(75, 25));
        okButton.setPreferredSize(new java.awt.Dimension(75, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        okButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                okButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        buttonPanel.add(okButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 8, 3);
        add(buttonPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void rateTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_rateTextFieldCaretUpdate
      try {
         if ((rateComboBox.getSelectedIndex() > 0) &&
            (((RateParameter)rateComboBox.getSelectedItem()).getValue()
                    != Double.parseDouble(rateTextField.getText()))){
            rateComboBox.setSelectedIndex(0);
         }
      } catch (NumberFormatException nfe){
         if (!nfe.getMessage().equalsIgnoreCase("empty String")) {
            System.out.println("NumberFormatException (not Empty String): \n" +
                    nfe.getMessage());
         }
      } catch (Exception e){
         System.out.println(e.toString());
      }
   }//GEN-LAST:event_rateTextFieldCaretUpdate

   private void rateTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTextFieldFocusLost
      focusLost(rateTextField);
   }//GEN-LAST:event_rateTextFieldFocusLost

   private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusLost
      focusLost(nameTextField);
   }//GEN-LAST:event_nameTextFieldFocusLost

   private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
      focusGained(nameTextField);
   }//GEN-LAST:event_nameTextFieldFocusGained

   private void rateTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTextFieldFocusGained
      focusGained(rateTextField);
   }//GEN-LAST:event_rateTextFieldFocusGained

   private void delayTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusLost
	      focusLost(delayTextField);
	   }//GEN-LAST:event_nameTextFieldFocusLost

   private void delayTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
	      focusGained(delayTextField);
	   }//GEN-LAST:event_nameTextFieldFocusGained
	   
   
   private void focusGained(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
      textField.moveCaretPosition(textField.getText().length());
   }
   
   private void focusLost(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
   }   
   
   
   private void rateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rateComboBoxActionPerformed
      int index = rateComboBox.getSelectedIndex();
      if (index > 0){
         rateTextField.setText(_pnmlData.markingRateParameters()[index-1].getValue().toString());
      }
   }//GEN-LAST:event_rateComboBoxActionPerformed

   
   private void timedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timedRadioButtonActionPerformed
      if (timedRadioButton.isSelected()){
         timedTransition();
      } else {
         immediateTransition();
      }
   }//GEN-LAST:event_timedRadioButtonActionPerformed

   private void immediateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_immediateRadioButtonActionPerformed
      if (immediateRadioButton.isSelected()){
         immediateTransition();
      } else {
         timedTransition();
      }
   }//GEN-LAST:event_immediateRadioButtonActionPerformed

   
   private void prioritySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_prioritySliderStateChanged
      priorityTextField.setText("" +prioritySlider.getValue());
   }//GEN-LAST:event_prioritySliderStateChanged

   
   private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_okButtonKeyPressed
      if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
         okButtonHandler(new java.awt.event.ActionEvent(this,0,""));
      }
   }//GEN-LAST:event_okButtonKeyPressed


   private final CaretListener caretListener = new javax.swing.event.CaretListener() {
      public void caretUpdate(javax.swing.event.CaretEvent evt) {
         JTextField textField = (JTextField)evt.getSource();
         textField.setBackground(new Color(255,255,255));
         //textField.removeChangeListener(this);
      }
   };   
   
       
   private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler

      _view.getHistoryManager().newEdit(); // new "transaction""
       
      String newName = nameTextField.getText();
      if (!newName.equals(name)){//表示改了名
         if (_pnmlData.checkTransitionIDAvailability(newName)){
            _view.getHistoryManager().addEdit(_transitionView.setPNObjectName(newName));
         } else{
            // aquest nom no est disponible...
            JOptionPane.showMessageDialog(null,
                    "There is already a transition named " + newName, "Error",
                                JOptionPane.WARNING_MESSAGE);
            return;
         }
      }
      // TODO
      
      //根据标签类型调整变迁编辑面板，以后再写
//      if (timedRadioButton.isSelected() != timed) {
//         _view.getHistoryManager().addEdit(
//                 _transitionView.setTimed(!timed));
//      }
      

      if (typeComboBox.getSelectedIndex() > 0) {
    	  Integer typeIndex = typeComboBox.getSelectedIndex();
    	  switch (typeIndex) {
          case 1:
        	  _view.getHistoryManager().addEdit(
                      _transitionView.setType(0));
             break;
          case 2:
        	  _view.getHistoryManager().addEdit(
                      _transitionView.setType(1));
             break;
          case 3:
        	  _view.getHistoryManager().addEdit(
                      _transitionView.setType(2));
             break;
          case 4:
        	  _view.getHistoryManager().addEdit(
                      _transitionView.setType(3));
              break;
          case 5:
        	  _view.getHistoryManager().addEdit(
                      _transitionView.setType(4));
              break;
          default:
             break;               
       }
       }
      
      if (infiniteServerRadioButton.isSelected() != infiniteServer) {
         _view.getHistoryManager().addEdit(
                 _transitionView.setInfiniteServer(!infiniteServer));
      }      
      
//      int newPriority = prioritySlider.getValue();
//      if (newPriority != priority && !_transitionView.isTimed()) {
//         _view.getHistoryManager().addEdit(_transitionView.setPriority(newPriority));
//      }
      
      if (rateComboBox.getSelectedIndex() > 0) {
         // There's a rate parameter selected
         RateParameter parameter = 
                 (RateParameter)rateComboBox.getSelectedItem() ;
         if (parameter != rParameter){

            if (rParameter != null) {
               // The rate parameter has been changed
               _view.getHistoryManager().addEdit(_transitionView.changeRateParameter(
                       (RateParameter)rateComboBox.getSelectedItem()));
            } else {
               // The rate parameter has been changed
               _view.getHistoryManager().addEdit(_transitionView.setRateParameter(
                       (RateParameter)rateComboBox.getSelectedItem()));
            }
         }
      } else {
         // There is no rate parameter selected
         if (rParameter != null) {
            // The rate parameter has been changed
            _view.getHistoryManager().addEdit(_transitionView.clearRateParameter());
         }
         try{
            Double newRate = Double.parseDouble(rateTextField.getText());
            if (!newRate.equals(rate)) {
               _view.getHistoryManager().addEdit(_transitionView.setRate(newRate));
            }
         } catch (NumberFormatException nfe){
            rateTextField.setBackground(new Color(255,0,0));
            rateTextField.addCaretListener(caretListener);
            return;
         } catch (Exception e){
            System.out.println(":" + e);
         }
      } 

      if (attributesVisible != attributesCheckBox.isSelected()){
         _transitionView.toggleAttributesVisible();
      }      
            
      Integer rotationIndex = rotationComboBox.getSelectedIndex();
      if (rotationIndex > 0) {
         int angle = 0;
         switch (rotationIndex) {
            case 1:
               angle = 45;
               break;
            case 2:
               angle = 90;
               break;
            case 3:
               angle = 135; //-45
               break;
            default:
               break;               
         }
         if (angle != 0) {
            _view.getHistoryManager().addEdit(_transitionView.rotate(angle));
         }
      }
      _transitionView.repaint();
      exit();
   }//GEN-LAST:event_okButtonHandler

   private void exit() {
      rootPane.getParent().setVisible(false);
   }
   
   
   private void cancelButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonHandler
      //Provisional!
      exit();
   }//GEN-LAST:event_cancelButtonHandler
      
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox attributesCheckBox;
    private javax.swing.JRadioButton immediateRadioButton;
    private javax.swing.JRadioButton infiniteServerRadioButton;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField delayTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel priorityLabel;
    private javax.swing.JPanel priorityPanel;
    private javax.swing.JSlider prioritySlider;
    private javax.swing.JTextField priorityTextField;
    private javax.swing.JComboBox rateComboBox;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JLabel delayLabel;
    private javax.swing.JTextField rateTextField;
    private javax.swing.JComboBox rotationComboBox;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.ButtonGroup semanticsButtonGroup;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JRadioButton singleServerRadioButton;
    private javax.swing.JRadioButton timedRadioButton;
    // End of variables declaration//GEN-END:variables
   
}

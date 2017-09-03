package pipe.gui.widgets;

import pipe.gui.PetriNetTab;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.CaretListener;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;


/**
 *
 * @author  pere
 */
public class TransitionEditorPanel2
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
   public TransitionEditorPanel2(JRootPane _rootPane, TransitionView _transitionView,
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
      
      
      rootPane.setDefaultButton(okButton);

      attributesVisible = this._transitionView.getAttributesVisible();
      
      rate = this._transitionView.getRate();
         
      typeTransition(type);
   
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
	      	      
	      	      delayLabel.setText("Delay:");
	      	      delayLabel.setEnabled(false);
	      		  delayTextField.setText("0");
	      		  delayTextField.setEnabled(false);
	      	      break;
	      	case 1:
	      		  rateLabel.setText("Rate:");
	      		  rateTextField.setText("" + _transitionView.getRate());
	  	      
	      		  prioritySlider.setEnabled(false);
	      		  priorityTextField.setText("0");
	      		  priorityLabel.setEnabled(false);
	      	     
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
	      	      
	      		  delayLabel.setEnabled(true);
	      		  delayTextField.setEnabled(true);
	      		  delayLabel.setText("Lambda:");
	      		  delayTextField.setText("" + _transitionView.getDelay());
	      		break;
	      	default:
	      		break;	      
	      }	          
	   }
   
   
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    	
        
        JLabel nameLabel = new JLabel();
        delayLabel = new JLabel();
        JLabel typeLabel = new JLabel();
        nameTextField = new javax.swing.JTextField();
        delayTextField = new javax.swing.JTextField();
        rateLabel = new javax.swing.JLabel();
        priorityLabel = new javax.swing.JLabel();
        attributesCheckBox = new javax.swing.JCheckBox();
        rateComboBox = new javax.swing.JComboBox();
       
        infiniteServerRadioButton = new javax.swing.JRadioButton();
        JLabel rotationLabel = new JLabel();
        rotationComboBox = new javax.swing.JComboBox();
        typeComboBox = new javax.swing.JComboBox();;
        rateTextField = new javax.swing.JTextField();
  
        prioritySlider = new javax.swing.JSlider();
        priorityTextField = new javax.swing.JTextField();
        JButton cancelButton = new JButton();
        okButton = new javax.swing.JButton();

        
        
        GroupLayout gl_panel = new GroupLayout(this);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createSequentialGroup()
								.addGap(66)
								.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
									.addComponent(rateLabel)
									.addComponent(nameLabel))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_panel.createSequentialGroup()
										.addComponent(rateTextField, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(delayLabel)
										.addGap(18)
										.addComponent(delayTextField, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
									.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)))
							.addGroup(gl_panel.createSequentialGroup()
								.addComponent(typeLabel, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(typeComboBox, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)))
						.addGap(49))
					.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap(51, Short.MAX_VALUE)
						.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(priorityLabel)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(prioritySlider, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(rotationLabel)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(attributesCheckBox)
										.addComponent(rotationComboBox, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(269)
									.addComponent(priorityTextField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)))
							.addGroup(gl_panel.createSequentialGroup()
								.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(cancelButton)
								.addGap(5)))
						.addGap(49))
			);
			gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup()
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(nameLabel)
							.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(12)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(rateLabel)
							.addComponent(rateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(delayLabel)
							.addComponent(delayTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(15)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(typeLabel)
							.addComponent(typeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addComponent(priorityLabel)
							.addComponent(priorityTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(prioritySlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(rotationLabel)
							.addComponent(rotationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(18)
						.addComponent(attributesCheckBox)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(okButton)
							.addComponent(cancelButton))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
		this.setLayout(gl_panel);
        
		nameLabel.setText("Name:");
		rateLabel.setText("Rate:");

        nameTextField.setText(_transitionView.getName());
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusLost(evt);
            }
        });
        
        rateLabel.setText("Rate:");

        priorityLabel.setText("Priority:");

        attributesCheckBox.setSelected(_transitionView.getAttributesVisible());
        attributesCheckBox.setText("Show transition attributes");
        attributesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        attributesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        delayLabel.setText("Delay:");
       
        delayTextField.setText(_transitionView.getDelay()+"");
        delayTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                delayTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                delayTextFieldFocusLost(evt);
            }
        });
        
       
        
        rateTextField.setText(_transitionView.getRate()+"");
        rateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusLost(evt);
            }
        });
        

        
        typeLabel.setText("Transition type:");

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel
        		(new String[] { "", "Immediate", "Exponential",
        				"Deterministic ","Uniform ","Erlang " }));
        typeComboBox.setMaximumSize(new java.awt.Dimension(140, 20));
        typeComboBox.setMinimumSize(new java.awt.Dimension(140, 20));
        typeComboBox.setPreferredSize(new java.awt.Dimension(140, 20));
        
        typeComboBox.addItemListener(new ItemListener() {
            
			@Override
			public void itemStateChanged(ItemEvent event) {
				// TODO Auto-generated method stub
				if(event.getStateChange() == ItemEvent.SELECTED){
                    typeTransition(typeComboBox.getSelectedIndex()-1);   //修改后
                }
			}
        });

 

        rotationLabel.setText("Rotation:");

        rotationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "+45\u00B0", "+90\u00B0", "-45\u00B0" }));
        rotationComboBox.setMaximumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setMinimumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setPreferredSize(new java.awt.Dimension(70, 20));


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
       

        
        priorityTextField.setEditable(false);
        priorityTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        priorityTextField.setText("1");
        priorityTextField.setMaximumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setMinimumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setPreferredSize(new java.awt.Dimension(36, 19));
        priorityTextField.setText(""+ _transitionView.getPriority());
        
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonHandler(evt);
            }
        });
        
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
        
    }// </editor-fold>//GEN-END:initComponents

 

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
   
   
   
   private void prioritySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_prioritySliderStateChanged
      priorityTextField.setText("" +prioritySlider.getValue());
   }//GEN-LAST:event_prioritySliderStateChanged

   
   private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_okButtonKeyPressed
      if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
         okButtonHandler(new java.awt.event.ActionEvent(this,0,""));
      }
   }//GEN-LAST:event_okButtonKeyPressed

  
   
       
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
      
      
      String rate = rateTextField.getText();
      if(!rate.equals(""))
    	  _view.getHistoryManager().addEdit(_transitionView.setRate(Double.parseDouble(rate)));
     
      String delay = delayTextField.getText();
      if(!delay.equals(""))
    	  _view.getHistoryManager().addEdit(_transitionView.setDelay(Double.parseDouble(delay)));
      
      _view.getHistoryManager().addEdit(_transitionView.setPriority(prioritySlider.getValue()));
      
      //根据标签类型调整变迁编辑面板，
      
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
    
    private javax.swing.JSlider prioritySlider;
    private javax.swing.JTextField priorityTextField;
    private javax.swing.JComboBox rateComboBox;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JLabel delayLabel;
    private javax.swing.JTextField rateTextField;
    private javax.swing.JComboBox rotationComboBox;
    private javax.swing.JComboBox typeComboBox;
    // End of variables declaration//GEN-END:variables
   
}

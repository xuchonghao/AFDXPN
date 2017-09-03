package pipe.gui.widgets.newwidges;

import pipe.gui.ApplicationSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

/**
 * Created by hanson on 2017/8/15.
 * 建模向导第二步，选择RES的数量
 */
public class ModelGuideDialog22 extends JDialog {
    // Variables declaration - do not modify
    private GuideModel nowModel;
    private ButtonGroup buttonGroup1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JRadioButton jRadioButton1;
    private JRadioButton jRadioButton2;
    private JRadioButton jRadioButton3;
    private JRadioButton jRadioButton4;
    private JRadioButton jRadioButton5;
    private JRadioButton jRadioButton6;

    private JSeparator jSeparator1;
    private JTextField jTextField1;
    // End of variables declaration
    private int returnStatus = RET_CANCEL;

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    public ModelGuideDialog22(Frame parent, boolean modal, GuideModel guideModel) {
        super(parent, modal);
        nowModel = guideModel;
        initComponents();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        buttonGroup1 = new ButtonGroup();
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jSeparator1 = new JSeparator();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jPanel2 = new JPanel();
        jRadioButton1 = new JRadioButton();
        jRadioButton2 = new JRadioButton();
        jRadioButton3 = new JRadioButton();
        jRadioButton4 = new JRadioButton();
        jRadioButton5 = new JRadioButton();
        jRadioButton6 = new JRadioButton();

        jLabel5 = new JLabel();
        jTextField1 = new JTextField();
        jPanel3 = new JPanel();
        jLabel4 = new JLabel();
        jButton4 = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("建模向导");

        jPanel1.setBackground(new Color(255, 255, 255));

        jLabel1.setText("第二步");

        jLabel2.setText("  选择RES终端组件的个数");

        //jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icon_guide.png"))); // NOI18N
        //jLabel3.setText(" ");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 206, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel2))
                                        .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                                .addContainerGap())
        );

        jButton2.setText("下一步");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton3.setText("取消");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);

        if(nowModel.getNumOfDU()==1)
        {
            jRadioButton1.setSelected(true);
        }

        jRadioButton1.setText(" 1");
        jRadioButton1.setAutoscrolls(true);
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        if(nowModel.getNumOfDU()==2)
        {
            jRadioButton2.setSelected(true);
        }
        jRadioButton2.setText(" 2");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        if(nowModel.getNumOfDU()==3)
        {
            jRadioButton3.setSelected(true);
        }
        jRadioButton3.setText(" 3");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });


        if(nowModel.getNumOfDU()>3)
        {
            jTextField1.setText(nowModel.getNumOfDU()+"");
        }
       // jLabel5.setText("其他");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jRadioButton1)
                                                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                .addComponent(jRadioButton4)
                                                               ))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jRadioButton2)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jRadioButton3))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jRadioButton5)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jRadioButton6))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()

                                                                .addGap(18, 18, 18)
                                                              ))))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jRadioButton2)
                                        .addComponent(jRadioButton3)
                                        .addComponent(jRadioButton1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jRadioButton4)
                                        .addComponent(jRadioButton5)
                                        .addComponent(jRadioButton6))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                       )
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(77, Short.MAX_VALUE))
        );

        jLabel4.setText("根据系统情况，选择终端组件个数");

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(42, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(26, 26, 26))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jLabel4)
                                .addContainerGap(101, Short.MAX_VALUE))
        );

        jButton4.setText("上一步");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(307, Short.MAX_VALUE)
                                .addComponent(jButton4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3)
                                .addGap(8, 8, 8))
                        .addComponent(jSeparator1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(79, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton3)
                                        .addComponent(jButton2)
                                        .addComponent(jButton4))
                                .addContainerGap())
        );

        pack();
    }

    private void jRadioButton1ActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        nowModel.setNumOfDU(1);
        jTextField1.setText("1");
    }

    private void jRadioButton2ActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        nowModel.setNumOfDU(2);
        jTextField1.setText("2");
    }

    private void jRadioButton3ActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        nowModel.setNumOfDU(3);
        jTextField1.setText("3");
    }


    private void jButton2ActionPerformed(ActionEvent evt) {
        String num = jTextField1.getText().trim();
        //System.out.println("373 nuum " + num);
        if(!num.equals(""))
        {
            Pattern pattern = Pattern.compile("[1|2|3][0-3]*");

            if(!pattern.matcher(num).matches())
            {
                JOptionPane.showMessageDialog(this, "终端个数输入有误！请重新输入", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }


            int count = Integer.parseInt(num);
            nowModel.setNumOfDU(count);
            //System.out.println("22 355nowModel，num" + count + nowModel.getNumOfSPM());
        }

        doClose(RET_OK);
//    	ModelGuideDialog3 guiDialog =  ModelGuideDialog3Factory.getDialog(CreateGui.getApp(), true, nowVo);
        ModelGuideDialog3 guiDialog =  new ModelGuideDialog3(ApplicationSettings.getApplicationView(), true, nowModel);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }


    private void jButton3ActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        doClose(RET_CANCEL);
    }
    private void jButton4ActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:   上一步
        String num = jTextField1.getText().trim();
        if(!num.equals(""))
        {
            int count = Integer.parseInt(num);
           nowModel.setNumOfSPM(count);//设置终端数
        }
        doClose(RET_CANCEL);
        ModelGuideDialog2 guiDialog =  new ModelGuideDialog2(ApplicationSettings.getApplicationView(), true, nowModel);
        //System.out.println(nowModel+"*********");
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);

    }
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
}

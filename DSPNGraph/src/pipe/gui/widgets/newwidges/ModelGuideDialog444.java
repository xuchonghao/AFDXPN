package pipe.gui.widgets.newwidges;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import org.jdom.Attribute;
import org.jdom.Element;
import pipe.gui.ApplicationSettings;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

//import static pipe.gui.widgets.newwidges.GuideModel.swCount;

/**
 * Created by hanson on 2017/8/15.
 * 建模向导第步，交换机 i
 */
public class ModelGuideDialog444 extends JDialog {
    private MultiSelectComboBox<String> comboBoxIn;
    private MultiSelectComboBox<String> comboBoxOut;
    //private int swCount = 1;
    private int firstCount ;
    int indexOfSw;
    ArrayList<Element> listElementOfSwPre = null;
    ArrayList<Element> arr = null;
    Element preSwLinkEnd = null;
    SW sw;
    public ModelGuideDialog444(Frame parent, boolean modal, GuideModel guideModel,int count,ArrayList<Element> arr, Element preSwLinkEnd) {
        super(parent, modal);
        nowModel = guideModel;
        indexOfSw = GuideModel.INDEXOFSW++;
        //swCount = count;//交换机的数量
        firstCount = guideModel.getNumOfSW();
        this.preSwLinkEnd = preSwLinkEnd;
        this.arr = arr;
        initComponents();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jSeparator1 = new JSeparator();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jPanel2 = new JPanel();
        jLabel5 = new JLabel();
        jTextField1 = new JTextField();
        jPanel3 = new JPanel();
        jLabel4 = new JLabel();
        jButton4 = new JButton();
        jPanel4 = new JPanel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("建模向导");

        jPanel1.setBackground(new Color(255, 255, 255));

        jLabel1.setText("第六步");

        jLabel2.setText(" 第"+(indexOfSw+1) +"个交换机         输入                                       输出");


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
                                .addContainerGap()));
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
                                .addContainerGap()));

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


        //开始SW业务
        sw = new SW();
        sw.setId("SW"+(indexOfSw+1));//这里要变成动态的

        int len = nowModel.getSpmList().size();
        String[] arrIn = new String[len+indexOfSw];//这里也得加上之前的交换机
        int l = 0;
        for(int i=0;i<len;i++)
            arrIn[l++] = "SPM" + (i+1);
        for(int i=0;i<indexOfSw;i++){
            arrIn[l++] = "SW" + (i+1);
        }
        comboBoxIn =  new MultiSelectComboBox<>(arrIn);
        comboBoxIn.setPreferredSize(new Dimension(150, 26));

        comboBoxIn.setPopupBackground(Color.LIGHT_GRAY);
        comboBoxIn.setBackground(Color.WHITE);

        //TODO 如果是第一个交换机
        listElementOfSwPre = ModelFactory2.getListOfSpmLinkEnd();
        /**复选框*/

        /*if(indexOfSw >= 1 && preSwLinkEnd!=null){//TODO 否则,上一个SW后面的链路最后一个元素应该传过来
            listElementOfSwPre.add(preSwLinkEnd);//S0
            System.out.println(preSwLinkEnd.getAttribute("id").getValue());
        }*/

        comboBoxIn.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //System.out.println("选择的值："+comboBoxIn.getSelectedItemsString());
                List<String> names = comboBoxIn.getSelectedItems();//选择的进入端口的名字SPM1  SW0

                System.out.println("in"+names);
                ArrayList<Element> swIn = new ArrayList<Element>();


                for(int i=0;i<listElementOfSwPre.size();i++){
                    Element e2 = listElementOfSwPre.get(i);
                    String id = e2.getAttribute("id").getValue();
                    String spmName = id.substring(0,4);
                   // String swName = id.substring(0,3);
                    for(String s : names){//TODO  这么做不一定对
                        if(spmName.equals(s)){// || swName.equals(s)){
                            swIn.add(e2);//把交换机后面的链路命名也加上了SW为前缀
                            break;
                        }
                    }
                }

                if(indexOfSw >= 1 && preSwLinkEnd!=null){//TODO 否则,上一个SW后面的链路最后一个元素应该传过来
                    String id = preSwLinkEnd.getAttribute("id").getValue();
                    String swName = id.substring(0,3);
                    for(String s : names){
                        if(swName.equals(s)){
                            swIn.add(preSwLinkEnd);
                            break;
                         }
                    }
                }
                sw.setIn(swIn);//输入只可能是前面的SPM   TODO 还可能是前面的SW
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });


        int duLen = nowModel.getNumOfDU();
        String[] arrOut = new String[nowModel.getNumOfSW()-1+duLen];
        int q=0;
        while(q<duLen){
            arrOut[q] = "RES" + (q+1) ;
            q++;
        }
        //System.out.println("GuideModel.swCount"+GuideModel.swCount);
        if(GuideModel.swCount > 1)
            for(int i=1;i<nowModel.getNumOfSW();i++){
                String s = "SW" + (i+1);
                if(sw.getId().equals(s)){
                    continue;
                }
                arrOut[q++] = s;
            }

        //System.out.println(arrOut.toString());
        comboBoxOut = new MultiSelectComboBox<>(arrOut);//输出是其他的交换机或者是DU
        comboBoxOut.setPreferredSize(new Dimension(150, 26));

        comboBoxOut.setPopupBackground(Color.LIGHT_GRAY);
        comboBoxOut.setBackground(Color.WHITE);
        comboBoxOut.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //System.out.println("222选择的值："+comboBoxOut.getSelectedItemsString());
                //List<Integer> indexs = comboBoxIn.getSelectedSortedIndexs();
                List<String> names = comboBoxOut.getSelectedItems();
                //System.out.println("out"+names);
                sw.setOut(names);//输入只可能是前面的SPM
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });


       /* if(nowModel.getNumOfSPM()>6)
        {
            jTextField1.setText(nowModel.getNumOfSPM()+"");
        }
        jLabel5.setText("其他");*/

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
                                                        .addComponent(comboBoxIn)
                                                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGap(18, 18, 18)
                                                                )
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(comboBoxOut)
                                                                .addGap(18, 18, 18))
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
                                        .addComponent(comboBoxIn))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(comboBoxOut))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                       )
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(77, Short.MAX_VALUE))
        );

        jLabel4.setText("根据系统情况，选择交换机的输入交换机的输出");

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


    private void jButton2ActionPerformed(ActionEvent evt) {//下一步
        String swId = sw.getId();
        ArrayList<Element> arrIn = sw.getIn();
        List<String> arrOut = sw.getOut();
        int outNum = arrOut.size();

        Element net = arr.get(1);
        //返回交换机输出最后的那个节点 indexOfSw   上一个的终点应该放进去
        ArrayList<Element> listOfSWLinkEnd = ModelFactory2.addSwitch(net,1610+ indexOfSw*1300,315+400 ,swId,outNum,arrIn);//添加交换机S0
        //swCount--;//交换机数量
        //System.out.println(GuideModel.swCount);
       // Element swLinkEnd2 = null;
        String str = null;
        for(int i=0;i<arrOut.size();i++){//数组 *先* 添加的DU 后添加的SW String str:arrOut
            str = arrOut.get(i);
            //System.out.println("out"+str);
            if("RES".equals(str.substring(0,3))){//添加DU
                Element swLinkEnd = listOfSWLinkEnd.get(i);

                ModelFactory2.addDU(nowModel,net,str,2400 + indexOfSw*1500,315+i*500,str,swLinkEnd);//当前DU的名字必须作为参数,DU前自动添加链路

            }else if("SW".equals(str.substring(0,2)) && (GuideModel.swCount--)>1){//    SW3,当这里是最后一个SW的时候才跳到5
                //swCount--;//交换机数量
                //如果是最初的数量直接结束
                //获取第i个交换机后面链路最后的结点
                Element swLinkEnd = listOfSWLinkEnd.get(i);
                //System.out.println(swLinkEnd.getAttribute("id").getValue());
                //跳转另一个交换机页面，此时的输入：之前的交换机和SPM
                //输出：之后的交换机和DU

                doClose(RET_OK);
                ModelGuideDialog444 guiDialog =  new ModelGuideDialog444(ApplicationSettings.getApplicationView(), true, nowModel,GuideModel.swCount,arr,swLinkEnd);
                guiDialog.pack();
                guiDialog.setLocationRelativeTo(null);
                guiDialog.setVisible(true);
            }

        }
        //for结束才能让它进来   里是最后一个SW的时候才跳到5 str===SW3
        if(GuideModel.swCount == 1 && GuideModel.FLAG &&(str.equals("SW"+firstCount) || "RES".equals(str.substring(0,3)))){
            //TODO
            doClose(RET_OK);
            ModelGuideDialog5 guiDialog =  new ModelGuideDialog5(ApplicationSettings.getApplicationView(), true, nowModel,arr);
            GuideModel.FLAG = false;
            guiDialog.pack();
            guiDialog.setLocationRelativeTo(null);
            guiDialog.setVisible(true);
        }
        if(firstCount>1)
            dispose();
        /*else if((GuideModel.swCount--) > 1 && GuideModel.FLAG){
            //跳转另一个交换机页面，此时的输入：之前的交换机和SPM
            //输出：之后的交换机和DU
            doClose(RET_OK);
            ModelGuideDialog444 guiDialog =  new ModelGuideDialog444(ApplicationSettings.getApplicationView(), true, nowModel,GuideModel.swCount,arr,swLinkEnd2);
            guiDialog.pack();
            guiDialog.setLocationRelativeTo(null);
            guiDialog.setVisible(true);
        }*/


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
        ModelGuideDialog4 guiDialog =  new ModelGuideDialog4(ApplicationSettings.getApplicationView(), true, nowModel);
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

    // Variables declaration - do not modify
    private GuideModel nowModel;
    //private ButtonGroup buttonGroup1;
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
    private JPanel jPanel4;
    //private JCheckBox Button1;
    //private JCheckBox Button2;
    //private JCheckBox Button3;
    //private JCheckBox Button4;
    //private JCheckBox Button5;
    //private JCheckBox Button6;




    private JSeparator jSeparator1;

    private JTextField jTextField1;
    // End of variables declaration
    private int returnStatus = RET_CANCEL;


    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
}

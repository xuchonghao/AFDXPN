package pipe.gui.widgets.newwidges;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchonghao on 9/1/17.
 */
public class MultiComboBox extends JComponent {
    private Object[] values;

    public Object[] defaultValues;

    private List<ActionListener> listeners = new ArrayList<ActionListener>();

    private MultiPopup popup;

    private JTextField editor;

    protected JButton  arrowButton;

    private String valueSperator;

    private static final String DEFAULT_VALUE_SPERATOR = ",";

    public MultiComboBox(Object[] value, Object[] defaultValue){
        this(value,defaultValue,DEFAULT_VALUE_SPERATOR);
    }

    public MultiComboBox(Object[] value, Object[] defaultValue , String valueSperator) {
        values = value;
        defaultValues = defaultValue;
        this.valueSperator = valueSperator;
        initComponent();
    }

    private void initComponent() {
        //暂时使用该布局,后续自己写个布局
        this.setLayout(new FlowLayout());
        popup =new  MultiPopup(values,defaultValues);
        popup.addActionListener(new PopupAction());
        editor = new JTextField();
        editor.setBackground(Color.WHITE);
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(150,20));
//      editor.setBorder(getBorder());
        editor.addMouseListener(new EditorHandler());
        arrowButton = createArrowButton();
        arrowButton.addMouseListener(new EditorHandler());
        add(editor);
        add(arrowButton);
        setText() ;


    }

    public Object[] getSelectedValues() {
        return popup.getSelectedValues();
    }

    public void addActionListener(ActionListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }

    protected void fireActionPerformed(ActionEvent e) {
        for (ActionListener l : listeners) {
            l.actionPerformed(e);
        }
    }

    private class PopupAction implements ActionListener{

        public void actionPerformed(ActionEvent e) {

            if(e.getActionCommand().equals(MultiPopup.CANCEL_EVENT)){

            }else if(e.getActionCommand().equals(MultiPopup.COMMIT_EVENT)){
                defaultValues = popup.getSelectedValues();
                setText();
                //把事件继续传递出去
                fireActionPerformed(e);
            }

            togglePopup();


        }

    }

    private void togglePopup(){
        if(popup.isVisible()){
            popup.setVisible(false);
        }else{
            popup.setDefaultValue(defaultValues);
            popup.show(this, 0, getHeight());
        }
    }

    private void setText() {
        StringBuilder builder = new StringBuilder();
        for(Object dv : defaultValues){
            builder.append(dv);
            builder.append(valueSperator);
        }

        editor.setText(builder.substring(0, builder.length() > 0 ? builder.length() -1  : 0).toString());
    }

    private class EditorHandler implements MouseListener{

        public void mouseClicked(MouseEvent e) {
            togglePopup();
        }

        public void mousePressed(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

    }


    public void paintComponent(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0,0,getWidth(),getHeight());
    }


    protected JButton createArrowButton() {
        JButton button = new BasicArrowButton(BasicArrowButton.SOUTH,
                UIManager.getColor("ComboBox.buttonBackground"),
                UIManager.getColor("ComboBox.buttonShadow"),
                UIManager.getColor("ComboBox.buttonDarkShadow"),
                UIManager.getColor("ComboBox.buttonHighlight"));
        button.setName("ComboBox.arrowButton");
        return button;
    }

    private class MulitComboboxLayout  implements LayoutManager{

        public void addLayoutComponent(String name, Component comp) {
            // TODO Auto-generated method stub

        }

        public void removeLayoutComponent(Component comp) {
            // TODO Auto-generated method stub

        }

        public Dimension preferredLayoutSize(Container parent) {
            return parent.getPreferredSize();
        }

        public Dimension minimumLayoutSize(Container parent) {
            return parent.getMinimumSize();
        }

        public void layoutContainer(Container parent) {
            int w=parent.getWidth();
            int h=parent.getHeight();
            Insets insets=parent.getInsets();
            h=h-insets.top-insets.bottom;

        }

    }
    public static void main(String[] args){



        UIManager.put("ScrollBarUI", com.sun.java.swing.plaf.windows.WindowsScrollBarUI.class.getName());// 设置滚动条样式为window风格的滚动条样式

        final JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        JLabel label3 = new JLabel("Media Outlets:");
        Object[] value = new String[]{ "Selected All","Al-Jazeera" , "BBC News" ,"Daily Mail" ,"Fox News","New York Daily News", "New York Times","the Guardian","Wall Street Journal"};
        Object[] defaultValue = new String[]{ "Selected All" };
        MultiComboBox mulit = new MultiComboBox(value, defaultValue);

        frame.add(mulit);
        frame.setVisible(true);

    }
}

package pipe.gui.widgets.newwidges;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Created by hanson on 2017/8/29.
 */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class MultiSelectComboBox<E> extends CustomComboBox<E> {

    public static void main(String[] args) {

        UIManager.put("ScrollBarUI", com.sun.java.swing.plaf.windows.WindowsScrollBarUI.class.getName());// 设置滚动条样式为window风格的滚动条样式

        final JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        final MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>(new String[] { "1", "2", "3", "4", "5", "6", "1", "2", "3", "4", "5", "6", "1", "2",
                "3", "4", "5", "6" });
        comboBox.setPreferredSize(new Dimension(150, 26));

        comboBox.setForegroundAndToPopup(Color.YELLOW);
        comboBox.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        comboBox.setPopupBorder(BorderFactory.createLineBorder(Color.RED));
        comboBox.setPopupBackground(Color.DARK_GRAY);
        comboBox.setSelectionBackground(Color.BLUE);
        comboBox.setSelectionForeground(Color.RED);
        comboBox.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                System.out.println("选择的值："+comboBox.getSelectedItemsString());
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        frame.add(comboBox);

        frame.setVisible(true);
    }

    protected Set<Integer> selectedIndexs = new HashSet<>();

    protected String spliceRegex = ",";

    public MultiSelectComboBox(Vector<E> vector) {
        super(new DefaultComboBoxModel(vector));
        init();
    }

    public MultiSelectComboBox() {
        super();
        init();
    }

    public MultiSelectComboBox(ComboBoxModel<E> aModel) {
        super(aModel);
        init();
    }

    public MultiSelectComboBox(E[] items) {
        super(items);
        init();
    }

    private void init() {
        setUI(new MultiSelectComboBoxUI());
        setRenderer(new MultiSelectComboBoxRenderer());
        setSelectionBackground(getBackground());
        setSelectionForeground(getForeground());
        synchAllToPopup();
        setSelectionModeIsMulti(true);
    }

    @Override
    public void updateUI() {
        setUI(new MultiSelectComboBoxUI());

        setSelectionBackground(selectionBackground);
        setSelectionForeground(selectionForeground);
        setPopupBackground(popupBackground);
        setPopupForeground(popupForeground);
        setPopupBorder(popupBorder);

        ListCellRenderer<?> renderer = getRenderer();
        if (renderer instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component) renderer);
        }
    }

    /**
     * 设置true为多选模式,false为单选模式
     *
     * @param isMulti
     *            是否多选
     */
    public void setSelectionModeIsMulti(boolean isMulti) {
        if (isMulti) {
            getPopup().getList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            getPopup().getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    /**
     * 返回true为多选模式,false为单选模式
     *
     * @return
     */
    public boolean getSelectionModeIsMulti() {
        return getPopup().getList().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    }

    /**
     * 添加Popup事件
     */
    public void addPopupMenuListener(PopupMenuListener l) {
        getPopup().addPopupMenuListener(l);
    }

    @Override
    public void setSelectedItem(Object anObject) {
        super.setSelectedItem(anObject);
        if (anObject == null) {
            clearSelectedIndexs();
        } else {
            boolean found = false;
            for (int i = 0; i < dataModel.getSize(); i++) {
                E element = dataModel.getElementAt(i);
                if (anObject.equals(element)) {
                    found = true;
                    addSelectedIndex(i);
                    break;
                }
            }
            if (!found) {
                clearSelectedIndexs();
            }
        }
    }

    public boolean addSelectedIndex(Integer index) {
        return selectedIndexs.add(index);
    }

    public boolean isSelected(Integer index) {
        return selectedIndexs.contains(index);
    }

    public boolean removeSelectedIndex(Integer index) {
        return selectedIndexs.remove(index);
    }

    public void clearSelectedIndexs() {
        selectedIndexs.clear();
    }

    /**
     * 获得选择项的索引
     *
     * @return
     */
    public Set<Integer> getSelectedIndexs() {
        return selectedIndexs;
    }

    /**
     * 获得已经排序后的选择项的索引
     *
     * @return
     */
    public List<Integer> getSelectedSortedIndexs() {
        List<Integer> list = new ArrayList<>(getSelectedIndexs());
        Collections.sort(list);
        return list;
    }

    /**
     * 获取选择的值
     *
     * @return
     */
    public List<E> getSelectedItems() {
        List<E> list = new ArrayList<>();
        for (Integer index : getSelectedSortedIndexs()) {
            list.add(getModel().getElementAt(index));
        }
        return list;
    }

    /**
     * 返回选择的值，用','拼接的字符串
     *
     * @return
     */
    public String getSelectedItemsString() {
        List<String> list = new ArrayList<>();
        for (Integer index : getSelectedSortedIndexs()) {
            E elementAt = getModel().getElementAt(index);
            list.add(elementAt == null ? "" : elementAt.toString());
        }
        return spliceCollectionValue(list, spliceRegex);
    }

    public String getSpliceRegex() {
        return spliceRegex;
    }

    public void setSpliceRegex(String spliceRegex) {
        this.spliceRegex = spliceRegex;
    }

    /**
     * 用指定字符串将一个字符串数组拼接成一个字符串
     */
    public static String spliceArrayValue(Object[] strs, String regex) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        for (Object temp : strs) {
            if (temp == null) {
                temp = "";
            }
            buffer.append(temp);
            buffer.append(regex);
        }
        int lastRegexIndex = buffer.lastIndexOf(regex);
        if (lastRegexIndex >= 0) {
            buffer.delete(lastRegexIndex, buffer.length());// 删除最后一个","
        }
        return buffer.toString();
    }

    /**
     * 用指定字符串将一个字符串集合拼接成一个字符串
     */
    public static String spliceCollectionValue(Collection strs, String regex) {
        if (strs == null || strs.isEmpty()) {
            return "";
        }
        return spliceArrayValue(strs.toArray(new Object[strs.size()]), regex);
    }

    public static class MultiSelectComboBoxUI extends CustomComboBoxUI {

        protected JLabel currentValueComponent = new JLabel();

        /**
         * Paints the currently selected item.
         */
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            currentValueComponent.setText(((MultiSelectComboBox) comboBox).getSelectedItemsString());
            currentValueComponent.setBackground(comboBox.getBackground());// 清除渲染器原来设置的背景色,将渲染器的背景设置成ComboBox的背景色
            currentValueComponent.setForeground(comboBox.getForeground());// 清除渲染器原来设置的前景色,将渲染器的前景设置成ComboBox的前景色
            currentValueComponent.setFont(comboBox.getFont());
            currentValueComponent.setOpaque(comboBox.isOpaque());
            if (comboBox.getRenderer() instanceof JComponent) {
                JComponent r = (JComponent) comboBox.getRenderer();
                currentValueComponent.setBorder(r.getBorder());
            }

            int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
            if (padding != null) {
                x = bounds.x + padding.left;
                y = bounds.y + padding.top;
                w = bounds.width - (padding.left + padding.right);
                h = bounds.height - (padding.top + padding.bottom);
            }

            currentValuePane.paintComponent(g, currentValueComponent, comboBox, x, y, w, h, false);
        }

        @Override
        protected ListCellRenderer<?> createRenderer() {
            return new MultiSelectComboBoxRenderer();
        }

        @Override
        protected ComboPopup createPopup() {
            return new MultiSelectComboBoxPopup(comboBox);
        }

        public class MultiSelectComboBoxPopup extends BasicComboPopup {

            public MultiSelectComboBoxPopup(JComboBox<?> cBox) {
                super(cBox);
            }

            @Override
            protected JList createList() {
                return new CustomList<>(comboBox.getModel());
            }

            @Override
            protected MouseListener createListMouseListener() {
                return new MouseAdapter() {
                    public void mousePressed(MouseEvent anEvent) {
                        int index = list.getSelectedIndex();
                        MultiSelectComboBox<?> multiComboBox = (MultiSelectComboBox<?>) comboBox;
                        if (list.getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
                            if (multiComboBox.isSelected(index)) {
                                multiComboBox.removeSelectedIndex(index);
                            } else {
                                multiComboBox.addSelectedIndex(index);
                            }
                        } else {
                            if (!multiComboBox.isSelected(index)) {
                                multiComboBox.clearSelectedIndexs();
                                multiComboBox.addSelectedIndex(index);
                            }
                        }
                        updateListBoxSelectionForEvent(anEvent, false);
                        comboBox.repaint();
                        list.repaint();
                    }

                    public void mouseReleased(MouseEvent anEvent) {
                        if (!(list.getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)) {
                            comboBox.setPopupVisible(false);
                        }
                    }
                };
            }

            @Override
            protected void configureList() {
                super.configureList();
                list.setSelectionModel(new DefaultListSelectionModel() {
                    public boolean isSelectedIndex(int index) {
                        return ((MultiSelectComboBox<?>) comboBox).isSelected(index);
                    }
                });
            }
        }
    }

    public static class MultiSelectComboBoxRenderer extends JCheckBox implements ListCellRenderer<Object>, Serializable {

        private Border rendererBorder = BorderFactory.createEmptyBorder(0, 1, 0, 0);

        public MultiSelectComboBoxRenderer() {
            setOpaque(true);
            setBorder(rendererBorder);

            // 如果觉得默认的CheckBox图标不好看可以直接设置:
            // setIcon(defaultIcon);
            // setSelectedIcon(selectedIcon)
            // setPressedIcon(pressedIcon)
            // setRolloverEnabled(true);
            // setRolloverIcon(rolloverIcon)
            // setRolloverSelectedIcon(rolloverSelectedIcon)
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setComponentOrientation(list.getComponentOrientation());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setEnabled(list.isEnabled());
            setSelected(isSelected);
            setText(value == null ? "" : value.toString());
            setFont(list.getFont());

            return this;
        }

        public Border getRendererBorder() {
            return rendererBorder;
        }

        public void setRendererBorder(Border rendererBorder) {
            this.rendererBorder = rendererBorder;
            setBorder(rendererBorder);
        }
    }
}
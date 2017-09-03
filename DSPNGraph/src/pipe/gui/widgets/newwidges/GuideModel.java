package pipe.gui.widgets.newwidges;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by hanson on 2017/8/22.
 */
public class GuideModel {
    private Integer typeOfBus = 0;//类型为AFDX，为了代码重用和以后的扩展
    private Integer numOfSPM = 1;//SPM终端的数量--假设有一个先
    private Integer numOfDU = 1;//DU终端的数量--假设有一个先
    private Integer numOfSW = 1;//SW的数量--假设有一个先

    private Queue<VLInfo> vlList;
    private Queue<SPM> spmList;
    private Queue<SW> swList;
    public final static int THE_AFDX = 101;
    public final static int EVENT_MESSAGE = 301;
    public final static int PERIOD_MESSAGE = 302;
    public static boolean FLAG = true;
    public static int swCount = 1;

    public static int INDEXOFSW = 0;


    public Integer getNumOfSW() {
        return numOfSW;
    }

    public void setNumOfSW(Integer numOfSW) {
        this.numOfSW = numOfSW;
        swCount = numOfSW;
    }

    public Integer getNumOfDU() {
        return numOfDU;
    }

    public void setNumOfDU(Integer numOfDU) {
        this.numOfDU = numOfDU;
    }



    public GuideModel() {
        vlList = new LinkedList<VLInfo>();
        spmList = new LinkedList<SPM>();
    }

    public Queue<VLInfo> getVlList() {
        return vlList;
    }

    public void setVlList(Queue<VLInfo> vlList) {
        this.vlList = vlList;
    }





    public Queue<SPM> getSpmList() {
        return spmList;
    }

    public void setSpmList(Queue<SPM> spmList) {
        this.spmList = spmList;
    }

    public Integer getNumOfSPM() {
        return numOfSPM;
    }

    public void setNumOfSPM(Integer numOfSPM) {
        this.numOfSPM = numOfSPM;
    }

    public Integer getTypeOfBus() {
        return typeOfBus;
    }

    public void setTypeOfBus(Integer typeOfBus) {
        this.typeOfBus = typeOfBus;
    }
}

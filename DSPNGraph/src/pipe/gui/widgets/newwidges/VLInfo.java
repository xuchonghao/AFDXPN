package pipe.gui.widgets.newwidges;

/**
 * Created by hanson on 2017/8/17.
 */

import java.util.ArrayList;

/**该类为虚链路的各种参数的组合，用于构成一条虚链路,用于传递各种信息*/
public class VLInfo {

    private String vlId;
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<String> getDestination() {
        return destination;
    }

    public void setDestination(ArrayList<String> destination) {
        this.destination = destination;
    }

    private ArrayList<String> destination;
    private String vlNumber;//链路号

    private int packageSize;//包大小
    private Boolean periodical;//是否为周期消息
    private double BAG;
    private double transCycle;//传输周期
    private int cacheSize;


    public VLInfo(String id, String vlNumber) {
        this.vlId = id;
        this.vlNumber = vlNumber;
    }

    public VLInfo() {

    }

    public String getVlId() {
        return vlId;
    }

    public void setVlId(String vlId) {
        this.vlId = vlId;
    }


    public String getVlNumber() {
        return vlNumber;
    }

    public void setVlNumber(String vlNumber) {
        this.vlNumber = vlNumber;
    }


    public Boolean getPeriodical() {
        return periodical;
    }

    public void setPeriodical(Boolean periodical) {
        this.periodical = periodical;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public double getBAG() {
        return BAG;
    }

    public void setBAG(double BAG) {
        this.BAG = BAG;
    }

    public double getTransCycle() {
        return transCycle;
    }

    public void setTransCycle(double transCycle) {
        this.transCycle = transCycle;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

}

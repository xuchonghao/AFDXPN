package pipe.gui.widgets.newwidges;

import java.util.ArrayList;

/**
 * Created by hanson on 2017/8/22.
 */
public class SPM {
    private String id;
    private double lxPeriod;

    public double getLxPeriod() {
        return lxPeriod;
    }

    public void setLxPeriod(double lxPeriod) {
        this.lxPeriod = lxPeriod;
    }

    private Paratition PARTITION1;
    private Paratition PARTITION2;
    private Paratition PARTITION3;

    private int par1VLNum;
    private int par2VLNum;
    private int par3VLNum;

    public SPM() {
        PARTITION1 = new Paratition();
        PARTITION2 = new Paratition();
        PARTITION3 = new Paratition();
        par1VLNum = 0;
        par2VLNum = 0;
        par3VLNum = 0;
    }

    public int getPar1VLNum() {
        return par1VLNum;
    }

    public void setPar1VLNum(int par1VLNum) {
        this.par1VLNum = par1VLNum;
    }

    public int getPar2VLNum() {
        return par2VLNum;
    }

    public void setPar2VLNum(int par2VLNum) {
        this.par2VLNum = par2VLNum;
    }

    public int getPar3VLNum() {
        return par3VLNum;
    }

    public void setPar3VLNum(int par3VLNum) {
        this.par3VLNum = par3VLNum;
    }

    public SPM(String id, Paratition PARTITION1, Paratition PARTITION2, Paratition PARTITION3) {
        this.id = id;
        PARTITION1 = new Paratition();
        PARTITION2 = new Paratition();
        PARTITION3 = new Paratition();
        par1VLNum = 0;
        par2VLNum = 0;
        par3VLNum = 0;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Paratition getPARTITION1() {
        return PARTITION1;
    }

    public void setPARTITION1(Paratition PARTITION1) {
        this.PARTITION1 = PARTITION1;
    }

    public Paratition getPARTITION2() {
        return PARTITION2;
    }

    public void setPARTITION2(Paratition PARTITION2) {
        this.PARTITION2 = PARTITION2;
    }

    public Paratition getPARTITION3() {
        return PARTITION3;
    }

    public void setPARTITION3(Paratition PARTITION3) {
        this.PARTITION3 = PARTITION3;
    }
}



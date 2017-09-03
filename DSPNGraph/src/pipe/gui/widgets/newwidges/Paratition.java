package pipe.gui.widgets.newwidges;

import java.util.ArrayList;

/**
 * Created by hanson on 2017/8/22.
 */
public class Paratition {
        private String parId;
        //private int VLCount;//链路数量
        private ArrayList<VLInfo> parititionVLInfo;

        public Paratition() {
            parititionVLInfo = new ArrayList<VLInfo>();
        }
        //public Paratition(int VLCount,String parId) {
        public Paratition(String parId) {
            this.parId = parId;
            parititionVLInfo = new ArrayList<VLInfo>();
        }

    public ArrayList<VLInfo> getParititionVLInfo() {
        return parititionVLInfo;
    }

    public void setParititionVLInfo(ArrayList<VLInfo> parititionVLInfo) {
        this.parititionVLInfo = parititionVLInfo;
    }

    public String getParId() {

        return parId;
    }

    public void setParId(String parId) {
        this.parId = parId;
    }

}

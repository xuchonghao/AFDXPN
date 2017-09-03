package pipe.gui.widgets.newwidges;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 根据给定的GuideVO，来生成一个xml文件，是对应的模型，打算用JDOM
 * @author Wang-wang   这个为了赶时间 写的超级丑陋  下次重构下吧
 *
 */
public class ModelFactory2 {

    public static int index = 1;
    private static Element  pnml, net;
    public static ArrayList<Element> buildHead()
    {
        Element labelText;

        pnml = new Element("pnml");
        net = new Element("net");
        //tokenclass = new Element("token");
        //place = new Element("place");
        //transition = new Element("transition");
        //arc = new Element("arc");

        Element label = new Element("labels");
        label.setAttribute("x", "20");
        label.setAttribute("y", "20");
        label.setAttribute("width", "104");
        label.setAttribute("height", "20");
        label.setAttribute("border", "true");

        labelText = new Element("text");
        labelText.addContent("Petri Model");
        label.addContent(labelText);
        net.setAttribute("id", "Net-One1");
        net.setAttribute("type", "P/T net");

        Element tokenClass = new Element("token");
        tokenClass.setAttribute("id","Black");
        tokenClass.setAttribute("enabled","true");
        tokenClass.setAttribute("red","0");
        tokenClass.setAttribute("green","0");
        tokenClass.setAttribute("blue","0");
        net.addContent(tokenClass);

        tokenClass = new Element("token");
        tokenClass.setAttribute("id","Red");
        tokenClass.setAttribute("enabled","true");
        tokenClass.setAttribute("red","255");
        tokenClass.setAttribute("green","0");
        tokenClass.setAttribute("blue","0");
        net.addContent(tokenClass);

        tokenClass = new Element("token");
        tokenClass.setAttribute("id","Green");
        tokenClass.setAttribute("enabled","true");
        tokenClass.setAttribute("red","0");
        tokenClass.setAttribute("green","255");
        tokenClass.setAttribute("blue","0");
        net.addContent(tokenClass);
        net.addContent(label);
        ArrayList<Element> arr = new ArrayList<Element>();
        arr.add(pnml);
        arr.add(net);
        return arr;
    }
    public static String buildModel(GuideModel guideModel,ArrayList<Element> arr)
    {
        Element pnml = arr.get(0);
        Element net = arr.get(1);

        pnml.addContent(net);

        Document doc = new Document(pnml);
        Format format = Format.getCompactFormat();
        format.setEncoding("iso-8859-1");           //设置xml文件的字符为iso-8859-1
        format.setIndent("    ");               //设置xml文件的缩进为4个空格

        String randomStr = "系统Petri网分析模型"+index++;
        System.out.println(randomStr);


        //String path = "d://"+randomStr+".xml";
//        String path = "c://AFDX_3_PRIORITY.xml";
        String path = "//home//xuchonghao//IntelliJ_workspace//"+randomStr+".xml";
        XMLOutputter XMLOut = new XMLOutputter(format);//在元素后换行，每一层元素缩排四格
        try {
//       XMLOut.output(doc, new FileOutputStream("C://Users//Administrator//Desktop//test1234.xml"));
            System.out.println("@@@@@@@@@@@@@@@@@2"+path);
            XMLOut.output(doc, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回的是生成模型的路径
        listOfPlacesInRtEnd = new LinkedList<Element>();
        listOfScheduleStartTransition = new LinkedList<Element>();
        pIndex=0;
        tIndex=0;
        return path;
    }

    private static void addArc(Element source, Element target,  Element net , int times , boolean isInhibitor,boolean curvePoint) {
        //是上面的数字 一般是1  默认是1
        Element arc1 = new Element("arc");
        arc1.setAttribute("id", ""+source.getAttributeValue("id")+" to "+target.getAttributeValue("id"));
        arc1.setAttribute("source",source.getAttributeValue("id"));
        arc1.setAttribute("target",target.getAttributeValue("id"));
        Element graphics = new Element("graphics");
        arc1.addContent(graphics);

        Element inscription = new Element("inscription");
        Element value = new Element("value");

        String str ="Black,"+times;
        value.setText(str);
        inscription.addContent(value);
        graphics = new Element("graphics");
        inscription.addContent(graphics);
        arc1.addContent(inscription);

        Element tagged = new Element("tagged");
        value = new Element("value");
        str ="false";
        value.setText(str);
        tagged.addContent(value);
        arc1.addContent(tagged);

        //TODO 根据弧是否curve来改变它的arcpath
        Element arcpath = new Element("arcpath");
        arcpath.setAttribute("id", "000");
        arcpath.setAttribute("x", "000");
        arcpath.setAttribute("y", "000");
        arcpath.setAttribute("curvePoint", "false");


        arc1.addContent(arcpath);
/*
        if(curvePoint){
            arcpath = new Element("arcpath");
            arcpath.setAttribute("id", "001");
            arcpath.setAttribute("x", "000");
            arcpath.setAttribute("y", "000");
            arcpath.setAttribute("curvePoint", "true");
            arc1.addContent(arcpath);
            System.out.println(source.getAttributeValue("x"));
            System.out.println(Integer.parseInt(source.getAttributeValue("x")));
            Attribute postion =  source.getAttribute("position");

            double zx = (Integer.parseInt(postion.getAttributeValue("x"))+Integer.parseInt(source.getAttributeValue("id")))/2;
            double zy = Integer.parseInt(source.getAttributeValue("y"))+8;
            arcpath = new Element("arcpath");
            arcpath.setAttribute("id", "002");
            arcpath.setAttribute("x", String.valueOf(zx));
            arcpath.setAttribute("y", String.valueOf(zy));
            arcpath.setAttribute("curvePoint", "true");
            arc1.addContent(arcpath);
        }else{
            arcpath = new Element("arcpath");
            arcpath.setAttribute("id", "001");
            arcpath.setAttribute("x", "000");
            arcpath.setAttribute("y", "000");
            arcpath.setAttribute("curvePoint", "false");
            arc1.addContent(arcpath);
        }*/
        arcpath = new Element("arcpath");
        arcpath.setAttribute("id", "001");
        arcpath.setAttribute("x", "000");
        arcpath.setAttribute("y", "000");
        arcpath.setAttribute("curvePoint", "false");
        arc1.addContent(arcpath);


        Element type = new Element("type");
        if(isInhibitor==false)
            type.setAttribute("value", "normal");
        else
            type.setAttribute("value", "inhibitor");
        arc1.addContent(type);
        net.addContent(arc1);

    }

    public static int getVLNUMBER() {
        return VLNUMBER;
    }

    public static void setVLNUMBER(int VLNUMBER) {
        ModelFactory2.VLNUMBER = VLNUMBER;
    }

    private static int VLNUMBER = 0;
    public static void addDU(GuideModel guideModel,Element net,String duName,int x,int y,String spmId,Element swLinkEnd){
        //Element linkEndElement = addLink(net,x,y-200,"S",place);
        //spmId = "DU"+ (duIndex++) + "_";
        spmId += "_";
        String placeId0 = spmId + "InQueue";
        Element place0 = buildPlace(net, placeId0, 1, x + 420, y);
        addArc(swLinkEnd,place0,net,1,false,false);

        Queue<VLInfo> vlList = guideModel.getVlList();
        int len = vlList.size();
        int k = 0;
        int d = 0;
        while(k < len){
            VLInfo info = vlList.remove();
            ArrayList<String> des = info.getDestination();
            String sour = info.getSource().substring(0,4);//SPM1
            for(int t = 0;t<des.size();t++){
                if(des.get(t).equals(duName)){//添加该目的地的虚链路
                //spmId +=  sour;

                addRecVL(net,x+420,y+d*60,spmId,sour,place0);
                d++;
            }
            }

            k++;
            vlList.add(info);
        }

    }
    private static void addRecVL( Element net,int x,int y,String spmId,String sourId,Element startElement){
        spmId += sourId + "_VL"+(reVlIndex++) + "_";

        String transitionId0 = spmId + "RM";
        Element transition0 = buildTransition(net,transitionId0,1,5,0,x+190,y);

        String placeId0 = spmId + "Rec-Port";
        Element place0 = buildPlace(net, placeId0, 1, x+380, y);

        String transitionId1 = spmId + "TenchLatency";
        Element transition1 = buildTransition(net,transitionId1,2,0.15,0,x+570,y);

        String placeId1 = spmId + "Receiption";
        Element place1 = buildPlace(net, placeId1, 1, x+760, y);

        addArc(startElement,transition0,net,1,false,false);
        addArc(transition0,place0,net,1,false,false);
        addArc(place0,transition1,net,1,false,false);
        addArc(transition1,place1,net,1,false,false);
    }
    public static void addSPM(GuideModel guideModel, ArrayList<Element> arr){
        Element net = arr.get(1);
        int x,y;
        int prelen = 1;
        Element linkEndElement = null;
         ArrayList<Integer> lMax = new ArrayList<>();
        for(int i=0; i<guideModel.getSpmList().size(); i++)
        {
            x = 180;
            y = 45 + i * 1800;
            SPM spm = guideModel.getSpmList().remove();
            double lxPeriod = spm.getLxPeriod();
            int p1Len = spm.getPar1VLNum();
            int p2Len = spm.getPar2VLNum();
            int p3Len = spm.getPar3VLNum();

            int len = p1Len + p2Len + p3Len;
            if(p1Len == 2){
                addParVLs2(guideModel, net,p1Len,x, y,lxPeriod);
            }else{
                addParVLs1(guideModel, net,p1Len,x, y,lxPeriod);
            }
             if(p2Len == 2){
                addParVLs2(guideModel, net,p2Len,x, y+700,lxPeriod);
            }else{
                addParVLs1(guideModel, net,p2Len,x, y+700,lxPeriod);
            }
             if(p3Len == 2){
                addParVLs2(guideModel, net,p3Len,x, y+1400,lxPeriod);
            }else{
                addParVLs1(guideModel, net,p3Len,x, y+1400,lxPeriod);
            }
           /* if(p1Len ==2 || p2Len == 2 || p3Len ==2 &&(p1Len<=2 && p2Len<=2 && p3Len<=2)) {//若某分区有两条VL

                addParVLs2(guideModel, net,p1Len,x, y,lxPeriod);
                addParVLs2(guideModel, net,p2Len,x, y + 700,lxPeriod);
                addParVLs2(guideModel, net,p3Len,x, y + 1400,lxPeriod);
            }
            else{
               // x = 180;
                //y = 45+i*prelen*500;
                //addParVLs(guideModel, net, len, x, y);

                addParVLs1(guideModel, net,p1Len,x, y,lxPeriod);
                addParVLs1(guideModel, net,p2Len,x, y + 700,lxPeriod);
                addParVLs1(guideModel, net,p3Len,x, y + 1400,lxPeriod);

            }*/
            prelen = len;

            //增加最后的库所和弧
            String spmId = spm.getId();
            String placeId = spmId + "TransM";
            Element place = buildPlace(net, placeId, 1, 1200, 450+i*len*400);

            //int j = len - 1;
            while(!listOfTranstionVLEnd.isEmpty()){
                Element e = listOfTranstionVLEnd.remove();
                addArc(e,place,net,1,false,false);
            }

            Paratition p1 = spm.getPARTITION1();
            Paratition p2 = spm.getPARTITION2();
            Paratition p3 = spm.getPARTITION3();
            ArrayList<VLInfo> l1 = p1.getParititionVLInfo();
            ArrayList<VLInfo> l2 = p2.getParititionVLInfo();
            ArrayList<VLInfo> l3 = p3.getParititionVLInfo();
            int len1 = l1.size();
            int len2 = l2.size();
            int len3 = l3.size();

            for(int j=0;j<len1;j++){
                VLInfo info = l1.get(j);
                lMax.add(info.getPackageSize());
            }

            for(int j=0;j<len2;j++){
                VLInfo info = l2.get(j);
                lMax.add(info.getPackageSize());
            }
            for(int j=0;j<len3;j++){
                VLInfo info = l3.get(j);
                lMax.add(info.getPackageSize());
            }
            //构建SPM后面的链路
            linkEndElement = addSPMLink(net,1200,450+i*len*400,spmId+"L",place,lMax);
            listOfSpmLinkEnd.add(linkEndElement);

            guideModel.getSpmList().add(spm);
        }
        arr.set(1,net);//TODO 这里还用返回吗？引用类型在此处应该就已经修改了，应该不用返回！？！？
    }
    private static void addParVLs2(GuideModel guideModel, Element net,int parLen,int x,int y,double lxPeriod){
        Queue<VLInfo> list = new LinkedList<VLInfo>();
        Queue<VLInfo> vlList = guideModel.getVlList();

        for(int i=0;i<VLNUMBER; i++){//
            VLInfo info = vlList.remove();
            list.add(info);
        }
        if(parLen == 2){
            VLInfo info1 = vlList.remove();
            VLInfo info2 = vlList.remove();
            addVL2(info1,info2,net,x, y); //180   45+i*400
            list.add(info1);
            list.add(info1);

        }/*else{
            for(int i=0; i<parLen; i++)
            {
                VLInfo info3 = vlList.remove();
                addVL (info3,net,x, y + i*400); //180   45+i*400
                list.add(info3);
            }
        }*/

        VLNUMBER += parLen;
        while(!vlList.isEmpty()){
            VLInfo info = guideModel.getVlList().remove();
            list.add(info);
        }
        guideModel.setVlList(list);
    }

     private static Element addSPMLink( Element net,int x,int y,String spmId,Element startElement,ArrayList<Integer> lMax ){
        //TODO 下面的period需要重新填充
        int len = lMax.size();
         int packageSize = 0;
        for(int i=0;i<len;i++){
         packageSize += lMax.get(i);

        }
        double t = packageSize/len*8/(Math.pow(10,5));
        spmId += (linkIndex++)+"_";
        String transitionId0 = spmId + "StartTrans";
        Element transition0 = buildTransition(net,transitionId0,0,0,0,x+110,y);

        String placeId0 = spmId + "LinkIdle";
        Element place0 = buildPlace(net, placeId0, 1, x+220, y+100);

        String placeId1 = spmId + "LinkBusy";
        Element place1 = buildPlace(net, placeId1, 0, x+220, y-100);

        String transitionId1 = spmId + "EndTrans";
        Element transition1 = buildTransition(net,transitionId1,2,t,0,x+330,y);

        addArc(startElement,transition0,net,1,false,false);
        addArc(place0,transition0,net,1,false,false);
        addArc(transition0,place1,net,1,false,false);
        addArc(place1,transition1,net,1,false,false);
        addArc(transition1,place0,net,1,false,false);
        return transition1;
    }

    private static Element addLink( Element net,int x,int y,String spmId,Element startElement){
        //TODO 下面的period需要重新填充
        spmId += (linkIndex++)+"_";
        String transitionId0 = spmId + "StartTrans";
        Element transition0 = buildTransition(net,transitionId0,0,0,0,x+110,y);

        String placeId0 = spmId + "LinkIdle";
        Element place0 = buildPlace(net, placeId0, 1, x+220, y+100);

        String placeId1 = spmId + "LinkBusy";
        Element place1 = buildPlace(net, placeId1, 0, x+220, y-100);

        String transitionId1 = spmId + "EndTrans";
        Element transition1 = buildTransition(net,transitionId1,2,0,0,x+330,y);

        addArc(startElement,transition0,net,1,false,false);
        addArc(place0,transition0,net,1,false,false);
        addArc(transition0,place1,net,1,false,false);
        addArc(place1,transition1,net,1,false,false);
        addArc(transition1,place0,net,1,false,false);
        return transition1;
    }

    public static ArrayList<Element> addSwitch(Element net,int x,int y,String spmId,int outNum,ArrayList<Element> listOfSpmLinkEnd){
        ArrayList<Element> listOfSWLinkEnd = new ArrayList<Element>();

        //spmId += (swIndex++) + "_";
        spmId += "_";
        String placeIdx = spmId + "OutQueue";
        Element placex = buildPlace(net, placeIdx, 0, x+220, y);

        int len = listOfSpmLinkEnd.size();
        int j = 0;
        //while(!listOfSpmLinkEnd.isEmpty()){
        while(j < len){
            //System.out.println("J= :"+ j);
            String placeId = spmId + "Inport_" + j;
            Element place = buildPlace(net, placeId, 0, x, y+j*300);

            //Element e = listOfSpmLinkEnd.remove(j);
            Element e = listOfSpmLinkEnd.get(j);

            addArc(e,place,net,1,false,false);

            String transitionId = spmId + "Forward_" + j;
            Element transition = buildTransition(net,transitionId,2,0.016,0,x+110,y+j*300);
            addArc(place,transition,net,1,false,false);
            addArc(transition,placex,net,1,false,false);
            j++;
        }


        //TODO 下面的period需要重新填充
        String transitionId0 = spmId + "Jitter";
        Element transition0 = buildTransition(net,transitionId0,1,0,0,x+330,y);

        String placeId0 = spmId + "Wait";
        Element place0 = buildPlace(net, placeId0, 1, x+440, y+100);

        String placeId1 = spmId + "Schedule";
        Element place1 = buildPlace(net, placeId1, 0, x+440, y-100);

        String transitionId1 = spmId + "techLatency";
        Element transition1 = buildTransition(net,transitionId1,2,0.084,0,x+550,y);

        String placeId2 = spmId + "sendM_";
        Element place2 = buildPlace(net, placeId2, 0, x+660, y);
        addArc(transition1,place2,net,1,false,false);

       j = 0;
        while(j < outNum) {
           // String placeId2 = spmId + "sendM_" + j;
            //Element place2 = buildPlace(net, placeId2, 0, x+660, 800);
           // addArc(transition1,place2,net,1,false,false);//TODO 不用改
            Element linkEndElement = addLink(net,x + 770,y-300+j*500,spmId+"L",place2);

            listOfSWLinkEnd.add(linkEndElement);
            j++;
        }

        addArc(placex,transition0,net,1,false,false);
        addArc(place0,transition0,net,1,false,false);
        addArc(transition0,place1,net,1,false,false);
        addArc(place1,transition1,net,1,false,false);
        addArc(transition1,place0,net,1,false,false);
        //addArc(transition1,place2,net,1,false,false);
        //添加链路，交换机后肯定有链路
        return listOfSWLinkEnd;
    }

    /*
     public static void addSPM(GuideModel guideModel, ArrayList<Element> arr){
        Element net = arr.get(1);
        int x,y;
        int prelen = 1;
        Element linkEndElement = null;
        for(int i=0; i<guideModel.getSpmList().size(); i++)
        {
            SPM spm = guideModel.getSpmList().remove();
            int p1 = spm.getPar1VLNum();

            int p2 = spm.getPar2VLNum();
            int p3 = spm.getPar3VLNum();
            int len = p1 + p2 + p3;

            x = 180;
            y = 45+i*prelen*400;
            if(p1 ==1 && p2 == 1 && p3 ==1)
                addParVLs(guideModel, net,len,x, y);

            prelen = len;

            //增加最后的库所和弧
            String spmId = spm.getId();
            String placeId = spmId + "TransM";
            Element place = buildPlace(net, placeId, 1, 1200, 450+i*len*400);

            int j = len - 1;
            while(!listOfTranstionVLEnd.isEmpty()){
                Element e = listOfTranstionVLEnd.remove(j--);
                addArc(e,place,net,1,false,false);
            }

            //构建SPM后面的链路
            linkEndElement = addLink(net,1200,450+i*len*400,spmId+"L",place);
            listOfSpmLinkEnd.add(linkEndElement);

            guideModel.getSpmList().add(spm);
        }
        arr.set(1,net);//TODO 这里还用返回吗？引用类型在此处应该就已经修改了，应该不用返回！？！？
    }*/
   /*
   private static void addParVLs(GuideModel guideModel, Element net,int len,int x,int y){
        Queue<VLInfo> list = new LinkedList<VLInfo>();
        Queue<VLInfo> vlList = guideModel.getVlList();
       // System.out.println("VLNUMBER:"+VLNUMBER);
       // System.out.println("len:"+len);
        for(int i=0;i<VLNUMBER; i++){//
            VLInfo info = vlList.remove();
            list.add(info);
        }
        for(int i=0; i<len; i++)
        {
            VLInfo info = vlList.remove();
            addVL (info,net,x, y + i*400); //180   45+i*400
            list.add(info);
        }
        VLNUMBER += len;
        while(!vlList.isEmpty()){
            VLInfo info = guideModel.getVlList().remove();
            list.add(info);
        }
        guideModel.setVlList(list);
    }
    */
    //创建某个分区中的链路 ,缺少参数lx周期

   private static void addParVLs1(GuideModel guideModel, Element net,int len,int x,int y,double lxPeriod){
        Queue<VLInfo> list = new LinkedList<VLInfo>();
        Queue<VLInfo> vlList = guideModel.getVlList();
        ArrayList<VLInfo> infoList = new ArrayList<VLInfo>();
       // System.out.println("VLNUMBER:"+VLNUMBER);
       // System.out.println("len:"+len);
        for(int i=0;i<VLNUMBER; i++){//
            VLInfo info = vlList.remove();
            list.add(info);
        }
        //先取出来一次用于计算,再将队列清空，再取出
       for(int i=0; i<len; i++)
        {
            VLInfo info = vlList.remove();
            //传入该分区链路的数量以便计算权重
            infoList.add(info);
            list.add(info);
        }
       while(!vlList.isEmpty()){
            VLInfo info = guideModel.getVlList().remove();
            list.add(info);
        }
        guideModel.setVlList(list);
               //第二次重新取出
        list = new LinkedList<VLInfo>();
        vlList = guideModel.getVlList();
        for(int i=0;i<VLNUMBER; i++){//
            VLInfo info = vlList.remove();
            list.add(info);
        }

        for(int i=0; i<len; i++)
        {
            VLInfo info = vlList.remove();
            //传入该分区链路的数量以便计算权重
            addVL1(info,net,x, y + i*400,infoList,lxPeriod); //180   45+i*400

            list.add(info);
        }
        VLNUMBER += len;
        while(!vlList.isEmpty()){
            VLInfo info = guideModel.getVlList().remove();
            list.add(info);
        }
        guideModel.setVlList(list);
    }

    private static void addVL2(VLInfo info1,VLInfo info2,Element net,int x,int y){
        Boolean isPeriodical = info1.getPeriodical();
        double transCycle = info1.getTransCycle();
        double bag = info1.getBAG();
        double jitt = 0.04 + (info1.getPackageSize()+20 + info2.getPackageSize()+20) * 8 / (Math.pow(10,5));
        double jittRate = 1 / jitt;
         double techLa = 0.15;
        int cacheSize1 = info1.getCacheSize();
        String vlId = info1.getVlId();


        String placeId0 = vlId + "_Application";
        Element place0 = buildPlace(net, placeId0, 1, x, y);
        String placeId1;
        Element place1;
        String transtionId0 ;
        Element transition0 ;
        if(isPeriodical)
        {
            placeId1 = vlId + "_Sampling_port";
            place1 = buildPlace(net, placeId1, 0, x+315, y);
            transtionId0 = vlId + "_"+"Period";
            // System.out.println("factory de 1017 transCycle周期:" + transCycle);
            transition0 = buildTransition(net, transtionId0, 2, transCycle, 0, x+135, y);
        }else {
            placeId1 = vlId + "_Sampling_port";
            place1 = buildPlace(net, placeId1, 0, x+315, y);
            transtionId0 = vlId + "_"+"EventM";
            transition0 = buildTransition(net, transtionId0, 1, transCycle, 0, x+135, y);
        }


        String placeId2 = vlId + "_Loss";
        Element place2 = buildPlace(net, placeId2, 0, x+630, y);
        String placeId3 = vlId + "_Wait1";
        Element place3 = buildPlace(net, placeId3, 1, x-60, y+165);

        String placeId4 = vlId + "_Wait2";
        Element place4 = buildPlace(net, placeId4, 0, x+225, y+210);
        String placeId5 = vlId + "_Schedule";
        Element place5 = buildPlace(net, placeId5, 0, x+540, y+210);

        String placeId6 = vlId + "_Buffer";
        Element place6 = buildPlace(net, placeId6, cacheSize1, x+735, y+90);

        String placeId7 = vlId + "_Queue";
        Element place7 = buildPlace(net, placeId7, 0, x+735, y+225);



        String transtionId1 = vlId + "_"+"TLoss";
        Element transition1 = buildTransition(net, transtionId1,0, 0, 0, x+465, y);

        // System.out.println("factory de 1046 bag:" + bag);
        String transtionId2 = vlId + "_"+"BAG";
        Element transition2 = buildTransition(net, transtionId2,2, bag, 0,x+60, y+210);

        String transtionId3 = vlId + "_"+"Jitter";
        Element transition3 = buildTransition(net, transtionId3,1 , jittRate, 0, x+390, y+210);

        String transtionId4 = vlId + "_"+"Enqueue";
        Element transition4 = buildTransition(net, transtionId4,0 , 0, 0, x+630, y+150);

        String transtionId5 = vlId + "_"+"TechLatency";
        Element transition5 = buildTransition(net, transtionId5,2 , techLa, 0, x+840, y+150);
        //System.out.println(transtionId5);

        String placeId8 = vlId + "_WaitPeriod";
        Element place8 = buildPlace(net, placeId8, 0, x+500, y+500);

        String transtionId6 = vlId + "_"+"TransPeriod";
        Element transition6 = buildTransition(net, transtionId6,2 , 0, 0, x+700, y+500);

        String placeId9 = vlId + "_Send";
        Element place9 = buildPlace(net, placeId9, 0, x+840, y+500);

        listOfTranstionVLEnd.add(transition5);


        addArc(place8,transition6,net,1,false,true);
        addArc(transition6,place9,net,1,false,true);
        addArc(transition6,place8,net,1,false,true);
        addArc(place9,transition5,net,1,false,true);
        //Element source, Element target,  Element net , int times , boolean isInhibitor
        addArc(place0,transition0,net,1,false,true);
        addArc(transition0,place0,net,1,false,false);
        addArc(transition0,place1,net,1,false,false);
        addArc(place1,transition1,net,1,false,false);
        addArc(place1,transition4,net,1,false,false);
        addArc(transition1,place2,net,1,false,false);

        addArc(place3,transition2,net,1,false,false);
        addArc(transition2,place4,net,1,false,false);
        addArc(place4,transition3,net,1,false,false);
        addArc(transition3,place5,net,1,false,false);
        addArc(place5,transition4,net,1,false,false);
        addArc(transition4,place7,net,1,false,false);
        addArc(place7,transition5,net,1,false,false);
        addArc(transition5,place6,net,1,false,false);
        addArc(place6,transition4,net,1,false,false);
        addArc(place6,transition1,net,1,true,false);
        addArc(transition4,place3,net,1,false,false);

        Boolean isPeriodical2 = info2.getPeriodical();
        double transCycle2 = info2.getTransCycle();
        double bag2 = info2.getBAG();
        //int cacheSize2 = info2.getCacheSize();
        String vlId2 = info2.getVlId();

        String placeId20 = vlId2 + "_Application";
        Element place20 = buildPlace(net, placeId20, 1, x, y+450);
        String placeId21;
        Element place21;
        String transtionId20 ;
        Element transition20 ;
        if(isPeriodical2)
        {
            placeId21 = vlId2 + "_Sampling_port";
            place21 = buildPlace(net, placeId21, 0, x+315, y+450);
            transtionId20 = vlId2 + "_"+"Period";
            // System.out.println("factory de 1017 transCycle周期:" + transCycle);
            transition20 = buildTransition(net, transtionId20, 2, transCycle2, 0, x+135, y+450);
        }else {
            placeId21 = vlId2 + "_Sampling_port";
            place21 = buildPlace(net, placeId21, 0, x+315, y+450);
            transtionId20 = vlId2 + "_"+"EventM";
            transition20 = buildTransition(net, transtionId20, 1, transCycle2, 0, x+135, y+450);
        }


        String placeId22 = vlId2 + "_Loss";
        Element place22 = buildPlace(net, placeId22, 0, x+630, y+450);
        String placeId23 = vlId2 + "_Wait1";
        Element place23 = buildPlace(net, placeId23, 1, x-60, y+350);

        String placeId24 = vlId2 + "_Wait2";
        Element place24 = buildPlace(net, placeId24, 0, x+225, y+300);
        String placeId25 = vlId2 + "_Schedule";
        Element place25 = buildPlace(net, placeId25, 0, x+540, y+300);

        String transtionId21 = vlId2 + "_"+"TLoss";
        Element transition21 = buildTransition(net, transtionId21,0, 0, 0, x+465, y+450);

        // System.out.println("factory de 1046 bag:" + bag);
        String transtionId22 = vlId2 + "_"+"BAG";
        Element transition22 = buildTransition(net, transtionId22,2, bag2, 0,x+60, y+300);
        String transtionId23 = vlId2 + "_"+"Jitter";
        Element transition23 = buildTransition(net, transtionId23,1 , jittRate, 0, x+390, y+300);

        String transtionId24 = vlId2 + "_"+"Enqueue";
        Element transition24 = buildTransition(net, transtionId24,0 , 0, 0, x+630, y+350);

        //System.out.println(transtionId5);

        //listOfTranstionVLEnd.add(transition5);


        //Element source, Element target,  Element net , int times , boolean isInhibitor
        addArc(place20,transition20,net,1,false,true);
        addArc(transition20,place20,net,1,false,false);
        addArc(transition20,place21,net,1,false,false);
        addArc(place21,transition21,net,1,false,false);
        addArc(place21,transition24,net,1,false,false);
        addArc(transition21,place22,net,1,false,false);

        addArc(place23,transition22,net,1,false,false);
        addArc(transition22,place24,net,1,false,false);
        addArc(place24,transition23,net,1,false,false);
        addArc(transition23,place25,net,1,false,false);
        addArc(place25,transition24,net,1,false,false);
        //addArc(transition24,place27,net,1,false,false);
        //addArc(place27,transition25,net,1,false,false);
        //addArc(transition25,place26,net,1,false,false);
        //addArc(place26,transition24,net,1,false,false);
        //addArc(place26,transition21,net,1,true,false);
        addArc(transition24,place23,net,1,false,false);


        addArc(transition24,place7,net,1,false,false);
        //addArc(place7,transition5,net,1,false,false);
        //addArc(transition5,place6,net,1,false,false);
        addArc(place6,transition21,net,1,false,false);
        addArc(place6,transition24,net,1,true,false);
    }
     private static void addVL1(VLInfo info,Element net,int x,int y,ArrayList<VLInfo> infoList,double lxPeriod){
        Boolean isPeriodical = info.getPeriodical();
        double transCycle = info.getTransCycle();
        double bag = info.getBAG();
        int cacheSize = info.getCacheSize();
        String vlId = info.getVlId();

        System.out.println("-----------------------");
         //System.out.println("transCycle:"+transCycle);
         //System.out.println("cacheSize:"+cacheSize);
         System.out.println("lxPeriod"+lxPeriod);

         //最后一个参数用于得到该分区中每条链路的BAG
         int len = infoList.size();
         int j = 0;
         double[] baglist = new double[len];
         double[] packagelist = new double[len];
         while(j < len){
             VLInfo infoi = infoList.remove(j);
             baglist[j] = infoi.getBAG();
             packagelist[j] = infoi.getPackageSize();
             j++;
         }
         //计算weight
         int weight = 0;
         double jitt = 0;
         for(int i=0;i<len;i++){
             weight += Math.floor(lxPeriod/(baglist[i]+1));
             jitt = 0.04 + (packagelist[i]+20) * 8 / (Math.pow(10,5));
         }
         if(weight==0)
             weight = 1;
        double jittRate = 1 / (jitt/1000);//单位得化成s
         double techLa = 0.15;

        String placeId0 = vlId + "_Application";
        Element place0 = buildPlace(net, placeId0, 1, x, y);
        String placeId1;
        Element place1;
        String transtionId0 ;
        Element transition0 ;
        if(isPeriodical)
        {
           placeId1 = vlId + "_Sampling_port";
           place1 = buildPlace(net, placeId1, 0, x+315, y);
           transtionId0 = vlId + "_"+"Period";
           // System.out.println("factory de 1017 transCycle周期:" + transCycle);
           transition0 = buildTransition(net, transtionId0, 2, transCycle, 0, x+135, y);
        }else {
            placeId1 = vlId + "_Sampling_port";
            place1 = buildPlace(net, placeId1, 0, x+315, y);
            transtionId0 = vlId + "_"+"EventM";
            transition0 = buildTransition(net, transtionId0, 1, transCycle, 0, x+135, y);
        }


        String placeId2 = vlId + "_Loss";
        Element place2 = buildPlace(net, placeId2, 0, x+630, y);
        String placeId3 = vlId + "_Wait1";
        Element place3 = buildPlace(net, placeId3, 1, x-60, y+165);

        String placeId4 = vlId + "_Wait2";
        Element place4 = buildPlace(net, placeId4, 0, x+225, y+210);
        String placeId5 = vlId + "_Schedule";
        Element place5 = buildPlace(net, placeId5, 0, x+540, y+210);

        String placeId6 = vlId + "_Buffer";
        Element place6 = buildPlace(net, placeId6, cacheSize, x+735, y+90);
        String placeId7 = vlId + "_Queue";
        Element place7 = buildPlace(net, placeId7, 0, x+735, y+225);


        String transtionId1 = vlId + "_"+"TLoss";
        Element transition1 = buildTransition(net, transtionId1,0, 0, 0, x+465, y);

       // System.out.println("factory de 1046 bag:" + bag);
        String transtionId2 = vlId + "_"+"VL-BAG";
        Element transition2 = buildTransition(net, transtionId2,2, bag, 0,x+60, y+210);
        String transtionId3 = vlId + "_"+"Jitter";
        Element transition3 = buildTransition(net, transtionId3,1 , jittRate, 0, x+390, y+210);

        String transtionId4 = vlId + "_"+"Enqueue";
        Element transition4 = buildTransition(net, transtionId4,0 , 0, 0, x+630, y+150);

        String transtionId5 = vlId + "_"+"TechLatency";
        Element transition5 = buildTransition(net, transtionId5,2 , techLa, 0, x+840, y+150);
        //System.out.println(transtionId5);

        String placeId8 = vlId + "_WaitPeriod";
        Element place8 = buildPlace(net, placeId8, 0, x+500, y+280);

        String transtionId6 = vlId + "_"+"TransPeriod";
        Element transition6 = buildTransition(net, transtionId6,2 , lxPeriod, 0, x+700, y+280);

        String placeId9 = vlId + "_Send";
        Element place9 = buildPlace(net, placeId9, 0, x+840, y+280);

        listOfTranstionVLEnd.add(transition5);


        addArc(place8,transition6,net,1,false,true);
        addArc(transition6,place9,net,weight,false,true);
        addArc(transition6,place8,net,1,false,true);
        addArc(place9,transition5,net,1,false,true);
        //Element source, Element target,  Element net , int times , boolean isInhibitor
        addArc(place0,transition0,net,1,false,true);
        addArc(transition0,place0,net,1,false,false);
        addArc(transition0,place1,net,1,false,false);
        addArc(place1,transition1,net,1,false,false);
        addArc(place1,transition4,net,1,false,false);
        addArc(transition1,place2,net,1,false,false);

        addArc(place3,transition2,net,1,false,false);
        addArc(transition2,place4,net,1,false,false);
        addArc(place4,transition3,net,1,false,false);
        addArc(transition3,place5,net,1,false,false);
        addArc(place5,transition4,net,1,false,false);
        addArc(transition4,place7,net,1,false,false);
        addArc(place7,transition5,net,1,false,false);
        addArc(transition5,place6,net,1,false,false);
        addArc(place6,transition4,net,1,false,false);
        addArc(place6,transition1,net,1,true,false);
        addArc(transition4,place3,net,1,false,false);

    }
    /*
    private static void addVL(VLInfo info,Element net,int x,int y){
        Boolean isPeriodical = info.getPeriodical();
        double transCycle = info.getTransCycle();
        double bag = info.getBAG();
        double jitt = 0.04 + (info.getPackageSize()+20) * 8 / (Math.pow(10,5));
        double jittRate = 1 / jitt;
        int cacheSize = info.getCacheSize();
        String vlId = info.getVlId();


        String placeId0 = vlId + "_Application";
        Element place0 = buildPlace(net, placeId0, 1, x, y);
        String placeId1;
        Element place1;
        String transtionId0 ;
        Element transition0 ;
        if(isPeriodical)
        {
           placeId1 = vlId + "_Sampling_port";
           place1 = buildPlace(net, placeId1, 0, x+315, y);
           transtionId0 = vlId + "_"+"Period";
           // System.out.println("factory de 1017 transCycle周期:" + transCycle);
           transition0 = buildTransition(net, transtionId0, 2, transCycle, 0, x+135, y);
        }else {
            placeId1 = vlId + "_Sampling_port";
            place1 = buildPlace(net, placeId1, 0, x+315, y);
            transtionId0 = vlId + "_"+"EventM";
            transition0 = buildTransition(net, transtionId0, 1, transCycle, 0, x+135, y);
        }


        String placeId2 = vlId + "_Loss";
        Element place2 = buildPlace(net, placeId2, 0, x+630, y);
        String placeId3 = vlId + "_Wait1";
        Element place3 = buildPlace(net, placeId3, 1, x-60, y+165);

        String placeId4 = vlId + "_Wait2";
        Element place4 = buildPlace(net, placeId4, 0, x+225, y+210);
        String placeId5 = vlId + "_Schedule";
        Element place5 = buildPlace(net, placeId5, 0, x+540, y+210);

        String placeId6 = vlId + "_Buffer";
        Element place6 = buildPlace(net, placeId6, cacheSize, x+735, y+90);
        String placeId7 = vlId + "_Queue";
        Element place7 = buildPlace(net, placeId7, 0, x+735, y+225);


        String transtionId1 = vlId + "_"+"TLoss";
        Element transition1 = buildTransition(net, transtionId1,0, 0, 0, x+465, y);

       // System.out.println("factory de 1046 bag:" + bag);
        String transtionId2 = vlId + "_"+"VL-BAG";
        Element transition2 = buildTransition(net, transtionId2,2, bag, 0,x+60, y+210);
        String transtionId3 = vlId + "_"+"Jitter";
        Element transition3 = buildTransition(net, transtionId3,1 , jittRate, 0, x+390, y+210);

        String transtionId4 = vlId + "_"+"Enqueue";
        Element transition4 = buildTransition(net, transtionId4,0 , 0, 0, x+630, y+150);

        String transtionId5 = vlId + "_"+"TechLatency";
        Element transition5 = buildTransition(net, transtionId5,2 , 0, 0, x+840, y+150);
        //System.out.println(transtionId5);

        String placeId8 = vlId + "_WaitPeriod";
        Element place8 = buildPlace(net, placeId8, 0, x+500, y+280);

        String transtionId6 = vlId + "_"+"TransPeriod";
        Element transition6 = buildTransition(net, transtionId6,2 , 0, 0, x+700, y+280);

        String placeId9 = vlId + "_Send";
        Element place9 = buildPlace(net, placeId9, 0, x+840, y+280);

        listOfTranstionVLEnd.add(transition5);


        addArc(place8,transition6,net,1,false,true);
        addArc(transition6,place9,net,1,false,true);
        addArc(transition6,place8,net,1,false,true);
        addArc(place9,transition5,net,1,false,true);
        //Element source, Element target,  Element net , int times , boolean isInhibitor
        addArc(place0,transition0,net,1,false,true);
        addArc(transition0,place0,net,1,false,false);
        addArc(transition0,place1,net,1,false,false);
        addArc(place1,transition1,net,1,false,false);
        addArc(place1,transition4,net,1,false,false);
        addArc(transition1,place2,net,1,false,false);

        addArc(place3,transition2,net,1,false,false);
        addArc(transition2,place4,net,1,false,false);
        addArc(place4,transition3,net,1,false,false);
        addArc(transition3,place5,net,1,false,false);
        addArc(place5,transition4,net,1,false,false);
        addArc(transition4,place7,net,1,false,false);
        addArc(place7,transition5,net,1,false,false);
        addArc(transition5,place6,net,1,false,false);
        addArc(place6,transition4,net,1,false,false);
        addArc(place6,transition1,net,1,true,false);
        addArc(transition4,place3,net,1,false,false);

    }
*/
    /**
     * 构造一个place 并且加在net上面
     * @param net 整个的网
     * @param placeName  若有名字 给名字，若为空 则用 默认的编号P1 P2等
     * @param initailMark  是否有初始的标记
     * @param x 坐标
     * @param y
     * @return place
     */
    private static Element buildPlace(Element net, String placeName ,int initailMark, int x, int y)
    {
        Element place = new Element("place");
//    place.setAttribute("id", "P"+pIndex);
        if(placeName=="")
            place.setAttribute("id", "P"+pIndex);
        else
            place.setAttribute("id", placeName);

        Element graphics = new Element("graphics");
        Element position = new Element("position");

        position.setAttribute("x", String.valueOf(x));
        position.setAttribute("y", String.valueOf(y));
        graphics.addContent(position);
        place.addContent(graphics);
        Element name = new Element("name");
        Element value = new Element("value");
        value.setText("P"+pIndex++);
        if(placeName=="")
            value.setText("P"+pIndex++);
        else
            value.setText(placeName);
        Element offset = new Element("offset");
        offset.setAttribute("x", "-5.0");
        offset.setAttribute("y", "35.0");
        graphics = new Element("graphics");
        graphics.addContent(offset);
        name.addContent(value);
        name.addContent(graphics);
        place.addContent(name);
        Element initialMarking = new Element("initialMarking");
        value = new Element("value");
        String str = "Black,"+initailMark;
        value.setText(str);
        graphics = new Element("graphics");
        offset = new Element("offset");
        offset.setAttribute("x", "0");
        offset.setAttribute("y", "0");
        graphics.addContent(offset);
        initialMarking.addContent(value);
        initialMarking.addContent(graphics);
        place.addContent(initialMarking);
        Element capacity = new Element("capacity");
        value = new Element("value");
        value.setText("0");
        capacity.addContent(value);
        place.addContent(capacity);
        net.addContent(place);
        return place;
    }

    /**
     * 构造一个变迁 并且加载net上面
     * @param net
     * @param transitionName
     * @param trType
     * @param period
     * @param angle  角度
     * @param x
     * @param y
     * @return
     */
    private static Element buildTransition(Element net, String transitionName ,int trType, double period, int angle, int x, int y)
    {
        Element transition = new Element("transition");
        if(transitionName=="")
            transition.setAttribute("id", "T"+tIndex);
        else
            transition.setAttribute("id", transitionName);
        Element graphics = new Element("graphics");
        Element position = new Element("position");
        position.setAttribute("x", String.valueOf(x));
        position.setAttribute("y", String.valueOf(y));
        graphics.addContent(position);
        transition.addContent(graphics);

        Element name = new Element("name");
        Element value = new Element("value");

        if(transitionName=="")
            value.setText("T"+tIndex++);
        else
            value.setText(transitionName);

        Element offset = new Element("offset");
        offset.setAttribute("x", "-5.0");
        offset.setAttribute("y", "35.0");
        graphics = new Element("graphics");

        graphics.addContent(offset);
        name.addContent(value);
        name.addContent(graphics);
        transition.addContent(name);

        Element orentation = new Element("orentation");
        value = new Element("value");
        String str ="0";
        value.setText(str);
        orentation.addContent(value);
        transition.addContent(orentation);



        Element type = new Element("type");
        value = new Element("value");
        str = "" + trType;
        value.setText(str);
        type.addContent(value);
        transition.addContent(type);


        Element rate = new Element("rate");
        value = new Element("value");
        //str ="" +period;
        //if(trType == 0)
            //str ="1";
        //TODO 这里先假设，之后再改
        if(period == 0)
            period = 1;
        str ="" +period;
        value.setText(str);
        rate.addContent(value);
        transition.addContent(rate);

        Element infiniteServer = new Element("infiniteServer");
        value = new Element("value");
        str ="false";
        value.setText(str);
        infiniteServer.addContent(value);
        transition.addContent(infiniteServer);

        Element priorty = new Element("priorty");
        value = new Element("value");
        str ="0";
        value.setText(str);
        priorty.addContent(value);
        transition.addContent(priorty);


        Element delay = new Element("delay");
        value = new Element("value");
        str ="0";
        //str =""+period;
        value.setText(str);
        delay.addContent(value);
        transition.addContent(delay);

        net.addContent(transition);

        return transition;
    }
    private static int pIndex;
    private static int tIndex;
    private static int swIndex;
    private static int duIndex;
    private static int reVlIndex;
    private static int linkIndex;

    private static List<Element> listOfPlacesInRtEnd = null;
    private static LinkedList<Element> listOfTranstionVLEnd = null;

    public static ArrayList<Element> getListOfSpmLinkEnd() {
        return listOfSpmLinkEnd;
    }

    public static void setListOfSpmLinkEnd(ArrayList<Element> listOfSpmLinkEnd) {
        ModelFactory2.listOfSpmLinkEnd = listOfSpmLinkEnd;
    }

    private static ArrayList<Element> listOfSpmLinkEnd = null;
    private static List<Element> listOfScheduleStartTransition = null;

    public ModelFactory2() {
        pIndex=0;
        tIndex=0;
        swIndex=0;
        duIndex=0;
        linkIndex=0;
        reVlIndex=0;
        pnml = null;
        net = null;

        listOfPlacesInRtEnd = new LinkedList<Element>();
       listOfTranstionVLEnd = new LinkedList<Element>();
        listOfSpmLinkEnd = new ArrayList<Element>();
        listOfScheduleStartTransition = new LinkedList<Element>();
    }
}
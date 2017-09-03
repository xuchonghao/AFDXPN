package pipe.gui.widgets.newwidges;

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
public class ModelFactory1 {

    public static int index = 1;

    public static String buildModel(GuideModel guideModel)
    {
        return buildAfdxModel(guideModel);
    }

    private static String buildAfdxModel(GuideModel guideModel)
    {
        //LinkedList<TokenClass> tokenClasses = CreateGui.getModel().getTokenClasses();
        Element  pnml, net, label1, tokenclass, place, transition, arc, labelText;

        pnml = new Element("pnml");
        net = new Element("net");
        tokenclass = new Element("token");
        place = new Element("place");
        transition = new Element("transition");
        arc = new Element("arc");

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

        addSPM(guideModel, net);

        //添加交换机
        ArrayList<Element>  listOfSWLinkEnd = addSwitch( net,1610,315+400,"S",listOfSpmLinkEnd);

        // 增加接收端
        int numOfDu = guideModel.getNumOfDU();
        if(numOfDu == 1){
           // addDU1(guideModel,net,2370,315,"",swEndPlace);
        }else if((numOfDu == 2)){
           // addDU2(guideModel,net,2300,315,"",swEndPlace);
        }
       //TODO addSchedule(guideModel, net);
        pnml.addContent(net);
        Document doc = new Document(pnml);
        Format format = Format.getCompactFormat();
        format.setEncoding("iso-8859-1");           //设置xml文件的字符为iso-8859-1
        format.setIndent("    ");               //设置xml文件的缩进为4个空格

        String randomStr = "系统Petri网分析模型"+index++;
        System.out.println(randomStr);


        String path = "d://"+randomStr+".xml";
//        String path = "c://AFDX_3_PRIORITY.xml";
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

       /* if(curvePoint){
            arcpath = new Element("arcpath");
            arcpath.setAttribute("id", "001");
            arcpath.setAttribute("x", "000");
            arcpath.setAttribute("y", "000");
            arcpath.setAttribute("curvePoint", "true");
            arc1.addContent(arcpath);

            arcpath = new Element("arcpath");
            arcpath.setAttribute("id", "002");
            arcpath.setAttribute("x", "240");
            arcpath.setAttribute("y", "30");
            arcpath.setAttribute("curvePoint", "false");
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
/*
  del vo, Element net) {



*/

    public static int getVLNUMBER() {
        return VLNUMBER;
    }

    public static void setVLNUMBER(int VLNUMBER) {
        ModelFactory1.VLNUMBER = VLNUMBER;
    }

    private static int VLNUMBER = 0;
    /*private static void addDU2(GuideModel guideModel,Element net,int x,int y,String spmId,Element place){
        //spmId += (duIndex++) + "_";
        //添加链路
        //String reVLId = spmId;
        Element linkEndElement = addLink(net,x,y-200,"S",place);
        spmId = "DU"+ (duIndex++) + "_";
        String placeId0 = spmId + "inQueue";
        Element place0 = buildPlace(net, placeId0, 1, x+440, y-200);

        addArc(linkEndElement,place0,net,1,false,false);
        //添加接收端链路
        //int numOfVL = guideModel.getVlList().size();
        addRecVL(net,x+550,y-200,spmId,place0);

        //添加第二条
        //spmId = "DU"+ (duIndex++) + "_";
        //添加链路
        //String reVLId = spmId;
        Element linkEndElement2 = addLink(net,x-30,y+400,"S",place);
        //添加交换机
        ArrayList<Element> listOfDULinkEnd = new ArrayList<Element>();
        listOfDULinkEnd.add(linkEndElement2);
        Element DU2EndOfSw = addSwitch( net,x+300,y+100,"sw",listOfDULinkEnd);
        //添加链路
        Element linkEndElement3 = addLink(net,x+850,y+270,"S",DU2EndOfSw);

        spmId = "DU"+ (duIndex++) + "_";
        String placeId1 = spmId + "inQueue";
        Element place1 = buildPlace(net, placeId1, 1, x+1300, y+270);

        addArc(linkEndElement3,place1,net,1,false,false);
        //添加接收端链路
        int numOfVL2 = guideModel.getVlList().size() - 1;
        for(int i=0;i<numOfVL2;i++)
            addRecVL(net,x+1350,y+200+i*50,spmId,place1);
    }*/
    private static void addDU1(GuideModel guideModel,Element net,int x,int y,String spmId,Element place){
        //spmId += "_";
        //添加链路
        Element linkEndElement = addLink(net,x,y,"S",place);

        String placeId0 = spmId + "DU_inQueue";
        Element place0 = buildPlace(net, placeId0, 1, x+440, y);

        addArc(linkEndElement,place0,net,1,false,false);
        //添加接收端链路
        int numOfVL = guideModel.getVlList().size();
        for(int i=0;i<numOfVL;i++)
            addRecVL(net,x+550,y+i*100,"DU",place0);
    }
    private static void addRecVL( Element net,int x,int y,String spmId,Element startElement){
        spmId += "vl"+(reVlIndex++) + "_";

        String transitionId0 = spmId + "RM";
        Element transition0 = buildTransition(net,transitionId0,1,0,0,x+110,y);

        String placeId0 = spmId + "Rec-Port";
        Element place0 = buildPlace(net, placeId0, 1, x+220, y);

        String transitionId1 = spmId + "tenchLatency";
        Element transition1 = buildTransition(net,transitionId1,2,0,0,x+330,y);
        addArc(startElement,transition0,net,1,false,false);
        addArc(transition0,place0,net,1,false,false);
        addArc(place0,transition1,net,1,false,false);
    }
    private static void addSPM(GuideModel guideModel, Element net){
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

            //addParVLs(guideModel, net,len,x, y);
            prelen = len;

            //增加最后的库所和弧
            String spmId = spm.getId();
            String placeId = spmId + "TransM";
            Element place = buildPlace(net, placeId, 1, 1200, 315+i*len*400);

            //System.out.println("listOfTranstionVLEnd.size():"+listOfTranstionVLEnd.size());
            int j = len - 1;
            while(!listOfTranstionVLEnd.isEmpty()){
                Element e = listOfTranstionVLEnd.remove(j--);
                addArc(e,place,net,1,false,false);
            }

            //构建SPM后面的链路
            linkEndElement = addLink(net,1200,315+i*len*400,spmId,place);
            listOfSpmLinkEnd.add(linkEndElement);

            guideModel.getSpmList().add(spm);
        }

    }

    private static Element addLink( Element net,int x,int y,String spmId,Element startElement){
        //TODO 下面的period需要重新填充
        spmId += (reVlIndex++)+"_";
        String transitionId0 = spmId + "startTrans";
        Element transition0 = buildTransition(net,transitionId0,0,0,0,x+110,y);

        String placeId0 = spmId + "LinkIdle";
        Element place0 = buildPlace(net, placeId0, 1, x+220, y+100);

        String placeId1 = spmId + "LinkBusy";
        Element place1 = buildPlace(net, placeId1, 0, x+220, y-100);

        String transitionId1 = spmId + "endTrans";
        Element transition1 = buildTransition(net,transitionId1,2,0,0,x+330,y);

        addArc(startElement,transition0,net,1,false,false);
        addArc(place0,transition0,net,1,false,false);
        addArc(transition0,place1,net,1,false,false);
        addArc(place1,transition1,net,1,false,false);
        addArc(transition1,place0,net,1,false,false);
        return transition1;
    }

    private static ArrayList<Element> addSwitch(Element net,int x,int y,String spmId,ArrayList<Element> listOfSpmLinkEnd){
        ArrayList<Element> listOfSWLinkEnd = new ArrayList<Element>();

        spmId += (swIndex++) + "_";
        String placeIdx = spmId + "OutQueue";
        Element placex = buildPlace(net, placeIdx, 0, x+220, y);

        int len = listOfSpmLinkEnd.size();
        int j = 0;
        //while(!listOfSpmLinkEnd.isEmpty()){
        while(j < len){
            //System.out.println("J= :"+ j);
            String placeId = spmId + "Inport_" + j;
            Element place = buildPlace(net, placeId, 0, x, y+j*1200);

            //Element e = listOfSpmLinkEnd.remove(j);
            Element e = listOfSpmLinkEnd.get(j);

            addArc(e,place,net,1,false,false);

            String transitionId = spmId + "Forward_" + j;
            Element transition = buildTransition(net,transitionId,2,0,0,x+110,y+j*1200);
            addArc(place,transition,net,1,false,false);
            addArc(transition,placex,net,1,false,false);
            j++;
        }


        //TODO 下面的period需要重新填充
        String transitionId0 = spmId + "Jitter";
        Element transition0 = buildTransition(net,transitionId0,0,0,0,x+330,y);

        String placeId0 = spmId + "Wait";
        Element place0 = buildPlace(net, placeId0, 1, x+440, y+100);

        String placeId1 = spmId + "Schedule";
        Element place1 = buildPlace(net, placeId1, 0, x+440, y-100);

        String transitionId1 = spmId + "techLatency";
        Element transition1 = buildTransition(net,transitionId1,0,0,0,x+550,y);

        j = 0;
        while(j < len) {
            String placeId2 = spmId + "sendM_" + j;
            Element place2 = buildPlace(net, placeId2, 0, x+660, 800);
            addArc(transition1,place2,net,1,false,false);
            Element linkEndElement = addLink(net,x,y,"S",place2);
            listOfSWLinkEnd.add(linkEndElement);
        }


        //addArc(place,transition,net,1,false,false);
        //addArc(transition,placex,net,1,false,false);

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
    private static void addParVLs(GuideModel guideModel, Element net,int len,int x,int y){
        Queue<VLInfo> list = new LinkedList<VLInfo>();
        Queue<VLInfo> vlList = guideModel.getVlList();
        for(int i=0;i<VLNUMBER; i++){//
            VLInfo info = guideModel.getVlList().remove();
            list.add(info);
        }
        for(int i=0; i<len; i++)
        {
            VLInfo info = guideModel.getVlList().remove();
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

    private static void addVL(VLInfo info,Element net,int x,int y){
        Boolean isPeriodical = info.getPeriodical();
        Integer transCycle = info.getTransCycle();
        Integer bag = info.getBAG();
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
        String placeId3 = vlId + "_VL-Wait1";
        Element place3 = buildPlace(net, placeId3, 1, x-60, y+165);

        String placeId4 = vlId + "_VL1-Wait2";
        Element place4 = buildPlace(net, placeId4, 0, x+225, y+210);
        String placeId5 = vlId + "_Schedule";
        Element place5 = buildPlace(net, placeId5, 0, x+540, y+210);

        String placeId6 = vlId + "_Buffer";
        Element place6 = buildPlace(net, placeId6, 1, x+735, y+90);
        String placeId7 = vlId + "_Queue";
        Element place7 = buildPlace(net, placeId7, 0, x+735, y+225);


        String transtionId1 = vlId + "_"+"TLoss";
        Element transition1 = buildTransition(net, transtionId1,0, 0, 0, x+465, y);

       // System.out.println("factory de 1046 bag:" + bag);
        String transtionId2 = vlId + "_"+"VL-BAG";
        Element transition2 = buildTransition(net, transtionId2,2, bag, 0,x+60, y+210);
        String transtionId3 = vlId + "_"+"VL-Jitter";
        Element transition3 = buildTransition(net, transtionId3,1 , 0, 0, x+390, y+210);

        String transtionId4 = vlId + "_"+"Enqueue";
        Element transition4 = buildTransition(net, transtionId4,0 , 0, 0, x+630, y+150);
        String transtionId5 = vlId + "_"+"TechLatency";
        Element transition5 = buildTransition(net, transtionId5,2 , 0, 0, x+840, y+150);
        //System.out.println(transtionId5);
        listOfTranstionVLEnd.add(transition5);

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
    private static Element buildTransition(Element net, String transitionName ,int trType, int period, int angle, int x, int y)
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

    private static List<Element> listOfPlacesInRtEnd = null;
    private static ArrayList<Element> listOfTranstionVLEnd = null;
    private static ArrayList<Element> listOfSpmLinkEnd = null;
    private static List<Element> listOfScheduleStartTransition = null;

    public ModelFactory1() {
        pIndex=0;
        tIndex=0;
        swIndex=0;
        duIndex=0;
        reVlIndex=0;

        listOfPlacesInRtEnd = new LinkedList<Element>();
       listOfTranstionVLEnd = new ArrayList<Element>();
        listOfSpmLinkEnd = new ArrayList<Element>();
        listOfScheduleStartTransition = new LinkedList<Element>();
    }
}
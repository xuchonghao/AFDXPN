package pipe.views;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pipe.DSPNModules.ArrayOfGraph;
import pipe.DSPNModules.FindAllPath;
import pipe.DSPNModules.Graph;
import pipe.common.dataLayer.StateGroup;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.widgets.newwidges.GuideModel;
import pipe.gui.widgets.newwidges.VLInfo;
import pipe.models.*;
import pipe.models.interfaces.IObserver;
import pipe.utilities.Copier;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.Note;
import pipe.views.viewComponents.Parameter;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.Observable;

public class PetriNetView extends Observable implements Cloneable, IObserver, Serializable
{
    //MOVED END
    private ArrayList<PlaceView> _placeViews;
    private ArrayList<TransitionView> _transitionViews;
    private ArrayList<ArcView> _arcViews;
    private ArrayList<InhibitorArcView> _inhibitorViews;
    private ArrayList<AnnotationNote> _labels;
    private ArrayList<RateParameter> _rateParameters;


    private LinkedList<MarkingView>[] _initialMarkingVector;
    private LinkedList<MarkingView>[] _currentMarkingVector;
    private int[] _capacityMatrix;
    private int[] _priorityMatrix;
    private boolean[] _timedMatrix;
    private double[] _initialDelayMatrix;
    private double[] _currentDelayMatrix;
    private LinkedList<MarkingView>[] _markingVectorAnimationStorage;
    private static boolean _initialMarkingVectorChanged = true;
    private static boolean _currentMarkingVectorChanged = true;

    private Hashtable _arcsMap;
    private Hashtable _inhibitorsMap;
    private ArrayList _stateGroups;
    private final HashSet _rateParameterHashSet = new HashSet();
    private TokenView _activeTokenView;
    private LinkedList<TokenView> _tokenViews;
    private PetriNetController _petriNetController;
    private PetriNet _model;
    private boolean[] preEnabledTransitions;
    //private Graph graph;
    private ArrayOfGraph arrayOfGraph;


    public PetriNetView(String pnmlFileName)
    {
        //graph = new Graph();
        //arrayOfGraph = new ArrayOfGraph();
        _tokenViews = null;
        _model = new PetriNet();
        _petriNetController = ApplicationSettings.getPetriNetController();
        _model.registerObserver(this);
        initializeMatrices();
        PNMLTransformer transform = new PNMLTransformer();
        File temp = new File(pnmlFileName);
        _model.setPnmlName(temp.getName());
        createFromPNML(transform.transformPNML(pnmlFileName));
    }

    public PetriNetView(PetriNetController petriNetController, PetriNet model)
    {
        //arrayOfGraph = new ArrayOfGraph();
        initializeMatrices();
        _model = model;
        model.registerObserver(this);
        _petriNetController = petriNetController;
        initializeMatrices();
        _model.registerObserver(this);
    }


    public PetriNetView clone()
    {
        PetriNetView newClone;
        try
        {
            newClone = (PetriNetView) super.clone();
            newClone._placeViews = deepCopy(_placeViews);
            newClone._transitionViews = deepCopy(_transitionViews);
            newClone._arcViews = deepCopy(_arcViews);
            newClone._inhibitorViews = deepCopy(_inhibitorViews);
            newClone._labels = deepCopy(_labels);
            newClone._tokenViews = (LinkedList<TokenView>) Copier.deepCopy(_tokenViews);
        }
        catch(CloneNotSupportedException e)
        {
            throw new Error(e);
        }
        return newClone;
    }

    public void setTokenViews(LinkedList<TokenView> tokenViews)
    {
        if(this._tokenViews == null)
        {
            this._tokenViews = tokenViews;
        }
        else
        {
            int currentSize = this._tokenViews.size();
            for(int i = 0; i < tokenViews.size(); i++)
            {
                if(i < currentSize)
                {
                    this._tokenViews.get(i).setColor(
                            tokenViews.get(i).getColor());
                    this._tokenViews.get(i).setID(tokenViews.get(i).getID());
                    this._tokenViews.get(i).setEnabled(tokenViews.get(i).isEnabled());
                }
                else
                {
                    this._tokenViews.add(tokenViews.get(i));
                }
                if(this._tokenViews.get(i).isEnabled())//指的是token是enabled的意思吗
                {
                    for(Object p : _placeViews)
                    {
                        int pos = positionInTheList(this._tokenViews.get(i).getID(), ((PlaceView) p).getCurrentMarkingView());
                        if(pos == -1)
                        {
                            ((PlaceView) p).getCurrentMarkingView().add(new MarkingView(this._tokenViews.get(i), 0));
                        }
                    }
                    for(Object a : _arcViews)
                    {
                        int pos = positionInTheList(this._tokenViews.get(i).getID(), ((ArcView) a).getWeight());
                        if(pos == -1)
                        {
                            ((ArcView) a).getWeight().add(new MarkingView(this._tokenViews.get(i), 0));
                        }
                    }
                }
                else
                {
                    for(Object p : _placeViews)
                    {
                        int pos = positionInTheList(this._tokenViews.get(i).getID(), ((PlaceView) p).getCurrentMarkingView());
                        if(pos != -1)
                        {
                            ((PlaceView) p).getCurrentMarkingView().remove(pos);
                        }
                    }
                    for(Object a : _arcViews)
                    {
                        int pos = positionInTheList(this._tokenViews.get(i).getID(), ((ArcView) a).getWeight());
                        if(pos != -1)
                        {
                            ((ArcView) a).getWeight().remove(pos);
                        }
                    }
                }
            }
        }

    }

    public LinkedList<TokenView> getTokenViews()
    {
        if(_tokenViews == null)
        {
            _tokenViews = new LinkedList<TokenView>();
            _tokenViews.add(new TokenView(true, "Default", Color.black));
            this.setActiveTokenView(_tokenViews.get(0));
        }
        return _tokenViews;
    }

    public TokenView getActiveTokenView()
    {
        return _activeTokenView;
    }

    public void setActiveTokenView(TokenView tc)
    {
        this._activeTokenView = tc;
        for(Object p : _placeViews)
        {
            ((PlaceView) p).setActiveTokenView(tc);
        }
    }

    public void lockTokenClass(String id)
    {
        for(TokenView tc : _tokenViews)
        {
            if(tc.getID().equals(id))
            {
                tc.incrementLock();
            }
        }
    }

    public void unlockTokenClass(String id)
    {
        for(TokenView tc : _tokenViews)
        {
            if(tc.getID().equals(id))
            {
                tc.decrementLock();
            }
        }
    }

    public int positionInTheList(String tokenClassID, LinkedList<MarkingView> markingViews)
    {
        int size = markingViews.size();
        for(int i = 0; i < size; i++)
        {
            MarkingView m = markingViews.get(i);
            if(m.getToken().getID().equals(tokenClassID))
                return i;
        }
        return -1;
    }

    public TokenView getTokenClassFromID(String id)
    {
        TokenView aNull = null;
        for(TokenView tc : _tokenViews)
        {
            if(tc.getID().equals(id))
                return tc;
        }
        return aNull;
    }

    private static ArrayList deepCopy(ArrayList original)
    {
        ArrayList result = (ArrayList) original.clone();
        ListIterator listIter = result.listIterator();

        while(listIter.hasNext())
        {
            PetriNetViewComponent pnObj = (PetriNetViewComponent) listIter.next();
            listIter.set(pnObj.clone());
        }
        return result;
    }

    private void initializeMatrices()
    {
        _placeViews = new ArrayList();
        _transitionViews = new ArrayList();
        _arcViews = new ArrayList();
        _inhibitorViews = new ArrayList();
        _labels = new ArrayList();
        _stateGroups = new ArrayList();
        _rateParameters = new ArrayList();
        _initialMarkingVector = null;
        _arcsMap = new Hashtable();
        _inhibitorsMap = new Hashtable();
        preEnabledTransitions = null;//
    }

    private void addPlace(PlaceView placeView)
    {
        boolean unique = true;

        if(placeView != null)
        {
            if(placeView.getId() != null && placeView.getId().length() > 0)
            {
                for(PlaceView _placeView : _placeViews)
                {
                    if(placeView.getId().equals(
                            _placeView.getId()))
                    {
                        unique = false;
                    }
                }
            }
            else
            {
                String id = null;
                if(_placeViews != null && _placeViews.size() > 0)
                {
                    int no = _placeViews.size();
                    do
                    {
                        for(PlaceView _placeView : _placeViews)
                        {
                            id = "P" + no;
                            if(_placeView != null)
                            {
                                if(id.equals(_placeView
                                                     .getId()))
                                {
                                    unique = false;
                                    no++;
                                }
                                else
                                {
                                    unique = true;
                                }
                            }
                        }
                    } while(!unique);
                }
                else
                {
                    id = "P0";
                }

                if(id != null)
                {
                    placeView.setId(id);
                }
                else
                {
                    placeView.setId("error");
                }
            }
            placeView.setActiveTokenView(_activeTokenView);
            _placeViews.add(placeView);
            setChanged();
            setMatrixChanged();
            notifyObservers(placeView);
        }
    }

    private void addAnnotation(AnnotationNote labelInput)
    {
        boolean unique = true;
        _labels.add(labelInput);
        setChanged();
        notifyObservers(labelInput);
    }

    private void addAnnotation(RateParameter rateParameterInput)
    {
        boolean unique = true;
        _rateParameters.add(rateParameterInput);
        setChanged();
        notifyObservers(rateParameterInput);
    }

    private void addTransition(TransitionView transitionViewInput)
    {
        boolean unique = true;

        if(transitionViewInput != null)
        {
            if(transitionViewInput.getId() != null
                    && transitionViewInput.getId().length() > 0)
            {
                for(TransitionView _transitionView : _transitionViews)
                {
                    if(transitionViewInput.getId().equals(
                            _transitionView.getId()))
                    {
                        unique = false;
                    }
                }
            }
            else
            {
                String id = null;
                if(_transitionViews != null && _transitionViews.size() > 0)
                {
                    int no = _transitionViews.size();
                    do
                    {
                        for(TransitionView _transitionView : _transitionViews)
                        {
                            id = "T" + no;
                            if(_transitionView != null)
                            {
                                if(id.equals(_transitionView.getId()))
                                {
                                    unique = false;
                                    no++;
                                }
                                else
                                {
                                    unique = true;
                                }
                            }
                        }
                    } while(!unique);
                }
                else
                {
                    id = "T0";
                }

                if(id != null)
                {
                    transitionViewInput.setId(id);
                }
                else
                {
                    transitionViewInput.setId("error");
                }
            }
            _transitionViews.add(transitionViewInput);
            setChanged();
            setMatrixChanged();
            notifyObservers(transitionViewInput);
        }
    }

    public void addArc(NormalArcView arcViewInput)
    {
        boolean unique = true;

        if(arcViewInput != null)
        {
            if(arcViewInput.getId() != null && arcViewInput.getId().length() > 0)
            {
                for(ArcView _arcView : _arcViews)
                {
                    if(arcViewInput.getId().equals(
                            _arcView.getId()))
                    {
                        unique = false;
                    }
                }
            }
            else
            {
                String id = null;
                if(_arcViews != null && _arcViews.size() > 0)
                {
                    int no = _arcViews.size();
                    do
                    {
                        for(ArcView _arcView : _arcViews)
                        {
                            id = "A" + no;
                            if(_arcView != null)
                            {
                                if(id.equals(_arcView.getId()))
                                {
                                    unique = false;
                                    no++;
                                }
                                else
                                {
                                    unique = true;
                                }
                            }
                        }
                    } while(!unique);
                }
                else
                {
                    id = "A0";
                }
                if(id != null)
                {
                    arcViewInput.setId(id);
                }
                else
                {
                    arcViewInput.setId("error");
                }
            }
            _arcViews.add(arcViewInput);
            addArcToArcsMap(arcViewInput);

            setChanged();
            setMatrixChanged();
            notifyObservers(arcViewInput);
        }
    }

    public void addArc(InhibitorArcView inhibitorArcViewInput)
    {
        boolean unique = true;

        if(inhibitorArcViewInput != null)
        {
            if(inhibitorArcViewInput.getId() != null
                    && inhibitorArcViewInput.getId().length() > 0)
            {
                for(InhibitorArcView _inhibitorView : _inhibitorViews)
                {
                    if(inhibitorArcViewInput.getId().equals(
                            _inhibitorView.getId()))
                    {
                        unique = false;
                    }
                }
            }
            else
            {
                String id = null;
                if(_inhibitorViews != null && _inhibitorViews.size() > 0)
                {
                    int no = _inhibitorViews.size();
                    do
                    {
                        for(InhibitorArcView _inhibitorView : _inhibitorViews)
                        {
                            id = "I" + no;
                            if(_inhibitorView != null)
                            {
                                if(id.equals(_inhibitorView
                                                     .getId()))
                                {
                                    unique = false;
                                    no++;
                                }
                                else
                                {
                                    unique = true;
                                }
                            }
                        }
                    } while(!unique);
                }
                else
                {
                    id = "I0";
                }
                if(id != null)
                {
                    inhibitorArcViewInput.setId(id);
                }
                else
                {
                    inhibitorArcViewInput.setId("error");
                }
            }
            _inhibitorViews.add(inhibitorArcViewInput);
            addInhibitorArcToInhibitorsMap(inhibitorArcViewInput);

            setChanged();
            setMatrixChanged();
            // notifyObservers(arcInput.getBounds());
            notifyObservers(inhibitorArcViewInput);
        }
    }

    private void addArcToArcsMap(NormalArcView arcViewInput)
    {
        ConnectableView source = arcViewInput.getSource();
        ConnectableView target = arcViewInput.getTarget();
        ArrayList newList;

        if(source != null)
        {
            if(_arcsMap.get(source) != null)
            {
                ((ArrayList) _arcsMap.get(source)).add(arcViewInput);
            }
            else
            {
                newList = new ArrayList();
                newList.add(arcViewInput);

                _arcsMap.put(source, newList);
            }
        }

        if(target != null)
        {
            if(_arcsMap.get(target) != null)
            {
                ((ArrayList) _arcsMap.get(target)).add(arcViewInput);
            }
            else
            {
                newList = new ArrayList();
                newList.add(arcViewInput);
                _arcsMap.put(target, newList);
            }
        }
    }

    private void addInhibitorArcToInhibitorsMap(InhibitorArcView inhibitorArcViewInput)
    {
        ConnectableView source = inhibitorArcViewInput.getSource();
        ConnectableView target = inhibitorArcViewInput.getTarget();
        ArrayList newList;

        if(source != null)
        {
            if(_inhibitorsMap.get(source) != null)
            {
                ((ArrayList) _inhibitorsMap.get(source)).add(inhibitorArcViewInput);
            }
            else
            {
                newList = new ArrayList();
                newList.add(inhibitorArcViewInput);
                _inhibitorsMap.put(source, newList);
            }
        }

        if(target != null)
        {
            if(_inhibitorsMap.get(target) != null)
            {
                ((ArrayList) _inhibitorsMap.get(target)).add(inhibitorArcViewInput);
            }
            else
            {
                newList = new ArrayList();
                newList.add(inhibitorArcViewInput);
                _inhibitorsMap.put(target, newList);
            }
        }
    }

    public void addStateGroup(StateGroup stateGroupInput)
    {
        boolean unique = true;
        String id;
        int no = _stateGroups.size();

        if(stateGroupInput.getId() != null
                && stateGroupInput.getId().length() > 0)
        {
            id = stateGroupInput.getId();

            for(Object _stateGroup : _stateGroups)
            {
                if(id.equals(((StateGroup) _stateGroup).getId()))
                {
                    unique = false;
                }
            }
        }
        else
        {
            unique = false;
        }

        if(!unique)
        {
            id = "SG" + no;
            for(int i = 0; i < _stateGroups.size(); i++)
            {
                if(id.equals(((StateGroup) _stateGroups.get(i)).getId()))
                {
                    id = "SG" + ++no;
                    i = 0;
                }
            }
            stateGroupInput.setId(id);
        }
        _stateGroups.add(stateGroupInput);
    }

    private void addToken(TokenView tokenViewInput)
    {
        boolean firstEntry = false;
        if(_tokenViews == null)
        {
            _tokenViews = new LinkedList<TokenView>();
            firstEntry = true;
        }
        boolean unique = true;

        if(tokenViewInput != null)
        {
            if(tokenViewInput.getID() != null && tokenViewInput.getID().length() > 0)
            {
                for(TokenView _tokenView : _tokenViews)
                {
                    if(tokenViewInput.getID().equals(
                            _tokenView.getID()))
                    {
                        unique = false;
                    }
                }
            }
            else
            {
                String id = null;
                if(_tokenViews != null && _tokenViews.size() > 0)
                {
                    int no = _tokenViews.size();
                    do
                    {
                        for(TokenView _tokenView : _tokenViews)
                        {
                            id = "token" + no;
                            if(_tokenView != null)
                            {
                                if(id.equals(_tokenView
                                                     .getID()))
                                {
                                    unique = false;
                                    no++;
                                }
                                else
                                {
                                    unique = true;
                                }
                            }
                        }
                    } while(!unique);
                }
                else
                {
                    id = "token0";
                }

                if(id != null)
                {
                    tokenViewInput.setID(id);
                }
                else
                {
                    tokenViewInput.setID("error");
                }
            }
            _tokenViews.add(tokenViewInput);
            if(firstEntry)
            {
                this.setActiveTokenView(tokenViewInput);
            }
            setChanged();
            setMatrixChanged();
            notifyObservers(tokenViewInput);
        }
    }

    public void addPetriNetObject(PetriNetViewComponent pn)
    {
        if(pn instanceof NormalArcView)
        {
            addArcToArcsMap((NormalArcView) pn);
            addArc((NormalArcView) pn);
        }
        else if(pn instanceof InhibitorArcView)
        {
            addInhibitorArcToInhibitorsMap((InhibitorArcView) pn);
            addArc((InhibitorArcView) pn);
        }
        else if(pn instanceof PlaceView)
        {
            addPlace((PlaceView) pn);
        }
        else if(pn instanceof TransitionView)
        {
            addTransition((TransitionView) pn);
        }
        else if(pn instanceof AnnotationNote)
        {
            _labels.add((AnnotationNote) pn);
        }
        else if(pn instanceof RateParameter)
        {
            _rateParameters.add((RateParameter) pn);
            _rateParameterHashSet.add(pn.getName());
        }
    }

    public void removePetriNetObject(PetriNetViewComponent pn)
    {
        ArrayList attachedArcs;

        try
        {

            if(pn instanceof ConnectableView)
            {

                if(_arcsMap.get(pn) != null)
                {

                    attachedArcs = ((ArrayList) _arcsMap.get(pn));
                    for(int i = attachedArcs.size() - 1; i >= 0; i--)
                    {
                        ((ArcView) attachedArcs.get(i)).delete();
                    }
                    _arcsMap.remove(pn);
                }

                if(_inhibitorsMap.get(pn) != null)
                {

                    attachedArcs = ((ArrayList) _inhibitorsMap.get(pn));

                    for(int i = attachedArcs.size() - 1; i >= 0; i--)
                    {
                        ((ArcView) attachedArcs.get(i)).delete();
                    }
                    _inhibitorsMap.remove(pn);
                }
            }
            else if(pn instanceof NormalArcView)
            {

                ConnectableView attached = ((ArcView) pn)
                        .getSource();

                if(attached != null)
                {
                    ArrayList a = (ArrayList) _arcsMap.get(attached);
                    if(a != null)
                    {
                        a.remove(pn);
                    }

                    attached.removeFromArc((ArcView) pn);
                    if(attached instanceof TransitionView)
                    {
                        ((TransitionView) attached)
                                .removeArcCompareObject((ArcView) pn);
                        attached.updateConnected();
                    }
                }

                attached = ((ArcView) pn).getTarget();
                if(attached != null)
                {
                    if(_arcsMap.get(attached) != null)
                    {
                        ((ArrayList) _arcsMap.get(attached))
                                .remove(pn);
                    }

                    attached.removeToArc((ArcView) pn);
                    if(attached instanceof TransitionView)
                    {
                        ((TransitionView) attached)
                                .removeArcCompareObject((ArcView) pn);
                        attached.updateConnected();
                    }
                }
            }
            else if(pn instanceof InhibitorArcView)
            {

                ConnectableView attached = ((ArcView) pn)
                        .getSource();

                if(attached != null)
                {
                    ArrayList a = (ArrayList) _inhibitorsMap.get(attached);
                    if(a != null)
                    {
                        a.remove(pn);
                    }

                    attached.removeFromArc((ArcView) pn);
                    if(attached instanceof TransitionView)
                    {
                        ((TransitionView) attached)
                                .removeArcCompareObject((ArcView) pn);
                    }
                }

                attached = ((ArcView) pn).getTarget();

                if(attached != null)
                {
                    if(_inhibitorsMap.get(attached) != null)
                    {
                        ((ArrayList) _inhibitorsMap.get(attached))
                                .remove(pn);
                    }

                    attached.removeToArc((ArcView) pn);
                    if(attached instanceof TransitionView)
                    {
                        ((TransitionView) attached)
                                .removeArcCompareObject((ArcView) pn);
                    }
                }
            }
            else if(pn instanceof RateParameter)
            {
                _rateParameterHashSet.remove(pn.getName());
            }

            setChanged();
            setMatrixChanged();
            notifyObservers(pn);
        }
        catch(NullPointerException npe)
        {
            System.out.println("NullPointerException [debug]\n"
                                       + npe.getMessage());
            throw npe;
        }
        //_changeArrayList = null;
    }

    public void removeStateGroup(StateGroup SGObject)
    {
        _stateGroups.remove(SGObject);
    }

    public boolean stateGroupAlreadyExists(String stateName)
    {
        Iterator<StateGroup> i = _stateGroups.iterator();
        while(i.hasNext())
        {
            StateGroup stateGroup = i.next();
            String stateGroupName = stateGroup.getName();
            if(stateName.equals(stateGroupName))
            {
                return true;
            }
        }
        return false;
    }

    public Iterator returnTransitions()
    {
        return _transitionViews.iterator();
    }

    public Iterator getPetriNetObjects()
    {
        ArrayList all = new ArrayList(_placeViews);
        all.addAll(_transitionViews);
        all.addAll(_arcViews);
        all.addAll(_labels);
        // tokensArray removed
        all.addAll(_rateParameters);

        return all.iterator();
    }

    public boolean hasPlaceTransitionObjects()
    {
        return (_placeViews.size() + _transitionViews.size()) > 0;
    }

    private AnnotationNote createAnnotation(Element inputLabelElement)
    {
        int positionXInput = 0;
        int positionYInput = 0;
        int widthInput = 0;
        int heightInput = 0;
        String text;
        boolean borderInput;

        String positionXTempStorage = inputLabelElement
                .getAttribute("xPosition");
        String positionYTempStorage = inputLabelElement
                .getAttribute("yPosition");
        String widthTemp = inputLabelElement.getAttribute("w");
        String heightTemp = inputLabelElement.getAttribute("h");
        String textTempStorage = inputLabelElement.getAttribute("txt");
        String borderTemp = inputLabelElement.getAttribute("border");

        if(positionXTempStorage.length() > 0)
        {
            positionXInput = Integer.valueOf(positionXTempStorage).intValue()
                    + (1);
        }

        if(positionYTempStorage.length() > 0)
        {
            positionYInput = Integer.valueOf(positionYTempStorage).intValue()
                    + (1);
        }

        if(widthTemp.length() > 0)
        {
            widthInput = Integer.valueOf(widthTemp).intValue()
                    + (1);
        }

        if(heightTemp.length() > 0)
        {
            heightInput = Integer.valueOf(heightTemp).intValue()
                    + (1);
        }

        if(borderTemp.length() > 0)
        {
            borderInput = Boolean.valueOf(borderTemp).booleanValue();
        }
        else
        {
            borderInput = true;
        }

        if(textTempStorage.length() > 0)
        {
            text = textTempStorage;
        }
        else
        {
            text = "";
        }

        return new AnnotationNote(text, positionXInput, positionYInput,
                                  widthInput, heightInput, borderInput);
    }

    private Parameter createParameter(Element inputDefinitionElement)
    {
        int positionXInput = 0;
        int positionYInput = 0;

        String positionXTempStorage = inputDefinitionElement
                .getAttribute("positionX");
        String positionYTempStorage = inputDefinitionElement
                .getAttribute("positionY");
        String nameTemp = inputDefinitionElement.getAttribute("name");
        String expressionTemp = inputDefinitionElement
                .getAttribute("expression");

        if(positionXTempStorage.length() > 0)
        {
            positionXInput = Integer.valueOf(positionXTempStorage).intValue();
        }

        if(positionYTempStorage.length() > 0)
        {
            positionYInput = Integer.valueOf(positionYTempStorage).intValue();
        }

        _rateParameterHashSet.add(nameTemp);
        return new RateParameter(nameTemp, Double
                .parseDouble(expressionTemp), positionXInput,
                                 positionYInput);
    }

    private TransitionView createTransition(Element element)
    {
        double positionXInput = 0;
        double positionYInput = 0;
        String idInput = null;
        String nameInput = null;
        double nameOffsetYInput = 0;
        double nameOffsetXInput = 0;
        
        String typenameInput = null;
        double typenameOffsetYInput = 0;
        double typenameOffsetXInput = 0;
        
        double rate;
        int typeTransition;
        boolean infiniteServer;
        int angle = 0;
        int priority = 1;
        double weight = 1.0;


        String positionXTempStorage = element.getAttribute("positionX");
        String positionYTempStorage = element.getAttribute("positionY");
        String idTempStorage = element.getAttribute("id");
        String nameTempStorage = element.getAttribute("name");
        String nameOffsetXTempStorage = element.getAttribute("nameOffsetX");
        String nameOffsetYTempStorage = element.getAttribute("nameOffsetY");
        
        String typenameTempStorage = element.getAttribute("typename");
        String typenameOffsetXTempStorage = element.getAttribute("typenameOffsetX");
        String typenameOffsetYTempStorage = element.getAttribute("typenameOffsetY");
        
        String nameRate = element.getAttribute("rate");
        //System.out.println("1073 createTransition:"+nameRate);


        String nameType = element.getAttribute("type");//表示什么类型的变迁
        String nameInfiniteServer = element.getAttribute("infiniteServer");
        String nameAngle = element.getAttribute("angle");
        String namePriority = element.getAttribute("priority");//
        String parameterTempStorage = element.getAttribute("parameter");

        //System.out.println("idTempStorage:"+idTempStorage +" ,typenameTempStorage:"+typenameTempStorage +
              //  " ,nameRate:"+nameRate+ " ,nameType:"+nameType+ " ,parameterTempStorage:"+parameterTempStorage+
                //" ,priority:"+priority);
        if(nameType.length() == 0)
        {
            typeTransition = 0;
        }
        else typeTransition = Integer.parseInt(nameType);

        infiniteServer = !(nameInfiniteServer.length() == 0 || nameInfiniteServer
                .length() == 5);

        if(positionXTempStorage.length() > 0)
        {
            positionXInput = Double.valueOf(positionXTempStorage).doubleValue()
                    * (1)
                    + (1);
        }
        if(positionYTempStorage.length() > 0)
        {
            positionYInput = Double.valueOf(positionYTempStorage).doubleValue()
                    * (1)
                    + (1);
        }

        positionXInput = Grid.getModifiedX(positionXInput);
        positionYInput = Grid.getModifiedY(positionYInput);

        if(idTempStorage.length() > 0)
        {
            idInput = idTempStorage;
        }
        else if(nameTempStorage.length() > 0)
        {
            idInput = nameTempStorage;
        }

        if(nameTempStorage.length() > 0)
        {
            nameInput = nameTempStorage;
        }
        else if(idTempStorage.length() > 0)
        {
            nameInput = idTempStorage;
        }

        
        if(typenameTempStorage.length() > 0)
        {
        	typenameInput = typenameTempStorage;
        }
        
        if(nameOffsetXTempStorage.length() > 0)
        {
            nameOffsetXInput = Double.valueOf(nameOffsetXTempStorage)
                    .doubleValue();
        }

        if(nameOffsetYTempStorage.length() > 0)
        {
            nameOffsetYInput = Double.valueOf(nameOffsetYTempStorage)
                    .doubleValue();
        }

        if(typenameOffsetXTempStorage.length() > 0)
        {
        	typenameOffsetXInput = Double.valueOf(typenameOffsetXTempStorage)
                    .doubleValue();
        }

        if(typenameOffsetYTempStorage.length() > 0)
        {
        	typenameOffsetYInput = Double.valueOf(typenameOffsetYTempStorage)
                    .doubleValue();
        }

        
        if(nameRate.length() == 0)
        {
            nameRate = "1.0";
        }
        if(!nameRate.equals("1.0"))
        {
            rate = Double.valueOf(nameRate).doubleValue();
        }
        else
        {
            rate = 1.0;
        }

        if(nameAngle.length() > 0)
        {
            angle = Integer.valueOf(nameAngle).intValue();
        }

        if(namePriority.length() > 0)
        {
            priority = Integer.valueOf(namePriority).intValue();
        }

        TransitionView transitionView = new TransitionView(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput, nameOffsetYInput, typeTransition, infiniteServer, angle, new Transition(idInput, nameInput, rate, priority)
        ,typenameInput, typenameOffsetXInput, typenameOffsetYInput);

        if(parameterTempStorage.length() > 0)
        {
            if(existsRateParameter(parameterTempStorage))
            {
                for(RateParameter _rateParameter : _rateParameters)
                {
                    if(parameterTempStorage
                            .equals(_rateParameter
                                            .getName()))
                    {
                        transitionView.setRateParameter(_rateParameter);
                    }
                }
            }
        }

        return transitionView;
    }

    private PlaceView createPlace(Element element)
    {
        double positionXInput = 0;
        double positionYInput = 0;
        String idInput = null;
        String nameInput = null;
        double nameOffsetYInput = 0;
        double nameOffsetXInput = 0;
        LinkedList<MarkingView> initialMarkingViewInput = new LinkedList<MarkingView>();
        double markingOffsetXInput = 0;
        double markingOffsetYInput = 0;
        int capacityInput = 0;

        String positionXTempStorage = element.getAttribute("positionX");
        String positionYTempStorage = element.getAttribute("positionY");
        String idTempStorage = element.getAttribute("id");
        String nameTempStorage = element.getAttribute("name");
        String nameOffsetXTempStorage = element.getAttribute("nameOffsetX");
        String nameOffsetYTempStorage = element.getAttribute("nameOffsetY");
        String initialMarkingTempStorage = element
                .getAttribute("initialMarking");
        String markingOffsetXTempStorage = element
                .getAttribute("markingOffsetX");
        String markingOffsetYTempStorage = element
                .getAttribute("markingOffsetY");
        String capacityTempStorage = element.getAttribute("capacity");
        String parameterTempStorage = element.getAttribute("parameter");

        if(positionXTempStorage.length() > 0)
        {
            positionXInput = Double.valueOf(positionXTempStorage).doubleValue()
                    * (1)
                    + (1);
        }
        if(positionYTempStorage.length() > 0)
        {
            positionYInput = Double.valueOf(positionYTempStorage).doubleValue()
                    * (1)
                    + (1);
        }
        positionXInput = Grid.getModifiedX(positionXInput);
        positionYInput = Grid.getModifiedY(positionYInput);

        if(idTempStorage.length() > 0)
        {
            idInput = idTempStorage;
        }
        else if(nameTempStorage.length() > 0)
        {
            idInput = nameTempStorage;
        }

        if(nameTempStorage.length() > 0)
        {
            nameInput = nameTempStorage;
        }
        else if(idTempStorage.length() > 0)
        {
            nameInput = idTempStorage;
        }

        if(nameOffsetYTempStorage.length() > 0)
        {
            nameOffsetXInput = Double.valueOf(nameOffsetXTempStorage)
                    .doubleValue();
        }
        if(nameOffsetXTempStorage.length() > 0)
        {
            nameOffsetYInput = Double.valueOf(nameOffsetYTempStorage)
                    .doubleValue();
        }

        if(initialMarkingTempStorage.length() > 0)
        {
            String[] stringArray = initialMarkingTempStorage.split(",");
            // Backward compatibility for pnmls without many _tokens
            if(stringArray.length == 1)
            {
                if(getActiveTokenView() == null)
                {
                    Color c = new Color(0, 0, 0);
                    TokenView tc = new TokenView(true, "Default", c);
                    addToken(tc);
                    MarkingView markingView = new MarkingView(tc, Integer.valueOf(stringArray[0]));
                    initialMarkingViewInput.add(markingView);
                }
                else
                {
                    MarkingView markingView = new MarkingView(getActiveTokenView(), Integer.valueOf(stringArray[0]));
                    initialMarkingViewInput.add(markingView);
                }
            }
            else
            {
                int i = 0;
                while(i < stringArray.length)
                {
                    // In case for some reason there are commas between markings
                    stringArray[i] = stringArray[i].trim();
                    MarkingView markingView = new MarkingView(this.getTokenClassFromID(stringArray[i]), Integer.valueOf(stringArray[i + 1]));
                    initialMarkingViewInput.add(markingView);
                    i += 2;
                }
            }
        }
        if(markingOffsetXTempStorage.length() > 0)
        {
            markingOffsetXInput = Double.valueOf(markingOffsetXTempStorage).doubleValue();
        }
        if(markingOffsetYTempStorage.length() > 0)
        {
            markingOffsetYInput = Double.valueOf(markingOffsetYTempStorage).doubleValue();
        }

        if(capacityTempStorage.length() > 0)
        {
            capacityInput = Integer.valueOf(capacityTempStorage).intValue();
        }

        return new PlaceView(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput, nameOffsetYInput, initialMarkingViewInput, markingOffsetXInput, markingOffsetYInput, capacityInput);
    }


    private TokenView createToken(Element inputTokenElement)
    {
        String id = inputTokenElement.getAttribute("id");
        boolean booleanEnabled = Boolean.parseBoolean(inputTokenElement.getAttribute("enabled"));
        int red = Integer.parseInt(inputTokenElement.getAttribute("red"));
        int green = Integer.parseInt(inputTokenElement.getAttribute("green"));
        int blue = Integer.parseInt(inputTokenElement.getAttribute("blue"));
        Color c = new Color(red, green, blue);
        return new TokenView(booleanEnabled, id, c);
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#createMatrixes()
      */
    public void createMatrixes()
    {
        for(TokenView tc : _tokenViews)
        {
            if(tc.isEnabled())
            {
                tc.createIncidenceMatrix(_arcViews, _transitionViews, _placeViews);
                tc.createInhibitionMatrix(_inhibitorViews, _transitionViews, _placeViews);
            }
        }
        createInitialMarkingVector();
        createInitialDelayMatix();
        createCurrentDelayMatix();
        createCurrentMarkingVector();
        createCapacityVector();
    }

/**
 * Creates initial Delay Matix from current Petri-Net
 */
    private void createInitialDelayMatix()
    {
        int transitionSize = _transitionViews.size();
        _initialDelayMatrix = new double[transitionSize];
        for(int transitionNo = 0; transitionNo < transitionSize; transitionNo++)
        {//这里必须默认设置瞬时变迁的delay=-1，去读取petri网的那里看一看！
            _initialDelayMatrix[transitionNo] = _transitionViews.get(transitionNo).getDelay();
        }
    }
    public double[] get_initialDelayMatrix(){
        createInitialDelayMatix();
        return _initialDelayMatrix;
    }
    /**
     * Creates current Delay Matix from current Petri-Net
     */
    private void createCurrentDelayMatix()
    {
        int transitionSize = _transitionViews.size();

        _currentDelayMatrix = new double[transitionSize];
        for(int transitionNo = 0; transitionNo < transitionSize; transitionNo++)
        {//这里必须默认设置瞬时变迁的delay=-1，去读取petri网的那里看一看！
            _currentDelayMatrix[transitionNo] = _transitionViews.get(transitionNo).getDelay();
        }
    }
    public double[] get_currentDelayMatrix(){
        createCurrentDelayMatix();
        return _currentDelayMatrix;
    }
    public void storeCurrentDelay(double[] arr)
    {
        int transitionSize = _transitionViews.size();
        _currentDelayMatrix = new double[transitionSize];
        for(int transitionNo = 0; transitionNo < transitionSize; transitionNo++)
        {
            _currentDelayMatrix[transitionNo] = arr[transitionNo];//需要区分新使能和持续是能的变迁？
            _transitionViews.get(transitionNo).setDelay(_currentDelayMatrix[transitionNo]);
            //如果有瞬时变迁新使能
            //如果有指数变迁新使能
            //如果有确定变迁新使能，放这里，还是放在这个函数之前
        }
    }
    /**
     * Creates Initial Marking Vector from current Petri-Net
     */
    private void createInitialMarkingVector()
    {
        int placeSize = _placeViews.size();

        _initialMarkingVector = new LinkedList[placeSize];
        for(int placeNo = 0; placeNo < placeSize; placeNo++)
        {
            _initialMarkingVector[placeNo] = _placeViews.get(placeNo).getInitialMarkingView();
        }
    }

    /**
     * Creates Current Marking Vector from current Petri-Net
     */
    private void createCurrentMarkingVector()
    {
        int placeSize = _placeViews.size();

        _currentMarkingVector = new LinkedList[placeSize];
        for(int placeNo = 0; placeNo < placeSize; placeNo++)
        {
            _currentMarkingVector[placeNo] = _placeViews.get(placeNo).getCurrentMarkingView();
        }
    }

    /**
     * Creates Capacity Vector from current Petri-Net
     */
    private void createCapacityVector()
    {
        int placeSize = _placeViews.size();

        _capacityMatrix = new int[placeSize];
        for(int placeNo = 0; placeNo < placeSize; placeNo++)
        {
            _capacityMatrix[placeNo] = _placeViews.get(placeNo)
                    .getCapacity();
        }
    }

    /**
     * Creates Timed Vector from current Petri-Net
     */
    private void createTimedVector()
    {
        int transitionSize = _transitionViews.size();

        _timedMatrix = new boolean[transitionSize];
        for(int transitionNo = 0; transitionNo < transitionSize; transitionNo++)
        {
            _timedMatrix[transitionNo] = (_transitionViews.get(transitionNo).getType()==0);
        }
    }

    /**
     * Creates Priority Vector from current Petri-Net
     */
    private void createPriorityVector()
    {
        int transitionSize = _transitionViews.size();

        _priorityMatrix = new int[transitionSize];
        for(int transitionNo = 0; transitionNo < transitionSize; transitionNo++)
        {
            _priorityMatrix[transitionNo] = _transitionViews
                    .get(transitionNo).getPriority();
        }
    }

    public void storeCurrentMarking2(GuideModel guideModel)
    {
        //这个要根据每条虚链路生成一个id，这里生成的是库所的
        Queue<VLInfo> vlInfos = guideModel.getVlList();
        int placeSize = _placeViews.size();
        _markingVectorAnimationStorage = new LinkedList[placeSize];
        for(int placeNo = 0; placeNo < placeSize; placeNo++)
        {
            _markingVectorAnimationStorage[placeNo] = Copier.mediumCopy(_placeViews
                                                                                .get(placeNo).getCurrentMarkingView());
      }
    }
    public void storeCurrentMarking()
    {
        int placeSize = _placeViews.size();
        _markingVectorAnimationStorage = new LinkedList[placeSize];
        for(int placeNo = 0; placeNo < placeSize; placeNo++)
        {
            _markingVectorAnimationStorage[placeNo] = Copier.mediumCopy(_placeViews
                                                                                .get(placeNo).getCurrentMarkingView());
        }
    }

    public void restorePreviousMarking()
    {
        if(_markingVectorAnimationStorage != null)
        {
            int placeSize = _placeViews.size();
            for(int placeNo = 0; placeNo < placeSize; placeNo++)
            {
                PlaceView placeView = _placeViews.get(placeNo);
                if(placeView != null)
                {
                    placeView
                            .setCurrentMarking(_markingVectorAnimationStorage[placeNo]);
                    setChanged();
                    notifyObservers(placeView);
                    setMatrixChanged();
                }
            }
        }
    }

    public void fireTransition(TransitionView transitionView)
    {
        if(transitionView != null)
        {
            if(transitionView.isEnabled() && _placeViews != null)
            {
                int transitionNo = _transitionViews.indexOf(transitionView);

                for(int placeNo = 0; placeNo < _placeViews.size(); placeNo++)
                {
                    for(MarkingView markingView : _placeViews.get(placeNo).getCurrentMarkingView())
                    {
                        TokenView tokenView = markingView.getToken();
                        int oldMarkingPositionInTheList = positionInTheList(tokenView.getID(), _currentMarkingVector[placeNo]);
                        int oldMarking = _currentMarkingVector[placeNo].get(oldMarkingPositionInTheList).getCurrentMarking();
                        int markingToBeAdded = tokenView.getIncidenceMatrix().get(placeNo, transitionNo);
                        markingView.setCurrentMarking(oldMarking + markingToBeAdded);
                    }
                    _placeViews.get(placeNo).repaint();
                }
                //变迁发生、token发生后，计算时间的问题
                double currentFiredTransitionDelay = transitionView.getDelay();
                LinkedList<MarkingView>[] markings = getCurrentMarkingVector();

                int len = this._currentDelayMatrix.length;
                boolean[] result = new boolean[len];
                double[] arr;
                //判断是否有新使能的变迁，还有当前变迁的delay也需要赋予一个新值
                result = areTransitionsEnabled(markings);//result要么全是瞬时变迁，要么全是非瞬时变迁
                TransitionView tra;
                int type;
                for(int i=0;i<len;i++){//给pre。。。添加set和get方法
                    //若这个变迁是瞬时变迁，只发生token的变化，对其他变迁不发生影响，重新进行使能判断即可；
                    arr = get_currentDelayMatrix();//将arr放在里面，每一次都要让它更新
                    //System.out.println(i+"  "+arr[i]);
                    tra = _transitionViews.get(i);
                    type = tra.getType();
                    if(type == 0)
                        break;
                    //若这个变迁不是瞬时变迁（说明不存在瞬时变迁，只有指数变迁和确定性变迁），需要刷新各使能变迁的时延值
                    // token发生变化后，应该讲变迁进行分类，持续使能的变迁减去当前变迁时间的值，不再进行新的赋值
                    //1、持续使能，目前正在发生的变迁可能是持续使能或者是新不使能
                    if(result[i] == true && preEnabledTransitions[i] ==true){
                        if(type != 0){//非瞬时变迁
                            arr[i] -= currentFiredTransitionDelay;//虽然是一个发生，但是变为0的变迁不止是一个
                            for(int j=0;j<len;j++){//刚发生的变迁，或者和它同等的变迁
                                TransitionView tr = getTransition(j);
                                if(arr[j] == 0 && tr.getType()!=0 && result[j] == true && preEnabledTransitions[j] == true){//将当前的arr保存到变迁中
                                    if(tr.getType() == 1)
                                        arr[j] = 1/tr.getRate();
                                    else if(tr.getType() == 2)
                                        arr[j] = tr.getRate();
                                }
                            }
                            /*if(tra.getId().equals(transitionView.getId())){
                                System.out.println(tra.getDelay());//TODO 验证？这里应该是0了
                                tra.setDelay(tra.getRate());
                            }*/
                        }

                    }
                    //2、新使能  //重新使能的变迁进行新的赋值---TODO 这里也应该减去刚才发生的那一段时间ma?
                    else if(result[i] == true && preEnabledTransitions[i] ==false){
                        if(type == 1){//指数
                            if(tra.getDelay() > 0){//如果之前使能过
                                arr[i] = tra.getDelay();
                            }else//之前没有使能过
                                arr[i] = 1 / tra.getRate();
                        }else if(type == 2){//确定变迁
                            if(tra.getDelay() > 0){//如果之前使能过
                              //  System.out.println("tra.getDelay():"+tra.getDelay());
                                arr[i] = tra.getDelay();
                            }
                            else {//之前没有使能过,使rate存储初始的delay值
                              //  System.out.println("tra.getRate():"+tra.getRate());
                                arr[i] = tra.getRate();//对于确定性时延需要重新修改
                            }
                        }
                    }
                    //3、新不使能
                    else if(result[i] == false && preEnabledTransitions[i] == true){
                        if(type != 0){//瞬时变迁，时延不发生变化
                            arr[i] -= currentFiredTransitionDelay;//让之前使能的减去发生的时间
                            //TODO 如果变为了0，还得把它重新赋值；前提得保证不是瞬时变迁，可能是多个发生了 1 1 1
                            for(int j=0;j<len;j++){//刚发生的变迁，或者和它同等的变迁
                                TransitionView tr = getTransition(j);
                                if(arr[j] == 0 && tr.getType()!=0 && result[j] == false && preEnabledTransitions[j] == true){//将当前的arr保存到变迁中
                                    if(tr.getType() == 1)
                                        arr[j] = 1/tr.getRate();
                                    else if(tr.getType() == 2)
                                        arr[j] = tr.getRate();
                                }
                            }
                            /*if(tra.getId().equals(transitionView.getId())){//刚发生的变迁
                                System.out.println(tra.getDelay());//TODO 验证？这里应该是0了--这里只是arr为0了，但是变迁的并没有变
                                tra.setDelay(tra.getRate());//这里好像没有设置好，这里给当前变迁设置好了，还用给arr设置吗？
                            }*/
                        }

                    }//4、持续不使能，无变化

                    storeCurrentDelay(arr);//把当前的时延矩阵重新存储起来
                }//for end

               /* if(transitionView.getType() != 0){//如果不是瞬时变迁
                    for(int i=0;i<len;i++){
                        if(arr[i] == -1)//说明这是瞬时变迁
                            break;
                        arr[i] -= currentFiredTransitionDelay;
                    }
                    //是不是应该给变迁加一个使能和pre使能的属性，用于判断是否为新使能的变迁
                    storeCurrentDelay(arr);//之后还需要更新什么吗？需不要要改变一下使能的属性？
                }*/
            }
        }
        setMatrixChanged();
    }

    //变迁发生规则
    public TransitionView getRandomTransition()
    {
        setEnabledTransitions();
        // All the enabled _transitions are of the same type:
        // a) all are immediate _transitions; 选择优先级最高的一个发生，若优先级相同，根据随机数来决定
        // b) or ，all are timed _transitions.所有的都是非瞬时变迁，选择一个时延最短的变迁，剩余的减去发生的时延
        //变迁发生规则
        ArrayList enabledTransitions = new ArrayList();
        ArrayList<Double> ArrayOfEnabledTransitionDelay = new ArrayList<Double>();
        double rate = 0;
        //1、都是瞬时变迁，靠优先级和权重（priority和weight）delay=0
        //2、    都是确定性变迁，靠delay
        //          都是指数变迁，靠rate---》转化为delay
        //          确定变迁和指数变迁
        for(TransitionView transitionView : _transitionViews)
        {//TODO 都是瞬时变迁的情况下，这里还用做吗
            if(transitionView.isEnabled())
            {
                enabledTransitions.add(transitionView);
                //将时延放入数组中
                ArrayOfEnabledTransitionDelay.add(transitionView.getDelay());

                rate += transitionView.getRate();
            }
        }

        // if there is only one enabled transition, return this transition
        if(enabledTransitions.size() == 1)
        {
            return (TransitionView) enabledTransitions.get(0);
        }
        //下面是指使能变迁的个数大于1的时候
        //1、全是瞬时变迁
        if(isAllImmTransition(_transitionViews)){//此时的优先级是一样的，通过权重来选择一个瞬时变迁，这里用rate表示的weight
            double random = _model.getRandomNumber().nextDouble();
            double x = 0;
            for(Object enabledTransition : enabledTransitions)
            {
                TransitionView t = (TransitionView) enabledTransition;

                x += t.getRate() / rate;

                if(random < x)//在x达到1之前总会得到一个合适的变迁出现
                {
                    return t;
                }
            }
        }else{//2、不是瞬时变迁的情况下，选择一个时延最短的变迁
            int len = this._currentDelayMatrix.length;
            int index;
            double value;
            //对数组进行复制-----这里感觉需要修改，get方法？----TODO 这里复制的或者下面操作的应该是那些使能的
            Double[] a = (Double[])ArrayOfEnabledTransitionDelay.toArray(new Double[enabledTransitions.size()]);
           // System.out.println(a[0]);
            //对时延数组进行排序，得到数组下标，返回最小时延所对应的那个变迁
            Arrays.sort(a);
            //找到合适的最小值
            value = minValue(a);
            //找到对应的下标，可能有多个，需要进行一个选择
            index = findRandomIndex(_currentDelayMatrix,value);//TODO 这里如果有多个相同的值
            if(index ==-1)
                return null;
            return this.getTransition(index);
        }
        // no enabled transition found, so no transition can be fired
        return null;
    }
    private int findRandomIndex(double[] arr,double value){//当有多个相同的时候，随机选择一个返回
        int len = arr.length;
        ArrayList list = new ArrayList();
        for(int i=0;i<len;i++){
            if(arr[i] == value && _transitionViews.get(i).isEnabled()){//TODO
               list.add(i);
            }
        }
        int ll = list.size();
        if(ll == 1)
            return (int)list.get(0);
        else if(ll > 1){
            Random r = new Random();
            int a = r.nextInt(ll);
            return a;
        }
        else
            return -1;
    }
    private double minValue(Double[] arr){//
        int len = arr.length;
        for(int i=0;i<len;i++){
          //  System.out.println(arr[i]);
            if(arr[i] == 0){//TODO 这里将-1改为了0，即设瞬时变迁默认值为0
                break;
            }else//第一个不是-1的值就是最小时延
                return arr[i];
        }
        return -1;
    }
    //判断是否全为瞬时变迁
    private boolean isAllImmTransition(ArrayList<TransitionView> _transitionViews){
        boolean flag = true;
        int count = 0;
        for(TransitionView transitionView : _transitionViews)
        {
            if(transitionView.isEnabled() && transitionView.getType()==0){
                count++;//变迁使能而且是瞬时变迁
            }
            if(transitionView.isEnabled() && transitionView.getType()!=0)
            {//变迁使能，但不是瞬时变迁
               flag =  false;
               return flag;
            }
        }
        if(count == 0){
            flag = false;
    }
        return flag;
    }
    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getEnabledTransitions()
      */
    public ArrayList<TransitionView> getEnabledTransitions()
    {
        setEnabledTransitions();
        // All the enabled _transitions are of the same type:
        // a) all are immediate _transitions; or
        // b) all are timed _transitions.

        ArrayList enabledTransitions = new ArrayList();
        for(TransitionView transitionView : _transitionViews)
        {
            if(transitionView.isEnabled())
            {
                enabledTransitions.add(transitionView);
            }
        }

        return enabledTransitions;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#fireTransitionBackwards(pipe.views.Transition)
      */

    public void fireTransitionBackwards(TransitionView transitionView)
    {
        if(transitionView != null)
        {
            setEnabledTransitionsBackwards();
            if(transitionView.isEnabled() && _placeViews != null)
            {
                int transitionNo = _transitionViews.indexOf(transitionView);
                for(int placeNo = 0; placeNo < _placeViews.size(); placeNo++)
                {
                    for(MarkingView m : _placeViews.get(placeNo)
                            .getCurrentMarkingView())
                    {
                        int oldMarkingPos = positionInTheList(m.getToken()
                                                                      .getID(), _currentMarkingVector[placeNo]);
                        int oldMarking = _currentMarkingVector[placeNo].get(
                                oldMarkingPos).getCurrentMarking();
                        int markingToBeSubtracted = m.getToken()
                                .getIncidenceMatrix()
                                .get(placeNo, transitionNo);
                        m.setCurrentMarking(oldMarking - markingToBeSubtracted);
                    }
                    _placeViews.get(placeNo).repaint();
                }
            }
        }
        setMatrixChanged();
    }

    /*
      * Method not used * / public void fireRandomTransitionBackwards() {
      * setEnabledTransitionsBackwards(); int transitionsSize =
      * _transitions.size() * _transitions.size() *
      * _transitions.size(); int randomTransitionNumber = 0; Transition
      * randomTransition = null; do { randomTransitionNumber =
      * _randomNumber.nextInt(_transitions.size()); randomTransition =
      * (Transition)_transitions.get(randomTransitionNumber);
      * transitionsSize--; if(transitionsSize <= 0){ break; } } while(!
      * randomTransition.isEnabled()); fireTransitionBackwards(randomTransition);
      * // System.out.println("Random Fired Transition Backwards" +
      * ((Transition)_transitions.get(randonTransition)).getId()); }
      */

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#resetEnabledTransitions()
      */
    public void resetEnabledTransitions()
    {
        for(TransitionView transitionView : _transitionViews)
        {
            transitionView.setEnabled(false);
            setChanged();
            notifyObservers(transitionView);
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#isTransitionEnabled(java.util.LinkedList, int)
      */
    public boolean isTransitionEnabled(LinkedList<MarkingView>[] markings,
                                       int transition)
    {
        int transCount = this.numberOfTransitions();
        int placeCount = this.numberOfPlaces();
        boolean[] result = new boolean[transCount];
        int[][] CMinus;

        // initialise matrix to true
        for(int k = 0; k < transCount; k++)
        {
            result[k] = true;
        }
        for(int i = 0; i < transCount; i++)
        {
            for(int j = 0; j < placeCount; j++)
            {
                boolean allTokenClassesEnabled = true;
                for(MarkingView m : markings[j])
                {
                    CMinus = (m.getToken()).getBackwardsIncidenceMatrix(_arcViews, _transitionViews, _placeViews);
                    if(m.getCurrentMarking() < CMinus[j][i])
                    {
                        result[i] = false;
                        break;
                    }
                }
            }
        }

        return result[transition];
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#areTransitionsEnabled(java.util.LinkedList)
      */
    public final boolean[] areTransitionsEnabled(LinkedList<MarkingView>[] markings)
    {
        int transitionCount = numberOfTransitions();
        int placeCount = numberOfPlaces();
        boolean[] result = new boolean[transitionCount];
        boolean hasTimed = false;
        boolean hasImmediate = false;

        int maxPriority = 0;

        for(int i = 0; i < transitionCount; i++)
        {
            result[i] = true; // inicialitzam a enabled
            for(int j = 0; j < placeCount; j++)
            {
                boolean allTokenClassesEnabled = true;
                int totalMarkings = 0;
                int totalForwardIncidenceMarkings = 0;
                int totalBackwardIncidenceMarkings = 0;
                for(MarkingView m : markings[j])
                {
                    totalMarkings += m.getCurrentMarking();
                    totalForwardIncidenceMarkings += (m.getToken()).getForwardsIncidenceMatrix().get(j, i);
                    totalBackwardIncidenceMarkings += (m.getToken()).getBackwardsIncidenceMatrix().get(j, i);
                    if((m.getCurrentMarking() < (m.getToken())
                            .getBackwardsIncidenceMatrix().get(j, i))
                            && (m.getCurrentMarking() != -1))
                    {
                        allTokenClassesEnabled = false;
                        break;

                    }
                    // inhibitor arcs
                    if(m.getToken().getInhibitionMatrix().get(j, i) > 0
                            && m.getCurrentMarking() >= m.getToken()
                            .getInhibitionMatrix().get(j, i))
                    {
                        // an inhibitor arc prevents the firing of this
                        // transition so
                        // the transition is not enabled
                        allTokenClassesEnabled = false;
                        break;
                    }
                }
                // capacities
                if(allTokenClassesEnabled && (_capacityMatrix[j] > 0) && (totalMarkings
                        + totalForwardIncidenceMarkings -
                        totalBackwardIncidenceMarkings > _capacityMatrix[j]))
                { // firing this transition would break a capacity
                    // restriction so the transition is not enabled
                    allTokenClassesEnabled = false;
                }


                if(!allTokenClassesEnabled)
                {
                    result[i] = false;
                    break;
                }
            }

            // we look for the highest priority of the enabled _transitions
            if(result[i])
            {
                TransitionView t = _transitionViews.get(i);
                if(t.getType() != 0)
                {
                    hasTimed = true;
                }
                else
                {
                    hasImmediate = true;
                    if(t.getPriority() > maxPriority)
                    {
                        maxPriority = t.getPriority();
                    }
                }
            }
        }

        // Now make sure that if any of the enabled _transitions are immediate
        // _transitions, only they can fire as this must then be a vanishing
        // state.
        // - disable the immediate _transitions with lower priority.
        // - disable all timed _transitions if there is an immediate transition
        // enabled.
        for(int i = 0; i < transitionCount; i++)
        {
            TransitionView t = _transitionViews.get(i);
            if(t.getType() == 0 && t.getPriority() < maxPriority)
            {
                result[i] = false;
            }
            if(hasTimed && hasImmediate)
            {
                if(t.getType() !=0)
                {
                    result[i] = false;
                }
            }
        }

        // print("areTransitionsEnabled: ",result);//debug
        return result;
    }

    // }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#setEnabledTransitionsBackwards()
      */
    public void setEnabledTransitionsBackwards()
    {

        if(_currentMarkingVectorChanged)
        {
            createMatrixes();
        }

        boolean[] enabledTransitions = getTransitionEnabledStatusArray(this.getTransitionViews(), this.getCurrentMarkingVector(),
                true, this.getCapacityMatrix(), this.numberOfPlaces(), this.numberOfTransitions());

        for(int i = 0; i < enabledTransitions.length; i++)
        {
            TransitionView transitionView = _transitionViews.get(i);
            if(enabledTransitions[i] != transitionView.isEnabled())
            {
                transitionView.setEnabled(enabledTransitions[i]);
                setChanged();
                notifyObservers(transitionView);
            }
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#setEnabledTransitions()
      */
    public void setEnabledTransitions()
    {
        if(_currentMarkingVectorChanged)
        {
            createMatrixes();
        }
        //得到使能的变迁
        boolean[] enabledTransitions = getTransitionEnabledStatusArray(this.getTransitionViews(), this.getCurrentMarkingVector(),
                false, this.getCapacityMatrix(), this.numberOfPlaces(), this.numberOfTransitions());
        //同步变迁相对应的View类
        for(int i = 0; i < enabledTransitions.length; i++)
        {
            TransitionView transitionView = _transitionViews.get(i);
            if(enabledTransitions[i] != transitionView.isEnabled())
            {
                transitionView.setEnabled(enabledTransitions[i]);
                setChanged();
                notifyObservers(transitionView);
            }
        }
    }
    //测试用，输出二维数组
    public void printf(int a[][]){

        for(int i =0;i<a.length;i++){//遍历行数
            for(int j= 0;j<a[i].length;j++){//遍历列数
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
    }
    public void printf(LinkedList<MarkingView>[] a){

        for(int i =0;i<a.length;i++){//遍历行数
            for(int j= 0;j<a[i].size();j++){//遍历列数
                System.out.print(a[i].get(j).getCurrentMarking()+" ");
            }
            System.out.println();
        }
    }

    private boolean[] getTransitionEnabledStatusArray(final TransitionView[] transArray,
            final LinkedList<MarkingView>[] markings, boolean backwards,
            final int capacities[], final int placeCount, final int transitionCount)
    {
        boolean[] result = new boolean[transitionCount];
        boolean hasTimed = false;
        boolean hasImmediate = false;

        int maxPriority = 0;

        for(int i = 0; i < transitionCount; i++)
        {
            result[i] = true; // inicialitzam a enabled
            for(int j = 0; j < placeCount; j++)
            {
                boolean allTokenClassesEnabled = true;
                int totalMarkings = 0;
                int totalCPlus = 0;
                int totalCMinus = 0;
                System.out.println(" markings:"+markings.length);


                System.out.println(" markings[j].size()):"+markings[j].size());//表示的是


                for(MarkingView m : markings[j])
                {
                    int[][] CMinus;
                    int[][] CPlus;
                    int[][] inhibition;
                    //输出这个m的全部内容
                   /* if(m!=null){
                        System.out.println("---------------------------");
                        //System.out.println(m.getToolTipText()) ;
                        System.out.println(m.getToken().getID()+":"+m.getCurrentMarking());
                        printf(CMinus);
                        //System.out.println(m.getName());
                        //System.out.println(m.getForeground().toString());
                    }*/
                        //System.out.println(m.getToolTipText() + "," + m.getCurrentMarking() +"," +m.getName() + "," +/* m.getToken().toString() +*/","+m.getForeground().toString());
                    if(backwards)
                    {
                        CMinus = m.getToken().getForwardsIncidenceMatrix(_arcViews, _transitionViews, _placeViews);
                        CPlus = m.getToken().getBackwardsIncidenceMatrix(_arcViews, _transitionViews, _placeViews);
                    }
                    else
                    {
                        CPlus = m.getToken().getForwardsIncidenceMatrix(_arcViews, _transitionViews, _placeViews);
                        //System.out.println("PetriNetView的getTransitionEnable...的CPlus矩阵值");
                        //printf(CPlus);
                        CMinus = m.getToken().getBackwardsIncidenceMatrix(_arcViews, _transitionViews, _placeViews);
                         //System.out.println("PetriNetView的getTransitionEnable...的CMinus矩阵值");
                         // printf(CMinus);
                        if(m!=null && m.getToken().getID().equals("Default")){
                            System.out.println("---------------------------");
                            System.out.println(m.getToken().getID()+":"+m.getCurrentMarking());
                            //printf(CMinus);
                            printf(markings);
                         }
                    }
                    inhibition = m.getToken().getInhibitionMatrix(_inhibitorViews, _transitionViews, _placeViews);

                    if((m.getCurrentMarking() < CMinus[j][i]) && (m.getCurrentMarking() != -1))
                    {
                        allTokenClassesEnabled = false;
                        break;
                    }
                    // capacities
                    totalMarkings += m.getCurrentMarking();//第j个库所到第i个变迁（+1）
                    totalCPlus += (m.getToken()).getForwardsIncidenceMatrix().get(j, i);
                    totalCMinus += (m.getToken()).getBackwardsIncidenceMatrix().get(j, i);
                    //System.out.println("_capacityMatrix[j]:"+_capacityMatrix[j]);
                    if(allTokenClassesEnabled && (_capacityMatrix[j] > 0) &&
                            (totalMarkings + totalCPlus - totalCMinus > _capacityMatrix[j]))
                    { // firing this transition would break a capacity
                        // restriction so the transition is not enabled
                        allTokenClassesEnabled = false;
                    }

                    // inhibitor arcs
                    if(inhibition[j][i] > 0 && m.getCurrentMarking() >= inhibition[j][i])
                    {
                        // an inhibitor arc prevents the firing of this
                        // transition
                        // so
                        // the transition is not enabled
                        allTokenClassesEnabled = false;
                        break;
                    }
                }
                System.out.println("++++++++++++++++++++++++");
                if(!allTokenClassesEnabled)
                {
                    result[i] = false;
                    break;
                }
            }//j结束
            // we look for the highest priority of the enabled _transitions
            if(result[i])
            {
                if(transArray[i].getType()!=0)
                {
                    hasTimed = true;
                }
                else
                {
                    hasImmediate = true;
                    if(transArray[i].getPriority() > maxPriority)
                    {
                        maxPriority = transArray[i].getPriority();
                    }
                }
            }

        }
        // Now make sure that if any of the enabled _transitions are immediate
        // _transitions, only they can fire as this must then be a vanishing state.
        // - disable the immediate _transitions with lower priority.
        // - disable all timed _transitions if there is an immediate transition
        // enabled.
        for(int i = 0; i < transitionCount; i++)
        {
            if((transArray[i].getType() == 0) && transArray[i].getPriority() < maxPriority)
            {
                result[i] = false;
            }
            if(hasTimed && hasImmediate)
            {
                if(transArray[i].getType()!=0)
                {
                    result[i] = false;
                }
            }
        }
        preEnabledTransitions = result;//用于保存上一次使能的变迁
        // print("areTransitionsEnabled: ",result);//debug
        return result;
    }

    /**
     * Empty all attributes, turn into empty Petri-Net
     */
    private void emptyPNML()
    {
        _model.resetPNML();
        _placeViews = null;
        _transitionViews = null;
        _arcViews = null;
        _labels = null;
        _rateParameters = null;
        _initialMarkingVector = null;
        _tokenViews = null;
        _arcsMap = null;
        initializeMatrices();
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#places()
      */
    public PlaceView[] places()
    {
        PlaceView[] returnArray = new PlaceView[_placeViews.size()];

        for(int i = 0; i < _placeViews.size(); i++)
        {
            returnArray[i] = _placeViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlacesArrayList()
      */
    public ArrayList<PlaceView> getPlacesArrayList()
    {
        return _placeViews;
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#numberOfPlaces()
      */
    public int numberOfPlaces()
    {
        if(_placeViews == null)
        {
            return 0;
        }
        else
        {
            return _placeViews.size();
        }
    }

    /* wjk added 03/10/2007 */
    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#marking()
      */
    public LinkedList<MarkingView>[] marking()
    {
        LinkedList<MarkingView>[] result = new LinkedList[_placeViews.size()];

        for(int i = 0; i < _placeViews.size(); i++)
        {
            result[i] = (LinkedList<MarkingView>) Copier.deepCopy(((PlaceView) _placeViews.get(i)).getCurrentMarkingView());
        }
        return result;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#labels()
      */
    public AnnotationNote[] labels()
    {
        AnnotationNote[] returnArray = new AnnotationNote[_labels.size()];

        for(int i = 0; i < _labels.size(); i++)
        {
            returnArray[i] = _labels.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#markingRateParameters()
      */
    public RateParameter[] markingRateParameters()
    {
        RateParameter[] returnArray = new RateParameter[_rateParameters
                .size()];

        for(int i = 0; i < _rateParameters.size(); i++)
        {
            returnArray[i] = _rateParameters.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitions()
      */
    public TransitionView[] getTransitionViews()
    {
        TransitionView[] returnArray = new TransitionView[_transitionViews.size()];

        for(int i = 0; i < _transitionViews.size(); i++)
        {
            returnArray[i] = _transitionViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionsArrayList()
      */
    public ArrayList<TransitionView> getTransitionsArrayList()
    {
        return _transitionViews;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#numberOfTransitions()
      */
    public int numberOfTransitions()
    {
        if(_transitionViews == null)
        {
            return 0;
        }
        else
        {
            return _transitionViews.size();
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#arcs()
      */
    public ArcView[] arcs()
    {
        ArcView[] returnArray = new ArcView[_arcViews.size()];

        for(int i = 0; i < _arcViews.size(); i++)
        {
            returnArray[i] = _arcViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getArcsArrayList()
      */
    public ArrayList<ArcView> getArcsArrayList()
    {
        return _arcViews;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#inhibitors()
      */
    public InhibitorArcView[] inhibitors()
    {
        InhibitorArcView[] returnArray = new InhibitorArcView[_inhibitorViews.size()];

        for(int i = 0; i < _inhibitorViews.size(); i++)
        {
            returnArray[i] = _inhibitorViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getInhibitorsArrayList()
      */
    public ArrayList<InhibitorArcView> getInhibitorsArrayList()
    {
        return _inhibitorViews;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionById(java.lang.String)
      */


    public LinkedList<MarkingView>[] getInitialMarkingVector()
    {
        if(_initialMarkingVectorChanged)
            createInitialMarkingVector();
        return _initialMarkingVector;
    }

    public LinkedList<MarkingView>[] getCurrentMarkingVector()
    {
        if(_currentMarkingVectorChanged)
        {
            createCurrentMarkingVector();
        }
        return _currentMarkingVector;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getCapacityMatrix()
      */
    public int[] getCapacityMatrix()
    {
        createCapacityVector();
        return _capacityMatrix;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPriorityMatrix()
      */
    public int[] getPriorityMatrix()
    {
        createPriorityVector();
        return _priorityMatrix;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTimedMatrix()
      */
    public boolean[] getTimedMatrix()
    {
        createTimedVector();
        return _timedMatrix;
    }

    private void setMatrixChanged()
    {
        for(TokenView tc : _tokenViews)
        {
            if(tc.getForwardsIncidenceMatrix() != null)
            {
                tc.getForwardsIncidenceMatrix().matrixChanged = true;
            }
            if(tc.getBackwardsIncidenceMatrix() != null)
            {
                tc.getBackwardsIncidenceMatrix().matrixChanged = true;
            }
            if(tc.getIncidenceMatrix() != null)
            {
                tc.getIncidenceMatrix().matrixChanged = true;
            }
            if(tc.getInhibitionMatrix() != null)
            {
                tc.getInhibitionMatrix().matrixChanged = true;
            }
        }
        _initialMarkingVectorChanged = true;
        _currentMarkingVectorChanged = true;
    }

    public int NumberOfGraphElement(NodeList nodeList,String Graphelement){
        int len = nodeList.getLength();
        Node node;
        Element element;
        int placeCount = 0;
        int transitionCount = 0;
        int edgeCount = 0;
        for(int i=0;i<len;i++){
            node = nodeList.item(i);
            if(node instanceof Element)
            {
                element = (Element) node;
                if("place".equals(element.getNodeName()))
                    placeCount++;
                else if("transition".equals(element.getNodeName()))
                    transitionCount++;
                else if("arc".equals(element.getNodeName()))
                   edgeCount++;
            }
        }
        if("vexNode".equals(Graphelement))
            return (placeCount + transitionCount);
        else if("edgeNode".equals(Graphelement))
            return edgeCount;
        return 0;
    }

    public ArrayOfGraph getArrayOfGraph() {
        return arrayOfGraph;
    }

    public void setArrayOfGraph(ArrayOfGraph arrayOfGraph) {
        this.arrayOfGraph = arrayOfGraph;
    }

    /* (non-Javadoc)
          * @see pipe.models.interfaces.IPetriNet#createFromPNML(org.w3c.dom.Document)
          */
    public void createFromPNML(Document PNMLDoc)
    {
        emptyPNML();
        Element element;
        Node node;
        NodeList nodeList;

        try
        {
            nodeList = PNMLDoc.getDocumentElement().getChildNodes();
            if(ApplicationSettings.getApplicationView() != null)
            {
                // Notifies used to indicate new instances.
                ApplicationSettings.getApplicationModel().setMode(Constants.CREATING);
            }
            System.out.println("Loading...");
            //统计边和节点的数量

            //int vexLen = NumberOfGraphElement(nodeList,"vexNode");
            int edgeLen = NumberOfGraphElement(nodeList,"edgeNode");
            //arrayOfGraph.setEdgeLen(edgeLen);
            arrayOfGraph = new ArrayOfGraph(edgeLen);
            HashMap<String,Object> vexMap = arrayOfGraph.getVexsMap();//这个可以放到
            //String[][] edges = new String[edgeLen][2];
            String[][] edges = arrayOfGraph.getEdges();

            //int vexCount = 0;
            int edgeCount = 0;
            //System.out.println(nodeList.getLength());
            for(int i = 0; i < nodeList.getLength(); i++)
            {
                node = nodeList.item(i);
                //System.out.println(node.getNodeName());
                if(node instanceof Element)
                {
                    element = (Element) node;
                    if("labels".equals(element.getNodeName()))
                    {
                        addAnnotation(createAnnotation(element));
                    }
                    else if("definition".equals(element.getNodeName()))
                    {
                        Note note = createParameter(element);
                        if(note instanceof RateParameter)
                        {
                            addAnnotation((RateParameter) note);
                        }
                    }
                    else if("place".equals(element.getNodeName()))
                    {
                        PlaceView placeView = createPlace(element);
                        //获得它的id并存储起来，还得想办法区分库所和变迁，使用HashMap怎么样？
                        vexMap.put(placeView.getId(),placeView);
                        addPlace(placeView);
                    }
                    else if("transition".equals(element.getNodeName()))
                    {
                        TransitionView transitionView = createTransition(element);
                        vexMap.put(transitionView.getId(),transitionView);
                        addTransition(transitionView);
                    }
                    else if("arc".equals(element.getNodeName()))
                    {
                        //System.out.println("PetriNetView2498" + element);
                        //System.out.println("PetriNetView2499" + element.getNodeName());
                        ArcView newArcView = createArc(element);

                        edges[edgeCount][0] = newArcView.getSource().getId();//TODO 看看对不对
                        edges[edgeCount][1] = newArcView.getTarget().getId();
                        edgeCount++;

                        if(newArcView instanceof InhibitorArcView)
                        {
                            addArc((InhibitorArcView) newArcView);
                        }
                        else
                        {
                            addArc((NormalArcView) newArcView);
                            checkForInverseArc((NormalArcView) newArcView);
                        }
                    }
                    else if("stategroup".equals(element.getNodeName()))
                    {
                        addStateGroup(createStateGroup(element));
                    }
                    else if("token".equals(element.getNodeName()))
                    {
                        addToken(createToken(element));
                    }
                    else
                    {
                        System.out.println("!" + element.getNodeName());
                    }

                }
            }
            //Agraph.setVexs(vexMap);
            //Agraph.setEdges(edges);
            //测试是否成功构建了petri网对应的图
            //Set<String> set = vexMap.keySet();
            arrayOfGraph.setVexsMap(vexMap);
            arrayOfGraph.setEdges(edges);//将数据保存

           /* //这些应该可以转移了
            Graph graph = new Graph();
            graph.buildGraph(arrayOfGraph.getVexsMap(),arrayOfGraph.getEdges());

            graph.printGraph();

            FindAllPath findAllPath = new FindAllPath();
            findAllPath.visit(graph,"P0","P3");
            */
            if(ApplicationSettings.getApplicationView() != null)
            {
                ApplicationSettings.getApplicationModel().restoreMode();
            }
            System.out.println("Done");
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a StateGroup object from a DOM element
     *
     * @param inputStateGroupElement input state group DOM Element
     * @return StateGroup Object
     */
    private StateGroup createStateGroup(Element inputStateGroupElement)
    {
        // Create the state group with name and id
        String id = inputStateGroupElement.getAttribute("id");
        String name = inputStateGroupElement.getAttribute("name");
        StateGroup newGroup = new StateGroup(id, name);

        Node node;
        NodeList nodelist;
        StringTokenizer tokeniser;
        nodelist = inputStateGroupElement.getChildNodes();

        // If this state group contains states then add them
        if(nodelist.getLength() > 0)
        {
            for(int i = 1; i < nodelist.getLength() - 1; i++)
            {
                node = nodelist.item(i);
                if(node instanceof Element)
                {
                    Element element = (Element) node;
                    if("statecondition".equals(element.getNodeName()))
                    {
                        // Loads the condition in the form "P0 > 4"
                        String condition = element.getAttribute("value");
                        // Now we tokenise the elements of the condition
                        // (i.e. "P0" ">" "4") to create a state
                        tokeniser = new StringTokenizer(condition);
                        newGroup.addState(tokeniser.nextToken(), tokeniser
                                .nextToken(), tokeniser.nextToken());
                    }
                }
            }
        }
        return newGroup;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getStateGroups()
      */
    public StateGroup[] getStateGroups()
    {
        StateGroup[] returnArray = new StateGroup[_stateGroups.size()];
        for(int i = 0; i < _stateGroups.size(); i++)
        {
            returnArray[i] = (StateGroup) _stateGroups.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getStateGroupsArray()
      */
    public ArrayList<StateGroup> getStateGroupsArray()
    {
        return this._stateGroups;
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#print()
      */
    public void print()
    {
        System.out.println("No of Places = " + _placeViews.size() + "\"");
        System.out.println("No of Transitions = " + _transitionViews.size()
                                   + "\"");
        System.out.println("No of Arcs = " + _arcViews.size() + "\"");
        System.out.println("No of Labels = " + _labels.size()
                                   + "\" (Model View Controller Design Pattern)");
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#existsRateParameter(java.lang.String)
      */
    public boolean existsRateParameter(String name)
    {
        return _rateParameterHashSet.contains(name);
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#changeRateParameter(java.lang.String, java.lang.String)
      */
    public boolean changeRateParameter(String oldName, String newName)
    {
        if(_rateParameterHashSet.contains(newName))
        {
            return false;
        }
        _rateParameterHashSet.remove(oldName);
        _rateParameterHashSet.add(newName);
        return true;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#hasTimedTransitions()
      */
    public boolean hasTimedTransitions()
    {
        TransitionView[] transitionViews = this.getTransitionViews();
        int transCount = transitionViews.length;

        for(int i = 0; i < transCount; i++)
        {
            if(transitionViews[i].getType() !=0 )
            {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#hasImmediateTransitions()
      */
    public boolean hasImmediateTransitions()
    {
        TransitionView[] transitionViews = this.getTransitionViews();
        int transCount = transitionViews.length;

        for(int i = 0; i < transCount; i++)
        {
            if(transitionViews[i].getType() == 0)
            {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#isTangibleState(java.util.LinkedList)
      */
    public boolean isTangibleState(LinkedList<MarkingView>[] marking)
    {
        TransitionView[] trans = this.getTransitionViews();
        int numTrans = trans.length;
        boolean hasTimed = false;
        boolean hasImmediate = false;

        for(int i = 0; i < numTrans; i++)
        {
            if(this.isTransitionEnabled(marking, i))
            {
                if(trans[i].getType()!=0)
                {
                    // If any immediate transtions exist, the state is vanishing
                    // as they will fire immediately
                    hasTimed = true;
                }
                else if(trans[i].getType() == 0)
                {
                    hasImmediate = true;
                }
            }
        }
        return (hasTimed && !hasImmediate);
    }

    private void checkForInverseArc(NormalArcView newArcView)
    {
        Iterator iterator = newArcView.getSource().getConnectToIterator();

        ArcView anArcView;
        while(iterator.hasNext())
        {
            anArcView = (ArcView) iterator.next();
            if(anArcView.getTarget() == newArcView.getSource()
                    && anArcView.getSource() == newArcView.getTarget())
            {
                if(anArcView.getClass() == NormalArcView.class)
                {
                    if(!newArcView.hasInverse())
                    {
                        ((NormalArcView) anArcView).setInverse(newArcView,
                                                               Constants.JOIN_ARCS);
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionName(int)
      */
    public String getTransitionName(int i)
    {
        return _transitionViews.get(i).getName();
    }

    // Function to check the structure of the Petri Net to ensure that if tagged
    // arcs are included then they obey the restrictions on how they can be used
    // (i.e. a transition may only have one input tagged Arc and one output
    // tagged Arc and if it has one it must have the other).
    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#validTagStructure()
      */
    public boolean validTagStructure()
    {
        ArrayList inputArcsArray = new ArrayList();
        ArrayList outputArcsArray = new ArrayList();

        TransitionView currentTrans;
        NormalArcView currentArcView;

        boolean taggedNet = false;
        boolean taggedTransition;
        boolean taggedInput;
        boolean taggedOutput;
        boolean validStructure = true;
        String checkResult;
        int noTaggedInArcs;
        int noTaggedOutArcs;

        checkResult = "Tagged structure validation result:\n";

        if(_transitionViews != null && _transitionViews.size() > 0)
        {
            // we need to check all the arcs....
            for(TransitionView _transitionView : _transitionViews)
            {
                currentTrans = _transitionView;
                taggedTransition = false;
                taggedInput = false;
                taggedOutput = false;
                // invalidStructure = false;
                noTaggedInArcs = 0;
                noTaggedOutArcs = 0;
                inputArcsArray.clear();
                outputArcsArray.clear();

                // we must:
                // i) find the arcs attached to this transition
                // ii) determine whether they are input arcs or output arcs
                // iii) check that if there is one tagged input arc there is
                // also
                // one output arc

                if(_arcViews != null && _arcViews.size() > 0)
                {
                    for(ArcView _arcView : _arcViews)
                    {
                        currentArcView = (NormalArcView) _arcView;
                        if(currentArcView.getSource() == currentTrans)
                        {
                            outputArcsArray.add(currentArcView);
                            if(currentArcView.isTagged())
                            {
                                taggedNet = true;
                                taggedTransition = true;
                                taggedOutput = true;
                                noTaggedOutArcs++;
                                if(noTaggedOutArcs > 1)
                                {
                                    checkResult = checkResult + "  Transition "
                                            + currentTrans.getName()
                                            + " has more than one"
                                            + " tagged output arc\n";
                                    validStructure = false;
                                }
                            }
                        }
                        else if(currentArcView.getTarget() == currentTrans)
                        {
                            inputArcsArray.add(currentArcView);
                            if(currentArcView.isTagged())
                            {
                                taggedNet = true;
                                taggedTransition = true;
                                taggedInput = true;
                                noTaggedInArcs++;
                                if(noTaggedInArcs > 1)
                                {
                                    checkResult = checkResult + "  Transition "
                                            + currentTrans.getName()
                                            + " has more than one"
                                            + " tagged input arc\n";
                                    validStructure = false;
                                }
                            }
                        }
                    }
                }

                // we have now built lists of input arcs and output arcs and
                // verified that there is at most one of each.
                // we must check, however, that if there is a tagged input there
                // is
                // a tagged output and vice-versa
                if(taggedTransition)
                {
                    if((taggedInput && !taggedOutput)
                            || (!taggedInput && taggedOutput))
                    {
                        checkResult = checkResult + "  Transition "
                                + currentTrans.getName()
                                + " does not have matching tagged arcs\n";
                        validStructure = false;
                    }
                }
            }
        }

        // if we reach the end with validStructure still true then everything
        // must
        // be OK!
        if(validStructure)
        {
            // System.out.println("Tagged arc structure is valid");
            checkResult = "Tagged structure validation result:\n  Tagged arc structure is valid\n";
            JOptionPane.showMessageDialog(null, checkResult,
                                          "Validation Results", JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            JOptionPane.showMessageDialog(null, checkResult,
                                          "Validation Results", JOptionPane.ERROR_MESSAGE);
        }

        // System.out.println(checkResult);

        return validStructure;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#checkTransitionIDAvailability(java.lang.String)
      */
    public boolean checkTransitionIDAvailability(String newName)
    {
        for(TransitionView _transitionView : _transitionViews)
        {
            if(_transitionView.getId()
                    .equals(newName))
            {
                // ID/name isn't available
                return false;
            }
        }
        // ID/name is available
        return true;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#checkPlaceIDAvailability(java.lang.String)
      */
    public boolean checkPlaceIDAvailability(String newName)
    {
        for(PlaceView _placeView : _placeViews)
        {
            if(_placeView.getId().equals(newName))
            {
                // ID/name isn't available
                return false;
            }
        }
        // ID/name is available
        return true;
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlaceIndex(java.lang.String)
      */
    public int getPlaceIndex(String placeName)
    {
        int index = -1;
        for(int i = 0; i < _placeViews.size(); i++)
        {
            if(_placeViews.get(i).getId().equals(placeName))
            {
                index = i;
                break;
            }
        }
        //		System.out.println("Returning " + index);

        return index;
    }

    // Added for passage time analysis of tagged nets
    /*use to check if structure contain any tagged token or tagged arc, then the structure
      * needs to be validated before animation
      */
    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#hasValidatedStructure()
      */
    public boolean hasValidatedStructure()
    {

        boolean tagged = false;

        for(ArcView _arcView : this._arcViews)
        {
            if(_arcView.isTagged())
                tagged = true;
        }

        if(tagged && _model.isValidated()) return true;
        else return !tagged;


    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#setValidate(boolean)
      */
    public void setValidate(boolean valid)
    {
        _model.setValidated(valid);
    }

    @Override
    public void update()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public TransitionView getTransitionById(String transitionID)
    {
        TransitionView returnTransitionView = null;

        if(_transitionViews != null)
        {
            if(transitionID != null)
            {
                for(TransitionView _transitionView : _transitionViews)
                {
                    if(transitionID
                            .equalsIgnoreCase(_transitionView.getId()))
                    {
                        returnTransitionView = _transitionView;
                    }
                }
            }
        }
        return returnTransitionView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionByName(java.lang.String)
      */
    public TransitionView getTransitionByName(String transitionName)
    {
        TransitionView returnTransitionView = null;

        if(_transitionViews != null)
        {
            if(transitionName != null)
            {
                for(TransitionView _transitionView : _transitionViews)
                {
                    if(transitionName
                            .equalsIgnoreCase(_transitionView.getName()))
                    {
                        returnTransitionView = _transitionView;
                    }
                }
            }
        }
        return returnTransitionView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransition(int)
      */
    public TransitionView getTransition(int transitionNo)
    {
        TransitionView returnTransitionView = null;

        if(_transitionViews != null)
        {
            if(transitionNo < _transitionViews.size())
            {
                returnTransitionView = _transitionViews
                        .get(transitionNo);
            }
        }
        return returnTransitionView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlaceById(java.lang.String)
      */
    public PlaceView getPlaceById(String placeID)
    {
        PlaceView returnPlaceView = null;

        if(_placeViews != null)
        {
            if(placeID != null)
            {
                for(PlaceView _placeView : _placeViews)
                {
                    if(placeID.equalsIgnoreCase(_placeView
                                                        .getId()))
                    {
                        returnPlaceView = _placeView;
                    }
                }
            }
        }
        return returnPlaceView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlaceByName(java.lang.String)
      */
    public PlaceView getPlaceByName(String placeName)
    {
        PlaceView returnPlaceView = null;

        if(_placeViews != null)
        {
            if(placeName != null)
            {
                for(PlaceView _placeView : _placeViews)
                {
                    if(placeName.equalsIgnoreCase(_placeView
                                                          .getName()))
                    {
                        returnPlaceView = _placeView;
                    }
                }
            }
        }
        return returnPlaceView;
    }

    public PlaceView getPlace(int placeNo)
    {
        PlaceView returnPlaceView = null;

        if(_placeViews != null)
        {
            if(placeNo < _placeViews.size())
            {
                returnPlaceView = _placeViews.get(placeNo);
            }
        }
        return returnPlaceView;
    }


    public ConnectableView getPlaceTransitionObject(String ptoId)
    {
        if(ptoId != null)
        {
            if(getPlaceById(ptoId) != null)
            {
                return getPlaceById(ptoId);
            }
            else if(getTransitionById(ptoId) != null)
            {
                return getTransitionById(ptoId);
            }
        }
        return null;
    }

    public ArcView createArc(Element inputArcElement)
    {
        //System.out.println("PetriNetView3126"+inputArcElement);
        String idInput = null;
        String sourceInput;
        String targetInput;
        LinkedList<MarkingView> weightInput = new LinkedList<MarkingView>();
        LinkedList<Marking> weightModel = new LinkedList<Marking>();
        boolean taggedArc;
        sourceInput = inputArcElement.getAttribute("source");
        targetInput = inputArcElement.getAttribute("target");
        String idTempStorage = inputArcElement.getAttribute("id");
        String sourceTempStorage = inputArcElement.getAttribute("source");
        String targetTempStorage = inputArcElement.getAttribute("target");
        String inscriptionTempStorage = inputArcElement.getAttribute("inscription");
        String taggedTempStorage = inputArcElement.getAttribute("tagged");
        taggedArc = !(taggedTempStorage.length() == 0 || taggedTempStorage.length() == 5);

        if(idTempStorage.length() > 0)
            idInput = idTempStorage;
        if(sourceTempStorage.length() > 0)
            sourceInput = sourceTempStorage;
        if(targetTempStorage.length() > 0)
            targetInput = targetTempStorage;
        if(inscriptionTempStorage.length() > 0)
        {
            String[] stringArray = inscriptionTempStorage.split(",");
            if(stringArray.length == 1)
            {
                MarkingView markingView = new MarkingView(getActiveTokenView(), Integer.valueOf(stringArray[0]));
                Marking marking = new Marking(getActiveTokenView().getModel(), Integer.valueOf(stringArray[0]));
                weightInput.add(markingView);
                weightModel.add(marking);
            }
            else
            {
                int i = 0;
                while(i < stringArray.length)
                {
                    MarkingView markingView = new MarkingView(this.getTokenClassFromID(stringArray[i]), Integer.valueOf(stringArray[i + 1]));
                    Marking marking = new Marking(getTokenClassFromID(stringArray[i]).getModel(), Integer.valueOf(stringArray[i + 1]));
                    weightInput.add(markingView);
                    weightModel.add(marking);
                    i += 2;
                }
            }
        }

        ConnectableView sourceIn = getPlaceTransitionObject(sourceInput);
        ConnectableView targetIn = getPlaceTransitionObject(targetInput);

        // add the insets and offset
        int aStartx = sourceIn.getX() + sourceIn.centreOffsetLeft();
        int aStarty = sourceIn.getY() + sourceIn.centreOffsetTop();

        int aEndx = targetIn.getX() + targetIn.centreOffsetLeft();
        int aEndy = targetIn.getY() + targetIn.centreOffsetTop();

        ArcView tempArcView;

        String type = "normal"; // default value
        NodeList nl = inputArcElement.getElementsByTagName("type");
        if(nl.getLength() > 0)
        {
            type = ((Element) (nl.item(0))).getAttribute("type");
        }

        if(type.equals("inhibitor"))
        {
            tempArcView = new InhibitorArcView((double) aStartx, (double) aStarty, (double) aEndx, (double) aEndy, sourceIn, targetIn, weightInput, idInput, new InhibitorArc(sourceIn.getModel(), targetIn.getModel()));//, weightModel));
        }
        else
        {
            tempArcView = new NormalArcView((double) aStartx, (double) aStarty, (double) aEndx, (double) aEndy, sourceIn, targetIn, weightInput, idInput, taggedArc, new NormalArc(sourceIn.getModel(), targetIn.getModel()));//, weightModel));
        }

        getPlaceTransitionObject(sourceInput).addOutbound(tempArcView);
        getPlaceTransitionObject(targetInput).addInbound(tempArcView);

        // **********************************************************************************
        // The following section attempts to load and display arcpath
        // details****************

        // NodeList nodelist = inputArcElement.getChildNodes();
        NodeList nodelist = inputArcElement.getElementsByTagName("arcpath");
        if(nodelist.getLength() > 0)
        {
            tempArcView.getArcPath().purgePathPoints();
            for(int i = 0; i < nodelist.getLength(); i++)
            {
                Node node = nodelist.item(i);
                if(node instanceof Element)
                {
                    Element element = (Element) node;
                    if("arcpath".equals(element.getNodeName()))
                    {
                        String arcTempX = element.getAttribute("x");
                        String arcTempY = element.getAttribute("y");
                        String arcTempType = element
                                .getAttribute("arcPointType");
                        float arcPointX = Float.valueOf(arcTempX).floatValue();
                        float arcPointY = Float.valueOf(arcTempY).floatValue();
                        arcPointX += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
                        arcPointY += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
                        boolean arcPointType = Boolean.valueOf(arcTempType)
                                .booleanValue();
                        tempArcView.getArcPath().addPoint(arcPointX, arcPointY,
                                                          arcPointType);
                    }
                }
            }
        }

        // Arc path creation ends
        // here***************************************************************
        // ******************************************************************************************
        return tempArcView;
    }

    public String getPNMLName()
    {
        return _model.getPnmlName();
    }
}

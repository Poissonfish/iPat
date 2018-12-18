import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

class Obj_Manager implements ActionListener, WindowListener {
    // indexarray
    ArrayList<Obj_Super> listOB;
    ArrayList<Integer> indexGr;
    ArrayList<Integer> indexFile;
    ArrayList<Integer> indexModule;
    ArrayList<Integer> indexLineSt;
    ArrayList<Integer> indexLineEd;
    // menu (file, module)
    int indexSelected;
    // counter
    int countGr;
    int countConfig;
    // config frame
//    ArrayList <GUI_Models_arc> config;
    ArrayList <Integer> deletedConfig;
    static int token = -1;

    public Obj_Manager() {
        this.listOB = new ArrayList<Obj_Super>();
        // Group
        this.indexGr = new ArrayList<Integer>();
        // Type
        this.indexFile = new ArrayList<Integer>();
        this.indexModule = new ArrayList<Integer>();
        // Line
        this.indexLineSt = new ArrayList<Integer>();
        this.indexLineEd = new ArrayList<Integer>();
        // Counter
        this.countGr = 0;
        this.countConfig = 0;
        // Menu
        this.indexSelected = -1;
        // config
//        config = new ArrayList<>();
        deletedConfig = new ArrayList<>();
    }

    JMenuItem iniMenuItem(String name) {
        JMenuItem item = new JMenuItem(name);
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(this);
        return item;
    }

    // get array
    ArrayList<Obj_Super> getObjects() {
        return this.listOB;
    }

    ArrayList<Obj_Super> getFiles() {
        ArrayList<Obj_Super> list = new ArrayList<Obj_Super>();
        for (int i : this.indexFile)
            list.add(this.listOB.get(i));
        return list;
    }

    ArrayList<Obj_Super> getModules() {
        ArrayList<Obj_Super> list = new ArrayList<Obj_Super>();
        for (int i : this.indexModule)
            list.add(this.listOB.get(i));
        return list;
    }

    ArrayList<Integer> getIndex() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (Obj_Super i : this.listOB)
            list.add(i.getIndex());
        return list;
    }

    ArrayList<Integer> getFileIndex() {
        return this.indexFile;
    }

    ArrayList<Integer> getModuleIndex() {
        return this.indexModule;
    }

    ArrayList<Integer> getIndexLineSt() {
        return this.indexLineSt;
    }

    ArrayList<Integer> getIndexLineEd() {
        return this.indexLineEd;
    }

    ArrayList<Integer> getGrN(int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < this.getOBCount(); i ++)
            if (this.getGrIndex(i) == n)
                list.add(i);
        return list;
    }

    // Get the ob index that pair with 'index' ob
    ArrayList<Integer> getLinePairs(int index) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < this.getLineCount(); i ++) {
            if (this.indexLineSt.get(i) == index)
                list.add(this.indexLineEd.get(i));
            if (this.indexLineEd.get(i) == index)
                list.add(this.indexLineSt.get(i));
        }
        return list;
    }
    // Get all index of line that contain the 'index' ob
    ArrayList<Integer> getLineIndexOfOB(int index) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < this.getLineCount(); i ++) {
            if (this.indexLineSt.get(i) == index)
                list.add(i);
            if (this.indexLineEd.get(i) == index)
                list.add(i);
        }
        return list;
    }
    // get value
    IPatFile getFile(int index, Enum_FileType file) {
        int indexGr = this.getGrIndex(index);
        ArrayList<Integer> indexInGr = this.getGrN(indexGr);
        indexInGr.removeIf(Predicate.isEqual(index));
        for (int i : indexInGr)
            if (this.getFileN(i).getFileType() == file)
                return this.getFileN(i).getFile();
        return new IPatFile();
    }
    // get mid point of the 'index' line
    Point getMidPointOfLine(int index) {
        int index1 = this.indexLineSt.get(index);
        int index2 = this.indexLineEd.get(index);
        Point pt1 = this.getObjectN(index1).getCenter();
        Point pt2 = this.getObjectN(index2).getCenter();
        return getMidPoint(pt1, pt2);
    }

    Point getMidPoint(Point p1, Point p2) {
        int x = (int) ((p1.getX() + p2.getX()) / 2.0);
        int y = (int) ((p1.getY() + p2.getY()) / 2.0);
        return new Point(x, y);
    }

    int getPointedObject(MouseEvent e) {
        for (Obj_Super i : this.listOB) {
            if (i.isPointed(e))
                return i.getIndex();
        }
        return -1;
    }

    int getPointedGroup(MouseEvent e) {
        for (int i = 0; i < this.getLineCount(); i ++) {
            int index1 = this.indexLineSt.get(i);
            int index2 = this.indexLineEd.get(i);
            if (isOnGroup(this.listOB.get(index1).getCenter(), this.listOB.get(index2).getCenter(), e))
                return this.getGrIndex(index1);
        }
        return -1;
    }

    int getPointedLine(MouseEvent e) {
        for (int i = 0; i < this.getLineCount(); i ++) {
            int index1 = this.indexLineSt.get(i);
            int index2 = this.indexLineEd.get(i);
            if (isOnLine(this.listOB.get(index1).getCenter(), this.listOB.get(index2).getCenter(), e))
                return i;
        }
        return -1;
    }

    int getClosestIndex(int index) {
        double valueMin = 99999;
        double valueTemp = valueMin;
        int indexClosest = -1;
        Point ptSelf = this.getObjectN(index).getCenter();
        for (Obj_Super i : this.getObjects()) {
            valueTemp = this.getDistance(ptSelf, i.getCenter());
            if (valueTemp < Math.min(200.0, valueMin) && valueTemp != 0) {
                valueMin = valueTemp;
                indexClosest = i.getIndex();
            }
        }
        return indexClosest;
    }

    double getDistance(Point pt1, Point pt2){
        double dist = 0;
        double devX = Math.pow((pt1.getX() - pt2.getX()), 2);
        double devY = Math.pow((pt1.getY() - pt2.getY()), 2);
        dist = Math.sqrt(devX + devY);
        dist = Math.round((dist * 100.0)/100.0);
        return dist;
    }

    Obj_Super getCurObject() {
        return this.listOB.get(this.getOBCount() - 1);
    }

    Obj_Super getObjectN(int n) {
        return this.listOB.get(n);
    }

    Obj_File getFileN(int n) {
        return (Obj_File) this.listOB.get(n);
    }

    Obj_Module getModuleN(int n) {
        return (Obj_Module) this.listOB.get(n);
    }

    int getGrIndex(int n) {
        return this.indexGr.get(n);
    }

    boolean isPartialTrue(boolean[] array){
        for (boolean b : array) if(b) return true;
        return false;
    }

    int diffValues(String[] array){
        int numOfDifferentVals = 0;
        ArrayList<String> diffNum = new ArrayList<>();
        // if diffNum not contain the element from array, add it
        for (String strTemp : array)
            if(!diffNum.contains(strTemp))
                diffNum.add(strTemp);
        // if only one kind of element, return 0, otherwise, its size
        numOfDifferentVals = diffNum.size() == 1 ? 0 : diffNum.size();
        return numOfDifferentVals;
    }

    // get count
    int getOBCount() {
        return this.listOB.size();
    }

    int getLineCount() {
        return this.indexLineSt.size();
    }
    int getFileCount() {
        return this.indexFile.size();
    }

    int getModuleCount() {
        return this.indexModule.size();
    }

    // set array
    void setGrOfGrIndex(int grIndex, int value) {
        for (int i : this.getGrN(grIndex))
            this.setGrIndex(i, value);
    }

    void setGrOfContainMO(int grIndex, boolean value) {
        for (int i : this.getGrN(grIndex))
            this.setContainMO(i, value);
    }

    // set value
    void setGrIndex(int index, int value) {
        this.indexGr.set(index, value);
        this.getObjectN(index).setGrIndex(value);
    }

    void setContainMO(int index, boolean value) {
        this.getObjectN(index).setContainMO(value);
    }

    void setNewGrIndexRecur(int indexPrev, int indexCur) {
        // Assign new group index
        this.setGrIndex(indexCur, this.countGr);
        // Find next node
        for (int i = 0; i < this.getLineCount(); i ++) {
            if (this.indexLineSt.get(i) == indexCur && this.indexLineEd.get(i) != indexPrev)
                setNewGrIndexRecur(indexCur, this.indexLineEd.get(i));
            else if (this.indexLineEd.get(i) == indexCur && this.indexLineSt.get(i) != indexPrev)
                setNewGrIndexRecur(indexCur, this.indexLineSt.get(i));
        }
    }

    // add
    void addiFile(int x, int y, String filename) throws IOException {
        this.indexFile.add(this.getOBCount());
        this.listOB.add(new Obj_File(x, y, filename));
        this.indexGr.add(-1);
    }

    void addiModule(int x, int y) throws IOException {
        this.indexModule.add(this.getOBCount());
        this.listOB.add(new Obj_Module(x, y));
        this.indexGr.add(-1);
    }

    void addNewGroup(int indexObj1, int indexObj2) {
        this.setGrIndex(indexObj1, this.countGr);
        this.setGrIndex(indexObj2, this.countGr);
        this.countGr ++;
    }

    void addLink(int indexSt, int indexEd) {
        this.indexLineSt.add(indexSt);
        this.indexLineEd.add(indexEd);
    }

    // boolean
    boolean isGroupContainCO(int indexGr) {
        for (int i : this.getGrN(indexGr))
            if (this.isModule(i))
                return true;
        return false;
    }

    boolean isPointedObject(MouseEvent e) {
        for (Obj_Super i : this.listOB)
            if (i.isPointed(e))
                return true;
        return false;
    }

    boolean isOnGroup(Point pt1, Point pt2, MouseEvent e) {
        return getDistance(pt1, e.getPoint()) + getDistance(pt2, e.getPoint()) < getDistance(pt1, pt2) + 20;
    }

    boolean isOnLine(Point pt1, Point pt2, MouseEvent e){
        return getDistance(pt1, e.getPoint()) + getDistance(pt2, e.getPoint()) < getDistance(pt1, pt2) + 2;
    }

    boolean isFile(int index) {
        return this.listOB.get(index).isFile();
    }

    boolean isModule(int index) {
        return this.listOB.get(index).isModule();
    }

    boolean isGroup(int index) {
        return this.listOB.get(index).isGroup();
    }

    boolean isContainCO(int index) {
        return this.listOB.get(index).isContainMO();
    }

    void removeObject(int index) {
        // Rearrange file list
        int indexDelete = -1;
        for (int i = 0; i < this.getFileCount(); i ++) {
            int indexCur = this.indexFile.get(i);
            if (indexCur > index) {
                this.indexFile.set(i, indexCur - 1);
                this.listOB.get(indexCur).setIndex(indexCur - 1);
            } else if (indexCur == index) {
                indexDelete = i;
            }
        }
        if (indexDelete != -1)
            this.indexFile.remove(indexDelete);
        // Rearrange module list
        indexDelete = -1;
        for (int i = 0; i < this.getModuleCount(); i ++) {
            int indexCur = this.indexModule.get(i);
            if (indexCur > index) {
                this.indexModule.set(i, indexCur - 1);
                this.listOB.get(indexCur).setIndex(indexCur - 1);
            } else if (indexCur == index) {
                indexDelete = i;
                Obj_Module.countMO--;
            }
        }
        if (indexDelete != -1)
            this.indexModule.remove(indexDelete);
        // Rearrange line list
        for (int i = 0; i < this.getLineCount(); i ++) {
            int indexSt = this.indexLineSt.get(i);
            int indexEd = this.indexLineEd.get(i);
            if (indexSt > index)
                this.indexLineSt.set(i, indexSt - 1);
            if (indexEd > index)
                this.indexLineEd.set(i, indexEd - 1);
        }
        // Put the label to far far away
        Obj_Super objTemp = this.listOB.get(index);
        objTemp.remove();
        // Remove list
        this.listOB.remove(index);
        this.indexGr.remove(index);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        this.indexSelected = -1;
    }

    void checkFormat(Obj_Module mo) {
        if (mo.getFormat().isNA()) {
            String msg = "No match format found. \nPlease see section 2.3 from <a href=\"http://zzlab.net/iPat/iPat_manual.pdf\">iPat User Manaul</a> for details.<br>";
            int indexGr = mo.getGrIndex();
            for (int i : this.getGrN(indexGr)) {
                Obj_Super ob = this.getObjectN(i);
                if(ob.isModule()) {
                    continue;
                }
                msg = msg + "   " + ob.getName() + ":\t" + ((Obj_File)ob).getFileType() + "<br>";
            }
            JEditorPane ep = new JEditorPane();
            ep.setEditable(false);
            ep.setBackground(new Color(237, 237, 237, 100));
            ep.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
            ep.setText(msg);
            // handle link events
            ep.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(e.getURL().toURI());
                            } catch (IOException | URISyntaxException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            });
            JOptionPane.showMessageDialog(new JFrame(), ep,
                    "Incorrect format", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }
    @Override
    public void windowClosing(WindowEvent e) {
//        System.out.println("Config closing (outer)");
//        System.out.println("Token is : " + this.token);
//        int indexTarget = this.token;
//        int cumulate = 0;
//        for (int i : this.deletedConfig) {
//            if (indexTarget > i)
//                cumulate++;
//        }
//        indexTarget -= cumulate;
//        System.out.println("Add " + this.token + " to delete array");
//        this.deletedConfig.add(this.token);
//        // get command from closing panel
//        System.out.println("get " + indexTarget);
//        GUI_Models_arc configTemp = this.config.get(indexTarget);
//        Obj_Module moTemp = this.getModuleN(configTemp.getIndex());
//        moTemp.setPhenotype(configTemp.panePhenotype.getSelected());
//        moTemp.setFile(configTemp.paneWD.getPath());
//        moTemp.setName(configTemp.paneWD.getProject());
//        moTemp.setMAF(Double.parseDouble(configTemp.paneQC.getMAF()));
//        moTemp.setMS(Double.parseDouble(configTemp.paneQC.getMS()));
//        if (configTemp.isDeployed()) {
//            switch (configTemp.getMethod()) {
//                case GWAS:
//                    moTemp.setDeployedGWASTool(configTemp.getTool());
//                    moTemp.setCommandGWAS(configTemp.getCommand());
//                    break;
//                case GS:
//                    moTemp.setDeployedGSTool(configTemp.getTool());
//                    moTemp.setCommandGS(configTemp.getCommand());
//                    break;
//                case BSA:
//                    moTemp.setCommandBSA(configTemp.getCommand());
//                    moTemp.setBSA(true);
//                    break;
//            }
//        }
//        System.out.println("Remove " + indexTarget);
//        this.config.remove(indexTarget);
//        System.out.println("Now the length is " + this.config.size());
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
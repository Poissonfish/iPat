import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

class iPatList implements ActionListener, WindowListener {
    // indexarray
    ArrayList<iPatObject> listOB;
    ArrayList<Integer> indexGr;
    ArrayList<Integer> indexFile;
    ArrayList<Integer> indexModule;
    ArrayList<Integer> indexLineSt;
    ArrayList<Integer> indexLineEd;
    // menu (file, module)
    int indexSelected;
    JPopupMenu menuFile;
    JMenuItem menuOpenFile;
    JMenuItem menuDelFile;
    JMenuItem menuIsRegular;
    JMenuItem menuIsCov;
    JMenuItem menuIsKin;
    JPopupMenu menuMO;
    JMenuItem menuOpenMO;
    JMenuItem menuDelMO;
    JMenuItem menuGWAS;
    JMenuItem menuGS;
    JMenuItem menuBSA;
    JMenuItem menuRun;
    // counter
    int countGr;
    int countConfig;
    // config frame
    ArrayList <ModuleConfig> config;
    ArrayList <Integer> deletedConfig;
    static int token = -1;

    public iPatList () {
        this.listOB = new ArrayList<iPatObject>();
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
        this.iniMenu();
        this.indexSelected = -1;
        // config
        config = new ArrayList<>();
        deletedConfig = new ArrayList<>();
    }

    // menu
    void iniMenu() {
        // Instantiate
        // module
        this.menuMO = new JPopupMenu();
        this.menuOpenMO = iniMenuItem("Open Working Directory");
        this.menuGWAS = iniMenuItem("GWAS (Empty)");
        this.menuGS = iniMenuItem("GS (Empty)");
        this.menuBSA = iniMenuItem("BSA (Empty)");
        this.menuRun = iniMenuItem("Run");
        // file
        this.menuFile = new JPopupMenu();
        this.menuOpenFile = iniMenuItem("Open File");
        this.menuIsRegular = iniMenuItem("Assign as a regular file");
        this.menuIsCov = iniMenuItem("Assign as a covariates file");
        this.menuIsKin = iniMenuItem("Assign as a kinship file");
        // Construct menu
        // module
        this.menuMO.add(menuOpenMO);
        this.menuMO.add(menuGWAS);
        this.menuMO.add(menuGS);
        this.menuMO.add(menuBSA);
        this.menuMO.addSeparator();
        this.menuMO.add(menuRun);
        this.menuRun.setEnabled(false);
        // file
        this.menuFile.add(menuOpenFile);
        this.menuFile.add(menuIsRegular);
        this.menuFile.add(menuIsCov);
        this.menuFile.add(menuIsKin);
        // Layout (module)
        this.menuMO.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.menuFile.setBorder(new BevelBorder(BevelBorder.RAISED));
    }

    JMenuItem iniMenuItem(String name) {
        JMenuItem item = new JMenuItem(name);
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(this);
        return item;
    }

    void showMenu(int index, MouseEvent e) {
        this.indexSelected = index;
        if (this.isFile(index)) {
            this.menuFile.show(e.getComponent(), e.getX(), e.getY());
        } else {
            // updatea based on the deployment
            iPatModule mo = (iPatModule)this.getObjectN(index);
            if (mo.isGWASDeployed())
                this.menuGWAS.setText("GWAS (" + mo.getDeployedGWASTool() + ")");
            else
                this.menuGWAS.setText("GWAS (Empty)");
            if (mo.isGSDeployed())
                this.menuGS.setText("GS (" + mo.getDeployedGSTool() + ")");
            else
                this.menuGS.setText("GS (Empty)");
            if (mo.isBSADeployed())
                this.menuBSA = iniMenuItem("BSA (Ready)");
            else
                this.menuBSA = iniMenuItem("BSA (Empty)");
            this.menuRun.setEnabled(mo.isDeployed());
            this.menuMO.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    void printStat() {
        System.out.println("Index : " + this.getIndex());
        System.out.println("File : " + this.getFileIndex());
        System.out.println("Module : " + this.getModuleIndex());
        System.out.println("Group : " + indexGr);
        System.out.println("line start: " + indexLineSt);
        System.out.println("line end :  " + indexLineEd);
    }

    // get array
    ArrayList<iPatObject> getObjects() {
        return this.listOB;
    }

    ArrayList<iPatObject> getFiles() {
        ArrayList<iPatObject> list = new ArrayList<iPatObject>();
        for (int i : this.indexFile)
            list.add(this.listOB.get(i));
        return list;
    }

    ArrayList<iPatObject> getModules() {
        ArrayList<iPatObject> list = new ArrayList<iPatObject>();
        for (int i : this.indexModule)
            list.add(this.listOB.get(i));
        return list;
    }

    ArrayList<Integer> getIndex() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (iPatObject i : this.listOB)
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
    iFile getFile(int index, FileType file) {
        int indexGr = this.getGrIndex(index);
        ArrayList<Integer> indexInGr = this.getGrN(indexGr);
        indexInGr.removeIf(Predicate.isEqual(index));
        for (int i : indexInGr)
            if (this.getFileN(i).getFileType() == file)
                return this.getFileN(i).getFile();
        return new iFile();
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
        for (iPatObject i : this.listOB) {
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
        for (iPatObject i : this.getObjects()) {
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

    iPatObject getCurObject() {
        return this.listOB.get(this.getOBCount() - 1);
    }

    iPatObject getObjectN(int n) {
        return this.listOB.get(n);
    }

    iPatFile getFileN(int n) {
        return (iPatFile) this.listOB.get(n);
    }

    iPatModule getModuleN(int n) {
        return (iPatModule) this.listOB.get(n);
    }

    int getGrIndex(int n) {
        return this.indexGr.get(n);
    }

    FileFormat getFormat(int indexMO) throws IOException {
        // need extension (without extension): Binary fam
        // type record which table is P(1), C(2) or K(3)
        // get all index in the group
        ArrayList<Integer> indexInGr = this.getGrN(this.getGrIndex(indexMO));
        indexInGr.removeIf(Predicate.isEqual(indexMO));
        // get file from OBs
        ArrayList<iFile> files = new ArrayList<>();
        for (int i : indexInGr)
            files.add(this.getObjectN(i).getFile());
        int countFile = files.size();
        // file by two string
        ArrayList<String[]> head2Lines = new ArrayList<>();
        // file by separated elements
        ArrayList<String[]> row1 = new ArrayList<>();
        ArrayList<String[]> row2 = new ArrayList<>();
        // file by count
        ArrayList<Integer> countRow = new ArrayList<>();
        ArrayList<Integer> countCol = new ArrayList<>();
        // fetch information from files
        for (iFile file : files) {
            String[] lines = file.getLines(2);
            String[] row1Temp = file.getSepStr(lines[0]);
            String[] row2Temp = file.getSepStr(lines[1]);
            int countColTemp = row1Temp.length;
            head2Lines.add(lines);
            row1.add(row1Temp);
            row2.add(row2Temp);
            countRow.add(file.getLineCount());
            countCol.add(countColTemp);
        }
        // determine which format
        int i1, i2, i3;
        switch (countFile) {
            case 2:
                boolean[] isPLINK = {
                        files.get(0).getPath().toUpperCase().endsWith("MAP") &&
                                files.get(1).getPath().toUpperCase().endsWith("PED") && countCol.get(0) == 4,
                        files.get(1).getPath().toUpperCase().endsWith("MAP") &&
                                files.get(0).getPath().toUpperCase().endsWith("PED") && countCol.get(1) == 4
                };
                boolean[] isVCF = {
                        countCol.get(0) - head2Lines.get(0)[1].split("/").length == 8 &&
                                head2Lines.get(0)[1].split("/").length > 1,
                        countCol.get(1) - head2Lines.get(1)[1].split("/").length == 8 &&
                                head2Lines.get(1)[1].split("/").length > 1
                };
                boolean[] isHMP = {
                        countCol.get(0) - countRow.get(1) == 11 || countCol.get(0) - countRow.get(1) == 10,
                        countCol.get(1) - countRow.get(0) == 11 || countCol.get(1) - countRow.get(0) == 10
                };
                boolean[] isNUM = {
                        Arrays.asList(row2.get(0)).containsAll(Arrays.asList("0", "1", "2")) &&
                                diffValues(row2.get(0)) < 5,
                        Arrays.asList(row2.get(1)).containsAll(Arrays.asList("0", "1", "2")) &&
                                diffValues(row2.get(1)) < 5
                };
                boolean[] isBSA = {
                        countCol.get(0) == 5 && countCol.get(1) == 3 && countRow.get(0) == countRow.get(1),
                        countCol.get(1) == 5 && countCol.get(0) == 3 && countRow.get(1) == countRow.get(0)
                };
                i1 = indexInGr.get(0);
                i2 = indexInGr.get(1);
                if (isPartialTrue(isPLINK)) {
                    this.getFileN(i1).setFileType(isPLINK[0] ? FileType.Map : FileType.Genotype);
                    this.getFileN(i2).setFileType(isPLINK[1] ? FileType.Map : FileType.Genotype);
//                    this.getModuleN(indexMO).setMap(isPLINK[0] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
//                    this.getModuleN(indexMO).setGenotype(isPLINK[1] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
                    return FileFormat.PLINK;
                } else if (isPartialTrue(isVCF)) {
                    this.getFileN(i1).setFileType(isVCF[0] ? FileType.Genotype : FileType.Phenotype);
                    this.getFileN(i2).setFileType(isVCF[1] ? FileType.Genotype : FileType.Phenotype);
//                    this.getModuleN(indexMO).setGenotype(isVCF[0] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
//                    this.getModuleN(indexMO).setPhenotype(isVCF[1] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
                    return FileFormat.VCF;
                } else if (isPartialTrue(isHMP)) {
                    this.getFileN(i1).setFileType(isHMP[0] ? FileType.Genotype : FileType.Phenotype);
                    this.getFileN(i2).setFileType(isHMP[1] ? FileType.Genotype : FileType.Phenotype);
//                    this.getModuleN(indexMO).setGenotype(isHMP[0] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
//                    this.getModuleN(indexMO).setPhenotype(isHMP[1] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
                    return FileFormat.Hapmap;
                } else if (isPartialTrue(isNUM)) {
                    this.getFileN(i1).setFileType(isNUM[0] ? FileType.Genotype : FileType.Phenotype);
                    this.getFileN(i2).setFileType(isNUM[1] ? FileType.Genotype : FileType.Phenotype);
//                    this.getModuleN(indexMO).setGenotype(isNUM[0] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
//                    this.getModuleN(indexMO).setPhenotype(isNUM[1] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
                    return FileFormat.Numeric;
                } else if (isPartialTrue(isBSA)) {
                    this.getFileN(i1).setFileType(isBSA[0] ? FileType.Genotype : FileType.Map);
                    this.getFileN(i2).setFileType(isBSA[1] ? FileType.Genotype : FileType.Map);
//                    this.getModuleN(indexMO).setGenotype(isPLINK[0] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
//                    this.getModuleN(indexMO).setMap(isPLINK[1] ? this.getFileN(i1).getFile() : this.getFileN(i2).getFile());
                    return FileFormat.BSA;
                }
                break;
            case 3:
                // Numerical
                for (int i = 0; i < 3; i++) {
                    i1 = indexInGr.get(i);
                    i2 = indexInGr.get((i + 1) % 3);
                    i3 = indexInGr.get((i + 2) % 3);
                    System.out.println("i1 = " + i1 + ", i2 = " + i2 + ", i3 = " + i3);
                    if (Arrays.asList(row2.get(i)).containsAll(Arrays.asList("0", "1", "2")) && diffValues(row2.get(i)) < 6) {
                        this.getFileN(i1).setFileType(FileType.Genotype);
                        // m or m+1 - m or m+1 = -1, 0 1
                        this.getFileN(i2).setFileType((countCol.get((i + 1) % 3) == 3 &&
                                Math.abs(countCol.get(i) - countRow.get((i + 1) % 3)) <= 1) ? FileType.Map : FileType.Phenotype);
                        this.getFileN(i3).setFileType((countCol.get((i + 2) % 3) == 3 &&
                                Math.abs(countCol.get(i) - countRow.get((i + 2) % 3)) <= 1) &&
                                this.getFileN(i2).getFileType() != FileType.Map ? FileType.Map : FileType.Phenotype);
                        // check if contains all filetype
                        ArrayList<FileType> alltype = new ArrayList<>();
                        alltype.add(this.getFileN(i1).getFileType());
                        alltype.add(this.getFileN(i2).getFileType());
                        alltype.add(this.getFileN(i3).getFileType());
                        if (alltype.containsAll(Arrays.asList(FileType.Genotype, FileType.Map, FileType.Phenotype)))
                            return FileFormat.Numeric;
                    }
                }
                // PLINK
                int PED = -1;
                int MAP = -1;
                for (int i = 0; i < 3; i++) {
                    i1 = indexInGr.get(i);
                    if (files.get(i).getPath().toUpperCase().endsWith("PED")) {
                        this.getFileN(i1).setFileType(FileType.Genotype);
                        PED = i;
                    } else if (files.get(i).getPath().toUpperCase().endsWith("MAP") && countCol.get(i) == 4) {
                        this.getFileN(i1).setFileType(FileType.Map);
                        MAP = i;
                    }
                }
                if (PED != -1 && MAP != -1) {
                    int indexP = indexInGr.get(3 - PED - MAP);
                    this.getFileN(indexP).setFileType(FileType.Phenotype);
                    return FileFormat.PLINK;
                }
                // Binary
                int BED = -1;
                int BIM = -1;
                int FAM = -1;
                for (int i = 0; i < 3; i++) {
                    i1 = indexInGr.get(i);
                    if (files.get(i).getPath().toUpperCase().endsWith("BED")) {
                        this.getFileN(i1).setFileType(FileType.Genotype);
                        BED = i;
                    } else if (files.get(i).getPath().toUpperCase().endsWith("BIM") && countCol.get(i) == 6) {
                        this.getFileN(i1).setFileType(FileType.BIM);
                        BIM = i;
                    } else if (files.get(i).getPath().toUpperCase().endsWith("BIM") && countCol.get(i) == 6) {
                        this.getFileN(i1).setFileType(FileType.FAM);
                        FAM = i;
                    }
                }
                if (BED != -1 && BIM != -1 && FAM != -1)
                    return FileFormat.PLINKBIN;
                break;
            case 4:
                // Binary
                BED = -1;
                BIM = -1;
                FAM = -1;
                for (int i = 0; i < 3; i++) {
                    i1 = indexInGr.get(i);
                    if (files.get(i).getPath().toUpperCase().endsWith("BED")) {
                        this.getFileN(i1).setFileType(FileType.Genotype);
                        BED = i;
                    } else if (files.get(i).getPath().toUpperCase().endsWith("BIM") && countCol.get(i) == 6) {
                        this.getFileN(i1).setFileType(FileType.BIM);
                        BIM = i;
                    } else if (files.get(i).getPath().toUpperCase().endsWith("BIM") && countCol.get(i) == 6) {
                        this.getFileN(i1).setFileType(FileType.FAM);
                        FAM = i;
                    }
                }
                if (BED != -1 && BIM != -1 && FAM != -1) {
                    int P = 6 - BED - BIM - FAM;
                    int indexP = indexInGr.get(P);
                    if (Math.abs(countRow.get(FAM) - countRow.get(P)) < 2) {
                        this.getFileN(indexP).setFileType(FileType.Phenotype);
                        return FileFormat.PLINKBIN;
                    }
                }
                break;
        }
        return FileFormat.NA;
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
        this.listOB.add(new iPatFile(x, y, filename));
        this.indexGr.add(-1);
    }

    void addiModule(int x, int y) throws IOException {
        this.indexModule.add(this.getOBCount());
        this.listOB.add(new iPatModule(x, y));
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
        for (iPatObject i : this.listOB)
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

    // remove line onlu
    void removeLine(int index) {
        int index1 = this.indexLineSt.get(index);
        int index2 = this.indexLineEd.get(index);
        // break linkage and update two group
        this.breakLinkage(index1, index2);
        this.countGr ++;
        // Rearrange line list
        this.indexLineSt.remove(index);
        this.indexLineEd.remove(index);

    }

    // similar to removeLine but arrange it at final
    void removeLineOfOB(int indexOB) {
        ArrayList<Integer> indexOfLineRemove = new ArrayList<>();
        for (int i : this.getLineIndexOfOB(indexOB)) {
            indexOfLineRemove.add(i);
            int index1 = this.indexLineSt.get(i);
            int index2 = this.indexLineEd.get(i);
            // break linkage and update two group
            System.out.println("Index1 : " + index1 + " Index2 : " + index2);
            System.out.println("Group Index : " + this.countGr);
            if (index1 == indexOB)
                this.breakLinkage(index1, index2);
            else
                this.breakLinkage(index2, index1);
            this.countGr ++;
        }
        Collections.sort(indexOfLineRemove);
        Collections.reverse(indexOfLineRemove);
        // Rearrange line list
        System.out.println("delete : " + indexOfLineRemove);
        for (int i : indexOfLineRemove) {
            this.indexLineSt.remove(i);
            this.indexLineEd.remove(i);
        }
        this.printStat();
    }

    void removeObject(int index) {
        // Get its pair and remove linkage
        removeLineOfOB(index);
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
                iPatModule.countMO--;
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
        iPatObject objTemp = this.listOB.get(index);
        objTemp.remove();
        // Remove list
        this.listOB.remove(index);
        this.indexGr.remove(index);

        this.printStat();
    }

    // assign new index, update containCO and isGroup
    void breakLinkage(int index1, int index2) {
        // assign new gr to the group of index2
        this.setNewGrIndexRecur(index1, index2);
        // get group index from both object
        int indexGr1 = this.getGrIndex(index1);
        System.out.println("indexGr1 = " + indexGr1);
        int indexGr2 = this.getGrIndex(index2);
        System.out.println("indexGr2 = " + indexGr2);
        // update ContainMO status
        this.setGrOfContainMO(indexGr1, isGroupContainCO(indexGr1));
        this.setGrOfContainMO(indexGr2, isGroupContainCO(indexGr2));
        // update isGroup status
        if (this.getGrN(indexGr1).size() == 1)
            this.setGrIndex(index1, -1);
        if (this.getGrN(indexGr2).size() == 1)
            this.setGrIndex(index2, -1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        MethodType method = MethodType.NA;
        if (source == this.menuIsRegular) {
            this.getFileN(this.indexSelected).setAsRegular();
        } else if (source == this.menuIsCov) {
            this.getFileN(this.indexSelected).setAsCov();
        } else if (source == this.menuIsKin) {
            this.getFileN(this.indexSelected).setAsKin();
        } else if (source == this.menuGWAS) {
            method = MethodType.GWAS;
        } else if (source == this.menuGS) {
            method = MethodType.GS;
        } else if (source == this.menuBSA) {
            method = MethodType.BSA;
        } else if (source == this.menuRun) {
            iPatModule mo = this.getModuleN(this.indexSelected);
            FileFormat format = mo.getFormat();
            // Add command for launching app (deep copy)
            Command commandGWAS = mo.getCommandGWAS().getCopy();
            Command commandGS = mo.getCommandGS().getCopy();
            Command commandBSA  = mo.getCommandBSA().getCopy();
            // do conversion if needed, add filepaths to the command
            try {
                String pathGD = this.getFile(this.indexSelected, FileType.Genotype).getAbsolutePath();
                String pathGM = this.getFile(this.indexSelected, FileType.Map).getAbsolutePath();
                String filename = pathGD.replaceFirst("[.][^.]+$", "");
                // GWAS
                if (mo.isGWASDeployed()) {
                    boolean isPLINK = mo.getDeployedGWASTool() == ToolType.PLINK;
                    new iPatConverter(format,
                            isPLINK ? FileFormat.PLINK : FileFormat.Numeric,
                            pathGD, pathGM);
                    if (isPLINK && format != FileFormat.PLINK) {
                        pathGD = filename + "_recode.ped";
                        pathGM = filename + "_recode.map";
                        format = FileFormat.PLINK;
                    } else if (!isPLINK && format != FileFormat.Numeric) {
                        pathGD = filename + "_recode.dat";
                        pathGM = filename + "_recode.nmap";
                        format = FileFormat.Numeric;
                    } else if (format == FileFormat.PLINKBIN) {
                        commandGWAS.addArg("-fam",
                                this.getFile(this.indexSelected, FileType.FAM).getAbsolutePath());
                        commandGWAS.addArg("-bim",
                                this.getFile(this.indexSelected, FileType.BIM).getAbsolutePath());
                    }
                    commandGWAS.addArg("-phenotype",
                            this.getFile(this.indexSelected, FileType.Phenotype).getAbsolutePath());
                    commandGWAS.addArg("-genotype", pathGD);
                    commandGWAS.addArg("-map", pathGM);
                    commandGWAS.addArg("-cov",
                            this.getFile(this.indexSelected, FileType.Covariate).getAbsolutePath());
                    commandGWAS.addArg("-kin",
                            this.getFile(this.indexSelected, FileType.Kinship).getAbsolutePath());
                }
                // GS
                if (mo.isGSDeployed()) {
                    new iPatConverter(format, FileFormat.Numeric,
                            pathGD, pathGM);
                    if (format != FileFormat.Numeric) {
                        filename = pathGD.replaceFirst("[.][^.]+$", "");
                        pathGD = filename + "_recode.dat";
                        pathGM = filename + "_recode.nmap";
                    }
                    commandGS.addArg("-phenotype",
                            this.getFile(this.indexSelected, FileType.Phenotype).getAbsolutePath());
                    commandGS.addArg("-genotype", pathGD);
                    commandGS.addArg("-map", pathGM);
                    commandGS.addArg("-cov",
                            this.getFile(this.indexSelected, FileType.Covariate).getAbsolutePath());
                    commandGS.addArg("-kin",
                            this.getFile(this.indexSelected, FileType.Kinship).getAbsolutePath());
                }
                // BSA
                if (mo.isBSADeployed()) {
                    commandGS.addArg("-phenotype",
                            this.getFile(this.indexSelected, FileType.Phenotype).getAbsolutePath());
                    commandGS.addArg("-genotype", pathGD);
                    commandGS.addArg("-map", pathGM);

                }
                // assemble command
                ArrayList<Command> commandRun = new ArrayList<>();
                commandRun.add(commandGWAS);
                commandRun.add(commandGS);
                commandRun.add(commandBSA);
                mo.run(commandRun);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        // Open config panel
        if (method != MethodType.NA && source != this.menuRun) {
            iPatModule ob = this.getModuleN(this.indexSelected);
            ob.setIcon("module");
            try {
                ob.setFormat(this.getFormat(this.indexSelected));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ModuleConfig newConfig = new ModuleConfig(this.indexSelected, ob.getName(), method,
                    method == MethodType.GWAS ? ob.getDeployedGWASTool() : ob.getDeployedGSTool(),
                    ob.getFormat(),
                    this.getFile(this.indexSelected, FileType.Phenotype), ob.getPhenotype(),
                    this.getFile(this.indexSelected, FileType.Covariate),
                    method == MethodType.GWAS ? ob.getCovGWAS() : ob.getCovGS(),
                    this.countConfig ++);
            newConfig.addWindowListener(this);
            this.config.add(newConfig);
            this.indexSelected = -1;
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("Config closing (outer)");
        System.out.println("Token is : " + this.token);
        int indexTarget = this.token;
        int cumulate = 0;
        for (int i : this.deletedConfig) {
            if (indexTarget > i)
                cumulate++;
        }
        indexTarget -= cumulate;
        System.out.println("Add " + this.token + " to delete array");
        this.deletedConfig.add(this.token);
        // get command from closing panel
        System.out.println("get " + indexTarget);
        ModuleConfig configTemp = this.config.get(indexTarget);
        iPatModule moTemp = this.getModuleN(configTemp.getIndex());
        moTemp.setPhenotype(configTemp.panePhenotype.getSelected());
        moTemp.setFile(configTemp.paneWD.getPath());
        moTemp.setName(configTemp.paneWD.getProject());
        if (configTemp.isDeployed()) {
            switch (configTemp.getMethod()) {
                case GWAS:
                    moTemp.setDeployedGWASTool(configTemp.getTool());
                    moTemp.setCommandGWAS(configTemp.getCommand());
                    break;
                case GS:
                    moTemp.setDeployedGSTool(configTemp.getTool());
                    moTemp.setCommandGS(configTemp.getCommand());
                    break;
                case BSA:
                    moTemp.setCommandBSA(configTemp.getCommand());
                    moTemp.setBSA(true);
                    break;
            }
        }
        System.out.println("Remove " + indexTarget);
        this.config.remove(indexTarget);
        System.out.println("Now the length is " + this.config.size());
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
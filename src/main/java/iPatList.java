import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

class iPatList {
    ArrayList<iPatObject> listOB;
    ArrayList<Integer> indexGr;
    ArrayList<Integer> indexFile;
    ArrayList<Integer> indexModule;
    ArrayList<Integer> indexLineSt;
    ArrayList<Integer> indexLineEd;

    int countIndex;
    int countGr;
    int countLine;

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
        this.countIndex = 0;
        this.countLine = 0;
        this.countGr = 0;
    }

    void printStat() {
        System.out.println("Index : " + this.getIndex());
        System.out.println("Group : " + indexGr);
        System.out.println("line start: " + indexLineSt);
        System.out.println("line end :  " + indexLineEd);
    }

    // get value
    int getIndexOfLinePair(int target, int pair1) {
        for (int i = 0; i < this.getLineCount(); i ++) {
            if (this.indexLineSt.get(i) == target && this.indexLineEd.get(i) != pair1)
                return i;
            else if (this.indexLineEd.get(i) == target && this.indexLineSt.get(i) != pair1)
                return i;
        }
        return -1;
    }

    int getPointed(MouseEvent e) {
        for (iPatObject i : this.listOB) {
            if (i.isPointed(e))
                return i.getIndex();
        }
        return -1;
    }

    iPatObject getCurObject() {
        return this.listOB.get(countIndex - 1);
    }

    iPatObject getObjectN(int n) {
        return this.listOB.get(n);
    }

    int getGrIndex(int n) {
        return this.indexGr.get(n);
    }

    boolean isGroup(int index) {
        return this.listOB.get(index).isGroup();
    }

    boolean isContainCO(int index) {
        return this.listOB.get(index).isContainMO();
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

    // get count
    int getOBCount() {
        return this.countIndex;
    }

    int getLineCount() {
        return this.countLine;
    }
    int getFileCount() {
        return this.indexFile.size();
    }

    int getModuleCount() {
        return this.indexModule.size();
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
        int indexNext = this.getIndexOfLinePair(indexCur, indexPrev);
        // base case (Can't find next node)
        if (indexNext == -1)
            this.countGr ++;
        // recursive step
        else
            setNewGrIndexRecur(indexCur, indexNext);
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

    // add
    void addiFile(int x, int y, String filename) throws IOException {
        this.listOB.add(new iPatFile(x, y, filename));
        this.indexGr.add(-1);
        this.countIndex++;
    }

    void addiModule(int x, int y) throws IOException {
        this.listOB.add(new iPatModule(x, y));
        this.indexGr.add(-1);
        this.countIndex ++;
    }

    void addNewGroup(int indexObj1, int indexObj2) {
        this.setGrIndex(indexObj1, this.countGr);
        this.setGrIndex(indexObj2, this.countGr);
        this.countGr ++;
    }

    void addLink(int indexSt, int indexEd) {
        this.indexLineSt.add(indexSt);
        this.indexLineEd.add(indexEd);
        this.countLine ++;
    }

    // remove
    void removeOjbect(int n) {
        // Rearrange file list
        int i = 0;
        while (i < this.getFileCount()) {
            int indexCur = this.indexFile.get(i);
            if (indexCur > n)
                this.indexFile.set(i ++, indexCur - 1);
            else if (indexCur == n)
                this.indexFile.remove(i);
        }
        // Rearrange module list
        i = 0;
        while (i < this.getFileCount()) {
            int indexCur = this.indexModule.get(i);
            if (indexCur > n)
                this.indexModule.set(i ++, indexCur - 1);
            else if (indexCur == n)
                this.indexModule.remove(i);
        }
        // Put the label to far far away
        iPatObject tempObj = this.listOB.get(n);
        tempObj.setBoundXY(-99999, -99999);
        // Remove list
        this.listOB.remove(n);
        this.indexGr.remove(n);
        // subtract counter by 1
        this.countIndex --;
    }
}


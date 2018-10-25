import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

abstract class Obj_Super {
    // Global
    static int countOB = 0;
    // Object
    Rectangle bound;
    Image icon;
    IPatFile file;
    String name;
    int indexOB;

    // Boolean
    boolean isDeleted;
    boolean isFile;
    boolean isModule;

    // Group
    boolean isGroup;
    boolean isContainMO;
    int indexGr;

    // Menud
    static JMenuItem menuOpen, menuDel;

    public Obj_Super(int x, int y) {
        this.indexOB = this.countOB ++;
        this.name = "";
        this.setBound(new Rectangle(x, y, 0, 0));
        this.isDeleted = false;
        this.isGroup = false;
        this.isContainMO = false;
        this.indexGr = -1;
    }

    // index
    int getIndex() {
        return this.indexOB;
    }
    void setIndex(int index) {
        this.indexOB = index;
    }

    // Boundary/Position
    void setBound(Rectangle newBound) {
        this.bound = newBound;
    }
    void setBoundXY(int newX, int newY) {
        int x = newX;
        int y = newY;
        int xLimit = iPat.WINDOWSIZE.getWidth();
        int yLimit = iPat.WINDOWSIZE.getHeight();
        // Avoid dragging the object out of screen
        if (x + this.getWidth() <= xLimit && x >= 0 &&
            y + this.getHeight() <= yLimit && y >= 0)
            this.bound = new Rectangle(newX, newY, this.getWidth(), this.getHeight());
    }
    void setBoundWH(int newW, int newH) {
        this.bound = new Rectangle(this.getX(), this.getY(), newW, newH);
    }
    void setDeltaBound(int dx, int dy) {
        this.setBoundXY(this.getX() + dx, this.getY() + dy);
    }
    void setDeltaBound(double dx, double dy) {
        this.setBoundXY(this.getX() + (int)dx, this.getY() + (int)dy);
    }

    Rectangle getBound() {
        return this.bound;
    }

    // Edge
    int getX() {
        return (int)this.bound.getX();
    }
    int getY() {
        return (int)this.bound.getY();
    }
    int getHeight() {
        return (int)this.bound.getHeight();
    }
    int getWidth() {
        return (int)this.bound.getWidth();
    }
    Point getCenter() {
        return (new Point ((int)(this.getX() + this.getWidth()/2),
                            (int)(this.getY() + this.getHeight()/2)));
    }

    // File/Image
    void setIcon(String filename) {
        this.icon = iPat.IMGLIB.getImage(filename);
        setBound(new Rectangle(this.getX(), this.getY(),
                this.icon.getWidth(null),
                this.icon.getHeight(null)));
    }

    Image getImage() {
        return this.icon;
    }

    String getPath(){
        return this.file.getPath();
    }

    String getName() {
        return this.name;
    }

    IPatFile getFile() {
        return this.file;
    }

    void setName(String text){
        this.name = text;
    }

    void setFile(String text) {
        this.file = new IPatFile(text);
        this.name = text;
    }

    void remove() {
        this.isDeleted = true;
        this.isGroup = false;
        this.isContainMO = false;
        this.indexGr = -1;
        this.countOB --;
        this.setBound(new Rectangle(-999, -999, 0, 0));
    }

    // Group
    int getGrIndex(){
        return this.indexGr;
    }

    void setGrIndex(int gr) {
        this.indexGr = gr;
        this.isGroup = (gr != -1);
    }
    void setContainMO(boolean isContain) {
        this.isContainMO = isContain;
    }

    boolean isGroup() {
        return this.isGroup;
    }
    boolean isContainMO() {
        return this.isContainMO;
    }

    // boolean
    boolean isFile() {
        return isFile;
    }
    boolean isModule() {
        return isModule;
    }
    boolean isDeleted() {
        return false;
    }

    // Mouse event
    boolean isPointed(MouseEvent e) {
        return this.getBound().contains(e.getPoint());
    }
    void openFile() {
        try {
            Desktop.getDesktop().open(this.file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
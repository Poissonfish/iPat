import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

abstract class iPatObject {
    // Global
    static int countOB = 0;
    // Object
    Rectangle bound;
    Image icon;
    iFile file;
    JLabel name;
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

    public iPatObject(int x, int y) {
        this.indexOB = this.countOB ++;
        name = new JLabel();
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
        updateLabel();
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
        updateLabel();
    }
    void setBoundWH(int newW, int newH) {
        this.bound = new Rectangle(this.getX(), this.getY(), newW, newH);
        updateLabel();
    }
    void setDeltaBound(int dx, int dy) {
        this.setBoundXY(this.getX() + dx, this.getY() + dy);
    }
    void setDeltaBound(double dx, double dy) {
        this.setBoundXY(this.getX() + (int)dx, this.getY() + (int)dy);
    }

    void updateLabel(){
        this.name.setLocation(this.getX(), this.getY() + this.getHeight());
        this.name.setSize(200, 15);
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

    JLabel getLabel() {
        return this.name;
    }

    iFile getFile() {
        return this.file;
    }

    void setLabel(String text){
        this.name.setText(text);
    }

    void setFile(String text) throws IOException {
        this.file = new iFile(text);
        this.name.setText(this.file.getName());
    }

    void remove() {
        this.isDeleted = true;
        this.isGroup = false;
        this.isContainMO = false;
        this.indexGr = -1;
        this.countOB --;
        this.name.setText("");
        this.setBound(new Rectangle(-1000, -1000, 1, 1));
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
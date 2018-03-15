import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public class FileConfig extends JPanel implements ActionListener,MouseMotionListener, MouseListener, KeyListener, DropTargetListener {
    iPatList iPatOB;
    // MotionListener
    int indexPress;
    int indexHover;
    int indexClosest;
    int indexSelect;
    Point ptPress;
    Point ptHover;
    // TemperateLine
    Point ptTempLineST;
    Point ptTempLineED;


    public FileConfig(int width, int height, int pheight) {
        // Instantiate
        iPatOB = new iPatList();
        indexPress = -1;
        indexHover = -1;
        indexClosest = -1;
        indexSelect = -1;
        ptPress = new Point(0, 0);
        ptHover = new Point(0, 0);
        ptTempLineST = new Point(0, 0);
        ptTempLineED = new Point(0, 0);
        // Add listener
        new DropTarget(this, DnDConstants.ACTION_COPY, this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

    }

    boolean isPressOnOB () {
        return this.indexPress != -1;
    }

    boolean isHoverOnOB () {
        return this.indexHover != -1;
    }

    boolean isClosestFound () {
        boolean isClosest = this.indexClosest != -1;
        if (isClosest) {
            boolean isSelfContainMO = iPatOB.getObjectN(this.indexPress).isContainMO();
            boolean isClosestContainMO = iPatOB.getObjectN(this.indexClosest).isContainMO();
            boolean isSameGroup = (iPatOB.getGrIndex(this.indexPress) == iPatOB.getGrIndex(this.indexClosest)) &&
                    iPatOB.isGroup(this.indexPress) && iPatOB.isGroup(this.indexClosest);
            return (!isSelfContainMO || !isClosestContainMO) && !isSameGroup;
        } else
            return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // iPat Logo
        g.drawImage(iPat.IMGLIB.getImage("iPat"), 530, 10, this);
        // linkage
        drawLine(g, this.ptTempLineST, this.ptTempLineED, iPat.IMGLIB.getStroke("dashed"));
        for (int i = 0; i < this.iPatOB.getLineCount(); i ++) {
            int indexSt = this.iPatOB.getIndexLineSt().get(i);
            int indexEd = this.iPatOB.getIndexLineEd().get(i);
            drawLine(g,
                    this.iPatOB.getObjectN(indexSt).getCenter(),
                    this.iPatOB.getObjectN(indexEd).getCenter(),
                    iPat.IMGLIB.getStroke("dashed"));
        }
        // iPatObject
        for (iPatObject i : this.iPatOB.getObjects())
            g.drawImage(i.getImage(), i.getX(), i.getY(), this);
    }

    void drawLine(Graphics g, Point pt1, Point pt2, Stroke s){
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();
        //set the stroke of the copy, not the original
        g2d.setStroke(s);
        g2d.drawLine((int)pt1.getX(), (int)pt1.getY(), (int)pt2.getX(), (int)pt2.getY());
        //gets rid of the copy
        g2d.dispose();
    }

    int getClosestIndex() {
        double valueMin = 99999;
        double valueTemp = valueMin;
        int indexClosest = -1;
        Point ptSelf = this.iPatOB.getObjectN(this.indexPress).getCenter();
        for (iPatObject i : this.iPatOB.getObjects()) {
            valueTemp = this.getDistance(ptSelf, i.getCenter());
            if (valueTemp < Math.min(200.0, valueMin) && valueTemp != 0) {
                valueMin = valueTemp;
                indexClosest = i.getIndex();
            }
        }
        return indexClosest;
    }

    void buildLink(int target) {
        iPatObject obSelf = this.iPatOB.getObjectN(this.indexPress);
        iPatObject obTarget = this.iPatOB.getObjectN(target);
        // Draw line
        if (!obSelf.isContainMO() || !obTarget.isContainMO())
            this.iPatOB.addLink(this.indexPress, target);
        // 1. w/ MO - w/ MO
        if (obSelf.isContainMO() && obTarget.isContainMO()) {
            // Do nothing
        // 2. w/o MO - w/ MO
        } else if (!obSelf.isContainMO() && obTarget.isContainMO()) {
            if (obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrOfGrIndex(obSelf.getGrIndex(), obTarget.getGrIndex());
                this.iPatOB.setGrOfContainMO(obSelf.getGrIndex(), true);
            } else if (!obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrIndex(this.indexPress, obTarget.getGrIndex());
                this.iPatOB.setContainMO(this.indexPress, true);
            } else if (obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.setGrIndex(target, obSelf.getGrIndex());
                this.iPatOB.setGrOfContainMO(obSelf.getGrIndex(), true);
            } else if (!obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.addNewGroup(this.indexPress, target);
                this.iPatOB.setContainMO(this.indexPress, true);
            }
        // 3. w/ MO - w/o MO
        } else if (obSelf.isContainMO() && !obTarget.isContainMO()) {
            if (obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrOfGrIndex(obTarget.getGrIndex(), obSelf.getGrIndex());
                this.iPatOB.setGrOfContainMO(obTarget.getGrIndex(), true);
            } else if (!obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrIndex(this.indexPress, obTarget.getGrIndex());
                this.iPatOB.setGrOfContainMO(obTarget.getGrIndex(), true);
            } else if (obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.setGrIndex(target, obSelf.getGrIndex());
                this.iPatOB.setContainMO(target, true);
            } else if (!obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.addNewGroup(this.indexPress, target);
                this.iPatOB.setContainMO(target, true);
            }
        // 4. w/o MO - w/o MO
        } else if (!obSelf.isContainMO() && !obTarget.isContainMO()) {
            if (obSelf.isGroup() && obTarget.isGroup())
                this.iPatOB.setGrOfGrIndex(obTarget.getGrIndex(), obSelf.getGrIndex());
            else if (!obSelf.isGroup() && obTarget.isGroup())
                this.iPatOB.setGrIndex(this.indexPress, obTarget.getGrIndex());
            else if (obSelf.isGroup() && !obTarget.isGroup())
                this.iPatOB.setGrIndex(target, obSelf.getGrIndex());
            else if (!obSelf.isGroup() && !obTarget.isGroup())
                this.iPatOB.addNewGroup(this.indexPress, target);
        }
    }

    void breakLinkage() {

    }

    void removeLine(int index) {

    }

    void removeObject(int index) {

    }

    double getDistance(Point pt1, Point pt2){
        double dist = 0;
        double devX = Math.pow((pt1.getX() - pt2.getX()), 2);
        double devY = Math.pow((pt1.getY() - pt2.getY()), 2);
        dist = Math.sqrt(devX + devY);
        dist = Math.round((dist * 100.0)/100.0);
        return dist;
    }

    boolean isOnLine(Point pt1, Point pt2, Point pointer){
        return getDistance(pt1, pointer) + getDistance(pt2, pointer) < getDistance(pt1, pt2) + 2;
    }

    void resetPressStatus() {
        this.indexPress = -1;
        this.indexClosest = -1;
        this.ptPress.setLocation(0, 0);
        this.ptTempLineST.setLocation(0, 0);
        this.ptTempLineED.setLocation(0, 0);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Get current point
        Point pt = e.getPoint();
        // If drag by left button
        if (SwingUtilities.isLeftMouseButton(e)) {
            // If on any object, move it
            if (this.isPressOnOB()) {
                double dx = pt.getX() - this.ptPress.getX();
                double dy = pt.getY() - this.ptPress.getY();
                this.iPatOB.getObjectN(this.indexPress).setDeltaBound(dx, dy);
                // Check if the new position need to build a line
                this.indexClosest = this.getClosestIndex();
                if (this.isClosestFound()) {
                    this.ptTempLineST = iPatOB.getObjectN(this.indexPress).getCenter();
                    this.ptTempLineED = iPatOB.getObjectN(this.indexClosest).getCenter();
                } else {
                    this.ptTempLineST.setLocation(0, 0);
                    this.ptTempLineED.setLocation(0, 0);
                }
            }
        }
        repaint();
        // Update the previous point
        this.ptPress.setLocation(pt);
    }



    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.ptPress.setLocation(e.getPoint());
        this.indexPress = this.iPatOB.getPointed(e);
        // Double click by left button
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            // if on a object, open it
            if (this.isPressOnOB())
                this.iPatOB.getObjectN(indexPress).openFile();
            // if on elsewhere, create a module
            else {
                try {
                    this.iPatOB.addiModule(
                            (int) this.ptPress.getX() - iPat.IMGLIB.getImage("module").getWidth(this) / 2,
                            (int) this.ptPress.getY() - iPat.IMGLIB.getImage("module").getHeight(this) / 2);
                    this.add(this.iPatOB.getCurObject().getLabel());
                    repaint();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        this.ptPress.setLocation(e.getPoint());
        this.indexPress = this.iPatOB.getPointed(e);
        if (SwingUtilities.isRightMouseButton(e)) {
            // if on a object, drop the menu
            if (this.isPressOnOB())
                this.iPatOB.getObjectN(indexPress).dropMenu(e);
            else
                this.iPatOB.printStat();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.isPressOnOB() && this.isClosestFound())
            this.buildLink(this.indexClosest);
        resetPressStatus();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("Mouse enter");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("Mouse exit");
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        System.out.println("Drag enter");
    }

    @Override
    public void dragOver(DropTargetDragEvent dte) {
        Point pt = dte.getLocation();
//      hint_drop_label.setLocation(pt.x - 50, pt.y + 20);
//        System.out.println("x: " + pt.x + " y: " + pt.y);
        System.out.println("Drag over");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        System.out.println("Drag change");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        System.out.println("Drag exit");
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        // Get dropped items' data
        Transferable transferable = dtde.getTransferable();
        // Get the format
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (DataFlavor flavor : flavors) {
            try {
                // If multiple files
                if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                    List<File> files = (List<File>) transferable.getTransferData(flavor);
                    int count = 0;
                    for (File file : files) {
                        Point pointer = dtde.getLocation();
                        int x = (int)pointer.getX() - iPat.IMGLIB.getImage("file").getWidth(this) / 2;
                        int y = (int)pointer.getY() - iPat.IMGLIB.getImage("file").getHeight(this) / 2;
                        this.iPatOB.addiFile(x + count*30, y + count*15, file.getAbsolutePath());
                        // Add label into it
                        this.add(this.iPatOB.getCurObject().getLabel());
                        repaint();
                        count ++;
                        // Print out the file path
                        System.out.println("File path is '" + file.getPath() + "'.");
                        repaint();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dtde.dropComplete(true);
//      hint_drop_label.setLocation(new Point(-99, -99));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

// Implement actionlistener
// timer = (5, this)
// paintcompoinent( timer.start())
// Action (repaint())
//
// before paint the image
//    float opacity = 0.5f;
//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

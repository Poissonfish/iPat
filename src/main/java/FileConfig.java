import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
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

public class FileConfig extends JPanel implements ActionListener,MouseMotionListener, MouseListener, DropTargetListener {
    iPatList iPatOB;
    // MouseListener (object)
    int indexOBPress;
    int indexOBHover;
    int indexOBSelect;
    int indexClosest;
    // MouseListener (Line)
    int indexLinePress;
    int indexLineHover;
    int indexLineSelect;
    int indexLineDrag;
    // MouseListener (Group)
    int indexGrPress;
    int indexGrHover;
    int indexGrSelect;
    // MouseListener
    Point ptPress;
    Point ptHover;
    // TemperateLine
    Point ptTempLineST;
    Point ptTempLineED;
    // Rotate
    Timer timerRotate;
    int degree = 0;
    double xRotate, yRotate;
    // hover shade
    Timer timerHover;
    float alphaHover;
    boolean wasOnOb;
    boolean wasOnLine;
    boolean wasOnGr;
    // repaint timer
    Timer timerRepaint;

    public FileConfig(int width, int height, int pheight) {
        // Instantiate
        iPatOB = new iPatList();
        timerRotate = new Timer(5, this);
        // rotate
        xRotate = iPat.IMGLIB.getImage("module").getWidth(this) / 2;
        yRotate = iPat.IMGLIB.getImage("module").getHeight(this) / 2;
        // hover
        timerHover = new Timer(5, this);
        alphaHover = 0f;
        this.wasOnOb = false;
        this.wasOnLine = false;
        this.wasOnGr = false;
        // repaint timer
        timerRepaint = new Timer(500, this);
        timerRepaint.start();
        // Add listener
        new DropTarget(this, DnDConstants.ACTION_COPY, this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        // keymep
        setupKeyMap();
        // reset gui index
        resetHoverStatus();
        resetSelectStatus();
        resetPressStatus();
    }

    void setupKeyMap() {
        InputMap im = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_BACK_SPACE), "delete");
        im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_D), "debug");
        am.put("delete",  new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isOBSelected())
                    iPatOB.removeObject(indexOBSelect);
                else if (isLineSelected())
                    iPatOB.removeLine(indexLineSelect);
                resetSelectStatus();
                resetHoverStatus();
                System.out.println("Select object : " + indexOBSelect);
                System.out.println("Select line : " + indexLineSelect);
                System.out.println("Press object : " + indexOBPress);
                System.out.println("Press line : " + indexLinePress);
            }
        });
//        am.put("debug", new AbstractAction() {
//            public void actionPerformed (ActionEvent e) {
//                debug = !debug;
//                System.out.println("Debug mode : " + (debug?"On":"OFF"));
//            }
//        });
    }

    boolean isPressOnOB () {
        return this.indexOBPress != -1;
    }

    boolean isHoverOnOB () {
        return this.indexOBHover != -1;
    }

    boolean isOBSelected() {
        return this.indexOBSelect != -1;
    }

    boolean isPressOnLine() {
        return this.indexLinePress != -1;
    }

    boolean isHoverOnLine() {
        return this.indexLineHover != -1;
    }

    boolean isLineSelected() {
        return this.indexLineSelect != -1;
    }

    boolean isLineDrag() {
        return this.indexLineDrag != -1;
    }

    boolean isPressOnGr() {
        return this.indexGrPress != -1;
    }

    boolean isHoverOnGr() {
        return this.indexGrHover != -1;
    }

    boolean isGrSelected() {
        return this.indexGrSelect != -1;
    }

    boolean isClosestFound () {
        boolean isClosest = this.indexClosest != -1;
        if (isClosest) {
            boolean isSelfContainMO = iPatOB.getObjectN(this.indexOBPress).isContainMO();
            boolean isClosestContainMO = iPatOB.getObjectN(this.indexClosest).isContainMO();
            boolean isSameGroup = (iPatOB.getGrIndex(this.indexOBPress) == iPatOB.getGrIndex(this.indexClosest)) &&
                    iPatOB.isGroup(this.indexOBPress) && iPatOB.isGroup(this.indexClosest);
            return (!isSelfContainMO || !isClosestContainMO) && !isSameGroup;
        } else
            return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // get 2d graphic
        Graphics2D g2d = (Graphics2D) g.create();
        Graphics2D g2dHover = (Graphics2D) g.create();
        // iPat Logo
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f));
        g2d.drawImage(iPat.IMGLIB.getImage("iPat"), 530, 10, this);
        // color area (hover)
        g2dHover.setColor(iPat.IMGLIB.getColor("ovalcolor"));
        // hover on object
        if (this.isHoverOnOB() && this.indexOBHover != this.indexOBSelect) {
            if (!this.wasOnOb)
                this.alphaHover = 0f;
            this.wasOnOb = true;
            this.wasOnLine = false;
            this.wasOnGr = false;
            this.timerHover.start();
            iPatObject ob = this.iPatOB.getObjectN(this.indexOBHover);
            g2dHover.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alphaHover));
            g2dHover.fillOval(ob.getX() - 10, ob.getY() - 10, ob.getWidth() + 20, ob.getHeight() + 20);
        // hover on line
        } else if (this.isHoverOnLine() && !this.isHoverOnOB()  && this.indexLineHover != this.indexLineSelect) {
            if (!this.wasOnLine)
                this.alphaHover = 0f;
            this.wasOnOb = false;
            this.wasOnLine = true;
            this.wasOnGr = false;
            this.timerHover.start();
            Point pt = new Point(this.iPatOB.getMidPointOfLine(this.indexLineHover));
            g2dHover.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alphaHover));
            g2dHover.fillOval(pt.x - 20, pt.y - 20, 40, 40);
        // hover on group
        } else if (this.isHoverOnGr() && !this.isHoverOnOB() && !this.isHoverOnLine()) {
            if (!this.wasOnGr)
                this.alphaHover = 0f;
            this.wasOnOb = false;
            this.wasOnLine = false;
            this.wasOnGr = true;
            this.timerHover.start();
            g2dHover.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alphaHover));
            for (int index : this.iPatOB.getGrN(this.indexGrHover)) {
                iPatObject ob = this.iPatOB.getObjectN(index);
                g2dHover.fillOval(ob.getX() - 10, ob.getY() - 10, ob.getWidth() + 20, ob.getHeight() + 20);
            }
        // hover on nothing
        } else
            this.alphaHover = 0f;
        g2dHover.dispose();
        // color area (select)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
        g2d.setColor(iPat.IMGLIB.getColor("red"));
        if (this.isOBSelected()) {
            iPatObject ob = this.iPatOB.getObjectN(this.indexOBSelect);
            g2d.fillOval(ob.getX() - 10, ob.getY() - 10, ob.getWidth() + 20, ob.getHeight() + 20);
        } else if (this.isLineSelected()) {
            Point pt = new Point(this.iPatOB.getMidPointOfLine(this.indexLineSelect));
            g2d.fillOval(pt.x - 20, pt.y - 20, 40, 40);
        }
        g2d.dispose();
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
        // drag line
        if (this.isLineDrag()) {
            Point ptDragST = new Point(this.ptPress.x - 30, this.ptPress.y - 30);
            Point ptDragED = new Point(this.ptPress.x + 30, this.ptPress.y + 30);
            drawLine(g, ptDragST, ptDragED, iPat.IMGLIB.getStroke("dashed"));
        }
        // iPatObject
        for (iPatObject i : this.iPatOB.getObjects()) {
            if (i.isModule() && ((iPatModule) i).rotateSwitch) {
                double rotateRadian = Math.toRadians (this.degree);
                AffineTransform tx = AffineTransform.getRotateInstance(rotateRadian, this.xRotate, this.yRotate);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                g.drawImage(op.filter((BufferedImage) i.getImage(), null), i.getX(), i.getY(), null);
                this.timerRotate.start();
            } else
                g.drawImage(i.getImage(), i.getX(), i.getY(), this);
            g.setFont(iPat.TXTLIB.getFont("label"));
            g.drawString(i.getName(), i.getX(), i.getY() + i.getHeight() + 15);
        }
        g.dispose();
    }

    void drawLine(Graphics g, Point pt1, Point pt2, Stroke s){
        Graphics2D g2d = (Graphics2D) g.create();
        //set the stroke of the copy, not the original
        g2d.setStroke(s);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
        g2d.drawLine((int)pt1.getX(), (int)pt1.getY(), (int)pt2.getX(), (int)pt2.getY());
        //gets rid of the copy
        g2d.dispose();
    }

    void buildLink(int target) {
        iPatObject obSelf = this.iPatOB.getObjectN(this.indexOBPress);
        iPatObject obTarget = this.iPatOB.getObjectN(target);
        // Draw line
        if (!obSelf.isContainMO() || !obTarget.isContainMO())
            this.iPatOB.addLink(this.indexOBPress, target);
        // 1. w/ MO - w/ MO
        if (obSelf.isContainMO() && obTarget.isContainMO()) {
            // Do nothing
        // 2. w/o MO - w/ MO
        } else if (!obSelf.isContainMO() && obTarget.isContainMO()) {
            if (obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrOfGrIndex(obSelf.getGrIndex(), obTarget.getGrIndex());
                this.iPatOB.setGrOfContainMO(obSelf.getGrIndex(), true);
            } else if (!obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrIndex(this.indexOBPress, obTarget.getGrIndex());
                this.iPatOB.setContainMO(this.indexOBPress, true);
            } else if (obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.setGrIndex(target, obSelf.getGrIndex());
                this.iPatOB.setGrOfContainMO(obSelf.getGrIndex(), true);
            } else if (!obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.addNewGroup(this.indexOBPress, target);
                this.iPatOB.setContainMO(this.indexOBPress, true);
            }
        // 3. w/ MO - w/o MO
        } else if (obSelf.isContainMO() && !obTarget.isContainMO()) {
            if (obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrOfGrIndex(obTarget.getGrIndex(), obSelf.getGrIndex());
                this.iPatOB.setGrOfContainMO(obTarget.getGrIndex(), true);
            } else if (!obSelf.isGroup() && obTarget.isGroup()) {
                this.iPatOB.setGrIndex(this.indexOBPress, obTarget.getGrIndex());
                this.iPatOB.setGrOfContainMO(obTarget.getGrIndex(), true);
            } else if (obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.setGrIndex(target, obSelf.getGrIndex());
                this.iPatOB.setContainMO(target, true);
            } else if (!obSelf.isGroup() && !obTarget.isGroup()) {
                this.iPatOB.addNewGroup(this.indexOBPress, target);
                this.iPatOB.setContainMO(target, true);
            }
        // 4. w/o MO - w/o MO
        } else if (!obSelf.isContainMO() && !obTarget.isContainMO()) {
            if (obSelf.isGroup() && obTarget.isGroup())
                this.iPatOB.setGrOfGrIndex(obTarget.getGrIndex(), obSelf.getGrIndex());
            else if (!obSelf.isGroup() && obTarget.isGroup())
                this.iPatOB.setGrIndex(this.indexOBPress, obTarget.getGrIndex());
            else if (obSelf.isGroup() && !obTarget.isGroup())
                this.iPatOB.setGrIndex(target, obSelf.getGrIndex());
            else if (!obSelf.isGroup() && !obTarget.isGroup())
                this.iPatOB.addNewGroup(this.indexOBPress, target);
        }
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
                this.iPatOB.getObjectN(this.indexOBPress).setDeltaBound(dx, dy);
                // Check if the new position need to build a line
                this.indexClosest = this.iPatOB.getClosestIndex(this.indexOBPress);
                if (this.isClosestFound()) {
                    this.ptTempLineST = iPatOB.getObjectN(this.indexOBPress).getCenter();
                    this.ptTempLineED = iPatOB.getObjectN(this.indexClosest).getCenter();
                } else {
                    this.ptTempLineST.setLocation(0, 0);
                    this.ptTempLineED.setLocation(0, 0);
                }
            } else if (this.isPressOnLine()) {
                this.indexLineDrag = this.indexLinePress;
            } else if (this.isPressOnGr()) {
                double dx = pt.getX() - this.ptPress.getX();
                double dy = pt.getY() - this.ptPress.getY();
                for (int index : this.iPatOB.getGrN(this.indexGrPress))
                    this.iPatOB.getObjectN(index).setDeltaBound(dx, dy);
            }
        }
        repaint();
        // Update the previous point
        this.ptPress.setLocation(pt);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.indexOBHover = this.iPatOB.getPointedObject(e);
        this.indexLineHover = this.iPatOB.getPointedLine(e);
        this.indexGrHover = this.iPatOB.getPointedGroup(e);
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.ptPress.setLocation(e.getPoint());
        this.indexOBPress = this.iPatOB.getPointedObject(e);
        // Double click by left button
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            // if on a object, open it
            if (this.isPressOnOB())
                this.iPatOB.getObjectN(indexOBPress).openFile();
            // if on elsewhere, create a module
            else {
                try {
                    this.iPatOB.addiModule(
                            (int) this.ptPress.getX() - iPat.IMGLIB.getImage("module").getWidth(this) / 2,
                            (int) this.ptPress.getY() - iPat.IMGLIB.getImage("module").getHeight(this) / 2);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        this.indexOBPress = -1;
        repaint();
    }
    @Override
    public void mousePressed(MouseEvent e) {
        this.ptPress.setLocation(e.getPoint());
        if (this.isHoverOnOB()) {
            this.indexOBPress = this.indexOBHover;
            this.indexLinePress = -1;
            this.indexGrPress = -1;
        } else if (this.isHoverOnLine()) {
            this.indexLinePress = this.indexLineHover;
            this.indexOBPress = -1;
            this.indexGrPress = -1;
        } else if (this.isHoverOnGr()) {
            this.indexGrPress = this.indexGrHover;
            this.indexOBPress = -1;
            this.indexLinePress = -1;
        }
        // Click on right
        if (SwingUtilities.isRightMouseButton(e)) {
            // if on a object, drop the menu
            if (this.isHoverOnOB())
                this.iPatOB.showMenu(this.indexOBHover, e);
        // Click on left
        } else if (SwingUtilities.isLeftMouseButton(e)) {

        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // press and release on the object
        if (this.isPressOnOB()) {
            System.out.println("===== relase ===== ");
            System.out.println("index : " + indexOBPress);
            this.iPatOB.printStat();
            // object nearby
            if (this.isClosestFound())
                this.buildLink(this.indexClosest);
            // assign select from press
            this.indexOBSelect = this.indexOBPress == this.indexOBSelect ? -1 : this.indexOBPress;
            this.indexLineSelect = -1;
        // press and release on the line
        } else if (this.isPressOnLine()) {
            System.out.println("===== relase line ===== ");
            System.out.println("index : " + indexLinePress);
            // assign select from press
            this.indexLineSelect = this.indexLinePress == this.indexLineSelect ? -1 : this.indexLinePress;
            this.indexOBSelect = -1;
        } else
            resetSelectStatus();
        resetPressStatus();
        repaint();
    }

    void resetHoverStatus() {
        this.indexOBHover = -1;
        this.indexLineHover = -1;
        this.indexGrHover = -1;
        this.ptHover = new Point(0, 0);
    }
    void resetSelectStatus() {
        this.indexOBSelect = -1;
        this.indexLineSelect = -1;
        this.indexGrSelect = -1;
    }
    void resetPressStatus() {
        this.indexOBPress = -1;
        this.indexClosest = -1;
        this.indexLinePress = -1;
        this.indexLineDrag = -1;
        this.indexGrPress = -1;
        this.ptPress = new Point(0, 0);
        this.ptTempLineST = new Point(0, 0);
        this.ptTempLineED = new Point(0, 0);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("Mouse enter");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("Mouse exit");
        this.iPatOB.printStat();
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        System.out.println("Drag enter");
    }

    @Override
    public void dragOver(DropTargetDragEvent dte) {
        Point pt = dte.getLocation();
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
        // Rotation information
            this.degree = (this.degree + 5) % 360;
        // Hover
            if (this.alphaHover < .25f)
                this.alphaHover += .005f;
            else
                this.timerHover.stop();
        repaint();
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

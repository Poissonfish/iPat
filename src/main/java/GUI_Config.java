import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GUI_Config extends JFrame implements ActionListener, WindowListener, MouseMotionListener, MouseListener, ItemListener {
    // Main panel
    JPanel paneMain;
    int height, width;
        // File Tray
        JPanel paneFileTray;
        FileLabel labelP, labelGD, labelGM, labelKin, labelCov, labelTemp;
        JLabel titleP, titleGD, titleGM, titleKin, titleCov;
        Point ptTemp, ptPress;
        ArrayList<FileLabel> arrayLabel;
        // Right panel
        JPanel cardRight;
            // Pick Analysis
            JPanel paneAnalysis;
            JButton toGWAS, toGS, toGWASGS, toConverter;
            // Config
            JPanel paneConfig;
                // Common Panel
                JTabbedPane paneCommon;
                PanelWD paneWD;
                SelectPanel panePhenotype;
                PanelQC paneQC;
                // Common Card
                JPanel cardConfig;
                    // GWAS Config
                    PanelBottom paneGWAS;
                    // GS Config
                    PanelBottom paneGS;
                    // GWAS-GS Config
                    PanelBottom paneGWAS2, paneGS2;
                // Converter Config
                JPanel paneConvert;
                GroupCombo inputConv, outputConv, batchConv;
                GroupCheckBox fillnaConv;
                JButton prevConv, doneConv;

    public GUI_Config (int width, int height, Obj_Module module) throws InterruptedException {
        this.height = height;
        this.width = width;
        // ================================= File Tray =================================
        // File Tray / Initialize labels
        this.labelP = new FileLabel(Enum_FileType.Phenotype);
        this.labelGD = new FileLabel(Enum_FileType.Genotype);
        this.labelGM = new FileLabel(Enum_FileType.Map);
        this.labelCov = new FileLabel(Enum_FileType.Covariate);
        this.labelKin = new FileLabel(Enum_FileType.Kinship);
        this.labelTemp = new FileLabel(Enum_FileType.NA);
        this.titleP = new JLabel("Phenotype");
        this.titleP.setHorizontalAlignment(SwingConstants.CENTER);
        this.titleP.setFont(new Font(this.titleP.getName(), Font.BOLD, 20));
        this.titleGD = new JLabel("Genotype");
        this.titleGD.setHorizontalAlignment(SwingConstants.CENTER);
        this.titleGD.setFont(new Font(this.titleGD.getName(), Font.BOLD, 20));
        this.titleGM = new JLabel("Map");
        this.titleGM.setHorizontalAlignment(SwingConstants.CENTER);
        this.titleGM.setFont(new Font(this.titleGM.getName(), Font.BOLD, 20));
        this.titleCov = new JLabel("Covariate");
        this.titleCov.setHorizontalAlignment(SwingConstants.CENTER);
        this.titleCov.setFont(new Font(this.titleCov.getName(), Font.BOLD, 20));
        this.titleKin = new JLabel("Kinship");
        this.titleKin.setHorizontalAlignment(SwingConstants.CENTER);
        this.titleKin.setFont(new Font(this.titleKin.getName(), Font.BOLD, 20));
        // File Tray / Array
        this.arrayLabel = new ArrayList<>();
        this.arrayLabel.add(this.labelP);
        this.arrayLabel.add(this.labelGD);
        this.arrayLabel.add(this.labelGM);
        this.arrayLabel.add(this.labelCov);
        this.arrayLabel.add(this.labelKin);
        // File Tray / Layout
        this.paneFileTray = new JPanel(new MigLayout("ins 5, fill, debug", "[150!]", "[grow][grow][grow][grow][grow][grow][grow][grow][grow][grow]"));
        this.paneFileTray.add(this.titleP, "cell 0 0, grow, align c");
        this.paneFileTray.add(this.labelP, "cell 0 1, grow, align c");
        this.paneFileTray.add(this.titleGD, "cell 0 2, grow, align c");
        this.paneFileTray.add(this.labelGD, "cell 0 3, grow, align c");
        this.paneFileTray.add(this.titleGM, "cell 0 4, grow, align c");
        this.paneFileTray.add(this.labelGM, "cell 0 5, grow, align c");
        this.paneFileTray.add(this.titleCov, "cell 0 6, grow, align c");
        this.paneFileTray.add(this.labelCov, "cell 0 7, grow, align c");
        this.paneFileTray.add(this.titleKin, "cell 0 8, grow, align c");
        this.paneFileTray.add(this.labelKin, "cell 0 9, grow, align c");
        // DnD feature
        new DropTarget(this.paneFileTray, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {}
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                Point pt = dtde.getLocation();
                for (FileLabel lb : arrayLabel) {
                    if (lb.getBounds().contains(pt)) {
                        // Do Color change
                    }
                }
            }
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}
            @Override
            public void dragExit(DropTargetEvent dte) {}
            @Override
            public void drop(DropTargetDropEvent dtde) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                // Get dropped items' data
                Transferable transferable = dtde.getTransferable();
                // Get the format
                DataFlavor[] flavors = transferable.getTransferDataFlavors();
                for (DataFlavor flavor : flavors) {
                    try {
                        if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                            List<File> files = (List<File>) transferable.getTransferData(flavor);
                            Point pt = dtde.getLocation();
                            for (FileLabel lb : arrayLabel) {
                                if (lb.getBounds().contains(pt)) {
                                    module.setFile(files.get(0).getPath(), lb.getFiletype());
//                                    lb.setFile(files.get(0).getAbsolutePath());
                                    lb.setFile(new IPatFile(files.get(0).getAbsolutePath()));
                                    System.out.println("File path is '" + files.get(0).getPath() + "'.");
                                    System.out.println("Cov is empty : " + labelCov.getFile().isEmpty());
                                    System.out.println("Cov has file : " + labelCov.getHasFile());
                                    // Do Color change
//                                    lb.setColor("themecolor");
                                }
                            }
                            repaint();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dtde.dropComplete(true);
            }
        });
        // ================================= Analysis Pane =================================
        this.toGWAS = new CardButton("GWAS", "gwas");
        this.toGWAS.addActionListener(this);
        this.toGS = new CardButton("GS", "gs");
        this.toGS.addActionListener(this);
        this.toGWASGS = new CardButton("<html><center>GWAS-Assisted<br>GS</center></html>", "gwas");
        this.toGWASGS.addActionListener(this);
        this.toConverter = new CardButton("Converter", "convert");
        this.toConverter.addActionListener(this);
        this.paneAnalysis = new JPanel(new MigLayout("fill", "[300!][300!]", "[grow][grow]"));
        this.paneAnalysis.add(this.toGWAS, "cell 0 0, grow");
        this.paneAnalysis.add(this.toGS, "cell 1 0, grow");
        this.paneAnalysis.add(this.toGWASGS, "cell 0 1, grow");
        this.paneAnalysis.add(this.toConverter, "cell 1 1, grow");
        // ================================= Common =================================
        this.paneWD = new PanelWD("Project");
        this.paneQC = new PanelQC();
        this.paneCommon = new JTabbedPane();
        this.paneCommon.setFont(new Font( "Dialog", Font.BOLD|Font.ITALIC, 18 ) );
        this.paneCommon.addTab("Working Directory", paneWD);
        this.paneCommon.addTab("Quality Control", paneQC);
        // ================================= GWAS =================================
        this.paneGWAS = new PanelBottom(Enum_Analysis.GWAS, this.labelCov, true);
        this.paneGWAS.buttonLeft.addActionListener(this);
        this.paneGWAS.buttonRight.addActionListener(this);
        // ================================= GS =================================
        this.paneGS = new PanelBottom(Enum_Analysis.GS,  this.labelCov, true);
        this.paneGS.buttonLeft.addActionListener(this);
        this.paneGS.buttonRight.addActionListener(this);
        // ================================= GWAS-assited GS =================================
        this.paneGWAS2 = new PanelBottom(Enum_Analysis.GWAS, this.labelCov, false);
        this.paneGWAS2.buttonLeft.addActionListener(this);
        this.paneGWAS2.buttonRight.addActionListener(this);
        this.paneGS2 = new PanelBottom(Enum_Analysis.GS,  this.labelCov, true);
        this.paneGS2.buttonLeft.addActionListener(this);
        this.paneGS2.buttonRight.addActionListener(this);
        // ================================= Config and Card =================================
        this.cardConfig = new JPanel(new CardLayout());
        this.cardConfig.add(this.paneGWAS, "gwas");
        this.cardConfig.add(this.paneGS, "gs");
        this.cardConfig.add(this.paneGWAS2, "gwas2");
        this.cardConfig.add(this.paneGS2, "gs2");
        this.paneConfig = new JPanel(new MigLayout("fill, ins 3", "[grow]", "[grow][grow]"));
        this.paneConfig.add(this.paneCommon, "wrap, grow");
        this.paneConfig.add(this.cardConfig, "grow");
        // ================================= Converter =================================
        this.paneConvert = new JPanel(new MigLayout("fill, debug", "[grow][grow]", "[grow][grow][grow][grow][]"));
        this.inputConv = new GroupCombo("Input Format", new String[]{"------       ", "Hapmap", "Numeric", "VCF", "PLINK", "GenomeStudio"});
        this.inputConv.combo.addItemListener(this);
        this.outputConv = new GroupCombo("Output Fromat", new String[]{"------      "});
        this.outputConv.combo.setEnabled(false);
        this.fillnaConv = new GroupCheckBox("Fill NAs as heterozygotes");
        this.batchConv = new GroupCombo("Batch of Sample Size", new String[]{"32", "64", "128", "256", "512"});
        this.batchConv.setValue(1); // set default as 64
        this.prevConv = new JButton("<- Select Analysis");
        this.prevConv.addActionListener(this);
        this.doneConv = new JButton("Confirm and Run");
        this.doneConv.addActionListener(this);
        // assemble
        this.paneConvert.add(this.inputConv, "cell 0 0 2 1, grow, align c");
        this.paneConvert.add(this.outputConv, "cell 0 1 2 1, grow, align c");
        this.paneConvert.add(this.fillnaConv, "cell 0 2 2 1, grow, align c");
        this.paneConvert.add(this.batchConv, "cell 0 3 2 1, grow, align c");
        this.paneConvert.add(this.prevConv, "cell 0 4, grow, align c");
        this.paneConvert.add(this.doneConv, "cell 1 4, grow, align c");
        // ================================= Right Panel (Card) =================================
        this.cardRight = new JPanel(new CardLayout());
        this.cardRight.add(this.paneAnalysis, "analysis");
        this.cardRight.add(this.paneConfig, "gg");
        this.cardRight.add(this.paneConvert, "converter");
        // ================================= Main =================================
        this.paneMain = new JPanel(new MigLayout("fill, debug", "[grow]", "[]"));
        this.paneMain.add(this.paneFileTray, "dock west");
        this.paneMain.add(this.cardRight, "grow");
//        this.paneCommon.setBounds(300, 1, 200, 200);
//        this.paneCommon.revalidate();
//        this.paneCard.revalidate();
//        this.paneCommon.setBounds(10, 10, 1000, 100);

        // ================================= Frame =================================
        this.addWindowListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setContentPane(this.paneMain);
        this.setResizable(false);
        this.setVisible(true);
        this.setSize(this.width, this.height);
        this.pack();
        this.setLocation(iPat.WINDOWSIZE.getAppLocation(this.getWidth(), this.getHeight()));
    }

    void rebuildCommonPane () {
        this.paneCommon.removeAll();
        this.panePhenotype = new SelectPanel(this.labelP.getFile(), "Phenotype <br> Not Found", 1);
        this.paneCommon.addTab("Working Directory", paneWD);
        this.paneCommon.addTab("Phenotype", panePhenotype);
        this.paneCommon.addTab("Quality Control", paneQC);
    }

    void buildGS () {

    }

    void buildGWASGS () {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == this.paneGWAS.buttonLeft ||
            obj == this.paneGS.buttonLeft ||
            obj == this.paneGWAS2.buttonLeft) {
            new SliderListener(this.cardRight, this.paneConfig, this.paneAnalysis, "analysis", false, 10);
        } else if (obj == this.prevConv) {
            new SliderListener(this.cardRight, this.paneConvert, this.paneAnalysis, "analysis", false, 10);
        } else if (obj == this.paneGWAS.buttonRight) {
            // Run Process

        } else if (obj == this.paneGS.buttonRight) {
            // Run Process

        } else if (obj == this.paneGWAS2.buttonRight) {
            new SliderListener(this.cardConfig, this.paneGWAS2, this.paneGS2, "gs2", true, 10);
        } else if (obj == this.paneGS2.buttonLeft) {
            new SliderListener(this.cardConfig, this.paneGS2, this.paneGWAS2, "gwas2", false, 10);
        } else if (obj == this.paneGS2.buttonRight) {
            // Run Process

        } else if (obj == this.doneConv) {
            // Run Process

        } else if (obj == this.toGWAS) {
            rebuildCommonPane();
            ((CardLayout)this.cardConfig.getLayout()).show(this.cardConfig, "gwas");
            new SliderListener(this.cardRight, this.paneAnalysis, this.paneConfig, "gg", true, 10);
        } else if (obj == this.toGS) {
            rebuildCommonPane();
            ((CardLayout)this.cardConfig.getLayout()).show(this.cardConfig, "gs");
            new SliderListener(this.cardRight, this.paneAnalysis, this.paneConfig, "gg", true, 10);
        } else if (obj == this.toGWASGS) {
            rebuildCommonPane();
            ((CardLayout)this.cardConfig.getLayout()).show(this.cardConfig, "gwas2");
            new SliderListener(this.cardRight, this.paneAnalysis, this.paneConfig, "gg", true, 10);
        } else if (obj == this.toConverter) {
            new SliderListener(this.cardRight, this.paneAnalysis, this.paneConvert, "converter", true, 10);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

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

    @Override
    public void mouseClicked(MouseEvent e) {
        Point pt = e.getPoint();
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            for (FileLabel lb : this.arrayLabel) {
                if (lb.getBounds().contains(pt))
                    lb.openFile();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point pt = e.getPoint();
        this.ptPress = pt;
        for (FileLabel lb : this.arrayLabel) {
            if (lb.getBounds().contains(pt)) {
                this.labelTemp = lb;
                this.ptTemp = lb.getLocation();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point pt = e.getPoint();
        // If dragging a label
        if (!this.labelTemp.getFiletype().isNA())
            for (FileLabel lb : this.arrayLabel) {
                System.out.println(!lb.equals(this.labelTemp));
                if (lb.getBounds().contains(pt) && !lb.equals(this.labelTemp))
                    this.labelTemp.swapFile(lb);
            }
        this.labelTemp.setLocation(this.ptTemp);
        this.labelTemp = new FileLabel(Enum_FileType.NA);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point pt = e.getPoint();
        if (!this.labelTemp.getFiletype().isNA())
            this.labelTemp.setLocation(pt.x - this.ptPress.x + this.ptTemp.x,
                    pt.y - this.ptPress.y + this.ptTemp.y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        System.out.println(this.inputConv.getValue());
        if (this.inputConv.getValue().equals("------       ")) {
            this.outputConv.combo.setEnabled(false);
            this.outputConv.combo.removeAllItems();
            this.outputConv.combo.addItem("------      ");
        } else if (this.inputConv.getValue().equals("Hapmap")) {
            this.outputConv.combo.removeAllItems();
            this.outputConv.combo.addItem("Numeric");
            this.outputConv.combo.addItem("PLINK");
            this.outputConv.combo.setEnabled(true);
        } else if (this.inputConv.getValue().equals("Numeric")) {
            this.outputConv.combo.removeAllItems();
            this.outputConv.combo.addItem("PLINK");
            this.outputConv.combo.setEnabled(true);
        } else if (this.inputConv.getValue().equals("VCF")) {
            this.outputConv.combo.removeAllItems();
            this.outputConv.combo.addItem("Numeric");
            this.outputConv.combo.addItem("PLINK");
            this.outputConv.combo.setEnabled(true);
        } else if (this.inputConv.getValue().equals("PLINK")) {
            this.outputConv.combo.removeAllItems();
            this.outputConv.combo.addItem("Numeric");
            this.outputConv.combo.setEnabled(true);
        } else if (this.inputConv.getValue().equals("GenomeStudio")) {
            this.outputConv.combo.removeAllItems();
            this.outputConv.combo.addItem("Numeric");
            this.outputConv.combo.setEnabled(true);
        }
    }
    // ================================= Class : File Tray =================================


    // ================================= Class : Common Panel =================================






}

class FileLabel extends JPanel {
    JLabel labelImage, labelFilename;
    IPatFile file;
    Enum_FileType filetype;
    boolean hasFile = false;

    public FileLabel(Enum_FileType filetype) {
        super(new MigLayout("fill, ins 0", "[grow]", "[grow][grow]"));
        // Filetype
        this.filetype = filetype;
        // Icon
        this.labelImage = new JLabel();
        this.labelImage.setHorizontalAlignment(SwingConstants.CENTER);
        labelImage.setIcon(new ImageIcon(iPat.IMGLIB.file));
        this.add(labelImage, "wrap, grow");
        // Filename
        this.labelFilename = new JLabel("");
        this.add(this.labelFilename, "grow");
        // File
        this.setFile(new IPatFile());
        // Backgroud
        this.setOpaque(false);
    }
//
//    public void paintComponent(Graphics g) {
//        g.setColor(getBackground());
//        Rectangle r = g.getClipBounds();
//        g.fillRect(r.x, r.y, r.width, r.height);
//        super.paintComponent(g);
//    }

    // File I/O Deploy
    Enum_FileType getFiletype() {
        return this.filetype;
    }
    void openFile() {
        this.file.open();
    }
    IPatFile getFile() {
        return this.file;
    }

    void setFile(IPatFile file) {
        this.file = file;
        this.setHasFile(!this.file.isEmpty());
        if (this.getHasFile()) {
            // Prevent long file name
            String nameOrg = file.getName();
            int len = nameOrg.length();
            // abcde.txt (9)
            // abcdefg.txt (11)
            // abc...txt (9)
            // lenTrim = 4
            if (len > 19) {
                int lenTrim = len - 19 + 2;
                String pattern = String.format(".{%d}\\.", lenTrim);
                String nameTrim = nameOrg.replaceAll(pattern,"...");
                this.labelFilename.setText(nameTrim);
            } else
                this.labelFilename.setText(nameOrg);
        } else {
            this.labelFilename.setText("Empty");
        }
    }
    boolean getHasFile() {
        return this.hasFile;
    }
    void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
        if (this.hasFile) {
            // Do File assigning
        } else {
            // Do File deleting
            this.file = new IPatFile();
        }
    }
    void swapFile (FileLabel target) {
        // No matter what, swap it.
        IPatFile fileTemp = target.getFile();
        target.setFile(this.getFile());
        this.setFile(fileTemp);
        // Check if file is empty and change accordingly
        if (target.getFile().isEmpty())
            target.setHasFile(false);
        if (this.getFile().isEmpty())
            this.setHasFile(false);
    }

    // Misc
    void setColor(String colorcode) {
//        this.setOpaque(true);
        this.setBackground(iPat.IMGLIB.getColor(colorcode));
    }
}


class CardButton extends JButton {
    String nameCard;
    public CardButton(String nameTitle, String nameCard) {
        super(nameTitle);
        super.setFont(iPat.TXTLIB.bold);
        this.nameCard = nameCard;
    }
    String getCard() {return this.nameCard;}
}

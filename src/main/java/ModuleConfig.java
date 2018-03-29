import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;


public class ModuleConfig extends JFrame implements ActionListener, WindowListener{
    // index
    int index;
    int indexConfig;
    // method
    MethodType method;
    // format
    FileFormat format;
    // Panel (Structure
    JPanel paneMain;
    PanelBottom paneBottom;
    JTabbedPane paneTop;
    // Panel wd
    PanelWD paneWD;
    // Panel phenotype
    PanelPhenotype panePhenotype;
    // Panel QC
    PanelQC paneQC;
    // Button restore
    JButton buttonRestore;

    public ModuleConfig(int index, String name, MethodType method, ToolType tool, FileFormat format, iFile fileP, iFile fileC, int indexConfig) {
        // initialize objects
        this.index = index;
        this.indexConfig = indexConfig;
        this.method = method;
        this.format = format;
        this.paneWD = new PanelWD(name);
        this.paneWD.setPath(iPat.MODVAL.mapCommon.get("wd"));
        this.panePhenotype = new PanelPhenotype(fileP, format);
        this.paneQC = new PanelQC();
        this.paneTop = new JTabbedPane();
        this.buttonRestore = new JButton("Restore Defaults");
        this.buttonRestore.addActionListener(this);
        // Border
        switch (method) {
            case GWAS:
                this.paneTop.setBorder(new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED, null, null),
                        "GWAS (Format: " + format + ")",
                        TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED)
                );
                break;
            case GS:
                this.paneTop.setBorder(new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED, null, null),
                        "GS (Format: " + format + ")",
                        TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED)
                );
                break;
            case BSA:
                this.paneTop.setBorder(new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED, null, null),
                        "Bulk Segregation Analysis",
                        TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED)
                );
                break;
        }
        // Assemble
        this.paneTop.addTab("Working Directory", this.paneWD);
        this.paneTop.addTab("Phenotype", this.panePhenotype.getPane());
        this.paneTop.addTab("Quality Control", this.paneQC);
        this.paneBottom = new PanelBottom(method, tool, fileC);
        this.paneBottom.setBorder("Tool Config.");
        // Layout
        this.paneMain = new JPanel(new MigLayout("fillx", "[]", "[][]"));
        this.paneMain.add(this.paneTop, "cell 0 0, grow");
        this.paneMain.add(this.paneBottom.getPanel(), "cell 0 1, grow");
        // JFrame
        this.addWindowListener(this);
        this.setLocation(600, 500);
        this.setContentPane(this.paneMain);
        this.setVisible(true);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == this.buttonRestore)
            restore();
    }

    void restore() {

    }

    int getIndex() {
        return this.index;
    }

    int getIndexConfig() {
        return this.indexConfig;
    }

    ArrayList<Command> getCommand() {
        // If no command out there
        if (!this.isDeployed())
            return null;
        // Instantiate command object
        ArrayList<Command> newCommand = new ArrayList<>();
        Command command1 = new Command();
        Command command2 = new Command();
        // Common command
        switch (this.getTool()) {
            case GAPIT:
                command1.add(iPat.REXC);
                command1.add(iPat.FILELIB.getAbsolutePath("iPatGAPIT.r"));
                break;
            case FarmCPU:
                command1.add(iPat.REXC);
                command1.add(iPat.FILELIB.getAbsolutePath("iPatFarmCPU.r"));
                break;
            case PLINK:
                command1.add(iPat.REXC);
                command1.add(iPat.FILELIB.getAbsolutePath("iPatPLINK.r"));
                break;
            case gBLUP:
                command1.add(iPat.REXC);
                command1.add(iPat.FILELIB.getAbsolutePath("iPatgBLUP.r"));
                break;
            case rrBLUP:
                command1.add(iPat.REXC);
                command1.add(iPat.FILELIB.getAbsolutePath("iPatrrBLUP.r"));
                break;
            case BGLR:
                command1.add(iPat.REXC);
                command1.add(iPat.FILELIB.getAbsolutePath("iPatBGLR.r"));
                break;
            case BSA:
                command1.add(iPat.REXC);
                command1.add(iPat.FILELIB.getAbsolutePath("iPatBSA.r"));
                break;
        }
        command1.addWD(this.paneWD.getPath());
        command1.addProject(this.paneWD.getProject());
        command1.addArg("-pSelect", this.panePhenotype.getSelected());
        command1.addArg("-maf", this.paneQC.getMAF());
        command1.addArg("-ms", this.paneQC.getMS());
        command1.addArg("-format", this.format.getName());
        // Specific command and cov
        command1.addAll(this.paneBottom.getCommand());
        // add to arraylist
        newCommand.add(command1);
        // Return
        return newCommand;
    }

    ToolType getTool() {
        return this.paneBottom.getTool();
    }

    MethodType getMethod() {
        return this.method;
    }

    boolean isDeployed() {
        return this.paneBottom.isDeployed();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        // so that the list will know which panel is closing
        System.out.println("Config closing (inter)");
        System.out.println("Token is : " + this.getIndexConfig());
        iPatList.token = this.getIndexConfig();
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


    class PanelPhenotype {
        // Structure
        SelectPanel panel;
        JScrollPane scroll;
        // Blank panel;
        JPanel panelNA;
        JLabel msgNA;
        // Trait
        iFile file;
        ArrayList<String> traitNames;

        public PanelPhenotype (iFile file, FileFormat format) {
            this.panelNA = new JPanel(new MigLayout("", "[grow]", "[grow]"));
            this.msgNA = new JLabel("<html><center> Phenotype Not Found </center></html>", SwingConstants.CENTER);
            this.msgNA.setFont(iPat.TXTLIB.plain);
            this.panelNA.add(this.msgNA, "grow");
            this.scroll = new JScrollPane(this.panelNA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.file = file;
            setupTraits(format);
        }

        void setupTraits (FileFormat format) {
            String tempStr = null;
            Boolean isContainFID = false;
            try {
                tempStr = this.file.getLines(1)[0];
            } catch (IOException e) {
                System.out.println("Can't find the file!");
                e.printStackTrace();
            }
            this.traitNames = new ArrayList<>(Arrays.asList(new iFile().getSepStr(tempStr)));
            // In case user use plink format phenotype but other format of genotype
            isContainFID = this.traitNames.get(0).toUpperCase().contains("FID");
            this.traitNames.remove(0);
            if (isContainFID || format == FileFormat.PLINK || format == FileFormat.PLINKBIN)
                this.traitNames.remove(0);
            this.panel = new SelectPanel(this.traitNames.toArray(new String[0]), new String[]{"Selected", "Excluded"});
            this.scroll = new JScrollPane(this.panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//            return this.traitNames.toArray(new String[0]);
        }

        String getSelected() {
            return this.panel.getSelected();
        }

        JScrollPane getPane() {
            return this.scroll;
        }
    }

    class PanelQC extends JPanel {
        // MS
        GroupSlider sliderMS;
        // MAF
        GroupSlider sliderMAF;

        public PanelQC() {
            super(new MigLayout("fillx", "[]", "[grow][grow]"));

            this.sliderMS = new GroupSlider("By missing rate", 5, 4,
                    new String[]{"0", "0.05", "0.1", "0.2", "0.5"});
            this.sliderMAF = new GroupSlider("By missing rate", 5, 3,
                    new String[]{"0", "0.01", "0.05", "0.1", "0.2"});
            this.add(this.sliderMS, "cell 0 0, grow");
            this.add(this.sliderMAF, "cell 0 1, grow");
        }

        String getMS() {
            return this.sliderMS.getStrValue();
        }

        String getMAF() {
            return this.sliderMAF.getStrValue();
        }
    }

    class PanelWD extends JPanel{
        // GUI objects
        GroupValue project;
        GroupPath path;
        JLabel format;

        public PanelWD(String name) {
            super(new MigLayout("fill", "[grow]", "[grow][grow]"));
            this.project = new GroupValue(7, "Module Name");
            this.project.setValue(name);
            this.path = new GroupPath("Output Directory");
            this.format = new JLabel("");
            this.add(this.project, "cell 0 0, grow");
            this.add(this.path,  "cell 0 1, grow");
        }

        String getProject() {
            return this.project.getValue();
        }

        String getPath() {
            return this.path.getPath();
        }

        void setProject(String value) {
            this.project.setValue(value);
        }

        void setPath(String value) {
            this.path.setPath(value);
        }
    }

    class PanelBottom implements MouseListener, MouseMotionListener {
        // Structure
        JPanel panel;
        PanelConfig config;
        // Mouse
        MethodType method;
        ToolType toolDrag;
        ILabel labelTemp;
        Point ptTemp;
        Point ptPress;
        // iLabel
        ILabel labelGAPIT;
        ILabel labelFarmCPU;
        ILabel labelPLINK;
        ILabel labelgBLUP;
        ILabel labelrrBLUP;
        ILabel labelBGLR;

        public PanelBottom(MethodType method, ToolType tool, iFile fileC) {
            this.method = method;
            switch (method) {
                case GWAS:
                    // ILabel
                    labelGAPIT   = new ILabel("GAPIT");
                    labelFarmCPU = new ILabel("FarmCPU");
                    labelPLINK   = new ILabel("PLINK");
                    // GUI
                    this.panel = new JPanel(new MigLayout("fill", "[grow][grow]", "[grow][grow][grow]"));
                    this.panel.add(labelGAPIT, "cell 1 0, grow, w 150:150:");
                    this.panel.add(labelFarmCPU, "cell 1 1, grow, w 150:150:");
                    this.panel.add(labelPLINK, "cell 1 2, grow, w 150:150:");
                    break;
                case GS:
                    // ILabel
                    labelgBLUP   = new ILabel("<html> GAPIT <br> (gBLUP)</html>");
                    labelrrBLUP= new ILabel("rrBLUP");
                    labelBGLR   = new ILabel("BGLR");
                    // GUI
                    this.panel = new JPanel(new MigLayout("fill", "[grow][grow]", "[grow][grow][grow]"));
                    this.panel.add(labelgBLUP, "cell 1 0, grow, w 150:150:");
                    this.panel.add(labelrrBLUP, "cell 1 1, grow, w 150:150:");
                    this.panel.add(labelBGLR, "cell 1 2, grow, w 150:150:");
                    break;
                case BSA:
                    this.panel = new JPanel(new MigLayout("fill", "[grow]", "[grow]"));
                    break;
            }
            this.config = new PanelConfig(method, tool, fileC);
            this.toolDrag = ToolType.NA;
            this.panel.add(this.config, "cell 0 0 1 3, grow, w 470:470:, h 270:270:");
            this.panel.addMouseListener(this);
            this.panel.addMouseMotionListener(this);
        }

        void setBorder(String title) {
            this.panel.setBorder(new TitledBorder(
                    new EtchedBorder(EtchedBorder.LOWERED, null, null),
                    title,
                    TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
        }
        JPanel getPanel() {
            return this.panel;
        }

        ToolType getTool() {
            return this.config.getTool();
        }

        Command getCommand() {
            return this.config.getCommand();
        }

        boolean isDeployed() {
            return this.config.isDeployed();
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point pt = e.getPoint();
            this.ptPress = pt;
            // If on the method label, record the method and its location
            if (this.method == MethodType.GWAS) {
                if (labelGAPIT.getBounds().contains(pt)) {
                    this.toolDrag = ToolType.GAPIT;
                    this.labelTemp = labelGAPIT;
                    this.ptTemp = labelGAPIT.getLocation();
                    this.config.changeFontDrop();
                } else if (labelFarmCPU.getBounds().contains(pt)) {
                    this.toolDrag = ToolType.FarmCPU;
                    this.labelTemp = labelFarmCPU;
                    this.ptTemp = labelFarmCPU.getLocation();
                    this.config.changeFontDrop();
                } else if (labelPLINK.getBounds().contains(pt)) {
                    this.toolDrag = ToolType.PLINK;
                    this.labelTemp = labelPLINK;
                    this.ptTemp = labelPLINK.getLocation();
                    this.config.changeFontDrop();
                }
            } else if (this.method == MethodType.GS) {
                if (labelgBLUP.getBounds().contains(pt)) {
                    this.toolDrag = ToolType.gBLUP;
                    this.labelTemp = labelgBLUP;
                    this.ptTemp = labelgBLUP.getLocation();
                    this.config.changeFontDrop();
                } else if (labelrrBLUP.getBounds().contains(pt)) {
                    this.toolDrag = ToolType.rrBLUP;
                    this.labelTemp = labelrrBLUP;
                    this.ptTemp = labelrrBLUP.getLocation();
                    this.config.changeFontDrop();
                } else if (labelBGLR.getBounds().contains(pt)) {
                    this.toolDrag = ToolType.BGLR;
                    this.labelTemp = labelBGLR;
                    this.ptTemp = labelBGLR.getLocation();
                    this.config.changeFontDrop();
                }
            }
            // If on the config panel (tap)
            if (this.config.getBounds().contains(pt) && this.config.isDeployed())
                this.config.showDeployedPane();
            // Refresh GUI
            this.config.setVisible(true);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point pt = e.getPoint();
            // If dragging a label
            if (this.toolDrag.isDeployed()) {
                // Entering config panel
                if (this.config.getBounds().contains(pt))
                    this.config.changeFontDrop();
                // Exiting config panel
                else
                    this.config.changeFontDrag();
                // Update position of label
                this.labelTemp.setLocation(pt.x - this.ptPress.x + this.ptTemp.x,
                        pt.y - this.ptPress.y + this.ptTemp.y);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Point pt = e.getPoint();
            // If dragging a label
            if (this.toolDrag.isDeployed()) {
                // and drop on the config panel
                if (this.config.getBounds().contains(pt)) {
                    this.config.setDeployedTool(this.toolDrag);
                    this.config.changeFontSelected();
                    this.config.setTapped(false);
                // or drop elsewhere and there's a method occupied (show detail), and has been tapped
                } else if (this.config.isDeployed() && this.config.isTapped())
                    this.config.showDeployedPane();
                // or drop elsewhere and there's a method occupied (font select), and hasn't tapped yet
                else if (this.config.isDeployed() && !this.config.isTapped())
                    this.config.changeFontSelected();
                // or drop elsewhere and there's no method occupied (font drag)
                else if (!this.config.isDeployed())
                    this.config.changeFontDrag();
                // put the label back to original position
                this.labelTemp.setLocation(ptTemp);
            }
            this.ptTemp.setLocation(-1, -1);
            this.toolDrag = ToolType.NA;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        private class ILabel extends JLabel {
            ILabel(String name) {
                this.setText(name);
                this.setHorizontalAlignment(SwingConstants.CENTER);
                this.setFont(iPat.TXTLIB.bold);
                this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            }
        }

        private class PanelConfig extends JPanel {
            JLabel msg;
            boolean isDeployed;
            ToolType toolDeployed;
            // tabbed pane
            PanelTool paneDeploy;
            PanelCov paneCov;
            // text
            boolean isTapped;

            PanelConfig(MethodType method, ToolType tool, iFile fileC) {
                this.msg = new JLabel("", SwingConstants.CENTER);
                this.msg.setFont(iPat.TXTLIB.plainBig);
                this.isTapped = false;
                // If is deployed
                if (tool.isDeployed()) {
                    this.isDeployed = true;
                    this.toolDeployed = tool;
                    this.changeFontSelected();
                // If not deployed
                } else {
                    this.isDeployed = false;
                    this.toolDeployed = ToolType.NA;
                    this.changeFontDrag();
                }
                this.paneCov = new PanelCov(method, fileC);
                // GUI
                this.setOpaque(true);
                this.setLayout(new MigLayout("", "[grow]", "[grow]"));
                this.add(this.msg, "grow");
            }

            void showDeployedPane() {
                this.removeAll();
                this.revalidate();
                this.repaint();
                System.out.println("remove!");
                if (!this.isTapped()) {
                    switch (this.toolDeployed) {
                        case GAPIT: this.paneDeploy = new PanelGAPIT(this.paneCov);
                            break;
                        case FarmCPU: this.paneDeploy = new PanelFarmCPU(this.paneCov);
                            break;
                        case PLINK: this.paneDeploy = new PanelPlink(this.paneCov);
                            break;
                        case gBLUP: this.paneDeploy = new PanelgBLUP(this.paneCov);
                            break;
                        case rrBLUP: this.paneDeploy = new PanelrrBLUP(this.paneCov);
                            break;
                        case BGLR: this.paneDeploy = new PanelBGLR(this.paneCov);
                            break;
                        case BSA: this.paneDeploy = new PanelBSA();
                            break;
                    }
                    this.isTapped = true;
                }
                this.setLayout(new MigLayout("", "[grow]", "[grow]"));
                this.add(this.paneDeploy, "grow");
            }

            void setTapped(boolean isTapped) {
                this.isTapped = isTapped;
            }

            boolean isTapped() {
                return this.isTapped;
            }

            boolean isDeployed() {
                return this.isDeployed;
            }

            Command getCommand() {
                Command command = new Command();
                // get cov select
                if (this.isTapped()) {
                    command.addAll(this.paneCov.getCommand());
                    command.addAll(this.paneDeploy.getCommand());
                    return command;
                // return default command
                } else {
                    command.addArg("-cSelect", "NA");
                    HashMap<String, String> map;
                    switch (this.toolDeployed) {
                        case GAPIT:
                            map = iPat.MODVAL.mapGAPIT;
                            command.add("-arg");
                            command.add(map.get("model"));
                            command.add(map.get("cluster"));
                            command.add(map.get("group"));
                            command.add(map.get("snpfrac"));
                            command.add(map.get("checkS"));
                            return command;
                        case FarmCPU:
                            map = iPat.MODVAL.mapFarmCPU;
                            command.add("-arg");
                            command.add(map.get("combo"));
                            command.add(map.get("loop"));
                            return command;
                        case PLINK:

                        case gBLUP:
                            map = iPat.MODVAL.mapgBLUP;
                            command.add("-arg");
                            command.add(map.get("snpfrac"));
                            command.add(map.get("checkS"));
                            return command;
                        case rrBLUP:
                            map = iPat.MODVAL.maprrBLUP;
                            command.add("-arg");
                            command.add(map.get("impute"));
                            command.add(map.get("shrink"));
                            return command;
                        case BGLR:
                            map = iPat.MODVAL.mapBGLR;
                            command.add("-arg");
                            command.add(map.get("model"));
                            command.add(map.get("response"));
                            command.add(map.get("niter"));
                            command.add(map.get("burn"));
                            command.add(map.get("thin"));
                            return command;
                        case BSA:
                            map = iPat.MODVAL.mapBGLR;
                            command.add("-arg");
                            command.add(map.get("window"));
                            command.add(map.get("power"));
                            return command;
                    }
                }
                return null;
            }

            ToolType getTool() {
                return this.toolDeployed;
            }

            void setDeployedTool(ToolType tool) {
                this.isDeployed = true;
                this.toolDeployed = tool;
            }
            void removeMethod() {
                this.isDeployed = false;
                this.toolDeployed = ToolType.NA;
            }

            void clearContent() {
                this.removeAll();
                this.setLayout(new MigLayout("", "[grow]", "[grow]"));
                this.add(this.msg, "grow");
            }

            void changeFontDrop() {
                this.clearContent();
                this.setBackground(iPat.IMGLIB.colorHintDrop);
                this.msg.setText("<html><center> Drop <br> to <br> Deploy </center></html>");
            }

            void changeFontDrag() {
                this.clearContent();
                this.setBackground(iPat.IMGLIB.colorHintDrag);
                this.msg.setText("<html><center> Drag a Package <br> Here  </center></html>");
            }

            void changeFontSelected() {
                this.clearContent();
                this.setBackground(iPat.IMGLIB.colorHintTap);
                this.msg.setText("<html><center>"+ this.toolDeployed +"<br> Selected <br> (Tap for details) </center></html>");
            }
        }
    }

    class PanelCov implements ActionListener{
        // Structure
        JPanel paneMain;
        SelectPanel panel;
        JScrollPane scroll;
        // Blank panel;
        JPanel panelNA;
        JLabel msgNA;
        // Trait
        ArrayList<String> covNames;
        // GWAS_Assisted
        GroupSlider slideCutoff;
        GroupCheckBox checkGWAS;
        // file is Empty
        boolean isEmpty = false;
        public PanelCov (MethodType method, iFile file) {
            this.isEmpty = file.isEmpty();
            // If no covariate file
            if (this.isEmpty) {
                this.panelNA = new JPanel(new MigLayout("", "[grow]", "[grow]"));
                this.msgNA = new JLabel("<html><center> Covariates Not Found </center></html>", SwingConstants.CENTER);
                this.msgNA.setFont(iPat.TXTLIB.plain);
                this.panelNA.add(this.msgNA, "grow");
                this.scroll = new JScrollPane(this.panelNA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            // If has covariate file
            } else {
                String tempStr = null;
                try {
                    tempStr = file.getLines(1)[0];
                } catch (IOException e) {
                    System.out.println("Can't find the file!");
                    e.printStackTrace();
                }
                this.covNames = new ArrayList<>(Arrays.asList(new iFile().getSepStr(tempStr)));
                this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"Selected", "Excluded"});
                this.scroll = new JScrollPane(this.panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            }
            // Assemble : if is GS
            if (method == MethodType.GS) {
                // Initialize components
                this.checkGWAS = new GroupCheckBox("Includes seleted SNPs from GWAS");
                this.checkGWAS.setCheck(false);
                this.checkGWAS.check.addActionListener(this);
                this.slideCutoff = new GroupSlider("Bonferroni cutoff (Power of 10)", 2, 10, 3, 1, 3);
                this.slideCutoff.slider.setEnabled(false);
                // Assemble
                this.paneMain = new JPanel(new MigLayout("fillx", "[grow]", "[grow][grow][grow]"));
                this.paneMain.add(this.checkGWAS, "grow, cell 0 0");
                this.paneMain.add(this.slideCutoff, "grow, cell 0 1");
                this.paneMain.add(this.scroll, "grow, cell 0 2");
            // Assemble : if is GWAS
            } else {
                // Assemble
                this.paneMain = new JPanel(new MigLayout("fillx", "[grow]", "[grow]"));
                this.paneMain.add(this.scroll, "grow, cell 0 0");
            }
        }

        boolean isEmpty() {
            return this.isEmpty;
        }

        String[] getCovs () {
            return this.covNames.toArray(new String[0]);
        }

        String getSelected () {
            return isEmpty ? "NA" : this.panel.getSelected();
        }

        void setAsBayes() {
            this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"FIXED", "BRR", "BayesA", "BL", "BayesB", "BayesC", "OMIT IT"});
        }

        void setAsRegular() {
            this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"Selected", "Excluded"});
        }

        JPanel getPane() {
            return this.paneMain;
        }

        Command getCommand() {
            Command command = new Command();
            command.addArg("-cSelect", this.getSelected());
            return command;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object obs = e.getSource();
            if (obs == checkGWAS.check)
                slideCutoff.slider.setEnabled(!slideCutoff.slider.isEnabled());
        }
    }

    class SelectPanel extends JPanel {
        GroupCombo[] combo;
        int size;

        public SelectPanel(String[] names, String[] items){
            this.size = names.length;
            this.setLayout(new MigLayout("fillx"));
            combo = new GroupCombo[names.length];
            for(int i = 0; i < this.size; i++){
                combo[i] = new GroupCombo(names[i], items);
                this.add(combo[i], "wrap, align c");
            }
        }

        public String getSelected(){
            StringBuffer indexCov = new StringBuffer();
            for (int i = 0; i < this.size; i ++)
                indexCov.append((String)combo[i].getValue() + "sep");
            return indexCov.toString();
        }
    }
}

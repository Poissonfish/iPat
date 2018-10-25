import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class GUI_Models extends JFrame implements ActionListener, WindowListener{
    // index
    int index;
    int indexConfig;
    // method
    Enum_Analysis method;
    // format
    Enum_FileFormat format;
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

    public GUI_Models(int index, String name, Enum_Analysis method,
                      Enum_Tool tool,
                      Enum_FileFormat format,
                      IPatFile fileP, String selectP,
                      IPatFile fileC, String selectC,
                      int indexConfig) {
        // initialize objects
        this.index = index;
        this.indexConfig = indexConfig;
        this.method = method;
        this.format = format;
        this.paneWD = new PanelWD(name);
        this.paneWD.setPath(iPat.MODVAL.mapCommon.get("wd"));
        this.panePhenotype = new PanelPhenotype(fileP, format, selectP);
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
        this.paneBottom = new PanelBottom(method, tool, fileC, selectC);
        this.paneBottom.setBorder("Tool Configuration");
        // Layout
        this.paneMain = new JPanel(new MigLayout("fillx", "[]", "[300!][400::]"));
        this.paneMain.add(this.paneTop, "cell 0 0, growx");
        this.paneMain.add(this.paneBottom.getPanel(), "cell 0 1, grow");
        // Fill in modify value
        this.load();
        // JFrame
        this.addWindowListener(this);
        this.setContentPane(this.paneMain);
        this.setResizable(false);
        this.setVisible(true);
        this.pack();
        this.setLocation(iPat.WINDOWSIZE.getAppLocation(this.getWidth(), this.getHeight()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == this.buttonRestore)
            restore();
    }

    void load() {
        // load for top panel
        this.paneWD.setPath(iPat.MODVAL.mapCommon.get("wd"));
        this.paneQC.setMAF(iPat.MODVAL.mapCommon.get("maf"));
        this.paneQC.setMS(iPat.MODVAL.mapCommon.get("ms"));

        // load for bottom panel
//        this.paneBottom.load();
    }

    void save() {
        // common
        iPat.MODVAL.mapCommon.put("wd", this.paneWD.getPath());
        iPat.MODVAL.mapCommon.put("maf", this.paneQC.getMAF());
        iPat.MODVAL.mapCommon.put("ms", this.paneQC.getMS());

    }

    void restore() {

    }

    int getIndex() {
        return this.index;
    }

    int getIndexConfig() {
        return this.indexConfig;
    }

    IPatCommand getCommand() {
        // If no command out there
        if (!this.isDeployed())
            return null;
        // Instantiate command object
        ArrayList<IPatCommand> newCommand = new ArrayList<>();
        IPatCommand command1 = new IPatCommand();
        IPatCommand command2 = new IPatCommand();
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
        command1.addCov(this.paneBottom.getCov());
        command1.addArg("-pSelect", this.panePhenotype.getSelected());
        command1.addArg("-maf", this.paneQC.getMAF());
        command1.addArg("-ms", this.paneQC.getMS());
        command1.addArg("-format", this.format.getName());
        // Specific command and cov
        command1.addAll(this.paneBottom.getCommand());
        return command1;
//        // add to arraylist
//        newCommand.add(command1);
//        // Return
//        return newCommand;
    }

    Enum_Tool getTool() {
        return this.paneBottom.getTool();
    }

    Enum_Analysis getMethod() {
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
        save();
        // so that the list will know which panel is closing
        System.out.println("Config closing (inter)");
        System.out.println("Token is : " + this.getIndexConfig());
        Obj_Manager.token = this.getIndexConfig();
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
        IPatFile file;
        ArrayList<String> traitNames;
        String selectP = null;

        public PanelPhenotype (IPatFile file, Enum_FileFormat format, String selectP) {
            this.panelNA = new JPanel(new MigLayout("", "[grow]", "[grow]"));
            this.msgNA = new JLabel("<html><center> Phenotype Not Found </center></html>", SwingConstants.CENTER);
            this.msgNA.setFont(iPat.TXTLIB.plain);
            this.panelNA.add(this.msgNA, "grow");
            this.scroll = new JScrollPane(this.panelNA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.file = file;
            this.selectP = selectP;
            setupTraits(format);
        }

        void setupTraits (Enum_FileFormat format) {
            String tempStr = null;
            Boolean isContainFID = false;
            try {
                tempStr = this.file.getLines(1)[0];
            } catch (IOException e) {
                System.out.println("Can't find the file!");
                e.printStackTrace();
            }
            this.traitNames = new ArrayList<>(Arrays.asList(new IPatFile().getSepStr(tempStr)));
            // In case user use plink format phenotype but other format of genotype
            isContainFID = this.traitNames.get(0).toUpperCase().contains("FID");
            this.traitNames.remove(0);
            if (isContainFID || format == Enum_FileFormat.PLINK || format == Enum_FileFormat.PLINKBIN)
                this.traitNames.remove(0);
            this.panel = new SelectPanel(this.traitNames.toArray(new String[0]), new String[]{"Selected", "Excluded"}, this.selectP);
            this.scroll = new JScrollPane(this.panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
            super(new MigLayout("fillx", "[grow]", "[grow][grow]"));

            this.sliderMS = new GroupSlider("By missing rate", 4,
                    new String[]{"0", "0.05", "0.1", "0.2", "0.5"});
            this.sliderMAF = new GroupSlider("By MAF ", 3,
                    new String[]{"0", "0.01", "0.05", "0.1", "0.2"});
            this.add(this.sliderMS, "cell 0 0, grow, align c");
            this.add(this.sliderMAF, "cell 0 1, grow, align c");
        }

        String getMS() {
            return this.sliderMS.getStrValue();
        }

        String getMAF() {
            return this.sliderMAF.getStrValue();
        }

        void setMS(String val) {
            this.sliderMS.setStrValue(val);
        }

        void setMAF(String val) {
            this.sliderMAF.setStrValue(val);
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
        Enum_Analysis method;
        Enum_Tool toolDrag;
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

        public PanelBottom(Enum_Analysis method, Enum_Tool tool, IPatFile fileC, String selectC) {
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
            this.config = new PanelConfig(method, tool, fileC, selectC);
            this.toolDrag = Enum_Tool.NA;
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

        Enum_Tool getTool() {
            return this.config.getTool();
        }

        String getCov() {
            return this.config.getCov();
        }

        IPatCommand getCommand() {
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
            if (this.method == Enum_Analysis.GWAS) {
                if (labelGAPIT.getBounds().contains(pt)) {
                    this.toolDrag = Enum_Tool.GAPIT;
                    this.labelTemp = labelGAPIT;
                    this.ptTemp = labelGAPIT.getLocation();
                    this.config.changeFontDrop();
                } else if (labelFarmCPU.getBounds().contains(pt)) {
                    this.toolDrag = Enum_Tool.FarmCPU;
                    this.labelTemp = labelFarmCPU;
                    this.ptTemp = labelFarmCPU.getLocation();
                    this.config.changeFontDrop();
                } else if (labelPLINK.getBounds().contains(pt)) {
                    this.toolDrag = Enum_Tool.PLINK;
                    this.labelTemp = labelPLINK;
                    this.ptTemp = labelPLINK.getLocation();
                    this.config.changeFontDrop();
                }
            } else if (this.method == Enum_Analysis.GS) {
                if (labelgBLUP.getBounds().contains(pt)) {
                    this.toolDrag = Enum_Tool.gBLUP;
                    this.labelTemp = labelgBLUP;
                    this.ptTemp = labelgBLUP.getLocation();
                    this.config.changeFontDrop();
                } else if (labelrrBLUP.getBounds().contains(pt)) {
                    this.toolDrag = Enum_Tool.rrBLUP;
                    this.labelTemp = labelrrBLUP;
                    this.ptTemp = labelrrBLUP.getLocation();
                    this.config.changeFontDrop();
                } else if (labelBGLR.getBounds().contains(pt)) {
                    this.toolDrag = Enum_Tool.BGLR;
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
            this.toolDrag = Enum_Tool.NA;
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
            Enum_Tool toolDeployed;
            // tabbed pane
            PanelTool paneDeploy;
            PanelCov paneCov;
            // text
            boolean isTapped;
            // method
            Enum_Analysis method;

            PanelConfig(Enum_Analysis method, Enum_Tool tool, IPatFile fileC, String selectC) {
                this.msg = new JLabel("", SwingConstants.CENTER);
                this.msg.setFont(iPat.TXTLIB.plainBig);
                this.isTapped = false;
                this.method = method;
                // If is deployed
                if (tool.isDeployed()) {
                    this.isDeployed = true;
                    this.toolDeployed = tool;
                    this.changeFontSelected();
                // If not deployed
                } else {
                    this.isDeployed = false;
                    this.toolDeployed = Enum_Tool.NA;
                    this.changeFontDrag();
                }
                this.paneCov = new PanelCov(method, fileC, selectC);
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

            String getCov() {
                return this.paneCov.getSelected();
            }

            IPatCommand getCommand() {
                IPatCommand command = new IPatCommand();
                if (this.method == Enum_Analysis.GS)
                    command.addAll(this.paneCov.getGWAS());
                if (this.isTapped()) {
                    command.addAll(this.paneDeploy.getCommand());
                    return command;
                // return default command
                } else {
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
                            command.add(map.get("bin"));
                            command.add(map.get("loop"));
                            return command;
                        case PLINK:
                            map = iPat.MODVAL.mapPLINK;
                            command.add("-arg");
                            command.add(map.get("ci"));
                            command.add(map.get("model"));
                            command.add(iPat.FILELIB.getAbsolutePath("plink"));
                            return command;
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

            Enum_Tool getTool() {
                return this.toolDeployed;
            }

            void setDeployedTool(Enum_Tool tool) {
                this.isDeployed = true;
                this.toolDeployed = tool;
            }
            void removeMethod() {
                this.isDeployed = false;
                this.toolDeployed = Enum_Tool.NA;
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
        Enum_Analysis method;
        // Structure
        JPanel paneMain;
        SelectPanel panel;
        JScrollPane scroll;
        // Blank panel;
        JLabel msgNA;
        // cov
        ArrayList<String> covNames;
        String selectC = null;
        // GWAS_Assisted
        GroupSlider slideCutoff;
        GroupCheckBox checkGWAS;
        // file is Empty
        boolean isEmpty = false;
        public PanelCov (Enum_Analysis method, IPatFile file, String selectC) {
            this.method = method;
            this.selectC = selectC;
            this.isEmpty = file.isEmpty();
            // If no covariate file
            if (this.isEmpty) {
                this.panel = new SelectPanel(new MigLayout("", "[grow]", "[grow]"));
                this.msgNA = new JLabel("<html><center> Covariates Not Found </center></html>", SwingConstants.CENTER);
                this.msgNA.setFont(iPat.TXTLIB.plain);
                this.panel.add(this.msgNA, "grow");
            // If has covariate file
            } else {
                String tempStr = null;
                try {
                    tempStr = file.getLines(1)[0];
                } catch (IOException e) {
                    System.out.println("Can't find the file!");
                    e.printStackTrace();
                }
                this.covNames = new ArrayList<>(Arrays.asList(new IPatFile().getSepStr(tempStr)));
                this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"Selected", "Excluded"}, selectC);
            }
            // Assemble : if is GS
            if (method == Enum_Analysis.GS) {
                JPanel paneSub = new JPanel(new MigLayout("fillx", "[grow]", "[grow][grow][grow]"));
                // Initialize components
                this.checkGWAS = new GroupCheckBox("Includes seleted SNPs from GWAS");
                this.checkGWAS.setCheck(false);
                this.checkGWAS.check.addActionListener(this);
                this.slideCutoff = new GroupSlider("<html>Bonferroni Cutoff<br>(Negative Power of 10)</html>", 3, new String[]{"0.001", "0.0001", "0.00001", "0.000001", "0.0000001", "0.00000001", "0.000000001", "0.0000000001"}, new String[]{"3", "4", "5", "6", "7", "8", "9", "10"});
                this.slideCutoff.slider.setEnabled(false);
                // Assemble
                paneSub.add(this.checkGWAS, "grow, cell 0 0, align c");
                paneSub.add(this.slideCutoff, "grow, cell 0 1, align c");
                paneSub.add(this.panel, "grow, cell 0 2, align c");
                this.scroll = new JScrollPane(paneSub, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            // Assemble : if is GWAS
            } else {
                // Assemble
                this.scroll = new JScrollPane(this.panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }
            this.paneMain = new JPanel(new MigLayout("fillx", "[grow]", "[grow]"));
            this.paneMain.add(this.scroll, "grow");
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
            this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"FIXED", "BRR", "BayesA", "BL", "BayesB", "BayesC", "OMIT IT"}, this.selectC);
        }

        void setAsRegular() {
            this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"Selected", "Excluded"}, this.selectC);
        }

        JPanel getPane() {
            return this.paneMain;
        }

        IPatCommand getGWAS() {
            IPatCommand command = new IPatCommand();
            command.addArg("-gwas", this.checkGWAS.isCheck() ? "TRUE" : "FALSE");
            command.add(this.slideCutoff.getStrValue());
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
        String[] select;
        int size;
        boolean isReset = true;

        public SelectPanel(MigLayout layout) {
            super(layout);
        }

        public SelectPanel(String[] names, String[] items, String select){
            this.size = names.length;
            this.setLayout(new MigLayout("fillx"));
            this.combo = new GroupCombo[size];
            // split select into array
            // if not null
            if (select != null) {
                this.select = select.split("sep");
                // if cov has not changed (same set and same tool
                boolean isSame = Arrays.asList(items).contains(this.select[0]);
                if (this.select.length == this.size && isSame)
                    this.isReset = false;
            }
            // build combo list
            for (int i = 0; i < this.size; i++) {
                combo[i] = new GroupCombo(names[i], items);
                if (!this.isReset)
                    combo[i].setValue(Arrays.asList(items).indexOf(this.select[i]));
                this.add(combo[i], "wrap, align c");
            }
        }

        public String getSelected(){
            StringBuffer indexCov = new StringBuffer();
            for (int i = 0; i < this.size; i ++)
                indexCov.append(combo[i].getValue() + "sep");
            return indexCov.toString();
        }
    }
}

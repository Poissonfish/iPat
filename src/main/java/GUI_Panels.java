import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

abstract class PanelTool extends JTabbedPane {
    public PanelTool() {
        super();
        this.setFont(new Font( "Dialog", Font.BOLD|Font.ITALIC, 18));
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
    }
    abstract ArrayList<String> getCommand();
}

// ================================= Class : Model Panel =================================
class PanelGAPIT extends PanelTool {
    JPanel paneBasic;
    SelectPanel paneCov;
    GroupCombo comboModel;
    GroupSlider slidePC;

    public PanelGAPIT(IPatFile cFile) {
        // Basic features
        this.paneBasic = new JPanel(new MigLayout("fillx", "[]", "[grow][grow]"));
        this.comboModel = new GroupCombo("Select a model", new String[]{"GLM", "MLM", "CMLM", "FarmCPU"});
        this.slidePC = new GroupSlider("Number of PCs included", 1, new String[]{"3", "4", "5", "6", "7", "8", "9", "10"});
        this.paneBasic.add(this.comboModel, "cell 0 0, align c");
        this.paneBasic.add(this.slidePC, "cell 0 1, align c");
        // cov
        this.paneCov = new SelectPanel(cFile, "Covariates <br> Not Found", 0);
        // Build tab pane
        this.addTab("GAPIT Input", this.paneBasic);
        this.addTab("Covariates", this.paneCov);
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add(iPat.REXC);
        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
        command.add("GAPIT");
        command.add("-arg");
        command.add(this.comboModel.getValue());
        command.add(this.slidePC.getStrValue());
        command.add("-cSelect");
        command.add(this.paneCov.getSelected());
        return command;
    }
}

class PanelFarmCPU extends PanelTool {
    JPanel paneBasic;
    SelectPanel paneCov;
    GroupCombo comboBin;
    GroupSlider slideLoop;

    public PanelFarmCPU(IPatFile cFile) {
        // Basic features
        paneBasic = new JPanel(new MigLayout("fillx", "[]", "[grow][grow]"));
        comboBin = new GroupCombo("Method bin",
                new String[]{"optimum", "static"});
        slideLoop = new GroupSlider("maxLoop", 10, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        paneBasic.add(comboBin, "cell 0 0, align c");
        paneBasic.add(slideLoop, "cell 0 1, align c");
        // cov
        this.paneCov = new SelectPanel(cFile, "Covariates <br> Not Found", 0);
        // Build tab pane
        this.addTab("FarmCPU input", this.paneBasic);
        this.addTab("Covariates", this.paneCov);
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add(iPat.REXC);
        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
        command.add("FarmCPU");
        command.add("-arg");
        command.add(this.comboBin.getValue());
        command.add(this.slideLoop.getStrValue());
        command.add("-cSelect");
        command.add(this.paneCov.getSelected());
        return command;
    }
}

class PanelPlink extends PanelTool {
    JPanel paneBasic;
    SelectPanel paneCov;
    GroupSlider slideCI;
    GroupCombo comboModel;

    public PanelPlink(IPatFile cFile) {
        // Basic features
        paneBasic = new JPanel(new MigLayout("fillx", "[]", "[grow][grow]"));
        slideCI = new GroupSlider("C.I.", 1, new String[]{"0.95", "0.975", "0.995"});
        comboModel = new GroupCombo("Method",
                new String[]{"GLM", "Logistic Regression"});
        paneBasic.add(slideCI, "cell 0 0, align c");
        paneBasic.add(comboModel, "cell 0 1, align c");
        // cov
        this.paneCov = new SelectPanel(cFile, "Covariates <br> Not Found", 0);
        // Build tab pane
        this.addTab("PLINK input", paneBasic);
        this.addTab("Covariates", paneCov);
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add(iPat.REXC);
        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
        command.add("PLINK");
        command.add("-arg");
        command.add(slideCI.getStrValue());
        command.add(comboModel.getValue());
        command.add(iPat.FILELIB.getAbsolutePath("plink"));
        command.add("-cSelect");
        command.add(this.paneCov.getSelected());
        return command;
    }
}

class PanelgBLUP extends PanelTool {
    JPanel paneBasic;
    JPanel paneGS;
    SelectPanel paneCov;
    JPanel paneGWASAssist;
    // parameters
    GroupCombo comboImpute;
    JCheckBox checkShrink;
    // validation
    JCheckBox checkValid;
    GroupSlider slideFold;
    GroupSlider slideIter;
    // GWAS-assist
    boolean isGWASGS;
    public PanelgBLUP(IPatFile cFile, boolean isGWASGS) {
        this.isGWASGS = isGWASGS;
        // GS
        this.paneGS = new JPanel(new MigLayout("fillx, ins 3", "[grow][grow]", "[grow][grow]"));
        this.checkValid = new JCheckBox("Validation on accuracy?");
        this.slideFold = new GroupSlider("Folds", 2, new String[]{"1", "3", "5", "10"});
        this.slideIter = new GroupSlider("Iteration", 1, new String[]{"10", "50", "100"});
        this.paneGS.add(this.checkValid, "cell 0 0 1 2, align c");
        this.paneGS.add(this.slideFold, "cell 1 0, align c");
        this.paneGS.add(this.slideIter, "cell 1 1, align c");
        // cov
        this.paneCov = new SelectPanel(cFile, "Covariates <br> Not Found", 0);
        // Build tab pane
        this.addTab("GS Validation", this.paneGS);
        this.addTab("Covariates", this.paneCov);
    }
    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add(iPat.REXC);
        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
        command.add("gBLUP");
        command.add("-gs");
        command.add(this.checkValid.isSelected() ? "TRUE" : "FALSE");
        command.add(this.slideFold.getStrValue());
        command.add(this.slideIter.getStrValue());
        command.add("-gwas");
        command.add(this.isGWASGS ? "TRUE" : "FALSE");
        command.add("-cSelect");
        command.add(this.paneCov.getSelected());
        return command;
    }
}

class PanelrrBLUP extends PanelTool {
    JPanel paneGS;
    SelectPanel paneCov;
    // validation
    JCheckBox checkValid;
    GroupSlider slideFold;
    GroupSlider slideIter;
    // GWAS-assist
    boolean isGWASGS;
    public PanelrrBLUP(IPatFile cFile, boolean isGWASGS) {
        this.isGWASGS = isGWASGS;
        // Basic features
        this.paneGS = new JPanel(new MigLayout("fillx, ins 3", "[grow][grow]", "[grow][grow]"));
        this.checkValid = new JCheckBox("Validation on accuracy?");
        this.slideFold = new GroupSlider("Folds", 2, new String[]{"1", "3", "5", "10"});
        this.slideIter = new GroupSlider("Iteration", 1, new String[]{"10", "50", "100"});
        this.paneGS.add(this.checkValid, "cell 0 0 1 2, align c");
        this.paneGS.add(this.slideFold, "cell 1 0, align c");
        this.paneGS.add(this.slideIter, "cell 1 1, align c");
        // cov
        this.paneCov = new SelectPanel(cFile, "Covariates <br> Not Found", 0);
        // Build tab pane
        this.addTab("GS Validation", this.paneGS);
        this.addTab("Covariates", this.paneCov);
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add(iPat.REXC);
        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
        command.add("rrBLUP");
        command.add("-gs");
        command.add(this.checkValid.isSelected() ? "TRUE" : "FALSE");
        command.add(this.slideFold.getStrValue());
        command.add(this.slideIter.getStrValue());
        command.add("-gwas");
        command.add(this.isGWASGS ? "TRUE" : "FALSE");
        command.add("-cSelect");
        command.add(this.paneCov.getSelected());
        return command;
    }
}

class PanelBGLR extends PanelTool  {
    JScrollPane scroll;
    JPanel basic;
    JPanel paneGS;
    SelectPanel paneCov;
    JPanel paneGWASAssist;

    GroupCombo comboModel;
    GroupCombo comboResponse;
    GroupSlider slideNIter;
    GroupSlider slideBurnIn;
    GroupSlider slideThin;
    // validation
    JCheckBox checkValid;
    GroupSlider slideFold;
    GroupSlider slideIter;
    // GWAS-assist1
    boolean isGWASGS;
    GroupSlider slideCut;

    public PanelBGLR(IPatFile cFile, boolean isGWASGS) {
        this.isGWASGS = isGWASGS;
        // Basic features
        this.basic = new JPanel(new MigLayout("fillx, ins 3", "[grow]", "[grow][grow][grow][grow][grow]"));
        this.comboModel = new GroupCombo("Model of the Predictor (markers)",
                new String[]{"BRR", "BL", "BayesA", "BayesB", "BayesC"});
        this.slideNIter = new GroupSlider("nIter", 2, new String[]{"1000", "5000", "10000", "30000", "50000", "100000"}, new String[]{"1K", "5K", "10K", "30k", "50k", "100k"});
        this.slideBurnIn = new GroupSlider("burnIn", 2, new String[]{"200", "500", "1000", "3000", "5000", "10000"});
        this.basic.add(this.comboModel, "cell 0 0, grow");
        this.basic.add(this.slideNIter, "cell 0 1, grow");
        this.basic.add(this.slideBurnIn, "cell 0 2, grow");
        // GS
        this.paneGS = new JPanel(new MigLayout("fillx, ins 3", "[grow][grow]", "[grow][grow]"));
        this.checkValid = new JCheckBox("Validation on accuracy?");
        this.slideFold = new GroupSlider("Folds", 2, new String[]{"1", "3", "5", "10"});
        this.slideIter = new GroupSlider("Iteration", 1, new String[]{"10", "50", "100"});
        this.paneGS.add(this.checkValid, "cell 0 0 1 2, align c");
        this.paneGS.add(this.slideFold, "cell 1 0, align c");
        this.paneGS.add(this.slideIter, "cell 1 1, align c");
        // cov
        this.paneCov = new SelectPanel(cFile, "Covariates <br> Not Found", 0);
        // scroll
//        scroll = new JScrollPane(this.basic, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Build tab pane
        this.addTab("BGLR Input", this.basic);
        this.addTab("GS Validation", this.paneGS);
        this.addTab("Covariates", paneCov);
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add(iPat.REXC);
        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
        command.add("BGLR");
        command.add("-arg");
        command.add(comboModel.getValue());
        command.add(slideNIter.getStrValue());
        command.add(slideBurnIn.getStrValue());
        command.add("-gs");
        command.add(this.checkValid.isSelected() ? "TRUE" : "FALSE");
        command.add(this.slideFold.getStrValue());
        command.add(this.slideIter.getStrValue());
        command.add("-gwas");
        command.add(this.isGWASGS ? "TRUE" : "FALSE");
        command.add("-cSelect");
        command.add(this.paneCov.getSelected());
        return command;
    }
}

// ================================= Class : Common Panel =================================

class PanelQC extends JPanel implements ActionListener {
    // MS
    GroupCheckBox checkMS;
    GroupSlider sliderMS;
    // MAF
    GroupCheckBox checkMAF;
    GroupSlider sliderMAF;

    public PanelQC() {
        super(new MigLayout("fillx, ins 3", "[grow][grow]", "[grow][grow]"));
        this.checkMS = new GroupCheckBox("By MS");
        this.sliderMS = new GroupSlider("keep markers with missing rate LOWER than", 6,
                new String[]{"0.01", "0.03", "0.05", "0.10", "0.20", "1.00"});
        this.checkMAF = new GroupCheckBox("By MAF");
        this.sliderMAF = new GroupSlider("Keep markers with minor allele frequencies HIGHER than", 1,
                new String[]{"0.00", "0.01", "0.03", "0.05", "0.10", "0.20"});
        this.add(this.checkMS, "cell 0 0, grow, align l");
        this.add(this.sliderMS, "cell 1 0, grow, align c");
        this.add(this.checkMAF, "cell 0 1, grow, align l");
        this.add(this.sliderMAF, "cell 1 1, grow, align c");
        // Add action listener
        this.sliderMS.slider.setEnabled(false);
        this.sliderMAF.slider.setEnabled(false);
        this.checkMS.check.addActionListener(this);
        this.checkMAF.check.addActionListener(this);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obs = e.getSource();
        if (obs == this.checkMS.check) {
            this.sliderMS.slider.setEnabled(!this.sliderMS.slider.isEnabled());
            if (!this.sliderMS.slider.isEnabled())
                this.sliderMS.setStrValue("1.00");
            else
                this.sliderMS.setStrValue("0.05");
        } else if (obs == this.checkMAF.check) {
            this.sliderMAF.slider.setEnabled(!this.sliderMAF.slider.isEnabled());
            if (!this.sliderMAF.slider.isEnabled())
                this.sliderMAF.setStrValue("0.00");
            else
                this.sliderMAF.setStrValue("0.05");
        }
    }
}

class PanelWD extends JPanel{
    // GUI objects
    GroupValue project;
    GroupPath path;
    public PanelWD(String name) {
        super(new MigLayout("fill, ins 3", "[grow]", "[grow][grow]"));
        this.project = new GroupValue(7, "Module Name");
        this.project.setValue(name);
        this.path = new GroupPath("Output Directory");
        this.path.setPath(System.getProperty("user.home"));
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

class PanelBottom extends JPanel implements MouseListener, MouseMotionListener {
    // Structure
    PanelConfig config;
    // Mouse
    Enum_Analysis method;
    Enum_Tool toolTap;
    // iLabel
    ToolButton toolGAPIT;
    ToolButton toolFarmCPU;
    ToolButton toolPLINK;
    ToolButton toolgBLUP;
    ToolButton toolrrBLUP;
    ToolButton toolBGLR;
    ArrayList<ToolButton> arrayTool;
    // Navi
    JButton buttonLeft, buttonRight;

    public PanelBottom(Enum_Analysis method, FileLabel LabelC, boolean isFinal) {
        super(new MigLayout("ins 3, fill", "[grow][grow][grow]", "[grow][grow][grow][grow]"));
        // Panel Config
        this.method = method;
        // ILabel
        this.toolGAPIT   = new ToolButton("GAPIT", Enum_Tool.GAPIT, Enum_Analysis.GWAS);
        this.toolFarmCPU = new ToolButton("FarmCPU", Enum_Tool.FarmCPU, Enum_Analysis.GWAS);
        this.toolPLINK   = new ToolButton("PLINK", Enum_Tool.PLINK, Enum_Analysis.GWAS);
        this.toolgBLUP   = new ToolButton("gBLUP", Enum_Tool.gBLUP, Enum_Analysis.GS);
        this.toolrrBLUP = new ToolButton("rrBLUP", Enum_Tool.rrBLUP, Enum_Analysis.GS);
        this.toolBGLR   = new ToolButton("BGLR", Enum_Tool.BGLR, Enum_Analysis.GS);
        switch (method) {
            case GWAS:
                // GUI
                this.add(this.toolGAPIT, "cell 2 0, grow, w 150:150:");
                this.add(this.toolFarmCPU, "cell 2 1, grow, w 150:150:");
                this.add(this.toolPLINK, "cell 2 2, grow, w 150:150:");
                break;
            case GS: case GWASGS:
                // GUI
                this.add(this.toolgBLUP, "cell 2 0, grow, w 150:150:");
                this.add(this.toolrrBLUP, "cell 2 1, grow, w 150:150:");
                this.add(this.toolBGLR, "cell 2 2, grow, w 150:150:");
                break;
        }
        // Collect labels and add listener
        this.arrayTool = new ArrayList<>();
        this.arrayTool.add(this.toolGAPIT);
        this.arrayTool.add(this.toolFarmCPU);
        this.arrayTool.add(this.toolPLINK);
        this.arrayTool.add(this.toolgBLUP);
        this.arrayTool.add(this.toolrrBLUP);
        this.arrayTool.add(this.toolBGLR);
        for (ToolButton tool : this.arrayTool)
            tool.addMouseListener(new ToolAdapter(tool));
        // Panel Navigation
        if (isFinal && method == Enum_Analysis.GWAS) {
            this.buttonLeft = new JButton("<- Select Analysis");
            this.buttonRight = new JButton("Confirm and Run");
        } else if (method == Enum_Analysis.GS) {
            this.buttonLeft = new JButton("<- Select Analysis");
            this.buttonRight = new JButton("Confirm and Run");
        } else if (method == Enum_Analysis.GWASGS) {
            this.buttonLeft = new JButton("<- Define GWAS");
            this.buttonRight = new JButton("Confirm and Run");
        } else if (!isFinal) {
            this.buttonLeft = new JButton("<- Select Analysis");
            this.buttonRight = new JButton("Define GS ->");
        }
        this.add(this.buttonLeft, "cell 0 3 1 1, grow, align c");
        this.add(this.buttonRight, "cell 1 3 2 1, grow, align c");
        this.config = new PanelConfig(method, LabelC);
        this.toolTap = Enum_Tool.NA;
        this.add(this.config, "cell 0 0 2 3, grow, w 470:470:, h 270:270:");
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    void setBorder(String title) {
        this.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED, null, null),
                title,
                TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
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
        // If on the config panel (tap)
        if (this.config.getBounds().contains(pt) && this.config.isDeployed())
            this.config.showDeployedPane();
        // Refresh GUI
        this.config.setVisible(true);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        Point pt = e.getPoint();
//        // If dragging a label
//        if (this.toolTap.isDeployed()) {
//            // and drop on the config panel
//            if (this.config.getBounds().contains(pt)) {                                       "Ready for being tapped"
//                this.config.setDeployedTool(this.toolTap);
//                this.config.changeFontSelected();
//                this.config.setTapped(false);
//                // or drop elsewhere and there's a method occupied (show detail), and has been tapped
//            } else if (this.config.isDeployed() && this.config.isTapped())                    "Show details"
//                this.config.showDeployedPane();
//                // or drop elsewhere and there's a method occupied (font select), and hasn't tapped yet
//            else if (this.config.isDeployed() && !this.config.isTapped())                     "Ready for being tapped"
//                this.config.changeFontSelected();
//                // or drop elsewhere and there's no method occupied (font drag)               "Default"
//            else if (!this.config.isDeployed())
//                this.config.changeFontDrag();
//        }
//        this.ptTemp.setLocation(-1, -1);
//        this.toolTap = Enum_Tool.NA;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private class ToolAdapter extends MouseAdapter {
        ToolButton tool;

        public ToolAdapter(ToolButton tool) {
            this.tool = tool;
        }

        public void mousePressed (MouseEvent evt) {
            // Panel
            toolTap = this.tool.getTool();
            config.setDeployedTool(toolTap);
            config.changeFontSelected();
            config.setTapped(false);
            // Button
            this.tool.select();
            for (ToolButton tool : arrayTool) {
                if (!this.tool.isEqual(tool))
                    tool.origin();
            }
        }

        public void mouseEntered (MouseEvent evt) {
            if (!this.tool.isSelected)
                this.tool.hover();
        }

        public void mouseExited (MouseEvent evt) {
            if (!this.tool.isSelected)
                this.tool.origin();
        }
    }

    private class ToolButton extends JButton {
        boolean isSelected = false;
        Enum_Tool tool;
        Enum_Analysis analysis;

        public ToolButton (String name, Enum_Tool tool, Enum_Analysis analysis) {
            super(name);
            this.tool = tool;
            this.analysis = analysis;
            super.setHorizontalAlignment(SwingConstants.CENTER);
            super.setFont(iPat.TXTLIB.bold);
//            this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            this.setOpaque(true);
            this.origin();
        }

        Enum_Tool getTool() {
            return this.tool;
        }

        Enum_Analysis getAnalysis() {
            return this.analysis;
        }

        void select () {
            this.isSelected = true;
            this.setBackground(iPat.IMGLIB.colorHintDrag);
        }

        void origin () {
            this.isSelected = false;
            this.setBackground(iPat.IMGLIB.colorHintDrop);
        }

        void hover () {
            this.setBackground(iPat.IMGLIB.ovalcolor);
        }

        boolean isEqual (ToolButton tool) {
            return this.tool.getName().equals(tool.tool.getName());
        }

    }

    private class PanelConfig extends JPanel {
        JLabel msg;
        boolean isDeployed;
        Enum_Tool toolDeployed;
        // tabbed pane
        PanelTool paneDeploy;
        FileLabel LabelC;
        // text
        boolean isTapped;
        // method
        Enum_Analysis method;

        PanelConfig(Enum_Analysis method, FileLabel LabelC) {
            this.msg = new JLabel("", SwingConstants.CENTER);
            this.msg.setFont(iPat.TXTLIB.plainBig);
            this.isTapped = false;
            this.method = method;
            this.isDeployed = false;
            this.toolDeployed = Enum_Tool.NA;
            this.changeFontDrag();
            this.LabelC = LabelC;
            // GUI
            this.setOpaque(true);
            this.setLayout(new MigLayout("ins 0", "[grow]", "[grow]"));
            this.add(this.msg, "grow");
        }

        void showDeployedPane() {
            this.removeAll();
            this.revalidate();
            this.repaint();
            System.out.println("remove!");
            if (!this.isTapped()) {
                switch (this.toolDeployed) {
                    case GAPIT: this.paneDeploy = new PanelGAPIT(this.LabelC.getFile());
                        break;
                    case FarmCPU: this.paneDeploy = new PanelFarmCPU(this.LabelC.getFile());
                        break;
                    case PLINK: this.paneDeploy = new PanelPlink(this.LabelC.getFile());
                        break;
                    case gBLUP: this.paneDeploy = new PanelgBLUP(this.LabelC.getFile(), this.method.equals(Enum_Analysis.GWASGS));
                        break;
                    case rrBLUP: this.paneDeploy = new PanelrrBLUP(this.LabelC.getFile(), this.method.equals(Enum_Analysis.GWASGS));
                        break;
                    case BGLR: this.paneDeploy = new PanelBGLR(this.LabelC.getFile(), this.method.equals(Enum_Analysis.GWASGS));
                        break;
                }
                this.isTapped = true;
            }
            this.setLayout(new MigLayout("ins 0", "[grow]", "[grow]"));
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
//            return this.paneDeploy.getCovs();
            return "na";
        }

        IPatCommand getCommand() {
            IPatCommand command = new IPatCommand();
//            if (this.method == Enum_Analysis.GS)
//                command.addAll(this.paneCov.getGWAS());
            if (this.isTapped()) {
                command.addAll(this.paneDeploy.getCommand());
                return command;
                // return default command
            } else {
                HashMap<String, String> map;
                switch (this.toolDeployed) {
                    case GAPIT:
                        map = iPat.MODVAL.mapGAPIT;
                        command.add(iPat.REXC);
                        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
                        command.add("GAPIT");
                        command.add("-arg");
                        command.add(map.get("model"));
                        command.add(map.get("pc"));
                        command.add("-cSelect");
                        command.add("NA");
                        return command;
                    case FarmCPU:
                        map = iPat.MODVAL.mapFarmCPU;
                        command.add(iPat.REXC);
                        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
                        command.add("FarmCPU");
                        command.add("-arg");
                        command.add(map.get("bin"));
                        command.add(map.get("loop"));
                        command.add("-cSelect");
                        command.add("NA");
                        return command;
                    case PLINK:
                        map = iPat.MODVAL.mapPLINK;
                        command.add(iPat.REXC);
                        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
                        command.add("PLINK");
                        command.add("-arg");
                        command.add(map.get("ci"));
                        command.add(map.get("model"));
                        command.add(iPat.FILELIB.getAbsolutePath("plink"));
                        command.add("-cSelect");
                        command.add("NA");
                        return command;
                    case gBLUP:
                        map = iPat.MODVAL.mapgBLUP;
                        command.add(iPat.REXC);
                        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
                        command.add("gBLUP");
                        command.add("-gs");
                        command.add(map.get("valid"));
                        command.add(map.get("fold"));
                        command.add(map.get("iter"));
                        command.add("-gwas");
                        command.add(map.get("isGWAS"));
                        command.add("-cSelect");
                        command.add("NA");
                        return command;
                    case rrBLUP:
                        map = iPat.MODVAL.maprrBLUP;
                        command.add(iPat.REXC);
                        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
                        command.add("rrBLUP");
                        command.add("-gs");
                        command.add(map.get("valid"));
                        command.add(map.get("fold"));
                        command.add(map.get("iter"));
                        command.add("-gwas");
                        command.add(map.get("isGWAS"));
                        command.add("-cSelect");
                        command.add("NA");
                        return command;
                    case BGLR:
                        map = iPat.MODVAL.mapBGLR;
                        command.add(iPat.REXC);
                        command.add(iPat.FILELIB.getAbsolutePath("launcher"));
                        command.add("BGLR");
                        command.add("-arg");
                        command.add(map.get("model"));
                        command.add(map.get("niter"));
                        command.add(map.get("burn"));
                        command.add("-gs");
                        command.add(map.get("valid"));
                        command.add(map.get("fold"));
                        command.add(map.get("iter"));
                        command.add("-gwas");
                        command.add(map.get("isGWAS"));
                        command.add("-cSelect");
                        command.add("NA");
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
            this.setLayout(new MigLayout("ins 0", "[grow]", "[grow]"));
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

class PanelCov extends JPanel implements ActionListener {
    Enum_Analysis method;
    // Structure
    JPanel paneMain;
    SelectPanel panel;
    JScrollPane scroll;
    // Blank panel;
    JLabel msgNA;
    // cov
    ArrayList<String> covNames;
    // GWAS_Assisted
    GroupSlider slideCutoff;
    GroupCheckBox checkGWAS;
    // file is Empty
    boolean isEmpty = false;

    public PanelCov (IPatFile cFile) {


//            this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"Selected", "Excluded"}, selectC);

        // Assemble : if is GS
//        if (method == Enum_Analysis.GS) {
//            JPanel paneSub = new JPanel(new MigLayout("fillx", "[grow]", "[grow][grow][grow]"));
//            // Initialize components
//            this.checkGWAS = new GroupCheckBox("Includes seleted SNPs from GWAS");
//            this.checkGWAS.setCheck(false);
//            this.checkGWAS.check.addActionListener(this);
//            this.slideCutoff = new GroupSlider("<html>Bonferroni Cutoff<br>(Negative Power of 10)</html>", 3, new String[]{"0.001", "0.0001", "0.00001", "0.000001", "0.0000001", "0.00000001", "0.000000001", "0.0000000001"}, new String[]{"3", "4", "5", "6", "7", "8", "9", "10"});
//            this.slideCutoff.slider.setEnabled(false);
//            // Assemble
//            paneSub.add(this.checkGWAS, "grow, cell 0 0, align c");
//            paneSub.add(this.slideCutoff, "grow, cell 0 1, align c");
//            paneSub.add(this.panel, "grow, cell 0 2, align c");
//            this.scroll = new JScrollPane(paneSub, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//            // Assemble : if is GWAS
//        } else {
//            // Assemble
//            this.scroll = new JScrollPane(this.panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        }
//        this.paneMain = new JPanel(new MigLayout("fillx", "[grow]", "[grow]"));
//        this.paneMain.add(this.scroll, "grow");
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
//        this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"FIXED", "BRR", "BayesA", "BL", "BayesB", "BayesC", "OMIT IT"}, this.selectC);
    }

    void setAsRegular() {
//        this.panel = new SelectPanel(this.covNames.toArray(new String[0]), new String[]{"Selected", "Excluded"}, this.selectC);
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

class SelectPanel extends JPanel implements ActionListener {
    private IPatFile file;
    private JLabel labelSrc;
    private JList listSrc;
    private SortedListModel modelSrc;

    private JLabel labelDes;
    private JList listDes;
    private SortedListModel modelDes;

    private JButton buttonAdd;
    private JButton buttonRm;
    private JButton buttonAddAll;
    private JButton buttonRmAll;

    public SelectPanel (IPatFile file, String messageEmpty, int skipCol) {
        this.file = file;
        //============================================= Layout ===========================================
        if (file.isEmpty()) {
            this.setLayout(new MigLayout("", "[grow]", "[grow]"));
            JLabel msgNA = new JLabel("<html><center>" + messageEmpty + "</center></html>", SwingConstants.CENTER);
            msgNA.setFont(iPat.TXTLIB.plain);
            this.add(msgNA, "grow");
        } else {
            this.setLayout(new MigLayout("fill, ins 3", "[150!][grow][150!]", "[30!][grow][grow][grow][grow]"));
            // Source
            this.labelSrc = new JLabel("Analyzed");
            this.labelSrc.setHorizontalAlignment(SwingConstants.CENTER);
            this.modelSrc = new SortedListModel();
            this.listSrc = new JList(this.modelSrc);
            this.add(this.labelSrc, "cell 0 0 1 1, grow");
            this.add(new JScrollPane(this.listSrc), "cell 0 1 1 4, grow");
            // Button
            this.buttonRm = new JButton("--->");
            this.add(buttonRm, "cell 1 1 1 1, grow");
            this.buttonRm.addActionListener(this);
            this.buttonAdd = new JButton("<---");
            this.add(buttonAdd, "cell 1 2 1 1, grow");
            this.buttonAdd.addActionListener(this);
            this.buttonRmAll = new JButton("Exclude All");
            this.add(buttonRmAll, "cell 1 3 1 1, grow");
            this.buttonRmAll.addActionListener(this);
            this.buttonAddAll = new JButton("Select All");
            this.add(buttonAddAll, "cell 1 4 1 1, grow");
            this.buttonAddAll.addActionListener(this);
            // Destination
            this.labelDes = new JLabel("Excluded");
            this.labelDes.setHorizontalAlignment(SwingConstants.CENTER);
            this.modelDes = new SortedListModel();
            this.listDes = new JList(this.modelDes);
            this.add(labelDes, "cell 2 0 1 1, grow");
            this.add(new JScrollPane(listDes), "cell 2 1 1 4, grow");
            this.loadFile(file, skipCol);
        }
    }

    public void loadFile(IPatFile file, int skipCol) {
        // Load File
        String tempStr = null;
        try {
            // Get the first line
            tempStr = file.getLines(1)[0];
        } catch (IOException e) {
            System.out.println("Can't find the file!");
            e.printStackTrace();
        }
        // Get separated items through IPatFile's function
        String[] covNames = new IPatFile().getSepStr(tempStr);
        if (skipCol != 0) {
            ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(covNames));
            for (int i = 0; i < skipCol; i ++)
                arrayList.remove(0);
            covNames = Arrays.stream(arrayList.toArray()).toArray(String[]::new);
        }
        this.addSrcItems(covNames);
    }

    public String getSelected(){
        if (!file.isEmpty()) {
            Object[] objs = this.modelSrc.getAllElements();
            String strSelect = "";
            for (int i = 0; i < objs.length; i ++)
                strSelect = strSelect.concat(objs[i].toString() + "sep");
            return strSelect;
        }
        return "NA";
    }

    public void addSrcItems (String items[]) {
        this.modelSrc.addAll(items);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == this.buttonRm) {
            Object[] selected = listSrc.getSelectedValues();
            this.modelDes.addAll(selected);
            this.clearSelected(this.modelSrc, this.listSrc);
        } else if (obj == this.buttonAdd) {
            Object[] selected = listDes.getSelectedValues();
            this.modelSrc.addAll(selected);
            this.clearSelected(this.modelDes, this.listDes);
        } else if (obj == this.buttonRmAll) {
            Object[] allItems = this.modelSrc.getAllElements();
            this.modelDes.addAll(allItems);
            this.modelSrc.clear();
        } else if (obj == this.buttonAddAll) {
            Object[] allItems = this.modelDes.getAllElements();
            this.modelSrc.addAll(allItems);
            this.modelDes.clear();
        }
    }

    private void clearSelected(SortedListModel model, JList list) {
        Object selected[] = list.getSelectedValues();
        for (int i = selected.length - 1; i >= 0; --i)
            model.removeElement(selected[i]);
        list.getSelectionModel().clearSelection();
    }
}

class SortedListModel extends AbstractListModel {
    SortedSet model;
    public SortedListModel() {
        model = new TreeSet();
    }
    public int getSize() {
        return model.size();
    }
    public Object[] getAllElements() {
        return model.toArray();
    }
    public Object getElementAt(int index) {
        return model.toArray()[index];
    }
    public void add(Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }
    public void addAll(Object elements[]) {
        Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }
    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }
    public boolean contains(Object element) {
        return model.contains(element);
    }
    public boolean removeElement (Object element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}



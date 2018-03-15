import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


public class ModuleConfig extends JFrame implements ActionListener {
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

    public ModuleConfig(MethodType method, ToolType tool, FileFormat format, iFile fileP, iFile fileC) {
        // initialize objects
        this.paneWD = new PanelWD();
        this.panePhenotype = new PanelPhenotype(fileP);
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
            case GS:
                this.paneTop.setBorder(new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED, null, null),
                        "GS (Format: " + format + ")",
                        TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED)
                );
            case BSA:
                this.paneTop.setBorder(new TitledBorder(
                        new EtchedBorder(EtchedBorder.LOWERED, null, null),
                        "Bulk Segregation Analysis",
                        TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED)
                );
        }
        // Assemble
        this.paneTop.addTab("Working Directory", this.paneWD);
        this.paneTop.addTab("Phenotype", this.panePhenotype.getPane());
        this.paneTop.addTab("Quality Control", this.paneQC);
        this.paneBottom = new PanelBottom(method, tool, fileC);
        // Layout
        this.paneMain = new JPanel(new MigLayout("fillx", "[]", "[grow][grow]"));
        this.paneMain.add(this.paneTop, "cell 0 0, grow");
        this.paneMain.add(this.paneBottom.getPanel(), "cell 0 1, grow");
        // JFrame
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

    Command getCommand() {
        // If no command out there
        if (!this.isDeployed())
            return null;
        // Instantiate command object
        Command newCommand = new Command();
        // Common command
        newCommand.addWD(this.paneWD.getPath());
        newCommand.addProject(this.paneWD.getProject());
        newCommand.addArg("-phenotype", this.panePhenotype.getSelected());
        newCommand.addArg("-maf", this.paneQC.getMAF());
        newCommand.addArg("-ms", this.paneQC.getMS());
        // Specific command
        newCommand.addAll(this.paneBottom.getCommand());
        // Return
        return newCommand;
    }

    ToolType getTool() {
        return this.paneBottom.getTool();
    }

    boolean isDeployed() {
        return this.paneBottom.isDeployed();
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

        public PanelPhenotype (iFile file) {
            this.panelNA = new JPanel(new MigLayout("", "[grow]", "[grow]"));
            this.msgNA = new JLabel("<html><center> Phenotype Not Found </center></html>", SwingConstants.CENTER);
            this.msgNA.setFont(iPat.TXTLIB.plain);
            this.panelNA.add(this.msgNA, "grow");
            this.scroll = new JScrollPane(this.panelNA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.file = file;
        }

        String[] getTraits (FileFormat format) {
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
            return this.traitNames.toArray(new String[0]);
        }

        String getSelected() {
            return this.panel.getSelected();
        }

        JScrollPane getPane() {
            return this.scroll;
        }
    }

    class PanelQC extends JPanel{
        // GUI objects
        GroupSlider ms;
        GroupSlider maf;

        public PanelQC() {
            super(new MigLayout("fillx", "[grow]", "[]"));
            this.ms = new GroupSlider("By missing rate", 0, 0.5, 0.2, new String[]{"0", "0.05", "0.1", "0.2", "0.5"});
            this.maf = new GroupSlider("By MAF", 0, 0.5, 0.05, new String[]{"0", "0.05", "0.1", "0.2", "0.5"});
            this.add(ms, "cell 0 0");
            this.add(maf, "cell 0 1");
        }

        String getMS() {
            return Double.toString(this.ms.getIntValue() / (double)1000);
        }

        String getMAF() {
            return Double.toString(this.maf.getIntValue() / (double)1000);
        }
    }

    class PanelWD extends JPanel{
        // GUI objects
        GroupValue project;
        GroupPath path;
        JLabel format;

        public PanelWD() {
            super(new MigLayout("fill", "[grow]", "[grow][grow]"));
            this.project = new GroupValue(3, "Project Name");
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
        Point ptMouse;
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
                case BSA:
                    this.panel = new JPanel(new MigLayout("fill", "[grow]", "[grow]"));
            }
            this.config = new PanelConfig(method, tool, fileC);
            this.toolDrag = ToolType.NA;
            this.panel.add(this.config, "cell 0 0 1 3, grow, w 470:470:, h 270:270:");
        }

        JPanel getPanel() {
            return this.panel;
        }

        ToolType getTool() {
            return this.config.getTool();
        }

        ArrayList<String> getCommand() {
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
            this.ptMouse = pt;
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
            if (this.config.getBounds().contains(pt))
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
                this.labelTemp.setLocation(this.ptMouse.x - pt.x + this.ptTemp.x,
                        this.ptMouse.y - pt.y + this.ptTemp.y);
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
                // or drop elsewhere and there's a method occupied (show detail), and has been tapped
                } else if (this.config.isTapped())
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
                switch(this.toolDeployed) {
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
                this.setLayout(new MigLayout("", "[grow]", "[grow]"));
                this.add(this.paneDeploy);
                this.isTapped = true;
            }

            boolean isTapped() {
                return this.isTapped;
            }

            boolean isDeployed() {
                return this.isDeployed;
            }

            ArrayList<String> getCommand() {
                return this.paneDeploy.getCommand();
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
                this.setBackground(iPat.IMGLIB.colorHintDrop);
                this.msg.setText("<html><center> Drop <br> to <br> Deploy </center></html>");
            }

            void changeFontDrag() {
                this.setBackground(iPat.IMGLIB.colorHintDrag);
                this.msg.setText("<html><center> Drag a Package <br> Here  </center></html>");
            }

            void changeFontSelected() {
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

        public PanelCov (MethodType method, iFile file) {
            // If no covariate file
            if (file.isEmpty()) {
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
                this.slideCutoff = new GroupSlider("Bonferroni cutoff (Power of 10)", -2, -10, -3, 1, 3);
                this.slideCutoff.setEnabled(false);
                // Assemble
                this.paneMain = new JPanel(new MigLayout("fillx", "[grow]", "[grow][grow][grow]"));
                this.paneMain.add(this.checkGWAS, "grow, cell 0 0");
                this.paneMain.add(this.slideCutoff, "grow, cell 0 1");
                this.paneMain.add(this.scroll, "grow cell 0 2");
            // Assemble : if is GWAS
            } else {
                // Assemble
                this.paneMain = new JPanel(new MigLayout("fillx", "[grow]", "[grow]"));
                this.paneMain.add(this.scroll, "grow cell 0 0");
            }
        }

        String[] getCovs () {
            return this.covNames.toArray(new String[0]);
        }

        String getSelected () {
            return this.panel.getSelected();
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

        @Override
        public void actionPerformed(ActionEvent e) {
            Object obs = e.getSource();
            if (obs == checkGWAS)
                slideCutoff.setEnabled(!slideCutoff.isEnabled());
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
                this.add(combo[i]);
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

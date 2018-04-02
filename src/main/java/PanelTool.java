import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;

abstract class PanelTool extends JTabbedPane {
    public PanelTool() {
        super();
    }
    abstract ArrayList<String> getCommand();
}

class PanelGAPIT extends PanelTool {
    JPanel basic;
    JPanel adv;
    GroupCombo comboKAlgr;
    GroupCombo comboKCluster;
    GroupCombo comboKGrp;
    GroupCombo comboModel;
    GroupSlider slideSNPfrac;
    GroupSlider slidefilefrag;
    JCheckBox checkModelSelect;

    public PanelGAPIT(ModuleConfig.PanelCov cov) {
        // Basic features
        basic = new JPanel(new MigLayout("fillx", "[]", "[grow][grow][grow]"));
        comboKAlgr = new GroupCombo("kinship.algorithm",
                new String[]{"VanRaden", "Loiselle", "EMMA"});
        comboKCluster = new GroupCombo("kinship.cluster",
                new String[]{"average", "complete", "ward", "single", "mcquitty", "median", "centroid"});
        comboKGrp = new GroupCombo("kinship.group",
                new String[]{"Mean", "Max", "Min", "Median"});
        comboModel = new GroupCombo("Select a model",
                new String[]{"GLM", "MLM", "CMLM"});
        basic.add(comboModel, "cell 0 0, align c");
        basic.add(comboKCluster, "cell 0 1, align c");
        basic.add(comboKGrp, "cell 0 2, align c");
        // Adv features
        adv = new JPanel(new MigLayout("fillx", "[]", "[grow][grow]"));
        slideSNPfrac = new GroupSlider("SNP.fraction", 5, new String[]{"0.2", "0.4", "0.6", "0.8", "1"});
        slidefilefrag = new GroupSlider("File fragment", 1, 512, 1, 64, 128);
        checkModelSelect = new JCheckBox("Model selection");
        adv.add(slideSNPfrac, "cell 0 0, align c");
        adv.add(checkModelSelect, "cell 0 1 2 1, align c");
        // cov
        if (!cov.isEmpty())
            cov.setAsRegular();
        // Build tab pane
        this.addTab("Basic Input", basic);
        this.addTab("Advance Input", adv);
        this.addTab("Covariates", cov.getPane());
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add("-arg");
        command.add(comboModel.getValue());
        command.add(comboKCluster.getValue());
        command.add(comboKGrp.getValue());
        command.add(slideSNPfrac.getStrValue());
        command.add(checkModelSelect.isSelected() ? "TRUE" : "FALSE");
        return command;
    }
}

class PanelFarmCPU extends PanelTool {
    JPanel basic;
    GroupCombo comboBin;
    GroupSlider slideLoop;

    public PanelFarmCPU(ModuleConfig.PanelCov cov) {
        // Basic features
        basic = new JPanel(new MigLayout("fillx"));
        comboBin = new GroupCombo("Method bin",
                new String[]{"static", "optimum"});
        slideLoop = new GroupSlider("maxLoop", 10, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        basic.add(comboBin, "cell 0 0");
        basic.add(slideLoop, "cell 0 1");
        // cov
        if (!cov.isEmpty())
            cov.setAsRegular();
        // Build tab pane
        this.addTab("FarmCPU input", basic);
        this.addTab("Covariates", cov.getPane());
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add("-arg");
        command.add(comboBin.getValue());
        command.add(slideLoop.getStrValue());
        return command;
    }
}

class PanelPlink extends PanelTool {
    JPanel basic;
    GroupSlider slideCI;
    GroupCombo comboModel;

    public PanelPlink(ModuleConfig.PanelCov cov) {
        // Basic features
        basic = new JPanel(new MigLayout("fillx"));
        slideCI = new GroupSlider("C.I.", 3, new String[]{"0.5", "0.68", "0.95"});
        comboModel = new GroupCombo("Method",
                new String[]{"GLM", "Logistic Regression"});
        basic.add(slideCI, "cell 0 0");
        basic.add(comboModel, "cell 0 1");
        // cov
        if (!cov.isEmpty())
            cov.setAsRegular();
        // Build tab pane
        this.addTab("PLINK input", basic);
        this.addTab("Covariates", cov.getPane());
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add("-arg");
        command.add(slideCI.getStrValue());
        command.add(comboModel.getValue());
        return command;
    }
}

class PanelgBLUP extends PanelTool {
    JPanel adv;
    GroupSlider slideSNPfrac;
    JCheckBox checkModelSelect;

    public PanelgBLUP(ModuleConfig.PanelCov cov) {
        // Adv features
        adv = new JPanel(new MigLayout("fillx"));
        slideSNPfrac = new GroupSlider("SNP.fraction", 5, new String[]{"0.2", "0.4", "0.6", "0.8", "1"});
        checkModelSelect = new JCheckBox("Model selection");
        adv.add(slideSNPfrac, "cell 0 0, align c");
        adv.add(checkModelSelect, "cell 0 1 2 1, align c");
        // cov
        if (!cov.isEmpty())
            cov.setAsRegular();
        // Build tab pane
        this.addTab("GAPIT Input", adv);
        this.addTab("Covariates", cov.getPane());
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add("-arg");
        command.add(slideSNPfrac.getStrValue());
        command.add(checkModelSelect.isSelected() ? "TRUE" : "FALSE");
        return command;
    }
}

class PanelrrBLUP extends PanelTool {
    JPanel basic;
    GroupCombo comboImpute;
    JCheckBox checkShrink;

    public PanelrrBLUP(ModuleConfig.PanelCov cov) {
        // Basic features
        basic = new JPanel(new MigLayout("fillx", "[]", "[grow]"));
        comboImpute = new GroupCombo("impute.method", new String[]{"mean", "EM"});
        checkShrink = new JCheckBox("Shrinkage estimation");
        basic.add(comboImpute, "cell 0 0, align c");
        basic.add(checkShrink, "cell 0 1 2 1, align c");
        // cov
        if (!cov.isEmpty())
            cov.setAsRegular();
        // Build tab pane
        this.addTab("rrBLUP Input", basic);
        this.addTab("Covariates", cov.getPane());
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add("-arg");
        command.add(comboImpute.getValue());
        command.add(checkShrink.isSelected() ? "True" : "False");
        return command;
    }
}

class PanelBGLR extends PanelTool  {
    JScrollPane scroll;
    JPanel basic;
    GroupCombo comboModel;
    GroupCombo comboResponse;
    GroupSlider slideNIter;
    GroupSlider slideBurnIn;
    GroupSlider slideThin;

    public PanelBGLR(ModuleConfig.PanelCov cov) {
        // Basic features
        basic = new JPanel(new MigLayout("fillx", "[grow]", "[grow][grow][grow][grow][grow]"));
        comboModel = new GroupCombo("Model of the Predictor (markers)",
                new String[]{"BRR", "BayesA", "BL", "BayesB", "BayesC", "FIXED"});
        comboResponse = new GroupCombo("response_type",
                new String[]{"gaussian", "ordinal"});
        slideNIter = new GroupSlider("nIter", 2, new String[]{"1000", "5000", "10000", "30000", "50000", "100000"}, new String[]{"1K", "5K", "10K", "30k", "50k", "100k"});
        slideBurnIn = new GroupSlider("burnIn", 2, new String[]{"200", "500", "1000", "3000", "5000", "10000"});
        slideThin = new GroupSlider("thin", 5, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        basic.add(comboModel, "cell 0 0, grow");
        basic.add(comboResponse, "cell 0 1, grow");
        basic.add(slideNIter, "cell 0 2, grow");
        basic.add(slideBurnIn, "cell 0 3, grow");
        basic.add(slideThin, "cell 0 4, grow");
        // cov
        if (!cov.isEmpty())
            cov.setAsBayes();
        // scroll
        scroll = new JScrollPane(this.basic, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Build tab pane
        this.addTab("BGLR Input", scroll);
        this.addTab("Covariates", cov.getPane());
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add("-arg");
        command.add(comboModel.getValue());
        command.add(comboResponse.getValue());
        command.add(slideBurnIn.getStrValue());
        command.add(slideBurnIn.getStrValue());
        command.add(slideThin.getStrValue());
        return command;
    }
}

class PanelBSA extends PanelTool {
    JPanel basic;
    GroupSlider slideWindow;
    GroupSlider slidePower;

    public PanelBSA() {
        // Basic features
        basic = new JPanel(new MigLayout("fillx", "[]", "[grow][grow]"));
        slideWindow = new GroupSlider("Window size", 10000, 100000, 20000, 5000, 20000);
        slidePower = new GroupSlider("Power of ED", 1, 5, 4, 1, 5);
        basic.add(slideWindow, "cell 0 0");
        basic.add(slidePower, "cell 0 1");
        // Build tab pane
        this.addTab("BSA Input", basic);
    }

    @Override
    ArrayList<String> getCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add("-arg");
        command.add(slideWindow.getStrValue());
        command.add(slidePower.getStrValue());
        return command;
    }
}
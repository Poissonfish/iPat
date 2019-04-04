import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

class Obj_Module extends Obj_Super implements ActionListener{
    // Global
    static int countMO = 0;
    // Module define
    int indexMO;
    // Phenotype
    ArrayList<String> traitNames;
    String selectP;
    // IPatCommand
    IPatCommand commandGWAS;
    IPatCommand commandGS;
    IPatCommand commandBSA;
    Enum_Tool toolGWAS;
    Enum_Tool toolGS;
    boolean callBSA;
    Enum_FileFormat format;
    // Durning running (GUI)
    boolean rotateSwitch;
    boolean rotatePermit;
    // multi run
    iPatThread thread;
    Timer timerThread;
    // QC
    double maf, ms;
    // File Cluster
    Obj_FileCluster files;

    public Obj_Module(int x, int y) throws IOException {
        super(x, y);
        // Define module
        this.isFile = false;
        this.isModule = true;
        this.isContainMO = true;
        this.indexMO = countMO++;
        this.setName("Module_" + (this.indexMO + 1));
        setIcon("module");
        // Define command
        this.format  = Enum_FileFormat.NA;
        this.commandGWAS = new IPatCommand();
        this.commandGS = new IPatCommand();
        this.commandBSA = new IPatCommand();
        this.toolGWAS = Enum_Tool.NA;
        this.toolGS = Enum_Tool.NA;
        this.callBSA = false;
        // Panel
        this.traitNames = new ArrayList<>();
        // During running (GUI)
        this.rotateSwitch = false;
        this.rotatePermit = false;
        // multirun
        this.timerThread = new Timer(500, this);
        // phenotype
        this.selectP = null;
        // file cluster
        files = new Obj_FileCluster();
    }

    // Files Cluster
    IPatFile getFile(Enum_FileType filetype) {
        return this.files.getFile(filetype);
    }
    void setFile(String filepath, Enum_FileType filetype) {
        this.files.setFile(new IPatFile(filepath), filetype);
    }

    // get
    Enum_Tool getDeployedGWASTool () {
        return this.toolGWAS;
    }
    Enum_Tool getDeployedGSTool () {
        return this.toolGS;
    }
    Enum_FileFormat getFormat() {
        return this.format;
    }
    IPatCommand getCommandGWAS() {
        return this.commandGWAS;
    }
    IPatCommand getCommandGS() {
        return this.commandGS;
    }
    IPatCommand getCommandBSA() {
        return this.commandBSA;
    }
    String getPhenotype() {
        return this.selectP;
    }
    String getCovGWAS() {
        return this.commandGWAS.getCov();
    }
    String getCovGS() {
        return this.commandGS.getCov();
    }
    double getMAF() {
        return this.maf;
    }
    double getMS() {
        return this.ms;
    }

    // set
    void setDeployedGWASTool (Enum_Tool tool) {
        this.toolGWAS = tool;
    }
    void setDeployedGSTool (Enum_Tool tool) {
        this.toolGS = tool;
    }
    void setBSA (boolean isBSA) {
        this.callBSA = isBSA;
    }
    void setFormat (Enum_FileFormat format) {
        this.format = format;
    }
    void setCommandGWAS(IPatCommand commandGWAS) {
        this.commandGWAS = commandGWAS;
    }
    void setCommandGS(IPatCommand commandGS) {
        this.commandGS = commandGS;
    }
    void setCommandBSA(IPatCommand commandBSA) {
        this.commandBSA = commandBSA;
    }
    void setPhenotype(String selectP) {
        this.selectP = selectP;
    }
    void setMAF(double maf) {
        this.maf = maf;
    }
    void setMS(double ms) {
        this.ms = ms;
    }

    boolean isGWASDeployed() {
        return !this.commandGWAS.isEmpty();
    }
    boolean isGSDeployed() {
        return !this.commandGS.isEmpty();
    }
    boolean isBSADeployed() {
        return !this.commandBSA.isEmpty();
    }
    boolean isDeployed() {
        return this.isGWASDeployed() || this.isGSDeployed() || this.isBSADeployed();
    }
    boolean isNAformat () {
        return this.format.isNA();
    }

    void run (ArrayList<IPatCommand> command, boolean isPLINK) throws IOException {
        Enum_FileFormat format = getFormat(this.files);
        this.timerThread.start();
        this.rotateSwitch = true;
        thread = new iPatThread(this.getName(), isPLINK, format,
                this.files.getFile(Enum_FileType.Genotype).getPath(), this.files.getFile(Enum_FileType.Map).getPath(),
                this.maf, this.ms, true, 64);
        thread.setCommandAndRun(command);
    }

    private int diffValues(String[] line2nd) {
        int numOfDifferentVals = 0;
        ArrayList<String> diffNum = new ArrayList<>();
        // if diffNum not contain the element from array, add it
        for (String strTemp : line2nd)
            if(!diffNum.contains(strTemp))
                diffNum.add(strTemp);
        // if only one kind of element, return 0, otherwise, its size
        numOfDifferentVals = diffNum.size() == 1 ? 0 : diffNum.size();
        return numOfDifferentVals;
    }
    private Enum_FileFormat getFormat(Obj_FileCluster files) throws IOException {
        IPatFile fileGD = files.getFile(Enum_FileType.Genotype);
        IPatFile fileP = files.getFile(Enum_FileType.Phenotype);
        String[] lines = fileGD.getLines(2);
        int countRowP = fileP.getLineCount();
        String[] line2nd = fileGD.getSepStr(lines[1]);
        int countColGD = line2nd.length;
        if (countColGD - lines[1].split("/").length == 8 &&
                lines[1].split("/").length > 1)
            return Enum_FileFormat.VCF;
        else if (countColGD - countRowP == 11 || countColGD - countRowP == 10)
            return Enum_FileFormat.Hapmap;
        else if (Arrays.asList(line2nd).containsAll(Arrays.asList("0", "1", "2")) &&
                diffValues(line2nd) < 5)
            return Enum_FileFormat.Numeric;
        else if (fileGD.getPath().toUpperCase().endsWith("PED"))
            return Enum_FileFormat.PLINK;
        return Enum_FileFormat.NA;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!thread.isRunning()) {
            this.rotateSwitch = false;
            timerThread.stop();
        }
    }

    public void showProgress(MouseEvent e) {
        if (this.thread != null) {
            this.thread.areaFrame.setLocation(e.getX() + iPat.IPATFRAME.getX() - 250,
                                                e.getY() + iPat.IPATFRAME.getY() - 352);
            this.thread.areaFrame.setVisible(true);
            this.thread.areaFrame.show();
        }
    }

    public void hideProgress() {
        if (this.thread != null)
            this.thread.areaFrame.show(false);
    }

    class iPatThread extends Thread {
        Runtime runtime;
        Process process;
        BufferedReader streamInput, streamError;
        String tempString;
        PrintWriter writerInput, writerError;
        boolean success;
        JTextArea areaText;
        JScrollPane areaScroll;
        JFrame areaFrame;
        ArrayList<IPatCommand> commands;
        // converter
        String pathGD, pathGM;
        boolean isPLINK;
        Enum_FileFormat format;
        double maf, ms;
        int batchSize;
        boolean nafill;
        // running
        boolean isRunning;

        public iPatThread(String title, boolean isPLINK, Enum_FileFormat format,
                          String pathGD, String pathGM,
                          double rateMAF, double rateNA,
                          boolean isNAFill, int batchSize) {
            this.areaText = new JTextArea();
            this.areaText.setEditable(false);
            this.areaScroll = new JScrollPane(areaText,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.areaFrame = new JFrame();
            this.areaFrame.setTitle(title);
            this.areaFrame.setContentPane(areaScroll);
            int width = 500;
            int height = 350;
            this.areaFrame.setSize(width, height);
            this.areaFrame.setLocation(iPat.WINDOWSIZE.getAppLocation(width, height));
//            this.areaFrame.setVisible(true);
//            this.areaFrame.show(false);
            this.areaFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {
                    success = false;
                    isRunning = false;
                    process.destroy();
                }
                @Override
                public void windowClosed(WindowEvent e) {}
                @Override
                public void windowIconified(WindowEvent e) {}
                @Override
                public void windowDeiconified(WindowEvent e) {}
                @Override
                public void windowActivated(WindowEvent e) {}
                @Override
                public void windowDeactivated(WindowEvent e) {}
            });
            // During running (BG)
            this.success = true;
            // converter
            this.pathGD = pathGD;
            this.pathGM = pathGM;
            this.isPLINK = isPLINK;
            this.format = format;
            this.maf = rateMAF;
            this.ms = rateNA;
            this.batchSize = batchSize;
            this.nafill = isNAFill;
            // running
            this.isRunning = false;
        }

        void setCommandAndRun(ArrayList<IPatCommand> command) {
            this.commands = command;
            this.start();
        }

        boolean isRunning() {
            return this.isRunning;
        }

        @Override
        public void run() {
            this.isRunning = true;
            String filename;
            for (IPatCommand command : commands) {
                if (command.isEmpty())
                    continue;
                else {
                    // format convertsion
                    switch (command.getType()) {
                        case GWAS:
                            try {
                                new Cpu_Converter(this.format,
                                        this.isPLINK ? Enum_FileFormat.PLINK : Enum_FileFormat.Numeric,
                                        this.pathGD, this.pathGM, this.maf, this.ms, this.nafill, this.batchSize, false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // rename
                            filename = pathGD.replaceFirst("[.][^.]+$", "");
                            if (isPLINK && format != Enum_FileFormat.PLINK) {
                                pathGD = filename + "_recode.ped";
                                pathGM = filename + "_recode.map";
                                format = Enum_FileFormat.PLINK;
                            } else if (!isPLINK) {
                                pathGD = filename + "_recode.dat";
                                pathGM = filename + "_recode.nmap";
                                format = Enum_FileFormat.Numeric;
                            } else if (isPLINK && format == Enum_FileFormat.PLINK) {
                                command.addArg("-maf", Double.toString(this.maf));
                                command.addArg("-ms", Double.toString(this.ms));
                            }
                            command.addArg("-genotype", pathGD);
                            command.addArg("-map", pathGM);
                            break;
                        case GS:
                            try {
                                new Cpu_Converter(this.format, Enum_FileFormat.Numeric,
                                        this.pathGD, this.pathGM, this.maf, this.ms, this.nafill, this.batchSize, false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            filename = pathGD.replaceFirst("[.][^.]+$", "");
                            pathGD = filename + "_recode.dat";
                            pathGM = filename + "_recode.nmap";
                            this.format = Enum_FileFormat.Numeric;
                            command.addArg("-genotype", pathGD);
                            command.addArg("-map", pathGM);
                            break;
                    }
                    System.out.println("IPatCommand : " + command);
                    setIcon("module");
                    runtime = Runtime.getRuntime();
                    try {
                        process = runtime.exec(command.getCommand());
                        this.areaText.append("\n");
                        this.areaText.setCaretPosition(this.areaText.getDocument().getLength());
                        streamInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        streamError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        while ((tempString = streamInput.readLine()) != null) {
                            this.areaText.append(tempString + System.getProperty("line.separator"));
                            this.areaText.setCaretPosition(this.areaText.getDocument().getLength());
                        }
                    } catch (IOException e) {
                        this.success = false;
                        areaFrame.dispose();
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        popErrorMsg(errors.toString());
                    }

                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        this.success = false;
                        areaFrame.dispose();
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        popErrorMsg(errors.toString());
                    }
                    // Output error message
                    boolean errorClose = false;
                    try {
                        writerError = new PrintWriter(new BufferedWriter(new FileWriter(command.getWD() + "/" + command.getProject() + ".err", true)));
                        while((tempString = streamError.readLine()) != null) {
                            errorClose = true;
                            writerError.println(tempString);
                            if (tempString.toUpperCase().contains("ERROR")) {
                                this.success = false;
                                popErrorMsg(tempString);
                                areaFrame.dispose();
                            }
                        }
                        if (errorClose)
                            writerError.close();
                        // Output normal message
                        File fileOutput = new File(command.getWD() + "/" + command.getProject() + ".log");
                        FileWriter writerOut = new FileWriter(fileOutput.getAbsoluteFile(), false);
                        areaText.write(writerOut);
                    } catch (IOException e) {
                        this.success = false;
                        areaFrame.dispose();
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        popErrorMsg(errors.toString());
                    }
                }
            }
            // Change icon based on successful or not
            if (this.success)
                setIcon("moduleSuc");
            else
                setIcon("moduleFal");
            this.isRunning = false;
            process.destroy();
            this.stop();
        }
    }

    void popErrorMsg(String msg) {
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage(msg);
        optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog(null, "Error");
        dialog.setVisible(true);
    }
}
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

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

    // Obj_FileCluster
//    Obj_FileCluster fileCluster = new Obj_FileCluster();

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

    void run (ArrayList<IPatCommand> command, Enum_FileFormat format, String pathGD, String pathGM, boolean isPLINK) throws IOException {
        this.timerThread.start();
        this.rotateSwitch = true;
        thread = new iPatThread(this.getName(), format, pathGD, pathGM, isPLINK);
        thread.setCommandAndRun(command);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!thread.isRunning()) {
            this.rotateSwitch = false;
            timerThread.stop();
        }
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
        // running
        boolean isRunning;

        public iPatThread(String title, Enum_FileFormat format, String pathGD, String pathGM, boolean isPLINK) throws IOException {
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
            this.areaFrame.setVisible(true);
            this.areaFrame.show();
            // During running (BG)
            this.success = true;
            // converter
            this.pathGD = pathGD;
            this.pathGM = pathGM;
            this.isPLINK = isPLINK;
            this.format = format;
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
                            new Cpu_Converter(this.format,
                                    this.isPLINK ? Enum_FileFormat.PLINK : Enum_FileFormat.Numeric,
                                    this.pathGD, this.pathGM);
                            // rename
                            filename = pathGD.replaceFirst("[.][^.]+$", "");
                            if (isPLINK && format != Enum_FileFormat.PLINK) {
                                pathGD = filename + "_recode.ped";
                                pathGM = filename + "_recode.map";
                                format = Enum_FileFormat.PLINK;
                            } else if (!isPLINK && format != Enum_FileFormat.Numeric) {
                                pathGD = filename + "_recode.dat";
                                pathGM = filename + "_recode.nmap";
                                format = Enum_FileFormat.Numeric;
                            }
                            command.addArg("-genotype", pathGD);
                            command.addArg("-map", pathGM);
                            break;
                        case GS:
                            new Cpu_Converter(format, Enum_FileFormat.Numeric,
                                    pathGD, pathGM);
                            if (format != Enum_FileFormat.Numeric) {
                                filename = pathGD.replaceFirst("[.][^.]+$", "");
                                pathGD = filename + "_recode.dat";
                                pathGM = filename + "_recode.nmap";
                                this.format = Enum_FileFormat.Numeric;
                            }
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
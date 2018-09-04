import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;

class iPatModule extends iPatObject implements ActionListener{
    // Global
    static int countMO = 0;
    // Module define
    int indexMO;
    // Phenotype
    ArrayList<String> traitNames;
    String selectP;
    // Command
    Command commandGWAS;
    Command commandGS;
    Command commandBSA;
    ToolType toolGWAS;
    ToolType toolGS;
    boolean callBSA;
    FileFormat format;
    // Durning running (GUI)
    boolean rotateSwitch;
    boolean rotatePermit;
    // multi run
    iPatThread thread;
    Timer timerThread;

    // FileCluster
//    FileCluster fileCluster = new FileCluster();

    public iPatModule(int x, int y) throws IOException {
        super(x, y);
        // Define module
        this.isFile = false;
        this.isModule = true;
        this.isContainMO = true;
        this.indexMO = countMO++;
        this.setName("Module_" + (this.indexMO + 1));
        setIcon("module");
        // Define command
        this.format  = FileFormat.NA;
        this.commandGWAS = new Command();
        this.commandGS = new Command();
        this.commandBSA = new Command();
        this.toolGWAS = ToolType.NA;
        this.toolGS = ToolType.NA;
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
    ToolType getDeployedGWASTool () {
        return this.toolGWAS;
    }
    ToolType getDeployedGSTool () {
        return this.toolGS;
    }
    FileFormat getFormat() {
        return this.format;
    }
    Command getCommandGWAS() {
        return this.commandGWAS;
    }
    Command getCommandGS() {
        return this.commandGS;
    }
    Command getCommandBSA() {
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
    void setDeployedGWASTool (ToolType tool) {
        this.toolGWAS = tool;
    }
    void setDeployedGSTool (ToolType tool) {
        this.toolGS = tool;
    }
    void setBSA (boolean isBSA) {
        this.callBSA = isBSA;
    }
    void setFormat (FileFormat format) {
        this.format = format;
    }
    void setCommandGWAS(Command commandGWAS) {
        this.commandGWAS = commandGWAS;
    }
    void setCommandGS(Command commandGS) {
        this.commandGS = commandGS;
    }
    void setCommandBSA(Command commandBSA) {
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

    void run (ArrayList<Command> command, FileFormat format, String pathGD, String pathGM, boolean isPLINK) throws IOException {
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
        ArrayList<Command> commands;
        // converter
        String pathGD, pathGM;
        boolean isPLINK;
        FileFormat format;
        // running
        boolean isRunning;

        public iPatThread(String title, FileFormat format, String pathGD, String pathGM, boolean isPLINK) throws IOException {
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

        void setCommandAndRun(ArrayList<Command> command) {
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
            for (Command command : commands) {
                if (command.isEmpty())
                    continue;
                else {
                    // format convertsion
                    switch (command.getType()) {
                        case GWAS:
                            new iPatConverter(this.format,
                                    this.isPLINK ? FileFormat.PLINK : FileFormat.Numeric,
                                    this.pathGD, this.pathGM);
                            // rename
                            filename = pathGD.replaceFirst("[.][^.]+$", "");
                            if (isPLINK && format != FileFormat.PLINK) {
                                pathGD = filename + "_recode.ped";
                                pathGM = filename + "_recode.map";
                                format = FileFormat.PLINK;
                            } else if (!isPLINK && format != FileFormat.Numeric) {
                                pathGD = filename + "_recode.dat";
                                pathGM = filename + "_recode.nmap";
                                format = FileFormat.Numeric;
                            }
                            command.addArg("-genotype", pathGD);
                            command.addArg("-map", pathGM);
                            break;
                        case GS:
                            new iPatConverter(format, FileFormat.Numeric,
                                    pathGD, pathGM);
                            if (format != FileFormat.Numeric) {
                                filename = pathGD.replaceFirst("[.][^.]+$", "");
                                pathGD = filename + "_recode.dat";
                                pathGM = filename + "_recode.nmap";
                                this.format = FileFormat.Numeric;
                            }
                            command.addArg("-genotype", pathGD);
                            command.addArg("-map", pathGM);
                            break;
                    }

                    System.out.println("Command : " + command);
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
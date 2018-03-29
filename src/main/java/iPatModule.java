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
    ArrayList<String> traitNames;
    // Command
    ArrayList<Command> commandGWAS;
    ArrayList<Command> commandGS;
    ArrayList<Command> commandBSA;
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
        this.setLabel("Module " + (this.indexMO + 1));
        setIcon("module");
        // Define command
        this.format  = FileFormat.NA;
        this.commandGWAS = new ArrayList<>();
        this.commandGS = new ArrayList<>();
        this.commandBSA = new ArrayList<>();
        this.toolGWAS = ToolType.NA;
        this.toolGS = ToolType.NA;
        this.callBSA = false;
        // Panel
        this.traitNames = new ArrayList<String>();
        // During running (GUI)
        this.rotateSwitch = false;
        this.rotatePermit = false;
        // multirun
        this.timerThread = new Timer(500, this);
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
    ArrayList<Command> getCommandGWAS() {
        return this.commandGWAS;
    }
    ArrayList<Command> getCommandGS() {
        return this.commandGS;
    }
    ArrayList<Command> getCommandBSA() {
        return this.commandBSA;
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
    void setCommandGWAS(ArrayList<Command> commandGWAS) {
        this.commandGWAS = commandGWAS;
    }
    void setCommandGS(ArrayList<Command> commandGS) {
        this.commandGS = commandGS;
    }
    void setcommandBSA(ArrayList<Command> commandBSA) {
        this.commandBSA = commandBSA;
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

    void run (ArrayList<Command> command) {
        timerThread.start();
        this.rotateSwitch = true;
        thread = new iPatThread();
        thread.setCommandAndRun(command);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("alive : " + thread.isAlive());
        if (!thread.isRunning()) {
            this.rotateSwitch = false;
            timerThread.stop();
        }
    }

    class iPatThread extends Thread {
        Runtime runtime;
        Process process = null;
        BufferedReader streamInput, streamError;
        String tempString;
        PrintWriter writerInput, writerError;
        boolean success;
        JTextArea areaText;
        JScrollPane areaScroll;
        JFrame areaFrame;
        ArrayList<Command> commands;

        public iPatThread() {
            this.areaText = new JTextArea();
            this.areaText.setEditable(false);
            this.areaScroll = new JScrollPane(areaText,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.areaFrame = new JFrame();
            this.areaFrame.setContentPane(areaScroll);
            this.areaFrame.show();
            // During running (BG)
            this.success = true;
        }

        void setCommandAndRun(ArrayList<Command> command) {
            this.commands = command;
            this.start();
        }

        boolean isRunning() {
            return this.process.isAlive();
        }

        @Override
        public void run() {
            for (Command command : commands) {
                if (command.isEmpty())
                    continue;
                else {
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
                        e.printStackTrace();
                    }

                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        this.success = false;
                        e.printStackTrace();
                    }

                    // Output error message
                    boolean errorClose = false;
                    try {
                        writerError = new PrintWriter(new BufferedWriter(new FileWriter(command.getWD() + "/" + command.getProject() + ".err", true)));
                        while((tempString = streamError.readLine()) != null) {
                            errorClose = true;
                            writerError.println(tempString);
                            if (tempString.toUpperCase().contains("ERROR"))
                                this.success = false;
                        }
                        if (errorClose)
                            writerError.close();
                        // Output normal message
                        File fileOutput = new File(command.getWD() + "/" + command.getProject() + ".log");
                        FileWriter writerOut = new FileWriter(fileOutput.getAbsoluteFile(), false);
                        areaText.write(writerOut);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Change icon based on successful or not
            if (this.success)
                setIcon("moduleSuc");
            else
                setIcon("moduleFal");
            process.destroy();
            this.stop();
        }
    }
}
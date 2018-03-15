import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

abstract class iPatObject implements ActionListener {
    // Global
    static int countOB = 0;
    // Object
    Rectangle bound;
    Image icon;
    iFile file;
    JLabel name;
    int indexOB;

    // Boolean
    boolean isDeleted;
    boolean isFile;
    boolean isModule;

    // Group
    boolean isGroup;
    boolean isContainMO;
    int indexGr;

    // Menu
    static JMenuItem menuOpen, menuDel;

    public iPatObject(int x, int y) {
        this.indexOB = this.countOB ++;
        name = new JLabel();
        this.setBound(new Rectangle(x, y, 0, 0));
        this.isDeleted = false;
        this.isGroup = false;
        this.isContainMO = false;
        this.indexGr = -1;
    }

    // Boundary/Position
    void setBound(Rectangle newBound) {
        this.bound = newBound;
        updateLabel();
    }
    void setBoundXY(int newX, int newY) {
        int x = newX;
        int y = newY;
        int xLimit = iPat.WINDOWSIZE.getWidth();
        int yLimit = iPat.WINDOWSIZE.getHeight();
        // Avoid dragging the object out of screen
        if (x + this.getWidth() <= xLimit && x >= 0 &&
            y + this.getHeight() <= yLimit && y >= 0)
            this.bound = new Rectangle(newX, newY, this.getWidth(), this.getHeight());
        updateLabel();
    }
    void setBoundWH(int newW, int newH) {
        this.bound = new Rectangle(this.getX(), this.getY(), newW, newH);
        updateLabel();
    }
    void setDeltaBound(int dx, int dy) {
        this.setBoundXY(this.getX() + dx, this.getY() + dy);
    }
    void setDeltaBound(double dx, double dy) {
        this.setBoundXY(this.getX() + (int)dx, this.getY() + (int)dy);
    }

    void updateLabel(){
        this.name.setLocation(this.getX(), this.getY() + this.getHeight());
        this.name.setSize(200, 15);
    }
    Rectangle getBound() {
        return this.bound;
    }

    // Edge
    int getX() {
        return (int)this.bound.getX();
    }
    int getY() {
        return (int)this.bound.getY();
    }
    int getHeight() {
        return (int)this.bound.getHeight();
    }
    int getWidth() {
        return (int)this.bound.getWidth();
    }
    Point getCenter() {
        return (new Point ((int)(this.getX() + this.getWidth()/2),
                            (int)(this.getY() + this.getHeight()/2)));
    }

    // File/Image
    void setIcon(String filename) {
        this.icon = iPat.IMGLIB.getImage(filename);
        setBound(new Rectangle(this.getX(), this.getY(),
                this.icon.getWidth(null),
                this.icon.getHeight(null)));
    }

    Image getImage() {
        return this.icon;
    }

    String getPath(){
        return this.file.getPath();
    }

    JLabel getLabel() {
        return this.name;
    }

    void setLabel(String text){
        this.name.setText(text);
    }

    void setFile(String text) throws IOException {
        this.file = new iFile(text);
        this.name.setText(this.file.getName());
    }

    void remove() {
        this.isDeleted = true;
        this.isGroup = false;
        this.isContainMO = false;
        this.indexGr = -1;
        setBound(new Rectangle(-1000, -1000, 1, 1));
    }

    // Group
    int getIndex() {
        return this.indexOB;
    }
    int getGrIndex(){
        return this.indexGr;
    }

    void setGrIndex(int gr) {
        this.indexGr = gr;
        this.isGroup = (gr != -1);
    }
    void setContainMO(boolean isContain) {
        this.isContainMO = isContain;
    }

    boolean isGroup() {
        return this.isGroup;
    }
    boolean isContainMO() {
        return this.isContainMO;
    }

    // boolean
    boolean isFile() {
        return isFile;
    }
    boolean isModule() {
        return isModule;
    }
    boolean isDeleted() {
        return false;
    }

    // Mouse event
    abstract void iniMenu();

    abstract void dropMenu(MouseEvent e);

    JMenuItem iniMenuItem(String name) {
        JMenuItem item = new JMenuItem(name);
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(this);
        return item;
    }
    boolean isPointed(MouseEvent e) {
        return this.getBound().contains(e.getPoint());
    }
    void openFile() {
        try {
            Desktop.getDesktop().open(this.file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

class iPatFile extends iPatObject {
    private FileType fileType = FileType.NA;
    // Menu
    JPopupMenu menuFile;
    JMenuItem menuIsRegular,
            menuIsCov,
            menuIsKin;

    public iPatFile(int x, int y, String filename) throws IOException {
        super(x, y);
        // Define the file
        this.isFile = true;
        this.isModule = false;
        this.isContainMO = false;
        this.file = new iFile(filename);
        this.setLabel(this.file.getName());
        setIcon("file");
        // Define Menu
        iniMenu();
    }

    void iniMenu() {
        // Instantiate
        this.menuFile = new JPopupMenu();
        this.menuOpen = iniMenuItem("Open File");
        this.menuIsRegular = iniMenuItem("Assign as a regular file");
        this.menuIsCov = iniMenuItem("Assign as a covariates file");
        this.menuIsKin = iniMenuItem("Assign as a kinship file");
        this.menuDel = iniMenuItem("Remove this file");
        // Construct menu
        this.menuFile.add(menuOpen);
        this.menuFile.add(menuIsRegular);
        this.menuFile.add(menuIsCov);
        this.menuFile.add(menuIsKin);
        this.menuFile.addSeparator();
        this.menuFile.add(menuDel);
        // Layout
        this.menuFile.setBorder(new BevelBorder(BevelBorder.RAISED));
    }

    @Override
    void dropMenu(MouseEvent e) {
        this.menuFile.show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

class iPatModule extends iPatObject {
    // Global
    static int countMO = 0;
    // Menu
    JPopupMenu menuMO;
    JMenuItem menuGWAS;
    JMenuItem menuGS;
    JMenuItem menuBSA;
    JMenuItem menuRun;
    // Module define
    int indexMO;
    ArrayList<String> traitNames;
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
    JTextArea areaText;
    JScrollPane areaScroll;
    JFrame areaFrame;
    // During running (BG)
    boolean success;

    public iPatModule(int x, int y) throws IOException {
        super(x, y);
        // Define module
        this.isFile = false;
        this.isModule = true;
        this.isContainMO = true;
        this.indexMO = countMO++;
        this.setLabel("Module " + (this.indexMO + 1));
        setIcon("module");
        // Define Menu
        iniMenu();
        // Define command
        this.format  = FileFormat.NA;
        this.commandGWAS = new Command();
        this.commandGS = new Command();
        this.commandBSA = new Command();
        this.toolGWAS = ToolType.NA;
        this.toolGS = ToolType.NA;
        this.callBSA = false;
        // Panel
        this.traitNames = new ArrayList<String>();
        // During running (GUI)
        this.rotateSwitch = false;
        this.rotatePermit = false;
        this.areaText = new JTextArea();
        this.areaText.setEditable(false);
        this.areaScroll = new JScrollPane(areaText,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.areaFrame = new JFrame();
        this.areaFrame.setContentPane(areaScroll);
        // During running (BG)
        this.success = false;
    }

    void iniMenu() {
        // Instantiate
        this.menuMO = new JPopupMenu();
        this.menuOpen = iniMenuItem("Open Working Dir.");
        this.menuGWAS = iniMenuItem("GWAS (Empty)");
        this.menuGS = iniMenuItem("GS (Empty)");
        this.menuBSA = iniMenuItem("BSA (Empty)");
        this.menuRun = iniMenuItem("Run");
        this.menuDel = iniMenuItem("Remove this module");
        // Construct menu
        this.menuMO.add(menuOpen);
        this.menuMO.add(menuGWAS);
        this.menuMO.add(menuGS);
        this.menuMO.add(menuBSA);
        this.menuMO.addSeparator();
        this.menuMO.add(menuRun);
        this.menuRun.setEnabled(false);
        this.menuMO.add(menuDel);
        // Layout
        this.menuMO.setBorder(new BevelBorder(BevelBorder.RAISED));
    }

    @Override
    void dropMenu(MouseEvent e) {
        this.menuMO.show(e.getComponent(), e.getX(), e.getY());
    }

    // set
    void setGWASMethod (ToolType method) {
        this.toolGWAS = method;
    }
    void setGSMethod (ToolType method) {
        this.toolGS = method;
    }
    void setBSAMethod (boolean isBSA) {
        this.callBSA = isBSA;
    }
    // is
    boolean isGWASDeployed () {
        return toolGWAS.isDeployed();
    }
    boolean isGSDeployed () {
        return toolGS.isDeployed();
    }
    boolean isBSADeployed () {
        return this.callBSA;
    }
    boolean isNAformat () {
        return this.format.isNA();
    }
    boolean isSuccessFul () {
        return this.success;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == menuGWAS) {

        } else if (source == menuGS) {

        }
    }

    void runCommand(Command firstCommand, Command secondCommand) throws IOException {
        Runtime runtime;
        Process process = null;
        Thread thread;
        BufferedReader streamInput, streamError;
        String tempString;
        PrintWriter writerInput, writerError;

        setIcon("module");
        this.rotatePermit = true;
        this.rotateSwitch = true;
        runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(firstCommand.getCommand());
        } catch (IOException e) {
            this.success = false;
            e.printStackTrace();
        }
        this.areaText.append("\n");
        this.areaText.setCaretPosition(this.areaText.getDocument().getLength());
        streamInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        streamError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((tempString = streamInput.readLine()) != null) {
            this.areaText.append(tempString + System.getProperty("line.separator"));
            this.areaText.setCaretPosition(this.areaText.getDocument().getLength());
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            this.success = false;
            e.printStackTrace();
        }
        // Output error message
        boolean errorClose = false;
        writerError = new PrintWriter(new BufferedWriter(new FileWriter(firstCommand.getWD() + "/" + firstCommand.getProject() + ".err", true)));
        while((tempString = streamError.readLine()) != null) {
            errorClose = true;
            writerError.println(tempString);
            if (tempString.toUpperCase().contains("ERROR"))
                this.success = false;
        }
        if (errorClose)
            writerError.close();
        // Output normal message
        File fileOutput = new File(firstCommand.getWD() + "/" + firstCommand.getProject() + ".log");
        FileWriter writerOut = new FileWriter(fileOutput.getAbsoluteFile(), false);
        areaText.write(writerOut);
        // Change icon based on successful or not
        if (this.success)
            setIcon("moduleSuc");
        else
            setIcon("moduleFal");
        // Stop rotating
        this.rotatePermit = false;
        this.rotateSwitch = false;
        process.destroy();
        // Run next command if needed
        if (secondCommand != null)
            this.runCommand(secondCommand, null);
    }
}
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

abstract class iPatObject implements MouseListener, MouseMotionListener, ActionListener {
    // Global
    static int countOB = 0;
    // Object
    Rectangle bound;
    Image icon;
    File file;
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

    public iPatObject(int x, int y, int w, int h, File file) {
        this.indexOB = this.countOB ++;
        name = new JLabel();
        this.setBound(new Rectangle(x, y, w, h));
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
        this.bound = new Rectangle(newX, newY, this.getWidth(), this.getHeight());
        updateLabel();
    }
    void setBoundWH(int newW, int newH) {
        this.bound = new Rectangle(this.getX(), this.getY(), newW, newH);
        updateLabel();
    }
    void setDeltaBound(int dx, int dy) {
        this.bound = new Rectangle(this.getX() + dx, this.getY() + dy, this.getWidth(), this.getHeight());
        updateLabel();
    }
    void updateLabel(){
        this.name.setLocation(this.getX(), this.getY() + this.getHeight());
        this.name.setSize(200, 15);
    }
    Rectangle getBound() {
        return this.bound;
    }
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
        try {
            this.icon = ImageIO.read(getClass().getResourceAsStream("../resources/" + filename));
        } catch(IOException e) {
            e.printStackTrace();
        }
        setBound(new Rectangle(this.getX(), this.getY(),
                this.icon.getWidth(null),
                this.icon.getHeight(null)));
    }
    String getPath(){
        return this.file.getPath();
    }
    void setLabel(String text){
        this.name.setText(text);
    }
    void setFile(String text){
        this.file = new File(text);
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
    int getIndexGr(){
        return this.indexGr;
    }
    boolean isGroup() {
        return false;
    }
    boolean isContainMO() {
        return false;
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

    void dropMenu(MouseEvent e, JPopupMenu menu) {
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
    JMenuItem iniMenuItem(String name) {
        JMenuItem item = new JMenuItem(name);
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(this);
        return item;
    }
    boolean isPointed(MouseEvent e) {
        return this.getBound().contains(e.getPoint());
    }
    void openFile(String path) {
        File file = new File(path);
        try {
            Desktop.getDesktop().open(file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        // When double left click
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            // If the pointer is on the object
            if (isPointed(e))
                openFile(this.getPath());
        }
    }

    @Override
    abstract public void mousePressed(MouseEvent e);

    @Override
    abstract public void mouseReleased(MouseEvent e);

    @Override
    abstract public void mouseEntered(MouseEvent e);

    @Override
    abstract public void mouseExited(MouseEvent e);

    @Override
    abstract public void mouseDragged(MouseEvent e);

    @Override
    abstract public void mouseMoved(MouseEvent e);
}
class iPatProject {


    Method method_gwas = Method.NA,
            method_gs = Method.NA;
    boolean method_bsa = false;
    // running project
    boolean rotate_switch = false;
    boolean rotate_permit = false;
    JTextArea textarea = new JTextArea();
    JScrollPane scroll = new JScrollPane(textarea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JFrame frame = new JFrame();
    BGThread multi_run;
    Runtime runtime;
    Process process;
    public iPatProject () {
        textarea.setEditable(false);
        frame.setContentPane(scroll);
    }
    // set
    void setGWASmethod (Method method) {
        method_gwas = method;
    }
    void setGSmethod (Method method) {
        method_gs = method;
    }
    void setBSAmethod (boolean isBSA) {
        method_bsa = isBSA;
    }
    // is
    boolean isGWASDeployed () {
        return method_gwas != Method.NA ? true : false;
    }
    boolean isGSDeployed () {
        return method_gs != Method.NA ? true : false;
    }
    boolean isBSADeployed () {
        return method_bsa;
    }
    boolean isNAformat () {
        return format == Format.NA ? true : false;
    }
    boolean isSuc () {
        return multi_run.suc;
    }
    // runcommand
    void runCommand (int index, String[] command, String[] con_command) {
        multi_run = new BGThread(index, command, con_command);
        multi_run.start();
    }
    void initial_phenotype(boolean isPLINK){
        if(isPLINK)
            panel_phenotype = new selectablePanel(trait_names.length - 2, ArrayUtils.remove(ArrayUtils.remove(trait_names, 0), 0), new String[]{"Selected", "Excluded"});
        else
            panel_phenotype = new selectablePanel(trait_names.length - 1, ArrayUtils.remove(trait_names, 0), new String[]{"Selected", "Excluded"});
    }
    void initial_cov(int cov_length, String[] cov_name, String[] model){
        panel_cov = new selectablePanel(cov_length, cov_name, model);
    }
    class selectablePanel extends JPanel{
        public Group_Combo[] CO;
        public int size;
        public selectablePanel(int size, String[] co_names, String[] methods){
            this.size = size;
            this.setLayout(new MigLayout("fillx"));
            CO = new Group_Combo[size];
            for(int i = 0; i < size; i++){
                CO[i] = new Group_Combo(co_names[i], methods);
                this.add(CO[i].name);
                this.add(CO[i].combo, "wrap");
            }
        }
        public String getSelected(){
            String index_cov = "";
            for(int i = 0; i < size; i++){index_cov = index_cov + (String)CO[i].combo.getSelectedItem() + "sep";}
            return index_cov;
        }
    }
    class BGThread extends Thread{
        int OBindex;
        String WD, Project;
        String[] command, con_command;
        boolean suc = false;
        public BGThread(int OBindex, String[] command, String[] con_command){
            this.OBindex = OBindex;
            this.command = command;
            this.Project = command[2];
            this.WD = command[3];
            this.con_command = con_command;}
        @Override
        public void run(){
            String line = ""; Boolean Suc_or_Fal = true;
            rotate_permit = true;
            rotate_switch = true;
            runtime = Runtime.getRuntime();
            iPatPanel.iOB[OBindex].updateImage(iPatPanel.MOimage);
            // Execute the command
            try{
//				ProcessBuilder pb = new ProcessBuilder(command);
//					pb. redirectErrorStream(true);
//
//					process = pb.start();
                process = runtime.exec(command);
            }catch (IOException e1) {e1.printStackTrace();}
            if(iPatPanel.debug){
                // Print command
                textarea.append("Command: \n");
                for (int i = 0; i < command.length; i++) textarea.append(command[i] + " ");
                textarea.append("\nFor R: \n");
                textarea.append("project=\""+command[2]+"\"\n");
                textarea.append("wd=\""+command[3]+"\"\n");
                textarea.append("lib=\""+command[4]+"\"\n");
                textarea.append("format=\""+command[5]+"\"\n");
                textarea.append("ms=as.numeric(\""+command[6]+"\")\n");
                textarea.append("maf=as.numeric(\""+command[7]+"\")\n");
                textarea.append("Y.path=\""+command[8]+"\"\n");
                textarea.append("Y.index=\""+command[9]+"\"\n");
                textarea.append("GD.path=\""+command[10]+"\"\n");
                textarea.append("GM.path=\""+command[11]+"\"\n");
                textarea.append("C.path=\""+command[12]+"\"\n");
                textarea.append("C.index=\""+command[13]+"\"\n");
                textarea.append("K.path=\""+command[14]+"\"\n");
                textarea.append("FAM.path=\""+command[15]+"\"\n");
                textarea.append("BIM.path=\""+command[16]+"\"\n");}
            textarea.append("\n");
            textarea.setCaretPosition(textarea.getDocument().getLength());
            try {
                // Print output message to the panel
                System.out.println("begin to print, "+OBindex);
                BufferedReader input_stream = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader error_stream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while((line = input_stream.readLine()) != null){
                    textarea.append(line+ System.getProperty("line.separator"));
                    textarea.setCaretPosition(textarea.getDocument().getLength());}
                process.waitFor();
                // Direct error to a file
                PrintWriter errWriter = new PrintWriter(new BufferedWriter(new FileWriter(WD + "/" + Project + ".err", true)));
                boolean err_close = false;
                try{
                    // Print error if there is any error message
                    while((line = error_stream.readLine()) != null){
                        err_close = true;
                        errWriter.println(line);
                        if(line.toUpperCase().indexOf("ERROR") >= 0) Suc_or_Fal = false;}
                } catch(IOException e){}
                if(err_close) errWriter.close();
                // Direct output to a file from panel
                File outfile = new File(WD + "/" + Project + ".log");
                FileWriter outWriter = new FileWriter(outfile.getAbsoluteFile(), false);
                textarea.write(outWriter);
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
                Suc_or_Fal = false;}
            // Indicator
            if(Suc_or_Fal)
                iPatPanel.iOB[OBindex].updateImage(iPatPanel.MO_suc);
            else
                iPatPanel.iOB[OBindex].updateImage(iPatPanel.MO_fal);
            // Stop rotating
            rotate_permit = false;
            rotate_switch = false;
            iPatPanel.iOB[OBindex].updateLabel();
            System.out.println("done");
            process.destroy();
            // Run next procedure if needed
            if(con_command!=null){
                multi_run = new BGThread(OBindex, con_command, null);
                multi_run.start();}
        }
    }
}


class iPatFile extends iPatObject {
    private FileType fileType = FileType.NA;
    JPopupMenu menuFile;
    JMenuItem menuIsRegular,
            menuIsCov,
            menuIsKin;

    public iPatFile(int x, int y, int w, int h, File file) throws IOException {
        super(x, y, w, h, file);
        this.isFile = true;
        this.isModule = false;
        setIcon("File.png");
    }

    void iniMenu() {
        // Instantiate
        this.menuFile = new JPopupMenu();
        this.menuOpen = iniMenuItem("Open File");
        this.menuIsRegular = iniMenuItem("Assign as a regular file");
        this.menuIsCov = iniMenuItem("Assign as a covariates file");
        this. menuIsKin = iniMenuItem("Assign as a kinship file");
        this. menuDel = iniMenuItem("Remove this file");
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
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

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
    SelectPanel panelPhenotype;
    SelectPanel panelCov;
    String[] traitNames;
    // Command
    ArrayList<String> commandGWAS;
    ArrayList<String> commandGS;
    ArrayList<String> commandBSA;
    ToolType toolGWAS;
    ToolType toolGS;
    boolean callBSA;

    FileFormat format = FileFormat.NA;

    public iPatModule(int x, int y, int w, int h, File file) throws IOException {
        super(x, y, w, h, file);
        // Define module
        this.isFile = false;
        this.isModule = true;
        this.indexMO = countMO++;
        // Define Menu
        setIcon("Module.png");
        iniMenu();
        // Define command

        toolGWAS = ToolType.NA;
        toolGS = ToolType.NA;
        callBSA = false;

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
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e))
            dropMenu(e, this.menuMO);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == menuGWAS) {

        } else if (source == menuGS) {

        }
    }

    class SelectPanel extends JPanel{
        public GroupCombo[] combo;
        public int size;

        public SelectPanel(int size, String[] coNames, String[] methods){
            this.size = size;
            this.setLayout(new MigLayout("fillx"));
            combo = new GroupCombo[size];
            for(int i = 0; i < size; i++){
                combo[i] = new GroupCombo(coNames[i], methods);
                this.add(combo[i].getLabel());
                this.add(combo[i].getCombo(), "wrap");
            }
        }

        public String getSelected(){
            StringBuffer indexCov = new StringBuffer();
            for (int i = 0; i < size; i ++)
                indexCov.append((String)combo[i].getCombo().getSelectedItem() + "sep");
            return indexCov.toString();
        }
    }

}
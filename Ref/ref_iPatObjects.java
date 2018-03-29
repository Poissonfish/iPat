xxrpackage main;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.ArrayUtils;

import main.iPatObject.Filetype;
import main.iPatObject.Object;
import main.iPatProject.BGThread;
import main.iPatProject.Format;
import main.iPatProject.Method;
import main.iPatProject.selectablePanel;
import net.miginfocom.swing.MigLayout;

class iPatObject {
	enum Filetype {
		NA("Unknown"), P("Phenotype"), GD("Genotype"), GM("Map"), FAM("FAM"), BIM("BIM"), C("Covariate"), K("Kinship");
		String name;
		private Filetype (String name) {
			this.name = name;
		}
		String getName () {
			return this.name;
		}
	}
	enum Object {
		NA, TB, MO;
	}
	// Object
	int X = 0, Y = 0, H = 0, W = 0;
	Point pt = new Point(0, 0);
	boolean isDeleted = false;
	Image image = null;
	Rectangle bound = new Rectangle(-100, -100, 0, 0);
	String path = "";
	JLabel name = new JLabel();
	Filetype type = Filetype.NA; 
	Object object = Object.NA;
	// Group
	boolean isGroup = false, containMO = false;
	int Groupindex = -1;
	public iPatObject () throws IOException {
	}
	void remove(){
		isDeleted = true;
		isGroup = false;
		containMO = false;
		Groupindex = -1;
		setLocation(new Point(-10000, -10000));
		updateLabel();
	}
	// set
	void setLocation(Point point){
		X = point.x;
		Y = point.y;
		pt.setLocation(X + W/2, Y + H/2);
		updateBound();
		updateLabel();
	}
	void setDeltaLocation(double dx, double dy){
		X += dx;
		Y += dy;
		pt.setLocation(X + W/2, Y + H/2);
		updateBound();
		updateLabel();
	}
	void setLabel(String text){
		name.setText(text);		
	}
	void setPath(String text){
		path = text;
		File file = new File(text);
		name.setText(file.getName());
	}
	void setAsTB(){
		object = Object.TB;
	}
	void setAsMO(){
		object = Object.MO;
	}
	// get
	String getPath(){
		return path; 
	}
	Point getLocation(){
		return pt;
	}
	Rectangle getBound(){
		return bound;
	}
	int getGroupIndex(){
		return Groupindex;
	}
	// is
	boolean isTB(){
		if(object == Object.TB) return true;
		else return false;
	}
	boolean isMO(){
		if(object == Object.MO) return true;
		else return false;
	}
	// update
	void updateBound(){
		bound = new Rectangle(X, Y, W, H);
	}
	void updateLabel(){
		name.setLocation(X, Y + H);
		name.setSize(200, 15);	
	}
	void updateImage(Image inputimage){
		image = inputimage;
		H = image.getHeight(null);
		W = image.getWidth(null);
		updateBound();
		updateLabel();
	}
}
class iPatProject {
	enum Format {
		NA("NA"), Hapmap("Hapmap"), Numerical("Numerical"), VCF("VCF"), PLINK("PLINK"), PLINK_bin("PLINK(Binary)"), BSA("BSA");
		String name;
		private Format (String name) {
			this.name = name;
		}
		String getName () {
			return this.name;
		}
	}
	enum Method {
		NA("NA", -1), GAPIT("GAPIT", 0), FarmCPU("FarmCPU", 1), PLINK("PLINK", 2), gBLUP("gBLUP", 3), rrBLUP("rrBLUP", 4), BGLR("BGLR", 5), BSA("BSA", 6);
		String name;
		int index;
		private Method (String name, int index) {
			this.name = name; 
			this.index = index;
		}
		String getName () {
			return this.name;
		}
		int getIndex () {
			return this.index;
		}
	}
	// project config
	Format format = Format.NA;
	 // phenotype
		selectablePanel panel_phenotype, panel_cov;
		String[] trait_names = new String[]{""};
	 // command
		String[] command_gwas,
				 command_gs, 
				 command_bsa;
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
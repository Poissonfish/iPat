package main;

import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import net.miginfocom.swing.MigLayout;

public class Classes {

}

class iPat_chooser{
	File selectedfile = null;

	public iPat_chooser(){
		int value;
    	JFileChooser folder_chooser = new JFileChooser();

    	folder_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	folder_chooser.setApproveButtonText("Link!");
    	folder_chooser.setDialogTitle("Choose your working directory");

    	folder_chooser.setApproveButtonToolTipText("New Approve Tool Tip");
       
    	value = folder_chooser.showOpenDialog(null);
    	if (value == JFileChooser.APPROVE_OPTION){
    		selectedfile = folder_chooser.getSelectedFile();
    	} 
    	selectedfile.getAbsolutePath();
	}
	
	String getPath(){
		return selectedfile.getAbsolutePath();
	}
}

class settingframe extends JFrame implements ActionListener{
	
	Preferences pref = Preferences.userRoot().node("/ipat");  
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_gapit;
	JButton gapit_go = new JButton("GO");
	
	String[] gapit_model_names= {"GCM", "MLM", "CMLM", "ECMLM", "FarmCPU"};
	JComboBox gapit_model= new JComboBox(gapit_model_names);	
	JLabel gapit_model_text = new JLabel("Model selection");
	
	JTextField PCA_input = new JTextField(3);
	JLabel PCA_text = new JLabel("PCA");
	
	Runnable back_run = new Runnable(){
		@Override
		public void run(){
			run_farm_cpu();
		}
	};
	int test_run = 0;
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_farm;
	JButton farm_go = new JButton("GO");
	
	JTextField loop_input = new JTextField(3);
	JLabel loop_text = new JLabel("# of iterations");
	
	JCheckBox loop_result_out = new JCheckBox("Output every loop result");
	JCheckBox MAF_calculate = new JCheckBox("MAF calculate");
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	JButton cancel= new JButton("Cancel");
	JPanel main_panel, ipat_panel; 

    String folder_path = new String();
	Rengine r;
	int  MOindex;
	
	public settingframe(String folder_path, Rengine r, int MOindex){	
		gapit_model.setSelectedItem(pref.get("model", "GCM"));
		PCA_input.setText(pref.get("PCA", "3"));
		loop_input.setText(pref.get("loop", "3"));
		loop_result_out.setSelected(pref.getBoolean("loop_out", false));
		MAF_calculate.setSelected(pref.getBoolean("MAF", false));
		
		this.folder_path = folder_path;
		this.r = r;
		this.MOindex = MOindex;
		//this.ipat_panel = ipat_panel;
	}
	
	public void initial(){
		panel_gapit = new JPanel(new MigLayout("fillx"));
		panel_gapit.add(gapit_model_text, "cell 0 0");
		panel_gapit.add(gapit_model, "cell 0 1");
		panel_gapit.add(PCA_text, "cell 1 0 1 2");
		panel_gapit.add(PCA_input, "cell 2 0 1 2");
		panel_gapit.add(gapit_go, "dock east");
		panel_gapit.setBorder(new TitledBorder(new EtchedBorder(), "GAPIT"));

		gapit_go.addActionListener(this);
		///////////////////////////////////////////////////////////////////////////////////////		
		panel_farm = new JPanel(new MigLayout("fillx"));
		panel_farm.add(loop_input, "cell 0 0 1 2");
		panel_farm.add(loop_text, "cell 1 0 1 2");
		panel_farm.add(loop_result_out, "cell 2 0");
		panel_farm.add(MAF_calculate, "cell 2 1");
		panel_farm.add(farm_go, "dock east");
		panel_farm.setBorder(new TitledBorder(new EtchedBorder(), "FarmCPU"));
		
		loop_result_out.addActionListener(this);
		MAF_calculate.addActionListener(this);
		farm_go.addActionListener(this);
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel = new JPanel(new MigLayout("fill", "[grow][grow]"));
		main_panel.add(panel_gapit, "cell 0 0, grow");
		main_panel.add(panel_farm, "cell 0 1, grow");
		this.setContentPane(main_panel);
		this.pack();
		this.setTitle("Configuration");
		this.show();
	}
	
	public void actionPerformed(ActionEvent ip){
	      Object source = ip.getSource();	
	      if (source == gapit_go){
	  		run_gapit();
	  		this.dispose();
	      }else if (source == farm_go){
	    	new Thread(back_run).start();
	    	this.dispose();
	      }else if (source == cancel){
	    	add_folder("FarmCPU Result");
		  	ipat_panel.repaint();	 		
	    	this.dispose();  
	      }
	}
		
	void add_folder(String folder_name){
		myPanel.TBcount++;
    	myPanel.TBimageX[myPanel.TBcount] = myPanel.MOimageX[MOindex]+ (myPanel.MOimageW[MOindex]/2)-(myPanel.TBimageW[myPanel.TBcount]/2);
    	myPanel.TBimageY[myPanel.TBcount] = myPanel.MOimageY[MOindex]+ 80;
    	myPanel.TBBound[myPanel.TBcount] = new Rectangle(myPanel.TBimageX[myPanel.TBcount], myPanel.TBimageY[myPanel.TBcount], 60, 60);
    	myPanel.TBfile[myPanel.TBcount] = folder_path;
    	myPanel.TBname[myPanel.TBcount].setLocation(myPanel.TBimageX[myPanel.TBcount], 
    												myPanel.TBimageY[myPanel.TBcount]+myPanel.TBimageH[myPanel.TBcount]- myPanel.panelHeigth+15);
    	myPanel.TBname[myPanel.TBcount].setSize(200, 15);
		myPanel.TBname[myPanel.TBcount].setText(folder_name);
    	
    	myPanel.MOco[MOindex][0] = 1;
	    myPanel.MOco[MOindex][1] = myPanel.COcount;
    	myPanel.MOco[MOindex][2] = 1;

    	myPanel.TBco[myPanel.TBcount][0] = 1;
    	myPanel.TBco[myPanel.TBcount][1] = myPanel.COcount;
    	myPanel.TBco[myPanel.TBcount][2] = 1;
    	
    	myPanel.linkline[myPanel.linklineindex][0] = 2;
    	myPanel.linkline[myPanel.linklineindex][1] = MOindex;
    	myPanel.linkline[myPanel.linklineindex][2] = 1;
    	myPanel.linkline[myPanel.linklineindex][3] = myPanel.TBcount;

    	myPanel.linklineindex++;
    	myPanel.COcount++;
	}
	
	void run_farm_cpu(){
		r.eval("setwd('"+folder_path+"')");
    	r.eval("library(bigmemory)");
    	r.eval("library(biganalytics)");
    	r.eval("require(compiler)");

    	r.eval("source('http://www.zzlab.net/GAPIT/gapit_functions.txt')");
    	r.eval("source('http://www.zzlab.net/FarmCPU/FarmCPU_functions.txt')");
    	r.eval("myY <- read.table('mdp_traits_validation.txt', head = TRUE)");
    	r.eval("myGD <- read.big.matrix('mdp_numeric.txt', type = 'char', sep = '\t',header = T)");
    	r.eval("myGM <-  read.table('mdp_SNP_information.txt', head = TRUE)");
    	r.eval("myFarmCPU=FarmCPU(Y=myY[,c(1,8)], GD=myGD, GM=myGM)");
  		add_folder("FarmCPU Result");
  		//ipat_panel.repaint();
  		pref.put("model", (String) gapit_model.getSelectedItem());
  		pref.put("PCA", PCA_input.getText());
  		pref.put("loop", loop_input.getText());
  		pref.putBoolean("loop_out", loop_result_out.isSelected());
  		pref.putBoolean("MAF", MAF_calculate.isSelected());
	}
	
	void run_gapit(){
		System.out.println("running");
  		r.eval("ptm <- proc.time()");
  		r.eval("setwd('"+folder_path+"')");
  		r.eval("library(MASS)");
  		r.eval("library(multtest)");
  		r.eval("library(gplots)");
  		r.eval("library(compiler)");
  		r.eval("library(scatterplot3d)");

  		r.eval("source('http://www.zzlab.net/GAPIT/emma.txt')");
  		r.eval("source('http://www.zzlab.net/GAPIT/gapit_functions.txt')");
  			  		
  		r.eval("myY  <- read.table('mdp_traits.txt', head = TRUE)");
  		r.eval("myG <- read.delim('mdp_genotype_test.hmp.txt', head = FALSE)");
  		r.eval("myGAPIT <- GAPIT(Y=myY,	G=myG, PCA.total=3)");
  		r.eval("x= proc.time() - ptm ");
  		r.eval("print(x)");
  		add_folder("GAPIT Result");
  		
  		pref.put("model", (String) gapit_model.getSelectedItem());
  		pref.put("PCA", PCA_input.getText());
  		pref.put("loop", loop_input.getText());
  		pref.putBoolean("loop_out", loop_result_out.isSelected());
  		pref.putBoolean("MAF", MAF_calculate.isSelected());
	}
	
}

class TextConsole implements RMainLoopCallbacks{
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
    }
    
    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy("+which+")");
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }
    
    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \""+message+"\"");
    }

    public String rChooseFile(Rengine re, int newFile) {
    	FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
    	fd.show();
    	String res=null;
    	if (fd.getDirectory()!=null) res=fd.getDirectory();
    	if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
    	return res;        
    }
    
    public void   rFlushConsole (Rengine re) {
    }
	
    public void   rLoadHistory  (Rengine re, String filename) {
    }			
    
    public void   rSaveHistory  (Rengine re, String filename) {
    }			
}


package main;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.rosuda.JRI.Rengine;

import net.miginfocom.swing.MigLayout;


public class Configuration extends JFrame implements ActionListener{
	
	Preferences pref = Preferences.userRoot().node("/ipat");  
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_EMMA;
	JLabel esp_text = new JLabel("esp");
	JTextField esp_input = new JTextField(5);
	JLabel llim_text = new JLabel("llim");
	JTextField llim_input = new JTextField(5);
	JLabel ngrid_text = new JLabel("ngrid");
	JTextField ngrid_input = new JTextField(5);
	JLabel ulim_text = new JLabel("ulim");
	JTextField ulim_input = new JTextField(5);
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_Farm;
	JLabel acceleration_text = new JLabel("acceleration");
	JTextField acceleration_input= new JTextField(5);
	JLabel converge_text = new JLabel("converge");
	JTextField converge_input= new JTextField(5);
	JLabel maxLoop_text = new JLabel("maxLoop");
	JTextField maxLoop_input= new JTextField(5);
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_file;
	JLabel file_Ext_G_text = new JLabel("Ext.G");
	JTextField file_Ext_G_input= new JTextField(5);
	JLabel file_Ext_GD_text = new JLabel("Ext.GD");
	JTextField file_Ext_GD_input= new JTextField(5);
	JLabel file_Ext_GM_text = new JLabel("Ext.GM");
	JTextField file_Ext_GM_input= new JTextField(5);
	JLabel file_fragment_text = new JLabel("Fragment");
	JTextField file_fragment_input= new JTextField(5);
	JLabel file_from_text = new JLabel("From");
	JTextField file_from_input= new JTextField(5);
	JLabel file_to_text = new JLabel("To");
	JTextField file_to_input= new JTextField(5);
	JLabel file_total_text = new JLabel("Total");
	JTextField file_total_input= new JTextField(5);
	JLabel file_G_text = new JLabel("G");
	JTextField file_G_input= new JTextField(5);
	JLabel file_GD_text = new JLabel("GD");
	JTextField file_GD_input= new JTextField(5);
	JLabel file_GM_text = new JLabel("GM");
	JTextField file_GM_input= new JTextField(5);
	JLabel file_path_text = new JLabel("Path");
	JTextField file_path_input= new JTextField(5);
	JCheckBox file_output = new JCheckBox("output");
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_group;
	JLabel group_by_text = new JLabel("By");
	JTextField group_by_input = new JTextField(5);
	JLabel group_from_text = new JLabel("From");
	JTextField group_from_input = new JTextField(5);
	JLabel group_to_text = new JLabel("To");
	JTextField group_to_input = new JTextField(5);
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_kinship;
	String[] kinship_algorithm_names= {"VanRaden"};
	JComboBox kinship_algorithm_input= new JComboBox(kinship_algorithm_names);
	JLabel kinship_algorithm_text = new JLabel("Algorithm");
	String[] kinship_cluster_names= {"average"};
	JComboBox kinship_cluster_input= new JComboBox(kinship_cluster_names);
	JLabel kinship_cluster_text = new JLabel("Cluster");
	String[] kinship_group_names= {"Mean"};
	JComboBox kinship_group_input= new JComboBox(kinship_group_names);
	JLabel kinship_group_text = new JLabel("Group");
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_LD;
	JLabel LD_chromosome_text = new JLabel("Chromosome");
	JTextField LD_chromosome_input= new JTextField(5);
	JLabel LD_location_text = new JLabel("Location");
	JTextField LD_location_input = new JTextField(5);
	JLabel LD_range_text = new JLabel("Range");
	JTextField LD_range_input = new JTextField(5);
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_method;
	String[] method_iteration_names= {"accum"};
	JComboBox method_iteration_input= new JComboBox(method_iteration_names);
	JLabel method_iteration_text = new JLabel("iteration");
	String[] method_bin_names= {"static"};
	JComboBox method_bin_input= new JComboBox(method_bin_names);
	JLabel method_bin_text = new JLabel("bin");
	String[] method_GLM_names= {"fast.lm"};
	JComboBox method_GLM_input= new JComboBox(method_GLM_names);
	JLabel method_GLM_text = new JLabel("GLM");
	String[] method_sub_names= {"reward"};
	JComboBox method_sub_input= new JComboBox(method_sub_names);
	JLabel method_sub_text = new JLabel("sub");
	String[] method_sub_final_names= {"reward"};
	JComboBox method_sub_final_input= new JComboBox(method_sub_final_names);
	JLabel method_sub_final_text = new JLabel("sub_final");
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_model;
	JCheckBox model_selection = new JCheckBox("Model selection");
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_output;
	JLabel output_cutOff_text = new JLabel("cutOff");
	JTextField output_cutOff_input= new JTextField(5);
	JLabel output_CV_Inheritance_text = new JLabel("CV inheritance");
	JTextField output_CV_Inheritance_input = new JTextField(5);
	JLabel output_DPP_text = new JLabel("DPP");
	JTextField output_DPP_input= new JTextField(5);
	JCheckBox output_Geno_View_output = new JCheckBox("Geno View output");
	JCheckBox output_iteration_output = new JCheckBox("iteration output");
	JLabel output_maxOut_text = new JLabel("maxOut");
	JTextField output_maxOut_input= new JTextField(5);
	JCheckBox output_hapmap = new JCheckBox("hapmap");
	JCheckBox output_numerical= new JCheckBox("numerical");
	String[] output_plot_style_names= {"Oceanic"};
	JComboBox output_plot_style_input= new JComboBox(output_plot_style_names);
	JLabel output_plot_style_text = new JLabel("plot style");
	JLabel output_threshold_text = new JLabel("threshold");
	JTextField output_threshold_input= new JTextField(5);
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_PCA;
	JLabel PCA_total_text = new JLabel("total");
	JTextField PCA_total_input= new JTextField(5);
	JCheckBox PCA_View_output = new JCheckBox("View output");
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_QTN;
	JLabel QTN_prior_text = new JLabel("Prior");
	JTextField QTN_prior_input= new JTextField(5);
	JLabel QTN_text = new JLabel("QTN");
	JTextField QTN_input= new JTextField(5);
	JLabel QTN_limit_text = new JLabel("QTN limit");
	JTextField QTN_limit_input= new JTextField(5);
	String[] QTN_method_names= {"Penalty"};
	JComboBox QTN_method_input = new JComboBox(QTN_method_names);
	JLabel QTN_method_text = new JLabel("method");
	JLabel QTN_position_text = new JLabel("position");
	JTextField QTN_position_input= new JTextField(5);
	JLabel QTN_round_text = new JLabel("round");
	JTextField QTN_round_input= new JTextField(5);
	JCheckBox QTN_update = new JCheckBox("update");
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_SNP;
	JCheckBox SNP_create_indicater = new JCheckBox("Create indicator");
	JCheckBox SNP_major_allele_zero = new JCheckBox("Major allele zero");

	JLabel SNP_CV_text = new JLabel("CV");
	JTextField SNP_CV_input= new JTextField(5);
	String[] SNP_effect_names= {"Add"};
	JComboBox SNP_effect_input = new JComboBox(SNP_effect_names);
	JLabel SNP_effect_text = new JLabel("effect");
	JLabel SNP_FDR_text = new JLabel("FDR");
	JTextField SNP_FDR_input= new JTextField(5);
	JLabel SNP_fraction_text = new JLabel("fraction");
	JTextField SNP_fraction_input= new JTextField(5);
	String[] SNP_impute_names= {"Middle"};
	JComboBox SNP_impute_input = new JComboBox(SNP_impute_names);
	JLabel SNP_impute_text = new JLabel("impute");
	JLabel SNP_MAF_text = new JLabel("MAF");
	JTextField SNP_MAF_input = new JTextField(5);
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////

	
/*
 * String[] QTN_method_names= {"Penalty"};
	JComboBox QTN_method_input = new JComboBox(QTN_method_names);
	JLabel QTN_method_text = new JLabel("method");

SNP.P3D	TRUE
SNP.permutation	FALSE
SNP.robust	"GLM"
SNP.test	TRUE

*/
	
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
	
	public Configuration(String folder_path, Rengine r, int MOindex){	
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
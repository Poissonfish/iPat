package main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;

import org.rosuda.JRI.*;
import net.miginfocom.swing.MigLayout;


public class Configuration extends JFrame implements ActionListener, WindowListener{
	Preferences pref;
	///////////////////////////////////////////////////////////////////////////////////////
	//Config 2
	JLabel project_text_s = new JLabel("Task name");
	JTextField project_input_s = new JTextField(10);
	JLabel workingdir_text_s = new JLabel("Working Directory");
	JTextField workingdir_input_s = new JTextField(15);
	JButton wd_browse_s = new JButton("Browse");

	JPanel panel_phenotype;
	JLabel P_filename = new JLabel("File:\tNA");

	JPanel panel_genotype;
	JLabel G_filename = new JLabel("File:\tNA");
	JLabel G_format = new JLabel("Format: HapMap");
	
	JPanel panel_ki;
	ButtonGroup KI_group = new ButtonGroup();
	JRadioButton KI_gapit = new JRadioButton("Calculate within GAPIT");
	JRadioButton KI_user = new JRadioButton("User input");
	JCheckBox Prediction = new JCheckBox("GP only");
	JButton KI_browse = new JButton("Browse");
	JTextField KI_path = new JTextField(15);
	iPat_chooser KI_chooser;	
	
	JPanel panel_co;
	ButtonGroup CO_group = new ButtonGroup();
	JRadioButton CO_gapit = new JRadioButton("Calculate within GAPIT");
	JLabel PCA_total_text_s = new JLabel("PCA.total");
	JTextField PCA_total_input_s= new JTextField(3);
	JRadioButton CO_user = new JRadioButton("User input");
	JButton CO_browse = new JButton("Browse");
	JTextField CO_path = new JTextField(15);
	iPat_chooser CO_chooser;	

	String[] kinship_cluster_names_s= {"average", "complete", "ward", "single", "mcquitty", "median", "centroid"};
	JComboBox kinship_cluster_input_s= new JComboBox(kinship_cluster_names_s);
	JLabel kinship_cluster_text_s= new JLabel("Cluster");
	String[] kinship_group_names_s= {"Mean", "Max", "Min", "Median"};
	JComboBox kinship_group_input_s= new JComboBox(kinship_group_names_s);
	JLabel kinship_group_text_s = new JLabel("Group");
	JLabel group_by_text_s = new JLabel("By");
	JTextField group_by_input_s = new JTextField(5);
	JLabel group_from_text_s = new JLabel("From");
	JTextField group_from_input_s = new JTextField(5);
	JLabel group_to_text_s = new JLabel("To");
	JTextField group_to_input_s = new JTextField(5);
	
	JLabel SNP_fraction_text_s = new JLabel("SNP fraction");
	JTextField SNP_fraction_input_s= new JTextField(5);
	String[] file_fragment_names_s = {"512", "256", "128", "64"};
	JLabel file_fragment_text_s = new JLabel("File fragment");
	JComboBox file_fragment_input_s= new JComboBox(file_fragment_names_s);
	JCheckBox model_selection_s = new JCheckBox("Model selection");

	///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////
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
	
	String[] file_fragment_names = {"512", "256", "128", "64"};
	JLabel file_fragment_text = new JLabel("File fragment");
	JComboBox file_fragment_input= new JComboBox(file_fragment_names);
	
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
	String[] kinship_cluster_names= {"average", "complete", "ward", "single", "mcquitty", "median", "centroid"};
	JComboBox kinship_cluster_input= new JComboBox(kinship_cluster_names);
	JLabel kinship_cluster_text = new JLabel("Cluster");
	String[] kinship_group_names= {"Mean", "Max", "Min", "Median"};
	JComboBox kinship_group_input= new JComboBox(kinship_group_names);
	JLabel kinship_group_text = new JLabel("Group");
	//
	JPanel panel_CMLM;
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
	JLabel PCA_total_text = new JLabel("PCA.total");
	JTextField PCA_total_input= new JTextField(3);
	JCheckBox PCA_View_input = new JCheckBox("View output");
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
	JLabel SNP_fraction_text = new JLabel("SNP fraction");
	JTextField SNP_fraction_input= new JTextField(5);
	String[] SNP_impute_names= {"Middle"};
	JComboBox SNP_impute_input = new JComboBox(SNP_impute_names);
	JLabel SNP_impute_text = new JLabel("impute");
	JLabel SNP_MAF_text = new JLabel("MAF");
	JTextField SNP_MAF_input = new JTextField(5);
	JCheckBox SNP_P3D = new JCheckBox("P3D");
	JCheckBox SNP_permutation = new JCheckBox("permutation");
	String[] SNP_robust_names= {"GLM"};
	JComboBox SNP_robust_input = new JComboBox(SNP_robust_names);
	JLabel SNP_robust_text = new JLabel("robust");
	JCheckBox SNP_test = new JCheckBox("SNP_test");
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_super;
	JLabel SUPER_bin_by_text = new JLabel("bin by");
	JTextField SUPER_bin_by_input = new JTextField(5);
	JLabel SUPER_bin_from_text = new JLabel("bin from");
	JTextField SUPER_bin_from_input = new JTextField(5);
	JLabel SUPER_bin_to_text = new JLabel("bin to");
	JTextField SUPER_bin_to_input = new JTextField(5);
	JLabel SUPER_bin_selection_text = new JLabel("bin selection");
	JTextField SUPER_bin_selection_input= new JTextField(5);
	JLabel SUPER_bin_size_text = new JLabel("bin size");
	JTextField SUPER_bin_size_input = new JTextField(5);
	
	JLabel SUPER_BINS_text = new JLabel("BINS");
	JTextField SUPER_BINS_input= new JTextField(5);
	JLabel SUPER_FDR_rate_text = new JLabel("FDR rate");
	JTextField SUPER_FDR_rate_input= new JTextField(5);
	JLabel SUPER_GT_index_text = new JLabel("GTindex");
	JTextField SUPER_GT_index_input= new JTextField(5);

	JLabel SUPER_inclosure_by_text = new JLabel("inclosure by");
	JTextField SUPER_inclosure_by_input= new JTextField(5);
	JLabel SUPER_inclosure_from_text = new JLabel("inclosure from");
	JTextField SUPER_inclosure_from_input= new JTextField(5);
	JLabel SUPER_inclosure_to_text = new JLabel("inclosure to");
	JTextField SUPER_inclosure_to_input= new JTextField(5);
	JLabel SUPER_LD_text = new JLabel("LD");
	JTextField SUPER_LD_input= new JTextField(5);
	JLabel SUPER_sanawich_bottom_text = new JLabel("sanawich bottom");
	JTextField SUPER_sanawich_bottom_input= new JTextField(5);
	JLabel SUPER_sanawich_top_text = new JLabel("sanawich top");
	JTextField SUPER_sanawich_top_input= new JTextField(5);
	JLabel SUPER_GD_text = new JLabel("GD");
	JTextField SUPER_GD_input= new JTextField(5);
	JCheckBox SUPER_GS = new JCheckBox("GS");
	
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel main_panel;
	JButton go = new JButton("GO");
	JButton go_2 = new JButton("GO");
	JButton go_3 = new JButton("GO");
	JPanel panel_advance;
	JCheckBox CMLM_enable = new JCheckBox("Enable");
	Boolean CMLM_open = false;
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel wd_panel;
	JButton browse = new JButton("Browse");
	iPat_chooser chooser;	
	JLabel wd_text = new JLabel("Working Directory");
	JTextField wd_input = new JTextField(15);
	JLabel n_text = new JLabel("Task Name");
	JTextField n_input = new JTextField(10);
	///////////////////////////////////////////////////////////////////////////////////////	
	Runnable back_run = new Runnable(){
		@Override
		public void run(){
			try {
				GAPIT_two(MOindex);
				//GAPIT(MOindex);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	};
	Runnable back_run_2 = new Runnable(){
		@Override
		public void run(){
			try {
				GAPIT_two(MOindex);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	};
	
	int test_run = 0;
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
    String folder_path = new String();
	int  MOindex;
	///////////////////////////////////////////////////////////////////////////////////////
	int[][] file_index = new int[10][2]; //tbindex; filetype: 1=G, 2=P
	
	public Configuration(int MOindex) throws FileNotFoundException, IOException{	
		ProcessBuilder pb = new ProcessBuilder("sh", "-c", "mkdir testfolder");
		Process pro = pb.start();
		pb = new ProcessBuilder("sh", "-c", "touch testtext");
		pro = pb.start();
		pb = new ProcessBuilder("sh", "-c", "Rscript test.r");
		pro = pb.start();
		pb = new ProcessBuilder("sh", "-c", "R CMD BATCH --no-save --no-restore '--args y=5' test2.r test.out");
		pro = pb.start();
		
		this.MOindex = MOindex;
		int index = 0;
		index = catch_files(file_index);	
		String P_name = "", G_name = "", GD_name = "", GM_name = "";
		for (int i = 0; i<4;i++){
			if(file_index[i][1] == 0){
				Path p = Paths.get(myPanel.TBfile[file_index[i][0]]);
				P_name = p.getFileName().toString();
			}else if(file_index[i][1] == 1){
				Path p = Paths.get(myPanel.TBfile[file_index[i][0]]);
				G_name = p.getFileName().toString();
			}else if(file_index[i][1] == 2){
				Path p = Paths.get(myPanel.TBfile[file_index[i][0]]);
				GD_name = p.getFileName().toString();
			}else if(file_index[i][1] == 3){
				Path p = Paths.get(myPanel.TBfile[file_index[i][0]]);
				GM_name = p.getFileName().toString();
			}
		}
		JScrollPane subPane = null;
		JScrollPane editPane = null;
		switch(index){
			case 1:
				break;
			case 2:
				subPane = config_two(P_name, G_name);
				break;
			case 3:
				break;
		}		
        JTabbedPane mainPane = new JTabbedPane();
        mainPane.addTab("Simple Config.", subPane);
		this.setContentPane(mainPane);
		this.setTitle("Configuration");
		this.pack();
		this.show();	
		pref = Preferences.userRoot().node("/ipat"); 
		load();
//		config_two();
//		configuration_initial();
		addWindowListener(this);
	}	
	
	int catch_files(int[][] file_index) throws IOException{
		for(int i=0;i<5;i++){
			file_index[i][0] = 0; //default to TBindex = 0, which is null
			file_index[i][1] = -1; //default to -1; 0:P, 1:G, 2:GD, 3:GM, 4:KI, 5:CO
		}
		int index = 0;
		for (int i = 1; i<=myPanel.TBcount; i++){
			if(index>3){break;}
			if(myPanel.TBco[i][3]==myPanel.MOco[MOindex][3] && myPanel.TBco[i][3]!=-1){
				System.out.println(myPanel.TBfile[i]);
				file_index[index][0] = i;
				index++;
			}
		}
		switch (index){
			case 1:
				file_index[0][1] = 0;
				break;
			case 2:
				String[] text1 = read_10_lines(myPanel.TBfile[file_index[0][0]]);
				String[] one_row1 = text1[1].split("\t");
				String[] one_row2 = text1[2].split("\t");
				String[] text2 = read_10_lines(myPanel.TBfile[file_index[1][0]]);
				String[] two_row1 = text2[1].split("\t");
				String[] two_row2 = text2[2].split("\t");
				if(one_row1.length<5){
					file_index[0][1] = 0;
					file_index[1][1] = 1;
				}else if(two_row1.length<5){
					file_index[0][1] = 1;
					file_index[1][1] = 0;
				}else if( (one_row1[4].equals("+")||one_row1[4].equals("-")) &&
						  (one_row2[4].equals("+")||one_row2[4].equals("-")) ){
					file_index[0][1] = 1;
					file_index[1][1] = 0;
				}else{
					file_index[0][1] = 0;
					file_index[1][1] = 1;
				}		
				break;
			case 3:
				//
				//Distinguish g and y
				//
				
				break;
		}
		return index;		
	}
	public void config_one(){
		
	}
	
	public JScrollPane config_two(String P_name, String G_name){
		go_2.setFont(new Font("Ariashowpril", Font.BOLD, 40));		///////////////////////////////////////////////////////////////////////////////////////
		wd_panel = new JPanel(new MigLayout("fillx"));
		wd_panel.add(project_text_s, "wrap");
		wd_panel.add(project_input_s, "wrap");
		wd_panel.add(workingdir_text_s, "wrap");
		wd_panel.add(workingdir_input_s);
		wd_panel.add(wd_browse_s, "wrap");
		wd_panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Task", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_genotype = new JPanel(new MigLayout("fillx"));
		panel_genotype.add(G_filename, "wrap");
		G_filename.setText("File:\t"+G_name);
		panel_genotype.add(G_format);
		panel_genotype.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Genotype", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_phenotype = new JPanel(new MigLayout("fillx"));
		panel_phenotype.add(P_filename);
		P_filename.setText("File:\t"+P_name);
		panel_phenotype.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Phenotype", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
	///////////////////////////////////////////////////////////////////////////////////////
		panel_ki = new JPanel(new MigLayout("fillx"));
		KI_group.add(KI_gapit);
		KI_gapit.setToolTipText("<html>" + "The kinship matrix will be calculated within GAPIT <br>" + "</html>");
		KI_gapit.setSelected(true);
		KI_group.add(KI_user);
		KI_user.setToolTipText("<html>" + "The kinship matrix can be input by users <br>" + "</html>");
		panel_ki.add(KI_gapit, "wrap");
		KI_gapit.isSelected();
		panel_ki.add(KI_user);
		panel_ki.add(Prediction, "wrap");
		Prediction.setToolTipText("Genomic prediction can be performed without running GWAS");
		Prediction.setEnabled(false);
		panel_ki.add(KI_path);
		KI_path.setEnabled(false);
		panel_ki.add(KI_browse);
		KI_browse.setEnabled(false);
		panel_ki.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Kinship", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_co = new JPanel(new MigLayout("fillx"));
		CO_group.add(CO_gapit);
		CO_gapit.setToolTipText("The covariates (e.g., PCs) can be calculated within GAPIT");
		CO_gapit.setSelected(true);
		CO_group.add(CO_user);
		CO_user.setToolTipText("The covariates (e.g., PCs) can be input by users");
		panel_co.add(CO_gapit, "wrap");
		panel_co.add(PCA_total_text);
		PCA_total_text.setToolTipText("Total Number of PCs as Covariates");
		panel_co.add(PCA_total_input, "wrap");
		PCA_total_input.setText("3");
		panel_co.add(CO_user, "wrap");
		panel_co.add(CO_path);
		CO_path.setEnabled(false);
		panel_co.add(CO_browse);
		CO_browse.setEnabled(false);
		panel_co.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_CMLM = new JPanel(new MigLayout("fillx"));
		panel_CMLM.add(CMLM_enable, "wrap");
		CMLM_enable.setSelected(false);
		CMLM_enable.setToolTipText("Users can specify additional clustering algorithms and kinship summary statistic");
		panel_CMLM.add(kinship_cluster_text_s);
		kinship_cluster_text_s.setToolTipText("Clustering algorithm to group individuals based on their kinship");
		panel_CMLM.add(kinship_cluster_input_s, "wrap");
		kinship_cluster_input_s.setEnabled(false);
		panel_CMLM.add(kinship_group_text_s);
		kinship_group_text_s.setToolTipText("Method to derive kinship among groups");
		panel_CMLM.add(kinship_group_input_s, "wrap");
		kinship_group_input_s.setEnabled(false);
		panel_CMLM.add(group_from_text_s);
		group_from_text_s.setToolTipText("The Starting Number of Groups of Compression");
		panel_CMLM.add(group_from_input_s, "wrap");
		group_from_input_s.setEnabled(false);
		panel_CMLM.add(group_to_text_s);
		group_to_text_s.setToolTipText("The Ending Number of Groups of Compression");
		panel_CMLM.add(group_to_input_s, "wrap");
		group_to_input_s.setEnabled(false);
		panel_CMLM.add(group_by_text_s);
		group_by_text_s.setToolTipText("The Grouping Interval of Compression");
		panel_CMLM.add(group_by_input_s, "wrap");		
		group_by_input_s.setEnabled(false);
		panel_CMLM.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "CMLM", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_advance = new JPanel(new MigLayout("fillx"));
		panel_advance.add(SNP_fraction_text_s);
		SNP_fraction_text_s.setToolTipText("<html> The computations of kinship and PCs are extensive with large number of SNPs. <br>"
				+ "Sampling a fraction of it would reduce computing time. <br>"
				+ "The valid value sould be greater than 0 and no greater than 1 </html>");
		panel_advance.add(SNP_fraction_input_s, "wrap");
		SNP_fraction_input_s.setText("1");
		panel_advance.add(file_fragment_text_s);
		file_fragment_text_s.setToolTipText("<html> With large amount of individuals, <br>"
				+ "loading a entire large genotype dataset could be difficult. <br>"
				+ "GAPIT can load a fragment of it each time. <br>"
				+ "The default of the fragment size is 512 SNPs </html>");
		panel_advance.add(file_fragment_input_s, "wrap");
		file_fragment_input_s.setSelectedItem("512");
		panel_advance.add(model_selection_s);
		model_selection_s.setToolTipText("<html> GAPIT has the capability to conduct BIC-based model selection <br>"
				+ "to find the optimal number of PCs for inclusion in the GWAS models. </html>");
		model_selection_s.setSelected(false);
		panel_advance.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Advance", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel = new JPanel(new MigLayout("fillx", "[grow][grow]"));
		main_panel.add(go_2, "dock north");
		main_panel.add(wd_panel, "cell 0 0, grow");
		main_panel.add(panel_genotype, "cell 0 1, grow");
		main_panel.add(panel_phenotype, "cell 0 2, grow");
		main_panel.add(panel_ki, "cell 0 3, grow");
		main_panel.add(panel_co, "cell 0 4, grow");
		main_panel.add(panel_CMLM, "cell 0 5, grow");
		main_panel.add(panel_advance, "cell 0 6, grow");
		///////////////////////////////////////////////////////////////////////////////////////
		JScrollPane pane = new JScrollPane(main_panel,  
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive
		
		go_2.addActionListener(this);
		wd_browse_s.addActionListener(this);
		
		KI_gapit.addActionListener(this);
		KI_user.addActionListener(this);
		KI_browse.addActionListener(this);
		
		CO_gapit.addActionListener(this);
		CO_user.addActionListener(this);
		CO_browse.addActionListener(this);
		
		CMLM_enable.addActionListener(this);
		return pane;

	}
	
	public void config_three(){
	
	}
	
	@Override
	public void actionPerformed(ActionEvent ip){
	      Object source = ip.getSource();	
	      if (source == go){
	    	  	save();
	    	  	myPanel.MOfile[MOindex] = wd_input.getText();
	    	  	myPanel.gapit_run = new Thread(back_run);
	    	  	myPanel.gapit_run.start();
	    	  	this.dispose();
	      }else if(source == browse){
	    	  	chooser = new iPat_chooser();
	    	  	wd_input.setText(chooser.getPath());	    	  	
	      // Config_2
	      }else if(source == go_2){
	    	  	save();
	    	  	myPanel.MOfile[MOindex] = wd_input.getText();
	    	  	myPanel.gapit_run = new Thread(back_run_2);
	    	  	myPanel.gapit_run.start();
	    	  	this.dispose();
	      }else if(source == wd_browse_s){
	    	  	chooser = new iPat_chooser();
	    	  	workingdir_input_s.setText(chooser.getPath());
	      }else if(source == KI_gapit){
	    	  	Prediction.setEnabled(false);
	    	  	KI_path.setEnabled(false);
	    	  	KI_browse.setEnabled(false);	    	  
	      }else if(source == KI_user){
	    	  	Prediction.setEnabled(true);
	    	  	KI_path.setEnabled(true);
	    	  	KI_browse.setEnabled(true);
	      }else if(source == KI_browse){
	    	  	JFileChooser KI_chooser = new JFileChooser();
				int value = KI_chooser.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION){
				    File selectedfile = KI_chooser.getSelectedFile();  	    					    
				  	KI_path.setText(selectedfile.getAbsolutePath());
				}
	      }else if(source == CO_gapit){
	    	  	PCA_total_text.setEnabled(true);
	    	  	PCA_total_input.setEnabled(true);
				CO_path.setEnabled(false);
				CO_browse.setEnabled(false); 
	      }else if(source == CO_user){
	    	  	PCA_total_text.setEnabled(false);
	    	  	PCA_total_input.setEnabled(false);
				CO_path.setEnabled(true);
				CO_browse.setEnabled(true); 
	      }else if(source == CO_browse){
	    	  	JFileChooser CO_chooser = new JFileChooser();
				int value = CO_chooser.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION){
				    File selectedfile = CO_chooser.getSelectedFile();  	    					    
				  	CO_path.setText(selectedfile.getAbsolutePath());
				}
	      }else if(source == CMLM_enable){
	    	  if(CMLM_open){
	    			kinship_cluster_input_s.setEnabled(false);
	    			kinship_group_input_s.setEnabled(false);
	    			group_from_input_s.setEnabled(false);
	    			group_to_input_s.setEnabled(false);
	    			group_by_input_s.setEnabled(false);
	    			CMLM_open = false;
	    	  }else{
	    		  	kinship_cluster_input_s.setEnabled(true);
	    			kinship_group_input_s.setEnabled(true);
	    			group_from_input_s.setEnabled(true);
	    			group_to_input_s.setEnabled(true);
	    			group_by_input_s.setEnabled(true);
	    		  	CMLM_open = true;
	    	  }
	      }
	}
	
	void GAPIT_two(int MOindex) throws FileNotFoundException{
		Boolean predict = false;
		String model_selection_string = "";
		String 	G = "", P = "", K = "", C = "",
				CM = "";	
		for(int i=0;i<5;i++){
			if(file_index[i][1] == 1){
				G = myPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 0){
				P = myPanel.TBfile[file_index[i][0]];
			}
		}	
		if(KI_user.isSelected() && Prediction.isSelected()){
			K = "KI = read.table('"+ KI_path.getText() +"', head = FALSE), SNP.test = FALSE";
		}else if(KI_user.isSelected()){
			K = "G=read.csv('"+G+"', head = FALSE, sep='\t'),"
				+ "KI = read.table('"+ KI_path.getText() +"', head = FALSE)";
		}else if(!KI_user.isSelected()){
			K = "G=read.csv('"+G+"', head = FALSE, sep='\t')";
		}
		if(CO_user.isSelected()){
			C = "CV = read.table('"+ CO_path.getText() +"', head = TRUE";
		}else{
			C = "PCA.total = " + PCA_total_input_s.getText();
		}
		if(CMLM_enable.isSelected()){
			CM =  "kinship.cluster = c("+kinship_cluster_input_s.getSelectedItem()+"),"
				+ "kinship.group = c("+kinship_cluster_input_s.getSelectedItem()+"),"
				+ "group.from = " + group_from_input_s.getText() + ","
				+ "group.to = " + group_to_input_s.getText() + "," 
				+ "group.by = " + group_by_input_s.getText() + ",";
		}
		if(model_selection_s.isSelected()){
			model_selection_string = "TRUE";
		}else{
			model_selection_string = "FALSE";
		}
		myPanel.permit[MOindex] = true;
		myPanel.rotate_index[MOindex] = 1;
		
	
		
		/*	
		r.eval("catch= tryCatch( {"
				+ "myGAPIT <- GAPIT("
				+ "Y=read.table('"+P+"', head = TRUE),"
				+ K + "," + C + "," + CM
				+ "SNP.fraction = " + SNP_fraction_input_s.getText() + ","
				+ "file.fragment = " + file_fragment_input_s.getSelectedItem() + ","
				+ "Model.selection = " + model_selection_string + ")},"
		 		+"error=function(e){e} )");
		
	    REXP rcatch= r.eval("as.character(catch)");
	    String rcatchs=((REXP)rcatch).asString();	    
	    try(  PrintWriter out = new PrintWriter( "error.txt" )  ){
	        out.println( rcatchs );
		    out.close();
	    }
	    if(rcatchs.indexOf("Error")>=0){
			myPanel.MO[MOindex] = myPanel.MO_fal;
	    }else{
			myPanel.MO[MOindex] = myPanel.MO_suc;
	    }*/
		myPanel.permit[MOindex] = false;
		myPanel.rotate_index[MOindex] = 0;
		myPanel.MOimageH[MOindex]=myPanel.MO[MOindex].getHeight(null);
		myPanel.MOimageW[MOindex]=myPanel.MO[MOindex].getWidth(null);
		myPanel.MOname[MOindex].setLocation(myPanel.MOimageX[MOindex], myPanel.MOimageY[MOindex]+ myPanel.MOimageH[MOindex]);
		System.out.println("done");	
	}
		
	public static String[] read_10_lines(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String[] lines = new String[10];
        String line;
        int nLine = 0;
        while((line = reader.readLine()) != null) {
            lines[nLine++] = line;
            if(nLine >= 10) {
                break;
            }
        }
        return lines;
    }
	
	void remove(){
		pref.remove("wd");
		
		pref.remove("emma_esp");
		pref.remove("emma_llim");
		pref.remove("emma_ngrid");
		pref.remove("emma_ulim");
		
		pref.remove("farm_acc");
		pref.remove("farm_con");
		pref.remove("farm_max");

		pref.remove("file_ext_g");
		pref.remove("file_ext_gd");
		pref.remove("file_ext_gm");
		pref.remove("file_fragment");
		pref.remove("file_from");
		pref.remove("file_to");
		pref.remove("file_total");
		pref.remove("file_g");
		pref.remove("file_gd");
		pref.remove("file_gm");
		pref.remove("file_out");
		pref.remove("file_path");
		
		pref.remove("group_by");
		pref.remove("group_from");
		pref.remove("group_to");
		
		pref.remove("kinship_algorithm");
		pref.remove("kinship_cluster");
		pref.remove("kinship_group");

		pref.remove("ld_chromosome");
		pref.remove("ld_location");
		pref.remove("ld_range");

		pref.remove("method_iteration");
		pref.remove("method_bin");
		pref.remove("method_GLM");
		pref.remove("method_sub");
		pref.remove("method_sub_final");

		pref.remove("model_selection");

		pref.remove("output_cutoff");
		pref.remove("output_cv");
		pref.remove("output_DPP");
		pref.remove("output_geno");
		pref.remove("output_iteration");
		pref.remove("output_maxout");
		pref.remove("output_hapmap");
		pref.remove("output_numeric");
		pref.remove("output_plot");
		pref.remove("output_threshold");

		pref.remove("pca_total");
		pref.remove("pca_view");

		pref.remove("qtn_prior");
		pref.remove("qtn");
		pref.remove("qtn_limit");
		pref.remove("qtn_method");
		pref.remove("qtn_position");
		pref.remove("qtn_round");
		pref.remove("qtn_update");

		pref.remove("snp_create");
		pref.remove("snp_major");
		pref.remove("snp_cv");
		pref.remove("snp_effect");
		pref.remove("snp_fdr");
		pref.remove("snp_fraction");
		pref.remove("snp_impute");
		pref.remove("snp_maf");
		pref.remove("snp_p3d");
		pref.remove("snp_permuation");
		pref.remove("snp_robust");
		pref.remove("snp_test");
		
		pref.remove("super_bin_by");
		pref.remove("super_bin_from");
		pref.remove("super_bin_to");
		pref.remove("super_bin_selection");
		pref.remove("super_bin_size");
		pref.remove("super_bin");
		pref.remove("super_fdr");
		pref.remove("super_gt");
		pref.remove("super_inclosure_by");
		pref.remove("super_inclosure_from");
		pref.remove("super_inclosure_to");
		pref.remove("super_ld");
		pref.remove("super_bottom");
		pref.remove("super_top");
		pref.remove("super_gd");
		pref.remove("super_gs");
	}
	
	void load(){
		wd_input.setText(pref.get("wd", " "));
		workingdir_input_s.setText(pref.get("wd", "~/"));
		n_input.setText(myPanel.MOname[MOindex].getText());
		project_input_s.setText(myPanel.MOname[MOindex].getText());
		
		esp_input.setText(pref.get("emma_esp", "1.00E-10"));
		llim_input.setText(pref.get("emma_llim", "-10"));
		ngrid_input.setText(pref.get("emma_ngrid", "100"));
		ulim_input.setText(pref.get("emma_ulim", "10"));
		
		acceleration_input.setText(pref.get("farm_acc", "0"));
		converge_input.setText(pref.get("farm_con", "1"));
		maxLoop_input.setText(pref.get("farm_max", "3"));
		
		file_Ext_G_input.setText(pref.get("file_ext_g", "NULL"));
		file_Ext_GD_input.setText(pref.get("file_ext_gd", "NULL"));
		file_Ext_GM_input.setText(pref.get("file_ext_gm", "NULL"));
		file_fragment_input.setSelectedItem(pref.get("file_fragment", "512"));
		file_from_input.setText(pref.get("file_from", "1"));
		file_to_input.setText(pref.get("file_to", "1"));
		file_total_input.setText(pref.get("file_total", "NULL"));
		file_G_input.setText(pref.get("file_g", "NULL"));
		file_GD_input.setText(pref.get("file_gd", "NULL"));
		file_GM_input.setText(pref.get("file_gm", "NULL"));
		file_output.setSelected(pref.getBoolean("file_out", true));
		file_path_input.setText(pref.get("file_path", "NULL"));
		
		group_by_input.setText(pref.get("group_by", "10"));
		group_from_input.setText(pref.get("group_from", "30"));
		group_to_input.setText(pref.get("group_to", "1000000"));
		
		kinship_algorithm_input.setSelectedItem(pref.get("kinship_algorithm", "VanRaden"));
		kinship_cluster_input.setSelectedItem(pref.get("kinship_cluster", "average"));
		kinship_group_input.setSelectedItem(pref.get("kinship_group", "Mean"));

		LD_chromosome_input.setText(pref.get("ld_chromosome", "NULL"));
		LD_location_input.setText(pref.get("ld_location", "NULL"));
		LD_range_input.setText(pref.get("ld_range", "NULL"));
		
		method_iteration_input.setSelectedItem(pref.get("method_iteration", "accum"));
		method_bin_input.setSelectedItem(pref.get("method_bin", "static"));
		method_GLM_input.setSelectedItem(pref.get("method_GLM", "fast.lm"));
		method_sub_input.setSelectedItem(pref.get("method_sub", "reward"));
		method_sub_final_input.setSelectedItem(pref.get("method_sub_final", "reward"));
		
		model_selection.setSelected(pref.getBoolean("model_selection", false));
		
		output_cutOff_input.setText(pref.get("output_cutoff", "0.01"));
		output_CV_Inheritance_input.setText(pref.get("output_cv", "NULL"));
		output_DPP_input.setText(pref.get("output_DPP", "100000"));
		output_Geno_View_output.setSelected(pref.getBoolean("output_geno", true));
		output_iteration_output.setSelected(pref.getBoolean("output_iteration", false));
		output_maxOut_input.setText(pref.get("output_maxout", "100"));
		output_hapmap.setSelected(pref.getBoolean("output_hapmap", false));
		output_numerical.setSelected(pref.getBoolean("output_numeric",  false));
		output_plot_style_input.setSelectedItem(pref.get("output_plot", "Oceanic"));
		output_threshold_input.setText(pref.get("output_threshold", "0.01"));
		
		PCA_total_input.setText(pref.get("pca_total", "3"));
		PCA_View_input.setSelected(pref.getBoolean("pca_view", true));
		
		QTN_prior_input.setText(pref.get("qtn_prior", "NULL"));
		QTN_input.setText(pref.get("qtn", "NULL"));
		QTN_limit_input.setText(pref.get("qtn_limit", "0"));
		QTN_method_input.setSelectedItem(pref.get("qtn_method", "Penalty"));
		QTN_position_input.setText(pref.get("qtn_position", "NULL"));
		QTN_round_input.setText(pref.get("qtn_round", "1"));
		QTN_update.setSelected(pref.getBoolean("qtn_update", true));
		
		SNP_create_indicater.setSelected(pref.getBoolean("snp_create",  false));
		SNP_major_allele_zero.setSelected(pref.getBoolean("snp_major", false));
		SNP_CV_input.setText(pref.get("snp_cv", "NULL"));
		SNP_effect_input.setSelectedItem(pref.get("snp_effect", "Add"));
		SNP_FDR_input.setText(pref.get("snp_fdr", "1"));
		SNP_fraction_input.setText(pref.get("snp_fraction", "1"));
		SNP_impute_input.setSelectedItem(pref.get("snp_impute", "Middle"));
		SNP_MAF_input.setText(pref.get("snp_maf", "0"));
		SNP_P3D.setSelected(pref.getBoolean("snp_p3d", true));
		SNP_permutation.setSelected(pref.getBoolean("snp_permuation", false));
		SNP_robust_input.setSelectedItem(pref.get("snp_robust", "GLM"));
		SNP_test.setSelected(pref.getBoolean("snp_test", true));
		
		SUPER_bin_by_input.setText(pref.get("super_bin_by", "10000"));
		SUPER_bin_from_input.setText(pref.get("super_bin_from", "10000"));
		SUPER_bin_to_input.setText(pref.get("super_bin_to", "10000"));
		SUPER_bin_selection_input.setText(pref.get("super_bin_selection", "c(10,20,50,100,200,500,1000)"));
		SUPER_bin_size_input.setText(pref.get("super_bin_size" ,"c(1000000)"));
		SUPER_BINS_input.setText(pref.get("super_bin", "20"));
		SUPER_FDR_rate_input.setText(pref.get("super_fdr", "1"));
		SUPER_GT_index_input.setText(pref.get("super_gt", "NULL"));
		SUPER_inclosure_by_input.setText(pref.get("super_inclosure_by", "10"));
		SUPER_inclosure_from_input.setText(pref.get("super_inclosure_from", "10"));
		SUPER_inclosure_to_input.setText(pref.get("super_inclosure_to", "10"));
		SUPER_LD_input.setText(pref.get("super_ld", "0.1"));
		SUPER_sanawich_bottom_input.setText(pref.get("super_bottom", "NULL"));
		SUPER_sanawich_top_input.setText(pref.get("super_top", "NULL"));
		SUPER_GD_input.setText(pref.get("super_gd", "NULL"));
		SUPER_GS.setSelected(pref.getBoolean("super_gs", false));
	}
	
	void save(){
		pref.put("wd",  wd_input.getText());
		myPanel.MOname[MOindex].setText(n_input.getText());
		
		pref.put("emma_esp", esp_input.getText());
		pref.put("emma_llim", llim_input.getText());
		pref.put("emma_ngrid", ngrid_input.getText());
		pref.put("emma_ulim", ulim_input.getText());
		
		pref.put("farm_acc", acceleration_input.getText());
		pref.put("farm_con", converge_input.getText());
		pref.put("farm_max", maxLoop_input.getText());

		pref.put("file_ext_g", file_Ext_G_input.getText());
		pref.put("file_ext_gd", file_Ext_GD_input.getText());
		pref.put("file_ext_gm", file_Ext_GM_input.getText());
		pref.put("file_fragment", (String) file_fragment_input.getSelectedItem());
		pref.put("file_from", file_from_input.getText());
		pref.put("file_to", file_to_input.getText());
		pref.put("file_total", file_total_input.getText());
		pref.put("file_g", file_G_input.getText());
		pref.put("file_gd", file_GD_input.getText());
		pref.put("file_gm", file_GM_input.getText());
		pref.putBoolean("file_out", file_output.isSelected());
		pref.put("file_path", file_path_input.getText());
		
		pref.put("group_by", group_by_input.getText());
		pref.put("group_from", group_from_input.getText());
		pref.put("group_to", group_to_input.getText());
		
		pref.put("kinship_algorithm", (String) kinship_algorithm_input.getSelectedItem());
		pref.put("kinship_cluster", (String) kinship_cluster_input.getSelectedItem());
		pref.put("kinship_group", (String) kinship_group_input.getSelectedItem());

		pref.put("ld_chromosome", LD_chromosome_input.getText());
		pref.put("ld_location", LD_location_input.getText());
		pref.put("ld_range", LD_range_input.getText());

		pref.put("method_iteration", (String) method_iteration_input.getSelectedItem());
		pref.put("method_bin", (String) method_bin_input.getSelectedItem());
		pref.put("method_GLM", (String) method_GLM_input.getSelectedItem());
		pref.put("method_sub", (String) method_sub_input.getSelectedItem());
		pref.put("method_sub_final", (String) method_sub_final_input.getSelectedItem());

		pref.putBoolean("model_selection", model_selection.isSelected());

		pref.put("output_cutoff", output_cutOff_input.getText());
		pref.put("output_cv", output_CV_Inheritance_input.getText());
		pref.put("output_DPP", output_DPP_input.getText());
		pref.putBoolean("output_geno", output_Geno_View_output.isSelected());
		pref.putBoolean("output_iteration", output_iteration_output.isSelected());
		pref.put("output_maxout", output_maxOut_input.getText());
		pref.putBoolean("output_hapmap", output_hapmap.isSelected());
		pref.putBoolean("output_numeric", output_numerical.isSelected());
		pref.put("output_plot", (String) output_plot_style_input.getSelectedItem());
		pref.put("output_threshold", output_threshold_input.getText());

		pref.put("pca_total", PCA_total_input.getText());
		pref.putBoolean("pca_view", PCA_View_input.isSelected());

		pref.put("qtn_prior", QTN_prior_input.getText());
		pref.put("qtn", QTN_input.getText());
		pref.put("qtn_limit", QTN_limit_input.getText());
		pref.put("qtn_method", (String) QTN_method_input.getSelectedItem());
		pref.put("qtn_position", QTN_position_input.getText());
		pref.put("qtn_round", QTN_round_input.getText());
		pref.putBoolean("qtn_update", QTN_update.isSelected());

		pref.putBoolean("snp_create", SNP_create_indicater.isSelected());
		pref.putBoolean("snp_major", SNP_major_allele_zero.isSelected());
		pref.put("snp_cv", SNP_CV_input.getText());
		pref.put("snp_effect", (String) SNP_effect_input.getSelectedItem());
		pref.put("snp_fdr", SNP_FDR_input.getText());
		pref.put("snp_fraction", SNP_fraction_input.getText());
		pref.put("snp_impute", (String) SNP_impute_input.getSelectedItem());
		pref.put("snp_maf", SNP_MAF_input.getText());
		pref.putBoolean("snp_p3d", SNP_P3D.isSelected());
		pref.putBoolean("snp_permuation", SNP_permutation.isSelected());
		pref.put("snp_robust", (String) SNP_robust_input.getSelectedItem());
		pref.putBoolean("snp_test", SNP_test.isSelected());
		
		pref.put("super_bin_by", SUPER_bin_by_input.getText());
		pref.put("super_bin_from", SUPER_bin_from_input.getText());
		pref.put("super_bin_to", SUPER_bin_to_input.getText());
		pref.put("super_bin_selection", SUPER_bin_selection_input.getText());
		pref.put("super_bin_size", SUPER_bin_size_input.getText());
		pref.put("super_bin", SUPER_BINS_input.getText());
		pref.put("super_fdr", SUPER_FDR_rate_input.getText());
		pref.put("super_gt", SUPER_GT_index_input.getText());
		pref.put("super_inclosure_by", SUPER_inclosure_by_input.getText());
		pref.put("super_inclosure_from", SUPER_inclosure_from_input.getText());
		pref.put("super_inclosure_to", SUPER_inclosure_to_input.getText());
		pref.put("super_ld", SUPER_LD_input.getText());
		pref.put("super_bottom", SUPER_sanawich_bottom_input.getText());
		pref.put("super_top", SUPER_sanawich_top_input.getText());
		pref.put("super_gd", SUPER_GD_input.getText());
		pref.putBoolean("super_gs", SUPER_GS.isSelected());
	}
	
	

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("close");	
		save();		
	}
	@Override
	public void windowOpened(WindowEvent e) {}
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
}

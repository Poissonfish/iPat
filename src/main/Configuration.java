package main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;

import main.myPanel.CustomOutputStream;
import net.miginfocom.swing.MigLayout;


public class Configuration extends JFrame implements ActionListener, WindowListener{
	Preferences pref;
	static Runtime[] runtime = new Runtime[myPanel.MOMAX];
	static Process[] process = new Process[myPanel.MOMAX];
	PrintStream printStream;
	///////////////////////////////////////////////////////////////////////////////////////
	//Config gapit
	JPanel main_panel;
	JButton go_gapit = new JButton("GO");
	
	JPanel wd_panel;
	Group_Value Project_g = new Group_Value("Task name");
	Group_Path WD_g = new Group_Path("Output Directory");

	JPanel panel_phenotype;
	JLabel P_filename = new JLabel("File:\tNA");
	JPanel panel_genotype;
	JLabel G_filename = new JLabel("");
	JLabel G_filename2 = new JLabel("");
	JLabel G_format = new JLabel("");
	
	JPanel panel_ki;
	Group_RadioButton Kinship = new Group_RadioButton(2);
	Group_Path KI_path = new Group_Path("");
	JCheckBox Prediction = new JCheckBox("GP only");
	
	JPanel panel_co;
	Group_RadioButton Covariate_g = new Group_RadioButton(2);
	Group_Value PCA = new Group_Value("PCA.total");
	Group_Path CO_path_g = new Group_Path("");
	
	JPanel panel_CMLM;
	Group_Combo K_cluster = new Group_Combo("Cluster", 
			new String[]{"average", "complete", "ward", "single", "mcquitty", "median", "centroid"});
	Group_Combo K_group = new Group_Combo("Group", 
			new String[]{"Mean", "Max", "Min", "Median"});
	Group_Combo model_select = new Group_Combo("Select a model",
			new String[]{"GLM", "MLM", "CMLM"});
	
	JPanel panel_advance;
	Group_Combo snp_frac = new Group_Combo("SNP fraction",
			new String[]{"1", "0.8", "0.5", "0.3", "0.1"});
	Group_Combo file_frag = new Group_Combo("File fragment",
			new String[]{"512", "256", "128", "64"});
	JCheckBox model_selection_s = new JCheckBox("Model selection");
	///////////////////////////////////////////////////////////////////////////////////////
	//Config farm
	JPanel main_panel_farm;
	JButton go_farm= new JButton("GO");

	JPanel wd_panel_farm;
	Group_Value Project_f = new Group_Value("Task name");
	Group_Path WD_f = new Group_Path("Output Directory");

	JPanel panel_phenotype_farm;
	JLabel P_filename_farm = new JLabel("File:\tNA");
	JPanel panel_genotype_farm;
	JLabel G_filename_farm = new JLabel("");
	JLabel G_filename2_farm = new JLabel("");
	JLabel G_format_farm = new JLabel("");
	
	JPanel panel_co_farm;
	Group_RadioButton Covariate_f = new Group_RadioButton(2);
	Group_Path CO_path_f = new Group_Path("");

	JPanel panel_adv_farm;
	Group_Combo method_bin = new Group_Combo("Method bin", 
			new String[]{"static", "optimum"});
	Group_Value maxloop = new Group_Value("Max Loop");
	
	Group_Combo maf = new Group_Combo("MAF threshold",
			new String[]{"No threshold", "5%", "10%", "20%"});
	Boolean MAF_open = false;
	///////////////////////////////////////////////////////////////////////////////////////	
	
	///////////////////////////////////////////////////////////////////////////////////////	
	Runnable back_run_gapit = new Runnable(){
		@Override
		public void run(){
			try {
		        // Construct panel
				showConsole(Project_g.longfield.getText());	            
				run_GAPIT(myPanel.file_index);
			} catch (FileNotFoundException e) {e.printStackTrace();}}
	};	
	Runnable back_run_farm = new Runnable(){
		@Override
		public void run(){
			try {
		        // Construct panel
				showConsole(Project_f.longfield.getText());
				run_Farm(myPanel.file_index);
			} catch (FileNotFoundException e) {e.printStackTrace();}}
	};
	int test_run = 0;
	///////////////////////////////////////////////////////////////////////////////////////
    String folder_path = new String();
	int  MOindex;
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	public Configuration(int MOindex, int file_count, int[][] file_index) throws FileNotFoundException, IOException{	
		this.MOindex = MOindex;
		String P_name = "", G_name = "", GD_name = "", GM_name = "";
		if(file_index[0][1]!=-1){ //input format supported
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
		}	
		JScrollPane pane_gapit = null;
		JScrollPane pane_farm = null;
		pref = Preferences.userRoot().node("/ipat"); 
		System.out.println("P:"+P_name+" GD:"+GD_name+" GM:"+GM_name+" G:"+G_name);
		System.out.println("files count:" + file_count);
		switch(file_count){
			case 1:
				break;
			case 2:
				pane_gapit = config_gapit(P_name, G_name, "0");
				pane_farm = config_farm(P_name, GD_name, "0");
				break;
			case 3:
				pane_gapit = config_gapit(P_name, GD_name, GM_name);
				pane_farm = config_farm(P_name, GD_name, GM_name);
				break;
		}		
        JTabbedPane mainPane = new JTabbedPane();
        mainPane.addTab("GAPIT", pane_gapit);
        mainPane.addTab("FarmCPU", pane_farm);
		this.setContentPane(mainPane);
		this.setTitle("Configuration");
		this.pack();
		this.show();	
		load();
		addWindowListener(this);
	}	

	public JScrollPane config_farm(String P_name, String G_name, String G2_name){
		go_farm.setFont(new Font("Ariashowpril", Font.BOLD, 40));		
		wd_panel_farm = new JPanel(new MigLayout("fillx"));
		wd_panel_farm.add(Project_f.name, "wrap");
		wd_panel_farm.add(Project_f.longfield, "wrap");
		wd_panel_farm.add(WD_f.name, "wrap");
		wd_panel_farm.add(WD_f.field);
		wd_panel_farm.add(WD_f.browse, "wrap");
		wd_panel_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Task", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_genotype_farm = new JPanel(new MigLayout("fillx"));
		G_filename_farm.setText("Genotype:\t"+G_name);
		G_filename2_farm.setText("Map:\t"+G2_name);
		G_format_farm.setText("Format: Numeric");
		
		panel_genotype_farm.add(G_filename_farm, "wrap");
		panel_genotype_farm.add(G_filename2_farm, "wrap");
		panel_genotype_farm.add(G_format_farm);
		panel_genotype_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Genotype", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
		///////////////////////////////////////////////////////////////////////////////////////
		panel_phenotype_farm = new JPanel(new MigLayout("fillx"));
		P_filename_farm.setText("File:\t"+P_name);
		
		panel_phenotype_farm.add(P_filename_farm);
		panel_phenotype_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Phenotype", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
		///////////////////////////////////////////////////////////////////////////////////////
		panel_co_farm = new JPanel(new MigLayout("fillx"));
		
		Covariate_f.setName(0, "Calculate within FarmCPU");
		Covariate_f.setName(1, "User input");
		Covariate_f.button[0].setToolTipText("The covariates (e.g., PCs) can be calculated within FarmCPU");
		Covariate_f.button[1].setToolTipText("The covariates (e.g., PCs) can be input by users");
		Covariate_f.button[0].setSelected(true);
		
		CO_path_f.field.setEnabled(false);
		CO_path_f.browse.setEnabled(false);
		
		panel_co_farm.add(Covariate_f.button[0], "wrap");
		panel_co_farm.add(Covariate_f.button[1], "wrap");
		panel_co_farm.add(CO_path_f.field);
		panel_co_farm.add(CO_path_f.browse);
		panel_co_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_adv_farm = new JPanel(new MigLayout("fillx"));
		
		method_bin.combo.setSelectedItem("static");
		
		maxloop.field.setText("10");
		maf.combo.setSelectedIndex(0);
		
		panel_adv_farm.add(method_bin.name);
		panel_adv_farm.add(method_bin.combo, "wrap");
		panel_adv_farm.add(maxloop.name);
		panel_adv_farm.add(maxloop.field, "wrap");
		panel_adv_farm.add(maf.name);
		panel_adv_farm.add(maf.combo);
		panel_adv_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Advance", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel_farm = new JPanel(new MigLayout("fillx", "[grow][grow]"));
		main_panel_farm.add(go_farm, "dock north");
		main_panel_farm.add(wd_panel_farm, "cell 0 0, grow");
		main_panel_farm.add(panel_genotype_farm, "cell 0 1, grow");
		main_panel_farm.add(panel_phenotype_farm, "cell 0 2, grow");
		main_panel_farm.add(panel_co_farm, "cell 0 3, grow");
		main_panel_farm.add(panel_adv_farm, "cell 0 4, grow");
		///////////////////////////////////////////////////////////////////////////////////////
		JScrollPane pane = new JScrollPane(main_panel_farm,  
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive
		go_farm.addActionListener(this);
		WD_f.browse.addActionListener(this);
		
		Covariate_f.button[0].addActionListener(this);
		Covariate_f.button[1].addActionListener(this);
		CO_path_f.browse.addActionListener(this);

		return pane;
	}
	
	public JScrollPane config_gapit(String P_name, String G_name, String G2_name){
		go_gapit.setFont(new Font("Ariashowpril", Font.BOLD, 40));		
		///////////////////////////////////////////////////////////////////////////////////////
		wd_panel = new JPanel(new MigLayout("fillx"));
		wd_panel.add(Project_g.name, "wrap");
		wd_panel.add(Project_g.longfield, "wrap");
		wd_panel.add(WD_g.name, "wrap");
		wd_panel.add(WD_g.field);
		wd_panel.add(WD_g.browse, "wrap");
		wd_panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Task", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_genotype = new JPanel(new MigLayout("fillx"));
		if(G2_name.equals("0")){
			panel_genotype.add(G_filename, "wrap");
			G_filename.setText("File:\t"+G_name);
			panel_genotype.add(G_format);
			G_format.setText("Format: HapMap");
		}else{
			panel_genotype.add(G_filename, "wrap");
			G_filename.setText("Genotype:\t"+G_name);
			panel_genotype.add(G_filename2, "wrap");
			G_filename2.setText("Map:\t"+G2_name);
			panel_genotype.add(G_format);
			G_format.setText("Format: Numeric");
		}
		panel_genotype.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Genotype", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
		///////////////////////////////////////////////////////////////////////////////////////
		panel_phenotype = new JPanel(new MigLayout("fillx"));
		panel_phenotype.add(P_filename);
		P_filename.setText("File:\t"+P_name);
		panel_phenotype.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Phenotype", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
	///////////////////////////////////////////////////////////////////////////////////////
		panel_ki = new JPanel(new MigLayout("fillx"));

		Kinship.setName(0, "Calculate within GAPIT");
		Kinship.setName(1, "User input");
		Kinship.button[0].setToolTipText("<html>" + "The kinship matrix will be calculated within GAPIT <br>" + "</html>");
		Kinship.button[0].setSelected(true);
		Kinship.button[1].setToolTipText("<html>" + "The kinship matrix can be input by users <br>" + "</html>");
		
		Prediction.setToolTipText("Genomic prediction can be performed without running GWAS");
		Prediction.setEnabled(false);

		KI_path.field.setEnabled(false);
		KI_path.browse.setEnabled(false);
		
		panel_ki.add(Kinship.button[0], "wrap");
		panel_ki.add(Kinship.button[1]);
		panel_ki.add(Prediction, "wrap");
		panel_ki.add(KI_path.field);
		panel_ki.add(KI_path.browse);
		panel_ki.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Kinship", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_co = new JPanel(new MigLayout("fillx"));
		
		Covariate_g.setName(0, "Calculate within GAPIT");
		Covariate_g.setName(1, "User input");	
		
		Covariate_g.button[0].setToolTipText("The Covariate_gs (e.g., PCs) can be calculated within GAPIT");
		Covariate_g.button[0].setSelected(true);
		Covariate_g.button[1].setToolTipText("The covariates (e.g., PCs) can be input by users");

		PCA.name.setToolTipText("Total Number of PCs as Covariates");

		CO_path_g.field.setEnabled(false);
		CO_path_g.browse.setEnabled(false);
		
		panel_co.add(Covariate_g.button[0], "wrap");
		panel_co.add(PCA.name);
		panel_co.add(PCA.field, "wrap");
		panel_co.add(Covariate_g.button[1], "wrap");
		panel_co.add(CO_path_g.field);
		panel_co.add(CO_path_g.browse);
		panel_co.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_CMLM = new JPanel(new MigLayout("fillx"));
		
		model_select.combo.setSelectedItem("CMLM");
		
		K_cluster.name.setToolTipText("Clustering algorithm to group individuals based on their kinship");
		K_group.name.setToolTipText("Method to derive kinship among groups");

		panel_CMLM.add(model_select.name);
		panel_CMLM.add(model_select.combo, "wrap");
		panel_CMLM.add(K_cluster.name);
		panel_CMLM.add(K_cluster.combo, "wrap");
		panel_CMLM.add(K_group.name);
		panel_CMLM.add(K_group.combo, "wrap");
	
		panel_CMLM.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Model", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_advance = new JPanel(new MigLayout("fillx"));
		
		snp_frac.name.setToolTipText("<html> The computations of kinship and PCs are extensive with large number of SNPs. <br>"
				+ "Sampling a fraction of it would reduce computing time. <br>"
				+ "The valid value sould be greater than 0 and no greater than 1 </html>");
		snp_frac.combo.setSelectedIndex(0);
		
		file_frag.name.setToolTipText("<html> With large amount of individuals, <br>"
				+ "loading a entire large genotype dataset could be difficult. <br>"
				+ "GAPIT can load a fragment of it each time. <br>"
				+ "The default of the fragment size is 512 SNPs </html>");
		file_frag.combo.setSelectedIndex(0);
		
		model_selection_s.setToolTipText("<html> GAPIT has the capability to conduct BIC-based model selection <br>"
				+ "to find the optimal number of PCs for inclusion in the GWAS models. </html>");
		model_selection_s.setSelected(false);
		
		panel_advance.add(snp_frac.name);
		panel_advance.add(snp_frac.combo, "wrap");
		panel_advance.add(file_frag.name);
		panel_advance.add(file_frag.combo, "wrap");
		panel_advance.add(model_selection_s);
		panel_advance.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Advance", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel = new JPanel(new MigLayout("fillx", "[grow][grow]"));
		main_panel.add(go_gapit, "dock north");
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
		pref = Preferences.userRoot().node("/ipat"); 
		go_gapit.addActionListener(this);
		WD_g.browse.addActionListener(this);
		
		Kinship.button[0].addActionListener(this);
		Kinship.button[1].addActionListener(this);
		KI_path.browse.addActionListener(this);
		
		Covariate_g.button[0].addActionListener(this);
		Covariate_g.button[1].addActionListener(this);
		CO_path_g.browse.addActionListener(this);
		
		return pane;
	}
	
	@Override
	public void actionPerformed(ActionEvent ip){
	      Object source = ip.getSource();	
	      if (source == go_gapit){
	    	  	save();
	    	  	myPanel.MOfile[MOindex] = WD_g.field.getText();
	    	  	myPanel.multi_run[MOindex] = new Thread(back_run_gapit);
	    	  	myPanel.multi_run[MOindex].start();
	    	  	this.dispose(); 	  	
	      }else if(source == go_farm){
	    	  	save();
	    	  	myPanel.MOfile[MOindex] = WD_f.field.getText();
	    	  	myPanel.multi_run[MOindex] = new Thread(back_run_farm);
	    	  	myPanel.multi_run[MOindex].start();
	    	  	this.dispose(); 
	      }else if(source == WD_g.browse){
	    	  	WD_g.setPath(true);
	      }else if(source == Kinship.button[0]){
	    	  	Prediction.setEnabled(false);
	    	  	KI_path.field.setEnabled(false);
	    	  	KI_path.browse.setEnabled(false);	    	  
	      }else if(source == Kinship.button[1]){
	    	  	Prediction.setEnabled(true);
	    	  	KI_path.field.setEnabled(true);
	    	  	KI_path.browse.setEnabled(true);
	      }else if(source == KI_path.browse){
	    	  	KI_path.setPath(false);  	
	      }else if(source == Covariate_g.button[0]){
	    	  	PCA.name.setEnabled(true);
	    	  	PCA.field.setEnabled(true);
				CO_path_g.field.setEnabled(false);
				CO_path_g.browse.setEnabled(false); 
	      }else if(source == Covariate_g.button[1]){
	    	  	PCA.name.setEnabled(false);
	    	  	PCA.field.setEnabled(false);
				CO_path_g.field.setEnabled(true);
				CO_path_g.browse.setEnabled(true); 
	      }else if(source == CO_path_g.browse){
	    	  	CO_path_g.setPath(false);
	     //Farm
	      }else if(source == WD_f.browse){
//	    	  	WD_f.setPath(true);
	    	    iPat_chooser chooser = new iPat_chooser();
	    	  	WD_f.field.setText(chooser.getPath());
	      }else if(source == Covariate_f.button[0]){
				CO_path_f.field.setEnabled(false);
				CO_path_f.browse.setEnabled(false); 
	      }else if(source == Covariate_g.button[1]){
	    	  	CO_path_f.field.setEnabled(true);
				CO_path_f.browse.setEnabled(true); 
	      }else if(source == CO_path_f.browse){
	    	  	CO_path_f.setPath(false);
	      }
	}
	
	void run_Farm(int[][] file_index) throws FileNotFoundException{
		String 	P = "", GD = "NULL", GM = "NULL", C = "", WD = "", Project_name = "",	
				method_b = "", maxloop_run = "", maf_cal = "", maf_threshold = "";		
		for(int i=0;i<5;i++){
			if(file_index[i][1] == 0){
				P = myPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 2){
				GD = myPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 3){
				GM = myPanel.TBfile[file_index[i][0]];
			}
		}			
		if(Covariate_f.button[1].isSelected()){
			C = CO_path_f.field.getText();
		}else{
			C = "NULL";
		}
		method_b = (String) method_bin.combo.getSelectedItem();
		maxloop_run = maxloop.field.getText();
		
		int maf_value = maf.combo.getSelectedIndex();
		switch(maf_value){
			case 0:
				maf_cal = "FALSE";
			case 1:
				maf_cal = "TRUE";
				maf_threshold = "0.05";
			case 2:
				maf_cal = "TRUE";
				maf_threshold = "0.1";
			case 3:
				maf_cal = "TRUE";
				maf_threshold = "0.2";
		}
		
		WD = WD_f.field.getText();
		Project_name = Project_f.longfield.getText();
		
        System.out.println("running FarmCPU");  
        // Command input
        String[] command = {" ", myPanel.jar.getParent()+"/libs/FarmCPU.R",
        		GM, GD, P, C, 
        		method_b, maxloop_run, maf_cal, maf_threshold, WD}; 
        String[] R_Path = {"/usr/local/bin/Rscript", "/usr/bin/Rsciprt", "/usr/Rscript"};
        run_command(MOindex, command, R_Path, WD, Project_name);
	}
	
	void run_GAPIT(int[][] file_index) throws FileNotFoundException{
		String model_selection_string = "";
		String 	G = "NULL", P = "", GD = "NULL", GM = "NULL", K = "", C = "",
				SNP_test = "", PCA = "",
				ki_c = "", ki_g = "", 
				g_from = "", g_to = "", g_by = "", 
				SNP_fraction = "", file_fragment = "", WD = "", Project_name = "";
		for(int i=0;i<5;i++){
			if(file_index[i][1] == 1){
				G = myPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 0){
				P = myPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 2){
				GD = myPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 3){
				GM = myPanel.TBfile[file_index[i][0]];
			}
		}	
		if(Kinship.button[1].isSelected() && Prediction.isSelected()){
			K = KI_path.field.getText();
			SNP_test = "FALSE";
			G = "NULL";
		}else if(Kinship.button[1].isSelected()){
			K = KI_path.field.getText();
			SNP_test = "TRUE";
		}else if(!Kinship.button[1].isSelected()){
			K = "NULL";
			SNP_test = "TRUE";
		}
		if(Covariate_g.button[1].isSelected()){
			C = CO_path_g.field.getText();
			PCA = "0";
		}else{
			C = "NULL";
			PCA =  "3";
		}
		String model_selected = (String)model_select.combo.getSelectedItem();
		if(model_selected.equals("GLM")){
			ki_c = (String) K_cluster.combo.getSelectedItem();
			ki_g = (String) K_group.combo.getSelectedItem();
			g_from = "1";
			g_to = "1";
			g_by = "10";
		}else if (model_selected.equals("CMLM")){
			ki_c = (String) K_cluster.combo.getSelectedItem();
			ki_g = (String) K_group.combo.getSelectedItem();
			g_from = "1";
			g_to = "10000000";
			g_by = "10";
		}else if (model_selected.equals("MLM")){
			ki_c = (String) K_cluster.combo.getSelectedItem();
			ki_g = (String) K_group.combo.getSelectedItem();
			g_from = "10000000";
			g_to = "10000000";
			g_by = "10";
		}
				
		if(model_selection_s.isSelected()){
			model_selection_string = "TRUE";
		}else{
			model_selection_string = "FALSE";
		}
		SNP_fraction = (String) snp_frac.combo.getSelectedItem();
		file_fragment = (String) file_frag.combo.getSelectedItem();
		WD = WD_g.field.getText();
		Project_name = Project_g.field.getText();
		
        System.out.println("running gapit");        
        // Command input
        String[] command = {" ", myPanel.jar.getParent()+"/libs/Gapit.R",
        		G, GM, GD, P, K, SNP_test, C, PCA, 
        		ki_c, ki_g, g_from, g_to, g_by, 
        		model_selection_string, SNP_fraction, file_fragment, WD};  
        String[] R_Path = {"/usr/local/bin/Rscript", "/usr/bin/Rsciprt", "/usr/Rscript"};
        run_command(MOindex, command, R_Path, WD, Project_name);      
	}
	
	public void showConsole(String title){
		myPanel.MOname[MOindex].setText(title);
		myPanel.text_console[MOindex] = new JTextArea();
        myPanel.text_console[MOindex].setEditable(false);
        myPanel.scroll_console[MOindex] = new JScrollPane(myPanel.text_console[MOindex] ,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        myPanel.frame_console[MOindex]  = new JFrame();
        myPanel.frame_console[MOindex].setContentPane(myPanel.scroll_console[MOindex]);
        myPanel.frame_console[MOindex].setTitle(title);
        myPanel.frame_console[MOindex].setSize(700,350);
        myPanel.frame_console[MOindex].setVisible(true); 
        myPanel.frame_console[MOindex].addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				if(process[MOindex].isAlive()){
					process[MOindex].destroy();
					myPanel.MO[MOindex] = myPanel.MO_fal;
				}
				System.out.println("Task killed");
			}
		});  
        /*
		printStream = new PrintStream(new CustomOutputStream(myPanel.text_console[MOindex]));
		System.setOut(printStream);
		System.setErr(printStream);
        */
        
	}
	
	public static void run_command(int MOindex, String[] command, String[] path, 
								   String WD, String name){
        int int_error = 0, loop_error = 0;
        String line = ""; Boolean Suc_or_Fal = true;
		myPanel.permit[MOindex] = true;
		myPanel.rotate_index[MOindex] = 1;
        runtime[MOindex] = Runtime.getRuntime();	        
         //Check the correct path until it can locate R
        while(int_error == 0){
        	command[0] = path[loop_error];
        	try{
        		process[MOindex] = runtime[MOindex].exec(command);
                int_error = process[MOindex].getErrorStream().read();
        	}catch (IOException e1) {e1.printStackTrace();}
        	++loop_error;
        	// check command	        
            for (int i = 0; i<command.length; i++){
            	  myPanel.text_console[MOindex].append(command[i]+" ");
            }
            myPanel.text_console[MOindex].setCaretPosition(myPanel.text_console[MOindex].getDocument().getLength());
        }	
        
        try {
            BufferedReader farm_in = new BufferedReader(new InputStreamReader(process[MOindex].getInputStream()));
	        while((line = farm_in.readLine()) != null){
	        		if(line.contains("Error")||line.contains("error")){Suc_or_Fal = false;}
	                System.out.println(line);
	                myPanel.text_console[MOindex].append(line+ System.getProperty("line.separator"));
	                myPanel.text_console[MOindex].setCaretPosition(myPanel.text_console[MOindex].getDocument().getLength());
	        }
	        process[MOindex].waitFor();
            File outfile = new File(WD+"/"+name+".log");
            FileWriter outWriter = new FileWriter(outfile.getAbsoluteFile(), true);
            myPanel.text_console[MOindex].write(outWriter);
		} catch (IOException | InterruptedException e1) {e1.printStackTrace();}				
	    if(Suc_or_Fal){
			myPanel.MO[MOindex] = myPanel.MO_suc;
	    }else{
			myPanel.MO[MOindex] = myPanel.MO_fal;
	    }    
		myPanel.permit[MOindex] = false;
		myPanel.rotate_index[MOindex] = 0;
		myPanel.MOimageH[MOindex]=myPanel.MO[MOindex].getHeight(null);
		myPanel.MOimageW[MOindex]=myPanel.MO[MOindex].getWidth(null);
		myPanel.MOname[MOindex].setLocation(myPanel.MOimageX[MOindex], myPanel.MOimageY[MOindex]+ myPanel.MOimageH[MOindex]);
		System.out.println("done");
	}
	
	
	public void remove(){
		System.out.println("REMOVE");

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
	public void load(){
		System.out.println("LOAD");	
	}
	public void save(){
		System.out.println("SAVE");
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
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

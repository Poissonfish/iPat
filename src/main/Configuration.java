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

import org.apache.commons.lang3.ArrayUtils;
import main.iPatPanel.CustomOutputStream;
import net.miginfocom.swing.MigLayout;


public class Configuration extends JFrame implements ActionListener, WindowListener{
	//default to TBindex = 0, which is null
			//default to -1; 0:P, 1:G, 2:GD, 3:GM, 4:VCF, 5: PED, 6: MAP, 7: BED, 8: FAM, 9: BIM			
			// 1: Hapmap, 2: Numeric, 3: VCF, 4: PLINK(ASCII), 5: PLINK(Binary)				
	String P_name = "", G_name = "", GD_name = "", GM_name = "",
		   P = "", G = "", GD = "", GM = "", VCF = "", PED = "", MAP = "", BED = "", FAM = "", BIM = ""; 
	int C_provided = 0, K_provided = 0;
	///////////////////////////////////////////////////////////////////////////////////////
	Preferences pref;
	static Runtime[] runtime = new Runtime[iPatPanel.MOMAX];
	static Process[] process = new Process[iPatPanel.MOMAX];
	PrintStream printStream;
	///////////////////////////////////////////////////////////////////////////////////////
	//Config gapit
	JPanel main_panel;
	JButton go_gapit = new JButton("GO");
	
	JPanel wd_panel;
	Group_Value Project_g = new Group_Value("Task name");
	Group_Path WD_g = new Group_Path("Output Directory");

	ListPanel panel_phenotype;
	JLabel P_filename = new JLabel("File:\tNA");
	JPanel panel_genotype;
	JLabel G_filename = new JLabel("");
	JLabel G_filename2 = new JLabel("");
	JLabel G_format = new JLabel("");
	String[] rowP_g;
	
	JPanel panel_co;
	Group_Value PCA = new Group_Value("PCA.total");
	
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

	JPanel panel_adv_farm;
	Group_Combo method_bin = new Group_Combo("Method bin", 
			new String[]{"static", "optimum"});
	Group_Value maxloop = new Group_Value("Max Loop");
	
	Group_Combo maf = new Group_Combo("MAF threshold",
			new String[]{"No threshold", "5%", "10%", "20%"});
	Boolean MAF_open = false;
	///////////////////////////////////////////////////////////////////////////////////////	
	//Config plink
	JPanel main_panel_p;
	JButton go_p = new JButton("GO");
	
	JPanel panel_wd_p;
	Group_Value Project_p = new Group_Value("Task name");
	Group_Path WD_p = new Group_Path("Output Directory");
	
	///////////////////////////////////////////////////////////////////////////////////////	
	//Config convert
	JPanel main_panel_c;
	JPanel panel_wd_c;
	ListPanel panel_select;
	Group_Value Project_c = new Group_Value("Task name");
	Group_Path WD_c = new Group_Path("Output Directory");
	
	///////////////////////////////////////////////////////////////////////////////////////	
	Runnable back_run_gapit = new Runnable(){
		@Override
		public void run(){
			try {
		        // Construct panel
				showConsole(Project_g.longfield.getText());	            
				run_GAPIT(iPatPanel.file_index);
			} catch (FileNotFoundException e) {e.printStackTrace();}}
	};	
	Runnable back_run_farm = new Runnable(){
		@Override
		public void run(){
			try {
		        // Construct panel
				showConsole(Project_f.longfield.getText());
				run_Farm(iPatPanel.file_index);
			} catch (FileNotFoundException e) {e.printStackTrace();}}
	};
	Runnable back_run_plink = new Runnable(){
		@Override
		public void run(){
			try {
		        // Construct panel
				showConsole(Project_p.longfield.getText());
				run_PLink(iPatPanel.file_index);
			} catch (FileNotFoundException e) {e.printStackTrace();}}
	};
	int test_run = 0;
	///////////////////////////////////////////////////////////////////////////////////////
    String folder_path = new String();
	int  MOindex;
	///////////////////////////////////////////////////////////////////////////////////////
	
	public Configuration(int MOindex, iPatPanel.FORMAT format, int[][] file_index, int C, int K) throws FileNotFoundException, IOException{	
		this.MOindex = MOindex;	
		this.C_provided = C;
		this.K_provided = K;
		if(file_index[0][1]!=-1){ //input format supported
			for (int i = 0; i < 3; i++){
				if(file_index[i][1] == 0){
					Path p = Paths.get(iPatPanel.TBfile[file_index[i][0]]);
					P_name = p.getFileName().toString();
					P = iPatPanel.TBfile[file_index[i][0]];
				}else if(file_index[i][1] == 1){
					Path p = Paths.get(iPatPanel.TBfile[file_index[i][0]]);
					G_name = p.getFileName().toString();
					G = iPatPanel.TBfile[file_index[i][0]];
				}else if(file_index[i][1] == 2){
					Path p = Paths.get(iPatPanel.TBfile[file_index[i][0]]);
					GD_name = p.getFileName().toString();
					GD = iPatPanel.TBfile[file_index[i][0]];
				}else if(file_index[i][1] == 3){
					Path p = Paths.get(iPatPanel.TBfile[file_index[i][0]]);
					GM_name = p.getFileName().toString();
					GM = iPatPanel.TBfile[file_index[i][0]];
				}
			}
		}	
		JScrollPane pane_gapit = null;
		JScrollPane pane_farm = null;
		JScrollPane pane_plink = null;
		JScrollPane pane_convert= null;
		
		pref = Preferences.userRoot().node("/ipat"); 
		System.out.println("P:"+P_name+" GD:"+GD_name+" GM:"+GM_name+" G:"+G_name);
		switch(format){
			case Hapmap:
				pane_gapit = config_gapit(P_name, G_name, "0");
				pane_farm = config_farm(P_name, GD_name, "0");
				pane_plink = config_plink();
				pane_convert = config_convert();
				break;
			case Numeric:
				pane_gapit = config_gapit(P_name, GD_name, GM_name);
				pane_farm = config_farm(P_name, GD_name, GM_name);
				pane_plink = config_plink();
				break;
			case VCF:
				break;
			case PLink_ASCII:
				break;
			case PLink_Binary:
				break;
		}	
        JTabbedPane mainPane = new JTabbedPane();
        mainPane.addTab("GAPIT", pane_gapit);
        mainPane.addTab("FarmCPU", pane_farm);
        mainPane.addTab("PLINK", pane_plink);
        mainPane.addTab("Format converter", pane_convert);
		this.setContentPane(mainPane);
		this.setTitle("Configuration");
		this.pack();
		this.show();	
		load();
		addWindowListener(this);
	}	
	
	public JScrollPane config_gapit(String P_name, String G_name, String G2_name) throws IOException{
		go_gapit.setFont(new Font("Ariashowpril", Font.BOLD, 40));		
		///////////////////////////////////////////////////////////////////////////////////////
		wd_panel = new JPanel(new MigLayout("fillx"));
		wd_panel.add(Project_g.name, "wrap");
		wd_panel.add(Project_g.longfield, "wrap");
		wd_panel.add(WD_g.name, "wrap");
		wd_panel.add(WD_g.field);
		wd_panel.add(WD_g.browse, "wrap");
		Project_g.longfield.setText("Project "+ MOindex);
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
		panel_phenotype = new ListPanel("Traits", "Excluded");
		String text = iPatPanel.read_lines(P, 1)[0];
		rowP_g = text.split("\t");
		for(int i = 1; i < rowP_g.length ; i++){
			panel_phenotype.addElement(rowP_g[i]);
		}		
		panel_phenotype.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Phenotype", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
		///////////////////////////////////////////////////////////////////////////////////////
		if(C_provided == 0){
			panel_co = new JPanel(new MigLayout("fillx"));
			PCA.name.setToolTipText("Total Number of PCs as Covariates");
			panel_co.add(PCA.name);
			panel_co.add(PCA.field, "wrap");
			panel_co.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		}
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
		if(C_provided == 0){
			main_panel.add(panel_co, "cell 0 3, grow");
			main_panel.add(panel_CMLM, "cell 0 4, grow");
			main_panel.add(panel_advance, "cell 0 5, grow");	
		}else{
			main_panel.add(panel_CMLM, "cell 0 3, grow");
			main_panel.add(panel_advance, "cell 0 4, grow");
		}
		///////////////////////////////////////////////////////////////////////////////////////
		JScrollPane pane = new JScrollPane(main_panel,  
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive
		pref = Preferences.userRoot().node("/ipat"); 
		go_gapit.addActionListener(this);
		WD_g.browse.addActionListener(this);
		return pane;
	}
	
	public JScrollPane config_farm(String P_name, String G_name, String G2_name){
		go_farm.setFont(new Font("Ariashowpril", Font.BOLD, 40));		
		wd_panel_farm = new JPanel(new MigLayout("fillx"));
		wd_panel_farm.add(Project_f.name, "wrap");
		wd_panel_farm.add(Project_f.longfield, "wrap");
		wd_panel_farm.add(WD_f.name, "wrap");
		wd_panel_farm.add(WD_f.field);
		wd_panel_farm.add(WD_f.browse, "wrap");
		Project_f.longfield.setText("Project "+ MOindex);
		
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
		//panel_co_farm = new JPanel(new MigLayout("fillx"));
		//panel_co_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_adv_farm = new JPanel(new MigLayout("fillx"));
						
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
		//main_panel_farm.add(panel_co_farm, "cell 0 3, grow");
		main_panel_farm.add(panel_adv_farm, "cell 0 3, grow");
		///////////////////////////////////////////////////////////////////////////////////////
		JScrollPane pane = new JScrollPane(main_panel_farm,  
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive
		go_farm.addActionListener(this);
		WD_f.browse.addActionListener(this);
		return pane;
	}
	
	public JScrollPane config_plink(){
		go_p.setFont(new Font("Ariashowpril", Font.BOLD, 40));		
		panel_wd_p = new JPanel(new MigLayout("fillx"));
		panel_wd_p.add(Project_p.name, "wrap");
		panel_wd_p.add(Project_p.longfield, "wrap");
		panel_wd_p.add(WD_p.name, "wrap");
		panel_wd_p.add(WD_p.field);
		panel_wd_p.add(WD_p.browse, "wrap");

		Project_p.longfield.setText("Project "+ MOindex);
		panel_wd_p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Task", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel_p = new JPanel(new MigLayout("fillx", "[grow][grow]"));
		main_panel_p.add(go_p, "dock north");
		main_panel_p.add(panel_wd_p, "cell 0 0, grow");
		///////////////////////////////////////////////////////////////////////////////////////
		JScrollPane pane = new JScrollPane(main_panel_p,  
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive
		
		go_p.addActionListener(this);	
		WD_p.browse.addActionListener(this);
		return pane;
	}
	
	public JScrollPane config_convert(){
		panel_select = new ListPanel("Traits", "");
		String[] items = {"one", "two", "three"};
		panel_select.addElements(items);
		
		JScrollPane pane = new JScrollPane(panel_select,  
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive
		
		WD_c.browse.addActionListener(this);
		return pane;
	}

	@Override
	public void actionPerformed(ActionEvent ip){
	      Object source = ip.getSource();	
	      //GAPIT
	      if (source == go_gapit){
	    	  save();
	    	  iPatPanel.MOfile[MOindex] = WD_g.field.getText();
	    	  iPatPanel.multi_run[MOindex] = new Thread(back_run_gapit);
	    	  iPatPanel.multi_run[MOindex].start();
	    	  this.dispose(); 	  		      
	      }else if(source == WD_g.browse){
	    	  WD_g.setPath(true);
	      //Farm
	      }else if(source == go_farm){
	    	  save();
	    	  iPatPanel.MOfile[MOindex] = WD_f.field.getText();
	    	  iPatPanel.multi_run[MOindex] = new Thread(back_run_farm);
	    	  iPatPanel.multi_run[MOindex].start();
	    	  this.dispose(); 
	      }else if(source == WD_f.browse){
	    	  WD_f.setPath(true);    
	      //Plink
	      }else if(source == go_p){
	    	  save();
	    	  iPatPanel.MOfile[MOindex] = WD_p.field.getText();
	    	  iPatPanel.multi_run[MOindex] = new Thread(back_run_plink);
	    	  iPatPanel.multi_run[MOindex].start();
	    	  this.dispose(); 	
	      }else if(source == WD_p.browse){
	    	  WD_p.setPath(true);    
	      }
	}
	
	void run_GAPIT(int[][] file_index) throws FileNotFoundException{
		String model_selection_string = "";
		String 	G = "NULL", P = "", GD = "NULL", GM = "NULL", K = "", C = "",
				SNP_test = "", PCA_count = "",
				ki_c = "", ki_g = "", 
				g_from = "", g_to = "", g_by = "", 
				SNP_fraction = "", file_fragment = "", WD = "", Project_name = "";
		////// Multiple trait
		String[] out = panel_phenotype.getElement(); //get remain traits
		String[] indexp = new String[out.length]; //create array for index
	
		for (int i = 0; i < out.length; i++){
			indexp[i] = Integer.toString(Arrays.asList(rowP_g).indexOf(out[i])); // get selected index
		}
		System.out.println("length"+indexp.length);
		//////
		
		for(int i=0;i<3;i++){
			if(file_index[i][1] == 1){
				G = iPatPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 0){
				P = iPatPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 2){
				GD = iPatPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 3){
				GM = iPatPanel.TBfile[file_index[i][0]];
			}
		}	
		SNP_test = "TRUE";
		if(K_provided != 0){
			K = iPatPanel.TBfile[K_provided];
		}else{
			K = "NULL";
		}
		if(C_provided != 0){
			C = iPatPanel.TBfile[C_provided];
			PCA_count = "0";
		}else{
			C = "NULL";
			PCA_count =  PCA.field.getText();
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
		Project_name = Project_g.longfield.getText();
		
        System.out.println("running gapit"); 
      
        // Command input
        String[] command = {" ", iPatPanel.jar.getParent()+"/libs/Gapit.R",
        		G, GM, GD, P, K, SNP_test, C, PCA_count, 
        		ki_c, ki_g, g_from, g_to, g_by, 
        		model_selection_string, SNP_fraction, file_fragment, WD};  
        String[] whole = (String[])ArrayUtils.addAll(command, indexp);
        String[] R_Path = {"/usr/local/bin/Rscript", "/usr/bin/Rsciprt", "/usr/Rscript"};
        run_command(MOindex, whole, R_Path, WD, Project_name);      
	}
	
	void run_Farm(int[][] file_index) throws FileNotFoundException{
		String 	P = "", GD = "NULL", GM = "NULL", C = "", WD = "", Project_name = "",	
				method_b = "", maxloop_run = "", maf_cal = "", maf_threshold = "";		
		for(int i=0;i<5;i++){
			if(file_index[i][1] == 0){
				P = iPatPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 2){
				GD = iPatPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 3){
				GM = iPatPanel.TBfile[file_index[i][0]];
			}
		}			
		if(C_provided != 0){
			C = iPatPanel.TBfile[C_provided];
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
        String[] command = {" ", iPatPanel.jar.getParent()+"/libs/FarmCPU.R",
        		GM, GD, P, C, 
        		method_b, maxloop_run, maf_cal, maf_threshold, WD}; 
        String[] R_Path = {"/usr/local/bin/Rscript", "/usr/bin/Rsciprt", "/usr/Rscript"};
        run_command(MOindex, command, R_Path, WD, Project_name);
	}
	
	void run_PLink(int[][] file_index) throws FileNotFoundException{
		String 	WD = "", Project_name;
		WD = WD_p.field.getText();
		Project_name = Project_p.longfield.getText();
		String[] command = {" ",
				"--file", iPatPanel.jar.getParent()+"/libs/PLINK/toy",
				"--freq", 
				"--out", WD+"/iPAT_test"};
		String[] Path = {iPatPanel.jar.getParent()+"/libs/PLINK/plink"};
		run_command(MOindex, command, Path, WD, Project_name);
	}
	
	public void showConsole(String title){
		iPatPanel.MOname[MOindex].setText(title);
		iPatPanel.text_console[MOindex] = new JTextArea();
        iPatPanel.text_console[MOindex].setEditable(false);
        iPatPanel.scroll_console[MOindex] = new JScrollPane(iPatPanel.text_console[MOindex] ,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        iPatPanel.frame_console[MOindex]  = new JFrame();
        iPatPanel.frame_console[MOindex].setContentPane(iPatPanel.scroll_console[MOindex]);
        iPatPanel.frame_console[MOindex].setTitle(title);
        iPatPanel.frame_console[MOindex].setSize(700,350);
        iPatPanel.frame_console[MOindex].setVisible(true); 
        iPatPanel.frame_console[MOindex].addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				if(process[MOindex].isAlive()){
					process[MOindex].destroy();
					iPatPanel.MO[MOindex] = iPatPanel.MO_fal;
				}
				System.out.println("Task killed");
			}
		});  
        /*
		printStream = new PrintStream(new CustomOutputStream(iPatPanel.text_console[MOindex]));
		System.setOut(printStream);
		System.setErr(printStream);
        */
        
	}
	
	public static void run_command(int MOindex, String[] command, String[] path, 
								   String WD, String name){
        int int_error = 0, loop_error = 0;
        String line = ""; Boolean Suc_or_Fal = true;
        PrintWriter errWriter = null;
		iPatPanel.permit[MOindex] = true;
		iPatPanel.rotate_index[MOindex] = 1;
        runtime[MOindex] = Runtime.getRuntime();	        
         //Check the correct path until it can locate R
        while(int_error == 0){
        	command[0] = path[loop_error];
        	try{
        		process[MOindex] = runtime[MOindex].exec(command);
                int_error = process[MOindex].getErrorStream().read();
        	}catch (IOException e1) {e1.printStackTrace();}
        	++loop_error;
        }
        // check command	        
        for (int i = 0; i<command.length; i++){
        	  iPatPanel.text_console[MOindex].append(command[i]+" ");
        }
        iPatPanel.text_console[MOindex].append(System.getProperty("line.separator"));
        iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());	   	
        try {
    	    BufferedReader input_stream = new BufferedReader(new InputStreamReader(process[MOindex].getInputStream()));
            BufferedReader error_stream= new BufferedReader(new InputStreamReader(process[MOindex].getErrorStream()));
	        while((line = input_stream.readLine()) != null){
	            System.out.println(line);
	            iPatPanel.text_console[MOindex].append(line+ System.getProperty("line.separator"));
	            iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());
	        }
	        process[MOindex].waitFor();        
	        while((line = error_stream.readLine()) != null){
	        	File error = new File(WD+"/"+name+".err");
	            errWriter = new PrintWriter(error.getAbsoluteFile());
	            errWriter.println(line);  
	        }    
	        errWriter.close();	      
	        File outfile = new File(WD+"/"+name+".log");
            FileWriter outWriter = new FileWriter(outfile.getAbsoluteFile(),true);
            iPatPanel.text_console[MOindex].write(outWriter);
		} catch (IOException | InterruptedException e1) {	
        	Suc_or_Fal = false;
			e1.printStackTrace();
		}				
        
	    if(Suc_or_Fal){
			iPatPanel.MO[MOindex] = iPatPanel.MO_suc;
	    }else{
			iPatPanel.MO[MOindex] = iPatPanel.MO_fal;
	    }    
		iPatPanel.permit[MOindex] = false;
		iPatPanel.rotate_index[MOindex] = 0;
		iPatPanel.MOimageH[MOindex]=iPatPanel.MO[MOindex].getHeight(null);
		iPatPanel.MOimageW[MOindex]=iPatPanel.MO[MOindex].getWidth(null);
		iPatPanel.MOname[MOindex].setLocation(iPatPanel.MOimageX[MOindex], iPatPanel.MOimageY[MOindex]+ iPatPanel.MOimageH[MOindex]);
		System.out.println("done");
	}
	
	
	public void remove(){
		
	}
	
	public void load(){
		// GAPIT
		WD_g.field.setText(pref.get("WD_g", "~/"));
		model_select.combo.setSelectedIndex(pref.getInt("model_select_g", 0));
		PCA.field.setText(pref.get("PCA_g", "3"));
		K_cluster.combo.setSelectedIndex(pref.getInt("K_cluster_g", 0));
		K_group.combo.setSelectedIndex(pref.getInt("K_group_g", 0));
		model_selection_s.setSelected(pref.getBoolean("model_selection_g", false));
		snp_frac.combo.setSelectedIndex(pref.getInt("snp_frac_g", 0));
		file_frag.combo.setSelectedIndex(pref.getInt("file_frag_g", 0));
		// FarmCPU
		WD_f.field.setText(pref.get("WD_f", "~/"));
		method_bin.combo.setSelectedIndex(pref.getInt("method_bin_f", 0));
		maxloop.field.setText(pref.get("maxloop_f", "10"));
		maf.combo.setSelectedIndex(pref.getInt("maf_f", 0));					
		System.out.println("LOAD");	
	}
	
	public void save(){
		// GAPIT
		pref.put("WD_g", WD_g.field.getText());
		//pref.put("Project_g", Project_g.longfield.getText());
		pref.putInt("model_select_g", model_select.combo.getSelectedIndex());
		pref.put("PCA_g", PCA.field.getText());
		pref.putInt("K_cluster_g", K_cluster.combo.getSelectedIndex());
		pref.putInt("K_group_g", K_group.combo.getSelectedIndex());
		pref.putBoolean("model_selection_g", model_selection_s.isSelected());
		pref.putInt("snp_frac_g", snp_frac.combo.getSelectedIndex());
		pref.putInt("file_frag_g", file_frag.combo.getSelectedIndex());
		// FarmCPU
		pref.put("WD_f", WD_f.field.getText());
		pref.putInt("method_bin_f", method_bin.combo.getSelectedIndex());
		pref.put("maxloop_f", maxloop.field.getText());
		pref.putInt("maf_f", maf.combo.getSelectedIndex());
		
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

package main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	int P_provided = 0, C_provided = 0, K_provided = 0;
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
	JLabel format_g = new JLabel("");
	
	ListPanel panel_phenotype;
	String[] rowP_g;
	
	JPanel panel_co;
	Group_Value PCA = new Group_Value("PCA.total");
	
	JPanel panel_filter_g;
	Group_CheckBox chr_g = new Group_CheckBox("By Chromosome");
	Group_Combo ms_g = new Group_Combo("By missing rate",
			new String[]{"No threshold", "20%", "10%", "5%"});
	Group_Combo maf_g = new Group_Combo("By MAF",
			new String[]{"No threshold", "5%", "10%", "20%"});
	
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
	JLabel format_f = new JLabel("");
	
	JPanel panel_filter_f;
	Group_CheckBox chr_f = new Group_CheckBox("By Chromosome");
	Group_Combo ms_f = new Group_Combo("By missing rate",
			new String[]{"No threshold", "20%", "10%", "5%"});
	Group_Combo maf_f = new Group_Combo("By MAF",
			new String[]{"No threshold", "5%", "10%", "20%"});
	
	JPanel panel_adv_farm;
	Group_Combo method_bin = new Group_Combo("Method bin", 
			new String[]{"static", "optimum"});
	Group_Value maxloop = new Group_Value("Max Loop");

	///////////////////////////////////////////////////////////////////////////////////////	
	//Config plink
	JPanel main_panel_p;
	JButton go_p = new JButton("GO");
	
	JPanel panel_wd_p;
	Group_Value Project_p = new Group_Value("Task name");
	Group_Path WD_p = new Group_Path("Output Directory");
	JLabel format_p = new JLabel("");

	
	JPanel panel_filter_p;
	Group_CheckBox chr_p = new Group_CheckBox("By Chromosome");
	Group_Combo ms_p = new Group_Combo("By missing rate",
			new String[]{"No threshold", "20%", "10%", "5%"});
	Group_Combo maf_p = new Group_Combo("By MAF",
			new String[]{"No threshold", "5%", "10%", "20%"}); 
	Group_CheckBox trt_p = new Group_CheckBox("By Trait");
	
	JPanel panel_analysis_p;
	Group_Combo ci_p = new Group_Combo("C.I.",
			new String[]{"95%", "97.5%", "99.5%"}); 
	
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
	
	public Configuration(int MOindex, iPatPanel.FORMAT format, Findex[] file_index, 
						 int Phe, int C, int K) throws FileNotFoundException, IOException{	
		this.MOindex = MOindex;	
		this.P_provided = Phe;
		this.C_provided = C;
		this.K_provided = K;
		if(file_index[0].file != Findex.FILE.unknown){ //input format supported
			Path p_path = Paths.get(iPatPanel.TBfile[P_provided]);
			P_name = p_path.getFileName().toString();
			P = iPatPanel.TBfile[P_provided];
			for (int i = 0; i < 3; i++){
				if(file_index[i].file == Findex.FILE.G){
					Path p = Paths.get(iPatPanel.TBfile[file_index[i].tb]);
					G_name = p.getFileName().toString();
					G = iPatPanel.TBfile[file_index[i].tb];
				}else if(file_index[i].file == Findex.FILE.GD){
					Path p = Paths.get(iPatPanel.TBfile[file_index[i].tb]);
					GD_name = p.getFileName().toString();
					GD = iPatPanel.TBfile[file_index[i].tb];
				}else if(file_index[i].file == Findex.FILE.GM){
					Path p = Paths.get(iPatPanel.TBfile[file_index[i].tb]);
					GM_name = p.getFileName().toString();
					GM = iPatPanel.TBfile[file_index[i].tb];
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
				pane_plink = config_plink();
				pane_convert = config_convert();
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
		wd_panel = new JPanel(new MigLayout("fillx", "[][grow]"));
		wd_panel.add(Project_g.name, "cell 0 0 2 1");
		wd_panel.add(Project_g.longfield, "cell 0 1 2 1");
		wd_panel.add(WD_g.name,  "cell 0 2 2 1");
		wd_panel.add(WD_g.field, "cell 0 3 1 1");
		wd_panel.add(WD_g.browse, "cell 1 3 1 1");
		Project_g.longfield.setText("Project "+ MOindex);
		wd_panel.add(format_g, "cell 0 4 2 1");
		format_g.setText("The format is "+iPatPanel.format);
		wd_panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Project", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
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
			panel_co.add(PCA.name, "align r");
			panel_co.add(PCA.field, "wrap, align l");
			panel_co.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		}
		///////////////////////////////////////////////////////////////////////////////////////
		panel_filter_g = new JPanel(new MigLayout("fillx"));
		panel_filter_g.add(chr_g.check, "cell 0 0, align r");
		panel_filter_g.add(chr_g.longfield, "cell 1 0,align l");
		panel_filter_g.add(ms_g.name, "cell 0 1, align r");
		panel_filter_g.add(ms_g.combo, "cell 1 1, align l");
		panel_filter_g.add(maf_g.name, "cell 0 2, align r");
		panel_filter_g.add(maf_g.combo, "cell 1 2, align l");
		panel_filter_g.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Filter", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_CMLM = new JPanel(new MigLayout("fillx"));
		K_cluster.name.setToolTipText("Clustering algorithm to group individuals based on their kinship");
		K_group.name.setToolTipText("Method to derive kinship among groups");

		panel_CMLM.add(model_select.name, "cell 0 0, align r");
		panel_CMLM.add(model_select.combo, "cell 1 0, align l");
		model_select.combo.setSelectedItem("CMLM");
		panel_CMLM.add(K_cluster.name, "cell 0 1, align r");
		panel_CMLM.add(K_cluster.combo, "cell 1 1, align l");
		panel_CMLM.add(K_group.name, "cell 0 2, align r");
		panel_CMLM.add(K_group.combo, "cell 1 2, align l");
	
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
		
		panel_advance.add(snp_frac.name, "cell 0 0, align r");
		panel_advance.add(snp_frac.combo, "cell 1 0, align l");
		panel_advance.add(file_frag.name, "cell 0 1, align r");
		panel_advance.add(file_frag.combo, "cell 1 1, align l");
		panel_advance.add(model_selection_s, "cell 0 2 2 1, align c");
		panel_advance.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Advance", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel = new JPanel(new MigLayout("fillx", "[grow]"));
		main_panel.add(go_gapit, "dock north");
		main_panel.add(wd_panel, "cell 0 0, grow");
		main_panel.add(panel_phenotype, "cell 0 1, grow");
		if(C_provided == 0){
			main_panel.add(panel_co, "cell 0 2, grow");
			main_panel.add(panel_filter_g, "cell 0 3, grow");
			main_panel.add(panel_CMLM, "cell 0 4, grow");
			main_panel.add(panel_advance, "cell 0 5, grow");	
		}else{
			main_panel.add(panel_filter_g, "cell 0 2, grow");
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
		wd_panel_farm = new JPanel(new MigLayout("fillx", "[][grow]"));
		wd_panel_farm.add(Project_f.name, "cell 0 0 2 1");
		wd_panel_farm.add(Project_f.longfield, "cell 0 1 2 1");
		wd_panel_farm.add(WD_f.name,  "cell 0 2 2 1");
		wd_panel_farm.add(WD_f.field, "cell 0 3 1 1");
		wd_panel_farm.add(WD_f.browse, "cell 1 3 1 1");
		Project_f.longfield.setText("Project "+ MOindex);
		wd_panel_farm.add(format_f, "cell 0 4 2 1");
		format_f.setText("The format is "+iPatPanel.format);
		wd_panel_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Project", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_filter_f = new JPanel(new MigLayout("fillx"));
		panel_filter_f.add(chr_f.check, "cell 0 0, align r");
		panel_filter_f.add(chr_f.longfield, "cell 1 0,align l");
		panel_filter_f.add(ms_f.name, "cell 0 1, align r");
		panel_filter_f.add(ms_f.combo, "cell 1 1, align l");
		panel_filter_f.add(maf_f.name, "cell 0 2, align r");
		panel_filter_f.add(maf_f.combo, "cell 1 2, align l");
		panel_filter_f.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Filter", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		//panel_co_farm = new JPanel(new MigLayout("fillx"));
		//panel_co_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_adv_farm = new JPanel(new MigLayout("fillx"));
		panel_adv_farm.add(method_bin.name,  "cell 0 0, align r");
		panel_adv_farm.add(method_bin.combo,  "cell 1 0, align l");
		panel_adv_farm.add(maxloop.name,  "cell 0 1, align r");
		panel_adv_farm.add(maxloop.field,  "cell 1 1, align l");;
		panel_adv_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Advance", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel_farm = new JPanel(new MigLayout("fillx", "[grow]"));
		main_panel_farm.add(go_farm, "dock north");
		main_panel_farm.add(wd_panel_farm, "cell 0 0, grow");
		main_panel_farm.add(panel_filter_f, "cell 0 1, grow");
		//main_panel_farm.add(panel_co_farm, "cell 0 3, grow");
		main_panel_farm.add(panel_adv_farm, "cell 0 2, grow");
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
		panel_wd_p = new JPanel(new MigLayout("fillx", "[][grow]"));
		panel_wd_p.add(Project_p.name, "cell 0 0 2 1");
		panel_wd_p.add(Project_p.longfield, "cell 0 1 2 1");
		panel_wd_p.add(WD_p.name,  "cell 0 2 2 1");
		panel_wd_p.add(WD_p.field, "cell 0 3 1 1");
		panel_wd_p.add(WD_p.browse, "cell 1 3 1 1");
		Project_p.longfield.setText("Project "+ MOindex);
		panel_wd_p.add(format_p, "cell 0 4 2 1");
		format_p.setText("The format is "+iPatPanel.format);
		panel_wd_p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Project", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_filter_p = new JPanel(new MigLayout("fillx"));
		panel_filter_p.add(chr_p.check, "cell 0 0, align r");
		panel_filter_p.add(chr_p.longfield, "cell 1 0,align l");
		panel_filter_p.add(ms_p.name, "cell 0 1, align r");
		panel_filter_p.add(ms_p.combo, "cell 1 1, align l");
		panel_filter_p.add(maf_p.name, "cell 0 2, align r");
		panel_filter_p.add(maf_p.combo, "cell 1 2, align l");
		panel_filter_p.add(trt_p.check, "cell 0 3, align r");
		panel_filter_p.add(trt_p.longfield, "cell 1 3, align l");
		panel_filter_p.add(ci_p.name, "cell 0 4, align r");
		panel_filter_p.add(ci_p.combo, "cell 1 4, align l");		
		panel_filter_p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Filter", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel_p = new JPanel(new MigLayout("fillx", "[grow]"));
		main_panel_p.add(go_p, "dock north");
		main_panel_p.add(panel_wd_p, "cell 0 0, grow");
		main_panel_p.add(panel_filter_p, "cell 0 1, grow");
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
	
	void run_GAPIT(Findex[] file_index) throws FileNotFoundException{
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
		P = iPatPanel.TBfile[P_provided];
		for(int i=0;i<3;i++){
			if(file_index[i].file == Findex.FILE.G){
				G = iPatPanel.TBfile[file_index[i].tb];
			}else if(file_index[i].file == Findex.FILE.GD){
				GD = iPatPanel.TBfile[file_index[i].tb];
			}else if(file_index[i].file == Findex.FILE.GM){
				GM = iPatPanel.TBfile[file_index[i].tb];
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
	
	void run_Farm(Findex[] file_index) throws FileNotFoundException{
		String 	P = "", GD = "NULL", GM = "NULL", C = "", WD = "", Project_name = "",	
				method_b = "", maxloop_run = "", maf_cal = "", maf_threshold = "";		
		P = iPatPanel.TBfile[P_provided];
		for(int i = 0; i < 3; i++){
			if(file_index[i].file == Findex.FILE.GD){
				GD = iPatPanel.TBfile[file_index[i].tb];
			}else if(file_index[i].file == Findex.FILE.GM){
				GM = iPatPanel.TBfile[file_index[i].tb];
			}
		}			
		if(C_provided != 0){
			C = iPatPanel.TBfile[C_provided];
		}else{
			C = "NULL";
		}
		method_b = (String) method_bin.combo.getSelectedItem();
		maxloop_run = maxloop.field.getText();
		
		int maf_value = maf_f.combo.getSelectedIndex();
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
	
	void run_PLink(Findex[] file_index) throws FileNotFoundException{
		String 	WD = "", Project_name;
		String 	p_path = "", bed = "", bim = "", fam = "", ci = "",
				ms = "", maf = "";
		p_path = iPatPanel.TBfile[P_provided];
		for(int i=0;i<3;i++){
			if(file_index[i].file == Findex.FILE.BED){
				bed = iPatPanel.TBfile[file_index[i].tb];
			}else if(file_index[i].file == Findex.FILE.BIM){
				bim = iPatPanel.TBfile[file_index[i].tb];
			}else if(file_index[i].file == Findex.FILE.FAM){
				fam = iPatPanel.TBfile[file_index[i].tb];
			}
		}	
		WD = WD_p.field.getText();
		Project_name = Project_p.longfield.getText(); 
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(iPatPanel.jar.getParent()+"/libs/plink");
		list.add("--bed"); list.add(bed);
		list.add("--bim"); list.add(bim);
		list.add("--fam"); list.add(fam);
		list.add("--assoc"); list.add("--allow-no-sex"); 
		list.add("--pheno"); list.add(p_path);
		list.add("--all-pheno"); list.add("-—adjust");
		switch(ci_p.combo.getSelectedIndex()){
			case 0: list.add("--ci"); list.add(".95"); break;
			case 1:	list.add("--ci"); list.add(".975"); break;
			case 2: list.add("--ci"); list.add(".995"); break;
		}
		switch(ms_p.combo.getSelectedIndex()){
			case 1:	list.add("--geno"); list.add(".2"); break;
			case 2: list.add("--geno"); list.add(".1"); break;
			case 3: list.add("--geno"); list.add(".05"); break;
		}
		switch(maf_p.combo.getSelectedIndex()){
			case 1:	list.add("--maf"); list.add(".05"); break;
			case 2: list.add("--maf"); list.add(".1"); break;
			case 3: list.add("--maf"); list.add(".2"); break;
		}
		list.add("--out"); list.add(WD+"/"+Project_name);
		String[] command = list.toArray(new String[0]);		
		//plink —bfile data —assoc —allow-no-sex —pheno phenos.txt —all-pheno —adjust —ci 0.95 —out output
		run_command_c(MOindex, command, WD, Project_name);
		iPatPanel.MO[MOindex] = iPatPanel.MOimage;
		String[] plot_com = {" ", iPatPanel.jar.getParent()+"/libs/PLinkPlots.R",
							WD, Project_name, "3"};
		String[] R_Path = {"/usr/local/bin/Rscript", "/usr/bin/Rsciprt", "/usr/Rscript"};
        run_command(MOindex, plot_com, R_Path, WD, Project_name);
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
	
	public static void run_command_c(int MOindex, String[] command,
			   						String WD, String name){
        String line = ""; Boolean Suc_or_Fal = true;
        PrintWriter errWriter = null;
		iPatPanel.permit[MOindex] = true;
		iPatPanel.rotate_index[MOindex] = 1;
        runtime[MOindex] = Runtime.getRuntime();	        
         //Check the correct path until it can locate R
       	try{
       		process[MOindex] = runtime[MOindex].exec(command);
       	}catch (IOException e1) {
        	Suc_or_Fal = false;
        	e1.printStackTrace();}
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
    	    while((line = error_stream.readLine()) != null){
	            System.out.println(line);
	        	File error = new File(WD+"/"+name+".err");
	            errWriter = new PrintWriter(error.getAbsoluteFile());
	            errWriter.println(line);
	            if(line.toUpperCase().startsWith("ERROR")){Suc_or_Fal = false;}
	        }  
    	    process[MOindex].waitFor();        
		} catch (IOException | InterruptedException e1) {	
        	Suc_or_Fal = false;
			e1.printStackTrace();
		}	
        
	    if(Suc_or_Fal){
			iPatPanel.MO[MOindex] = iPatPanel.MO_suc;
	    }else{
			iPatPanel.MO[MOindex] = iPatPanel.MO_fal;
			errWriter.close();
	    }    
		iPatPanel.permit[MOindex] = false;
		iPatPanel.rotate_index[MOindex] = 0;
		iPatPanel.MOimageH[MOindex]=iPatPanel.MO[MOindex].getHeight(null);
		iPatPanel.MOimageW[MOindex]=iPatPanel.MO[MOindex].getWidth(null);
		iPatPanel.MOname[MOindex].setLocation(iPatPanel.MOimageX[MOindex], iPatPanel.MOimageY[MOindex]+ iPatPanel.MOimageH[MOindex]);
		System.out.println("done");
		process[MOindex].destroy();
	}
	public static void run_command(int MOindex, String[] command, String[] path, 
								   String WD, String name){
        int int_error = 0, loop_error = 0;
        String line = ""; Boolean Suc_or_Fal = true;
        PrintWriter errWriter = null;
		iPatPanel.permit[MOindex] = true;
		iPatPanel.rotate_index[MOindex] = 1;
		System.out.println("one");
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
		process[MOindex].destroy();
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
		maf_f.combo.setSelectedIndex(pref.getInt("maf_f", 0));
		// PLINK
		WD_p.field.setText(pref.get("WD_p", "~/"));
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
		pref.putInt("maf_f", maf_f.combo.getSelectedIndex());
		// PLINK
		pref.put("WD_p", WD_p.field.getText());
		
		
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

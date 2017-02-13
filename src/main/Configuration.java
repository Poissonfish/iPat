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
import net.miginfocom.swing.MigLayout;


public class Configuration extends JFrame implements ActionListener, WindowListener{
	Preferences pref;
	static Runtime[] gapit_runtime = new Runtime[myPanel.MOMAX];
	static Process[] gapit_pro = new Process[myPanel.MOMAX];

	///////////////////////////////////////////////////////////////////////////////////////
	//Config 
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
	JPanel panel_CMLM;
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel panel_PCA;
	JLabel PCA_total_text = new JLabel("PCA.total");
	JTextField PCA_total_input= new JTextField(3);
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
	iPat_chooser chooser;	
	///////////////////////////////////////////////////////////////////////////////////////	

	Boolean[] Suc_or_Fal = new Boolean[myPanel.MOMAX];
	
	Runnable back_run = new Runnable(){
		@Override
		public void run(){
			try {
		        // Construct panel
	            myPanel.text_console[MOindex] = new JTextArea();
	            myPanel.text_console[MOindex].setEditable(false);
	            myPanel.scroll_console[MOindex] = new JScrollPane(myPanel.text_console[MOindex] ,
	                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	            myPanel.frame_console[MOindex]  = new JFrame();
	            myPanel.frame_console[MOindex].setContentPane(myPanel.scroll_console[MOindex]);
	            myPanel.frame_console[MOindex].setTitle(project_input_s.getText());
	            myPanel.frame_console[MOindex].setSize(700,350);
	            myPanel.frame_console[MOindex].setVisible(true); 
	            myPanel.frame_console[MOindex].addWindowListener(new WindowAdapter(){
	    			@Override
	    			public void windowClosing(WindowEvent e) {
	    				if(Configuration.gapit_pro[MOindex].isAlive()){
	    					Configuration.gapit_pro[MOindex].destroy();
	    					Suc_or_Fal[MOindex] = false;
	    				}
	    				System.out.println("Task killed");
	    			}
	    		});
	            
				GAPIT_two(MOindex);
				//GAPIT(MOindex);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	};
	int test_run = 0;
	///////////////////////////////////////////////////////////////////////////////////////
    String folder_path = new String();
	int  MOindex;
	///////////////////////////////////////////////////////////////////////////////////////
	int[][] file_index = new int[10][2]; //tbindex; filetype: 1=G, 2=P
	
	public Configuration(int MOindex) throws FileNotFoundException, IOException{	
		this.MOindex = MOindex;
		int index = 0;
		Arrays.fill(Suc_or_Fal, true);
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
				pref = Preferences.userRoot().node("/ipat"); 
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
		load();
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
		pref = Preferences.userRoot().node("/ipat"); 

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
	    	      	  	
	      // Config_2
	      }else if(source == go_2){
	    	  	save();
	    	  	myPanel.MOfile[MOindex] = workingdir_input_s.getText();
	    	  	myPanel.gapit_run[MOindex] = new Thread(back_run);
	    	  	myPanel.gapit_run[MOindex].start();
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
				SNP_test = "", PCA = "",
				ki_c = "", ki_g = "", 
				g_from = "", g_to = "", g_by = "", 
				SNP_fraction = "", file_fragment = "", WD = "";
		for(int i=0;i<5;i++){
			if(file_index[i][1] == 1){
				G = myPanel.TBfile[file_index[i][0]];
			}else if(file_index[i][1] == 0){
				P = myPanel.TBfile[file_index[i][0]];
			}
		}	
		if(KI_user.isSelected() && Prediction.isSelected()){
			K = KI_path.getText();
			SNP_test = "FALSE";
			G = "NULL";
		}else if(KI_user.isSelected()){
			K = KI_path.getText();
			SNP_test = "TRUE";
		}else if(!KI_user.isSelected()){
			K = "NULL";
			SNP_test = "TRUE";
		}
		if(CO_user.isSelected()){
			C = CO_path.getText();
			PCA = "0";
		}else{
			C = "NULL";
			PCA =  "3";
					//PCA_total_input_s.getText();
		}
		if(CMLM_enable.isSelected()){
			ki_c = (String) kinship_cluster_input_s.getSelectedItem();
			ki_g = (String) kinship_group_input_s.getSelectedItem();
			g_from = group_from_input_s.getText();
			g_to = group_to_input_s.getText();
			g_by = group_by_input_s.getText();
		}else{
			ki_c = "average"; 
			ki_g = "Mean";
			g_from = "1";
			g_to = "10000000";
			g_by = "10";
		}
		if(model_selection_s.isSelected()){
			model_selection_string = "TRUE";
		}else{
			model_selection_string = "FALSE";
		}
		SNP_fraction = SNP_fraction_input_s.getText();
		file_fragment = (String) file_fragment_input_s.getSelectedItem();
		WD = workingdir_input_s.getText();
		myPanel.permit[MOindex] = true;
		myPanel.rotate_index[MOindex] = 1;
		Process pl;
		try {
			// Check working directory
			System.out.println("running gapit");
			pl = Runtime.getRuntime().exec("pwd");
			String line = "", gapit_line = "";
	        BufferedReader pl_in = new BufferedReader(new InputStreamReader(pl.getInputStream()));
	        while((line = pl_in.readLine()) != null){
	                System.out.println(line);
	        }
	        pl_in.close();
	        // Command input
	        String[] command = {"rscript", "./libs/Gapit.R",
	        		G, "NULL", "NULL", P, K, SNP_test, C, PCA, 
	        		ki_c, ki_g, g_from, g_to, g_by, 
	        		model_selection_string, SNP_fraction, file_fragment, WD};
	        System.out.println(command);
            
            // Run Gapit
            gapit_runtime[MOindex] = Runtime.getRuntime();
            gapit_pro[MOindex] = gapit_runtime[MOindex].exec(command);
            BufferedReader gapit_in = new BufferedReader(new InputStreamReader(gapit_pro[MOindex].getInputStream()));
	        while((gapit_line = gapit_in.readLine()) != null){
	        		if(gapit_line.contains("Error")){Suc_or_Fal[MOindex] = false;}
	                System.out.println(gapit_line);
	                myPanel.text_console[MOindex].append(gapit_line+ System.getProperty("line.separator"));
	                myPanel.text_console[MOindex].setCaretPosition(myPanel.text_console[MOindex].getDocument().getLength());
	        }
            gapit_pro[MOindex].waitFor();
            File outfile = new File(WD+"/"+project_input_s.getText()+".log");
            FileWriter outWriter = new FileWriter(outfile.getAbsoluteFile(), true);
            myPanel.text_console[MOindex].write(outWriter);
		} catch (IOException | InterruptedException e1) {e1.printStackTrace();}
				
	    if(Suc_or_Fal[MOindex]){
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
		workingdir_input_s.setText(pref.get("wds", "~/"));
		project_input_s.setText(myPanel.MOname[MOindex].getText());	
		PCA_total_input.setText(pref.get("pca_total", "3"));		
	}
	
	public void save(){
		System.out.println("SAVE");
		pref.put("wds", workingdir_input_s.getText());
		myPanel.MOname[MOindex].setText(project_input_s.getText());
		pref.put("pca_total", PCA_total_input.getText());	
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

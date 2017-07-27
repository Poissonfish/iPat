package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.ArrayUtils;

import net.miginfocom.swing.MigLayout;

class AlphaLabel extends JLabel {
    private float alpha = 1f;        
    public float getAlpha() {
        return alpha;
    }
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));          
        super.paintComponent(g2);
    }
}
class Group_Value{
	public JLabel name = new JLabel();
	public JTextField longfield = new JTextField(10);
	public JTextField field = new JTextField(5);
	public Group_Value(String text){
		name.setText(text);
	}
}
class Group_RadioButton{
	public ButtonGroup	group = new ButtonGroup();
	public JRadioButton[] button;
	public Group_RadioButton(int size){
		button = new JRadioButton[size];
		for(int i = 0; i<size; i++){
			button[i] = new JRadioButton("");
			group.add(button[i]);
		}
	}
	public void setName(int num, String text){
		button[num].setText(text);
	}	
}
class Group_CheckBox implements ActionListener{
	public JCheckBox check = new JCheckBox();
	public JTextField longfield = new JTextField(10);
	public JTextField field = new JTextField(3);
	public Group_CheckBox(String text){
		check.setText(text);
		check.setSelected(false);
		check.addActionListener(this);
		longfield.setEnabled(false);
		field.setEnabled(false);
	}
	@Override
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();	
	      //GAPIT
	      if (source == check){
	    	 longfield.setEnabled(!longfield.isEnabled());
	    	 field.setEnabled(!field.isEnabled());
	      }
	}
}
class Group_Combo{
	public JLabel name = new JLabel();
	public JComboBox combo;
	public Group_Combo(String text, String[] list){
		name.setText(text);
		combo = new JComboBox(list);
	}
}
class Group_Path{
	public JLabel name = new JLabel();
	public JButton browse = new JButton("Browse");
	public JTextField field = new JTextField(15);
	public Group_Path(String text){
		name.setText(text);
	}	
	public void setPath(boolean folder){
		JFileChooser chooser;
		File selectedfile;
		if(folder){
			selectedfile = iPatPanel.iPatChooser("Choose a output directory", true);
		}else{
			selectedfile = iPatPanel.iPatChooser("Choose a file", false);
		}
		field.setText(selectedfile.getAbsolutePath());
	}
}

class Findex{
	public static enum FILE{
		unknown, P, G, GD, GM, VCF, PED, MAP, BED, FAM, BIM, TB, C, K, 
	}
	public FILE file = FILE.unknown;
	public int tb = 0;
	public Findex(){
	}
}
class OS{
	public static enum TYPE{
		Mac, Windows, Linux, Unknown
	}
	public TYPE type = TYPE.Unknown;
	public OS(){
	}
}
class BGThread extends Thread{
	int MOindex;
	String WD, Project;
	String[] command, con_command;
	public BGThread(int MOindex, String[] command, String[] con_command){
		this.MOindex = MOindex;
		this.command = command;
		this.Project = command[2];
		this.WD = command[3];
		this.con_command = con_command;
	}
	@Override
	public void run(){
      	String line = ""; Boolean Suc_or_Fal = true;	
        iPatPanel.permit[MOindex] = true;
		iPatPanel.rotate_index[MOindex] = 1;
		iPatPanel.runtime[MOindex] = Runtime.getRuntime();
		
		// Execute the command
		try{iPatPanel.process[MOindex] = iPatPanel.runtime[MOindex].exec(command);
    	}catch (IOException e1) {e1.printStackTrace();}	
		
		if(iPatPanel.debug){
			// Print command	
			System.out.println("print debug command");
			for (int i = 0; i<command.length; i++){iPatPanel.text_console[MOindex].append(command[i]+" ");}
	    }
		     	
        iPatPanel.text_console[MOindex].append("\n");
        iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());	   	

        try {
        	// Print output message to the panel
        	System.out.println("begin to print, "+MOindex);
    	    BufferedReader input_stream = new BufferedReader(new InputStreamReader(iPatPanel.process[MOindex].getInputStream()));
            BufferedReader error_stream = new BufferedReader(new InputStreamReader(iPatPanel.process[MOindex].getErrorStream()));
	        while((line = input_stream.readLine()) != null){
	            iPatPanel.text_console[MOindex].append(line+ System.getProperty("line.separator"));
	            iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());
	        }
	        iPatPanel.process[MOindex].waitFor();        
        	
	        // Direct error to a file
	        PrintWriter errWriter = new PrintWriter(new BufferedWriter(new FileWriter(WD+"/"+Project+".err", true)));
        	boolean err_close = false;
	        try{
        		// Print error if there is any error message
    	        while((line = error_stream.readLine()) != null){
    	        	err_close = true;
    	        	errWriter.println(line);
    	        	if(line.toUpperCase().indexOf("ERROR") >= 0) Suc_or_Fal = false;
    	        	System.out.println("failed");
    	        }	
        	}catch(IOException e){}
        	if(err_close){errWriter.close();}

        	// Direct output to a file from panel
	        File outfile = new File(WD+"/"+Project+".log");
            FileWriter outWriter = new FileWriter(outfile.getAbsoluteFile(), false);
            iPatPanel.text_console[MOindex].write(outWriter);
        } catch (IOException | InterruptedException e1) {	
			e1.printStackTrace();
        	Suc_or_Fal = false;
		}	       
        // Indicator
	    if(Suc_or_Fal) iPatPanel.MO[MOindex] = iPatPanel.MO_suc; else iPatPanel.MO[MOindex] = iPatPanel.MO_fal;     
	    // Stop rotating
	    iPatPanel.permit[MOindex] = false;
		iPatPanel.rotate_index[MOindex] = 0;
		iPatPanel.MOimageH[MOindex]=iPatPanel.MO[MOindex].getHeight(null);
		iPatPanel.MOimageW[MOindex]=iPatPanel.MO[MOindex].getWidth(null);
		iPatPanel.MOname[MOindex].setLocation(iPatPanel.MOimageX[MOindex], iPatPanel.MOimageY[MOindex]+ iPatPanel.MOimageH[MOindex]);
		System.out.println("done");
		iPatPanel.process[MOindex].destroy();
		// Run next procedure if needed
		if(con_command!=null){
    	  	iPatPanel.multi_run[MOindex] = new BGThread(MOindex, con_command, null);
    	  	iPatPanel.multi_run[MOindex].start();
		}
	}
}

class ConfigPane extends JPanel implements ActionListener{
	JLabel msg = new JLabel("", SwingConstants.CENTER);
	boolean isDeployed = false;
	ConfigFrame.method existmethod = ConfigFrame.method.NA;
	int MOindex = 0;
	
	public ConfigPane(int MOindex){
		this.MOindex = MOindex;
		this.setOpaque(true);
		msg.setFont(new Font("Ariashowpril", Font.PLAIN, 30));
		this.setLayout(new MigLayout("", "[grow]", "[grow]"));
		this.add(msg, "grow");	
		HintDrag();
	}
	public void Clear(){
		this.removeAll();
		this.setLayout(new MigLayout("", "[grow]", "[grow]"));
		this.add(msg, "grow");	
	}
	public void HintDrop(){
		this.setBackground(Color.decode("#D0EDDA"));
		msg.setText("<html><center> Drop <br> to <br> Deploy </center></html>");
	}
	public void HintDrag(){
		this.setBackground(Color.decode("#D0EDE8"));
		msg.setText("<html><center> Drag Here <br> a Method </center></html>");
	}

	public void MethodSelected(ConfigFrame.method method){
		isDeployed = true;
		existmethod = method;
		this.setBackground(Color.decode("#D0E4ED"));
		msg.setText("<html><center>"+ method +"<br> Selected <br> (Tap for details) </center></html>");
	}
	public String[] MethodCommand(){
		// Get common information
			String[] command_common = {
					ConfigFrame.project_name.field.getText(), // 2
					ConfigFrame.wd_path.field.getText(), 
					iPatPanel.jar.getParent()+"/libs/", 
					iPatPanel.format.Name(), 
					(String)ConfigFrame.ms_qc.combo.getSelectedItem(), 
					(String)ConfigFrame.maf_qc.combo.getSelectedItem(), // 7 
					ConfigFrame.path_P, iPatPanel.panel_phenotype[MOindex].getSelected(),
					ConfigFrame.path_G, 
					ConfigFrame.path_M, // 11
					ConfigFrame.path_C, ConfigFrame.C_exist? panel_cov.getSelected():"NA",
					ConfigFrame.path_K, 
					ConfigFrame.path_FAM, // 15
					ConfigFrame.path_BIM
			};
		// Get specific method
			String[] command_exe = null;
			String[] command_specific = null;
			switch(existmethod){
				case GAPIT:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_Gapit.R"};
					command_specific = new String[]{
							(String)model_select.combo.getSelectedItem(),  // 17
							(String)K_cluster.combo.getSelectedItem(),
							(String)K_group.combo.getSelectedItem(),
							(String)snp_frac.combo.getSelectedItem(),
							//(String)file_frag.combo.getSelectedItem(), // 21
							model_selection.isSelected()?"TRUE":"FALSE"};
					break;
				case FarmCPU:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_FarmCPU.R"};
					command_specific = new String[]{
							(String)method_bin.combo.getSelectedItem(),  // 17
							(String)maxloop.combo.getSelectedItem()
							};
					break;
				case PLINK:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_PLINK.R"};
					command_specific = new String[]{
							(String)ci.combo.getSelectedItem()  // 17
							};
					break;
				case gBLUP:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_gBLUP.R"};
					command_specific = new String[]{
							(String)snp_frac.combo.getSelectedItem(),  // 17
							//(String)file_frag.combo.getSelectedItem(),
							model_selection.isSelected()?"TRUE":"FALSE",
							enable.isSelected()?"TRUE":"FALSE",
							(String)bonferroni.combo.getSelectedItem()}; // 21
					break;
				case rrBLUP:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_rrBLUP.R"};
					command_specific = new String[]{
							(String)impute_method.combo.getSelectedItem(),  // 17
							shrink.isSelected()?"TRUE":"FALSE",
							enable.isSelected()?"TRUE":"FALSE",
							(String)bonferroni.combo.getSelectedItem()};
					break;
				case BGLR:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_BGLR.R"};
					command_specific = new String[]{
							(String)model_b.combo.getSelectedItem(),  // 17
							(String)response_b.combo.getSelectedItem(),
							(String)niter_b.combo.getSelectedItem(),
							(String)burnin_b.combo.getSelectedItem(),							
							(String)thin_b.combo.getSelectedItem(),  // 21
							enable.isSelected()?"TRUE":"FALSE",
							(String)bonferroni.combo.getSelectedItem()};
					break;
			}
		
		// combine whole command
			String[] command =  ArrayUtils.addAll(command_exe, ArrayUtils.addAll(command_common, command_specific));
			return command;
	}
	
	// Common used
	JTabbedPane pane = new JTabbedPane();
	selectablePanel panel_cov;
	String CO_head;
	String[] CO_names;
	JPanel panel_gwas = new JPanel();
	JCheckBox enable = new JCheckBox("");
	Group_Combo bonferroni = new Group_Combo("Bonferroni cut-off",  
			new String[]{"0.05", "0.01", "0.005", "0.001", "0.0001"});
	// COV pane
	public void CovPane(boolean C_exist, String[] model_names) throws IOException{
		if(C_exist){
			CO_head = iPatPanel.read_lines(iPatPanel.TBfile[ConfigFrame.C_index], 1)[0];
			CO_names = CO_head.split("\t");
			panel_cov = new selectablePanel(CO_names.length, CO_names, model_names);
		}else{
			panel_cov = new selectablePanel(0, new String[]{}, new String[]{"Selected", "Excluded"});
			panel_cov.setLayout(new MigLayout("", "[grow]", "[grow]"));
			JLabel na_co = new JLabel("<html><center> Covariates <br> Unavailable </center></html>", SwingConstants.CENTER);
			na_co.setFont(new Font("Ariashowpril", Font.PLAIN, 20));
			panel_cov.add(na_co, "grow");
		}	
	}
	// GWAS pane
	public void GWASPane(){
		if(iPatPanel.Deployed[MOindex][ConfigFrame.analysis.GWAS.index] != ConfigFrame.method.NA){
			panel_gwas.removeAll();	
			enable = new JCheckBox("Enable GWAS-Assisted feature (By " + iPatPanel.Deployed[MOindex][ConfigFrame.analysis.GWAS.index].Name()  + ")");
			panel_gwas.setLayout(new MigLayout("fillx"));
			panel_gwas.add(enable, "wrap");
			panel_gwas.add(bonferroni.name);
			panel_gwas.add(bonferroni.combo, "wrap");
			enable.setSelected(true);
			enable.addActionListener(this);
		}else{
			panel_gwas.setLayout(new MigLayout("", "[grow]", "[grow]"));
			JLabel na_msg = new JLabel("<html><center> GWAS-Assisted GS <br> Unavailable <br> Please select a GWAS method first </center></html>", SwingConstants.CENTER);
			na_msg.setFont(new Font("Ariashowpril", Font.PLAIN, 18));
			panel_gwas.add(na_msg, "grow");
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src == enable){
			bonferroni.combo.setEnabled(!bonferroni.combo.isEnabled());
		}
	}
	// Method specific
	JPanel panel_gapit;
	Group_Combo K_algorithm = new Group_Combo("kinship.algorithm", 
			new String[]{"VanRaden", "Loiselle", "EMMA"});
	Group_Combo K_cluster = new Group_Combo("kinship.cluster", 
			new String[]{"average", "complete", "ward", "single", "mcquitty", "median", "centroid"});
	Group_Combo K_group = new Group_Combo("kinship.group", 
			new String[]{"Mean", "Max", "Min", "Median"});
	Group_Combo model_select = new Group_Combo("Select a model",
			new String[]{"GLM", "MLM", "CMLM"});
	JPanel panel_advance;
	Group_Combo snp_frac = new Group_Combo("SNP.fraction",
			new String[]{"1", "0.8", "0.5", "0.3", "0.1"});
	Group_Combo file_frag = new Group_Combo("File fragment",
			new String[]{"NULL", "512", "256", "128", "64"});
	JCheckBox model_selection = new JCheckBox("Model selection");
	public void config_gapit() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			CovPane(ConfigFrame.C_exist, new String[]{"Selected", "Excluded"});
		// specific
			panel_gapit = new JPanel(new MigLayout("fillx"));
			panel_gapit.add(model_select.name, "cell 0 0, align r");
			panel_gapit.add(model_select.combo, "cell 1 0, align l");
			panel_gapit.add(K_cluster.name, "cell 0 1, align r");
			panel_gapit.add(K_cluster.combo, "cell 1 1, align l");
			panel_gapit.add(K_group.name, "cell 0 2, align r");
			panel_gapit.add(K_group.combo, "cell 1 2, align l");
			panel_advance = new JPanel(new MigLayout("fillx"));
			panel_advance.add(snp_frac.name, "cell 0 0, align r");
			panel_advance.add(snp_frac.combo, "cell 1 0, align l");
			panel_advance.add(model_selection, "cell 0 1 2 1, align c");
		pane.addTab("Covariates", panel_cov);
		pane.addTab("GAPIT input", panel_gapit);
		pane.addTab("Advance", panel_advance);
		this.add(pane, "grow");
	}
	public void config_gblup() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			CovPane(ConfigFrame.C_exist, new String[]{"Selected", "Excluded"});
		// gwas
			GWASPane();
		// specific
			panel_advance = new JPanel(new MigLayout("fillx"));
			panel_advance.add(snp_frac.name, "cell 0 0, align r");
			panel_advance.add(snp_frac.combo, "cell 1 0, align l");
			panel_advance.add(model_selection, "cell 0 1 2 1, align c");
		pane.addTab("Covariates", panel_cov);
		pane.addTab("GWAS-Assist",  panel_gwas);
		pane.addTab("Advance", panel_advance);
		this.add(pane, "grow");
	}
	
	JPanel panel_farm;
	Group_Combo method_bin = new Group_Combo("Method bin", 
			new String[]{"static", "optimum"});
	Group_Combo maxloop = new Group_Combo("maxLoop", 
			new String[]{"10", "1", "2", "5", "20"});
	public void config_farm() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			CovPane(ConfigFrame.C_exist, new String[]{"Selected", "Excluded"});
		// specific
			panel_farm = new JPanel(new MigLayout("fillx"));
			panel_farm.add(method_bin.name, "cell 0 0, align r");
			panel_farm.add(method_bin.combo, "cell 1 0, align l");
			panel_farm.add(maxloop.name, "cell 0 1, align r");
			panel_farm.add(maxloop.combo, "cell 1 1, align l");
		pane.addTab("Covariates", panel_cov);
		pane.addTab("FarmCPU input", panel_farm);	
		this.add(pane, "grow");
	}
	
	JPanel panel_plink;
	Group_Combo ci = new Group_Combo("C.I.",
			new String[]{"0.95", "0.975", "0.995"}); 
	public void config_plink() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			CovPane(ConfigFrame.C_exist, new String[]{"Selected", "Excluded"});
		// specific
			panel_plink = new JPanel(new MigLayout("fillx"));
			panel_plink.add(ci.name, "cell 0 0, align r");
			panel_plink.add(ci.combo, "cell 1 0, align l");
		pane.addTab("Covariates", panel_cov);
		pane.addTab("PLINK input", panel_plink);
		this.add(pane, "grow");
	}
	//
	JPanel panel_rrblup;
	Group_Combo impute_method = new Group_Combo("impute.method", 
			new String[]{"mean", "EM"});
	JCheckBox shrink = new JCheckBox("Shrinkage estimation");
	public void config_rrblup() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			CovPane(ConfigFrame.C_exist, new String[]{"Selected", "Excluded"});
		// gwas
			GWASPane();
		// specific
			panel_rrblup = new JPanel(new MigLayout("fillx"));
			panel_rrblup.add(impute_method.name, "cell 0 0, align r");
			panel_rrblup.add(impute_method.combo, "cell 1 0, align l");
			panel_rrblup.add(shrink, "cell 0 1, align c");
		pane.addTab("Covariates", panel_cov);
		pane.addTab("GWAS-Assist",  panel_gwas);
		pane.addTab("rrBLUP input", panel_rrblup);
		this.add(pane, "grow");
	}
	
	JPanel panel_args_b;
	Group_Combo model_b = new Group_Combo("Model of the Predictor (Markers)", 
			new String[]{"BRR", "BayesA", "BL", "BayesB", "BayesC", "FIXED"});
	Group_Combo response_b = new Group_Combo("response_type", 
			new String[]{"gaussian", "ordinal"});
	Group_Combo niter_b = new Group_Combo("nIter", 
			new String[]{"1200", "1500", "2000", "5000", "12000"});
	Group_Combo burnin_b = new Group_Combo("burnIn",
			new String[]{"200", "500", "700", "1000", "2000"});
	Group_Combo thin_b = new Group_Combo("thin", 
			new String[]{"1", "2", "5", "10"}); // 5
	public void config_bglr() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			CovPane(ConfigFrame.C_exist, new String[]{"FIXED", "BRR", "BayesA", "BL", "BayesB", "BayesC", "OMIT IT"});
		// gwas
			GWASPane();
		// specific
			panel_args_b = new JPanel(new MigLayout("fillx"));
			panel_args_b.add(model_b.name, "cell 0 0, align r");
			panel_args_b.add(model_b.combo, "cell 1 0, align l");
			panel_args_b.add(response_b.name, "cell 0 1, align r");
			panel_args_b.add(response_b.combo, "cell 1 1, align l");
			panel_args_b.add(niter_b.name, "cell 0 2, align r");
			panel_args_b.add(niter_b.combo, "cell 1 2, align l");
			panel_args_b.add(burnin_b.name, "cell 0 3, align r");
			panel_args_b.add(burnin_b.combo, "cell 1 3, align l");
			panel_args_b.add(thin_b.name, "cell 0 4, align r");
			panel_args_b.add(thin_b.combo, "cell 1 4, align l");
		pane.addTab("Covariates", panel_cov);
		pane.addTab("GWAS-Assist",  panel_gwas);
		pane.addTab("BGLR input", panel_args_b);
		this.add(pane, "grow");		
	}	
	
	public void save (boolean isGWAS){
		if(isGWAS){
			// GAPIT
				iPatPanel.K_algoriithm = (String) K_algorithm.combo.getSelectedItem();
				iPatPanel.K_cluster = (String) K_cluster.combo.getSelectedItem();
				iPatPanel.K_group = (String) K_group.combo.getSelectedItem();
				iPatPanel.model_select = (String) model_select.combo.getSelectedItem();
				iPatPanel.snp_frac = (String) snp_frac.combo.getSelectedItem();
				//iPatPanel.file_frag = (String) file_frag.combo.getSelectedItem();
				iPatPanel.model_selection = model_selection.isSelected();
			// FarmCPU
				iPatPanel.method_bin = (String) method_bin.combo.getSelectedItem();
				iPatPanel.maxloop = (String) maxloop.combo.getSelectedItem();
			// PLINK
				iPatPanel.ci = (String) ci.combo.getSelectedItem();
		}else{
			// gBLUP
				iPatPanel.snp_frac = (String) snp_frac.combo.getSelectedItem();
				//iPatPanel.file_frag = (String) file_frag.combo.getSelectedItem();
				iPatPanel.model_selection = model_selection.isSelected();
			// rrBLUP
				iPatPanel.impute_method = (String) impute_method.combo.getSelectedItem();
				iPatPanel.shrink = shrink.isSelected();
			// BGLR
				iPatPanel.model_b = (String) model_b.combo.getSelectedItem();
				iPatPanel.response_b = (String) response_b.combo.getSelectedItem();
				iPatPanel.niter_b = (String) niter_b.combo.getSelectedItem();
				iPatPanel.burnin_b = (String) burnin_b.combo.getSelectedItem();
				iPatPanel.thin_b = (String) thin_b.combo.getSelectedItem();	
			// GWAS
				iPatPanel.bon = (String)bonferroni.combo.getSelectedItem();
				iPatPanel.enable = enable.isSelected();
		}	
	}
	public void load (boolean isGWAS){
		if(isGWAS){
			// GAPIT
				K_algorithm.combo.setSelectedItem(iPatPanel.K_algoriithm);
				K_cluster.combo.setSelectedItem(iPatPanel.K_cluster);
				K_group.combo.setSelectedItem(iPatPanel.K_group);
				model_select.combo.setSelectedItem(iPatPanel.model_select);
				snp_frac.combo.setSelectedItem(iPatPanel.snp_frac);
				//file_frag.combo.setSelectedItem(iPatPanel.file_frag);
				model_selection.setSelected(iPatPanel.model_selection);
			// FarmCPU
				method_bin.combo.setSelectedItem(iPatPanel.method_bin);
				maxloop.combo.setSelectedItem(iPatPanel.maxloop);
			// PLINK
				ci.combo.setSelectedItem(iPatPanel.ci);
		}else{
			// gBLUP
				snp_frac.combo.setSelectedItem(iPatPanel.snp_frac);
				//file_frag.combo.setSelectedItem(iPatPanel.file_frag);
				model_selection.setSelected(iPatPanel.model_selection);
			// rrBLUP
				impute_method.combo.setSelectedItem(iPatPanel.impute_method);
				shrink.setSelected(iPatPanel.shrink);
			// BGLR
				model_b.combo.setSelectedItem(iPatPanel.model_b);
				response_b.combo.setSelectedItem(iPatPanel.response_b);
				niter_b.combo.setSelectedItem(iPatPanel.niter_b);
				burnin_b.combo.setSelectedItem(iPatPanel.burnin_b);
				thin_b.combo.setSelectedItem(iPatPanel.thin_b);
			// GWAS
				bonferroni.combo.setSelectedItem(iPatPanel.bon);
				enable.setSelected(iPatPanel.enable);
		}
	}
	public void restore (boolean isGWAS){
		if(isGWAS){
			// GAPIT
				K_algorithm.combo.setSelectedItem(iPatPanel.df_K_algoriithm);
				K_cluster.combo.setSelectedItem(iPatPanel.df_K_cluster);
				K_group.combo.setSelectedItem(iPatPanel.df_K_group);
				model_select.combo.setSelectedItem(iPatPanel.df_model_select);
				snp_frac.combo.setSelectedItem(iPatPanel.df_snp_frac);
				//file_frag.combo.setSelectedItem(iPatPanel.df_file_frag);
				model_selection.setSelected(iPatPanel.df_model_selection);
			// FarmCPU
				method_bin.combo.setSelectedItem(iPatPanel.df_method_bin);
				maxloop.combo.setSelectedItem(iPatPanel.df_maxloop);
			// PLINK
				ci.combo.setSelectedItem(iPatPanel.df_ci);
		}else{
			// gBLUP
				snp_frac.combo.setSelectedItem(iPatPanel.df_snp_frac);
				//file_frag.combo.setSelectedItem(iPatPanel.df_file_frag);
				model_selection.setSelected(iPatPanel.df_model_selection);
			// rrBLUP
				impute_method.combo.setSelectedItem(iPatPanel.df_impute_method);
				shrink.setSelected(iPatPanel.df_shrink);
			// BGLR
				model_b.combo.setSelectedItem(iPatPanel.df_model_b);
				response_b.combo.setSelectedItem(iPatPanel.df_response_b);
				niter_b.combo.setSelectedItem(iPatPanel.df_niter_b);
				burnin_b.combo.setSelectedItem(iPatPanel.df_burnin_b);
				thin_b.combo.setSelectedItem(iPatPanel.df_thin_b);
			// GWAS
				bonferroni.combo.setSelectedItem(iPatPanel.df_bon);
				enable.setSelected(iPatPanel.df_enable);
		}
	}
	
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

class Fade_timer extends Timer{
	boolean actived = false, out = false;
	Timer timer_in, timer_out;
	public Fade_timer(int delay, AlphaLabel label, ActionListener listener) {
		super(delay, listener);
		timer_in = new Timer(30, new ActionListener() {
			float alpha = 0;
			@Override
		    public void actionPerformed(ActionEvent ae) {
		    		if(label.getAlpha() <.9f){
		    			alpha += .1f;
		    			label.setAlpha(alpha);
		    		}else{
		    			timer_in.stop();
		    			out = true;
		    		}					
		    }
		});
		timer_out = new Timer(30, new ActionListener() {
			float alpha = 1.0f;
			@Override
		    public void actionPerformed(ActionEvent ae) {
		    		if(label.getAlpha() >.1f){
		    			alpha -= .1f;
		    			label.setAlpha(alpha);
		    		}else{
		    			label.setAlpha(0f);
		    			timer_out.stop();
		    		}				
		    }
		});
	}
	public void fade_in(){
		timer_in.start();
	}
	public void fade_out(){
		timer_out.start();
	}
	public void setActived(boolean active){
		actived = active;
	}
	public boolean isActived(){
		return actived;
	}
	public boolean TimeToOut(){
		return out;
	}
}

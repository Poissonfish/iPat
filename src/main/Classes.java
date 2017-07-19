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
		Configuration.runtime[MOindex] = Runtime.getRuntime();
		
		// Execute the command
		try{Configuration.process[MOindex] = Configuration.runtime[MOindex].exec(command);
    	}catch (IOException e1) {e1.printStackTrace();}	
		
		if(iPatPanel.debug){
			// Print command	
			System.out.println("print debug command");
			for (int i = 0; i<command.length; i++){iPatPanel.text_console[MOindex].append(command[i]+" ");}
	    }
		     	
        iPatPanel.text_console[MOindex].append(System.getProperty("line.separator"));
        iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());	   	

        try {
        	// Print output message to the panel
        	System.out.println("begin to print, "+MOindex);
    	    BufferedReader input_stream = new BufferedReader(new InputStreamReader(Configuration.process[MOindex].getInputStream()));
            BufferedReader error_stream = new BufferedReader(new InputStreamReader(Configuration.process[MOindex].getErrorStream()));
	        while((line = input_stream.readLine()) != null){
	            iPatPanel.text_console[MOindex].append(line+ System.getProperty("line.separator"));
	            iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());
	        }
	        Configuration.process[MOindex].waitFor();        
        	
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
		Configuration.process[MOindex].destroy();
		// Run next procedure if needed
		if(con_command!=null){
    	  	iPatPanel.multi_run[MOindex] = new BGThread(MOindex, con_command, null);
    	  	iPatPanel.multi_run[MOindex].start();
		}
	}
}

class ConfigPane extends JPanel{
	JLabel msg = new JLabel("<html><center>Drag Here<br> a Method</center></html>", SwingConstants.CENTER);
	boolean isDeployed = false;
	ConfigFrame.method existmethod = ConfigFrame.method.NA;
	
	public ConfigPane(){
		this.setOpaque(true);
		msg.setFont(new Font("Ariashowpril", Font.PLAIN, 30));
		this.setBackground(Color.decode("#D0EDE8"));
		this.setLayout(new MigLayout("", "[grow]", "[grow]"));
		this.add(msg, "grow");	
	}
	public void MethodClear(){
		this.removeAll();
		this.setLayout(new MigLayout("", "[grow]", "[grow]"));
		this.add(msg, "grow");	
		isDeployed = false;
	}
	public void MethodEnter(){
		this.setBackground(Color.decode("#D0EDDA"));
		msg.setText("<html><center> Drop <br> to <br> Deploy </center></html>");
	}
	public void MethodAbsent(){
		existmethod = ConfigFrame.method.NA;
		this.setBackground(Color.decode("#D0EDE8"));
		msg.setText("<html><center>Drag Here<br> a Method</center></html>");
	}
	public void MethodDrop(ConfigFrame.method method){
		existmethod = method;
		this.setBackground(Color.decode("#D0E4ED"));
		msg.setText("<html><center>"+ method +"<br> Selected <br> (Tap for details) </center></html>");
		isDeployed = true;
	}
	public String[] MethodCommand(){
		// Get common information
			String[] command_common = {
					ConfigFrame.project_name.field.getText(), 
					ConfigFrame.wd_path.field.getText(), 
					iPatPanel.jar.getParent()+"/libs/",
				iPatPanel.format.Name(),
					(String)ConfigFrame.ms_qc.combo.getSelectedItem(),
					(String)ConfigFrame.maf_qc.combo.getSelectedItem(), 
					ConfigFrame.path_P, ConfigFrame.panel_phenotype.getSelected(ConfigFrame.trait_names),
					ConfigFrame.path_G, 
					ConfigFrame.path_M, 
					ConfigFrame.path_C, ConfigFrame.C_exist? panel_cov.getSelected():"NA",
					ConfigFrame.path_K,
					ConfigFrame.path_FAM, 
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
							(String)model_select.combo.getSelectedItem(), 
							(String)K_cluster.combo.getSelectedItem(),
							(String)K_group.combo.getSelectedItem(),
							(String)snp_frac.combo.getSelectedItem(),
							(String)file_frag.combo.getSelectedItem(),
							model_selection.isSelected()?"TRUE":"FALSE"};
					break;
				case FarmCPU:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_FarmCPU.R"};
					command_specific = new String[]{
							(String)method_bin.combo.getSelectedItem(),
							(String)maxloop.combo.getSelectedItem()
							};
					break;
				case PLINK:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_PLINK.R"};
					command_specific = new String[]{
							(String)ci.combo.getSelectedItem()
							};
					break;
				case gBLUP:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_gBLUP.R"};
					command_specific = new String[]{
							(String)snp_frac.combo.getSelectedItem(),
							(String)file_frag.combo.getSelectedItem(),
							model_selection.isSelected()?"TRUE":"FALSE"};
					break;
				case rrBLUP:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_rrBLUP.R"};
					command_specific = new String[]{
							(String)impute_method.combo.getSelectedItem(),
							shrink.isSelected()?"TRUE":"FALSE"};
					break;
				case BGLR:
					command_exe = new String[]{
							ConfigFrame.R_exe,
							iPatPanel.jar.getParent()+"/libs/iPat_BGLR.R"};
					command_specific = new String[]{
							(String)model_b.combo.getSelectedItem(),
							(String)response_b.combo.getSelectedItem(),
							(String)niter_b.combo.getSelectedItem(),
							(String)burnin_b.combo.getSelectedItem(),							
							(String)thin_b.combo.getSelectedItem()
					};
					break;
			}
		
		// combine whole command
			String[] command =  ArrayUtils.addAll(command_exe, ArrayUtils.addAll(command_common, command_specific));
			return command;
	}
	
	// Common used
	JTabbedPane pane = new JTabbedPane();
	covPanel panel_cov;
	String CO_head;
	String[] CO_names;
	
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
			if(ConfigFrame.C_exist){
				CO_head = iPatPanel.read_lines(iPatPanel.TBfile[ConfigFrame.C_index], 1)[0];
				CO_names = CO_head.split("\t");
				panel_cov = new covPanel(CO_names.length, CO_names, new String[]{"Selected", "Excluded"});
				panel_cov.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
				pane.addTab("Covariates", panel_cov);
			}else{
				JPanel panel_unable = new JPanel();
				pane.addTab("Covariates", panel_unable);
				panel_unable.setEnabled(false);
			}
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
			panel_advance.add(file_frag.name, "cell 0 1, align r");
			panel_advance.add(file_frag.combo, "cell 1 1, align l");
			panel_advance.add(model_selection, "cell 0 2 2 1, align c");
			pane.addTab("GAPIT input", panel_gapit);
			pane.addTab("Advance", panel_advance);
		this.add(pane, "grow");
	}
	public void config_gblup() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			if(ConfigFrame.C_exist){
				CO_head = iPatPanel.read_lines(iPatPanel.TBfile[ConfigFrame.C_index], 1)[0];
				CO_names = CO_head.split("\t");
				panel_cov = new covPanel(CO_names.length, CO_names, new String[]{"Selected", "Excluded"});
				panel_cov.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
				pane.addTab("Covariates", panel_cov);
			}
		// specific
			panel_advance = new JPanel(new MigLayout("fillx"));
			panel_advance.add(snp_frac.name, "cell 0 0, align r");
			panel_advance.add(snp_frac.combo, "cell 1 0, align l");
			panel_advance.add(file_frag.name, "cell 0 1, align r");
			panel_advance.add(file_frag.combo, "cell 1 1, align l");
			panel_advance.add(model_selection, "cell 0 2 2 1, align c");
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
			if(ConfigFrame.C_exist){
				CO_head = iPatPanel.read_lines(iPatPanel.TBfile[ConfigFrame.C_index], 1)[0];
				CO_names = CO_head.split("\t");
				panel_cov = new covPanel(CO_names.length, CO_names, new String[]{"Selected", "Excluded"});
				panel_cov.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
				pane.addTab("Covariates", panel_cov);
			}
		// specific
			panel_farm = new JPanel(new MigLayout("fillx"));
			panel_farm.add(method_bin.name, "cell 0 0, align r");
			panel_farm.add(method_bin.combo, "cell 1 0, align l");
			panel_farm.add(maxloop.name, "cell 0 1, align r");
			panel_farm.add(maxloop.combo, "cell 1 1, align l");
			panel_farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Input Arguments", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
			pane.addTab("FarmCPU input",  panel_farm);
		this.add(pane, "grow");
	}
	
	JPanel panel_plink;
	Group_Combo ci = new Group_Combo("C.I.",
			new String[]{"95%", "97.5%", "99.5%"}); 
	public void config_plink() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			if(ConfigFrame.C_exist){
				CO_head = iPatPanel.read_lines(iPatPanel.TBfile[ConfigFrame.C_index], 1)[0];
				CO_names = CO_head.split("\t");
				panel_cov = new covPanel(CO_names.length, CO_names, new String[]{"Selected", "Excluded"});
				panel_cov.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
				pane.addTab("Covariates", panel_cov);
			}
		// specific
			panel_plink = new JPanel(new MigLayout("fillx"));
			panel_plink.add(ci.name, "cell 0 0, align r");
			panel_plink.add(ci.combo, "cell 1 0, align l");
			panel_plink.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Input Arguments", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
			pane.addTab("PLINK input", panel_plink);
		this.add(pane, "grow");
	}
	
	JPanel panel_rrblup;
	Group_Combo impute_method = new Group_Combo("impute.method", 
			new String[]{"mean", "EM"});
	JCheckBox shrink = new JCheckBox("Shrinkage estimation");
	public void config_rrblup() throws IOException{
		this.removeAll();
		pane = new JTabbedPane();
		// cov
			if(ConfigFrame.C_exist){
				CO_head = iPatPanel.read_lines(iPatPanel.TBfile[ConfigFrame.C_index], 1)[0];
				CO_names = CO_head.split("\t");
				panel_cov = new covPanel(CO_names.length, CO_names, new String[]{"Selected", "Excluded"});
				panel_cov.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
				pane.addTab("Covariates", panel_cov);
			}
		// specific
			panel_rrblup = new JPanel(new MigLayout("fillx"));
			panel_rrblup.add(impute_method.name, "cell 0 0, align r");
			panel_rrblup.add(impute_method.combo, "cell 1 0, align l");
			panel_rrblup.add(shrink, "cell 0 1");
			panel_rrblup.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Input Arguments", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
			pane.addTab("rrBLUP input", panel_rrblup);
		this.add(pane, "grow");
	}
	
	String[] bglr_model = {"FIXED", "BRR", "BayesA", "BL", "BayesB", "BayesC", "OMIT IT"};
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
			if(ConfigFrame.C_exist){
				CO_head = iPatPanel.read_lines(iPatPanel.TBfile[ConfigFrame.C_index], 1)[0];
				CO_names = CO_head.split("\t");
				panel_cov = new covPanel(CO_names.length, CO_names, bglr_model);
				panel_cov.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Covariates", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
				pane.addTab("Covariates", panel_cov);
			}
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
			panel_args_b.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Input Arguments", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));	
			pane.addTab("BGLR input", panel_args_b);
		this.add(pane, "grow");		
	}	
	
	public void save(){
		
	}
	public void load(){
		
	}
	public void reset(){
		
	}
	
}

class covPanel extends JPanel{
	public Group_Combo[] CO;
	public int size;
	public covPanel(int size, String[] co_names, String[] methods){
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

class ListPanel extends JPanel implements ActionListener{
	JList list_selected, list_excluded;
	JButton button_excluded, button_include;
	SortedListModel selected, excluded;
	
	public void addElements(String[] elements){
		selected.addAll(elements);
	}
	public void addElement(String element){
		selected.add(element);
	}
	public String[] getElement(){
		String[] out = new String[selected.getSize()];
		for(int i = 0; i < out.length; i++){
			out[i] = (String)selected.getElementAt(i);
		}
		return out;
	}
	
	MigLayout layout = new MigLayout("fill", "[grow]", "[grow][grow]");
	JPanel panel_included = new JPanel(layout);
    JScrollPane scroll_included= new JScrollPane(list_selected,  
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    JPanel panel_excluded = new JPanel(layout);
    JScrollPane scroll_excluded = new JScrollPane(list_excluded, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    JPanel panel_buttons = new JPanel(new MigLayout("fill", "[]", "[grow][grow]"));
	public ListPanel(String name1, String name2){  
        selected = new SortedListModel();
        excluded = new SortedListModel();
        
        list_selected = new JList(selected);
        list_selected.setVisibleRowCount(5);
        list_selected.setFixedCellWidth(80);
        list_selected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        list_excluded = new JList(excluded);
        list_excluded.setVisibleRowCount(5);
        list_excluded.setFixedCellWidth(80);
        list_excluded.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                  
        scroll_included.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive);
        panel_included.add(new JLabel(name1), "cell 0 0, grow");
        panel_included.add(scroll_included, "cell 0 1, grow");

        scroll_excluded .getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive);
        panel_excluded.add(new JLabel(name2), "cell 0 0, grow");
        panel_excluded.add(scroll_excluded , "cell 0 1, grow");   
              
        button_excluded = new JButton(">>");
        button_include= new JButton("<<");
        
        panel_buttons.add(button_excluded, "cell 0 0, align c");
        panel_buttons.add(button_include, "cell 0 1, align c");
        button_excluded.addActionListener(this);
        button_include.addActionListener(this);

        this.setLayout(new MigLayout("fill", "[grow][grow][grow]", "[grow]"));
        this.add(panel_included, "cell 0 0, grow, align r");
        this.add(panel_buttons, "cell 1 0, grow, align c, w 120!");
        this.add(panel_excluded, "cell 2 0, grow, align l");
	}
	
	public String getSelected(String[] trait_names){
		String index_p = "";
		String[] out = this.getElement();
		for (int i = 0; i < out.length; i++){index_p = index_p + Integer.toString(Arrays.asList(trait_names).indexOf(out[i]))+ "sep";}
		return index_p;
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
        if(e.getSource() == button_excluded){
            Object[] value = list_selected.getSelectedValues();
            excluded.addAll(value);
            for (int i = value.length - 1; i >= 0; --i) {
            	selected.removeElement(value[i]);
            }
            list_selected.getSelectionModel().clearSelection();
        }else if(e.getSource() == button_include){
        	Object[] value = list_excluded.getSelectedValues();
            selected.addAll(value);
            for (int i = value.length - 1; i >= 0; --i) {
            	excluded.removeElement(value[i]);
            }
            list_excluded.getSelectionModel().clearSelection();
        }
	}
}

class SortedListModel extends AbstractListModel {
	  SortedSet<Object> model;
	  public SortedListModel() {
	    model = new TreeSet<Object>();
	  }
	  public int getSize() {
	    return model.size();
	  }
	  public Object getElementAt(int index) {
	    return model.toArray()[index];
	  }
	  public void add(Object element) {
	    if (model.add(element)) {
	      fireContentsChanged(this, 0, getSize());
	    }
	  }
	  public void addAll(Object elements[]) {
	    Collection<Object> c = Arrays.asList(elements);
	    model.addAll(c);
	    fireContentsChanged(this, 0, getSize());
	  }
	  public void clear() {
	    model.clear();
	    fireContentsChanged(this, 0, getSize());
	  }
	  public boolean removeElement(Object element) {
	    boolean removed = model.remove(element);
	    if (removed) {
	      fireContentsChanged(this, 0, getSize());
	    }
	    return removed;
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

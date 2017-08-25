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

public class ConfigFrame extends JFrame implements ActionListener{
	int MOindex = -1, iIndex = -1, gr_index = -1,
		C_provided = 0, K_provided = 0;
	boolean isGWAS;
	iPatObject[] ob;
	iPatProject[] pro;
	public static iPatProject.Format format;
	JPanel pane_main;
	JTabbedPane pane_top;
		// WD panel
		public static JPanel panel_wd;
		public static Group_Value project_name = new Group_Value("Project name");
		public static Group_Path wd_path = new Group_Path("Output Directory");
		public static JLabel project_format = new JLabel("");
		// Phenotype
		public static JScrollPane scroll_phenotype;
		// COV panel
		public static JScrollPane scroll_cov;
		// QC panel
		public static JPanel panel_qc;
		public static Group_Combo ms_qc = new Group_Combo("By missing rate", 
				new String[]{"No_threshold", "0.2", "0.1", "0.05"});
		public static Group_Combo maf_qc = new Group_Combo("By MAF", 
				new String[]{"No_threshold", "0.05", "0.1", "0.2"});	
	ConfigPane pane_config;
	JButton bottom_restore = new JButton("Restore Defaults");
		
	int NumOfMethod = 6;
	MLabel[] label_method = new MLabel[NumOfMethod];
	Point tempLabel = new Point(-1, -1);
	Point pt;
	
	// COV pane
				String CO_head;
				String[] CO_names;
	// For Command used
	public static String 	path_P = "NA", path_G = "NA", path_M = "NA", 
							path_C = "NA", path_K = "NA",
							path_FAM = "NA", path_BIM = "NA";
	public static boolean C_exist = false, K_exist = false;
	public static int C_index = 0, K_index = 0;
	
	iPatProject.Method[] ListGWAS = {iPatProject.Method.GAPIT, iPatProject.Method.FarmCPU, iPatProject.Method.PLINK}, 
						 ListGS = {iPatProject.Method.gBLUP, iPatProject.Method.rrBLUP, iPatProject.Method.BGLR};
	iPatProject.Method indexDrag = iPatProject.Method.NA;
	
	public ConfigFrame(int iIndex,  iPatObject[] ob, int MOindex, iPatProject[] pro, boolean isGWAS) throws IOException{
		this.iIndex = iIndex;
		this.ob = ob;
		gr_index = ob[iIndex].getGroupIndex();
		this.MOindex = MOindex;
		this.pro = pro;
		this.format = pro[MOindex].format;
		this.isGWAS = isGWAS;
		initialize();
		// Catch primary files
			int index_p = iPatPanel.getIndexofType(gr_index, iPatObject.Filetype.P), 
				index_gd = iPatPanel.getIndexofType(gr_index, iPatObject.Filetype.GD),
				index_gm = iPatPanel.getIndexofType(gr_index, iPatObject.Filetype.GM),
				index_fam = iPatPanel.getIndexofType(gr_index, iPatObject.Filetype.FAM),
				index_bim = iPatPanel.getIndexofType(gr_index, iPatObject.Filetype.BIM);
			path_P   = index_p   != -1 ? ob[index_p].getPath() : "NA";
			path_G 	 = index_gd  != -1 ? ob[index_gd].getPath() : "NA";
			path_M 	 = index_gm  != -1 ? ob[index_gm].getPath() : "NA";
			path_FAM = index_fam != -1 ? ob[index_fam].getPath() : "NA";
			path_BIM = index_bim != -1 ? ob[index_bim].getPath() : "NA";
		// Catch C and K
			for (int i : iPatPanel.getOBinGroup(gr_index)){
				System.out.println("Checking : " + i);
				switch(ob[i].type){
				case C: C_exist = true; C_index = i; path_C = ob[i].getPath(); break;
				case K: K_exist = true; K_index = i; path_K = ob[i].getPath(); break;}}

		// Replace MO icon to original one
			ob[iIndex].updateImage(iPatPanel.MOimage);
		// Bottom pane
			bottom_restore.addActionListener(this);
		// Config pane
			pane_config = new ConfigPane();
			pane_config.setBorder(BorderFactory.createLoweredBevelBorder());
		// Top (Common) pane
			pane_top = new JTabbedPane();
			// WD panel initialization
			    panel_wd = new JPanel(new MigLayout("fill", "[grow][grow][grow]"));
				panel_wd.add(project_name.name, "cell 0 0 1 1, grow");
				panel_wd.add(project_name.field, "cell 1 0 1 1");
				panel_wd.add(wd_path.name,  "cell 0 1 1 1, grow");
				panel_wd.add(wd_path.field, "cell 1 1 1 1");
				panel_wd.add(wd_path.browse, "cell 2 1 1 1");
				wd_path.browse.addActionListener(this);
			// Phenotype panel initialization
				// phenotype not applicable
				if(path_P.equals("NA")){
					pro[MOindex].trait_names = new String[]{"NA", "NA"};
					pro[MOindex].initial_phenotype(false);
					JPanel nullpanel = new JPanel(new MigLayout("", "[grow]", "[grow]"));
					JLabel na_msg = new JLabel("<html><center> Subsetting unavailable </center></html>", SwingConstants.CENTER);
					na_msg.setFont(new Font("Ariashowpril", Font.PLAIN, 18));
					nullpanel.add(na_msg, "grow");
					scroll_phenotype = new JScrollPane(nullpanel,
			                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);}
				// if never initialized
				else if(pro[MOindex].trait_names.length <= 1){
					String headline = iPatPanel.read_lines(path_P, 1)[0];
					pro[MOindex].trait_names = headline.split("\t").length <= 1 ? headline.split(" ") : headline.split("\t");
					// Other format with PLINK phenotype
					if(pro[MOindex].trait_names[0].toUpperCase().equals("FID"))
						pro[MOindex].initial_phenotype(true);						
					else{
						switch(format){
						case PLINK: case PLINK_bin:
							pro[MOindex].initial_phenotype(true); break;
						default:
							pro[MOindex].initial_phenotype(false); break;}}
					scroll_phenotype = new JScrollPane(pro[MOindex].panel_phenotype,
			                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);}
			// QC panel initialization
				panel_qc = new JPanel(new MigLayout("fillx"));
				panel_qc.add(ms_qc.name, "cell 0 0, align r");
				panel_qc.add(ms_qc.combo, "cell 1 0,align l");
				panel_qc.add(maf_qc.name, "cell 0 1, align r");
				panel_qc.add(maf_qc.combo, "cell 1 1, align l"); 
			pane_top.addTab("Working Directory", panel_wd);
			pane_top.addTab("Phenotype", scroll_phenotype);
			pane_top.addTab("Quality Control", panel_qc);
		// Main pane
			pane_main = new JPanel(new MigLayout("fill", "[grow][grow]", "[grow][grow][grow]"));
			if(isGWAS){
				pane_main.add(label_method[iPatProject.Method.GAPIT.getIndex()]   = new MLabel("GAPIT"), "cell 1 0, grow, w 150:150:");
				pane_main.add(label_method[iPatProject.Method.FarmCPU.getIndex()] = new MLabel("FarmCPU"), "cell 1 1, grow, w 150:150:");
				pane_main.add(label_method[iPatProject.Method.PLINK.getIndex()]   = new MLabel("PLINK"), "cell 1 2, grow, w 150:150:");
				pane_top.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), 
						"GWAS (Format: " + format + ")", TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));}
			else{
				pane_main.add(label_method[iPatProject.Method.gBLUP.getIndex()]  = new MLabel("<html> GAPIT <br> (gBLUP)</html>"), "cell 1 0, grow, w 150:150:");
				pane_main.add(label_method[iPatProject.Method.rrBLUP.getIndex()] = new MLabel("rrBLUP"), "cell 1 1, grow, w 150:150:");
				pane_main.add(label_method[iPatProject.Method.BGLR.getIndex()]   = new MLabel("BGLR"), "cell 1 2, grow, w 150:150:");
				pane_top.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), 
						"GS (Format: " + format + ")", TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));}        
			pane_main.add(pane_config, "cell 0 0 1 3, grow, w 470:470:, h 270:270:");
			pane_main.add(pane_top, "dock north, h 200:200:");
			pane_main.add(bottom_restore, "cell 0 3 2 1, align l");
		
		load();
		this.setLocation(600, 500);
		this.setContentPane(pane_main);
		this.setVisible(true);
		this.pack();
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				pt = e.getPoint();
				for(iPatProject.Method PressedMethod : (isGWAS ? ListGWAS:ListGS)){
					if(label_method[PressedMethod.getIndex()].getBounds().contains(pt)){
						indexDrag = PressedMethod; 
						tempLabel = label_method[PressedMethod.getIndex()].getLocation();
						break;}}	
				if(pane_config.isDeployed && pane_config.getBounds().contains(pt)){
					try { 
						switch(pane_config.existmethod){
							case GAPIT: 	pane_config.config_gapit(); break;
							case FarmCPU: 	pane_config.config_farm(); break;
							case PLINK: 	pane_config.config_plink(); break;
							case gBLUP: 	pane_config.config_gblup(); break;
							case rrBLUP: 	pane_config.config_rrblup(); break;
							case BGLR: 		pane_config.config_bglr(); break;}
						refresh();
					} catch (IOException e1) {e1.printStackTrace();}}
			}
			@Override
			public void mouseReleased(MouseEvent e){
				// Dragging a method
				if(indexDrag != iPatProject.Method.NA){
					// Drop in the panel
					if(pane_config.getBounds().contains(e.getPoint())){
						pane_config.MethodSelected(indexDrag);
						try {
						switch(indexDrag){
						case GAPIT: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
						case FarmCPU: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
						case PLINK: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
						case gBLUP: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
						case rrBLUP: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
						case BGLR: preCovPane(C_exist, new String[]{"FIXED", "BRR", "BayesA", "BL", "BayesB", "BayesC", "OMIT IT"}); break;
						}} catch (IOException e1) {e1.printStackTrace();}
					// Drop outside of the panel
					}else if(pane_config.isDeployed){
						pane_config.MethodSelected(pane_config.existmethod);
						try {
							switch(pane_config.existmethod){
							case GAPIT: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
							case FarmCPU: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
							case PLINK: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
							case gBLUP: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
							case rrBLUP: preCovPane(C_exist, new String[]{"Selected", "Excluded"}); break;
							case BGLR: preCovPane(C_exist, new String[]{"FIXED", "BRR", "BayesA", "BL", "BayesB", "BayesC", "OMIT IT"}); break;
							}} catch (IOException e1) {e1.printStackTrace();}
					}
					label_method[indexDrag.index].setLocation(tempLabel);
					indexDrag = iPatProject.Method.NA;
					tempLabel = new Point(-1, -1);}
			}
			
			public void preCovPane(boolean C_exist, String[] model_names) throws IOException{
				if(C_exist){
					System.out.println("CO from object " + iIndex);
					CO_head = iPatPanel.read_lines(ob[C_index].getPath(), 1)[0];
					CO_names = CO_head.split("\t");
					pro[MOindex].initial_cov(CO_names.length, CO_names, model_names);
					scroll_cov = new JScrollPane(pro[MOindex].panel_cov,
			                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);}
				else{
					System.out.println("NO CO found");
					pro[MOindex].initial_cov(0, new String[]{}, new String[]{"Selected", "Excluded"});
					pro[MOindex].panel_cov.setLayout(new MigLayout("", "[grow]", "[grow]"));
					JLabel na_co = new JLabel("<html><center> Covariates <br> Unavailable </center></html>", SwingConstants.CENTER);
					na_co.setFont(new Font("Ariashowpril", Font.PLAIN, 20));
					pro[MOindex].panel_cov.add(na_co, "grow");}	
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
                int dx = e.getX() - pt.x;
                int dy = e.getY() - pt.y;
                // Dragging a method
				if(indexDrag != iPatProject.Method.NA){
					// Entering panel
					if(pane_config.getBounds().contains(e.getPoint())){
						pane_config.Clear();
						pane_config.HintDrop();}
					// Outside of panel
					else{
						pane_config.Clear();
						pane_config.HintDrag();}
					label_method[indexDrag.index].setLocation(tempLabel.x + dx, tempLabel.y + dy);	
					repaint();}
            }
		});
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("closed");
				if(pane_config.isDeployed){
					if(isGWAS){
						pro[MOindex].command_gwas = pane_config.MethodCommand();
						pro[MOindex].setGWASmethod(pane_config.existmethod);}
					else{
						pro[MOindex].command_gs = pane_config.MethodCommand();
						pro[MOindex].setGSmethod(pane_config.existmethod);}}
				ob[iIndex].setLabel(project_name.field.getText());
				ob[iIndex].setPath(wd_path.field.getText());
				save();
			}
		});
	}
	@Override
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if(source == bottom_restore)
			restore();
		else if(source == wd_path.browse)
			wd_path.setPath(true);
	}
	void initialize(){
		C_exist = false;
		K_exist = false;
		C_index = 0;
		K_index = 0;
		C_provided = 0;
		K_provided = 0;
		CO_head = " ";
		CO_names = null;
		path_C = "NA";
		path_K = "NA";
	}
	public void refresh(){
		this.setVisible(true);
	}
	public void save(){
		ob[iIndex].name.setText(project_name.field.getText());
		iPatPanel.wd = wd_path.field.getText();
		iPatPanel.maf = (String) maf_qc.combo.getSelectedItem();
		iPatPanel.ms = (String) ms_qc.combo.getSelectedItem();
		pane_config.save(isGWAS);
	}
	public void load(){
		project_name.field.setText(ob[iIndex].name.getText());
		wd_path.field.setText(iPatPanel.wd);
		maf_qc.combo.setSelectedItem(iPatPanel.maf);
		ms_qc.combo.setSelectedItem(iPatPanel.ms);
		pane_config.load(isGWAS);
		if(isGWAS && pro[MOindex].isGWASDeployed())
			pane_config.MethodSelected(pro[MOindex].method_gwas);
		else if(!isGWAS && pro[MOindex].isGSDeployed())
			pane_config.MethodSelected(pro[MOindex].method_gs);
	}
	public void restore(){
		project_name.field.setText("Project_" + (MOindex + 1));
		wd_path.field.setText(iPatPanel.df_wd);
		maf_qc.combo.setSelectedItem(iPatPanel.df_maf);
		ms_qc.combo.setSelectedItem(iPatPanel.df_ms);
		pane_config.restore(isGWAS);
		pane_config.Clear();
		pane_config.RemoveMethod();
		pane_config.HintDrag();
		if(isGWAS)
			pro[MOindex].setGWASmethod(iPatProject.Method.NA);	
		else if(!isGWAS)
			pro[MOindex].setGSmethod(iPatProject.Method.NA);
	}
	public class MLabel extends JLabel {
		public MLabel(String name){
			this.setText(name);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setFont(new Font("Ariashowpril", Font.BOLD, 25));
			this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
	}
	class ConfigPane extends JPanel implements ActionListener{
		JLabel msg = new JLabel("", SwingConstants.CENTER);
		boolean isDeployed = false;
		iPatProject.Method existmethod = iPatProject.Method.NA;
		public ConfigPane(){
			this.setOpaque(true);
			msg.setFont(new Font("Ariashowpril", Font.PLAIN, 30));
			this.setLayout(new MigLayout("", "[grow]", "[grow]"));
			this.add(msg, "grow");	
			HintDrag();
		}
		public void RemoveMethod(){
			existmethod = iPatProject.Method.NA;
			isDeployed = false;
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
			msg.setText("<html><center> Drag a Package <br> Here  </center></html>");
		}
		public void MethodSelected(iPatProject.Method method){
			isDeployed = true;
			existmethod = method;
			this.setBackground(Color.decode("#D0E4ED"));
			msg.setText("<html><center>"+ method +"<br> Selected <br> (Tap for details) </center></html>");
		}
		public String[] MethodCommand(){
			// Get common information
				String[] command_common = {
						project_name.field.getText(), // 2
						wd_path.field.getText(), 
						iPatPanel.jar.getParent()+"/res/",
						pro[MOindex].format.getName(), 
						(String)ms_qc.combo.getSelectedItem(), 
						(String)maf_qc.combo.getSelectedItem(), // 7 
						path_P, pro[MOindex].panel_phenotype.getSelected(),
						path_G, 
						path_M, // 11
						path_C, C_exist ? pro[MOindex].panel_cov.getSelected() : "NA",
						path_K, 
						path_FAM, // 15
						path_BIM
				};
			// Get specific method
				String[] command_exe = null;
				String[] command_specific = null;
				switch(existmethod){
				case GAPIT:
					System.out.println("Path to " + Paths.get(iPatPanel.jar.getParent(), "res", "iPat_GAPIT.R").toString());
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_GAPIT.R").toString()};
					command_specific = new String[]{
							(String)model_select.combo.getSelectedItem(),  // 17
							(String)K_cluster.combo.getSelectedItem(),
							(String)K_group.combo.getSelectedItem(),
							(String)snp_frac.combo.getSelectedItem(),
							(String)file_frag.combo.getSelectedItem(), // 21
							model_selection.isSelected()?"TRUE":"FALSE"}; break;
				case FarmCPU:
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_FarmCPU.R").toString()};
					command_specific = new String[]{
							(String)method_bin.combo.getSelectedItem(),  // 17
							(String)maxloop.combo.getSelectedItem()}; break;
				case PLINK:
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_PLINK.R").toString()};
					command_specific = new String[]{
							(String)ci.combo.getSelectedItem(),  // 17
							"TRUE", 
							(String)model.combo.getSelectedItem()}; break;
				case gBLUP:
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_gBLUP.R").toString()};
					command_specific = new String[]{
							(String)snp_frac.combo.getSelectedItem(),  // 17
							(String)file_frag.combo.getSelectedItem(),
							model_selection.isSelected()?"TRUE":"FALSE",
							enable.isSelected()?"TRUE":"FALSE",
							(String)bonferroni.combo.getSelectedItem()}; break; // 21
				case rrBLUP:
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_rrBLUP.R").toString()};
					command_specific = new String[]{
							(String)impute_method.combo.getSelectedItem(),  // 17
							shrink.isSelected()?"TRUE":"FALSE",
							enable.isSelected()?"TRUE":"FALSE",
							(String)bonferroni.combo.getSelectedItem()}; break;
				case BGLR:
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_BGLR.R").toString()};
					command_specific = new String[]{
							(String)model_b.combo.getSelectedItem(),  // 17
							(String)response_b.combo.getSelectedItem(),
							(String)niter_b.combo.getSelectedItem(),
							(String)burnin_b.combo.getSelectedItem(),							
							(String)thin_b.combo.getSelectedItem(),  // 21
							enable.isSelected()?"TRUE":"FALSE",
							(String)bonferroni.combo.getSelectedItem()}; break;}
			// combine whole command
				String[] command =  ArrayUtils.addAll(command_exe, ArrayUtils.addAll(command_common, command_specific));
				return command;
		}
		// Common used
		JTabbedPane pane = new JTabbedPane();
		JPanel panel_gwas = new JPanel();
		JCheckBox enable = new JCheckBox("");
		Group_Combo bonferroni = new Group_Combo("Bonferroni cut-off",  
				new String[]{"0.05", "0.01", "0.005", "0.001", "0.0001"});
		// GWAS pane
		public void GWASPane(){
			if(pro[MOindex].isGWASDeployed()){
				panel_gwas.removeAll();	
				enable = new JCheckBox("Enable GWAS-Assisted feature (By " + pro[MOindex].method_gwas.getName() + ")");
				panel_gwas.setLayout(new MigLayout("fillx"));
				panel_gwas.add(enable, "wrap");
				panel_gwas.add(bonferroni.name);
				panel_gwas.add(bonferroni.combo, "wrap");
				enable.setSelected(true);
				enable.addActionListener(this);}
			else{
				panel_gwas.removeAll();	
				panel_gwas.setLayout(new MigLayout("", "[grow]", "[grow]"));
				JLabel na_msg = new JLabel("<html><center> GWAS-Assisted GS <br> Unavailable <br> Please select a GWAS method first </center></html>", SwingConstants.CENTER);
				na_msg.setFont(new Font("Ariashowpril", Font.PLAIN, 18));
				enable.setSelected(false);
				panel_gwas.add(na_msg, "grow");}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src == enable)
				bonferroni.combo.setEnabled(!bonferroni.combo.isEnabled());
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
			if(C_exist) pane.addTab("Covariates", scroll_cov);
			else pane.addTab("Covariates", pro[MOindex].panel_cov);
			pane.addTab("GAPIT input", panel_gapit);
			pane.addTab("Advance", panel_advance);
			this.add(pane, "grow");
		}
		public void config_gblup() throws IOException{
			this.removeAll();
			pane = new JTabbedPane();
			// gwas
				GWASPane();
			// specific
				panel_advance = new JPanel(new MigLayout("fillx"));
				panel_advance.add(snp_frac.name, "cell 0 0, align r");
				panel_advance.add(snp_frac.combo, "cell 1 0, align l");
				panel_advance.add(model_selection, "cell 0 1 2 1, align c");
			if(C_exist) pane.addTab("Covariates", scroll_cov);
			else pane.addTab("Covariates", pro[MOindex].panel_cov);
			pane.addTab("GWAS-Assist",  panel_gwas);
			pane.addTab("Advance", panel_advance);
			this.add(pane, "grow");
		}
		JPanel panel_farm;
		Group_Combo method_bin = new Group_Combo("Method bin", 
				new String[]{"static", "optimum"});
		Group_Combo maxloop = new Group_Combo("maxLoop", 
				new String[]{"10", "1", "2", "5", "20"});
		void config_farm() throws IOException{
			this.removeAll();
			pane = new JTabbedPane();
			// specific
				panel_farm = new JPanel(new MigLayout("fillx"));
				panel_farm.add(method_bin.name, "cell 0 0, align r");
				panel_farm.add(method_bin.combo, "cell 1 0, align l");
				panel_farm.add(maxloop.name, "cell 0 1, align r");
				panel_farm.add(maxloop.combo, "cell 1 1, align l");
			if(C_exist) pane.addTab("Covariates", scroll_cov);
			else pane.addTab("Covariates", pro[MOindex].panel_cov);
			pane.addTab("FarmCPU input", panel_farm);	
			this.add(pane, "grow");
		}		
		JPanel panel_plink;
		Group_Combo ci = new Group_Combo("C.I.",
				new String[]{"0.95", "0.975", "0.995"}); 
		Group_Combo model = new Group_Combo("Method", 
				new String[]{"GLM", "Logistic Regression"});
		void config_plink() throws IOException{
			this.removeAll();
			pane = new JTabbedPane();
			// specific
				panel_plink = new JPanel(new MigLayout("fillx"));
				panel_plink.add(ci.name, "cell 0 0, align r");
				panel_plink.add(ci.combo, "cell 1 0, align l");
				panel_plink.add(model.name, "cell 0 1, align r");
				panel_plink.add(model.combo, "cell 1 1, align l");
			if(C_exist) pane.addTab("Covariates", scroll_cov);
			else pane.addTab("Covariates", pro[MOindex].panel_cov);
			pane.addTab("PLINK input", panel_plink);
			this.add(pane, "grow");
		}
		//
		JPanel panel_rrblup;
		Group_Combo impute_method = new Group_Combo("impute.method", 
				new String[]{"mean", "EM"});
		JCheckBox shrink = new JCheckBox("Shrinkage estimation");
		void config_rrblup() throws IOException{
			this.removeAll();
			pane = new JTabbedPane();
			// gwas
				GWASPane();
			// specific
				panel_rrblup = new JPanel(new MigLayout("fillx"));
				panel_rrblup.add(impute_method.name, "cell 0 0, align r");
				panel_rrblup.add(impute_method.combo, "cell 1 0, align l");
				panel_rrblup.add(shrink, "cell 0 1, align c");
			if(C_exist) pane.addTab("Covariates", scroll_cov);
			else pane.addTab("Covariates", pro[MOindex].panel_cov);
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
		void config_bglr() throws IOException{
			this.removeAll();
			pane = new JTabbedPane();
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
			if(C_exist) pane.addTab("Covariates", scroll_cov);
			else pane.addTab("Covariates", pro[MOindex].panel_cov);
			pane.addTab("GWAS-Assist",  panel_gwas);
			pane.addTab("BGLR input", panel_args_b);
			this.add(pane, "grow");		
		}
		void save (boolean isGWAS){
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
					iPatPanel.ci = (String) ci.combo.getSelectedItem();}
			else{
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
					iPatPanel.enable = enable.isSelected();}	
		}
		void load (boolean isGWAS){
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
					ci.combo.setSelectedItem(iPatPanel.ci);}
			else{
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
					enable.setSelected(iPatPanel.enable);}
		}
		void restore (boolean isGWAS){
			if(isGWAS){
				// GAPIT
					K_algorithm.combo.setSelectedItem(iPatPanel.df_K_algoriithm);
					K_cluster.combo.setSelectedItem(iPatPanel.df_K_cluster);
					K_group.combo.setSelectedItem(iPatPanel.df_K_group);
					model_select.combo.setSelectedItem(iPatPanel.df_model_select);
					snp_frac.combo.setSelectedItem(iPatPanel.df_snp_frac);
					// file_frag.combo.setSelectedItem(iPatPanel.df_file_frag);
					model_selection.setSelected(iPatPanel.df_model_selection);
				// FarmCPU
					method_bin.combo.setSelectedItem(iPatPanel.df_method_bin);
					maxloop.combo.setSelectedItem(iPatPanel.df_maxloop);
				// PLINK
					ci.combo.setSelectedItem(iPatPanel.df_ci);}
			else{
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
					enable.setSelected(iPatPanel.df_enable);}
		}
	}
}
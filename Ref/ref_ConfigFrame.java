package main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;
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
		// COV panel
		public static JScrollPane scroll_cov;
		// QC panel
		public static JPanel panel_qc;

	ConfigPane pane_config;
	JButton bottom_restore = new JButton("Restore Defaults");
	
	// GAPIT, FarmCPU, PLINK, gBLUP, rrBLUP, BGLR, BSA
	int NumOfMethod = 7;
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
						 ListGS = {iPatProject.Method.gBLUP, iPatProject.Method.rrBLUP, iPatProject.Method.BGLR},
						 ListBSA = {iPatProject.Method.BSA};
	iPatProject.Method indexDrag = iPatProject.Method.NA;
	// GWAS-assist
	JCheckBox gwas_enable = new JCheckBox();
	
	public ConfigFrame(int iIndex,  iPatObject[] ob, int MOindex, iPatProject[] pro, boolean isGWAS, boolean isBSA) throws IOException{
		this.iIndex = iIndex;
		this.ob = ob;
		this.gr_index = ob[iIndex].getGroupIndex();
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

		// Top (Common) pane

		load();

		this.addWindowListener (new WindowAdapter() {
			@Override
			public void windowClosing (WindowEvent e) {
				System.out.println("closed");
				if(pane_config.isDeployed){
					if (isGWAS) { 
						pro[MOindex].command_gwas = pane_config.MethodCommand();
						if(pro[MOindex].isGSDeployed()) {
							pro[MOindex].command_gs[2] = pro[MOindex].command_gwas[2]; 
							pro[MOindex].command_gs[3] = pro[MOindex].command_gwas[3]; 
						}
						pro[MOindex].setGWASmethod(pane_config.existmethod);
					} else if (!isGWAS && !isBSA) {
						pro[MOindex].command_gs = pane_config.MethodCommand();
						if(pro[MOindex].isGWASDeployed()) {
							pro[MOindex].command_gwas[2] = pro[MOindex].command_gs[2];
							pro[MOindex].command_gwas[3] = pro[MOindex].command_gs[3];
						}
						pro[MOindex].setGSmethod(pane_config.existmethod);
					} else {
						pro[MOindex].command_bsa = pane_config.MethodCommand();
						pro[MOindex].setBSAmethod(true);
					}
				}
				ob[iIndex].setLabel(project_name.field.getText());
				ob[iIndex].setPath(wd_path.field.getText());
				save();
			}
		});
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
				switch (existmethod) {
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
							(String) snp_frac.combo.getSelectedItem(),  // 17
							(String) file_frag.combo.getSelectedItem(),
							model_selection.isSelected() ? "TRUE" : "FALSE",
							gwas_enable.isSelected() ? "TRUE" : "FALSE",
							(String) bonferroni.combo.getSelectedItem()}; break; // 21
				case rrBLUP:
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_rrBLUP.R").toString()};
					command_specific = new String[]{
							(String) impute_method.combo.getSelectedItem(),  // 17
							shrink.isSelected()?"TRUE":"FALSE",
							gwas_enable.isSelected()?"TRUE":"FALSE",
							(String) bonferroni.combo.getSelectedItem()}; break;
				case BGLR:
					command_exe = new String[]{
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_BGLR.R").toString()};
					command_specific = new String[]{
							(String) model_b.combo.getSelectedItem(),  // 17
							(String) response_b.combo.getSelectedItem(),
							(String) niter_b.combo.getSelectedItem(),
							(String) burnin_b.combo.getSelectedItem(),							
							(String) thin_b.combo.getSelectedItem(),  // 21
							gwas_enable.isSelected() ? "TRUE" : "FALSE",
							(String) bonferroni.combo.getSelectedItem()}; break;
				case BSA:
					command_exe = new String[] {
							iPat.R_exe,
							Paths.get(iPatPanel.jar.getParent(), "res", "iPat_BSA.R").toString()};
					command_specific = new String[]{
							(String) window_bsa.combo.getSelectedItem(),  // 17
							(String) pow_bsa.combo.getSelectedItem()};
							break;
				}
				// combine whole command
				String[] command =  ArrayUtils.addAll(command_exe, ArrayUtils.addAll(command_common, command_specific));
				return command;
		}
		// Common used
		JTabbedPane pane = new JTabbedPane();
		JPanel panel_gwas = new JPanel();
		Group_Combo bonferroni = new Group_Combo("Bonferroni cut-off",  
				new String[]{"0.05", "0.01", "0.005", "0.001", "0.0001"});
		// GWAS pane
		public void GWASPane(){
			if (pro[MOindex].isGWASDeployed()){
				panel_gwas.removeAll();	
				gwas_enable = new JCheckBox("Enable GWAS-Assisted feature (By " + pro[MOindex].method_gwas.getName() + ")");
				panel_gwas.setLayout(new MigLayout("fillx"));
				panel_gwas.add(gwas_enable, "wrap");
				panel_gwas.add(bonferroni.name);
				panel_gwas.add(bonferroni.combo, "wrap");
				gwas_enable.setSelected(true);
				gwas_enable.addActionListener(this);}
			else {
				panel_gwas.removeAll();	
				panel_gwas.setLayout(new MigLayout("", "[grow]", "[grow]"));
				JLabel na_msg = new JLabel("<html><center> GWAS-Assisted GS <br> Unavailable <br> Please select a GWAS method first </center></html>", SwingConstants.CENTER);
				na_msg.setFont(new Font("Ariashowpril", Font.PLAIN, 18));
				gwas_enable.setSelected(false);
				panel_gwas.add(na_msg, "grow");}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src == gwas_enable)
				bonferroni.combo.setEnabled(!bonferroni.combo.isEnabled());
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

		void save (boolean isGWAS){
			if (isGWAS) {
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
			} else {
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
					iPatPanel.bon = (String) bonferroni.combo.getSelectedItem();
					iPatPanel.enable = gwas_enable.isSelected();
			}
			// BSA
				iPatPanel.window_bsa = (String) window_bsa.combo.getSelectedItem();
				iPatPanel.pow_bsa = (String) pow_bsa.combo.getSelectedItem();
			System.out.println("selected? : " + gwas_enable.isSelected());
		}
		void load (boolean isGWAS) {
			if (isGWAS) {
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
			} else {
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
					gwas_enable.setSelected(false);
			}
			// BSA
			window_bsa.combo.setSelectedItem(iPatPanel.window_bsa);
			pow_bsa.combo.setSelectedItem(iPatPanel.pow_bsa);
		}
		void restore (boolean isGWAS) {
			if (isGWAS) {
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
					ci.combo.setSelectedItem(iPatPanel.df_ci);
			} else {
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
					gwas_enable.setSelected(iPatPanel.df_enable);
			}
			window_bsa.combo.setSelectedItem(iPatPanel.df_window_bsa);
			pow_bsa.combo.setSelectedItem(iPatPanel.df_pow_bsa);
		}
	}
}
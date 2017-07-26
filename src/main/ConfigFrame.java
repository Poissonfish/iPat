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
	public static int MOindex = -1, C_provided = 0, K_provided = 0;
	public static analysis Analysis;
	public static iPatPanel.FORMAT format;
	JPanel pane_main;
	JTabbedPane pane_top;
		// WD panel
		public static JPanel panel_wd;
		public static Group_Value project_name = new Group_Value("Project name");
		public static Group_Path wd_path = new Group_Path("Output Directory");
		public static JLabel project_format = new JLabel("");
		// Phenotype
		public static JScrollPane scroll_phenotype;
		// QC panel
		public static JPanel panel_qc;
		public static Group_Combo ms_qc = new Group_Combo("By missing rate", 
				new String[]{"No threshold", "0.2", "0.1", "0.05"});
		public static Group_Combo maf_qc = new Group_Combo("By MAF", 
				new String[]{"No threshold", "0.05", "0.1", "0.2"});	
	ConfigPane pane_config;
	JButton bottom_restore = new JButton("Restore Defaults");
		
	int NumOfMethod = 6;
	MLabel[] label_method = new MLabel[NumOfMethod];
	Point tempLabel = new Point(-1, -1);
	Point pt;
	
	// For Command used
	public static String 	path_P = "NA", path_G = "NA", path_M = "NA", 
							path_C = "NA", path_K = "NA",
							path_FAM = "NA", path_BIM = "NA", 
							R_exe = "NA";
	public static boolean C_exist = false, K_exist = false;
	public static int C_index = 0, K_index = 0;
	
	public static enum method {
		NA(-1, "NA"), GAPIT(0, "GAPIT"), FarmCPU(1, "FarmCPU"), PLINK(2, "PLINK"), gBLUP(3, "gBLUP"), rrBLUP(4, "rrBLUP"), BGLR(5, "BGLR");
		int index;
		String name;
		private method(int index, String name){this.name= name; this.index = index;}
		public int index(){return this.index;}
		public String Name(){return this.name;}
	}
	public static enum analysis {
		GWAS(0), GS(1);
		int index;
		private analysis(int index){this.index = index;}
		public int index(){return this.index;}
	}
	public static method[]  ListGWAS = {method.GAPIT, method.FarmCPU, method.PLINK}, 
							ListGS = {method.gBLUP, method.rrBLUP, method.BGLR};
	public method indexDrag = method.NA;
	
	public ConfigFrame(int MOindex, analysis Analysis, iPatPanel.FORMAT format, Findex[] file_index) throws IOException{
		this.MOindex = MOindex;
		this.Analysis = Analysis;
		this.format = format;
		// Catch primary files
			for(int i = 0; i < iPatPanel.maxfile; i++){
				switch(file_index[i].file){
					case P: 						path_P = iPatPanel.TBfile[file_index[i].tb]; break;
					case GD: case VCF: case BED:	path_G = iPatPanel.TBfile[file_index[i].tb]; break;
					case GM:						path_M = iPatPanel.TBfile[file_index[i].tb]; break;
					case FAM:						path_FAM = iPatPanel.TBfile[file_index[i].tb]; break;
					case BIM:						path_BIM  = iPatPanel.TBfile[file_index[i].tb]; break;
				}
			}
		// Catch C and K
			for (int i = 1; i <= iPatPanel.TBcount; i++){
				if(iPatPanel.TBco[i][3] == iPatPanel.MOco[MOindex][3] && iPatPanel.TBco[i][3] != -1){ //catch file in the same group
					switch(iPatPanel.TBtype[i]){
						case C: C_exist = true; C_index = i; path_C = iPatPanel.TBfile[C_index]; break;
						case K: K_exist = true; K_index = i; path_K = iPatPanel.TBfile[K_index]; break;
					}
				}
			}
		// Catch R exe path
			switch(iPat.UserOS.type){
				case Windows: 	R_exe = "Rscript"; break;
				case Mac: 		R_exe = "/usr/local/bin/Rscript"; break;
				case Linux: 	R_exe = "/usr/local/bin/Rscript"; break;
			}
		// Replace MO icon to original one
			iPatPanel.MO[MOindex] = iPatPanel.MOimage;
		// Bottom pane
			bottom_restore.addActionListener(this);
			//bottom_restore.setFont(new Font("Ariashowpril", Font.PLAIN, 30));
		// Config pane
			pane_config = new ConfigPane(MOindex);
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
				String headline = iPatPanel.read_lines(path_P, 1)[0];
				// if never initialized
				if(iPatPanel.trait_names[MOindex].length <= 1){
					iPatPanel.trait_names[MOindex] = headline.split("\t").length <= 1 ? headline.split(" ") : headline.split("\t");
					iPatPanel.panel_phenotype[MOindex] = new selectablePanel(iPatPanel.trait_names[MOindex].length - 1,
														  ArrayUtils.remove(iPatPanel.trait_names[MOindex], 0), 
														  new String[]{"Selected", "Excluded"});}
				scroll_phenotype = new JScrollPane(iPatPanel.panel_phenotype[MOindex],
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
			switch(Analysis){
				case GWAS:
					label_method[method.GAPIT.index()] = new MLabel("GAPIT");
					label_method[method.FarmCPU.index()] = new MLabel("FarmCPU");
					label_method[method.PLINK.index()] = new MLabel("PLINK");
					pane_main.add(label_method[method.GAPIT.index()], "cell 1 0, grow, w 150:150:");
					pane_main.add(label_method[method.FarmCPU.index()], "cell 1 1, grow, w 150:150:");
					pane_main.add(label_method[method.PLINK.index()], "cell 1 2, grow, w 150:150:");
					pane_top.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), 
							"GWAS (Format: " + iPatPanel.format + ")", TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
					break;
				case GS:
					label_method[method.gBLUP.index()] = new MLabel("<html> GAPIT <br> (gBLUP)</html>");
					label_method[method.rrBLUP.index()] = new MLabel("rrBLUP");
					label_method[method.BGLR.index()] = new MLabel("BGLR");	
					pane_main.add(label_method[method.gBLUP.index()], "cell 1 0, grow, w 150:150:");
					pane_main.add(label_method[method.rrBLUP.index()], "cell 1 1, grow, w 150:150:");
					pane_main.add(label_method[method.BGLR.index()], "cell 1 2, grow, w 150:150:");
					pane_top.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), 
							"GS (Format: " + iPatPanel.format + ")", TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
					break;
			}         
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
				for(method PressedMethod : (Analysis == analysis.GWAS? ListGWAS:ListGS)){
					if(label_method[PressedMethod.index].getBounds().contains(pt)){
						indexDrag = PressedMethod; 
						tempLabel = label_method[PressedMethod.index].getLocation();
						break;
					}
				}	
				if(pane_config.isDeployed && pane_config.getBounds().contains(pt)){
					try { 
						switch(pane_config.existmethod){
							case GAPIT: 	pane_config.config_gapit(); break;
							case FarmCPU: 	pane_config.config_farm(); break;
							case PLINK: 	pane_config.config_plink(); break;
							case gBLUP: 	pane_config.config_gblup(); break;
							case rrBLUP: 	pane_config.config_rrblup(); break;
							case BGLR: 		pane_config.config_bglr(); break;
						}
						refresh();
					} catch (IOException e1) {e1.printStackTrace();}
				}
			}
			@Override
			public void mouseReleased(MouseEvent e){
				// Dragging a method
				if(indexDrag != method.NA){
					// Drop in the panel
					if(pane_config.getBounds().contains(e.getPoint())){
						pane_config.MethodSelected(indexDrag);
					// Drop outside of the panel
					}else if(pane_config.isDeployed){
						pane_config.MethodSelected(pane_config.existmethod);
					};
					label_method[indexDrag.index].setLocation(tempLabel);
					indexDrag = method.NA;
					tempLabel = new Point(-1, -1);	
				}
			}
		});	
		this.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
                int dx = e.getX() - pt.x;
                int dy = e.getY() - pt.y;
                // Dragging a method
				if(indexDrag != method.NA){
					// Entering panel
					if(pane_config.getBounds().contains(e.getPoint())){
						pane_config.Clear();
						pane_config.HintDrop();
					// Outside of panel
					}else{
						pane_config.Clear();
						pane_config.HintDrag();
					}
					label_method[indexDrag.index].setLocation(tempLabel.x + dx, tempLabel.y + dy);	
					repaint();
				}
            }
		});
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("closed");
				if(pane_config.isDeployed){
					switch(Analysis){
					case GWAS:
						iPatPanel.command_gwas[MOindex] = pane_config.MethodCommand();
						iPatPanel.Deployed[MOindex][analysis.GWAS.index] = pane_config.existmethod;
						for (int i = 0; i < iPatPanel.command_gwas[MOindex].length; i++){
							System.out.println(iPatPanel.command_gwas[MOindex][i]);		
						}
						break;
					case GS:
						iPatPanel.command_gs[MOindex] = pane_config.MethodCommand();
						iPatPanel.Deployed[MOindex][analysis.GS.index] = pane_config.existmethod;
						for (int i = 0; i < iPatPanel.command_gs[MOindex].length; i++){
							System.out.println(iPatPanel.command_gs[MOindex][i]);		
						}
						break;
					}
				}
				iPatPanel.MOname[MOindex].setText(project_name.field.getText());
				iPatPanel.MOfile[MOindex] = wd_path.field.getText();
				save();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if(source == bottom_restore){
			restore();
		}else if(source == wd_path.browse){
			wd_path.setPath(true);
		}
	}
	public void refresh(){
		this.setVisible(true);
	}
	public void save(){
		iPatPanel.project[MOindex] = project_name.field.getText();
		iPatPanel.wd = wd_path.field.getText();
		iPatPanel.maf = (String) maf_qc.combo.getSelectedItem();
		iPatPanel.ms = (String) ms_qc.combo.getSelectedItem();
		pane_config.save(Analysis == analysis.GWAS);
	}
	public void load(){
		project_name.field.setText(iPatPanel.project[MOindex]);
		wd_path.field.setText(iPatPanel.wd);
		maf_qc.combo.setSelectedItem(iPatPanel.maf);
		ms_qc.combo.setSelectedItem(iPatPanel.ms);
		pane_config.load(Analysis == analysis.GWAS);
		if(iPatPanel.Deployed[MOindex][Analysis.index] != method.NA)
			pane_config.MethodSelected(iPatPanel.Deployed[MOindex][Analysis.index]);
	}
	public void restore(){
		project_name.field.setText("Project_" + MOindex);
		wd_path.field.setText(iPatPanel.df_wd);
		maf_qc.combo.setSelectedItem(iPatPanel.df_maf);
		ms_qc.combo.setSelectedItem(iPatPanel.df_ms);
		pane_config.restore(Analysis == analysis.GWAS);
	}
	public class MLabel extends JLabel {
		public MLabel(String name){
			this.setText(name);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setFont(new Font("Ariashowpril", Font.BOLD, 25));
			this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
	}
}
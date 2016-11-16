package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.rosuda.JRI.Rengine;

import net.miginfocom.swing.MigLayout;


public class Configuration extends JFrame implements ActionListener, WindowListener{
	Preferences pref;
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
	JLabel file_fragment_text = new JLabel("Fragment");
	JTextField file_fragment_input= new JTextField(5);
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
	String[] kinship_cluster_names= {"average"};
	JComboBox kinship_cluster_input= new JComboBox(kinship_cluster_names);
	JLabel kinship_cluster_text = new JLabel("Cluster");
	String[] kinship_group_names= {"Mean"};
	JComboBox kinship_group_input= new JComboBox(kinship_group_names);
	JLabel kinship_group_text = new JLabel("Group");
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
	JLabel PCA_total_text = new JLabel("total");
	JTextField PCA_total_input= new JTextField(5);
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
	JLabel SNP_fraction_text = new JLabel("fraction");
	JTextField SNP_fraction_input= new JTextField(5);
	String[] SNP_impute_names= {"Middle"};
	JComboBox SNP_impute_input = new JComboBox(SNP_impute_names);
	JLabel SNP_impute_text = new JLabel("impute");
	JLabel SNP_MAF_text = new JLabel("MAF");
	JTextField SNP_MAF_input = new JTextField(5);
	JCheckBox SNP_P3D = new JCheckBox("P3D");
	JCheckBox SNP_permutation = new JCheckBox("permutation");
	String[] SNP_robust_names= {"GLM"};
	JComboBox SNP_robust_input = new JComboBox(SNP_impute_names);
	JLabel SNP_robust_text = new JLabel("robust");
	JCheckBox SNP_test = new JCheckBox("test");
	
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
	///////////////////////////////////////////////////////////////////////////////////////
	JPanel wd_panel;
	JButton browse = new JButton("Browse");
	iPat_chooser chooser;
	JLabel wd_text = new JLabel("Working Directory");
	JTextField wd_input = new JTextField(15);
	JLabel n_text = new JLabel("Project Name");
	JTextField n_input = new JTextField(10);
	///////////////////////////////////////////////////////////////////////////////////////	
	Runnable back_run = new Runnable(){
		@Override
		public void run(){
			
		}
	};
	int test_run = 0;
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
    String folder_path = new String();
	Rengine r;
	int  MOindex;
	
	public Configuration(Rengine r, int MOindex){	
		this.r = r;
		this.MOindex = MOindex;
		pref = Preferences.userRoot().node("/ipat");  
		load();
		addWindowListener(this);
	}
	
	public void configuration_initial(){
		go.setFont(new Font("Ariashowpril", Font.BOLD, 40));
		///////////////////////////////////////////////////////////////////////////////////////
		wd_panel = new JPanel(new MigLayout("fillx"));
		wd_panel.add(n_text, "wrap");
		wd_panel.add(n_input, "wrap");
		wd_panel.add(wd_text, "wrap");
		wd_panel.add(wd_input);
		wd_panel.add(browse, "wrap");
		wd_panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Project", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_EMMA = new JPanel(new MigLayout("fillx"));
		panel_EMMA.add(esp_text); esp_text.setToolTipText("EMMA parameter");
		panel_EMMA.add(esp_input, "wrap");
		panel_EMMA.add(llim_text); llim_text.setToolTipText("EMMA parameter");
		panel_EMMA.add(llim_input, "wrap");
		panel_EMMA.add(ngrid_text); ngrid_text.setToolTipText("EMMA parameter");
		panel_EMMA.add(ngrid_input, "wrap");
		panel_EMMA.add(ulim_text); ulim_text.setToolTipText("EMMA parameter");
		panel_EMMA.add(ulim_input, "wrap");
		panel_EMMA.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "EMMA", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_Farm = new JPanel(new MigLayout("fillx"));
		panel_Farm.add(acceleration_text); acceleration_text.setToolTipText("FarmCPU parameter");
		panel_Farm.add(acceleration_input, "wrap");
		panel_Farm.add(converge_text); converge_text.setToolTipText("FarmCPU parameter");
		panel_Farm.add(converge_input, "wrap");
		panel_Farm.add(maxLoop_text); maxLoop_text.setToolTipText("maximum number ofloops in FarmCPU");
		panel_Farm.add(maxLoop_input, "wrap");
		panel_Farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "FarmCPU", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_file = new JPanel(new MigLayout("fillx"));
		panel_file.add(file_Ext_G_text);
		panel_file.add(file_Ext_G_input, "wrap");
		panel_file.add(file_Ext_GD_text);
		panel_file.add(file_Ext_GD_input, "wrap");
		panel_file.add(file_Ext_GM_text);
		panel_file.add(file_Ext_GM_input, "wrap");
		panel_file.add(file_fragment_text);
		panel_file.add(file_fragment_input, "wrap");
		panel_file.add(file_from_text);
		panel_file.add(file_from_input, "wrap");
		panel_file.add(file_to_text);
		panel_file.add(file_to_input, "wrap");
		panel_file.add(file_total_text);
		panel_file.add(file_total_input, "wrap");
		panel_file.add(file_G_text);
		panel_file.add(file_G_input, "wrap");
		panel_file.add(file_GD_text);
		panel_file.add(file_GD_input, "wrap");
		panel_file.add(file_GM_text);
		panel_file.add(file_GM_input, "wrap");
		panel_file.add(file_path_text);
		panel_file.add(file_path_input, "wrap");
		panel_file.add(file_output);
		panel_file.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "File", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_group = new JPanel(new MigLayout("fillx"));
		panel_group.add(group_by_text);
		panel_group.add(group_by_input, "wrap");
		panel_group.add(group_from_text);
		panel_group.add(group_from_input, "wrap");
		panel_group.add(group_to_text);
		panel_group.add(group_to_input, "wrap");
		panel_group.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Group", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_kinship = new JPanel(new MigLayout("fillx"));
		panel_kinship.add(kinship_algorithm_text);
		panel_kinship.add(kinship_algorithm_input, "wrap");
		panel_kinship.add(kinship_cluster_text);
		panel_kinship.add(kinship_cluster_input, "wrap");
		panel_kinship.add(kinship_group_text);
		panel_kinship.add(kinship_group_input, "wrap");
		panel_kinship.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Kinship", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_LD = new JPanel(new MigLayout("fillx"));
		panel_LD.add(LD_chromosome_text);
		panel_LD.add(LD_chromosome_input, "wrap");
		panel_LD.add(LD_location_text);
		panel_LD.add(LD_location_input, "wrap");
		panel_LD.add(LD_range_text);
		panel_LD.add(LD_range_input, "wrap");
		panel_LD.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "LD", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_method = new JPanel(new MigLayout("fillx"));
		panel_method.add(method_iteration_text);
		panel_method.add(method_iteration_input, "wrap");
		panel_method.add(method_bin_text);
		panel_method.add(method_bin_input, "wrap");
		panel_method.add(method_GLM_text);		
		panel_method.add(method_GLM_input, "wrap");
		panel_method.add(method_sub_text);
		panel_method.add(method_sub_input, "wrap");
		panel_method.add(method_sub_final_text);
		panel_method.add(method_sub_final_input, "wrap");
		panel_method.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Method", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_model = new JPanel(new MigLayout("fillx"));
		panel_model.add(model_selection);
		panel_model.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Model", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_output = new JPanel(new MigLayout("fillx"));
		panel_output.add(output_cutOff_text);
		panel_output.add(output_cutOff_input, "wrap");
		panel_output.add(output_CV_Inheritance_text);
		panel_output.add(output_CV_Inheritance_input, "wrap");
		panel_output.add(output_DPP_text);
		panel_output.add(output_DPP_input, "wrap");
		panel_output.add(output_Geno_View_output, "wrap");
		panel_output.add(output_iteration_output, "wrap");
		panel_output.add(output_maxOut_text);
		panel_output.add(output_maxOut_input, "wrap");
		panel_output.add(output_hapmap, "wrap");
		panel_output.add(output_numerical, "wrap");
		panel_output.add(output_plot_style_text);
		panel_output.add(output_plot_style_input, "wrap");
		panel_output.add(output_threshold_text);
		panel_output.add(output_threshold_input, "wrap");
		panel_output.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Output", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_PCA = new JPanel(new MigLayout("fillx"));
		panel_PCA.add(PCA_total_text);
		panel_PCA.add(PCA_total_input, "wrap");
		panel_PCA.add(PCA_View_input, "wrap");
		panel_PCA.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "PCA", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_QTN = new JPanel(new MigLayout("fillx"));
		panel_QTN.add(QTN_prior_text);
		panel_QTN.add(QTN_prior_input, "wrap");
		panel_QTN.add(QTN_text);
		panel_QTN.add(QTN_input, "wrap");
		panel_QTN.add(QTN_limit_text);
		panel_QTN.add(QTN_limit_input, "wrap");
		panel_QTN.add(QTN_method_text);
		panel_QTN.add(QTN_method_input, "wrap");
		panel_QTN.add(QTN_position_text);
		panel_QTN.add(QTN_position_input, "wrap");
		panel_QTN.add(QTN_round_text);
		panel_QTN.add(QTN_round_input, "wrap");
		panel_QTN.add(QTN_update, "wrap");
		panel_QTN.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "QTN", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_SNP = new JPanel(new MigLayout("fillx"));
		panel_SNP.add(SNP_create_indicater, "wrap");
		panel_SNP.add(SNP_major_allele_zero, "wrap");
		panel_SNP.add(SNP_CV_text);
		panel_SNP.add(SNP_CV_input, "wrap");
		panel_SNP.add(SNP_effect_text);
		panel_SNP.add(SNP_effect_input, "wrap");
		panel_SNP.add(SNP_FDR_text);
		panel_SNP.add(SNP_FDR_input, "wrap");
		panel_SNP.add(SNP_fraction_text);
		panel_SNP.add(SNP_fraction_input, "wrap");
		panel_SNP.add(SNP_impute_text);
		panel_SNP.add(SNP_impute_input, "wrap");
		panel_SNP.add(SNP_MAF_text);
		panel_SNP.add(SNP_MAF_input, "wrap");
		panel_SNP.add(SNP_P3D, "wrap");
		panel_SNP.add(SNP_permutation, "wrap");
		panel_SNP.add(SNP_robust_text);
		panel_SNP.add(SNP_robust_input, "wrap");
		panel_SNP.add(SNP_test, "wrap");
		panel_SNP.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "SNP", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		panel_super = new JPanel(new MigLayout("fillx"));
		panel_super.add(SUPER_bin_by_text);
		panel_super.add(SUPER_bin_by_input, "wrap");
		panel_super.add(SUPER_bin_from_text);
		panel_super.add(SUPER_bin_from_input, "wrap");
		panel_super.add(SUPER_bin_to_text);
		panel_super.add(SUPER_bin_to_input, "wrap");
		panel_super.add(SUPER_bin_selection_text);
		panel_super.add(SUPER_bin_selection_input, "wrap");
		panel_super.add(SUPER_bin_size_text);
		panel_super.add(SUPER_bin_size_input, "wrap");
		
		panel_super.add(SUPER_BINS_text);
		panel_super.add(SUPER_BINS_input, "wrap");
		panel_super.add(SUPER_FDR_rate_text);
		panel_super.add(SUPER_FDR_rate_input, "wrap");
		panel_super.add(SUPER_GT_index_text);
		panel_super.add(SUPER_GT_index_input, "wrap");
		
		panel_super.add(SUPER_inclosure_by_text);
		panel_super.add(SUPER_inclosure_by_input, "wrap");
		panel_super.add(SUPER_inclosure_from_text);
		panel_super.add(SUPER_inclosure_from_input, "wrap");
		panel_super.add(SUPER_inclosure_to_text);
		panel_super.add(SUPER_inclosure_to_input, "wrap");
		panel_super.add(SUPER_LD_text);
		panel_super.add(SUPER_LD_input, "wrap");
		panel_super.add(SUPER_sanawich_bottom_text);
		panel_super.add(SUPER_sanawich_bottom_input, "wrap");
		panel_super.add(SUPER_sanawich_top_text);
		panel_super.add(SUPER_sanawich_top_input, "wrap");
		panel_super.add(SUPER_GD_text);
		panel_super.add(SUPER_GD_input, "wrap");
		panel_super.add(SUPER_GS, "wrap");
		panel_super.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "SUPER", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		///////////////////////////////////////////////////////////////////////////////////////
		main_panel = new JPanel(new MigLayout("fillx", "[grow][grow]"));
		main_panel.add(go, "dock north");
		main_panel.add(wd_panel, "cell 0 0, grow");
		main_panel.add(panel_EMMA, "cell 0 1, grow");
		main_panel.add(panel_Farm, "cell 0 2, grow");
		main_panel.add(panel_file, "cell 0 3, grow");
		main_panel.add(panel_group, "cell 0 4, grow");
		main_panel.add(panel_kinship, "cell 0 5, grow");
		main_panel.add(panel_LD, "cell 0 6, grow");
		main_panel.add(panel_method, "cell 0 7, grow");
		main_panel.add(panel_model, "cell 0 8, grow");
		main_panel.add(panel_output, "cell 0 9, grow");
		main_panel.add(panel_PCA, "cell 0 10, grow");
		main_panel.add(panel_QTN, "cell 0 11, grow");
		main_panel.add(panel_SNP, "cell 0 12, grow");
		main_panel.add(panel_super, "cell 0 13, grow");

		///////////////////////////////////////////////////////////////////////////////////////
		JScrollPane pane = new JScrollPane(main_panel,  
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		go.addActionListener(this);
		browse.addActionListener(this);
		this.setContentPane(pane);
		this.setTitle("Configuration");
		this.pack();
		this.show();
	}
	
	public void actionPerformed(ActionEvent ip){
	      Object source = ip.getSource();	
	      if (source == go){
	  		save();
	  		this.dispose();
	      }else if(source == browse){
	    	  	chooser = new iPat_chooser();
	    	  	wd_input.setText(chooser.getPath());
	      }
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
		pref.removeBoolean("output_hapmap", output_hapmap.isSelected());
		pref.removeBoolean("output_numeric", output_numerical.isSelected());
		pref.remove("output_plot", (String) output_plot_style_input.getSelectedItem());
		pref.remove("output_threshold", output_threshold_input.getText());

		pref.remove("pca_total", PCA_total_input.getText());
		pref.removeBoolean("pca_view", PCA_View_input.isSelected());

		pref.remove("qtn_prior", QTN_prior_input.getText());
		pref.remove("qtn", QTN_input.getText());
		pref.remove("qtn_limit", QTN_limit_input.getText());
		pref.remove("qtn_method", (String) QTN_method_input.getSelectedItem());
		pref.remove("qtn_position", QTN_position_input.getText());
		pref.remove("qtn_round", QTN_position_input.getText());
		pref.removeBoolean("qtn_update", QTN_update.isSelected());

		pref.removeBoolean("snp_create", SNP_create_indicater.isSelected());
		pref.removeBoolean("snp_major", SNP_major_allele_zero.isSelected());
		pref.remove("snp_cv", SNP_CV_input.getText());
		pref.remove("snp_effect", (String) SNP_effect_input.getSelectedItem());
		pref.remove("snp_fdr", SNP_FDR_input.getText());
		pref.remove("snp_fraction", SNP_fraction_input.getText());
		pref.remove("snp_impute", (String) SNP_impute_input.getSelectedItem());
		pref.remove("snp_maf", SNP_MAF_input.getText());
		pref.removeBoolean("snp_p3d", SNP_P3D.isSelected());
		pref.removeBoolean("snp_permuation", SNP_permutation.isSelected());
		pref.remove("snp_robust", (String) SNP_robust_input.getSelectedItem());
		pref.removeBoolean("snp_test", SNP_test.isSelected());
		
		pref.remove("super_bin_by", SUPER_bin_by_input.getText());
		pref.remove("super_bin_from", SUPER_bin_from_input.getText());
		pref.remove("super_bin_to", SUPER_bin_to_input.getText());
		pref.remove("super_bin_selection", SUPER_bin_selection_input.getText());
		pref.remove("super_bin_size", SUPER_bin_size_input.getText());
		pref.remove("super_bin", SUPER_BINS_input.getText());
		pref.remove("super_fdr", SUPER_FDR_rate_input.getText());
		pref.remove("super_gt", SUPER_GT_index_input.getText());
		pref.remove("super_inclosure_by", SUPER_inclosure_by_input.getText());
		pref.remove("super_inclosure_from", SUPER_inclosure_from_input.getText());
		pref.remove("super_inclosure_to", SUPER_inclosure_to_input.getText());
		pref.remove("super_ld", SUPER_LD_input.getText());
		pref.remove("super_bottom", SUPER_sanawich_bottom_input.getText());
		pref.remove("super_top", SUPER_sanawich_top_input.getText());
		pref.remove("super_gd", SUPER_GD_input.getText());
		pref.removeBoolean("super_gs", SUPER_GS.isSelected());
	}
	void load(){
		wd_input.setText(pref.get("wd", " "));
		n_input.setText(myPanel.MOname[MOindex].getText());

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
		file_fragment_input.setText(pref.get("file_fragment", "99999"));
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
		
		PCA_total_input.setText(pref.get("pca_total", "0"));
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
		pref.put("file_fragment", file_fragment_input.getText());
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
		pref.put("qtn_round", QTN_position_input.getText());
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
	
	void add_folder(String folder_name){
	myPanel.TBcount++;
    	myPanel.TBimageX[myPanel.TBcount] = myPanel.MOimageX[MOindex]+ (myPanel.MOimageW[MOindex]/2)-(myPanel.TBimageW[myPanel.TBcount]/2);
    	myPanel.TBimageY[myPanel.TBcount] = myPanel.MOimageY[MOindex]+ 80;
    	myPanel.TBBound[myPanel.TBcount] = new Rectangle(myPanel.TBimageX[myPanel.TBcount], myPanel.TBimageY[myPanel.TBcount], 60, 60);
    	myPanel.TBfile[myPanel.TBcount] = folder_path;
    	myPanel.TBname[myPanel.TBcount].setLocation(myPanel.TBimageX[myPanel.TBcount], 
    												myPanel.TBimageY[myPanel.TBcount]+myPanel.TBimageH[myPanel.TBcount]- myPanel.panelHeigth+15);
    	myPanel.TBname[myPanel.TBcount].setSize(200, 15);
    	myPanel.TBname[myPanel.TBcount].setText(folder_name);
    	
    	myPanel.MOco[MOindex][0] = 1;
	    myPanel.MOco[MOindex][1] = myPanel.COcount;
    	myPanel.MOco[MOindex][2] = 1;

    	myPanel.TBco[myPanel.TBcount][0] = 1;
    	myPanel.TBco[myPanel.TBcount][1] = myPanel.COcount;
    	myPanel.TBco[myPanel.TBcount][2] = 1;
    	
    	myPanel.linkline[myPanel.linklineindex][0] = 2;
    	myPanel.linkline[myPanel.linklineindex][1] = MOindex;
    	myPanel.linkline[myPanel.linklineindex][2] = 1;
    	myPanel.linkline[myPanel.linklineindex][3] = myPanel.TBcount;

    	myPanel.linklineindex++;
    	myPanel.COcount++;
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
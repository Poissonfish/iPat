package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.FlowLayout;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Window.Type;

public class Config extends JFrame {

	private JPanel contentPane;
	private JTextField esp_input;
	private JTextField llim_input;
	private JTextField ngrid;
	private JTextField textField;
	private JTextField acc_input;
	private JTextField con_input;
	private JTextField max_input;
	private JTextField file_Ext_G_input;
	private JTextField file_Ext_GD_input;
	private JTextField file_Ext_GM_input;
	private JTextField file_fragment_input;
	private JTextField file_from_input;
	private JTextField file_to_input;
	private JTextField file_total_input;
	private JTextField file_G_input;
	private JTextField file_GD_input;
	private JTextField file_GM_input;
	private JTextField file_path_input;
	private JTextField group_by_input;
	private JTextField group_from_input;
	private JTextField group_to_input;
	private JTextField LD_chromosome_input;
	private JTextField LD_location_text;
	private JTextField LD_range_input;
	private JTextField output_cutOff_input;
	private JTextField output_CV_inheritance_input;
	private JTextField output_DPP_input;
	private JTextField output_maxOut_input;
	private JTextField output_threshold_input;
	private JTextField PCA_total_input;
	private JTextField QTN_prior_input;
	private JTextField QTN_input;
	private JTextField QTN_limit_input;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Config frame = new Config();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Config() {
		setTitle("Configuration");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 680, 2000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 50, 50));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("fillx", "[grow]", "[grow]"));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane, "cell 0 0,grow");
		
		JPanel main_panel = new JPanel();
		scrollPane.setViewportView(main_panel);
		main_panel.setLayout(new MigLayout("fillx", "[300.00,grow][grow]", "[][][][][][grow]"));
		
		JPanel panel_EMMA = new JPanel();
		main_panel.add(panel_EMMA, "flowx,cell 0 0,grow");
		panel_EMMA.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "EMMA", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		panel_EMMA.setLayout(new MigLayout("fillx", "[][]", "[][]"));
		
		JLabel lblEsp = new JLabel("esp");
		lblEsp.setToolTipText("this is a sample");
		panel_EMMA.add(lblEsp, "flowx,cell 0 0");
		
		esp_input = new JTextField();
		panel_EMMA.add(esp_input, "cell 0 0");
		esp_input.setColumns(5);
		
		JLabel lblllim = new JLabel("llim");
		panel_EMMA.add(lblllim, "flowx,cell 1 0");
		
		llim_input = new JTextField();
		panel_EMMA.add(llim_input, "cell 1 0");
		llim_input.setColumns(5);
		
		JLabel lblNgrid = new JLabel("ngrid");
		panel_EMMA.add(lblNgrid, "flowx,cell 0 1");
		
		ngrid = new JTextField();
		panel_EMMA.add(ngrid, "cell 0 1");
		ngrid.setColumns(5);
		
		JLabel lblUlim = new JLabel("ulim");
		panel_EMMA.add(lblUlim, "flowx,cell 1 1");
		
		textField = new JTextField();
		panel_EMMA.add(textField, "cell 1 1");
		textField.setColumns(5);
				
				JPanel panel_Farm = new JPanel();
				main_panel.add(panel_Farm, "cell 1 0,grow");
				panel_Farm.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "FarmCPU", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				panel_Farm.setLayout(new MigLayout("fillx", "[76px]", "[16px][][]"));
				
				JLabel lblAcceleration = new JLabel("acceleration");
				panel_Farm.add(lblAcceleration, "flowx,cell 0 0,alignx left,aligny top");
				
				JLabel lblConverge = new JLabel("converge");
				panel_Farm.add(lblConverge, "flowx,cell 0 1");
				
				JLabel lblMaxloop = new JLabel("maxLoop");
				panel_Farm.add(lblMaxloop, "flowx,cell 0 2");
				
				acc_input = new JTextField();
				panel_Farm.add(acc_input, "cell 0 0");
				acc_input.setColumns(10);
				
				con_input = new JTextField();
				panel_Farm.add(con_input, "cell 0 1");
				con_input.setColumns(10);
				
				max_input = new JTextField();
				panel_Farm.add(max_input, "cell 0 2");
				max_input.setColumns(10);
				
				JPanel panel_file = new JPanel();
				panel_file.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "File", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_file, "cell 0 1 2 1,grow");
				panel_file.setLayout(new MigLayout("fillx", "", ""));
				
				JLabel lblExtg = new JLabel("Ext.G");
				panel_file.add(lblExtg, "cell 0 0");
				
				file_Ext_G_input = new JTextField();
				panel_file.add(file_Ext_G_input, "cell 0 0");
				file_Ext_G_input.setColumns(10);
				
				JLabel lblExtgd = new JLabel("Ext_GD");
				panel_file.add(lblExtgd, "cell 0 0");
				
				file_Ext_GD_input = new JTextField();
				panel_file.add(file_Ext_GD_input, "cell 0 0,growx");
				file_Ext_GD_input.setColumns(10);
				
						JLabel lblExtgm = new JLabel("Ext_GM");
						panel_file.add(lblExtgm, "cell 0 0");
						
						file_Ext_GM_input = new JTextField();
						panel_file.add(file_Ext_GM_input, "cell 0 0");
						file_Ext_GM_input.setColumns(10);
						
						JLabel lblFragment = new JLabel("Fragment");
						panel_file.add(lblFragment, "flowx,cell 0 1");
						
						file_fragment_input = new JTextField();
						panel_file.add(file_fragment_input, "cell 0 1");
						file_fragment_input.setColumns(10);
						
						JLabel lblFrom = new JLabel("From");
						panel_file.add(lblFrom, "flowx,cell 0 2");
						
						file_from_input = new JTextField();
						panel_file.add(file_from_input, "cell 0 2");
						file_from_input.setColumns(10);
						
						JLabel lblTo = new JLabel("To");
						panel_file.add(lblTo, "cell 0 2");
						
						file_to_input = new JTextField();
						panel_file.add(file_to_input, "cell 0 2");
						file_to_input.setColumns(10);
						
						JLabel lblTotal = new JLabel("Total");
						panel_file.add(lblTotal, "cell 0 2");
						
						file_total_input = new JTextField();
						panel_file.add(file_total_input, "cell 0 2");
						file_total_input.setColumns(10);
						
						JLabel lblG = new JLabel("G");
						panel_file.add(lblG, "flowx,cell 0 3");
						
						file_G_input = new JTextField();
						panel_file.add(file_G_input, "cell 0 3");
						file_G_input.setColumns(10);
						
						JLabel lblPath = new JLabel("Path");
						panel_file.add(lblPath, "flowx,cell 0 4");
						
						JLabel lblGd = new JLabel("GD");
						panel_file.add(lblGd, "cell 0 3");
						
						file_GD_input = new JTextField();
						panel_file.add(file_GD_input, "cell 0 3");
						file_GD_input.setColumns(10);
						
						JLabel lblGm = new JLabel("GM");
						panel_file.add(lblGm, "cell 0 3");
						
						file_GM_input = new JTextField();
						panel_file.add(file_GM_input, "cell 0 3");
						file_GM_input.setColumns(10);
						
						file_path_input = new JTextField();
						panel_file.add(file_path_input, "cell 0 4");
						file_path_input.setColumns(10);
						
						JCheckBox chckbxOutput = new JCheckBox("Output");
						panel_file.add(chckbxOutput, "cell 0 4");
				
				JPanel panel_group = new JPanel();
				panel_group.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Group", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_group, "cell 0 2,grow");
				panel_group.setLayout(new MigLayout("fillx", "[]", "[]"));
				
				JLabel lblBy = new JLabel("By");
				panel_group.add(lblBy, "flowx,cell 0 0");
				
				group_by_input = new JTextField();
				panel_group.add(group_by_input, "cell 0 0");
				group_by_input.setColumns(10);
				
				JLabel lblFrom_1 = new JLabel("From");
				panel_group.add(lblFrom_1, "cell 0 0");
				
				group_from_input = new JTextField();
				panel_group.add(group_from_input, "cell 0 0");
				group_from_input.setColumns(10);
				
				JLabel lblTo_1 = new JLabel("To");
				panel_group.add(lblTo_1, "cell 0 0");
				
				group_to_input = new JTextField();
				panel_group.add(group_to_input, "cell 0 0");
				group_to_input.setColumns(10);
				
				JPanel panel_model = new JPanel();
				panel_model.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Model", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_model, "cell 1 2,grow");
				panel_model.setLayout(new MigLayout("fillx", "[]", "[]"));
				
				JCheckBox chckbxModelSelection = new JCheckBox("Model Selection");
				panel_model.add(chckbxModelSelection, "cell 0 0");
				
				JPanel panel_kinship = new JPanel();
				panel_kinship.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Kinship", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_kinship, "cell 0 3,grow");
				panel_kinship.setLayout(new MigLayout("fillx", "[grow]", "[][][]"));
				
				JLabel lblAlgorithm = new JLabel("Algorithm");
				panel_kinship.add(lblAlgorithm, "flowx,cell 0 0");
				
				JComboBox comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(new String[] {"VanRaden"}));
				panel_kinship.add(comboBox, "cell 0 0");
				
				JLabel lblCluster = new JLabel("Cluster");
				panel_kinship.add(lblCluster, "flowx,cell 0 1");
				
				JComboBox comboBox_1 = new JComboBox();
				comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"average"}));
				panel_kinship.add(comboBox_1, "cell 0 1");
				
				JLabel lblGroup = new JLabel("Group");
				panel_kinship.add(lblGroup, "flowx,cell 0 2");
				
				JComboBox comboBox_2 = new JComboBox();
				comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Mean"}));
				panel_kinship.add(comboBox_2, "cell 0 2,aligny baseline");
				
				JPanel panel_LD = new JPanel();
				panel_LD.setBorder(new TitledBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "LD", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "LD", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_LD, "cell 1 3,grow");
				panel_LD.setLayout(new MigLayout("fillx", "[]", "[][][]"));
				
				JLabel lblChromosome = new JLabel("Chromosome");
				panel_LD.add(lblChromosome, "flowx,cell 0 0");
				
				LD_chromosome_input = new JTextField();
				panel_LD.add(LD_chromosome_input, "cell 0 0");
				LD_chromosome_input.setColumns(10);
				
				JLabel lblLocation = new JLabel("Location");
				panel_LD.add(lblLocation, "flowx,cell 0 1");
				
				LD_location_text = new JTextField();
				panel_LD.add(LD_location_text, "cell 0 1");
				LD_location_text.setColumns(10);
				
				JLabel lblRange = new JLabel("Range");
				panel_LD.add(lblRange, "flowx,cell 0 2");
				
				LD_range_input = new JTextField();
				panel_LD.add(LD_range_input, "cell 0 2");
				LD_range_input.setColumns(10);
				
				JPanel panel_method = new JPanel();
				panel_method.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Method", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_method, "cell 0 4,grow");
				panel_method.setLayout(new MigLayout("fillx", "[]", "[][][][][]"));
				
				JLabel lblIteration = new JLabel("Iteration");
				panel_method.add(lblIteration, "flowx,cell 0 0");
				
				JComboBox comboBox_3 = new JComboBox();
				comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"accum"}));
				panel_method.add(comboBox_3, "cell 0 0");
				
				JLabel lblBin = new JLabel("Bin");
				panel_method.add(lblBin, "flowx,cell 0 1");
				
				JComboBox comboBox_4 = new JComboBox();
				comboBox_4.setModel(new DefaultComboBoxModel(new String[] {"static"}));
				panel_method.add(comboBox_4, "cell 0 1");
				
				JLabel lblGLM = new JLabel("GLM");
				panel_method.add(lblGLM, "flowx,cell 0 2");
				
				JComboBox comboBox_5 = new JComboBox();
				comboBox_5.setModel(new DefaultComboBoxModel(new String[] {"fast.lm"}));
				panel_method.add(comboBox_5, "cell 0 2");
				
				JLabel lblSub = new JLabel("Sub");
				panel_method.add(lblSub, "flowx,cell 0 3");
				
				JLabel lblSubFinal = new JLabel("Sub Final");
				panel_method.add(lblSubFinal, "flowx,cell 0 4");
				
				JComboBox comboBox_7 = new JComboBox();
				comboBox_7.setModel(new DefaultComboBoxModel(new String[] {"reward"}));
				panel_method.add(comboBox_7, "cell 0 4");
				
				JComboBox comboBox_6 = new JComboBox();
				comboBox_6.setModel(new DefaultComboBoxModel(new String[] {"reward"}));
				panel_method.add(comboBox_6, "cell 0 3");
				
				JPanel panel_output = new JPanel();
				panel_output.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Output", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_output, "cell 1 4,grow");
				panel_output.setLayout(new MigLayout("fillx", "[grow]", "[][][][][][][][]"));
				
				JLabel lblCutoff = new JLabel("CutOFF");
				panel_output.add(lblCutoff, "flowx,cell 0 0");
				
				output_cutOff_input = new JTextField();
				panel_output.add(output_cutOff_input, "cell 0 0");
				output_cutOff_input.setColumns(10);
				
				JLabel lblCvInheritance = new JLabel("CV inheritance");
				panel_output.add(lblCvInheritance, "flowx,cell 0 1");
				
				output_CV_inheritance_input = new JTextField();
				panel_output.add(output_CV_inheritance_input, "cell 0 1");
				output_CV_inheritance_input.setColumns(10);
				
				JLabel lblDpp = new JLabel("DPP");
				panel_output.add(lblDpp, "flowx,cell 0 2");
				
				output_DPP_input = new JTextField();
				panel_output.add(output_DPP_input, "cell 0 2");
				output_DPP_input.setColumns(10);
				
				JCheckBox chckbxGenoViewOutput = new JCheckBox("Geno View output");
				panel_output.add(chckbxGenoViewOutput, "flowx,cell 0 3");
				
				JCheckBox chckbxIterationOutput = new JCheckBox("Iteration output");
				panel_output.add(chckbxIterationOutput, "flowx,cell 0 4");
				
				JLabel lblMaxout = new JLabel("MaxOut");
				panel_output.add(lblMaxout, "flowx,cell 0 5");
				
				output_maxOut_input = new JTextField();
				panel_output.add(output_maxOut_input, "cell 0 5");
				output_maxOut_input.setColumns(10);
				
				JLabel lblPlotStyle = new JLabel("Plot Style");
				panel_output.add(lblPlotStyle, "flowx,cell 0 6");
				
				JLabel lblThreshold = new JLabel("Threshold");
				panel_output.add(lblThreshold, "flowx,cell 0 7");
				
				JCheckBox chckbxHapmap = new JCheckBox("Hapmap");
				panel_output.add(chckbxHapmap, "cell 0 3");
				
				JCheckBox chckbxNumerical = new JCheckBox("Numerical");
				panel_output.add(chckbxNumerical, "cell 0 4");
				
				JComboBox comboBox_8 = new JComboBox();
				comboBox_8.setModel(new DefaultComboBoxModel(new String[] {"Oceanic"}));
				panel_output.add(comboBox_8, "cell 0 6,growx");
				
				output_threshold_input = new JTextField();
				panel_output.add(output_threshold_input, "cell 0 7");
				output_threshold_input.setColumns(10);
				
				JPanel panel_PCA = new JPanel();
				panel_PCA.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "PCA", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_PCA, "cell 0 5,grow");
				panel_PCA.setLayout(new MigLayout("fillx", "[]", "[][]"));
				
				JLabel lblTotal_1 = new JLabel("Total");
				panel_PCA.add(lblTotal_1, "flowx,cell 0 0");
				
				PCA_total_input = new JTextField();
				panel_PCA.add(PCA_total_input, "cell 0 0");
				PCA_total_input.setColumns(10);
				
				JCheckBox chckbxViewOutput = new JCheckBox("View Output");
				panel_PCA.add(chckbxViewOutput, "cell 0 1");
				
				JPanel panel_QTN = new JPanel();
				panel_QTN.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "QTN", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
				main_panel.add(panel_QTN, "cell 1 5,grow");
				panel_QTN.setLayout(new MigLayout("fillx", "[]", "[][][]"));
				
				JLabel lblPrior = new JLabel("Prior");
				panel_QTN.add(lblPrior, "flowx,cell 0 0");
				
				QTN_prior_input = new JTextField();
				panel_QTN.add(QTN_prior_input, "cell 0 0");
				QTN_prior_input.setColumns(10);
				
				JLabel lblQtn = new JLabel("QTN");
				panel_QTN.add(lblQtn, "flowx,cell 0 1");
				
				QTN_input = new JTextField();
				panel_QTN.add(QTN_input, "cell 0 1");
				QTN_input.setColumns(10);
				
				JLabel lblQtnLimit = new JLabel("QTN Limit");
				panel_QTN.add(lblQtnLimit, "flowx,cell 0 2");
				
				QTN_limit_input = new JTextField();
				panel_QTN.add(QTN_limit_input, "cell 0 2");
				QTN_limit_input.setColumns(10);
	}

}

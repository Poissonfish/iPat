package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.ArrayUtils;

class iPatPanel extends JPanel implements MouseMotionListener, KeyListener{	
	//  private static class ipatButton extends JButton {
	private static final int TBMAX = 40;
	private static final int MOMAX = 10;
	private static final int LINEMAX = 300;
	static File jar;
	static String res;
	static iPatObject[] iOB = new iPatObject[TBMAX + MOMAX];
	static iPatProject[] iPro = new iPatProject[MOMAX];
	// iOB / project
	static int iIndex = -1, 
			   iOBcount = 0, MOcount = 0, 
			   Groupindex = -1, Groupcount = 0;
	// link
	static Point lineST = new Point(0, 0),
	        	 lineED = new Point(0, 0),
	        	 linedragST = new Point(0, 0),
	        	 linedragED = new Point(0, 0);
	static int[][] iOBlink = new int[LINEMAX][2];
	static boolean[] iOBlink_delete = new boolean[LINEMAX];
	static int lineindex = -1, 
			   linkcount = 0, 
			   linktype = 0;
	static Point temppt = new Point(0, 0);
	static int iIndex_target = -1;
	static final Point nullPoint = new Point(0, 0);
	// select
	static boolean selectable = false;
	static int  iIndex_temp = -1, iIndex_select = -1,
				lineindex_temp = -1, lineindex_select = -1;
	// popup menu
	JPopupMenu popup_tb, popup_mo;
	static JMenuItem popup_isR, popup_isC, popup_isK, popup_opentb, popup_deltb,
					 popup_gwas, popup_gs, popup_bsa, popup_run, popup_openmo, popup_delmo;
	static int iIndex_popup = -1;
	// boundary 
	static Rectangle boundary;
	// drag
	int MOimageX_int = 430;
	int MOimageY_int = 200;
	int pos;
	
	Image[] Trash= new Image[10];
	Image[] White= new Image[10];
	static Image 	TBimage, TB_C, TB_K, TB_P,
			MOimage, Prefbar, MO_suc, MO_fal, 
			hint_project, hint_trash, hint_model,
			hint_drag, hint_drop,
			iconIP, iconiPAT;
	static JLayeredPane startPanel;
	JPanel mainPanel;	
	JLayeredPane nullPanel;
	JPanel buttonPanel;
	 
	AlphaLabel iPat_title = new AlphaLabel();

	public static FileDialog chooser;
	//animation
	Timer 	TrashAnimation, DashLineAnimation, CombinedDeleteAnimation, 
			Fadein1, Fadein2, Fadein3, Fadein4, Fadesave, Wipe;	  	
	Boolean TA;
		
	 //combine 
	int CDAindex=-1, CDAint=0, CDAi=0;
	int CDAW, CDAH;
	int panH;
	int panW;
	
	Timer fade;
	public static final long RT=100;
	private float alpha=0f;
	private long startTime=-1;
	
	//removal zone
	boolean removeornot=false;
	JLabel trashl= new JLabel();
	int trashH, trashW;
	int delbboundx;
	int delbboundy;
	
	//line
	int[] line_drag_x=new int[2],
		  line_drag_y=new int[2];
	
	boolean linktolink=false;
    	Stroke dashed = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {10, 10}, 0);
    	Stroke solid = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {10, 0}, 0);
    	Stroke select = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {10, 10}, 0);
  
	//Color
	Color red = new Color(231, 57, 131, 100);
	Color lightred = new Color(231, 57, 131, 80);
	Color dlightred = new Color(255, 0, 0, 10);
	Color ovalcolor = new Color(0, 0, 0, 80);
	Color themecolor = new Color(54, 164, 239, 150);
	
	//windows size
	static int Wide, Heigth, panelHeigth;

	//Intro
	Timer Intro;
	Color[] black = new Color[13];
	Boolean in = false, insub = false;
	
	//Button
	Boolean ifopenfile = false, ifproperty = false, ifproperty_tb = false, ifproperty_mo = false;
	int openindex = -1;
	
	//Gapit
	String[] File_names;
	File[] File_paths = new File[30];
	//settingframe model_frame;
	
    //object select
	int TBindex_select = 0;
	int MOindex_select = 0;
    int object_selected = -1;   //1 table, 2 model
	int select_intex = -1;
	//Gear rotate
	static double rotate = Math.toRadians(0);
	static boolean running = false;	
	Timer gear_rotate;	
	//periodically repaint (delcare after fill value in permit)
	Timer periodically_repaint;
	//hint_object
	AlphaLabel hint_project_label = new AlphaLabel();
	Fade_timer hint_project_timer;
	//hint_model
	static int link_model = -1;
	AlphaLabel hint_model_label = new AlphaLabel();
	Fade_timer hint_model_timer;
	//hint_trash
	AlphaLabel hint_trash_label = new AlphaLabel();
	Fade_timer hint_trash_timer;
	//hint_drag
	AlphaLabel hint_drag_label = new AlphaLabel();
	Fade_timer hint_drag_timer;
	//hint_drop
	JLabel hint_drop_label = new JLabel();
	// file format catch
	static int maxfile = 4;
	////
	public static class CustomOutputStream extends OutputStream {
	    private JTextArea textArea;	     
	    public CustomOutputStream(JTextArea textArea) {
	        this.textArea = textArea;
	    }	     
	    @Override
	    public void write (int b) throws IOException {
	        // redirects data to the text area
	        textArea.append(String.valueOf((char)b));
	        // scrolls the text area to the end of data
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
	}
	public static boolean debug = false, first_d = false;
	// Value default
		// Common
				static String  df_wd = System.getProperty("user.home"), 
							   df_maf = "0.05", df_ms = "No threshold";
			 // Specific
			  // GAPIT
				static String  df_K_algoriithm = "VanRaden", df_K_cluster = "average", df_K_group = "Mean",
							   df_model_select = "GLM", df_snp_frac = "1", df_file_frag = "NULL";
				static boolean df_model_selection = false;
			  // FarmCPU
				static String  df_method_bin = "static", df_maxloop = "10";
			  // PLINK
				static String  df_ci = "0.95";
			  // rrBLUP
				static String df_impute_method = "mean";
				static boolean df_shrink = false;
			  // BGLR 
				static String df_model_b = "BRR", df_response_b = "gaussian", 
							  df_niter_b = "1200", df_burnin_b = "200", df_thin_b = "5";
			  // GWAS-Assist
				static String df_bon = "0.05";
				static boolean df_enable = false;
			  // BSA
				static String df_window_bsa = "50000", 
							  df_pow_bsa = "4";
	// Value Stored
		// Common
				static String[] project = new String[MOMAX];
				static String  wd = df_wd, 
							   maf = df_maf, ms = df_ms;
		 // Specific
			  // GAPIT
				static String  K_algoriithm = df_K_algoriithm, K_cluster = df_K_cluster, K_group = df_K_group,
							   model_select = df_model_select, snp_frac = df_snp_frac, file_frag = df_file_frag;
				static boolean model_selection = df_model_selection;
			  // FarmCPU
				static String  method_bin = df_method_bin, maxloop = df_maxloop;
			  // PLINK
				static String  ci = df_ci;
			  // rrBLUP
				static String impute_method = df_impute_method;
				static boolean shrink = df_shrink;
			  // BGLR 
				static String model_b = df_model_b, response_b = df_response_b, 
							  niter_b = df_niter_b, burnin_b = df_burnin_b, thin_b = df_thin_b;	
			  // GWAS-Assist
				static String bon = df_bon;
				static boolean enable = df_enable;
			  // BSA
				static String window_bsa = df_window_bsa,
							  pow_bsa = df_pow_bsa;
	public iPatPanel(int Wideint, int Heigthint, int pH) throws IOException{
		this.Wide = Wideint;
		this.Heigth = Heigthint;
		this.panelHeigth = pH;
		delbboundx = Wide - 110;
		delbboundy = Heigth - 150;	
	    InputMap im = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
	    ActionMap am = this.getActionMap();
	    im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_BACK_SPACE), "delete"); 
	    im.put(KeyStroke.getKeyStroke((char) KeyEvent.VK_D), "debug");
	    am.put("delete",  new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            		System.out.println("iIndex_select = " + iIndex_select);
            		System.out.println("lineindex_select = " + lineindex_select);
            		if (iIndex_select != -1)
            			removeiOB();
            		else if (lineindex_select != -1)
            			removeiLine();
            }
        });
	    am.put("debug", new AbstractAction() {
		    public void actionPerformed (ActionEvent e) {
	    			debug = !debug; 
	             System.out.println("Debug mode : " + (debug?"On":"OFF"));
	        }
	    });
		// Context menu
		popup_tb = new JPopupMenu();
		popup_mo = new JPopupMenu();
		ActionListener menuListener = new ActionListener() {
	      public void actionPerformed(ActionEvent event) {
	    	int adx = 7, ady = 9;
	    	Object source = event.getSource();
	    	int ProIndex = iIndex_popup != -1 ? getProIndex(iIndex_popup) : -1;
	    	if (source == popup_opentb) {
			openfile(iOB[iIndex_popup].getPath());
	    	} else if (source == popup_isR) {
	    		iOB[iIndex_popup].type = iPatObject.Filetype.NA;
			iOB[iIndex_popup].updateImage(TBimage);
			iOB[iIndex_popup].setDeltaLocation(adx, ady);
	    	} else if (source == popup_isC) {
	    		iOB[iIndex_popup].type = iPatObject.Filetype.C;
			iOB[iIndex_popup].updateImage(TB_C);
			iOB[iIndex_popup].setDeltaLocation(-adx, -ady);
	    	} else if (source == popup_isK) {
	    		iOB[iIndex_popup].type = iPatObject.Filetype.K;
			iOB[iIndex_popup].updateImage(TB_K);
			iOB[iIndex_popup].setDeltaLocation(-adx, -ady);
	    	} else if (source == popup_deltb) {
			removeiOB();
	    	} else if (source == popup_openmo) {
			openfile(iOB[iIndex_popup].getPath());
	    	} else if (source == popup_gwas){
		   	try {
		   		open_config(iIndex_popup, true, false);
			} catch (IOException e) {
				e.printStackTrace();
			}   	
	    	} else if (source == popup_gs) {
	    		try {
	    			open_config(iIndex_popup, false, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	} else if (source == popup_bsa) {
	    		try {
	    			open_config(iIndex_popup, false, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	} else if(source == popup_run){
	    		iOB[iIndex_popup].updateImage(MOimage);
	    		boolean gwas_exist = iPro[getProIndex(iIndex_popup)].isGWASDeployed(), 
	    				gs_exist   = iPro[getProIndex(iIndex_popup)].isGSDeployed(),
	    				bsa_exist = iPro[getProIndex(iIndex_popup)].isBSADeployed();
	    		try {
	    			iPro[ProIndex].format = catch_files(iIndex_popup);
		    		if (gwas_exist && gs_exist) {
		    			reAssign(iIndex_popup, iPro[ProIndex].command_gwas);
		    			reAssign(iIndex_popup, iPro[ProIndex].command_gs);
		    			if(iPro[ProIndex].command_gwas[8].equals("NA")){
		    				String temp = null;
		    				switch(iPro[ProIndex].format){
		    				case PLINK: temp = iPro[ProIndex].command_gwas[10]; break; 
		    				case PLINK_bin: temp = iPro[ProIndex].command_gwas[15]; break;}
		    				iPro[ProIndex].command_gs[8] = temp.substring(0, temp.length() - 3) + "plinktrait";}
		    			 int reply = JOptionPane.showConfirmDialog(null, ConfirmFrame(ProIndex,
				    				iPro[ProIndex].method_gwas.getName(),
				    				iPro[ProIndex].method_gs.getName(),
				    				iPro[ProIndex].command_gwas,
				    				iPro[ProIndex].command_gs), "Your Configuration", 
		    					 	JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
		    					 	new ImageIcon("resources/iPat_icon.png"));
		    		      if (reply == JOptionPane.YES_OPTION) {
		    		    	  	showConsole(iIndex_popup, ProIndex, iPro[ProIndex].command_gwas[2], iPro[ProIndex].command_gwas[3]);
					    		format_conversion(iIndex_popup, ProIndex, true);
					    		iPro[ProIndex].command_gs[10] = iPro[ProIndex].command_gwas[10]; // GD
					    		iPro[ProIndex].command_gs[11] = iPro[ProIndex].command_gwas[11]; // GM
					    		format_conversion(iIndex_popup, ProIndex, false);
					    		PrintStatus(ProIndex,
					    				iPro[ProIndex].method_gwas.getName(),
					    				iPro[ProIndex].method_gs.getName(),
					    				iPro[ProIndex].command_gwas,
					    				iPro[ProIndex].command_gs);
					    		iPro[ProIndex].runCommand(iIndex_popup, 
					    				iPro[ProIndex].command_gwas,
					    				iPro[ProIndex].command_gs);
					  }
		    		} else if (gwas_exist) {
		    			reAssign(iIndex_popup, iPro[ProIndex].command_gwas);
		    			int reply = JOptionPane.showConfirmDialog(null, ConfirmFrame(ProIndex,
			    				iPro[ProIndex].method_gwas.getName(), null, 
			    				iPro[ProIndex].command_gwas, null), "Your Configuration",  
	    					 	JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
	    					 	new ImageIcon("resources/iPat_icon.png"));
		    			if (reply == JOptionPane.YES_OPTION) {
		    				showConsole(iIndex_popup, ProIndex, iPro[ProIndex].command_gwas[2], iPro[ProIndex].command_gwas[3]);
		    				format_conversion(iIndex_popup, ProIndex, true);
		    				PrintStatus(ProIndex,
				    				iPro[ProIndex].method_gwas.getName(), null,
				    				iPro[ProIndex].command_gwas, null);
				    		iPro[ProIndex].runCommand(iIndex_popup, 
				    				iPro[ProIndex].command_gwas,
				    				null);
				    	}
		    		} else if (gs_exist) {
		    			reAssign(iIndex_popup, iPro[ProIndex].command_gs);
		    			int reply = JOptionPane.showConfirmDialog(null, ConfirmFrame(ProIndex,
		    					null, iPro[ProIndex].method_gs.getName(),
			    				null, iPro[ProIndex].command_gs), "Your Configuration",  
	    					 	JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
	    					 	new ImageIcon("resources/iPat_icon.png"));
		    			if (reply == JOptionPane.YES_OPTION) {
			    			showConsole(iIndex_popup, ProIndex, iPro[ProIndex].command_gs[2], iPro[ProIndex].command_gs[3]);
				    		format_conversion(iIndex_popup, ProIndex, false);
				    		PrintStatus(ProIndex,
				    				null, iPro[ProIndex].method_gs.getName(),
				    				null, iPro[ProIndex].command_gs);
				    		iPro[ProIndex].runCommand(iIndex_popup, 
				    				iPro[ProIndex].command_gs,
				    				null);
				    	}
		    		} else if (bsa_exist) {
		    			reAssign(iIndex_popup, iPro[ProIndex].command_bsa);
		    			showConsole(iIndex_popup, ProIndex, iPro[ProIndex].command_bsa[2], iPro[ProIndex].command_bsa[3]);
			    		format_conversion(iIndex_popup, ProIndex, false);
			    		PrintStatus(ProIndex,
			    				null, "Bulk Segregation Analysis",
			    				null, iPro[ProIndex].command_bsa);
			    		iPro[ProIndex].runCommand(iIndex_popup, 
			    				iPro[ProIndex].command_bsa,
			    				null);
		    		}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	} else if (source == popup_delmo)
	    		removeiOB();
	    	iIndex_popup = -1;
	    	repaint(); 
	      };
		};
	    popup_tb.add(popup_opentb = new JMenuItem("Open file"));
	    popup_opentb.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_opentb.addActionListener(menuListener);
	    popup_tb.add(popup_isR = new JMenuItem("Treated as a Basic file"));
	    popup_isR.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_isR.addActionListener(menuListener);
	    popup_tb.add(popup_isC = new JMenuItem("Treated as Covariate"));
	    popup_isC.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_isC.addActionListener(menuListener);
	    popup_tb.add(popup_isK = new JMenuItem("Treated as Kinship"));
	    popup_isK.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_isK.addActionListener(menuListener);
	    popup_tb.addSeparator();
	    popup_tb.add(popup_deltb = new JMenuItem("Delete file"));
	    popup_deltb.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_deltb.addActionListener(menuListener);
	    popup_tb.setBorder(new BevelBorder(BevelBorder.RAISED));
	    
	    // Popup list on project
	    // Open working directory
	    popup_mo.add(popup_openmo = new JMenuItem("Open WD"));
	    popup_openmo.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_openmo.addActionListener(menuListener);
	    // Assign GWAS
	    popup_mo.add(popup_gwas = new JMenuItem("GWAS (Empty)"));
	    popup_gwas.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_gwas.addActionListener(menuListener);
	    // Assign GS
	    popup_mo.add(popup_gs = new JMenuItem("GS (Empty)"));
	    popup_gs.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_gs.addActionListener(menuListener);
	    // Assign BSA
	    popup_mo.add(popup_bsa = new JMenuItem("BSA (Empty)"));
	    popup_bsa.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_bsa.addActionListener(menuListener);
	    // Method section ends
	    popup_mo.addSeparator();
	    // Run
	    popup_mo.add(popup_run= new JMenuItem("Run"));
	    popup_run.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_run.addActionListener(menuListener);
	    popup_run.setEnabled(false);
	    popup_mo.add(popup_delmo = new JMenuItem("Delete project"));
	    popup_delmo.setHorizontalTextPosition(JMenuItem.RIGHT); 
	    popup_delmo.addActionListener(menuListener);
	    popup_mo.setBorder(new BevelBorder(BevelBorder.RAISED));
	    // DnD feature
    	new DropTarget(this, new DropTargetListener(){
    		@Override
    		public void drop(DropTargetDropEvent event) {
    			event.acceptDrop(DnDConstants.ACTION_COPY);
    			// Get dropped items' data
    			Transferable transferable = event.getTransferable();
    			// Get the format
    			DataFlavor[] flavors = transferable.getTransferDataFlavors();
    			for (DataFlavor flavor : flavors) {
    				try {
	    				// If multiple files
	    				if(flavor.equals(DataFlavor.javaFileListFlavor)){
	        				List<File> files = (List<File>) transferable.getTransferData(flavor);
	    					int count = 0;
	        				for (File file : files) {
	    						Point pointer = event.getLocation();
	            					int x = (int)pointer.getX();
	    						int y = (int)pointer.getY();
	    						create_TB(x + count*30, y + count*15, file);
	    						count++;
	    						// Print out the file path
	            				System.out.println("File path is '" + file.getPath() + "'.");
	            				repaint();}}
        			}catch (Exception e) {e.printStackTrace();}}
        		event.dropComplete(true); 
				hint_drop_label.setLocation(new Point(-99, -99));
    		}
			@Override
			public void dragOver(DropTargetDragEvent event) {
				Point pt = event.getLocation();
				hint_drop_label.setLocation(pt.x - 50, pt.y + 20);
				System.out.println("x: " + pt.x + " y: " + pt.y);
				System.out.println("Drag over");
			}
	        @Override
	       	public void dropActionChanged(DropTargetDragEvent event) {
				System.out.println("Drag change");}
	    	@Override
	    	public void dragEnter(DropTargetDragEvent dtde) {
				System.out.println("Drag enter");}
	    	@Override
	    	public void dragExit(DropTargetEvent dte) {
				System.out.println("Drag exit");}
    	});

	    try{
			jar = new File(iPat.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e1) {e1.printStackTrace();}
        
		try{
			iconIP = ImageIO.read(getClass().getResource("resources/iPat.png"));
			iPat_title.setIcon(new ImageIcon(iconIP));
			iPat_title.setAlpha((float)0.5);
		} catch (IOException ex){}
		try{
			for (int i=0; i<10; i++){
				Trash[i] = ImageIO.read(getClass().getResource("resources/trash" + i + ".png"));
				White[i] = ImageIO.read(getClass().getResource("resources/white" + i + ".png"));
			}
			trashH= Trash[0].getHeight(null);
			trashW= Trash[0].getWidth(null);	
		} catch (IOException ex){}		
		try{
			TBimage = ImageIO.read(this.getClass().getResourceAsStream("resources/File.png"));
			TB_C = ImageIO.read(this.getClass().getResourceAsStream("resources/File_c.png"));
			TB_K = ImageIO.read(this.getClass().getResourceAsStream("resources/File_k.png"));
			MOimage = ImageIO.read(this.getClass().getResourceAsStream("resources/Model.png"));
			MO_suc = ImageIO.read(this.getClass().getResourceAsStream("resources/Model_suc.png"));
			MO_fal = ImageIO.read(this.getClass().getResourceAsStream("resources/Model_fal.png"));
			hint_project = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_project.png"));			
			hint_trash = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_trash.png"));	
			hint_model = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_model.png"));	
			hint_drag = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_drag.png"));	
			hint_drop = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_drop.png"));
			hint_drop_label.setIcon(new ImageIcon(hint_drop));
			hint_drag_label.setIcon(new ImageIcon(hint_drag));
			hint_trash_label.setIcon(new ImageIcon(hint_trash));
			hint_project_label.setIcon(new ImageIcon(hint_project));
			hint_model_label.setIcon(new ImageIcon(hint_model));
		} catch (IOException ex){}
	
		for (int i=1; i<14; i++)
			black[i-1]= new Color(228+2*i,228+2*i,228+2*i, 255);
		this.setBackground(Color.white);

		////////////
		//LAYOUT.START
		////////////
		iPat_title.setOpaque(false);	

		trashl = new JLabel(new ImageIcon(Trash[0]));	
		startPanel = new JLayeredPane();
		nullPanel= new JLayeredPane();
		
		startPanel.setPreferredSize(new Dimension(Wide, Heigth));	
		startPanel.add(hint_trash_label, new Integer(1));			
		startPanel.add(hint_project_label, new Integer(2));		
		startPanel.add(trashl, new Integer(3));
		startPanel.add(iPat_title, new Integer(4));
		startPanel.add(hint_drag_label, new Integer(5));	
		startPanel.add(hint_model_label, new Integer(6));		
		startPanel.add(hint_drop_label, new Integer(7));
		
		hint_project_label.setBounds(new Rectangle(0, 0, hint_project.getWidth(null), hint_project.getHeight(null)));
		hint_project_label.setVisible(true);	
		hint_trash_label.setBounds(new Rectangle(0, 0, hint_trash.getWidth(null), hint_trash.getHeight(null)));
		hint_trash_label.setVisible(true);	
		hint_model_label.setBounds(new Rectangle(0, 0, hint_model.getWidth(null), hint_model.getHeight(null)));
		hint_model_label.setVisible(true);
		hint_drag_label.setBounds(new Rectangle(0, -10, hint_drag.getWidth(null), hint_drag.getHeight(null)));
		hint_drag_label.setVisible(true);
		hint_drop_label.setBounds(new Rectangle(-99, -99, hint_drag.getWidth(null), hint_drop.getHeight(null)));
		hint_drop_label.setVisible(true);
		
		trashl.setBounds(new Rectangle(-100,-100, trashW, trashH));
		trashl.setVisible(true);	
		
		hint_project_label.setAlpha(0);
		hint_trash_label.setAlpha(0);
		hint_model_label.setAlpha(0);
		hint_drag_label.setAlpha(0);
		
		iPat_title.setBounds(new Rectangle(510, 10, iconIP.getWidth(this), iconIP.getHeight(this))); 
		
		//this.setLayout(new MigLayout("debug, fill","[grow]","[grow]"));
		this.setLayout(null);
		this.add(startPanel);
		startPanel.setBounds(0, 0, Wide, Heigth);
		//this.add(nullPanel,"grow"); 
		startPanel.setOpaque(false);
			
		// Initial value
		Arrays.fill(iOBlink_delete, false);	
		boundary = startPanel.bounds();
		////////////
		////////////
		//LAYOUT.END
		////////////
		////////////
		gear_rotate = new Timer(50, new ActionListener() {
			int i=0;
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		    	if(all_false_permit()){
		    		running = false;
			    	rotate = Math.toRadians(0);
		    		gear_rotate.stop();
		    	}
		    	rotate = Math.toRadians(i*36);
		    	repaint();
		    	i++;	    	
		    }
		});	
		//periodically repaint (delcare after fill value in permit)
		periodically_repaint = new Timer(1000, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		    	repaint(); 
		    	if(partial_true_permit() && !running){ // avoid running it again over again
		    		running = true;
		    		gear_rotate.start();}
		    }
		});	
		
		hint_drag_timer = new Fade_timer(7000, hint_drag_label, new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent ae) {
				repaint();
				if(!hint_drag_timer.isActived() &&
					getTBindex().length == 0){
					hint_drag_timer.setActived(true);
					hint_drag_timer.fade_in();}
				else if(hint_drag_timer.TimeToOut()){
					System.out.println("drag show");
					hint_drag_timer.fade_out();
					hint_drag_timer.stop();}				
		    }
		});
		hint_project_timer = new Fade_timer(7001, hint_project_label, new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent ae) {
				repaint();
				if(!hint_project_timer.isActived() && 
					getTBindex().length > 0 && getMOindex().length == 0){
					hint_project_timer.setActived(true);
					hint_project_timer.fade_in();}
				else if(hint_project_timer.TimeToOut()){
					System.out.println("project show");
					hint_project_timer.fade_out();
					hint_project_timer.stop();}				
		    }
		});
		hint_model_timer= new Fade_timer(7002, hint_model_label, new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent ae) {
				repaint();
				if(!hint_model_timer.isActived() &&
					getMOindex().length > 0 && isContainMOgroup()){
					hint_model_timer.setActived(true);
					hint_model_timer.fade_in();}
				else if(hint_model_timer.TimeToOut()){
					System.out.println("model show");
					hint_model_timer.fade_out();
					hint_model_timer.stop();}				
		    }
		});
		periodically_repaint.start();
		hint_project_timer.start();
		hint_model_timer.start();
		hint_drag_timer.start();
		this.addMouseListener(new MouseAdapter() {	
			@Override
			public void mousePressed (MouseEvent ev) {
				// unselect object and line
				iIndex_select = -1;
				lineindex_select = -1;
				// make temp(hover) to real 
				temppt = ev.getPoint();
				if (selectable) {
					iIndex = iIndex_temp;    			
					if (iIndex != -1) {
						System.out.println("OBindex = " + iIndex + " groupindex: " + iOB[iIndex].getGroupIndex() + " containMO: " + iOB[iIndex].containMO + " isGroup: "+iOB[iIndex].isGroup);
						if (iOB[iIndex].isMO()) 
							System.out.println("Format : " + iPro[getProIndex(iIndex)].format);
					}
					lineindex = lineindex_temp;
					iIndex_select = iIndex_temp;
					lineindex_select = lineindex_temp;
					if (SwingUtilities.isRightMouseButton(ev)) {
						iIndex_popup = iIndex;
						switch (iOB[iIndex_popup].object) {
							case TB:
								popup_tb.show(iPatPanel.this, ev.getX(), ev.getY());
								break;
							case MO:
						    		int ProIndex = getProIndex(iIndex_popup);
								boolean gwas_exist = iPro[ProIndex].isGWASDeployed(), 
			    							gs_exist   = iPro[ProIndex].isGSDeployed(),
			    							bsa_exist  = iPro[ProIndex].isBSADeployed();
						   		if (gwas_exist)
						   			popup_gwas.setText("GWAS (" + iPro[ProIndex].method_gwas.getName() + ")"); 
						   		else
						   			popup_gwas.setText("GWAS (Empty)"); 			  		
						   		if (gs_exist)
									popup_gs.setText("GS ("+ iPro[ProIndex].method_gs.getName() + ")"); 
						   		else
									popup_gs.setText("GS (Empty)"); 
						   		if (bsa_exist)
						   			popup_bsa.setText("BSA (Ready)");
						   		// Check if any method exist and ready to run
						   		if (gwas_exist || gs_exist || bsa_exist)
						   			popup_run.setEnabled(true);
						   		else
						   			popup_run.setEnabled(false);
								popup_mo.show(iPatPanel.this, ev.getX(), ev.getY());   		
								break;
						}
					}
				}	
    			repaint();
			}				
			@Override 
			public void mouseReleased (MouseEvent ev) {			
				if (iIndex_target != -1) BuildLinkage();
				if (removeornot && ev.getY() > delbboundy && ev.getX() > delbboundx) {
					if (iIndex != -1)
						removeiOB();
					else if (lineindex != -1)
						removeiLine();			
	    				trashl.setBounds(new Rectangle(-1000, -50, Wide, 300));  				
					trashl.setVisible(true);
					removeornot=false;
				}
				iIndex = -1;
				iIndex_target = -1;
				lineindex = -1;
				linedragST.setLocation(0, 0);
				linedragED.setLocation(0, 0);
				Groupindex = -1;
				repaint();
			}	
			@Override
	    		public void mouseClicked (MouseEvent ev) {
				// When double left click
	    			if (SwingUtilities.isLeftMouseButton(ev) && ev.getClickCount() == 2) {
	    				// On a iPatobject
	    				if (iIndex_temp != -1) 
	    					openfile(iOB[iIndex_temp].getPath());
	    				// On an empty area, create a Project
					else {
						try {
							create_MO(ev.getX(), ev.getY());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
	    				repaint();
	    			}
	    		}
		});	
		addKeyListener(this);
		addMouseMotionListener(this);
	}	
	@Override
	public void mouseMoved(MouseEvent ev) {
		selectable = false;
		lineindex_temp = -1;
		for (int i = 0; i < linkcount; i++) { 
			if (iOBlink_delete[i]) 
				continue;
			if (isOnLine(iOB[iOBlink[i][0]].getLocation(), iOB[iOBlink[i][1]].getLocation(), ev.getPoint())) {
				selectable = true;
				lineindex_temp = i; 
				break;
			}
		}
		iIndex_temp = -1;
		for (int i = 0; i < iOBcount; i++){
			if(iOB[i].getBound().contains(ev.getPoint())){
				selectable = true;
				iIndex_temp = i;
				break;}}
		this.setCursor(Cursor.getPredefinedCursor(selectable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));	
	}
	@Override
	 public void mouseDragged(MouseEvent ev) {
		Point pt = ev.getPoint();
		if(iIndex != -1) {
			LinkToObject();
			if(isInBoundary() ||
			  (isEscapeToLeft()  && isMoveToRight(pt))    || (isEscapeToRight() && isMoveToLeft(pt)) ||
			  (isEscapeToAbove() && isMoveToDownward(pt)) || (isEscapeToBelow() && isMoveToUpward(pt))){
				iOB[iIndex].setDeltaLocation(pt.getX() - temppt.getX(), pt.getY() - temppt.getY());
				iOB[iIndex].updateLabel();
				temppt = pt;}}
		// Drag line segment, and prevent from creating line when draging objects
		if(iIndex == -1 && lineindex != -1){ 
			linedragST = new Point(pt.x - 30 + 10, pt.y - 30);
			linedragED = new Point(pt.x + 30 + 10, pt.y + 30);}
		// Trash hint show
		if((iIndex != -1 || lineindex != -1) && pt.y > delbboundy && pt.x > delbboundx && !removeornot){
			TrashAnimation = new Timer(15, new ActionListener() {
				int i=0;
			    @Override
			    public void actionPerformed(ActionEvent ae) {
			    	if(i < 10 && TA){
			    		trashl.setBounds(new Rectangle(Wide - trashW,  Heigth - trashH - 20, trashW, trashH));
			    		trashl.setIcon(new ImageIcon(Trash[i]));
						startPanel.setLayer(trashl, new Integer(200));
						trashl.setVisible(true);
						startPanel.revalidate();  
				        i++;
			    	}else{
			    		TrashAnimation.stop();
			    		i=0;
			    		TA=false;
			    	}
			    }
			});	
			TA = true;
			TrashAnimation.start();
			removeornot = true;}
		else if(pt.y < delbboundy || pt.x < delbboundx){
			trashl.setBounds(new Rectangle(Wide, -50, Wide, 300));
			startPanel.setLayer(trashl, new Integer(1));
			trashl.setVisible(true);
			removeornot = false;}	
		repaint();	
	}
	
	// iPat Object methods /////////////////////////
	static void create_linkedOB(int index, String path, iPatObject.Filetype type) throws IOException{
		int newIndex = iOBcount;
		System.out.println(index);
		create_TB(iOB[index].X + iOB[index].W/2, iOB[index].Y + 150, new File(path));
		iOB[newIndex].containMO = true;
		iOB[newIndex].isGroup = true;
		iOB[newIndex].Groupindex = iOB[index].getGroupIndex();
		iOB[newIndex].type = type;
		iOBlink[linkcount][0] = newIndex;
		iOBlink[linkcount][1] = index;
		linkcount ++;
	}
	static int getProIndex(int index){
		// 0 1 2 3 4 5 6 7
		// + - - - + - - +
		// 0 ----- 1 --- 2
		int index_pro = 0;
		for (int i = 0; i < index; i++)
			if(iOB[i].isMO()) index_pro++;
		return index_pro;
	}
	static int getOBIndex(int ProIndex){
		int counter = -1;
		for (int i = 0; i < iOBcount; i++){
			if(iOB[i].isMO()) counter++;
			if(counter == ProIndex) return i;}
		return -1;
	}
	static int[] getTBindex(){
		int[] TBindex = {};
		for (int i = 0; i < iOBcount; i++)
			if(iOB[i].object == iPatObject.Object.TB) TBindex = ArrayUtils.addAll(TBindex, i);
		return TBindex;
	}
	static int[] getMOindex(){
		int[] MOindex = {};
		for (int i = 0; i < iOBcount; i++)
			if(iOB[i].object == iPatObject.Object.MO) MOindex = ArrayUtils.addAll(MOindex, i);
		return MOindex;
	}
	static int[] getOBinGroup(int group){
		int[] OBindex = {};
		for(int i = 0; i < iOBcount; i++)
			if(iOB[i].getGroupIndex() == group) OBindex = ArrayUtils.addAll(OBindex, i);
		return OBindex;
	}
	static int getIndexofType(int group, iPatObject.Filetype type){
		for(int i : getOBinGroup(group))
			if(iOB[i].type == type) return i;
		return -1;	
	}
	static iPatObject.Filetype[] getTypeinGroup(int group){
		iPatObject.Filetype[] type = null;
		for(int i : getOBinGroup(group))
			type = ArrayUtils.addAll(type, iOB[i].type);
		return type;
	}
	static int[] getOBPair(int index){
		int[] pair = {};
		for(int i = 0; i < linkcount; i++){
			System.out.println(iOBlink[i][0] + " - " + iOBlink[i][1]);
			if(iOBlink[i][0] == index)
				pair = ArrayUtils.addAll(pair, iOBlink[i][1]);
			else if(iOBlink[i][1] == index)
				pair = ArrayUtils.addAll(pair, iOBlink[i][0]);}
		return pair;
	}
	static int[] getLinePair(int index){
		return new int[]{iOBlink[index][0], iOBlink[index][1]};
	}
	static void setGroupIndex(int group, int grindex){
		for (int i : getOBinGroup(group))
			iOB[i].Groupindex = grindex;
	}
	static void setContainMO(int group, boolean containMO){
		for (int i : getOBinGroup(group))
			iOB[i].containMO = containMO;
	}	
	static boolean isContainMO(int group){
		for (int iIndex_group : getOBinGroup(group)){
			if(iOB[iIndex_group].isMO()) return true;}
		return false;
	}
	static boolean isContainMOgroup(){
		for (int i : getMOindex()){
			if(iOB[i].isGroup) {
				link_model = i;
				return true;}}
		return false;
	}
	void removeiOB(){
		iOB[iIndex_select].remove();
		int[] pair = getOBPair(iIndex_select);
		for (int i : getOBPair(iIndex_select))
			BreakLinkage(iIndex_select, i);
		for (int i = 0; i < linkcount; i++){
			if(iOBlink[i][0] == iIndex_select || iOBlink[i][1] == iIndex_select){
				iOBlink_delete[i] = true;
				iOBlink[i][0] = -1;
				iOBlink[i][1] = -1;}}
		iIndex_select = -1;
		repaint();
	}
	void removeiLine(){
		BreakLinkage(iOBlink[lineindex_select][0], iOBlink[lineindex_select][1]);
		iOBlink_delete[lineindex_select] = true;
		iOBlink[lineindex_select][0] = -1;
		iOBlink[lineindex_select][1] = -1;
		lineindex_select = -1;
		repaint();
	}
	boolean isInBoundary(){
		return boundary.intersection(iOB[iIndex].getBound()).equals(iOB[iIndex].getBound()); 
	}
	boolean isEscapeToLeft(){
		return boundary.getMinX() > iOB[iIndex].getBound().getMinX();
	}
	boolean isEscapeToRight(){
		return boundary.getMaxX() < iOB[iIndex].getBound().getMaxX();
	}
	boolean isEscapeToAbove(){
		return boundary.getMinY() > iOB[iIndex].getBound().getMinY();
	}
	boolean isEscapeToBelow(){
		return boundary.getMaxY() < iOB[iIndex].getBound().getMaxY();
	}
	boolean isMoveToRight(Point point_drag){
		return point_drag.getX() - temppt.getX() > 0;
	}
	boolean isMoveToLeft(Point point_drag){
		return point_drag.getX() - temppt.getX() < 0;
	}
	boolean isMoveToDownward(Point point_drag){
		return point_drag.getY() - temppt.getY() > 0;
	}
	boolean isMoveToUpward(Point point_drag){
		return point_drag.getY() - temppt.getY() < 0;
	}
	////////////////////////////////////////////////
	void LinkToObject(){
		iIndex_target = getTargetIndex();
		linktype = getLinkType(iIndex, iIndex_target); 
		switch (linktype){
		// 1. w/o MO - w/o MO
		case 1: 
			lineST = iOB[iIndex].pt;
			lineED = iOB[iIndex_target].pt;
			System.out.println("case 1");
			break;
		// 2. w/ MO - w/o MO
		case 2: 
			lineST = iOB[iIndex].pt;
			lineED = iOB[iIndex_target].pt;
			System.out.println("case 2");
			break;			
		// 3. w/o MO - w/ MO
		case 3:
			lineST = iOB[iIndex].pt;
			lineED = iOB[iIndex_target].pt;
			System.out.println("case 3");
			break;
		// 4. w/ MO - w/ MO
		case 4:
			lineST = nullPoint;
			lineED = nullPoint;
			iIndex_target = -1;
			System.out.println("case 4");
			break;
		default: 
			lineST = nullPoint;
			lineED = nullPoint;
			iIndex_target = -1;
			System.out.println("case 0");
			break;
		}
	}
	int getTargetIndex(){
		int target = -1;
		double minValue = 99999;
		for (int i = 0; i < iOBcount; i++){
			double dist = getDistance(iOB[iIndex].pt, iOB[i].pt);
			System.out.println("index " + i + " = " + dist);
			if(i == iIndex) 
				continue;
			else if(dist < minValue && dist < 200){
					// if two objects are not in the same group or both of them belong to group 0
				//	((iOB[iIndex].Groupindex == -1 && iOB[i].Groupindex == -1) || iOB[iIndex].Groupindex != iOB[i].Groupindex)){
				System.out.println("selected i = " + i);
				minValue = dist;
				target = i;}}
		return target;
	}
	int getLinkType(int ob1, int ob2){
		// prevent no target
		if(ob2 == -1 ||
		  (iOB[ob1].getGroupIndex() != -1 && iOB[ob1].getGroupIndex() == iOB[ob2].getGroupIndex()))	return 0;
		else if(!iOB[ob1].containMO && !iOB[ob2].containMO)		return 1;
		else if( iOB[ob1].containMO && !iOB[ob2].containMO) 	return 2;		
		else if(!iOB[ob1].containMO &&  iOB[ob2].containMO) 	return 3;
		else if( iOB[ob1].containMO &&  iOB[ob2].containMO) 	return 4;
		return 0;
	}
	void BuildLinkage(){
		switch(linktype){
		// 1. w/o MO - w/o MO
		case 1: 
			if(!iOB[iIndex].isGroup && !iOB[iIndex_target].isGroup){
				iOB[iIndex].isGroup = true;
				iOB[iIndex].Groupindex = Groupcount;
				iOB[iIndex_target].isGroup = true;
				iOB[iIndex_target].Groupindex = Groupcount;
				Groupcount++;}
			else if(!iOB[iIndex].isGroup && iOB[iIndex_target].isGroup){
				iOB[iIndex].isGroup = true;
				iOB[iIndex].Groupindex = iOB[iIndex_target].Groupindex;}
			else if(iOB[iIndex].isGroup && !iOB[iIndex_target].isGroup){
				iOB[iIndex_target].isGroup = true;
				iOB[iIndex_target].Groupindex = iOB[iIndex].Groupindex;}
			else if(iOB[iIndex].isGroup && iOB[iIndex_target].isGroup)
				setGroupIndex(iOB[iIndex_target].getGroupIndex(), iOB[iIndex].Groupindex);
			iOBlink[linkcount][0] = iIndex;
			iOBlink[linkcount][1] = iIndex_target;
			linkcount ++;
			System.out.println("build case 1");
			break;
		// 2. w/ MO - w/o MO
		case 2: 
			if(!iOB[iIndex].isGroup && !iOB[iIndex_target].isGroup){
				iOB[iIndex].isGroup = true;
				iOB[iIndex].Groupindex = Groupcount;
				iOB[iIndex_target].isGroup = true;
				iOB[iIndex_target].Groupindex = Groupcount;
				Groupcount++;
				iOB[iIndex_target].containMO = true;}
			else if(!iOB[iIndex].isGroup && iOB[iIndex_target].isGroup){
				iOB[iIndex].isGroup = true;
				iOB[iIndex].Groupindex = iOB[iIndex_target].Groupindex;
				setContainMO(iOB[iIndex_target].getGroupIndex(), true);}
			else if(iOB[iIndex].isGroup && !iOB[iIndex_target].isGroup){
				iOB[iIndex_target].isGroup = true;
				iOB[iIndex_target].Groupindex = iOB[iIndex].Groupindex;
				iOB[iIndex_target].containMO = true;}
			else if(iOB[iIndex].isGroup && iOB[iIndex_target].isGroup){
				setContainMO(iOB[iIndex_target].getGroupIndex(), true);
				setGroupIndex(iOB[iIndex_target].getGroupIndex(), iOB[iIndex].Groupindex);}
			iOBlink[linkcount][0] = iIndex;
			iOBlink[linkcount][1] = iIndex_target;
			linkcount ++;
			System.out.println("build case 2");
			break;			
		// 3. w/o MO - w/ MO
		case 3:
			if(!iOB[iIndex].isGroup && !iOB[iIndex_target].isGroup){
				iOB[iIndex].isGroup = true;
				iOB[iIndex].Groupindex = Groupcount;
				iOB[iIndex_target].isGroup = true;
				iOB[iIndex_target].Groupindex = Groupcount;
				Groupcount++;
				iOB[iIndex].containMO = true;}
			else if(!iOB[iIndex].isGroup && iOB[iIndex_target].isGroup){
				iOB[iIndex].isGroup = true;
				iOB[iIndex].Groupindex = iOB[iIndex_target].Groupindex;
				iOB[iIndex].containMO = true;}
			else if(iOB[iIndex].isGroup && !iOB[iIndex_target].isGroup){
				iOB[iIndex_target].isGroup = true;
				iOB[iIndex_target].Groupindex = iOB[iIndex].Groupindex;
				setContainMO(iOB[iIndex].getGroupIndex(), true);}
			else if(iOB[iIndex].isGroup && iOB[iIndex_target].isGroup){
				setContainMO(iOB[iIndex].getGroupIndex(), true);
				setGroupIndex(iOB[iIndex].getGroupIndex(), iOB[iIndex_target].Groupindex);}
			iOBlink[linkcount][0] = iIndex;
			iOBlink[linkcount][1] = iIndex_target;
			linkcount ++;
			System.out.println("build case 3");
			break;
		// 4. w/ MO - w/ MO
		case 4: break;
		}
		lineST = nullPoint;
		lineED = nullPoint;
		linktype = 0;
	}
	void BreakLinkage(int ob1, int ob2){
		// set a new group index, trace toward ob2 direction
		traceLinkage(ob1, ob2);
		int gr1 = iOB[ob1].getGroupIndex(), 
			gr2 = iOB[ob2].getGroupIndex();
		Groupcount ++;
		// see if contain MO
		System.out.println("Group1: " + gr1);
		if(isContainMO(gr1)) setContainMO(gr1, true);
		else setContainMO(gr1, false);

		System.out.println("Group1: " + gr2);
		if(isContainMO(gr2)) setContainMO(gr2, true);
		else setContainMO(gr2, false);
		// see if still a group
		System.out.println("gr1: "+gr1 + " length " + getOBinGroup(gr1).length);
		System.out.println("gr2: "+gr2 + " length " + getOBinGroup(gr2).length);
		if(getOBinGroup(gr1).length == 1){
			iOB[ob1].isGroup = false;
			iOB[ob1].Groupindex = -1;}
		if(getOBinGroup(gr2).length == 1){
			iOB[ob2].isGroup = false;
			iOB[ob2].Groupindex = -1;}
	}
	void traceLinkage(int ori, int traceindex){
		iOB[traceindex].Groupindex = Groupcount;
		System.out.println("iOB: " + traceindex + "; set to: " + Groupcount);
		for (int i = 0; i < linkcount; i++){
			if(iOBlink[i][0] == traceindex && iOBlink[i][1] != ori){
				traceLinkage(traceindex, iOBlink[i][1]);}
			else if(iOBlink[i][1] == traceindex && iOBlink[i][0] != ori){
				traceLinkage(traceindex, iOBlink[i][0]);}}
	}
	public void open_config (int iIndex, boolean isGWAS, boolean isBSA) throws IOException {
		int MOindex = getProIndex(iIndex);		
		iOB[iIndex].updateImage(MOimage);
		// Catch format
		iPro[MOindex].format = catch_files(iIndex);
		// If doesn't find a match format
		if (iPro[MOindex].isNAformat()) {	
			String msg = "No match format found. \nPlease see section 2.3 from <a href=\"http://zzlab.net/iPat/iPat_manual.pdf\">iPat User Manaul</a> for details.<br>";
			for (int i : getOBinGroup(iOB[iIndex].getGroupIndex())) {
				if(iOB[i].isMO()) 
					continue; 
				msg = msg + "   " + iOB[i].name.getText() + ":\t" + iOB[i].type.getName() + "<br>";
			}
			JEditorPane ep = new JEditorPane();
			ep.setEditable(false);
			ep.setBackground(new Color(237, 237, 237, 100));
		    ep.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		    ep.setText(msg);
		    // handle link events
	        ep.addHyperlinkListener(new HyperlinkListener() {
	            @Override
	            public void hyperlinkUpdate(HyperlinkEvent e) {
	                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
	                    if (Desktop.isDesktopSupported()) {
	                        try {
	                            Desktop.getDesktop().browse(e.getURL().toURI());
	                        } catch (IOException | URISyntaxException e1) {
	                            e1.printStackTrace();
	                        }
	                    }
	                }
	            }
	        });		
			JOptionPane.showMessageDialog(new JFrame(), ep,
				    "Incorrect format", JOptionPane.ERROR_MESSAGE);
		// If find a match format
		} else {
		   	GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	       	Point centerPoint = local_env.getCenterPoint();
	       	int dx = centerPoint.x - 500 / 2;
	        int dy = centerPoint.y - 400 / 2;	
	        System.out.println("MOindex : " + MOindex);
			ConfigFrame configframe = new ConfigFrame(iIndex, iOB, MOindex, iPro, isGWAS, isBSA);
			configframe.setResizable(true);
			configframe.setLocation(dx, dy);
		}
	}
	public void showConsole(int OBindex, int MOindex, String title, String MOPath){
		iOB[OBindex].setPath(MOPath);
		iOB[OBindex].setLabel(title);
        GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = local_env.getCenterPoint();
   		int dx = centerPoint.x - 500 / 2;
   		int dy = centerPoint.y - 350 / 2;  
  		iPro[MOindex].frame.setBounds(dx - 100, dy, 500, 350);
  		iPro[MOindex].frame.setVisible(true); 
  		iPro[MOindex].frame.setTitle(title);
  		iPro[MOindex].frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				if(iPro[MOindex].process.isAlive()){
					iPro[MOindex].process.destroy();
					iOB[OBindex].updateImage(MO_fal);}
				System.out.println("Project killed");}});  
	}
	@Override
	protected void paintComponent(Graphics g) {	
		if(hint_model_timer.isActived()){
			hint_model_label.setBounds(new Rectangle(iOB[link_model].X - 125, iOB[link_model].Y + 65, 
													 hint_model.getWidth(null), hint_model.getHeight(null)));}    	
	    super.paintComponent(g);	
	    g.setColor(ovalcolor);
		Draw_Lines(g, lineST, lineED, dashed); //temp_link 
		Draw_Lines(g, linedragST, linedragED, dashed); //drag_link
		DrawLinkedLine(g); //object_link
		// Draw OB image
		for (int i = 0; i < iOBcount; i++){
			int ProIndex = getProIndex(i);
			if(!iOB[i].isDeleted && 
			  ((iOB[i].isMO() && !iPro[ProIndex].rotate_switch) || (iOB[i].isTB())))
				g.drawImage(iOB[i].image, iOB[i].X, iOB[i].Y, this);}
		// Make gear rotate
		for (int i = 0; i < MOcount; i++){
			int OBindex = getOBIndex(i);
			if(iOB[OBindex].isDeleted) continue;
			if(iPro[i].rotate_switch){
				AffineTransform tx = AffineTransform.getRotateInstance(rotate, iOB[OBindex].W/2, iOB[OBindex].H/2);
        		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
       		  	g.drawImage(op.filter((BufferedImage) iOB[OBindex].image, null), iOB[OBindex].X, iOB[OBindex].Y, this);}}
		// Draw select iOB
		if(iIndex_select != -1) Draw_Rects(g, iOB[iIndex_select].X - 5, iOB[iIndex_select].Y - 3, iOB[iIndex_select].W + 10, iOB[iIndex_select].H + 6, select);
    }
	public void openfile(String path){
		File openfile= new File(path);
		try{
			Desktop.getDesktop().open(openfile);
		} catch(IOException e) {e.printStackTrace();}
	}
	JScrollPane ConfirmFrame(int MOindex, String method_gwas, String method_gs, String[] command_gwas, String[] command_gs){
		String format1 = "%1$27s %2$s", format2 = "%1$-30s %2$s";
		JTextArea TextArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(TextArea);
		TextArea.setLineWrap(true);
		TextArea.setWrapStyleWord(true);
		TextArea.setSize(500, 230);
		TextArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		TextArea.append("************************** Welcome to iPat **************************\n");
		if(method_gwas != null && method_gs != null){	
			TextArea.append("Project Name:" + "\n");
			TextArea.append(String.format(format1, " ", command_gwas[2]) + "\n");
			TextArea.append("Working Directory:" + "\n");
			TextArea.append(String.format(format1, " ", command_gwas[3]) + "\n");
			TextArea.append("Data Format:" + "\n");
			TextArea.append(String.format(format1, " ", command_gwas[5]) + "\n"); 
			TextArea.append("Quality Control:" + "\n");
			TextArea.append(String.format(format1, "Missing Values: ", "< " + command_gwas[6]) + "\n");
			TextArea.append(String.format(format1, "MAF: ", "> " + command_gwas[7]) + "\n");
			TextArea.append("GWAS Method: " + method_gwas + "\n");
			switch(method_gwas){
			case "GAPIT":
				TextArea.append(String.format(format1, "Linear Model: ", command_gwas[17]) + "\n");
				TextArea.append(String.format(format1, "kinship.cluster: ", command_gwas[18]) + "\n");
				TextArea.append(String.format(format1, "kinship.group: ", command_gwas[19]) + "\n");
				TextArea.append(String.format(format1, "SNP.fraction: ", command_gwas[20]) + "\n");
				TextArea.append(String.format(format1, "file.fragment: ", command_gwas[21]) + "\n");
				TextArea.append(String.format(format1, "model selection: ", command_gwas[22]) + "\n");
				break;
			case "FarmCPU":
				TextArea.append(String.format(format1, "method.bin:", command_gwas[17]) + "\n");
				TextArea.append(String.format(format1, "maxLoop:", command_gwas[18]) + "\n");
				break;
			case "PLINK":
				TextArea.append(String.format(format1, "Confidence Interval:", command_gwas[17]) + "\n");
				break;}
			TextArea.append("GS Method: " + method_gs + "\n");
			switch(method_gs){
			case "gBLUP":
				TextArea.append(String.format(format1, "GWAS-Assisted:",command_gs[20]) + "\n");
				TextArea.append(String.format(format1, "Bonferroni cutoff:", command_gs[21]) + "\n");
				TextArea.append(String.format(format1, "SNP.fraction:", command_gs[17]) + "\n");
				TextArea.append(String.format(format1, "file.fragment:", command_gs[18]) + "\n");
				TextArea.append(String.format(format1, "model selection:", command_gs[19]) + "\n");
				break;
			case "rrBLUP":
				TextArea.append(String.format(format1, "GWAS-Assisted:",command_gs[19]) + "\n");
				TextArea.append(String.format(format1, "Bonferroni cutoff:", command_gs[20]) + "\n");
				TextArea.append(String.format(format1, "impute.method:", command_gs[17]) + "\n");
				TextArea.append(String.format(format1, "shrink:", command_gs[18]) + "\n");
				break;
			case "BGLR":
				TextArea.append(String.format(format1, "GWAS-Assisted:",command_gs[22]) + "\n");
				TextArea.append(String.format(format1, "Bonferroni cutoff:", command_gs[23]) + "\n");
				TextArea.append(String.format(format1, "Model (Markers):", command_gs[17]) + "\n");
				TextArea.append(String.format(format1, "response_type:", command_gs[18]) + "\n");
				TextArea.append(String.format(format1, "nIter:", command_gs[19]) + "\n");
				TextArea.append(String.format(format1, "burnIn:", command_gs[20]) + "\n");
				TextArea.append(String.format(format1, "thin:", command_gs[21]) + "\n");
				break;}}
		else if(method_gwas != null){
			TextArea.append("Project Name:" + "\n");
			TextArea.append(String.format(format1, " ", command_gwas[2]) + "\n");
			TextArea.append("Working Directory:" + "\n");
			TextArea.append(String.format(format1, " ", command_gwas[3]) + "\n");
			TextArea.append("Data Format:" + "\n");
			TextArea.append(String.format(format1, " ", command_gwas[5]) + "\n"); 
			TextArea.append("Quality Control:" + "\n");
			TextArea.append(String.format(format1, "Missing Values: ", "< " + command_gwas[6]) + "\n");
			TextArea.append(String.format(format1, "MAF: ", "> " + command_gwas[7]) + "\n");
			TextArea.append("GWAS Method: " + method_gwas + "\n");
			switch(method_gwas){
			case "GAPIT":
				TextArea.append(String.format(format1, "Linear Model: ", command_gwas[17]) + "\n");
				TextArea.append(String.format(format1, "kinship.cluster: ", command_gwas[18]) + "\n");
				TextArea.append(String.format(format1, "kinship.group: ", command_gwas[19]) + "\n");
				TextArea.append(String.format(format1, "SNP.fraction: ", command_gwas[20]) + "\n");
				TextArea.append(String.format(format1, "file.fragment: ", command_gwas[21]) + "\n");
				TextArea.append(String.format(format1, "model selection: ", command_gwas[22]) + "\n");
				break;
			case "FarmCPU":
				TextArea.append(String.format(format1, "method.bin:", command_gwas[17]) + "\n");
				TextArea.append(String.format(format1, "maxLoop:", command_gwas[18]) + "\n");
				break;
			case "PLINK":
				TextArea.append(String.format(format1, "Confidence Interval:", command_gwas[17]) + "\n");
				break;}}
		else if(method_gs != null){
			TextArea.append("Project Name:" + "\n");
			TextArea.append(String.format(format1, " ", command_gs[2]) + "\n");
			TextArea.append("Working Directory:" + "\n");
			TextArea.append(String.format(format1, " ", command_gs[3]) + "\n");
			TextArea.append("Data Format:" + "\n");
			TextArea.append(String.format(format1, " ", command_gs[5]) + "\n"); 
			TextArea.append("Quality Control:" + "\n");
			TextArea.append(String.format(format1, "Missing Values: ", "< " + command_gs[6]) + "\n");
			TextArea.append(String.format(format1, "MAF: ", "> " + command_gs[7]) + "\n");
			TextArea.append("GS Method: " + method_gs + "\n");
			switch(method_gs){
			case "gBLUP":
				TextArea.append(String.format(format1, "GWAS-Assisted:",command_gs[20]) + "\n");
				TextArea.append(String.format(format1, "Bonferroni cutoff:", command_gs[21]) + "\n");
				break;
			case "rrBLUP":
				TextArea.append(String.format(format1, "GWAS-Assisted:",command_gs[19]) + "\n");
				TextArea.append(String.format(format1, "Bonferroni cutoff:", command_gs[20]) + "\n");
				TextArea.append(String.format(format1, "impute.method:", command_gs[17]) + "\n");
				TextArea.append(String.format(format1, "shrink:", command_gs[18]) + "\n");
				break;
			case "BGLR":
				TextArea.append(String.format(format1, "GWAS-Assisted:",command_gs[22]) + "\n");
				TextArea.append(String.format(format1, "Bonferroni cutoff:", command_gs[23]) + "\n");
				TextArea.append(String.format(format1, "Model (Markers):", command_gs[17]) + "\n");
				TextArea.append(String.format(format1, "response_type:", command_gs[18]) + "\n");
				TextArea.append(String.format(format1, "nIter:", command_gs[19]) + "\n");
				TextArea.append(String.format(format1, "burnIn:", command_gs[20]) + "\n");
				TextArea.append(String.format(format1, "thin:", command_gs[21]) + "\n");
				break;}}
		TextArea.append("********************************************************************* \n");
		return scrollPane;
	}
	
	public void PrintStatus(int MOindex, String method_gwas, String method_gs, String[] command_gwas, String[] command_gs){
		String format1 = "%1$27s %2$s", format2 = "%1$-30s %2$s";
		iPro[MOindex].textarea.setFont(new Font("monospaced", Font.PLAIN, 12));
		iPro[MOindex].textarea.append("************************** Welcome to iPat **************************\n");
		iPro[MOindex].textarea.append("Date/Time:" + "\n");
		iPro[MOindex].textarea.append(String.format(format1, " ", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())) + "\n");
		if(method_gwas != null && method_gs != null){	
			iPro[MOindex].textarea.append("Project Name:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gwas[2]) + "\n");
			iPro[MOindex].textarea.append("Working Directory:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gwas[3]) + "\n");
			iPro[MOindex].textarea.append("Data Format:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gwas[5]) + "\n"); 
			iPro[MOindex].textarea.append("Quality Control:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, "Missing Values: ", "< " + command_gwas[6]) + "\n");
			iPro[MOindex].textarea.append(String.format(format1, "MAF: ", "> " + command_gwas[7]) + "\n");
			iPro[MOindex].textarea.append("GWAS Method: " + method_gwas + "\n");
			switch(method_gwas){
			case "GAPIT":
				iPro[MOindex].textarea.append(String.format(format1, "Linear Model: ", command_gwas[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "kinship.cluster: ", command_gwas[18]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "kinship.group: ", command_gwas[19]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "SNP.fraction: ", command_gwas[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "file.fragment: ", command_gwas[21]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "model selection: ", command_gwas[22]) + "\n");
				break;
			case "FarmCPU":
				iPro[MOindex].textarea.append(String.format(format1, "method.bin:", command_gwas[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "maxLoop:", command_gwas[18]) + "\n");
				break;
			case "PLINK":
				iPro[MOindex].textarea.append(String.format(format1, "Confidence Interval:", command_gwas[17]) + "\n");
				break;}
			iPro[MOindex].textarea.append("GS Method: " + method_gs + "\n");
			switch(method_gs){
			case "gBLUP":
				iPro[MOindex].textarea.append(String.format(format1, "GWAS-Assisted:",command_gs[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Bonferroni cutoff:", command_gs[21]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "SNP.fraction:", command_gs[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "file.fragment:", command_gs[18]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "model selection:", command_gs[19]) + "\n");
				break;
			case "rrBLUP":
				iPro[MOindex].textarea.append(String.format(format1, "GWAS-Assisted:",command_gs[19]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Bonferroni cutoff:", command_gs[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "impute.method:", command_gs[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "shrink:", command_gs[18]) + "\n");
				break;
			case "BGLR":
				iPro[MOindex].textarea.append(String.format(format1, "GWAS-Assisted:",command_gs[22]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Bonferroni cutoff:", command_gs[23]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Model (Markers):", command_gs[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "response_type:", command_gs[18]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "nIter:", command_gs[19]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "burnIn:", command_gs[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "thin:", command_gs[21]) + "\n");
				break;}}
		else if(method_gwas != null){
			iPro[MOindex].textarea.append("Project Name:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gwas[2]) + "\n");
			iPro[MOindex].textarea.append("Working Directory:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gwas[3]) + "\n");
			iPro[MOindex].textarea.append("Data Format:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gwas[5]) + "\n"); 
			iPro[MOindex].textarea.append("Quality Control:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, "Missing Values: ", "< " + command_gwas[6]) + "\n");
			iPro[MOindex].textarea.append(String.format(format1, "MAF: ", "> " + command_gwas[7]) + "\n");
			iPro[MOindex].textarea.append("GWAS Method: " + method_gwas + "\n");
			switch(method_gwas){
			case "GAPIT":
				iPro[MOindex].textarea.append(String.format(format1, "Linear Model: ", command_gwas[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "kinship.cluster: ", command_gwas[18]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "kinship.group: ", command_gwas[19]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "SNP.fraction: ", command_gwas[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "file.fragment: ", command_gwas[21]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "model selection: ", command_gwas[22]) + "\n");
				break;
			case "FarmCPU":
				iPro[MOindex].textarea.append(String.format(format1, "method.bin:", command_gwas[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "maxLoop:", command_gwas[18]) + "\n");
				break;
			case "PLINK":
				iPro[MOindex].textarea.append(String.format(format1, "Confidence Interval:", command_gwas[17]) + "\n");
				break;}}
		else if(method_gs != null){
			iPro[MOindex].textarea.append("Project Name:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gs[2]) + "\n");
			iPro[MOindex].textarea.append("Working Directory:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gs[3]) + "\n");
			iPro[MOindex].textarea.append("Data Format:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, " ", command_gs[5]) + "\n"); 
			iPro[MOindex].textarea.append("Quality Control:" + "\n");
			iPro[MOindex].textarea.append(String.format(format1, "Missing Values: ", "< " + command_gs[6]) + "\n");
			iPro[MOindex].textarea.append(String.format(format1, "MAF: ", "> " + command_gs[7]) + "\n");
			iPro[MOindex].textarea.append("GS Method: " + method_gs + "\n");
			switch(method_gs){
			case "gBLUP":
				iPro[MOindex].textarea.append(String.format(format1, "GWAS-Assisted:",command_gs[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Bonferroni cutoff:", command_gs[21]) + "\n");
				break;
			case "rrBLUP":
				iPro[MOindex].textarea.append(String.format(format1, "GWAS-Assisted:",command_gs[19]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Bonferroni cutoff:", command_gs[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "impute.method:", command_gs[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "shrink:", command_gs[18]) + "\n");
				break;
			case "BGLR":
				iPro[MOindex].textarea.append(String.format(format1, "GWAS-Assisted:",command_gs[22]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Bonferroni cutoff:", command_gs[23]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "Model (Markers):", command_gs[17]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "response_type:", command_gs[18]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "nIter:", command_gs[19]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "burnIn:", command_gs[20]) + "\n");
				iPro[MOindex].textarea.append(String.format(format1, "thin:", command_gs[21]) + "\n");
				break;}}
		iPro[MOindex].textarea.append("********************************************************************* \n");
	}
	public static void create_TB (int x, int y, File file) throws IOException{
		iOB[iOBcount] = new iPatObject();
		startPanel.add(iOB[iOBcount].name);
		iOB[iOBcount].setPath(file.getAbsolutePath());
		iOB[iOBcount].setLabel(file.getName());
		iOB[iOBcount].updateImage(TBimage);
		iOB[iOBcount].setAsTB();
		iOB[iOBcount].setLocation(new Point(x - (iOB[iOBcount].W/2), y - iOB[iOBcount].H - 5));
		iOB[iOBcount].updateLabel();
		iOBcount++;
	}
	public static void create_MO (int x, int y) throws IOException{
		iOB[iOBcount] = new iPatObject();
		startPanel.add(iOB[iOBcount].name);
		iOB[iOBcount].setPath(df_wd);
		iOB[iOBcount].setLabel("Project_" + (MOcount + 1));
		iOB[iOBcount].updateImage(MOimage);
		iOB[iOBcount].setAsMO();
		iOB[iOBcount].setLocation(new Point(x - (iOB[iOBcount].W/2), y - iOB[iOBcount].H - 5));
		iOB[iOBcount].updateLabel();
		iOB[iOBcount].containMO = true;
		iPro[MOcount] = new iPatProject();
		MOcount ++;
		iOBcount++;
	}
	public static File iPatChooser (String title, boolean DirOnly) {
		File selectedfile = null;
		if (DirOnly) {
			switch (iPat.UserOS.type) {
				case Windows:
					int flag;
					JFileChooser fc = new JFileChooser();
					//choose folder only
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					String path = null;
					File f = null;
					flag = fc.showOpenDialog(null);     
					if (flag == JFileChooser.APPROVE_OPTION) {
			            f=fc.getSelectedFile();    
			            selectedfile = new File(f.getPath());
			        }      
				    break;
				default:
					System.setProperty("apple.awt.fileDialogForDirectories", "true");
					chooser = new FileDialog(new JFrame(), title, FileDialog.LOAD);   
					chooser.setVisible(true);   
				    if (chooser != null)   
				    		selectedfile = chooser.getFiles()[0];
					System.setProperty("apple.awt.fileDialogForDirectories", "false");
					break;
			}
		} else {
			chooser = new FileDialog(new JFrame(), title, FileDialog.LOAD);   
			chooser.setVisible(true);   
		    if (chooser != null)   
		    		selectedfile = chooser.getFiles()[0];
		}	
		return selectedfile;
	}
			
	int MinValue(int[] array){  
	     int minValue = array[0];  
	     for (int i = 1; i < array.length; i++)  
	    	 if(array[i] < minValue) minValue = array[i]; 
	     return minValue;  
	} 
	double getDistance(Point pt1, Point pt2){
		double dist = 0;
		dist = Math.sqrt(Math.pow((pt1.getX() - pt2.getX()), 2) + Math.pow((pt1.getY() - pt2.getY()), 2));
		dist = Math.round((dist * 100.0)/100.0);
		return dist;
	}
	boolean isOnLine(Point pt1, Point pt2, Point pointer){
		return getDistance(pt1, pointer) + getDistance(pt2, pointer) < getDistance(pt1, pt2) + 2;
	}
	void Draw_Lines(Graphics g, Point pt1, Point pt2, Stroke s){	
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();
        //set the stroke of the copy, not the original 
        g2d.setStroke(s);
        g2d.drawLine((int)pt1.getX(), (int)pt1.getY(), (int)pt2.getX(), (int)pt2.getY());
        //gets rid of the copy
        g2d.dispose();        
	}
	void Draw_Rects(Graphics g, int x1, int y1, int x2, int y2, Stroke s){	
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();
        //set the stroke of the copy, not the original 
        g2d.setStroke(s);
        g2d.drawRect(x1, y1, x2, y2);
        //gets rid of the copy
        g2d.dispose();        
	}
	public void DrawLinkedLine(Graphics g){
		for (int i = 0; i < linkcount; i++){
			int[] pair = getLinePair(i);
			if(iOBlink_delete[i] || iOB[pair[0]].isDeleted || iOB[pair[1]].isDeleted) continue;
			Draw_Lines(g, iOB[iOBlink[i][0]].getLocation(), 
						  iOB[iOBlink[i][1]].getLocation(), 
						  i == lineindex_select ? solid : dashed);}
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode(); 
		System.out.println("key input: "+key);
		// press "del"
		if(key == 8){
			System.out.println("delete");
			if(iIndex_select != -1)
				removeiOB();
			else if(lineindex_select != -1)
				removeiLine();}
		// Debug: press "d", then "b" to activate
		if(first_d && key == 66){
			debug = !debug; 
			first_d = false;
			System.out.println("debug: "+debug);}
		else if(key == 68)
			first_d = true; 
		else
			first_d = false;
	}
	@Override
	public void keyReleased(KeyEvent e) {}	
	public static boolean all_true(boolean[] array){
	    for (boolean b : array) if(!b) return false;
	    return true;
	}
	public static boolean all_false(boolean[] array){
	    for (boolean b : array) if(b) return false;
	    return true;
	}
	public static boolean partial_true(boolean[] array){
	    for (boolean b : array) if(b) return true;
	    return false;
	}
	
	boolean all_false_permit(){
		for(int i = 0; i < MOcount; i++) if(iPro[i].rotate_permit) return false;
		return true;
	}
	boolean partial_true_permit(){
		for(int i = 0; i < MOcount; i++) if(iPro[i].rotate_permit) return true;
		return false;
	}
	
	public static String[] read_lines(String filename, int bound) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String[] lines = new String[10];
        String readline;
        int nLine = 0, boundary = 0;
        while((readline = reader.readLine()) != null && nLine < bound && boundary < 100)  {
        	if(!readline.startsWith("##")){ //VCF comment escape
            	lines[nLine++] = readline;
        	}
        	boundary++;
        }
        return lines;
    }
	public static int countLines(String filename) throws IOException {
	    InputStream reader = new BufferedInputStream(new FileInputStream(filename));
	    try{
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = reader.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') ++count;           
	            }}
	        return (count == 0 && !empty) ? 1 : count;
	    }finally{
	    	reader.close();}
	}	
	public static int diffValues(String[] Array){
	    int numOfDifferentVals = 0;
	    ArrayList<String> diffNum = new ArrayList<>();
	    for (int i = 0; i < Array.length; i++){
	        if(!diffNum.contains(Array[i]))
	            diffNum.add(Array[i]);
	    }
	    numOfDifferentVals = diffNum.size() == 1 ? 0 : diffNum.size();
	    return numOfDifferentVals;
	}
	public void format_conversion(int iIndex, int ProIndex, boolean isGWAS) throws IOException{
		iPatProject.Format format = iPro[ProIndex].format;
		iPatProject.Method method = isGWAS ? iPro[ProIndex].method_gwas : iPro[ProIndex].method_gs;
		String[] command = isGWAS ? iPro[ProIndex].command_gwas : iPro[ProIndex].command_gs;
    	int grIndex = iOB[iIndex_popup].getGroupIndex();
    	int GDindex = getIndexofType(grIndex, iPatObject.Filetype.GD),
    	    GMindex = getIndexofType(grIndex, iPatObject.Filetype.GM),
    	    BIMindex = getIndexofType(grIndex, iPatObject.Filetype.BIM),
	    	FAMindex = getIndexofType(grIndex, iPatObject.Filetype.FAM);
		String filename;
		switch(method){
		case GAPIT: case FarmCPU:
			switch(format){
			case Hapmap:
				new iPat_converter("hmp", "num", command[10], command[11]); 
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.dat";
				command[11] = filename + "_recode.nmap";
				iOB[GDindex].setPath(command[10]);
				create_linkedOB(iIndex, command[11], iPatObject.Filetype.GM);
				break;
			case VCF:
				new iPat_converter("vcf", "num", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.dat";
				command[11] = filename + "_recode.nmap";
				iOB[GDindex].setPath(command[10]);
				create_linkedOB(iIndex, command[11], iPatObject.Filetype.GM);
				break;
			case PLINK:
				new iPat_converter("plink", "num", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.dat";
				command[11] = filename + "_recode.nmap";
				iOB[GDindex].setPath(command[10]);
				iOB[GMindex].setPath(command[11]);
				break;
			case PLINK_bin:
				Process p = null;
				try {
					p = Runtime.getRuntime().exec(new String[]{iPatPanel.jar.getParent()+"/res/plink",
							  "--bed", command[10],
							  "--fam", command[15],
							  "--bim", command[16], 
							  "--recode", "tab", 
							  "--out", command[10].replaceFirst("[.][^.]+$", "")});
					p.waitFor();
				} catch (InterruptedException |IOException e2) {
					e2.printStackTrace();} 
				filename = command[10].replaceFirst("[.][^.]+$", "");
				new iPat_converter("plink", "num", 	filename + ".ped", 
													filename + ".map");
				command[10] = filename + "_recode.dat";
				command[11] = filename + "_recode.nmap";
				break;}
			iPro[ProIndex].format = iPatProject.Format.Numerical; break;
		case PLINK:
			switch(format){
			case Hapmap:
				new iPat_converter("hmp", "plink", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.ped";
				command[11] = filename + "_recode.map";
				iOB[GDindex].setPath(command[10]);
				create_linkedOB(iIndex, command[11], iPatObject.Filetype.GM);
				command[18] = "FALSE";
				break;
			case VCF:
				new iPat_converter("vcf", "plink", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.ped";
				command[11] = filename + "_recode.map";
				iOB[GDindex].setPath(command[10]);
				create_linkedOB(iIndex, command[11], iPatObject.Filetype.GM);
				command[18] = "FALSE";
				break;
			case Numerical:
				new iPat_converter("num", "plink", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.ped";
				command[11] = filename + "_recode.map";
				iOB[GDindex].setPath(command[10]);
				iOB[GMindex].setPath(command[11]);
				command[18] = "FALSE";
				break;
			case PLINK:
				command[18] = "FALSE";
				break;
			case PLINK_bin:
				Process p = null;
				try {
					p = Runtime.getRuntime().exec(new String[]{iPatPanel.jar.getParent()+"/res/plink",
							  "--bed", command[10],
							  "--fam", command[15],
							  "--bim", command[16], 
							  "--recode", "tab", 
							  "--out", command[10].replaceFirst("[.][^.]+$", "")});
					p.waitFor();
				} catch (InterruptedException |IOException e2) {
					e2.printStackTrace();} 
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + ".ped";
				command[11] = filename + ".map";
				iOB[GDindex].setPath(command[10]);
				iOB[BIMindex].setPath(command[11]);
				iOB[BIMindex].type = iPatObject.Filetype.GM;
				command[18] = "FALSE";
				break;}
			iPro[ProIndex].format = iPatProject.Format.PLINK; break;
		case gBLUP: case rrBLUP: case BGLR:
			switch(format){
			case Hapmap:
				new iPat_converter("hmp", "num", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.dat";
				command[11] = filename + "_recode.nmap";
				iOB[GDindex].setPath(command[10]);
				create_linkedOB(iIndex, command[11], iPatObject.Filetype.GM);
				break;
			case VCF:
				new iPat_converter("vcf", "num", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.dat";
				command[11] = filename + "_recode.nmap";
				iOB[GDindex].setPath(command[10]);
				create_linkedOB(iIndex, command[11], iPatObject.Filetype.GM);
				break;
			case PLINK:
				new iPat_converter("plink", "num", command[10], command[11]);
				filename = command[10].replaceFirst("[.][^.]+$", "");
				command[10] = filename + "_recode.dat";
				command[11] = filename + "_recode.nmap";
				iOB[GDindex].setPath(command[10]);
				iOB[GMindex].setPath(command[11]);
				break;}
			iPro[ProIndex].format = iPatProject.Format.Numerical; break;
		}
	}	
	private void reAssign(int index, String[] command){
		int grIndex = iOB[index].getGroupIndex();
		// Catch primary files
		int index_p = iPatPanel.getIndexofType(grIndex, iPatObject.Filetype.P), 
			index_gd = iPatPanel.getIndexofType(grIndex, iPatObject.Filetype.GD),
			index_gm = iPatPanel.getIndexofType(grIndex, iPatObject.Filetype.GM),
			index_fam = iPatPanel.getIndexofType(grIndex, iPatObject.Filetype.FAM),
			index_bim = iPatPanel.getIndexofType(grIndex, iPatObject.Filetype.BIM),
			index_c = iPatPanel.getIndexofType(grIndex, iPatObject.Filetype.C),
			index_k = iPatPanel.getIndexofType(grIndex, iPatObject.Filetype.K);
			command[8]  = index_p   != -1 ? iOB[index_p].getPath() : "NA";
			command[10]	= index_gd  != -1 ? iOB[index_gd].getPath() : "NA";
			command[11]	= index_gm  != -1 ? iOB[index_gm].getPath() : "NA";
			command[12]	= index_c  != -1 ? iOB[index_c].getPath() : "NA";
			command[14]	= index_k  != -1 ? iOB[index_k].getPath() : "NA";
			command[15] = index_fam != -1 ? iOB[index_fam].getPath() : "NA";
			command[16] = index_bim != -1 ? iOB[index_bim].getPath() : "NA";
	}
	iPatProject.Format catch_files(int index) throws IOException{
		// need extension (without extension): Binary fam
		// type record which table is P(1), C(2) or K(3)
		int count = 0;
		int[] fIndex = new int[maxfile];
		String[][] lines = new String[maxfile][2], 
				   row1 = new String[maxfile][], 
				   row2 = new String[maxfile][];
		int[] row_count = new int[maxfile];
		int[] col_count = new int[maxfile];
		int BED = -1, BIM = -1, FAM = -1;
		iPatProject.Format format = iPatProject.Format.NA;
		// get file path from table
		for (int i : getOBinGroup(iOB[index].getGroupIndex())) {
			if (iOB[i].type == iPatObject.Filetype.C || iOB[i].type == iPatObject.Filetype.K) 
				continue;
			if (iOB[i].isTB() && count < maxfile) {
				System.out.println(iOB[i].getPath());
				lines[count] = read_lines(iOB[i].getPath(), 2);
				fIndex[count] = i;
//				// Print first two lines
//				System.out.println(lines[count][0]);
//				System.out.println(lines[count][1]);
				// Get first two lines information if not null
				if (lines[count][0] != null && lines[count][1] != null) {
					row1[count] = lines[count][0].replaceAll("\"", "").split("\t");
					if(row1[count].length <= 1) 
						row1[count] = lines[count][0].replaceAll("\"", "").split(" +");
					if(row1[count].length <= 1) 
						row1[count] = lines[count][0].replaceAll("\"", "").split(",");
					row2[count] = lines[count][1].replaceAll("\"", "").split("\t");
					if(row2[count].length <= 1) 
						row2[count] = lines[count][1].replaceAll("\"", "").split(" +");
					if(row2[count].length <= 1) 
						row2[count] = lines[count][1].replaceAll("\"", "").split(",");
					row_count[count] = countLines(iOB[i].getPath());
					col_count[count] = row2[count].length;
				}
				count++;
			}
		}
		// determine which format
		switch (count){
			case 2:	
				boolean[] PLINK_con  = {iOB[fIndex[0]].getPath().toUpperCase().endsWith("MAP") && iOB[fIndex[1]].getPath().toUpperCase().endsWith("PED") && col_count[0] == 4,
									    iOB[fIndex[1]].getPath().toUpperCase().endsWith("MAP") && iOB[fIndex[0]].getPath().toUpperCase().endsWith("PED") && col_count[1] == 4};
				boolean[] VCF_con = {col_count[0] - lines[0][1].split("/").length == 8 && lines[0][1].split("/").length > 1, 
									 col_count[1] - lines[1][1].split("/").length == 8 && lines[1][1].split("/").length > 1};
				boolean[] HMP_con = {col_count[0] - row_count[1] == 11 || col_count[0] - row_count[1] == 10,
									 col_count[1] - row_count[0] == 11 || col_count[1] - row_count[0] == 10};
				boolean[] NUM_con = {Arrays.asList(row2[0]).containsAll(Arrays.asList("0", "1", "2")) && diffValues(row2[0]) < 5,
									 Arrays.asList(row2[1]).containsAll(Arrays.asList("0", "1", "2")) && diffValues(row2[1]) < 5};
				boolean[] BSA_con = {col_count[0] == 5 && col_count[1] == 3 && row_count[0] == row_count[1],
									col_count[1] == 5 && col_count[0] == 3 && row_count[0] == row_count[1]};
				if (partial_true(PLINK_con)) {
					iOB[fIndex[0]].type = PLINK_con[0] ? iPatObject.Filetype.GM : iPatObject.Filetype.GD;
					iOB[fIndex[1]].type = PLINK_con[1] ? iPatObject.Filetype.GM : iPatObject.Filetype.GD;
					format = iPatProject.Format.PLINK;
				} else if (partial_true(VCF_con)) {
					iOB[fIndex[0]].type = VCF_con[0] ? iPatObject.Filetype.GD : iPatObject.Filetype.P;
					iOB[fIndex[1]].type = VCF_con[1] ? iPatObject.Filetype.GD : iPatObject.Filetype.P;
					format = iPatProject.Format.VCF;
				} else if (partial_true(HMP_con)) {
					iOB[fIndex[0]].type = HMP_con[0] ? iPatObject.Filetype.GD : iPatObject.Filetype.P;
					iOB[fIndex[1]].type = HMP_con[1] ? iPatObject.Filetype.GD : iPatObject.Filetype.P;
					format = iPatProject.Format.Hapmap;
				} else if (partial_true(NUM_con)) {
					iOB[fIndex[0]].type = NUM_con[0] ? iPatObject.Filetype.GD : iPatObject.Filetype.P;
					iOB[fIndex[1]].type = NUM_con[1] ? iPatObject.Filetype.GD : iPatObject.Filetype.P;
					format = iPatProject.Format.Numerical;
				} else if (partial_true(BSA_con)) {
					iOB[fIndex[0]].type = BSA_con[0] ? iPatObject.Filetype.GD : iPatObject.Filetype.GM;
					iOB[fIndex[1]].type = BSA_con[1] ? iPatObject.Filetype.GD : iPatObject.Filetype.GM;
					format = iPatProject.Format.BSA;					
				}
				break;
			case 3:	
				int P_index = -1;
				// Numerical
				for (int i = 0; i < 3; i++){
					int i2 = (i + 1)%3, i3 = (i + 2)%3;
//					System.out.println("i differvalue : " + diffValues(row2[i]));
//					System.out.println("i containall : " + Arrays.asList(row2[i]).containsAll(Arrays.asList("0", "1", "2")));
//					System.out.println("i  col count : " + col_count[i]);
//					System.out.println("i2 col count : " + col_count[i2]);
//					System.out.println("i3 col count : " + col_count[i3]);
//					System.out.println("i row count : " + row_count[i]);
//					System.out.println("i2 row count : " + row_count[i2]);
//					System.out.println("i3 row count : " + row_count[i3]);
					if(Arrays.asList(row2[i]).containsAll(Arrays.asList("0", "1", "2")) && diffValues(row2[i]) < 6){
						iOB[fIndex[i]].type = iPatObject.Filetype.GD;
						iOB[fIndex[i2]].type = (col_count[i2] == 3 && Math.abs(col_count[i] - row_count[i2]) <= 1) ? iPatObject.Filetype.GM : iPatObject.Filetype.P; // m or m+1 - m or m+1 = -1, 0 1
						iOB[fIndex[i3]].type = (col_count[i3] == 3 && Math.abs(col_count[i] - row_count[i3]) <= 1 && iOB[fIndex[i2]].type != iPatObject.Filetype.GM) ? iPatObject.Filetype.GM : iPatObject.Filetype.P; 
						if(Arrays.asList(getTypeinGroup(iOB[index].getGroupIndex())).containsAll(Arrays.asList(iPatObject.Filetype.GD, iPatObject.Filetype.GM, iPatObject.Filetype.P))){
							format = iPatProject.Format.Numerical; 
							break;}}}
				// PLINK
				if(format != iPatProject.Format.Numerical){
					int PED = -1, MAP = -1;
					for (int i = 0; i < 3; i++){
						if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("PED")){
							iOB[fIndex[i]].type = iPatObject.Filetype.GD; PED = i;}
						else if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("MAP") && col_count[i] == 4){
							iOB[fIndex[i]].type = iPatObject.Filetype.GM; MAP = i;}}
					if(PED != -1 && MAP != -1){
						int P = 3 - PED - MAP;
						iOB[fIndex[P]].type = iPatObject.Filetype.P;
						format = iPatProject.Format.PLINK;}}
				//Binary
				if(format != iPatProject.Format.Numerical && format != iPatProject.Format.PLINK){
					for (int i = 0; i < 3; i++){
						if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("BED")){
							iOB[fIndex[i]].type = iPatObject.Filetype.GD; BED = i;}
						else if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("BIM") && col_count[i] == 6){
							iOB[fIndex[i]].type = iPatObject.Filetype.BIM; BIM = i;}
						else if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("FAM") && col_count[i] == 6){
							iOB[fIndex[i]].type = iPatObject.Filetype.FAM; FAM = i;}}
					if(BED!=-1 && BIM!=-1 && FAM!=-1) format = iPatProject.Format.PLINK_bin;}
				break;
			case 4:
				//Binary
				for (int i = 0; i < 4; i++){
					if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("BED")){
						iOB[fIndex[i]].type = iPatObject.Filetype.GD; BED = i;}
					else if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("BIM") && col_count[i] == 6){
						iOB[fIndex[i]].type = iPatObject.Filetype.BIM; BIM = i;}
					else if(iOB[fIndex[i]].getPath().toUpperCase().endsWith("FAM") && col_count[i] == 6){
						iOB[fIndex[i]].type = iPatObject.Filetype.FAM; FAM = i;}}
				if(BED!=-1 && BIM!=-1 && FAM!=-1){
					int P = 6 - BED - BIM - FAM;
					if(Math.abs(row_count[FAM]-row_count[P]) < 2){
						iOB[fIndex[P]].type = iPatObject.Filetype.P; 
						format = iPatProject.Format.PLINK_bin;}}
				break;
		}
		System.out.println("It's format "+format);
		System.out.println( iOB[fIndex[0]].type + " " + iOB[fIndex[1]].type + " " + 
							iOB[fIndex[2]].type + " " + iOB[fIndex[3]].type);
		return format;		
	} 
}
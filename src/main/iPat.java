package main;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import net.miginfocom.swing.MigLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.filechooser.FileSystemView;
import java.util.List;

public class iPat {
	static int Wide=1200;
	static int Heigth=700;
	static int PHeight=190;
	static String folder_path = new String("path");
	static String OS_string;
	public static OS UserOS = new OS();
	public static iPatPanel ipat;
	public static void main(String[] args){  
		System.out.println("Welcome to iPat!");
		OS_string = System.getProperty("os.name");
		if(OS_string.toUpperCase().contains("WINDOWS")){
			UserOS.type = OS.TYPE.Windows;
		}else if(OS_string.toUpperCase().contains("MAC")){
			UserOS.type = OS.TYPE.Mac;
		}else{
			UserOS.type = OS.TYPE.Linux;
		}
		System.out.println("You're runngin iPat on "+ UserOS.type);// Mac OS X, Windows 10
		JFrame main = new JFrame();
		//Set to center
		main.setSize(Wide, Heigth);
		Dimension windowSize = main.getSize();
       	GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
       	Point centerPoint = local_env.getCenterPoint();
       	int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;    

		main.setResizable(false);
        main.setLocation(dx, dy);
		main.setLayout(new BorderLayout());
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();

		Wide= main.getWidth();
		Heigth= main.getHeight();
		ipat = new iPatPanel(Wide, Heigth, PHeight);

		ipat.setFocusable(true); // for keylistener
		ipat.requestFocusInWindow(); // for keylistener
		cPane.add(ipat);	

		main.setVisible(true);
		main.addComponentListener(new ComponentAdapter() {
			@Override
	        		public void componentResized(ComponentEvent evt) {
				System.out.println("componentResized");
	            		Component c = (Component)evt.getSource();
	            		System.out.println("H: "+c.getHeight()+" W: "+c.getWidth()); 
	        		}	
		});			 
	}
}

class iPatPanel extends JPanel implements MouseMotionListener, KeyListener{	
	//  private static class ipatButton extends JButton {
	private static final int TBMAX=40;
	static int MOMAX=10;
	private static final int COMAX=20;	
	static File jar;
	static int[] TBimageX= new int[TBMAX];
	static int[] TBimageY= new int[TBMAX];
	static 	int[] TBimageH= new int[TBMAX];
	static int[] TBimageW= new int[TBMAX];
	// 
	//1=combined or not(-1,1), 
	//2= coindex, 
	//3=subcombined or not (-1,1) 
	//4= coindex, 
	//5= if include model
	static int[][] TBco= new int[TBMAX][5];
	static Findex.FILE[] TBtype = new Findex.FILE[TBMAX]; 
	static int[] TBdelete= new int[TBMAX];
	boolean link_to_TB = false;
	int link_to_TB_index = -1;

	static int[] MOimageX= new int[MOMAX];
	static int[] MOimageY= new int[MOMAX];
	static int[] MOimageH= new int[MOMAX];
	static int[] MOimageW= new int[MOMAX];
	static int[][] MOco= new int[MOMAX][5];
	static int[] MOdelete= new int[MOMAX];
	boolean link_to_MO = false;
	int link_to_MO_index = -1;
	
	Boolean link=false;
	int link_case = -1;
	
	static int TBindex =0, TBindex_temp=0;
	static int MOindex =0, MOindex_temp=0;
	static int COindex =-1;
	
	static int TBcount =0;
	static int MOcount =0;
	static int COcount =0;
	
	static Rectangle[] TBBound= new Rectangle[TBMAX];
	static Rectangle[] MOBound= new Rectangle[MOMAX];
	static Rectangle[] COBound= new Rectangle[COMAX];
	
	int MOimageX_int=430;
	int MOimageY_int=200;
	
	int pos;
	
	static Image[] TB= new Image[TBMAX];
	static Image[] MO= new Image[MOMAX];
	Image[] Trash= new Image[10];
	Image[] White= new Image[10];
	static Image 	TBimage, TB_C, TB_K, TB_P,
			MOimage, Prefbar, MO_suc, MO_fal, 
			hint_object, hint_trash, hint_model, hint_table,
			iconIP;
	
	JLayeredPane startPanel;
	JPanel mainPanel;	
	JLayeredPane nullPanel;
	JPanel buttonPanel;
	 
	AlphaLabel iPat = new AlphaLabel();

	public static FileDialog chooser;
	static String[] TBfile= new String[TBMAX];
	int[] TBvalue= new int[TBMAX];
	static String[] MOfile= new String[MOMAX];

	static JLabel[] TBname= new JLabel[TBMAX];	
	static JLabel[] MOname= new JLabel[MOMAX];		

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
	int[] linex=new int[2],
		  liney=new int[2],
		  line_drag_x=new int[2],
		  line_drag_y=new int[2];
	
	static int[][] linkline= new int[500][4];
	int[] linedelete= new int[500];
	static int linklineindex=0;
	int lineselected=-1;
	int lineselected_temp=-1;
	int lineindex=-1;	
	boolean ableselect=false, linktolink=false;
    	Stroke dashed = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10,10}, 0);
    	Stroke solid = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10,0}, 0);
    	Stroke select = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10,10}, 0);
  
	//Color
	Color red = new Color(231,57,131, 100);
	Color lightred = new Color(231,57,131, 80);
	Color dlightred = new Color(255, 0, 0, 10);
	Color ovalcolor = new Color(0, 0, 0, 80);
	Color themecolor = new Color(54, 164, 239, 150);
	
	//windows size
	static int Wide, Heigth, panelHeigth;

	//Intro
	Timer Intro;
	Color[] black= new Color[13];
	Boolean in=false, insub=false;
	
	//Button
	JButton TBButton= new JButton();
	JButton MOButton= new JButton();
	Image addTB, addMO;
	Timer TBanimation, MOanimation;
	Boolean ifopenfile=false, ifproperty=false, ifproperty_tb=false, ifproperty_mo=false;
	int openindex=-1;
	int createX=-1, createY=-1;
	
	//Gapit
	String[] File_names;
	File[] File_paths = new File[30];
	//settingframe model_frame;
	Preferences pref = Preferences.userRoot().node("/iPat"); 
	
    //object select
	int TBindex_select = 0;
	int MOindex_select = 0;
    	int object_selected = -1;   //1 table, 2 model
	int select_intex= -1;
	
	//Gear rotate
	static double rotate = Math.toRadians(0);
	static int[] rotate_index = new int[MOMAX];
	static boolean[] permit = new boolean[MOMAX];
	static boolean running = false;	
	Timer gear_rotate;
	
	//periodically repaint (delcare after fill value in permit)
	Timer periodically_repaint;
	//hint_object
	AlphaLabel hint_drag_label = new AlphaLabel();
	Fade_timer hint_drag_timer;
	//hint_model
	static int link_model = 0;
	AlphaLabel hint_model_label = new AlphaLabel();
	Fade_timer hint_model_timer;
	//hint_trash
	AlphaLabel hint_trash_label = new AlphaLabel();
	Fade_timer hint_trash_timer;
	
	//multi-thread
	static BGThread[] multi_run = new BGThread[MOMAX]; 
	//Console panel
	static JScrollPane[] scroll_console = new JScrollPane[MOMAX];
	static JTextArea[] text_console = new JTextArea[MOMAX];
	static JFrame[] frame_console = new JFrame[MOMAX];
	// file format catch
	static int maxfile = 4;
	static Findex[] file_index = new Findex[maxfile];
	////
	public static class CustomOutputStream extends OutputStream {
	    private JTextArea textArea;	     
	    public CustomOutputStream(JTextArea textArea) {
	        this.textArea = textArea;
	    }	     
	    @Override
	    public void write(int b) throws IOException {
	        // redirects data to the text area
	        textArea.append(String.valueOf((char)b));
	        // scrolls the text area to the end of data
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
	}
	////
	public static enum FORMAT{
		NA, Hapmap, Numeric, VCF, PLink_ASCII, PLink_Binary
	}
	public static FORMAT format = FORMAT.NA;
	
	public iPatPanel(int Wideint, int Heigthint, int pH){
		this.Wide=Wideint;
		this.Heigth=Heigthint;
		this.panelHeigth=pH;
		delbboundx=Wide-50;
		delbboundy=Heigth-70;
	    	// Connect the label with a drag and drop listener
	    	new DropTarget(this, new DropTargetListener(){
	        		@Override
	        		public void drop(DropTargetDropEvent event) {
	            			// Accept copy drops
	            			event.acceptDrop(DnDConstants.ACTION_COPY);
	            			// Get the transfer which can provide the dropped item data
	            			Transferable transferable = event.getTransferable();
	            			// Get the data formats of the dropped item
	            			DataFlavor[] flavors = transferable.getTransferDataFlavors();
	            			// Loop through the flavors
	            			for (DataFlavor flavor : flavors) {
	            				try {
	                    				// If the drop items are files
	                    				if(flavor.equals(DataFlavor.javaFileListFlavor)){
	                        					List<File> files = (List<File>) transferable.getTransferData(flavor);
	                    					int count = 0;
	                        					for (File file : files) {
	                    						Point pointer = event.getLocation();
	                            					int x = (int)pointer.getX();
	                    						int y = (int)pointer.getY();
	                    						create_TB(x+count*30, y+count*15);
	                    						TBindex = TBcount;
	                    						TB_assign(file);
	                    						count++;
	                    						// Print out the file path
	                            					System.out.println("File path is '" + file.getPath() + "'.");
	                            					repaint();
	                        					}
	                    				}
	                			}catch (Exception e) {
	                    				e.printStackTrace();
	                			}
	           		 	}
	            			TBindex = 0;
	            			// Inform that the drop is complete
	            			event.dropComplete(true);     
	        		}
			@Override
			public void dragOver(DropTargetDragEvent event) {}
		          	@Override
		       	public void dropActionChanged(DropTargetDragEvent event) {}
		    	@Override
		    	public void dragEnter(DropTargetDragEvent dtde) {}
		    	@Override
		    	public void dragExit(DropTargetEvent dte) {}
	    	});

	        	try {
			jar = new File(iPat.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e1) {e1.printStackTrace();}
        
		try{
			iconIP = ImageIO.read(getClass().getResource("resources/iPat.png"));
			iPat.setIcon(new ImageIcon(iconIP));
			iPat.setAlpha((float)0.5);
		} catch (IOException ex){}
		try{
			for(int i=0; i<10; i++){
				Trash[i] = ImageIO.read(getClass().getResource("resources/trash"+i+".png"));
				White[i] = ImageIO.read(getClass().getResource("resources/white"+i+".png"));
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
			hint_object = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_object.png"));			
			hint_trash = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_trash.png"));	
			hint_model = ImageIO.read(this.getClass().getResourceAsStream("resources/hint_model.png"));	
			hint_table =  ImageIO.read(this.getClass().getResourceAsStream("resources/hint_link.png"));	
			hint_trash_label.setIcon(new ImageIcon(hint_trash));
			hint_drag_label.setIcon(new ImageIcon(hint_object));
			hint_model_label.setIcon(new ImageIcon(hint_model));
		} catch (IOException ex){}
	
		for (int i=1; i<14; i++){
			black[i-1]= new Color(228+2*i,228+2*i,228+2*i, 255);
		}
		this.setBackground(Color.white);

		////////////
		//LAYOUT.START
		////////////
		iPat.setOpaque(false);	

		trashl = new JLabel(new ImageIcon(Trash[0]));	
		startPanel = new JLayeredPane();
		nullPanel= new JLayeredPane();
		
		startPanel.setPreferredSize(new Dimension(Wide, Heigth));	
		startPanel.add(iPat, new Integer(4));
		startPanel.add(trashl, new Integer(3));
		startPanel.add(hint_drag_label, new Integer(2));		
		startPanel.add(hint_trash_label, new Integer(1));		
		startPanel.add(hint_model_label, new Integer(6));		

		hint_drag_label.setBounds(new Rectangle(0, 0, hint_object.getWidth(null), hint_object.getHeight(null)));
		hint_drag_label.setVisible(true);	
		hint_trash_label.setBounds(new Rectangle(0, 0, hint_trash.getWidth(null), hint_trash.getHeight(null)));
		hint_trash_label.setVisible(true);	
		hint_model_label.setBounds(new Rectangle(0, 0, hint_model.getWidth(null), hint_model.getHeight(null)));
		hint_model_label.setVisible(true);	
		trashl.setBounds(new Rectangle(-100,-100, trashW, trashH));
		trashl.setVisible(true);	
		
		hint_drag_label.setAlpha(0);
		hint_trash_label.setAlpha(0);
		hint_model_label.setAlpha(0);
		
		iPat.setBounds(new Rectangle(510, 10, iconIP.getWidth(this), iconIP.getHeight(this))); 
		
		//this.setLayout(new MigLayout("debug, fill","[grow]","[grow]"));
		this.setLayout(null);
		this.add(startPanel);
		startPanel.setBounds(0,0,Wide,Heigth);
		//this.add(nullPanel,"grow"); 
		startPanel.setOpaque(false);
				
		for (int i=1; i<=TBMAX-1; i++){
			TB[i] = TBimage;
			TBtype[i] = Findex.FILE.TB;
			TBfile[i]= "null";
			TBname[i]= new JLabel();
			startPanel.add(TBname[i]);
			TBimageH[i]=TB[i].getHeight(null);	
			TBimageW[i]=TB[i].getWidth(null);	
			Arrays.fill(TBco[i], -1);
		}	
		for (int i=1; i<=MOMAX-1; i++){
			MO[i] = MOimage;
			MOname[i]= new JLabel();		
			startPanel.add(MOname[i]);
			MOimageH[i]=MO[i].getHeight(null);	
			MOimageW[i]=MO[i].getWidth(null);
			Arrays.fill(MOco[i], -1);
			rotate_index[i]=0;
		}			
		Arrays.fill(permit, false);
		for(int i = 0; i<maxfile; i++){
			file_index[i] = new Findex();	
		}	
		////////////
		////////////
		//LAYOUT.END
		////////////
		////////////
		gear_rotate = new Timer(50, new ActionListener() {
			int i=0;
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		    	if(all_false(permit)){
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
		    	if(partial_true(permit) && !running){ // avoid running it again over again
		    		running = true;
		    		gear_rotate.start();
		    	}
		    }
		});	
		
		hint_drag_timer = new Fade_timer(7000, hint_drag_label, new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent ae) {
					repaint();
					if(!hint_drag_timer.isActived() && TBcount < 2 && MOcount <1){
						hint_drag_timer.setActived(true);
						hint_drag_timer.fade_in();
					}else if(hint_drag_timer.TimeToOut()){
						System.out.println("out");
						hint_drag_timer.fade_out();
						hint_drag_timer.stop();
					}				
		    }
		});
		hint_model_timer= new Fade_timer(7002, hint_model_label, new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent ae) {
					repaint();
					if(!hint_model_timer.isActived() && 
						!hint_drag_timer.isRunning() && MOcount>0 && check_model_linked()!=0){
						hint_model_timer.setActived(true);
						hint_model_timer.fade_in();
					}else if(hint_model_timer.TimeToOut()){
						hint_model_timer.fade_out();
						hint_model_timer.stop();
					}				
		    }
		});
		hint_trash_timer = new Fade_timer(7003, hint_trash_label, new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent ae) {
					repaint();
					if(!hint_trash_timer.isActived() && hint_drag_timer.isActived()  && hint_model_timer.isActived() &&
						!hint_model_timer.isRunning() && (TBcount + MOcount > 4)){
						hint_trash_timer.setActived(true);
						hint_trash_timer.fade_in();
					}else if(hint_trash_timer.TimeToOut()){
						hint_trash_timer.fade_out();
						hint_trash_timer.stop();
					}				
		    }
		});
		periodically_repaint.start();
		hint_drag_timer.start();
		hint_model_timer.start();
		hint_trash_timer.start();

		this.addMouseListener(new MouseAdapter(){		
			@Override
			public void mousePressed(MouseEvent ee){
				int move_x=ee.getX();
    			int move_y=ee.getY();
    			double x=ee.getX();
    			double y=ee.getY();
    			COindex=-1;
    			lineindex=-1;
    			TBindex= TBindex_temp;  // catcth the mouse_move result
    			MOindex= MOindex_temp;
    			if(TBindex_select != TBindex_temp){
        			TBindex_select = TBindex_temp;
    			}else{
    				TBindex_select = -1;
    			}
    			if(MOindex_select != MOindex_temp){
        			MOindex_select = MOindex_temp;
    			}else{
    				MOindex_select = -1;
    			}

    			//Debug section
    			if(TBindex!=0){
        			System.out.println("COgroup: "+TBco[TBindex][3]+" index= "+TBindex); 
        			System.out.println(TBfile[TBindex].matches("null"));
    			}else if(MOindex!=0){
    				System.out.println("COgourp: "+MOco[MOindex][3]+" index= "+MOindex);
    			}
    			//  			
    			if (ableselect){
        			lineindex= lineselected_temp;
        			System.out.println("lineindex:"+lineindex);	
    			} 
    			
    			String folder_path = null;
				if(TBindex!=0 & SwingUtilities.isRightMouseButton(ee)){
					int adx = 7, ady = 9;
					switch(TBtype[TBindex]){
						case TB:
							TBtype[TBindex] = Findex.FILE.C;
							TB[TBindex] = TB_C;
							TBimageH[TBindex] = TB[TBindex].getHeight(null);
							TBimageW[TBindex] = TB[TBindex].getWidth(null);
							TBimageX[TBindex]-=adx;
							TBimageY[TBindex]-=ady;
							break;
						case C:
							TBtype[TBindex] = Findex.FILE.K;
							TB[TBindex] = TB_K;
							TBimageH[TBindex] = TB[TBindex].getHeight(null);
							TBimageW[TBindex] = TB[TBindex].getWidth(null);
							break;
						case K:
							TBtype[TBindex] = Findex.FILE.TB;
							TB[TBindex] = TBimage;
							TBimageH[TBindex] = TB[TBindex].getHeight(null);
							TBimageW[TBindex] = TB[TBindex].getWidth(null);
							TBimageX[TBindex]+=adx;
							TBimageY[TBindex]+=ady;
							break;
					}
					repaint();
			   	}else if(MOindex!=0 & SwingUtilities.isRightMouseButton(ee)){ 
			   		Configuration model_frame;
			   		JFrame wrong_formate = null;
			   		int[] type = new int[2]; //0: C, 1: K, record which table
			   		Arrays.fill(type, 0);
			   		try {
						format = catch_files(type);
					} catch (IOException e1) {e1.printStackTrace();}	
					try {
						if(format != FORMAT.NA){
							MO[MOindex] = MOimage;
							System.out.println("C: "+TBfile[type[0]]+"; K: "+TBfile[type[1]]);
							model_frame = new Configuration(MOindex, format, file_index, 
															type[0], type[1]);
					       	GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
							Point centerPoint = local_env.getCenterPoint();
				       		int dx = centerPoint.x - 550 / 2;
				        	int dy = centerPoint.y - 350 / 2;  
					  		model_frame.setBounds(dx, dy, 550, 350);
					   		model_frame.setResizable(true);
					   		model_frame.setVisible(true);
						}else{
							JOptionPane.showMessageDialog(wrong_formate,
								    "No match format with either Hapmap, Numeric, VCF or PLINK. Please check demo files to correct the format and the extension name.",
								    "Incorrect format",
								    JOptionPane.ERROR_MESSAGE);
						}	
					} catch (IOException e) {
						e.printStackTrace();
					}
			   		
			   	}			
    			if(ableselect&&TBindex<=0&&MOindex<=0){
					if(lineselected!=lineselected_temp){lineselected=lineselected_temp;}else{lineselected=-1;}	
    			}else{
    				lineselected = -1;
    			}
    			repaint();
			}	
				
			@Override 
			public void mouseReleased(MouseEvent ee){				
				int x=ee.getX();
				int y=ee.getY();		
    			//To compute whether the objects should be created
				 								 //case 1
				if( (TBindex!=0|MOindex!=0) && 	 //ä¸”æ­£åœ¨é�¸æŸ�å€‹ç‰©ä»¶
					 link_case == 1 && !removeornot){		//sure to link something		
					System.out.println("unlink-unlink");
						//self sure
						if (TBindex!=0){
							TBco[TBindex][2] = TBco[TBindex][0];
							TBco[TBindex][3] = TBco[TBindex][1];
							linkline[linklineindex][0]=1; 			//draw line from a table	
							linkline[linklineindex][1]=TBindex;		//draw line from which table
							System.out.println("TB1");
						}else if(MOindex!=0){
							MOco[MOindex][2] = MOco[MOindex][0];
							MOco[MOindex][3] = MOco[MOindex][1];	
							MOco[MOindex][4] = 1;
							linkline[linklineindex][0]=2;		  	//draw line from a model	
							linkline[linklineindex][1]=MOindex;		//draw line from which model
							System.out.println("MO1");
						}
						
						//target sure
						for(int i=1; i<=TBcount; i++){
							if(TBco[i][1] == COcount && i != TBindex){
								TBco[i][2] = TBco[i][0];
								TBco[i][3] = TBco[i][1];
								if(MOindex!=0){TBco[i][4] = 1;}
								linkline[linklineindex][2]=1;
								linkline[linklineindex][3]=i;
								System.out.println("TB2");
								break;
							}
						}
						for(int i=1; i<=MOcount; i++){
							if(MOco[i][1] == COcount && i != MOindex){
								MOco[i][2] = MOco[i][0];
								MOco[i][3] = MOco[i][1];
								MOco[i][4] = 1;
								if(TBindex!=0){TBco[TBindex][4] = 1;}
								linkline[linklineindex][2]=2;
								linkline[linklineindex][3]=i;
								System.out.println("MO2");
								break;
							}
						}
						linklineindex++;
						COcount++;
						link_case = -1;						
														 //case 2
				}else if((TBindex!=0|MOindex!=0) && 	 //ä¸”æ­£åœ¨é�¸æŸ�å€‹ç‰©ä»¶
						  link_case == 2 && !removeornot){			
					System.out.println("unlink-link");
					//self sure
					if (TBindex!=0){
						TBco[TBindex][2] = TBco[TBindex][0];
						TBco[TBindex][3] = TBco[TBindex][1];
						linkline[linklineindex][0]=1; 			//draw line from a table	
						linkline[linklineindex][1]=TBindex;		//draw line from which table
						System.out.println("TB1");
					}else if(MOindex!=0){
						MOco[MOindex][2] = MOco[MOindex][0];
						MOco[MOindex][3] = MOco[MOindex][1];	
						MOco[MOindex][4] = 1;
						linkline[linklineindex][0]=2;		  	//draw line from a model
						linkline[linklineindex][1]=MOindex;		//draw line from which model
						System.out.println("MO1");
					}				
					//target sure
					if(link_to_TB_index != -1){
						linkline[linklineindex][2]=1;
						linkline[linklineindex][3]=link_to_TB_index;
						if(MOindex!=0){
							for(int i=1; i<=TBcount; i++){
								if(TBco[i][3] == MOco[MOindex][3]){
									TBco[i][4] = 1;
								}
							}
						}else if(TBindex!=0 && TBco[link_to_TB_index][4]==1){
							TBco[TBindex][4] =1;
						}
						link_to_TB_index = -1;
						System.out.println("TB2");
					}else if(link_to_MO_index != -1){
						linkline[linklineindex][2]=2;
						linkline[linklineindex][3]=link_to_MO_index;
						if(TBindex!=0){TBco[TBindex][4] = 1;}
						link_to_MO_index = -1;
						System.out.println("MO2");
					}				
					linklineindex++;
					link_case = -1;				
														 //case 3
				}else if((TBindex!=0|MOindex!=0) && 	 //ä¸”æ­£åœ¨é�¸æŸ�å€‹ç‰©ä»¶
						  link_case == 3 && !removeornot){			
					System.out.println("link-link");
					link_case = -1;
														 //case 4		
				}else if((TBindex!=0|MOindex!=0) && 	 //ä¸”æ­£åœ¨é�¸æŸ�å€‹ç‰©ä»¶
						  link_case == 4 && !removeornot){			
					System.out.println("link-unlink");
					//self sure
					if (TBindex!=0){
						linkline[linklineindex][0]=1; 			//draw line from a table	
						linkline[linklineindex][1]=TBindex;		//draw line from which table
						System.out.println("TB1");
					}else if(MOindex!=0){
						linkline[linklineindex][0]=2;		  	//draw line from a model
						linkline[linklineindex][1]=MOindex;		//draw line from which model
						System.out.println("MO1");
					}	
					//target sure
					if(link_to_TB_index != -1){
						TBco[link_to_TB_index][2] = 1;
						TBco[link_to_TB_index][3] = TBco[link_to_TB_index][1];
						if((TBindex != 0 && TBco[TBindex][4] == 1)||	//TBLinkgroup - TB
						   (MOindex != 0)){								//MOgroup - TB
							TBco[link_to_TB_index][4] = 1;
						}
						linkline[linklineindex][2] = 1;
						linkline[linklineindex][3] = link_to_TB_index;
						link_to_TB_index = -1;
						System.out.println("TB2");
					}else if(link_to_MO_index != -1){
						MOco[link_to_MO_index][2] = 1;
						MOco[link_to_MO_index][3] = MOco[link_to_MO_index][1];
						MOco[link_to_MO_index][4] = 1;
						if(TBindex!=1){
							for(int i=1; i<=TBcount; i++){
								if(TBco[i][3] == MOco[link_to_MO_index][3]){
									TBco[i][4] = 1;
								}
							}
						}
						linkline[linklineindex][2] = 2;
						linkline[linklineindex][3] = link_to_MO_index;
						link_to_MO_index = -1;
						System.out.println("MO2");
					}						
					linklineindex++;
					link_case = -1;
				}				
				
				if (removeornot){
					if ((TBindex!=0||MOindex!=0) && y>=delbboundy && x>=delbboundx){
						break_object();
					}else if(lineindex!=-1&&(y>=(delbboundy)&&x>=(delbboundx))){
						break_linkage();
					}
    				trashl.setBounds(new Rectangle(-1000, -50, Wide, 300));  				
					trashl.setVisible(true);
					removeornot=false;
	    		}
				line_drag_x[0]=0;
				line_drag_y[0]=0;
				line_drag_x[1]=0;
				line_drag_y[1]=0;
				
				linex[0]=0;
				linex[1]=0;
				liney[0]=0;
				liney[1]=0;	
				
				repaint();
				TBindex=0;
				MOindex=0;	
				COindex=-1;
				link=false;
				linktolink=false;
			}
		
			@Override
    		public void mouseClicked(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			createX=x;
    			createY=y;
    			if (evt.getClickCount() == 2 & SwingUtilities.isLeftMouseButton(evt)) {
    				int open_MO = 0;
    				boolean ifopenMO = false;
    				for (int i=1; i<=TBcount; i++){
    					if (TBBound[i].contains(x,y)){
    						openindex=i;
    						ifopenfile=true;
        					break;
    					}
    				}
    				for (int i=1; i<=MOcount; i++){
    					if (MOBound[i].contains(x,y)){
    						open_MO=i;
    						ifopenMO=true;
        					break;
    					}
    				}
    				if(ifopenfile){
    					ifopenfile=false;
    					TBopenfile(openindex);
    				}else if(ifopenMO){
    					ifopenMO=false;
    					MOopenfile(open_MO);
    			  /*}else if (x<(Wide/2)&&TBindex_temp==0){
    					create_TB(x,y);
    					repaint(); */
    				}else if(MOindex_temp==0){
    					create_MO(x,y);
    					repaint();
    				}
    			}
    		}
		});	
	addKeyListener(this);
	addMouseMotionListener(this);
	}	

	@Override
	protected void paintComponent(Graphics g) {	
		if(hint_model_timer.isActived()){
			hint_model_label.setBounds(new Rectangle(MOimageX[link_model]-85, MOimageY[link_model]-120, hint_model.getWidth(null), hint_model.getHeight(null)));
		}
    	
	    super.paintComponent(g);	
	    g.setColor(ovalcolor);
	     
	    if(lineindex!=-1){
			 Draw_Lines(g, line_drag_x[0], line_drag_y[0], line_drag_x[1], line_drag_y[1], dashed);	
	    }     
		Draw_Lines(g, linex[0], liney[0], linex[1], liney[1], dashed); //temp_link 
		DrawLinkedLine(g); //object_link
		 
    	for (int i=1; i<=TBcount; i++){
		     g.drawImage(TB[i],TBimageX[i],TBimageY[i], this); 
		     if(TBindex_select == i){
		    	Draw_Rects(g, TBimageX[i]-5, TBimageY[i]-3, TBimageW[i]+10, TBimageH[i]+6, select); //temp_link 
		     }
    	}
    	for (int i=1; i<=MOcount; i++){
    		 if(rotate_index[i] == 1){
    			 AffineTransform tx = AffineTransform.getRotateInstance(rotate, MO[i].getWidth(null)/2,MO[i].getHeight(null)/2);
        		 AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
       		  	 g.drawImage(op.filter((BufferedImage) MO[i], null),MOimageX[i],MOimageY[i], this); 	     
    		 }else{
    		     g.drawImage(MO[i],MOimageX[i],MOimageY[i], this); 
    		 }
		     if(MOindex_select == i){
			     Draw_Rects(g, MOimageX[i]-5, MOimageY[i]-3, MOimageW[i]+10, MOimageH[i]+6, select); //temp_link 
		     }
    	}  	
    }
	
	@Override
	 public void mouseDragged(MouseEvent e) {
		int imX = e.getX();
		int imY = e.getY();
		int boundN=0; //205
		int boundS=Heigth-20;
		int boundE=Wide;
		
		if(TBindex!=0){
			TBindex_select = TBindex;
			CombinedorNot(TBindex, TBimageX, TBimageY, TBimageW, TBimageH, 1);
			KeepInPanel (imX, imY, TBindex, TBimageW, TBimageH, TBimageX, TBimageY,
						 boundN,  boundS,  boundE, TBBound);
		 	TBname[TBindex].setLocation(TBimageX[TBindex],TBimageY[TBindex]+TBimageH[TBindex]);
			TBBound[TBindex]=new Rectangle(TBimageX[TBindex], TBimageY[TBindex], TB[TBindex].getWidth(null), TB[TBindex].getHeight(null));
			repaint();
		}else if(MOindex!=0){
			MOindex_select = MOindex;
			CombinedorNot(MOindex, MOimageX, MOimageY, MOimageW, MOimageH, 2);
			KeepInPanel (imX, imY, MOindex, MOimageW, MOimageH, MOimageX, MOimageY,
						 boundN,  boundS,  boundE, MOBound);
			MOname[MOindex].setLocation(MOimageX[MOindex],MOimageY[MOindex]+MOimageH[MOindex]);
			MOBound[MOindex]=new Rectangle(MOimageX[MOindex], MOimageY[MOindex], MO[MOindex].getWidth(null), MO[MOindex].getHeight(null));
			repaint();
		}
		if((TBindex==0&&MOindex==0)&&lineindex!=-1){ //prevent from creating line when draging objects
			line_drag_x[0]=imX-30+10;
			line_drag_y[0]=imY-30;
			line_drag_x[1]=imX+30+10;
			line_drag_y[1]=imY+30;
			repaint();
		}
		
		if ((TBindex!=0|MOindex!=0|lineindex!=-1|COindex!=-1)&&(imY>=(delbboundy)&&imX>=(delbboundx))&&!removeornot){
			TrashAnimation = new Timer(15, new ActionListener() {
				int i=0;
			    @Override
			    public void actionPerformed(ActionEvent ae) {
			    	if(i<10&TA){
			    		trashl.setBounds(new Rectangle(Wide-trashW,  Heigth-trashH-10, trashW, trashH));
			    		//trashl.setBounds(new Rectangle(200,200,100,100));
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
			TA=true;
			TrashAnimation.start();
			removeornot=true;		
		}else if(imY<(delbboundy)||imX<(delbboundx)){
			trashl.setBounds(new Rectangle(Wide, -50, Wide, 300));
			startPanel.setLayer(trashl, new Integer(1));
			trashl.setVisible(true);
			removeornot=false;				
		}		
	}
	
	@Override
	public void mouseMoved(MouseEvent ev) {
		int y= ev.getY();
		int x= ev.getX();
		if(y<(delbboundy)||x<(delbboundx)){
			trashl.setBounds(new Rectangle(Wide, -50, Wide, 300));
			startPanel.setLayer(trashl, new Integer(1));
			trashl.setVisible(true);
			removeornot=false;				
		}
		lineindex=-1;
		lineselected_temp=-1;
		ableselect=false;
		
		if (TBindex<=0&MOindex<=0){
			for (int i=0; i<linklineindex; i++){ // if(distance(A, C) + distance(B, C) == distance(A, B)) , to determine if pointer is on the line.
				if(linedelete[i]==-1){
					continue;
				}
				TBindex_temp=0;
				MOindex_temp=0;				
				if(linkline[i][0]==1){
					if(linkline[i][2]==1){	
						if(Whether_On_Line(	TBimageX[linkline[i][1]]+(TBimageW[linkline[i][1]]/2), TBimageY[linkline[i][1]]+(TBimageH[linkline[i][1]]/2),
											TBimageX[linkline[i][3]]+(TBimageW[linkline[i][3]]/2), TBimageY[linkline[i][3]]+(TBimageH[linkline[i][3]]/2),
											x,y)){
							lineselected_temp=i;
							ableselect=true;
							break;
						}
					}else if(linkline[i][2]==2){
						if(Whether_On_Line(	TBimageX[linkline[i][1]]+(TBimageW[linkline[i][1]]/2), TBimageY[linkline[i][1]]+(TBimageH[linkline[i][1]]/2),
											MOimageX[linkline[i][3]]+(MOimageW[linkline[i][3]]/2), MOimageY[linkline[i][3]]+(MOimageH[linkline[i][3]]/2),
											x,y)){
							lineselected_temp=i;
							ableselect=true;
							break;
						}
					}
				}else if(linkline[i][0]==2){
					if(linkline[i][2]==1){
						if(Whether_On_Line(	MOimageX[linkline[i][1]]+(MOimageW[linkline[i][1]]/2), MOimageY[linkline[i][1]]+(MOimageH[linkline[i][1]]/2),
											TBimageX[linkline[i][3]]+(TBimageW[linkline[i][3]]/2), TBimageY[linkline[i][3]]+(TBimageH[linkline[i][3]]/2),
											x,y)){
							lineselected_temp=i;
							ableselect=true;
							break;
						}
					}else if(linkline[i][2]==2){
						if(Whether_On_Line(	MOimageX[linkline[i][1]]+(MOimageW[linkline[i][1]]/2), MOimageY[linkline[i][1]]+(MOimageH[linkline[i][1]]/2),
											MOimageX[linkline[i][3]]+(MOimageW[linkline[i][3]]/2), MOimageY[linkline[i][3]]+(MOimageH[linkline[i][3]]/2),
											x,y)){
							lineselected_temp=i;
							ableselect=true;		   
							break;				
						}
					}
				}
			}			
		}	
		
		if(ableselect){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}else{
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		if(TBcount>0){
			for (int i=1; i<=TBcount; i++){
				if (TBBound[i].contains(x, y)){
					this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					TBindex_temp=i;
					break;
				}else if(!ableselect){	//not select on object and not select a line
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					TBindex_temp=0;
				}
			}
		}
		if(MOcount>0&&TBindex_temp==0){
			for (int i=1; i<=MOcount; i++){
				if(MOBound[i].contains(x,y)){
					this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					MOindex_temp=i;
					break;
				}else if(!ableselect){
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					MOindex_temp=0;
				}
			}
		}
	}
	
	public void TBopenfile(int i){
		File openfile= new File(TBfile[i]);
			try{
				Desktop.getDesktop().open(openfile);
			} catch(IOException e) {
				e.printStackTrace();
			}
	}	
	
	public void MOopenfile(int i){
		File openfile= new File(MOfile[i]);
			try{
				Desktop.getDesktop().open(openfile);
			} catch(IOException e) {
				e.printStackTrace();
			}
	}	
	
	/*
	public static void iconchange(int i){
	  	if 		(TBfile[i].indexOf(".csv")>=0){TB[i]=Excel;}
	  	else if	(TBfile[i].indexOf(".xlm")>=0){TB[i]=Excel;}
	  	else if	(TBfile[i].indexOf(".xls")>=0){TB[i]=Excel;}
	  	else if	(TBfile[i].indexOf(".xlt")>=0){TB[i]=Excel;}
	  	
	  	else if	(TBfile[i].indexOf(".ppt")>=0){TB[i]=Powerpoint;}
	  	else if	(TBfile[i].indexOf(".ppa")>=0){TB[i]=Powerpoint;}
	  	else if	(TBfile[i].indexOf(".pps")>=0){TB[i]=Powerpoint;}
	  	else if	(TBfile[i].indexOf(".pot")>=0){TB[i]=Powerpoint;}
	  	else if	(TBfile[i].indexOf(".sldx")>=0){TB[i]=Powerpoint;}
	  	
	  	else if	(TBfile[i].indexOf(".doc")>=0){TB[i]=Word;}
	  	else if	(TBfile[i].indexOf(".dot")>=0){TB[i]=Word;}
	  	  	
	  	else if	(TBfile[i].indexOf(".m4a")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".m4b")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".m4p")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".mmf")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".mp3")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".mpc")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".msv")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".wav")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".wma")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".aiff")>=0){TB[i]=Music;}
	  	else if	(TBfile[i].indexOf(".3gp")>=0){TB[i]=Music;}
	  	
	  	else if	(TBfile[i].indexOf(".avi")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".asf")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".mov")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".avchd")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".flv")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".swf")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".gif")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".mpg")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".mp4")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".wmv")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".H.264")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".m4v")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".mkv")>=0){TB[i]=Video;}
	  	else if	(TBfile[i].indexOf(".rm")>=0){TB[i]=Video;}
	  	
		else if	(TBfile[i].indexOf(".txt")>=0){TB[i]=Text;}
		else if	(TBfile[i].indexOf(".r")>=0){TB[i]=Text;}
		else if	(TBfile[i].indexOf(".java")>=0){TB[i]=Text;}
		else if	(TBfile[i].indexOf(".log")>=0){TB[i]=Text;}
	  	
	  	else{TB[i]=Unknown;}	  	
	}*/

	
	void break_linkage(){
		int[][] traceback = new int[linklineindex][3]; //class, class_index, which link
		int[][] traceback_temp = new int[linklineindex][3]; //class, class_index, which link
		int trace_index = 0;
		// linkline[][2]   class, class index 
		// traceback[][3]  class, class index, which link  				
		for (int i=0; i<linklineindex; i++){
			traceback[i][0] = -1;
			traceback[i][1] = -1;
			traceback[i][2] = -1;
			traceback_temp[i][0] = -1;
			traceback_temp[i][1] = -1;
			traceback_temp[i][2] = -1;
		}
		traceback[0][0] = linkline[lineindex][2];
		traceback[0][1] = linkline[lineindex][3];
		traceback[0][2] = lineindex;
		//modified the first one
		if(linkline[lineindex][2] == 1){
			TBco[linkline[lineindex][3]][1] = COcount;
			TBco[linkline[lineindex][3]][3] = COcount;
		}else if(linkline[lineindex][2] == 2){
			MOco[linkline[lineindex][3]][1] = COcount;
			MOco[linkline[lineindex][3]][3] = COcount;							
		}	
		for(int i=0; i<linklineindex; i++){
			System.out.println(linkline[i][0]+" "+linkline[i][1]+")--("+linkline[i][2]+" "+linkline[i][3]);
		}
		// should be able to do recursive function
		for (int ex=0; ex<10; ex++){
			trace_index = 0;
			for (int t=0; t<linklineindex; t++){  //search index
				if(traceback[t][0] == -1){break;} //no more layer need to be searched
				for (int i=0; i<linklineindex; i++){
					if(i == traceback[t][2]){continue;} //skip the last round pair
					if(linkline[i][0] == traceback[t][0] && linkline[i][1] == traceback[t][1]){
						if(linkline[i][2]==1){ //table
							TBco[linkline[i][3]][1] = COcount;
							TBco[linkline[i][3]][3] = COcount;
							traceback_temp[trace_index][0] = linkline[i][2];
							traceback_temp[trace_index][1] = linkline[i][3];
							traceback_temp[trace_index][2] = i;
							trace_index++;
						}else if(linkline[i][2]==2){  //model
							MOco[linkline[i][3]][1] = COcount;
							MOco[linkline[i][3]][3] = COcount;
							traceback_temp[trace_index][0] = linkline[i][2];
							traceback_temp[trace_index][1] = linkline[i][3];
							traceback_temp[trace_index][2] = i;
							trace_index++;
						}
					}else if(linkline[i][2] == traceback[t][0] && linkline[i][3] == traceback[t][1]){
						if(linkline[i][0]==1){ //table
							TBco[linkline[i][1]][1] = COcount;
							TBco[linkline[i][1]][3] = COcount;
							traceback_temp[trace_index][0] = linkline[i][0];
							traceback_temp[trace_index][1] = linkline[i][1];
							traceback_temp[trace_index][2] = i;
							trace_index++;
						}else if(linkline[i][0]==2){  //model
							MOco[linkline[i][1]][1] = COcount;
							MOco[linkline[i][1]][3] = COcount;
							traceback_temp[trace_index][0] = linkline[i][0];
							traceback_temp[trace_index][1] = linkline[i][1];
							traceback_temp[trace_index][2] = i;
							trace_index++;
						}
					}
				}
			}
			for (int repo = 0; repo<linklineindex; repo++){
				traceback[repo][0] = traceback_temp[repo][0];
				traceback[repo][1] = traceback_temp[repo][1];
				traceback[repo][2] = traceback_temp[repo][2];
			}
			traceback[trace_index][0] =-1;
		}
		//////
		check_alone_and_model();
		COcount ++;
		linedelete[lineindex]=-1;
		linkline[lineindex][0] = -1;
		linkline[lineindex][1] = -1;
		linkline[lineindex][2] = -1;
		linkline[lineindex][3] = -1;
		repaint();
	}
	
	void mark_break_iteration(int[]traceback, int brench){
		int[] traceback_temp = new int[3];
		traceback_temp[0] = -1;
		traceback_temp[1] = -1;
		traceback_temp[2] = -1;		
		for (int i = 0; i<linklineindex; i++){
			if(i == traceback[2]){continue;} //skip the last round pair
			if(linkline[i][0] == traceback[0] && linkline[i][1] == traceback[1]){
				if(linkline[i][2]==1){ //table
					TBco[linkline[i][3]][1] = COcount+brench;
					TBco[linkline[i][3]][3] = COcount+brench;
					traceback_temp[0] = linkline[i][2];
					traceback_temp[1] = linkline[i][3];
					traceback_temp[2] = i;
					mark_break_iteration(traceback_temp, brench);
				}else if(linkline[i][2]==2){  //model
					MOco[linkline[i][3]][1] = COcount+brench;
					MOco[linkline[i][3]][3] = COcount+brench;
					traceback_temp[0] = linkline[i][2];
					traceback_temp[1] = linkline[i][3];
					traceback_temp[2] = i;
					mark_break_iteration(traceback_temp, brench);
				}
			}else if(linkline[i][2] == traceback[0] && linkline[i][3] == traceback[1]){
				if(linkline[i][0]==1){ //table
					TBco[linkline[i][1]][1] = COcount+brench;
					TBco[linkline[i][1]][3] = COcount+brench;
					traceback_temp[0] = linkline[i][0];
					traceback_temp[1] = linkline[i][1];
					traceback_temp[2] = i;
					mark_break_iteration(traceback_temp, brench);
				}else if(linkline[i][0]==2){  //model
					MOco[linkline[i][1]][1] = COcount+brench;
					MOco[linkline[i][1]][3] = COcount+brench;
					traceback_temp[0] = linkline[i][0];
					traceback_temp[1] = linkline[i][1];
					traceback_temp[2] = i;
					mark_break_iteration(traceback_temp, brench);
				}
			}	
		}
	}
	void check_alone_and_model(){
		int catch_t, catch_m, count;
		System.out.println("cocount="+COcount);
		for (int i = 0; i<= COcount; i++){
			catch_t = 0; catch_m = 0; count = 0;
			for (int t = 1; t<TBMAX; t++){
				if(TBco[t][3] == i){
					count ++;
					catch_t = t;
				}
			}
			for (int m = 1; m<MOMAX; m++){
				if(MOco[m][3] == i){
					count ++;
					catch_m = m;
				}
			}
			System.out.println("count: "+count+" catch_t= "+catch_t+" catch_m= "+catch_m+" group= "+ i);
			if(count == 1){
				if(catch_t != 0){
					TBco[catch_t][2] = -1;
					TBco[catch_t][3] = -1;
					TBco[catch_t][4] = -1;
				}else if(catch_m != 0){
					MOco[catch_m][2] = -1;
					MOco[catch_m][3] = -1;
					MOco[catch_m][4] = -1;
				}
			}
			if(catch_m == 0){ // indecate "group_ori" doesn't have model
				for(int e = 1; e<TBMAX; e++){
					if(TBco[e][3]== i){TBco[e][4] = -1;}
				}
			}
		}
	}
	
	void break_object(){
		int[] traceback = new int[3];
		int trace_index = 0, trace_max = 1, group_ori= 0;
		// linkline[][2]   class, class index 
		// traceback[][3]  class, class index, which link  				
		traceback[0] = -1;
		traceback[1] = -1;
		traceback[2] = -1;	
		int brench = 0;
		for (int i=0; i<linklineindex; i++){
			if(TBindex!= 0){
				if(linkline[i][0]==1 && linkline[i][1]==TBindex){
					traceback[0] = linkline[i][2];
					traceback[1] = linkline[i][3];
					traceback[2] = i;
					if(linkline[i][2]==1){
						TBco[linkline[i][3]][1] = COcount+brench;
						TBco[linkline[i][3]][3] = COcount+brench;
					}else if(linkline[i][2]==2){
						MOco[linkline[i][3]][1] = COcount+brench;
						MOco[linkline[i][3]][3] = COcount+brench;
					}		
					mark_break_iteration(traceback, brench);
					brench++;
					System.out.println(traceback[0]+"-"+traceback[1]+";"+ COcount+brench+", case1");
				}else if(linkline[i][2]==1 && linkline[i][3]==TBindex){
					traceback[0] = linkline[i][0];
					traceback[1] = linkline[i][1];
					traceback[2] = i;
					if(linkline[i][0]==1){
						TBco[linkline[i][1]][1] = COcount+brench;
						TBco[linkline[i][1]][3] = COcount+brench;
					}else if(linkline[i][0]==2){
						MOco[linkline[i][1]][1] = COcount+brench;
						MOco[linkline[i][1]][3] = COcount+brench;
					}
					mark_break_iteration(traceback, brench);
					brench++;
					System.out.println(traceback[0]+"-"+traceback[1]+";"+ COcount+brench+", case2");
				}
			}else if(MOindex!= 0){
				if(linkline[i][0]==2 && linkline[i][1]==MOindex){
					traceback[0] = linkline[i][2];
					traceback[1] = linkline[i][3];
					traceback[2] = i;
					if(linkline[i][2]==1){
						TBco[linkline[i][3]][1] = COcount+brench;
						TBco[linkline[i][3]][3] = COcount+brench;
					}else if(linkline[i][2]==2){
						MOco[linkline[i][3]][1] = COcount+brench;
						MOco[linkline[i][3]][3] = COcount+brench;
					}
					mark_break_iteration(traceback, brench);
					brench++;
					System.out.println(traceback[0]+"-"+traceback[1]+";"+ COcount+brench+", case3");
				}else if(linkline[i][2]==2 && linkline[i][3]==MOindex){
					traceback[0] = linkline[i][0];
					traceback[1] = linkline[i][1];
					traceback[2] = i;
					if(linkline[i][0]==1){
						TBco[linkline[i][1]][1] = COcount+brench;
						TBco[linkline[i][1]][3] = COcount+brench;
					}else if(linkline[i][0]==2){
						MOco[linkline[i][1]][1] = COcount+brench;
						MOco[linkline[i][1]][3] = COcount+brench;
					}
					mark_break_iteration(traceback, brench);
					brench++;
					System.out.println(traceback[0]+"-"+traceback[1]+";"+ COcount+brench+", case4");
				}		
			}
		}
		COcount += brench+1;
		check_alone_and_model();
		if(TBindex!=0){
			TBimageX[TBindex]=-1000;
			TBimageY[TBindex]=-1000;
			TBBound[TBindex]=new Rectangle(-100,-100,0,0);
			TBname[TBindex].setLocation(-100,-100);
			TBdelete[TBindex]=-1;
		}else if(MOindex!=0){
			MOimageX[MOindex]=-1000;
			MOimageY[MOindex]=-1000;
			MOBound[MOindex]=new Rectangle(-100,-100,0,0);
			MOname[MOindex].setLocation(-100,-100);
			MOdelete[MOindex]=-1;
		}
		repaint();
	}	
	
	public void CombinedorNot(int index, int[] Xs, int[] Ys, int[] Ws, int[] Hs, int TorM_s){
		int[] TBdist= new int[TBcount+1];
		int[] MOdist= new int[MOcount+1];
		int minvalue= min_dist(index, Xs, Ys, Ws, Hs, TBdist, MOdist);
		//System.out.println("----------log start-------------");
		//System.out.println("minvalue="+minvalue);
		int case3_count = 0;
		if(TBindex!=0){
			//System.out.println("check model");
			combine_type_determined(MOdist, minvalue, case3_count,
									Xs, Ys, Ws, Hs, index, TorM_s, 
									MOimageX, MOimageY, MOimageW, MOimageH, MOcount, MOco, 2);
		}
		//System.out.println("check table");
		combine_type_determined(TBdist, minvalue, case3_count,
								Xs, Ys, Ws, Hs, index, TorM_s, 
								TBimageX, TBimageY, TBimageW, TBimageH, TBcount, TBco, 1);						
		//System.out.println("----------log end-------------");
	}		
	
	public int min_dist(int index, int[] X, int[] Y, int[] W, int[] H, int[] TBdist, int[] MOdist){
		int x= X[index]+(W[index]/2);
		int y= Y[index]+(H[index]/2);	
		for (int i=1; i<=TBcount; i++){
			if (i == TBindex) {
	            TBdist[i]= 10000;
	        }else{
	        	int x2= TBimageX[i]+(TBimageW[i]/2);
				int y2= TBimageY[i]+(TBimageH[i]/2);
				int dist= (int)Distance(x, y, x2, y2);
				TBdist[i]= dist;	
	        }			
		}
		for (int i=1; i<=MOcount; i++){
			if (i == MOindex) {
				MOdist[i]= 10000;   
	        }else{
	        	int x2= MOimageX[i]+(MOimageW[i]/2);
				int y2= MOimageY[i]+(MOimageH[i]/2);
				int dist= (int)Distance(x, y, x2, y2);
				MOdist[i]= dist;
	        }	
		}
		int[] newdist = new int[TBdist.length + MOdist.length-2];
		System.arraycopy(TBdist, 1, newdist, 0 		   		, TBdist.length-1);
		System.arraycopy(MOdist, 1, newdist, TBdist.length-1, MOdist.length-1);
		return MinValue(newdist);
	}	
	
	public static void create_TB(int x, int y){
		TBcount++;
		TBimageX[TBcount]=x-TBimageW[TBcount]/2;
		TBimageY[TBcount]=y-TBimageH[TBcount]-5;
		TBBound[TBcount]= new Rectangle(TBimageX[TBcount], TBimageY[TBcount], TBimage.getWidth(null), TBimage.getHeight(null));
	}
	public static void create_MO(int x, int y){
		MOcount++;
		MOimageX[MOcount]=x-MOimageW[MOcount]/2;
		MOimageY[MOcount]=y-MOimageH[MOcount]-5;
		MOBound[MOcount]= new Rectangle(MOimageX[MOcount], MOimageY[MOcount], MOimage.getWidth(null), MOimage.getHeight(null));
		MOimageH[MOcount]=MO[MOcount].getHeight(null);
	  	MOimageW[MOcount]=MO[MOcount].getHeight(null);
		MOname[MOcount].setLocation(MOimageX[MOcount], MOimageY[MOcount]+MOimageH[MOcount]);
		MOname[MOcount].setSize(200,15);
		MOname[MOcount].setText("Project "+MOcount);
	}
	public static File iPatChooser(String title, boolean DirOnly){
		File selectedfile = null;
		if(!DirOnly){
			chooser = new FileDialog(new JFrame(), title, FileDialog.LOAD);   
			chooser.setVisible(true);   
		    if(chooser!=null){   
		    	selectedfile = chooser.getFiles()[0];
		    }
		}else{
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			chooser = new FileDialog(new JFrame(), title, FileDialog.LOAD);   
			chooser.setVisible(true);   
		    if(chooser!=null){   
		    	selectedfile = chooser.getFiles()[0];
		    }
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		}	
		return selectedfile;
	}
	
	public static void TB_assign(File selectedfile){
		   TBfile[TBindex]= selectedfile.getAbsolutePath();
		   TBimageH[TBindex]=TB[TBindex].getHeight(null);
		   TBimageW[TBindex]=TB[TBindex].getWidth(null);
		   TBname[TBindex].setLocation(TBimageX[TBindex], TBimageY[TBindex]+TBimageH[TBindex]);
		   TBname[TBindex].setSize(200,15);
		   TBname[TBindex].setText(selectedfile.getName());
		   TBindex=0;
		   COindex=-1;
	}
	
	// target_specify
	public void combine_type_determined(int[] dist, int minvalue, int case3_count,
										int[] Xs, int[] Ys, int[] Ws, int[] Hs, int index, int TorM_s,
										int[] Xt, int[] Yt, int[] Wt, int[] Ht, int count_t, int[][] co_t, int TorM_t){
		for (int i= 1; i<=count_t; i++){
			//System.out.println(" i="+i+" count= "+count_t);
			if(TorM_s == TorM_t && index == i){ 
				//System.out.println("  return");
				continue;} //skip target to itself and model-link object
			if(dist[i]==minvalue && minvalue < 100){		//1. target object, <100
				if(((TorM_s==1 && TBco[index][2]==-1) || (TorM_s==2 && MOco[index][2]==-1)) &&  //1-1. unlink-unlink
				   (co_t[i][2]==-1)){
					//self
					if(TorM_s==1){	
						TBco[index][0]=1; 	
						TBco[index][1]=COcount;
					}else if (TorM_s==2){
						MOco[index][0]=1;
						MOco[index][1]=COcount;
					}	
				  	//target
					co_t[i][0]=1;
					co_t[i][1]=COcount;				
					
					//link 
					linex[0]= Xs[index]+(Ws[index]/2);
					liney[0]= Ys[index]+(Hs[index]/2);
					linex[1]= Xt[i]+(Wt[i]/2);
					liney[1]= Yt[i]+(Ht[i]/2);
					link_case = 1;
					//System.out.println("  case 1-1");
				}else if(((TorM_s==1 && TBco[index][2]==-1) || (TorM_s==2 && MOco[index][2]==-1)) &&  //1-2. unlink-link
				   (co_t[i][2]!=-1)){
					if(co_t[i][4]==1 && TorM_s==2){continue;} //skip model-link group
					//self 
					if(TorM_s==1){	
						TBco[index][0]=1; 	
						TBco[index][1]=co_t[i][3]; // self group still need to be change, not only the link
					}else if (TorM_s==2){
						MOco[index][0]=1;
						MOco[index][1]=co_t[i][3];
					}	
					//target
					if(TorM_t==1){
						link_to_TB_index = i;
					}else if (TorM_t==2){
						link_to_MO_index = i;
					}
					//link
					linex[0]= Xs[index]+(Ws[index]/2);
					liney[0]= Ys[index]+(Hs[index]/2);
					linex[1]= Xt[i]+(Wt[i]/2);
					liney[1]= Yt[i]+(Ht[i]/2);
					link_case = 2;
					//System.out.println("  case 1-2");
				}else if(((TorM_s==1 && TBco[index][2]!=-1) || (TorM_s==2 && MOco[index][2]!=-1)) &&  //1-3. link-link
				   (co_t[i][2]!=-1)){
					linex[0]=0;
					linex[1]=0;
					liney[0]=0;
					liney[1]=0;
					link_case = 3;
					//System.out.println("  case 1-3");
				}else if(((TorM_s==1 && TBco[index][2]!=-1) || (TorM_s==2 && MOco[index][2]!=-1)) &&  //1-4. link-unlink
				   (co_t[i][2]==-1)){				
					if((TorM_s==1 && TBco[index][4] == 1 && TorM_t == 2) ||  //skip TBgroup - model
					   (TorM_s==2 && TorM_t == 2)){  	//skip MOgroup - model
						continue;
					}
					//target 
					if(TorM_t==1){
						link_to_TB_index = i;
					}else if (TorM_t==2){
						link_to_MO_index = i;
					}
					co_t[i][0]=1;
					if(TorM_s==1){
						co_t[i][1]=TBco[index][3];
					}else if(TorM_s==2){
						co_t[i][1]=MOco[index][3];
					}
					//link
					linex[0]= Xs[index]+(Ws[index]/2);
					liney[0]= Ys[index]+(Hs[index]/2);
					linex[1]= Xt[i]+(Wt[i]/2);
					liney[1]= Yt[i]+(Ht[i]/2);
					link_case = 4;
					//System.out.println("  case 1-4");
				}
			}else if(dist[i]==minvalue && minvalue >= 100){ //2. target object, >=100
				linex[0]=0;
				linex[1]=0;
				liney[0]=0;
				liney[1]=0;
				link_case = -1;
				//System.out.println("  case 2");
			}else{ 											//3. other remain (not closest)
				if(TorM_t==1 && link_to_TB_index == i){
					link_to_TB_index = -1;
				}else if (TorM_t==2 && link_to_MO_index == i){
					link_to_MO_index = -1;
				}
				co_t[i][0]=-1;
				co_t[i][1]=-1;
				//System.out.println("  case 3");
				++case3_count;
				if(TBindex!=0 && case3_count == MOcount + TBcount -1){  // to catch all case 3, make line disappear
					linex[0]=0;
					linex[1]=0;
					liney[0]=0;
					liney[1]=0;
					link_case = -1;
					//System.out.println("*** ALL case 3 ***");
				}else if (MOindex!=0 && case3_count == TBcount){
					linex[0]=0;
					linex[1]=0;
					liney[0]=0;
					liney[1]=0;
					link_case = -1;
					//System.out.println("*** ALL case 3 ***");
				}
			}
		}
	}	
	
	public void dragcombined (int dx, int dy, int index,
			  				  int[] X, int[] Y, int[] W, int[] H, Image[] image, Rectangle[] bound, JLabel[] name){						
		X[index]=X[index]+dx;
		Y[index]=Y[index]+dy;
		bound[index]= new Rectangle(X[index], Y[index], 
									image[index].getWidth(null), image[index].getHeight(null));
		name[index].setLocation(X[index], Y[index]+H[index]);
	}
				
	public void KeepInPanel (int x, int y, int index, int[] imageW, int[] imageH, int[] imageX, int[] imageY,
							 int boundN, int boundS, int boundE, Rectangle[] Bound){			
		if(x<(0+(imageW[index]/2))){
			imageX[index]=0;
			if(y<(boundN+(imageH[index]/2))){
			 	imageY[index]=boundN;
			}else if(y>(boundS-(imageH[index]/2))){
				imageY[index]=boundS-imageH[index];
			}else{
				imageY[index]=y-(imageH[index]/2);
			}
			
		}else if(y<(boundN+(imageH[index]/2))){
		 	imageY[index]=boundN;
		 	if(x<(0+imageW[index]/2)){
				imageX[index]=0;
		 	}else if(x>(boundE-imageW[index]/2)){
		 		imageX[index]=boundE-imageW[index];
		 	}else{
		 		imageX[index]=x-(imageW[index]/2);
		 	}
		 	
		}else if(x>(boundE-(imageW[index]/2))){
		 	imageX[index]=boundE-imageW[index];
		 	if(y<(boundN+(imageH[index]/2))){
			 	imageY[index]=boundN;
			}else if(y>(boundS-(imageH[index]/2))){
				imageY[index]=boundS-imageH[index];
			}else{
				imageY[index]=y-(imageH[index]/2);
			}
		 	
		}else if(y>(boundS-(imageH[index]/2))){
		 	imageY[index]=boundS-imageH[index];
		 	if(x<(0+imageW[index]/2)){
				imageX[index]=0;
		 	}else if(x>(boundE-imageW[index]/2)){
		 		imageX[index]=boundE-imageW[index];
		 	}else{
		 		imageX[index]=x-(imageW[index]/2);
		 	}	
		 	
		 }else{
			imageX[index]=x-(imageW[index]/2);
		 	imageY[index]=y-(imageH[index]/2);
		}
	}
	
	public static int MinValue(int[] array){  
	     int minValue = array[0];  
	     for(int i=1; i<array.length; i++){  
	    	 if(array[i] < minValue){  
	    		 minValue = array[i];  
	    	 }	  
	     }  
	     return minValue;  
	}  
		
	
	public static double Distance(double x1, double y1, double x2, double y2){
		double dist=0;
		dist= Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
		dist= Math.round( (dist * 100.0 ) / 100.0);
		return dist;
	}
	
	public static boolean Whether_On_Line(double x1, double y1, double x2, double y2, double x, double y){
		boolean online;
		online= ( Distance(x1, y1, x, y)+Distance(x2, y2, x, y)<Distance(x1, y1, x2, y2)+2);
		return online;
	}

	public void Draw_Lines(Graphics g, int x1, int y1, int x2, int y2, Stroke s){	
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();
        //set the stroke of the copy, not the original 
        g2d.setStroke(s);
        g2d.drawLine(x1, y1, x2, y2);
        //gets rid of the copy
        g2d.dispose();        
	}
	
	public void Draw_Rects(Graphics g, int x1, int y1, int x2, int y2, Stroke s){	
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();
        //set the stroke of the copy, not the original 
        g2d.setStroke(s);
        g2d.drawRect(x1, y1, x2, y2);
        //gets rid of the copy
        g2d.dispose();        
	}
	
	public void DrawLinkedLine(Graphics g){
		Stroke temp_stroke;
		for (int i=0; i<linklineindex; i++){
			if(linedelete[i]==-1){
				continue;
			}
			if(i==lineselected){
				temp_stroke=solid;
			}else{
				temp_stroke=dashed;
			}
			if(linkline[i][0]==1){
				if(linkline[i][2]==1){
					if(TBdelete[linkline[i][1]]!=-1 & TBdelete[linkline[i][3]]!=-1 ){		//not drawing deleted objects
						Draw_Lines(g, TBimageX[linkline[i][1]]+(TBimageW[linkline[i][1]]/2), TBimageY[linkline[i][1]]+(TBimageH[linkline[i][1]]/2),
								  TBimageX[linkline[i][3]]+(TBimageW[linkline[i][3]]/2), TBimageY[linkline[i][3]]+(TBimageH[linkline[i][3]]/2),
								  temp_stroke);
					}		
				}else if(linkline[i][2]==2){
					if(TBdelete[linkline[i][1]]!=-1 & MOdelete[linkline[i][3]]!=-1 ){	
						Draw_Lines(g, TBimageX[linkline[i][1]]+(TBimageW[linkline[i][1]]/2), TBimageY[linkline[i][1]]+(TBimageH[linkline[i][1]]/2),
								  	  MOimageX[linkline[i][3]]+(MOimageW[linkline[i][3]]/2), MOimageY[linkline[i][3]]+(MOimageH[linkline[i][3]]/2),
								  	  temp_stroke);
					}
				}
			}else if(linkline[i][0]==2){
				if(linkline[i][2]==1){
					if(MOdelete[linkline[i][1]]!=-1 & TBdelete[linkline[i][3]]!=-1 ){
						Draw_Lines(g, MOimageX[linkline[i][1]]+(MOimageW[linkline[i][1]]/2), MOimageY[linkline[i][1]]+(MOimageH[linkline[i][1]]/2),
								  	  TBimageX[linkline[i][3]]+(TBimageW[linkline[i][3]]/2), TBimageY[linkline[i][3]]+(TBimageH[linkline[i][3]]/2),
								  	  temp_stroke);
					}			
				}else if(linkline[i][2]==2){
					if(MOdelete[linkline[i][1]]!=-1 & MOdelete[linkline[i][3]]!=-1 ){
						Draw_Lines(g, MOimageX[linkline[i][1]]+(MOimageW[linkline[i][1]]/2), MOimageY[linkline[i][1]]+(MOimageH[linkline[i][1]]/2),
									  MOimageX[linkline[i][3]]+(MOimageW[linkline[i][3]]/2), MOimageY[linkline[i][3]]+(MOimageH[linkline[i][3]]/2),
									  temp_stroke);
					}	
				}
			}
		}
	}
	
	public int check_model_linked(){
		int catch_TB = 0, catch_MO = 0;
		for (int i=1; i<=TBcount; i++){
			if(!TBfile[i].matches("null") && TBco[i][4] == 1){
				catch_TB = i;
				break;
			}
		}
		if(catch_TB!=0){
			for (int i=1; i<=MOcount; i++){
				if(MOco[i][3] == TBco[catch_TB][3]){
					catch_MO = i;
					break;
				}
			}
			iPatPanel.link_model = catch_MO;
			return catch_MO;
		}
		return 0;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode(); 
		System.out.println("key input: "+key);
		if(key==8){
			System.out.println("lineselect= "+lineselected);
			if(lineselected!=-1){
				break_linkage();
			}else if(TBindex_select != 0){
				TBindex = TBindex_select;
				break_object();
				TBindex = 0;	
			}else if(MOindex_select != 0){
				MOindex = MOindex_select;
				break_object();
				MOindex =0;
			}else{
				gear_rotate.start();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}	

	public static boolean all_true(boolean[] array){
	    for(boolean b : array) if(!b) return false;
	    return true;
	}
	public static boolean all_false(boolean[] array){
	    for(boolean b : array) if(b) return false;
	    return true;
	}
	public static boolean partial_true(boolean[] array){
	    for(boolean b : array) if(b) return true;
	    return false;
	}
	
	public static void read_binary(String file){
		//Binary
		/*File input_file = new File (file);
		DataInputStream data_in;
		long [] array_of_ints = new long [1000000];
	    int index = 0;
		try {
			data_in = new DataInputStream(new BufferedInputStream( new FileInputStream(input_file)));
			while(true){
				try{
					long a = data_in.readLong();
					index++;
					System.out.println(a);
				}catch(EOFException eof) {
					System.out.println ("End of File");
					break;
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	public static int diffValues(String[] Array){
	    int numOfDifferentVals = 0;
	    ArrayList<String> diffNum = new ArrayList<>();

	    for(int i=0; i<Array.length; i++){
	        if(!diffNum.contains(Array[i])){
	            diffNum.add(Array[i]);
	        }
	    }
	    numOfDifferentVals = diffNum.size()==1 ? 0:diffNum.size();
	    return numOfDifferentVals;
	}
	FORMAT catch_files(int[] type) throws IOException{
		// need extension (without extension): Binary fam
		// type record which table is P(1), C(2) or K(3)
		int count = 0;
		String[][] lines = new String[maxfile][], 
				   row1 = new String[maxfile][], 
				   row2 = new String[maxfile][];
		int[] row_count = new int[maxfile];
		int[] col_count = new int[maxfile];
		FORMAT format = FORMAT.NA;
		//Get file path from table
		for (int i = 1; i <= TBcount; i++){
			if(TBco[i][3] == MOco[MOindex][3] && TBco[i][3] != -1){ //catch file in the same group
				switch(TBtype[i]){
					case C: type[0] = i; break;
					case K: type[1] = i; break;
					default:
						if(count < maxfile){
							System.out.println(TBfile[i]);
							file_index[count].tb = i;
							lines[count] = read_lines(TBfile[i], 3); 
							System.out.println(lines[count][0]);
							System.out.println(lines[count][1]);
							if(lines[count][0]!=null && lines[count][1]!=null){
								row1[count] = lines[count][0].split("\t");
								if(row1[count].length<=1){row1[count] = lines[count][0].split(" ");}
								row2[count] = lines[count][1].split("\t");	
								if(row2[count].length<=1){row2[count] = lines[count][0].split(" ");}
								row_count[count] = countLines(TBfile[i]);
								col_count[count] = row2[count].length;
							}	
							count++;	
						}
						break;
		}}}				
		switch (count){
			case 2:	
				boolean[] VCF_con = {col_count[0] - lines[0][1].split("/").length == 8 && lines[0][1].split("/").length > 1, 
									 col_count[1] - lines[1][1].split("/").length == 8 && lines[1][1].split("/").length > 1};
				boolean[] HMAP_con = {col_count[0] - row_count[1] == 11 || col_count[0] - row_count[1] == 10,
									  col_count[1] - row_count[0] == 11 || col_count[1] - row_count[0] == 10};
				boolean[] NUM_con = {Arrays.asList(row2[0]).containsAll(Arrays.asList("0", "1", "2")) && diffValues(row2[0]) < 5,
									 Arrays.asList(row2[1]).containsAll(Arrays.asList("0", "1", "2")) && diffValues(row2[1]) < 5};
				System.out.println(col_count[0]);
				System.out.println(col_count[1]);
				System.out.println(row_count[0]);
				System.out.println(row_count[1]);
				if(partial_true(VCF_con)){
					file_index[0].file = VCF_con[0]?Findex.FILE.VCF:Findex.FILE.P;
					file_index[1].file = VCF_con[1]?Findex.FILE.VCF:Findex.FILE.P;
					format = FORMAT.VCF;
				}else if(partial_true(HMAP_con)){
					file_index[0].file = HMAP_con[0]?Findex.FILE.G:Findex.FILE.P;
					file_index[1].file = HMAP_con[1]?Findex.FILE.G:Findex.FILE.P;
					format = FORMAT.Hapmap;
				}else if(partial_true(NUM_con)){
					file_index[0].file = NUM_con[0]?Findex.FILE.GD:Findex.FILE.P;
					file_index[1].file = NUM_con[1]?Findex.FILE.GD:Findex.FILE.P;
					format = FORMAT.Numeric;	
				}
				break;
			case 3:	
				int P_index = -1;
				for(int i = 0; i<3; i++){
					int i2 = (i+1)%3, i3 = (i+2)%3;
					if(TBfile[file_index[i].tb].toUpperCase().endsWith("MAP") && col_count[i] == 4){
						file_index[i].file = Findex.FILE.MAP;
						file_index[i2].file = TBfile[file_index[i2].tb].toUpperCase().endsWith("PED") ? Findex.FILE.PED :Findex.FILE.P;
						file_index[i3].file = (TBfile[file_index[i3].tb].toUpperCase().endsWith("PED") &&  file_index[i2].file != Findex.FILE.PED) ? Findex.FILE.PED : Findex.FILE.P;
						if(Arrays.asList(file_index[0].file, file_index[1].file, file_index[2].file).containsAll(Arrays.asList(Findex.FILE.PED, Findex.FILE.MAP, Findex.FILE.P))){
							format = FORMAT.PLink_ASCII; break;
						}
					}else if(Arrays.asList(row2[i]).containsAll(Arrays.asList("0", "1", "2")) && diffValues(row2[i]) < 5){
						file_index[i].file = Findex.FILE.GD;
						file_index[i2].file = (col_count[i2] == 3 && Math.abs(col_count[i] - row_count[i2]) <= 1) ? Findex.FILE.GM : Findex.FILE.P; // m or m+1 - m or m+1 = -1, 0 1
						file_index[i3].file = (col_count[i3] == 3 && Math.abs(col_count[i] - row_count[i3]) <= 1 && file_index[i2].file != Findex.FILE.GM) ? Findex.FILE.GM : Findex.FILE.P;
						if(Arrays.asList(file_index[0].file, file_index[1].file, file_index[2].file).containsAll(Arrays.asList(Findex.FILE.GD, Findex.FILE.GM, Findex.FILE.P))){
							format = FORMAT.Numeric; break;
						}
					}
				}
				break;
			case 4:
				//Binary
				int BED = -1, BIM = -1, FAM = -1;
				for (int i = 0; i<4; i++){
					if(TBfile[file_index[i].tb].toUpperCase().endsWith("BED")){
						file_index[i].file = Findex.FILE.BED; BED = i;
					}else if(TBfile[file_index[i].tb].toUpperCase().endsWith("BIM") && col_count[i] == 6){
						file_index[i].file = Findex.FILE.BIM; BIM = i; 
					}else if(TBfile[file_index[i].tb].toUpperCase().endsWith("FAM") && col_count[i] == 6){
						file_index[i].file = Findex.FILE.FAM; FAM = i;
					}
				}
				if(BED!=-1 && BIM!=-1 && FAM!=-1){
					int P = 6 - BED - BIM - FAM;
					if(Math.abs(row_count[FAM]-row_count[P]) < 2){file_index[P].file = Findex.FILE.P; format = FORMAT.PLink_Binary;}						
				}
				break;
		}
		System.out.println("It's format "+format);
		System.out.println(file_index[0].file+" "+file_index[1].file+" "+file_index[2].file+" "+file_index[3].file);
		return format;		
	} 
	
}

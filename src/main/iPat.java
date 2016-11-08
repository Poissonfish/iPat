package main;
import org.rosuda.JRI.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import net.miginfocom.swing.MigLayout;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

public class iPat {
	static int Wide=1200;
	static int Heigth=700;
	static int PHeight=190;
	static String folder_path = new String("path");

	public static void main(String[] args){    	
		JFrame main = new JFrame();
		main.setLocation(200, 0); 
		main.setSize(Wide, Heigth);
		main.setLayout(new BorderLayout());
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();
		Wide= main.getWidth();
		Heigth= main.getHeight();
		myPanel ipat = new myPanel(Wide, Heigth, PHeight);
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

class myPanel extends JPanel implements MouseMotionListener{	
	//  private static class ipatButton extends JButton {
	private static final int TBMAX=20;
	private static final int MOMAX=20;
	private static final int COMAX=10;	
	
	static int[] TBimageX= new int[TBMAX];
	static int[] TBimageY= new int[TBMAX];
	static 	int[] TBimageH= new int[TBMAX];
	static int[] TBimageW= new int[TBMAX];
	static int[][] TBco= new int[TBMAX][5];  //1=combined or not(-1,1), 2= coindex, 3=subcombined or not (-1,1) 4= coindex, 5= if include model
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
	int COindex =-1;
	
	static int TBcount =0;
	static int MOcount =0;
	static int COcount =0;
	
	static Rectangle[] TBBound= new Rectangle[TBMAX];
	static Rectangle[] MOBound= new Rectangle[MOMAX];
	static Rectangle[] COBound= new Rectangle[COMAX];
	
	int MOimageX_int=430;
	int MOimageY_int=200;
	
	int pos;
	
	Image[] TB= new Image[TBMAX];
	Image[] MO= new Image[MOMAX];
	Image[] Trash= new Image[10];
	Image[] White= new Image[10];
	Image Excel, Powerpoint, Word, Music, Video, Unknown, Text, 
		  TBimage, MOimage, Prefbar;
	
	JLayeredPane startPanel;
	JPanel mainPanel;	
	JLayeredPane nullPanel;
	JPanel buttonPanel;
	 
	JLabel iPat = new JLabel();

	JFileChooser[] TBchooser= new JFileChooser[TBMAX];
	static String[] TBfile= new String[TBMAX];
	int[] TBvalue= new int[TBMAX];
	
	static JLabel[] TBname= new JLabel[TBMAX];	
	static JLabel[] MOname= new JLabel[MOMAX];		

	//animation
	Timer TrashAnimation, DashLineAnimation, CombinedDeleteAnimation, 
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
    Stroke dashed = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10,10}, 0);
    Stroke solid = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10,0}, 0);
    	
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
	JFileChooser folder_chooser = new JFileChooser(); 
	iPat_chooser chooser; 
	
	Rengine r = new Rengine(new String[]{"--no-save"}, true, new TextConsole());
	//settingframe model_frame;
	Preferences pref = Preferences.userRoot().node("/iPat"); 
	
	public myPanel(int Wideint, int Heigthint, int pH){	
		this.Wide=Wideint;
		this.Heigth=Heigthint;
		this.panelHeigth=pH;
		delbboundx=Wide-50;
		delbboundy=Heigth-70;
		
		try{
			Image iconIP = ImageIO.read(getClass().getResource("resources/iPat.png"));
			iPat.setIcon(new ImageIcon(iconIP));
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
			Excel = ImageIO.read(this.getClass().getResourceAsStream("resources/Excel.png"));
			Powerpoint = ImageIO.read(this.getClass().getResourceAsStream("resources/Powerpoint.png"));
			Word = ImageIO.read(this.getClass().getResourceAsStream("resources/Word.png"));
			Video = ImageIO.read(this.getClass().getResourceAsStream("resources/Video.png"));
			Music = ImageIO.read(this.getClass().getResourceAsStream("resources/Music.png"));
			Text = ImageIO.read(this.getClass().getResourceAsStream("resources/Text.png"));
			Unknown = ImageIO.read(this.getClass().getResourceAsStream("resources/Unknown.png"));
		} catch (IOException ex){}
		try{
			TBimage = ImageIO.read(this.getClass().getResourceAsStream("resources/Table.png"));
			addTB = ImageIO.read(this.getClass().getResourceAsStream("resources/add_Table.png"));
			MOimage = ImageIO.read(this.getClass().getResourceAsStream("resources/Model.png"));
			addMO = ImageIO.read(this.getClass().getResourceAsStream("resources/add_Model.png"));
			TBButton.setIcon(new ImageIcon(addTB));
			MOButton.setIcon(new ImageIcon(addMO));
		} catch (IOException ex){}
		
		for (int i=1; i<=TBMAX-1; i++){
			TB[i] = TBimage;
		}
		for (int i=1; i<=MOMAX-1; i++){
			MO[i] = MOimage;
		}	
		
		for (int i=1; i<14; i++){
			black[i-1]= new Color(228+2*i,228+2*i,228+2*i, 255);
		}
		this.setBackground(Color.white);
		
		////////////
		//LAYOUT.START
		////////////
		//this ---startPanel(layered)------trashl*
		//      |					 |	
		//		|					 --layoutPanel-----iPat*
		//		|							 		|
		//		|							 		--TBButton*
		//		|									|
		//		|							 		--MOButton*
		//		|
		//		--nullPanel(layered)----prefbarl*
		//							  |
		//				              ---whitel*
		//		
		//		
		
		TBButton.setOpaque(false);
		TBButton.setContentAreaFilled(false);
		TBButton.setBorderPainted(false);
		MOButton.setOpaque(false);
		MOButton.setContentAreaFilled(false);
		MOButton.setBorderPainted(false);
		
		iPat.setOpaque(false);	

		trashl = new JLabel(new ImageIcon(Trash[0]));	
		startPanel = new JLayeredPane();
		nullPanel= new JLayeredPane();
		
		startPanel.setPreferredSize(new Dimension(Wide, panelHeigth));	
		startPanel.add(iPat, new Integer(4));
		nullPanel.add(trashl, new Integer(3));
		nullPanel.add(TBButton, new Integer(1));
		nullPanel.add(MOButton, new Integer(2));
		
		trashl.setBounds(new Rectangle(-100,-100, trashW, trashH));
		trashl.setVisible(true);	
		
		iPat.setBounds(new Rectangle(523, 10, 150, 80)); 
		TBButton.setBounds(new Rectangle(-10000, -10000, TBimage.getWidth(null), TBimage.getHeight(null)));
		MOButton.setBounds(new Rectangle(-10000, -10000, MOimage.getWidth(null), MOimage.getHeight(null)));
		nullPanel.setPreferredSize(new Dimension(Wide, Heigth-panelHeigth));
		
		this.setLayout(new MigLayout("fillx","[grow]","[grow]"));
		this.add(startPanel," dock north");
		this.add(nullPanel,"grow"); 
		
		startPanel.setOpaque(false);
		nullPanel.setOpaque(false);
				
		for (int i=1; i<=TBMAX-1; i++){
			TBchooser[i]= new JFileChooser();
			TBfile[i]= new String();
			TBname[i]= new JLabel();
			nullPanel.add(TBname[i]);
			TBimageH[i]=TB[i].getHeight(null);	
			TBimageW[i]=TB[i].getWidth(null);	
			TBco[i][0]=-1;
			TBco[i][1]=-1;
			TBco[i][2]=-1;
			TBco[i][3]=-1;
			TBco[i][4]=-1;
		}	
		for (int i=1; i<=MOMAX-1; i++){
			MOname[i]= new JLabel();		
			nullPanel.add(MOname[i]);
			MOimageH[i]=MO[i].getHeight(null);	
			MOimageW[i]=MO[i].getWidth(null);
			MOco[i][0]=-1;
			MOco[i][1]=-1;
			MOco[i][2]=-1;
			MOco[i][3]=-1;	
			MOco[i][4]=-1;
		}	
		////////////
		////////////
		//LAYOUT.END
		////////////
		////////////
		
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
    			
    			//Debug section
    			if(TBindex!=0){
        			System.out.println("COgroup: "+TBco[TBindex][3]+" index= "+TBindex);        				
    			}else if(MOindex!=0){
    				System.out.println("COgourp: "+MOco[MOindex][3]);
    			}
    			//  			
    			if (ableselect){
        			lineindex= lineselected_temp;
        			System.out.println("lineindex:"+lineindex);	
    			}
    			
    			String folder_path = null;
				if(TBindex!=0 & SwingUtilities.isRightMouseButton(ee)){
					 TBchooser[TBindex].setApproveButtonText("Link!");
					 TBvalue[TBindex]= TBchooser[TBindex].showOpenDialog(null);
					 if (TBvalue[TBindex] == JFileChooser.APPROVE_OPTION){
					    File selectedfile = TBchooser[TBindex].getSelectedFile();  	    					    
					  	TBfile[TBindex]= selectedfile.getAbsolutePath();
					  	iconchange(TBindex); 
					  	TBimageH[TBindex]=TB[TBindex].getHeight(null);
					  	TBimageW[TBindex]=TB[TBindex].getHeight(null);
						TBname[TBindex].setLocation(TBimageX[TBindex], TBimageY[TBindex]+TBimageH[TBindex]-panelHeigth+15);
						TBname[TBindex].setSize(200,15);
						TBname[TBindex].setText(selectedfile.getName());
						TBindex=0;
						COindex=-1;
						repaint();
					 }
			   	}else if(MOindex!=0 & SwingUtilities.isRightMouseButton(ee)){ 	
			   		chooser = new iPat_chooser();
			   		Configuration model_frame = new Configuration(chooser.getPath(), r, MOindex);
			  		model_frame.setLocation(450, 250);
			   		model_frame.setResizable(false);
			   		model_frame.initial();
			   		
			   	}else if(MOindex!=0 & SwingUtilities.isLeftMouseButton(ee)){
			   
			   	} 			
    			if(ableselect&&TBindex<=0&&MOindex<=0){
					if(lineselected!=lineselected_temp){lineselected=lineselected_temp;}else{lineselected=-1;}	
    			}		
    			repaint();
			}	
				
			@Override
			public void mouseReleased(MouseEvent ee){				
				int x=ee.getX();
				int y=ee.getY();		

    			//To compute whether the objects should be created
				if( (TBindex!=0|MOindex!=0) && 	 //且正在選某個物件
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
						
				}else if((TBindex!=0|MOindex!=0) && 	 //且正在選某個物件
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
					
				}else if((TBindex!=0|MOindex!=0) && 	 //且正在選某個物件
						  link_case == 3 && !removeornot){			
					System.out.println("link-link");
					link_case = -1;
							
				}else if((TBindex!=0|MOindex!=0) && 	 //且正在選某個物件
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
					if (TBindex!=0&&(y>=(delbboundy)&&x>=(delbboundx))){
						TBimageX[TBindex]=-1000;
						TBimageY[TBindex]=-1000;
						TBBound[TBindex]=new Rectangle(-100,-100,0,0);
						TBname[TBindex].setLocation(-100,-100);
						repaint();
						TBdelete[TBindex]=-1;
					}else if(MOindex!=0&&(y>=(delbboundy)&&x>=(delbboundx))){
						MOimageX[MOindex]=-1000;
						MOimageY[MOindex]=-1000;
						MOBound[MOindex]=new Rectangle(-100,-100,0,0);
						MOname[MOindex].setLocation(-100,-100);
						repaint();
						MOdelete[MOindex]=-1;
					}else if(lineindex!=-1&&(y>=(delbboundy)&&x>=(delbboundx))){
						int[][] traceback = new int[linklineindex][3]; //class, class_index, which link
						int[][] traceback_temp = new int[linklineindex][3]; //class, class_index, which link

						int trace_index = 0, trace_max = 1;		
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
							System.out.println(linkline[i][0]+" "+linkline[i][1]+"--"+linkline[i][2]+" "+linkline[i][3]);
						}
						for (int ex=0; ex<10; ex++){
							trace_index = 0;
							for (int t=0; t<linklineindex; t++){  //search index
								System.out.println("traceback: "+traceback[t][0]+", "+traceback[t][1]);
								if(traceback[t][0] == -1){
									System.out.println("break");
									break;
								} //no more layer need to be searched
								for (int i=0; i<linklineindex; i++){
									System.out.println(linkline[i][0]+" "+linkline[i][1]+"--"+linkline[i][2]+" "+linkline[i][3]);
									System.out.println("traceback(new):"+traceback[t][0]+", "+traceback[t][1]);
									if(i == traceback[t][2]){
										System.out.println("comtinue");
										continue;} //skip the last round pair
									if(linkline[i][0] == traceback[t][0] && linkline[i][1] == traceback[t][1]){
										if(linkline[i][2]==1){ //table
											TBco[linkline[i][3]][1] = COcount;
											TBco[linkline[i][3]][3] = COcount;
											traceback_temp[trace_index][0] = linkline[i][2];
											traceback_temp[trace_index][1] = linkline[i][3];
											traceback_temp[trace_index][2] = i;
											trace_index++;
											System.out.println("Layer:"+ex+" , trace: "+trace_index+ " ["+linkline[i][2]+", "+linkline[i][3]+"]");
										}else if(linkline[i][2]==2){  //model
											MOco[linkline[i][3]][1] = COcount;
											MOco[linkline[i][3]][3] = COcount;
											traceback_temp[trace_index][0] = linkline[i][2];
											traceback_temp[trace_index][1] = linkline[i][3];
											traceback_temp[trace_index][2] = i;
											trace_index++;
											System.out.println("Layer:"+ex+" , trace: "+trace_index+ " ["+linkline[i][2]+", "+linkline[i][3]+"]");
										}
									}else if(linkline[i][2] == traceback[t][0] && linkline[i][3] == traceback[t][1]){
										if(linkline[i][0]==1){ //table
											TBco[linkline[i][1]][1] = COcount;
											TBco[linkline[i][1]][3] = COcount;
											traceback_temp[trace_index][0] = linkline[i][0];
											traceback_temp[trace_index][1] = linkline[i][1];
											traceback_temp[trace_index][2] = i;
											trace_index++;
											System.out.println("Layer:"+ex+" , trace: "+trace_index+ " ["+linkline[i][0]+", "+linkline[i][1]+"]");
										}else if(linkline[i][0]==2){  //model
											MOco[linkline[i][1]][1] = COcount;
											MOco[linkline[i][1]][3] = COcount;
											traceback_temp[trace_index][0] = linkline[i][0];
											traceback_temp[trace_index][1] = linkline[i][1];
											traceback_temp[trace_index][2] = i;
											trace_index++;
											System.out.println("Layer:"+ex+" , trace: "+trace_index+ " ["+linkline[i][0]+", "+linkline[i][1]+"]");
										}
									}
								}
							}
							traceback = traceback_temp;
							traceback[trace_index][0] =-1;
						}
						COcount ++;
						linedelete[lineindex]=-1;
						repaint();
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
    				for (int i=1; i<=TBcount; i++){
    					if (TBBound[i].contains(x,y)){
    						ifproperty_tb=true;
    						openindex=i;
    						ifopenfile=true;
        					break;
    					}
    				}			
    				if(ifopenfile == true){
    					ifopenfile=false;
    					TBopenfile(openindex);
    				}else if ( x<(Wide/2) ){
    					TBcount++;
        				TBimageX[TBcount]=createX-TBimageW[TBcount]/2;
        				TBimageY[TBcount]=createY-TBimageH[TBcount]-5;
        				TBBound[TBcount]= new Rectangle(TBimageX[TBcount], TBimageY[TBcount], TBimage.getWidth(null), TBimage.getHeight(null));
        				repaint();
    				}else if( x>(Wide/2)){
    					MOcount++;
        				MOimageX[MOcount]=createX-MOimageW[MOcount]/2;
        				MOimageY[MOcount]=createY-MOimageH[MOcount]-5;
        				MOBound[MOcount]= new Rectangle(MOimageX[MOcount], MOimageY[MOcount], MOimage.getWidth(null), MOimage.getHeight(null));
        				repaint();
    				}
    			}
    		}
		});	
	
	addMouseMotionListener(this);
	}	
	
	@Override
	protected void paintComponent(Graphics g) {	
	     super.paintComponent(g);
	 
	     g.setColor(ovalcolor);

	     if(lineindex!=-1){
			 Draw_Lines(g, line_drag_x[0], line_drag_y[0], line_drag_x[1], line_drag_y[1], dashed);	
	     }     
		 Draw_Lines(g, linex[0], liney[0], linex[1], liney[1], dashed);	
		 DrawLinkedLine(g);
		 
    	 for (int i=1; i<=TBcount; i++){
		     g.drawImage(TB[i],TBimageX[i],TBimageY[i], this); 
    	 }
    	 for (int i=1; i<=MOcount; i++){
		     g.drawImage(MO[i],MOimageX[i],MOimageY[i], this); 
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
			CombinedorNot(TBindex, TBimageX, TBimageY, TBimageW, TBimageH, 1);
			KeepInPanel (imX, imY, TBindex, TBimageW, TBimageH, TBimageX, TBimageY,
						 boundN,  boundS,  boundE, TBBound);
		 	TBname[TBindex].setLocation(TBimageX[TBindex],TBimageY[TBindex]+TBimageH[TBindex]-panelHeigth+15);
			TBBound[TBindex]=new Rectangle(TBimageX[TBindex], TBimageY[TBindex], TB[TBindex].getWidth(null), TB[TBindex].getHeight(null));
			repaint();
		}else if(MOindex!=0){
			CombinedorNot(MOindex, MOimageX, MOimageY, MOimageW, MOimageH, 2);
			KeepInPanel (imX, imY, MOindex, MOimageW, MOimageH, MOimageX, MOimageY,
						 boundN,  boundS,  boundE, MOBound);
			MOname[MOindex].setLocation(MOimageX[MOindex],MOimageY[MOindex]+MOimageH[MOindex]-panelHeigth+15);
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
			    		trashl.setBounds(new Rectangle(Wide-trashW,  Heigth-panelHeigth-trashH-10, trashW, trashH));
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
						}else{
							ableselect=false;
						}
					}else if(linkline[i][2]==2){
						if(Whether_On_Line(	TBimageX[linkline[i][1]]+(TBimageW[linkline[i][1]]/2), TBimageY[linkline[i][1]]+(TBimageH[linkline[i][1]]/2),
											MOimageX[linkline[i][3]]+(MOimageW[linkline[i][3]]/2), MOimageY[linkline[i][3]]+(MOimageH[linkline[i][3]]/2),
											x,y)){
							lineselected_temp=i;
						   ableselect=true;
						   break;
						}else{
							ableselect=false;
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
						}else{
							ableselect=false;
						}
					}else if(linkline[i][2]==2){
						if(Whether_On_Line(	MOimageX[linkline[i][1]]+(MOimageW[linkline[i][1]]/2), MOimageY[linkline[i][1]]+(MOimageH[linkline[i][1]]/2),
											MOimageX[linkline[i][3]]+(MOimageW[linkline[i][3]]/2), MOimageY[linkline[i][3]]+(MOimageH[linkline[i][3]]/2),
											x,y)){
							lineselected_temp=i;
						   ableselect=true;		   
						   break;
						}else{
							ableselect=false;					
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
	
	public void iconchange(int i){
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
	}
	
	public void CombinedorNot(int index, int[] Xs, int[] Ys, int[] Ws, int[] Hs, int TorM_s){
		int[] TBdist= new int[TBcount+1];
		int[] MOdist= new int[MOcount+1];
		int minvalue= min_dist(index, Xs, Ys, Ws, Hs, TBdist, MOdist);
		System.out.println("----------log start-------------");
		System.out.println("minvalue="+minvalue);
		int case3_count = 0;
		if(TBindex!=0){
			System.out.println("check model");
			combine_type_determined(MOdist, minvalue, case3_count,
									Xs, Ys, Ws, Hs, index, TorM_s, 
									MOimageX, MOimageY, MOimageW, MOimageH, MOcount, MOco, 2);
		}
		System.out.println("check table");
		combine_type_determined(TBdist, minvalue, case3_count,
								Xs, Ys, Ws, Hs, index, TorM_s, 
								TBimageX, TBimageY, TBimageW, TBimageH, TBcount, TBco, 1);						
		System.out.println("----------log end-------------");
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
	
	
	// target_specify
	public void combine_type_determined(int[] dist, int minvalue, int case3_count,
										int[] Xs, int[] Ys, int[] Ws, int[] Hs, int index, int TorM_s,
										int[] Xt, int[] Yt, int[] Wt, int[] Ht, int count_t, int[][] co_t, int TorM_t){
		for (int i= 1; i<=count_t; i++){
			System.out.println(" i="+i+" count= "+count_t);
			if(TorM_s == TorM_t && index == i){ 
				System.out.println("  return");
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
					System.out.println("  case 1-1");
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
					System.out.println("  case 1-2");
				}else if(((TorM_s==1 && TBco[index][2]!=-1) || (TorM_s==2 && MOco[index][2]!=-1)) &&  //1-3. link-link
				   (co_t[i][2]!=-1)){
					linex[0]=0;
					linex[1]=0;
					liney[0]=0;
					liney[1]=0;
					link_case = 3;
					System.out.println("  case 1-3");
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
					System.out.println("  case 1-4");
				}
			}else if(dist[i]==minvalue && minvalue >= 100){ //2. target object, >=100
				linex[0]=0;
				linex[1]=0;
				liney[0]=0;
				liney[1]=0;
				link_case = -1;
				System.out.println("  case 2");
			}else{ 											//3. other remain (not closest)
				if(TorM_t==1 && link_to_TB_index == i){
					link_to_TB_index = -1;
				}else if (TorM_t==2 && link_to_MO_index == i){
					link_to_MO_index = -1;
				}
				co_t[i][0]=-1;
				co_t[i][1]=-1;
				System.out.println("  case 3");
				++case3_count;
				if(TBindex!=0 && case3_count == MOcount + TBcount -1){  // to catch all case 3, make line disappear
					linex[0]=0;
					linex[1]=0;
					liney[0]=0;
					liney[1]=0;
					link_case = -1;
					System.out.println("*** ALL case 3 ***");
				}else if (MOindex!=0 && case3_count == TBcount){
					linex[0]=0;
					linex[1]=0;
					liney[0]=0;
					liney[1]=0;
					link_case = -1;
					System.out.println("*** ALL case 3 ***");
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
		name[index].setLocation(X[index], Y[index]+H[index]-panelHeigth+15);
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
}
package main;
//import org.rosuda.JRI.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
	static JLayeredPane startPanel;
	static JLayeredPane nullPanel;
	static JPanel layoutPanel;
	
	public static void main(String[] args){    	
		JFrame main = new JFrame();
		main.setTitle("iPat");	
		main.setLocation(200, 0); 
		main.setSize(Wide, Heigth);
		main.setLayout(new BorderLayout());
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();
		Wide= main.getWidth();
		Heigth= main.getHeight();
		JPanel ipat = new myPanel(Wide, Heigth, PHeight, layoutPanel, startPanel, nullPanel);
		cPane.add(ipat);	
		main.setVisible(true);
		main.addComponentListener(new ComponentAdapter() {
			@Override
	        public void componentResized(ComponentEvent evt) {
				System.out.println("componentResized");
	            Component c = (Component)evt.getSource();
	            System.out.println("H: "+c.getHeight()+" W: "+c.getWidth()); 
	         //   Wide=main.getWidth();
	            main.setSize(Wide, Heigth);
	            System.out.println("Wide: "+Wide);
	            /*startPanel.resize(Wide,200);
	            http://stackoverflow.com/questions/6666637/resize-jpanel-from-jframe
	            layoutPanel.setBounds(new Rectangle(0, 0, Wide,200));
	            nullPanel.setSize(new Dimension(Wide, 500));

	            startPanel.setVisible(true);
	            layoutPanel.setVisible(true);
	            nullPanel.setVisible(true);
	            
	            startPanel.revalidate();
	            layoutPanel.revalidate();
	            nullPanel.revalidate();
	            ipat.revalidate();
	            cPane.revalidate();
	            */	            
	        }	
		});	
	}	
}




class myPanel extends JPanel implements MouseMotionListener{	
	  private static class ipatButton extends JButton {
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
	
	private static final int TBMAX=200;
	private static final int MOMAX=200;
	private static final int COMAX=100;	
	
	int[] TBimageX= new int[TBMAX];
	int[] TBimageY= new int[TBMAX];
	int[] TBimageH= new int[TBMAX];
	int[] TBimageW= new int[TBMAX];
	int[][] TBco= new int[TBMAX][3];  //1=combined or not(-1,1,2), 2= coindex, 3=subcombined or not (-1,1)
	int[] TBdelete= new int[TBMAX];
	
	int[] MOimageX= new int[MOMAX];
	int[] MOimageY= new int[MOMAX];
	int[] MOimageH= new int[MOMAX];
	int[] MOimageW= new int[MOMAX];
	int[][] MOco= new int[MOMAX][3];
	int[] MOdelete= new int[MOMAX];
	
	int[] COimageX= new int[COMAX];
	int[] COimageY= new int[COMAX];
	int[] COimageH= new int[COMAX];
	int[] COimageW= new int[COMAX];
	int[][] COco= new int[COMAX][4];
	
	Boolean link=false;
	
	int TBindex =0;
	int MOindex =0;
	int COindex =-1;
	
	int TBcount =0;
	int MOcount =0;
	int COcount =0;
	
	Rectangle[] TBBound= new Rectangle[TBMAX];
	Rectangle[] MOBound= new Rectangle[MOMAX];
	Rectangle[] COBound= new Rectangle[COMAX];
	
	int[] TBposX={50,  50,  50,  50,  50,  50,  50,
				  200, 200, 200, 200, 200, 200, 200,
				  350, 350, 350, 350, 350, 350, 350,
				  60, 60, 60, 60, 60, 60, 60,
				  210, 210, 210, 210, 210, 210, 210,
				  360, 360, 360, 360, 360, 360, 360,
				  70, 70, 70, 70, 70, 70, 70,
				  220, 220, 220, 220, 220, 220, 220,
				  370, 370, 370, 370, 370, 370, 370};
	int[] TBposY={160, 240, 320, 400, 480, 560, 640,
				  160, 240, 320, 400, 480, 560, 640,
				  160, 240, 320, 400, 480, 560, 640,
				  170, 250, 330, 410, 490, 570, 650,
				  170, 250, 330, 410, 490, 570, 650,
				  170, 250, 330, 410, 490, 570, 650,
				  180, 260, 340, 420, 500, 580, 660,
				  180, 260, 340, 420, 500, 580, 660,
				  180, 260, 340, 420, 500, 580, 660};
	
	int MOimageX_int=430;
	int MOimageY_int=200;
	
	int pos;
	
	Image[] TB= new Image[TBMAX];
	Image[] MO= new Image[MOMAX];
	Image[] Trash= new Image[10];
	Image[] White= new Image[10];
	Image Excel, Powerpoint, Word, Music, Video, Unknown, 
		  TBimage, MOimage, Prefbar;
	
	JLayeredPane startPanel;
	JPanel mainPanel;	
	JLayeredPane nullPanel;
	JPanel buttonPanel;
	JPanel layoutPanel;
	
	ipatButton TBButton = new ipatButton();
	ipatButton MOButton = new ipatButton();
	JLabel iPat = new JLabel();

	JFileChooser[] TBchooser= new JFileChooser[TBMAX];
	String[] TBfile= new String[TBMAX];
	int[] TBvalue= new int[TBMAX];
	
	JLabel[] TBname= new JLabel[TBMAX];	
	JLabel[] MOname= new JLabel[MOMAX];		

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
	int delbbound=170;
	JLabel trashl;
	
	//line
	int[] linex=new int[2],
		  liney=new int[2];
	
	//Color
	Color red = new Color(231,57,131, 100);
	Color lightred = new Color(231,57,131, 80);
	Color dlightred = new Color(255, 0, 0, 10);
	Color ovalcolor = new Color(231,57,131, 150);
	Color themecolor = new Color(54, 164, 239, 150);
	
	//windows size
	int Wide, Heigth, panelHeigth;
	
	//Preference
	Preferences pref1 = Preferences.userRoot().node("/ipat1");  
	Preferences pref2 = Preferences.userRoot().node("/ipat2");  
	Preferences pref3 = Preferences.userRoot().node("/ipat3");  
	Preferences pref4 = Preferences.userRoot().node("/ipat4");  
	Rectangle prefBound1 = new Rectangle();
	Rectangle prefBound2 = new Rectangle();
	Rectangle prefBound3 = new Rectangle();
	Rectangle prefBound4 = new Rectangle();
	Rectangle saveBound = new Rectangle();
	Rectangle restoreBound = new Rectangle();
	JLabel whitel;
	JLabel preshowl;
	int prefselect =0;
	int prefindex =0;
	Timer preshow1, preshow2, preshow3, preshow4;
	Image[] Preshow1= new Image[10];
	Image[] Preshow2= new Image[10];
	Image[] Preshow3= new Image[10];
	Image[] Preshow4= new Image[10];
	Boolean pre, showornot=false;
	
	//Intro
	Timer Intro;
	Color[] black= new Color[13];
	Boolean in=false, insub=false;
	
	//Button
	Timer TBanimation, MOanimation;
	
	
	public myPanel(int Wideint, int Heigthint, int pH, JPanel l, JLayeredPane s, JLayeredPane n){	
		this.Wide=Wideint;
		this.Heigth=Heigthint;
		this.panelHeigth=pH;
		this.layoutPanel=l;
		this.startPanel=s;
		this.nullPanel=n;
			
		try{
			Image iconIP = ImageIO.read(getClass().getResource("resources/iPat.png"));
			iPat.setIcon(new ImageIcon(iconIP));
		} catch (IOException ex){}
		try{
			for(int i=0; i<10; i++){
				Trash[i] = ImageIO.read(getClass().getResource("resources/trash"+i+".png"));
				White[i] = ImageIO.read(getClass().getResource("resources/white"+i+".png"));
				Preshow1[i] = ImageIO.read(getClass().getResource("resources/prefbar1"+i+".png"));
				Preshow2[i] = ImageIO.read(getClass().getResource("resources/prefbar2"+i+".png"));
				Preshow3[i] = ImageIO.read(getClass().getResource("resources/prefbar3"+i+".png"));
				Preshow4[i] = ImageIO.read(getClass().getResource("resources/prefbar4"+i+".png"));
			}
		} catch (IOException ex){}
		
		try{
			Excel = ImageIO.read(this.getClass().getResourceAsStream("resources/Excel.png"));
			Powerpoint = ImageIO.read(this.getClass().getResourceAsStream("resources/Powerpoint.png"));
			Word = ImageIO.read(this.getClass().getResourceAsStream("resources/Word.png"));
			Video = ImageIO.read(this.getClass().getResourceAsStream("resources/Video.png"));
			Music = ImageIO.read(this.getClass().getResourceAsStream("resources/Music.png"));
			Unknown = ImageIO.read(this.getClass().getResourceAsStream("resources/Unknown.png"));
		} catch (IOException ex){}
		
		
		try{
			TBimage = ImageIO.read(this.getClass().getResourceAsStream("resources/Table.png"));
			TBButton.setIcon(new ImageIcon(TBimage));
			MOimage = ImageIO.read(this.getClass().getResourceAsStream("resources/Model.png"));
			MOButton.setIcon(new ImageIcon(MOimage));
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
		this.setBackground(black[0]);
		
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
		trashl = new JLabel(new ImageIcon());
			
		startPanel = new JLayeredPane();
		nullPanel= new JLayeredPane();
		preshowl = new JLabel(new ImageIcon());
		whitel = new JLabel(new ImageIcon());		
		
		startPanel.setPreferredSize(new Dimension(Wide, panelHeigth));	
		startPanel.add(trashl, new Integer(1));
		startPanel.add(iPat, new Integer(4));
		startPanel.add(TBButton, new Integer(5));
		startPanel.add(MOButton, new Integer(6));
		
		trashl.setBounds(new Rectangle(-1000, -50, Wide, panelHeigth));
		iPat.setBounds(new Rectangle(523, 10, 150, 80)); 
		
		nullPanel.setPreferredSize(new Dimension(Wide, Heigth-panelHeigth));
		nullPanel.add(preshowl);
		nullPanel.add(whitel);
		
		prefBound1= new Rectangle(30,Heigth-58,80,60);
		prefBound2= new Rectangle(110,Heigth-58,80,60);
		prefBound3= new Rectangle(195,Heigth-58,80,60);
		prefBound4= new Rectangle(280,Heigth-58,80,60);
		saveBound= new Rectangle(413,Heigth-58,43,60);
		restoreBound= new Rectangle(1119,Heigth-58,48,60);
		
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
		}	
		for (int i=1; i<=MOMAX-1; i++){
			MOname[i]= new JLabel();		
			nullPanel.add(MOname[i]);
			MOimageH[i]=MO[i].getHeight(null);	
			MOimageW[i]=MO[i].getWidth(null);
			MOco[i][0]=-1;
			MOco[i][1]=-1;
		}	
		////////////
		////////////
		////////////
		//LAYOUT.END
		////////////
		////////////
		////////////	
		
		TBanimation = new Timer(10, new ActionListener() {
			int ant=10;
			int[] tbposx={365, 390, 415, 440, 465};
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		    	int tbin=(TBcount-1)%5;
				TBimageX[TBcount]=tbposx[tbin]-ant*2;
		        repaint();
		        ant--;
		        if(ant==-1){
		        	System.out.println("TBend");
		        	TBanimation.stop();
		        	TBBound[TBcount]=new Rectangle(TBimageX[TBcount], TBimageY[TBcount],TB[TBcount].getWidth(null), TB[TBcount].getHeight(null));
		        	ant=10;
		        }
		    }
		});	
		MOanimation = new Timer(10, new ActionListener() {
			int ant=10;
			int[] moposx={665, 690, 715, 740, 765};
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		    	int moin=(MOcount-1)%5;
				MOimageX[MOcount]=moposx[moin]-ant*2;
		        repaint();
		        ant--;
		        if(ant==-1){
		        	MOanimation.stop();
		        	MOBound[MOcount]=new Rectangle(MOimageX[MOcount], MOimageY[MOcount],MO[MOcount].getWidth(null), MO[MOcount].getHeight(null));
		        	ant=10;
		        }
		    }
		});	
		
		
		TBButton.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (contains(x,y)){
    				TBcount++;				
    				TBimageY[TBcount]=200;					
    				TBanimation.start();	
    			}		
    		}
    	});  
		
		MOButton.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (contains(x,y)){
    				MOcount++;
    				System.out.println(MOcount);
    				MOimageY[MOcount]=200;
    				MOanimation.start();
    			}		
    		}
    	}); 	
		this.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent ee){
				int move_x=ee.getX();
    			int move_y=ee.getY();
    			COindex=-1;
				TBindex=0;
    			MOindex=0;
    			
    			if(!in){
    				Intro= new Timer(20, new ActionListener(){
    					int t=10;
    					float f=0f;
    					@Override
    					public void actionPerformed(ActionEvent ani) {	
    						if(insub){				
    							if(f<1f){
    								TBButton.setAlpha(f);		
        							TBButton.setBounds(new Rectangle((Wide/2)-182, 90-(t*2), 80, 60)); 
    							} 							
    							if(f>0.3f){
    								MOButton.setAlpha(f-0.3f);		
        							MOButton.setBounds(new Rectangle((Wide/2)+107, 90-((t+3)*2), 80, 60)); 
    							}
    							
    							MOButton.setVisible(true);
    							TBButton.setVisible(true);
    							setBackground(black[10-t]);
	    				    	f=f+0.1f;
	    				    	t--;
	    				    	if(f-0.3f>1f){
	    				    		Intro.stop();
	    				    		setBackground(Color.WHITE);
	    				    		f=0f;
	    				    		insub=false;
	    				    	}	
    						}					
    					}	
    				});
    				insub=true;
    				Intro.start();
    				in=true;
    			}
    			
    			if (TBcount>0){			
    				for (int i=1; i<=TBcount;i++){
    					if (TBBound[i].contains(move_x, move_y)){
    						TBindex=i;			
    						if (SwingUtilities.isRightMouseButton(ee)){
    							 TBvalue[i]= TBchooser[i].showOpenDialog(null);
    	    					 if (TBvalue[i] == JFileChooser.APPROVE_OPTION){
    	    					    File selectedfile = TBchooser[i].getSelectedFile();  	    					    
    	    					  	TBfile[i]= selectedfile.getAbsolutePath();
    	    					  	iconchange(i); 
    	    					  	TBimageH[TBindex]=TB[i].getHeight(null);
    	    					  	TBimageW[TBindex]=TB[i].getHeight(null);
    	    						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
    	    						TBname[i].setSize(200,15);
    	    						TBname[i].setText(selectedfile.getName());
    	    						TBindex=0;
    	    						COindex=-1;
    	    						repaint();
    	    					 }
    					   	};
    					}
    				}
    			}     	
    			if (MOcount>0&&TBindex==0){			
    				for (int i=1; i<=MOcount; i++){
    					if (MOBound[i].contains(move_x, move_y)){
    						MOindex=i;
    					}
    				}
    			} 
    			if (COBound[0]!=null){
    				for (int i=0; i<=COcount-1; i++){
    					if(COBound[i].contains(move_x, move_y)){
    						COindex=i;
    						MOindex=0;
    						TBindex=0;
    						System.out.println("CO is selected");	
    					}
    				}
    			}
    		
    			System.out.println("TBcount= "+TBcount);
    			System.out.println("MOcount= "+MOcount);
    			System.out.println("COcount= "+COcount);
    			System.out.println("TBco= "+TBco[1][0]+TBco[1][1]+TBco[1][2]);
    			System.out.println("MOco= "+MOco[1][0]+MOco[1][1]+MOco[1][2]);
    			System.out.println("COco= "+COco[0][0]+COco[0][1]+COco[0][2]+COco[1][3]);
    			System.out.println("TBX= "+TBimageX[1]+"  TBY= "+TBimageY[1]+"  TBW= "+TBimageW[1]+"  TBH= "+TBimageH[1]);
    			System.out.println("COX= "+COimageX[0]+"  COY= "+COimageY[0]+"  COW= "+COimageW[0]+"  COH= "+COimageH[0]);
    			
			}	
				
			@Override
			public void mouseReleased(MouseEvent ee){				
				int x=ee.getX();
				int y=ee.getY();		
				System.out.println("TBLable[1]: "+TBname[1].getText()+"  TBnamepos: "+TBname[1].getLocation());
				if( (COco[COcount][0]>0) &  //若這個新做的co有指定table或model了
					(TBindex!=0|MOindex!=0) & 	 //且正在選某個物件
					!link& !removeornot){
						System.out.println("linked");
						if (TBindex!=0){
							TBco[TBindex][2]=1;			
						}else if(MOindex!=0){
							MOco[MOindex][2]=1;	
						}
						if(COco[COcount][1]== 1){ //if link to table
							TBco[COco[COcount][3]][0]=2;
							TBco[COco[COcount][3]][1]=COcount;
							TBco[COco[COcount][3]][2]=1;
						}else if(COco[COcount][1]== 2){ //if link to model
							MOco[COco[COcount][3]][0]=2;
							MOco[COco[COcount][3]][1]=COcount;
							MOco[COco[COcount][3]][2]=1;
						}
						
						int X1=0, Y1=0, W1=0, H1=0, X2=0, Y2=0, W2=0, H2=0;
						if(COco[COcount][0]+COco[COcount][1]==4){
							X1=MOimageX[MOindex]; Y1=MOimageY[MOindex];
							W1=MOimageW[MOindex]; H1=MOimageH[MOindex];
							X2=MOimageX[COco[COcount][3]];	Y2=MOimageY[COco[COcount][3]];
							W2=MOimageW[COco[COcount][3]];	H2=MOimageH[COco[COcount][3]];
						}else if(COco[COcount][0]==1 &&COco[COcount][1]==2){
							X1=TBimageX[TBindex]; Y1=TBimageY[TBindex];
							W1=TBimageW[TBindex]; H1=TBimageH[TBindex];
							X2=MOimageX[COco[COcount][3]]; Y2=MOimageY[COco[COcount][3]];
							W2=MOimageW[COco[COcount][3]];	H2=MOimageH[COco[COcount][3]];
						}else if(COco[COcount][0]==2 &&COco[COcount][1]==1){
							X1=MOimageX[MOindex]; Y1=MOimageY[MOindex];
							W1=MOimageW[MOindex]; H1=MOimageH[MOindex];
							X2=TBimageX[COco[COcount][3]]; Y2=TBimageY[COco[COcount][3]];
							W2=TBimageW[COco[COcount][3]];	H2=TBimageH[COco[COcount][3]];				
						}else if(COco[COcount][0]+COco[COcount][1]==2){
							X1=TBimageX[TBindex]; Y1=TBimageY[TBindex];
							W1=TBimageW[TBindex]; H1=TBimageH[TBindex];
							X2=TBimageX[COco[COcount][3]]; Y2=TBimageY[COco[COcount][3]];
							W2=TBimageW[COco[COcount][3]];	H2=TBimageH[COco[COcount][3]];
						}
						COimageX[COcount]=Math.min(X1, X2);
						COimageY[COcount]=Math.min(Y1, Y2);
						if (X2>=X1){
							COimageW[COcount]=X2+W2-X1;
						}else{
							COimageW[COcount]=X1+W1-X2;
						}
						if (Y2>=Y1){
							COimageH[COcount]=Y2+H2-Y1;
						}else{
							COimageH[COcount]=Y1+H1-Y2;
						}
						COBound[COcount]=new Rectangle(COimageX[COcount], COimageY[COcount], COimageW[COcount], COimageH[COcount]);		
						
						CDAindex=COcount;
						CombinedDeleteAnimation= new Timer(10, new ActionListener() {
							int t=12;
					    	int CX=COimageX[CDAindex];
					    	int CY=COimageY[CDAindex];
						    @Override
						    public void actionPerformed(ActionEvent ani) {	
						    	COimageX[CDAindex]=(int)(CX+t);
						    	COimageY[CDAindex]=(int)(CY+t);
						    	CDAW=(int)((12-t)*2);
						    	CDAH=(int)((12-t)*2);
						    	t--;
						    	repaint();
						    	if(t==0){
						    		CDAindex=-1;
						    		CombinedDeleteAnimation.stop();
						    	} 
						    	/*
						    	 g.fillOval((int)(COimageX[i]-12.5+COimageW[i]*.5), (int)(COimageY[i]-12.5 +COimageH[i]*.5),
					    				 	25, 25);
						    	 g.fillOval((int)(COimageX[i]-12.5+COimageW[i]*.5), (int)(COimageY[i]-12.5 +COimageH[i]*.5),
					    				 	CDAW, CDAH);
						    	 */
						    }
						});   	
						CombinedDeleteAnimation.start();
						COcount++;	
				}
				
				if (removeornot){
					if (TBindex!=0&&y<delbbound){
						TBimageX[TBindex]=-1000;
						TBimageY[TBindex]=-1000;
						TBBound[TBindex]=new Rectangle(-100,-100,0,0);
						TBname[TBindex].setLocation(-100,-100);
						repaint();
						TBdelete[TBindex]=-1;
					}else if(MOindex!=0&&y<delbbound){
						MOimageX[MOindex]=-1000;
						MOimageY[MOindex]=-1000;
						MOBound[MOindex]=new Rectangle(-100,-100,0,0);
						MOname[MOindex].setLocation(-100,-100);
						repaint();
						MOdelete[MOindex]=-1;
					}else if(COindex!=-1&&y<delbbound){
						COimageX[COindex]=-1000;
						COimageY[COindex]=-1000;
						COBound[COindex]=new Rectangle(COimageX[COcount], COimageY[COcount], COimageW[COcount], COimageH[COcount]);	
						for (int t=1; t<=TBcount; t++){
							if(TBco[t][1]==COindex){
								TBco[t][0]=-1;
								TBco[t][1]=-1;
								TBco[t][2]=-1;
								TBimageX[t]=-1000;
								TBimageY[t]=-1000;
								TBBound[t]=new Rectangle(-100,-100,0,0);
								TBname[t].setLocation(-100,-100);
								repaint();
								TBdelete[t]=-1;
							}
						}
						for (int m=1; m<=MOcount; m++){
							if(MOco[m][1]==COindex){
								MOco[m][0]=-1;
								MOco[m][1]=-1;
								MOco[m][2]=-1;
								MOimageX[m]=-1000;
								MOimageY[m]=-1000;
								MOBound[m]=new Rectangle(-100,-100,0,0);
								MOname[m].setLocation(-100,-100);
								repaint();
								MOdelete[m]=-1;
							}
						}
					}
    				trashl.setBounds(new Rectangle(-1000, -50, Wide, 300));  				
					trashl.setVisible(true);
					removeornot=false;
	    		}
				
				if(link){
					System.out.println("link to mainstem");
					if(TBindex!=0){
						TBco[TBindex][2]=1;
					}else if(MOindex!=0){
						MOco[MOindex][2]=1;
					}
				}
				
				Fadein1= new Timer(10, new ActionListener(){
					int t=0;
					int[] thread={9,8,7,6,5,4,3,2,1,0,1,2,3,4,5,6,7,8,9};
					@Override
					public void actionPerformed(ActionEvent ani) {	
						whitel.setBounds(0, 0, Wide, Heigth);
						whitel.setIcon(new ImageIcon(White[thread[t]]));
						whitel.setVisible(true);
						preshowl.setIcon(new ImageIcon(Preshow1[9]));
						if(t==10){getPreference(pref1);}
				    	t++;		    			    	
				    	if(t==19){
				    		Fadein1.stop();	
				    		whitel.setBounds(Wide, 0, Wide, Heigth);
				    		prefindex=1;
				    		t=0;
				    	}	
					}	
				});
				Fadein2= new Timer(10, new ActionListener(){
					int t=0;
					int[] thread={9,8,7,6,5,4,3,2,1,0,1,2,3,4,5,6,7,8,9};
					@Override
					public void actionPerformed(ActionEvent ani) {	
						whitel.setBounds(0, 0, Wide, Heigth);
						whitel.setIcon(new ImageIcon(White[thread[t]]));
						whitel.setVisible(true);
						preshowl.setIcon(new ImageIcon(Preshow2[9]));
						if(t==10){getPreference(pref2);}
				    	t++;		    			    	
				    	if(t==19){
				    		Fadein2.stop();	
				    		whitel.setBounds(Wide, 0, Wide, Heigth);
		    				prefindex=2;
				    		t=0;
				    	}	
					}	
				});

				Fadein3= new Timer(10, new ActionListener(){
					int t=0;
					int[] thread={9,8,7,6,5,4,3,2,1,0,1,2,3,4,5,6,7,8,9};
					@Override
					public void actionPerformed(ActionEvent ani) {	
						whitel.setBounds(0, 0, Wide, Heigth);
						whitel.setIcon(new ImageIcon(White[thread[t]]));
						whitel.setVisible(true);
						preshowl.setIcon(new ImageIcon(Preshow3[9]));
						if(t==10){getPreference(pref3);}
				    	t++;		    			    	
				    	if(t==19){
				    		Fadein3.stop();	
				    		whitel.setBounds(Wide, 0, Wide, Heigth);
				    		prefindex=3;
				    		t=0;
				    	}	
					}	
				});
				Fadein4= new Timer(10, new ActionListener(){
					int t=0;
					int[] thread={9,8,7,6,5,4,3,2,1,0,1,2,3,4,5,6,7,8,9};
					@Override
					public void actionPerformed(ActionEvent ani) {	
						whitel.setBounds(0, 0, Wide, Heigth);
						whitel.setIcon(new ImageIcon(White[thread[t]]));
						whitel.setVisible(true);
						preshowl.setIcon(new ImageIcon(Preshow4[9]));
						if(t==10){getPreference(pref4);}
				    	t++;		    			    	
				    	if(t==19){
				    		Fadein4.stop();	
				    		whitel.setBounds(Wide, 0, Wide, Heigth);
				    		prefindex=4;
				    		t=0;
				    	}	
					}	
				});
				Fadesave= new Timer(30, new ActionListener(){
					int t=0;	
					int[] thread={0,2,4,6,8,
								  0,0,0,0,0,1,1,2,2,3,
								  3,4,4,5,5,6,6,7,7,8,
								  8,9,9};
					@Override
					public void actionPerformed(ActionEvent ani) {	
						whitel.setBounds(0, 0, Wide, Heigth);
						whitel.setIcon(new ImageIcon(White[thread[t]]));
						whitel.setVisible(true);
				    	t++;		    			    	
				    	if(t==28){
				    		Fadesave.stop();	
				    		whitel.setBounds(Wide, 0, Wide, Heigth);
				    		t=0;
				    	}	
					}	
				});
				
				Wipe= new Timer(20, new ActionListener(){
					int t=0;
					@Override
					public void actionPerformed(ActionEvent ani) {	
						whitel.setBounds(Wide*(t-50)/50, 0, Wide, Heigth);
				    	t++;		    			    	
				    	System.out.println(whitel.getLocation());
				    	System.out.println(t);
				    	if(t==50){
				    		Wipe.stop();
				    		if(prefindex==1){
		    					removePreference(pref1);
		    					getPreference(pref1);
		    				}else if (prefindex==2){
		    					removePreference(pref2);
		    					getPreference(pref2);
		    				}else if (prefindex==3){
		    					removePreference(pref3);
		    					getPreference(pref3);
		    				}else if (prefindex==4){
		    					removePreference(pref4);
		    					getPreference(pref4);
		    				}
				    		whitel.setBounds(Wide, 0, Wide, Heigth);
				    		t=0;
				    	}	
					}	
				});
				
				if(prefBound1.contains(x, y)){
					Fadein1.start();
    			}else if(prefBound2.contains(x, y)){
    				Fadein2.start();
    			}else if(prefBound3.contains(x, y)){
    				Fadein3.start();
    			}else if(prefBound4.contains(x, y)){
    				Fadein4.start();
    			}else if(saveBound.contains(x, y)){
    				Fadesave.start();
    				System.out.println("save");
    				if(prefindex==1){
    					setPreference(pref1);
    					getPreference(pref1);
    				}else if (prefindex==2){
    					setPreference(pref2);
    					getPreference(pref2);
    				}else if (prefindex==3){
    					setPreference(pref3);
    					getPreference(pref3);
    				}else if (prefindex==4){
    					setPreference(pref4);
    					getPreference(pref4);
    				}
    			}else if(restoreBound.contains(x, y)){
    				whitel.setIcon(new ImageIcon(White[0]));
					whitel.setVisible(true);
    				Wipe.start();
    			}
				
				linex[0]=0;
				linex[1]=0;
				liney[0]=0;
				liney[1]=0;	
				
				repaint();
				TBindex=0;
				MOindex=0;
				COindex=-1;
				link=false;
			}
			
			
			@Override
    		public void mouseClicked(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (evt.getClickCount() == 2 ) {
    				for (int i=1; i<=TBcount-1; i++){
    					if (TBBound[i].contains(x,y)){
        					TBopenfile(i);
    					}
    				}
    			}
    			if (evt.getClickCount() == 3 & SwingUtilities.isRightMouseButton(evt)) {
    				for (CDAi=0; CDAi<=COcount-1; CDAi++){
    					if (COBound[CDAi].contains(x,y)){
    						CDAindex=CDAi;
    						CombinedDeleteAnimation= new Timer(2, new ActionListener() {
    							int t=0, nt=50-t;
    							int[] xp={0,0,0,0,0, 1,1,1,1,1,
    									2,2,2,2,2, 3,3,3,3,3, 
    									4,4,4,4,4, 5,5,5,5,5, 
    									6,6,6,6,6, 7,7,7,8,8, 
    									8,9,9,9,10, 10,10,12,12,12};
    					    	int CX=COimageX[CDAindex];
    					    	int CY=COimageY[CDAindex];
    						    @Override
    						    public void actionPerformed(ActionEvent ani) {	
    						    	COimageX[CDAindex]=(int)(CX+1+xp[t]);
    						    	COimageY[CDAindex]=(int)(CY+xp[t]);
    						    	CDAW=(int)(25-t*.5);
    						    	CDAH=(int)(25-t*.5);
    						    	repaint();
    						    	t++;
    						    	if(t==50){
    						    		CombinedDeleteAnimation.stop();
    						    		t=0;
    						        	COimageX[CDAindex]=-1000;
    									COimageY[CDAindex]=-1000;
    									COBound[CDAindex]=new Rectangle(COimageX[CDAindex], COimageY[CDAindex], COimageW[CDAindex], COimageH[CDAindex]);	
    									CDAindex=-1;
    									repaint();	
    						    	}
    						    }      					    
    						});
    						CombinedDeleteAnimation.start();	
    						for (int t=1; t<=TBcount; t++){
    							if(TBco[t][1]==CDAi){
    								TBco[t][0]=-1;
    								TBco[t][1]=-1;
    								TBco[t][2]=-1;
    							}
    						}
    						for (int m=1; m<=MOcount; m++){
    							if(MOco[m][1]==CDAi){
    								MOco[m][0]=-1;
    								MOco[m][1]=-1;
    								MOco[m][2]=-1;
    							}
    						}
    					}  					
    				}
    			}
    		}
		});	
	prefindex=1;
	getPreference(pref1);
	addMouseMotionListener(this);
}
	
	@Override
	protected void paintComponent(Graphics g) {	
	     super.paintComponent(g);
	     if (prefselect!=0){
		     g.setColor(lightred);
		     if(prefselect==1){
		    	 g.fill3DRect(66,Heigth-48 , 5, 5, false);
		     }else if(prefselect==2){
		    	 g.fill3DRect(149,Heigth-48 , 5, 5, false);
		     }else if(prefselect==3){
		    	 g.fill3DRect(234,Heigth-48 , 5, 5, false);
		     }else if(prefselect==4){
		    	 g.fill3DRect(317,Heigth-48 , 5, 5, false);
		     }else if(prefselect==5){
		    	 g.fill3DRect(434,Heigth-48 , 5, 5, false);
		     }else if(prefselect==-1){
		    	 g.fill3DRect(1140, Heigth-48, 5, 5, false);
		     }
	     }
	     g.setColor(ovalcolor);
	     if (COcount>0){
	    	 for (int i=0; i<COcount; i++){    	
	    		 if(i==CDAindex){
	    			 g.fillOval((int)(COimageX[i]-12.5+COimageW[i]*.5), (int)(COimageY[i]-12.5 +COimageH[i]*.5),
		    				 	CDAW, CDAH);
	    		 }else{
	    			 g.fillOval((int)(COimageX[i]-12.5+COimageW[i]*.5), (int)(COimageY[i]-12.5 +COimageH[i]*.5),
		    				 	25, 25);
	    		 }
	    		 
	    	 }
	     }	
		 DrawDashedLine(g, linex[0], liney[0], linex[1], liney[1]);	
		 DrawImageandLine(TBcount, TBdelete, TBco, g, TB, TBimageX, TBimageY, TBimageH, TBimageW);
		 DrawImageandLine(MOcount, MOdelete, MOco, g, MO, MOimageX, MOimageY, MOimageH, MOimageW); 
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
		
		if(COindex!=-1){	
			int dx= imX- (COimageX[COindex]+(COimageW[COindex]/2));
			int dy= imY- (COimageY[COindex]+(COimageH[COindex]/2));
			if(COco[COindex][0]==1&COco[COindex][1]==1){
					DragandKeepCombinedinPanel (imX, imY, dx, dy, boundE, boundS,
							COco[COindex][2], TBimageX, TBimageY, TBimageW, TBimageH, TBBound, TBname, 1,
							COco[COindex][3], TBimageX, TBimageY, TBimageW, TBimageH, TBBound, TBname, 1);
			}else if(COco[COindex][0]==1&COco[COindex][1]==2){
					DragandKeepCombinedinPanel (imX, imY, dx, dy, boundE, boundS,
							COco[COindex][2], TBimageX, TBimageY, TBimageW, TBimageH, TBBound, TBname, 1,
							COco[COindex][3], MOimageX, MOimageY, MOimageW, MOimageH, MOBound, MOname, 2);		
			}else if (COco[COindex][0]==2&COco[COindex][1]==1){
					DragandKeepCombinedinPanel (imX, imY, dx, dy, boundE, boundS,
							COco[COindex][2], MOimageX, MOimageY, MOimageW, MOimageH, MOBound, MOname, 2,
							COco[COindex][3], TBimageX, TBimageY, TBimageW, TBimageH, TBBound, TBname, 1);
			}else if(COco[COindex][0]==2&COco[COindex][1]==2){
					DragandKeepCombinedinPanel (imX, imY, dx, dy, boundE, boundS,
							COco[COindex][2], MOimageX, MOimageY, MOimageW, MOimageH, MOBound, MOname, 2,
							COco[COindex][3], MOimageX, MOimageY, MOimageW, MOimageH, MOBound, MOname, 2);
			}
			repaint();
		}	
	
			
		if ((TBindex!=0|MOindex!=0|COindex!=-1)&&imY<=(delbbound)&&!removeornot){
			TrashAnimation = new Timer(15, new ActionListener() {
				int i=0;
			    @Override
			    public void actionPerformed(ActionEvent ae) {
			    	if(i<10&TA){
			    		System.out.println("this is "+i);
			    		trashl.setBounds(new Rectangle(0, -50, Wide, 300));
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
		}else if(imY>delbbound){
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
		System.out.println("x= "+x+"y= "+y);
		if(y>delbbound){
			trashl.setBounds(new Rectangle(Wide, -50, Wide, 300));
			startPanel.setLayer(trashl, new Integer(1));
			trashl.setVisible(true);
			removeornot=false;				
		}			
		
		if(y>Heigth-80&!showornot){		
			preshow1= new Timer(20, new ActionListener(){
				int t=0;
				@Override
				public void actionPerformed(ActionEvent ani) {			
					if(pre){
						System.out.println(t);
						preshowl.setBounds(0, Heigth-panelHeigth-40, Wide, 40);
						preshowl.setIcon(new ImageIcon(Preshow1[t]));
						preshowl.setVisible(true);
				    	t++;
				    	if(t==10){
				    		preshow1.stop();
				    		t=0;
				    		pre=false;
				    	}   	
				    }		    			    			    		
				}	
			});	
			preshow2= new Timer(20, new ActionListener(){
				int t=0;
				@Override
				public void actionPerformed(ActionEvent ani) {			
					if(pre){
						System.out.println(t);
						preshowl.setBounds(0, Heigth-panelHeigth-40, Wide, 40);
						preshowl.setIcon(new ImageIcon(Preshow2[t]));
						preshowl.setVisible(true);
				    	t++;
				    	if(t==10){
				    		preshow2.stop();
				    		t=0;
				    		pre=false;
				    	}   	
				    }		    			    			    		
				}	
			});	
			preshow3= new Timer(20, new ActionListener(){
				int t=0;
				@Override
				public void actionPerformed(ActionEvent ani) {			
					if(pre){
						System.out.println(t);
						preshowl.setBounds(0, Heigth-panelHeigth-40, Wide, 40);
						preshowl.setIcon(new ImageIcon(Preshow3[t]));
						preshowl.setVisible(true);
				    	t++;
				    	if(t==10){
				    		preshow3.stop();
				    		t=0;
				    		pre=false;
				    	}   	
				    }		    			    			    		
				}	
			});	
			preshow4= new Timer(20, new ActionListener(){
				int t=0;
				@Override
				public void actionPerformed(ActionEvent ani) {			
					if(pre){
						System.out.println(t);
						preshowl.setBounds(0, Heigth-panelHeigth-40, Wide, 40);
						preshowl.setIcon(new ImageIcon(Preshow4[t]));
						preshowl.setVisible(true);
				    	t++;
				    	if(t==10){
				    		preshow4.stop();
				    		t=0;
				    		pre=false;
				    	}   	
				    }		    			    			    		
				}	
			});	
			pre=true;
			if(prefindex==1){
				preshow1.start();
			}else if(prefindex==2){
				preshow2.start();
			}else if(prefindex==3){
				preshow3.start();
			}else if(prefindex==4){
				preshow4.start();
			}
			showornot=true;
		}else if(y<=Heigth-80){		
			showornot=false;
			preshowl.setBounds(Wide, Heigth-panelHeigth-40, Wide, 40);
		}
		
		if(prefBound1.contains(x, y)){
			prefselect=1;repaint();
		}else if(prefBound2.contains(x, y)){
			prefselect=2;repaint();
		}else if(prefBound3.contains(x, y)){
			prefselect=3;repaint();
		}else if(prefBound4.contains(x, y)){
			prefselect=4;repaint();
		}else if(saveBound.contains(x, y)){
			prefselect=5;repaint();
		}else if(restoreBound.contains(x, y)){
			prefselect=-1;repaint();
		}else{
			prefselect=0;repaint();
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
	  	
	  	else{TB[i]=Unknown;}	  	
	}
	
	// html
	public void CombinedorNot(int index, int[] X, int[] Y, int[] W, int[] H, int TorM){
		int[] TBdist= new int[TBcount+1];
		int[] MOdist= new int[MOcount+1];
		int x= X[index]+(W[index]/2);
		int y= Y[index]+(H[index]/2);	
		for (int i=1; i<=TBcount; i++){
			if (i == TBindex) {
	            TBdist[i]=10000;
	        }else{
	        	int x2= TBimageX[i]+(TBimageW[i]/2);
				int y2= TBimageY[i]+(TBimageH[i]/2);
				int dist= (int) Math.sqrt(Math.pow((x-x2), 2) + Math.pow((y-y2), 2));
				System.out.println("TBdist"+i+" : "+dist);
				TBdist[i]= dist;	
	        }			
		}
		for (int i=1; i<=MOcount; i++){
			if (i == MOindex) {
				MOdist[i]=10000;   
	        }else{
	        	int x2= MOimageX[i]+(MOimageW[i]/2);
				int y2= MOimageY[i]+(MOimageH[i]/2);
				int dist= (int) Math.sqrt(Math.pow((x-x2), 2) + Math.pow((y-y2), 2));
				System.out.println("MOdist"+i+" : "+dist);
				MOdist[i]= dist;
	        }	
		}
		int[] newdist = new int[TBdist.length + MOdist.length-2];
		System.arraycopy(TBdist, 1, newdist, 0 		   		, TBdist.length-1);
		System.arraycopy(MOdist, 1, newdist, TBdist.length-1, MOdist.length-1);
		int minvalue= MinValue(newdist);	
		System.out.println("minvalue= "+minvalue);
		
		if (minvalue<100& minvalue>50){
			for (int i=1; i<=MOcount; i++){
				if(MOdist[i]==minvalue){					
					if(MOco[i][0]==-1){
						COco[COcount][0]= TorM;//
						COco[COcount][1]= 2;
						COco[COcount][2]= index;
						COco[COcount][3]= i;
												
						if(TorM==1){
							TBco[index][0]=2;
							TBco[index][1]=COcount;
						}else if (TorM==2){
							MOco[index][0]=2;
							MOco[index][1]=COcount;
						}
						link=false;						
					}else if(MOco[i][0]!=-1){
						if(TorM==1){
							TBco[index][0]=1;
							TBco[index][1]=MOco[i][1];
						}else if (TorM==2){
							MOco[index][0]=1;
							MOco[index][1]=MOco[i][1];
						}
						link=true;
					}
					
					linex[0]= X[index]+(W[index]/2);
					liney[0]= Y[index]+(H[index]/2);
					linex[1]= MOimageX[i]+(MOimageW[i]/2);
					liney[1]= MOimageY[i]+(MOimageH[i]/2);
					repaint();
					break;
				}	
			}
			for (int i=1; i<=TBcount; i++){
				if(TBdist[i]==minvalue){
					if(TBco[i][0]==-1){
						System.out.println("TBlink to "+i);
						COco[COcount][0]= TorM;//
						COco[COcount][1]= 1;
						COco[COcount][2]= index;
						COco[COcount][3]= i;			
						if(TorM==1){
							TBco[index][0]=2;
							TBco[index][1]=COcount;
						}else if (TorM==2){
							MOco[index][0]=2;
							MOco[index][1]=COcount;
						}	
						link=false;
					}else if (TBco[i][0]!=-1){	
						System.out.println("TBlink to "+i);
						if(TorM==1){
							TBco[index][0]=1;
							TBco[index][1]=TBco[i][1];
						}else if (TorM==2){
							MOco[index][0]=1;
							MOco[index][1]=TBco[i][1];
						}
						link=true;
						System.out.println("link true");
					}
					linex[0]= X[index]+(W[index]/2);
					liney[0]= Y[index]+(H[index]/2);
					linex[1]= TBimageX[i]+(TBimageW[i]/2);
					liney[1]= TBimageY[i]+(TBimageH[i]/2);
					repaint();
					break;					
				}
			}		
			
		}else{
			COco[COcount][0]= -1;//
			COco[COcount][1]= -1;
			COco[COcount][2]= -1;
			COco[COcount][3]= -1;
			if(TorM==1){
				TBco[index][0]=-1;
				TBco[index][1]=-1;
			}else if (TorM==2){
				MOco[index][0]=-1;
				MOco[index][1]=-1;
			}
			link=false;
			linex[0]=0;
			linex[1]=0;
			liney[0]=0;
			liney[1]=0;
			repaint();
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
	
	public void DragandKeepCombinedinPanel (int x, int y, int dx, int dy, int boundE, int boundS, 
											int index1, int[] X1, int[] Y1, int[] W1, int[] H1, Rectangle[] Bound1, JLabel[] name1, int TBMO1,
											int index2, int[] X2, int[] Y2, int[] W2, int[] H2, Rectangle[] Bound2, JLabel[] name2,int TBMO2){	
		int[] tbx=new int[TBcount+1];
		int[] tby=new int[TBcount+1];
		int[] mox=new int[MOcount+1];
		int[] moy=new int[MOcount+1];
		for (int i=1; i<=TBcount; i++){
			if(TBMO1==1&i==index1){continue;}
			if(TBMO2==1&i==index2){continue;}
			if(TBco[i][1]==COindex){
				tbx[i]=TBimageX[i]-COimageX[COindex];
				tby[i]=TBimageY[i]-COimageY[COindex];
			}
		}
		for (int i=1; i<=MOcount; i++){
			if(TBMO1==2&i==index1){continue;}
			if(TBMO2==2&i==index2){continue;}
			if(MOco[i][1]==COindex){
				mox[i]=MOimageX[i]-COimageX[COindex];
				moy[i]=MOimageY[i]-COimageY[COindex];	
			}
		}
		if (x-(COimageW[COindex]/2)<0){
			COimageX[COindex]=0;								
			if(X1[index1]>X2[index2]){
				X1[index1]=0+COimageW[COindex]-W1[index1];
				X2[index2]=0;
			}else{
				X1[index1]=0;
				X2[index2]=0+COimageW[COindex]-W2[index2];			
			}		
			if(y-(COimageH[COindex]/2)<0){
				COimageY[COindex]=0;
				if(Y1[index1]>Y2[index2]){
					Y1[index1]=0+COimageH[COindex]-H1[index1];
					Y2[index2]=0;		
				}else{
					Y1[index1]=0;
					Y2[index2]=0+COimageH[COindex]-H2[index2];
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else if(y+(COimageH[COindex]/2)>boundS){
				COimageY[COindex]=boundS-COimageH[COindex];
				if(Y1[index1]>Y2[index2]){
					Y1[index1]=boundS-H1[index1];
					Y2[index2]=boundS-COimageH[COindex];
				}else{
					Y1[index1]=boundS-COimageH[COindex];
					Y2[index2]=boundS-H1[index2];
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else {
				COimageY[COindex]=COimageY[COindex]+dy;
				Y1[index1]=Y1[index1]+dy;
				Y2[index2]=Y2[index2]+dy;
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=TBimageY[i]+dy;
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=MOimageY[i]+dy;
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}	
			}	
		}else if(x+(COimageW[COindex]/2)>boundE){
			COimageX[COindex]=boundE-COimageW[COindex];								
			if(X1[index1]>X2[index2]){
				X1[index1]=boundE-W1[index1];
				X2[index2]=boundE-COimageW[COindex];
			}else{
				X1[index1]=boundE-COimageW[COindex];
				X2[index2]=boundE-W2[index2];		
			}		
			if(y-(COimageH[COindex]/2)<0){
				COimageY[COindex]=0;
				if(Y1[index1]>Y2[index2]){
					Y1[index1]=0+COimageH[COindex]-H1[index1];
					Y2[index2]=0;		
				}else{
					Y1[index1]=0;
					Y2[index2]=0+COimageH[COindex]-H2[index2];
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else if(y+(COimageH[COindex]/2)>boundS){
				COimageY[COindex]=boundS-COimageH[COindex];
				if(Y1[index1]>Y2[index2]){
					Y1[index1]=boundS-H1[index1];
					Y2[index2]=boundS-COimageH[COindex];
				}else{
					Y1[index1]=boundS-COimageH[COindex];
					Y2[index2]=boundS-H1[index2];
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else {
				COimageY[COindex]=COimageY[COindex]+dy;
				Y1[index1]=Y1[index1]+dy;
				Y2[index2]=Y2[index2]+dy;
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=TBimageY[i]+dy;
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=MOimageY[i]+dy;
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}	
			}	
		}else if(y-(COimageH[COindex]/2)<0){
			COimageY[COindex]=0;						
			if(Y1[index1]>Y2[index2]){
				Y1[index1]=0+COimageH[COindex]-H1[index1];
				Y2[index2]=0;		
			}else{
				Y1[index1]=0;
				Y2[index2]=0+COimageH[COindex]-H2[index2];
			}			
			if (x-(COimageW[COindex]/2)<0){
				COimageX[COindex]=0;								
				if(X1[index1]>X2[index2]){
					X1[index1]=0+COimageW[COindex]-W1[index1];
					X2[index2]=0;
				}else{
					X1[index1]=0;
					X2[index2]=0+COimageW[COindex]-W2[index2];			
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else if(x+(COimageW[COindex]/2)>boundE){
				COimageX[COindex]=boundE-COimageW[COindex];								
				if(X1[index1]>X2[index2]){
					X1[index1]=boundE-W1[index1];
					X2[index2]=boundE-COimageW[COindex];
				}else{
					X1[index1]=boundE-COimageW[COindex];
					X2[index2]=boundE-W2[index2];		
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else{
				COimageX[COindex]=COimageX[COindex]+dx;
				X1[index1]=X1[index1]+dx;
				X2[index2]=X2[index2]+dx;
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=TBimageX[i]+dx;
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=MOimageX[i]+dx;
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}	
		}else if(y+(COimageH[COindex]/2)>boundS){
			COimageY[COindex]=boundS-COimageH[COindex];
			if(Y1[index1]>Y2[index2]){
				Y1[index1]=boundS-H1[index1];
				Y2[index2]=boundS-COimageH[COindex];
			}else{
				Y1[index1]=boundS-COimageH[COindex];
				Y2[index2]=boundS-H1[index2];
			}	
			if (x-(COimageW[COindex]/2)<0){
				COimageX[COindex]=0;								
				if(X1[index1]>X2[index2]){
					X1[index1]=0+COimageW[COindex]-W1[index1];
					X2[index2]=0;
				}else{
					X1[index1]=0;
					X2[index2]=0+COimageW[COindex]-W2[index2];			
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else if(x+(COimageW[COindex]/2)>boundE){
				COimageX[COindex]=boundE-COimageW[COindex];								
				if(X1[index1]>X2[index2]){
					X1[index1]=boundE-W1[index1];
					X2[index2]=boundE-COimageW[COindex];
				}else{
					X1[index1]=boundE-COimageW[COindex];
					X2[index2]=boundE-W2[index2];		
				}
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=COimageX[COindex]+tbx[i];
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=COimageX[COindex]+mox[i];
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}else{
				COimageX[COindex]=COimageX[COindex]+dx;
				X1[index1]=X1[index1]+dx;
				X2[index2]=X2[index2]+dx;
				for (int i=1; i<=TBcount; i++){
					if(TBMO1==1&i==index1){continue;}
					if(TBMO2==1&i==index2){continue;}
					if(TBco[i][1]==COindex){
						TBimageX[i]=TBimageX[i]+dx;
						TBimageY[i]=COimageY[COindex]+tby[i];
						TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
					}
				}
				for (int i=1; i<=MOcount; i++){
					if(TBMO1==2&i==index1){continue;}
					if(TBMO2==2&i==index2){continue;}
					if(MOco[i][1]==COindex){
						MOimageX[i]=MOimageX[i]+dx;
						MOimageY[i]=COimageY[COindex]+moy[i];
						MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
					}
				}
			}
		}else{
			COimageX[COindex]=COimageX[COindex]+dx;
			COimageY[COindex]=COimageY[COindex]+dy;
			X1[index1]=X1[index1]+dx;
			X2[index2]=X2[index2]+dx;
			Y1[index1]=Y1[index1]+dy;
			Y2[index2]=Y2[index2]+dy;
			for (int i=1; i<=TBcount; i++){
				if(TBMO1==1&i==index1){continue;}
				if(TBMO2==1&i==index2){continue;}
				if(TBco[i][1]==COindex){
					TBimageX[i]=TBimageX[i]+dx;
					TBimageY[i]=TBimageY[i]+dy;
					TBBound[i]= new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
					TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
				}
			}
			for (int i=1; i<=MOcount; i++){
				if(TBMO1==2&i==index1){continue;}
				if(TBMO2==2&i==index2){continue;}
				if(MOco[i][1]==COindex){
					MOimageX[i]=MOimageX[i]+dx;
					MOimageY[i]=MOimageY[i]+dy;
					MOBound[i]= new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
					MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH[i]-panelHeigth+15);
				}
			}
		}
		Bound1[index1]= new Rectangle(X1[index1], Y1[index1], W1[index1], H1[index1]);
		Bound2[index2]= new Rectangle(X2[index2], Y2[index2], W2[index2], H2[index2]);
		name1[index1].setLocation(X1[index1], Y1[index1]+H1[index1]-panelHeigth+15);
		name2[index2].setLocation(X2[index2], Y2[index2]+H2[index2]-panelHeigth+15);
		COBound[COindex]=new Rectangle(COimageX[COindex], COimageY[COindex], COimageW[COindex], COimageH[COindex]);	
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
	
	public void DrawDashedLine(Graphics g, int x1, int y1, int x2, int y2){
		
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        //set the stroke of the copy, not the original 
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10,6,3,6}, 0);
        g2d.setStroke(dashed);
        g2d.setColor(lightred);
        g2d.drawLine(x1, y1, x2, y2);
        //gets rid of the copy
        
        g2d.dispose();
	}
	public void DrawSolidLine(Graphics g, int x1, int y1, int x2, int y2){
        Graphics2D g2d = (Graphics2D) g.create();
        Stroke solid = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10,0}, 0);
        g2d.setStroke(solid);

		g2d.setColor(dlightred);
        g2d.drawLine(x1, y1, x2, y2);

        g2d.dispose();
	}

	public void DrawImageandLine(int count, int[] delete, int[][] co, Graphics g, Image[] image, int[] X, int[] Y, int[] H, int[] W){
		 if (count>0){	    	 
	    	 for (int i=1; i<=count; i++){
	    		 if(delete[i]==-1){
	    			 continue;
	    		 }else if(co[i][2]==1){
	    		     for(int j=1; j<=TBcount; j++){
	    		    	 if(TBdelete[j]==-1|TBco[j][0]==-1){
	    		    		 continue;
	    		    	 }else if( !(co[i][0]==2&TBco[j][0]==2) &
	    		    			 	TBco[j][2]==1 &
	    		    			 	(Math.abs(X[i]-TBimageX[j])<100) & (Math.abs(Y[i]-TBimageY[j])<100) &
	    		    			 	!(TBco[j][1]!=co[i][1])
	    		    			 	){ 
	    		    		 DrawDashedLine(g, X[i]+(W[i]/2), Y[i]+(H[i]/2),
		    				 		 TBimageX[j]+(TBimageW[j]/2), TBimageY[j]+(TBimageH[j]/2));    	
    		 	
    		    			 DrawSolidLine(g, X[i]+(W[i]/2), Y[i]+(H[i]/2),
		    				 		 TBimageX[j]+(TBimageW[j]/2), TBimageY[j]+(TBimageH[j]/2));  	
	    		    	 }	    			    		 		    	 
	    		     } 
	    		     for(int j=1; j<=MOcount; j++){
	    		    	 if(MOdelete[j]==-1|MOco[j][0]==-1){
	    		    		 continue;
	    		    	 }else if( !(co[i][0]==2&MOco[j][0]==2) &
	    		    			 	MOco[j][2]==1 &
	    		    			 	(Math.abs(X[i]-MOimageX[j])<100) & (Math.abs(Y[i]-MOimageY[j])<100) &
	    		    			 	!(MOco[j][1]!=co[i][1])
	    		    			 	){ 
	    		    		 DrawDashedLine(g, X[i]+(W[i]/2), Y[i]+(H[i]/2),
 		    					 	MOimageX[j]+(MOimageW[j]/2), MOimageY[j]+(MOimageH[j]/2));  	
	    		    		 DrawSolidLine(g, X[i]+(W[i]/2), Y[i]+(H[i]/2),
 		    				 		MOimageX[j]+(MOimageW[j]/2), MOimageY[j]+(MOimageH[j]/2));
	    		    	 }
	    		     } 
	    		 }
	    	 }
		 }	
	}
	
	public void setPreference(Preferences pre){
		pre.putInt("TBcount", TBcount);
		pre.putInt("MOcount", MOcount);
		pre.putInt("COcount", COcount);
		for (int i=1; i<=TBcount; i++){
			String TBnametemp= TBname[i].getText();
			pre.put("TBname"+i, TBnametemp);
			pre.put("TBfile"+i, TBfile[i]);
			pre.putInt("TBimageX"+i, TBimageX[i]);
			pre.putInt("TBimageY"+i, TBimageY[i]);
			TBname[i].setVisible(true);
			pre.putInt("TBimageH"+i, TBimageH[i]);
			pre.putInt("TBimageW"+i, TBimageW[i]);
			pre.putInt("TBdelte"+i, TBdelete[i]);
			pre.putInt("TBvalue"+i, TBvalue[i]);
			for (int j=0; j<=2; j++){
				pre.putInt("TBco"+i+j, TBco[i][j]);
			}					
		}
		for (int i=1; i<=MOcount; i++){
			String MOnametemp= MOname[i].getText();
			pre.put("MOname"+i, MOnametemp);
			pre.putInt("MOimageX"+i, MOimageX[i]);
			pre.putInt("MOimageY"+i, MOimageY[i]);
			pre.putInt("MOimageH"+i, MOimageH[i]);
			pre.putInt("MOimageW"+i, MOimageW[i]);
			pre.putInt("MOdelte"+i, MOdelete[i]);
			for (int j=0; j<=2; j++){
				pre.putInt("MOco"+i+j, MOco[i][j]);
			}					
		}
		for (int i=0; i<=COcount-1; i++){
			pre.putInt("COimageX"+i, COimageX[i]);
			pre.putInt("COimageY"+i, COimageY[i]);
			pre.putInt("COimageH"+i, COimageH[i]);
			pre.putInt("COimageW"+i, COimageW[i]);
			for (int j=0; j<=3; j++){
				pre.putInt("COco"+i+j, COco[i][j]);
			}	
		}
	}
	
	public void getPreference(Preferences pre){
		TBcount= pre.getInt("TBcount", 0);
		MOcount= pre.getInt("MOcount", 0);
		COcount= pre.getInt("COcount", 0);
		//problem here
		for (int i=1; i<=TBMAX-1; i++){
			TBfile[i]= pre.get("TBfile"+i, "");
			TBimageX[i]= pre.getInt("TBimageX"+i, -100);
			TBimageY[i]= pre.getInt("TBimageY"+i, -100);
			TBimageH[i]= pre.getInt("TBimageH"+i, 0);
			TBimageW[i]= pre.getInt("TBimageW"+i, 0);
			TBdelete[i]= pre.getInt("TBdelete"+i, 0);
			TBvalue[i]= pre.getInt("TBvalue"+i, 0);		
			
			for (int j=0; j<=2; j++){
				if(j==2){
					TBco[i][j]=pre.getInt("TBco"+i+j, 0);
				}else{TBco[i][j]=pre.getInt("TBco"+i+j, -1);}
			}	
			TB[i]=TBimage;
			if(TBimageH[i]==0|TBimageW[i]==0){
				TBimageH[i]=TB[i].getHeight(null);
				TBimageW[i]=TB[i].getWidth(null);
			}
			TBname[i].setText(pre.get("TBname"+i, ""));
			TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-panelHeigth+15);
			TBname[i].setSize(200,15);
			if(!TBfile[i].isEmpty()){
				iconchange(i);	
			}
			TBBound[i]=new Rectangle(TBimageX[i], TBimageY[i], TB[i].getWidth(null), TB[i].getHeight(null));
		}
		for (int i=1; i<=MOMAX-1; i++){
			MOname[i].setText(pre.get("MOname"+i, ""));
			MOimageX[i]= pre.getInt("MOimageX"+i, -100);
			MOimageY[i]= pre.getInt("MOimageY"+i, -100);
			MOimageH[i]= pre.getInt("MOimageH"+i, 0);
			MOimageW[i]= pre.getInt("MOimageW"+i, 0);
			MOdelete[i]= pre.getInt("MOdelete"+i, 0);		
			for (int j=0; j<=2; j++){
				if(j==2){
					MOco[i][j]=pre.getInt("MOco"+i+j, 0);
				}else{MOco[i][j]=pre.getInt("MOco"+i+j, -1);}
			}	
			MO[i]=MOimage;
			if(MOimageH[i]==0|MOimageW[i]==0){
				MOimageH[i]=MO[i].getHeight(null);
				MOimageW[i]=MO[i].getWidth(null);
			}
			MOBound[i]=new Rectangle(MOimageX[i], MOimageY[i], MO[i].getWidth(null), MO[i].getHeight(null));
		}
		for (int i=0; i<=COMAX-1; i++){
			COimageX[i]= pre.getInt("COimageX"+i, -100);
			COimageY[i]= pre.getInt("COimageY"+i, -100);
			COimageH[i]= pre.getInt("COimageH"+i, 0);
			COimageW[i]= pre.getInt("COimageW"+i, 0);
			for (int j=0; j<=3; j++){
				COco[i][j]= pre.getInt("COco"+i+j, 0);
			}	
			COBound[i]=new Rectangle(COimageX[i], COimageY[i], COimageW[i], COimageH[i]);
		}
		repaint();
	}
	
	public void removePreference(Preferences pre){
		int TBc=TBcount;
		int MOc=MOcount;
		int COc=COcount;
		pre.remove("TBcount");
		pre.remove("MOcount");
		pre.remove("COcount");
		for (int i=1; i<=TBc; i++){
			System.out.println("TBremove "+i);
			pre.remove("TBname"+i);
			pre.remove("TBfile"+i);
			System.out.println("TBimageX "+i+pre.getInt("TBimageX"+i,0));
			pre.remove("TBimageX"+i);
			pre.remove("TBimageY"+i);
			pre.remove("TBimageH"+i);
			pre.remove("TBimageW"+i);
			pre.remove("TBdelte"+i);
			pre.remove("TBvalue"+i);
			for (int j=0; j<=2; j++){
				pre.remove("TBco"+i+j);
			}				
			System.out.println("TBimageX "+i+pre.getInt("TBimageX"+i,0));
		}
		for (int i=1; i<=MOc; i++){
			pre.remove("MOname"+i);
			pre.remove("MOimageX"+i);
			pre.remove("MOimageY"+i);
			pre.remove("MOimageH"+i);
			pre.remove("MOimageW"+i);
			pre.remove("MOdelte"+i);
			for (int j=0; j<=2; j++){
				pre.remove("MOco"+i+j);
			}					
		}
		for (int i=0; i<=COc-1; i++){
			pre.remove("COimageX"+i);
			pre.remove("COimageY"+i);
			pre.remove("COimageH"+i);
			pre.remove("COimageW"+i);
			for (int j=0; j<=3; j++){
				pre.remove("COco"+i+j);
			}	
		}
		repaint();
	}
	
	/*
	static Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon)icon).getImage();
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }
    */
	
	/*
	final Timer fade = new Timer(40, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (startTime < 0) {
                 startTime = System.currentTimeMillis();
             } else {
                 long time = System.currentTimeMillis();
                 long duration = time - startTime;
                 if (duration >= RT) {
                     startTime = -1;
                     ((Timer) e.getSource()).stop();
                     alpha = 1f;
                 } else {
                     alpha = 1f - ((float) duration / (float) RT);
                 }
                 repaint();
             }
         }
     });
	*/
	

}

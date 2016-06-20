package main;
//import org.rosuda.JRI.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
	static int Wide=1000;
	static JLayeredPane startPanel;
	static JPanel nullPanel;
	static JPanel layoutPanel;
	
	public static void main(String[] args){    	
		JFrame main = new JFrame();
		JPanel ipat = new myPanel(Wide, layoutPanel, startPanel, nullPanel);
		main.setTitle("iPat");	
		main.setLocation(200,0); 
		main.setLayout(new BorderLayout());
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();
		cPane.add(ipat);	
		main.pack();
		main.setVisible(true);
		main.addComponentListener(new ComponentAdapter() {
			@Override
	        public void componentResized(ComponentEvent evt) {
				System.out.println("componentResized");
	            Component c = (Component)evt.getSource();
	            System.out.println("H: "+c.getHeight()+" W: "+c.getWidth()); 
	            Wide=main.getWidth();
	            main.setSize(Wide, 700);
	            startPanel.resize(Wide,200);
	            //(new Dimension(Wide, 200));
	            //http://stackoverflow.com/questions/6666637/resize-jpanel-from-jframe
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
	            System.out.println("Wide: "+Wide);
	        }	
		});	
	}	
}

class myPanel extends JPanel implements MouseMotionListener{	
	private static final int TBMAX=999;
	private static final int MOMAX=999;
	private static final int COMAX=100;	
	
	int[] TBimageX= new int[TBMAX];
	int[] TBimageY= new int[TBMAX];
	int[] TBimageH= new int[TBMAX];
	int[] TBimageW= new int[TBMAX];
	int[][] TBco= new int[TBMAX][3];  //1=combined or not(-1,1,2), 2= coindex, 3=subcombined or not (-1,1)
	int[] TBdelete=new int[TBMAX];
	
	int[] MOimageX= new int[MOMAX];
	int[] MOimageY= new int[MOMAX];
	int[] MOimageH= new int[MOMAX];
	int[] MOimageW= new int[MOMAX];
	int[][] MOco= new int[MOMAX][3];
	int[] MOdelete=new int[MOMAX];
	
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
	Image Excel, Powerpoint, Word, Music, Video, Unknown, 
		  TBimage, MOimage;
	
	JPanel mainstartPanel;
	JLayeredPane startPanel;
	JPanel mainPanel;	
	JPanel nullPanel;
	JPanel buttonPanel;
	JPanel layoutPanel;
	
	JButton TBButton = new JButton();
	JButton MOButton = new JButton();
	JLabel iPat = new JLabel();

	JFileChooser[] TBchooser= new JFileChooser[TBMAX];
	String[] TBfile= new String[TBMAX];
	int[] TBvalue= new int[TBMAX];
	
	JLabel[] TBname= new JLabel[TBMAX];	
	JLabel[] MOname= new JLabel[MOMAX];		

	//animation
	Timer timer, TrashAnimation, DashLineAnimation;
	Boolean TA;
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
	int Wide;
	
	public myPanel(int Wideint, JPanel l, JLayeredPane s, JPanel n){	
		this.Wide=Wideint;
		this.layoutPanel=l;
		this.startPanel=s;
		this.nullPanel=n;
		
		this.setBackground(Color.white);
		try{
			Image iconIP = ImageIO.read(getClass().getResource("iPat.png"));
			iPat.setIcon(new ImageIcon(iconIP));
		} catch (IOException ex){}
		try{
			for(int i=0; i<10; i++){
				Trash[i] = ImageIO.read(getClass().getResource("trash"+i+".png"));
			}
		} catch (IOException ex){}
		
		try{
			Excel = ImageIO.read(this.getClass().getResourceAsStream("Excel.png"));
		} catch (IOException ex){}
		try{
			Powerpoint = ImageIO.read(this.getClass().getResourceAsStream("Powerpoint.png"));
		} catch (IOException ex){}
		try{
			Word = ImageIO.read(this.getClass().getResourceAsStream("Word.png"));
		} catch (IOException ex){}
		try{
			Video = ImageIO.read(this.getClass().getResourceAsStream("Video.png"));
		} catch (IOException ex){}
		try{
			Music = ImageIO.read(this.getClass().getResourceAsStream("Music.png"));
		} catch (IOException ex){}
		try{
			Unknown = ImageIO.read(this.getClass().getResourceAsStream("Unknown.png"));
		} catch (IOException ex){}		
		
		try{
			TBimage = ImageIO.read(this.getClass().getResourceAsStream("Table.png"));
			TBButton.setIcon(new ImageIcon(TBimage));
		} catch (IOException ex){}
		try{
			MOimage = ImageIO.read(this.getClass().getResourceAsStream("Model.png"));
			MOButton.setIcon(new ImageIcon(MOimage));
		} catch (IOException ex){}			
		for (int i=1; i<=TBMAX-1; i++){
			TB[i] = TBimage;
		}
		for (int i=1; i<=MOMAX-1; i++){
			MO[i] = MOimage;
		}
		
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
		
		////////////
		////////////
		////////////
		//LAYOUT.START
		////////////
		////////////
		////////////		
		TBButton.setOpaque(false);
		TBButton.setContentAreaFilled(false);
		TBButton.setBorderPainted(false);
		MOButton.setOpaque(false);
		MOButton.setContentAreaFilled(false);
		MOButton.setBorderPainted(false);
		iPat.setOpaque(false);
				
		startPanel = new JLayeredPane();
		layoutPanel = new JPanel(new MigLayout(" fillx", "[grow]","[][]"));	
		nullPanel= new JPanel();
		
		layoutPanel.add(iPat,"wrap, span 2, alignx c");
		layoutPanel.add(TBButton,"grow, alignx r");
		layoutPanel.add(MOButton,"grow, alignx l");
		
		
		startPanel.setPreferredSize(new Dimension(Wide, 200));
		trashl = new JLabel(new ImageIcon());
		startPanel.add(trashl, new Integer(1));
		startPanel.add(layoutPanel,  new Integer(3));
		trashl.setBounds(new Rectangle(-1000, -50, Wide, 300));
		trashl.setVisible(true);
		layoutPanel.setBounds(new Rectangle(0, 0, Wide,200));
		layoutPanel.setVisible(true);
		
		nullPanel.setLayout(null);
		nullPanel.setPreferredSize(new Dimension(Wide, 500));
		
		this.setLayout(new MigLayout("fillx","[grow]","[grow]"));
		this.add(startPanel," dock north");
		this.add(nullPanel,"grow"); 
	
		
		
		startPanel.setOpaque(false);
		layoutPanel.setOpaque(false);
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
	
		timer = new Timer(10, new ActionListener() {
			int ant=10;
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		    	double d=Math.pow(ant*50, .3);
		    	int dd= (int)d;
				TBimageY[TBcount]=TBimageY[TBcount]+(dd);
		        repaint();
		        ant--;
		        if(ant==0){
		        	timer.stop();
		        	TBBound[TBcount]=new Rectangle(TBimageX[TBcount], TBimageY[TBcount],TB[TBcount].getWidth(null), TB[TBcount].getHeight(null));
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
    				pos= ((TBcount-1)%63);
    				TBimageX[TBcount]=TBposX[pos];
    				TBimageY[TBcount]=TBposY[pos];					
       				timer.start();	
       				/* 
    				TBimageX[TBcount]=250;
    				TBimageY[TBcount]=120;	
		        	TBBound[TBcount]=new Rectangle(TBimageX[TBcount], TBimageY[TBcount],TB[TBcount].getWidth(null), TB[TBcount].getHeight(null));
		        	repaint();*/
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
    				MOimageX[MOcount]=MOimageX_int;
    				MOimageY[MOcount]=MOimageY_int+100*(MOcount-1);		
    				MOBound[MOcount]=new Rectangle(MOimageX[MOcount], MOimageY[MOcount],MO[MOcount].getWidth(null), MO[MOcount].getHeight(null));
    				repaint();
    			}		
    		}
    	}); 
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent ee){
				System.out.println(COco[COcount][0]);
				int move_x=ee.getX();
    			int move_y=ee.getY();
    			COindex=-1;
				TBindex=0;
    			MOindex=0;
    			
    			if (TBcount>0){			
    				for (int i=1; i<=TBcount;i++){
    					if (TBBound[i].contains(move_x, move_y)){
    						TBindex=i;
    						System.out.println("this is "+ i);					
    						if (SwingUtilities.isRightMouseButton(ee)){
    							 TBvalue[i]= TBchooser[i].showOpenDialog(null);
    	    					 if (TBvalue[i] == JFileChooser.APPROVE_OPTION){
    	    					    File selectedfile = TBchooser[i].getSelectedFile();  	    					    
    	    					  	TBfile[i]= selectedfile.getAbsolutePath();
    	    					  	iconchange(i); 
    	    					  	TBimageH[TBindex]=TB[i].getHeight(null);
    	    					  	TBimageW[TBindex]=TB[i].getHeight(null);
    	    						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH[i]-195);
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
			}	
				
			@Override
			public void mouseReleased(MouseEvent ee){	
				int y=ee.getY();		
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
    				trashl.setBounds(new Rectangle(-1000, -50, 550, 300));  				
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
					/*
					int X1=0, Y1=0, W1=0, H1=0, X2=0, Y2=0, W2=0, H2=0, cindex=0;
					if (TBindex!=0){
						cindex= TBco[TBindex][1] ;
						X1=TBimageX[TBindex]; Y1=TBimageY[TBindex];
						W1=TBimageW[TBindex]; H1=TBimageH[TBindex];	
					}else if(MOindex!=0){
						cindex= MOco[MOindex][1] ;
						X1=MOimageX[MOindex]; Y1=MOimageY[MOindex];
						W1=MOimageW[MOindex]; H1=MOimageH[MOindex];
					}
					X2=COimageX[cindex]; Y2=COimageY[cindex];
					W2=COimageW[cindex]; H2=COimageH[cindex];
					
					COimageX[cindex]=Math.min(X1, X2);
					COimageY[cindex]=Math.min(Y1, Y2);
					if (X2>=X1){
						COimageW[cindex]=X2+W2-X1;
					}else{
						COimageW[cindex]=X1+W1-X2;
					}
					if (Y2>=Y1){
						COimageH[cindex]=Y2+H2-Y1;
					}else{
						COimageH[cindex]=Y1+H1-Y2;
					}
					COBound[cindex]=new Rectangle(COimageX[cindex], COimageY[cindex], COimageW[cindex], COimageH[cindex]);
					
					
					linex[0]=0;
					linex[1]=0;
					liney[0]=0;
					liney[1]=0;
					repaint();
					*/
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
    			if (evt.getClickCount() >= 2) {
    				for (int i=1; i<=TBcount; i++){
    					if (TBBound[i].contains(x,y)){
        					TBopenfile(i);
    					}
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
	     if (COcount>0){
	    	 for (int i=0; i<COcount; i++){    		 
	    		 g.fillOval((int)(COimageX[i]-25+COimageW[i]*.5), (int)(COimageY[i]-25 +COimageH[i]*.5),
	    				 	50, 50);
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
		int boundS=680;
		int boundE=Wide;
		System.out.println("COindex= "+COindex);
		if(TBindex!=0){
			CombinedorNot(TBindex, TBimageX, TBimageY, TBimageW, TBimageH, 1);
			KeepInPanel (imX, imY, TBindex, TBimageW, TBimageH, TBimageX, TBimageY,
						 boundN,  boundS,  boundE, TBBound);
		 	TBname[TBindex].setLocation(TBimageX[TBindex],TBimageY[TBindex]+TBimageH[TBindex]-195);
			TBBound[TBindex]=new Rectangle(TBimageX[TBindex], TBimageY[TBindex], TB[TBindex].getWidth(null), TB[TBindex].getHeight(null));
			repaint();
			
		}else if(MOindex!=0){
			CombinedorNot(MOindex, MOimageX, MOimageY, MOimageW, MOimageH, 2);
			KeepInPanel (imX, imY, MOindex, MOimageW, MOimageH, MOimageX, MOimageY,
						 boundN,  boundS,  boundE, MOBound);
			MOname[MOindex].setLocation(MOimageX[MOindex],MOimageY[MOindex]+MOimageH[MOindex]-195);
			MOBound[MOindex]=new Rectangle(MOimageX[MOindex], MOimageY[MOindex], MO[MOindex].getWidth(null), MO[MOindex].getHeight(null));
			repaint();
		}
		
		if(COindex!=-1){		
			int dx= imX- (COimageX[COindex]+(COimageW[COindex]/2));
			int dy= imY- (COimageY[COindex]+(COimageH[COindex]/2));
			System.out.println("COindex= "+COindex);	
			COimageX[COindex]=COimageX[COindex]+dx;					
			COimageY[COindex]=COimageY[COindex]+dy;
			COBound[COindex]=new Rectangle(COimageX[COindex], COimageY[COindex], COimageW[COindex], COimageH[COindex]);
			if(COco[COindex][0]==1){
				if(COco[COindex][1]==1){
					dragcombined(dx, dy, COco[COindex][2], TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
					dragcombined(dx, dy, COco[COindex][3], TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
					for (int i=1; i<=TBcount; i++){
						if(i==COco[COindex][2]|i==COco[COindex][3]){continue;}
						if(TBco[i][1]==COindex){
							dragcombined(dx, dy, i, TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
						}
					}
					for (int i=1; i<=MOcount; i++){
						if(MOco[i][1]==COindex){
							dragcombined(dx, dy, i, MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
						}
					}
				}else if(COco[COindex][1]==2){
					dragcombined(dx, dy, COco[COindex][2], TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
					dragcombined(dx, dy, COco[COindex][3], MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
					for (int i=1; i<=TBcount; i++){
						if(i==COco[COindex][2]){continue;}
						if(TBco[i][1]==COindex){
							dragcombined(dx, dy, i, TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
						}
					}
					for (int i=1; i<=MOcount; i++){
						if(i==COco[COindex][3]){continue;}
						if(MOco[i][1]==COindex){
							dragcombined(dx, dy, i, MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
						}
					}
				}		
			}else if (COco[COindex][0]==2){
				if(COco[COindex][1]==1){	
					dragcombined(dx, dy, COco[COindex][2], MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
					dragcombined(dx, dy, COco[COindex][3], TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
					for (int i=1; i<=MOcount; i++){
						if(i==COco[COindex][2]){continue;}
						if(MOco[i][1]==COindex){
							dragcombined(dx, dy, i, MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
						}
					}
					for (int i=1; i<=TBcount; i++){
						if(i==COco[COindex][3]){continue;}
						if(TBco[i][1]==COindex){
							dragcombined(dx, dy, i, TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
						}
					}
					
				}else if(COco[COindex][1]==2){
					dragcombined(dx, dy, COco[COindex][2], MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
					dragcombined(dx, dy, COco[COindex][3], MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
					for (int i=1; i<=MOcount; i++){
						if(i==COco[COindex][2]|i==COco[COindex][3]){continue;}
						if(MOco[i][1]==COindex){
							dragcombined(dx, dy, i, MOimageX, MOimageY, MOimageW, MOimageH, MO, MOBound, MOname);
						}
					}
					for (int i=1; i<=TBcount; i++){
						if(TBco[i][1]==COindex){
							dragcombined(dx, dy, i, TBimageX, TBimageY, TBimageW, TBimageH, TB, TBBound, TBname);
						}
					}
				}
			}
			repaint();
		}	
	
		TrashAnimation = new Timer(20, new ActionListener() {
			int i=0;
			Boolean quit=true;
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		    	if(i<10&TA){
		    		System.out.println("x: "+imX +" y: "+ imY);
		    		trashl.setBounds(new Rectangle(0, -50, 550, 300));
					trashl.setIcon(new ImageIcon(Trash[i]));
					startPanel.setLayer(trashl, new Integer(200));
					trashl.setVisible(true);
					startPanel.revalidate();    	
			        repaint();
			        i++;
		    	}else{
		    		TrashAnimation.stop();
		    		i=0;
		    		TA=false;
		    	}
		    }
		});		
		if ((TBindex!=0|MOindex!=0|COindex!=-1)&&imY<=(delbbound)&&!removeornot){
			TA=true;
			TrashAnimation.start();
			removeornot=true;		
		}else if(imY>delbbound){
			trashl.setBounds(new Rectangle(-1000, -50, 550, 300));
			startPanel.setLayer(trashl, new Integer(1));
			trashl.setVisible(true);
			removeornot=false;				
		}		

	}

	
	@Override
	public void mouseMoved(MouseEvent ev) {
		int y= ev.getY();
		int x= ev.getX();
		if(y>delbbound){
			trashl.setBounds(new Rectangle(-1000, -50, 550, 300));
			startPanel.setLayer(trashl, new Integer(1));
			trashl.setVisible(true);
			removeornot=false;				
		}	
		/*
		System.out.println("("+x+", "+y+")");
		*/
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
						System.out.println("link to M individual");
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
		name[index].setLocation(X[index], Y[index]+H[index]-195);
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
}
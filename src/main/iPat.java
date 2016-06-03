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

	public static void main(String[] args){    	
		JFrame main = new JFrame();
		main.setTitle("iPat");	
		main.setSize(550,800);
		main.setLocation(200,0); 
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();
		cPane.add(new myPanel());	
		main.setVisible(true);
		System.out.println(cPane.getWidth());
	}
	
}

class myPanel extends JPanel implements MouseMotionListener{	
	private static final int TBMAX=50;
	private static final int MOMAX=50;

	int[] TBimageX= new int[TBMAX];
	int[] TBimageY= new int[TBMAX];
	int TBimageX_int=50;
	int TBimageY_int=200;
	int[] MOimageX= new int[MOMAX];
	int[] MOimageY= new int[MOMAX];
	int MOimageX_int=430;
	int MOimageY_int=200;
	
	int TBimageH, TBimageW,
		MOimageH, MOimageW;
	
	Image[] TB= new Image[TBMAX];
	Image[] MO= new Image[MOMAX];
	Image csv, TBimage;
	
	Rectangle[] TBBound= new Rectangle[TBMAX];
	Rectangle[] MOBound= new Rectangle[MOMAX];
	
	int TBindex =0;
	int MOindex =0;
	
	int TBcount =0;
	int MOcount =0;
	
	JPanel startPanel;
	JPanel mainPanel;	
	JPanel nullPanel;
	JPanel buttonPanel;
	
	JButton TBButton = new JButton();
	JButton MOButton = new JButton();
	JLabel iPat = new JLabel();

	JFileChooser[] TBchooser= new JFileChooser[TBMAX];
	
	String[] TBfile= new String[TBMAX];
		
	int[] TBvalue= new int[TBMAX];
	
	JLabel[] TBname= new JLabel[TBMAX];	
	JLabel[] MOname= new JLabel[MOMAX];		

	Timer timer;
	int panH;
	int panW;

	Timer fade;
	public static final long RT=100;
	private float alpha=0f;
	private long startTime=-1;
	
	public myPanel(){	
	
		try{
			Image iconIP = ImageIO.read(getClass().getResource("iPat.png"));
			iPat.setIcon(new ImageIcon(iconIP));
		} catch (IOException ex){}
		try{
			csv = ImageIO.read(this.getClass().getResourceAsStream("CSV.png"));
		} catch (IOException ex){System.out.println("file not found!");}
				
		try{
			TBimage = ImageIO.read(this.getClass().getResourceAsStream("Table.png"));
			TBButton.setIcon(new ImageIcon(TBimage));
		} catch (IOException ex){}	
		for (int i=1; i<=TBMAX-1; i++){
				TB[i] = TBimage;
		}
		
		try{
			Image iconPS = ImageIO.read(this.getClass().getResourceAsStream("Model.png"));
			MOButton.setIcon(new ImageIcon(iconPS));
		} catch (IOException ex){}	
		for (int i=1; i<=MOMAX-1; i++){
			try{
				MO[i] = ImageIO.read(this.getClass().getResourceAsStream("Model.png"));
			} catch (IOException ex){System.out.println("file not found!");}
		}

		TBButton.setOpaque(false);
		TBButton.setContentAreaFilled(false);
		TBButton.setBorderPainted(false);
		MOButton.setOpaque(false);
		MOButton.setContentAreaFilled(false);
		MOButton.setBorderPainted(false);
		iPat.setOpaque(false);
		
		TBimageH=TB[1].getHeight(null);	
		TBimageW=TB[1].getWidth(null);	
		MOimageH=MO[1].getHeight(null);	
		MOimageW=MO[1].getWidth(null);
		
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
			
		startPanel = new JPanel(new MigLayout("debug, fillx", "[]","[][]"));	
		//buttonPanel = new JPanel(new MigLayout("debug, fillx","[][]","[]"));
		//mainPanel= new JPanel(new MigLayout("debug, fillx","[]","[][]"));	
		nullPanel= new JPanel();

		startPanel.add(iPat,"wrap, span 2, alignx c");
		startPanel.add(TBButton,"alignx r");
		startPanel.add(MOButton,"alignx l");

		/*
		buttonPanel.add(TBButton,"alignx r");
		buttonPanel.add(MOButton,"alignx l");
		
		mainPanel.add(startPanel, "alignx c, grow, wrap");
		mainPanel.add(nullPanel, "alignx c, grow");
		*/
		
		this.setLayout(new MigLayout("debug, fillx","[grow]","[][grow]"));
		this.add(startPanel,"grow, wrap");
		this.add(nullPanel,"grow"); 

		nullPanel.setLayout(null);
		
		startPanel.setOpaque(false);
		nullPanel.setOpaque(false);
		
		
		for (int i=1; i<=TBMAX-1; i++){
			TBchooser[i]= new JFileChooser();
			TBfile[i]= new String();
			TBname[i]= new JLabel();
			nullPanel.add(TBname[i]);
		}	
		for (int i=1; i<=MOMAX-1; i++){
			MOname[i]= new JLabel();		
			nullPanel.add(MOname[i]);
		}			
		
		startPanel.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseEntered(MouseEvent evt) {
    			System.out.println("enter");
    			if (TBindex!=0){
        			fade.start();	
    			}
    		}
    		@Override
    		public void mouseExited(MouseEvent evt) {
    			System.out.println("out");
    		}
		});
		
		addMouseMotionListener(this);
		
		
		
		
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
    				TBimageX[TBcount]=TBimageX_int;
    				TBimageY[TBcount]=TBimageY_int-40+100*(TBcount-1);					
       				timer.start();	   			
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
				
				int move_x=ee.getX();
    			int move_y=ee.getY();		
    			
    			if (TBcount>0){			
    				for (int i=1; i<=TBcount;i++){
    					if (TBBound[i].contains(move_x, move_y)){
    						TBindex=i;
    						System.out.println(i);					
    						if (SwingUtilities.isRightMouseButton(ee)){
    							System.out.println("right");
    							 TBvalue[i]= TBchooser[i].showOpenDialog(null);
    	    					 if (TBvalue[i] == JFileChooser.APPROVE_OPTION){
    	    					    File selectedfile = TBchooser[i].getSelectedFile();  	    					    
    	    					  	TBfile[i]= selectedfile.getAbsolutePath();
    	    					  	iconchange(i); 
    	    						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH-200);
    	    						TBname[i].setSize(200,15);
    	    						TBname[i].setText(selectedfile.getName());
    	    						TBindex=0;
    	    						repaint();
    	    					 }
    					   	};
    					}
    				}
    			}     	
    			if (MOcount>0&&TBindex==0){			
    				for (int i=1; i<=MOcount;i++){
    					if (MOBound[i].contains(move_x, move_y)){
    						MOindex=i;
    						System.out.println(i);		
    					}
    				}
    			} 
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
			@Override
			public void mouseReleased(MouseEvent ee){
				TBindex=0;
				MOindex=0;
			}
			
		});				
	}	
	
	public void iconchange(int i){
	  	if 		(TBfile[i].indexOf(".csv")>=0){TB[i]=csv;}
	  	else if	(TBfile[i].indexOf(".xls")>=0){TB[i]=csv;}
	  	else if	(TBfile[i].indexOf(".xlt")>=0){TB[i]=csv;}
	  	else if	(TBfile[i].indexOf(".xlm")>=0){TB[i]=csv;}
	  	else if	(TBfile[i].indexOf(".xlsx")>=0){TB[i]=csv;}
	  	else if	(TBfile[i].indexOf(".xlsm")>=0){TB[i]=csv;}
	  	else if	(TBfile[i].indexOf(".xltx")>=0){TB[i]=csv;}
	  	else{TB[i]=TBimage;}	  	
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	     super.paintComponent(g);
		// System.out.println("use paint");
	     if (TBcount>0){	    	 
	    	 for (int i=1; i<=TBcount; i++){
			     g.drawImage(TB[i], TBimageX[i], TBimageY[i], this);
	    	 }
	     }	   
	     if (MOcount>0){	    	 
	    	 for (int i=1; i<=MOcount; i++){
			     g.drawImage(MO[i], MOimageX[i], MOimageY[i], this);	
	    	 }
	     }
	     Graphics2D g2d= (Graphics2D) g.create();
	     g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
	     g2d.drawImage(csv, 300, 100, this);
	     g2d.dispose();
	}
	
	@Override
	public void mouseMoved(MouseEvent ev) {
		int x= ev.getX();
		int y= ev.getY();
		//System.out.println("("+x+", "+y+")");
	}
	
	@Override
	 public void mouseDragged(MouseEvent e) {
		int imX = e.getX();
		int imY = e.getY();
		int boundN=0; //205
		int boundS=800;
		int boundE=550;
		if (TBindex !=0){		
			if(imX<(0+(TBimageW/2))){
				TBimageX[TBindex]=0;
				if(imY<(boundN+(TBimageH/2))){
				 	TBimageY[TBindex]=boundN;
				}else if(imY>(boundS-(TBimageH/2))){
					TBimageY[TBindex]=boundS-TBimageH;
				}else{
					TBimageY[TBindex]=imY-(TBimageH/2);
				}
				
			}else if(imY<(boundN+(TBimageH/2))){
			 	TBimageY[TBindex]=boundN;
			 	if(imX<(0+TBimageW/2)){
					TBimageX[TBindex]=0;
			 	}else if(imX>(boundE-TBimageW/2)){
			 		TBimageX[TBindex]=boundE-TBimageW;
			 	}else{
			 		TBimageX[TBindex]=imX-(TBimageW/2);
			 	}
			 	
			}else if(imX>(boundE-(TBimageW/2))){
			 	TBimageX[TBindex]=boundE-TBimageW;
			 	if(imY<(boundN+(TBimageH/2))){
				 	TBimageY[TBindex]=boundN;
				}else if(imY>(boundS-(TBimageH/2))){
					TBimageY[TBindex]=boundS-TBimageH;
				}else{
					TBimageY[TBindex]=imY-(TBimageH/2);
				}
			 	
			}else if(imY>(boundS-(TBimageH/2))){
			 	TBimageY[TBindex]=boundS-TBimageH;
			 	if(imX<(0+TBimageW/2)){
					TBimageX[TBindex]=0;
			 	}else if(imX>(boundE-TBimageW/2)){
			 		TBimageX[TBindex]=boundE-TBimageW;
			 	}else{
			 		TBimageX[TBindex]=imX-(TBimageW/2);
			 	}	
			 	
			 }else{
				TBimageX[TBindex]=imX-(TBimageW/2);
			 	TBimageY[TBindex]=imY-(TBimageH/2);
			}
		 	TBname[TBindex].setLocation(TBimageX[TBindex],TBimageY[TBindex]+TBimageH-200);
			TBBound[TBindex]=new Rectangle(TBimageX[TBindex], TBimageY[TBindex], TB[TBindex].getWidth(null), TB[TBindex].getHeight(null));
		 	repaint();
		}
		if (MOindex !=0){
			if(imX<(0+(MOimageW/2))){
				MOimageX[TBindex]=0;
				if(imY<(boundN+(MOimageH/2))){
				 	MOimageY[MOindex]=boundN;
				}else if(imY>(boundS-(MOimageH/2))){
					MOimageY[MOindex]=boundS-MOimageH;
				}else{
					MOimageY[MOindex]=imY-(MOimageH/2);
				}
				
			}else if(imY<(boundN+(MOimageH/2))){
			 	MOimageY[MOindex]=boundN;
			 	if(imX<(0+MOimageW/2)){
					MOimageX[MOindex]=0;
			 	}else if(imX>(boundE-MOimageW/2)){
			 		MOimageX[MOindex]=boundE-MOimageW;
			 	}else{
			 		MOimageX[MOindex]=imX-(MOimageW/2);
			 	}
			 	
			}else if(imX>(boundE-(MOimageW/2))){
			 	MOimageX[MOindex]=boundE-MOimageW;
			 	if(imY<(boundN+(MOimageH/2))){
				 	MOimageY[MOindex]=boundN;
				}else if(imY>(boundS-(MOimageH/2))){
					MOimageY[MOindex]=boundS-MOimageH;
				}else{
					MOimageY[MOindex]=imY-(MOimageH/2);
				}
			 	
			}else if(imY>(boundS-(MOimageH/2))){
			 	MOimageY[MOindex]=boundS-MOimageH;
			 	if(imX<(0+MOimageW/2)){
					MOimageX[MOindex]=0;
			 	}else if(imX>(boundE-MOimageW/2)){
			 		MOimageX[MOindex]=boundE-MOimageW;
			 	}else{
			 		MOimageX[MOindex]=imX-(MOimageW/2);
			 	}	
			 	
			 }else{
				MOimageX[MOindex]=imX-(MOimageW/2);
			 	MOimageY[MOindex]=imY-(MOimageH/2);
			}
			
		 	MOname[MOindex].setLocation(MOimageX[MOindex],MOimageY[MOindex]+MOimageH-200);
			MOBound[MOindex]=new Rectangle(MOimageX[MOindex], MOimageY[MOindex], MO[MOindex].getWidth(null), MO[MOindex].getHeight(null));
		 	repaint();
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
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
import javax.swing.Icon;

public class iPat {

	public static void main(String[] args){        
		JFrame main = new JFrame();
		main.setTitle("iPat");	
		main.setSize(550,800);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();
		cPane.add(new myPanel());	
		main.setVisible(true);
	}
}

class myPanel extends JPanel implements MouseMotionListener{	
	private static final int TBMAX=50;
	private static final int FGMAX=50;
	private static final int MOMAX=50;

	int[] TBimageX= new int[TBMAX];
	int[] TBimageY= new int[TBMAX];
	int TBimageX_int=50;
	int TBimageY_int=200;
	int[] FGimageX= new int[FGMAX];
	int[] FGimageY= new int[FGMAX];
	int FGimageX_int=250;
	int FGimageY_int=200;
	int[] MOimageX= new int[MOMAX];
	int[] MOimageY= new int[MOMAX];
	int MOimageX_int=430;
	int MOimageY_int=200;
	
	int TBimageH, TBimageW,
		FGimageH, FGimageW,
		MOimageH, MOimageW;
	
	Image[] TB= new Image[TBMAX];
	Image[] FG= new Image[FGMAX];
	Image[] MO= new Image[MOMAX];
	
	Rectangle[] TBBound= new Rectangle[TBMAX];
	Rectangle[] FGBound= new Rectangle[FGMAX];
	Rectangle[] MOBound= new Rectangle[MOMAX];
	
	int TBindex =0;
	int FGindex =0;
	int MOindex =0;
	
	int TBcount =0;
	int FGcount =0;
	int MOcount =0;
		
	JButton TBButton = new JButton();
	JButton FGButton = new JButton();
	JButton MOButton = new JButton();
	JLabel iPat = new JLabel();

	JFileChooser[] TBchooser= new JFileChooser[TBMAX];
	JFileChooser[] FGchooser= new JFileChooser[FGMAX];
	JFileChooser[] MOchooser= new JFileChooser[MOMAX];
	
	String[] TBfile= new String[TBMAX];
	String[] FGfile= new String[FGMAX];
	String[] MOfile= new String[MOMAX];
		
	int[] TBvalue= new int[TBMAX];
	int[] FGvalue= new int[FGMAX];
	int[] MOvalue= new int[MOMAX];
	
	JLabel[] TBname= new JLabel[TBMAX];		
	JLabel[] FGname= new JLabel[FGMAX];		
	JLabel[] MOname= new JLabel[MOMAX];		

	Timer timer; 
	
	
	public myPanel(){			
		try{
			Image iconIP = ImageIO.read(getClass().getResource("iPat.png"));
			iPat.setIcon(new ImageIcon(iconIP));
		} catch (IOException ex){}

		try{
			Image iconPS = ImageIO.read(this.getClass().getResourceAsStream("Table.png"));
			TBButton.setIcon(new ImageIcon(iconPS));
		} catch (IOException ex){}	
		
		for (int i=1; i<=TBMAX-1; i++){
			try{
				TB[i] = ImageIO.read(this.getClass().getResourceAsStream("Table.png"));
			} catch (IOException ex){System.out.println("file not found!");}
		}
		try{
			Image iconPS = ImageIO.read(this.getClass().getResourceAsStream("Figure.png"));
			FGButton.setIcon(new ImageIcon(iconPS));
		} catch (IOException ex){}	
		
		for (int i=1; i<=FGMAX-1; i++){
			try{
				FG[i] = ImageIO.read(this.getClass().getResourceAsStream("Figure.png"));
			} catch (IOException ex){System.out.println("file not found!");}
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

		for (int i=1; i<=TBMAX-1; i++){
			TBchooser[i]= new JFileChooser();
			TBfile[i]= new String();
			TBname[i]= new JLabel("hello");
			this.add(TBname[i]);
		}
		for (int i=1; i<=FGMAX-1; i++){
			FGchooser[i]= new JFileChooser();
			FGfile[i]= new String();
			FGname[i]= new JLabel("hello");
			this.add(FGname[i]);	
		}	
		for (int i=1; i<=MOMAX-1; i++){
			MOchooser[i]= new JFileChooser();
			MOfile[i]= new String();
			MOname[i]= new JLabel("hello");
			this.add(MOname[i]);	
		}	
		TBButton.setOpaque(false);
		TBButton.setContentAreaFilled(false);
		TBButton.setBorderPainted(false);
		FGButton.setOpaque(false);
		FGButton.setContentAreaFilled(false);
		FGButton.setBorderPainted(false);
		MOButton.setOpaque(false);
		MOButton.setContentAreaFilled(false);
		MOButton.setBorderPainted(false);
		iPat.setOpaque(false);
		
		TBimageH=TB[1].getHeight(null);	
		TBimageW=TB[1].getWidth(null);
		FGimageH=FG[1].getHeight(null);	
		FGimageW=FG[1].getWidth(null);
		MOimageH=MO[1].getHeight(null);	
		MOimageW=MO[1].getWidth(null);
		
		JPanel startPanel = new JPanel(new MigLayout("fillx","[][grow][]",""));	
		startPanel.add(TBButton,"grow");
		startPanel.add(FGButton,"grow");
		startPanel.add(MOButton,"grow");
		JPanel mainPanel= new JPanel(new MigLayout("fillx"));	
		mainPanel.add(iPat,"alignx c, wrap");
		mainPanel.add(startPanel, "alignx c");	
		mainPanel.setLocation(80,0);
		mainPanel.setSize(400,200);
		
		
		startPanel.setOpaque(false);
		mainPanel.setOpaque(false);
		
		
		this.setLayout(null);		
		this.add(mainPanel);
		
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
		
		FGButton.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (contains(x,y)){
    				FGcount++;
    				FGimageX[FGcount]=FGimageX_int;
    				FGimageY[FGcount]=FGimageY_int+100*(FGcount-1);		
    				FGBound[FGcount]=new Rectangle(FGimageX[FGcount], FGimageY[FGcount],FG[FGcount].getWidth(null), FG[FGcount].getHeight(null));
    				repaint();
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
    	    					    Icon result = TBchooser[i].getUI().getFileView(TBchooser[i]).getIcon(selectedfile);
    	    					    TB[i]= iconToImage(result);
//    	    					    TB[i] = ((ImageIcon) ico).getImage();
    	    					  	TBfile[i]= selectedfile.getAbsolutePath();
    	    						TBname[i].setLocation(TBimageX[i], TBimageY[i]+TBimageH+10);
    	    						TBname[i].setSize(200,15);
    	    						TBname[i].setText(selectedfile.getName());
    	    						TBindex=0;
    	    						repaint();
    	    					 }
    					   	};
    					}
    				}
    			} 
    			if (FGcount>0&&TBindex==0){			
    				for (int i=1; i<=FGcount;i++){
    					if (FGBound[i].contains(move_x, move_y)){
    						FGindex=i;
    						System.out.println(i);					
    						if (SwingUtilities.isRightMouseButton(ee)){
    							System.out.println("right");
    							 FGvalue[i]= FGchooser[i].showOpenDialog(null);
    	    					 if (FGvalue[i] == JFileChooser.APPROVE_OPTION){
    	    					    File selectedfile = FGchooser[i].getSelectedFile();
    	    					  	FGfile[i]= selectedfile.getAbsolutePath();
    	    						FGname[i].setLocation(FGimageX[i], FGimageY[i]+FGimageH+10);
    	    						FGname[i].setSize(200,15);
    	    						FGname[i].setText(selectedfile.getName());
    	    						FGindex=0;
    	    					 }
    					   	};
    					}
    				}
    			} 
    			if (MOcount>0&&TBindex==0&&FGindex==0){			
    				for (int i=1; i<=MOcount;i++){
    					if (MOBound[i].contains(move_x, move_y)){
    						MOindex=i;
    						System.out.println(i);					
    						if (SwingUtilities.isRightMouseButton(ee)){
    							System.out.println("right");
    							 MOvalue[i]= MOchooser[i].showOpenDialog(null);
    	    					 if (MOvalue[i] == JFileChooser.APPROVE_OPTION){
    	    					    File selectedfile = MOchooser[i].getSelectedFile();
    	    					  	MOfile[i]= selectedfile.getAbsolutePath();
    	    						MOname[i].setLocation(MOimageX[i], MOimageY[i]+MOimageH+10);
    	    						MOname[i].setSize(200,15);
    	    						MOname[i].setText(selectedfile.getName());
    	    						MOindex=0;
    	    					 }
    					   	};
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
    				for (int i=1; i<=FGcount; i++){
    					if (FGBound[i].contains(x,y)){
        					FGopenfile(i);
    					}
    				}
    				for (int i=1; i<=MOcount; i++){
    					if (MOBound[i].contains(x,y)){
        					MOopenfile(i);
    					}
    				} 				
    			}
    		}		
			@Override
			public void mouseReleased(MouseEvent ee){
				TBindex=0;
				FGindex=0;
				MOindex=0;
			}
			
		});		
	}	
	@Override
	protected void paintComponent(Graphics g) {
	     super.paintComponent(g);
		 System.out.println("use paint");
	     //Graphics2D g2D = (Graphics2D) g;
	     if (TBcount>0){	    	 
	    	 for (int i=1; i<=TBcount; i++){
			     g.drawImage(TB[i], TBimageX[i], TBimageY[i], this);	
	    	 }
	     }
	     if (FGcount>0){	    	 
	    	 for (int i=1; i<=FGcount; i++){
			     g.drawImage(FG[i], FGimageX[i], FGimageY[i], this);	
	    	 }
	     }
	     if (MOcount>0){	    	 
	    	 for (int i=1; i<=MOcount; i++){
			     g.drawImage(MO[i], MOimageX[i], MOimageY[i], this);	
	    	 }
	     }
	}
	
	@Override
	public void mouseMoved(MouseEvent ev) {
	}
	
	@Override
	 public void mouseDragged(MouseEvent e) {
		int imX = e.getX();
		int imY = e.getY();
		if (TBindex !=0){
			TBimageX[TBindex]=imX-(TBimageW/2);
		 	TBimageY[TBindex]=imY-(TBimageH/2);
		 	TBname[TBindex].setLocation(TBimageX[TBindex],TBimageY[TBindex]+TBimageH+10);
			TBBound[TBindex]=new Rectangle(TBimageX[TBindex], TBimageY[TBindex], TB[TBindex].getWidth(null), TB[TBindex].getHeight(null));
		 	repaint();
		}	
		if (FGindex !=0){
			FGimageX[FGindex]=imX-(FGimageW/2);
		 	FGimageY[FGindex]=imY-(FGimageH/2);
		 	FGname[FGindex].setLocation(FGimageX[FGindex],FGimageY[FGindex]+FGimageH+10);
			FGBound[FGindex]=new Rectangle(FGimageX[FGindex], FGimageY[FGindex], FG[FGindex].getWidth(null), FG[FGindex].getHeight(null));
		 	repaint();
		}
		if (MOindex !=0){
			MOimageX[MOindex]=imX-(MOimageW/2);
		 	MOimageY[MOindex]=imY-(MOimageH/2);
		 	MOname[MOindex].setLocation(MOimageX[MOindex],MOimageY[MOindex]+MOimageH+10);
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
	public void FGopenfile(int i){
		File openfile= new File(FGfile[i]);
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
}
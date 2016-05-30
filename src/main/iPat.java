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

public class iPat {
	public static void main(String[] args){        
		JFrame main = new JFrame();
		main.setTitle("iPat");	
		main.setSize(2000,600);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();
		cPane.add(new myPanel());	
		main.setVisible(true);
	}
}

class myPanel extends JPanel implements MouseMotionListener{	
	private static final int MAX=10;
	int[] imageX= new int[MAX];
	int[] imageY= new int[MAX];
	int imageH;
	int imageW;
	
	Image[] ttt= new Image[MAX];
	Rectangle[] imageBounds= new Rectangle[MAX];
		
	int TBindex =0;
	int count=0;
	Boolean Create=false;
	
	JButton TB = new JButton();
	JPanel buPanel = new JPanel(new MigLayout("debug"));	

	@Override
	protected void paintComponent(Graphics g) {
	     super.paintComponent(g);
		 System.out.println("use paint");
	     //Graphics2D g2D = (Graphics2D) g;
	     if (Create){	    	 
	    	 for (int i=1; i<=count; i++){
			     g.drawImage(ttt[i], imageX[i], imageY[i], this);	
				 System.out.println(i);
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
			Graphics graphics = getGraphics();
		
	 		graphics.setXORMode(getBackground());
	 		((Graphics2D) graphics).drawImage(ttt[TBindex], imageX[TBindex], imageY[TBindex], this);
	 		
			imageBounds[TBindex]=new Rectangle(imX, imY,ttt[TBindex].getWidth(null), ttt[TBindex].getHeight(null));
			
			imageX[TBindex]=imX;
		 	imageY[TBindex]=imY;
			((Graphics2D) graphics).drawImage(ttt[TBindex], imageX[TBindex], imageY[TBindex], this);		
	 		
			graphics.dispose();

		}	
	}
	
	
	public myPanel(){	
		
		JPanel mainPanel= new JPanel(new MigLayout("debug"));
		try{
			Image iconPS = ImageIO.read(this.getClass().getResourceAsStream("Model.png"));
			TB.setIcon(new ImageIcon(iconPS));
		} catch (IOException ex){}
		
		for (int i=1; i<=MAX-1; i++){
			try{
				ttt[i] = ImageIO.read(this.getClass().getResourceAsStream("Figure.png"));
			} catch (IOException ex){System.out.println("file not found!");}
		}
		int imageH=ttt[1].getHeight(null);	
		int imageW=ttt[1].getWidth(null);				

		//}
		
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent ee){
				int move_x=ee.getX();
    			int move_y=ee.getY();			
    			if (Create){			
    				for (int i=1; i<=count;i++){
    					if (imageBounds[i].contains(move_x, move_y)){
    						TBindex=i;
    						System.out.println(i);
    					}
    				}
    			}
			}
			
			@Override
			public void mouseReleased(MouseEvent ee){
				TBindex=0;
			}
		});
		
		TB.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (contains(x,y)){
    				count++; 
    				Create=true;
    				imageX[count]=50+200*(count-1);
    				imageY[count]=300;		
    				imageBounds[count]=new Rectangle(imageX[count], imageY[count],ttt[count].getHeight(null), ttt[count].getWidth(null));
    				repaint();
    			}
    				
    		}
    	});   	
		/*
		mainPanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent evp){
				//Click= -1;
			}
			@Override
			public void mouseReleased(MouseEvent evr){
				//Click= 1;
			}
			
		});
		*/					
		buPanel.add(TB);
		buPanel.setBorder(new TitledBorder(new EtchedBorder(),"Data Input"));
		//JPanel mainPanel= new JPanel(new MigLayout("debug ,fill","","[grow][grow][grow]"));
		mainPanel.add(buPanel);					
		this.add(mainPanel);
		addMouseMotionListener(this);
	}
}
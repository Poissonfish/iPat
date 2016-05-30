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
		main.setSize(400, 300);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();
		cPane.add(new myPanel());	
		main.setVisible(true);
	}
}
class myPanel extends JPanel implements MouseMotionListener{
	
	int imageX[];
	int imageY[];
	Image tbg[];
	Image ttt;
	Rectangle imageBounds[];
			
			
	int TBindex =0;
	int Click=1;
	
	JButton TB = new JButton();
	JPanel buPanel = new JPanel(new MigLayout("debug"));
	

	@Override
	protected void paintComponent(Graphics g) {
	     super.paintComponent(g);
	     //Graphics2D g2D = (Graphics2D) g;
	     g.drawImage(tbg[TBindex], imageX[TBindex], imageY[TBindex], this);	     
	}
	
	@Override
	public void mouseMoved(MouseEvent ev) {
		int move_x=ev.getX();
		int move_y=ev.getY();
		
		for (int i=0;i<10;i++){
			if (imageBounds[i].contains(move_x, move_y)){
				TBindex=i;
			}
		}
	}
	
	
	
	
	@Override
	 public void mouseDragged(MouseEvent e) {
		int imX = e.getX();
		int imY = e.getY();
		Graphics graphics = getGraphics();
 		graphics.setXORMode(getBackground());
 		((Graphics2D) graphics).drawImage(tbg[TBindex], imageX[TBindex], imageY[TBindex], this);
 		((Graphics2D) graphics).drawImage(tbg[TBindex], imX, imY, this);
 		graphics.dispose();
	}
	
	
	public myPanel(){	
		JPanel mainPanel= new JPanel(new MigLayout("debug"));

		try{
			Image iconPS = ImageIO.read(getClass().getResource("../resources/Model.png"));
			TB.setIcon(new ImageIcon(iconPS));
		} catch (IOException ex){}
		//for (int i=0; i<11; i++){
			try{
				//tbg[0] = ImageIO.read(getClass().getResource("../resources/Figure.png"));
				ttt = ImageIO.read(getClass().getResource("../resources/Figure.png"));

			} catch (IOException ex){}
						
		//}
	

		
		TB.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (contains(x,y)){ 
    				imageX[TBindex]=0;
    				imageY[TBindex]=0;
    				imageBounds[TBindex]=new Rectangle(imageX[TBindex], imageY[TBindex],150, 150);
    				repaint();
    				TBindex++;
    			}
    		}
    	});   	
		
		mainPanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent evp){
				Click= -1;
			}
			@Override
			public void mouseReleased(MouseEvent evr){
				Click= 1;
			}
			
		});
				
				
		buPanel.add(TB);
		buPanel.setBorder(new TitledBorder(new EtchedBorder(),"Data Input"));
		//JPanel mainPanel= new JPanel(new MigLayout("debug ,fill","","[grow][grow][grow]"));
		mainPanel.add(buPanel);					
		this.add(mainPanel);
		addMouseMotionListener(this);
	}
}
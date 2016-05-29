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
		JFrame main = new iPatFrame();
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setTitle("iPat");		
		main.pack();
		main.show();
	}
}


class iPatFrame extends JFrame {
	JButton TB = new JButton();
	JButton FG = new JButton();
	JButton EG = new JButton();
	
	ImageIcon[] TBs = new ImageIcon[10];
	//JButton[] FGs = new JButton[10];
	//JButton[] EGs = new JButton[10];
	
	int TBindex = 1;
	int FGindex = 1;
	int EGindex = 1;
	
	JPanel buPanel = new JPanel(new MigLayout());
	JPanel paPanel = new JPanel();		

	public void addTB() {
    		try{
    			Image TBI = ImageIO.read(getClass().getResource("../resources/Table.png"));
    			TBs[1]= new ImageIcon(TBI);
    		} catch (IOException ex){}
    		repaint();
    		
    		System.out.println("helo");
    		/*
    	 	SwingUtilities.updateComponentTreeUI(this);
    		frame.invalidate();
    		frame.validate();
    		frame.repaint();
    		*/
   
	}

	public iPatFrame(){	
		try{
			Image iconPS = ImageIO.read(getClass().getResource("../resources/Table.png"));
			TB.setIcon(new ImageIcon(iconPS));
		} catch (IOException ex){}
			
		buPanel.add(TB);
		//buPanel.add(FG);
		//buPanel.add(EG);
		buPanel.setBorder(new TitledBorder(new EtchedBorder(),"Data Input"));
		paPanel.setBorder(new TitledBorder(new EtchedBorder(),"Paint Input"));
		JPanel mainPanel= new JPanel();
		//JPanel mainPanel= new JPanel(new MigLayout("debug ,fill","","[grow][grow][grow]"));
		mainPanel.setPreferredSize(new Dimension(400, 700));
		mainPanel.add(buPanel);
		mainPanel.add(paPanel);

		this.setContentPane(mainPanel);	
		
		TB.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (contains(x,y)){    			
    				addTB();
    			}
    		}
    	});
		
    	//addMouseMotionListener(this);				
	}
	
	
	
	
}
	



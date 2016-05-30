package for_test;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class myPanel extends JPanel {
	//ImageIcon[] TBs = new ImageIcon[10];

	ImageIcon ikk = new ImageIcon(this.getClass().getResource("../resources/Figure.png"));
	int x2;
	int y2;
	
	public myPanel() {
	
		repaint();
		
		System.out.println("helo");
		/*
	 	SwingUtilities.updateComponentTreeUI(this);
		frame.invalidate();
		frame.validate();
		frame.repaint();
		*/

	}
	public void add(int x, int y){
		int x2 =x;
		int y2 =y;
		repaint();
	}
	 @Override
	 public void paintComponent(Graphics g) {	
	   super.paintComponent(g);
	   ikk.paintIcon(this, g, x2, y2);

	    }
	 
}
	
	
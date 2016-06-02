package for_test;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class DrawSketch {
	public static void main(String[] args) {
		JFrame jFrame = new JFrame();
		jFrame.setTitle("");
		jFrame.setSize(300, 200);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = jFrame.getContentPane();
		cPane.add(new sketch());
		jFrame.setVisible(true);
	}
}

class sketch extends JPanel implements MouseMotionListener {

    private static final int recW = 14;
    private static final int MAX = 30;
    private Rectangle[] rect = new Rectangle[MAX];
    private int numOfRecs = 0;
    private int currentSquareIndex = -1;

    public sketch() {
    	addMouseListener(new MouseAdapter() {
    		@Override
    		public void mousePressed(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			currentSquareIndex = getRec(x, y);
    			if (currentSquareIndex < 0) // not inside a square
    			{
    				add(x, y);
    			}
    		}

    		@Override
    		public void mouseClicked(MouseEvent evt) {
    			int x = evt.getX();
    			int y = evt.getY();
    			if (evt.getClickCount() >= 2) {
    				remove(currentSquareIndex);
    			}
    		}
    	});
    	addMouseMotionListener(this);
    }
    
    @Override
    public void mouseMoved(MouseEvent event) {
    	int x = event.getX();
    	int y = event.getY();
    	if (getRec(x, y) >= 0) {
    		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    	} else {
    		setCursor(Cursor.getDefaultCursor());
    	}
    }

    @Override
    public void mouseDragged(MouseEvent event) {
    	int x = event.getX();
    	int y = event.getY();
    	if (currentSquareIndex >= 0) {
    		Graphics graphics = getGraphics();
    		graphics.setXORMode(getBackground());
    		((Graphics2D) graphics).draw(rect[currentSquareIndex]);
    		rect[currentSquareIndex].x = x;
    		rect[currentSquareIndex].y = y;
    		((Graphics2D) graphics).draw(rect[currentSquareIndex]);
    		graphics.dispose();
    	}
    }
    
    @Override
    public void paintComponent(Graphics g) {	
    	super.paintComponent(g);
    	for (int i = 0; i < numOfRecs; i++) {
    		((Graphics2D) g).draw(rect[i]);
    		}
    	}

    public int getRec(int x, int y) {    	
    	for (int i = 0; i < numOfRecs; i++) {
    		if (rect[i].contains(x, y)) {    		
    			return i;
    			}
    		}
    		return -1;
    }
    
    public void add(int x, int y) {
    	if (numOfRecs < MAX) {
    		rect[numOfRecs] = new Rectangle(x, y, recW, recW);
    		currentSquareIndex = numOfRecs;
    		numOfRecs++;
    		repaint();
    	}
    }

    @Override
    public void remove(int n) {
    	if (n < 0 || n >= numOfRecs) {
    		return;
    	}
    	numOfRecs--;
    	rect[n] = rect[numOfRecs];
    	if (currentSquareIndex == n) {
    		currentSquareIndex = -1;
    	}
    	repaint();
    }      
}


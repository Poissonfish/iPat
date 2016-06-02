package for_test;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TimerBasedAnimation extends JPanel implements ActionListener {
  private Ellipse2D.Float ellipse = new Ellipse2D.Float();

  private double esize;

  private double maxSize = 0;

  private boolean initialize = true;

  Timer timer;

  ActionListener updateProBar;

  public TimerBasedAnimation() {
    setXY(20 * Math.random(), 200, 200);

    timer = new Timer(20, this);
    timer.setInitialDelay(190);
    timer.start();
  }

  public void setXY(double size, int w, int h) {
    esize = size;
    ellipse.setFrame(10, 10, size, size);
  }

  public void reset(int w, int h) {
    maxSize = w / 10;
    setXY(maxSize * Math.random(), w, h);
  }

  public void step(int w, int h) {
    esize++;
    if (esize > maxSize) {
      setXY(1, w, h);
    } else {
      ellipse.setFrame(ellipse.getX(), ellipse.getY(), esize, esize);
    }
  }

  public void render(int w, int h, Graphics2D g2) {
    g2.setColor(Color.BLUE);
    g2.draw(ellipse);
  }

  public void paint(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    g2.setRenderingHints(rh);
    Dimension size = getSize();

    if (initialize) {
      reset(size.width, size.height);
      initialize = false;
    }
    this.step(size.width, size.height);
    render(size.width, size.height, g2);
  }

  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("TimerBasedAnimation");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new TimerBasedAnimation());
    frame.setSize(350, 250);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
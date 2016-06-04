package for_test;


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


public class TestFadeLabel {

    public static void main(String[] args) {
        new TestFadeLabel();
    }

    public TestFadeLabel() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                } catch (InstantiationException ex) {
                } catch (IllegalAccessException ex) {
                } catch (UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new MainPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class MainPane extends JPanel {

        private float direction = -0.05f;
        private FadeLabel label = new FadeLabel();

        public MainPane() {
            setLayout(new BorderLayout());
            JLabel background = new JLabel();
            background.setLayout(new GridBagLayout());
            try {
                background.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/main/Table.png"))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            add(background);

            label = new FadeLabel();
            background.add(label);

            Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    float alpha = label.getAlpha();
                    alpha += direction;
                    if (alpha < 0) {
                        alpha = 0;
                        direction = 0.05f;
                    } else if (alpha > 1) {
                        alpha = 1;
                        direction = -0.05f;
                    }
                    label.setAlpha(alpha);
                }
            });
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.start();
        }
    }

    public class FadeLabel extends JLabel {

        private float alpha;
        private BufferedImage background;

        public FadeLabel() {
            try {
                background = ImageIO.read(getClass().getResource("/main/CSV.png"));
            } catch (Exception e) {
            }
            setText("Hide and go seek");
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setAlpha(1f);
        }

        public void setAlpha(float value) {
            if (alpha != value) {
                float old = alpha;
                alpha = value;
                firePropertyChange("alpha", old, alpha);
                repaint();
            }
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        public Dimension getPreferredSize() {
            return background == null ? super.getPreferredSize() : new Dimension(background.getWidth(), background.getHeight());
        }

        @Override
        public void paint(Graphics g) {
            // This is one of the few times I would directly override paint
            // This makes sure that the entire paint chain is now using
            // the alpha composite, including borders and child components
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
            super.paint(g2d);
            g2d.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            // This is one of the few times that doing this before the super call
            // will work...
            if (background != null) {
                int x = (getWidth() - background.getWidth()) / 2;
                int y = (getHeight() - background.getHeight()) / 2;
                g.drawImage(background, x, y, this);
            }
            super.paintComponent(g);
        }
    }
}
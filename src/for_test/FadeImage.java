package for_test;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FadeImage {

    public static void main(String[] args) {
        new FadeImage();
    }

    public FadeImage() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static class TestPane extends JPanel {

        public static final long RUNNING_TIME = 100;

        private BufferedImage inImage;
        private BufferedImage outImage;

        private float alpha = 0f;
        private long startTime = -1;

        public TestPane() {
            try {
                inImage = ImageIO.read(new File("/home/mclab/git/iPat/bin/for_test/CSV.png"));
                outImage = ImageIO.read(new File("/home/mclab/git/iPat/bin/for_test/Model.png"));
            } catch (IOException exp) {
                exp.printStackTrace();
            }

            final Timer timer = new Timer(40, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (startTime < 0) {
                        startTime = System.currentTimeMillis();
                    } else {

                        long time = System.currentTimeMillis();
                        long duration = time - startTime;
                        if (duration >= RUNNING_TIME) {
                            startTime = -1;
                            ((Timer) e.getSource()).stop();
                            alpha = 0f;
                        } else {
                            alpha = 1f - ((float) duration / (float) RUNNING_TIME);
                        }
                        repaint();
                    }
                }
            });
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    alpha = 0f;
                    BufferedImage tmp = inImage;
                    inImage = outImage;
                    outImage = tmp;
                    timer.start();
                }

            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(
                            Math.max(inImage.getWidth(), outImage.getWidth()), 
                            Math.max(inImage.getHeight(), outImage.getHeight()));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
            int x = (getWidth() - inImage.getWidth()) / 2;
            int y = (getHeight() - inImage.getHeight()) / 2;
            g2d.drawImage(inImage, x, y, this);

            g2d.setComposite(AlphaComposite.SrcOver.derive(1f - alpha));
            x = (getWidth() - outImage.getWidth()) / 2;
            y = (getHeight() - outImage.getHeight()) / 2;
            g2d.drawImage(outImage, x, y, this);
            g2d.dispose();
        }

    }

}
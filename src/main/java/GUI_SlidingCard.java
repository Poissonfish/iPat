import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Copyright © 2003-2012 Stanislav Lapitsky. All Rights Reserved.
class SliderListener implements ActionListener {
    Component c1;
    Component c2;
    int steps;
    int step ;
    Timer timer;
    boolean isNext;
    JPanel pane;
    String nameDes;

    public SliderListener(JPanel pane, Component c1, Component c2, String nameDes, boolean isNext, int elapse) {
        this.pane = pane;
        this.step = 1;
        this.steps = 30;
        this.timer  = new Timer(elapse, this);
        this.c1 = c1;
        this.c2 = c2;
        this.nameDes = nameDes;
        this.isNext = isNext;
        c2.setVisible(true);
        this.timer.start();
    }

    public void actionPerformed(ActionEvent e) {
        Rectangle bounds = c1.getBounds();
        // Constant * SUM_EXP = width
        double constant = bounds.width / iPat.SUM_EXP;
        int shift = (int)Math.ceil(constant * Math.pow(0.9, step));

        if (isNext) {
            c1.setLocation(bounds.x - shift, bounds.y);
            c2.setLocation(bounds.x - shift + bounds.width, bounds.y);
        }
        else {
            c1.setLocation(bounds.x + shift, bounds.y);
            c2.setLocation(bounds.x + shift - bounds.width, bounds.y);
        }
//        pane.repaint();
        step ++;
        if (step == steps) {
            timer.stop();
            c2.setVisible(false);
            CardLayout cl = (CardLayout) pane.getLayout();
            cl.show(pane, nameDes);
        }
    }
}
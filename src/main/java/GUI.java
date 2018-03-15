import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class AlphaLabel extends JLabel {
    private float alpha = 1f;

    float getAlpha () {
        return alpha;
    }

    void setAlpha (float alpha) {
        this.alpha = alpha;
        repaint();
    }

    @Override
    public void paintComponent (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paintComponent(g2);
    }
}

class GroupValue extends JPanel{
    private JLabel name;
    private JTextField field;

    public GroupValue (int length, String text) {
        super(new MigLayout("fillx", "[grow][grow]", "[]"));
        this.name = new JLabel(text);
        this.field = new JTextField(length);
        this.add(this.name, "cell 0 0, align r");
        this.add(this.name, "cell 1 0, align l");
    }

    String getValue() {
        return this.field.getText();
    }
}

class GroupCheckBox extends JPanel{
    JCheckBox check;

    public GroupCheckBox (String text) {
        super(new MigLayout("fillx", "[grow]", "[]"));
        this.check = new JCheckBox();
        this.check.setText(text);
        this.check.setSelected(false);
        this.add(this.check, "cell 0 0, align r");
    }
    void setCheck(boolean check) {
        this.check.setSelected(check);
    }
    boolean isCheck() {
        return this.check.isSelected();
    }
}

class GroupCombo extends JPanel {
    private JComboBox combo;
    private JLabel name;

    public GroupCombo (String text, String[] list) {
        super(new MigLayout("fillx", "[grow][grow]", "[]"));
        this.name = new JLabel(text);
        this.combo = new JComboBox(list);
        this.add(this.name, "cell 0 0, align r");
        this.add(this.combo, "cell 1 0, align l");
    }

    String getValue() {
        return (String)this.combo.getSelectedItem();
    }
}

// name value slider |------------|
class GroupSlider extends JPanel implements ChangeListener {
    private JSlider slider;
    private JLabel name;
    private JLabel value;
    private boolean isDouble = false;
    private boolean isPow = false;

    public GroupSlider(String name, int min, int max, int defaultVal, int minTick, int majTick) {
        super(new MigLayout("fillx", "[grow][grow][grow]", "[]"));
        this.name = new JLabel(name + " :");
        this.value = new JLabel(Integer.toString(defaultVal));
        this.slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultVal);
        this.slider.setMinorTickSpacing(minTick);
        this.slider.setMajorTickSpacing(majTick);
        this.slider.setPaintTicks(true);
        this.slider.setPaintLabels(true);
        this.slider.setLabelTable(this.slider.createStandardLabels(majTick));
        this.slider.addChangeListener(this);
        this.add(this.name, "cell 0 0, align r");
        this.add(this.value, "cell 1 0, align l");
        this.add(this.slider, "cell 2 0, align l");
    }

    public GroupSlider(String name, double min, double max, double defaultVal, String[] tablename) {
        super(new MigLayout("fillx", "[grow][grow][grow]", "[]"));
        this.name = new JLabel(name + " :");
        this.value = new JLabel(Double.toString(defaultVal));
        this.slider = new JSlider(JSlider.HORIZONTAL, (int) (min * 1000) , (int) (max * 1000), (int) (defaultVal * 1000));
        this.isDouble = true;
        this.slider.setMinorTickSpacing(5);
        this.slider.setMajorTickSpacing(10);
        this.slider.setPaintTicks(true);
        this.slider.setPaintLabels(true);
        Hashtable table = new Hashtable();
        for (int i = 0; i < table.size(); i ++)
            table.put((int)(Double.parseDouble(tablename[i]) * 1000), tablename[i]);
        this.slider.setLabelTable(table);
        this.slider.addChangeListener(this);
        this.add(this.name, "cell 0 0, align r");
        this.add(this.value, "cell 1 0, align l");
        this.add(this.slider, "cell 2 0, align l");
    }

    int getIntValue() {
        return this.slider.getValue();
    }

    String getStrValue() {
        return Integer.toString(this.slider.getValue());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == this.slider && !this.isDouble)
            this.value.setText(Integer.toString(slider.getValue()));
        else if (source == this.slider && this.isDouble)
            this.value.setText(Double.toString(slider.getValue()/(double)1000));
    }
}

// name field browse
class GroupPath extends JPanel implements ActionListener {
    JLabel name;
    JButton browse;
    JTextField field;

    public GroupPath (String text) {
        super(new MigLayout("fillx", "[grow][grow][grow]", "[]"));
        name = new JLabel();
        browse = new JButton("Browse");
        field = new JTextField(15);
        this.name.setText(text);
        this.browse.addActionListener(this);
        this.add(this.name, "cell 0 0, grow");
        this.add(this.field, "cell 1 0");
        this.add(this.browse, "cell 2 0");
    }

    String getPath() {
        return this.field.getText();
    }

    public void setPath (boolean showDirOnly) {
        String msg;
        if (showDirOnly)
            msg = "Choose a output directory";
        else
            msg = "Choose a file";
        File selectedFile = getChooserFile(msg, showDirOnly);
        this.field.setText(selectedFile.getAbsolutePath());
    }

    public File getChooserFile (String title, boolean showDirOnly) {
        File selectedfile = null;
        if (showDirOnly) {
            switch (iPat.USEROS) {
                case Windows:
                    JFileChooser fc = new JFileChooser();
                    //choose folder only
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int flag = fc.showOpenDialog(null);
                    if (flag == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        selectedfile = new File(f.getPath());
                    }
                    break;
                default:
                    System.setProperty("apple.awt.fileDialogForDirectories", "true");
                    FileDialog chooser = new FileDialog(new JFrame(), title, FileDialog.LOAD);
                    chooser.setVisible(true);
                    if (chooser != null)
                        selectedfile = chooser.getFiles()[0];
                    System.setProperty("apple.awt.fileDialogForDirectories", "false");
                    break;
            }
        } else {
            FileDialog chooser = new FileDialog(new JFrame(), title, FileDialog.LOAD);
            chooser.setVisible(true);
            if (chooser != null)
                selectedfile = chooser.getFiles()[0];
        }
        return selectedfile;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == browse)
            setPath(true);
    }
}

class FadeTimer extends Timer{
    private boolean isActivated = false;
    private boolean isOut       = false;
    private Timer timerIn;
    private Timer timerOut;

    public FadeTimer (int delay, final AlphaLabel label, ActionListener listener) {
        super(delay, listener);
        this.timerIn = new Timer(30, new ActionListener() {
            float alpha = 0.0f;

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (label.getAlpha() < 0.9f) {
                    alpha += 0.1f;
                    label.setAlpha(alpha);
                } else {
                    timerIn.stop();
                    isOut = true;
                }
            }
        });

        this.timerOut = new Timer(30, new ActionListener() {
            float alpha = 1.0f;

            @Override
            public void actionPerformed (ActionEvent ae) {
                if (label.getAlpha() > 0.1f) {
                    alpha -= 0.1f;
                    label.setAlpha(alpha);
                } else {
                    label.setAlpha(0f);
                    timerOut.stop();
                }
            }
        });
    }

    public void fadeIn () {
        this.timerIn.start();
    }

    public void fade_out () {
        this.timerOut.start();
    }

    public void setActived (boolean active) {
        this.isActivated = active;
    }

    public boolean isActived () {
        return this.isActivated;
    }

    public boolean isTimeToOut () {
        return this.isOut;
    }
}
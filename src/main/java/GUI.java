import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

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

class GroupValue {
    private JLabel name = new JLabel();
    private JTextField field;

    public GroupValue (int length, String text) {
        field = new JTextField(length);
        name.setText(text);
    }
}

class GroupRadioButton{
    private ButtonGroup	group = new ButtonGroup();
    private JRadioButton[] button;

    public GroupRadioButton (int size) {
        button = new JRadioButton[size];
        for (int i = 0; i < size; i ++) {
            button[i] = new JRadioButton("");
            group.add(button[i]);
        }
    }

    public void setName (int num, String text) {
        button[num].setText(text);
    }
}

class GroupCheckBox implements ActionListener {
    private JCheckBox check = new JCheckBox();
    private JTextField field;

    public GroupCheckBox (int length, String text) {
        field  = new JTextField(length);
        check.setText(text);
        check.setSelected(false);
        check.addActionListener(this);
        field.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == check)
            field.setEnabled(!field.isEnabled());
    }
}

class GroupCombo {
    private JComboBox combo;
    private JLabel name = new JLabel();

    public GroupCombo (String text, String[] list) {
        name.setText(text);
        combo = new JComboBox(list);
    }

    JLabel getLabel() {
        return this.name;
    }

    JComboBox getCombo() {
        return this.combo;
    }
}
class GroupPath {
    private JLabel name = new JLabel();
    private JButton browse = new JButton("Browse");
    private JTextField field = new JTextField(15);

    public GroupPath (String text) {
        name.setText(text);
    }

    public void setPath (boolean showDirOnly) {
        String msg;

        if (showDirOnly)
            msg = "Choose a output directory";
        else
            msg = "Choose a file";
        File selectedFile = getChooserFile(msg, showDirOnly);
        field.setText(selectedFile.getAbsolutePath());
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
package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.ArrayUtils;

import net.miginfocom.swing.MigLayout;

class AlphaLabel extends JLabel {
    private float alpha = 1f;        
    public float getAlpha() {
        return alpha;
    }
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));          
        super.paintComponent(g2);
    }
}
class Group_Value{
	public JLabel name = new JLabel();
	public JTextField longfield = new JTextField(10);
	public JTextField field = new JTextField(5);
	public Group_Value(String text){
		name.setText(text);
	}
}
class Group_RadioButton{
	public ButtonGroup	group = new ButtonGroup();
	public JRadioButton[] button;
	public Group_RadioButton(int size){
		button = new JRadioButton[size];
		for(int i = 0; i<size; i++){
			button[i] = new JRadioButton("");
			group.add(button[i]);}
	}
	public void setName(int num, String text){
		button[num].setText(text);
	}	
}
class Group_CheckBox implements ActionListener{
	public JCheckBox check = new JCheckBox();
	public JTextField longfield = new JTextField(10);
	public JTextField field = new JTextField(3);
	public Group_CheckBox(String text){
		check.setText(text);
		check.setSelected(false);
		check.addActionListener(this);
		longfield.setEnabled(false);
		field.setEnabled(false);
	}
	@Override
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();	
	      //GAPIT
	      if (source == check){
	    	 longfield.setEnabled(!longfield.isEnabled());
	    	 field.setEnabled(!field.isEnabled());}
	}
}
class Group_Combo{
	public JLabel name = new JLabel();
	public JComboBox combo;
	public Group_Combo(String text, String[] list){
		name.setText(text);
		combo = new JComboBox(list);
	}
}
class Group_Path{
	public JLabel name = new JLabel();
	public JButton browse = new JButton("Browse");
	public JTextField field = new JTextField(15);
	public Group_Path(String text){
		name.setText(text);
	}	
	public void setPath(boolean folder){
		JFileChooser chooser;
		File selectedfile;
		if(folder)
			selectedfile = iPatPanel.iPatChooser("Choose a output directory", true);
		else
			selectedfile = iPatPanel.iPatChooser("Choose a file", false);
		field.setText(selectedfile.getAbsolutePath());
	}
}

class OS{
	public static enum TYPE{
		Mac, Windows, Linux, Unknown}
	public TYPE type = TYPE.Unknown;
	public OS(){}
}
class Fade_timer extends Timer{
	boolean actived = false, out = false;
	Timer timer_in, timer_out;
	public Fade_timer(int delay, AlphaLabel label, ActionListener listener) {
		super(delay, listener);
		timer_in = new Timer(30, new ActionListener() {
			float alpha = 0;
			@Override
		    public void actionPerformed(ActionEvent ae) {
	    		if(label.getAlpha() <.9f){
	    			alpha += .1f;
	    			label.setAlpha(alpha);}
	    		else{
	    			timer_in.stop();
	    			out = true;}					
		    }});
		timer_out = new Timer(30, new ActionListener() {
			float alpha = 1.0f;
			@Override
		    public void actionPerformed(ActionEvent ae) {
	    		if(label.getAlpha() >.1f){
	    			alpha -= .1f;
	    			label.setAlpha(alpha);}
	    		else{
	    			label.setAlpha(0f);
	    			timer_out.stop();}				
		    }});
	}
	public void fade_in(){
		timer_in.start();
	}
	public void fade_out(){
		timer_out.start();
	}
	public void setActived(boolean active){
		actived = active;
	}
	public boolean isActived(){
		return actived;
	}
	public boolean TimeToOut(){
		return out;
	}
}
//

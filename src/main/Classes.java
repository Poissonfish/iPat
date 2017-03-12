package main;

import java.awt.AlphaComposite;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

class iPat_chooser{
	File selectedfile = null;
	public iPat_chooser(){
		int value;
    		JFileChooser folder_chooser = new JFileChooser(){
    			public void approveSelection()
    		    {
    		        if (getSelectedFile().isFile()){
    		            // beep
    		            return;
    		        }else{
    		        	super.approveSelection();
    		        }   
    		    }
    		};
    		folder_chooser.setAcceptAllFileFilterUsed(false);
    		folder_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    		folder_chooser.setDialogTitle("Choose your working directory");	
    		folder_chooser.setApproveButtonToolTipText("New Approve Tool Tip");
    		value = folder_chooser.showOpenDialog(null);
    		if (value == JFileChooser.APPROVE_OPTION){
    			selectedfile = folder_chooser.getSelectedFile();
    		}	 	
    		selectedfile.getAbsolutePath();
	}
	String getPath(){
		return selectedfile.getAbsolutePath();
	}
}

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
	public JTextField field = new JTextField(3);
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
			group.add(button[i]);
		}
	}
	public void setName(int num, String text){
		button[num].setText(text);
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
		if(folder){
			chooser = new JFileChooser(){
				public void approveSelection(){
					if (getSelectedFile().isFile()){
						return;
					}else{
						super.approveSelection();
					}
				}
			};
    		chooser.setAcceptAllFileFilterUsed(false);
    		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}else{
			chooser = new JFileChooser();
		}
		int value = chooser.showOpenDialog(null);
		if (value == JFileChooser.APPROVE_OPTION){
		    File selectedfile = chooser.getSelectedFile();  	    					    
		  	field.setText(selectedfile.getAbsolutePath());
		}
	}
}

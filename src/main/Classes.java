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

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import net.miginfocom.swing.MigLayout;

public class Classes {

}

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

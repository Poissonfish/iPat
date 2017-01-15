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

class TextConsole implements RMainLoopCallbacks{
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
    }
    
    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy("+which+")");
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }
    
    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \""+message+"\"");
    }

    public String rChooseFile(Rengine re, int newFile) {
    	FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
    	fd.show();
    	String res=null;
    	if (fd.getDirectory()!=null) res=fd.getDirectory();
    	if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
    	return res;        
    }
    
    public void   rFlushConsole (Rengine re) {
    }
	
    public void   rLoadHistory  (Rengine re, String filename) {
    }			
    
    public void   rSaveHistory  (Rengine re, String filename) {
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

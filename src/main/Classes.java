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
	    	 field.setEnabled(!field.isEnabled());
	      }
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
		if(folder){
			selectedfile = iPatPanel.iPatChooser("Choose a output directory", true);
		}else{
			selectedfile = iPatPanel.iPatChooser("Choose a file", false);
		}
		field.setText(selectedfile.getAbsolutePath());
	}
}

class Findex{
	public static enum FILE{
		unknown, P, G, GD, GM, VCF, PED, MAP, BED, FAM, BIM, TB, C, K, 
	}
	public FILE file = FILE.unknown;
	public int tb = 0;
	public Findex(){
	}
}

class OS{
	public static enum TYPE{
		Mac, Windows, Linux, Unknown
	}
	public TYPE type = TYPE.Unknown;
	public OS(){
	}
}

class BGThread extends Thread{
	int MOindex;
	String WD;
	String Project;
	String[] command, con_command;
	Boolean R, con_R;
	public BGThread(int MOindex, String WD, String Project, String[] command,  Boolean R, 
															String[] con_command, Boolean con_R){
		this.MOindex = MOindex;
		this.command = command;
		this.WD = WD;
		this.Project = Project;
		this.R = R;
		this.con_command = con_command;
		this.con_R = con_R;
	}
	@Override
	public void run(){
      	String line = ""; Boolean Suc_or_Fal = true;	
        iPatPanel.permit[MOindex] = true;
		iPatPanel.rotate_index[MOindex] = 1;
		Configuration.runtime[MOindex] = Runtime.getRuntime();
		
		if(iPatPanel.debug){
			// Execute the command
			try{Configuration.process[MOindex] = Configuration.runtime[MOindex].exec(command);
	    	}catch (IOException e1) {e1.printStackTrace();}	
		}
		     	
        // Print command	
        for (int i = 0; i<command.length; i++){iPatPanel.text_console[MOindex].append(command[i]+" ");}
        iPatPanel.text_console[MOindex].append(System.getProperty("line.separator"));
        iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());	   	

        try {
        	// Print output message to the panel
        	System.out.println("begin to print");
    	    BufferedReader input_stream = new BufferedReader(new InputStreamReader(Configuration.process[MOindex].getInputStream()));
            BufferedReader error_stream = new BufferedReader(new InputStreamReader(Configuration.process[MOindex].getErrorStream()));
	        while((line = input_stream.readLine()) != null){
	            iPatPanel.text_console[MOindex].append(line+ System.getProperty("line.separator"));
	            iPatPanel.text_console[MOindex].setCaretPosition(iPatPanel.text_console[MOindex].getDocument().getLength());
	        }
	        Configuration.process[MOindex].waitFor();        
        	
	        // Direct error to a file
	        PrintWriter errWriter = new PrintWriter(new BufferedWriter(new FileWriter(WD+"/"+Project+".err", true)));
        	boolean err_close = false;
	        try{
        		// Print error if there is any error message
    	        while((line = error_stream.readLine()) != null){
    	        	err_close = true;
    	        	errWriter.println(line);
    	        	if(line.toUpperCase().indexOf("ERROR") >= 0) Suc_or_Fal = false;
    	        	System.out.println("failed");
    	        }	
        	}catch(IOException e){}
        	if(err_close){errWriter.close();}

        	// Direct output to a file from panel
	        File outfile = new File(WD+"/"+Project+".log");
            FileWriter outWriter = new FileWriter(outfile.getAbsoluteFile(), false);
            iPatPanel.text_console[MOindex].write(outWriter);
        } catch (IOException | InterruptedException e1) {	
			e1.printStackTrace();
        	Suc_or_Fal = false;
		}	 
        
        // Indicator
	    if(Suc_or_Fal) iPatPanel.MO[MOindex] = iPatPanel.MO_suc; else iPatPanel.MO[MOindex] = iPatPanel.MO_fal;     
	    // Stop rotating
	    iPatPanel.permit[MOindex] = false;
		iPatPanel.rotate_index[MOindex] = 0;
		iPatPanel.MOimageH[MOindex]=iPatPanel.MO[MOindex].getHeight(null);
		iPatPanel.MOimageW[MOindex]=iPatPanel.MO[MOindex].getWidth(null);
		iPatPanel.MOname[MOindex].setLocation(iPatPanel.MOimageX[MOindex], iPatPanel.MOimageY[MOindex]+ iPatPanel.MOimageH[MOindex]);
		System.out.println("done");
		Configuration.process[MOindex].destroy();
		// Run next procedure if needed
		if(con_command!=null){
    	  	iPatPanel.multi_run[MOindex] = new BGThread(MOindex, WD, Project, 
    	  												con_command, true, null, false);
    	  	iPatPanel.multi_run[MOindex].start();
		}
	}
}


class ListPanel extends JPanel implements ActionListener{
	JList list_items, list_selected;
	JButton button_in, button_out;
	SortedListModel items, selected;
	
	public void addElements(String[] elements){
		items.addAll(elements);
	}
	public void addElement(String element){
		items.add(element);
	}
	public String[] getElement(){
		String[] out = new String[items.getSize()];
		for(int i = 0; i < out.length; i++){
			out[i] = (String)items.getElementAt(i);
		}
		return out;
	}
	public ListPanel(String name1, String name2){  
		MigLayout layout = new MigLayout("fillx");
        items = new SortedListModel();
        selected = new SortedListModel();
        
        list_items = new JList(items);
        list_items.setVisibleRowCount(5);
        list_items.setFixedCellWidth(80);
        list_items.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        list_selected = new JList(selected);
        list_selected.setVisibleRowCount(5);
        list_selected.setFixedCellWidth(80);
        list_selected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            
        JPanel panel_items = new JPanel(layout);
        JScrollPane scroll_items = new JScrollPane(list_items,  
   				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
   				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll_items.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive);
        panel_items.add(new JLabel(name1), "wrap, align r");
        panel_items.add(scroll_items, "align r");
        
        JPanel panel_selected = new JPanel(layout);
        JScrollPane scroll_selected = new JScrollPane(list_selected, 
   				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
   				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll_selected.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive);
        panel_selected.add(new JLabel(name2), "wrap, align l");
        panel_selected.add(scroll_selected, "align l");
              
        JPanel panel_buttons = new JPanel(new MigLayout("fill", "[]", "[grow][grow]"));
        button_in = new JButton(">>");
        button_out= new JButton("<<");
        
        panel_buttons.add(button_in, "cell 0 0, align c");
        panel_buttons.add(button_out, "cell 0 1, align c");
        button_in.addActionListener(this);
        button_out.addActionListener(this);
        
        this.setLayout(new MigLayout("fill"));
        this.add(panel_items, "cell 0 0, grow");
        this.add(panel_buttons, "cell 1 0, grow");
        this.add(panel_selected, "cell 2 0, grow");
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
        if(e.getSource() == button_in){
            Object[] value = list_items.getSelectedValues();
            selected.addAll(value);
            for (int i = value.length - 1; i >= 0; --i) {
              items.removeElement(value[i]);
            }
            list_items.getSelectionModel().clearSelection();
        }else if(e.getSource() == button_out){
        	Object[] value = list_selected.getSelectedValues();
            items.addAll(value);
            for (int i = value.length - 1; i >= 0; --i) {
              selected.removeElement(value[i]);
            }
            list_selected.getSelectionModel().clearSelection();
        }
	}
}

class SortedListModel extends AbstractListModel {
	  SortedSet<Object> model;
	  public SortedListModel() {
	    model = new TreeSet<Object>();
	  }
	  public int getSize() {
	    return model.size();
	  }
	  public Object getElementAt(int index) {
	    return model.toArray()[index];
	  }
	  public void add(Object element) {
	    if (model.add(element)) {
	      fireContentsChanged(this, 0, getSize());
	    }
	  }
	  public void addAll(Object elements[]) {
	    Collection<Object> c = Arrays.asList(elements);
	    model.addAll(c);
	    fireContentsChanged(this, 0, getSize());
	  }
	  public void clear() {
	    model.clear();
	    fireContentsChanged(this, 0, getSize());
	  }
	  public boolean removeElement(Object element) {
	    boolean removed = model.remove(element);
	    if (removed) {
	      fireContentsChanged(this, 0, getSize());
	    }
	    return removed;
	 }
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
		    			label.setAlpha(alpha);
		    		}else{
		    			timer_in.stop();
		    			out = true;
		    		}					
		    }
		});
		timer_out = new Timer(30, new ActionListener() {
			float alpha = 1.0f;
			@Override
		    public void actionPerformed(ActionEvent ae) {
		    		if(label.getAlpha() >.1f){
		    			alpha -= .1f;
		    			label.setAlpha(alpha);
		    		}else{
		    			label.setAlpha(0f);
		    			timer_out.stop();
		    		}				
		    }
		});
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
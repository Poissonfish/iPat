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
            
        JPanel panel_items = new JPanel(new MigLayout());
        JScrollPane scroll_items = new JScrollPane(list_items,  
   				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
   				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll_items.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive);
        panel_items.add(new JLabel(name1), "wrap");
        panel_items.add(scroll_items);
        
        JPanel panel_selected = new JPanel(layout);
        JScrollPane scroll_selected = new JScrollPane(list_selected, 
   				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
   				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll_selected.getVerticalScrollBar().setUnitIncrement(16); //scrolling sensitive);
        panel_selected.add(new JLabel(name2), "wrap");
        panel_selected.add(scroll_selected);
              
        JPanel panel_buttons = new JPanel(layout);
        button_in = new JButton(">>");
        button_out= new JButton("<<");
        
        panel_buttons.add(button_in, "wrap");
        panel_buttons.add(button_out);
        button_in.addActionListener(this);
        button_out.addActionListener(this);
        
        this.setLayout(layout);
        this.add(panel_items);
        this.add(panel_buttons);
        this.add(panel_selected);
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

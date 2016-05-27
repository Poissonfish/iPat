//ananimport org.rosuda.JRI.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import net.miginfocom.swing.MigLayout;
import java.io.IOException;

public class iPat {
	public static void main(String[] args){        
		iPatFrame main = new iPatFrame();
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setTitle("iPat");		
		main.pack();
		main.show();
	}
}

class iPatFrame extends JFrame implements ActionListener{
	JButton PS = new JButton();
	JButton PE = new JButton();
	JButton GE = new JButton();
	JButton PSPre = new JButton("Preview");
	JButton PEPre = new JButton("Preview");
	JButton GEPre = new JButton("Preview");
	JLabel PSL = new JLabel("File Name:");
	JLabel PEL = new JLabel("File Name:");
	JLabel GEL = new JLabel("File Name:");
	JLabel iPat = new JLabel();
	
	JButton MO= new JButton();
	JLabel MOL = new JLabel("Model:");
	
	JButton F1 = new JButton();
	JButton F2 = new JButton();
	JButton F3 = new JButton();
	JLabel F1L = new JLabel("Figure_1");
	JLabel F2L = new JLabel("Figure_2");
	JLabel F3L = new JLabel("Figure_3");
	
	JTextArea LOG = new JTextArea("This is Log Panel");
	JScrollPane LOGScroll= new JScrollPane( LOG);
			
	JFileChooser choose = new JFileChooser();
	JFileChooser choose2 = new JFileChooser();
	JFileChooser choose3 = new JFileChooser();
	int value;
	int value2;
	int value3;
	JTextField PST = new JTextField(10);
	JTextField PET = new JTextField(10);
	JTextField GET = new JTextField(10);
	
	public iPatFrame(){	
		try{
			Image iconPS = ImageIO.read(getClass().getResource("resources/PopStructure.png"));
			PS.setIcon(new ImageIcon(iconPS));
		} catch (IOException ex){}
		try{
			Image iconPE = ImageIO.read(getClass().getResource("resources/Phenotype.png"));
			PE.setIcon(new ImageIcon(iconPE));
		} catch (IOException ex){}
		try{
			Image iconGE = ImageIO.read(getClass().getResource("resources/Genotype.png"));
			GE.setIcon(new ImageIcon(iconGE));
		} catch (IOException ex){}
		try{
			Image iconIP = ImageIO.read(getClass().getResource("resources/iPat.png"));
			iPat.setIcon(new ImageIcon(iconIP));
		} catch (IOException ex){}
		try{
			Image iconMO = ImageIO.read(getClass().getResource("resources/Model.png"));
			MO.setIcon(new ImageIcon(iconMO));
		} catch (IOException ex){}
		try{
			Image iconF1 = ImageIO.read(getClass().getResource("resources/Figure.png"));
			F1.setIcon(new ImageIcon(iconF1));
		} catch (IOException ex){}
		try{
			Image iconF2 = ImageIO.read(getClass().getResource("resources/Figure.png"));
			F2.setIcon(new ImageIcon(iconF2));
		} catch (IOException ex){}
		try{
			Image iconF3 = ImageIO.read(getClass().getResource("resources/Figure.png"));
			F3.setIcon(new ImageIcon(iconF3));
		} catch (IOException ex){}
		
		//GE.setPreferredSize(new Dimension(150,145));
		//PE.setPreferredSize(new Dimension(150,145));
		//PS.setPreferredSize(new Dimension(150,145));
		GE.setOpaque(false);
		GE.setContentAreaFilled(false);
		GE.setBorderPainted(false);
		PE.setOpaque(false);
		PE.setContentAreaFilled(false);
		PE.setBorderPainted(false);
		PS.setOpaque(false);
		PS.setContentAreaFilled(false);
		PS.setBorderPainted(false);
		
		F1.setOpaque(false);
		F1.setContentAreaFilled(false);
		F1.setBorderPainted(false);
		F2.setOpaque(false);
		F2.setContentAreaFilled(false);
		F2.setBorderPainted(false);
		F3.setOpaque(false);
		F3.setContentAreaFilled(false);
		F3.setBorderPainted(false);
		MO.setOpaque(false);
		MO.setContentAreaFilled(false);
		MO.setBorderPainted(false);
		
		F1.setEnabled(false);
		F2.setEnabled(false);
		F3.setEnabled(false);
		PEPre.setEnabled(false);
		PSPre.setEnabled(false);
		GEPre.setEnabled(false);
		
		PS.addActionListener(this);
		PE.addActionListener(this);
		GE.addActionListener(this);
		PSPre.addActionListener(this);
		PEPre.addActionListener(this);		
		GEPre.addActionListener(this);
			
		JPanel ipPanel = new JPanel(new MigLayout("fillx"));
		ipPanel.add(iPat, "alignx c");
		
		JPanel buPanel = new JPanel(new MigLayout("","[][grow][]",""));
		buPanel.add(PS,"cell 0 0 1 3");
		buPanel.add(PE,"cell 0 3 1 3");
		buPanel.add(GE,"cell 0 6 1 3");

		buPanel.add(PSL,"cell 1 1");
		buPanel.add(PEL,"cell 1 4");
		buPanel.add(GEL,"cell 1 7");
		
		buPanel.add(PSPre,"cell 2 1");
		buPanel.add(PEPre,"cell 2 4");
		buPanel.add(GEPre,"cell 2 7");
		
		buPanel.setBorder(new TitledBorder(new EtchedBorder(),"Data Input"));

		JPanel moPanel = new JPanel(new MigLayout("fillx","","[][grow]"));
		moPanel.add(MO,"alignx c, cell 0 0");
		moPanel.add(MOL,"cell 0 1");
		
		moPanel.setBorder(new TitledBorder(new EtchedBorder(),"Model Select"));
		
		JPanel rePanel = new JPanel(new MigLayout("fillx"));
		rePanel.add(F1,"alignx c");
		rePanel.add(F2,"alignx c");
		rePanel.add(F3,"alignx c, Wrap");
		rePanel.add(F1L,"alignx c");
		rePanel.add(F2L,"alignx c");
		rePanel.add(F3L,"alignx c");
		rePanel.setBorder(new TitledBorder(new EtchedBorder(),"Result"));
			
		//JPanel logPanel = new JPanel(new MigLayout("fill, debug"));
		//logPanel.add(LOGScroll);	
		JPanel logPanel = new JPanel(new BorderLayout());
		logPanel.add(LOGScroll,BorderLayout.CENTER);
		
		JPanel mainPanel= new JPanel(new MigLayout("fill","","[grow][grow][grow]"));
		mainPanel.setPreferredSize(new Dimension(790, 700));
		mainPanel.add(ipPanel, "dock north");		
		mainPanel.add(buPanel, "grow, w 450::,h ::450, cell 0 0 1 2");
		mainPanel.add(moPanel,"grow, cell 1 0");
		mainPanel.add(rePanel,"grow, cell 1 1");
		mainPanel.add(logPanel,"grow, cell 0 2 2 1, h 200:400:");
		
		this.setContentPane(mainPanel);		
	}
	
	public void openfile(){
		File file= new File(PST.getText());
			try{
				Desktop.getDesktop().open(file);
			} catch(IOException e) {
				e.printStackTrace();
			}
	}
	public void openfile2(){
		File file= new File(PET.getText());
			try{
				Desktop.getDesktop().open(file);
			} catch(IOException e) {
				e.printStackTrace();
			}
	}
	public void openfile3(){
		File file= new File(GET.getText());
			try{
				Desktop.getDesktop().open(file);
			} catch(IOException e) {
				e.printStackTrace();
			}
	}
	
	public void actionPerformed(ActionEvent ae){
	      Object source = ae.getSource();	
	      if (source == PS){
	    	 //choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    	 value= choose.showOpenDialog(null);
	    	 if (value == JFileChooser.APPROVE_OPTION){
	    		 File selectedfile = choose.getSelectedFile();
	    		 PSL.setText("File Name: "+ selectedfile.getName());
				 PST.setText(selectedfile.getAbsolutePath());
				 PSPre.setEnabled(true);			
			 };
	      }else if (source == PE){
	    	  //choose2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    	 value2= choose2.showOpenDialog(null);
		    	 if (value2 == JFileChooser.APPROVE_OPTION){
		    		 File selectedfile2 = choose2.getSelectedFile();
		    		 PEL.setText("File Name: "+ selectedfile2.getName());
		    		 PET.setText(selectedfile2.getAbsolutePath());
					 PEPre.setEnabled(true);
				 };
	      }else if (source == GE){
	    	  //choose3.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    	 value3= choose3.showOpenDialog(null);
		    	 if (value3 == JFileChooser.APPROVE_OPTION){
		    		 File selectedfile3 = choose3.getSelectedFile();
		    		 GEL.setText("File Name: "+ selectedfile3.getName());
		    		 GET.setText(selectedfile3.getAbsolutePath());
		    		 GEPre.setEnabled(true);
				 };
	      }else if (source == PSPre){
	    	openfile();
	      }else if (source == PEPre){
	    	openfile2();
	      }else if (source == GEPre){
	    	openfile3();
	      };
	}
}
	



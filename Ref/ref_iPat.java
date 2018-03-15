package main;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.ArrayUtils;
import net.miginfocom.swing.MigLayout;

public class iPat {
	static int Wide=1200;
	static int Heigth=700;
	static int PHeight=190;
	static String folder_path = new String("path");
	static String OS_string;
	static OS UserOS = new OS();
	static String R_exe = "NA";
	static iPatPanel ipat;
	
	public static void main(String[] args) throws IOException{  
		System.out.println("Welcome to iPat!");
		OS_string = System.getProperty("os.name");
		if(OS_string.toUpperCase().contains("WINDOWS"))
			UserOS.type = OS.TYPE.Windows;
		else if(OS_string.toUpperCase().contains("MAC"))
			UserOS.type = OS.TYPE.Mac;
		else
			UserOS.type = OS.TYPE.Linux;
		// Catch R exe path
		switch(UserOS.type){
			case Windows: 	
				File file = new File(Paths.get("C:\\","Program Files", "R").toString());
				String[] directories = file.list(new FilenameFilter() {
				  @Override
				  public boolean accept(File current, String name) {
				    return new File(current, name).isDirectory();
				  }
				});
				int ver_int = -1;
				int ver_index = -1;
				for (int i = 0; i < directories.length; i++) {
					String name = directories[i];
					name = name.replaceAll("\\.", "");
					name = name.replaceAll("R-", "");
					int ver = Integer.parseInt(name);
					if (ver > ver_int) {
						ver_int = ver;
						ver_index = i;
				}
				}
				R_exe = Paths.get("C:\\","Program Files", "R", directories[ver_index], "bin", "Rscript").toString();  
				break;
			case Mac: 	
				R_exe = "/usr/local/bin/Rscript"; 
				break;
			case Linux: 	
				R_exe = "/usr/local/bin/Rscript"; 
				break;
		}
		System.out.println("You're running iPat on "+ UserOS.type);// Mac OS X, Windows 10
		JFrame main = new JFrame();
		//Set to center
		main.setSize(Wide, Heigth);
		Dimension windowSize = main.getSize();
       	GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
       	Point centerPoint = local_env.getCenterPoint();
       	int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;    

		main.setResizable(false);
        main.setLocation(dx, dy);
        System.out.println("x: " + dx + "y: " + dy);
		main.setLayout(new BorderLayout());
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = main.getContentPane();

		Wide= main.getWidth();
		Heigth= main.getHeight();
		ipat = new iPatPanel(Wide, Heigth, PHeight);

		ipat.setFocusable(true); // for keylistener
		ipat.requestFocusInWindow(); // for keylistener
		cPane.add(ipat);	

		main.setVisible(true);
		main.addComponentListener(new ComponentAdapter () {
			@Override
	        		public void componentResized(ComponentEvent evt) {
				System.out.println("componentResized");
	            		Component c = (Component)evt.getSource();
	            		System.out.println("H: "+c.getHeight()+" W: "+c.getWidth()); 
	        		}	
		});	
	}
}
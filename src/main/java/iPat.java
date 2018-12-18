import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class iPat {
    static Enum_UserOS USEROS;
    static WindowSize WINDOWSIZE;
    static String REXC;
    static Lib_File FILELIB;
    static Lib_Image IMGLIB;
    static Lib_Text TXTLIB;
    static Lib_Ref DEFAULTVAL;
    static Lib_Ref MODVAL;
    static JFrame IPATFRAME;
    static double SUM_EXP = 0;

    public iPat() throws URISyntaxException, IOException, InterruptedException {
        USEROS = getOS();
        WINDOWSIZE = new WindowSize();
        WINDOWSIZE = setWindowSize(1200, 700);
        REXC = getREXC();
        FILELIB = new Lib_File();
        IMGLIB = new Lib_Image();
        TXTLIB = new Lib_Text();
        DEFAULTVAL = new Lib_Ref();
        MODVAL = new Lib_Ref();
        printWelcomeMsg();
        // Calculate sum of exponential (fewer number for rounding problem)
        for (int i = 1; i < 35; i++)
            SUM_EXP += Math.pow(0.9, i);
        // Run iPat
//        String test = "doublcment/ij.ldjk/b.nioeh.ej.txt";
//        int len = 10;
//        String pattern = String.format("(.{%d}\\.)", len);
//        String nameTrim = test.replaceAll(pattern,"...");
//        System.out.println(nameTrim);
//        Pattern pattern = Pattern.compile("[01.,]{1}[|/]{1}[01.,]{1}");
//        Matcher matcher = pattern.matcher(test);
//        matcher.find();
//        System.out.println(matcher.group());
        launchIPat();
    }

    private Enum_UserOS getOS() {
        String osName = System.getProperty("os.name");
        if (osName.toUpperCase().contains("WINDOWS"))
            return Enum_UserOS.Windows;
        else if (osName.toUpperCase().contains("MAC"))
            return Enum_UserOS.MacOS;
        else
            return Enum_UserOS.Linux;
    }

    private WindowSize setWindowSize(int W, int H) {
        WindowSize window = new WindowSize();
        window.setWidth(W);
        window.setHeight(H);
        return window;
    }

    private String getREXC() {
        String rexc;
        switch (USEROS) {
            case Windows :
                // Assume user install R in C:\\Program Files\R
                File rFolder = new File(Paths.get("C:\\","Program Files", "R").toString());
                // List all folder names with version number
                String[] versionNamesArray = rFolder.list(new FilenameFilter() {
                    @Override
                    public boolean accept (File current, String name) {
                        return new File(current, name).isDirectory();
                    }
                });
                // Get the latest version of R
                int verTemp = -1;
                int verMax = -1;
                int indexLatest = -1;
                String nameFolder;
                // Find the max number of version
                Pattern p = Pattern.compile("\\D*");
                for (int i = 0; i < versionNamesArray.length; i ++) {
                    nameFolder = versionNamesArray[i];
                    Matcher match = p.matcher(nameFolder);
                    nameFolder = match.replaceAll("");
                    try {
                        verTemp = Integer.parseInt(nameFolder);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        continue;
                    }
                    // Compare and find the max
                    if (verTemp > verMax) {
                        verMax = verTemp;
                        indexLatest = i;
                    }
                }
                return Paths.get("C:\\","Program Files", "R",
                        versionNamesArray[indexLatest], "bin", "Rscript").toString();
            case MacOS :
                return "/usr/local/bin/Rscript";
            case Linux :
                return "/usr/local/bin/Rscript";
        }
        return "NULL";
    }

    private void printWelcomeMsg() {
        System.out.println("Welcome to iPat!");
        System.out.println("You're running iPat on "+ USEROS);
    }

    private void launchIPat() throws IOException, InterruptedException {
        // Get the center coordinate for the given window size
        GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = local_env.getCenterPoint();
        int dx = centerPoint.x - WINDOWSIZE.getWidth() / 2;
        int dy = centerPoint.y - WINDOWSIZE.getHeight() / 2;
        // Initialize a frame for iPat
        IPATFRAME = new JFrame();
        IPATFRAME.setSize(WINDOWSIZE.getDimension());
        IPATFRAME.setResizable(false);
        IPATFRAME.setLocation(dx, dy);
        IPATFRAME.setLayout(new BorderLayout());
        IPATFRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Implement resize feature
        IPATFRAME.setVisible(true);
        IPATFRAME.addComponentListener(new ComponentAdapter () {
            @Override
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                System.out.println("Change size to H: "+c.getHeight()+" W: "+c.getWidth());
            }
        });
        // Initialize a functional panel for iPat
        GUI_Objs iPat = new GUI_Objs(WINDOWSIZE.getWidth(), WINDOWSIZE.getHeight());
        iPat.setFocusable(true); // Keylistener
        iPat.requestFocusInWindow(); // Keylistener
        // Add the panel into JFrame
        IPATFRAME.setContentPane(iPat);
        IPATFRAME.show();
        new Cpu_Converter(Enum_FileFormat.Numeric, Enum_FileFormat.PLINK,
                "/Users/jameschen/Desktop/Test/iPatDEMO/demo.dat", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.map",
                0.05,  0.2,
                true, 64);
    }
}

class WindowSize {
    private int width;
    private int height;
    private Dimension dim;
    private Point ptCenter;

    public WindowSize() {
        dim = new Dimension();
        GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ptCenter = local_env.getCenterPoint();
    }

    void setWidth(int width) {
        this.width = width;
        this.dim.width = width;
    }

    void setHeight(int height) {
        this.height = height;
        this.dim.height = height;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    Point getAppLocation(int w, int h) {
        return new Point(this.ptCenter.x - w / 2, this.ptCenter.y - h / 2);
    }

    Point getCenterPoint() {
        return new Point((int)(this.width/(double)2), (int)(this.height/(double)2));
    }

    Dimension getDimension() {
        return this.dim;
    }
}
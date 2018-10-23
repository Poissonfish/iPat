import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class iPat {
    static UserOS USEROS;
    static WindowSize WINDOWSIZE;
    static String REXC;
    static FileLib FILELIB;
    static ImageLib IMGLIB;
    static TextLib TXTLIB;
    static MapValue DEFAULTVAL;
    static MapValue MODVAL;

    public iPat() throws URISyntaxException {
        USEROS = getOS();
        WINDOWSIZE = new WindowSize();
        WINDOWSIZE = setWindowSize(1200, 700);
        REXC = getREXC();
        FILELIB = new FileLib();
        IMGLIB = new ImageLib();
        TXTLIB = new TextLib();
        DEFAULTVAL = new MapValue();
        MODVAL = new MapValue();
        printWelcomeMsg();
        launchIPat();
    }

    private UserOS getOS() {
        String osName = System.getProperty("os.name");
        if (osName.toUpperCase().contains("WINDOWS"))
            return UserOS.Windows;
        else if (osName.toUpperCase().contains("MAC"))
            return UserOS.MacOS;
        else
            return UserOS.Linux;
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

    private void launchIPat() {
        // Get the center coordinate for the given window size
        GraphicsEnvironment local_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = local_env.getCenterPoint();
        int dx = centerPoint.x - WINDOWSIZE.getWidth() / 2;
        int dy = centerPoint.y - WINDOWSIZE.getHeight() / 2;
        // Initialize a frame for iPat
        JFrame iPatFrame = new JFrame();
        iPatFrame.setSize(WINDOWSIZE.getDimension());
        iPatFrame.setResizable(false);
        iPatFrame.setLocation(dx, dy);
        iPatFrame.setLayout(new BorderLayout());
        iPatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Implement resize feature
        iPatFrame.setVisible(true);
        iPatFrame.addComponentListener(new ComponentAdapter () {
            @Override
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                System.out.println("Change size to H: "+c.getHeight()+" W: "+c.getWidth());
            }
        });
        // Initialize a functional panel for iPat
        FileConfig iPat = new FileConfig(WINDOWSIZE.getWidth(), WINDOWSIZE.getHeight());
        iPat.setFocusable(true); // Keylistener
        iPat.requestFocusInWindow(); // Keylistener
        // Add the panel into JFrame
        iPatFrame.setContentPane(iPat);
        iPatFrame.show();
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

    Dimension getDimension() {
        return this.dim;
    }
}
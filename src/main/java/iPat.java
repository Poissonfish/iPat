import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;

public class iPat {
    static UserOS USEROS;
    static WindowSize WINDOWSIZE;
    static String REXC;

    public iPat() {
        USEROS = getOS();
        WINDOWSIZE = setWindowSize(1200, 700, 190);
        REXC = getREXC();
        printWelcomeMsg();
        launchIPat();
    }

    private UserOS getOS() {
        String osName = System.getProperty("os.name");
        if (osName.toUpperCase().contains("WINDOWS"))
            return UserOS.Windows;
        else if(osName.toUpperCase().contains("MAC"))
            return UserOS.MacOS;
        else
            return UserOS.Linux;
    }

    private WindowSize setWindowSize(int W, int H, int pH) {
        WindowSize window = new WindowSize();
        window.setWidth(W);
        window.setHeight(H);
        window.setPHeight(pH);
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
                for (int i = 0; i < versionNamesArray.length; i ++) {
                    nameFolder = versionNamesArray[i];
                    // Remove prefix of the folder
                    nameFolder = nameFolder.replaceAll("\\.", "");
                    nameFolder = nameFolder.replaceAll("R-", "");
                    verTemp = Integer.parseInt(nameFolder);
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
        FileConfig iPat = new FileConfig(WINDOWSIZE.getWidth(), WINDOWSIZE.getHeight(), WINDOWSIZE.getPHeight());
        iPat.setFocusable(true); // Keylistener
        iPat.requestFocusInWindow(); // Keylistener
        // Add the panel into JFrame
        Container contentPane = iPatFrame.getContentPane();
        contentPane.add(iPat);
    }
}

class WindowSize {
    private int width;
    private int height;
    private int pHeight;
    private Dimension dim;

    void setWidth(int width) {
        this.width = width;
        this.dim.width = width;
    }

    void setHeight(int height) {
        this.height = height;
        this.dim.height = height;
    }

    void setPHeight(int pHeight) {
        this.pHeight = pHeight;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    int getPHeight() {
        return this.pHeight;
    }

    Dimension getDimension() {
        return this.dim;
    }
}
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;

class Cpu_Converter extends Cpu_SuperConverter {
     // progress
    JFrame frameProgress;
    Container content;
    Border border;
    JProgressBar progressBar;

    //        args = new String[]{"-in", "hmp", "-out", "num", "-GD", "/Users/jameschen/sam.hmp"};
    public Cpu_Converter(Enum_FileFormat formatIn, Enum_FileFormat formatOut, String pathGD, String pathGM,
                         double rateMAF, double rateNA,
                         boolean isNAFill, int batchSize) throws IOException {
        super();
        // Translate enum in iPat to enum in converter
        iPatFormat input = null, output = null;
        switch (formatIn.getName()) {
            case "Hapmap": input = iPatFormat.Hapmap; break;
            case "Numeric": input = iPatFormat.Numerical; break;
            case "VCF": input = iPatFormat.VCF; break;
            case "PLINK": input = iPatFormat.PLINK; break;
            case "GenomeStudio": input = iPatFormat.genStudio; break;
        }
        switch (formatOut.getName()) {
            case "Hapmap": output = iPatFormat.Hapmap; break;
            case "Numeric": output = iPatFormat.Numerical; break;
            case "VCF": output = iPatFormat.VCF; break;
            case "PLINK": output = iPatFormat.PLINK; break;
            case "GenomeStudio": output = iPatFormat.genStudio; break;
        }
        this.rateMAF = rateMAF;
        this.rateNA = rateNA;
        this.isNAFill = isNAFill;
        this.sub_n = batchSize;
        // Do conversion
        run(input, output, pathGD, pathGM);
    }

    @Override
    void printHelp() {

    }

    @Override
    void iniProgress(String title, String name) {
        frameProgress = new JFrame(title);
        frameProgress.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        border = BorderFactory.createTitledBorder(name);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setBorder(border);
        content = frameProgress.getContentPane();
        content.add(progressBar, BorderLayout.CENTER);
        int width = 400;
        int height = 100;
        frameProgress.setLocation(iPat.WINDOWSIZE.getAppLocation(width, height));
        frameProgress.setSize(width, height);
        frameProgress.setVisible(true);
    }

    @Override
    void updateProgress(int current, int all) {
        int progress = (int) ((current / (double) all) * 100);
        System.out.println("progress " + progress);
        progressBar.setValue(progress);
    }

    @Override
    void doneProgress() {

    }
}
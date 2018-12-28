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
    iPatFormat inputF, outputF;
    String pathGD, pathGM;

    public Cpu_Converter(Enum_FileFormat formatIn, Enum_FileFormat formatOut, String pathGD, String pathGM,
                         double rateMAF, double rateNA,
                         boolean isNAFill, int batchSize,
                         boolean isBG) throws IOException {
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
        this.inputF = input;
        this.outputF = output;
        this.pathGD = pathGD;
        this.pathGM = pathGM;
        // Run conversion
        if (isBG)
            ConvertInBackground();
        else
            ConvertAndWait();
    }
    void ConvertInBackground() {
        ConvertRun run = new ConvertRun(this);
        new Thread(run).start();
    }
    void ConvertAndWait() throws IOException {
        this.run(this.inputF, this.outputF, this.pathGD, this.pathGM);
    }

    private class ConvertRun implements Runnable {
        Cpu_Converter converterTemp;
        ConvertRun(Cpu_Converter converter) {
            this.converterTemp = converter;
        }
        @Override
        public void run() {
            try {
                this.converterTemp.run(this.converterTemp.inputF, this.converterTemp.outputF, this.converterTemp.pathGD, this.converterTemp.pathGM);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        frameProgress.dispose();
    }
}
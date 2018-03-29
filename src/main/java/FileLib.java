import java.io.File;
import java.net.URISyntaxException;

public class FileLib {
    // R script
    File fileiPat;
    File fileGAPIT;
    File fileFarmCPU;
    File filePLINK;
    File filegBLUP;
    File filerrBLUP;
    File fileBGLR;
    File fileBSA;
    File exePLINK;

    public FileLib() throws URISyntaxException {
        fileiPat = new File(iPat.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        fileGAPIT = new File(this.getClass().getResource("iPatGAPIT.r").toURI());
        fileFarmCPU = new File(this.getClass().getResource("iPatFarmCPU.r").toURI());
        filePLINK = new File(this.getClass().getResource("iPatPLINK.r").toURI());
        filegBLUP = new File(this.getClass().getResource("iPatgBLUP.r").toURI());
        filerrBLUP = new File(this.getClass().getResource("iPatrrBLUP.r").toURI());
        fileBGLR = new File(this.getClass().getResource("iPatBGLR.r").toURI());
        fileBSA = new File(this.getClass().getResource("iPatBSA.r").toURI());
        exePLINK = new File(this.getClass().getResource("plink").toURI());
    }

    File getFile(String name) {
        switch (name) {
            case "iPatGAPIT.r": return this.fileGAPIT;
            case "iPatFarmCPU.r": return this.fileFarmCPU;
            case "iPatPLINK.r": return this.filePLINK;
            case "iPatgBLUP.r": return this.filegBLUP;
            case "iPatrrBLUP.r": return this.filerrBLUP;
            case "iPatBGLR.r": return this.fileBGLR;
            case "iPatBSA.r": return this.fileBSA;
            case "plink.r": return this.exePLINK;
        }
        return null;
    }

    String getAbsolutePath(String name) {
        switch (name) {
            case "iPatGAPIT.r": return this.fileGAPIT.getAbsolutePath();
            case "iPatFarmCPU.r": return this.fileFarmCPU.getAbsolutePath();
            case "iPatPLINK.r": return this.filePLINK.getAbsolutePath();
            case "iPatgBLUP.r": return this.filegBLUP.getAbsolutePath();
            case "iPatrrBLUP.r": return this.filerrBLUP.getAbsolutePath();
            case "iPatBGLR.r": return this.fileBGLR.getAbsolutePath();
            case "iPatBSA.r": return this.fileBSA.getAbsolutePath();
            case "plink.r": return this.exePLINK.getAbsolutePath();
        }
        return null;
    }
}

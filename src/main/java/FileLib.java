import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLib {
    // R script
    String pathJar;
    String fileGAPIT;
    String fileFarmCPU;
    String filePLINK;
    String filegBLUP;
    String filerrBLUP;
    String fileBGLR;
    String fileBSA;
    String exePLINK;

    public FileLib() throws URISyntaxException {
        pathJar = Paths.get(iPat.class.getProtectionDomain().getCodeSource().getLocation().toURI()).
                getParent().toString();
        fileGAPIT = Paths.get(pathJar, "res", "iPatGAPIT.r").toString();
        fileFarmCPU = Paths.get(pathJar, "res", "iPatFarmCPU.r").toString();
        filePLINK = Paths.get(pathJar, "res", "iPatPLINK.r").toString();
        filegBLUP = Paths.get(pathJar, "res", "iPatgBLUP.r").toString();
        filerrBLUP = Paths.get(pathJar, "res", "iPatrrBLUP.r").toString();
        fileBGLR = Paths.get(pathJar, "res", "iPatBGLR.r").toString();
        fileBSA = Paths.get(pathJar, "res", "iPatBSA.r").toString();
        exePLINK = Paths.get(pathJar, "res", "plink").toString();
    }

//    File getFile(String name) {
//        switch (name) {
//            case "iPatGAPIT.r": return this.fileGAPIT;
//            case "iPatFarmCPU.r": return this.fileFarmCPU;
//            case "iPatPLINK.r": return this.filePLINK;
//            case "iPatgBLUP.r": return this.filegBLUP;
//            case "iPatrrBLUP.r": return this.filerrBLUP;
//            case "iPatBGLR.r": return this.fileBGLR;
//            case "iPatBSA.r": return this.fileBSA;
//            case "plink.r": return this.exePLINK;
//        }
//        return null;
//    }

    String getAbsolutePath(String name) {
        switch (name) {
            case "iPatGAPIT.r": return this.fileGAPIT;
            case "iPatFarmCPU.r": return this.fileFarmCPU;
            case "iPatPLINK.r": return this.filePLINK;
            case "iPatgBLUP.r": return this.filegBLUP;
            case "iPatrrBLUP.r": return this.filerrBLUP;
            case "iPatBGLR.r": return this.fileBGLR;
            case "iPatBSA.r": return this.fileBSA;
            case "plink": return this.exePLINK;
        }
        return null;
    }
}

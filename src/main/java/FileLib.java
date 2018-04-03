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
        fileGAPIT = Paths.get(pathJar, "lib", "iPatGAPIT.r").toString();
        fileFarmCPU = Paths.get(pathJar, "lib", "iPatFarmCPU.r").toString();
        filePLINK = Paths.get(pathJar, "lib", "iPatPLINK.r").toString();
        filegBLUP = Paths.get(pathJar, "lib", "iPatgBLUP.r").toString();
        filerrBLUP = Paths.get(pathJar, "lib", "iPatrrBLUP.r").toString();
        fileBGLR = Paths.get(pathJar, "lib", "iPatBGLR.r").toString();
        fileBSA = Paths.get(pathJar, "lib", "iPatBSA.r").toString();
        exePLINK = Paths.get(pathJar, "lib", "plink").toString();
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

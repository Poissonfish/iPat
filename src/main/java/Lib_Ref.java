import java.util.HashMap;

public class Lib_Ref {
    HashMap<String, String> mapCommon;
    HashMap<String, String> mapGAPIT;
    HashMap<String, String> mapFarmCPU;
    HashMap<String, String> mapPLINK;
    HashMap<String, String> mapgBLUP;
    HashMap<String, String> maprrBLUP;
    HashMap<String, String> mapBGLR;
    HashMap<String, String> mapBSA;
    public Lib_Ref() {
        // Common
        mapCommon = new HashMap<>();
        if (iPat.USEROS == Enum_UserOS.Windows)
            mapCommon.put("wd", System.getProperty("user.home"));
        else
            mapCommon.put("wd", System.getProperty("user.home"));
        mapCommon.put("maf", "0.05");
        mapCommon.put("ms", "0.2");
        // GAPIT
        mapGAPIT = new HashMap<>();
        mapGAPIT.put("model", "CMLM");
        mapGAPIT.put("pc", "3");
        // FarmCPU
        mapFarmCPU = new HashMap<>();
        mapFarmCPU.put("bin", "optimum");
        mapFarmCPU.put("loop", "10");
        // PLINK
        mapPLINK = new HashMap<>();
        mapPLINK.put("ci", "0.95");
        mapPLINK.put("model", "GLM");
        // gBLUP
        mapgBLUP = new HashMap<>();
        mapgBLUP.put("isGWAS", "FALSE");
        mapgBLUP.put("valid", "FALSE");
        mapgBLUP.put("fold", "1");
        mapgBLUP.put("iter", "1");
        // rrBLUP
        maprrBLUP = new HashMap<>();
        maprrBLUP.put("isGWAS", "FALSE");
        maprrBLUP.put("valid", "FALSE");
        maprrBLUP.put("fold", "1");
        maprrBLUP.put("iter", "1");
        // BGLR
        mapBGLR = new HashMap<>();
        mapBGLR.put("isGWAS", "FALSE");
        mapBGLR.put("model", "BRR");
        mapBGLR.put("niter", "5000");
        mapBGLR.put("burn", "500");
        mapBGLR.put("valid", "FALSE");
        mapBGLR.put("fold", "1");
        mapBGLR.put("iter", "1");
        // BSA
        mapBSA = new HashMap<>();
        mapBSA.put("window", "20000");
        mapBSA.put("power", "4");
    }
}

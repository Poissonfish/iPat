import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public class MapValue {
    HashMap<String, String> mapCommon;
    HashMap<String, String> mapGAPIT;
    HashMap<String, String> mapFarmCPU;
    HashMap<String, String> mapPLINK;
    HashMap<String, String> mapgBLUP;
    HashMap<String, String> maprrBLUP;
    HashMap<String, String> mapBGLR;
    HashMap<String, String> mapBSA;
    public MapValue() {
        // Common
        mapCommon = new HashMap<>();
        if (iPat.USEROS == UserOS.Windows)
            mapCommon.put("wd", System.getProperty("user.home"));
        else
            mapCommon.put("wd", System.getProperty("user.home"));
        mapCommon.put("maf", "0.05");
        mapCommon.put("ms", "0.2");
        // GAPIT
        mapGAPIT = new HashMap<>();
        mapGAPIT.put("model", "GLM");
        mapGAPIT.put("cluster", "average");
        mapGAPIT.put("group", "Mean");
        mapGAPIT.put("snpfrac", "1");
        mapGAPIT.put("checkS", "FALSE");
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
        mapgBLUP.put("snpfrac", "1");
        mapgBLUP.put("checkS", "FALSE");
        // rrBLUP
        maprrBLUP = new HashMap<>();
        maprrBLUP.put("impute", "mean");
        maprrBLUP.put("shrink", "TRUE");
        // BGLR
        mapBGLR = new HashMap<>();
        mapBGLR.put("model", "BRR");
        mapBGLR.put("response", "gaussian");
        mapBGLR.put("niter", "5000");
        mapBGLR.put("burn", "500");
        mapBGLR.put("thin", "5");
        // BSA
        mapBSA = new HashMap<>();
        mapBSA.put("window", "20000");
        mapBSA.put("power", "4");
    }
}

public enum ToolType {
    NA("NA", -1), GAPIT("GAPIT", 0), FarmCPU("FarmCPU", 1), PLINK("PLINK", 2),
    gBLUP("gBLUP", 3), rrBLUP("rrBLUP", 4), BGLR("BGLR", 5), BSA("BSA", 6);

    String name;
    int index;

     ToolType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    String getName() {
        return this.name;
    }

    int getIndex() {
        return this.index;
    }
}

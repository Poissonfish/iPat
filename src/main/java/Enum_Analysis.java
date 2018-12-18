public enum Enum_Analysis {
    NA("NA", -1), GWAS("GWAS", 0), GS("GS", 1), GWASGS("GWAS/GS", 2), BSA("BSA", 3);

    String name;
    int index;

    Enum_Analysis(String name, int index) {
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

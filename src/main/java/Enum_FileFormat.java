public enum Enum_FileFormat {
    NA("NA"), Hapmap("Hapmap"), Numeric("Numeric"), VCF("VCF"),
    PLINK("PLINK"), genStudio("GenomeStudio"), BSA("BSA");

    String name;

    Enum_FileFormat(String name) {
        this.name = name;
    }

    String getName () {
        return this.name;
    }

    boolean isNA() {
        return this == NA;
    }
}

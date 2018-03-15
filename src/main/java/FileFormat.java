public enum FileFormat {
    NA("NA"), Hapmap("Hapmap"), Numeric("Numeric"), VCF("VCF"),
    PLINK("PLINK"), PLINKBIN("PLINK(Binary)"), BSA("BSA");

    String name;

    private FileFormat (String name) {
        this.name = name;
    }

    String getName () {
        return this.name;
    }

    boolean isNA() {
        return this == NA;
    }
}

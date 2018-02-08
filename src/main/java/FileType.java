public enum FileType {
    NA("NA"), Phenotype("Phenotype"), Genotype("Genotype"), Map("Map"),
    FAM("FAM"), BIM("BIM"), Covariate("Covariate"), Kinship("Kinship");

    String name;

    FileType(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }
}

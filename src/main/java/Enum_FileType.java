public enum Enum_FileType {
    NA("NA"), Phenotype("Phenotype"), Genotype("Genotype"), Map("Map"),
    FAM("FAM"), BIM("BIM"), Covariate("Covariate"), Kinship("Kinship");

    String name;

    Enum_FileType(String name) {
        this.name = name;
    }
}

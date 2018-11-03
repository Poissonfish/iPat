public class Obj_FileCluster {
    IPatFile filePhenotype;
    IPatFile fileGenotype;
    IPatFile fileMap;
    IPatFile fileCov;
    IPatFile fileKin;
    public Obj_FileCluster() {
        this.filePhenotype = new IPatFile();
        this.fileGenotype = new IPatFile();
        this.fileMap = new IPatFile();
        this.fileCov = new IPatFile();
        this.fileKin = new IPatFile();
    }

    IPatFile getFile(Enum_FileType filetype) {
        switch (filetype) {
            case Phenotype: return this.filePhenotype;
            case Genotype: return this.fileGenotype;
            case Map: return this.fileMap;
            case Covariate: return this.fileCov;
            case Kinship: return this.fileKin;
        }
        return null;
    }

    void setFile(IPatFile file, Enum_FileType filetype) {
        switch (filetype) {
            case Phenotype: this.filePhenotype = file; break;
            case Genotype: this.fileGenotype = file; break;
            case Map: this.fileMap = file; break;
            case Covariate: this.fileCov = file; break;
            case Kinship: this.fileKin = file; break;
        }
    }
}

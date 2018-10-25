public class Obj_FileCluster {
    IPatFile filePhenotype;
    IPatFile fileGenotype;
    IPatFile fileMap;
    IPatFile fileFAM;
    IPatFile fileBIM;
    IPatFile fileCov;
    IPatFile fileKin;
    public Obj_FileCluster() {
        this.filePhenotype = new IPatFile();
        this.fileGenotype = new IPatFile();
        this.fileMap = new IPatFile();
        this.fileFAM = new IPatFile();
        this.fileBIM = new IPatFile();
        this.fileCov = new IPatFile();
        this.fileKin = new IPatFile();
    }

    IPatFile getPhenotype() {
        return this.filePhenotype;
    }
    IPatFile getGenotype() {
        return this.fileGenotype;
    }
    IPatFile getMap() {
        return this.fileMap;
    }
    IPatFile getFAM() {
        return this.fileFAM;
    }
    IPatFile getBIM() {
        return this.fileBIM;
    }
    IPatFile getCov() {
        return this.fileCov;
    }
    IPatFile getKin() {
        return this.fileKin;
    }
    void setPhenotype(IPatFile file) {
        this.filePhenotype.setFile(file);
    }

    void setGenotype(IPatFile file) {
        this.fileGenotype.setFile(file);
    }

    void setMap(IPatFile file) {
        this.fileMap.setFile(file);
    }

    void setFAM(IPatFile file) {
        this.fileFAM.setFile(file);
    }

    void setBIM(IPatFile file) {
        this.fileBIM.setFile(file);
    }

    void setCov(IPatFile file) {
        this.fileCov.setFile(file);
    }

    void setKin(IPatFile file) {
        this.fileKin.setFile(file);
    }
}

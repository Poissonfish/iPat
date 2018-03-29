public class FileCluster {
    iFile filePhenotype;
    iFile fileGenotype;
    iFile fileMap;
    iFile fileFAM;
    iFile fileBIM;
    iFile fileCov;
    iFile fileKin;
    public FileCluster() {
        this.filePhenotype = new iFile();
        this.fileGenotype = new iFile();
        this.fileMap = new iFile();
        this.fileFAM = new iFile();
        this.fileBIM = new iFile();
        this.fileCov = new iFile();
        this.fileKin = new iFile();
    }

    iFile getPhenotype() {
        return this.filePhenotype;
    }
    iFile getGenotype() {
        return this.fileGenotype;
    }
    iFile getMap() {
        return this.fileMap;
    }
    iFile getFAM() {
        return this.fileFAM;
    }
    iFile getBIM() {
        return this.fileBIM;
    }
    iFile getCov() {
        return this.fileCov;
    }
    iFile getKin() {
        return this.fileKin;
    }
    void setPhenotype(iFile file) {
        this.filePhenotype.setFile(file);
    }

    void setGenotype(iFile file) {
        this.fileGenotype.setFile(file);
    }

    void setMap(iFile file) {
        this.fileMap.setFile(file);
    }

    void setFAM(iFile file) {
        this.fileFAM.setFile(file);
    }

    void setBIM(iFile file) {
        this.fileBIM.setFile(file);
    }

    void setCov(iFile file) {
        this.fileCov.setFile(file);
    }

    void setKin(iFile file) {
        this.fileKin.setFile(file);
    }
}

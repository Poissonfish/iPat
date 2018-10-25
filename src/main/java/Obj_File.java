import java.io.IOException;

class Obj_File extends Obj_Super {
    private Enum_FileType fileType;

    public Obj_File(int x, int y, String filename) throws IOException {
        super(x, y);
        // Define the file
        this.isFile = true;
        this.isModule = false;
        this.isContainMO = false;
        this.file = new IPatFile(filename);
        this.name = this.file.getName();
        this.fileType = Enum_FileType.NA;
        setIcon("file");
    }


    Enum_FileType getFileType() {
        return this.fileType;
    }

    void setFileType(Enum_FileType type) {
        this.fileType = type;
    }

    void setAsRegular() {
        this.fileType = Enum_FileType.NA;
        setIcon("file");
    }

    void setAsCov() {
        this.fileType = Enum_FileType.Covariate;
        setIcon("cov");
    }

    void setAsKin() {
        this.fileType = Enum_FileType.Kinship;
        setIcon("kin");
    }

    boolean isCov() {
        return this.fileType == Enum_FileType.Covariate;
    }

    boolean isKin() {
        return this.fileType == Enum_FileType.Kinship;
    }
}
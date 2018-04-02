import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

class iPatFile extends iPatObject {
    private FileType fileType;

    public iPatFile(int x, int y, String filename) throws IOException {
        super(x, y);
        // Define the file
        this.isFile = true;
        this.isModule = false;
        this.isContainMO = false;
        this.file = new iFile(filename);
        this.name = this.file.getName();
        this.fileType = FileType.NA;
        setIcon("file");
    }


    FileType getFileType() {
        return this.fileType;
    }

    void setFileType(FileType type) {
        this.fileType = type;
    }

    void setAsRegular() {
        this.fileType = FileType.NA;
        setIcon("file");
    }

    void setAsCov() {
        this.fileType = FileType.Covariate;
        setIcon("cov");
    }

    void setAsKin() {
        this.fileType = FileType.Kinship;
        setIcon("kin");
    }
}
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

class iPatFile extends iPatObject {
    private FileType fileType = FileType.NA;

    public iPatFile(int x, int y, String filename) throws IOException {
        super(x, y);
        // Define the file
        this.isFile = true;
        this.isModule = false;
        this.isContainMO = false;
        this.file = new iFile(filename);
        this.setLabel(this.file.getName());
        setIcon("file");
    }


    FileType getFileType() {
        return this.fileType;
    }

    void setFileType(FileType type) {
        this.fileType = type;
    }

}
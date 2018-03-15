import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class ImageLib {
    // Stroke
    Stroke dashed;
    Stroke solid;
    Stroke select;
    // Color
    Color red;
    Color lightred;
    Color dlightred;
    Color ovalcolor;
    Color themecolor;
    Color colorHintDrop;
    Color colorHintDrag;
    Color colorHintTap;
    // Image
    Image iPat;
    Image file;
    Image cov;
    Image kin;
    Image module;
    Image moduleSuc;
    Image moduleFal;
    Image hintProject;
    Image hintTrash;
    Image hintModule;
    Image hintDrag;
    Image hintDrop;

    public ImageLib() {
        // Stroke
        dashed = new BasicStroke(3,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[] {10, 10}, 0);
        solid = new BasicStroke(3,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[] {10, 0}, 0);
        select = new BasicStroke(2,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[] {10, 10}, 0);
        // Color
        red = new Color(231, 57, 131, 100);
        lightred = new Color(231, 57, 131, 80);
        dlightred = new Color(255, 0, 0, 10);
        ovalcolor = new Color(0, 0, 0, 80);
        themecolor = new Color(54, 164, 239, 150);
        colorHintDrop = Color.decode("#D0EDDA");
        colorHintDrag = Color.decode("#D0EDE8");
        colorHintTap = Color.decode("#D0E4ED");
        // Image
        try {
            iPat = ImageIO.read(this.getClass().getResource("iPat.png"));
            file = ImageIO.read(this.getClass().getResource("File.png"));
            cov = ImageIO.read(this.getClass().getResource("File_c.png"));
            kin = ImageIO.read(this.getClass().getResource("File_k.png"));
            module = ImageIO.read(this.getClass().getResource("Model.png"));
            moduleSuc = ImageIO.read(this.getClass().getResource("Model_suc.png"));
            moduleFal = ImageIO.read(this.getClass().getResource("Model_fal.png"));
            hintProject = ImageIO.read(this.getClass().getResource("hint_project.png"));
            hintTrash = ImageIO.read(this.getClass().getResource("hint_trash.png"));
            hintModule = ImageIO.read(this.getClass().getResource("hint_model.png"));
            hintDrag = ImageIO.read(this.getClass().getResource("hint_drag.png"));
            hintDrop = ImageIO.read(this.getClass().getResource("hint_drop.png"));
        } catch (IOException e) {
            System.out.println("Failed to load images!");
        }
    }

    Image getImage(String name) {
        switch (name) {
            case "iPat": return this.iPat;
            case "file": return this.file;
            case "cov" : return this.cov;
            case "kin" : return this.kin;
            case "module" : return this.module;
            case "moduleSuc" : return this.moduleSuc;
            case "moduleFal" : return this.moduleFal;
            case "hintProject" : return this.hintProject;
            case "hintTrash" : return this.hintTrash;
            case "hintModule" : return this.hintModule;
            case "hintDrag" : return this.hintDrag;
            case "hintDrop" : return this.hintDrop;
        }
        return null;
    }

    Stroke getStroke(String name) {
        switch (name) {
            case "dashed" : return this.dashed;
            case "solid" : return this.solid;
            case "select" : return this.select;
        }
        return null;
    }
}

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class Lib_Image {
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
    Image file_empty;
    Image file_detect;
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

    public Lib_Image() {
        // Stroke
        dashed = new BasicStroke(2,
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
        ovalcolor = new Color(0, 0, 0, 10);
        themecolor = new Color(54, 164, 239, 150);
        colorHintDrop = new Color(208,237,218, 255);
        colorHintDrag = new Color(208,237,232, 255);
        colorHintTap = new Color(208,228,237, 255);
        // Image
        try {
            iPat = ImageIO.read(this.getClass().getResource("img/iPat.png"));
            file = ImageIO.read(this.getClass().getResource("img/File.png"));
            file_empty = ImageIO.read(this.getClass().getResource("img/File_Empty.png"));
            file_detect = ImageIO.read(this.getClass().getResource("img/File_Empty_Hover.png"));
            cov = ImageIO.read(this.getClass().getResource("img/File_c.png"));
            kin = ImageIO.read(this.getClass().getResource("img/File_k.png"));
            module = ImageIO.read(this.getClass().getResource("img/Model.png"));
            moduleSuc = ImageIO.read(this.getClass().getResource("img/Model_suc.png"));
            moduleFal = ImageIO.read(this.getClass().getResource("img/Model_fal.png"));
            hintProject = ImageIO.read(this.getClass().getResource("img/hint_project.png"));
            hintTrash = ImageIO.read(this.getClass().getResource("img/hint_trash.png"));
            hintModule = ImageIO.read(this.getClass().getResource("img/hint_model.png"));
            hintDrag = ImageIO.read(this.getClass().getResource("img/hint_drag.png"));
            hintDrop = ImageIO.read(this.getClass().getResource("img/hint_drop.png"));
        } catch (IOException e) {
            System.out.println("Failed to load images!");
        }
    }

    Image getImage(String name) {
        switch (name) {
            case "iPat": return this.iPat;
            case "file": return this.file;
            case "file_empty": return this.file_empty;
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

    Color getColor(String name) {
        switch (name) {
            case "red" : return this.red;
            case "lightred" : return this.lightred;
            case "dlightred" : return this.dlightred;
            case "ovalcolor" : return this.ovalcolor;
            case "themecolor" : return this.themecolor;
            case "colorHintDrop" : return this.colorHintDrop;
            case "colorHintDrag" : return this.colorHintDrag;
            case "colorHintTap" : return this.colorHintTap;
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

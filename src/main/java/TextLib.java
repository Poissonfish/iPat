import java.awt.Font;

public class TextLib {
    // Font
    Font plain;
    Font plainBig;
    Font bold;

    public TextLib() {
        plain = new Font("Ariashowpril", Font.PLAIN, 18);
        plainBig = new Font("Ariashowpril", Font.PLAIN, 30);
        bold = new Font("Ariashowpril", Font.BOLD, 25);
    }

    Font getFont(String name) {
        switch (name) {
            case "plain": return this.plain;
            case "plainBig": return this.plainBig;
            case "bold": return this.bold;
        }
        return null;
    }
}
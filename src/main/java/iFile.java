import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class iFile extends File {
    boolean isEmpty;

    public iFile(String name) {
        super(name);
        isEmpty = false;
    }

    public iFile() {
        super("NA");

        isEmpty = true;
        System.out.println("the file path is : " + this.getAbsolutePath());
    }

    void setFile(iFile file) {
        this.renameTo(file);
        isEmpty = false;
    }

    boolean isEmpty() {
        return this.getName().contains("NA");
    }

    // Read n line from the file
    String[] getLines(int n) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.getAbsolutePath()));
        String[] lines = new String[n];
        String readline;
        int nLine = 0;
        while((readline = reader.readLine()) != null && nLine < n)  {
            //VCF comment escape
            if(!readline.startsWith("##"))
                lines[nLine++] = readline;
        }
        reader.close();
        return lines;
    }

    // Get number of lines in the file
    int getLineCount() throws IOException {
        Path path = Paths.get(this.getAbsolutePath());
        long lineCount = Files.lines(path).count();
        return (int) lineCount;
    }

    // Get separated string from the input [tab, space or comma]
    String[] getSepStr(String string) {
        String[] sepStr = string.replaceAll("\"", "").split("\t");
        if (sepStr.length == 1)
            sepStr = string.replaceAll("\"", "").split(" +");
        else
            return sepStr;
        if (sepStr.length == 1)
            sepStr = string.replaceAll("\"", "").split(",");
        else
            return sepStr;
        if (sepStr.length == 1)
            return null;
        else
            return sepStr;
    }
}

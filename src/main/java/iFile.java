import java.io.*;

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
        InputStream reader = new BufferedInputStream(new FileInputStream(this.getAbsolutePath()));
        try{
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = reader.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') ++count;
                }}
            return (count == 0 && !empty) ? 1 : count;
        }finally{
            reader.close();
        }
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

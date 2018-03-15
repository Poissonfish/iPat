import java.io.*;

public class iFile extends File {
    FileType type;
    boolean isEmpty;

    public iFile(String name) {
        super(name);
        isEmpty = false;
    }

    public iFile() {
        super("NA");
        isEmpty = true;
    }

    boolean isEmpty() {
        return this.isEmpty;
    }

    String[] getLines(int size) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.getAbsolutePath()));
        String[] lines = new String[size];
        String readline;
        int nLine = 0;
        while((readline = reader.readLine()) != null && nLine < size)  {
            //VCF comment escape
            if(!readline.startsWith("##"))
                lines[nLine++] = readline;
        }
        return lines;
    }

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
            reader.close();}
    }

    String[] getSepStr(String string) {
        String[] sepStr = string.replaceAll("\"", "").split("\t");
        if (sepStr.length == 1)
            sepStr = string.replaceAll("\"", "").split(" +");
        else
            return sepStr;
        if (sepStr.length == 1)
            sepStr = string.replaceAll("\"", "").split(" +");
        else
            return sepStr;
        if (sepStr.length == 1)
            return null;
        else
            return sepStr;
    }
}

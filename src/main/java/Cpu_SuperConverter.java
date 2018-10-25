import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;

abstract class Cpu_SuperConverter {
    int sub_n = 32, sub_m = 8192;
    String sep = "\t";
    // Buffer read and write
    BufferedReader reader;
    PrintWriter out;
    // Counter
    int mCountQC = 0, mCount, nCount, lineLength;
    int size_GD, size_GM,
            lastPosition = 0, currentWindow = 0;
    int vcfAnnotation = -1;
    // Sample information
    ArrayList<String> taxa = new ArrayList<String>();
    ArrayList<String> marker = new ArrayList<String>();
    // Marker information
    char[] RefAllele;
    boolean[] hasTwoAlt;
    char m1, m2;
    boolean homo, isRefAllele, m1isNA, m2isNA;
    // Space for matrix of gd and gm
    String[][] table_GD,
            table_GM;
    String[] headerLine = null;
    // temp
    String tempRead = null;
    int idxTemp = 0;
    // Misc
    boolean isNAFill = false;
    boolean hasHeaderGM = false;
    boolean hasHeaderGD = false;
    // Quality control
    double rateMAF = 0;
    double rateNA = 0;
    boolean[] isKeep;
    // Constructor
    public Cpu_SuperConverter() {}
    // Abstract functions
    abstract void printHelp();
    abstract void iniProgress(String title, String name);
    abstract void updateProgress(int current, int all);
    abstract void doneProgress();
    // Enum
    enum iPatFormat {
        NA("NA"), Numerical("Numeric"), Hapmap("Hapmap"),
        VCF("VCF"), PLINK("PLINK"), genStudio("GenomeStudio");
        String name;
        iPatFormat(String name) {
            this.name = name;
        }
        public String getName () {
            return this.name();
        }
    }
    // Main converter functions
    protected void run (iPatFormat InputFormat, iPatFormat OutputFormat, String pathGD, String pathGM) throws IOException {
        switch (InputFormat) {
            case Numerical:
                NumToPlink(pathGD, pathGM);
                break;
            case Hapmap:
                switch (OutputFormat) {
                    case Numerical:
                        HmpToNum(pathGD);
                        break;
                    case PLINK:
                        HmpToPlink(pathGD);
                        break;
                }
                break;
            case VCF:
                switch (OutputFormat) {
                    case Numerical:
                        VCFToNum(pathGD);
                        break;
                    case PLINK:
                        VCFToPlink(pathGD);
                        break;
                }
                break;
            case PLINK:
                PlinkToNum(pathGD, pathGM);
                break;
            case genStudio:
                GenStdioToNum(pathGD);
                break;
        }
        if (InputFormat == iPatFormat.NA || OutputFormat == iPatFormat.NA)
            printHelp();
        else
            System.out.println("Conversion Done!\n");
    }

    private void GenStdioToNum(String GD_path) throws IOException {
        // Front 2 and last 5 columns are for meta inforamtion (Frac A	Frac C	Frac T	Frac G	GenTrain Score)
        System.out.println("GS -> N" + " GD: " + GD_path);
        this.size_GD = getCountOfLines(GD_path);
        // Read the first line, catch sep, linelength and nCount
        this.sep = getSep(GD_path, 0);
        this.headerLine = getNthLine(GD_path, 0, this.sep);
        // the last five columns are ATCG and score
        this.lineLength = this.headerLine.length - 5;
        // Get n and m count (the first two columns are meta information)
        this.nCount = this.lineLength - 2;
        this.mCount = this.size_GD - 1;
        this.isKeep = new boolean[this.mCount];
        // Get taxa name and store them into 'taxa'
        this.taxa = getTaxa(true, 2, this.nCount, this.headerLine);
        // ========= ========= ========= ========= Marker ========= ========= ========= =========
        // Get marker name and check if only two clusters from the second line
        char[] codeAB = new char[this.mCount];
        this.idxTemp = 0;
        boolean hasAA = false;
        boolean hasAB = false;
        boolean hasBB = false;
        String[] tempStr = null;
        String tempRead = null;
        resetToNthLine(GD_path, 1);
        iniProgress("Scanning Markers",
                String.format("Marker %d ~ %d : ", 1, this.mCount));
        while ((tempRead = this.reader.readLine()) != null) {
            updateProgress(this.idxTemp + 1, this.mCount);
            tempStr = tempRead.replaceAll("\"", "").split(this.sep);
            // marker name is in the second column
            this.marker.add(tempStr[1]);
            hasAA = Arrays.stream(tempStr).anyMatch("AA"::equals);
            hasAB = Arrays.stream(tempStr).anyMatch("AB"::equals);
            hasBB = Arrays.stream(tempStr).anyMatch("BB"::equals);
            // AA, AB
            if (hasAA && hasAB && !hasBB)
                codeAB[this.idxTemp++] = '2';
                // BB, AB
            else if (hasBB && hasAB && !hasAA)
                codeAB[this.idxTemp++] = '0';
                // AB
            else if (!hasAA && !hasBB && hasAB)
                codeAB[this.idxTemp++] = '0';
            else
                codeAB[this.idxTemp++] = '1';
        }
        doneProgress();
        // ========= ========= ========= ========= QC ========= ========= ========= =========
        if (this.rateMAF > 0 || this.rateNA > 0) {
            this.isKeep = doQCGenStdio(GD_path, codeAB);
            for(boolean b : this.isKeep)
                this.mCountQC += b ? 1 : 0;
        }
        // ========= ========= ========= ========= GD ========= ========= ========= =========
        // Set writer
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
        // Write first line in numeric file ('taxa' and marker names)
        this.out.write("taxa");
        for (int i = 0; i < this.mCount; i ++) {
            if (this.isKeep[i])
                this.out.write("\t" + this.marker.get(i));
        }
        this.out.write("\n");
        this.lastPosition = 2;
        iniProgress("Converting Genotype File",
                String.format("Sample %d ~ %d : ", 1, this.nCount));
        while (this.lastPosition < this.lineLength) {
            updateProgress(this.lastPosition, this.lineLength - 2);
            // Reset reader to the first line and skip the header line
            resetToNthLine(GD_path, 1);
            // subN by M matrix
            this.table_GD = getSubTransposedGD(this.lastPosition, this.sub_n, this.mCount, this.lineLength, this.sep);
            this.currentWindow = this.table_GD.length;
            // loop over individuals
            for (int i = 0; i < this.currentWindow; i ++) {
                this.out.write(this.taxa.get(this.lastPosition - 2 + i));
                // loop over markers
                for (int j = 0; j < this.mCount; j ++) {
                    if (!this.isKeep[i])
                        continue;
                    this.m1 = this.table_GD[i][j].charAt(0);
                    this.m2 = this.table_GD[i][j].charAt(1);
                    if (this.m1 == 'N' || this.m2 == 'C') {
                        // missing data
                        this.out.write(this.isNAFill? "\t1" : "\tNA");
                    } else if (this.m1 == this.m2 && this.m1 == 'A') {
                        // 2 alleles are the same, and equal to the first allele
                        this.out.write("\t0");
                    } else if (this.m1 == this.m2 && this.m1 == 'B') {
                        // 2 alleles are the same, and equal to the second allele
                        this.out.write("\t2");
                    } else if (this.m1 != this.m2) {
                        // 2 alleles are not the same
                        this.out.write("\t" + codeAB[j]);
                    }
                }
                this.out.write("\n");
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void NumToPlink (String GD_path, String GM_path) throws IOException {
        System.out.println("N -> P" + " GD: " + GD_path + " GM: " + GM_path);
        // Determine the direction and assume both files have similar lines if GD is m by n
        this.size_GD = getCountOfLines(GD_path);
        this.size_GM = getCountOfLines(GM_path);
        boolean nBym = Math.abs(this.size_GD - this.size_GM) > 1;
        // ========= ========= ========= ========= Map ========= ========= ========= =========
        // Read the first line, catch sep
        this.sep = getSep(GM_path, 0);
        this.lastPosition = 0;
        try {
            setReader(GM_path);
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
            iniProgress("Converting Map File",
                    String.format("Marker %d ~ %d : ", 1, this.mCount));
            while (this.lastPosition < this.size_GM) {
                updateProgress(this.lastPosition, this.size_GM);
                this.table_GM = getNLines(this.sub_m, this.sep);
                // It shouldn't be a chromosome number if w/o header
                this.hasHeaderGM = !(this.table_GM[0][1].length() == 1);
                this.currentWindow = this.table_GM.length;
                for (int row = this.hasHeaderGM ? 1 : 0; row < this.currentWindow; row ++)
                    out.write(this.table_GM[row][1] + "\t" + this.table_GM[row][0] + "\t0\t" + this.table_GM[row][2] + "\n");
                this.lastPosition += this.currentWindow;
            }
            doneProgress();
            this.out.flush();
            this.out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        // ========= ========= ========= ========= Ped ========= ========= ========= =========
        try {
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.ped");
            if (nBym)
                NumToPlinkNByM(GD_path);
            else
                NumToPlinkMByN(GD_path);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    private void NumToPlinkNByM (String GD_path) throws IOException {
        boolean hasHeader = false, hasTaxa = false;
        this.lastPosition = 0;
        setReader(GD_path);
        // While haven't reach the end of the files
        iniProgress("Converting Genotype File",
                String.format("Sample %d ~ %d : ", 1, this.nCount));
        while (this.lastPosition < this.size_GD) {
            updateProgress(this.lastPosition, this.size_GD);
            // Read part of the file
            this.table_GD = getNLines(this.sub_n, this.sep);
            // Get number of lines in this read
            this.currentWindow = this.table_GD.length;
            // If is the first read of the file
            if (this.lastPosition == 0) {
                // See is singular number or a string
                hasHeader = !(this.table_GD[0][1].length() == 1);
                hasTaxa = !(this.table_GD[1][0].length() == 1);
            }
            for (int row = 0; row < this.currentWindow; row ++) {
                // If is the first read of the file, not necessarily starts from the first line
                if (this.lastPosition == 0 && hasHeader && row == 0)
                    continue;
                // If contain taxa, give the taxa name. Otherwise, name it initail as "Sample_"
                if (hasTaxa)
                    this.out.write(this.table_GD[row][0] + "\t" + this.table_GD[row][0] + "\t0\t0\t0\t0\t");
                else
                    this.out.write("Family_" + row + "\t" + "Sample_" + row + "\t0\t0\t0\t0\t");
                // If contain taxa, starts from the second column
                for (int col = hasTaxa ? 1 : 0; col < this.table_GD[row].length; col ++) {
                    switch (this.table_GD[row][col]) {
                        case "0" :
                            this.out.write("A A");
                            break;
                        case "1" :
                            this.out.write("A T");
                            break;
                        case "2" :
                            this.out.write("T T");
                            break;
                        case "NA" :
                            this.out.write(this.isNAFill? "A T" : "0 0");
                    }
                    // If haven't reached the end of the row, seperate with tab
                    if (col != this.table_GD[row].length - 1)
                        this.out.write("\t");
                }
                // For each row, end it with newline
                this.out.write("\n");
            }
            this.lastPosition += this.currentWindow;
        }
    }

    private void NumToPlinkMByN (String path) throws IOException {
        RandomAccessFile rdmr = new RandomAccessFile (new File(path), "rw");
        // parameter
        int readSize = sub_n * 2;
        // line finder
        long lineLength = 0;
        boolean lengthFound = false;
        // set pointer
        long lastPosition = 0;
        // reader
        int readChar = 0;
        byte[] bytes = new byte[readSize];
        // counter
        long countLine = 0;
        // matrix holder (subN by M)
        char[][] temp = new char[sub_n][size_GD];
        int tempIndex = 0;
        // line finder
        while ((readChar = rdmr.read(bytes)) != -1) {
            // For each read
            for (int i = 0; i < readChar; i ++) {
                // If is end of the line, record the length and off the loop
                if (bytes[i] == '\n') {
                    lineLength += i + 1;
                    lengthFound = true;
                    break;
                }
            }
            // If found the length, off the loop. Otherwise, keep searching it.
            if (lengthFound)
                break;
            else
                lineLength += readSize;
        }
        iniProgress("Converting Genotype File",
                String.format("Sample %d ~ %d : ", 1, nCount));
        // Search until reach the end of the row (marker)
        while (lastPosition < lineLength) {
            updateProgress((int)lastPosition, (int)lineLength);
            // Go through the part of each row (marker)
            while (countLine < size_GD) {
                // Move the pointer to the specific position (sample) of each rows and read it
                rdmr.seek(countLine * lineLength + lastPosition);
                readChar = rdmr.read(bytes);
                // Scan each character of the read
                for (int i = 0; i < readChar; i ++) {
                    tempIndex = (int) (i / (double) 2);
                    // If reach end, jump to the next line.
                    if (bytes[i] == '\n')
                        break;
                        // If not the end nor the seperator, store the value
                    else if (bytes[i] != '\t' && bytes[i] != ' ' && bytes[i] != ',')
                        // It's an subN by M matrix (Do transpose here simultaneously)
                        temp[tempIndex][(int) countLine] = (char) bytes[i];
                }
                countLine++;
            }

            // Write the temp into the output file (tempIndex is the index of last letter, so plus one)
            for (int n = 0; n < tempIndex + 1; n ++) {
                out.write("Family_" + ((lastPosition / 2)  + 1 + n) + "\t" + "Sample_" + ((lastPosition / 2)  + 1 + n)  + "\t0\t0\t0\t0\t");
                for (int m = 0; m < size_GD; m ++) {
                    switch (temp[n][m]) {
                        case '0' :
                            out.write("A A");
                            break;
                        case '1' :
                            out.write("A T");
                            break;
                        case '2' :
                            out.write("T T");
                            break;
                    }
                    // If haven't reached the end of the row, seperate with tab
                    if (m != size_GD - 1)
                        out.write("\t");
                }
                // For each row, end it with newline
                out.write("\n");
            }
            countLine = 0;
            lastPosition += readSize;
        }

    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void HmpToNum (String GD_path) throws IOException {
        System.out.println("H -> N" + " GD: " + GD_path );
        this.size_GD = getCountOfLines(GD_path);
        // Read first line, catch sep, linelength and nCount
        this.sep = getSep(GD_path, 0);
        this.headerLine = getNthLine(GD_path, 0, this.sep);
        this.lineLength = this.headerLine.length;
        // See the if the first marker show only one character
        boolean isOneChar = this.reader.readLine().replaceAll("\"", "").split(sep)[11].length() == 1;
        String valueNA = (isOneChar) ? "N" : "NA";
        // Get n and m count (the first 11 columns are meta information)
        this.nCount = this.lineLength - 11;
        this.mCount = this.size_GD - 1;
        this.isKeep = new boolean[this.mCount];
        // Get taxa
        this.taxa = getTaxa(true, 11, this.nCount, this.headerLine);
        // Initial first allele
        this.RefAllele = new char[this.mCount];
        // ========= ========= ========= ========= QC ========= ========= ========= =========
        if (this.rateMAF > 0 || this.rateNA > 0) {
            this.isKeep = doQCHapmap(GD_path, valueNA, isOneChar);
            for(boolean b : this.isKeep)
                this.mCountQC += b ? 1 : 0;
        }
        // ========= ========= ========= ========= Map ========= ========= ========= =========
        iniProgress("Converting Map File",
                String.format("Marker %d ~ %d : ", 1, this.mCount));
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
        this.out.write("SNP\tChromosome\tPosition\n");
        this.lastPosition = 0;
        while (this.lastPosition < mCount) {
            updateProgress(this.lastPosition, this.mCount);
            this.table_GD = getNLines(this.sub_m, this.sep);
            this.currentWindow = this.table_GD.length;
            for (int i = 0; i < this.currentWindow; i++) {
                if (!this.isKeep[this.lastPosition + i])
                    continue;
                this.out.write(this.table_GD[i][0] + "\t" + this.table_GD[i][2] + "\t" + this.table_GD[i][3] + "\n");
                this.marker.add(this.table_GD[i][0]);
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
        // ========= ========= ========= ========= GD ========= ========= ========= =========
        // output marker names as 1st line
        this.out.write("taxa");
        for (int i = 0; i < this.mCountQC; i ++)
            this.out.write("\t" + this.marker.get(i));
        this.out.write("\n");
        iniProgress("Converting Genotype File",
                String.format("Sample %d ~ %d : ", 1, this.nCount));
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
        this.lastPosition = 11;
        while (this.lastPosition < this.lineLength) {
            updateProgress(this.lastPosition, this.lineLength);
            // Skip the first line if contains header
            resetToNthLine(GD_path, 1);
            // subN by M matrix
            this.table_GD = getSubTransposedGD(this.lastPosition, this.sub_n, this.mCount, this.lineLength, this.sep);
            this.currentWindow = this.table_GD.length;
            for (int i = 0; i < this.currentWindow; i ++) {
                this.out.write(this.taxa.get(this.lastPosition - 11 + i));
                if (isOneChar) {
                    for (int j = 0; j < this.mCount; j ++) {
                        if (!this.isKeep[j])
                            continue;
                        this.m1 = this.table_GD[i][j].charAt(0);
                        if (this.m1 == 'N' || this.m1 == 'n') {
                            // missing data
                            this.out.write(this.isNAFill? "\t1" : "\tNA");
                        } else if (this.m1 == this.RefAllele[j]) {
                            // 2 alleles are the same, and equal to the first allele
                            this.out.write("\t0");
                        } else if (this.m1 != this.RefAllele[j]) {
                            // 2 alleles are the same, and equal to the second allele
                            this.out.write("\t2");
                        } else {
                            // 2 alleles are not the same
                            this.out.write("\t1");
                        }
                    }
                } else {
                    for (int j = 0; j < this.mCount; j ++) {
                        if (!this.isKeep[j])
                            continue;
                        this.m1 = this.table_GD[i][j].charAt(0);
                        this.m2 = this.table_GD[i][j].charAt(1);
                        if (this.m1 == 'N' || this.m2 == 'N' || this.m1 == 'n' || this.m2 == 'n') {
                            // missing data
                            this.out.write(this.isNAFill? "\t1" : "\tNA");
                        } else if (m1 == this.m2 && this.m1 == this.RefAllele[j]) {
                            // 2 alleles are the same, and equal to the first allele
                            this.out.write("\t0");
                        } else if (m1 == this.m2 && this.m1 != this.RefAllele[j]) {
                            // 2 alleles are the same, and equal to the second allele
                            this.out.write("\t2");
                        } else if (this.m1 != this.m2) {
                            // 2 alleles are not the same
                            this.out.write("\t1");
                        }
                    }
                }
                this.out.write("\n");
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void HmpToPlink (String GD_path) throws IOException{
        System.out.println("H -> P" + " GD: " + GD_path);
        this.size_GD = getCountOfLines(GD_path);
        // Setup reader
        setReader(GD_path);
        // Read first line, catch sep, linelength and nCount
        this.sep = getSep(GD_path, 0);
        this.headerLine = getNthLine(GD_path, 0, this.sep);
        this.lineLength = this.headerLine.length;
        // See the if the first marker show only one character
        boolean isOneChar = this.reader.readLine().replaceAll("\"", "").split(sep)[11].length() == 1;
        String valueNA = (isOneChar) ? "N" : "NA";
        // Get n and m count (the first 11 columns are meta information)
        this.nCount = this.lineLength - 11;
        this.mCount = this.size_GD - 1;
        this.isKeep = new boolean[this.mCount];
        // Get taxa
        this.taxa = getTaxa(true, 11, this.nCount, this.headerLine);
        // Initial first allele
        this.RefAllele = new char[this.mCount];
        // ========= ========= ========= ========= QC ========= ========= ========= =========
        if (this.rateMAF > 0 || this.rateNA > 0) {
            this.isKeep = doQCHapmap(GD_path, valueNA, isOneChar);
            for(boolean b : this.isKeep)
                this.mCountQC += b ? 1 : 0;
        }
        // ========= ========= ========= ========= Map ========= ========= ========= =========
        System.out.println("Converting Map file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
        iniProgress("Converting Map File",
                String.format("Marker %d ~ %d : ", 1, mCount));
        this.lastPosition = 0;
        while (this.lastPosition < mCount) {
            updateProgress(this.lastPosition, mCount);
            this.table_GD = getNLines(this.sub_m, this.sep);
            this.currentWindow = table_GD.length;
            for (int i = 0; i < this.currentWindow; i ++) {
                if (!this.isKeep[this.lastPosition + i])
                    continue;
                out.write(table_GD[i][2] + "\t" + table_GD[i][0] + "\t0\t" + table_GD[i][3] + "\n");
                //marker.add(table_GD[i][2]); no need in ped file
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        out.flush();
        out.close();
        // ========= ========= ========= ========= GD ========= ========= ========= =========
        iniProgress("Converting Genotype File",
                String.format("Sample %d ~ %d : ", 1, nCount));
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.ped");
        this.lastPosition = 11;
        while (lastPosition < lineLength) {
            updateProgress(lastPosition, lineLength);
            // Skip the first line if contains header
            resetToNthLine(GD_path, 1);
            // subN by M matrix
            this.table_GD = getSubTransposedGD(this.lastPosition, this.sub_n, this.mCount, this.lineLength, this.sep);
            this.currentWindow = this.table_GD.length;
            for (int i = 0; i < this.currentWindow; i ++) {
                out.write(taxa.get(lastPosition - 11 + i) + "\t" + taxa.get(lastPosition - 11 + i) + "\t0\t0\t0\t-9\t");
                if (isOneChar) {
                    for (int j = 0; j < this.mCount; j ++) {
                        if (!this.isKeep[j])
                            continue;
                        this.m1 = this.table_GD[i][j].charAt(0);
                        if (this.m1 == 'N' || this.m1 == 'n') {
                            // missing data, coded as 0 0
                            this.out.write("\t0 0");
                        } else {
                            // record the exact value
                            this.out.write("\t" + this.m1 + " " + this.m1);
                        }
                    }
                } else {
                    for (int j = 0; j < this.mCount; j ++) {
                        if (!this.isKeep[j])
                            continue;
                        this.m1 = this.table_GD[i][j].charAt(0);
                        this.m2 = this.table_GD[i][j].charAt(1);
                        if (this.m1 == 'N' || this.m2 == 'N' || this.m1 == 'n' || this.m2 == 'n') {
                            // missing data, coded as 0 0
                            this.out.write("\t0 0");
                        } else {
                            // record the exact value
                            this.out.write("\t" + this.m1 + " " + this.m2);
                        }
                    }
                }
                this.out.write("\n");
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void VCFToNum(String GD_path) throws IOException{
        int GTindex = -1;
        System.out.println("V -> N" + " GD: " + GD_path);
        this.size_GD = getCountOfLines(GD_path);
        // Setup reader
        setReader(GD_path);
        // Find redundant, store headerline as "tempread" and push pointer to the first marker line
        // vcfAnnotation is 4 if 3## and 1 header
        this.vcfAnnotation = getCountOfAnnotation();
        // Get header as "headerline" from "tempred" and find sep and linelength
        this.sep = getSep(GD_path, this.vcfAnnotation - 1);
        this.headerLine = getNthLine(GD_path, this.vcfAnnotation - 1, this.sep);
        this.lineLength = this.headerLine.length;
        // Get n and m count (the first 9 columns are meta information)
        this.nCount = this.lineLength - 9;
        this.mCount = this.size_GD - this.vcfAnnotation;
        this.taxa = getTaxa(true, 9, this.nCount, this.headerLine);
        // Initialize alt
        this.hasTwoAlt = new boolean[this.mCount];
        // Get GT position
        String[] FORMAT = this.reader.readLine().split(this.sep)[8].split(":");
        for (int i = 0; i < FORMAT.length; i ++) {
            if (FORMAT[i].equals("GT")) {
                GTindex = i;
                break;
            }
        }
        // ========= ========= ========= ========= Map ========= ========= ========= =========
        resetToNthLine(GD_path, this.vcfAnnotation);
        iniProgress("Converting Map File",
                String.format("Marker %d ~ %d : ", 1, this.mCount));
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
        this.out.write("SNP\tChromosome\tPosition\n");
        this.lastPosition = 0;
        while (this.lastPosition < this.mCount) {
            updateProgress(this.lastPosition, this.mCount);
            this.table_GD = getNLines(this.sub_m, this.sep);
            this.currentWindow = this.table_GD.length;
            for (int i = 0; i < this.currentWindow; i++) {
                this.out.write(this.table_GD[i][2] + "\t" + this.table_GD[i][0] + "\t" + this.table_GD[i][1] + "\n");
                this.marker.add(this.table_GD[i][2]);
                this.hasTwoAlt[i] = this.table_GD[i][4].contains(",");
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
        // ========= ========= ========= ========= GD ========= ========= ========= =========
        // output marker names as 1st line
        this.out.write("taxa");
        for (int i = 0; i < this.mCount; i ++)
            this.out.write("\t" + this.marker.get(i));
        this.out.write("\n");
        iniProgress("Converting Genotype File",
                String.format("Sample %d ~ %d : ", 1, this.mCount));
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
        this.lastPosition = 9;
        while (this.lastPosition < this.lineLength) {
            updateProgress(this.lastPosition, this.lineLength);
            // Reset reader
            resetToNthLine(GD_path, this.vcfAnnotation);
            // subN by M matrix
            this.table_GD = getSubTransposedGD(this.lastPosition, this.sub_n, this.mCount, this.lineLength, this.sep);
            this.currentWindow = this.table_GD.length;
            for (int i = 0; i < this.currentWindow; i ++) {
                this.out.write(this.taxa.get(this.lastPosition - 9 + i));
                for (int j = 0; j < this.mCount; j ++) {
                    this.tempRead = this.table_GD[i][j].split(":")[GTindex];
                    this.m1 = this.tempRead.charAt(0);
                    this.m2 = this.tempRead.charAt(2);
                    // missing data
                    if (this.m1 == '.' || this.m2 == '.') {
                        this.out.write(this.isNAFill? "\t1" : "\tNA");
                        // Heterozygous
                    } else if (this.m1 != this.m2) {
                        this.out.write("\t1");
                        // If has two alts
                    } else if (this.hasTwoAlt[j]) {
                        switch (this.m1) {
                            case '0':
                                // Homozygous, and are ref allele
                                this.out.write("\t0");
                                break;
                            case '1':
                                // Homozygous, and are 1st alt allele
                                this.out.write("\t1");
                                break;
                            case '2':
                                // Homozygous, and are 2nd alt allele
                                this.out.write("\t2");
                                break;
                        }
                        // If has one alt
                    } else {
                        switch (this.m1) {
                            case '0' :
                                // Homozygous, and are ref allele
                                this.out.write("\t0");
                                break;
                            case '1' :
                                // Homozygous, and are alt allele
                                this.out.write("\t2");
                                break;
                        }
                    }
                }
                this.out.write("\n");
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void VCFToPlink(String GD_path) throws IOException{
        int GTindex = -1;
        System.out.println("V -> N" + " GD: " + GD_path);
        this.size_GD = getCountOfLines(GD_path);
        // Setup reader
        setReader(GD_path);
        // Find redundant, store headerline as "tempread" and push pointer to the first marker line
        // vcfAnnotation is 4 if 3## and 1 header
        this.vcfAnnotation = getCountOfAnnotation();
        // Get header as "headerline" from "tempred" and find sep and linelength
        this.sep = getSep(GD_path, this.vcfAnnotation - 1);
        this.headerLine = getNthLine(GD_path, this.vcfAnnotation - 1, this.sep);
        this.lineLength = this.headerLine.length;
        // Get n and m count (the first 9 columns are meta information)
        this.nCount = this.lineLength - 9;
        this.mCount = this.size_GD - this.vcfAnnotation;
        this.taxa = getTaxa(true, 9, this.nCount, this.headerLine);
        // Initialize alt
        this.hasTwoAlt = new boolean[this.mCount];
        // Get GT position
        String[] FORMAT = this.reader.readLine().split(this.sep)[8].split(":");
        for (int i = 0; i < FORMAT.length; i ++) {
            if (FORMAT[i].equals("GT")) {
                GTindex = i;
                break;
            }
        }
        // ========= ========= ========= ========= Map ========= ========= ========= =========
        resetToNthLine(GD_path, this.vcfAnnotation);
        iniProgress("Converting Map File",
                String.format("Marker %d ~ %d : ", 1, this.mCount));
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
        this.lastPosition = 0;
        while (this.lastPosition < this.mCount) {
            updateProgress(this.lastPosition, this.mCount);
            this.table_GD = getNLines(this.sub_m, this.sep);
            this.currentWindow = this.table_GD.length;
            for (int i = 0; i < this.currentWindow; i ++) {
                this.out.write(this.table_GD[i][0] + "\t" + this.table_GD[i][2] + "\t0\t" + this.table_GD[i][1] + "\n");
                this.marker.add(this.table_GD[i][2]);
                this.hasTwoAlt[i] = this.table_GD[i][4].contains(",");
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
        // ========= ========= ========= ========= GD ========= ========= ========= =========
        // output marker names as 1st line
        this.out.write("taxa");
        for (int i = 0; i < this.mCount; i ++)
            this.out.write("\t" + this.marker.get(i));
        this.out.write("\n");
        iniProgress("Converting Genotype File",
                String.format("Sample %d ~ %d : ", 1, this.nCount));
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
        this.lastPosition = 9;
        while (this.lastPosition < this.lineLength) {
            updateProgress(this.lastPosition, this.lineLength);
            // Reset reader
            resetToNthLine(GD_path, this.vcfAnnotation);
            // subN by M matrix
            this.table_GD = getSubTransposedGD(this.lastPosition, this.sub_n, this.mCount, this.lineLength, this.sep);
            this.currentWindow = this.table_GD.length;
            for (int i = 0; i < this.currentWindow; i ++) {
                this.out.write(this.taxa.get(this.lastPosition - 9 + i));
                for (int j = 0; j < this.mCount; j ++) {
                    this.tempRead = this.table_GD[i][j].split(":")[GTindex];
                    this.m1 = this.tempRead.charAt(0);
                    this.m2 = this.tempRead.charAt(2);
                    // missing data, imputed as 1
                    if (this.m1 == '.' || this.m2 == '.') {
                        this.out.write(this.isNAFill? "\tA T" : "\t0 0");
                        // If has two alts
                    } else if (this.hasTwoAlt[j]) {
                        // Heterozygous
                        if (this.m1 != this.m2) {
                            switch (this.m1) {
                                case '0':
                                    this.out.write("\tA ");
                                    break;
                                case '1':
                                    this.out.write("\tT ");
                                    break;
                                case '2':
                                    this.out.write("\tG ");
                                    break;
                            }
                            switch (this.m2) {
                                case '0':
                                    this.out.write("A");
                                    break;
                                case '1':
                                    this.out.write("T");
                                    break;
                                case '2':
                                    this.out.write("G");
                                    break;
                            }
                            // Homozygous
                        } else {
                            switch (this.m1) {
                                case '0':
                                    // Homozygous, and are ref allele
                                    this.out.write("\tA A");
                                    break;
                                case '1':
                                    // Homozygous, and are 1st alt allele
                                    this.out.write("\tT T");
                                    break;
                                case '2':
                                    // Homozygous, and are 2nd alt allele
                                    this.out.write("\tG G");
                                    break;
                            }
                        }
                        // If has one alt
                    } else {
                        // Heterozygous
                        if (this.m1 != this.m2) {
                            this.out.write("\tA T");
                            // Homozygous
                        } else {
                            switch (this.m1) {
                                case '0' :
                                    // Homozygous, and are ref allele
                                    this.out.write("\tA A");
                                    break;
                                case '1' :
                                    // Homozygous, and are alt allele
                                    this.out.write("\tT T");
                                    break;
                            }
                        }
                    }
                }
                this.out.write("\n");
            }
            this.lastPosition += this.currentWindow;
        }
        doneProgress();
        this.out.flush();
        this.out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void PlinkToNum(String GD_path, String GM_path) throws IOException{
        System.out.println("P -> N" + " GD: " + GD_path + " GM: " + GM_path);
        this.size_GD = getCountOfLines(GD_path);
        this.size_GM = getCountOfLines(GM_path);
        this.nCount = this.size_GD;
        this.mCount = this.size_GM;
        this.RefAllele = new char[this.mCount];
        // GD
        try {
            // Setup reader as 'reader', and writer as 'out'
            setReader(GD_path);
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
            // Progress message
            iniProgress("Converting Genotype File",
                    String.format("Sample %d ~ %d : ", 1, this.nCount));
            // Start conversion
            this.lastPosition = 0;
            while ((this.tempRead = this.reader.readLine()) != null) {
                updateProgress(this.lastPosition++, size_GD);
                // Get rid of first 6 columns and all space (\s)
                String rowTaxa = this.tempRead.replaceAll("^(\\S*)", "");
                Matcher mtchr = Pattern.compile("\\s(\\S+)\\s").matcher(rowTaxa);
                mtchr.find();
                rowTaxa = mtchr.group().replaceAll("\\s*", "");
                this.tempRead = this.tempRead.replaceAll("^(\\S*\\s){6}", "").replaceAll("\\s*", "");
                // Write taxa
                this.out.write(rowTaxa + "\t");
                // Convert this line (individual)
                for (int i = 0; i < this.mCount; i++) {
                    // Read alleles
                    this.m1 = this.tempRead.charAt(2 * i);
                    this.m2 = this.tempRead.charAt(2 * i + 1);
                    this.homo = this.m1 == this.m2;
                    this.m1isNA = this.m1 == '0';
                    this.m2isNA = this.m2 == '0';
                    // Assign reference allele
                    if (this.RefAllele[i] == '\u0000') {
                        if (this.m1 != '0')
                            this.RefAllele[i] = this.m1;
                        else if (m2 != '0')
                            this.RefAllele[i] = this.m2;
                    }
                    this.isRefAllele = this.m1 == this.RefAllele[i];
                    // Write genotype
                    if (this.homo && this.isRefAllele) {
                        // 2 alleles are the same, and equal to the reference allele
                        this.out.write("0");
                    } else if (this.homo && !this.isRefAllele && !this.m1isNA) {
                        // 2 alleles are the same, but not equal to the first allele and are not a missing value
                        this.out.write("2");
                    } else if (this.m1isNA || this.m2isNA) {
                        // missing data, imputed as 1
                        this.out.write(this.isNAFill? "1" : "NA");
                    } else if (!this.homo) {
                        // 2 alleles are not the same
                        this.out.write("1");
                    }
                    if (i + 1 == this.mCount)
                        this.out.write("\n");
                    else
                        this.out.write("\t");

                }
            }
            doneProgress();
            this.out.flush();
            this.out.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        // ========= ========= ========= ========= Map ========= ========= ========= =========
        try{
            this.sep = getSep(GM_path, 0);
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
            this.out.write("SNP\tChromosome\tPosition\n");
            iniProgress("Converting Map File",
                    String.format("Marker %d ~ %d : ", 1, this.mCount));
            this.lastPosition = 0;
            while (this.lastPosition < this.size_GM) {
                updateProgress(this.lastPosition, this.size_GM);
                this.table_GM = getNLines(this.sub_m, this.sep);
                this.currentWindow = table_GM.length;
                for (int row = 0; row < this.currentWindow; row++) {
                    out.write(table_GM[row][1] + "\t" + table_GM[row][0] + "\t" + table_GM[row][3] + "\n");
                }
                this.lastPosition += this.currentWindow;
            }
            doneProgress();
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    // ================================== Setup writer and reader ==================================
    private void setWriter (String file) throws IOException {
        out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
    }
    private void setReader (String file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }
    // ======================================= Read file =======================================
    // Extract 'nLines' of lines from the reader
    private String[][] getNLines (int nLines, String sep) throws IOException {
        int index = 0;
        String readline = null;
        String[][] lines = new String[nLines][];
        while (index < nLines && (readline = reader.readLine()) != null)
            lines[index++] = readline.replaceAll("\"", "").split(sep);
        // In case more space is created than lines needed
        if (index < nLines - 1)
            lines = Arrays.copyOf(lines, index);
        return lines;
    }
    // Extract genotype table from m x n to n x m
    private String[][] getSubTransposedGD (int lastPosition, int sizeN, int sizeM, int lineLen, String sep) throws IOException {
        int index = 0, upperBound = Math.min(lastPosition + sizeN, lineLen);
        String readline = null;
        // Transposed dimension (sub_n x m)
        String[][] tableGD = new String[upperBound - lastPosition][sizeM];
        while ((readline = reader.readLine()) != null) {
            String[] tempLines = readline.replaceAll("\"", "").split(sep);
            // Hapmap Specific. If is the first round, catch the RefAllele if necessary
            if (lastPosition == 11)
                RefAllele[index] = tempLines[11].charAt(0);
            for (int i = lastPosition; i < upperBound; i ++)
                tableGD[i - lastPosition][index] = tempLines[i];
            index ++;
        }
        return tableGD;
    }
    // ======================================= Set separator =======================================
    private String getSep (String path, int whichLine) throws IOException {
        resetToNthLine(path, whichLine);
        String tempLine = reader.readLine();
        // by tab
        String[] tempLines = tempLine.replaceAll("\"", "").split("\t");
        // by space
        if (tempLines.length <= 1)
            tempLines = tempLine.replaceAll("\"", "").split(" +");
        else
            return "\t";
        // csv
        if (tempLines.length <= 1)
            return ",";
        else
            return " +";
    }
    // ======================================= VCF functions =======================================
    private int getCountOfAnnotation () throws IOException {
        int index = 0;
        String readline;
        while ((readline = this.reader.readLine()) != null) {
            // Skip the line initial with ## and count the number
            if (readline.startsWith("##"))
                index ++;
            else
                // Found the header and add 1 to the marker line
                return (index + 1);
        }
        return -1;
    }
    // ======================================= Misc functions =======================================
    // Get how many lines exited in the file named 'filename'
    private int getCountOfLines(String filename) throws IOException {
        Path path = Paths.get(filename);
        long lineCount = Files.lines(path).count();
        return (int) lineCount;
    }
    // Reset the reader and skip 'nLine' line
    private void resetToNthLine (String path, int nLine) throws IOException {
        setReader(path);
        for (int i = 0; i < nLine; i ++)
            this.reader.readLine();
    }
    // Get specific line(string array) from the file
    private String[] getNthLine (String path, int nLine, String sep) throws IOException {
        resetToNthLine(path, nLine);
        return this.reader.readLine().replaceAll("\"", "").split(sep);
    }
    // Skip the first nMetaCol items from headerline, and return taxa names to 'taxaTemp'
    private ArrayList<String> getTaxa (boolean hasHeader, int nMetaCol, int nCount, String[] headerlines) {
        ArrayList<String> taxaTemp = new ArrayList<>();
        if (hasHeader) {
            for (int i = 0; i < nCount; i ++)
                taxaTemp.add(headerlines[i + nMetaCol]);
        } else {
            for (int i = 0; i < nCount; i ++)
                taxaTemp.add("Sample " + (i + 1));
        }
        return taxaTemp;
    }

    // ======================================= Quality Control =======================================
    private boolean[] doQCGenStdio (String GD_path, char[] codeAB) throws IOException {
        boolean[] iskeep = new boolean[this.mCount];
        this.idxTemp = 0;
        resetToNthLine(GD_path, 1);
        iniProgress("Quality Control",
                String.format("Marker %d ~ %d : ", 1, this.mCount));
        while ((this.tempRead = this.reader.readLine()) != null) {
            updateProgress(this.idxTemp, this.mCount);
            String[] arrayLine = this.tempRead.replaceAll("\"", "").split(this.sep);
            arrayLine = Arrays.copyOfRange(arrayLine, 2, this.mCount + 2);
            // Count all the elements
            Map<String, Long> tableMap = Arrays.asList(arrayLine).stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            // Fill NC if no missin gvalues
            if (!tableMap.containsKey("NC"))
                tableMap.put("NC", 0L);
            double maf = 0;
            double na = 0;
            switch (codeAB[this.idxTemp]) {
                // AB, BB or AB only
                case '0':
                    // AB
                    if (tableMap.size() == 2)
                        maf = 0;
                        // AB, BB
                    else
                        maf = (tableMap.get("BB"))/(double)(this.nCount - tableMap.get("NC"));
                    break;
                // AA, AB, BB
                case '1':
                    maf = (tableMap.get("AA")*2 + tableMap.get("AB"))/((double)((this.nCount - tableMap.get("NC")) * 2));
                    break;
                // AA, AB
                case '2':
                    maf = (tableMap.get("AA"))/(double)(this.nCount - tableMap.get("NC"));
                    break;
            }
            maf = Math.min(maf, 1 - maf);
            na = tableMap.get("NC")/((double)(this.nCount));
            this.isKeep[this.idxTemp++] = (maf > this.rateMAF) && (na < this.rateNA);
        }
        doneProgress();
        return iskeep;
    }

    private boolean[] doQCHapmap (String GD_path, String valueNA, boolean isOneChar) throws IOException {
        boolean[] iskeep = new boolean[this.mCount];
        this.idxTemp = 0;
        resetToNthLine(GD_path, 1);
        iniProgress("Quality Control",
                String.format("Marker %d ~ %d : ", 1, this.mCount));
        while ((this.tempRead = this.reader.readLine()) != null) {
            updateProgress(this.idxTemp, this.mCount);
            String a1 = "nan";
            String a2 = "nan";
            String[] arrayLine = this.tempRead.replaceAll("\"", "").split(this.sep);
            arrayLine = Arrays.copyOfRange(arrayLine, 11, this.lineLength);
            // Count all the elements
            Map<String, Long> tableMap = Arrays.asList(arrayLine).stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            // Fill NC if no missin gvalues
            if (!tableMap.containsKey(valueNA))
                tableMap.put(valueNA, 0L);
            // QC
            double maf = 0;
            double na = 0;
            if (isOneChar) {
                // Found the ref allele
                for (Map.Entry<String, Long> entry : tableMap.entrySet()) {
                    if (entry.getKey().equals(valueNA))
                        continue;
                    a1 = entry.getKey().substring(0, 1);
                    break;
                }
                // calculate maf
                switch (tableMap.size()) {
                    // N, A
                    case 2:
                        maf = 0;
                        break;
                    // N, A, B
                    case 3:
                        maf = (tableMap.get(a1))/(double)(this.nCount - tableMap.get(valueNA));
                        break;
                }
            } else {
                // Found the ref allele
                for (Map.Entry<String, Long> entry : tableMap.entrySet()) {
                    if (entry.getKey().equals(valueNA))
                        continue;
                    if (a1.equals("nan")) {
                        a1 = entry.getKey().substring(0, 1);
                        a2 = entry.getKey().substring(1, 2);
                    } else if (a1.equals(a2)) {
                        if (entry.getKey().substring(0, 1).equals(a1))
                            a2 = entry.getKey().substring(1, 2);
                        else
                            a2 = entry.getKey().substring(0, 1);
                    }
                    else
                        break;
                }
                // calculate maf
                switch (tableMap.size()) {
                    // NA, {AA, BB, AB}
                    case 2:
                        // homozygous
                        if (a1.equals(a2))
                            maf = 1;
                            // heterozygous
                        else
                            maf = 0.5;
                        break;
                    // AA AB, AA BB, AB BB
                    case 3:
                        if (tableMap.containsKey(a1 + a1) && tableMap.containsKey(a1 + a2))
                            maf = (tableMap.get(a1 + a1)*2 + tableMap.get(a1 + a2))/((double)((this.nCount - tableMap.get(valueNA)) * 2));
                        else if (tableMap.containsKey(a1 + a1) && tableMap.containsKey(a2 + a2))
                            maf = (tableMap.get(a1 + a1))/((double)(this.nCount - tableMap.get(valueNA)));
                        else if (tableMap.containsKey(a2 + a2) && tableMap.containsKey(a1 + a2))
                            maf = (tableMap.get(a2 + a2)*2 + tableMap.get(a1 + a2))/((double)((this.nCount - tableMap.get(valueNA)) * 2));
                        break;
                    // AA AB BB
                    case 4:
                        maf = (tableMap.get(a1 + a1)*2 + tableMap.get(a1 + a2))/((double)((this.nCount - tableMap.get(valueNA)) * 2));
                        break;
                }
            }
            maf = Math.min(maf, 1 - maf);
            na = tableMap.get(valueNA)/((double)(this.nCount));
            iskeep[this.idxTemp++] = (maf > this.rateMAF) && (na < this.rateNA);
        }
        doneProgress();
        return iskeep;
    }
}
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class iPatConverter {
    int sub_n = 32, sub_m = 8192;
    String sep = "\t";
    // info
    String pathGD;
    String pathGM;
    FileFormat InputFormat;
    FileFormat OutputFormat;
    // Buffer read and write
    BufferedReader reader;
    FileWriter fr, mfr;
    BufferedWriter br, mbr;
    PrintWriter out, mout;
    // Counter
    int mCount, nCount, lineLength;
    int size_GD, count_GD = 0,
            size_GM, count_GM = 0,
            currentCount = 0;
    int vcfAnnotation = -1;
    // Sample information
    ArrayList<String> taxa = new ArrayList<String>();
    ArrayList<String> marker = new ArrayList<String>();
    // Marker information
    char[] RefAllele;
    boolean[] hasTwoAlt;
    char m1, m2;
    boolean homo, isRefAllele, m1isNA, m2isNA;
    // Space for matrix of gd and gm, headerline
    String[][] table_GD,
            table_GM;
    // temp read
    String[] headerline = null;
    String tempread = null;

    //        args = new String[]{"-in", "hmp", "-out", "num", "-GD", "/Users/jameschen/sam.hmp"};
    public iPatConverter (FileFormat formatIn, FileFormat formatOut, String pathGD, String pathGM) throws IOException {
        System.out.println("File Converter for iPAT");
        this.InputFormat = formatIn;
        this.OutputFormat = formatOut;
        this.pathGD = pathGD;
        this.pathGM = pathGM;
        if (this.InputFormat != this.OutputFormat) {
            switch (InputFormat) {
                case Numeric:
                    NumToPlink(pathGD, pathGM);
                    break;
                case Hapmap:
                    switch (OutputFormat) {
                        case Numeric:
                            HapToNum(pathGD);
                            break;
                        case PLINK:
                            HapToPlink(pathGD);
                            break;
                    }
                    break;
                case VCF:
                    switch (OutputFormat) {
                        case Numeric:
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
            }
        }
        System.out.println("Conversion Done!\n");
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void NumToPlink (String GD_path, String GM_path) throws IOException {
        System.out.println("N -> P" + " GD: " + GD_path + " GM: " + GM_path);
        size_GD = getCountofLines(GD_path);
        size_GM = getCountofLines(GM_path);
        boolean nBym = Math.abs(size_GD - size_GM) > 1;
        // Map
        boolean header_GM = false;
        try {
            System.out.println("Converting Map file");
            setReader(GM_path);
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
            while (count_GM < size_GM) {
                table_GM = readNumPLINKMap();
                header_GM = !(table_GM[0][1].length() == 1);
                currentCount = table_GM.length;
                for (int row = header_GM ? 1 : 0; row < currentCount; row ++) {
                    progressbar(String.format("Marker %d ~ %d : ", count_GM + 1, count_GM + currentCount), row, currentCount);
                    out.write(table_GM[row][1] + "\t" + table_GM[row][0] + "\t0\t" + table_GM[row][2] + "\n");
                }
                System.out.println(String.format("Marker %d ~ %d : ", count_GM + 1, count_GM + currentCount) + "Done                    ");
                count_GM += currentCount;
            }
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        // Ped
        try {
            System.out.println("Converting Genotype file");
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.ped");
            if (nBym) {
                setReader(GD_path);
                NumToPlinkNByM();
            } else
                NumToPlinkMByN(GD_path);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    private void NumToPlinkNByM () throws IOException {
        boolean header_GD = false, contain_taxa = false;
        // While haven't reach the end of the files
        while (count_GD < size_GD) {
            // Read part of the file
            table_GD = readNumGD();
            // Get number of lines in this read
            currentCount = table_GD.length;
            // If is the first read of the file
            if (count_GD == 0) {
                // See is singular number or a string
                header_GD = !(table_GD[0][1].length() == 1);
                contain_taxa = !(table_GD[1][0].length() == 1);
            }
            for (int row = 0; row < currentCount; row ++) {
                progressbar(String.format("Sample %d ~ %d : ", count_GD + 1, count_GD + currentCount), row, currentCount);
                // If is the first read of the file, not necessarily starts from the first line
                if (count_GD == 0 && header_GD && row == 0)
                    continue;
                // If contain taxa, give the taxa name. Otherwise, name it initail as "Sample_"
                if (contain_taxa)
                    out.write(table_GD[row][0] + "\t" + table_GD[row][0] + "\t0\t0\t0\t0\t");
                else
                    out.write("Family_" + row + "\t" + "Sample_" + row + "\t0\t0\t0\t0\t");
                // If contain taxa, starts from the second column
                for (int col = contain_taxa ? 1 : 0; col < table_GD[row].length; col ++) {
                    switch (table_GD[row][col]) {
                        case "0" :
                            out.write("A A");
                            break;
                        case "1" :
                            out.write("A T");
                            break;
                        case "2" :
                            out.write("T T");
                            break;
                    }
                    // If haven't reached the end of the row, seperate with tab
                    if (col != table_GD[row].length - 1)
                        out.write("\t");
                }
                // For each row, end it with newline
                out.write("\n");
            }
            System.out.println(String.format("Sample %d ~ %d : ", count_GD + 1, count_GD + currentCount) + "Done                    ");
            count_GD += currentCount;
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
        // Search until reach the end of the row (marker)
        while (lastPosition < lineLength) {
            // Go through the part of each row (marker)
            while (countLine < size_GD) {
                progressbar(String.format("Read Sample %d ~ %d : ", (lastPosition / 2)  + 1, Math.min((lastPosition / 2)  + 1 + (sub_n - 1), lineLength / 2)),
                        (int) countLine, size_GD);
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
            System.out.println(String.format("Read Sample %d ~ %d : ", (lastPosition / 2)  + 1, Math.min((lastPosition / 2)  + 1 + (sub_n - 1), lineLength / 2)) + "Done                         ");
            // Write the temp into the output file (tempIndex is the index of last letter, so plus one)
            for (int n = 0; n < tempIndex + 1; n ++) {
                progressbar(String.format("Write Sample %d ~ %d : ", (lastPosition / 2)  + 1, Math.min((lastPosition / 2)  + 1 + (sub_n - 1), lineLength / 2)),
                        n, tempIndex);
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
            System.out.println(String.format("Write Sample %d ~ %d : ", (lastPosition / 2)  + 1, Math.min((lastPosition / 2)  + 1 + (sub_n - 1), lineLength / 2)) + "Done                          ");
            countLine = 0;
            lastPosition += readSize;
        }
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void HapToNum (String GD_path) throws IOException {
        System.out.println("H -> N" + " GD: " + GD_path );
        size_GD = getCountofLines(GD_path);
        // Setup reader
        setReader(GD_path);
        // Read first line, catch sep and nCount
        setHmpSep();
        // It's col chrom, check if is a chromosome number (digit)
        boolean header = !headerline[2].matches("^\\d+$");
        // Get taxa
        getTaxa(header, true);
        // Reset reader if without header
        if (!header) {
            setReader(GD_path);
            mCount = size_GD;
        } else
            mCount = size_GD - 1;
        // Initial first allele
        RefAllele = new char[mCount];
        // GM starts
        System.out.println("Converting Map file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
        out.write("SNP\tChromosome\tPosition\n");
        while (count_GD < mCount) {
            table_GD = readHmpVcfMap();
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount), i, currentCount);
                out.write(table_GD[i][0] + "\t" + table_GD[i][2] + "\t" + table_GD[i][3] + "\n");
                marker.add(table_GD[i][0]);
            }
            System.out.println(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount) + "Done                    ");
            count_GD += currentCount;
        }
        out.flush();
        out.close();
        // GD starts
        System.out.println("Converting Genotype file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
        // output marker names as 1st line
        out.write("taxa");
        for (int i = 0; i < mCount; i ++)
            out.write("\t" + marker.get(i));
        out.write("\n");
        int lastPosition = 11;
        while (lastPosition < lineLength) {
            // Reset reader
            setReader(GD_path);
            // Skip the first line if contains header
            if (header)
                reader.readLine();
            // subN by M matrix
            table_GD = readHmpGD(lastPosition);
            boolean isOneChar = table_GD[0][0].length() == 1;
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Sample %d ~ %d : ", (lastPosition - 11) + 1, (lastPosition - 11) + currentCount), i, currentCount);
                out.write(taxa.get(lastPosition - 11 + i));
                if (isOneChar) {
                    for (int j = 0; j < mCount; j ++) {
                        m1 = table_GD[i][j].charAt(0);
                        if (m1 == 'N' || m1 == 'n') {
                            // missing data
                            out.write("\tNA");
                        } else if (m1 == RefAllele[j]) {
                            // 2 alleles are the same, and equal to the first allele
                            out.write("\t0");
                        } else if (m1 != RefAllele[j]) {
                            // 2 alleles are the same, and equal to the second allele
                            out.write("\t2");
                        } else {
                            // 2 alleles are not the same
                            out.write("\t1");
                        }
                    }
                } else {
                    for (int j = 0; j < mCount; j ++) {
                        m1 = table_GD[i][j].charAt(0);
                        m2 = table_GD[i][j].charAt(1);
                        if (m1 == 'N' || m2 == 'N' || m1 == 'n' || m2 == 'n') {
                            // missing data
                            out.write("\tNA");
                        } else if (m1 == m2 && m1 == RefAllele[j]) {
                            // 2 alleles are the same, and equal to the first allele
                            out.write("\t0");
                        } else if (m1 == m2 && m1 != RefAllele[j]) {
                            // 2 alleles are the same, and equal to the second allele
                            out.write("\t2");
                        } else if (m1 != m2) {
                            // 2 alleles are not the same
                            out.write("\t1");
                        }
                    }
                }
                out.write("\n");
            }
            System.out.println(String.format("Sample %d ~ %d : ",  (lastPosition - 11) + 1, (lastPosition - 11) + currentCount) + "Done                    ");
            lastPosition += currentCount;
        }
        out.flush();
        out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void HapToPlink (String GD_path) throws IOException{
        System.out.println("H -> P" + " GD: " + GD_path);
        size_GD = getCountofLines(GD_path);
        // Setup reader
        setReader(GD_path);
        // Read first line, catch sep and nCount
        setHmpSep();
        // It's col chrom, check if is a chromosome number (digit)
        boolean header = !headerline[2].matches("^\\d+$");
        // Get taxa
        getTaxa(header, true);
        // Reset reader if without header
        if (!header) {
            setReader(GD_path);
            mCount = size_GD;
        } else
            mCount = size_GD - 1;
        // Initial first allele
        RefAllele = new char[mCount];
        // GM starts
        System.out.println("Converting Map file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
        while (count_GD < mCount) {
            table_GD = readHmpVcfMap();
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount), i, currentCount);
                out.write(table_GD[i][2] + "\t" + table_GD[i][0] + "\t0\t" + table_GD[i][3] + "\n");
                //marker.add(table_GD[i][2]); no need in ped file
            }
            System.out.println(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount) + "Done                    ");
            count_GD += currentCount;
        }
        out.flush();
        out.close();
        // GD starts
        System.out.println("Converting Genotype file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.ped");
        int lastPosition = 11;
        while (lastPosition < lineLength) {
            // Reset reader
            setReader(GD_path);
            // Skip the first line if contains header
            if (header)
                reader.readLine();
            // subN by M matrix
            table_GD = readHmpGD(lastPosition);
            boolean isOneChar = table_GD[0][0].length() == 1;
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Sample %d ~ %d : ", (lastPosition - 11) + 1, (lastPosition - 11) + currentCount), i, currentCount);
                out.write(taxa.get(lastPosition - 11 + i) + "\t" + taxa.get(lastPosition - 11 + i) + "\t0\t0\t0\t-9\t");
                if (isOneChar) {
                    for (int j = 0; j < mCount; j ++) {
                        m1 = table_GD[i][j].charAt(0);
                        if (m1 == 'N' || m1 == 'n') {
                            // missing data, coded as 0 0
                            out.write("\t0 0");
                        } else {
                            // record the exact value
                            out.write("\t" + m1 + " " + m1);
                        }
                    }
                } else {
                    for (int j = 0; j < mCount; j ++) {
                        m1 = table_GD[i][j].charAt(0);
                        m2 = table_GD[i][j].charAt(1);
                        if (m1 == 'N' || m2 == 'N' || m1 == 'n' || m2 == 'n') {
                            // missing data, coded as 0 0
                            out.write("\t0 0");
                        } else {
                            // record the exact value
                            out.write("\t" + m1 + " " + m2);
                        }
                    }
                }

                out.write("\n");
            }
            System.out.println(String.format("Sample %d ~ %d : ",  (lastPosition - 11) + 1, (lastPosition - 11) + currentCount) + "Done                    ");
            lastPosition += currentCount;
        }
        out.flush();
        out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void VCFToNum(String GD_path) throws IOException{
        int GTindex = -1;
        System.out.println("V -> N" + " GD: " + GD_path);
        size_GD = getCountofLines(GD_path);
        // Setup reader
        setReader(GD_path);
        // Find redundant, store headerline as "tempread" and push pointer to the first marker line
        // vcfAnnotation is 4 if 3## and 1 header
        vcfAnnotation = getAnnotationLine();
        // Get header as "headerline" from "tempred" and find sep and linelength
        setVcfSep();
        // Get GT position
        String[] FORMAT = reader.readLine().split(sep)[8].split(":");
        for (int i = 0; i < FORMAT.length; i ++) {
            if (FORMAT[i].equals("GT")) {
                GTindex = i;
                break;
            }
        }
        // Get taxa
        getTaxa(true, false);
        // Set mCount
        mCount = size_GD - vcfAnnotation;
        hasTwoAlt = new boolean[mCount];
        // GM starts
        resetToMarkerLine(GD_path, vcfAnnotation);
        System.out.println("Converting Map file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
        out.write("SNP\tChromosome\tPosition\n");
        while (count_GD < mCount) {
            table_GD = readHmpVcfMap();
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount), i, currentCount);
                out.write(table_GD[i][2] + "\t" + table_GD[i][0] + "\t" + table_GD[i][1] + "\n");
                marker.add(table_GD[i][2]);
                hasTwoAlt[i] = table_GD[i][4].contains(",") ? true : false;
            }
            System.out.println(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount) + "Done                    ");
            count_GD += currentCount;
        }
        out.flush();
        out.close();
        // GD starts
        System.out.println("Converting Genotype file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
        // output marker names as 1st line
        out.write("taxa");
        for (int i = 0; i < mCount; i ++)
            out.write("\t" + marker.get(i));
        out.write("\n");
        int lastPosition = 9;
        while (lastPosition < lineLength) {
            // Reset reader
            resetToMarkerLine(GD_path, vcfAnnotation);
            // subN by M matrix
            table_GD = readVcfGD(lastPosition);
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Sample %d ~ %d : ", (lastPosition - 9) + 1, (lastPosition - 9) + currentCount), i, currentCount);
                out.write(taxa.get(lastPosition - 9 + i));
                for (int j = 0; j < mCount; j ++) {
                    tempread = table_GD[i][j].split(":")[GTindex];
                    m1 = tempread.charAt(0);
                    m2 = tempread.charAt(2);
                    // missing data
                    if (m1 == '.' || m2 == '.') {
                        out.write("\tNA");
                        // Heterozygous
                    } else if (m1 != m2) {
                        out.write("\t1");
                        // If has two alts
                    } else if (hasTwoAlt[j]) {
                        switch (m1) {
                            case '0':
                                // Homozygous, and are ref allele
                                out.write("\t0");
                                break;
                            case '1':
                                // Homozygous, and are 1st alt allele
                                out.write("\t1");
                                break;
                            case '2':
                                // Homozygous, and are 2nd alt allele
                                out.write("\t2");
                                break;
                        }
                        // If has one alt
                    } else {
                        switch (m1) {
                            case '0' :
                                // Homozygous, and are ref allele
                                out.write("\t0");
                                break;
                            case '1' :
                                // Homozygous, and are alt allele
                                out.write("\t2");
                                break;
                        }
                    }
                }
                out.write("\n");
            }
            System.out.println(String.format("Sample %d ~ %d : ",  (lastPosition - 9) + 1, (lastPosition - 9) + currentCount) + "Done                    ");
            lastPosition += currentCount;
        }
        out.flush();
        out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void VCFToPlink(String GD_path) throws IOException{
        int GTindex = -1;
        System.out.println("V -> N" + " GD: " + GD_path);
        size_GD = getCountofLines(GD_path);
        // Setup reader
        setReader(GD_path);
        // Find redundant, store headerline as "tempread" and push pointer to the first marker line
        // vcfAnnotation is 4 if 3## and 1 header
        vcfAnnotation = getAnnotationLine();
        // Get header as "headerline" from "tempred" and find sep and linelength
        setVcfSep();
        // Get GT position
        String[] FORMAT = reader.readLine().split(sep)[8].split(":");
        for (int i = 0; i < FORMAT.length; i ++) {
            if (FORMAT[i].equals("GT")) {
                GTindex = i;
                break;
            }
        }
        // Get taxa
        getTaxa(true, false);
        // Set mCount
        mCount = size_GD - vcfAnnotation;
        hasTwoAlt = new boolean[mCount];
        // GM starts
        resetToMarkerLine(GD_path, vcfAnnotation);
        System.out.println("Converting Map file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
        while (count_GD < mCount) {
            table_GD = readHmpVcfMap();
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount), i, currentCount);
                out.write(table_GD[i][0] + "\t" + table_GD[i][2] + "\t0\t" + table_GD[i][1] + "\n");
                marker.add(table_GD[i][2]);
                hasTwoAlt[i] = table_GD[i][4].contains(",") ? true : false;
            }
            System.out.println(String.format("Marker %d ~ %d : ", count_GD + 1, count_GD + currentCount) + "Done                    ");
            count_GD += currentCount;
        }
        out.flush();
        out.close();
        // GD starts
        System.out.println("Converting Genotype file");
        setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
        // output marker names as 1st line
        out.write("taxa");
        for (int i = 0; i < mCount; i ++)
            out.write("\t" + marker.get(i));
        out.write("\n");
        int lastPosition = 9;
        while (lastPosition < lineLength) {
            // Reset reader
            resetToMarkerLine(GD_path, vcfAnnotation);
            // subN by M matrix
            table_GD = readVcfGD(lastPosition);
            currentCount = table_GD.length;
            for (int i = 0; i < currentCount; i ++) {
                progressbar(String.format("Sample %d ~ %d : ", (lastPosition - 9) + 1, (lastPosition - 9) + currentCount), i, currentCount);
                out.write(taxa.get(lastPosition - 9 + i));
                for (int j = 0; j < mCount; j ++) {
                    tempread = table_GD[i][j].split(":")[GTindex];
                    m1 = tempread.charAt(0);
                    m2 = tempread.charAt(2);
                    // missing data, imputed as 1
                    if (m1 == '.' || m2 == '.') {
                        out.write("\t0 0");
                        // If has two alts
                    } else if (hasTwoAlt[j]) {
                        // Heterozygous
                        if (m1 != m2) {
                            switch (m1) {
                                case '0':
                                    out.write("\tA ");
                                    break;
                                case '1':
                                    out.write("\tT ");
                                    break;
                                case '2':
                                    out.write("\tG ");
                                    break;
                            }
                            switch (m2) {
                                case '0':
                                    out.write("A");
                                    break;
                                case '1':
                                    out.write("T");
                                    break;
                                case '2':
                                    out.write("G");
                                    break;
                            }
                            // Homozygous
                        } else {
                            switch (m1) {
                                case '0':
                                    // Homozygous, and are ref allele
                                    out.write("\tA A");
                                    break;
                                case '1':
                                    // Homozygous, and are 1st alt allele
                                    out.write("\tT T");
                                    break;
                                case '2':
                                    // Homozygous, and are 2nd alt allele
                                    out.write("\tG G");
                                    break;
                            }
                        }
                        // If has one alt
                    } else {
                        // Heterozygous
                        if (m1 != m2) {
                            out.write("\tA T");
                            // Homozygous
                        } else {
                            switch (m1) {
                                case '0' :
                                    // Homozygous, and are ref allele
                                    out.write("\tA A");
                                    break;
                                case '1' :
                                    // Homozygous, and are alt allele
                                    out.write("\tT T");
                                    break;
                            }
                        }
                    }
                }
                out.write("\n");
            }
            System.out.println(String.format("Sample %d ~ %d : ",  (lastPosition - 9) + 1, (lastPosition - 9) + currentCount) + "Done                    ");
            lastPosition += currentCount;
        }
        out.flush();
        out.close();
    }
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    //==============================================================================//
    private void PlinkToNum(String GD_path, String GM_path) throws IOException{
        System.out.println("P -> N" + " GD: " + GD_path + " GM: " + GM_path);
        size_GD = getCountofLines(GD_path);
        size_GM = getCountofLines(GM_path);
        RefAllele = new char[size_GM];
        int index_first = 0;
        // GD
        try {
            System.out.println("Converting Genotype file");
            setReader(GD_path);
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
            while (count_GD < size_GD) {
                table_GD = readPED();
                currentCount = table_GD.length;
                mCount = (int) ((table_GD[0].length - 6) / (double) 2);
                RefAllele = new char[mCount];
                for (int row = 0; row < currentCount; row++) {
                    progressbar(String.format("Sample %d ~ %d : ", count_GD + 1, count_GD + currentCount), row, currentCount);
                    // Add taxa name
                    out.write(table_GD[row][1] + "\t");
                    for (int col = 6; col < table_GD[row].length; col += 2) {
                        index_first = (int) ((col - 6) / (double) 2);
                        // Catch the first allele of the marker
                        if (RefAllele[index_first] == '\u0000') {
                            RefAllele[index_first] = table_GD[row][col].charAt(0);
                            // If the allele is NA, wait for next sample to give the allele
                            if (RefAllele[index_first] == '0')
                                RefAllele[index_first] = '\u0000';
                        }
                        // Get 1st allele and 2nd allele of the marker in specific sample
                        m1 = table_GD[row][col].charAt(0);
                        m2 = table_GD[row][col + 1].charAt(0);
                        homo = m1 == m2;
                        isRefAllele = m1 == RefAllele[index_first];
                        m1isNA = m1 == '0';
                        m2isNA = m2 == '0';
                        if (homo && isRefAllele) {
                            // 2 alleles are the same, and equal to the first allele
                            out.write("0");
                        }
                        else if (homo && !isRefAllele && !m1isNA) {
                            // 2 alleles are the same, but not equal to the first allele and are not a missing value
                            out.write("2");
                        }
                        else if (m1isNA || m2isNA) {
                            // missing data, imputed as 1
                            out.write("NA");
                        }
                        else if (!homo) {
                            // 2 alleles are not the same
                            out.write("1");
                        }
                        if (col != table_GD[row].length - 2)
                            out.write("\t");
                        else
                            out.write("\n");
                    }
                }
                System.out.println(String.format("Sample %d ~ %d : ", count_GD + 1, count_GD + currentCount) + "Done                    ");
                count_GD += currentCount;
            }
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        // GM
        try{
            System.out.println("Converting Map file");
            setReader(GM_path);
            setWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
            out.write("SNP\tChromosome\tPosition\n");
            while (count_GM < size_GM) {
                table_GM = readNumPLINKMap();
                currentCount = table_GM.length;
                for (int row = 0; row < currentCount; row++) {
                    progressbar(String.format("Marker %d ~ %d : ", count_GM + 1, count_GM + currentCount), row, currentCount);
                    out.write(table_GM[row][1] + "\t" + table_GM[row][0] + "\t" + table_GM[row][3] + "\n");
                }
                System.out.println(String.format("Marker %d ~ %d : ", count_GM + 1, count_GM + currentCount) + "Done                    ");
                count_GM += currentCount;
            }
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    private void setWriter (String file) throws IOException {
        fr = new FileWriter(file);
        br = new BufferedWriter(fr);
        out = new PrintWriter(br);
    }
    private void setMapWriter (String file) throws IOException {
        mfr = new FileWriter(file);
        mbr = new BufferedWriter(mfr);
        mout = new PrintWriter(mbr);
    }
    private void setReader (String file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }
    private void progressbar (String prefix, int current, int all) {
        double progress = (current / (double) all) / 0.05;
        int barCount = (int)Math.floor(progress);
        System.out.print(prefix);
        System.out.print("||" + String.format("%s%s",
                String.join("", Collections.nCopies(barCount, "=")),
                String.join("", Collections.nCopies(20 - barCount, " "))) + "||\r");
    }
    private String[][] readPED () throws IOException {
        int index = 0;
        String readline = null;
        String[][] lines = new String[sub_n][];
        while (index < sub_n && (readline = reader.readLine()) != null)
            // Seperated by space and tab
            lines[index++] = readline.split("\t| +");
        if (index < sub_n - 1)
            lines = Arrays.copyOf(lines,  index);
        return lines;
    }
    private String[][] readVcfGD (int lastPosition) throws IOException {
        int index = 0, upperBound = Math.min(lastPosition + sub_n, lineLength);
        String readline = null;
        // Transposed dimension
        String[][] lines = new String[upperBound - lastPosition][mCount];
        // temp
        String[] temp = null;
        while ((readline = reader.readLine()) != null) {
            temp = readline.replaceAll("\"", "").split(sep);
            for (int i = lastPosition; i < upperBound; i ++)
                lines[i - lastPosition][index] = temp[i];
            index ++;
        }
        return lines;
    }
    private int getAnnotationLine () throws IOException {
        int index = 0;
        String readline = null;
        while ((readline = reader.readLine()) != null) {
            // Skip the line initial with ## and count the number
            if (readline.startsWith("##")) {
                index ++;
                continue;
            } else {
                // Store the header read
                tempread = readline;
                // Found the header and add 1 to the marker line
                return(index + 1);
            }
        }
        return -1;
    }
    private void resetToMarkerLine (String path, int annotation) throws IOException {
        setReader(path);
        for (int i = 0; i < annotation; i ++)
            reader.readLine();
    }
    private String[][] readHmpGD (int lastPosition) throws IOException {
        int index = 0, upperBound = Math.min(lastPosition + sub_n, lineLength);
        String readline = null;
        // Transposed dimension
        String[][] lines = new String[upperBound - lastPosition][mCount];
        // temp
        String[] temp = null;
        while ((readline = reader.readLine()) != null) {
            temp = readline.replaceAll("\"", "").split(sep);
            // If is the first round, catch the RefAllele
            if (lastPosition == 11)
                RefAllele[index] = temp[11].charAt(0);
            for (int i = lastPosition; i < upperBound; i ++)
                lines[i - lastPosition][index] = temp[i];
            index ++;
        }
        return lines;
    }
    private String[][] readHmpVcfMap () throws IOException {
        int index = 0;
        String readline = null;
        String[][] lines = new String[sub_m][];
        while (index < sub_m && (readline = reader.readLine()) != null)
            lines[index++] = readline.replaceAll("\"", "").split(sep);
        if (index < sub_m - 1)
            lines = Arrays.copyOf(lines, index);
        return lines;
    }
    private String[][] readNumGD () throws IOException {
        int index = 0;
        String readline = null, sep = "\t";
        String[][] lines = new String[sub_n][];
        while (index < sub_n && (readline = reader.readLine()) != null) {
            if (index == 0) {
                lines[index] = readline.replaceAll("\"", "").split(sep);
                if(lines[index].length <= 1){
                    sep = " +";
                    lines[index] = readline.replaceAll("\"", "").split(sep);
                }
                if(lines[index].length <= 1){
                    sep = ",";
                    lines[index] = readline.replaceAll("\"", "").split(sep);
                }
            }
            lines[index++] = readline.replaceAll("\"", "").split(sep);
        }
        if (index < sub_n - 1)
            lines = Arrays.copyOf(lines, index);
        return lines;
    }
    private String[][] readNumPLINKMap () throws IOException {
        int index = 0;
        String readline = null, sep = "\t";
        String[][] lines = new String[sub_m][];
        while (index < sub_m && (readline = reader.readLine()) != null) {
            if (index == 0) {
                lines[index] = readline.replaceAll("\"", "").split(sep);
                if (lines[index].length <= 1) {
                    sep = " +";
                    lines[index] = readline.replaceAll("\"", "").split(sep);
                }
                if (lines[index].length <= 1) {
                    sep = ",";
                    lines[index] = readline.replaceAll("\"", "").split(sep);
                }
            }
            lines[index++] = readline.replaceAll("\"", "").split(sep);
        }
        if (index < sub_m - 1)
            lines = Arrays.copyOf(lines, index);
        return lines;
    }
    private void setVcfSep () throws IOException {
        // Find seperator (default is by tab)
        headerline = tempread.replaceAll("\"", "").split(sep);
        // by space
        if (headerline.length <= 1) {
            sep = " +";
            headerline = tempread.replaceAll("\"", "").split(sep);
        }
        lineLength = headerline.length;
        nCount = lineLength - 9;
    }
    private void setHmpSep () throws IOException {
        String tempLine = reader.readLine();
        // Find seperator (default is by tab)
        headerline = tempLine.replaceAll("\"", "").split(sep);
        // by space
        if (headerline.length <= 1) {
            sep = " +";
            headerline = tempLine.replaceAll("\"", "").split(sep);
        }
        // csv
        if (headerline.length <= 1) {
            sep = ",";
            headerline = tempLine.replaceAll("\"", "").split(sep);
        }
        lineLength = headerline.length;
        nCount = lineLength - 11;
    }
    private int getCountofLines(String filename) throws IOException {
        Path path = Paths.get(filename);
        long lineCount = Files.lines(path).count();
        return (int) lineCount;
//        InputStream reader = new BufferedInputStream(new FileInputStream(filename));
//        try{
//            byte[] c = new byte[1024];
//            int count = 0;
//            int readChars = 0;
//            boolean empty = true, endsNL = true;
//            while ((readChars = reader.read(c)) != -1) {
//                empty = false;
//                for (int i = 0; i < readChars; ++i)
//                    if (c[i] == '\n' || c[i] == '\r')
//                        ++count;
//                // In case the line without \n
//                endsNL = c[readChars - 1] == '\n';
//            }
//            if (!endsNL)
//                count += 1;
//            return (count == 0 && !empty) ? 1 : count;
//        } finally {
//            reader.close();
//        }
    }
    private void getTaxa (boolean header, boolean isHmp) {
        // if hapmap, skip first 11 items, otherwise it's vcf and skip first 9 items.
        int info = isHmp ? 11 : 9;
        if (header) {
            for (int i = 0; i < nCount; i ++)
                taxa.add(headerline[i + info]);
        } else {
            for (int i = 0; i < nCount; i ++)
                taxa.add("Sample " + (i + 1));
        }
    }
    private enum format {
        NA("NA"), Numerical("Numerical"), Hapmap("Hapmap"), VCF("VCF"), PLINK("PLINK");
        String name;
        private format (String name) {
            this.name = name;
        }
        public String getName () {
            return this.name();
        }
    }
}
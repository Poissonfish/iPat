package main;
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

class iPat_converter{
	public enum format{
		NA("NA"), Numerical("Numerical"), Hapmap("Hapmap"), VCF("VCF"), PLINK("PLINK");
		String name;
		private format(String name){
			this.name = name;}
		public String getName(){
			return this.name();}
	}
	public iPat_converter(String input, String output, String gd, String gm) throws IOException{
		System.out.println("File Converter for iPAT");
		format InputFormat = format.NA, OutputFormat = format.NA;
		String GD_path = gd, GM_path = gm;
		switch(input){
			case "num":
				InputFormat = format.Numerical; break;
			case "hmp":
				InputFormat = format.Hapmap; break;
			case "vcf":
				InputFormat = format.VCF; break;
			case "plink":
				InputFormat = format.PLINK; break;}
		switch(output){
			case "num":
				OutputFormat = format.Numerical; break;
			case "hmp":
				OutputFormat = format.Hapmap; break;
			case "vcf":
				OutputFormat = format.VCF; break;
			case "plink":
				OutputFormat = format.PLINK; break;}	
		switch(InputFormat){
			case Numerical:
				NumToPlink(GD_path, GM_path); break;
			case Hapmap:
				switch(OutputFormat){
				case Numerical: HapToNum(GD_path); break;
				case PLINK: HapToPlink(GD_path); break;}
				break;
			case VCF:
				switch(OutputFormat){
				case Numerical: VCFToNum(GD_path); break;
				case PLINK: VCFToPlink(GD_path); break;}
				break;
			case PLINK:
				PlinkToNum(GD_path, GM_path); break;}
	}
	public static String[][] read_table(String filename, int size) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int index = 0, redundant = 0;
        String readline = null;
        String[][] lines = null;
        String sep = "\t";
        while((readline = reader.readLine()) != null){
        	if(index == 0){
        		if(readline.startsWith("##")){
        			redundant++;
        			continue;}
        		else{
            		lines = new String[size - redundant][];}
        		lines[index] = readline.replaceAll("\"", "").split(sep);
        		if(lines[index].length <= 1){
        			sep = " +";
            		lines[index] = readline.replaceAll("\"", "").split(sep);}
        		if(lines[index].length <= 1){
        			sep = ",";
            		lines[index] = readline.replaceAll("\"", "").split(sep);}}
        	lines[index++] = readline.replaceAll("\"", "").split(sep);   	
        }
        return lines;
    }
	public static int getCountofLines(String filename) throws IOException {
	    InputStream reader = new BufferedInputStream(new FileInputStream(filename));
	    try{
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = reader.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) 
	                if (c[i] == '\n') ++count;}
	        return (count == 0 && !empty) ? 1 : count;
	    }finally{
	    	reader.close();}
	}	
	public static void NumToPlink(String GD_path, String GM_path) throws IOException{
		System.out.println("N -> P" + " GD: " + GD_path + " GM: " + GM_path);
		int size_GD = getCountofLines(GD_path), 
			size_GM = getCountofLines(GM_path);
		String[][] table_GD = read_table(GD_path, size_GD),
				   table_GM = read_table(GM_path, size_GM);
		// ped
		boolean header_GD = table_GD[0][1].length() == 1 ? false:true,
				contain_taxa = table_GD[1][0].length() == 1 ? false:true;
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.ped");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    if(contain_taxa){
		    	for(int row = header_GD ? 1 : 0; row < size_GD; row++){
		    		out.write(table_GD[row][0] + "\t" + table_GD[row][0] + "\t0\t0\t0\t0\t");
		    		for(int col = 1; col < table_GD[row].length; col++){
		    			switch(table_GD[row][col]){
		    			case "0": out.write("A A"); break;
		    			case "1": out.write("A T"); break;
		    			case "2": out.write("T T"); break;}
		    			if(col != table_GD[row].length - 1)
		    				out.write("\t");}
		    		out.write("\n");}}
		    else{
		    	int sample_num = 0;
		    	for(int row = header_GD ? 1 : 0; row < size_GD; row++){
		    		out.write("Family_" + sample_num + "\t" + "Sample_" + sample_num + "\t0\t0\t0\t0\t");
		    		for(int col = 1; col < table_GD[row].length; col++){
		    			switch(table_GD[row][col]){
		    			case "0": out.write("A A"); break;
		    			case "1": out.write("A T"); break;
		    			case "2": out.write("T T"); break;}
		    			if(col != table_GD[row].length - 1)
		    				out.write("\t");}
		    		out.write("\n");}}
		    out.close();
		}catch(IOException e){System.out.println(e);}
		// map
		boolean header_GM = table_GM[0][1].length() == 1 ? false:true;
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    for(int row = header_GM ? 1 : 0; row < size_GM; row++)
		    	out.write(table_GM[row][1] + "\t" + table_GM[row][0] + "\t0\t" + table_GM[row][2] + "\n");
		    out.close();
		}catch(IOException e){System.out.println(e);}
	}
	public static void HapToPlink(String GD_path) throws IOException{
		System.out.println("H -> P" + " GD: " + GD_path);
		int size_GD = getCountofLines(GD_path);
		String[][] table_GD = read_table(GD_path, size_GD);
		boolean header = !table_GD[0][2].matches("^\\d+$");
		// ped
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.ped");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    if(header){
		    	for(int col = 11; col < table_GD[0].length; col++){
		    		out.write(table_GD[0][col] + "\t" + table_GD[0][col] + "\t0\t0\t0\t-9\t");
		    		for(int row = 1; row < size_GD; row++){
		    			if(table_GD[row][col].charAt(0) == 'N')
		    				out.write("0 0");
		    			else
		    				out.write(table_GD[row][col].charAt(0) + " " + table_GD[row][col].charAt(1));
		    			if(row != size_GD - 1) out.write("\t");}
		    		out.write("\n");}}
		    else{
		    	for(int col = 11; col < table_GD[0].length; col++){
		    		out.write("Family_" + (col - 10) + "\t" + "Sample_" + (col - 10) + "\t0\t0\t0\t-9\t");
		    		for(int row = 1; row < size_GD; row++){
		    			out.write(table_GD[row][col].charAt(0) + " " + table_GD[row][col].charAt(1));
		    			if(row != size_GD - 1)
		    				out.write("\t");}
		    		out.write("\n");}}
		    out.close();
		}catch(IOException e){System.out.println(e);}
		// map
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    for(int row = header? 1 : 0; row < size_GD; row++)
		    	out.write(table_GD[row][2] + "\t" + table_GD[row][0] + "\t0\t" + table_GD[row][3] + "\n");
		    out.close();
		}catch(IOException e){System.out.println(e);}	
	}
	public static void VCFToPlink(String GD_path) throws IOException{
		System.out.println("V -> P" + " GD: " + GD_path);
		int size_GD = getCountofLines(GD_path), start_line = 0;
		String[][] table_GD = read_table(GD_path, size_GD);
		while(table_GD[start_line].length == 1) start_line++;
		// ped
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.ped");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    for(int col = 9; col < table_GD[start_line].length; col++){
	    		out.write(table_GD[start_line][col] + "\t" + table_GD[start_line][col] + "\t0\t0\t0\t-9\t");
	    		for(int row = start_line + 1; row < size_GD; row++){
	    			char A = table_GD[row][3].charAt(0),
	    				 B = table_GD[row][4].charAt(0);
	    			String m1 = table_GD[row][col].split("/")[0],
	    				   m2 = table_GD[row][col].split("/")[1];
	    			switch(m1){
	    			case "0": out.write(A + " "); break;
	    			case "1": out.write(B + " "); break;
	    			// imputed as 0
	    			default : out.write("0 "); break;}
	    			switch(m2){
	    			case "0": out.write(A); break;
	    			case "1": out.write(B); break;
	    			// imputed as 0
	    			default : out.write("0"); break;}
	    			if(row != size_GD - 1) out.write("\t"); else out.write("\n");}}
		    out.close();
		}catch(IOException e){System.out.println(e);}
		// map
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.map");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    for(int row = start_line + 1; row < size_GD; row++)
		    	out.write(table_GD[row][0] + "\t" + table_GD[row][2] + "\t0\t" + table_GD[row][1] + "\n");
		    out.close();
		}catch(IOException e){System.out.println(e);}
	}
	public static void PlinkToNum(String GD_path, String GM_path) throws IOException{
		System.out.println("P -> N" + " GD: " + GD_path + " GM: " + GM_path);
		int size_GD = getCountofLines(GD_path), 
			size_GM = getCountofLines(GM_path);
		String[][] table_GD = read_table(GD_path, size_GD),
				   table_GM = read_table(GM_path, size_GM);
		// GD
		try{
			FileWriter fr = new FileWriter(GM_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    // catch the first allele
		    int marker_count = table_GD[0].length - 6;
    		String[] A = new String[marker_count];
		    for(int row = 0; row < size_GD; row++){
	    		out.write(table_GD[row][1] + "\t");
	    		for(int col = 6; col < table_GD[row].length; col++){
	    			if(A[col-6] == null){
		    			A[col - 6] = table_GD[row][col].split(" ")[0];
		    			if(A[col - 6].equals("0")) A[col-6] = null;}
	    			String m1 = table_GD[row][col].split(" ")[0];
	    			String m2 = table_GD[row][col].split(" ")[1];
	    			if(m1.equals(m2) && m1.equals(A[col - 6])){
	    				// 2 alleles are the same, and equal to the first allele
	    				out.write("0");}
	    			else if(m1.equals(m2) && !m1.equals(A[col - 6]) && !m1.equals("0")){
	    				// 2 alleles are the same, but not equal to the first allele and are not a missing value
	    				out.write("2");}
	    			else if(m1.equals("0") || m2.equals("0")){
	    				// missing data, imputed as 1
	    				out.write("1");}
	    			else if(!m1.equals(m2)){
	    				// 2 alleles are not the same
	    				out.write("1");}
	    			if(col != table_GD[row].length - 1) out.write("\t"); else out.write("\n");}}
		    out.close();
		}catch(IOException e){System.out.println(e);}	
		// GM
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    out.write("SNP\tChromosome\tPosition\n");
		    for(int row = 0; row < size_GM; row++)
		    	out.write(table_GM[row][1] + "\t" + table_GM[row][0] + "\t" + table_GM[row][3] + "\n");
		    out.close();
		}catch(IOException e){System.out.println(e);}	
	}
	public static void HapToNum(String GD_path) throws IOException{
		System.out.println("H -> N" + " GD: " + GD_path );
		int size_GD = getCountofLines(GD_path);
		String[][] table_GD = read_table(GD_path, size_GD);
		boolean header = !table_GD[0][2].matches("^\\d+$");
		// gd
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
	    	for(int col = 11; col < table_GD[0].length; col++){
	    		if(header) out.write(table_GD[0][col] + "\t"); else out.write("Sample_" + (col - 10) + "\t");
	    		for(int row = (header) ? 1 : 0; row < size_GD; row++){
	    			char A = table_GD[row][1].split("/")[0].charAt(0),
	    				 B = table_GD[row][1].split("/")[1].charAt(0),
	    				 m1 = table_GD[row][col].charAt(0),
	    				 m2 = table_GD[row][col].charAt(1);
	    			if(m1 == m2 && m1 == A){
	    				// 2 alleles are the same, and equal to the first allele
	    				out.write("0");}
	    			else if(m1 == m2 && m1 == B){
	    				// 2 alleles are the same, and equal to the second allele
	    				out.write("2");}
	    			else if(m1 == 'N' || m2 == 'N' || m1 == 'n' || m2 == 'n'){
	    				// missing data, imputed as 1
	    				out.write("1");}
	    			else if(m1 != m2){
	    				// 2 alleles are not the same
	    				out.write("1");}
	    			if(row != size_GD - 1) out.write("\t"); else out.write("\n");}}
		    out.close();
		}catch(IOException e){System.out.println(e);}
		// gm
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    out.write("SNP\tChromosome\tPosition\n");
		    for(int row = header? 1 : 0; row < size_GD; row++)
		    	out.write(table_GD[row][0] + "\t" + table_GD[row][2] + "\t" + table_GD[row][3] + "\n");
		    out.close();
		}catch(IOException e){System.out.println(e);}
	}
	public static void VCFToNum(String GD_path) throws IOException{
		System.out.println("V -> N" + " GD: " + GD_path);
		int size_GD = getCountofLines(GD_path), start_line = 0;
		String[][] table_GD = read_table(GD_path, size_GD);
		size_GD = table_GD.length;
		// gd
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.dat");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    for(int col = 9; col < table_GD[start_line].length; col++){
		    	out.write(table_GD[start_line][col] + "\t");
		    	for(int row = start_line + 1; row < size_GD; row++){
	    			char m1 = table_GD[row][col].split("/")[0].charAt(0),
	    				 m2 = table_GD[row][col].split("/")[1].charAt(0);
	    			if(m1 == m2 && m1 == '0'){
	    				// 2 alleles are the same, and equal to the first allele
	    				out.write("0");}
	    			else if(m1 == m2 && m1 == '1'){
	    				// 2 alleles are the same, and equal to the second allele
	    				out.write("2");}
	    			else if(m1 == '.' || m2 == '.'){
	    				// missing data, imputed as 1
	    				out.write("1");}
	    			else if(m1 != m2){
	    				// 2 alleles are not the same
	    				out.write("1");}
	    			if(row != size_GD - 1) out.write("\t"); else out.write("\n");}}
		    out.close();
		}catch(IOException e){System.out.println(e);}
		// gm
		try{
			FileWriter fr = new FileWriter(GD_path.replaceFirst("[.][^.]+$", "") + "_recode.nmap");
		    BufferedWriter br = new BufferedWriter(fr);
		    PrintWriter out = new PrintWriter(br);
		    out.write("SNP\tChromosome\tPosition\n");
		    for(int row = start_line + 1; row < size_GD; row++)
		    	out.write(table_GD[row][2] + "\t" + table_GD[row][0] + "\t" + table_GD[row][1] + "\n");
		    out.close();
		}catch(IOException e){System.out.println(e);}
	}
}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GeneBankCreateBTree {
	
	/*
	 * Usage:
	 * java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]
	 * 
	 * degree is the degree to be used for the BTree. If the user specifies 0, then your
	 * program should choose the optimum degree based on a disk block size of 4096 bytes
	 * and the size of your BTree node on disk
	 * 
	 * debug level:
	 * 0 Any diagnostic messages, help and status messages must be be printed on standard
	 * error stream.
	 * 1 The program writes a text file named dump, that has the following line format:
	 * <frequency> <DNA string>. The dump file contains frequency and DNA string
	 * (corresponding to the key stored) in an inorder traversal.
	*/
	public GeneBankCreateBTree(){
		
	}
	
	//what happens if we don't get a full 32 base number (16 bases?)
	
	
	public static long stringToKey(String subsequence) {
		long theKey = 0;
		subsequence.toLowerCase();
		for (int i = 0; i < subsequence.length(); i += 1) {
			//System.out.println(subsequence.substring(i, i+1));
			long base = mapBase(subsequence.substring(i, i+1));
			theKey = theKey | (base<<2*i);	//setbit
		}
		return theKey;
	}
	
	private static long mapBase(String theBase) {
		if( theBase.equals("a") ) {
			return 0b00;
		} else if (theBase.equals("t")) {
			return 0b11;
		} else if (theBase.equals("c")) {
			return 0b01;
		} else if (theBase.equals("g")) {
			return 0b10;
		} else {
			System.err.println("mapBase Error");
			return -1;
		}
	}
	
	public static String keyToString(long theKey) {
		String theSequence = "";
		for (int i = 0; i < 31; i += 1) {
			//System.out.println(subsequence.substring(i, i+1));
			//long base = mapBase(subsequence.substring(i, i+1));
			long theBits = (theKey>>2*i) & (~(~0<<2));
			//System.out.println("i: " + i + ", theBits: " + Long.toBinaryString(theBits));
			String base = mapKey(theBits);
			theSequence += base;
		}
		return theSequence;
	}
	
	public static String mapKey(long twoDigit) {
		if( twoDigit ==  0b00) {
			return "a";	//a
		} else if (twoDigit == 0b11) {
			return "t";	//t
		} else if (twoDigit == 0b01) {
			return "c";	//c 
		} else if (twoDigit == 0b10) {
			return "g";	//g
		} else {
			System.err.println("mapBase Error");
			return "error";
		}
	}
	
	
	
	
	public static void main(String args[]) {
		if (args.length < 3) {
			printUsage();
		}
		
		int degree = Integer.parseInt(args[0]);
		if(degree < 0) {
			throw new IllegalArgumentException("Improper Degree Selection");
		}
		
		//check gbk file
		String filename = args[1];
		String fileSuffix = filename.substring((filename.length()-4), filename.length());
		File theFile = new File(filename);
		if(!fileSuffix.equals(".gbk")) {	//make sure .gbk file
			System.err.println("Wrong File Type.");
			System.exit(1);		
		}else if(!theFile.exists()) {
			System.err.println("File not found.");
			System.exit(1);
		}
			
			
		
		int sequenceLength = Integer.parseInt(args[2]);
		if(sequenceLength <= 0 ) {
			throw new IllegalArgumentException("Improper Load Factor Specification");
		}
		
		int debugMode = 0;
		if (args.length > 3) {
			debugMode = Integer.parseInt(args[3]);
			if(!(debugMode == 0 || debugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}
		
	}
	
	
	
	
	/**
	 * prints the proper command line arguments if the wrong inputs are detected
	 */
	private static void printUsage() {
		System.out.println(
			"Usage:\n"
			+ " java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]\n"
			+ " degree is the degree to be used for the BTree. If 0 is entered, the program will\n"
			+ " choose the optimum degree based on a disk block size of 4096 bytes and the size\n"
			+ " of the BTreeNode on disk\n"
			+ " gbk file is the file of DNA sequences\n"
			+ " sequence length is the length of DNA bases to be stored in each BTreeNode\n"
			+ " debug level [optional]:\n"
			+ " 0 Any diagnostic messages, help and status messages must be be printed on standard\n"
			+ " error stream.\n"
			+ " 1 The program writes a text file named dump, that has the following line format:\n"
			+ " <frequency> <DNA string>. The dump file contains frequency and DNA string\n"
			+ "(corresponding to the key stored) in an inorder traversal.\n"
		);
		System.exit(1);
	}
	

}

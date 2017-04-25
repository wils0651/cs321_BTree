import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class GeneBankCreateBTree {
	private int sequenceLength;	//Length of each DNA sequence stored in the BTree, k
	private int degree;	//Number of DNA sequences stored per BTreeNode, t
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
	
	
	
	
	
	
	
	public static void main(String args[]) throws IOException {
		if (args.length < 3) {
			printUsage();
		}
		
		int degree = Integer.parseInt(args[0]);
		if(degree < 0) {
			throw new IllegalArgumentException("Improper Degree Selection");
		} else if(degree == 0) {
			degree = bTreeDefaultSize();	//default number of DNA sequences
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
			throw new IllegalArgumentException("Improper Sequence Length Specification");
		}
		
		int debugMode = 0;
		if (args.length > 3) {
			debugMode = Integer.parseInt(args[3]);
			if(!(debugMode == 0 || debugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}
		
		//TODO: send file to parser
		FileInputStream theFileStream = new FileInputStream(theFile);
		Parser gbkParser = new Parser(theFileStream, sequenceLength);
		int countSeq = 0;
		long elKey = 0;
		
		while(gbkParser.hasMore() && (countSeq < degree)) {
			String testString = gbkParser.nextSubSequence();	//TODO: remove
			System.out.print(countSeq);
			System.out.print(", testString: " + testString); 	//TODO: remove
			long testBases = stringToKey(testString, sequenceLength); 	//TODO: remove
			//System.out.print("testBases: "+ testBases);
			System.out.println(" in binary: "+Long.toBinaryString(testBases));
			
			elKey = elKey | (testBases<<2*sequenceLength*(countSeq+1));	//setbit
			
			countSeq++;
		}
		
		//TODO: Put stuff into a Btree
		
		
		
		
		//TODO: write to disk.
		int k = sequenceLength;
		int t = degree;
		//String theFilename = filename+".btree.data." + k +"." +t;	//TODO: uncomment this
		String theFilename = "theTestFile.txt";
		File outputFile = new File(theFilename);
		String mode = "rw";			//read write
		RandomAccessFile fileWriter = new RandomAccessFile(outputFile, mode);
		
		fileWriter.writeLong(elKey);		//Writes a long to the file as eight bytes, high byte first.
		// to view file in console: xxd -b file
		fileWriter.close();
		
		


//		//public byte[] longToBytes(long x) {
//			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//			buffer.putLong(x);
//			return buffer.array();
//		//}
//
//		//public long bytesToLong(byte[] bytes) {
//			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//			buffer.put(bytes);
//			buffer.flip();//need flip 
//			return buffer.getLong();
//		//}
		
		
		
		RandomAccessFile fileReader = new RandomAccessFile(outputFile, "r");
		//fileReader.seek(0);
		long elLong = fileReader.readLong();
		System.out.println("elLong: "+elLong);
		
		fileReader.close();		//close the fileReader
		
		for(int i = 0; i < degree; i++) {
			long losBits = (elLong>>2*sequenceLength*(i+1)) & (~(~0<<2*sequenceLength*(i+1)));
			System.out.println("testing bases: " + keyToString(losBits, sequenceLength));
		}
	}
	
	
	/**
	 * converts a string of letter DNA bases to a number
	 * @param subsequence
	 * @param sequenceLength
	 * @return a long that represents the sequence
	 */
	public static long stringToKey(String subsequence, int sequenceLength) {
		long theKey = 0;
		subsequence.toLowerCase();
		int k = sequenceLength;
		for (int i = 0; i < k; i += 1) {
			//System.out.println(subsequence.substring(i, i+1));
			long base = mapBase(subsequence.substring(i, i+1));
			theKey = theKey | (base<<2*i);	//setbit
		}
		return theKey;
	}
	
	/**
	 * helper method that converts a letter DNA base to a two digit binary number
	 * @param theBase
	 * @return base as a number
	 */
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
	
	/**
	 * Converts a binary number of DNA bases to a sting of letters 
	 * @param theKey
	 * @param sequenceLength
	 * @return
	 */
	public static String keyToString(long theKey, int sequenceLength) {
		String theSequence = "";
		int k = sequenceLength;
		for (int i = 0; i < k; i += 1) {
			//System.out.println(subsequence.substring(i, i+1));
			//long base = mapBase(subsequence.substring(i, i+1));
			long theBits = (theKey>>2*i) & (~(~0<<2));
			//System.out.println("i: " + i + ", theBits: " + Long.toBinaryString(theBits));
			String base = mapKey(theBits);
			theSequence += base;
		}
		return theSequence;
	}
	
	
	/**
	 * private helper method to convert a two binary digit number to the appropriate 
	 * DNA base 
	 * @param twoDigit
	 * @return DNA base
	 */
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
	
	/**
	 * method to write to a textfile named dumep. Uses a hashmap to count the 
	 * frequency of sequences in an inorder traversal.
	 */
	private void debugDump() {
		/*
		 * The program writes a text file named dump, that has the following line format:
		 * <frequency> <DNA string>. The dump file contains frequency and DNA string
		 * (corresponding to the key stored) in an inorder traversal.
		 */
		
		//TODO: create a hashmap of sequences from and in-order traversal
		
		//TODO: dump the hashmap to a file 
		
	}
	
	/**
	 * sets the default size of the B Trees
	 */
	private static int bTreeDefaultSize() {
		int sizeHeader = 100;	//bytes
		int sizeObject = 40;	//bytes, (2t-1)
		int sizeChild  = 4;		//bytes, (2t)
		int sizeParent = 4;		//bytes
		int sizeBlock  = 4096;	//bytes, size of block on disk
		int numObjects = (sizeBlock - sizeParent + sizeObject - sizeHeader)/(2*sizeChild + 2*sizeObject);
		return numObjects;
	}
	

}

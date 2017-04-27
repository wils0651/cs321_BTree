import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class GeneBankCreateBTree {
	private int sequenceLength;	//Length of each DNA sequence stored in the BTree, k
	private int degree;	//Number of DNA sequences stored per BTreeNode, t
	private Parser gbkParser; //parser
	private String filename;
	private BTree theBTree;

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
	public GeneBankCreateBTree(int degree, String filename, int sequenceLength) throws IOException{
		this.degree = degree;
		this.filename = filename;
		this.sequenceLength = sequenceLength;
		
		int k = sequenceLength;
		int t = degree;
		String theFilename = filename+".btree.data." + k +"." +t;	//output filename?
		
		theBTree = new BTree(degree, theFilename);
	}

	
	public static void main(String args[]) throws IOException {
		if (args.length < 3) {
			printUsage();
		}

		int thisDegree = Integer.parseInt(args[0]);
		if(thisDegree < 0) {
			throw new IllegalArgumentException("Improper Degree Selection");
		} else if(thisDegree == 0) {
			thisDegree = bTreeDefaultSize();	//default number of DNA sequences
		}

		//check gbk file
		String thisFilename = args[1];
		String fileSuffix = thisFilename.substring((thisFilename.length()-4), thisFilename.length());
		File theFile = new File(thisFilename);
		if(!fileSuffix.equals(".gbk")) {	//make sure .gbk file
			System.err.println("Wrong File Type.");
			System.exit(1);		
		}else if(!theFile.exists()) {
			System.err.println("File not found.");
			System.exit(1);
		}

		int ThisSequenceLength = Integer.parseInt(args[2]);
		if(ThisSequenceLength <= 0 ) {
			throw new IllegalArgumentException("Improper Sequence Length Specification");
		}

		int debugMode = 0;
		if (args.length > 3) {
			debugMode = Integer.parseInt(args[3]);
			if(!(debugMode == 0 || debugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}


		//Create an object, pass int the degree, output filename,and sequence length
		
		GeneBankCreateBTree gbcbt = new GeneBankCreateBTree(thisDegree, thisFilename, ThisSequenceLength);

		gbcbt.sendToParser();

		//TODO: Put stuff into a Btree

		//gbcbt.testWrite();

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

	//TODO: move to BTree.java
	/**
	 * write relevant information to a metadata file
	 */
	private void writeMetadata() {
		/* Metadata storage. We need to store some metadata about the BTree on disk. For
		 * example, we can store 
		 * 	the degree of the tree, 
		 * 	sequence length
		 * 	the byte offset of the root node (so we can find it), 
		 * 	the number of nodes etc. 
		 * This information could be stored in separate metadata file or it can be 
		 * stored at the beginning of the BTree file.
		 */
		String fileName = "BTreeMetadata.txt";

		try{
			PrintWriter writer1 = new PrintWriter(fileName, "UTF-8");
			//writer1.println(gbkFileName);	//name of the BTree file
			writer1.println(degree);	//degree of tree;
			
			writer1.println(sequenceLength);	//
			
			writer1.println(theBTree.getRoot().getFileOffset());	//offset of the rootnode
			
			writer1.println(theBTree.numNodes());	//numberOfNodes
			
			
			writer1.close();
		} catch (IOException e) {
			System.err.println("Error creating file: " + fileName);
			System.exit(1);
		}
	}

	//TODO: move to Search?
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	private void readMetadata() throws FileNotFoundException {
		String fileName = "BTreeMetadata.txt";
		File theFile = new File(fileName);
		Scanner fileScan = new Scanner(theFile);
		String gbkFileName = fileScan.nextLine();
		int degree = Integer.parseInt(fileScan.nextLine() );
		int sequenceLength = Integer.parseInt(fileScan.nextLine() );
		long offsetRoot = Integer.parseInt(fileScan.nextLine() );//offset of the rootnode
		int numNodes= Integer.parseInt(fileScan.nextLine() );	//

	}

	public void sendToParser() throws IOException {
		File theFile = new File(filename);
		//TODO: send file to parser
		FileInputStream theFileStream;
		try {
			theFileStream = new FileInputStream(theFile);
			gbkParser = new Parser(theFileStream, sequenceLength);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long nextKey;
		while(gbkParser.hasMore() ) {
			//String testString = gbkParser.nextSubSequence();	//TODO: remove
			//System.out.print(countSeq);
			//System.out.print(", testString: " + testString); 	//TODO: remove
			//long testBases = gbkParser.stringToKey(testString, sequenceLength); 	//TODO: remove
			//System.out.println(" in binary: "+Long.toBinaryString(testBases));
			nextKey = gbkParser.getNextKey();
			if (nextKey != -1){
			System.out.println(gbkParser.keyToString(nextKey, sequenceLength)+" in binary: "+Long.toBinaryString(nextKey));
			theBTree.insert(gbkParser.getNextKey());
			}
		}
		
		
	}


	/**
	 * test method
	 * @throws IOException
	 */
	public void testWrite() throws IOException {
		//TODO: delete this method
		
		File theFile = new File(filename);
		//TODO: send file to parser
		FileInputStream theFileStream;
		try {
			theFileStream = new FileInputStream(theFile);
			gbkParser = new Parser(theFileStream, sequenceLength);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO: write to disk.
		int k = sequenceLength;
		int t = degree;
		String theFilename = filename+".btree.data." + k +"." +t;	//TODO: uncomment this
		//String theFilename = "theTestFile.txt";
		File outputFile = new File(theFilename);
		String mode = "rw";			//read write
		RandomAccessFile fileWriter;
		fileWriter = new RandomAccessFile(outputFile, mode);

		int maxCount = 20;
		int countSeq = 0;

		while(gbkParser.hasMore() && (countSeq < maxCount)) {
			String testString = gbkParser.nextSubSequence();	//TODO: remove
			System.out.print(countSeq);
			System.out.print(", testString: " + testString); 	//TODO: remove
			long testBases = gbkParser.stringToKey(testString, sequenceLength); 	//TODO: remove
			System.out.println(" in binary: "+Long.toBinaryString(testBases));

			//fileWriter.writeLong(elKey);		//Writes a long to the file as eight bytes, high byte first.
			fileWriter.writeLong(testBases);		//Writes a long to the file as eight bytes, high byte first.

			countSeq++;
		}

		// to view file in console: xxd -b file
		fileWriter.close();

		RandomAccessFile fileReader = new RandomAccessFile(outputFile, "r");
		fileReader.seek(8);

		for(int i = 0; i < maxCount/2; i++) {
			long elLong = fileReader.readLong();
			//TODO: use readbyte with offset
			System.out.print("elLong: "+elLong);
			//long losBits = (elLong>>2*sequenceLength*(i+1)) & (~(~0<<2*sequenceLength*(i+1)));
			//					System.out.println("testing bases: " + keyToString(losBits, sequenceLength));
			System.out.println(", testing bases: " + gbkParser.keyToString(elLong, sequenceLength));
		}
		fileReader.close();		//close the fileReader
	}
}

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Scanner;

public class GeneBankCreateBTree {
	private int sequenceLength;	//Length of each DNA sequence stored in the BTree, k
	private int degree;	//Number of DNA sequences stored per BTreeNode, t
	private Parser gbkParser; //parser
	private String filename;
	private BTree theBTree;
	private KeyStringConverter ksConverter;

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
	public GeneBankCreateBTree(int cache, int cacheSize, int degree, String filename, int sequenceLength) throws IOException{
		this.degree = degree;
		this.filename = filename;
		this.sequenceLength = sequenceLength;

		//int k = sequenceLength;
		//int t = degree;
		String theFilename = filename+".btree.data." + sequenceLength +"." +degree;	//output filename?

		theBTree = new BTree(cache, cacheSize, degree, sequenceLength, theFilename);
		ksConverter = new KeyStringConverter();
	}


	public static void main(String args[]) throws Exception {
		if (args.length < 4) {
			printUsage();
		}

		int thisCache = Integer.parseInt(args[0]);
		if(thisCache == 1 || thisCache == 0){ 

		}
		else{
			throw new IllegalArgumentException("Improper Cache Specification");
		}
		
		
		int thisDegree = Integer.parseInt(args[1]);

		if(thisDegree < 0) {
			throw new IllegalArgumentException("Improper Degree Selection");
		} else if(thisDegree == 0) {
			thisDegree = bTreeDefaultSize();	//default number of DNA sequences
		}

		//check gbk file
		String thisFilename = args[2];
		String fileSuffix = thisFilename.substring((thisFilename.length()-4), thisFilename.length());
		File theFile = new File(thisFilename);
		if(!fileSuffix.equals(".gbk")) {	//make sure .gbk file
			System.err.println("Wrong File Type.");
			System.exit(1);		
		}else if(!theFile.exists()) {
			System.err.println("File not found.");
			System.exit(1);
		}

		int ThisSequenceLength = Integer.parseInt(args[3]);
		if(ThisSequenceLength <= 0 ) {
			throw new IllegalArgumentException("Improper Sequence Length Specification");
		}


		int thisCacheSize = 0;
		if(thisCache == 1){
			try{
				thisCacheSize = Integer.parseInt(args[4]);
				if(thisCacheSize <= 0 ) {
					throw new IllegalArgumentException("Improper Cache Size Specification");
				}
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.out.println("Please enter a size for your cache");
				System.exit(0);
			}
		}


		int debugMode = 0;
//		if ((args.length == 4 && Integer.parseInt(args[0]) == 0) || args.length == 5) {
//			debugMode = Integer.parseInt(args[4]);
//			if(!(debugMode == 0 || debugMode == 1)) {
//				throw new IllegalArgumentException("Improper Debug Mode Selection");
//			}
//		}


		//Create an object, pass int the degree, output filename,and sequence length

		GeneBankCreateBTree gbcbt = new GeneBankCreateBTree(thisCache, thisCacheSize, thisDegree, thisFilename, ThisSequenceLength);

		int numberOfOnes = gbcbt.sendToParser(); 
		if(gbcbt.theBTree.numNodes() > 1){
			gbcbt.writeMetadata();
			if(thisCache == 1){
			gbcbt.theBTree.writeCache();
			gbcbt.theBTree.getCacheSize();
			}
			gbcbt.debugDump();
		} else {
			System.out.println("Empty B Tree");
		}
	}


	/**
	 * prints the proper command line arguments if the wrong inputs are detected
	 */
	private static void printUsage() {
		System.out.println(
				"Usage:\n"
						+ " java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>] \n"
						+ " degree is the degree to be used for the BTree. If 0 is entered, the program will \n"
						+ " choose the optimum degree based on a disk block size of 4096 bytes and the size \n"
						+ " of the BTreeNode on disk \n"
						+ " gbk file is the file of DNA sequences \n"
						+ " sequence length is the length of DNA bases to be stored in each BTreeNode \n"
						+ " debug level [optional]: \n"
						+ " 0 Any diagnostic messages, help and status messages must be be printed on standard \n"
						+ " error stream. \n"
						+ " 1 The program writes a text file named dump, that has the following line format: \n"
						+ " <frequency> <DNA string>. The dump file contains frequency and DNA string \n"
						+ "(corresponding to the key stored) in an inorder traversal. \n"
				);
		System.exit(1);
	}

	/**
	 * method to write to a textfile named dump. Uses a hashmap to count the 
	 * frequency of sequences in an inorder traversal.
	 * @throws InterruptedException 
	 */
	private void debugDump() throws InterruptedException {
		/*
		 * The program writes a text file named dump, that has the following line format:
		 * <frequency> <DNA string>. The dump file contains frequency and DNA string
		 * (corresponding to the key stored) in an inorder traversal.
		 */
		LinkedList<Long> dumpList = theBTree.inorderTraverseTree();
		
		String fileName = "dump";

		try{
			PrintWriter writer1 = new PrintWriter(fileName, "UTF-8");
//			writer1.println(gbkFileName);	//name of the BTree file
			while (!dumpList.isEmpty()) {
			  Long key = dumpList.remove();
			  String sequence = ksConverter.keyToString(key, sequenceLength);
			  long freq = dumpList.remove();
			  String output = sequence + ": " + freq;
			  writer1.println(output);	
			}
			writer1.close();
		} catch (IOException e) {
			System.err.println("Error creating file: " + fileName);
			System.exit(1);
		}
	}
	
	
	

	/**
	 * sets the default size of the B Trees
	 */
	private static int bTreeDefaultSize() {
		//TODO: check this to see if it matches what we do
		int sizeHeader = 4 + 4 + 4 + 8;	//bytes
		int sizeObject = 8 + 4;	//bytes, (2t-1)
		int sizeChild  = 8;		//bytes, (2t)
		int sizeParent = 4;		//bytes
		int sizeBlock  = 4096;	//bytes, size of block on disk
		//int numObjects = (sizeBlock - sizeParent + sizeObject - sizeHeader)/(2*sizeChild + 2*sizeObject);
		int numObjects = (sizeBlock + sizeObject - sizeHeader)/(2*sizeChild + 2*sizeObject);
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


	/**
	 * method to send the gene base file to the parser and B Tree
	 * @throws Exception 
	 */
	public int sendToParser() throws Exception {
		File theFile = new File(filename);
		FileInputStream theFileStream;
		try {
			theFileStream = new FileInputStream(theFile);
			gbkParser = new Parser(theFileStream, sequenceLength);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int count = 0;
		long nextKey;
		while(gbkParser.hasMore() ) {
			nextKey = gbkParser.getNextKey();
			if(nextKey == 1){
				count++;
			}
			if (nextKey != -1){
			System.out.println(ksConverter.keyToString(nextKey, sequenceLength)+" encoded: "+Long.toBinaryString(nextKey));
			theBTree.insert(nextKey);
			}
		}
		return count;
	}


//	/**
//	 * test method
//	 * @throws Exception 
//	 */
//	public void testWrite() throws Exception {
//		//TODO: delete this method
//
//		File theFile = new File(filename);
//		FileInputStream theFileStream;
//		try {
//			theFileStream = new FileInputStream(theFile);
//			gbkParser = new Parser(theFileStream, sequenceLength);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		int k = sequenceLength;
//		int t = degree;
//		String theFilename = filename+".btree.data." + k +"." +t;	
//		//String theFilename = "theTestFile.txt";
//		File outputFile = new File(theFilename);
//		String mode = "rw";			//read write
//		RandomAccessFile fileWriter;
//		fileWriter = new RandomAccessFile(outputFile, mode);
//
//		int maxCount = 20;
//		int countSeq = 0;
//
//		while(gbkParser.hasMore() && (countSeq < maxCount)) {
//			String testString = gbkParser.nextSubSequence();	
//			System.out.print(countSeq);
//			System.out.print(", testString: " + testString); 	//: remove
//			long testBases = ksConverter.stringToKey(testString, sequenceLength); 	//: remove
//			System.out.println(" in binary: "+Long.toBinaryString(testBases));
//
//			fileWriter.writeLong(testBases);		//Writes a long to the file as eight bytes, high byte first.
//
//			countSeq++;
//		}
//
//		// to view file in console: xxd -b file
//		fileWriter.close();
//
//		RandomAccessFile fileReader = new RandomAccessFile(outputFile, "r");
//		System.out.println("Seek to 8");
//		fileReader.seek(8);
//
//		for(int i = 0; i < (maxCount-1); i++) {
//			long elLong = fileReader.readLong();
//			System.out.print("elLong: "+elLong);
//			System.out.println(", testing bases: " + ksConverter.keyToString(elLong, sequenceLength));
//		}
//		fileReader.close();		//close the fileReader
//
//		RandomAccessFile fileReader2 = new RandomAccessFile(outputFile, "r");
//		System.out.println("Seek again");
//		fileReader2.seek(0);
//
//		for(int i = 0; i < (maxCount-2); i++) {
//			long elLong = fileReader2.readLong();
//			System.out.println(", testing bases: " + ksConverter.keyToString(elLong, sequenceLength));
//		}
//		fileReader2.close();		//close the fileReader
//	}
}

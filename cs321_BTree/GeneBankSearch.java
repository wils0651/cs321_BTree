import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import sun.misc.Queue;

public class GeneBankSearch {
	//private String bTreeFilename;
	private String queryFilename;
	private int debugMode;
	private static int thisCache;
	private static int cacheSize;
	private int degree;
	private int sequenceLength;
	private long fileOffsetRoot;//offset of the root node
	private int numNodes;
	private static RandomAccessFile fileReader;
	private KeyStringConverter ksConverter;
	
	private Queue<Long> q;


	/*
	 * Usage:
	 * java GeneBankSearch <btree file> <query file> [<debug level>]
	 * 
	 * debug level:
	 * 0 The output of the queries should be printed on the standard output stream. Any
	 * diagnostic messages, help and status messages must be be printed on standard
	 * error stream.
	 * 
	 * 
	 * The search returns the frequency of occurrence of the query string
	 */

	/**
	 * constructor
	 * @param bTreeFilename
	 * @param queryFilename
	 * @param debugMode
	 * @throws FileNotFoundException
	 */
	public GeneBankSearch(String bTreeFilename, String queryFilename, int debugMode) throws FileNotFoundException {
		//this.bTreeFilename = bTreeFilename;
		this.queryFilename = queryFilename;
		this.debugMode = debugMode;
		ksConverter = new KeyStringConverter();
		String mode = "r";
		fileReader = new RandomAccessFile(bTreeFilename, mode);
	}

	/**
	 * main method
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 3  || args.length > 5) {
			printUsage();
		}
		
		if(Integer.parseInt(args[0]) == 1){
			thisCache = Integer.parseInt(args[0]);
		}
		else{
			thisCache = 0;
		}

		// Check <btree file>
		String thisBTreeFilename = args[1]; 
		File theFile = new File(thisBTreeFilename);
		if(!theFile.exists()) {
			System.err.println("BTree file not found.");
			System.exit(1);
		}

		//check <query file>
		String thisQueryFilename = args[2];
		File theQueryFile = new File(thisQueryFilename);
		if(!theQueryFile.exists()) {
			System.err.println("Query file not found.");
			System.exit(1);
		}
		if (args.length == 4 && thisCache == 1){
			cacheSize = Integer.parseInt(args[3]);
		}
		else if (args.length == 4 && thisCache == 0){
			cacheSize = 0;
		}

		int thisDebugMode = 0;
		if (args.length == 5 && thisCache == 0) {
			//TODO: change if another Debug level is added
			cacheSize = 0;
			thisDebugMode = Integer.parseInt(args[4]);
			if(!(thisDebugMode == 0 || thisDebugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}
		else if(args.length == 5 && thisCache == 1){
			cacheSize = Integer.parseInt(args[3]);
			thisDebugMode = Integer.parseInt(args[4]);
			if(!(thisDebugMode == 0 || thisDebugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}

		GeneBankSearch gbs = new GeneBankSearch(thisBTreeFilename, thisQueryFilename, thisDebugMode);

		//gbs.readMetadata();

		gbs.readFile();
		
		//gbs.traverseTree();
		
		gbs.searchQueries();
		
		fileReader.close();

	}


	/**
	 * prints the proper command line arguments if the wrong inputs are detected
	 */
	private static void printUsage() {
		System.out.println(
				"Usage:\n"
				+ " java GeneBankSearch <btree file> <query file> [<debug level>] \n"
				+ " <btree file> file that has the B-Tree data \n"
				+ " <query file> file that has the base sequences to find \n"
				);
		System.exit(1);
	}

//	/**
//	 * I put all of this info into the b tree file
//	 * @throws FileNotFoundException 
//	 * 
//	 */
//	private void readMetadata() throws FileNotFoundException {
//		String fileName = "BTreeMetadata.txt";
//		File theFile = new File(fileName);
//		Scanner fileScan = new Scanner(theFile);
//
//		//String gbkFileName = fileScan.nextLine();
//		//degree = Integer.parseInt(fileScan.nextLine() );
//		int asequenceLength = Integer.parseInt(fileScan.nextLine() );
//		long afileOffsetRoot = Long.parseLong(fileScan.nextLine() );//offset of the rootnode
//		//System.out.println("fileOffsetRoot: "+ fileOffsetRoot);
//		int anumNodes= Integer.parseInt(fileScan.nextLine() );	//
//		//System.out.println("numNodes: " + numNodes);
//		fileScan.close();
//	}

	
	/**
	 * Reads the metadata from the B Tree file
	 * @throws InterruptedException
	 */
	public void readFile() throws InterruptedException {
		//File Header Structure:
		//root file offset (long), sequence length (int), degree (int), number of nodes (int), 
		//sequence length (int), degree (int), number of nodes (int), root file offset (long)

		try{ 
			fileReader.seek(0);
			fileOffsetRoot = fileReader.readLong();
			sequenceLength = fileReader.readInt();	//length of base sequence
			degree = fileReader.readInt();		// the degree of the B Tree nodes
			numNodes = fileReader.readInt();	// the number of keys in the long
			
			System.out.println("sequenceLength: " + sequenceLength);
			System.out.println("degree: " + degree);
			System.out.println("numNodes: " + numNodes);
			System.out.println("fileOffsetRoot: "+ fileOffsetRoot);
//
//			System.out.println("1540 appears " + searchKey(1540, fileOffsetRoot) + " times");
//			System.out.println("2949 appears " + searchKey(2949, fileOffsetRoot) + " times");
//			System.out.println("1 appears " + searchKey(1, fileOffsetRoot) + " times");
//			System.out.println("386 appears " + searchKey(382, fileOffsetRoot) + " times");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to search the b tree for sequences
	 * @param key
	 * @param fileOffset
	 * @return
	 * @throws IOException
	 */
	public int searchKey(long key, long fileOffset) throws IOException{
		
		fileReader.seek(fileOffset);

		int numberOfKeys;			//number of keys in a node

		long[] keys;
		int[] frequencies;
		long[] childOffsets;

		numberOfKeys =  fileReader.readInt();
		keys = new long[numberOfKeys];
		frequencies = new int[numberOfKeys];
		childOffsets = new long[numberOfKeys+1];

		for(int i = 0; i < (2*degree-1); i += 1) {
			if ( i < numberOfKeys) {
				keys[i] = fileReader.readLong();
				frequencies[i] = fileReader.readInt();
				if(keys[i] == key){
					return frequencies[i];
				}
			} else {
				long junk1 = fileReader.readLong();
				int junk2 = fileReader.readInt();
			}
		}
		
		for(int i = 0; i <= (2*degree); i += 1) {
			if(i <= numberOfKeys) {
				childOffsets[i] = fileReader.readLong();
				//TODO: this seems odd...
				if(childOffsets[0] == 0){               //there are no children, so no key in the tree
					return 0;
				}
				else if(key < keys[i]){                 //first time we find a key its less than it should be somewhere in that child
					return searchKey(key, childOffsets[i]);
				}
				else if(key > keys[numberOfKeys-1]){                 //if its not less than any it should be in the last child
					return searchKey(key, childOffsets[numberOfKeys]);
				}	
					
			} else {
				long junk = fileReader.readLong();
			}
		}
		return 0;         //I don't think it can ever get here but if it does we should return 0
	}

	public void traverseTree() throws InterruptedException, IOException {
		//Queue q = new Queue<Long>();
		q = new Queue<Long>();
		q.enqueue(fileOffsetRoot);
		int nodeCount = 0;
		//traverseTreeRecursive(q, nodeCount);	//TODO
		traverseTreeRecursive(nodeCount);
	}

	//public void traverseTreeRecursive(Queue<Long> q, int nodeCount) throws InterruptedException, IOException {
	public void traverseTreeRecursive(int nodeCount) throws InterruptedException, IOException {
		if (q.isEmpty()){
			return;
		}
		if (nodeCount == numNodes){
			return;
		}

		Long fileOffset = q.dequeue();
		nodeCount++;
		System.out.println("This is node number: " + nodeCount);
		System.out.println("FileOffset: "+fileOffset);

		fileReader.seek(fileOffset);

		int numberOfKeys;			//number of keys in a node

		long[] keys;
		int[] frequencies;
		long[] childOffsets;

		numberOfKeys =  fileReader.readInt();
		keys = new long[numberOfKeys];
		frequencies = new int[numberOfKeys];
		childOffsets = new long[numberOfKeys+1];

		try{
		for(int i = 0; i < (2*degree-1); i += 1) {
			if ( i < numberOfKeys) {
				keys[i] = fileReader.readLong();
				frequencies[i] = fileReader.readInt();
				System.out.println("key: " + keys[i]);
			} else {
				long junk1 = fileReader.readLong();
				int junk2 = fileReader.readInt();
//				System.out.println("junk1: "+junk1);
//				System.out.println("junk2: "+junk2);
			}
		}
		
		for(int i = 0; i <= (2*degree); i += 1) {
			if(i <= numberOfKeys) {
				childOffsets[i] = fileReader.readLong();
				System.out.println("FileOffset: "+childOffsets[i]);
				if(childOffsets[0] != 0){
					q.enqueue(childOffsets[i]);
				}

			} else {
				long junk = fileReader.readLong();
//				System.out.println("junk: "+junk);
			}
		}
		}
		catch(EOFException e){
			System.out.println("end of file");
		}
		//traverseTreeRecursive(q,nodeCount);	//TODO: fix?
		traverseTreeRecursive(nodeCount);
	}
	
	/**
	 * method to search for sequences in the query file and print out the 
	 * frequency of occurrence to the console
	 * @throws Exception 
	 */
	public void searchQueries() throws Exception {
		File queryFile = new File(queryFilename);
		Scanner fileScan = new Scanner(queryFile);
		System.out.println("Query and Frequency");
		int totalSequences = 0;
		
			String testString = "TATTTTT";
			Long testLong = ksConverter.stringToKey(testString, sequenceLength);
			int testFreq = searchKey(testLong, fileOffsetRoot);
			System.out.println(testString + ": " + testFreq);

		while(fileScan.hasNext()) {
			String queryString = fileScan.nextLine();
			Long queryLong = ksConverter.stringToKey(queryString, sequenceLength);
			int queryFreq = searchKey(queryLong, fileOffsetRoot);
			System.out.println(queryString + ": " + queryFreq);
			if(queryFreq != 0) {
				totalSequences += queryFreq;
			}
		}
	
		fileScan.close();
		System.out.println("Total Sequences found: "+totalSequences);
	}

}
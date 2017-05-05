import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class GeneBankCreateBTree {
	private int sequenceLength;	//Length of each DNA sequence stored in the BTree, k
	private int degree;	//Number of DNA sequences stored per BTreeNode, t
	private String filename;
	private BTree theBTree;
	private KeyStringConverter ksConverter;
	private int debugMode;

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
	public GeneBankCreateBTree(int cache, int cacheSize, int degree, String filename, int sequenceLength, int debugMode) throws IOException{
		this.degree = degree;
		this.filename = filename;
		this.sequenceLength = sequenceLength;
		this.debugMode = debugMode;

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


		int thisDebugMode = 0;
		if ((args.length == 5 && Integer.parseInt(args[0]) == 0) || args.length == 6) {
			thisDebugMode = Integer.parseInt(args[5]);
			if(!(thisDebugMode == 0 || thisDebugMode == 1)) {
				printUsage();
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}


		//Create an object, pass int the degree, output filename,and sequence length

		GeneBankCreateBTree gbcbt = new GeneBankCreateBTree(thisCache, thisCacheSize, thisDegree, thisFilename, ThisSequenceLength, thisDebugMode);

		long startTime = System.currentTimeMillis();
		
		System.out.println("processing file...");
		gbcbt.sendToParser(); 
//		gbcbt.writeMetadata();
		if(gbcbt.theBTree.myRoot != null){
			if(thisCache == 1){
				if(thisDebugMode == 1) {System.out.println("writing cache...");}
				gbcbt.theBTree.writeCache();
				gbcbt.theBTree.getCacheSize();
			}
			if(thisDebugMode == 1) {
				System.out.println("writing dump file...");
				gbcbt.debugDump();
				}
		} else {
			System.out.println("Empty B Tree");
		}
		System.out.println("Processing Finished.");
		if(thisDebugMode == 1){
			long processTime = System.currentTimeMillis() - startTime;
			long processSeconds = processTime/1000;
			System.out.println("Process Time: "+processSeconds);
		}
	}


	/**
	 * prints the proper command line arguments if the wrong inputs are detected
	 */
	private static void printUsage() {
		System.out.println(
				"Usage:\n"
						+ " java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> \n"
						+ " <cache size> [<debug level>] \n"
						+ " <cache> will speed up the creation of the B Tree be reducing the number of saves"
						+ " <degree> is the degree to be used for the BTree. If 0 is entered, the program will \n"
						+ " 	choose the optimum degree based on a disk block size of 4096 bytes and the size \n"
						+ " 	of the BTreeNode on disk \n"
						+ " <gbk file> is the file of DNA sequences \n"
						+ " <sequence length> is the length of DNA bases to be stored in each BTreeNode \n"
						+ " <cache size>"
						+ " <debug level> [optional]: \n"
						+ " 	0 Any diagnostic messages, help and status messages must be be printed on standard \n"
						+ " 	error stream. \n"
						+ " 	1 The program writes a text file named dump, that has the following line format: \n"
						+ " 	<frequency> <DNA string>. The dump file contains frequency and DNA string \n"
						+ "		(corresponding to the key stored) in an inorder traversal. \n"
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
		int sizeHeader = 4 + 4 + 4 + 8;	//bytes
		int sizeObject = 8 + 4;	//bytes, (2t-1)
		int sizeChild  = 8;		//bytes, (2t)
		int sizeBlock  = 4096;	//bytes, size of block on disk
		int numObjects = (sizeBlock + sizeObject - sizeHeader)/(2*sizeChild + 2*sizeObject);
		return numObjects;
	}

	/**
	 * method to send the gene base file to the parser and B Tree
	 * @throws Exception 
	 */
	public void sendToParser() throws Exception {
        GeneBankParser geneBankParser = new GeneBankParser();
        
        String fileContents = new String(Files.readAllBytes(Paths.get(filename)));
        if(debugMode == 1) {System.out.println("fileContents: "+fileContents);} 

        for(String sequence : geneBankParser.parseSequencesFromFileContents(fileContents)){
            for(String subSequence : geneBankParser.generateSubSequencesFromSequence(sequence, sequenceLength)){
                Long value = geneBankParser.convertSequenceToLong(subSequence);
                theBTree.insert(value);
                if(debugMode == 1) {System.out.println("value: "+value);}
            }
        }
        
        LinkedList<Long> keys = theBTree.inorderTraverseTree();

        while (keys.size() > 0) {
            long key = keys.remove(0);
            long frequency = keys.remove(0);
            if(debugMode == 1) {System.out.printf("%s: %s\n", geneBankParser.convertLongToSequence(key, sequenceLength), frequency);}
        }
	}
}


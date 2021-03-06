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
	private static int debugMode;
	private static int thisCache;
	private static int cacheSize;
	private static int degree;
	private static int sequenceLength;
	private static long fileOffsetRoot;//offset of the root node
	private int numNodes;
	private static RandomAccessFile fileReader;
	private static KeyStringConverter ksConverter;
	private Queue<Long> q;
	private static String bTreeFilename;


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
		this.bTreeFilename = bTreeFilename;
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

		GeneBankSearch.readFile();
		
		gbs.searchQueries();
		
		fileReader.close();

	}


	/**
	 * prints the proper command line arguments if the wrong inputs are detected
	 */
	private static void printUsage() {
		System.out.println(
				"Usage:\n"
				+ " java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> <cache size> \n"
				+ "\t [<debug level>] \n"
				+ " <cache> makes the program run faster by reducing reads from disk"
				+ " <btree file> file that has the B-Tree data \n"
				+ " <query file> file that has the base sequences to find \n"
				+ " <cache size> size of the cache"
				+ " [<debug level>] Optional, 1 prints debug statements to the console"
				);
		System.exit(1);
	}

	
	/**
	 * 
	 * @throws InterruptedException
	 */
	void readTest() throws InterruptedException {
        readFile();
    }

    public static void readFile() throws InterruptedException {
        //File Header Structure:
        //root file offset (long), sequence length (int), degree (int), number of nodes (int),
        //sequence length (int), degree (int), number of nodes (int), root file offset (long)

        try {
            GeneBankParser geneBankParser = new GeneBankParser();

            fileReader.seek(0);
            fileOffsetRoot = fileReader.readLong();
            sequenceLength = fileReader.readInt();    //length of base sequence
            degree = fileReader.readInt();        // the degree of the B Tree nodes


            if(debugMode == 1){System.out.println("sequenceLength: " + sequenceLength);
            System.out.println("degree: " + degree);
            System.out.println("fileReadOffset " + fileOffsetRoot);}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int searchKey(long key, long fileOffset, int degree) throws IOException {

        fileReader.seek(fileOffset);

        int numberOfKeys;            //number of keys in a node

        long[] keys;
        int[] frequencies;
        long[] childOffsets;

        numberOfKeys = fileReader.readInt();
        keys = new long[numberOfKeys];
        frequencies = new int[numberOfKeys];
        childOffsets = new long[numberOfKeys + 1];
        
        if(debugMode == 1){System.out.println("seeking key: "+ksConverter.keyToString(key, sequenceLength)+", "+key);}

        for (int i = 0; i < (2 * degree - 1); i += 1) {
            if (i < numberOfKeys) {
                keys[i] = fileReader.readLong();
                if(debugMode == 1){System.out.println("key: "+ksConverter.keyToString(keys[i], sequenceLength)+", "+keys[i]);}
                frequencies[i] = fileReader.readInt();
                if (keys[i] == key) {
                    return frequencies[i];
                }
            } else {
                long junk1 = fileReader.readLong();
                int junk2 = fileReader.readInt();
            }
        }

        for (int i = 0; i < (2 * degree); i += 1) {
            if (i <= numberOfKeys) {
                childOffsets[i] = fileReader.readLong();
            } else {
                long junk = fileReader.readLong();
            }
        }
        for (int i = 0; i <= numberOfKeys; i += 1) {
            if (childOffsets[0] == 0) {               //there are no children, so no key in the tree
                return 0;
            } else if (key < keys[i]) {                 //first time we find a key its less than it should be somewhere in that child
                return searchKey(key, childOffsets[i], degree);
            } else if (i == (numberOfKeys - 1) ){ 	
            	if (key > keys[numberOfKeys - 1]) {                 //if its not less than any it should be in the last child
            		return searchKey(key, childOffsets[numberOfKeys], degree);
            	}
            } else{
            	//String seq = ksConverter.keyToString(key);
            	if(debugMode == 1){System.out.println("Failed to find: "+ ksConverter.keyToString(key, sequenceLength));}
            }
        }
        return 0;         //I don't think it can ever get here but if it does we should return 0
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
		
		System.out.println("degree "+degree);
		System.out.println("fileOffsetRoot "+fileOffsetRoot);
		System.out.println("sequenceLength "+sequenceLength);
		

		while(fileScan.hasNext()) {
			String queryString = fileScan.nextLine().trim();
			Long queryLong = ksConverter.stringToKey(queryString, sequenceLength);
			int queryFreq = searchKey(queryLong, fileOffsetRoot, degree);
			if(queryFreq > 0) {
				System.out.println(queryString + ": " + queryFreq);
				totalSequences += queryFreq;
			}
		}
	
		fileScan.close();
		System.out.println("Total Sequences found: "+totalSequences);
	}
	
	
	

}
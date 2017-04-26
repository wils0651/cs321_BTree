import java.io.File;

public class GeneBankSearch {
	private String bTreeFilename;
	private String queryFilename;
	private int debugMode;
	
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
	
	public GeneBankSearch(String bTreeFilename, String queryFilename, int debugMode) {
		this.bTreeFilename = bTreeFilename;
		this.queryFilename = queryFilename;
		this.debugMode = debugMode;
		
	}

	public static void main(String[] args) {
		if (args.length < 2 || args.length < 4) {
			printUsage();
		}

		// Check <btree file>
		String thisBTreeFilename = args[0];
		File theFile = new File(thisBTreeFilename);
		if(!theFile.exists()) {
			System.err.println("BTree file not found.");
			System.exit(1);
		}

		//check <query file>
		String thisQueryFilename = args[0];
		File theQueryFile = new File(thisQueryFilename);
		if(!theQueryFile.exists()) {
			System.err.println("Query file not found.");
			System.exit(1);
		}
		


		int thisDebugMode = -1;
		if (args.length > 2) {
			thisDebugMode = Integer.parseInt(args[2]);
			if(!(thisDebugMode == 0 || thisDebugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}
		
		GeneBankSearch gbs = new GeneBankSearch(thisBTreeFilename, thisQueryFilename, thisDebugMode);
	}
	
	
	/**
	 * prints the proper command line arguments if the wrong inputs are detected
	 */
	private static void printUsage() {
		System.out.println(
			"Usage:\n"
			+ " java GeneBankSearch <btree file> <query file> [<debug level>]\n"
			+ "        TODO     \n"
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
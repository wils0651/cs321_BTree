import java.io.File;

public class GeneBankSearch {
	private String filename;
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
	 */
	
	public GeneBankSearch(String filename, int debugMode) {
		this.filename = filename;
		this.debugMode = debugMode;
		
	}

	public static void main(String[] args) {
		if (args.length < 2 || args.length < 4) {
			printUsage();
		}

		// Check <btree file>
		String thisFilename = args[1];
		File theFile = new File(thisFilename);
		if(!theFile.exists()) {
			System.err.println("File not found.");
			System.exit(1);
		}

		// TODO: check <query file>


		int thisDebugMode = -1;
		if (args.length > 2) {
			thisDebugMode = Integer.parseInt(args[2]);
			if(!(thisDebugMode == 0 || thisDebugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}
		
		GeneBankSearch gbs = new GeneBankSearch(thisFilename, thisDebugMode);
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
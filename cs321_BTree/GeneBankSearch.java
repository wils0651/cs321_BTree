import java.io.File;

public class GeneBankSearch {
	
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

	public static void main(String[] args) {
		if (args.length < 2 || args.length < 4) {
			printUsage();
		}

		// Check <btree file>
		String filename = args[1];
		File theFile = new File(filename);
		if(!theFile.exists()) {
			System.err.println("File not found.");
			System.exit(1);
		}

		// TODO: check <query file>


		int debugMode;
		if (args.length > 2) {
			debugMode = Integer.parseInt(args[2]);
			if(!(debugMode == 0 || debugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}
	}
	
	
	/**
	 * prints the proper command line arguments if the wrong inputs are detected
	 */
	private static void printUsage() {
		//TODO
		System.out.println(
			"Usage:\n"
			+ " java GeneBankSearch <btree file> <query file> [<debug level>]\n"
			+ " TODO"
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

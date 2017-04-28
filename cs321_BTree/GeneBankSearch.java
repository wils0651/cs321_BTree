import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import javax.sound.midi.Sequence;

import sun.misc.Queue;

public class GeneBankSearch {
	private String bTreeFilename;
	private String queryFilename;
	private int debugMode;
	
	private int degree;
	private int sequenceLength;
	private long fileOffsetRoot;//offset of the rootnode
	private int numNodes;
	private RandomAccessFile fileReader;
	
	
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

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		if (args.length < 2 || args.length > 3) {
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
		String thisQueryFilename = args[1];
		File theQueryFile = new File(thisQueryFilename);
		if(!theQueryFile.exists()) {
			System.err.println("Query file not found.");
			System.exit(1);
		}
		
		


		int thisDebugMode = 0;
		if (args.length > 2) {
			//TODO: change if another Debug level is added
			thisDebugMode = Integer.parseInt(args[2]);
			if(!(thisDebugMode == 0 || thisDebugMode == 1)) {
				throw new IllegalArgumentException("Improper Debug Mode Selection");
			}
		}
		
		GeneBankSearch gbs = new GeneBankSearch(thisBTreeFilename, thisQueryFilename, thisDebugMode);
		
		int fileNameLength = thisQueryFilename.length();
		//gbs.sequenceLength = Integer.parseInt(thisQueryFilename.substring(fileNameLength-1, fileNameLength) );
		gbs.sequenceLength = 6;
		
		
		gbs.readMetadata();
		
		gbs.readFile();
		
		
		
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
	
	//TODO: move to Search?
		/**
		 * @throws FileNotFoundException 
		 * 
		 */
		private void readMetadata() throws FileNotFoundException {
			String fileName = "BTreeMetadata.txt";
			File theFile = new File(fileName);
			Scanner fileScan = new Scanner(theFile);
			
			//String gbkFileName = fileScan.nextLine();
			degree = Integer.parseInt(fileScan.nextLine() );
			sequenceLength = Integer.parseInt(fileScan.nextLine() );
			fileOffsetRoot = Long.parseLong(fileScan.nextLine() );//offset of the rootnode
			System.out.println("fileOffsetRoot: "+ fileOffsetRoot);
			numNodes= Integer.parseInt(fileScan.nextLine() );	//
			System.out.println("numNodes: " + numNodes);
		}
		
//		public BTreeNode readFile() {
		public void readFile() throws InterruptedException {
			String mode = "r";			//rw is read write
			//int numNodes;				//number of nodes in the full file
			//long fileOffsetRoot;		//fileoffset of the root
			
			
			try{ 
				fileReader = new RandomAccessFile(bTreeFilename, mode);
				fileReader.seek(0);
				numNodes = fileReader.readInt();	// the number of keys in the long
				fileOffsetRoot = fileReader.readLong();
				System.out.println("numNodes: " + numNodes);
				System.out.println("fileOffsetRoot: "+ fileOffsetRoot);
				
				traverseTree();
				
				fileReader.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		public void traverseTree() throws InterruptedException, IOException {
			Queue q = new Queue<Long>();
			q.enqueue(fileOffsetRoot);
			int nodeCount = 0;
			traverseTreeRecursive(q, nodeCount);
		}

		public void traverseTreeRecursive(Queue<Long> q, int nodeCount) throws InterruptedException, IOException {
			if (q.isEmpty()){
				return;
			}
			if (nodeCount ==numNodes){
				return;
			}
			
			Long fileOffset = q.dequeue();
			nodeCount++;
			System.out.println("This is node number: " + nodeCount);
			System.out.println(fileOffset);

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
					System.out.println("key: " + keys[i]);
				} else {
					long junk1 = fileReader.readLong();
					int junk2 = fileReader.readInt();
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
				}
			}

			traverseTreeRecursive(q,nodeCount);
		}

//
//				for(int i = 0; i < (2*t-1); i += 1) {
//					if (i < rear) {
//						fileWriter.writeLong(keys[i].key);		//Writes a long to the file as eight bytes, high byte first.
//						fileWriter.writeInt(keys[i].frequency);
//					} else {
//						fileWriter.writeLong(0);
//						fileWriter.writeInt(0);
//					}
//				}
//				for(int i = 0; i < childRear; i += 1) {
//					if(i<rear) {
//						fileWriter.writeLong(children[i].getFileOffset());		//Writes a long to the file as eight bytes, high byte first.
//					} else {
//						fileWriter.writeLong(0);
//					}
//				}
//				fileWriter.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//
//			}
	
	
}
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 */
public class TylerTest {
	private static int degree;
	private long rootOffset;
	private static long rootReadOffset;
	private static int sequenceLength;
	private static int numNodes;
	private static BTree tylerTree;
	private static RandomAccessFile fileReader;


	private static void writeMetadata(BTree tylerTree) {
		/* Metadata storage. We need to store some metadata about the BTree on disk. For
		 * example, we can store 
		 * 	the degree of the tree, 
		 * 	sequence length
		 * 	the byte offset of the root node (so we can find it), 
		 * 	the number of nodes etc. 
		 * This information could be stored in separate metadata file or it can be 
		 * stored at the beginning of the BTree file.
		 */
		String fileName = "BTreeMetadata2.txt";

		try{
			PrintWriter writer1 = new PrintWriter(fileName, "UTF-8");
			//writer1.println(gbkFileName);	//name of the BTree file
			writer1.println(tylerTree.getDegree());	//degree of tree;
			writer1.println(tylerTree.getSequenceLength());	//
			writer1.println(tylerTree.getRoot().getFileOffset());	//offset of the rootnode
			writer1.println(tylerTree.numNodes());	//numberOfNodes 

			writer1.close();
		} catch (IOException e) {
			System.err.println("Error creating file: " + fileName);
			System.exit(1);
		}
	}

	public static void main(String[] args) throws Exception {
		degree = 4;
		sequenceLength = 3; 

		
		int cache = 0;

		BTree tylerTree = new BTree(cache, 1000, degree, sequenceLength, "TyTest.txt");
		if(Integer.parseInt(args[0]) == 1){
			Scanner scan = new Scanner(System.in);		
			System.out.println();
			System.out.println("---------------------");
			System.out.println();
			for (int i=0; i< 1000; i++){
				tylerTree.insert(i);
				System.out.println(i);
			}
			for (int i=0; i< 100; i++){
				tylerTree.insert(i);
				//tylerTree.traverseTree();
			}

			tylerTree.insert(256);

			tylerTree.insert(29);
			
			if(cache == 1){
				tylerTree.writeCache();
				System.out.println(tylerTree.getCacheSize());
			}
			System.out.println("done with cache?");
			writeMetadata(tylerTree);
			System.out.println(tylerTree.getRoot().getFileOffset());

			LinkedList<Long> testList = tylerTree.inorderTraverseTree();
			for(Long l: testList){
				System.out.println(l);
			}
			//			
			//			while(true) {
			//				System.out.println("Enter a number to add to your tree, Tyler");
			//				long treeInsert = scan.nextLong();
			//				tylerTree.insert(treeInsert);
			//				tylerTree.traverseTree();
			//			}



		}
		else if (Integer.parseInt(args[0]) == 0){
			fileReader = new RandomAccessFile("TyTest.txt", "r");

			readFile();



		}
		else if (Integer.parseInt(args[0]) == 2){
			
			int sequenceLength = 7;
			FileInputStream stream = new FileInputStream("test3.gbk");
			Parser aparse = new Parser(stream, sequenceLength);
			KeyStringConverter ks = new KeyStringConverter();
			HashMap<String, Integer> testMap = new HashMap<String,Integer>();
			
			int count = 0;
			while(aparse.hasMore()){
				System.out.println("number of sequences so far" +count);
				StringBuilder sb = new StringBuilder();
				sb.append(ks.keyToString(aparse.getNextKey(), sequenceLength));
				sb.reverse();
				System.out.println(sb.toString());
				if(testMap.containsKey(sb.toString())){
					testMap.put(sb.toString(), testMap.get(sb.toString())+1);
				}
				else{
					testMap.put(sb.toString(), 1);
				}
				count++;
			}

			for(Map.Entry<String, Integer> entry: testMap.entrySet()){
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		}
	}

	public static void readFile() throws InterruptedException {
		//File Header Structure:
		//root file offset (long), sequence length (int), degree (int), number of nodes (int), 
		//sequence length (int), degree (int), number of nodes (int), root file offset (long)

		try{ 
			fileReader.seek(0);
			rootReadOffset = fileReader.readLong();
			sequenceLength = fileReader.readInt();	//length of base sequence
			degree = fileReader.readInt();		// the degree of the B Tree nodes
			numNodes = fileReader.readInt();	// the number of keys in the long

			System.out.println("sequenceLength: " + sequenceLength);
			System.out.println("degree: " + degree);
			System.out.println("numNodes: " + numNodes);
			System.out.println("fileReadOffset "+ rootReadOffset);

			for (int i=0; i< 1200; i++){
				System.out.println("number of occurences of " + i +": " + searchKey(i, rootReadOffset));
			}
			
			System.out.println("number of occurences of " + 3 +": " + searchKey(3, rootReadOffset));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int searchKey(long key, long fileOffset) throws IOException{

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

		for(int i = 0; i < (2*degree); i += 1) {
			if(i <= numberOfKeys) {
				childOffsets[i] = fileReader.readLong();		
			} else {
				long junk = fileReader.readLong();
			}
		}
		for(int i = 0; i <= numberOfKeys; i += 1) {
			if(childOffsets[0] == 0){               //there are no children, so no key in the tree
				return 0;
			}
			else if(key < keys[i]){                 //first time we find a key its less than it should be somewhere in that child
				return searchKey(key, childOffsets[i]);
			}
			else if(key > keys[numberOfKeys-1]){                 //if its not less than any it should be in the last child
				return searchKey(key, childOffsets[numberOfKeys]);
			}	
		}
		return 0;         //I don't think it can ever get here but if it does we should return 0
	}


}

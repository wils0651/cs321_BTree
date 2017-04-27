import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.Scanner;

/**
 */
public class TylerTest {

	public static void main(String[] args) throws InterruptedException, IOException {
		BTree tylerTree = new BTree(4, "TyTest.txt");

		if(Integer.parseInt(args[0]) == 1){
			Scanner scan = new Scanner(System.in);

//			Parser parse = new Parser(new FileInputStream("test1mod.gbk"), 5);
//			while(parse.hasMore()){
//				long key = parse.stringToKey(parse.nextSubSequence(), 5);
//				System.out.println(key);
//				tylerTree.insert(key);
//				tylerTree.traverseTree();
//			}
		

					for (int i=0; i< 61; i++){
						tylerTree.insert(i);
						tylerTree.traverseTree();
					}
					tylerTree.insert(101);
					tylerTree.traverseTree();
					tylerTree.insert(100);
					tylerTree.traverseTree();
		
		//			 Random ran = new Random(); 
		//			for (int i = 10000; i >= 0; i--){
		//				int x = ran.nextInt(100);
		//				System.out.println("adding "+x);
		//				tylerTree.insert(x);

					while(true) {
		
						System.out.println("Enter a number to add to your tree, Tyler");
						long treeInsert = scan.nextLong();
						tylerTree.insert(treeInsert);
						tylerTree.traverseTree();
					}
		//			
	

//			 Random ran = new Random(); 
//			for (int i = 10000; i >= 0; i--){
//				int x = ran.nextInt(100);
//				System.out.println("adding "+x);
//				tylerTree.insert(x);
//				tylerTree.traverseTree();
//			}

//			while(true) {
//
//				System.out.println("Enter a number to add to your tree, Tyler");
//
//				long treeInsert = scan.nextLong();
//				tylerTree.insert(treeInsert);
//				tylerTree.traverseTree(tylerTree.getRoot());
//			}
//			
//			System.out.println(tylerTree.search(tylerTree.getRoot(), 20));
//			tylerTree.traverseTree();
		}
		else if (Integer.parseInt(args[0]) == 2){
			RandomAccessFile file = new RandomAccessFile("TyTest.txt", "rw");

			int rear = file.readInt();
			System.out.println("rear: "+rear);
			int count = 0;
			while(true){
				if(count < rear) {
					long key = file.readLong();
					int frequency = file.readInt();

					System.out.println(key);
					System.out.println(frequency);
					for(int i = 0; i < frequency; i++){
						tylerTree.insert(key);
					}
				}
				tylerTree.traverseTree();
			}
		}
	}
}

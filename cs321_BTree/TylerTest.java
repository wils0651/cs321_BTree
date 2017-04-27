import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 */
public class TylerTest {

	public static void main(String[] args) throws InterruptedException, IOException {
		BTree tylerTree = new BTree(3, "TyTest.txt");

		if(Integer.parseInt(args[0]) == 1){
			Scanner scan = new Scanner(System.in);

			System.out.println("adding 5");
			tylerTree.insert(5);
			System.out.println("adding 10");
			tylerTree.insert(10);
			System.out.println("adding 15");
			tylerTree.insert(15);
			System.out.println("adding 20");
			tylerTree.insert(20);
			System.out.println("adding 25");
			tylerTree.insert(25);
			System.out.println("adding 30");
			tylerTree.insert(30);
			System.out.println("adding 7");
			tylerTree.insert(7);
			System.out.println("adding 30");
			tylerTree.insert(30);
			System.out.println("adding 7");
			tylerTree.insert(7);


//			while(true) {
//
//				System.out.println("Enter a number to add to your tree, Tyler");
//
//				long treeInsert = scan.nextLong();
//				tylerTree.insert(treeInsert);
//				tylerTree.traverseTree(tylerTree.getRoot());
//			}
//			
			System.out.println(tylerTree.search(tylerTree.getRoot(), 20));
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
				tylerTree.traverseTree(tylerTree.getRoot());
			}
		}
	}
}

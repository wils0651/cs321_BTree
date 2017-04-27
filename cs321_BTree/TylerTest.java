import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.Scanner;

/**
 */
public class TylerTest {

	public static void main(String[] args) throws InterruptedException, IOException {
		BTree tylerTree = new BTree(2, "TyTest.txt");

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
			System.out.println("adding 6");
			tylerTree.insert(6);
			System.out.println("adding 5");
			tylerTree.insert(5);
			System.out.println("adding 4");
			tylerTree.insert(4);
			System.out.println("adding 3");
			tylerTree.insert(3);
			System.out.println("adding 2");
			tylerTree.insert(2);
			System.out.println("adding 1");
			tylerTree.insert(1);
			System.out.println("adding 60");
			tylerTree.insert(60);
			System.out.println("adding 65");
			tylerTree.insert(65);
			System.out.println("adding 70");
			tylerTree.insert(70);
			System.out.println("adding 24");
			tylerTree.insert(24);

			 Random ran = new Random(); 
			for (int i = 10000; i >= 0; i--){
				int x = ran.nextInt(100);
				System.out.println("adding "+x);
				tylerTree.insert(x);
				tylerTree.traverseTree(tylerTree.getRoot());
			}

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
			tylerTree.traverseTree(tylerTree.getRoot());
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

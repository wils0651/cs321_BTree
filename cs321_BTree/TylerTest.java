import java.util.Scanner;

/**
 */
public class TylerTest {

    public static void main(String[] args) throws InterruptedException {
        BTree tylerTree = new BTree(2, "TyTest.txt");
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
        
        while(true) {

            System.out.println("Enter a number to add to your tree, Tyler");

            long treeInsert = scan.nextLong();
            tylerTree.insert(treeInsert);
            tylerTree.traverseTree(tylerTree.getRoot());
        }

    }

}

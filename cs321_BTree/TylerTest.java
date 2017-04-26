import java.util.Scanner;

/**
 */
public class TylerTest {

    public static void main(String[] args) throws InterruptedException {
        BTree tylerTree = new BTree(2);
        Scanner scan = new Scanner(System.in);
        
        tylerTree.insert(5);
        tylerTree.insert(10);
        tylerTree.insert(15);
        tylerTree.insert(20);
        tylerTree.insert(25);
        tylerTree.insert(30);
        tylerTree.insert(7);
        
        while(true) {
            System.out.println("Enter a number to add to your tree, Michael");
            long treeInsert = scan.nextLong();
            tylerTree.insert(treeInsert);
            tylerTree.traverseTree(tylerTree.getRoot());
        }

    }

}

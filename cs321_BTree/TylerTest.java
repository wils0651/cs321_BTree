import java.util.Scanner;

/**
 * Created by Tylermortis on 4/22/2017.
 */
public class TylerTest {

    public static void main(String[] args) throws InterruptedException {
        BTree tylerTree = new BTree(2);
        Scanner scan = new Scanner(System.in);
        tylerTree.insert(5);
        tylerTree.insert(15);
        tylerTree.insert(25);
        tylerTree.insert(35);
        tylerTree.insert(45);
        tylerTree.insert(55);
        tylerTree.insert(65);
        tylerTree.insert(10);
        tylerTree.insert(20);
        tylerTree.insert(70);
        while(true) {
            System.out.println("Enter a number to add to your tree, Tyler");
            long treeInsert = scan.nextLong();
            tylerTree.insert(treeInsert);
            tylerTree.traverseTree(tylerTree.getRoot());
        }

    }

}

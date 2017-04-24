import java.util.Scanner;

/**
 * Created by Tylermortis on 4/22/2017.
 */
public class TylerTest {

    public static void main(String[] args) throws InterruptedException {
        BTree tylerTree = new BTree(4);
        Scanner scan = new Scanner(System.in);

        while(true) {
            System.out.println("Enter a number to add to your tree Tyler");
            long treeInsert = scan.nextLong();
            tylerTree.insert(treeInsert);
            tylerTree.traverseTree(tylerTree.getRoot());
        }

    }

}

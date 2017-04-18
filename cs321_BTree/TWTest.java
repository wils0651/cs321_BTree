import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class TWTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String testSequence = "actgaactg";
		System.out.println("testSequence: "+testSequence);
		long testKey = GeneBankCreateBTree.stringToKey(testSequence);
		System.out.println("testKey: "+Long.toBinaryString(testKey));
		String returnedSeq = GeneBankCreateBTree.keyToString(testKey);
		System.out.println("returnedSeq: " + returnedSeq);
		

		//Tyler's test
//		File file = new File("something.gbk");
//		int sequenceLength = 3;
//		Parser aparse = new Parser(file, 3);
//		aparse.parse();
		BTLinkedList list = new BTLinkedList(3);
		FileInputStream test = new FileInputStream("test.txt");
		DataInputStream data = new DataInputStream(test);
		
		while(data.available()>0){
			list.add((char)data.readByte());
		}
		
		while(!list.isEmpty()){
			System.out.println(list.getSubsequence());
			
		}
	}

}

import java.io.FileInputStream;
import java.io.IOException;

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
		FileInputStream file = new FileInputStream("test.txt");
		int sequenceLength = 3;
		Parser aparse = new Parser(file, sequenceLength);
		while(aparse.hasMore()){
			String s = aparse.nextSubSequence();
			if (s.length() == 3){
				System.out.println(s);
			}
		}
		

	}


}

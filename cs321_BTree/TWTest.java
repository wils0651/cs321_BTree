import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class TWTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String testSequence = "actgaactg";
		int k = 3;	//Sequence length
		System.out.println("testSequence: "+testSequence + ", sequence length: " + k);

		long testKey = GeneBankCreateBTree.stringToKey(testSequence, k);
		System.out.println("testKey: "+Long.toBinaryString(testKey));
		//String returnedSeq = GeneBankCreateBTree.keyToString(testKey);
		//System.out.println("returnedSeq: " + returnedSeq);


		//Tyler's test
		FileInputStream file = new FileInputStream("test.txt");
		int sequenceLength = 3;
		Parser aparse = new Parser(file, sequenceLength);
		while(aparse.hasMore()){
			String s = aparse.nextSubSequence();
			if (s.length() == sequenceLength){
				System.out.println(s);
			}
		}



		String returnedSeq = GeneBankCreateBTree.keyToString(testKey, k);
		System.out.println("returnedSeq: " + returnedSeq);

	}
}
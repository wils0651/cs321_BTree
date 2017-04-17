

public class TWTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String testSequence = "actgaactg";
		System.out.println("testSequence: "+testSequence);
		long testKey = GeneBankCreateBTree.stringToKey(testSequence);
		System.out.println("testKey: "+Long.toBinaryString(testKey));
		String returnedSeq = GeneBankCreateBTree.keyToString(testKey);
		System.out.println("returnedSeq: " + returnedSeq);
		

	}

}

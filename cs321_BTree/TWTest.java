import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class TWTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		KeyStringConverter ksConverter = new KeyStringConverter();
		
		String testSequence = "actgact";
		int k = 7;	//Sequence length
		System.out.println("testSequence: "+testSequence + ", sequence length: " + k);

		long key = ksConverter.stringToKey(testSequence, k);
		System.out.println("converted to key: "+key);
		
		String testSeq2 = ksConverter.keyToString(key, k);
		System.out.println("converted back to string: "+testSeq2);
		
		//long testKey = GeneBankCreateBTree.stringToKey(testSequence, k);
		//System.out.println("testKey: "+Long.toBinaryString(testKey));
		//String returnedSeq = GeneBankCreateBTree.keyToString(testKey);
		//System.out.println("returnedSeq: " + returnedSeq);


//		//Tyler's test
//		FileInputStream file = new FileInputStream("test.txt");
//		int sequenceLength = 3;
//		Parser aparse = new Parser(file, sequenceLength);
//		while(aparse.hasMore()){
//			String s = aparse.nextSubSequence();
//			if (s.length() == sequenceLength){
//				System.out.println(s);
//			}
//		}



		//String returnedSeq = GeneBankCreateBTree.keyToString(testKey, k);
		//System.out.println("returnedSeq: " + returnedSeq);

	}
}


// DEAD Code

//				//public byte[] longToBytes(long x) {
//					ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//					buffer.putLong(x);
//					return buffer.array();
//				//}
//
//				//public long bytesToLong(byte[] bytes) {
//					ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//					buffer.put(bytes);
//					buffer.flip();//need flip 
//					return buffer.getLong();
//				//}



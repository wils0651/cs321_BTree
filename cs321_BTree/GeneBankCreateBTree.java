
public class GeneBankCreateBTree {
	
	/*
	 * Usage:
	 * java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]
	 * 
	 * degree is the degree to be used for the BTree. If the user specifies 0, then your
	 * program should choose the optimum degree based on a disk block size of 4096 bytes
	 * and the size of your BTree node on disk
	 * 
	 * debug level:
	 * 0 Any diagnostic messages, help and status messages must be be printed on standard
	 * error stream.
	 * 1 The program writes a text file named dump, that has the following line format:
	 * <frequency> <DNA string>. The dump file contains frequency and DNA string
	 * (corresponding to the key stored) in an inorder traversal.
	*/
	public GeneBankCreateBTree(){
		
	}
	
	//what happens if we don't get a full 32 base number (16 bases?)
	
	
	public static long stringToKey(String subsequence) {
		long theKey = 0;
		subsequence.toLowerCase();
		for (int i = 0; i < subsequence.length(); i += 1) {
			//System.out.println(subsequence.substring(i, i+1));
			long base = mapBase(subsequence.substring(i, i+1));
			theKey = theKey | (base<<2*i);	//setbit
		}
		return theKey;
	}
	
	private static long mapBase(String theBase) {
		if( theBase.equals("a") ) {
			return 0b00;
		} else if (theBase.equals("t")) {
			return 0b11;
		} else if (theBase.equals("c")) {
			return 0b01;
		} else if (theBase.equals("g")) {
			return 0b10;
		} else {
			System.err.println("mapBase Error");
			return -1;
		}
	}
	
	public static String keyToString(long theKey) {
		String theSequence = "";
		for (int i = 0; i < 32; i += 2) {
			//System.out.println(subsequence.substring(i, i+1));
			//long base = mapBase(subsequence.substring(i, i+1));
			long theBits = (theKey>>i) & (~(~0<<2));
			//System.out.println("i: " + i + ", theBits: " + Long.toBinaryString(theBits));
			String base = mapKey(theBits);
			theSequence += base;
		}
		return theSequence;
	}
	
	public static String mapKey(long twoDigit) {
		if( twoDigit ==  0b00) {
			return "a";	//a
		} else if (twoDigit == 0b11) {
			return "t";	//t
		} else if (twoDigit == 0b01) {
			return "c";	//c 
		} else if (twoDigit == 0b10) {
			return "g";	//g
		} else {
			System.err.println("mapBase Error");
			return "error";
		}
	}
	
	
	
	
	public static void main(String args[]) {
		String testSequence = "actgaactg";
		long testKey = stringToKey(testSequence);
		System.out.println("testKey: "+Long.toBinaryString(testKey));
		long mapTest = mapBase("t");
		//System.out.println("mapBase: "+mapTest);
		String returnedSeq = keyToString(testKey);
		System.out.println("returnedSeq: " + returnedSeq);
		
	}
	

}

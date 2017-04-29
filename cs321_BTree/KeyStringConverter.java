

public class KeyStringConverter {
	
	/**
	 * Constructor
	 */
	public KeyStringConverter() {
		
	}
	

	/**
	 * converts a string of letter DNA bases to a number
	 * @param subsequence
	 * @param sequenceLength
	 * @return a long that represents the sequence
	 * @throws Exception 
	 */
	public long stringToKey(String subsequence, int sequenceLength) throws Exception {
		long theKey = 0;
		subsequence = subsequence.toLowerCase();
		int k = sequenceLength;
		for (int i = 1; i <= k; i += 1) {
			//System.out.println(subsequence.substring(i, i+1));
			long base = mapBase(subsequence.substring(i-1, i));
			theKey = theKey | (base<<2*(i-1));	//setbit
		}
		return theKey;
	}

	/**
	 * helper method that converts a letter DNA base to a two digit binary number
	 * @param theBase
	 * @return base as a number
	 * @throws Exception 
	 */
	private long mapBase(String theBase) throws Exception {
		if( theBase.equals("a") ) {
			return 0b00;
		} else if (theBase.equals("t")) {
			return 0b11;
		} else if (theBase.equals("c")) {
			return 0b01;
		} else if (theBase.equals("g")) {
			return 0b10;
		} else {
			System.err.println("mapBase Error: " + theBase);
			throw new Exception();
			//System.exit(1);
			//return -1;
		}
	}

	/**
	 * Converts a binary number of DNA bases to a sting of letters 
	 * @param theKey
	 * @param sequenceLength
	 * @return
	 */
	public String keyToString(long theKey, int sequenceLength) {
		String theSequence = "";
		int k = sequenceLength;
		for (int i = 0; i < k; i += 1) {
			//System.out.println(subsequence.substring(i, i+1));
			//long base = mapBase(subsequence.substring(i, i+1));
			long theBits = (theKey>>2*i) & (~(~0<<2));
			//System.out.println("i: " + i + ", theBits: " + Long.toBinaryString(theBits));
			String base = mapKey(theBits);
			theSequence += base;
		}
		return theSequence;
	}


	/**
	 * private helper method to convert a two binary digit number to the appropriate 
	 * DNA base 
	 * @param twoDigit
	 * @return DNA base
	 */
	private String mapKey(long twoDigit) {
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


}

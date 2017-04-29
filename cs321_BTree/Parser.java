import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser {
	private FileInputStream input;
	private int sequenceLength;
	private DataInputStream data;
	private boolean firstTime;
	private BTLinkedList characterList;
	private boolean hasMore;
	private KeyStringConverter ksConverter;

	public Parser(FileInputStream input, int sequenceLength) throws FileNotFoundException{
		this.input = input;
		this.sequenceLength = sequenceLength;
		data = new DataInputStream(input);
		firstTime = true;
		characterList = new BTLinkedList(sequenceLength);
		hasMore = true;
		ksConverter = new KeyStringConverter();
	}

	public String nextSubSequence() throws IOException{
		if (firstTime){
			skipHeader();
			firstTime = false;
		}

		char g;
		String ss;
		
		//System.out.println("data.available: " + data.available());

		if (data.available() == 0){
			hasMore = false;
		}
		try{
		while((g = (char)data.readByte()) != -1){
			//g = (char)data.readByte();
			characterList.add(g);
			ss = characterList.getSubsequence();

			//TODO: fix this
			if(contains(ss, '/')) {
				characterList.poll();
				if(contains(ss, '/')) {
					skipHeader();
				}
			}

			if(contains(ss,'N') || contains(ss,'n') || contains(ss, ' ') || contains(ss, '\n') || 
					containsDigit(ss) || contains(ss, '\t') || contains(ss, '\r')){

				for (int i = 0; i < ss.length(); i++){
					characterList.poll();
				}
			}
			else if(ss.length() == sequenceLength){
				characterList.poll();
				return ss;
			}
		}
		}
		catch(EOFException e){
			return "";
		}

		return "";
	}

	public void printList(){
		for (Character c: characterList){
			System.out.println(c);
		}
	}

	public boolean hasMore(){
		return hasMore;
	}

	public void skipHeader() throws IOException{
		while(data.available() > 0){
			if((char)data.readByte() == 'O'){
				if((char)data.readByte() == 'R')
					if((char)data.readByte() == 'I')
						if((char)data.readByte() == 'G')
							if((char)data.readByte() == 'I')
								if((char)data.readByte() == 'N'){

									return;
								}
			}
		}
	}

	public boolean contains(String s, char find){
		for(int i = 0; i < s.length(); i++){
			if (s.charAt(i) == find){
				return true;
			}
		}
		return false;
	}

	public boolean containsDigit(String s){
		for(int i = 0; i < s.length(); i++){
			if (Character.isDigit(s.charAt(i))){
				return true;
			}
		}
		return false;
	}


	/**
	 * method to return the next sub sequence as a long
	 * @throws Exception 
	 */
	public long getNextKey() throws Exception {
		String ss = nextSubSequence();
		if(ss.length() < sequenceLength){
			return -1;
		}
		return ksConverter.stringToKey(ss, sequenceLength );
	}
//
//	/**
//	 * converts a string of letter DNA bases to a number
//	 * @param subsequence
//	 * @param sequenceLength
//	 * @return a long that represents the sequence
//	 */
//	public long stringToKey(String subsequence, int sequenceLength) {
//		long theKey = 0;
//		subsequence = subsequence.toLowerCase();
//		int k = sequenceLength;
//		for (int i = 1; i <= k; i += 1) {
//			//System.out.println(subsequence.substring(i, i+1));
//			long base = mapBase(subsequence.substring(i-1, i));
//			theKey = theKey | (base<<2*(i-1));	//setbit
//		}
//		return theKey;
//	}
//
//	/**
//	 * helper method that converts a letter DNA base to a two digit binary number
//	 * @param theBase
//	 * @return base as a number
//	 */
//	private long mapBase(String theBase) {
//		if( theBase.equals("a") ) {
//			return 0b00;
//		} else if (theBase.equals("t")) {
//			return 0b11;
//		} else if (theBase.equals("c")) {
//			return 0b01;
//		} else if (theBase.equals("g")) {
//			return 0b10;
//		} else {
//			System.err.println("mapBase Error: " + theBase);
//			return -1;
//		}
//	}
//
//	/**
//	 * Converts a binary number of DNA bases to a sting of letters 
//	 * @param theKey
//	 * @param sequenceLength
//	 * @return
//	 */
//	public String keyToString(long theKey, int sequenceLength) {
//		String theSequence = "";
//		int k = sequenceLength;
//		for (int i = 0; i < k; i += 1) {
//			//System.out.println(subsequence.substring(i, i+1));
//			//long base = mapBase(subsequence.substring(i, i+1));
//			long theBits = (theKey>>2*i) & (~(~0<<2));
//			//System.out.println("i: " + i + ", theBits: " + Long.toBinaryString(theBits));
//			String base = mapKey(theBits);
//			theSequence += base;
//		}
//		return theSequence;
//	}
//
//
//	/**
//	 * private helper method to convert a two binary digit number to the appropriate 
//	 * DNA base 
//	 * @param twoDigit
//	 * @return DNA base
//	 */
//	private String mapKey(long twoDigit) {
//		if( twoDigit ==  0b00) {
//			return "a";	//a
//		} else if (twoDigit == 0b11) {
//			return "t";	//t
//		} else if (twoDigit == 0b01) {
//			return "c";	//c 
//		} else if (twoDigit == 0b10) {
//			return "g";	//g
//		} else {
//			System.err.println("mapBase Error");
//			return "error";
//		}
//	}



}
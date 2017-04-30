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

				if(g != ' ' && g != '\n' && g != '\r' && g != '\t'){
					characterList.add(g);
				}

				ss = characterList.getSubsequence();

				if(contains(ss, '/')) {
					characterList.poll();
					if(contains(ss, '/')) {
						skipHeader();
					}
				}

				else if(contains(ss,'N') || contains(ss,'n') || containsDigit(ss)){
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
}
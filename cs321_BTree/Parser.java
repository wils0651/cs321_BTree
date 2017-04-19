import java.io.DataInputStream;
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

	public Parser(FileInputStream input, int sequenceLength) throws FileNotFoundException{
		this.input = input;
		this.sequenceLength = sequenceLength;
		data = new DataInputStream(input);
		firstTime = true;
		characterList = new BTLinkedList(sequenceLength);
		hasMore = true;
	}
	
	public String nextSubSequence() throws IOException{
		if (firstTime){
			skipHeader();
			firstTime = false;
		}
		char g;
		String ss;
		
		if(data.available() == 1){
			hasMore = false;
		}
		
		while(data.available() > 0){
			g = (char)data.readByte();
			characterList.add(g);
			ss = characterList.getSubsequence();
			
			if(contains(ss,'N')){
				for (int i = 0; i < sequenceLength; i++){
					characterList.poll();
				}
			}
			else if(ss.length() == sequenceLength){
				characterList.poll();
				return ss;
			}
		}
		return "";
	}
	
	public boolean hasMore(){
		return hasMore;
	}

	public void skipHeader() throws IOException{
		while(data.available() > 0){
			if((char)data.readByte() == 'O')
				if((char)data.readByte() == 'R')
					if((char)data.readByte() == 'I')
						if((char)data.readByte() == 'G')
							if((char)data.readByte() == 'I')
								if((char)data.readByte() == 'N'){
									return;
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
}
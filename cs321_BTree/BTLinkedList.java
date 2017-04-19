import java.util.LinkedList;

public class BTLinkedList extends LinkedList<Character>{
private int sequenceLength;
	
	
	public BTLinkedList(int sequenceLength){
		super();
		this.sequenceLength = sequenceLength;
	}
	
	public String getSubsequence(){
		
		String retval = "";
		
		for(int i = 0; i < sequenceLength; i++){
			retval += this.get(i);
		}
		return retval;
	}
}

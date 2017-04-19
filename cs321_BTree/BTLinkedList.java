import java.util.LinkedList;

public class BTLinkedList extends LinkedList<Character>{
private int sequenceLength;
	
	
	public BTLinkedList(int sequenceLength){
		super();
		this.sequenceLength = sequenceLength;
	}
	
	public String getSubsequence(){
		
		//String retval = "";
		String retval = "" + this.remove();
		
		for(int i = 1; i < sequenceLength; i++){
			retval += this.get(i);
		}
		return retval;
	}
}

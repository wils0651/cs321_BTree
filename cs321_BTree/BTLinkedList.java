import java.util.LinkedList;

public class BTLinkedList extends LinkedList<Character>{
private int sequenceLength;
	
	
	public BTLinkedList(int sequenceLength){
		super();
		this.sequenceLength = sequenceLength;
	}
	
	public String getSubsequence(){
		
		String retval = "";
		int size = 0;
		if(this.size() < sequenceLength){
			size = this.size();
		}
		else{
			size = sequenceLength;
		}
		for(int i = 0; i < size; i++){
		//String retval = "";
			retval += this.get(i);
		}
		return retval;
	}
}

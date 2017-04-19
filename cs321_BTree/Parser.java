import java.util.Queue;
import java.util.Scanner;
import java.io.File;

public class Parser {
	private File input;
	private int sequenceLength;
	private Scanner scan;
	private boolean firstTime;
	private Queue<Character> qla;

	public Parser(File input, int sequenceLength){
		this.input = input;
		this.sequenceLength = sequenceLength;
		scan = new Scanner(input);
		firstTime = true;
		qla = new Queue<Character>();
	}
	
	public String nextSubSequence(){
		if (firstTime){
			skipHeader();
			firstTime = false;
		}
		char g;
		String ss;
		
		while(g gbf.getNext(gene)!= NULL){
			qla.enqueue(g);
			ss = qla.getSubsequence(sequenceLength);
			
			if(ss.contains('N')){
				for (int i = 0; i<k; i++){
					qla.dequeue();
				}
			}
			else if(ss.length == k){
				qla.dequeue();
				return ss;
			}
			return ""; //not sure what to return if the next substring has an 'N'
		}
	}
	

	public void skipHeader(){
		while(scan.hasNextByte()){
			if(scan.nextByte() == 'O')
				if(scan.nextByte() == 'R')
					if(scan.nextByte() == 'I')
						if(scan.nextByte() == 'G')
							if(scan.nextByte() == 'I')
								if(scan.nextByte() == 'N'){
									return;
								}
		}
	}
}
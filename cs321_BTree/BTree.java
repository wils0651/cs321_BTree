import java.io.File;

public class BTree {
	BTreeNode myRoot;
	long fileOffset;
	File btreeFile;
	private int t;

	public BTree(){

	}

	public void insert(long sskey){			//doesn't need to return anything but we can if we want!
		if(myRoot == null){
			myRoot = new BTreeNode();
			fileOffset = btreeFile.length();	//maybe replace this code with createNode()
			myRoot.setFileOffset(fileOffset);
			myRoot.writeNode();
		}

		myRoot.insert(sskey);

	}

	public boolean find(long sskey){		//true if the tree contains the element, can also return frequency instead
		if(myRoot.contains(sskey)){
			return true;
		}

		return false;
	}


	public BTreeNode createNode(long sskey){
		BTreeNode aNode = new BTreeNode();
		fileOffset = btreeFile.length();
		aNode.setFileOffset(fileOffset);
		aNode.writeNode();
		return aNode;
	}

	public class BTreeNode{
		private BTreeNode myparent;
		private long[] keys;
		private BTreeNode[] children;
		private int rear;
		private int childRear;
		private long fileOffset;

		public BTreeNode(){				//constructor
			keys = new long[2*t-1];
			children = new BTreeNode[2*t];
			rear = 0;
			childRear = 0;
		}


		public void insert(long sskey){
			if(numChildren() == 2*t-1){
				//split
			}
			if(numChildren() == 0){
				keys[rear] = sskey;
				rear++;					//leaf
			}

			if(myparent != null){		//insertion sort #efficiency
				//split by adding 1 new Node
				//push median to parent
				BTreeNode splitNode = createNode(sskey);
				splitNode.insert(this.keys[rear/2]);    //shitty guess
				splitNode.remove(this.keys[rear/2]);
			}
		}
		public int numChildren(){
			return childRear;
		}

		public long remove(long key){
			for(long i = this.keys[0]; i < this.keys[rear]; i++){
				if(keys[(int) i] == key){
					if(i == rear){
						rear--;
						return key;
					}
					else{
						keys[(int)i] = keys[(int)i+1];
					}
				}
			}
			return key;
		}

		public boolean contains(long key){
			for(long i = this.keys[0]; i < this.keys[rear]; i+=1){
				if(keys[(int) i] == key){
					return true;
				}
			}
			return false;
		}
		
		public void addChild(long key){
			children[childRear] = createNode(key);
			childRear++;
		}
		
		public long[] insertionSort(long[] array, int arrayRear){
			for (int i = 0; i < arrayRear; i++){
				int j = i;
				while(j > 0 && array[j-1] > array[j]){
					long temp = array[j];
					array[j] = array[j-1];
					array[j-1] = temp;
					j = j - 1;
				}
			}
			
			return array;
		}

		public void setFileOffset(long fileOffset) {
			this.fileOffset = fileOffset;

		}

		public void writeNode() {
			// TODO Auto-generated method stub

		}
		public boolean isFull(){
			return  rear == 2*t-1;
		}
		
		public boolean childrenFull(){
			return childRear == 2*t;
		}
	}

}	

import sun.misc.Queue;

import java.io.File;


public class BTree {
	BTreeNode myRoot;
	long fileOffset;
	File btreeFile;
	private int t;

	public BTree(int t){
		this.t = t;
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

	public void traverseTree(BTreeNode root) throws InterruptedException {
		Queue q = new Queue<BTreeNode>();
		q.enqueue(root);
		traverseTreeRecursive(q);
	}

	public BTreeNode getRoot(){
		return myRoot;
	}

	public void traverseTreeRecursive(Queue<BTreeNode> q) throws InterruptedException {
		if (q.isEmpty()){
			return;
		}
		BTreeNode node = q.dequeue();

		System.out.println("[" + node.toString() + "]");

		for (int i = 0; i < node.numChildren(); i++){
			q.enqueue(node.getChildren()[i]);
		}

		traverseTreeRecursive(q);
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
			if(numChildren() == 2*t-1){					//logic for this taken from:    https://webdocs.cs.ualberta.ca/~holte/T26/ins-b-tree.html
				keys[rear] = sskey;                     //adds key to array
				insertionSort(keys, rear+1);       //sorts using insertion sort
				int middleIndex = 1+(t-1)/2;           //middle index to be moved up
				long removeKey = keys[middleIndex];
				//overflow here

				//Left:the first (M-1)/2 values
				BTreeNode rightNode = createNode(middleIndex+1);   //moves half the elements to a new node
				for(int i = middleIndex+2; i < rear; i++){
					rightNode.insert(this.remove(i));
				}
				rightNode.setParent(myparent);

				myparent.insert(removeKey);       //removes middle index from current node and adds it to parent
												  //I think the parent's array will get sorted with it gets inserted...
				//Middle: the middle value (position 1+((M-1)/2)
				//Right: the last (M-1)/2 values

			}
			else if(numChildren() == 0){
				keys[rear] = sskey;
				insertionSort(keys, rear);
				rear++;					//leaf
			}

			else if(myparent != null){		//I don't really understand this condition
				//BTreeNode splitNode = createNode(sskey);
				//long middleIndex = 1+(t-1)/2;
				//splitNode.insert(middleIndex);    //shitty guess remove the halfway element and make it the parent
				//this.remove(middleIndex);
				//this.setParent(splitNode);

			}
		}
		public int numChildren(){
			return childRear;
		}

		//public BTreeNode splitNode(long sskey){           //kinda inserts and splits
		//	BTreeNode splitNode = createNode(sskey);
		//	long middleIndex = 1+(t-1)/2;
		//	splitNode.insert(middleIndex);    //shitty guess remove the halfway element and make it the parent
		//	this.remove(middleIndex);
		//	this.setParent(splitNode);

//			return splitNode;
//		}

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
			children[childRear].setParent(this);
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

		public long[] getKeys(){
			return keys;
		}

		public int getRear(){
			return rear;
		}

		public void setRear(int rear){
			this.rear = rear;
		}

		public void setFileOffset(long fileOffset) {
			this.fileOffset = fileOffset;

		}

		public void setParent(BTreeNode parent){
			this.myparent = parent;
		}

		public BTreeNode[] getChildren(){
			return children;
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

		public String toString(){
			String ss = "[";
			for(int i = 0; i < rear-1; i++){
				ss += keys[i] + ", ";
			}
			ss += keys[rear];
			ss += "]";
			return ss;
		}
	}



}	

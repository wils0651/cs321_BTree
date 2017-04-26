import sun.misc.Queue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


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
			//fileOffset = btreeFile.length();	//maybe replace this code with createNode()
			//myRoot.setFileOffset(fileOffset);
			//myRoot.writeNode();
		}

		myRoot.insert(sskey);

	}

	public BTreeNode getRoot(){
		return myRoot;
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

	public void traverseTreeRecursive(Queue<BTreeNode> q) throws InterruptedException {
		if (q.isEmpty()){
			return;
		}
		BTreeNode node = q.dequeue();

		System.out.println(node.toString());

		for (int i = 0; i < node.numChildren(); i++){
			q.enqueue(node.getChildren()[i]);
		}

		traverseTreeRecursive(q);
	}

	public BTreeNode createNode(long sskey){
		BTreeNode aNode = new BTreeNode();
		aNode.insert(sskey);
		//	fileOffset = btreeFile.length();
		//	aNode.setFileOffset(fileOffset);
		//	aNode.writeNode();
		return aNode;
	}

	public class BTreeNode{
		private BTreeNode myparent;
		private long[] keys;
		private BTreeNode[] children;
		private int rear;
		private int childRear;
		private long fileOffset;
		private boolean splitInsert;

		public BTreeNode(){				//constructor
			keys = new long[2*t-1];
			children = new BTreeNode[2*t];
			rear = 0;
			childRear = 0;
			myparent = null;
			splitInsert = false;
		}


		public void insert(long sskey){
			if(rear == 2*t-1){					//logic for this taken from:    https://webdocs.cs.ualberta.ca/~holte/T26/ins-b-tree.html
				int middleIndex = 1+(t-1)/2;			//middle index to be moved up
				long middleValue = keys[middleIndex];
				if(myRoot != this){
					long removeKey = remove(middleIndex);
					myparent.setSplitInsert(true);
					myparent.insert(removeKey);
				}
				else{
					long removeKey = keys[middleIndex];	
					remove(middleIndex);
					BTreeNode splitNode = createNode(removeKey);
					myRoot = splitNode;
					this.setParent(splitNode);
					myparent.addChild(this);
				}


				long removeValue = remove(middleIndex);
				BTreeNode rightNode = createNode(removeValue);   //moves half the elements to a new node
				//				for(int i = middleIndex; i < rear; i++){
				//					rightNode.insert(remove(i));
				//				}
				while(keys[middleIndex] != 0){
					rightNode.insert(remove(middleIndex));
				}

				rightNode.setParent(myparent);
				myparent.addChild(rightNode);
				
				BTreeNode addNode = null;
				while((addNode = removeChild(middleValue)) != null){
					rightNode.addChild(addNode);
					addNode.setParent(rightNode);
				}
				
				
				if(sskey <= middleValue){
					insert(sskey);
				}
				else{
					rightNode.insert(sskey);
				}
				rightNode.childrenSort();
				childrenSort();
				myparent.childrenSort();
				myRoot.childrenSort();

			}
			else if(numChildren() == 0 || splitInsert){
				keys[rear] = sskey;
				rear++;					//leaf
				if (rear > 1){
					insertionSort(keys, rear);
				}
				splitInsert = false;

			}
			else{
				if(keys[0] > sskey){
					children[0].insert(sskey);
				}
				else if(keys[rear-1] < sskey){
					children[childRear-1].insert(sskey);
				}
				else{
					for(int i = 0; i < rear-1; i++){
						if(keys[i] <= sskey && keys[i+1] > sskey){
							children[i+1].insert(sskey);
							return;
						}
					}
				}
			}
		}

		public void insertAtIndex(BTreeNode node, int index){
			BTreeNode next = children[index+1];
			for (int i = index; i < childRear; i++){
				children[i] = next;
				next = children[i+1];
			}
			children[index] = node;
		}

		public int numChildren(){
			return childRear;
		}

		public long remove(int index){
			long retval = 0;
			if(rear - 1 == index){
				retval = keys[index];
			}

			for (int i = index; i < rear-1; i++){
				retval = keys[index];
				keys[index] = keys[index+1];
			}
			rear--;
			keys[rear] = 0;
			return retval;
		}

		public boolean contains(long key){
			for(long i = this.keys[0]; i < this.keys[rear]; i+=1){
				if(keys[(int) i] == key){
					return true;
				}
			}
			return false;
		}

		public void addChild(BTreeNode child){
			children[childRear] = child;
			children[childRear].setParent(this);
			childRear++;
		}
		
		public BTreeNode removeChild(long key){
			BTreeNode retval = null;
			boolean canSwitch = false;
			for(int i = 0; i < childRear; i++){
				if(children[i].getKeys()[0] > key && !canSwitch){
					retval = children[i];
					canSwitch = true;
					System.out.println("addNodes");
				}
				if(canSwitch && i < 2*t-1){
					children[i] = children[i+1];
				}
			}
			if(retval != null){
				childRear--;
			}
			return retval;
		}

		public void childrenSort(){
			for (int i = 0; i < childRear; i++){
				int j = i;
				while(j > 0 && children[j-1].getKeys()[0] > children[j].getKeys()[0]){
					BTreeNode temp = children[j];
					children[j] = children[j-1];
					children[j-1] = temp;
					j = j - 1;
				} 
			}
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
		public void writeNode() throws IOException {
			// TODO design file format
			//String theFilename = "theTestFile.txt";
			//File outputFile = new File(theFilename);
			String mode = "rw";			//read write
			RandomAccessFile fileWriter = new RandomAccessFile(btreeFile, mode);
			fileWriter.seek(fileOffset);
			for(int i = 0; i < 2*t-1; i += 1) {
				fileWriter.writeLong(keys[i]);		//Writes a long to the file as eight bytes, high byte first.
			}
			for(int i = 0; i < 2*t; i += 1) {
				//fileWriter.writeLong(children[i].getOffset);		//Writes a long to the file as eight bytes, high byte first.
			}
			fileWriter.close();

		}
		public boolean isFull(){
			return  rear == 2*t-1;
		}

		public boolean childrenFull(){
			return childRear == 2*t;
		}
		
		public void setSplitInsert(boolean splitInsert){
			this.splitInsert = splitInsert;
		}


		public String toString(){
			String ss = "[";
			for(int i = 0; i < rear-1; i++){
				ss += keys[i] + ", ";
			}
			ss += keys[rear-1];
			ss += "]";
			return ss;
		}
	}



}	

import sun.misc.Queue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class BTree {
	BTreeNode myRoot;
	long fileOffset;
	private File btreeFile;
	private int t;	//degree
	private int numNodes;

	public BTree(int t, String filename){
		this.t = t;
		btreeFile = new File(filename);
		numNodes = 1;
	}

	public void insert(long sskey){			//doesn't need to return anything but we can if we want!
		if(myRoot == null){
			myRoot = new BTreeNode();
		}

		myRoot.insert(new BTreeObject(sskey));

		//fileOffset = btreeFile.length();	//maybe replace this code with createNode()
		fileOffset = 8*(2*t-1) + 4*(2*t-1) + 8*(2*t);
		//myRoot.setFileOffset(fileOffset);
		try {
			myRoot.writeNode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getDegree(){
		return t;
	}

	public BTreeNode getRoot(){
		return myRoot;
	}

	public boolean find(long sskey){		//true if the tree contains the element, can also return frequency instead
		if(myRoot.contains(sskey) != null){
			return true;
		}
		return false;
	}

	public int search(BTreeNode root, long key){
		BTreeObject found = root.contains(key);
		if (found != null){
			return found.frequency;
		}
		else if(root.numChildren() == 0){
			return 0;				
		}
		else {
			for (int i = 0; i < root.getRear(); i++){
				if(key < root.getKeys()[i].key){
					return search(root.getChildren()[i], key);
				}
				else{
					return search(root.getChildren()[root.childRear-1], key);
				}
			}
		}
		return 0;
	}

	public int numNodes(){
		return numNodes;
	}

	public void traverseTree() throws InterruptedException {
		Queue q = new Queue<BTreeNode>();
		q.enqueue(myRoot);
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

	public BTreeNode createNode(BTreeObject sskey){
		BTreeNode aNode = new BTreeNode();
		aNode.insert(sskey);
		//	fileOffset = btreeFile.length();
		fileOffset = 8*(2*t-1) + 4*(2*t-1) + 8*(2*t);
		aNode.setFileOffset(fileOffset);
		try {
			aNode.writeNode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aNode;
	}

	public class BTreeNode{
		private BTreeNode myparent;
		private BTreeObject[] keys;
		private BTreeNode[] children;
		private int rear;
		private int childRear;
		private long fileOffset;
		private boolean splitInsert;


		public BTreeNode(){				//constructor
			keys = new BTreeObject[2*t-1];
			children = new BTreeNode[2*t];
			rear = 0;
			childRear = 0;
			myparent = null;
			splitInsert = false;
		}


		public void insert(BTreeObject sskey){
			BTreeObject duplicate = contains(sskey.key);

			if(duplicate != null){
				duplicate.incrementFreq();
				return;
			}

			if(rear == 2*t-1){					//logic for this taken from:    https://webdocs.cs.ualberta.ca/~holte/T26/ins-b-tree.html
				int middleIndex = 1+(t-1)/2;			//middle index to be moved up
				long middleValue = keys[middleIndex].key;
				if(myRoot != this){
					BTreeObject removeKey = remove(middleIndex);
					myparent.setSplitInsert(true);
					myparent.insert(removeKey);

					numNodes++;
				}
				else{
					BTreeObject removeKey = keys[middleIndex];	
					remove(middleIndex);
					BTreeNode splitNode = createNode(removeKey);
					myRoot = splitNode;
					this.setParent(splitNode);
					myparent.addChild(this);
					numNodes += 2;
				}


				BTreeObject removeObject = remove(middleIndex);
				BTreeNode rightNode = createNode(removeObject);   //moves half the elements to a new node


				while(keys[middleIndex] != null){
					removeObject = remove(middleIndex);
					rightNode.insert(removeObject);
				}

				rightNode.setParent(myparent);
				myparent.addChild(rightNode);

				BTreeNode addNode = null;
				while((addNode = removeChild(middleValue)) != null){
					rightNode.addChild(addNode);
					addNode.setParent(rightNode);
				}
				if(sskey.key < middleValue){
					insert(sskey);
				} else {
					rightNode.insert(sskey);
				}
				rightNode.childrenSort();
				childrenSort();
				myparent.childrenSort();
				myRoot.childrenSort();

			} else if (numChildren() == 0 || splitInsert){
				keys[rear] = sskey;
				rear++;
				//leaf
				if (rear > 1){
					insertionSort(keys, rear);
				}

				splitInsert = false;
			}

			else{

				if(keys[0].key > sskey.key){
					children[0].insert(sskey);
				} else if(keys[rear-1].key < sskey.key){
					children[childRear-1].insert(sskey);
				} else{
					for(int i = 0; i < rear-1; i++){
						if(keys[i].key < sskey.key && keys[i+1].key > sskey.key){
							children[i+1].insert(sskey);
							return;
						}
					}
				}
			}

		}


		public int numChildren(){
			return childRear;
		}

		public BTreeObject remove(int index){
			BTreeObject retval = null;
			if(rear - 1 == index){
				retval = keys[index];
			}

			for (int i = index; i < rear-1; i++){
				retval = keys[index];
				keys[i] = keys[i+1];
			}
			rear--;
			keys[rear] = null;
			return retval;
		}

		public BTreeObject contains(long key){
			for(int i = 0; i < rear; i++){
				if(keys[i].key == key){
					return keys[i];
				}
			}
			return null;
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
				if(children[i].getKeys()[0].key > key && !canSwitch){
					retval = children[i];
					canSwitch = true;
				}
				if(canSwitch && i < 2*t-1){
					children[i] = children[i+1];
				}
			}
			if(retval != null){
				childRear--;
				children[childRear] = null;
			}
			return retval;
		}

		public void childrenSort(){
			for (int i = 0; i < childRear; i++){
				int j = i;
				while(j > 0 && children[j-1].getKeys()[0].key > children[j].getKeys()[0].key){
					BTreeNode temp = children[j];
					children[j] = children[j-1];
					children[j-1] = temp;
					j = j - 1;
				} 
			}
		}

		public BTreeObject[] insertionSort(BTreeObject[] array, int arrayRear){
			for (int i = 0; i < arrayRear; i++){
				int j = i;
				while(j > 0 && array[j-1].key > array[j].key){
					BTreeObject temp = array[j];
					array[j] = array[j-1];
					array[j-1] = temp;
					j = j - 1;
				}
			}

			return array;
		}

		public BTreeObject[] getKeys(){
			return keys;
		}

		public int getRear(){
			return rear;
		}

		public void setRear(int rear){
			this.rear = rear;
		}

		public long getFileOffset() {
			return fileOffset;
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

		//		public BTreeNode readFile() {
		//			String mode = "rw";			//rw is read write
		//			try{ 
		//				RandomAccessFile fileReader = new RandomAccessFile(btreeFile, mode);
		//				fileReader.seek(fileOffset);
		//				fileReader.writeInt(rear);	// the number of keys in the long
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//
		//			}
		//		}

		/**
		 * writes to node
		 * @throws IOException
		 */
		public void writeNode() throws IOException {
			// TODO design file format
			//String theFilename = "theTestFile.txt";
			//File outputFile = new File(theFilename);
			String mode = "rw";			//rw is read write
			try{ 
				RandomAccessFile fileWriter = new RandomAccessFile(btreeFile, mode);
				fileWriter.seek(0);
				System.out.println("rear: "+rear);
				fileWriter.writeInt(127);	// the number of keys in the long

				fileWriter.seek(fileOffset);

				for(int i = 0; i < (2*t-1); i += 1) {
					if (i < rear) {
						fileWriter.writeLong(keys[i].key);		//Writes a long to the file as eight bytes, high byte first.
						fileWriter.writeInt(keys[i].frequency);
					} else {
						fileWriter.writeLong(0);
						fileWriter.writeInt(0);
					}
				}
				for(int i = 0; i < childRear; i += 1) {
					if(i<rear) {
						fileWriter.writeLong(children[i].getFileOffset());		//Writes a long to the file as eight bytes, high byte first.
					} else {
						fileWriter.writeLong(0);
					}
				}
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

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
				ss += keys[i].key + ", ";
			}
			ss += keys[rear-1].key;
			ss += "]";
			return ss;
		}
	}
	public class BTreeObject{
		private int frequency;
		private long key;

		public BTreeObject(long key){
			this.key = key;
			frequency = 1;
		}

		public void incrementFreq(){
			frequency++;
		}
	}
}	

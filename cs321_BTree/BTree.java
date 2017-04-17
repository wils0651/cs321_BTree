import java.io.File;

public class BTree {
	BTreeNode myRoot;
	long fileOffset;
	File btreeFile;

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

	}


	public BTreeNode createNode(){
		BTreeNode aNode = new BTreeNode();
		fileOffset = btreeFile.length();
		aNode.setFileOffset(fileOffset);
		aNode.writeNode();
		return aNode;
	}

	public class BTreeNode{
		public BTreeNode(){				//constructor

		}


		void insert(long sskey){
			if(numChildren() == 2t-1){
				//split
			}
			if(numChildren() == 0){
				insert(sskey);			//leaf
			}

			if(myparent != null){		//insertion sort #efficiency
				//split by adding 1 new Node
				//push median to parent
				BTreeNode splitNode = createNode();
			}

		}
		public void setFileOffset(long fileOffset) {
			// TODO Auto-generated method stub

		}

		public void writeNode() {
			// TODO Auto-generated method stub

		}
	}

}	

import sun.misc.Queue;

import java.io.IOException;
import java.util.LinkedList;

public class BTree {
    public static final int SEQUENCE_SIZE_IN_BYTES = 4;
    public static final int DEGREE_SIZE_IN_BYTES = 4;
    public static final int NODE_COUNT_SIZE_IN_BYTES = 4;
    public static final int FILE_OFFSET_IN_BYTES = 8;

    public static final int NODE_OBJECT_KEY_IN_BYTES = 8;
    public static final int NODE_OBJECT_FREQUENCY_IN_BYTES = 4;
    public static final int NODE_OBJECT_IN_BYTES = NODE_OBJECT_KEY_IN_BYTES + NODE_OBJECT_FREQUENCY_IN_BYTES;

    public static final long fileOffsetInitial = SEQUENCE_SIZE_IN_BYTES + DEGREE_SIZE_IN_BYTES + NODE_COUNT_SIZE_IN_BYTES + FILE_OFFSET_IN_BYTES;

    BTreeNode myRoot;
    long fileOffsetInterval;
    private int degree;
    private int nodeCount;
    private int sequenceLength;
    private BTreeFileAccess bTreeFileAccess;
    protected int maxKeysPerNode;
    protected int maxChildrenPerNode;

    private boolean usingCache;
    private Cache theCache = null;

    // used for debugging
    private static final boolean WRITE_TO_DISK = true;

    public BTree(int cache, int cacheSize, int degree, int sequenceLength, String filename) throws IOException {
        this.degree = degree;
        this.sequenceLength = sequenceLength;
        bTreeFileAccess = new BTreeFileAccess(degree, filename);
        nodeCount = 1;
        maxKeysPerNode = (2 * degree) - 1;
        maxChildrenPerNode = 2 * degree;
        fileOffsetInterval = SEQUENCE_SIZE_IN_BYTES + NODE_OBJECT_IN_BYTES * maxKeysPerNode + FILE_OFFSET_IN_BYTES * maxChildrenPerNode;

        // when we create a btree, we need to insert the first node
        myRoot = new BTreeNode();
        writeMetaData();
        myRoot.writeNode();

        this.usingCache = (cache == 1);
        if (usingCache) {
            theCache = new Cache(cacheSize);
        }
    }

    public int getMaxKeysPerNode() {
        return maxKeysPerNode;
    }

    public int getMaxChildrenPerNode() {
        return maxChildrenPerNode;
    }

    public int getCacheSize() {
        return theCache.getSize();
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    // TODO: we need to write to disk here
    public void insert(long sskey) throws IOException {
        myRoot.insert(new BTreeObject(sskey));
    }

    public int getDegree() {
        return degree;
    }

    public BTreeNode getRoot() {
        return myRoot;
    }

    //true if the tree contains the element, can also return frequency instead
    public boolean find(long sskey) {
        if (myRoot.contains(sskey) != null) {
            return true;
        }
        return false;
    }

    public int search(BTreeNode root, long key) {
        BTreeObject found = root.contains(key);
        if (found != null) {
            return found.getFrequency();
        } else if (root.numChildren() == 0) {
            return 0;
        } else {
            for (int i = 0; i < root.keySize(); i++) {
                if (key < root.getKeys()[i].getKey()) {
                    return search(root.getChildren()[i], key);
                } else {
                    return search(root.getChildren()[root.childCount - 1], key);
                }
            }
        }
        return 0;
    }

    public int numNodes() {
        return nodeCount;
    }

    public void traverseTree() throws InterruptedException {
        Queue q = new Queue<BTreeNode>();
        q.enqueue(myRoot);
        traverseTreeRecursive(q);
    }

    public void traverseTreeRecursive(Queue<BTreeNode> q) throws InterruptedException {
        if (q.isEmpty()) {
            return;
        }
        BTreeNode node = q.dequeue();

        System.out.println(node.toString());

        for (int i = 0; i < node.numChildren(); i++) {
            System.out.println(node.getChildren()[i].getFileOffset());
            q.enqueue(node.getChildren()[i]);
        }

        traverseTreeRecursive(q);
    }

    public BTree.BTreeNode createNode() throws IOException {
        return createNode(null);
    }

    public BTree.BTreeNode createNode(BTreeObject bTreeObject) throws IOException {
        BTreeNode node = this.new BTreeNode();

        if (bTreeObject != null) {
            node.insert(bTreeObject);
        }
        //	fileOffset = btreeFile.length();
        //fileOffset = 8*(2*degree-1) + 4*(2*degree-1) + 8*(2*degree);
        //aNode.setFileOffset(fileOffsetInterval*nodeCount+fileOffsetInitial);
        node.writeNode();

        return node;
    }

    public LinkedList<Long> inorderTraverseTree() throws InterruptedException {
        LinkedList<Long> list = new LinkedList<>();
        return inorderTraverseTreeRecursive(list, myRoot);
    }

    public LinkedList<Long> inorderTraverseTreeRecursive(LinkedList<Long> list, BTreeNode node) throws InterruptedException {
        if (node.numChildren() == 0) {
            for (int i = 0; i < node.keySize(); i++) {
                list.add(node.getKeys()[i].getKey());
                list.add((long) node.getKeys()[i].getFrequency());
            }
            return list;
        }

        for (int i = 0; i <= node.keySize(); i++) {
            if (i > 0) {
                list.add(node.getKeys()[i - 1].getKey());
                list.add((long) node.getKeys()[i - 1].getFrequency());
            }
            inorderTraverseTreeRecursive(list, node.getChildren()[i]);
        }
        return list;
    }

    private void writeMetaData() throws IOException {
        BTreeMetaData bTreeMetaData = new BTreeMetaData(myRoot.fileOffset, sequenceLength, degree, nodeCount);
        bTreeFileAccess.writeMetaData(bTreeMetaData);
    }

    public void writeCache() throws IOException {
        writeMetaData();
        BTreeNode node = theCache.removeFirst();
        while (node != null) {
            bTreeFileAccess.writeNode(this, node);
            node = theCache.removeFirst();
        }
    }

    /// =======================================================================

    public class BTreeNode {
        private BTreeNode myparent;

        private BTreeObject[] keys;
        private BTreeNode[] children;

        private int keySize;
        private int childCount;
        private long fileOffset;
        private boolean splitInsert;

        public BTreeNode(int keySize, int childCount, long fileOffset, BTreeObject[] keys, BTreeNode[] children) {
            this.keySize = keySize;
            this.childCount = childCount;
            this.fileOffset = fileOffset;
            this.keys = keys;
            this.children = children;
            myparent = null;
            splitInsert = false;
        }

        public BTreeNode() {                //constructor
            keys = new BTreeObject[2 * degree - 1];
            children = new BTreeNode[2 * degree];
            keySize = 0;
            childCount = 0;
            myparent = null;
            splitInsert = false;
            fileOffset = fileOffsetInitial + (fileOffsetInterval * nodeCount);
        }

        private void splitNode(BTreeObject bTreeObject) throws IOException {
            int middleIndex = degree - 1;            //middle index to be moved up

            // remove middle element and push it to parent and create a parent if need be (root node)
            BTreeObject middleValue = keys[middleIndex];
            if (myRoot == this) {
                BTreeObject removeKey = remove(middleIndex);
                nodeCount++;
                BTreeNode splitNode = createNode(removeKey);
                myRoot = splitNode;
                setParent(splitNode);
                myRoot.addChild(this);
            } else {
                BTreeObject removeKey = remove(middleIndex);
                myparent.setSplitInsert(true);
                myparent.insert(removeKey);
            }

            // split the current node and insert right half into the rightNode that was created above
            // the few lines below will take the first element of the right half and go create that node
            BTreeObject removeObject = remove(middleIndex);
            nodeCount++;
            BTreeNode rightNode = createNode(removeObject);

            // go get the rest of the right half and move them to the right node
            while (keys[middleIndex] != null) {
                removeObject = remove(middleIndex);
                rightNode.insert(removeObject);
            }

            rightNode.setParent(myparent);
            myparent.addChild(rightNode);

            // split up children evenly
            // one at a time:
            // remove right-most children from left parent
            // then add them to right parent
            BTreeNode addNode = null;
            while ((addNode = removeChild(degree)) != null) {
                rightNode.addChild(addNode);
                addNode.setParent(rightNode);
            }

            sortEverything(rightNode);

            // attention span made it to here. Maybe check this
            if (bTreeObject.key < middleValue.key) {
                insert(bTreeObject);
            } else {
                rightNode.insert(bTreeObject);
            }

            sortEverything(rightNode);

            myparent.writeNode();
            rightNode.writeNode();
            writeNode();
        }

        private void sortEverything(BTreeNode rightNode) {
            rightNode.childrenSort();
            childrenSort();
            myparent.childrenSort();
            myRoot.childrenSort();
        }

        public void insert(BTreeObject bTreeObject) throws IOException {
            // duplicate
            BTreeObject duplicate = contains(bTreeObject.key);
            if (duplicate != null) {
                duplicate.incrementFreq();
                writeNode();
                return;
            }

            if (keySize == maxKeysPerNode) {                    //logic for this taken from:    https://webdocs.cs.ualberta.ca/~holte/T26/ins-b-tree.html
                splitNode(bTreeObject);
            } else if (numChildren() == 0 || splitInsert) {
                keys[keySize] = bTreeObject;
                keySize++;
                //leaf
                if (keySize > 1) {
                    insertionSort(keys, keySize);
                }

                splitInsert = false;

                if(myparent != null){
                    myparent.writeNode();
                }

                writeNode();
            } else {
                if (keys[0].key > bTreeObject.key) {
                    children[0].insert(bTreeObject);
                } else if (keys[keySize - 1].key < bTreeObject.key) {
                    children[childCount - 1].insert(bTreeObject);
                } else {
                    for (int i = 0; i < keySize - 1; i++) {
                        if ((keys[i].key < bTreeObject.key) && (keys[i + 1].key > bTreeObject.key)) {
                            children[i + 1].insert(bTreeObject);
                            return;
                        }
                    }
                }
            }
        }

        public int numChildren() {
            return childCount;
        }

        // remove and shift
        public BTreeObject remove(int index) {
            if (index >= keySize()) {
                return null;
            }

            BTreeObject removedObject = keys[index];
            for (int i = index; i < keySize - 1; i++) {
                keys[i] = keys[i + 1];
            }

            keySize--;
            keys[keySize] = null;
            return removedObject;
        }

        public BTreeObject contains(long key) {
            for (int i = 0; i < keySize; i++) {
                if (keys[i].key == key) {
                    return keys[i];
                }
            }
            return null;
        }

        public void addChild(BTreeNode child) {
            children[childCount] = child;
            childCount++;
        }

        public BTreeNode removeChild(int index) {
            if (index >= childCount) {
                return null;
            }

            BTreeNode removedChild = children[index];
            for (int i = index; i < childCount - 1; i++) {
                children[i] = children[i + 1];
            }

            childCount--;
            children[childCount] = null;
            return removedChild;
        }

        public void childrenSort() {
            for (int i = 0; i < childCount; i++) {
                int j = i;
                while (j > 0 && children[j - 1].getKeys()[0].key > children[j].getKeys()[0].key) {
                    BTreeNode temp = children[j];
                    children[j] = children[j - 1];
                    children[j - 1] = temp;
                    j = j - 1;
                }
            }
        }

        public BTreeObject[] insertionSort(BTreeObject[] array, int arrayRear) {
            for (int i = 0; i < arrayRear; i++) {
                int j = i;
                while (j > 0 && array[j - 1].key > array[j].key) {
                    BTreeObject temp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = temp;
                    j = j - 1;
                }
            }

            return array;
        }

        public BTreeNode getMyparent() {
            return myparent;
        }

        public BTreeObject[] getKeys() {
            return keys;
        }

        public int keySize() {
            return keySize;
        }

        public int getChildCount() {
            return childCount;
        }

        public void setKeySize(int keySize) {
            this.keySize = keySize;
        }

        public long getFileOffset() {
            return fileOffset;
        }

        //		public void setFileOffset(long fileOffset) {
        //			this.fileOffset = fileOffset;
        //
        //		}

        public void setParent(BTreeNode parent) {
            this.myparent = parent;
        }

        public BTreeNode[] getChildren() {
            return children;
        }

        //		public BTreeNode readFile() {
        //			try{
        //				RandomAccessFile fileReader = new RandomAccessFile(btreeFile, mode);
        //				fileReader.seek(fileOffset);
        //				fileReader.writeInt(keySize);	// the number of keys in the long
        //			} catch (IOException e) {
        //				// TODO Auto-generated catch block
        //				e.printStackTrace();
        //
        //			}
        //		}

        /**
         * writes to node
         *
         * @throws IOException
         */
        public void writeToFile(BTreeNode node) throws IOException {
            // TODO design file format
            //root file offset (long), sequence length (int), degree (int), number of nodes (int)
            //String theFilename = "theTestFile.txt";
            //File outputFile = new File(theFilename);
            if (!WRITE_TO_DISK) {
                return;
            }

            writeMetaData();
            bTreeFileAccess.writeNode(BTree.this, node);
        }

        public void writeNode() throws IOException {
            // TODO design file format
            //root file offset (long), sequence length (int), degree (int), number of nodes (int)
            //String theFilename = "theTestFile.txt";
            //File outputFile = new File(theFilename);
            BTreeNode writeNode = null;
            if (usingCache) {
                writeNode = theCache.addObject(this);
            } else {
                writeNode = this;
            }

            if (!usingCache) {
                writeToFile(writeNode);
            }
        }

        public boolean isFull() {
            return keySize == maxKeysPerNode;
        }

        public boolean childrenFull() {
            return childCount == maxChildrenPerNode;
        }

        public void setSplitInsert(boolean splitInsert) {
            this.splitInsert = splitInsert;
        }

        public String toString() {
            String ss = "[";
            for (int i = 0; i < keySize - 1; i++) {
                ss += keys[i].key + ", ";
            }
            ss += keys[keySize - 1].key;
            ss += "]";
            return ss;
        }
    }

}
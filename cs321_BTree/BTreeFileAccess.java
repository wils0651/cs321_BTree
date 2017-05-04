import java.io.IOException;
import java.io.RandomAccessFile;

public class BTreeFileAccess {
    private static final String WRITE_MODE = "rwd";

    private final int degree;
    private String fileName;
    private BTreeMetaData lastWrittenMetaData;

    public BTreeFileAccess(int degree, String fileName) {
        this.degree = degree;
        this.fileName = fileName;
    }

    public void writeMetaData(BTreeMetaData metaData) throws IOException {
//        if ((lastWrittenMetaData != null) && lastWrittenMetaData.equals(metaData)) {
//            return;
//        }

        lastWrittenMetaData = metaData;
        try (RandomAccessFile fileWriter = new RandomAccessFile(fileName, WRITE_MODE)) {
            fileWriter.seek(0);
            fileWriter.writeLong(metaData.rootFileOffset);
            fileWriter.writeInt(metaData.sequenceLength);
            fileWriter.writeInt(metaData.degree);
            fileWriter.writeInt(metaData.nodeCount);
        }
    }

    public void writeNode(BTree bTree, BTree.BTreeNode node) throws IOException {
        try (RandomAccessFile fileWriter = new RandomAccessFile(fileName, WRITE_MODE)) {
            fileWriter.seek(node.getFileOffset());
            fileWriter.writeInt(node.keySize());    //number of keys in node
            for (int i = 0; i < bTree.getMaxKeysPerNode(); i += 1) {
                if (i < node.keySize()) {
                    fileWriter.writeLong(node.getKeys()[i].getKey());        //Writes a long to the file as eight bytes, high byte first.
                    fileWriter.writeInt(node.getKeys()[i].getFrequency());
                } else {
                    fileWriter.writeLong(0);
                    fileWriter.writeInt(0);
                }
            }
            for (int i = 0; i < bTree.getMaxChildrenPerNode(); i += 1) {
                if (i < node.getChildCount()) {
                    fileWriter.writeLong(node.getChildren()[i].getFileOffset());        //Writes a long to the file as eight bytes, high byte first.
                } else {
                    fileWriter.writeLong(0);
                }
            }
        }
    }

    public BTreeMetaData readMetaData() throws IOException {
//        if (lastWrittenMetaData == null) {
//            return null;
//        }

        try (RandomAccessFile fileReader = new RandomAccessFile(fileName, WRITE_MODE)) {
            fileReader.seek(0);
            long rootFileOffset = fileReader.readLong();
            int sequenceLength = fileReader.readInt();
            int degree = fileReader.readInt();
            int nodeCount = fileReader.readInt();
            return new BTreeMetaData(rootFileOffset, sequenceLength, degree, nodeCount);
        }
    }

    // get root node
    public BTree.BTreeNode getRootNode(BTree bTree) throws IOException {
        BTreeMetaData metaData = readMetaData();
        return searchNode(bTree, metaData.rootFileOffset);
    }

    private BTree.BTreeNode searchNode(BTree bTree, long fileOffset) throws IOException {
        try (RandomAccessFile fileReader = new RandomAccessFile(fileName, WRITE_MODE)) {
            fileReader.seek(fileOffset);

            int numberOfKeys = fileReader.readInt(); //number of keys in a node
            int childCount = 0;
            BTreeObject[] bTreeObjects = new BTreeObject[numberOfKeys];
            long[] childOffsets = new long[numberOfKeys + 1];

            for (int i = 0; i < bTree.getMaxKeysPerNode(); i++) {
                if (i < numberOfKeys) {
                    long key = fileReader.readLong();
                    int frequency = fileReader.readInt();
                    bTreeObjects[i] = new BTreeObject(key, frequency);
                } else {
                    // advance random access file - file offset cursor
                    fileReader.readLong();
                    fileReader.readInt();
                }
            }

            for (int i = 0; i < bTree.getMaxChildrenPerNode(); i += 1) {
                if (i <= numberOfKeys) {
                    long childOffset = fileReader.readLong();
                    childOffsets[i] = childOffset;
                    if (childOffset == 0L) {
                        break;
                    }

                    childCount++;
                } else {
                    // advance random access file - file offset cursor
                    fileReader.readLong();
                }
            }

            BTree.BTreeNode[] childrenObjects = new BTree.BTreeNode[numberOfKeys + 1];
            for (int i = 0; i < childCount; i++) {
                long childOffset = childOffsets[i];
                if (childOffset == 0L) {
                    break;
                }

                childrenObjects[i] = searchNode(bTree, childOffset);
            }

            return bTree.new BTreeNode(numberOfKeys, childCount, fileOffset, bTreeObjects, childrenObjects);
        }
    }

}
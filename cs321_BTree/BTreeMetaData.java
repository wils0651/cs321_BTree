public class BTreeMetaData {
    public long rootFileOffset;
    public int sequenceLength;
    public int degree;
    public int nodeCount;

    public BTreeMetaData(long rootFileOffset, int sequenceLength, int degree, int nodeCount) {
        this.rootFileOffset = rootFileOffset;
        this.sequenceLength = sequenceLength;
        this.degree = degree;
        this.nodeCount = nodeCount;
    }
}
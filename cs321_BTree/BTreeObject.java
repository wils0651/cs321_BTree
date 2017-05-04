public class BTreeObject {
    public long key;
    public int frequency;

    public BTreeObject(long key) {
        this.key = key;
        frequency = 1;
    }

    public BTreeObject(long key, int frequency) {
        this.key = key;
        this.frequency = frequency;
    }

    public void incrementFreq() {
        frequency++;
    }

    public long getKey() {
        return key;
    }

    public int getFrequency() {
        return frequency;
    }
}
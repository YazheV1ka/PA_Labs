public class StringHeap {
    private int size = 0;
    private final String[] heapBase;

    Heap heap = new Heap();

    public StringHeap(int capacity) {
        heapBase = new String[capacity];
    }

    public void insert(String item) {
        heapBase[size++] = item;
    }

    public String replace(String item) {
        String smallest = heapBase[0];

        heapBase[0] = item;
        heap.downHeapFrom();

        return smallest;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return heapBase.length;
    }
}

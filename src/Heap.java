public class Heap {
    private int size = 0;
    private int[] heapBaseK;
    private String[] heapBaseV;

    public Heap() {
    }

    public Heap(int capacity) {
        heapBaseK = new int[capacity];
        heapBaseV = new String[capacity];
    }

    public void insert(Pair<Integer, String> item) {
        heapBaseK[size] = item.key;
        heapBaseV[size] = item.value;
        size++;

        for (int position = size - 1; position > 0; ) {
            int parentPosition = (position - 1) / 2;
            if (item.value.compareTo(heapBaseV[parentPosition]) > 0)
                return;

            swap(position, position = parentPosition);
        }
    }

    public void get() {
        if (heapBaseV[0] == null)
            return;

        heapBaseK[0] = heapBaseK[--size];
        heapBaseV[0] = heapBaseV[size];

        downHeapFrom();
    }

    public Pair<Integer, String> replace(Pair<Integer, String> item) {
        int smallestK = heapBaseK[0];
        String smallestV = heapBaseV[0];

        heapBaseK[0] = item.key;
        heapBaseV[0] = item.value;
        downHeapFrom();

        return new Pair<>(smallestK, smallestV);
    }

    public Pair<Integer, String> peek() {
        return new Pair<>(heapBaseK[0], heapBaseV[0]);
    }

    public int size() {
        return size;
    }

    void downHeapFrom() {
        int position = 0;

        for (; ; ) {
            int d = position * 2 + 1;
            if (d >= size)
                return;

            if (d + 1 < size && heapBaseV[d].compareTo(heapBaseV[d + 1]) > 0)
                d++;

            if (heapBaseV[position].compareTo(heapBaseV[d]) > 0) {
                swap(position, d);
                position = d;
            } else
                return;
        }
    }

    private void swap(int idxA, int idxB) {
        int tmpK = heapBaseK[idxA];
        String tmpV = heapBaseV[idxA];

        heapBaseK[idxA] = heapBaseK[idxB];
        heapBaseV[idxA] = heapBaseV[idxB];

        heapBaseK[idxB] = tmpK;
        heapBaseV[idxB] = tmpV;
    }
}

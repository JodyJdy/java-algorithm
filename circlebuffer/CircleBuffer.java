package circlebuffer;

/**
 * 环形缓冲区
 */
public class CircleBuffer<T> {
    private final T[] buffer;
    private int writePos = 0;
    private int readPos = 0;
    private final int SIZE;

    private boolean empty = true;
    private boolean full = false;


    @SuppressWarnings("unchecked")
    public CircleBuffer(int size) {
        SIZE = size;
        buffer = (T[]) new Object[size];
    }

    /**
     * 返回未读取的数据
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        int dataSize = getDataSize();
        T[] a = (T[]) new Object[dataSize];

        if (dataSize > 0) {
            if (readPos < writePos) {
                // 数据在缓冲区中是连续的
                System.arraycopy(buffer, readPos, a, 0, dataSize);
            } else {
                // 数据被环绕存储，需要分两部分复制
                System.arraycopy(buffer, readPos, a, 0, SIZE - readPos);
                System.arraycopy(buffer, 0, a, SIZE - readPos, writePos);
            }
        }
        return a;
    }

    private int getDataSize() {
        if (full) {
            return SIZE;
        }
        if (writePos >= readPos) {
            return writePos - readPos;
        } else {
            return SIZE - readPos + writePos;
        }
    }

    public void write(T i) {
        if ((writePos) % SIZE == readPos && full) {
            throw new RuntimeException("CircleBuffer is full");
        }
        buffer[writePos] = i;
        empty = false;
        if (!((writePos + 1) % SIZE == readPos)) {
            writePos++;
        } else {
            full = true;
        }
        writePos = writePos % SIZE;
    }

    public T read() {
        if (readPos == writePos && empty) {
            throw new RuntimeException("CircleBuffer is empty");
        }
        T i = buffer[readPos];
        if (readPos == writePos) {
            empty = true;
        } else {
            readPos++;
        }
        full = false;
        readPos = readPos % SIZE;
        return i;
    }

    public boolean isEmpty() {
        return readPos == writePos;
    }
}

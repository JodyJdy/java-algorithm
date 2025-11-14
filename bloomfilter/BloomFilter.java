package bloomfilter;

import java.util.BitSet;

public class BloomFilter<E> {

    /**
     * bitset尺寸
     */
    private final int size;
    /**
     * hash 函数数量
     */
    private final int hashCount;
    /**
     * 使用 bitset 记录 元素是否存在
     */
    private final BitSet bitSet;

    public BloomFilter(int size, int hashCount) {
        this.size = size;
        this.hashCount = hashCount;
        this.bitSet = new BitSet(size);
    }

    /**
     使用hashMap的hash函数计算hash值
     */
    static  int hashMapHash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private int hash(int rawHashCode, int seed) {
        return Math.abs(((rawHashCode * seed) ^ rawHashCode) % size) ;
    }

    public void add(E element) {
        int rawHashCode = hashMapHash(element);
        for (int i = 0; i < hashCount; i++) {
            int hash = hash(rawHashCode, i);
            bitSet.set(hash);
        }
    }

    public boolean mightContain(E element) {
        int rawHashCode = hashMapHash(element);
        for (int i = 0; i < hashCount; i++) {
            int hash = hash(rawHashCode, i);
            //只要有一个hash函数没有命中，说明不存在
            if (!bitSet.get(hash)) {
                return false;
            }
        }
        return true;
    }

}

package bloomfilter;


public class Main {
    public static void main(String[] args) {

        BloomFilter<String> bloomFilter = new BloomFilter<>(20, 5);
        bloomFilter.add("hello");
        bloomFilter.add("world");

        System.out.println(bloomFilter.mightContain("hello"));
        System.out.println(bloomFilter.mightContain("world"));
        System.out.println(bloomFilter.mightContain("sldkfjldss"));

    }
}

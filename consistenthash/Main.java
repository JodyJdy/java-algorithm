package consistenthash;

public class Main {
    public static void main(String[] args) {

        //生成20个真实节点，每个节点有500个虚拟节点
        ConsistentHash<String, String> consistentHash = new ConsistentHash<>(20,500);

        System.out.println(consistentHash.getRealNodeNames());

        System.out.println(consistentHash.getVirtualNodeHash(ConsistentHash.randomString()));
        System.out.println(consistentHash.getVirtualNodeHash(ConsistentHash.randomString()));
        System.out.println(consistentHash.getVirtualNodeHash(ConsistentHash.randomString()));
        System.out.println(consistentHash.getVirtualNodeHash(ConsistentHash.randomString()));
        System.out.println(consistentHash.getVirtualNodeHash(ConsistentHash.randomString()));



    }
}

package redblacktree;

/**
 * @author jdy
 * @title: redblacktree
 * @description:
 * @data 2023/9/7 13:06
 */
public class Main {
    public static void main(String[] args) {
        test();
    }
    public static void test(){
        RedBlackTree<Integer ,Person> tree = new RedBlackTree<>();
        for (int i = 1; i <= 10000; i++) {
            tree.insert(new Person(i));
        }
        for (int i = 5001; i <= 10000; i++) {
            tree.delete(i);
        }
        for (int i = 1; i <= 5000; i++) {
            System.out.println(tree.search(i));
        }
        System.out.println(tree.search(9999));

    }
    public static <K extends Comparable<K>,V extends NodeValue<K>> void  show(TreeNode<K,V>node){
        if (node == null) {
            return;
        }
        System.out.println(node.getVal());
        show(node.getLeft());
        show(node.getRight());
    }
    public static int a = 0;
    public static <K extends Comparable<K>,V extends NodeValue<K>> void  checkRight(TreeNode<K,V>node){
        if (node == null) {
            return;
        }
        if (node.getLeft() != null) {
            checkRight(node.getLeft());
        }
        System.out.println(node.getVal());
        checkRight(node.getRight());
    }



    public static class Person  implements NodeValue<Integer>{
        @Override
        public Integer  getKey() {
            return key;
        }
        Integer key;

        public Person(Integer key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "key='" + key + '\'' +
                    '}';
        }
    }
}

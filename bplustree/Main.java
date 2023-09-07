/**
 * @author jdy
 * @title: Main
 * @description:
 * @data 2023/9/6 10:29
 */
public class Main {
    public static void main(String[] args) {


                BPlusTree<Integer, Person> tree = new BPlusTree<>(33);
                for (int i = 1; i < 2000; i++) {
                    tree.insert(new Person(i));
                }
        for (int i = 1; i < 995; i++) {
            tree.delete(i);
        }

        TreeLeafNode<Integer, Person> t = tree.searchNode(996);
        while (t != null) {
            System.out.println(t.getKeys());
            t = t.getRightNode();
        }


    }

    static class Person implements NodeValue<Integer>{
        @Override
        public String toString() {
            return "Person{" +
                    "id=" + id +
                    '}';
        }

        private Integer id;

        public Person(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getKey() {
            return id;
        }

    }

}

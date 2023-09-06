import java.util.Iterator;
import java.util.List;

import static utl.CompareUtil.*;

/**
 * @author jdy
 * @title: BTree
 * @description:
 * @data 2023/9/6 10:33
 */
public class BPlusTree<Key extends Comparable<Key>, Val extends NodeValue<Key>> {
    /**
     * b+ 树 度的数量
     */
    private final int m;
    private TreeNode<Key, Val> rootNode;

    public BPlusTree(int m) {
        this.m = m;
    }


    public Val search(Key k) {
        TreeLeafNode<Key, Val> treeLeafNode = searchNode(k);
        for (Val v : treeLeafNode.getValues()) {
            if (eq(v.getKey(), k)) {
                return v;
            }
        }
        return null;
    }

    /**
     * 搜索key 所在的叶子节点
     */
    public TreeLeafNode<Key, Val> searchNode(Key k) {
        return doSearchNode(k, rootNode);
    }

    /**
     * 从 from 节点开始进行搜索
     */
    private TreeLeafNode<Key, Val> doSearchNode(Key k, TreeNode<Key, Val> from) {
        TreeIndexNode<Key, Val> indexNode;
        while (!from.isLeaf()) {
            indexNode = (TreeIndexNode<Key, Val>) from;
            List<Key> keyList = from.getKeys();
            int i = 0;
            for (; i < keyList.size(); i++) {
                if (le(k, keyList.get(i))) {
                    from = indexNode.getSubNodes().get(i);
                    break;
                }
            }
            //未找到，那就是最后一个
            if (i == keyList.size()) {
                from = indexNode.getLastNode();
            }
        }
        return (TreeLeafNode<Key, Val>) from;
    }

    public void insert(Val val) {
        //插入根节点
        if (rootNode == null) {
            rootNode = new TreeLeafNode<>(NodeType.ROOT_LEAF);
            //进行转型，方便处理
            TreeLeafNode<Key, Val> tempRoot = (TreeLeafNode<Key, Val>) rootNode;
            tempRoot.addKey(val.getKey());
            tempRoot.addVal(val);
            return;
        }
        //根据key找到指定的位置
        Key k = val.getKey();
        TreeLeafNode<Key, Val> insertedNode = searchNode(k);
        doInsertNode(val, insertedNode);
    }

    /**
     * 向叶子节点添加值
     */
    private void doInsertNode(Val val, TreeLeafNode<Key, Val> t) {
        List<Key> keys = t.getKeys();
        List<Val> values = t.getValues();
        int insertIndex = -1;
        for (int i = 0; i < keys.size(); i++) {
            if (le(val.getKey(), keys.get(i))) {
                insertIndex = i;
                break;
            }
        }
        //末尾追加即可
        if (insertIndex == -1) {
            keys.add(val.getKey());
            values.add(val);
        } else {
            keys.add(insertIndex, val.getKey());
            values.add(insertIndex, val);
        }
        // 如果满了，进行分裂
        if (isFull(t)) {
            split(t);
        }
    }

    /**
     * 插入数据后，导致页分裂时
     * 向父节点中插入一个key
     */
    private void insertParentIndexNode(TreeIndexNode<Key, Val> parentNode, TreeNode<Key, Val> left, TreeNode<Key, Val> right, Key k, TreeNode<Key, Val> splitNode) {
        //获取被分裂的节点的下标
        int index = parentNode.getSubNodes().indexOf(splitNode);
        //新插入两个节点，替换原有的 splitNode
        parentNode.getSubNodes().set(index, right);
        parentNode.getSubNodes().add(index, left);

        // key不存在才会进行插入
        //插入key
        int insertIndex = -1;
        for (int i = 0; i < parentNode.getKeys().size(); i++) {
            if (le(k, parentNode.getKeys().get(i))) {
                insertIndex = i;
                break;
            }
        }
        //末尾追加即可
        if (insertIndex == -1) {
            parentNode.addKey(k);
        } else {
            parentNode.getKeys().add(insertIndex, k);
        }
        //判断 parentNode 需不需要 分裂
        if (isFull(parentNode)) {
            split(parentNode);
        }
    }

    /**
     * 插入索引节点
     */
    private void doInsertIndexNode(TreeIndexNode<Key, Val> t, Key k) {

    }


    private void splitLeafNode(TreeLeafNode<Key, Val> leafNode) {
        //创建两个新的叶子节点
        TreeLeafNode<Key, Val> left = new TreeLeafNode<>(NodeType.LEAF);
        TreeLeafNode<Key, Val> right = new TreeLeafNode<>(NodeType.LEAF);
        //维护双向链表关系
        if (leafNode.getLeftNode() != null) {
            leafNode.getLeftNode().setRightNode(left);
        }
        //设置叶子节点的右节点
        left.setRightNode(right);
        right.setRightNode(leafNode.getRightNode());
        //设置叶子节点的左节点
        right.setLeftNode(left);
        left.setLeftNode(leafNode.getLeftNode());
        //拷贝数据，左节点，包含前 m/2个记录
        for (int i = 0; i < m / 2; i++) {
            left.addKey(leafNode.getKeys().get(i));
            left.addVal(leafNode.getValues().get(i));
        }
        for (int i = m / 2; i < m; i++) {
            right.addKey(leafNode.getKeys().get(i));
            right.addVal(leafNode.getValues().get(i));
        }
        //向父节点插入索引节点，父节点一定是 TreeIndexNode类型的
        //t 是root节点，需要重新创建root节点
        if (leafNode.getParentNode() == null) {
            this.rootNode = new TreeIndexNode<>(NodeType.ROOT_INDEX);
            leafNode.setParentNode((TreeIndexNode<Key, Val>) this.rootNode);
            ((TreeIndexNode<Key, Val>) this.rootNode).addNode(leafNode);
        }
        //设置 left，right的父节点
        left.setParentNode(leafNode.getParentNode());
        right.setParentNode(leafNode.getParentNode());
        //父节点中添加key
        insertParentIndexNode(leafNode.getParentNode(), left, right, leafNode.getKeys().get(m / 2), leafNode);
    }

    private void splitIndexNode(TreeIndexNode<Key, Val> indexNode) {
        //创建两个新的叶子节点
        TreeIndexNode<Key, Val> left = new TreeIndexNode<>(NodeType.INDEX);
        TreeIndexNode<Key, Val> right = new TreeIndexNode<>(NodeType.INDEX);
        //设置叶子节点的右节点
        //拷贝数据，左节点，包含前 m/2个记录
        for (int i = 0; i < m / 2; i++) {
            //拷贝key
            left.addKey(indexNode.getKeys().get(i));
        }
        final Iterator<TreeNode<Key, Val>> each = indexNode.getSubNodes().iterator();
        while (each.hasNext()) {
            TreeNode<Key, Val> cur = each.next();
            if (lt(cur.getLastKey(), indexNode.getKeys().get(m / 2))) {
                left.addNode(cur);
                cur.setParentNode(left);
                each.remove();
            }
        }
        //这里与 splitNode不一样， 第m/2 个key 插入到父节点中
        for (int i = m / 2 + 1; i < m; i++) {
            right.addKey(indexNode.getKeys().get(i));
        }
        //剩下的全都加入到right里面
        right.getSubNodes().addAll(indexNode.getSubNodes());
        right.getSubNodes().forEach(s -> s.setParentNode(right));

        //向父节点插入索引节点，父节点一定是 TreeIndexNode类型的
        //t 是root节点，需要重新创建root节点
        if (indexNode.getParentNode() == null) {
            this.rootNode = new TreeIndexNode<>(NodeType.ROOT_INDEX);
            indexNode.setParentNode((TreeIndexNode<Key, Val>) this.rootNode);
            ((TreeIndexNode<Key, Val>) this.rootNode).addNode(indexNode);
        }
        //设置 left，right的父节点
        left.setParentNode(indexNode.getParentNode());
        right.setParentNode(indexNode.getParentNode());
        //父节点中添加key
        insertParentIndexNode(indexNode.getParentNode(), left, right, indexNode.getKeys().get(m / 2), indexNode);
    }

    private void split(TreeNode<Key, Val> t) {
        //叶子节点分裂
        if (t instanceof TreeLeafNode) {
            splitLeafNode((TreeLeafNode<Key, Val>) t);
            return;
        }
        //索引节点分裂
        splitIndexNode((TreeIndexNode<Key, Val>) t);
    }

    public boolean isFull(TreeNode<Key, Val> t) {
        return t.getKeys().size() >= m;
    }


}

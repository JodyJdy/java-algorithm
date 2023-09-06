import java.util.ArrayList;
import java.util.List;

/**
 * @author jdy
 * @title: TreeNode
 * @description:
 * @data 2023/9/6 10:36
 */
public abstract class TreeNode<Key extends Comparable<Key>, Val extends NodeValue<Key>> {
    /**
     * 节点类型
     */
    private final NodeType type;

    /**
     * 父节点，根节点的父节点为空
     */
    private TreeIndexNode<Key, Val> parentNode;

    /**
     *通过 keys 的下标，subNodes的下标来确定对应关系
     */
    private final List<Key> keys = new ArrayList<>();

    public TreeNode(NodeType type) {
        this.type = type;
    }

    public NodeType getType() {
        return type;
    }

    public TreeIndexNode<Key,Val> getParentNode() {
        return parentNode;
    }



    public List<Key> getKeys() {
        return keys;
    }
    public void addKey(Key k) {
        keys.add(k);
    }
    public Key getLastKey(){
        return keys.get(keys.size() - 1);
    }

    public void setParentNode(TreeIndexNode<Key, Val> parentNode) {
        this.parentNode = parentNode;
    }

    /**
     *是叶子节点
     */
    public boolean isLeaf(){
        return type == NodeType.LEAF || type == NodeType.ROOT_LEAF;
    }
}

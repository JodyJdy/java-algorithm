package redblacktree;

/**
 * @author jdy
 * @title: RedBlackTree
 * @description:
 * @data 2023/9/7 16:26
 */
import static redblacktree.utl.CompareUtil.*;
public class RedBlackTree <Key extends Comparable<Key>,Val extends NodeValue<Key>>{
    public TreeNode<Key, Val> getRoot() {
        return root;
    }

    private TreeNode<Key,Val> root;


    public void insert(Val v) {
        TreeNode<Key, Val> node = new TreeNode<>(ColorEnum.RED,v);
        // case 1 : 第一个节点插入
        if (root == null) {
            root = node;
            return;
        }
        // 找到要插入的节点
        TreeNode<Key, Val> insertNode = searchNodeForInsert(v.getKey(), root);

        node.setParent(insertNode);
        if (insertLeft(v, insertNode)) {
            insertNode.setLeft(node);
        } else{
            insertNode.setRight(node);
        }
        // case2  父节点是根节点，染色即可
        if (insertNode.equals(root)) {
            root.setBlack();
            return;
        }
        color(node);
        //根节点染成黑色
    }

    public void color(TreeNode<Key, Val> cur) {
        if (root.equals(cur)) {
            return;
        }
        TreeNode<Key, Val> parent = cur.getParent();
        TreeNode<Key,Val> uncle = parent.getSiblings();
        // case3 当前节点 N 的父节点 P 和叔节点 U 均为红色
        if (parent.isRed() && uncle != null && uncle.isRed()) {
            parent.setBlack();
            uncle.setBlack();
            parent.getParent().setRed();
            color(parent.getParent());
            return;
        }
        //case 4 当前节点 N 与父节点 P 的方向相反
        if(cur.getDirection() != parent.getDirection()){
            if (cur.getDirection() == Direction.LEFT) {
                rotateRight(parent);
            } else{
                rotateLeft(parent);
            }
            cur = parent;
        }
        // 如果经过了case4 cur发生了变化，需要从重新计算 parent
        parent = cur.getParent();
        // case 5  当前节点 N 与父节点 P 的方向相同
        if (cur.getDirection() == parent.getDirection()) {
            TreeNode<Key,Val> grandPa = parent.getParent();
            if (parent.getDirection() == Direction.LEFT) {
                rotateRight(grandPa);
            } else{
                rotateLeft(grandPa);
            }
            cur.getParent().setBlack();
            cur.getSiblings().setRed();
        }
    }

    public boolean insertLeft(Val v, TreeNode<Key, Val> node) {
        return lt(v.getKey(),node.getVal().getKey());
    }


    public TreeNode<Key, Val> searchNodeForInsert(Key k, TreeNode<Key,Val> from) {
        Key compare = from.getVal().getKey();
        if (eq(k, compare)) {
            return from;
        }
        if(lt(k,compare)){
            if (from.getLeft() != null) {
                return searchNodeForInsert(k, from.getLeft());
            }
        } else{
            if (from.getRight() != null) {
                return searchNodeForInsert(k, from.getRight());
            }
        }
        return from;

    }

    public Val search(Key k) {
        TreeNode<Key, Val> result = doSearch(k, root);
        if (result == null) {
            return null;
        }
        return result.getVal();
    }

    public TreeNode<Key,Val> doSearch(Key k, TreeNode<Key, Val> from) {
        if (from == null) {
            return null;
        }
        Key f = from.getVal().getKey();
        if(eq(k,f)){
            return from;
        }
        if(lt(k,f)){
            return doSearch(k,from.getLeft());
        }
        return doSearch(k,from.getRight());
    }
    public TreeNode<Key, Val> searchNode(Key k) {
        return doSearch(k,root);
    }

    public void delete(Key k) {
        TreeNode<Key, Val> deleteNode = doSearch(k,root);
        if (deleteNode == null) {
            return;
        }
        // case0 删除节点为根节点，直接删除
        if(root.equals(deleteNode) && root.getLeft() == null && root.getRight() == null){
            root = null;
        }
        // case1 若待删除节点 N 既有左子节点又有右子节点，则需找到它的前驱或后继节点进行替换（仅替换数据，不改变节点颜色和内部引用关系）1
        if (deleteNode.getLeft() != null && deleteNode.getRight() != null) {
            TreeNode<Key, Val> replace = successor(deleteNode);
            swapNode(deleteNode,replace);
            //进行替换
            deleteNode = replace;
        }
        //case 2 待删除节点为叶子节点，若该节点为红色，直接删除即可，删除后仍能保证红黑树的 4 条性质。若为黑色，删除后性质 4 被打破，需要重新进行维护
        if (deleteNode.getRight() == null && deleteNode.getLeft() == null) {
            //先维护，再删除
            if (deleteNode.isBlack()) {
                balanceAfterRemove(deleteNode);
            }
            //删除
            if (deleteNode.getDirection() == Direction.LEFT) {
                deleteNode.getParent().setLeft(null);
            } else if(deleteNode.getDirection() == Direction.RIGHT){
                deleteNode.getParent().setRight(null);
            } else{
                throw new RuntimeException("异常情况");
            }
            return;
        }
        // case 3 待删除节点有且仅有一个非 NIL 子节点，若待删除节点为红色，直接使用其子节点 S 替换即可；若为黑色，则直接使用子节点 S 替代会打破性质 4，需要在使用 S 替代后判断 S 的颜色，若为红色，则将其染黑后即可满足性质 4，否则需要进行维护才可以满足性质 4
        if ((deleteNode.getLeft() != null && deleteNode.getRight() == null) || (deleteNode.getLeft() == null && deleteNode.getRight() != null)) {
           TreeNode<Key,Val>  son = deleteNode.getLeft() == null ? deleteNode.getRight():deleteNode.getLeft();
           TreeNode<Key,Val> parent = deleteNode.getParent();
            switch (deleteNode.getDirection()) {
                case ROOT:this.root = son;break;
                case LEFT:
                    parent.setLeft(son);break;
                case RIGHT:parent.setRight(son);
            }
            if (!deleteNode.equals(root)) {
                son.setParent(parent);
            }
            if (deleteNode.isBlack()) {
                if (son.isRed()) {
                    son.setBlack();
                } else{
                    balanceAfterRemove(son);
                }
            }
        }
    }

    private void balanceAfterRemove(TreeNode<Key, Val> deleteNode) {
        //case1 兄弟节点 (sibling node) S 为红色
        TreeNode<Key,Val> parent = deleteNode.getParent();
        TreeNode<Key,Val> siblings = deleteNode.getSiblings();
        if (siblings != null && siblings.isRed() && parent != null && parent.isBlack()) {
            if (siblings.getLeft() != null && siblings.getLeft().isBlack() && siblings.getRight() != null && siblings.getRight().isBlack()) {
                if (deleteNode.getDirection() == Direction.LEFT) {
                    rotateLeft(parent);
                } else{
                    rotateRight(parent);
                }
                siblings.setBlack();
                parent.setRed();
                balanceAfterRemove(parent);
                return;
            }
        }
        //case 2  兄弟节点 S 和侄节点 C, D 均为黑色，父节点 P 为红色。此时只需将 S 染红，将 P 染黑
        if (siblings != null && siblings.isBlack() && parent.isRed()) {
            if (siblings.getLeft() != null && siblings.getRight() != null && siblings.getLeft().isBlack() && siblings.getRight().isBlack()) {
                siblings.setRed();
                parent.setBlack();
                return;
            }
        }
        //case 3 兄弟节点 S，父节点 P 以及侄节点 C, D 均为黑色。
        if (siblings != null && siblings.isBlack() && parent.isBlack()) {
            if (siblings.getLeft() != null && siblings.getRight() != null && siblings.getLeft().isBlack() && siblings.getRight().isBlack()) {
                siblings.setRed();
                balanceAfterRemove(parent);
                return;
            }
        }
        // case 4  兄弟节点是黑色，且与 N 同向的侄节点 C 为红色，与 N 反向的侄节点 D 为黑色
        if (siblings != null && siblings.isBlack()) {
            if (siblings.getLeft() != null && siblings.getRight() != null) {
                if ((deleteNode.getDirection() == Direction.LEFT && siblings.getLeft().isRed() && siblings.getRight().isBlack()) ||
                        deleteNode.getDirection() == Direction.RIGHT && siblings.getRight().isRed() && siblings.getLeft().isBlack()
                ) {
                    //同向的侄节点
                    TreeNode<Key,Val> nephew = deleteNode.getDirection() == Direction.LEFT ? siblings.getLeft():siblings.getRight();
                    if (deleteNode.getDirection() == Direction.LEFT) {
                        rotateRight(parent);
                    } else{
                        rotateLeft(parent);
                    }
                    siblings.setRed();
                    nephew.setBlack();
                    //调整了siblings
                    siblings =  deleteNode.getSiblings();
                }
            }
        }
        //旋转后，可能发生变更，影响下面的流程
        parent = deleteNode.getParent();
        // case 5 兄弟节点是黑色，且 close nephew 节点 C 为红色，distant nephew 节点 D 为黑色，父节点既可为红色又可为黑色。
        if (siblings != null && siblings.isBlack()) {
            TreeNode<Key,Val> close;
            TreeNode<Key,Val> distance;
            if (deleteNode.getDirection() == Direction.LEFT) {
                close = siblings.getLeft();
                distance = siblings.getRight();
            } else{
                close = siblings.getRight();
                distance = siblings.getLeft();
            }
            if((close == null || close.isBlack()) && distance  != null && distance.isRed()){
                if (deleteNode.getDirection() == Direction.LEFT) {
                    rotateLeft(parent);
                } else{
                    rotateRight(parent);
                }
                siblings.setColor(deleteNode.getParent().getColor());
                deleteNode.getParent().setBlack();
                distance.setBlack();
            }
        }

    }


    /**
     *左旋
     *        |                       |
     *        N                       S
     *       / \     l-rotate(N)     / \
     *      L   S    ==========>    N   R
     *         / \                 / \
     *        M   R               L   M
     */
    public void rotateLeft(TreeNode<Key, Val> node) {
        if (node == null) {
            throw new RuntimeException("无法旋转");
        }
        if (node.getRight() == null) {
            throw new RuntimeException("无法旋转");
        }

        //父节点
        TreeNode<Key,Val> parent = node.getParent();
        //newNode 用于替换 当前node的位置
        TreeNode<Key,Val> newNode = node.getRight();

        node.setRight(newNode.getLeft());
        if (newNode.getLeft() != null) {
            newNode.getLeft().setParent(node);
        }
        newNode.setLeft(node);
        //设置父节点中的子节点为 newNode
        switch (node.getDirection()) {
            case ROOT:this.root = newNode;break;
            case LEFT:parent.setLeft(newNode);break;
            case RIGHT:parent.setRight(newNode);break;
        }
        //设置parent
        node.setParent(newNode);
        newNode.setParent(parent);
    }

    /**
     * 右旋
     * <p>
     * *         |                       |
     * *        N                       S
     * *       / \     r-rotate(N)     / \
     * *      L   S    <==========    N   R
     * *         / \                 / \
     * *        M   R               L   M
     */
    public void rotateRight(TreeNode<Key, Val> node) {
        if (node == null) {
            throw new RuntimeException("无法旋转");
        }
        if (node.getLeft() == null) {
            throw new RuntimeException("无法旋转");
        }

        //父节点
        TreeNode<Key,Val> parent = node.getParent();
        //newNode 用于替换 当前node的位置
        TreeNode<Key,Val> newNode = node.getLeft();

        node.setLeft(newNode.getRight());
        if (newNode.getRight() != null) {
            newNode.getRight().setParent(node);
        }
        newNode.setRight(node);

        //设置父节点中的子节点为 newNode
        switch (node.getDirection()) {
            case ROOT:this.root = newNode;break;
            case LEFT:parent.setLeft(newNode);break;
            case RIGHT:parent.setRight(newNode);break;
        }
        //设置parent
        node.setParent(newNode);
        newNode.setParent(parent);
    }

    /**
     *交换节点数据
     */
    public void swapNode(TreeNode<Key, Val> a, TreeNode<Key, Val> b) {
        Val v = a.getVal();
        a.setVal(b.getVal());
        b.setVal(v);
    }

    /**
     *找到后继节点
     */
    public TreeNode<Key, Val> successor(TreeNode<Key, Val> node) {
        TreeNode<Key,Val> succ = node.getRight();
        while (succ.getLeft() != null) {
            succ = succ.getLeft();
        }
        return succ;
    }

    /**
     * 找到前驱节点
     */
    public TreeNode<Key, Val> predecessor(TreeNode<Key, Val> node) {
        TreeNode<Key,Val> pred = node.getLeft();
        while (pred.getRight() != null) {
            pred = pred.getRight();
        }
        return pred;

    }
}

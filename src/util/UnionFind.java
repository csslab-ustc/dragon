package util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

//
public class UnionFind<X> {

    public class Node{
        public X data;
        private Node parent;  // root's parent == null;
        // for debugging:
        private final Label label;
        private final LinkedList<Node> children;

        public Node(X data){
            this.data = data;
            this.parent = null;
            this.label = new Label();
            this.children = new LinkedList<>();
        }

        @Override
        public String toString(){
            return (label.toString() + ":"
                    + data.toString());
        }
    }

    // union-find
    private final LinkedList<Node> allNodes;
    // this is unnecessary for normal functionalities, but for debugging
    private final LinkedList<Node> roots;


    public UnionFind(){
        this.allNodes = new LinkedList<>();
        // for debugging
        this.roots = new LinkedList<>();
    }

    //
    private Node add(X x){
        Node node = new Node(x);
        this.allNodes.add(node);
        // for debugging:
        // a new node is a root
        this.roots.add(node);
        return node;
    }

    private Node findNode(X data){
        Node targetNode = null;
        for(Node node : this.allNodes){
            if(node.data.equals(data)){
                targetNode = node;
                break;
            }
        }
        if(targetNode == null){
            targetNode = add(data);
        }
        while(targetNode.parent != null){
            targetNode = targetNode.parent;
        }
        return targetNode;
    }

    public X find(X data){
        Node targetNode = this.findNode(data);
        return targetNode.data;
    }

    public void union(X data1, X data2, boolean direction){
        Node root1 = findNode(data1);
        Node root2 = findNode(data2);
        if(root1.equals(root2)){
            return;
        }
        // true/false
        if(direction) {
            root2.parent = root1;
            // for debugging:
            this.roots.remove(root2);
            root1.children.add(root2);
        }
        else {
            root1.parent = root2;
            // for debugging:
            this.roots.remove(root1);
            root2.children.add(root1);
        }
    }

    private void addEdge(Tree<String> tree, Node node){
        node.children.forEach((c) -> {
            tree.addNode(c.toString());
//            System.out.println(node.toString() + " -> " + c.toString());
            tree.addEdge(node.toString(), c.toString());
            addEdge(tree, c);
        });
    }

    private Tree<String> toTree(Node root){
        Tree<String> tree = new Tree<>(root.label.toString());
        tree.addRoot(root.toString());
        addEdge(tree, root);
        return tree;
    }

    public List<Tree<String>> toTrees(){
        // to convert each sub-tree, respectively:
       return this.roots.stream().map(this::toTree).toList();
    }

    public void dot(){
        List<Tree<String>> trees = this.toTrees();
        for(Tree<String> tree : trees){
            tree.dot("unionFind", Layout::str);
        }
    }

    // test
    @SuppressWarnings("unused")
    @Nested
    public class UnitTest {
        private static class Type {
            public sealed interface T permits Arrow, Int, Ptr, Var {
            }

            record Arrow(T from, T to) implements T {
            }

            record Int() implements T {
            }

            record Ptr(T ty) implements T {
            }

            record Var(Id var) implements T {
            }

            public static String toString(T ty) {
                switch (ty) {
                    case Arrow(T from, T to) -> {
                        return "Arrow_" + toString(from) + "__" + toString(to) + "_";
                    }
                    case Int() -> {
                        return "Int";
                    }
                    case Ptr(T ty1) -> {
                        return "Ptr(" + toString(ty1) + ")";
                    }
                    case Var(Id ty1) -> {
                        return "Id_" + ty1.toString();
                    }
                }
            }
        }


        public static void checkOccur(Id var1, Type.T t2) {
            switch (t2) {
                case Type.Arrow(Type.T from, Type.T to) -> {
                    checkOccur(var1, from);
                    checkOccur(var1, to);
                }
                case Type.Int() -> {
                }
                case Type.Ptr(Type.T ty) -> {
                    checkOccur(var1, ty);
                }
                case Type.Var(Id var) -> {
                    if (var1.equals(var)) {
                        throw new Error("recursive type");
                    } else {
                        System.out.println(var1.toString() + " " + var.toString());
                    }
                }
            }
        }

        public static Type.T normalize(Type.T ty, UnionFind<Type.T> uf) {
            switch (ty) {
                case Type.Arrow(Type.T from, Type.T to) -> {
                    var newFrom = normalize(from, uf);
                    var newTo = normalize(to, uf);
                    return new Type.Arrow(newFrom, newTo);
                }
                case Type.Int() -> {
                    return ty;
                }
                case Type.Ptr(Type.T ty2) -> {
                    return new Type.Ptr(normalize(ty2, uf));
                }
                case Type.Var(Id var2) -> {
                    var targetType = uf.find(ty);
                    switch (targetType) {
                        case Type.Arrow(_, _) -> {
                            return normalize(targetType, uf);
                        }
                        case Type.Int() -> {
                            return targetType;
                        }
                        case Type.Ptr(_) -> {
                            return normalize(targetType, uf);
                        }
                        case Type.Var(Id var3) -> {
                            return new Type.Var(var3);
                        }
                    }
                }
            }
        }

        public static void unify(Type.T x,
                                 Type.T y,
                                 UnionFind<Type.T> uf) {
            Type.T xtype = normalize(x, uf);
            Type.T ytype = normalize(y, uf);

            switch (xtype) {
                case Type.Arrow(Type.T from1, Type.T to1) -> {
                    switch (ytype) {
                        case Type.Arrow(Type.T from2, Type.T to2) -> {
                            // recursion
                            unify(from1, from2, uf);
                            unify(to1, to2, uf);
                        }
                        case Type.Int() -> {
                            throw new Error("bad equation");
                        }
                        case Type.Ptr(Type.T ty2) -> {
                            throw new Error("bad equation");
                        }
                        case Type.Var(Id var2) -> {
                            // occurs
                            checkOccur(var2, xtype);
                            uf.union(ytype, xtype, false);
                        }
                    }
                }
                case Type.Int() -> {
                    switch (ytype) {
                        case Type.Int() -> {
                        }
                        case Type.Var(Id var) -> {
                            uf.union(ytype, xtype, false);
                        }
                        default -> {
                            throw new Error("bad equation");
                        }
                    }
                }
                case Type.Ptr(Type.T ty2) -> {
                    switch (ytype) {
                        case Type.Ptr(Type.T ty3) -> {
                            unify(ty2, ty3, uf);
                        }
                        default -> {
                            throw new Error("bad equation");
                        }
                    }
                }
                case Type.Var(Id var1) -> {
                    switch (ytype) {
                        case Type.Arrow(Type.T from, Type.T to) -> {
                            // occurs
                            checkOccur(var1, ytype);
                            uf.union(xtype, ytype, false);
                        }
                        case Type.Int() -> {
                            uf.union(xtype, ytype, false);
                        }
                        case Type.Ptr(Type.T ty2) -> {
                            checkOccur(var1, ytype);
                            uf.union(xtype, ytype, false);
                        }
                        case Type.Var(Id var2) -> {
                            uf.union(xtype, ytype, false);
                        }
                    }
                }
            }
        }

        public static void unifyList(List<Tuple.Two<Type.T, Type.T>> list, UnionFind uf) {
            list.forEach((x) -> {
                unify(x.first(), x.second(), uf);
            });
        }

        @Nested
        public class UnitTest2 {

            @org.junit.jupiter.api.Test
            public void test() {

                UnionFind<Type.T> uf = new UnionFind<>();
                Type.Var varX = new Type.Var(Id.newName("x"));
                Type.Var varY = new Type.Var(Id.newName("y"));
                Type.Var varZ = new Type.Var(Id.newName("z"));

//            // x = y
                unify(varX, varY, uf);
//            uf.dot();

                // y = z -> z
                unify(varY, new Type.Arrow(varZ, varZ), uf);
//            uf.dot();

                // z = int -> int
                unify(varZ, new Type.Arrow(new Type.Int(), new Type.Int()), uf);
//            uf.dot();

                System.out.println(normalize(varX, uf));
                System.out.println(normalize(varY, uf));
                System.out.println(normalize(varZ, uf));


                // spa, page 23
                uf = new UnionFind<>();
                var shortId = new Type.Var(Id.newName("short"));
                var input = new Type.Var(Id.newName("input"));
                var allocX = new Type.Var(Id.newName("allocx"));
                var x = new Type.Var(Id.newName("x"));
                var y = new Type.Var(Id.newName("y"));
                var starY = new Type.Var(Id.newName("*y"));
                var z = new Type.Var(Id.newName("z"));

                unifyList(List.of(new Tuple.Two<>(shortId, new Type.Arrow(new Type.Int(), z)),
                                new Tuple.Two<>(input, new Type.Int()),
                                new Tuple.Two<>(x, input),
                                new Tuple.Two<>(allocX, new Type.Ptr(x)),
                                new Tuple.Two<>(y, allocX),
                                new Tuple.Two<>(y, new Type.Ptr(x)),
                                new Tuple.Two<>(z, starY),
                                new Tuple.Two<>(y, new Type.Ptr(starY))),
                        uf);

                System.out.println("shortid: " + normalize(shortId, uf));
                System.out.println("input: " + normalize(input, uf));
                System.out.println("allocx: " + normalize(allocX, uf));
                System.out.println("stary: " + normalize(starY, uf));
                System.out.println("x: " + normalize(x, uf));
                System.out.println("y: " + normalize(y, uf));
                System.out.println("z: " + normalize(z, uf));
                uf.dot();

            }
        }
    }
}

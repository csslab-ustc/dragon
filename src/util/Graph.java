package util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

// a graph is parameterized by its containing data type "X" on the node
@SuppressWarnings("unused")
public class Graph<X> {

    // graph node
    public class Node {
        private X data;
        private LinkedList<Edge> edges;
        private Plist plist;

        public Node() {
            this.data = null;
            this.edges = null;
            this.plist = null;
        }

        public Node(X data) {
            this.data = data;
            this.edges = new LinkedList<>();
            this.plist = new Plist();
        }

        public X getData() {
            return this.data;
        }

        public void setData(X data) {
            this.data = data;
        }

        public Plist getPlist() {
            return this.plist;
        }

        public HashSet<Node> successors() {
            HashSet<Node> successors = new HashSet<>();
            this.edges.forEach(edge -> {
                successors.add(edge.to);
            });
            return successors;
        }

        public HashSet<Node> predecessors() {
            HashSet<Node> predecessors = new HashSet<>();
            Graph.this.allNodes.forEach(n -> {
                n.edges.forEach(edge -> {
                    if (edge.to.equals(this)) {
                        predecessors.add(edge.from);
                    }
                });
            });
            return predecessors;
        }
    }

    // graph edge
    public class Edge {
        Node from;
        Node to;
        public Plist plist;

        public Edge(Node from, Node to) {
            this.from = from;
            this.to = to;
            this.plist = new Plist();
        }

        public Plist getPlist() {
            return plist;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof Graph.Edge)) return false;

            return (this == o);
        }

    }

    // graph data structures:
    private final String name;
    private final LinkedList<Node> allNodes;

    public Graph(String name) {
        this.name = name;
        this.allNodes = new LinkedList<>();
    }

    private void addNode(Node node) {
        this.allNodes.addLast(node);
    }

    public Node addNode(X data) {
        // sanity checking to make the data unique:
        for (Node n : this.allNodes)
            if (n.data.equals(data))
                throw new Error();

        Node node = new Node(data);
        this.addNode(node);
        return node;
    }

    // create a new node, but does not insert into the graph
    public Node newNode() {
        // sanity checking to make the data unique:
        Node node = new Node(null);
        return node;
    }

    public Node lookupNode(X data) {
        for (Node node : this.allNodes) {
            if (node.data.equals(data))
                return node;
        }
        return null;
    }

    public Edge addEdge(Node from, Node to) {
        Edge edge = new Edge(from, to);
        from.edges.addLast(edge);
        return edge;
    }

//    public void addEdge(X from, X to) {
//        Node f = this.lookupNode(from);
//        Node t = this.lookupNode(to);
//
//        if (f == null || t == null)
//            throw new Error();
//
//        this.addEdge(f, t);
//    }

    private <Y> Y dfsDoit(Node n,
                          BiFunction<Node, Y, Y> reduce,
                          Y value,
                          HashSet<Node> isVisited) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    public <Y> Y dfs(Node start,
                     BiFunction<Node, Y, Y> reduce,
                     Y value) {
        if (start == null)
            throw new Error();

        HashSet<Node> isVisited = new HashSet<>();

        Y result = dfsDoit(start,
                reduce,
                value,
                isVisited);

//        // For control-flow allNodes, we do not need this, as
//        // the "startNode" will reach all other nodes.
//        for (Node n : this.allNodes) {
//            if (!visited.contains(n))
//                dfsDoit(n, doit, value, visited);
//        }
        return result;
    }

    public <Y> Y reverseDfs(Node start,
                            BiFunction<Node, Y, Y> doit,
                            Y value) {
        if (start == null)
            throw new Error();

        List<Node> allNodes = topoSort(start).reversed();
        while (!allNodes.isEmpty()) {
            Node n = allNodes.removeFirst();
            value = doit.apply(n, value);
        }

//        // For control-flow allNodes, we do not need this, as
//        // the "startNode" will reach all other nodes.
//        for (Node n : this.allNodes) {
//            if (!visited.contains(n))
//                dfsDoit(n, doit, value, visited);
//        }
        return value;
    }

    private void topoSortDoit(Node n, List<Node> allNodes, HashSet<Node> visited) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    public List<Node> topoSort(Node start) {
        if (start == null)
            throw new Error();

        List<Node> allNodes = new LinkedList<>();
        HashSet<Node> visited = new HashSet<>();
        topoSortDoit(start, allNodes, visited);

//        // For control-flow allNodes, we do not need this, as
//        // the "startNode" will reach all other nodes.
//        for (Node n : this.allNodes) {
//            if (!visited.contains(n))
//                dfsDoit(n, doit, value, visited);
//        }
        return allNodes.reversed();
    }

    public void splitEdge(Edge edge,
                          Supplier<X> dataSupplier,
                          BiConsumer<X, Tuple.Two<X, X>> dataConnector) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    public List<Edge> findCriticalEdges(Node n, HashSet<Node> visited) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    public void criticalEdgeSplit(Node start,
                                  // data supplier is used to construct a new date for new node.
                                  // e.g. newData = dataSupplier();
                                  Supplier<X> dataSupplier,
                                  // data connector is used to build relationship between data.
                                  // support the original relationship is: data1 -> data2
                                  // if we want to replace data2 to a new data (named data3)
                                  // we can use dataConnector to handle this.
                                  // e.g. dataConnect(data1, new Tuple.Two<>(data2, data3));
                                  // then we will get: data1 -> data3
                                  BiConsumer<X, Tuple.Two<X, X>> dataConnector) {
        if (start == null)
            throw new Error();

        HashSet<Node> visited = new HashSet<>();
        // find all critical edges
        List<Edge> allCriticalEdges = findCriticalEdges(start, visited);
        // split all critical edges
        allCriticalEdges.forEach(edge -> splitEdge(edge, dataSupplier, dataConnector));
    }

    public void dot(Function<X, Layout.T> converter) {
        Dot dot = new Dot(this.name);
        for (Node node : this.allNodes) {
            for (Edge edge : node.edges)
                dot.insert(converter.apply(edge.from.data),
                        converter.apply(edge.to.data));
        }
        dot.visualize();
    }
}

















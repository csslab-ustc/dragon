package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.set.FunSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GraphTest {
    /// ///////////////////////////////////

    static Integer counter = 0;
    static Graph<Integer> graph = buildGraph();
    static Graph<Integer>.Node start = graph.lookupNode(0);

    @Test
    void testDFS() {
        var order = graph.dfs(start,
                (Graph<Integer>.Node n, List<Graph<Integer>.Node> list) -> {
                    list.add(n);
                    return list;
                },
                new ArrayList<>());
        // print result
        System.out.print("dfs order: ");
        order.forEach((node) -> System.out.print(node.getData() + ", "));
        System.out.println();
    }

    @Test
    void testTopoSort() {
        var order = graph.topoSort(start);
        // print result
        System.out.print("topoSort order: ");
        order.forEach((node) -> System.out.print(node.getData() + ", "));
        System.out.println();
    }

    @Test
    void testCriticalEdgeSplit() {
        graph.criticalEdgeSplit(start, () -> counter++, (_, _) -> {
        });
        graph.dot("", (Integer i) -> Layout.str(i.toString()));
    }

    static Graph<Integer> buildGraph() {
        counter = 0;
        Graph<Integer> graph = new Graph<>("test-graph");
        List<Graph<Integer>.Node> allNodes = IntStream.range(0, 7)
                .mapToObj((_) -> graph.newAndAddNode(counter++))
                .toList();
        graph.addEdge(allNodes.get(0), allNodes.get(1));
        graph.addEdge(allNodes.get(0), allNodes.get(2));
        graph.addEdge(allNodes.get(1), allNodes.get(5));
        graph.addEdge(allNodes.get(1), allNodes.get(6));
        graph.addEdge(allNodes.get(2), allNodes.get(6));
        graph.addEdge(allNodes.get(3), allNodes.get(1));
        graph.addEdge(allNodes.get(3), allNodes.get(4));
        graph.addEdge(allNodes.get(5), allNodes.get(3));
        return graph;
    }

    // the Figure 2.2
    enum DomAlgorithms{
        Cooper,
        Definition
    }
    @Test
    void testDominatorTwoAlgorithms() {
        testDominator(DomAlgorithms.Definition);
        testDominator(DomAlgorithms.Cooper);
    }
    void testDominator(DomAlgorithms which){
        Graph<String> graph = new Graph<>("test-dominator");
        List<Graph<String>.Node> allNodes = IntStream.range(0, 9)
                .mapToObj((i) -> graph.newAndAddNode("B"+i))
                .toList();
        graph.addEdge(allNodes.get(0), allNodes.get(1));
        graph.addEdge(allNodes.get(1), allNodes.get(2));
        graph.addEdge(allNodes.get(1), allNodes.get(5));
        graph.addEdge(allNodes.get(2), allNodes.get(3));
        graph.addEdge(allNodes.get(3), allNodes.get(4));
        graph.addEdge(allNodes.get(3), allNodes.get(1));
        graph.addEdge(allNodes.get(5), allNodes.get(6));
        graph.addEdge(allNodes.get(5), allNodes.get(8));
        graph.addEdge(allNodes.get(6), allNodes.get(7));
        graph.addEdge(allNodes.get(7), allNodes.get(3));
        graph.addEdge(allNodes.get(8), allNodes.get(7));

        graph.dot("dot", Layout::str);

        var domProp = which==DomAlgorithms.Definition?
                graph.dominators(allNodes.get(0)):
                graph.dominatorsCooper(allNodes.get(0));
        // the Table 2.2
        Assertions.assertEquals(domProp.get(allNodes.get(0)),
                new FunSet<>(allNodes.get(0)));
        Assertions.assertEquals(domProp.get(allNodes.get(1)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1))));
        Assertions.assertEquals(domProp.get(allNodes.get(2)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1), allNodes.get(2))));
        Assertions.assertEquals(domProp.get(allNodes.get(3)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1), allNodes.get(3))));
        Assertions.assertEquals(domProp.get(allNodes.get(4)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1), allNodes.get(3), allNodes.get(4))));
        Assertions.assertEquals(domProp.get(allNodes.get(5)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1), allNodes.get(5))));
        Assertions.assertEquals(domProp.get(allNodes.get(6)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1), allNodes.get(5), allNodes.get(6))));
        Assertions.assertEquals(domProp.get(allNodes.get(7)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1), allNodes.get(5), allNodes.get(7))));
        Assertions.assertEquals(domProp.get(allNodes.get(8)),
                new FunSet<>(List.of(allNodes.get(0), allNodes.get(1), allNodes.get(5), allNodes.get(8))));

    }
}




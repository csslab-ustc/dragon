package util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UtilTest {
    /// ///////////////////////////////////
    /// graph test
    @Nested
    class GraphTest {
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
            graph.criticalEdgeSplit(start, () -> counter++, (_, _) -> {});
            graph.dot((Integer i) -> Layout.str(i.toString()));
        }

        static Graph<Integer> buildGraph() {
            counter = 0;
            Graph<Integer> graph = new Graph<>("test-graph");
            ArrayList<Graph<Integer>.Node> allNodes = IntStream.range(0, 7)
                    .mapToObj((_) -> graph.addNode(counter++))
                    .collect(Collectors.toCollection(ArrayList::new));
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
    }
}

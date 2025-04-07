package cfg.lab1;

import org.junit.jupiter.api.Test;
import util.*;
import util.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.stream.Collectors;

import static cfg.Cfg.*;

@SuppressWarnings("unused")
public class CriticalEdgeSplit {
    // split the edge <from, to> if it is a critical edge
    // return the label of new block
    static Label splitEdge(Label from,
                           Label to,
                           Graph<Block.T> graph,
                           HashMap<Label, Graph<Block.T>.Node> map,
                           List<Graph<Block.T>.Edge> edges,
                           List<Block.T> blocks
    ) {
        Graph<Block.T>.Node fromNode = map.get(from);
        Graph<Block.T>.Node toNode = map.get(to);
        for (Graph<Block.T>.Edge edge : edges) {
            if (edge.getFrom().equals(fromNode) && edge.getTo().equals(toNode)) {
                Block.T newBlock = new Block.Singleton(
                        new Label(),
                        List.of(),
                        new Transfer.Jmp(new Label(), to)
                );
                blocks.add(newBlock);
                return Block.getLabel(newBlock);
            }
        }
        return to;
    }

    static List<Block.T> doitBlock(
            Block.T block,
            Graph<Block.T> graph,
            HashMap<Label, Graph<Block.T>.Node> map,
            List<Graph<Block.T>.Edge> edges
    ) {
        List<Block.T> blocks = new ArrayList<>();
        switch (block) {
            case Block.Singleton(
                    Label label,
                    List<Stm.T> stms,
                    Transfer.T transfer
            ) -> {
                switch (transfer) {
                    case Transfer.If(
                            Label tlabel,
                            Id x,
                            Label trueLabel,
                            Label falseLabel
                    ) -> {
                        Label newTrueLabel = splitEdge(label, trueLabel, graph, map, edges, blocks);
                        Label newFalseLabel = splitEdge(label, falseLabel, graph, map, edges, blocks);
                        blocks.addFirst(new Block.Singleton(
                                label,
                                stms,
                                new Transfer.If(tlabel, x, newTrueLabel, newFalseLabel)
                        ));
                    }
                    case Transfer.Jmp(
                            Label tlabel,
                            Label target
                    ) -> {
                        Label newTargetLabel = splitEdge(label, target, graph, map, edges, blocks);
                        blocks.addFirst(new Block.Singleton(
                                label,
                                stms,
                                new Transfer.Jmp(tlabel, newTargetLabel))
                        );
                    }
                    case Transfer.Ret(_, _) -> blocks.add(block);
                }
            }
        }
        return blocks;
    }

    // split all critical edges of given func
    // return a new func
    static Function.T doitFunction(Function.T func) {
        // build its control-flow graph
        var result = Function.buildControlFlowGraph(func);
        Graph<Block.T> graph = result.first();
        HashMap<Label, Graph<Block.T>.Node> map = result.second();

        switch (func) {
            case Function.Singleton(
                    Type.T retType,
                    Id functionId,
                    List<Dec.T> formals,
                    List<Dec.T> locals,
                    List<Block.T> blocks,
                    Label entryBlock,
                    Label exitBlock
            ) -> {
                Graph<Block.T>.Node start = map.get(entryBlock);
                if (start == null) {
                    throw new Error();
                }
                // get all the critical edges in cfg
                List<Graph<Block.T>.Edge> edges = graph.findCriticalEdges(start, new HashSet<>());
                System.out.println("Critical edges: " + edges.size());
                // construct new blocks
                List<Block.T> newBlocks = new ArrayList<>();
                blocks.forEach(block -> newBlocks.addAll(doitBlock(block, graph, map, edges)));
                return new Function.Singleton(
                        retType,
                        functionId,
                        formals,
                        locals,
                        newBlocks,
                        // Label of entryBlock & exitBlock will never change
                        entryBlock,
                        exitBlock
                );
            }
        }
    }

    // split all critical edges of all functions in given program
    // return a new program
    static public Program.T doitProgram(Program.T program) {
        switch (program) {
            case Program.Singleton(List<Function.T> functions) -> {
                List<Function.T> newFunctions = functions.stream()
                        .map(CriticalEdgeSplit::doitFunction)
                        .collect(Collectors.toList());
                return new Program.Singleton(newFunctions);
            }
        }
    }

    @Test
    void test() {
        Label label1, label2, label3;
        label1 = new Label();
        label2 = new Label();
        label3 = new Label();
        Block.T b1 = new Block.Singleton(
                label1,
                List.of(),
                new Transfer.Jmp(label2)
        );
        Block.T b2 = new Block.Singleton(
                label2,
                List.of(),
                new Transfer.If(
                        new Label(), Id.newName("i"), label3, label2
                )
        );
        Block.T b3 = new Block.Singleton(
                label3,
                List.of(),
                new Transfer.Ret(Id.newName("i"))
        );
        List<Block.T> blocks = List.of(b1, b2, b3);
        Function.T func = new Function.Singleton(null,
                Id.newName("test"),
                List.of(),
                List.of(),
                blocks,
                label1,
                label3);
        Program.T p = new Program.Singleton(List.of(func));
        p = doitProgram(p);
        // FIXME: permission denied
//        Program.dot(p);
    }
}

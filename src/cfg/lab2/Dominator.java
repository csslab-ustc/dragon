package cfg.lab2;

import cfg.Cfg;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.*;

import java.util.*;

public class Dominator {
    private static final Property<Graph<Cfg.Block.T>.Node,
            Integer> numberProp = new Property<>(Graph.Node::getPlist);

    // getNumber return the number on the node. We can store the node's number
    // on the node's plist as its attribute. You don't have to use this method,
    // but it's just a shortcut for your reference.
    private int getNumber(Graph<Cfg.Block.T>.Node n) {
        return numberProp.get(n);
    }

    private Graph<Cfg.Block.T>.Node intersect(Graph<Cfg.Block.T>.Node[] idoms, Graph<Cfg.Block.T>.Node a,
                                              Graph<Cfg.Block.T>.Node b) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    private Tree<Cfg.Block.T> doitFunction(Cfg.Function.T func) {
        // In this part, you should build the dominator tree using Cooper's algorithm.
        // For convenience, you can use the Graph class to deal with the reverse dfs (or topological sort),
        // and find basic block's predecessors, and so on. You can build function's Graph using the method
        // buildControlFlowGraph provided in CFG.java.
        // TODO: please add your code:
        throw new util.Todo();

    }

    private Map<Cfg.Function.T, Tree<Cfg.Block.T>> doitProgram0(Cfg.Program.T prog) {
        Map<Cfg.Function.T, Tree<Cfg.Block.T>> dominatorTree = new HashMap<>();

        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> functions.forEach(f -> dominatorTree.put(f, doitFunction(f)));
        }
        return dominatorTree;
    }

    public Map<Cfg.Function.T, Tree<Cfg.Block.T>> doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.Dominator",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                (x) ->{
            for(Map.Entry<Cfg.Function.T, Tree<Cfg.Block.T>> entry: x.entrySet()){
                var func = entry.getKey();
                Cfg.Function.dot(func);
                var tree = entry.getValue();
                tree.dot("domTree", Cfg.Block::layout);
            }
                });
        return trace.doit();
    }



    @Nested
    class TestDominator {
        @Test
        public void test() throws Exception {
            Cfg.Program.T cfg = new Frontend().buildCfg("./test/test-dominator.c");

            // comment the following line, if you do NOT want the result.
            Control.tracedMethodNames.add("cfg.Dominator");
            var _ = new Dominator().doitProgram(cfg);
        }
    }
}

package cfg.lab2;

import cfg.Cfg;
import cfg.Cfg.Exp;
import cfg.Cfg.Stm;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.*;
import util.set.FunSet;

import java.util.HashSet;
import java.util.List;

public class Liveness {
    // properties
    // attach liveIn/liveOut set to a graph node
    private static final Property<Graph<Cfg.Block.T>.Node, FunSet<Id>> inProp =
            new Property<>(Graph.Node::getPlist);
    private static final Property<Graph<Cfg.Block.T>.Node, FunSet<Id>> outProp =
            new Property<>(Graph.Node::getPlist);
    private static final Property<Stm.T, FunSet<Id>> liveOutPropForStm =
            new Property<>(Stm::getPlist);

    // calculate gen-kill
    static class GenKill {
        private static FunSet<Id> gkExp(Exp.T exp) {
            // TODO: please add your code:
            throw new util.Todo();

        }

        // return: (gen, kill)
        public static Tuple.Two<FunSet<Id>, FunSet<Id>> gkStm(Stm.T stm) {
            // TODO: please add your code:
            throw new util.Todo();

        }

        // return: (gen, kill)
        public static Tuple.Two<FunSet<Id>, FunSet<Id>> gkTransfer(Cfg.Transfer.T transfer) {
            // TODO: please add your code:
            throw new util.Todo();

        }
    }
    // end of gen-kill


    // /////////////////////////////////////////////////////////
    // statement
    // given the "out", calculates and returns the "in"
    private FunSet<Id> doitStm(Stm.T stm, FunSet<Id> liveOut) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    // given the "liveOut", calculates and returns the "liveIn"
    private FunSet<Id> doitTransfer(Cfg.Transfer.T transfer, FunSet<Id> liveOut) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of transfer

    // /////////////////////////////////////////////////////////
    // block
    // takes an liveOut, and calculates and returns a liveIn
    private FunSet<Id> doitBlock(Cfg.Block.T b, FunSet<Id> liveOut) {
        switch (b) {
            case Cfg.Block.Singleton(
                    Label label,
                    List<Stm.T> stms,
                    Cfg.Transfer.T transfer
            ) -> {
                // logging
                Control.log(label.toString() + ": ");
                Control.log("liveOut = ");
                Control.logln(liveOut.layout(), Layout.Style.C);
                // transfer
                FunSet<Id> liveIn = doitTransfer(transfer, liveOut);
                for(Stm.T s: stms.reversed()){
                    liveIn = doitStm(s, liveIn);
                }
                Control.log(label.toString() + ": ");
                Control.log("liveIn = ");
                Control.logln(liveIn.layout(), Layout.Style.C);
                Control.logln("");
                return liveIn;
            }
        }
    }
    // end of block

    private Object doitNode(Graph<Cfg.Block.T>.Node node) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    // /////////////////////////////////////////////////////////
    // function
    private int rounds = 0;
    private boolean isChanged = false;

    private void doitFunction(Cfg.Function.T func) {
        switch (func) {
            case Cfg.Function.Singleton(
                    Cfg.Type.T retType,
                    Id functionId,
                    List<Cfg.Dec.T> formals,
                    List<Cfg.Dec.T> locals,
                    List<Cfg.Block.T> blocks,
                    Label entryBlock,
                    Label exitBlock
            ) -> {
                // build the control flow graph
                // we may also build the block2node data structure mapping
                // each basic block to its corresponding graph node
                var graphAndMap = Cfg.Function.buildControlFlowGraph(func);
                var graph = graphAndMap.first();
                var block2node = graphAndMap.second();
                // dump the graph
                graph.dot("cfg", Cfg.Block::layout);

                // fix-point algorithm
                do {
                    isChanged = false;
                    rounds++;
                    Control.logln("\nround: [" + rounds + "]");
                    graph.dfs(block2node.get(entryBlock),
                            (Graph<Cfg.Block.T>.Node node, Object obj) ->
                                    doitNode(node),
                            null);
                } while (isChanged);

                Control.log(FunSet.status(), Layout.Style.C);

                // debugging
                blocks.forEach(block -> {
                    switch (block){
                        case Cfg.Block.Singleton(Label label,
                                                 List<Stm.T> stms,
                                                 Cfg.Transfer.T transfer) ->{
                            stms.forEach(s -> {
                                Control.log(Stm.layout(s), Layout.Style.C);
                                // may be null
                                var liveOut = liveOutPropForStm.get(s);
                                Control.log("liveOut = ");
                                if(liveOut == null)
                                    Control.logln("<null>");
                                else
                                    Control.logln(liveOut.layout(), Layout.Style.C);
                            });

//                                Control.log(Cfg.Transfer.layout(transfer), Layout.Style.C);
//                                var out = liveOutPropForTransfer.get(s);
//                                out.print();
//                                Control.log("\n");
                        }
                    }
                });
            }
        }
    }
    // end of function

    // /////////////////////////////////////////////////////////
    // program
    private Property<Stm.T, FunSet<Id>> doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> functions.forEach(this::doitFunction);
        }
        Control.logln("executions = " + rounds);
        inProp.clear();
        outProp.clear();
        return liveOutPropForStm;
    }

    // return the "liveOut" for each statement
    public Property<Stm.T, FunSet<Id>> doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.Liveness",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                (x) -> {
                });
        return trace.doit();
    }
    // end of program
}
// end of liveness
















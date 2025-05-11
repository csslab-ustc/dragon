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

public class ReachDefinition {
    // the "knowledge" we are calculating
    static class InOutKnowledge extends FunSet<Stm.T> {
    }

    // properties
    // attach definition sites to each identifier
    private static final Property<Id, FunSet<Stm.T>> defSitesProp =
            new Property<>(Id::getPlist);
    // attach predecessors to each graph node
    private final Property<Graph<Cfg.Block.T>.Node, HashSet<Graph<Cfg.Block.T>.Node>> predPropForBlock =
            new Property<>(Graph.Node::getPlist);
    // attach liveIn/liveOut set to each graph node (i.e., block)
    private final Property<Graph<Cfg.Block.T>.Node, FunSet<Stm.T>> inPropForBlock =
            new Property<>(Graph.Node::getPlist);
    private final Property<Graph<Cfg.Block.T>.Node, FunSet<Stm.T>> outPropForBlock =
            new Property<>(Graph.Node::getPlist);
    // attach liveIn set to each statement
    private final Property<Stm.T, FunSet<Stm.T>> inPropForStm =
            new Property<>(Stm::getPlist);
    // attach liveIn set to each transfer
    private final Property<Cfg.Transfer.T, FunSet<Stm.T>> inPropForTransfer =
            new Property<>(Cfg.Transfer::getPlist);


    // calculate gen-kill sets
    static class GenKill {
        // to make the return result explicit:
        record Result(FunSet<Stm.T> gen,
                      FunSet<Stm.T> kill) {
        }

        // return: (gen, kill)
        public static Result gkStm(Stm.T stm) {
            // d: t = x1 op x2
            // gen[d] = {d}
            // kill[d] = defs[t] - {d} or kill[d] = defs[t] - gen[d]
            // TODO: please add your code:
            throw new util.Todo();

        }

        // calculate defs(x) for each variable x
        public static void defsForFunction(Cfg.Function.T function) {
            // TODO: please add your code:
            throw new util.Todo();

        }
    }
    // end of gen-kill

    // /////////////////////////////////////////////////////////
    // statement
    // forward: given the "in", calculates and returns the "out"
    private FunSet<Stm.T> doitStm(Stm.T stm, FunSet<Stm.T> liveIn) {
        // liveOut = (liveIn - kill) \/ gen
        // to debug, you can print the liveIn & liveOut like this:
        // liveIn.print() / liveOut.print();
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    private FunSet<Stm.T> doitTransfer(Cfg.Transfer.T t, FunSet<Stm.T> in) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of transfer

    // /////////////////////////////////////////////////////////
    // block
    // takes an liveIn, and calculates and returns a liveOut
    private FunSet<Stm.T> doitBlock(Cfg.Block.T b, FunSet<Stm.T> in) {
        switch (b) {
            case Cfg.Block.Singleton(
                    Label label,
                    List<Stm.T> stms,
                    Cfg.Transfer.T transfer
            ) -> {
                // debugging
                Control.log(label.toString() + ":\n");
                Control.log("liveIn = ");
                Control.logln(in.layout(), Layout.Style.C);
                for (Stm.T s : stms) {
                    in = doitStm(s, in);
                }
                Control.log("liveOut = ");
                Control.logln(in.layout(), Layout.Style.C);
                in = doitTransfer(transfer, in);
                Control.logln("");
                return in;
            }
        }
    }
    // end of block

    private Object doitNode(Graph<Cfg.Block.T>.Node node) {
        // get all predecessors
        HashSet<Graph<Cfg.Block.T>.Node> predecessors = predPropForBlock.getOrInitFun(node,
                Graph.Node::predecessors);
        // get all the liveOut for predecessors
        List<FunSet<Stm.T>> outForPrecessors = predecessors.stream().
                map(n -> outPropForBlock.getOrInitConst(n, new FunSet<>())).toList();
        // liveIn = \/ liveOut[preds]
        FunSet<Stm.T> liveIns = FunSet.unionSets(outForPrecessors);
        var oldLiveIn = inPropForBlock.get(node); // may be null
        // determine whether "liveOut" has changed
        if (!liveIns.equals(oldLiveIn)) {
            isChanged = true;
            // record the liveOut for this node
            inPropForBlock.put(node, liveIns);
            FunSet<Stm.T> newLiveOut = doitBlock(node.getData(), liveIns);
            // record the liveOut for this node
            outPropForBlock.put(node, newLiveOut);
        }
        return null;
    }

    // /////////////////////////////////////////////////////////
    // function
    // record the execution rounds
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
//                graph.dot((x) -> Cfg.Block.getLabel(x).toString());

                // init the gen-kill
                GenKill.defsForFunction(func);
                formals.forEach(formal -> {
                    switch (formal) {
                        case Cfg.Dec.Singleton(Cfg.Type.T type, Id id) -> {
                            Control.log("defs[" + id + "] = ");
                            var theSet = defSitesProp.getOrInitConst(id, new FunSet<>());
                            Control.logln(theSet.layout(), Layout.Style.C);
                        }
                    }
                });
                locals.forEach(formal -> {
                    switch (formal) {
                        case Cfg.Dec.Singleton(Cfg.Type.T type, Id id) -> {
                            Control.log("defs[" + id + "] = ");
                            var theSet = defSitesProp.getOrInitConst(id, new FunSet<>());
                            Control.logln(theSet.layout(), Layout.Style.C);
                        }
                    }
                });
                Control.logln("");

                // fix-point algorithm
                // remember to use isChanged & rounds
                // TODO: please add your code:
                throw new util.Todo();


                // clear the defs property
//                defSitesProp.clear();
//                predPropForBlock.clear();
            }
        }
    }
    // end of function

    // /////////////////////////////////////////////////////////
    // program
    private Tuple.Two<Property<Stm.T, FunSet<Stm.T>>,
            Property<Cfg.Transfer.T, FunSet<Stm.T>>>
    doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> functions.forEach(this::doitFunction);
        }
        Control.logln("reach definition execution rounds = " + rounds);

        inPropForBlock.clear();
        outPropForBlock.clear();
        return new Tuple.Two<>(inPropForStm, inPropForTransfer);
    }

    public Tuple.Two<Property<Stm.T, FunSet<Stm.T>>,
            Property<Cfg.Transfer.T, FunSet<Stm.T>>>
    doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.ReachDef",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                (x) -> {
                });
        return trace.doit();
    }
    // end of program
}
// end of class



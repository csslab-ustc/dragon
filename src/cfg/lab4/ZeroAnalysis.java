package cfg.lab4;

import cfg.Cfg;
import cfg.Cfg.Exp;
import cfg.Cfg.Stm;
import control.Control;
import control.Control.Trace;
import util.*;
import util.lattice.DiamondLattice;
import util.lattice.MapLattice;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ZeroAnalysis {

    // Lattice 1: ZeroLattice
    public static class ZeroLattice extends DiamondLattice{
        public ZeroLattice(T s){
            super(s);
        }

        // factory methods
        public static ZeroLattice newMany(){
            return new ZeroLattice(new Top());
        }
        public static ZeroLattice newZero(){
            return new ZeroLattice(new M0());
        }
        public static ZeroLattice newNoneZero(){
            return new ZeroLattice(new M1());
        }
        public static ZeroLattice newNone(){
            return new ZeroLattice(new Bot());
        }

        public boolean isZero() {
            return super.isM0();
        }

        public Layout.T layout(){
            return Layout.str(this.toString());
        }

        @Override
        public String toString() {
            return switch (this.state){
                case Bot() -> "Unknown";
                case M0() -> "Zero";
                case M1() -> "NoneZero";
                case Top() -> "Any";
            };
        }
    }

    // Lattice 2: a map lattice
    public static class Id2ZeroMapLattice extends MapLattice<Id, ZeroLattice> {
        public Id2ZeroMapLattice(List<Id> keys, Supplier<ZeroLattice> latticeGenerator) {
            super(keys, latticeGenerator);
        }

        // factory methods
        public static Id2ZeroMapLattice newWithNone(List<Id> keys){
            return new Id2ZeroMapLattice(keys, ZeroLattice::newNone);
        }

        public static Id2ZeroMapLattice newWithMany(List<Id> keys){
            return new Id2ZeroMapLattice(keys, ZeroLattice::newMany);
        }

        public boolean mayLiftTo(List<Id2ZeroMapLattice> others) {
            boolean changed = false;
            for(Id2ZeroMapLattice other: others){
                if(mayLiftTo(other, ZeroLattice::mayLiftTo))
                    changed = true;
            }
            return changed;
        }

        public Layout.T layout(){
            return Layout.str(this.toString());
        }

        public Id2ZeroMapLattice clone(){
            try {
                var x = (Id2ZeroMapLattice) super.clone();
                return x;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public boolean equals(Object right){
            return super.equals(right);
        }
    }

    // maintain global information for this analysis
    static class Global{
        static int rounds = 0;
        static boolean isChanged = false;
        // formals & globals
        static List<Id> allIds = new LinkedList<>();

        static void reset(){
            rounds = 0;
            isChanged = false;
            allIds.clear();
        }

        static Id2ZeroMapLattice defaultMapWithBottom(){
            return Id2ZeroMapLattice.newWithNone(allIds);
        }
        static Id2ZeroMapLattice defaultMapWithTop(){
            return Id2ZeroMapLattice.newWithMany(allIds);
        }
    }

    // /////////////////////////////////////////////////////////
    // properties
    // attach predecessors to each graph node
    private static final Property<Graph<Cfg.Block.T>.Node, HashSet<Graph<Cfg.Block.T>.Node>> predProp =
            new Property<>(Graph.Node::getPlist);
    // attach liveIn/liveOut set to each graph node
    private static final Property<Graph<Cfg.Block.T>.Node, Id2ZeroMapLattice> inForBlockProp =
            new Property<>(Graph.Node::getPlist);
    private static final Property<Graph<Cfg.Block.T>.Node, Id2ZeroMapLattice> outForBlockProp =
            new Property<>(Graph.Node::getPlist);
    // attach liveOut set for each statement
    private static final Property<Stm.T, Id2ZeroMapLattice> outForStmProp =
            new Property<>(Cfg.Stm::getPlist);


    // /////////////////////////////////////////////////////////
    // expression
    // forward: given the "in", calculates and returns the "out"
    private ZeroLattice doitBinaryOperator(Cfg.BinaryOperator.T op,
                                           List<Id> operands,
                                           Id2ZeroMapLattice liveIn) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    // /////////////////////////////////////////////////////////
    // expression
    // forward: given the "in", calculates and returns the "out"
    private ZeroLattice doitExp(Exp.T exp,
                                Id2ZeroMapLattice liveIn) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    // forward: given the "in", calculates the "out"
    private void doitStm(Stm.T stm,
                         Id2ZeroMapLattice liveIn) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    // given the "out", calculates and returns the "in"
//    private FunSet<Id> doitTransfer(Cfg.Transfer.T t, FunSet<Id> out) {
////        Tuple.Two<FunSet<Id>, FunSet<Id>> genKill = GenKill.gkTransfer(t);
//        return out.sub(genKill.second()).union(genKill.first());
//    }
    // end of transfer

    // /////////////////////////////////////////////////////////
    // block
    // takes as input an liveIn, and calculates and returns a liveOut
    private void doitBlock(Cfg.Block.T b,
                           Id2ZeroMapLattice in) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of block

    private Object doitNode(Graph<Cfg.Block.T>.Node node) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    // /////////////////////////////////////////////////////////
    // function
    private void doitFunction(Cfg.Function.T func) {
        switch (func) {
            case Cfg.Function.Singleton(
                    Cfg.Type.T _,
                    Id _,
                    List<Cfg.Dec.T> formals,
                    List<Cfg.Dec.T> locals,
                    List<Cfg.Block.T> _,
                    Label entryLabel,
                    Label _
            ) -> {
                Global.reset();
                Global.allIds.addAll(formals.stream().map((x) -> Cfg.Dec.destroy(x).second()).toList());
                Global.allIds.addAll(locals.stream().map((x) -> Cfg.Dec.destroy(x).second()).toList());

                // Step #1: build the control flow graph
                // we may also build the block2node data structure mapping
                // each basic block to its corresponding graph node
                var graphAndMap = Cfg.Function.buildControlFlowGraph(func);
                var graph = graphAndMap.first();
                var block2node = graphAndMap.second();
                // dump the graph
//                graph.dot("zeroAnalysis", Cfg.Block::layout);

                // debug
                Control.logln(Layout.halignSepRight(Layout.str(", "),
                                Global.allIds.stream().map(Id::layout).toList()),
                    Layout.Style.C);

                // Step #3: for the entry block, initialize its liveIn[] to be "defaultMapWithTop"
                // to reflect the fact that all variables are uninitialized.
                Graph<Cfg.Block.T>.Node entryNode = block2node.get(entryLabel);

                // this is a special hack
                // we attach a "fake" predecessor to the entry node
                // so that we can get the information about the formals and locals
                var extraEntryNode = graph.newAndAddNode(null);
                outForBlockProp.put(extraEntryNode, Global.defaultMapWithTop());
                predProp.put(entryNode, new HashSet<>(List.of(extraEntryNode)));

                // Step #3: fix-point algorithm
                do {
                    Global.isChanged = false;
                    Global.rounds++;
                    graph.dfs(entryNode,
                            (Graph<Cfg.Block.T>.Node node, Object _) ->
                        doitNode(node),
                            null);
                } while (Global.isChanged);
                Control.logln("execution rounds = " + Global.rounds);
            }
        }
    }
    // end of function

    // /////////////////////////////////////////////////////////
    // program
    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> functions.forEach(this::doitFunction);
        }
        // clean up
        predProp.clear();
        inForBlockProp.clear();
        outForBlockProp.clear();
        return prog;
    }

    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        Trace<Cfg.Program.T, Cfg.Program.T> trace = new Control.Trace<>("cfg.ZeroAnalysis",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program
}
// end of ZeroAnalysis



















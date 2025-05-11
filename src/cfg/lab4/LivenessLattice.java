package cfg.lab4;

import cfg.Cfg;
import cfg.Cfg.Exp;
import cfg.Cfg.Stm;
import control.Control;
import util.*;
import util.lattice.PowerSetLattice;
import util.set.FunSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// liveness analysis, using the powerset lattice
//@SuppressWarnings("all")
public class LivenessLattice {

    // maintain global information
    static class Global {
        static WorkList<Graph<Cfg.Block.T>.Node> workList = null;
        static HashMap<Id, Integer> execRounds = new HashMap<>();

        static Id currentFunctionId;

        static void init(Id funId) {
            currentFunctionId = funId;
        }

        static void tick() {
            execRounds.put(currentFunctionId, execRounds.getOrDefault(currentFunctionId, 0) + 1);
        }

        static void clear() {
            PowerSetLattice.printBeforeClear();
        }

        static void statistics() {
            Control.logln("execution rounds:");
            execRounds.forEach((k, v) ->
                    Control.logln("\tfunction " + k.toString() + ": " + v));
        }
    }

    static class IdPowerSetLattice extends PowerSetLattice<Id> {
        public IdPowerSetLattice() {
            super();
        }
        public IdPowerSetLattice(Id id) {
            super(id);
        }
        public IdPowerSetLattice(List<Id> ids) {
            super(ids);
        }
        public IdPowerSetLattice(FunSet<Id> ids) {
            super(ids);
        }

    }


    // properties
    // attach successors/predecessors to a graph node
    private final Property<Graph<Cfg.Block.T>.Node, HashSet<Graph<Cfg.Block.T>.Node>> predProp =
            new Property<>(Graph.Node::getPlist);
    private final Property<Graph<Cfg.Block.T>.Node, HashSet<Graph<Cfg.Block.T>.Node>> succProp =
            new Property<>(Graph.Node::getPlist);

    // attach liveIn/liveOut set to a graph node
    private final Property<Graph<Cfg.Block.T>.Node, IdPowerSetLattice> inProp =
            new Property<>(Graph.Node::getPlist);
    private final Property<Graph<Cfg.Block.T>.Node, IdPowerSetLattice> outProp =
            new Property<>(Graph.Node::getPlist);

    // calculate gen-kill
    static class GenKill {
        private static IdPowerSetLattice gkExp(Exp.T exp) {
            switch (exp) {
                case Exp.Bop(
                        Cfg.BinaryOperator.T op,
                        List<Id> operands
                ) -> {
                    return new IdPowerSetLattice(operands);
                }
                case Exp.Call(Id func, List<Id> args) -> {
                    return new IdPowerSetLattice(args);
                }
                case Exp.Eid(Id id) -> {
                    return new IdPowerSetLattice(id);
                }
                case Exp.Int(int n) -> {
                    return new IdPowerSetLattice();
                }
                case Exp.Print(Id x) -> {
                    return new IdPowerSetLattice(x);
                }
                default -> throw new Todo(exp);
            }
        }

        // return: (gen, kill)
        public static Tuple.Two<IdPowerSetLattice, IdPowerSetLattice> gkStm(Stm.T stm) {
            switch (stm) {
                case Stm.Assign(_, Id x, Exp.T exp) -> {
                    return new Tuple.Two<>(gkExp(exp),
                            new IdPowerSetLattice(x));
                }
            }
        }

        // return: (gen, kill)
        public static Tuple.Two<IdPowerSetLattice, IdPowerSetLattice>
        gkTransfer(Cfg.Transfer.T transfer) {
            // TODO: please add your code:
            throw new util.Todo();

        }
    }
    // end of gen-kill


    // /////////////////////////////////////////////////////////
    // statement
    // given the "out", calculates and returns the "in"
    private IdPowerSetLattice doitStm(Stm.T stm, IdPowerSetLattice liveOut) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    // given the "out", calculates and returns the "in"
    private IdPowerSetLattice doitTransfer(Cfg.Transfer.T t, IdPowerSetLattice out) {
        Tuple.Two<IdPowerSetLattice, IdPowerSetLattice> genKill = GenKill.gkTransfer(t);
        return new IdPowerSetLattice(out.getSet().sub(genKill.second().getSet()).union(genKill.first().getSet()));
    }
    // end of transfer

    // /////////////////////////////////////////////////////////
    // block
    // takes an liveOut, and calculates and returns a liveIn
    private IdPowerSetLattice doitBlock(Cfg.Block.T b, IdPowerSetLattice out) {
        switch (b) {
            case Cfg.Block.Singleton(
                    Label label,
                    List<Stm.T> stms,
                    Cfg.Transfer.T transfer
            ) -> {
                // TODO: please add your code:
                throw new util.Todo();

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
    private void doitFunction(Cfg.Function.T func) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of function

    // /////////////////////////////////////////////////////////
    // program
    public Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> functions.forEach(this::doitFunction);
        }
        Global.statistics();
        inProp.clear();
        outProp.clear();
        return prog;
    }

    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        Control.Trace<Cfg.Program.T, Cfg.Program.T> trace = new Control.Trace<>("cfg.livenessLattice",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program
}
// end of liveness



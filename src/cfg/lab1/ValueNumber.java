package cfg.lab1;

import cfg.Cfg;
import cfg.Cfg.Exp;
import cfg.Cfg.Stm;
import cfg.SamplePrograms;
import control.Control;
import org.junit.jupiter.api.Nested;
import util.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ValueNumber {
    // /////////////////////////////////////////////////////////
    // properties
    // attach new Id to the statement
    private static final Property<Stm.T, Id> redefProp =
            new Property<>(Cfg.Stm::getPlist);
    private static final Property<Stm.T, Id> substProp =
            new Property<>(Cfg.Stm::getPlist);
    private static final Property<Cfg.Block.T, Cfg.Block.T> newBlockProp =
            new Property<>(Cfg.Block::getPlist);

    // the map
    private static class Map{
        // to use "Id" to implement the abstract values,
        // also record where the exp resides
        private static HashMap<Cfg.Exp.T, Tuple.Two<Id, Stm.T>> values = new HashMap<>();

        public static void init(){
            values = new HashMap<>();
        }

        public static Tuple.Three<Boolean, Id, Stm.T> lookupOrInsert(Exp.T exp, Stm.T context){
            var idAndStm = values.get(exp);
            if(idAndStm != null) {
                var id = idAndStm.first();
                var stm = idAndStm.second();
                return new Tuple.Three<>(true, id, stm);
            }
            Id id = Id.newNoname();
            values.put(exp, new Tuple.Two<>(id, context));
            return new Tuple.Three<>(false, id, null);
        }

        public static void put(Exp.T exp, Id id, Stm.T context){
            values.put(exp, new Tuple.Two<>(id, context));
        }
    }

    private static final LinkedList<Id> generatedIds = new LinkedList<>();

    // /////////////////////////////////////////////////////////
    // expression
    // given the concreate expression, return the "valued" expression
    private Exp.T doitExp(Exp.T exp, Stm.T stm) {
        switch (exp){
            // we only care about binary operations
            // TODO: please add your code:
            default -> throw new util.Todo();

        }
    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    private void doitStm(Stm.T stm) {
        switch (stm){
            // TODO: please add your code:
            default -> throw new util.Todo();

        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // block
    private void doitBlock(Cfg.Block.T b) {
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
        doitBlock(node.getData());
        // changed
        Cfg.Block.pp(node.getData());
        return null;
    }

    // /////////////////////////////////////////////////////////
    // function
    private Cfg.Function.T doitFunction(Cfg.Function.T func) {
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
                // Step #1: build the control flow graph
                // we may also build the block2node data structure mapping
                // each basic block to its corresponding graph node
                var graphAndMap = Cfg.Function.buildControlFlowGraph(func);
                var graph = graphAndMap.first();
                var block2node = graphAndMap.second();
                // dump the graph
//                graph.dot((x) -> Cfg.Block.getLabel(x).toString());

                // Step #2: for the entry block, init its liveIn to be "defaultMapWithTop"
                // to reflect the fact that all variables are uninitialized.
                var entryNode = block2node.get(entryBlock);

                // Step #3: fix-point algorithm
                // TODO: please add your code:
                throw new util.Todo();


//                return new Cfg.Function.Singleton(retType, functionId, formals, locals,
//                        blocks.stream().map(newBlockProp::get).toList(),
//                        entryBlock,
//                        exitBlock);
            }
        }
    }
    // end of function

    // /////////////////////////////////////////////////////////
    // program
    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        prog = switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> new Cfg.Program.Singleton(functions.stream().map(this::doitFunction).toList());
        };
        return prog;
    }

    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        var trace = new Trace<>("cfg.ValueNumber",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program


    /// ////////////////////////////////////////////////////////
    // unit test
    @Nested
    class UnitTest{

        @Test
        void test() {
            var prog = SamplePrograms.valueNum;
            Control.tracedMethodNames.add("cfg.ValueNumber");
            prog = new ValueNumber().doitProgram(prog);
        }
    }
}
// end of value number



package cfg.lab3;

import cfg.Cfg;
import cfg.Cfg.Stm;
import cfg.lab2.ReachDefinition;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Id;
import util.Label;
import util.Property;
import util.Todo;
import util.set.FunSet;

import javax.swing.text.html.Option;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ConstPropagation {

    // /////////////////////////////////////////////////////////
    // properties
    // the liveIn definitions for each statement
    Property<Stm.T, FunSet<Stm.T>> liveInPropForStm;
    // the liveIn definitions for each transfer
    Property<Cfg.Transfer.T, FunSet<Stm.T>> liveInPropForTransfer;

    // the constant attached to each statement
    Property<Stm.T, Integer> constPropForStm = new Property<>(Stm::getPlist);
    // the constant attached to each transfer
    Property<Cfg.Transfer.T, Integer> constPropForTransfer = new Property<>(Cfg.Transfer::getPlist);


    // /////////////////////////////////////////////////////////
    //
    static boolean foundNewConst = false;

    // /////////////////////////////////////////////////////////
    // id
    // we may build a use-def chain to speed up
    private Optional<Integer> doitId(Id x, FunSet<Stm.T> liveIn) {
        // filter all the definitions for "x", among all liveIn
        Stream<Stm.T> result = liveIn.toList().stream().filter(
                (s) -> Stm.destroy(s).first().equals(x));
        // calculate all potential constant definitions
        Stream<Optional<Integer>> result2 = result.map((Stm.T s) -> {
            Integer v = constPropForStm.get(s);
            if (v == null) {
                return Optional.empty();
            }
            return Optional.of(v);
        });
        // the unique constant, if any
        List<Optional<Integer>> list = result2.toList();
        Optional<Integer> uniqueValue = Optional.empty();
        for (Optional<Integer> oi : list) {
            if (oi.isEmpty()) {
                return Optional.empty();
            }
            if (uniqueValue.isEmpty()) {
                uniqueValue = oi;
            } else {
                if (!uniqueValue.get().equals(oi.get())) {
                    return Optional.empty();
                }
            }
        }
        return uniqueValue;
    }
    // id

    // /////////////////////////////////////////////////////////
    // op
    private Optional<Integer> doitBinaryOperator(Cfg.BinaryOperator.T op, List<Optional<Integer>> operands) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of op

    // /////////////////////////////////////////////////////////
    // expression
    private Optional<Integer> doitExp(Cfg.Exp.T exp, FunSet<Stm.T> liveIn) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    private void doitStm(Stm.T stm) {
        switch (stm) {
            case Stm.Assign(Label label, Id x, Cfg.Exp.T exp) -> {
                var liveInSets = liveInPropForStm.get(stm);
                Optional<Integer> theConst = doitExp(exp, liveInSets);
                theConst.ifPresent(value ->
                        constPropForStm.getOrInitFun(stm,
                                (s) -> {
                                    foundNewConst = true;
                                    return value;
                                }));
            }
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    private void doitTransfer(Cfg.Transfer.T t) {
        var liveInSets = liveInPropForTransfer.get(t);
        // TODO: please add your code:
        throw new util.Todo();

    }

    // /////////////////////////////////////////////////////////
    // block
    private void doitBlock(Cfg.Block.T b) {
        switch (b) {
            case Cfg.Block.Singleton(
                    Label label,
                    List<Stm.T> stms,
                    Cfg.Transfer.T transfer
            ) -> {
                stms.forEach(this::doitStm);
                doitTransfer(transfer);
            }
        }
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
                    Label entry,
                    Label exit
            ) -> {
                // #1 pass: analysis
                do {
                    foundNewConst = false;
                    blocks.forEach(this::doitBlock);
                } while (foundNewConst);

                // #2 pass: rewrite
//                List<Cfg.Block.T> newBlocks = blocks.stream().map((b) -> {
//                    switch (b) {
//                        case Cfg.Block.Singleton(
//                                Label label,
//                                List<Stm.T> stms,
//                                Cfg.Transfer.T transfer
//                        ) -> {
//                            // rewrite stm
//                            // TODO: please add your code:
////                            throw new util.Todo();
//
//                            // rewrite transfer
//                            // TODO: please add your code:
////                            throw new util.Todo();
//
//                        }
//                    }
//                }).toList();
//                return new Cfg.Function.Singleton(retType,
//                        functionId,
//                        formals,
//                        locals,
//                        newBlocks, entry, exit);
                return null;
            }
        }
    }

    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> {
                var props = new ReachDefinition().doitProgram(prog);
                liveInPropForStm = props.first();
                liveInPropForTransfer = props.second();
                prog = new Cfg.Program.Singleton(
                        functions.stream().map(this::doitFunction).toList());

                // as we modify transfer, we should reshape the cfg.
                prog = new UnreachableBlock().doitProgram(prog);

                return prog;
            }
        }
    }

    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.ConstProp",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program
}

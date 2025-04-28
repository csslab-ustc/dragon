package cfg.lab3;

import cfg.Cfg;
import cfg.lab2.Liveness;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Id;
import util.Label;
import util.Property;
import util.Todo;
import util.set.FunSet;

import java.util.ArrayList;
import java.util.List;

// TODO:
public class DeadCode {
    // /////////////////////////////////////////////////////////
    // properties
    Property<Cfg.Stm.T, FunSet<Id>> liveOutPropForStm;
    private boolean isChanged = false;
    // /////////////////////////////////////////////////////////
    // expression
    private boolean doitExp(Cfg.Exp.T e) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // block
    private Cfg.Block.T doitBlock(Cfg.Block.T b) {
        switch (b) {
            case Cfg.Block.Singleton(
                    Label label,
                    List<Cfg.Stm.T> stms,
                    Cfg.Transfer.T transfer
            ) -> {
                // TODO: please add your code:
                throw new util.Todo();

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
                    Label entryLabel,
                    Label exitLabel
            ) -> {
                var newBlocks = blocks.stream().map(this::doitBlock).toList();
                return new Cfg.Function.Singleton(retType, functionId,
                        formals, locals, newBlocks, entryLabel, exitLabel);
            }
        }
    }

    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        do {
            isChanged = false;
            switch (prog) {
                case Cfg.Program.Singleton(
                        List<Cfg.Function.T> functions
                ) -> {
                    liveOutPropForStm = new Liveness().doitProgram(prog);
                    prog = new Cfg.Program.Singleton(functions.stream().map(this::doitFunction).toList());
                }
            }
        } while (isChanged);
        // dead code again
        prog = new UnreachableBlock().doitProgram(prog);
        return prog;
    }


    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.DeadCode",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }

    @Nested
    class UnitTest{

        @Test
        public void test() {
            var cfg = new Frontend().buildCfg("test/test-deadcode.c");
//            Control.loggedMethodNames.add("cfg.DeadCode");
            Control.tracedMethodNames.add("cfg.DeadCode");
            new DeadCode().doitProgram(cfg);
        }
    }
}

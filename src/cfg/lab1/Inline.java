package cfg.lab1;

import cfg.Cfg;
import cfg.Cfg.Exp;
import cfg.Cfg.Stm;
import control.Control;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Inline {
    // /////////////////////////////////////////////////////////
    //
    // TODO: please add your code:
    // throw new util.Todo();

    // expression
    private void doitExp(Exp.T exp, Stm.T stm) {
        switch (exp){
            // TODO: please add your code:
            default -> throw new util.Todo();

        }
    }
    // end of expression

    // statement
    private void doitStm(Cfg.Stm.T stm) {
        switch (stm) {
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
                    List<Cfg.Stm.T> stms,
                    Cfg.Transfer.T transfer
            ) -> {
                // TODO: please add your code:
                throw new util.Todo();

            }
        }
    }

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
                // TODO: please add your code:
                throw new util.Todo();

            }
        }
    }

    // /////////////////////////////////////////////////////////
    // program
    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.Inline",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program


    // unit test
    @Nested
    public class UnitTest {

        @Test
        public void test() throws Exception {
            Cfg.Program.T cfg = new frontend.Frontend().buildCfg("./test/test-inline.c");
            Control.tracedMethodNames.add("cfg.Inline");
            var newCfg = new Inline().doitProgram(cfg);

        }
    }
}
// end of inline



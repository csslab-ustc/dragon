package cfg.lab2;


import cfg.Cfg;
import control.Control;
import util.*;
import util.set.Set;

import java.util.HashMap;
import java.util.List;

public class AvailExp {

    // record information within a single "map"
    private final HashMap<Object, Tuple.Two<Set<Cfg.Exp.T>, Set<Cfg.Exp.T>>>
            genKillMap;

    // for "block", "transfer", and "statement".
    private final HashMap<Object, Tuple.Two<Set<Cfg.Exp.T>, Set<Cfg.Exp.T>>>
            inOutMap;

    public AvailExp() {
        genKillMap = new HashMap<>();
        inOutMap = new HashMap<>();
    }


    // /////////////////////////////////////////////////////////
    // statement
    private void doitStm(Cfg.Stm.T t) {
        throw new Todo();
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    private void doitTransfer(Cfg.Transfer.T t) {
        throw new Todo();
    }

    // /////////////////////////////////////////////////////////
    // block
    private void doitBlock(Cfg.Block.T b) {
        switch (b) {
            case Cfg.Block.Singleton(Label label,
                                     List<Cfg.Stm.T> stms,
                                     Cfg.Transfer.T transfer) -> {
                throw new Todo();
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // function
    private boolean stillChange = true;

    private void doitFunction(Cfg.Function.T func) {
        switch (func) {
            case Cfg.Function.Singleton(
                    Cfg.Type.T retType,
                    Id functionId,
                    List<Cfg.Dec.T> formals,
                    List<Cfg.Dec.T> locals,
                    List<Cfg.Block.T> blocks,
                    _,
                    _
            ) -> throw new Todo();
        }
    }

    // /////////////////////////////////////////////////////////
    // program
    private HashMap<Object, Tuple.Two<Set<Cfg.Exp.T>, Set<Cfg.Exp.T>>>
    doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> {
                functions.forEach(this::doitFunction);
                return inOutMap;
            }
        }
    }

    public HashMap<Object, Tuple.Two<Set<Cfg.Exp.T>, Set<Cfg.Exp.T>>>
    doitProgram(Cfg.Program.T prog) {
        Control.Trace<Cfg.Program.T, HashMap<Object, Tuple.Two<Set<Cfg.Exp.T>, Set<Cfg.Exp.T>>>> trace
                = new Control.Trace<>("cfg.AvailExp",
                this::doitProgram0,
                prog,
                Cfg.Program::layout,
                (x) -> Layout.str("<NONE>"));
        return trace.doit();
    }

    // /////////////////////////////////////////////////////////
    // unit test

}

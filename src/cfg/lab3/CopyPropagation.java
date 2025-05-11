package cfg.lab3;

import cfg.Cfg;
import control.Control;
import util.Id;
import util.Label;
import util.Todo;

import java.util.List;


// TODO: to fill in code
public class CopyPropagation {


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
            case Cfg.Block.Singleton(
                    Label label,
                    List<Cfg.Stm.T> stms,
                    _
            ) -> throw new Todo();
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
                    _,
                    _
            ) -> throw new Todo();
        }
    }

    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> {
                throw new Todo(prog);
            }
        }
    }

    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.CopyProp",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program

}

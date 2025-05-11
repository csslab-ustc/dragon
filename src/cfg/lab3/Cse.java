package cfg.lab3;

import cfg.Cfg;
import cfg.lab2.AvailExp;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Id;
import util.Label;
import util.Property;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Cse {
    Property<Cfg.Stm.T, Map<Cfg.Exp.T, Set<Label>>>
            inPropForStm;
    boolean stillChange;

    // /////////////////////////////////////////////////////////
    // function
    private void doitFunction(Cfg.Function.T func) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        Control.Trace<Cfg.Program.T, Cfg.Program.T> trace = new Control.Trace<>("cfg.Cse",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program

}

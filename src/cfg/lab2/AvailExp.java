package cfg.lab2;

import cfg.Cfg;
import control.Control;
import util.Id;
import util.Label;
import util.Layout;
import util.Property;

import java.util.*;

public class AvailExp {
    // attach liveIn/liveOut set to each graph node (i.e., block)
    private final Property<Cfg.Block.T, Map<Cfg.Exp.T, Set<Label>>> inPropForBlock =
            new Property<>(Cfg.Block::getPlist);
    private final Property<Cfg.Block.T, Map<Cfg.Exp.T, Set<Label>>> outPropForBlock =
            new Property<>(Cfg.Block::getPlist);
    // attach liveIn set to each statement
    private final Property<Cfg.Stm.T, Map<Cfg.Exp.T, Set<Label>>> inPropForStm =
            new Property<>(Cfg.Stm::getPlist);

    // attach gen/kill set to each statement
    private final Property<Cfg.Stm.T, Map<Cfg.Exp.T, Label>> genPropForStm =
            new Property<>(Cfg.Stm::getPlist);
    private final Property<Cfg.Stm.T, Set<Cfg.Exp.T>> killPropForStm =
            new Property<>(Cfg.Stm::getPlist);

    // TODO: please add your code:
//    throw new util.Todo();


    // /////////////////////////////////////////////////////////
    // function
    private void doitFunction(Cfg.Function.T func) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    // /////////////////////////////////////////////////////////
    // program
    private Property<Cfg.Stm.T, Map<Cfg.Exp.T, Set<Label>>>
    doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> {
                functions.forEach(this::doitFunction);
                return inPropForStm;
            }
        }
    }

    public Property<Cfg.Stm.T, Map<Cfg.Exp.T, Set<Label>>>
    doitProgram(Cfg.Program.T prog) {
        Control.Trace<Cfg.Program.T, Property<Cfg.Stm.T, Map<Cfg.Exp.T, Set<Label>>>> trace
                = new Control.Trace<>("cfg.AvailExp",
                this::doitProgram0,
                prog,
                Cfg.Program::layout,
                (x) -> Layout.str("<NONE>"));
        return trace.doit();
    }
    // end of program
}

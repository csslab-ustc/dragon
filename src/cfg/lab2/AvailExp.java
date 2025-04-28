package cfg.lab2;

import cfg.Cfg;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
    // /////////////////////////////////////////////////////////
    // unit test

    @Nested
    class UnitTest {
        @Test
        public void test() throws Exception {
            Cfg.Program.T cfg = new Frontend().buildCfg("test/test-cse.c");

            Control.Printer.shouldPrintStmLabel = true;
            Cfg.Program.pp(cfg);

            Property<Cfg.Stm.T, Map<Cfg.Exp.T, Set<Label>>> liveInForStms = new AvailExp().doitProgram(cfg);

            ((Cfg.Program.Singleton)cfg).functions().forEach(f -> {
                Cfg.Function.Singleton fs = (Cfg.Function.Singleton) f;
                System.out.println("function: " + fs.id());
                fs.blocks().forEach(b -> {
                    Cfg.Block.Singleton bs = (Cfg.Block.Singleton) b;
                    System.out.println("basic block: " + bs.label());
                    bs.stms().forEach(s -> {
                        Cfg.Stm.Assign assign = (Cfg.Stm.Assign) s;
                        System.out.print(assign.label() + ": ");
                        Cfg.Stm.pp(assign);
                        System.out.print("\nlive in:\n");
                        for (var entry : liveInForStms.get(s).entrySet()) {
                            var layout = Cfg.Exp.layout(entry.getKey());
                            Layout.printDefault(layout);
                            System.out.print(" ");
                            entry.getValue().forEach(v -> System.out.print(v + " "));
                            System.out.println();
                        }
                        System.out.println("---------------------");
                    });
                });
            });
        }
    }
}

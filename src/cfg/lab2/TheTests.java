package cfg.lab2;

import cfg.Cfg;
import cfg.lab4.LivenessLattice;
import cfg.lab4.ZeroAnalysis;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Label;
import util.Layout;
import util.Property;

import java.util.Map;
import java.util.Set;

public class TheTests {

        @Test
        public void test() {
            Cfg.Program.T cfg = new Frontend().buildCfg("test/test-cse.c");

            Control.Printer.shouldPrintStmLabel = true;
            Cfg.Program.pp(cfg);

            Property<Cfg.Stm.T, Map<Cfg.Exp.T, Set<Label>>> liveInForStms = new AvailExp().doitProgram(cfg);

            ((Cfg.Program.Singleton) cfg).functions().forEach(f -> {
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

    @Test
    public void testLiveness() {
//            Control.loggedMethodNames.add("cfg.Liveness");

        var cfg = new Frontend().buildCfg("test/sum-rec.c");
        new Liveness().doitProgram(cfg);
    }

    @org.junit.jupiter.api.Test
    void testLivenessLattice() {
//        Control.loggedMethodNames.addLast("cfg.livenessLattice");
//        Control.tracedMethodNames.addLast("cfg.livenessLattice");
//        Control.Printer.shouldPrintStmLabel = true;

        var cfg = new Frontend().buildCfg("test/reach-def.c");
        var _ = new LivenessLattice().doitProgram(cfg);
    }

    @Test
    public void testReachDef() {
        // comment the following line, if you do NOT need the log.
//        Control.loggedMethodNames.add("cfg.ReachDef");

        var cfg = new Frontend().buildCfg("test/reach-def.c");
        new ReachDefinition().doitProgram(cfg);
    }


}

package cfg.lab4;

import cfg.Cfg;
import frontend.Frontend;

public class TheTests {

    @org.junit.jupiter.api.Test
    void testZeroLattice() {
//        Control.loggedMethodNames.addLast("cfg.ZeroAnalysis");

        Cfg.Program.T prog = new Frontend().buildCfg("test/4-1.c");
        var _ = new ZeroAnalysis().doitProgram(prog);
    }


    @org.junit.jupiter.api.Test
    void testLivenessLattice() {
//        Control.loggedMethodNames.addLast("cfg.livenessLattice");
//        Control.tracedMethodNames.addLast("cfg.livenessLattice");
//        Control.Printer.shouldPrintStmLabel = true;

        var cfg = new Frontend().buildCfg("test/reach-def.c");
        var _ = new LivenessLattice().doitProgram(cfg);
    }


}

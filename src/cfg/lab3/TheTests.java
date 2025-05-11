package cfg.lab3;

import cfg.Cfg;
import cfg.lab2.AvailExp;
import cfg.lab2.Liveness;
import cfg.lab2.ReachDefinition;
import cfg.lab4.LivenessLattice;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Test;
import util.Label;
import util.Layout;
import util.Property;

import java.util.Map;
import java.util.Set;

public class TheTests {
    @Test
    public void testConstPropagation() {
//        Control.tracedMethodNames.addLast("cfg.ConstProp");

        var cfg = new Frontend().buildCfg("test/test-const-prop.c");
        var new_cfg = new ConstPropagation().doitProgram(cfg);
    }

    @Test
    public void testCopyPropagation() {
//        Control.tracedMethodNames.addLast("cfg.CopyProp");

        var cfg = new Frontend().buildCfg("test/test-copy-prop.c");
        var new_cfg = new CopyPropagation().doitProgram(cfg);
    }

    @Test
    public void testCse() {
//        Control.tracedMethodNames.addLast("cfg.Cse");

        Cfg.Program.T cfg = new Frontend().buildCfg("test/test-cse.c");
        var _ = new Cse().doitProgram(cfg);
    }

    @Test
    public void testDeadCode() {
        var cfg = new Frontend().buildCfg("test/test-deadcode.c");
//            Control.loggedMethodNames.add("cfg.DeadCode");
        Control.tracedMethodNames.add("cfg.DeadCode");
        new DeadCode().doitProgram(cfg);
    }

    @Test
    public void testUnreachable() {
//
//        Control.loggedMethodNames.add("cfg.UnreachableBlock");
//        Control.tracedMethodNames.add("cfg.UnreachableBlock");

        var cfg = new Frontend().buildCfg("test/test-deadcode.c");
        new UnreachableBlock().doitProgram(cfg);
    }

}

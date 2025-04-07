package interproc;

import cfg.Cfg;

public class InterProcTest {
    //
    @org.junit.jupiter.api.Test
    public void test() {
        // pretty print
        Cfg.Program.T program1 = SampleProgram1.generate();
        Cfg.Program.pp(program1);
//        new ContextFreeAnalysis().doitProgram(program1);
        Cfg.Program.pp(program1);


    }
}
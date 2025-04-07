package interproc;

import cfg.Cfg;
import util.Id;
import util.Label;

import java.util.List;
import java.util.stream.Stream;

public class SampleProgram1 {
    // we create a sample Cfg:
    public static Cfg.Program.T generate() {

        Id a = Id.newName("a");
        Id b = Id.newName("b");

        Id x = Id.newName("x");
        Id y = Id.newName("y");
        Id z = Id.newName("z");
        Id arg = Id.newName("arg");
        Id result = Id.newName("result");


        Id inc = Id.newName("inc");
        Id main = Id.newName("main");


        // the function "inc"
        Label inc_l0 = new Label();
        Cfg.Block.T inc_b0 = new Cfg.Block.Singleton(inc_l0,
                List.of(new Cfg.Stm.Assign(b, new Cfg.Exp.Int(1)),
                        new Cfg.Stm.Assign(a, new Cfg.Exp.Bop(Cfg.BinaryOperator.T.Mul, List.of(a, b)))),
                new Cfg.Transfer.Ret(a));
        Cfg.Function.T incFunc = new Cfg.Function.Singleton(new Cfg.Type.Int(),
                inc,
                List.of(new Cfg.Dec.Singleton(new Cfg.Type.Int(), a)),
                List.of(new Cfg.Dec.Singleton(new Cfg.Type.Int(), b)),
                        List.of(inc_b0),
                inc_l0,
                inc_l0);

        // the "main" function
        // the basic block "BB0" in "main"
        Label main_l0 = new Label();
        Cfg.Block.T main_bb0 = new Cfg.Block.Singleton(main_l0,
                List.of(new Cfg.Stm.Assign(arg, new Cfg.Exp.Int(17)),
                        new Cfg.Stm.Assign(x, new Cfg.Exp.Call(inc, List.of(arg))),
                        new Cfg.Stm.Assign(arg, new Cfg.Exp.Int(87)),
                        new Cfg.Stm.Assign(y, new Cfg.Exp.Call(inc, List.of(arg))),
                        new Cfg.Stm.Assign(z, new Cfg.Exp.Bop(Cfg.BinaryOperator.T.Add, List.of(x, y)))),
                new Cfg.Transfer.Ret(z));
        
        // the function "main"
        Cfg.Function.T mainFunc = new Cfg.Function.Singleton(new Cfg.Type.Int(),
                main,
                List.of(),
                Stream.of(x, y, z, arg).map((Id localArg) -> (Cfg.Dec.T) new Cfg.Dec.Singleton(new Cfg.Type.Int(), localArg)).toList(),
                List.of(main_bb0),
                main_l0,
                main_l0);

        // the whole program
        Cfg.Program.T program = new Cfg.Program.Singleton(List.of(incFunc, mainFunc));
        return program;
    }
}

package cfg.lab1;

import cfg.Cfg;
import cfg.Cfg.Exp;
import cfg.Cfg.Stm;
import control.Control;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Inline {
    // /////////////////////////////////////////////////////////
    //
    // TODO: please add your code:
    // throw new util.Todo();

    // expression
    private void doitExp(Exp.T exp, Stm.T stm) {
        switch (exp){
            // TODO: please add your code:
            default -> throw new util.Todo();

        }
    }
    // end of expression

    // statement
    private void doitStm(Cfg.Stm.T stm) {
        switch (stm) {
            // TODO: please add your code:
            default -> throw new util.Todo();

        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // block
    private void doitBlock(Cfg.Block.T b) {
        switch (b) {
            case Cfg.Block.Singleton(
                    Label label,
                    List<Cfg.Stm.T> stms,
                    Cfg.Transfer.T transfer
            ) -> {
                // TODO: please add your code:
                throw new util.Todo();

            }
        }
    }

    private Cfg.Function.T doitFunction(Cfg.Function.T func) {
        switch (func) {
            case Cfg.Function.Singleton(
                    Cfg.Type.T retType,
                    Id functionId,
                    List<Cfg.Dec.T> formals,
                    List<Cfg.Dec.T> locals,
                    List<Cfg.Block.T> blocks,
                    Label entryBlock,
                    Label exitBlock
            ) -> {
                // TODO: please add your code:
                throw new util.Todo();

            }
        }
    }

    // /////////////////////////////////////////////////////////
    // program
    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        // TODO: please add your code:
        throw new util.Todo();

    }
    // end of program
    public static void main(String[] args) {
        // a new test case for inline
        Id a = Id.newName("a");
        Id b = Id.newName("b");
        Id c = Id.newName("c");
        Id x = Id.newName("x");
        Id y = Id.newName("y");
        Id z = Id.newName("z");
        Id callee = Id.newName("doitAdd");

        Label l0 = new Label();
        Label l1 = new Label();

        Cfg.Block.T inline_bb0 = new Cfg.Block.Singleton(l0,
                List.of(new Stm.Assign(c,
                        new Exp.Bop(Cfg.BinaryOperator.T.Add,
                                List.of(a, b)))),
                new Cfg.Transfer.Ret(c));
        Cfg.Function.T doitAdd = new Cfg.Function.Singleton(
                new Cfg.Type.Int(), // return type
                callee, // id
                Stream.of(new Cfg.Dec.Singleton(new Cfg.Type.Int(), a),
                        new Cfg.Dec.Singleton(new Cfg.Type.Int(), b)).collect(Collectors.toCollection(ArrayList::new)), // formals
                Stream.of(new Cfg.Dec.Singleton(new Cfg.Type.Int(), c)).collect(Collectors.toCollection(ArrayList::new)), // locals
                List.of(inline_bb0),
                l0,
                l0);

        Cfg.Block.T inline_bb1 = new Cfg.Block.Singleton(l1,
                new ArrayList<>(List.of(new Stm.Assign(x,
                                new Exp.Int(5)),
                        new Stm.Assign(y,
                                new Exp.Int(3)),
                        new Stm.Assign(z,
                                new Exp.Call(callee,
                                        List.of(x, y))),
                        new Stm.Assign(x,
                                new Exp.Int(7)),
                        new Stm.Assign(y,
                                new Exp.Int(8)),
                        new Stm.Assign(z,
                                new Exp.Call(callee,
                                        List.of(x, y))))),
                new Cfg.Transfer.Ret(z));


        Cfg.Function.T doitInline = new Cfg.Function.Singleton(
                new Cfg.Type.Int(), // return type
                Id.newName("doitInline"), // id
                new ArrayList<>(List.<Cfg.Dec.T>of()), // formals
                Stream.of(new Cfg.Dec.Singleton(new Cfg.Type.Int(), x),
                        new Cfg.Dec.Singleton(new Cfg.Type.Int(), y),
                        new Cfg.Dec.Singleton(new Cfg.Type.Int(), z)).collect(Collectors.toCollection(ArrayList::new)), // locals
                List.of(inline_bb1),
                l1,
                l1);
        Cfg.Program.T progInline = new Cfg.Program.Singleton(List.of(doitAdd, doitInline));
        var layout = Cfg.Program.layout(progInline);
        Layout.print(layout, System.out::print, Layout.Style.C);
        Inline inline = new Inline();
        var prog = inline.doitProgram(progInline);
        layout = Cfg.Program.layout(prog);
        Layout.print(layout, System.out::print, Layout.Style.C);
    }
}
// end of inline



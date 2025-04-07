package cfg.lab1;

import ast.Ast;
import cfg.Cfg;
import control.Control;
import util.*;

import java.util.*;

public class Translate {
    /**
     * FunctionContext is a helpful class to maintain function's data, including formals,
     * locals and statements, etc.
     */
    // TODO: 这个类的实现，可以维护当前的基本块
    private static class FunctionContext {
        // We use currentBBLabel to record the basic block we are processing.
        // The first created label in FunctionContext will be considered the entry basic block label,
        // and the final created label will be considered the exit basic block label.
        private Label currentBBLabel;

        // formals and locals are used to record the parameters of a function and the variables
        // defined within the function, respectively. When converting AST to CFG, some intermediate
        // variables will be created and added to locals, see emitLocalDec method.
        private final List<Cfg.Dec.T> formals = new ArrayList<>();
        private final List<Cfg.Dec.T> locals = new ArrayList<>();

        private final List<Cfg.Block.T> blocks = new ArrayList<>();
        private List<Cfg.Stm.T> stms;

        public FunctionContext() {
            currentBBLabel = newBBLabel();
            stms = new ArrayList<>();
        }

        public Label getCurrentBBLabel() {
            return currentBBLabel;
        }

        public List<Cfg.Dec.T> getFormals() {
            return formals;
        }

        public List<Cfg.Dec.T> getLocals() {
            return locals;
        }

        public List<Cfg.Block.T> getBlocks() {
            return blocks;
        }

        public void emitFormalDec(Cfg.Dec.T formal) {
            formals.add(formal);
        }

        public void emitLocalDec(Cfg.Dec.T dec) {
            locals.add(dec);
        }

        public void emitStmt(Cfg.Stm.T stm) {
            stms.add(stm);
        }

        public Label newBBLabel() {
            return new Label();
        }

        // emitBlock creates a new basic block using the transfer and the statements
        // emitted after last call of emitBlock, and switch to a new basic block
        // context represented by nextBlockLabel.
        public void emitBlock(Cfg.Transfer.T transfer, Label nextBlockLabel) {
            blocks.add(new Cfg.Block.Singleton(currentBBLabel, stms, transfer));
            currentBBLabel = nextBlockLabel;
            stms = new ArrayList<>();
        }
    }

    private Cfg.Type.T translateType(Ast.Type.T type) {
        return switch (type) {
            case Ast.Type.Int() -> new Cfg.Type.Int();
        };
    }

    private Cfg.Type.T getVariableType(FunctionContext ctx, Id id) {
        for (Cfg.Dec.T dec : ctx.getFormals()) {
            Cfg.Dec.Singleton ds = (Cfg.Dec.Singleton) dec;
            if (ds.id().equals(id)) return ds.type();
        }

        for (Cfg.Dec.T dec : ctx.getLocals()) {
            Cfg.Dec.Singleton ds = (Cfg.Dec.Singleton) dec;
            if (ds.id().equals(id)) return ds.type();
        }
        throw new IllegalStateException("Not found variable type");
    }

    private Cfg.Type.T getBopResultType(FunctionContext ctx, Id leftId, Id rightId) {
        // TODO: We need to take into account implicit conversions if we want
        //  to support multiple types.
        return getVariableType(ctx, leftId);
    }

    private Cfg.BinaryOperator.T translateBop(Ast.BinaryOperator.T bop) {
        return switch (bop) {
            case Add -> Cfg.BinaryOperator.T.Add;
            case Sub -> Cfg.BinaryOperator.T.Sub;
            case Mul -> Cfg.BinaryOperator.T.Mul;
            case Div -> Cfg.BinaryOperator.T.Div;
            case Lt -> Cfg.BinaryOperator.T.Lt;
            case Le -> Cfg.BinaryOperator.T.Le;
            case Gt -> Cfg.BinaryOperator.T.Gt;
            case Ge -> Cfg.BinaryOperator.T.Ge;
            case Eq -> Cfg.BinaryOperator.T.Eq;
            case Ne -> Cfg.BinaryOperator.T.Ne;
        };
    }

    /**
     * translateExpr translate Ast.Exp to Cfg.Exp, and return an intermediate variable that
     * records expression's result.
     * This function might create a lot of assign statements, such as for statement a = 100,
     * it will create two assign statements: %x_1 = 100, a = %x_1. but it doesn't matter,
     * because we can easily optimize it later.
     */
    private Id translateExpr(FunctionContext ctx, Ast.Exp.T e) {
        switch (e) {
            case Ast.Exp.ExpId(Ast.AstId id) -> {
                return Id.newName(id.id.toString());
            }
            case Ast.Exp.Num(int num) -> {
                Id resultId = Id.newNoname();
                ctx.emitStmt(new Cfg.Stm.Assign(resultId, new Cfg.Exp.Int(num)));
                ctx.emitLocalDec(new Cfg.Dec.Singleton(new Cfg.Type.Int(), resultId));
                return resultId;
            }
            case Ast.Exp.Call(Ast.AstId funId, List<Ast.Exp.T> args, Ast.Type.T retType) -> {
                // Here, you should translate the Call expression to its relevant CFG form.
                // For examples, foo(x + y) could be translated to
                // %x_1 = x + y;
                // %x_2 = foo(%x_1)
                // the %x_1 and %x_2 are local variables, you can learn from other code how to
                // create them and emit them. The translation is not unique, you can write code
                // any way you like.
                // TODO: please add your code:
                throw new util.Todo();

            }
            case Ast.Exp.Bop(Ast.Exp.T left, Ast.BinaryOperator.T bop, Ast.Exp.T right) -> {
                // TODO: please add your code:
                throw new util.Todo();

            }
        }
    }

    private void translateStm(FunctionContext ctx, Ast.Stm.T stm) {
        switch (stm) {
            case Ast.Stm.Assign(Ast.AstId aid, Ast.Exp.T exp) -> {
                Id resutlId = translateExpr(ctx, exp);
                ctx.emitStmt(new Cfg.Stm.Assign(aid.id, new Cfg.Exp.Eid(resutlId)));
            }
            case Ast.Stm.Block(List<Ast.Stm.T> stms) -> {
                stms.forEach(s -> translateStm(ctx, s));
            }
            case Ast.Stm.If(Ast.Exp.T cond, Ast.Stm.T thenn, Ast.Stm.T elsee) -> {
                Label thenLabel = ctx.newBBLabel(), elseLabel = ctx.newBBLabel(), exitLabel = ctx.newBBLabel();

                Id condId = translateExpr(ctx, cond);
                ctx.emitBlock(new Cfg.Transfer.If(condId, thenLabel, elseLabel), thenLabel);

                // process then branch
                translateStm(ctx, thenn);
                // Notice: due to if statement may be nested, the Jmp statement may emit to
                // another basic block, instead of thenLabel.
                ctx.emitBlock(new Cfg.Transfer.Jmp(exitLabel), elseLabel);

                // process else branch;
                translateStm(ctx, elsee);
                ctx.emitBlock(new Cfg.Transfer.Jmp(exitLabel), exitLabel);
            }
            case Ast.Stm.While(Ast.Exp.T cond, Ast.Stm.T body) -> {
                // Here, you should translate While statement to its relevant CFG form. You should
                // create some new basic blocks and emit the instructions into them using the methods
                // provided by FunctionContext class. Feel free to use other given methods, you don't
                // need to redefine your own.
                // The translation of While statement is very similar to If, you can refer to it.
                // TODO: please add your code:
                throw new util.Todo();

            }
            case Ast.Stm.Print(Ast.Exp.T exp) -> {
                Id x = translateExpr(ctx, exp);
                Id tempId = Id.newNoname();
                ctx.emitLocalDec(new Cfg.Dec.Singleton(new Cfg.Type.Int(), tempId));
                ctx.emitStmt(new Cfg.Stm.Assign(tempId, new Cfg.Exp.Print(x)));
            }
        }
    }

    private Cfg.Dec.T translateDec(Ast.Dec.T dec) {
        return switch (dec) {
            case Ast.Dec.Singleton(
                    Ast.Type.T type, Ast.AstId aid
            ) -> new Cfg.Dec.Singleton(translateType(type), aid.id);
        };
    }

    private Cfg.Function.T translateFunc(Ast.Function.T func) {
        switch (func) {
            case Ast.Function.Singleton(
                    Ast.Type.T retType, Ast.AstId methodId, List<Ast.Dec.T> formals, List<Ast.Dec.T> locals,
                    List<Ast.Stm.T> stms, Ast.Exp.T retExp
            ) -> {
                FunctionContext ctx = new FunctionContext();
                Label entryBBLabel = ctx.getCurrentBBLabel();

                formals.forEach(x -> ctx.emitFormalDec(translateDec(x)));
                locals.forEach(x -> ctx.emitLocalDec(translateDec(x)));

                stms.forEach(s -> translateStm(ctx, s));

                Id retId = translateExpr(ctx, retExp);
                Label exitBBLabel = ctx.getCurrentBBLabel();
                ctx.emitBlock(new Cfg.Transfer.Ret(retId), null);

                return new Cfg.Function.Singleton(translateType(retType), methodId.id, ctx.getFormals(),
                        ctx.getLocals(), ctx.getBlocks(), entryBBLabel, exitBBLabel);
            }
        }
    }

    private Cfg.Program.T doitProgram0(Ast.Program.T ast) {
        switch (ast) {
            case Ast.Program.Singleton(List<Ast.Function.T> functions) -> {
                return new Cfg.Program.Singleton(functions.stream().map(this::translateFunc).toList());
            }
        }
    }

    // given an abstract syntax tree, lower it down
    // to a corresponding control-flow graph.
    public Cfg.Program.T doitProgram(Ast.Program.T ast) {
        var trace = new Control.Trace<>("ast.Translate",
                this::doitProgram0,
                ast,
                Ast.Program::layout,
                Cfg.Program::layout);
        return trace.doit();
    }
}

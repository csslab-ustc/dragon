package frontend;

import ast.Ast.*;
import frontend.minic.MiniCParser.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;
import util.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * ASTTranslator translates Antlr parse tree to our Abstract Syntax Tree
 */
public class ASTTranslator {
    private Exp.T translateBop(ExprContext left, ExprContext right, Token op) {
        Exp.T leftExp = translateExpr(left);
        Exp.T rightExp = translateExpr(right);

        BinaryOperator.T bop = switch (op.getText()) {
            case "*" -> BinaryOperator.T.Mul;
            case "/" -> BinaryOperator.T.Div;
            case "+" -> BinaryOperator.T.Add;
            case "-" -> BinaryOperator.T.Sub;
            case "<" -> BinaryOperator.T.Lt;
            case "<=" -> BinaryOperator.T.Le;
            case ">" -> BinaryOperator.T.Gt;
            case ">=" -> BinaryOperator.T.Ge;
            case "==" -> BinaryOperator.T.Eq;
            case "!=" -> BinaryOperator.T.Ne;
            default -> throw new IllegalStateException("Unexpected value: " + op.getText());
        };
        return new Exp.Bop(leftExp, bop, rightExp);
    }

    private Exp.T translateExpr(ExprContext ctx) {
        return switch (ctx) {
            case ExpIDContext exp -> new Exp.ExpId(new AstId(Id.newName(exp.ID().getText())));
            case ExpIntContext exp -> new Exp.Num(Integer.parseInt(exp.getText()));
            case ExpCallContext exp -> {
                AstId funcId = new AstId(Id.newName(exp.ID().getText()));
                List<Exp.T> args = exp.expr().stream().map(this::translateExpr).toList();

                yield new Exp.Call(funcId, args, new Type.Int());
            }
            case ExpMulOrDivContext exp -> translateBop(exp.left, exp.right, exp.op);
            case ExpAddOrSubContext exp -> translateBop(exp.left, exp.right, exp.op);
            case ExpLeOrGeContext exp -> translateBop(exp.left, exp.right, exp.op);
            case ExpEqOrNeContext exp -> translateBop(exp.left, exp.right, exp.op);
            case ExpParenContext exp -> translateExpr(exp.expr());
            default -> throw new IllegalStateException("Unexpected value: " + ctx);
        };
    }

    private Stm.T translateStmComp(StmCompContext ctx) {
        return new Stm.Block(ctx.statement().stream().map(this::translateStmt).toList());
    }

    private Stm.T translateStmIf(StmIfContext ctx) {
        Exp.T cond = translateExpr(ctx.cond);
        Stm.T then = translateStmt(ctx.then);
        Stm.T else_ = translateStmt(ctx.else_);
        return new Stm.If(cond, then, else_);
    }

    private Stm.T translateStmWhile(StmWhileContext ctx) {
        Exp.T cond = translateExpr(ctx.cond);
        Stm.T body = translateStmt(ctx.body);
        return new Stm.While(cond, body);
    }

    private Stm.T translateAssign(StmAssignContext ctx) {
        AstId astId = new AstId(Id.newName(ctx.ID().getText()));
        Exp.T exp = translateExpr(ctx.expr());

        return new Stm.Assign(astId, exp);
    }

    private Stm.T translateStmt(StatementContext ctx) {
        return switch (ctx) {
            case StmCompContext stm -> translateStmComp(stm);
            case StmIfContext stm -> translateStmIf(stm);
            case StmWhileContext stm -> translateStmWhile(stm);
            case StmAssignContext stm -> translateAssign(stm);
            default -> throw new IllegalStateException("Unexpected value: " + ctx);
        };
    }

    private Type.T translateType(TypeContext ctx) {
        return switch (ctx.getText()) {
            case "int" -> new Type.Int();
            default -> throw new IllegalStateException("illegal type");
        };
    }

    private List<Dec.T> translateParams(ParamsContext ctx) {
        List<Dec.T> decList = new ArrayList<>();
        for (ParamContext param : ctx.param()) {
            AstId astId = new AstId(Id.newName(param.ID().getText()));
            Dec.T dec = new Dec.Singleton(translateType(param.type()), astId);
            decList.add(dec);
        }
        return decList;
    }

    private List<Dec.T> translateVarDecl(List<VarDeclContext> varDeclContexts) {
        List<Dec.T> varDecl = new ArrayList<>();
        for (VarDeclContext ctx : varDeclContexts) {
            for (TerminalNode id : ctx.ID()) {
                AstId astId = new AstId(Id.newName(id.getText()));
                Dec.T decl = new Dec.Singleton(translateType(ctx.type()), astId);
                varDecl.add(decl);
            }
        }
        return varDecl;
    }

    private Function.T translateFuncDecl(FuncDeclContext ctx) {
        Type.T retType = translateType(ctx.type());
        AstId funcId = new AstId(Id.newName(ctx.ID().getText()));
        List<Dec.T> formals = translateParams(ctx.params());
        List<Dec.T> locals = translateVarDecl(ctx.funcBody().varDecl());
        List<Stm.T> stms = ctx.funcBody().statement().stream().map(this::translateStmt).toList();
        Exp.T retExp = translateExpr(ctx.funcBody().returnStm().expr());

        return new Function.Singleton(retType, funcId, formals, locals, stms, retExp);
    }

    private Program.Singleton translate(ProgRootContext ctx) {
        List<Function.T> func = ctx.funcDecl().stream().map(this::translateFuncDecl).toList();
        return new Program.Singleton(func);
    }

    /**
     * translate method is the entry method that translate Antlr parse tree
     * to our Abstract Syntax Tree
     *
     * @param ctx - The root node of Antlr parse tree
     * @return Program's Abstract Syntax Tree
     */
    public Program.T translate(ProgContext ctx) {
        return translate((ProgRootContext) ctx);
    }
}

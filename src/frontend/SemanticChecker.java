package frontend;

import frontend.minic.MiniCParser.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * SemanticChecker performs semantic check on Antlr parse tree
 */
public class SemanticChecker {

    private boolean passed = true;

    /**
     * check is the entry method that performs semantic check on Antlr parse tree.
     *
     * @param ctx - The root node of Antlr parse tree
     * @return Returns true if the semantic check passed, otherwise returns false
     */
    public boolean check(ProgContext ctx) {
        check((ProgRootContext) ctx);
        return passed;
    }

    /**
     * FuncSignature records the function declaration.
     */
    private record FuncSignature(String retType, List<String> params) {
    }

    /**
     * funcDeclRecord records all function declarations in program.
     */
    private final Map<String, FuncSignature> funcSignature = new HashMap<>();

    /**
     * currentFunction records the function name of current function.
     */
    private String currentFunction;

    /**
     * funcVarDecl maps all variables defined in the function including formals
     * and locals to its type string. It will be reset before each function check.
     */
    private Map<String, String> funcVarDecl;

    private void report(Token token, String msg) {
        System.out.printf("Error at line %d: %s\n", token.getLine(), msg);
        passed = false;
    }

    private void check(ProgRootContext ctx) {
        ctx.funcDecl().forEach(this::buildFuncDecl);
        ctx.funcDecl().forEach(this::checkFuncDecl);
    }

    private void buildFuncDecl(FuncDeclContext ctx) {
        String funcName = ctx.ID().getText();
        FuncSignature signature = new FuncSignature(ctx.type().getText(), new ArrayList<>());
        for (ParamContext param : ctx.params().param()) {
            signature.params.add(param.type().getText());
        }
        funcSignature.put(funcName, signature);
    }

    private void checkFuncDecl(FuncDeclContext ctx) {
        currentFunction = ctx.ID().getText();
        // check the definition of formals and locals
        funcVarDecl = new HashMap<>();
        checkParams(ctx.params());
        ctx.funcBody().varDecl().forEach(this::checkVarDecl);

        // check function body
        ctx.funcBody().statement().forEach(this::checkStmt);
        checkReturnStm(ctx.funcBody().returnStm());
    }

    private void checkType(TypeContext ctx) {
        switch (ctx.getText()) {
            default:
                report(ctx.getStart(), String.format("Unknown type '%s'", ctx.getText()));
                break;
            case "int":
        }
    }

    private void checkParams(ParamsContext ctx) {
        for (ParamContext param : ctx.param()) {
            checkType(param.type());
            // check variable redefine
            if (funcVarDecl.containsKey(param.ID().getText()))
                report(param.ID().getSymbol(), String.format("'%s' is redefined", param.ID().getText()));
            funcVarDecl.put(param.ID().getText(), param.type().getText());
        }
    }

    private void checkVarDecl(VarDeclContext ctx) {
        checkType(ctx.type());
        for (TerminalNode var : ctx.ID()) {
            if (funcVarDecl.containsKey(var.getText()))
                report(var.getSymbol(), String.format("'%s' is redefined", var.getText()));
            funcVarDecl.put(var.getText(), ctx.type().getText());
        }
    }

    private void checkStmt(StatementContext ctx) {
        switch (ctx) {
            case StmCompContext stm -> checkStmComp(stm);
            case StmIfContext stm -> checkStmIf(stm);
            case StmWhileContext stm -> checkStmWhile(stm);
            case StmAssignContext stm -> checkStmAssign(stm);
            default -> throw new IllegalStateException("Unexpected value: " + ctx);
        }
    }

    private void checkReturnStm(ReturnStmContext ctx) {
        String retType = checkExpr(ctx.expr());
        String funcRetType = funcSignature.get(currentFunction).retType;
        if (!retType.equals(funcRetType)) report(ctx.getStart(), "return type mismatch");
    }

    private void checkStmComp(StmCompContext ctx) {
        ctx.statement().forEach(this::checkStmt);
    }

    private void checkStmIf(StmIfContext ctx) {
        // TODO: we should to check that the result type of conditional expression
        //  is an acceptable type for If's condition if we want to support multiple types.
        checkExpr(ctx.cond);
        checkStmt(ctx.then);
        checkStmt(ctx.else_);
    }

    private void checkStmWhile(StmWhileContext ctx) {
        checkExpr(ctx.cond);
        checkStmt(ctx.body);
    }

    private void checkStmAssign(StmAssignContext ctx) {
        String rType = checkExpr(ctx.expr());
        String lType = funcVarDecl.get(ctx.ID().getText());
        if (!rType.equals(lType)) report(ctx.ID().getSymbol(), "Assignment type mismatch");
    }

    private String checkExpr(ExprContext ctx) {
        switch (ctx) {
            case ExpIDContext exp -> {
                String typ = funcVarDecl.get(exp.ID().getText());
                if (typ == null) {
                    report(exp.start, String.format("'%s' used but not defined", exp.ID().getText()));
                    return "int"; // return a fake type for following check
                }
                return typ;
            }
            case ExpIntContext _ -> {
                return "int";
            }
            case ExpCallContext exp -> {
                String funcName = exp.ID().getText();
                FuncSignature signature = funcSignature.get(funcName);
                if (signature == null) {
                    report(exp.start, "Undefined function " + funcName);
                    return "int"; // return a fake type
                }

                if (exp.expr().size() != signature.params().size()) {
                    report(exp.start, "Mismatched number of parameters");
                    return signature.retType;
                }

                for (int i = 0; i < exp.expr().size(); i++) {
                    ExprContext arg = exp.expr(i);
                    String paramType = signature.params.get(i);
                    if (!checkExpr(arg).equals(paramType)) report(arg.start, "Parameter type mismatch");
                }
                return signature.retType;
            }
            case ExpMulOrDivContext exp -> {
                return checkBop(exp.left, exp.right, exp.op);
            }
            case ExpAddOrSubContext exp -> {
                return checkBop(exp.left, exp.right, exp.op);
            }
            case ExpLeOrGeContext exp -> {
                return checkBop(exp.left, exp.right, exp.op);
            }
            case ExpEqOrNeContext exp -> {
                return checkBop(exp.left, exp.right, exp.op);
            }
            case ExpParenContext exp -> {
                return checkExpr(exp.expr());
            }
            default -> throw new IllegalStateException("Unexpected value: " + ctx);
        }
    }

    private String checkBop(ExprContext left, ExprContext right, Token op) {
        String lType = checkExpr(left);
        String rType = checkExpr(right);

        // TODO: We need to take into account implicit conversions if we want
        //  to support multiple types. But now we only consider no implicit conversions.
        if (!lType.equals(rType)) report(op, "Operand type mismatch");
        return lType;
    }
}

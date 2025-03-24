package frontend;

import frontend.minic.MiniCParser.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class AntlrTreePrinter {
    private int indent = 0;

    private void doIndentPrint(String... text) {
        for (int i = 0; i < indent; i++)
            System.out.print("  ");
        doPrint(text);
    }

    private void doPrint(String... text) {
        System.out.print(String.join(" ", text));
    }

    private void incIndent() {
        indent++;
    }

    private void decIndent() {
        indent--;
    }

    public void print(ProgContext ctx) {
        printProgRoot((ProgRootContext) ctx);
    }

    private void printProgRoot(ProgRootContext ctx) {
        ctx.funcDecl().forEach(this::printFuncDecl);
    }

    private void printVarDecl(VarDeclContext ctx) {
        List<String> vars = ctx.ID().stream().map(ParseTree::getText).toList();
        doIndentPrint(String.format("%s %s;\n", ctx.type().getText(), String.join(", ", vars)));
    }

    private void printFuncDecl(FuncDeclContext ctx) {
        String returnType = ctx.type().getText();
        String funcName = ctx.ID().getText();

        List<String> paramList = ctx.params().param().stream().map(param -> String.format("%s %s", param.type().getText(), param.ID())).toList();
        String params = String.join(", ", paramList);

        doIndentPrint(String.format("%s %s(%s)\n{\n", returnType, funcName, params));
        incIndent();

        printFuncBody(ctx.funcBody());
        decIndent();
        doIndentPrint("}\n\n");
    }

    private void printFuncBody(FuncBodyContext ctx) {
        ctx.varDecl().forEach(this::printVarDecl);
        doPrint("\n");
        ctx.statement().forEach(this::printStatement);
        printReturnStm(ctx.returnStm());
    }

    private void printStatement(StatementContext ctx) {
        switch (ctx) {
            case StmCompContext stm -> {
                // we decrease indent before to print {.
                decIndent();
                doIndentPrint("{\n");
                incIndent();

                for (StatementContext s : stm.statement())
                    printStatement(s);

                decIndent();
                doIndentPrint("}\n");
                incIndent();
            }
            case StmIfContext stm -> {
                doIndentPrint("if", "(");
                printExpr(stm.cond);
                doPrint(")\n");

                incIndent();
                printStatement(stm.then);
                decIndent();

                doIndentPrint("else\n");
                incIndent();
                printStatement(stm.else_);
                decIndent();
            }
            case StmWhileContext stm -> {
                doIndentPrint("while (");
                printExpr(stm.cond);
                doPrint(")\n");

                incIndent();
                printStatement(stm.body);
                decIndent();
            }
            case StmAssignContext stm -> {
                doIndentPrint(stm.ID().getText(), "= ");
                printExpr(stm.expr());
                doPrint(";\n");
            }
            default -> throw new IllegalStateException("Unexpected value: " + ctx);
        }
    }

    private void printReturnStm(ReturnStmContext ctx) {
        doIndentPrint("return ");
        printExpr(ctx.expr());
        doPrint(";\n");
    }

    private void printExpr(ExprContext ctx) {
        if (ctx == null) return;

        switch (ctx) {
            case ExpIDContext exp -> {
                doPrint(exp.getText());
            }
            case ExpIntContext exp -> {
                doPrint(exp.getText());
            }
            case ExpCallContext exp -> {
                doPrint(exp.ID().getText(), "(");
                for (int i = 0; i < exp.expr().size(); i++) {
                    if (i > 0) doPrint(", ");
                    printExpr(exp.expr(i));
                }
                doPrint(")");
            }
            case ExpMulOrDivContext exp -> {
                printExpr(exp.left);
                doPrint(String.format(" %s ", exp.op.getText()));
                printExpr(exp.right);
            }
            case ExpAddOrSubContext exp -> {
                printExpr(exp.left);
                doPrint(String.format(" %s ", exp.op.getText()));
                printExpr(exp.right);
            }
            case ExpLeOrGeContext exp -> {
                printExpr(exp.left);
                doPrint(String.format(" %s ", exp.op.getText()));
                printExpr(exp.right);
            }
            case ExpEqOrNeContext exp -> {
                printExpr(exp.left);
                doPrint(String.format(" %s ", exp.op.getText()));
                printExpr(exp.right);
            }
            case ExpParenContext exp -> {
                doPrint("(");
                printExpr(exp.expr());
                doPrint(")");
            }
            default -> throw new IllegalStateException("Unexpected value: " + ctx);
        }
    }
}

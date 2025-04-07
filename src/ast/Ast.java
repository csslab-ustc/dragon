package ast;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Id;
import util.Layout;
import util.Tuple;

import java.util.List;

public class Ast {
    // /////////////////////////////////////////////////////////
    // ast-id
    // we use class instead of record, as we need to change these
    // fields
    public static class AstId {
        public Id id;
        public Id freshId;
        public Type.T type;

        public AstId(Id id) {
            this.id = id;
            // set the initial value to "id"
            this.freshId = id;
            this.type = null;
        }

        public Id genFreshId() {
            this.freshId = this.id.newSameOrigName();
            return this.freshId;
        }

        public Layout.T layout(){
            return Layout.str(this.id.toString());
        }
    }

    //  ///////////////////////////////////////////////////////////
    //  type
    public static class Type {
        public sealed interface T
                permits Int {
        }

        // int
        public record Int() implements T {
        }

        // operations
        public static Layout.T layout(T ty){
            switch (ty){
                case Int() -> {
                    return Layout.str("int");
                }
            }
        }
    }
    // end of type

    // ///////////////////////////////////////////////////
    // declaration
    public static class Dec {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Type.T type,
                                AstId aid) implements T {
        }

        public static Type.T getType(T dec) {
            switch (dec) {
                case Singleton(Type.T type, _) -> {
                    return type;
                }
            }
        }

        /* operations */
        public static Layout.T layout(T dec){
            switch (dec){
                case Singleton(Type.T type, AstId aid) ->{
                    return Layout.halignVararg(Type.layout(type), Layout.str(" "), aid.layout());
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // binary operators
    public static class BinaryOperator {
        public enum T {
            /* data structures */
            // integer operators
            Add("+"),
            Sub("-"),
            Mul("*"),
            Div("/"),
            // boolean operators
            Lt("<"),
            Le("<="),
            Gt(">"),
            Ge(">="),
            Eq("=="),
            Ne("!="),
            ;

            private final String name;

            T(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return this.name;
            }
        }

        public static Layout.T layout(T x) {
                return Layout.str(x.toString());
            }
    }

    // /////////////////////////////////////////////////////////
    // expression
    public static class Exp {
        /* data structures */
        // the record names should be alphabetically-ordered
        public sealed interface T
                permits Bop, Call, ExpId, Num {
        }

        // binary operations
        public record Bop(T left,
                          BinaryOperator.T bop,
                          T right) implements T {
        }

        // Call
        public record Call(AstId funId,
                           List<T> args,
                           Type.T retType) implements T {
        }

        // ExpId
        public record ExpId(AstId id) implements T {
        }

        // integer literals
        public record Num(int num) implements T {
        }

        /* operations */
        public static Layout.T layout(T e) {
            switch (e) {
                case Bop(T left, BinaryOperator.T bop, T right) -> {
                    return Layout.halignVararg(layout(left),
                            BinaryOperator.layout(bop),
                            layout(right));
                }
                case Call(AstId funId, List<T> args, Type.T retType) -> {
                    return Layout.halignVararg(Layout.str(funId.id.toString()),
                            Layout.str("("),
                            Layout.halign(args.stream().map(Exp::layout).toList()),
                            Layout.str(")"));
                }
                case ExpId(AstId id) -> {
                    return Layout.str(id.id.toString());
                }
                case Num(int num) -> {
                    return Layout.str(Integer.toString(num));
                }
                default -> {
                    throw new Error(e.toString());
                }
            }

        }

    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        // alphabetically-ordered
        public sealed interface T
                permits Assign, Block, If, Print, While {
        }

        // assign: id = exp;
        public record Assign(AstId aid,
                             Exp.T exp) implements T {
        }

        // block
        public record Block(List<T> stms) implements T {
        }

        // if
        public record If(Exp.T cond,
                         T thenn,
                         T elsee) implements T {
        }

        // System.out.println
        public record Print(Exp.T exp) implements T {
        }

        // while
        public record While(Exp.T cond,
                            T body) implements T {
        }

        /* operations */
        public static Layout.T layout(T s) {
            switch (s) {
                case Assign(AstId aid,
                            Exp.T exp) -> {
                    return Layout.halignVararg(Layout.str(aid.id.toString()),
                            Layout.str(" = "),
                            Exp.layout(exp),
                            Layout.str(";"));
                }
                case If(Exp.T cond,
                        T thenn,
                        T elsee) -> {
                    return Layout.valignVararg(Layout.halignVararg(Layout.str("if("),
                                    Exp.layout(cond),
                                    Layout.str(") {")),
                            Layout.indent(layout(thenn)),
                            Layout.str("} else {"),
                            Layout.indent(layout(elsee)),
                            Layout.str("}"));
                }
                default -> {
                    throw new Error(s.toString());
                }
            }
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // function
    public static class Function {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Type.T retType,
                                AstId methodId,
                                List<Dec.T> formals,
                                List<Dec.T> locals,
                                List<Stm.T> stms,
                                Exp.T retExp) implements T {
        }

        /* operations */
        public static Layout.T layout(T func) {
            switch (func) {
                case Singleton(
                        Type.T retType,
                        AstId methodId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Stm.T> stms,
                        Exp.T retExp
                ) -> {
                    return Layout.valignVararg(Layout.halignVararg(
                            Type.layout(retType),
                            Layout.str(" "),
                            Layout.str(methodId.id.toString()),
                                    Layout.str("("),
                                    Layout.halignSepRight(Layout.str(","), formals.stream().map(Dec::layout).toList()),
                                    Layout.str(") {")),
                            Layout.indent(Layout.valignSepRight(Layout.str(";"),
                                    locals.stream().map(Dec::layout).toList())),
                            Layout.indent(Layout.valign(stms.stream().map(Stm::layout).toList())),
                            Layout.indent(Layout.halignVararg(Layout.str("return "),
                                    Exp.layout(retExp),
                                    Layout.str(";"))),
                            Layout.str("}"));
                }
            }
        }
    }

    // whole program
    public static class Program {
        /* data structures */
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(List<Function.T> functions) implements T {
        }

        /* operations */
        public static Layout.T layout(T prog) {
            switch (prog) {
                case Singleton(List<Function.T> functions) -> {
                    return Layout.valignVararg(Layout.valign(functions.stream().map(Function::layout).toList()),
                            Layout.str(""));
                }
            }
        }

        public static void pp(T prog){
            Layout.print(layout(prog),
                    System.out::print,
                    Layout.Style.C);
        }
    }
    // end of program

    @Nested
    class UnitTest{
        @Test
        public void test() {
            // to test the pretty printer
            Layout.T layout = Ast.Program.layout(SamplePrograms.progSumRec);
            Layout.print(layout, System.out::print, Layout.Style.C);
            Layout.print(Ast.Program.layout(SamplePrograms.progFac), System.out::print, Layout.Style.C);
        }
    }
}








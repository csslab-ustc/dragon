package control;

import ast.Ast;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Control {

    // verbose
    public enum Verbose {
        L_1(-1),
        L0(0), // top level
        L1(1),
        L2(2);
        public final int order;

        Verbose(int i) {
            this.order = i;
        }
    }
    public static Verbose verbose = Verbose.L_1;

    // trace
    public static List<String> tracedMethodNames = new LinkedList<>();
    public static boolean isBeingTraced(String method) {
        return tracedMethodNames.contains(method);
    }

    // this is a special hack to test the compiler
    // without hacking the lexer and parser.
    public static Ast.Program.T bultinAst = null;

    // dot-related
    public static class Dot {
        public static boolean keep = true;
        public static String format = "png";
        public static List<String> irs = new LinkedList<>();
        public static boolean beingDotted(String ir) {
            return irs.contains(ir);
        }
    }

    // pretty printer
    // the default is the standard out
    public static class Printer{
        public enum Style {
            C,
            Dot,
        }

        public static Style style = Style.C;
        public static Consumer<String> println = System.out::println;
        public static Consumer<String> print = System.out::print;
        // for dot
        public static String newLine = style==Style.C? "": "\\l";

        public static boolean shouldPrintStmLabel = false;
    }

    // utils
    public static class Util {
        public static boolean dumpId = false;
    }

    // the frontend
    public static class Frontend {
        public static boolean dumpToken = false;
    }

    // the type checker
    public static class Type {
    }

    // the CFG
    public static class Cfg {
        public static boolean embedComment = false;
    }

}


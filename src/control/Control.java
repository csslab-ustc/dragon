package control;

import ast.Ast;
import util.Layout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    // log
    public static List<String> loggedMethodNames = new LinkedList<>();
    public static boolean isBeingLogged(String method) {
        return loggedMethodNames.contains(method);
    }

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
        public static Consumer<String> printer = null;
        public static boolean shouldPrintStmLabel = false;
    }
    public static void log(String message) {
        if(Printer.printer != null)
            Printer.printer.accept(message);
    }
    public static void logln(String message) {
        if(Printer.printer != null)
            Printer.printer.accept(message + "\n");
    }
    public static void log(Layout.T layout, Layout.Style style) {
        if(Printer.printer != null)
            Layout.print(layout, Printer.printer, style);
    }
    public static void logln(Layout.T layout, Layout.Style style) {
        if (Printer.printer != null) {
            Layout.print(layout, Printer.printer, style);
            Printer.printer.accept("\n");
        }
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

    // timer
    public static long elapsedTime;
    public record Timer<X, Y>(String name,
                            Function<X, Y> f,
                            X x){
        public Y doit(){
            long start = System.currentTimeMillis();
            Y result = f.apply(x);
            long end = System.currentTimeMillis();
            elapsedTime = (end - start);
            if (Printer.printer!=null)
                Printer.printer.accept("Elapsed time: @" + elapsedTime + " ms");
            return result;
        }
    }

    // log
    public record Log<X, Y>(String name,
                            Function<X, Y> f,
                            X x){
        public Y doit(){
            if(isBeingLogged(name))
                Printer.printer = System.out::print;
            Y result = new Timer<>(name, f, x).doit();
            Printer.printer = null;
            return result;
        }
    }

    // trace
    public record Trace<X, Y>(String name,
                              Function<X, Y> f,
                              X x,
                              Consumer<X> consumeX,
                              Consumer<Y> consumeY) {
        public Y doit() {
            boolean isTraced = isBeingTraced(name);
            if (isTraced) {
                System.out.println("\nbefore " + this.name);
                consumeX.accept(x);
            }
            Y y = new Log<>(name, f, x).doit();
            if (isTraced) {
                System.out.println("\nafter " + this.name);
                consumeY.accept(y);
            }
            if (Dot.beingDotted(name)) {
                cfg.Cfg.Program.dot((cfg.Cfg.Program.T) y);
            }
            return y;
        }
    }
}


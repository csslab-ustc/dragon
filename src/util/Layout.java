package util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Layout {
    /* data structures */
    public enum Style {
        C("C"),
        Dot("Dot");

        private final String name;

        Style(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static String getNewLine(Style style){
        return switch(style){
            case Style.C -> "\n";
            case Dot -> "\\l";
        };
    }



    public sealed interface T
            permits Empty, Halign,
            Indent, Str, Valign {
    }

    private record Halign(List<T> items) implements T {
    }

    // the default identification is 3
    private record Indent(T x) implements T {
    }

    private record Empty() implements T {
    }

    private record Str(String s) implements T {
    }

    private record Valign(List<T> items) implements T {
    }

    /* operations */
    // factory methods
    public static Layout.T halign(List<T> items) {
        return new Layout.Halign(items);
    }

    public static Layout.T halignSepRight(T sep, List<T> items) {
        List<T> newItems = items.stream().mapMulti((T x, Consumer<T> consumer) -> {
            consumer.accept(x);
            consumer.accept(Layout.halignVararg(sep, Layout.str(" ")));
        }).toList();
//        newItems.removeLast();
        return new Layout.Halign(newItems);
    }

    public static Layout.T halignVararg(T... items) {
        return new Layout.Halign(Arrays.stream(items).toList());
    }

    // add separator at the right of each item (including the last one)
    public static Layout.T halignVarargSepRight(T sep, T... items) {
        List<T> itemsList = Arrays.asList(items);
        itemsList = Stream.concat(itemsList.stream(), Stream.of(sep)).collect(Collectors.toList());
        return new Layout.Halign(itemsList);
    }

    public static Layout.T indent(T x) {
        return new Layout.Indent(x);
    }

    public static Layout.T str(String s) {
        return new Layout.Str(s);
    }

    public static Layout.T valign(List<T> items) {
        return new Layout.Valign(items);
    }

    public static Layout.T valignSepRight(T sep, List<T> items) {
        List<T> newItems = items.stream().map((T x) -> Layout.halignVararg(x, sep)).toList();
        return new Layout.Valign(newItems);
    }

    public static Layout.T valignVararg(T... items) {
        return new Layout.Valign(Arrays.stream(items).toList());
    }

    public static Layout.T valignVarargSepRight(T sep, T... items) {
        List<T> itemsList = Arrays.asList(items);
        List<T> newItems = itemsList.stream().mapMulti((T x, Consumer<T> consumer) -> {
            consumer.accept(x);
            consumer.accept(Layout.halignVararg(sep, Layout.str(" ")));
        }).toList();
        return new Layout.Valign(newItems);
    }

    // print methods
    private static boolean shouldSync = false;
    private static int currentIndent = 0;
    private static final int nest = 3;

    private static void indent() {
        currentIndent += nest;
    }

    private static void unIndent() {
        currentIndent -= nest;
    }

    private static void printSpaces() {
        if (!shouldSync) {
            return;
        }
        int i = currentIndent;
        while (i-- > 0)
            System.out.print(" ");
        shouldSync = false;
    }

    private static Consumer<String> thePrinter = null;
    private static Style theStyle = null;

    private static void print(T x) {
        switch (x) {
            case Empty() -> {
            }
            case Halign(List<T> items) -> items.forEach(Layout::print);
            case Indent(T y) -> {
                indent();
                print(y);
                unIndent();
            }
            case Str(String s) -> {
                printSpaces();
                thePrinter.accept(s);
            }
            case Valign(List<T> items) -> {
                int i = 0;
                for (; i < items.size() - 1; i++) {
                    print(items.get(i));
                    thePrinter.accept(getNewLine(theStyle));
                    // we should sync because we start a new line
                    shouldSync = true;
                }
                if(i < items.size())
                    print(items.get(i));
            }
        }
    }

    public static void print(T x, Consumer<String> printer, Style style) {
        thePrinter = printer;
        theStyle = style;
        print(x);
    }
}



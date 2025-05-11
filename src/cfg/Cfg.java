package cfg;

import control.Control;
import util.*;
import util.Error;

import java.util.*;

public class Cfg {

    //  ///////////////////////////////////////////////////////////
    //  type
    public static class Type {
        public sealed interface T
                permits Int {
        }

        public record Int() implements T {
        }

        // operations
        public static Layout.T layout(T ty) {
            return switch (ty) {
                case Int() -> Layout.str("int");
            };
        }
    }

    // ///////////////////////////////////////////////////
    // declaration
    public static class Dec {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Type.T type,
                                Id id) implements T {
        }

        public static Tuple.Two<Type.T, Id> destroy(T t){
            switch (t){
                case Singleton(Type.T ty, Id x) -> {
                    return new Tuple.Two<>(ty, x);
                }
            }
        }

        public static Layout.T layout(T dec) {
            return switch (dec) {
                case Singleton(
                        Type.T type,
                        Id id
                ) -> Layout.halignVararg(Type.layout(type),
                        Layout.str(" "),
                        id.layout());
            };
        }
    }

    // /////////////////////////////////////////////////////////
    // Binary operators
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
            public String toString(){
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
        public sealed interface T
                permits Bop, Call, Eid, Int, Print {
        }

        public record Bop(BinaryOperator.T op,
                          List<Id> operands) implements T {
        }

        public record Call(Id func,
                           List<Id> operands) implements T {
        }

        public record Eid(Id x) implements T {
        }

        public record Int(int n) implements T {
        }

        public record Print(Id x) implements T {
        }

        public static Layout.T layout(T t) {
            return switch (t) {
                case Bop(BinaryOperator.T op,
                         List<Id> operands) ->
                    Layout.halignVararg(BinaryOperator.layout(op),
                            Layout.str("("),
                            Layout.halignSepRight(Layout.str(","),
                                    operands.stream().map(Id::layout).toList()),
                    Layout.str(")"));
                case Call(Id func, List<Id> args) ->
                    //                    Type.pp(retType);
                        Layout.halignVararg(Layout.str(func.toString()),
                                Layout.str("("),
                                Layout.halignSepRight(Layout.str(","),
                                        args.stream().map(Id::layout).toList()),
                                Layout.str(")  @retType:"));
                case Eid(Id id) -> id.layout();
                case Int(int n) -> Layout.str(Integer.toString(n));
                case Print(Id x) -> Layout.halignVararg(Layout.str("print("),
                        x.layout(),
                        Layout.str(")"));
                default -> throw new Todo(t);
            };
        }
    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public sealed interface T
                permits Assign {
        }

        // "x" should not be "null", even if the exp is not used.
        public record Assign(Label label,
                             Id x,
                             Exp.T exp) implements T {
            public Assign(Id x,
                          Exp.T exp){
                this(new Label(), x, exp);
            }
        }

        public static Label getLabel(T stm) {
            switch (stm){
                case Assign(Label label, _, _) -> {
                    return label;
                }
            }
        }

        public static Plist getPlist(T stm) {
            switch (stm){
                case Assign(Label label, _, _) -> {
                    return label.getPlist();
                }
            }
        }

        public static Layout.T layout(T t) {
            return switch (t) {
                case Assign(Label label, Id x, Exp.T exp) ->
                        Layout.halignVararg(Control.Printer.shouldPrintStmLabel?
                                Layout.str(label.toString() + ": "):
                                Layout.str(""),
                Layout.str(x + " = "),
                Exp.layout(exp),
                Layout.str(";"));
            };
        }

        public static void pp(T t){
            var layout = layout(t);
            Layout.print(layout, System.out::print, Layout.Style.C);
        }

        public static Tuple.Two<Id, Exp.T> destroy(T stm) {
            switch (stm){
                case Assign(Label label,
                            Id id,
                            Exp.T exp) -> {
                    return new Tuple.Two<>(id, exp);
                }
            }
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    public static class Transfer {
        public sealed interface T
                permits If, Jmp, Ret {
        }

        public record If(Label label,
                         Id x,
                         Label trueLabel,
                         Label falseLabel) implements T {
            public If(Id x, Label trueLabel, Label falseLabel){
                this(new Label(), x, trueLabel, falseLabel);
            }
        }

        public record Jmp(Label label,
                          Label target) implements T {
            public Jmp(Label target){
                this(new Label(), target);
            }
        }

        public record Ret(Label label,
                          Id x) implements T {
            public Ret(Id x){
                this(new Label(), x);
            }
        }

        public static Plist getPlist(T t) {
            return switch (t){
                case If(Label label, Id x, Label trueBlock, Label falseBlock) ->
                        label.getPlist();
                case Jmp(Label label, Label target) ->
                        label.getPlist();
                case Ret(Label label, Id x) ->
                        label.getPlist();
            };
        }

        public static Layout.T layout(T t) {
            return switch (t) {
                case If(_,
                        Id x,
                        Label thenn,
                        Label elsee
                ) -> Layout.halignVararg(Layout.str("if(" + x.toString() +
                        ", " + thenn.toString() + ", " + elsee.toString() + ");"));
                case Jmp(_,
                         Label target) -> Layout.str("jmp " + target.toString() + ";");
                case Ret(_,
                         Id x) -> Layout.str("ret " + x.toString() + ";");
            };
        }

        public static void pp(T t){
            var layout = layout(t);
            Layout.print(layout, System.out::print, Layout.Style.C);
        }
    }

    // /////////////////////////////////////////////////////////
    // block
    public static class Block {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Label label,
                                List<Stm.T> stms,
                                Transfer.T transfer) implements T {
        }

        public static void add(T b, Stm.T s) {
            switch (b) {
                case Singleton(
                        _,
                        List<Stm.T> stms,
                        _
                ) -> stms.add(s);
            }
        }

        public static Plist getPlist(T b) {
            return switch (b){
                case Singleton(Label label,
                               _,
                               _) -> label.getPlist();
            };
        }

        public static Label getLabel(T t) {
            return switch (t) {
                case Singleton(
                        Label label,
                        _,
                        _
                ) -> label;
            };
        }

        public static Layout.T layout(T b) {
            return switch (b) {
                case Singleton(
                        Label label,
                        List<Stm.T> stms,
                        Transfer.T transfer
                ) -> Layout.valignVararg(Layout.str(label.toString() + ":"),
                        Layout.indent(Layout.valign(stms.stream().map(Stm::layout).toList())),
                        Layout.indent(Transfer.layout(transfer)));
            };
        }

        public static void pp(T t){
            var layout = layout(t);
            Layout.print(layout, System.out::print, Layout.Style.C);
        }

        public static int size(T b){
            return switch (b) {
                case Singleton(
                        Label label,
                        List<Stm.T> stms,
                        Transfer.T transfer
                ) -> stms.size() + 1;
            };
        }
    }
    // end of block

    // /////////////////////////////////////////////////////////
    // function
    public static class Function {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Type.T retType,
                                Id id,
                                List<Dec.T> formals,
                                List<Dec.T> locals,
                                List<Block.T> blocks,
                                Label entryBlock,
                                Label exitBlock) implements T {
        }

        // rename all variables and labels
        public static T alphaRename(T func) {
            switch (func) {
                case Singleton(
                        Type.T ty,
                        Id id,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks,
                        Label entryLabel,
                        Label exitLabel
                ) -> {
                    // generate new names
                    Property<Id, Id> newNameProp = new Property<>(Id::getPlist);
                    java.util.function.Function<Dec.T, Dec.T> rename = (Dec.T dec) -> {
                        switch (dec){
                            case Dec.Singleton(Type.T type, Id id1) -> {
                                var newId = Id.newNoname();
                                newNameProp.put(id1, newId);
                                return new Dec.Singleton(type, newId);
                            }
                        }
                    };
                    var newFormals = formals.stream().map(rename).toList();
                    var newLocals = locals.stream().map(rename).toList();
                    // generate new labels
                    Property<Label, Label> newLabelProp = new Property<>(Label::getPlist);
                    blocks.forEach(block -> {
                        switch (block){
                            case Block.Singleton(Label label,
                                                 List<Stm.T> stms,
                                                 Transfer.T transfer) -> newLabelProp.put(label, new Label());
                        }
                    });
                    // rewrite
                    var newBlocks = blocks.stream().map((b) -> {
                        switch (b){
                            case Block.Singleton(Label label,
                                                 List<Stm.T> stms,
                                                 Transfer.T transfer) -> {
                                var newStms = stms.stream().map((s) -> {
                                    switch (s){
                                        case Stm.Assign(Label label1,
                                                        Id x,
                                                        Exp.T exp) ->{
                                            Exp.T newExp = switch (exp){
                                                case Exp.Bop(BinaryOperator.T op,
                                                             List<Id> operands) ->
                                                    new Exp.Bop(op, operands.stream().map(newNameProp::get).toList());
                                                case Exp.Call(Id func1,
                                                              List<Id> operands) ->
                                                        new Exp.Call(func1, operands.stream().map(newNameProp::get).toList());
                                                case Exp.Int(int n) -> exp;
                                                case Exp.Eid(Id x1) -> new Exp.Eid(newNameProp.get(x1));
                                                case Exp.Print(Id x1) -> new Exp.Print(newNameProp.get(x1));
                                            };
                                            return (Stm.T)new Stm.Assign(label1,
                                                    newNameProp.get(x),
                                                    newExp);
                                        }
                                    }
                                }).toList();
                                var newTransfer = switch (transfer){
                                    case Transfer.If(Label label1,
                                                     Id x,
                                                     Label trueLabel,
                                                     Label falseLabel) ->
                                        new Transfer.If(label1,
                                                newNameProp.get(x),
                                                newLabelProp.get(trueLabel),
                                                newLabelProp.get(falseLabel));
                                    case Transfer.Jmp(Label label1, Label target) ->
                                            new Transfer.Jmp(label1, newLabelProp.get(target));
                                    case Transfer.Ret(Label label1, Id x) ->
                                        new Transfer.Ret(label1, newNameProp.get(x));
                                };
                                return (Block.T)new Block.Singleton(newLabelProp.get(label),
                                        newStms,
                                        newTransfer);
                            }
                        }
                    }).toList();
                    return new Function.Singleton(ty,
                            id,
                            newFormals,
                            newLocals,
                            newBlocks,
                            newLabelProp.get(entryLabel),
                            newLabelProp.get(exitLabel)
                            );
                }
            }
        }

        public static Block.T getBlock(T func, Label label) {
            switch (func) {
                case Singleton(
                        Type.T _,
                        Id _,
                        List<Dec.T> _,
                        List<Dec.T> _,
                        List<Block.T> blocks,
                        Label entryBlock,
                        Label exitBlock
                ) -> {
                    for(Block.T block : blocks) {
                        if (Block.getLabel(block).equals(label)) {
                            return block;
                        }
                    }
                    throw new Error(label.toString());
                }
            }
        }

        public static Plist getPlist(T func) {
            switch (func) {
                case Singleton(
                        Type.T _,
                        Id id,
                        List<Dec.T> _,
                        List<Dec.T> _,
                        List<Block.T> blocks,
                        Label entryBlock,
                        Label exitBlock
                ) -> {
                    return id.getPlist();
                }
            }
        }

        // build its control-flow graph
        // return the graph and a map from basic block to its graph node
        public static Tuple.Two<Graph<Block.T>,
                HashMap<Label, Graph<Block.T>.Node>>
        buildControlFlowGraph(T func) {
            switch (func) {
                case Singleton(
                        Type.T retType,
                        Id functionId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks,
                        Label entryLabel,
                        Label exitBlock) -> {
                    // build a new graph
                    Graph<Block.T> graph = new Graph<>(functionId.toString());
                    // map a basic block to its corresponding graph node
                    HashMap<Label, Graph<Block.T>.Node> label2node = new HashMap<>();
                    // add all blocks to the graph
                    blocks.forEach(block -> {
                        var node = graph.newAndAddNode(block);
                        label2node.put(Block.getLabel(block), node);
                    });

                    // add all control flow edges to the graph
                    blocks.forEach(block -> {
                        switch (block){
                            case Block.Singleton(Label label,
                                                 _,
                                                 Transfer.T transfer) -> {
                                var fromNode = label2node.get(label);
                                switch (transfer){
                                    case Transfer.If(_,
                                                     Id x,
                                                     Label thenn,
                                                     Label elsee) -> {
                                        List.of(thenn, elsee).forEach((l) -> {
                                            Graph<Block.T>.Node toNode = label2node.get(l);
                                            graph.addEdge(fromNode, toNode);
                                        });
                                    }
                                    case Transfer.Jmp(_,
                                                      Label targetLabel) ->{
                                        Graph<Block.T>.Node toNode = label2node.get(targetLabel);
                                        graph.addEdge(fromNode, toNode);
                                    }
                                    case Transfer.Ret(_, Id x) ->{
                                        // nop
                                    }
                                }
                            }
                        }
                });
                    return new Tuple.Two<>(graph, label2node);
                }
            }
        }

        public static boolean isLeaf(T func) {
            switch (func) {
                case Singleton(
                        Type.T retType,
                        Id functionId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks,
                        _,
                        _
                ) -> {
                    for (Block.T block : blocks) {
                        switch (block){
                            case Block.Singleton(Label label,
                                                 List<Stm.T> stms,
                                                 Transfer.T transfer) ->{
                                for (Stm.T stm : stms) {
                                    switch (stm){
                                        case Stm.Assign(Label label1,
                                                        Id x,
                                                        Exp.T exp) ->{
                                            switch (exp){
                                                case Exp.Call _, Exp.Print _ ->{
                                                    return false;
                                                }
                                                default -> {}
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }

        public static void dot(T func) {
            switch (func) {
                case Singleton(
                        Type.T retType,
                        Id functionId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks,
                        _,
                        _
                ) -> {
                    var tuple = buildControlFlowGraph(func);
                    var cfg = tuple.first();

                    cfg.dot("-cfg", Block::layout);
                }
            }
        }

        public static Layout.T layout(T f) {
            return switch (f) {
                case Singleton(
                        Type.T retType,
                        Id id,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks,
                        Label entry,
                        Label exit
                ) -> Layout.valignVararg(
                        Layout.halignVararg(Layout.str("// @ entry: "),
                                Layout.str(entry.toString()),
                                Layout.str(", exit: "),
                                Layout.str(exit.toString())),
                        Layout.halignVararg(Type.layout(retType),
                                Layout.str(" " + id + "("),
                                Layout.halignSepRight(Layout.str(", "),
                                        formals.stream().map(Dec::layout).toList()),
                                Layout.str("){")),
                        Layout.indent(Layout.valignSepRight(Layout.str(";"),
                                locals.stream().map(Dec::layout).toList())),
                        Layout.indent(Layout.valign(blocks.stream().map(Block::layout).toList())),
                        Layout.str("}"));
            };
        }

        public static void pp(Function.T t){
            var layout = layout(t);
            Layout.print(layout, System.out::print, Layout.Style.C);
        }

        // the number of statements and transfers
        public static int size(Function.T f) {
            switch (f){
                case Singleton(
                        Type.T retType,
                        Id functionId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks,
                        _,
                        _
                ) -> {
                    return blocks.stream().map(Block::size).reduce(Integer::sum).orElse(0);
                }
            }
        }
    }
    // end of function

    // /////////////////////////////////////////////////////////
    // whole program
    public static class Program {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(List<Function.T> functions) implements T {
        }

        // operations
        public static void dot(T prog) {
            switch (prog) {
                case Singleton(List<Function.T> functions
                ) -> functions.forEach(Function::dot);
            }
        }

        public static Layout.T layout(T prog) {
            return switch (prog) {
                case Singleton(
                        List<Function.T> functions
                ) -> Layout.halign(functions.stream().map(Function::layout).toList());
            };
        }

        public static void pp(T prog) {
            var layout = layout(prog);
            Layout.print(layout, System.out::print, Layout.Style.C);
        }
    }
    // end of program
}

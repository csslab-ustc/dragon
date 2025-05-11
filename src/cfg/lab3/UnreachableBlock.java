package cfg.lab3;

import cfg.Cfg;
import control.Control;
import frontend.Frontend;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Error;
import util.Id;
import util.Label;
import util.Property;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class UnreachableBlock {

    static class Global{
        static boolean removed = false;
    }

    // /////////////////////////////////////////////////////////
    // properties
    private static final Property<Cfg.Block.T, Boolean> reachableProp =
            new Property<>(Cfg.Block::getPlist);
    Property<Label, Label> replaceProp = new Property<>(Label::getPlist);


    private Optional<Label> findLast(Label current){
        var first = replaceProp.get(current);
        if(first == null){
            return Optional.empty();
        }
        while(first != null){
            var second = replaceProp.get(first);
            if(second == null){
                return Optional.of(first);
            }
            first = second;
        }
        throw new Error("");
    }

    // /////////////////////////////////////////////////////////
    // function
    private Cfg.Function.T removeEmptyFunction(Cfg.Function.T func) {
        switch (func) {
            case Cfg.Function.Singleton(
                    Cfg.Type.T retType,
                    Id functionId,
                    List<Cfg.Dec.T> formals,
                    List<Cfg.Dec.T> locals,
                    List<Cfg.Block.T> blocks,
                    var entryLabel,
                    var exitLabel
            ) -> {
                // TODO: please add your code:
                throw new util.Todo();

            }
        }
    }

    private Cfg.Function.T removeUnreachableFunction(Cfg.Function.T func) {
        switch (func) {
            case Cfg.Function.Singleton(
                    Cfg.Type.T retType,
                    Id functionId,
                    List<Cfg.Dec.T> formals,
                    List<Cfg.Dec.T> locals,
                    List<Cfg.Block.T> blocks,
                    var entryLabel,
                    var exitLabel
            ) -> {
                var graphAndMap = Cfg.Function.buildControlFlowGraph(func);
                var graph = graphAndMap.first();
                var block2node = graphAndMap.second();
                var entryNode = block2node.get(entryLabel);

                // TODO: please add your code:
                throw new util.Todo();

            }
        }
    }

    private Cfg.Function.T doitFunction(Cfg.Function.T func){
        do{
            Global.removed = false;
            func = removeUnreachableFunction(func);
            func = removeEmptyFunction(func);
        }while(Global.removed);
        return func;
    }
    // end of function

    // /////////////////////////////////////////////////////////
    // program
    private Cfg.Program.T doitProgram0(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    List<Cfg.Function.T> functions
            ) -> {
                var fs = functions.stream().map(this::doitFunction).toList();
                return new Cfg.Program.Singleton(fs);
            }
        }
    }


    public Cfg.Program.T doitProgram(Cfg.Program.T prog) {
        var trace = new Control.Trace<>("cfg.UnreachableBlock",
                this::doitProgram0,
                prog,
                Cfg.Program::pp,
                Cfg.Program::pp);
        return trace.doit();
    }
    // end of program

}

//package interproc;
//
//import cfg.Cfg;
//import cfg.Cfg.Exp;
//import cfg.Cfg.Stm;
//import util.*;
//import util.Error;
//import util.lattice.ZeroLattice;
//import util.map.FunMap;
//
//import java.util.HashSet;
//import java.util.List;
//
//public class ContextFreeAnalysis {
//
//    // we use an arbitrary string as the key:
//    private static String contextFreeKey = "[Key: contextFree]";
//    private static Id gFunctionId = null;
//
//    // the lattice we are using:
//    // ZeroLattice
//
//
//
//    // the initial lattice
//    private static FunMap<Id, ZeroLattice> defaultMapForDec(Cfg.Dec.T dec,
//                                                            FunMap<Id, ZeroLattice> map,
//                                                            ZeroLattice defaultValue){
//        switch (dec){
//            case Cfg.Dec.Singleton(Cfg.Type.T type, Id id) ->{
//                return map.put(id, defaultValue);
//            }
//        }
//    }
//    private static FunMap<Id, ZeroLattice> genDefaultMap(List<Cfg.Dec.T> formals,
//                                                         List<Cfg.Dec.T> locals,
//                                                         ZeroLattice defaultValueForFormal,
//                                                         ZeroLattice defaultValueForLocal){
//        FunMap<Id, ZeroLattice> map = new FunMap<>();
//        for (Cfg.Dec.T formal : formals){
//            map = defaultMapForDec(formal, map, defaultValueForFormal);
//        }
//        for (Cfg.Dec.T local : locals){
//            map = defaultMapForDec(local, map, defaultValueForLocal);
//        }
//        return map;
//    }
//
//    private static FunMap<Id, ZeroLattice> defaultMapForAll;
//    private static FunMap<Id, ZeroLattice> mapForEntryBlock;
//
//
//    // /////////////////////////////////////////////////////////
//    // properties
//    // attach predecessors to each graph node
//    private static final Property<Graph<Cfg.Block.T>.Node, HashSet<Graph<Cfg.Block.T>.Node>> predProp =
//            new Property<>(Graph.Node::getPlist);
//    // attach liveIn/liveOut set to each graph node
//    private static final Property<Graph<Cfg.Block.T>.Node, FunMap<Id, ZeroLattice>> inProp =
//            new Property<>(Graph.Node::getPlist);
//    private static final Property<Graph<Cfg.Block.T>.Node, FunMap<Id, ZeroLattice>> outProp =
//            new Property<>(Graph.Node::getPlist);
//    // attach liveIn/liveOut set to each function (function's name)
//    private static final Property<Id, Context> inFuncProp =
//            new Property<>(Id::getPlist);
//    private static final Property<Id, ZeroLattice> outFuncProp =
//            new Property<>(Id::getPlist);
//    // attach graph to each function
//    private static final Property<Id, Tuple.Two<Graph<Cfg.Block.T>, Graph<Cfg.Block.T>.Node>> graphFuncProp =
//            new Property<>(Id::getPlist);
//
//    // maintain the information
//    static class GenKill {
//
//        // return: (gen, kill)
//        public static FunMap<Id, ZeroLattice.T> join(List<FunMap<Id, ZeroLattice.T>> funMaps) {
//            var map = defaultMapForAll;
//            for (var funMap : funMaps) {
//                map = map.join(funMap,
//                        ZeroLattice::lub);
//            }
//            return map;
//        }
//    }
//    // end of gen-kill
//
//    // /////////////////////////////////////////////////////////
//    // expression
//    // forward: given the "in", calculates and returns the "out"
//    private ZeroLattice doitBinaryOperator(Cfg.BinaryOperator.T op,
//                                           List<Id> operands,
//                                           FunMap<Id, ZeroLattice> liveIn) {
//        List<ZeroLattice> operandsList = operands.stream().map(liveIn::get).toList();
//        ZeroLattice first = operandsList.getFirst();
//        ZeroLattice second = operandsList.get(1);
//        switch (op){
//            // see Table 4.2
//            case Add -> {
//                if(first.isBottom()
//                        || second.isBottom()){
//                    return ZeroLattice.newBottom();
//                }
//                if (first.isTop() || second.isTop()){
//                    return ZeroLattice.newTop();
//                }
//                if(first.isZero())
//                    return second;
//                if(second.isZero())
//                    return first;
//                return ZeroLattice.newTop();
//            }
//            case Mul -> {
//                if(first.isZero() || second.isZero()) {
//                    return ZeroLattice.newZero();
//                }
//                if(first.isBottom() || second.isBottom()){
//                    return ZeroLattice.newBottom();
//                }
//                if (first.isTop() || second.isTop()){
//                    return ZeroLattice.newTop();
//                }
//                return ZeroLattice.newNotZero();
//            }
//            case Eq -> {
//                if(first.isBottom() || second.isBottom()){
//                    return ZeroLattice.newBottom();
//                }
//                if (first.isTop() || second.isTop()){
//                    return ZeroLattice.newTop();
//                }
//                if(first.isZero() && second.isZero())
//                    return ZeroLattice.newNotZero();
//                if(first.isZero() || second.isZero())
//                    return ZeroLattice.newZero();
//                return ZeroLattice.newTop();
//            }
//            case Div -> {
//                if(first.isBottom() || second.isBottom()){
//                    return ZeroLattice.newBottom();
//                }
//                if (second.isZero()){
//                    return ZeroLattice.newBottom();
//                }
//                if (first.isTop() || second.isTop()){
//                    return ZeroLattice.newTop();
//                }
//                if(first.isZero())
//                    return ZeroLattice.newZero();
//                return ZeroLattice.newTop();
//            }
//            default -> {
//                throw new Error(op);
//            }
//        }
//    }
//
//    // /////////////////////////////////////////////////////////
//    // expression
//    // forward: given the "in", calculates and returns the "out"
//    private ZeroLattice doitExp(Exp.T exp, FunMap<Id, ZeroLattice> liveIn) {
////        System.out.print("\t\tliveIn = ");
////        liveIn.print();
////        Exp.pp(exp);
//        ZeroLattice result;
//        switch (exp){
//            case Exp.Int(int n) -> {
//                if(n==0)
//                    result = ZeroLattice.newZero();
//                else
//                    result = ZeroLattice.newNotZero();
//            }
//            case Exp.Call(Id target, List<Id> operands) -> {
//                // #1 step: propagate the info to the callee "target"
//                Context<String, ZeroLattice> theContext =
//                        inFuncProp.getInitConst(target,
//                            new Context<String, ZeroLattice>(operands.size(), ZeroLattice.newBottom()));
//                theContext.merge(contextFreeKey,
//                        operands.stream().map(liveIn::get).toList(),
//                        ZeroLattice::lub);
//
//                // #2 step: take the return value from the callee
//                ZeroLattice retValue = outFuncProp.getInitConst(target,
//                        ZeroLattice.newBottom());
//
//                // debugging
////                System.out.println("has put?");
////                theContext = inFuncProp.get(target);
////                theContext.print(ZeroLattice::print);
//
//                return retValue;
//            }
//            case Exp.Bop(Cfg.BinaryOperator.T op, List<Id> operands) -> {
//                return doitBinaryOperator(op, operands, liveIn);
//            }
//            case Exp.Eid(Id x) -> {
//                return liveIn.get(x);
//            }
//            case Exp.Print(Id x) -> {
//                return ZeroLattice.newTop();
//            }
//        }
////        System.out.print("\n\t\texp lattice = ");
////        result.print();
//        return result;
//    }
//    // end of expression
//
//    // /////////////////////////////////////////////////////////
//    // statement
//    // forward: given the "in", calculates and returns the "out"
//    private FunMap<Id, ZeroLattice> doitStm(Stm.T stm, FunMap<Id, ZeroLattice> liveIn) {
//        System.out.print("\tliveIn = ");
//        liveIn.print();
//        Stm.pp(stm);
//        switch (stm){
//            case Stm.Assign(_, Id x, Exp.T exp) -> {
//                ZeroLattice lattice = doitExp(exp, liveIn);
//                FunMap<Id, ZeroLattice> liveOut = liveIn.put(x, lattice);
//                System.out.print("\tliveOut = ");
//                liveOut.print();
//                return liveOut;
//            }
//        }
//    }
//    // end of statement
//
//    // /////////////////////////////////////////////////////////
//    // transfer
//    // given the "out", calculates and returns the "in"
//    private FunMap<Id, ZeroLattice> doitTransfer(Cfg.Transfer.T t, FunMap<Id, ZeroLattice> in) {
//        switch (t){
//            case Cfg.Transfer.Ret(Label label, Id x) ->{
//                ZeroLattice lattice = in.get(x);
//                var oldLattice = outFuncProp.get(gFunctionId);
//                if(!lattice.equals(oldLattice)){
//                    isChanged = true;
//                    outFuncProp.put(gFunctionId, lattice);
//                    System.out.print("\n\tret lattice = ");
//                    lattice.print();
//                }
//                return in;
//            }
//            default -> {
//                return in;
//            }
//        }
//    }
//    // end of transfer
//
//    // /////////////////////////////////////////////////////////
//    // block
//    // takes as input an liveIn, and calculates and returns a liveOut
//    private FunMap<Id, ZeroLattice> doitBlock(Cfg.Block.T b, FunMap<Id, ZeroLattice> in) {
//        switch (b) {
//            case Cfg.Block.Singleton(
//                    Label label,
//                    List<Stm.T> stms,
//                    List<Cfg.Transfer.T> transfer
//            ) -> {
//                // debugging
//                System.out.println(label.toString() + ": ");
//                System.out.print("liveIn = ");
//                in.print();
//                // do not calculate transfer
//                for(Stm.T s: stms){
//                    in = doitStm(s, in);
//                }
//                System.out.print("liveOut = ");
//                for(Cfg.Transfer.T s: transfer){
//                    in = doitTransfer(s, in);
//                }
//                in.print();
//                return in;
//            }
//        }
//    }
//    // end of block
//
//    private Object doitNode(Graph<Cfg.Block.T>.Node node) {
//        // get all predecessors
//        HashSet<Graph<Cfg.Block.T>.Node> predecessors = predProp.getInitFun(node,
//                Graph.Node::predecessors);
//
//        FunMap<Id, ZeroLattice> liveOutsFromPredecessors;
//        // no predecessors, so this is the entry block
//        if(predecessors.isEmpty()) {
//            liveOutsFromPredecessors = mapForEntryBlock;
//        }else {
//            // get all the liveOut for predecessors
//            List<FunMap<Id, ZeroLattice>> outForPredecessors = predecessors.stream().
//                    map(n -> outProp.getInitConst(n, defaultMapForAll)).toList();
//            liveOutsFromPredecessors = GenKill.join(outForPredecessors);
//        }
//
//        // liveIn = |_| liveOut[preds]
//        var oldLiveIn = inProp.get(node); // may be null
//        // determine whether "liveOut" has changed
////        if (!liveOutsFromPredecessors.isSame(oldLiveIn)) {
//            liveOutsFromPredecessors.print();
//            if(oldLiveIn != null)
//                oldLiveIn.print();
//            else{
////                System.out.println("oldLiveIn is null");
//            }
////            isChanged = true;
//            // record the liveOut for this node
//            inProp.put(node, liveOutsFromPredecessors);
//            FunMap<Id, ZeroLattice> newLiveOut = doitBlock(node.getData(), liveOutsFromPredecessors);
//            // record the liveOut for this node
//            outProp.put(node, newLiveOut);
////        }
//        return null;
//    }
//
//    // /////////////////////////////////////////////////////////
//    // function
//    private static int rounds = 0;
//    private static boolean isChanged = false;
//
//    private void doitFunction(Cfg.Function.T func) {
//        switch (func) {
//            case Cfg.Function.Singleton(
//                    Cfg.Type.T retType,
//                    Id functionId,
//                    List<Cfg.Dec.T> formals,
//                    List<Cfg.Dec.T> locals,
//                    List<Cfg.Block.T> blocks,
//                    Cfg.Block.T entryBlock,
//                    Cfg.Block.T exitBlock
//            ) -> {
//                gFunctionId = functionId;
//                defaultMapForAll = genDefaultMap(formals,
//                        locals,
//                        ZeroLattice.newBottom(),
//                        ZeroLattice.newBottom());
//                // initial bottom
//                mapForEntryBlock = genDefaultMap(formals,
//                        locals,
//                        ZeroLattice.newBottom(),
//                        ZeroLattice.newTop());
//                Context<String, ZeroLattice> argValues = inFuncProp.get(functionId);
//                if(argValues != null) {
//                    var list = argValues.collapse(contextFreeKey, ZeroLattice::lub);
////                    list.forEach(ZeroLattice::print);
//                    Integer index = 0;
//                    for(Cfg.Dec.T x: formals) {
//                        switch (x){
//                            case Cfg.Dec.Singleton(Cfg.Type.T type, Id id) ->{
//                                mapForEntryBlock = mapForEntryBlock.put(id, list.get(index));
//                                index++;
//                            }
//                        }
//                    }
//                }
//
//                System.out.println(functionId + ": initial maps");
//                mapForEntryBlock.print();
//
//
//                Graph<Cfg.Block.T> graph;
//                Graph<Cfg.Block.T>.Node entryNode;
//
//                // we must be careful not to build the graph multiple times
//                Tuple.Two<Graph<Cfg.Block.T>, Graph<Cfg.Block.T>.Node> graphAndStart =
//                        graphFuncProp.get(functionId);
//
//                if(graphAndStart != null){
//                   graph = graphAndStart.first();
//                   entryNode = graphAndStart.second();
//                }else {
//                    // the slow path...
//                    // Step #1: build the control flow graph
//                    // we may also build the block2node data structure mapping
//                    // each basic block to its corresponding graph node
//                    var graphAndMap = Cfg.Function.buildGraph(func);
//                    graph = graphAndMap.first();
//                    var block2node = graphAndMap.second();
//                    // dump the graph
////                graph.dot((x) -> Cfg.Block.getLabel(x).toString());
//
//
//                    // Step #3: for the entry block, init its liveIn to be "defaultMapWithTop"
//                    // to reflect the fact that all variables are uninitialized.
//                    entryNode = block2node.get(entryBlock);
//
//                    // remember the graph
//                    graphFuncProp.put(functionId, new Tuple.Two<>(graph, entryNode));
//                }
//
//                // we make a fake predecessor for entryblock to hold the value from the function
//
//
//                // Step #3: fix-point algorithm
////                do {
////                    isChanged = false;
////                    rounds++;
//                    graph.dfs(entryNode,
//                            (Graph<Cfg.Block.T>.Node node, Object obj) ->
//                                    doitNode(node),
//                            null);
////                } while (isChanged);
//
//                // clear the defs property
////                predProp.clear();
//            }
//        }
//    }
//    // end of function
//
//    // /////////////////////////////////////////////////////////
//    // program
//    public void doitProgram(Cfg.Program.T prog) {
//        switch (prog) {
//            case Cfg.Program.Singleton(
//                    List<Cfg.Function.T> functions
//            ) -> {
//                do {
//                    isChanged = false;
//                    rounds++;
//                    functions.forEach(this::doitFunction);
//                } while (isChanged);
//            }
//        }
//        System.out.println("execution rounds = " + rounds);
//        inProp.clear();
//        outProp.clear();
//    }
//    // end of program
//}
//// end of ReachDefinition
//
//

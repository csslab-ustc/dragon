package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

// two implementation of the cubic algorithm
public class Cubic {
    // an experimental implementation of the cubic algorithm
    // spa, page 138
    public static class Cubic1<X, T>{
        private final Property<X, HashSet<T>> solutionProp;
        private final Property<X, HashSet<X>> successorsProp;
        private final Property<X, HashMap<T, HashSet<Tuple.Two<X, X>>>> condProp;
        private final WorkList<Tuple.Two<T, X>> workList;

        public Cubic1(Function<X, Plist> getPlist){
            this.solutionProp = new Property<>(getPlist);
            this.successorsProp = new Property<>(getPlist);
            this.condProp = new Property<>(getPlist);
            this.workList = new WorkList<>();
        }

        private void addToken(T t, X x){

            HashSet<T> solution = solutionProp.getOrInitConst(x, new HashSet<>());
            if(!solution.contains(t)){
                solution.add(t);
                workList.add(new Tuple.Two<>(t, x));
            }
        }

        private void addEdge(X from, X to){
            HashSet<X> successors = successorsProp.getOrInitConst(from, new HashSet<>());

            if(!from.equals(to) && !successors.contains(to)) {
                successors.add(to);
                HashSet<T> solution = solutionProp.getOrInitConst(from, new HashSet<>());
                solution.forEach(t -> addToken(t, to));
            }
        }

        private void propagate(){
            while(!workList.isEmpty()){
                Tuple.Two<T, X> tAndX = workList.remove();
                T t = tAndX.first();
                X x = tAndX.second();
                var condMap = condProp.getOrInitConst(x, new HashMap<>());
                var condSet = condMap.computeIfAbsent(t, k -> new HashSet<>());
                condSet.forEach(cond -> {
                    X y = cond.first();
                    X z = cond.second();
                    addEdge(y, z);
                });
                var succs = successorsProp.getOrInitConst(x, new HashSet<>());
                succs.forEach(y -> {
                    addToken(t, y);
                });
            }
        }

        // exposed interfaces
        // t \in X
        public void in(T t, X x){
            addToken(t, x);
            propagate();
        }

        // X <= Y
        public void subset(X x, X y){
            addEdge(x, y);
            propagate();
        }

        // t\in x ==> y <= z
        public void imply(T t, X x, X y, X z){
            HashSet<T> solution = solutionProp.getOrInitConst(x, new HashSet<>());
            if(solution.contains(t)){
                addEdge(y, z);
                propagate();
            }else{
                HashMap<T, HashSet<Tuple.Two<X, X>>> condMap = condProp.getOrInitConst(x, new HashMap<>());
                var condSet = condMap.computeIfAbsent(t, k -> new HashSet<>());
                condSet.add(new Tuple.Two<>(y, z));
            }
        }

        public Property<X, HashSet<T>> getSolution(){
            return this.solutionProp;
        }
    }

    // a second algorithm
    static class Cubic2 {
        public static class Element<X> {
            private final X data;
            HashSet<Element<X>> solutions;
            HashSet<Element<X>> succs;
            LinkedList<Consumer<Element<X>>> promises;

            public Element(X data) {
                this.data = data;
                this.solutions = new HashSet<>();
                this.succs = new HashSet<>();
                this.promises = new LinkedList<>();
            }

            @Override
            public String toString() {
                return data.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (o == null)
                    return false;
                if (!(o instanceof Element<?> element)) {
                    return false;
                } else {
                    return this.data.equals(element.data);
                }
            }

            @Override
            public int hashCode() {
                return data.hashCode();
            }

            private void printSolutions() {
                System.out.println(solutions);
//            this.solutions.forEach(System.out::println);
            }
        }

        private static final HashMap<Object, Element<Object>> unique = new HashMap<>();

        // public interfaces
        @SuppressWarnings("unchecked")
        public static <X> Element<X> newElement(X data){
            Element<X> elem = (Element<X>)unique.computeIfAbsent(data, k -> new Element<>(data));
            return elem;
        }

        // t \in e
        public static <X> void addToken(Element<X> t,
                                        Element<X> e) {
            if (!e.solutions.contains(t)) {
                e.solutions.add(t);
                e.succs.forEach(succ -> addToken(t, succ));
                // deliver promises
                e.promises.forEach(consumer -> consumer.accept(t));
            }
        }

        // X <= Y
        public static <X> void addEdge(Element<X> x,
                                       Element<X> y) {
            if (!x.equals(y) && !x.succs.contains(y)) {
                x.succs.add(y);
                x.solutions.forEach((t) -> addToken(t, y));
            }
        }

        // imply:
        // t\in x -> y <=z
        public static <X> void addImply(Element<X> t,
                                        Element<X> x,
                                        Function<Element<X>, Element<X>> yf,
                                        Function<Element<X>, Element<X>> zf) {
            if (x.solutions.contains(t)) {
                addEdge(yf.apply(t), zf.apply(t));
            } else {
                x.promises.addLast((Element<X> u) -> {
                    addEdge(yf.apply(u), zf.apply(u));
                });
            }
        }
    }

    // test code
    public static void main(String[] args) {
        Cubic1<Id, Id> cubic = new Cubic1<>(Id::getPlist);

        // page 149
        Id p = Id.newName("p");
        Id q = Id.newName("q");
        Id x = Id.newName("x");
        Id y = Id.newName("y");
        Id z = Id.newName("z");
        Id alloc1 = Id.newName("alloc1");
        List<Id> cells = List.of(p, q, x, y, z, alloc1);

        cubic.in(alloc1, p); // alloc1 \in [p]
        cubic.subset(y, x);  // [y] <= [x]
        cubic.subset(z, x);  // [z] <= [x]
        cells.forEach((c) ->   // c \in p -> z <=c
                cubic.imply(c, p, z, c));
        cubic.subset(q, p);
        cubic.in(y, q);
        cells.forEach((c) ->
                cubic.imply(c, p, c, x));
        cubic.in(z, p);

        Property<Id, HashSet<Id>> solution = cubic.getSolution();
        cells.forEach((c) -> {
            System.out.println(c.toString() + ": " + solution.getOrInitConst(c, new HashSet<>()));
        });

        // test the second implementation
        Cubic2.Element<Id> pe = Cubic2.newElement(p);
        Cubic2.Element<Id> qe = Cubic2.newElement(q);
        Cubic2.Element<Id> xe = Cubic2.newElement(x);
        Cubic2.Element<Id> ye = Cubic2.newElement(y);
        Cubic2.Element<Id> ze = Cubic2.newElement(z);
        Cubic2.Element<Id> alloc1e = Cubic2.newElement(alloc1);
        List<Cubic2.Element<Id>> elements
                = List.of(pe, qe, xe, ye, ze, alloc1e);


//        elements.forEach(element -> {System.out.println(element);});

        // constraints
        Cubic2.addToken(alloc1e, pe);
        debug(elements, "after alloc1 \\in [p]");

        Cubic2.addEdge(ye, xe);

        debug(elements, "after [y] <= [x]");

        Cubic2.addEdge(ze, xe);

        debug(elements, "after [z] <= [x]");

        elements.forEach((Cubic2.Element<Id> ce) ->
                Cubic2.addImply(ce, pe,
                        ( zx) -> ze,
                        (zcx) -> (zcx)));

        debug(elements, "after c\\in [p] ==> [z] <= [c]");

        Cubic2.addEdge(qe, pe);

        debug(elements, "after [q] <= [p]");

        Cubic2.addToken(ye, qe);

        debug(elements, "after y \\in [q]");

        elements.forEach((Cubic2.Element<Id> ce) ->
                Cubic2.addImply(ce, pe,
                        (cxx) -> (cxx),
                        (xex) -> xe));

        debug(elements, "after c\\in [p] ==> [c] <= [x]");

        Cubic2.addToken(ze, pe);

        debug(elements, "after z \\in [p]");

    }

    static void debug(List<Cubic2.Element<Id>> elements, String info){
        System.out.println(info);
        elements.forEach(element -> {
            System.out.print("\t" +  element.toString() + ": ");
            element.printSolutions();
        });

    }
}
























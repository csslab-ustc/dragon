package util.lattice;

/* This is an imperative implementation of the PowerSet lattice.
* that is, the set is changed in-place.

// layout:

    {x1, ..., xn}
       /   \
       .....
  \            /
  {x1} ...   {xn}
       \    /
         {}

 */

import util.set.FunSet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

// X: the element type, not the set type
@SuppressWarnings("all")
public class PowerSetLattice2<X> {

    private static int numDistinctPowerSets = 0;

    // we memoize all sets that have appeared in the lattice
    // this will not only save spaces, but also speed up equality testing.
    private static HashSet<PowerSetLattice2<?>> allLattices = new HashSet<>();


    // fields
    private final HashSet<X> theSet;
    private LinkedList<Consumer<X>> promises;


    private static PowerSetLattice2 lookupOrPut(PowerSetLattice2 self){
        HashSet<PowerSetLattice2<?>> temp = allLattices;
        for (PowerSetLattice2<?> lattice : temp) {
            if (lattice.theSet.equals(self.theSet)) {
                return lattice;
            }
        }
        numDistinctPowerSets++;
        temp.add(self);
        return self;
    }


    public PowerSetLattice2(){
        this.theSet = new HashSet<>();
        this.promises = new LinkedList<>();
    }

    protected PowerSetLattice2(X x){
        this();
        this.theSet.add(x);
    }

    protected PowerSetLattice2(List<X> elements){
        this();
        this.theSet.addAll(elements);
    }

    // factory methods
    @SuppressWarnings("all")
    public static PowerSetLattice2 newEmpty(){
        PowerSetLattice2 lattice = new PowerSetLattice2<>();
        return lookupOrPut(lattice);
    }

    public static <X> PowerSetLattice2 newSingleton(X x){
        PowerSetLattice2<?> lattice = new PowerSetLattice2<>(x);
        return lookupOrPut(lattice);
    }

    public static PowerSetLattice2 newList(List list) {
        return lookupOrPut(new PowerSetLattice2<>(list));
    }

    public static PowerSetLattice2 newSet(FunSet<?> set) {
        return lookupOrPut(new PowerSetLattice2<>(set));
    }

    // init the class
    public static <X> void init() {
        allLattices = new HashSet<>();
    }

    public void addElement(X x) {
        if(this.theSet.contains(x)){
            return;
        }
        this.theSet.add(x);
        this.promises.forEach(promise -> promise.accept(x));
    }

    public void addElements(List<X> elements) {
        elements.forEach(this::addElement);
    }

    public void addElements(HashSet<X> elements) {
        elements.forEach(this::addElement);
    }

    public void addPromise(Consumer<X> p) {
        this.promises.add(p);
        this.theSet.forEach(x -> p.accept(x));
    }

    // this <= other
    // force "this" to be a subset of "other"
    public void forceSubseteq(PowerSetLattice2<X> other) {
        addPromise((X x) -> other.addElement(x));
    }

    // return a clone of the set, so that the initial set remains unchanged
    public HashSet<X> getSet() {
        return (HashSet<X>)this.theSet.clone();
    }

    public void print() {
        this.theSet.forEach(System.out::println);
        return;
    }

    public static void clear(){
        System.out.println("number of sets: " + numDistinctPowerSets);
        allLattices.clear();
        numDistinctPowerSets = 0;
    }
}












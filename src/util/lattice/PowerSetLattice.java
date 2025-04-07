package util.lattice;

/* the PowerSet lattice:

// layout:

    {x1, ..., xn}
       /   \
       .....
  \            /
  {x1} ...   {xn}
       \    /
         {}

 */

import util.Layout;
import util.Todo;
import util.set.FunSet;

import java.util.HashSet;
import java.util.List;

// X: the element type, not the set type
public class PowerSetLattice<X> {

    private static int numDistinctPowerSets = 0;

    // we memoize all sets that have appeared in the lattice
    // this will not only save spaces, but also speed up equality testing.
    private static HashSet<PowerSetLattice<?>> allLattices = new HashSet<>();

    // the current state
    private final FunSet<X> theSet;


    @SuppressWarnings("all")
    private static <X> PowerSetLattice<X> lookupOrPut(PowerSetLattice self){
        HashSet<PowerSetLattice<?>> temp = allLattices;
        for (PowerSetLattice<?> lattice : temp) {
            if (lattice.theSet.isSame(self.theSet)) {
                return (PowerSetLattice<X>)lattice;
            }
        }
        numDistinctPowerSets++;
        temp.add(self);
        return (PowerSetLattice<X>)self;
    }


    private PowerSetLattice(){
        this.theSet = new FunSet<>();
    }

    private PowerSetLattice(X x){
        this.theSet = new FunSet<>(x);
    }

    private PowerSetLattice(FunSet<X> theSet){
        this.theSet = theSet;
    }

    private PowerSetLattice(List<X> elements){
        this.theSet = new FunSet<>(elements);
    }

    // factory methods
    public static <X> PowerSetLattice<X> newEmpty(){
        PowerSetLattice<X> lattice = new PowerSetLattice<>();
        return lookupOrPut(lattice);
    }

    public static <X> PowerSetLattice<X> newSingleton(X x){
        PowerSetLattice<?> lattice = new PowerSetLattice<>(x);
        return lookupOrPut(lattice);
    }

    public static <X> PowerSetLattice<X> newList(List<X> list) {
        return lookupOrPut(new PowerSetLattice<X>(list));
    }

    public static <X> PowerSetLattice<X> newSet(FunSet<?> set) {
        return lookupOrPut(new PowerSetLattice<>(set));
    }

    // init the class
    public static <X> void init() {
        allLattices = new HashSet<>();
    }

    // least upper bound
    public PowerSetLattice<X> lub(PowerSetLattice<X> right) {
        PowerSetLattice<X> result = new PowerSetLattice<>(this.theSet.union(right.theSet));
        return lookupOrPut(result);
    }

    // greatest lower bound, which is used to create a reversed powerset.
    public PowerSetLattice<X> glb(PowerSetLattice<X> right) {
        throw new Todo(this);
//        return new PowerSetLattice<>(this.theSet.union(right.theSet));
    }

    public boolean isSame(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof PowerSetLattice)) {
            return false;
        }
        return this==o;
//        return isSameFun.apply(this.current, ((PowerSetLattice<X>) o).current);
    }

    public FunSet<X> getSet() {
        return this.theSet;
    }

    public static <X> PowerSetLattice<X> reduce(PowerSetLattice<X> start,
                                            List<PowerSetLattice<X>> latticeList) {
        return latticeList.stream().reduce(start,
                ( result, element) ->
                        newSet(result.theSet.union(element.theSet)));
    }

    public Layout.T layout() {
        return this.theSet.layout();
    }

    public static void clear(){
        System.out.println("number of sets: " + numDistinctPowerSets);
        numDistinctPowerSets = 0;
    }
}












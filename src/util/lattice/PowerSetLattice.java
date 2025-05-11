package util.lattice;

/* the PowerSet lattice:

// layout:

    {x1, ..., xn}
       /   \
       .....
    \         /
  {x1} ...   {xn}
       \    /
         {}

 */

import control.Control;
import util.Layout;
import util.set.FunSet;

import java.util.HashSet;
import java.util.List;

// X: the set element type, not the set type
public class PowerSetLattice<X> {

    // we memoize all power sets that have appeared
    // this will not only save spaces, but also speed up equality testing.
    private static HashSet<FunSet<?>> allSets = new HashSet<>();

    // the current state
    protected FunSet<X> theSet;

//    @SuppressWarnings("unchecked")
    private static <X> FunSet<X> lookupOrAdd(FunSet<X> theSet) {
        for (FunSet<?> currentSet : allSets) {
            if (currentSet.equals(theSet)) {
                return (FunSet<X>)currentSet;
            }
        }
        allSets.add(theSet);
        return theSet;
    }


    protected PowerSetLattice(){
        FunSet<X> set = lookupOrAdd(new FunSet<>());
        this.theSet = set;
    }

    protected PowerSetLattice(X x){
        FunSet<X> set = lookupOrAdd(new FunSet<>(x));
        this.theSet = set;
    }

    protected PowerSetLattice(FunSet<X> theSet){
        FunSet<X> set = lookupOrAdd(theSet);
        this.theSet = set;
    }

    protected PowerSetLattice(List<X> elements){
        FunSet<X> set = lookupOrAdd(new FunSet<>(elements));
        this.theSet = set;
    }

    // least upper bound
    public boolean mayLiftTo(PowerSetLattice<X> other) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    public FunSet<X> getSet() {
        return this.theSet;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PowerSetLattice<?> obj)) {
            return false;
        }
        // as we memoize all sets, so that equality
        // testing is address testing.
        return this.theSet==obj.theSet;
    }

    @Override
    public int hashCode() {
        return this.theSet.hashCode();
    }

    @Override
    public String toString() {
        return this.theSet.toString();
    }

    public Layout.T layout() {
        return this.theSet.layout();
    }

    public static void printBeforeClear(){
        Control.logln("number of distinct sets: " + allSets.size());
        for(FunSet<?> set : allSets){
            Control.logln(set.toString());
        }
        allSets.clear();
    }
}












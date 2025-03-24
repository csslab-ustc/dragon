package util.lattice;

// strictly speaking, "MapLattice" is NOT a new lattice, but
// a wrapper on the underlying lattice L.
// So it does not need its own lattice data structures.

import util.map.FunMap;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

// map X to the lattice L
public class MapLattice<X, L> {
    // we use a functional map because we want to
    // avoid the pain of memoization.
    public FunMap<X, L> state;

    protected MapLattice(FunMap<X, L> map) {
        this.state = map;
    }

    public MapLattice(List<X> keys, L lattice) {
        this.state = new FunMap<>();
        for(X k: keys)
            this.state = this.state.put(k, lattice);
    }

    public FunMap<X, L> lub(MapLattice<X, L> right,
                                 BiFunction<L, L, L> lub) {
        var newState = this.state.join(right.state, lub);
        return newState;
    }

    public FunMap<X, L> lub(List<MapLattice<X, L>> right,
                                BiFunction<L, L, L> lub) {
        FunMap<X, L> newMap = this.state;
        for(var lattice: right){
            newMap = newMap.join(lattice.state, lub);
        }
        return newMap;
    }

    public L get(X key) {
        return this.state.get(key);
    }

    public FunMap<X, L> put(X key, L value) {
        return this.state.put(key, value);
    }

    // we do not use "equals", because that also force us
    // to implement "hashCode"
    @Override
    public boolean equals(Object right) {
        if(!(right instanceof MapLattice))
            return false;
        return this.state.isSame(((MapLattice<X, L>)right).state);
    }

    public void print(Function<X, String> f1, Function<L, String> f2) {
        this.state.print(f1, f2);
    }
}




















package util.lattice;

// strictly speaking, "MapLattice" is NOT a new lattice, but
// a wrapper on the underlying lattice L.
// So it does not need its own lattice data structures.

import util.map.FunMap;

import java.util.List;
import java.util.function.*;

// map X to a lattice L
public class MapLattice<X, L> implements Cloneable{
    public FunMap<X, L> state;

    public MapLattice(FunMap<X, L> map) {
        this.state = map;
    }

    public MapLattice(List<X> keys, Supplier<L> latticeGenerator) {
        this.state = new FunMap<>();
        for(X k: keys)
            this.state = this.state.putData(k, latticeGenerator.get());
    }

    public boolean mayLiftTo(MapLattice<X, L> other,
                          BiFunction<L, L, Boolean> liftConsumer) {
        boolean changed = false;
        for(X k: this.state.keySet()){
            var lattice1 = this.state.get(k);
            var lattice2 = other.state.get(k);
            if (liftConsumer.apply(lattice1, lattice2))
                changed = true;
        }
        return changed;
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

    public void update(X key, L value) {
        this.state.put(key, value);
    }

    @Override
    public boolean equals(Object right) {
        if(!(right instanceof MapLattice<?, ?> obj))
            return false;
        return this.state.equals(obj.state);
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapLattice<X, L> clone() {
        try {
            var x = (MapLattice<X, L>)super.clone();
            x.state = (FunMap<X, L>)state.clone();
            return x;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return this.state.toString();
    }
}




















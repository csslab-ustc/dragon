package util.lattice;

/* the two point lattice:

     T
     |
    _|_

 */

import util.Error;

public class TwoPointLattice<Names extends TwoPointLattice.N> {
    // names
    public interface N{
        String toString(TwoPointLattice.T t);
    }
    private final Class<?> nameOfClass;

    // possible states
    public sealed interface T
        permits Bot, Top{
    }
    public record Top() implements T {}
    public record Bot() implements T {}

    // the state
    final T state;

    @SafeVarargs
    public TwoPointLattice(T state, Names... names) {
        this.state = state;
        // only to pass lattice names, ugly
        if(names.length != 0){
            throw new Error("do not call this constructor with nonempty names");
        }
        this.nameOfClass = names.getClass().componentType();
    }

    // least upper bound
    public TwoPointLattice<Names> lub(TwoPointLattice<Names> other) {
        switch (this.state) {
            case Bot() -> {
                return new TwoPointLattice<>(other.state);
            }
            case Top() -> {
                return new TwoPointLattice<>(this.state);
            }
        }
    }

    public boolean isTop(){
        return switch (this.state) {
            case TwoPointLattice.Bot() -> false;
            case TwoPointLattice.Top() -> true;
        };
    }

    public boolean isBot(){
        return switch (this.state) {
            case TwoPointLattice.Bot() -> true;
            case TwoPointLattice.Top() -> false;
        };
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TwoPointLattice.T obj)){
            return false;
        }
        return this.state.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    @Override
    public String toString() {
        String s;
        try {
           s =  ((N)nameOfClass.getDeclaredConstructor().newInstance()).toString(this.state);
        }catch (Exception e){
            throw new Error(e);
        }
        return s;
    }
}





















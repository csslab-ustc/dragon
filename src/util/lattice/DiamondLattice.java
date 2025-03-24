package util.lattice;

/* the Diamond lattice:

     T
   /   \
  M0     M1
   \    /
    _|_

 */

import util.Error;

public class DiamondLattice<Names extends DiamondLattice.N> {
    // names
    public interface N{
        String toString(DiamondLattice.T t);
    }
    private final Class<?> nameOfClass;

    // possible states
    public sealed interface T
        permits Bot, M0, M1, Top{
    }
    public record Top() implements T {}
    public record M0() implements T {}
    public record M1() implements T {}
    public record Bot() implements T {}

    // current state
    public final T state;

    @SafeVarargs
    public DiamondLattice(T state, Names... names) {
        this.state = state;
        // only to pass lattice names, ugly
        if(names.length != 0){
            throw new Error("do not call this constructor with nonempty names");
        }
        this.nameOfClass = names.getClass().componentType();
    }

    // least upper bound: |_|
    public T lub(DiamondLattice<Names> other) {
        switch (this.state) {
            case Bot() -> {
                return other.state;
            }
            case M0() -> {
                switch (other.state){
                    case Bot(), M0() -> {
                        return this.state;
                    }
                    case M1(), Top() ->{
                        return new Top();
                    }
                }
            }
            case M1() -> {
                switch (other.state){
                    case Bot(), M1() -> {
                        return this.state;
                    }
                    case M0(), Top() ->{
                        return new Top();
                    }
                }
            }
            case Top() -> {
                return this.state;
            }
        }
    }

    public boolean isTop(){
        return this.state.getClass().equals(Top.class);
    }

    public boolean isM0(){
        return this.state.getClass().equals(M0.class);
    }

    public boolean isBot(){
        return this.state.getClass().equals(Bot.class);
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof DiamondLattice<?> obj)){
            return false;
        }
        return this.state.equals(obj.state);
    }

    @Override
    public int hashCode(){
        return this.state.hashCode();
    }

    @Override
    public String toString(){
        String s;
        try {
            s =  ((DiamondLattice.N)nameOfClass.getDeclaredConstructor().newInstance()).toString(this.state);
        }catch (Exception e){
            throw new Error(e);
        }
        return s;
    }

}





















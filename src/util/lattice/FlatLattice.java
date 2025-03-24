package util.lattice;

/* flat lattice:

              T
       /   / ...    \       \
       x1 x2        x_{n-1}  xn
        \   \ ...   /      /
             _|_
     */

import util.Error;

public class FlatLattice<X, Names extends FlatLattice.N> {
    // names
    public interface N{
        String toString(FlatLattice.T t);
    }
    private final Class<?> nameOfClass;

    public sealed interface T
        permits Top, Middle, Bot{
    }

    public record Top() implements T{}
    public record Middle<X>(X data) implements T{
    }
    public record Bot() implements T{}

    // the state:
    public final T state;

    @SafeVarargs
    public FlatLattice(T t, Names... names) {
        this.state = t;
        // pass names
        if(names.length != 0){
            throw new Error("do not call this constructor with nonempty names");
        }
        this.nameOfClass = names.getClass().componentType();
    }

    // least upper bound:
    public T lub(FlatLattice<X, Names> right){
        switch(this.state){
            case Bot() -> {
                return right.state;
            }
            case Middle(var data) -> {
                switch (right.state){
                    case Bot() ->{
                        return this.state;
                    }
                    case Middle(var data2)->{
                        if(data.equals(data2))
                            return this.state;
                        return new Top();
                    }
                    case Top()->{
                        return right.state;
                    }
                }
            }
            case Top() -> {
                return this.state;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof FlatLattice.T obj)){
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
            s =  ((FlatLattice.N)nameOfClass.getDeclaredConstructor().newInstance()).toString(this.state);
        }catch (Exception e){
            throw new Error(e);
        }
        return s;
    }

}




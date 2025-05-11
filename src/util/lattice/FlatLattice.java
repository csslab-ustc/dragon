package util.lattice;

/* flat lattice:

              T
       /   / ...    \       \
       x1 x2        x_{n-1}  xn
        \   \ ...   /      /
             _|_
     */

import util.Layout;

public class FlatLattice<X> {

    // all possible states:
    public sealed interface T
        permits Top, Middle, Bot{
    }

    public record Top() implements T{}
    public record Middle<X>(X data) implements T{
    }
    public record Bot() implements T{}

    // current state:
    protected T state;

    public FlatLattice(T t) {
        this.state = t;
    }

    // least upper bound:
    public T lub(FlatLattice<X> right){
        // TODO: please add your code:
        throw new util.Todo();

    }

    public boolean mayLiftTo(FlatLattice<X> other){
        // TODO: please add your code:
        throw new util.Todo();

    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof FlatLattice<?> obj)){
            return false;
        }
        return this.state.equals(obj.state);
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    @Override
    public String toString() {
        return switch (this.state){
            case Bot() -> "bot";
            case Middle(var data) -> "middle<" + data.toString() + ">";
            case Top() -> "top";
        };
    }

    public Layout.T layout(){
        return Layout.str(this.toString());
    }

}




















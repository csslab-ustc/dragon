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
    public T state;

    public FlatLattice(T t) {
        this.state = t;
    }

    // least upper bound:
    public T lub(FlatLattice<X> right){
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

    public boolean mayLiftTo(FlatLattice<X> other){
        switch(this.state){
            case Bot() -> {
                switch (other.state){
                    case Bot() ->{
                        return false;
                    }
                    case Middle(var data2)->{
                        this.state = new Middle<>(data2);
                        return true;
                    }
                    case Top()->{
                        this.state = new Top();
                        return true;
                    }
                }
            }
            case Middle(var data) -> {
                switch (other.state){
                    case Bot() ->{
                        return false;
                    }
                    case Middle(var data2)->{
                        if(data.equals(data2))
                            return false;
                        this.state = new Middle<>(data2);
                        return true;
                    }
                    case Top()->{
                        this.state = new Top();
                        return true;
                    }
                }
            }
            case Top() -> {
                return false;
            }
        }
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




















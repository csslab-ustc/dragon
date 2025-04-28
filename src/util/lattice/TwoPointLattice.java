package util.lattice;

/* the two point lattice:

     T
     |
    _|_

 */

import util.Error;
import util.Layout;

public class TwoPointLattice {
    // possible states
    public sealed interface T
        permits Bot, Top{
    }
    public record Top() implements T {}
    public record Bot() implements T {}

    // the state
    T state;

    public TwoPointLattice(T state) {
        this.state = state;
    }

    // least upper bound
    public T lub(TwoPointLattice other) {
        switch (this.state) {
            case Bot() -> {
                return other.state;
            }
            case Top() -> {
                return this.state;
            }
        }
    }

    public boolean mayLiftTo(TwoPointLattice other) {
        switch (this.state) {
            case Bot() -> {
                switch (other.state){
                    case Bot() -> {
                        return false;
                    }
                    case Top() -> {
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

    public boolean isTop(){
        return switch (this.state) {
            case Bot() -> false;
            case Top() -> true;
        };
    }

    public boolean isBot(){
        return switch (this.state) {
            case Bot() -> true;
            case Top() -> false;
        };
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TwoPointLattice obj)){
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
            case Top() -> "top";
        };
    }

    public Layout.T layout(){
        return Layout.str(this.toString());
    }
}





















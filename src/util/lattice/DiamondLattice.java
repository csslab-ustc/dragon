package util.lattice;

/* the Diamond lattice:
this is really a special form of flat lattice

     T
   /   \
  M0     M1
   \    /
    _|_

 */

public class DiamondLattice {
    // possible states
    public sealed interface T
        permits Bot, M0, M1, Top{
    }
    public record Top() implements T {}
    public record M0() implements T {}
    public record M1() implements T {}
    public record Bot() implements T {}

    // current state
    public T state;

    public DiamondLattice(T state) {
        this.state = state;
        // only to pass lattice names, ugly
    }

    // least upper bound: |_|
    public T lub(DiamondLattice other) {
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

    // lift "this" at least to "other"
    public Boolean mayLiftTo(DiamondLattice other) {
        switch (this.state) {
            case Bot() -> {
                switch (other.state) {
                    case Bot() -> {
                        return false;
                    }
                    default -> {
                        this.state = other.state;
                        return true;
                    }
                }
            }
            case M0() -> {
                switch (other.state){
                    case Bot(), M0() -> {
                        return false;
                    }
                    case M1(), Top() -> {
                        this.state = new Top();
                        return true;
                    }
                }
            }
            case M1() -> {
                switch (other.state){
                    case Bot(), M1() -> {
                        return false;
                    }
                    case M0(), Top() -> {
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
        return switch (this.state){
            case Top() -> true;
            default -> false;
        };
    }

    public boolean isM0(){
        return switch (this.state){
            case M0() -> true;
            default -> false;
        };
    }

    public boolean isBot(){
        return switch (this.state){
            case Bot() -> true;
            default -> false;
        };
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof DiamondLattice obj)){
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
        return switch (this.state) {
            case Bot() -> "Bot";
            case M0() -> "M0";
            case M1() -> "M1";
            case Top() -> "Top";
        };
    }

}





















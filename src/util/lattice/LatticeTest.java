package util.lattice;


public class LatticeTest {

    static class IntFlatLattice extends FlatLattice<Integer> {
        public IntFlatLattice(T t) {
            super(t);
        }

        // factory methods
        public static IntFlatLattice newNone() {
            return new IntFlatLattice(new Bot());
        }

        public static IntFlatLattice newSingleton(Integer n) {
            return new IntFlatLattice(new Middle<>(n));
        }

        public static IntFlatLattice newMany() {
            return new IntFlatLattice(new Top());
        }

        public IntFlatLattice lub(IntFlatLattice other) {
            return new IntFlatLattice(super.lub(other));
        }

        public boolean mayLiftTo(IntFlatLattice other) {
            return super.mayLiftTo((FlatLattice)other);
        }

        @Override
        public String toString() {
            return switch (this.state){
                case Bot() -> "no-value";
                case FlatLattice.Middle(var v) -> "int<"+v.toString()+">";
                case FlatLattice.Top() -> "many-value";
            };
        }
    }


    static class NullLattice extends TwoPointLattice {

        public NullLattice(T state) {
            super(state);
        }

        // factory methods
        public static NullLattice newNull() {
            return new NullLattice(new Bot());
        }

        public static NullLattice newNoneNull() {
            return new NullLattice(new Top());
        }

        public NullLattice lub(NullLattice other){
            return new NullLattice(super.lub(other));
        }

        public void mayLiftTo(NullLattice other){
            super.mayLiftTo(other);
        }

        @Override
        public String toString() {
            return switch (this.state) {
                case Top() -> "none-null";
                case Bot() -> "null";
            };
        }
    }

    @org.junit.jupiter.api.Test
    public void f() {
        // test the flat lattice
        {
            var l1 = IntFlatLattice.newNone();
            var l2 = IntFlatLattice.newNone();
            var l3 = IntFlatLattice.newSingleton(42);
            var l4 = IntFlatLattice.newSingleton(42);
            System.out.println(l1.lub(l2).lub(l3).lub(l4));

        }

        // test the two point Lattice
        var n1 = NullLattice.newNull();
        System.out.println(n1);
        var n2 = NullLattice.newNull();
        System.out.println(n2);

        var n3 = n1.lub(n2);
        System.out.println(n3);

    }
}

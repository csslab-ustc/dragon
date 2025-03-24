package util.lattice;

import java.util.function.Function;


public class Test {
    static class IntFlatLatticeNames implements FlatLattice.N{
        @Override
        public String toString(FlatLattice.T t) {
            return switch (t){
                case FlatLattice.Bot bot -> "no-value";
                case FlatLattice.Middle(var v) -> "value("+v.toString()+")";
                case FlatLattice.Top top -> "many-value";
            };
        }
    }
    static class IntFlatLattice extends FlatLattice<Integer, IntFlatLatticeNames> {
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
    }

    static class ZeroNames implements DiamondLattice.N {
        public String toString(DiamondLattice.T t) {
            return switch (t){
                case DiamondLattice.Top() -> "many";
                case DiamondLattice.M0() -> "zero";
                case DiamondLattice.M1() -> "nonZero";
                case DiamondLattice.Bot() -> "none";
            };
        }

    }
    static class ZeroLattice extends DiamondLattice<ZeroNames> {
        public ZeroLattice(T s) {
            super(s);
        }

        // factory methods
        public static ZeroLattice newMany() {
            return new ZeroLattice(new Top());
        }

        public static ZeroLattice newZero() {
            return new ZeroLattice(new M0());
        }

        public static ZeroLattice newNonZero() {
            return new ZeroLattice(new M1());
        }

        public static ZeroLattice newNone() {
            return new ZeroLattice(new Bot());
        }

        public ZeroLattice lift(ZeroLattice other) {
            return new ZeroLattice(this.lub(other));
        }
    }

    static class NullLatticeNames implements TwoPointLattice.N {
        public String toString(TwoPointLattice.T x) {
            return switch (x) {
                case TwoPointLattice.Top() -> "none-null";
                case TwoPointLattice.Bot() -> "null";
            };
        }
    }
    static class NullLattice extends TwoPointLattice<NullLatticeNames> {

        public NullLattice(T state) {
            super(state);
        }

        // factory methods
        public static NullLattice newNull() {
            return new  NullLattice(new Bot());
        }

        public static NullLattice newNonNull() {
            return new  NullLattice(new Top());
        }

        public NullLattice lub(NullLattice other){
            return new NullLattice(super.lub(other).state);
        }
    }

    public static void main(String[] args) {
        // test the flat lattice

        {
            var l1 = IntFlatLattice.newNone();
            var l2 = IntFlatLattice.newNone();
            var l3 = IntFlatLattice.newSingleton(42);
            var l4 = IntFlatLattice.newSingleton(42);
            System.out.println(l1.lub(l2).lub(l3).lub(l4));

        }

        // test the diamond Lattice



        var a = ZeroLattice.newMany();
        System.out.println(a);


        // test the diamond Lattice



        var n1 = NullLattice.newNull();
        System.out.println(n1);
        var n2 = NullLattice.newNull();
        System.out.println(n2);

        var n3 = n1.lub(n2);
        System.out.println(n3);

    }
}

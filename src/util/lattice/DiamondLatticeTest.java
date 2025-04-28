package util.lattice;

import org.junit.jupiter.api.Assertions;

public class DiamondLatticeTest {

    static class ZeroLattice extends DiamondLattice {
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

        public static ZeroLattice newNoneZero() {
            return new ZeroLattice(new M1());
        }

        public static ZeroLattice newNone() {
            return new ZeroLattice(new Bot());
        }

        public ZeroLattice lub(ZeroLattice other) {
            return new ZeroLattice(super.lub(other));
        }

        @Override
        public String toString() {
            return switch (this.state){
                case Bot() -> "Unknown";
                case M0() -> "Zero";
                case M1() -> "NoneZero";
                case Top() -> "Any";
            };
        }
    }

    @org.junit.jupiter.api.Test
    public void f() {
        // test the diamond Lattice
        var a = ZeroLattice.newNoneZero();
        var b = ZeroLattice.newZero();
        a.mayLiftTo(b);
        Assertions.assertTrue(a.isTop());


    }
}

package util;

import cfg.Cfg;
import control.Control;

import java.util.function.Consumer;
import java.util.function.Function;

public record Trace<X, Y>(String name,
                          Function<X, Y> f,
                          X x,
                          Consumer<X> consumeX,
                          Consumer<Y> consumeY) {
    public Y doit() {
        boolean isTraced = Control.isBeingTraced(name);
        if (isTraced) {
            System.out.println("before " + this.name);
            consumeX.accept(x);
        }
        Y y = f.apply(x);
        if (isTraced) {
            System.out.println("after " + this.name);
            consumeY.accept(y);
        }
        if (Control.Dot.beingDotted(name)) {
            Cfg.Program.dot((Cfg.Program.T) y);
        }
        return y;
    }
}
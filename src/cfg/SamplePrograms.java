package cfg;

import cfg.Cfg.*;
import util.Id;
import util.Label;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SamplePrograms {
    // Lab2, exercise 2: read the following code and make
    // sure you understand how the sample program "test/Factorial.java" is
    // encoded.

    // /////////////////////////////////////////////////////
    // To represent the "Factorial.java" program in memory manually
    // this is for demonstration purpose only, and
    // no one would want to do this in reality (boring and error-prone).
    /*
     * class Factorial {
     *     public static void main(String[] a) {
     *         System.out.println(new Fac().ComputeFac(10));
     *     }
     * }
     *
     * class Fac {
     *     public int ComputeFac(int num) {
     *         int num_aux;
     *         if (num < 1)
     *             num_aux = 1;
     *         else
     *             num_aux = num * (this.ComputeFac(num-1));
     *         return num_aux;
     *     }
     * }
     */

    // a new test case for value number
    static Id a = Id.newName("a");
    static Id b = Id.newName("b");
    static Id c = Id.newName("c");
    static Id d = Id.newName("d");

    static Label l0 = new Label();
    static Label l1 = new Label();
    static Label l2 = new Label();

    static Block.T value_bb1 = new Block.Singleton(l1,
            List.of(new Stm.Assign(a,
                            new Exp.Bop(BinaryOperator.T.Add,
                                    List.of(b, c))),
                    new Stm.Assign(b,
                            new Exp.Bop(BinaryOperator.T.Sub,
                                    List.of(a, d))),
                    new Stm.Assign(c,
                            new Exp.Bop(BinaryOperator.T.Add,
                                    List.of(b, c))),
                    new Stm.Assign(d,
                            new Exp.Bop(BinaryOperator.T.Sub,
                                    List.of(a, d)))),
            new Transfer.Ret(d));
    static Block.T value_bb0 = new Block.Singleton(l0,
            new ArrayList<>(List.of(new Stm.Assign(a,
                            new Exp.Bop(BinaryOperator.T.Add,
                                    List.of(b, c))),
                    new Stm.Assign(a,
                            new Exp.Bop(BinaryOperator.T.Add,
                                    List.of(b, c))),
                    new Stm.Assign(a,
                            new Exp.Bop(BinaryOperator.T.Add,
                                    List.of(b, c))),
                    new Stm.Assign(a,
                            new Exp.Bop(BinaryOperator.T.Add,
                                    List.of(b, c))))),
            new Transfer.Jmp(l1));

    static Function.T doitValue = new Function.Singleton(
            new Type.Int(), // return type
            Id.newName("doitValue"), // id
            new ArrayList<>(List.<Dec.T>of()), // formals
            Stream.of(new Dec.Singleton(new Type.Int(), a),
                    new Dec.Singleton(new Type.Int(), b),
                    new Dec.Singleton(new Type.Int(), c),
                    new Dec.Singleton(new Type.Int(), d)).collect(Collectors.toCollection(ArrayList::new)), // locals
            List.of(value_bb0, value_bb1),
            l0,
            l1);
    public static Program.T valueNum = new Program.Singleton(List.of(doitValue));

}
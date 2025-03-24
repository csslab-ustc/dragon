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

    // // class "Fac"
//    static Function.T fac = new Function.Singleton(
//                    Type.getInt(),
//                    Id.newName("fac")),
//                    List.of(new Dec.Singleton(Type.getInt(), new AstId(Id.newName("num")))),
//                    List.of(new Dec.Singleton(Type.getInt(), new AstId(Id.newName("num_aux")))),
//                    List.of(new If(
//                            new Bop(new ExpId(new AstId(Id.newName("num"))),
//                                    BinaryOperator.T.Lt,
//                                    new Num(1)),
//                            new Assign(new AstId(Id.newName("num_aux")), new Num(1)),
//                            new Assign(
//                                    new AstId(Id.newName("num_aux")),
//                                    new Bop(new ExpId(new AstId(Id.newName("num"))),
//                                            BinaryOperator.T.Mul,
//                                            new Call(new AstId(Id.newName("ComputeFac")),
//                                                    List.of(new Bop(new ExpId(new AstId(Id.newName("num"))),
//                                                            BinaryOperator.T.Sub,
//                                                            new Num(1))),
//                                                    new Tuple.One<>(),
//                                                    new Tuple.One<>()))))),
//                    new ExpId(new AstId(Id.newName("num_aux"))));
//
//    // program
//    public static Program.T progFac = new Program.Singleton(List.of(fac));


    // to encode "test/SumRec.java"
//    class SumRec {
//        public static void main(String[] a) {
//            System.out.println(new Doit().doit(100));
//        }
//    }
//
//    class Doit {
//        public int doit(int n) {
//            int sum;
//            if (n < 1)
//                sum = 0;
//            else
//                sum = n + (this.doit(n - 1));
//            return sum;
//        }
//    }

    // // class "Doit"
    static Label sum_l0 = new Label();
    static Label sum_l1 = new Label();
    static Label sum_l2 = new Label();
    static Label sum_l3 = new Label();

    static Block.T fac_bb3 = new Block.Singleton(sum_l3,
            List.of(),
            new Transfer.Ret(Id.newName("sum"))
    );
    static Block.T fac_bb1 = new Block.Singleton(sum_l1,
            List.of(new Stm.Assign(Id.newName("sum"),
                    new Exp.Int(1))),
            new Transfer.Jmp(sum_l3)
    );
    static Block.T fac_bb2 = new Block.Singleton(sum_l2,
            List.of(new Stm.Assign(Id.newName("%x_2"),
                            new Exp.Int(1)),
                    new Stm.Assign(Id.newName("%x_3"),
                            new Exp.Bop(BinaryOperator.T.Sub,
                                    List.of(Id.newName("n"),
                                            Id.newName("%x_2")))),
                    new Stm.Assign(Id.newName("%x_4"),
                    new Exp.Call(Id.newName("sumRec"),
                            List.of(Id.newName("%x_3")))),
                    new Stm.Assign(Id.newName("sum"),
                            new Exp.Bop(BinaryOperator.T.Add,
                                    List.of(Id.newName("n"),
                                            Id.newName("%x_4"))))),
            new Transfer.Jmp(sum_l3)
    );
    static Block.T fac_bb0 = new Block.Singleton(sum_l0,
            List.of(new Stm.Assign(Id.newName("%x_1"),
                    new Exp.Int(1)),
                    new Stm.Assign(Id.newName("%x_0"),
                    new Exp.Bop(BinaryOperator.T.Lt,
                            List.of(Id.newName("n"),
                                    Id.newName("%x_1"))))),
            new Transfer.If(Id.newName("%x_0"), sum_l1, sum_l2));

    static Function.T doitSumRec = new Function.Singleton(
                    new Type.Int(), // return type
                    Id.newName("sumRec"), // id
                    List.of(new Dec.Singleton(new Type.Int(), Id.newName("n"))), // formals
                    List.of(new Dec.Singleton(new Type.Int(), Id.newName("sum")),
                            new Dec.Singleton(new Type.Int(), Id.newName("%x_0")),
                            new Dec.Singleton(new Type.Int(), Id.newName("%x_1")),
                            new Dec.Singleton(new Type.Int(), Id.newName("%x_2")),
                            new Dec.Singleton(new Type.Int(), Id.newName("%x_3")),
                            new Dec.Singleton(new Type.Int(), Id.newName("%x_4"))), // locals
                    List.of(fac_bb0, fac_bb1, fac_bb2, fac_bb3),
            sum_l0,
            sum_l3);

    public static Program.T progSumRec = new Program.Singleton(List.of(doitSumRec));


    // program 4.1
    static class ProgZero {
        static Label l0 = new Label();
        static Label l1 = new Label();
        static Label l2 = new Label();
        static Label l3 = new Label();

        static Id n = Id.newName("n");
        static Id k = Id.newName("k");
        static Id z = Id.newName("z");

        static Block.T zero_bb3 = new Block.Singleton(l3,
                List.of(new Stm.Assign(k,
                        new Exp.Bop(BinaryOperator.T.Div,
                                List.of(n, z)))),
                new Transfer.Ret(k));
        static Block.T zero_bb1 = new Block.Singleton(l1,
                List.of(new Stm.Assign(z,
                        new Exp.Int(3))),
                new Transfer.Jmp(l3));
        static Block.T zero_bb2 = new Block.Singleton(l2,
                List.of(new Stm.Assign(z,
                        new Exp.Int(3))),
                new Transfer.Jmp(l3));
        static Block.T zero_bb0 = new Block.Singleton(l0,
                List.of(new Stm.Assign(Id.newName("n"),
                                new Exp.Int(1)),
                        new Stm.Assign(Id.newName("%x_0"),
                                new Exp.Eid(Id.newName("n"))),
                        new Stm.Assign(Id.newName("%x_1"),
                                new Exp.Bop(BinaryOperator.T.Add,
                                        List.of(Id.newName("n"),
                                                Id.newName("%x_0"))))),
                new Transfer.If(Id.newName("%x_1"),
                        l1,
                        l2));

        static Function.T doitZero = new Function.Singleton(
                new Type.Int(), // return type
                Id.newName("doitZero"), // id
                List.of(new Dec.Singleton(new Type.Int(), Id.newName("n"))), // formals
                List.of(new Dec.Singleton(new Type.Int(), Id.newName("k")),
                        new Dec.Singleton(new Type.Int(), Id.newName("%x_0")),
                        new Dec.Singleton(new Type.Int(), Id.newName("%x_1")),
                        new Dec.Singleton(new Type.Int(), Id.newName("z"))), // locals
                List.of(zero_bb0, zero_bb1, zero_bb2, zero_bb3),
                l0,
                l3);
        public static Program.T progZero = new Program.Singleton(List.of(doitZero));
    }


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
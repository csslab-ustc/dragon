package util.set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Layout;

import java.util.List;

public class FunSetTest {

    @Test
    public void test() {
        FunSet<String> set1 = new FunSet<>();
        FunSet<String> set2 = set1.addList(List.of("a", "b", "c", "d", "a", "c"));

        System.out.print("\nset1: ");
        Layout.print(set1.layout(), System.out::print, Layout.Style.C);
        System.out.print("\nset2: ");
        Layout.print(set2.layout(), System.out::print, Layout.Style.C);

        FunSet<String> set3 = set1.addData("a").addData("b");
        System.out.print("\nset3: ");
        Layout.print(set3.layout(), System.out::print, Layout.Style.C);

        var b = set2.equals(set3);
        Assertions.assertNotEquals(set2, set3);
        System.out.print("\nequals(set2, set3) = " + b);

        // set2 /\ set3
        Assertions.assertEquals(FunSet.retainSets(set2, set3),
                new FunSet<>(List.of("a", "b")));

        FunSet<String> set4 = set2.removeData("d").removeData("c");
        System.out.print("\nset4: ");
        Layout.print(set4.layout(), System.out::print, Layout.Style.C);
        b = set3.equals(set4);
        Assertions.assertTrue(b);
        System.out.print("\nequals(set3, set4) = " + b);
    }
}

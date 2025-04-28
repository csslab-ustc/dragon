package util.map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Id;

public class FunMapTest {

    @Test
    public void f() {
        FunMap<String, Integer> map1 = new FunMap<>();
        map1.print(String::toString, (x) -> Integer.toString(x));
        int n = 1;
        var map2 = map1.putData("a", n++).
                putData("b", n++).
                putData("c", n++).
                putData("d", n++).
                putData("e", n++);
        map2.print();

        System.out.println(map1.equals(map2));
        n = 1;
        var map21 = map1.putData("a", n++).
                putData("b", n++).
                putData("c", n++).
                putData("d", n++).
                putData("e", n++);
        map21.print();
        Assertions.assertEquals(map2, map21);

        var map3 = map2.putData("a", 9);
        Assertions.assertEquals(map2, map21);
        map3.print();
        Assertions.assertNotEquals(map2, map3);

        FunMap<Id, Integer> map4 = new FunMap<>();
        var map5 = map4.putData(Id.newName("a"), 3);
        map5.print();
        var map6 = map4.putData(Id.newName("a"), 4);
        map6.print();
        Assertions.assertNotEquals(map5, map6);


    }
}

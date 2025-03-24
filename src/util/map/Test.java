package util.map;

import util.Id;

public class Test {
    public static void main(String[] args) {
        FunMap<String, Integer> map1 = new FunMap<>();
        map1.print(String::toString, (x) -> Integer.toString(x));
        var map2 = map1.put("a", 1).
                put("b", 2).put("c", 3).
                put("d", 4).put("e", 5);
        map2.print();

        System.out.println(map1.isSame(map2));
        System.out.println(map2.isSame(map2));


        var map3 = map2.put("a", 9);
        map3.print();

        System.out.print(map2.isSame(map3));

        FunMap<Id, Integer> map4 = new FunMap<>();
        var map5 = map4.put(Id.newName("a"), 3);
        map5.print();
        var map6 = map4.put(Id.newName("a"), 4);

        map6.print();




    }
}

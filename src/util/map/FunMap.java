package util.map;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

// a functional map.
public class FunMap<X, Y> extends HashMap<X, Y> {

    public FunMap() {
        super();
    }
//    public FunMap(X x) {
//        this.map = new java.util.HashSet<>();
//        this.map.add(x);
//    }
//    public FunMap(List<X> list) {
//        this.map = new java.util.HashSet<>();
//        this.map.addAll(list);
//    }
//    // add a dummy parameter to disable JVM errors
//    public FunMap(List<FunMap<X, Y>> sets, boolean b) {
//        this.map = new java.util.HashSet<>();
//        sets.forEach(set -> this.map.addAll(set.map));
//    }

    // t = s + {k: v}
    public FunMap<X, Y> putData(X k, Y v) {
        var newMap = (FunMap<X, Y>)this.clone();
        newMap.put(k, v);
        return newMap;
    }

    public FunMap<X, Y> join(FunMap<X, Y> theMap,
                             BiFunction<Y, Y, Y> f) {
        FunMap<X, Y> newMap = new FunMap<>();
        this.forEach((k, v) -> {
            Y value = theMap.get(k);
            Y newValue = f.apply(v, value);
            newMap.put(k, newValue);
        });
        return newMap;
    }

    public int size() {
        return super.size();
    }

    public void print(){
        System.out.print("{");
        this.forEach((key, value) -> {
            System.out.print(key.toString());
            System.out.print(" -> ");
            System.out.print(value.toString());
            System.out.print(", ");
        });
        System.out.println("}");
    }

    public void print(Function<X, String> f1,
                      Function<Y, String> f2){
        System.out.print("{");
        this.forEach((key, value) -> {
            System.out.print(f1.apply(key));
            System.out.print(" -> ");
            System.out.print(f2.apply(value));
            System.out.print(", ");
        });
        System.out.println("}");
    }
}



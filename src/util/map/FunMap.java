package util.map;

import util.Error;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

// a functional map.
public class FunMap<X, Y> {
    private final java.util.HashMap<X, Y> map;

    public FunMap() {
        this.map = new java.util.HashMap<>();
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

    @SuppressWarnings("unchecked")
    private FunMap(FunMap<X, Y> theMap) {
        try {
            this.map = (java.util.HashMap<X, Y>) theMap.map.clone();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    // t = s + {k: v}
    public FunMap<X, Y> put(X k, Y v) {
        var newMap = new FunMap<>(this);
        newMap.map.put(k, v);
        return newMap;
    }

    public FunMap<X, Y> join(FunMap<X, Y> theMap,
                             BiFunction<Y, Y, Y> f) {
        FunMap<X, Y> newMap = new FunMap<>();
        this.map.forEach((k, v) -> {
            Y value = theMap.get(k);
            Y newValue = f.apply(v, value);
            newMap.map.put(k, newValue);
        });
        return newMap;
    }

    public Y get(X k) {
        return this.map.get(k);
    }

    // we don't want to overwrite "equals",
    // because we also need to overwrite "hashCode" otherwise.
    public boolean isSame(FunMap<X, Y> rightMap) {
        if (rightMap == null)
            return false;
        if (this.map.size() != rightMap.map.size())
            return false;
        for(Map.Entry<X, Y> entry: this.map.entrySet()) {
            X thisKey = entry.getKey();
            Y thisValue = entry.getValue();
            Y rightValue = rightMap.get(thisKey);
            if (thisValue == null || !thisValue.equals(rightValue)) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return this.map.size();
    }

    public void print(){
        System.out.print("{");
        this.map.forEach((key, value) -> {
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
        this.map.forEach((key, value) -> {
            System.out.print(f1.apply(key));
            System.out.print(" -> ");
            System.out.print(f2.apply(value));
            System.out.print(", ");
        });
        System.out.println("}");
    }
}



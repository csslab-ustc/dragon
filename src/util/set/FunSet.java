package util.set;

import util.Error;
import util.Layout;

import java.util.List;
import java.util.stream.Collectors;

// a functional set.
public class FunSet<X> {
    private static int numSet = 0;

    private final java.util.HashSet<X> set;

    public FunSet() {
        numSet++;
        this.set = new java.util.HashSet<>();
    }
    public FunSet(X x) {
        numSet++;
        this.set = new java.util.HashSet<>();
        this.set.add(x);
    }
    public FunSet(List<X> list) {
        numSet++;
        this.set = new java.util.HashSet<>();
        this.set.addAll(list);
    }
    // add a dummy parameter to disable JVM errors
    public FunSet(List<FunSet<X>> sets, boolean b) {
        numSet++;
        this.set = new java.util.HashSet<>();
        sets.forEach(set -> this.set.addAll(set.set));
    }

    @SuppressWarnings("unchecked")
    private FunSet(FunSet<X> theSet) {
        numSet++;
        try {
            this.set = (java.util.HashSet<X>) theSet.set.clone();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    // s \/ {data}
    public FunSet<X> add(X data) {
        var targetSet = new FunSet<>(this);
        targetSet.set.add(data);
        return targetSet;
    }

    // s - {data}
    public FunSet<X> remove(X data) {
        var targetSet = new FunSet<>(this);
        targetSet.set.remove(data);
        targetSet.set.add(data);
        return targetSet;
    }

    // s \/ [data]
    public FunSet<X> addList(List<X> list) {
        var targetSet = new FunSet<>(this);
        targetSet.set.addAll(list);
        return targetSet;
    }

    // s1 \/ s2
    public FunSet<X> union(FunSet<X> theSet) {
        var targetSet = new FunSet<>(this);
        targetSet.set.addAll(theSet.set);
        return targetSet;
    }

    // \/ {s1, s2, ..., sn}
    public static <X> FunSet<X> unionList(List<FunSet<X>> theSets) {
        var targetSet = new FunSet<X>();
        for (FunSet<X> theSet : theSets)
            targetSet.set.addAll(theSet.set);
        return targetSet;
    }

    // s1 - s2
    public FunSet<X> sub(FunSet<X> theSet) {
        var targetSet = new FunSet<>(this);
        targetSet.set.removeAll(theSet.set);
        return targetSet;
    }

    public boolean contains(X data) {
        return this.set.contains(data);
    }

    public boolean isSame(FunSet<X> theSet) {
        if (theSet == null)
            return false;
        if (this.set.size() != theSet.set.size())
            return false;
        for (X data : this.set) {
            if (!theSet.set.contains(data))
                return false;
        }
        return true;
    }

    public List<X> toList() {
        return this.set.stream().toList();
    }

    public int size() {
        return this.set.size();
    }

    public Layout.T layout(){
        return Layout.halign(List.of(Layout.str("{"),
                Layout.halignSepRight(Layout.str(", "),
                        this.set.stream().map(x -> Layout.str(x.toString())).collect(Collectors.toList())),
                Layout.str("}")));
    }

    public static Layout.T status(){
        String info = "number of sets: = " + numSet;
        numSet = 0;
        return Layout.str(info);
    }

}



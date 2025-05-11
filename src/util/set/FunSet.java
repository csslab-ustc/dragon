package util.set;

import util.Error;
import util.Layout;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

// a functional set.
@SuppressWarnings("unchecked")
public class FunSet<X> extends HashSet<X> {
    private static int numSet = 0;

    public FunSet() {
        numSet++;
        super();
    }
    public FunSet(X x) {
        numSet++;
        super(List.of(x));
    }
    public FunSet(List<X> list) {
        numSet++;
        super(list);
    }

    // s \/ {data}
    public FunSet<X> addData(X data) {
        FunSet<X> newSet = (FunSet<X>) this.clone();
        newSet.add(data);
        return newSet;
    }

    // s - {data}
    public FunSet<X> removeData(X data) {
        FunSet<X> newSet = (FunSet<X>) this.clone();
        newSet.remove(data);
        return newSet;
    }

    // s \/ [d1, ..., dn]
    public FunSet<X> addList(List<X> list) {
        FunSet<X> newSet = (FunSet<X>) this.clone();
        newSet.addAll(list);
        return newSet;
    }

    // s1 \/ s2
    public FunSet<X> union(FunSet<X> theSet) {
        FunSet<X> newSet = (FunSet<X>) this.clone();
        newSet.addAll(theSet);
        return newSet;
    }

    // \/ {s1, s2, ..., sn}
    public static <X> FunSet<X> unionSets(List<FunSet<X>> allSets) {
        FunSet<X> newSet = new FunSet<>();
        for (FunSet<X> theSet : allSets)
            newSet.addAll(theSet);
        return newSet;
    }

    // s1 /\ s2
    public static <X> FunSet<X> retainSets(FunSet<X> set1, FunSet<X> set2) {
        FunSet<X> newSet = (FunSet<X>) set1.clone();
        newSet.retainAll(set2);
        return newSet;
    }

    // /\ {s1, s2, ..., sn}
    public static <X> FunSet<X> retainSets(List<FunSet<X>> allSets) {
        if(allSets.isEmpty())
            return new FunSet<>();
        FunSet<X> newSet = (FunSet<X>)allSets.get(0).clone();
        for (FunSet<X> theSet : allSets)
            newSet.retainAll(theSet);
        return newSet;
    }

    // s1 - s2
    public FunSet<X> sub(FunSet<X> theSet) {
        FunSet<X> newSet = (FunSet<X>) this.clone();
        newSet.removeAll(theSet);
        return newSet;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Layout.T layout(){
        return Layout.halign(List.of(Layout.str("{"),
                Layout.halignSepRight(Layout.str(", "),
                        this.stream().map(x -> Layout.str(x.toString())).collect(Collectors.toList())),
                Layout.str("}")));
    }

    public List<X> toList() {
        return List.copyOf(this);
    }

    public static Layout.T status(){
        String info = "number of sets: = " + numSet;
        numSet = 0;
        return Layout.str(info);
    }

}



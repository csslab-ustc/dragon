package interproc;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

// context for function
// K: the key to index into the context
// LS: the lattice being used
public class Context<K, L> {
    private int size;
    private L defaultValue;
    private HashMap<K, List<L>> args;

    public Context(int size, L initialLatticeValue) {
        this.size = size;
        this.defaultValue = initialLatticeValue;
        this.args = new HashMap<>();
    }

    private List<L> genDefaultValues () {
        List<L> defaultValues = new LinkedList<>();
        for(int i=0; i<size; i++) {
            defaultValues.addLast(defaultValue);
        }
        return defaultValues;
    }

    public void merge(K key, List<L> values, BiFunction<L, L, L> f) {
        List<L> theArgs = this.args.get(key);
        if(theArgs == null) {
            theArgs = genDefaultValues();
        }
        LinkedList<L> newValues = new LinkedList<>();
        for(int i=0; i<size; i++) {
            newValues.addLast(f.apply(theArgs.get(i), values.get(i)));
        }
        this.args.put(key, newValues);
    }

    private static <L> List<L> collapseList(List<L> left, List<L> right,
                                        BiFunction<L, L, L> f){
        LinkedList<L> list = new LinkedList<>();
        for(int i=0; i<left.size(); i++) {
            list.addLast(f.apply(left.get(i), right.get(i)));
        }
        return list;
    }

    public List<L> collapse(K key, BiFunction<L, L, L> f) {
        var newValues = genDefaultValues();
        List<L> args = this.args.get(key);
        return collapseList(new LinkedList<>(args), newValues, f);
    }

    public void print(Consumer<L> f) {
        this.args.forEach((k,l)->{
            System.out.println(k+": ");
            System.out.print("\t");
            l.forEach(f);
        });
    }

}






















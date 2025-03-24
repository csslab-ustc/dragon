package util.set;

import java.util.Collection;

// the straightforward set representation
// this always modify the underlying set.
public class HashSet<X> extends java.util.HashSet<X> {

    public HashSet() {
        super();
    }

    public HashSet(Collection<? extends X> c) {
        super(c);
    }

    // s \/ {data}
    public HashSet<X> addElement(X data) {
        super.add(data);
        return this;
    }

    // s - {data}
    public HashSet<X> removeElement(Object data) {
        super.remove(data);
        return this;
    }

    // s1 \/ s2
    public HashSet<X> union(java.util.HashSet<X> theSet) {
        super.addAll(theSet);
        return this;
    }

    // s1 - s2
    public HashSet<X> sub(java.util.HashSet<X> theSet) {
        super.removeAll(theSet);
        return this;
    }

    public void print(){
        System.out.print("{");
        this.forEach((x) -> {
            System.out.print(x + ", ");
        });
        System.out.print("}\n");
    }
}



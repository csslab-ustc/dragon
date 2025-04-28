package util.set;

// an abstract set interface.
// with several concrete implementations:
//   1. FunSet (in FunSet.java): a functional set;
//   2. BitSet (in BitSet.java): a bit-vector-based set; and
//   3. OrderSet (in OrderSet.java): an ordered set.
public interface Set<X> {
    // s \/ {data}
    void addData(X data);

    // s - {data}
    void removeData(X data);

    // s1 \/ s2
    void union(Set<X> theSet);

    // s1 - s2
    void sub(Set<X> theSet);

    // We use copy constructor instead of clone, see:
    //   "Copy Constructor versus Cloning"
    // in:
    // https://www.artima.com/articles/josh-bloch-on-design#part13
    Set<X> getClone();
}

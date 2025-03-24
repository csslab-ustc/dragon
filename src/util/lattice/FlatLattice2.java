package util.lattice;

/*
             T
     /   /  ...    \       \
    x1  x2        x_{n-1}  xn
     \   \  ...   /      /
            _|_
*/

import util.Error;

import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;

public class FlatLattice2<X, Names extends FlatLattice2.N> {
    // names
    public interface N{
        String toString(FlatLattice2.T t);
    }
    private final Class<?> nameOfClass;

    // datatypes
    public sealed interface T
        permits Top, Middle, Bot{
    }

    public record Top() implements T{}
    public record Middle<X>(X data) implements T{
    }
    public record Bot<X>() implements T{
    }

    // fields
    public T state;
    LinkedList<Consumer<T>> promises;

    // constructors
    @SafeVarargs
    public FlatLattice2(T t, Names... names){
        this.state = t;
        this.promises = new LinkedList<>();
        // pass names
        if(names.length != 0){
            throw new Error("do not call this constructor with nonempty names");
        }
        this.nameOfClass = names.getClass().componentType();
    }

    @SafeVarargs
    public FlatLattice2(X data, Names... names) {
        this.state = new Middle<>(data);
        this.promises = new LinkedList<>();
        // pass names
        if(names.length != 0){
            throw new Error("do not call this constructor with nonempty names");
        }
        this.nameOfClass = names.getClass().componentType();
    }

    // methods
    public void addPromise(Consumer<T> promise){
        this.promises.add(promise);
    }

    public void assignValue(T newState){
        if(this.state.equals(newState)){
            return;
        }
        this.state = newState;
        this.promises.forEach(c -> c.accept(this.state));
    }

    // least upper bound:
    public void lub(FlatLattice2<X, Names> other){
        switch(this.state){
            case Bot() -> {
                this.state = other.state;
            }
            case Middle(var data1) -> {
                switch (other.state){
                    case Bot() ->{
                        // unchange
                    }
                    case Middle(var data2)->{
                        if(data1.equals(data2)) {
                            // unchange
                        }
                        else {
                            this.state = new Top();
                        }
                    }
                    case Top()->{
                        this.state = new Top();
                    }
                }
            }
            case Top() -> {
                // unchanged
            }
        }
    }

    public void lub(List<FlatLattice2<X, Names>> others){
        others.forEach(this::lub);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof FlatLattice.T obj)){
            return false;
        }
        return this.state.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }


    @Override
    public String toString() {
        String s;
        try {
            s =  ((FlatLattice2.N)nameOfClass.getDeclaredConstructor().newInstance()).toString(this.state);
        }catch (Exception e){
            throw new Error(e);
        }
        return s;
    }

}




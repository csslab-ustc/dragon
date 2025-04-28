package util.lattice;

/*
             T
      /   /      \       \
     /   /  ...    \      \
    x1  x2        x_{n-1}  xn
     \   \  ...   /       /
      \   \      /       /
            _|_
*/

import util.Error;
import util.Todo;

import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;

public class FlatLattice2<X> {
    // datatypes
    public sealed interface T
        permits Top, Middle, Bot{
    }

    public record Top() implements T{}
    public record Middle<X>(X data) implements T{
    }
    public record Bot<X>() implements T{
    }

    // current state
    public T state;
    LinkedList<Consumer<T>> promises;

    // constructors
    public FlatLattice2(T t){
        this.state = t;
        this.promises = new LinkedList<>();
    }

    public FlatLattice2(X data) {
        this.state = new Middle<>(data);
        this.promises = new LinkedList<>();
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
    public void lub(FlatLattice2<X> other){
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

    public void lub(List<FlatLattice2<X>> others){
        others.forEach(this::lub);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof FlatLattice obj)){
            return false;
        }
        return this.state.equals(obj.state);
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }


    @Override
    public String toString() {
        throw new Todo();
    }

}



